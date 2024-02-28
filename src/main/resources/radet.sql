KEY
-----------------------------------------------------
?1 = facility_id e.g. 					1620
?2 = start date e.g. 					'1980-01-01'
?3 = end date e.g.						'2023-07-05'
?4 = previous quarter date e.g.			'2023-06-30'
?5 = previous previous quarter date e.g.'2023-03-31'
------------------------------------------------------
WITH bio_data AS (SELECT DISTINCT (p.uuid) AS personUuid,p.hospital_number AS hospitalNumber, h.unique_id as uniqueId,
                                    EXTRACT(YEAR FROM  AGE(NOW(), date_of_birth)) AS age,
                                    INITCAP(p.sex) AS gender,
                                    p.date_of_birth AS dateOfBirth,
                                    facility.name AS facilityName,
                                    facility_lga.name AS lga,
                                    facility_state.name AS state,
                                    boui.code AS datimId,
                                    tgroup.display AS targetGroup,
                                    eSetting.display AS enrollmentSetting,
                                    hac.visit_date AS artStartDate,
                                    hr.description AS regimenAtStart,
                                    p.date_of_registration as dateOfRegistration,
                                    h.date_of_registration as dateOfEnrollment,
                                    h.ovc_number AS ovcUniqueId,
                                    h.house_hold_number AS householdUniqueNo,
                                    ecareEntry.display AS careEntry,
                                    hrt.description AS regimenLineAtStart
                  FROM patient_person p
                        INNER JOIN base_organisation_unit facility ON facility.id = facility_id
                        INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id
                        INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id
                        INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID'
                        INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid
                        LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id
                        LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id
                        LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id
                        INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid
                                                               AND hac.archived = 0
                        INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id
                        INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id
                  WHERE
                      h.archived = 0
                    AND p.archived = 0
                    AND h.facility_id = ?1
                    AND hac.is_commencement = TRUE
                    AND hac.visit_date >= ?2
                    AND hac.visit_date < ?3
                  ),
    patient_lga AS (SELECT DISTINCT ON (personUuid) personUuid AS personUuid11,
                    CASE WHEN (addr ~ '^[0-9\\.]+$') = TRUE
                        THEN (SELECT name FROM base_organisation_unit WHERE id = cast(addr AS INT)) ELSE
                            (SELECT name FROM base_organisation_unit WHERE id = cast(facilityLga AS INT)) END AS lgaOfResidence
                        --then (select name from base_organisation_unit where id = cast(addr as int)) end as lgaOfResidence
                    FROM (
                    SELECT pp.uuid AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga,
                           (jsonb_array_elements(pp.address->'address')->>'district') AS addr FROM patient_person pp
                        LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER)
                    --select uuid AS personUuid, (jsonb_array_elements(address->'address')->>'district') as addr from patient_person
                        ) dt),
    current_clinical AS (SELECT DISTINCT ON (tvs.person_uuid) tvs.person_uuid AS person_uuid10,
                                body_weight AS currentWeight,
                                tbs.display AS tbStatus1,
                                bac.display AS currentClinicalStage,
                                (CASE
                                    WHEN INITCAP(pp.sex) = 'Male' THEN NULL
                                    WHEN preg.display IS NOT NULL THEN preg.display
                                    ELSE hac.pregnancy_status
                                   END ) AS pregnancyStatus,
                                --(CASE
                                --WHEN preg.display IS NOT NULL THEN preg.display
                                --ELSE hac.pregnancy_status  END ) AS pregnancyStatus,
                               CASE
                                   WHEN hac.tb_screen IS NOT NULL THEN hac.visit_date
                                   ELSE NULL
                                   END AS dateOfTbScreened1
                         FROM
                             triage_vital_sign tvs
                                 INNER JOIN (
                                             SELECT
                                                 person_uuid,
                                                 MAX(capture_date) AS MAXDATE
                                             FROM
                                                 triage_vital_sign
                                             GROUP BY
                                                 person_uuid
                                             ORDER BY
                                                 MAXDATE ASC
                                 ) AS current_triage ON current_triage.MAXDATE = tvs.capture_date
                                     AND current_triage.person_uuid = tvs.person_uuid
                                     INNER JOIN hiv_art_clinical hac ON tvs.uuid = hac.vital_sign_uuid
                                       LEFT JOIN patient_person pp ON tvs.person_uuid = pp.uuid
                                     INNER JOIN (
                                     SELECT
                                         person_uuid,
                                         MAX(hac.visit_date) AS MAXDATE
                                     FROM
                                         hiv_art_clinical hac
                                     GROUP BY
                                         person_uuid
                                     ORDER BY
                                         MAXDATE ASC
                                 ) AS current_clinical_date ON current_clinical_date.MAXDATE = hac.visit_date
                                     AND current_clinical_date.person_uuid = hac.person_uuid
                                     INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid
                                     LEFT JOIN base_application_codeset bac ON bac.id = hac.clinical_stage_id
                                     LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status
                                     LEFT JOIN base_application_codeset tbs ON tbs.id = CAST(hac.tb_status AS INTEGER)
                                         WHERE
                                           hac.archived = 0
                                           AND he.archived = 0
                                           AND hac.visit_date < ?3
                                           AND he.facility_id = ?1
                                         ),
    sample_collection_date AS (SELECT CAST(sample.date_sample_collected AS DATE ) as dateOfViralLoadSampleCollection, patient_uuid as person_uuid120
                               FROM (
                               SELECT lt.viral_load_indication, sm.facility_id,sm.date_sample_collected, sm.patient_uuid, sm.archived,
                                      ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_sample_collected DESC) as rnkk
                               FROM public.laboratory_sample  sm
                                   INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id
                               WHERE lt.lab_test_id=16
                               AND  lt.viral_load_indication !=719
                               AND date_sample_collected IS NOT null
                               AND date_sample_collected <= ?3
                               ) as sample
                               WHERE sample.rnkk = 1
                               AND (sample.archived is null OR sample.archived = 0)
                               AND sample.facility_id = ?1
                               ),
    tbstatus AS (SELECT personUuid133, dateOfTbScreened, tbStatus, tbStatusOutcome
                 FROM
                     (SELECT DISTINCT ON (hac.person_uuid) hac.person_uuid AS personUuid133,
                                                           ho.date_of_observation AS dateOfTbScreened,
                                                           ho.data->'tbIptScreening'->>'status' AS tbStatus,
                                                           ho.data->'tbIptScreening'->>'outcome' AS tbStatusOutcome,
                                                           ROW_NUMBER() OVER (PARTITION BY hac.person_uuid
                                                               ORDER BY ho.date_of_observation DESC) AS rowNums
                      FROM hiv_art_clinical hac
                          LEFT JOIN hiv_observation ho ON ho.person_uuid = hac.person_uuid
                      WHERE
                          ho.type = 'Chronic Care'
                        AND ho.data IS NOT NULL
                        AND hac.archived = 0
                        AND ho.date_of_observation BETWEEN ?2
                        AND ?3
                        AND hac.facility_id = ?1) dt
                 WHERE
                     dt.rowNums = 1
                 ),
    tblam AS (SELECT * FROM (SELECT CAST(lr.date_result_reported AS DATE) AS dateOfLastTbLam,
                                    lr.patient_uuid as personuuidtblam,
                                    lr.result_reported as tbLamResult,
                                    ROW_NUMBER () OVER (PARTITION BY lr.patient_uuid
                                        ORDER BY
                                            lr.date_result_reported DESC) AS rank2333
                             FROM
                                 laboratory_result lr
                                     INNER JOIN public.laboratory_test lt on lr.test_id = lt.id
                             WHERE
                            lt.lab_test_id = 51
                            AND lr.date_result_reported IS NOT NULL
                            AND lr.date_result_reported <= ?3
                            AND lr.date_result_reported >= ?2
                            AND lr.result_reported is NOT NULL
                            AND lr.archived = 0
                            AND lr.facility_id = ?1) AS tblam
                       WHERE
                           tblam.rank2333 = 1
                       ),
    current_vl_result AS (SELECT * FROM
                                       (SELECT CAST(ls.date_sample_collected AS DATE ) AS dateOfCurrentViralLoadSample,
                                                sm.patient_uuid AS person_uuid130 ,
                                                sm.facility_id AS vlFacility,
                                                sm.archived AS vlArchived,
                                                acode.display AS viralLoadIndication,
                                                sm.result_reported AS currentViralLoad,
                                                CAST(sm.date_result_reported AS DATE) AS dateOfCurrentViralLoad,
                                                ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) AS rank2
                                        FROM public.laboratory_result  sm
                                            INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id
                                            INNER JOIN public.laboratory_sample ls on ls.test_id = lt.id
                                            INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication
                                        WHERE lt.lab_test_id = 16
                                        AND  lt.viral_load_indication !=719
                                        AND sm. date_result_reported IS NOT NULL
                                        AND sm.date_result_reported <= ?3
                                        AND sm.result_reported IS NOT NULL
                                        ) AS vl_result
                                   WHERE vl_result.rank2 = 1
                                   AND (vl_result.vlArchived = 0 OR vl_result.vlArchived IS null)
                                   AND  vl_result.vlFacility = ?1
                                   ),
    careCardCD4 AS (SELECT visit_date, coalesce(cast(cd_4 AS VARCHAR), cd4_semi_quantitative) AS cd_4, person_uuid AS cccd4_person_uuid
                    FROM public.hiv_art_clinical
                    WHERE is_commencement IS TRUE
                    AND  archived = 0
                    AND  cd_4 != 0
                    AND visit_date <= ?3
                    AND facility_id = ?1
                    ),
    labCD4 AS (SELECT * FROM ( SELECT sm.patient_uuid AS cd4_person_uuid,
                                      sm.result_reported as cd4Lb,
                                      sm.date_result_reported as dateOfCD4Lb,
                                      ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) AS rnk
                               FROM public.laboratory_result  sm
                                   INNER JOIN public.laboratory_test  lt ON sm.test_id = lt.id
                               WHERE lt.lab_test_id IN (1, 50)
                                AND sm. date_result_reported IS NOT NULL
                                AND sm.archived = 0
                                AND sm.facility_id = ?1
                                AND sm.date_result_reported <= ?3
                               ) AS cd4_result
                        WHERE  cd4_result.rnk = 1
                        ),
    tb_sample_collection AS (SELECT sample.created_by,CAST(sample.date_sample_collected AS DATE) AS dateOfTbSampleCollection,
                                    patient_uuid AS personTbSample
                             FROM (SELECT llt.lab_test_name, sm.created_by,
                                          lt.viral_load_indication,
                                          sm.facility_id,
                                          sm.date_sample_collected,
                                          sm.patient_uuid,
                                          sm.archived,
                                          ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_sample_collected DESC) AS rnkk
                                   FROM public.laboratory_sample  sm
                                       INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id
                                       INNER JOIN  laboratory_labtest llt ON llt.id = lt.lab_test_id
                                   WHERE lt.lab_test_id IN (65,51,66,64)
                                   AND sm.archived = 0
                                   AND sm. date_sample_collected <= ?3
                                   AND sm.facility_id = ?1 )  AS sample
                             WHERE sample.rnkk = 1
                             ),
    current_tb_result AS (WITH tb_test AS (SELECT personTbResult, dateofTbDiagnosticResultReceived,
                                                  COALESCE(
                                                          MAX(CASE WHEN lab_test_id = 65 THEN tbDiagnosticResult END) ,
                                                          MAX(CASE WHEN lab_test_id = 51 THEN tbDiagnosticResult END) ,
                                                          MAX(CASE WHEN lab_test_id = 66 THEN tbDiagnosticResult END),
                                                          MAX(CASE WHEN lab_test_id = 64 THEN tbDiagnosticResult END),
                                                          MAX(CASE WHEN lab_test_id = 67 THEN tbDiagnosticResult END),
                                                          MAX(CASE WHEN lab_test_id = 68 THEN tbDiagnosticResult END)
                                                  ) AS tbDiagnosticResult,
                                                  COALESCE(
                                                           MAX(CASE WHEN lab_test_id = 65 THEN 'Gene Xpert' END) ,
                                                           MAX(CASE WHEN lab_test_id = 51 THEN 'TB-LAM' END) ,
                                                           MAX(CASE WHEN lab_test_id = 66 THEN 'Chest X-ray' END),
                                                           MAX(CASE WHEN lab_test_id = 64 THEN 'AFB microscopy' END),
                                                           MAX(CASE WHEN lab_test_id = 67 THEN 'Gene Xpert' END) ,
                                                           MAX(CASE WHEN lab_test_id = 58 THEN 'TB-LAM' END)
                                                  ) AS tbDiagnosticTestType
                                           FROM
                                               (SELECT sm.patient_uuid AS personTbResult,
                                                          sm.result_reported AS tbDiagnosticResult,
                                                          CAST(sm.date_result_reported AS DATE) AS dateofTbDiagnosticResultReceived,
                                                          lt.lab_test_id
                                                  FROM laboratory_result sm
                                                      INNER JOIN public.laboratory_test  lt ON sm.test_id = lt.id
                                                  WHERE lt.lab_test_id IN (65,51,66,64) AND sm.archived = 0
                                                  AND sm.date_result_reported IS NOT NULL
                                                  AND sm.facility_id = ?1
                                                  AND sm.date_result_reported <= ?3
                                                  ) AS dt
                                           GROUP BY dt.personTbResult, dt.dateofTbDiagnosticResultReceived
                                           )
                          SELECT * FROM
                                       (SELECT *, ROW_NUMBER() OVER (PARTITION BY personTbResult
                                           ORDER BY dateofTbDiagnosticResultReceived DESC ) AS rnk FROM tb_test) AS dt
                                   WHERE rnk = 1
                                   ),
    tbTreatment AS (SELECT * FROM
                                 (SELECT
                                     COALESCE(NULLIF(CAST(data->'tbIptScreening'->>'treatementType' AS text), ''), '') AS tbTreatementType,
                                     NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'tbTreatmentStartDate', '') AS DATE), NULL) AS tbTreatmentStartDate,
                                     CAST(data->'tbIptScreening'->>'treatmentOutcome' AS text) AS tbTreatmentOutcome,
                                     NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'completionDate', '') AS DATE), NULL) AS tbCompletionDate,
                                     person_uuid AS tbTreatmentPersonUuid,
                                     ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS row_number
                                  FROM public.hiv_observation WHERE type = 'Chronic Care'
                                                                AND facility_id = ?1 ) tbTreatment
                             WHERE row_number = 1
                               AND tbTreatmentStartDate IS NOT NULL
                             ),
    pharmacy_details_regimen AS (SELECT * FROM
                                              (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid40 ORDER BY pr1.lastPickupDate DESC) AS rnk3
                                               FROM
                                                   (SELECT p.person_uuid AS person_uuid40,
                                                            COALESCE(ds_model.display, p.dsd_model_type) AS dsdModel,
                                                            p.visit_date AS lastPickupDate,
                                                            r.description AS currentARTRegimen,
                                                            rt.description AS currentRegimenLine,
                                                            p.next_appointment AS nextPickupDate,
                                                            CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfARVRefill
                                                     FROM public.hiv_art_pharmacy p
                                                         INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id
                                                         INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id
                                                         INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id
                                                         LEFT JOIN base_application_codeset ds_model on ds_model.code = p.dsd_model_type
                                                     WHERE r.regimen_type_id in (1,2,3,4,14)
                                                     AND  p.archived = 0
                                                     AND  p.facility_id = ?1
                                                     AND  p.visit_date >= ?2
                                                     AND  p.visit_date  < ?3) AS pr1
                                               ) AS pr2
                                          WHERE pr2.rnk3 = 1
                                          ),
    eac AS (SELECT DISTINCT ON (he.person_uuid)
                he.person_uuid AS person_uuid50,
                max_date_eac.eac_session_date AS dateOfCommencementOfEAC,
                COUNT AS numberOfEACSessionCompleted,
                last_eac_complete.eac_session_date AS dateOfLastEACSessionCompleted,
                ext_date.eac_session_date AS dateOfExtendEACCompletion,
                r.date_result_reported AS DateOfRepeatViralLoadResult,
                r.result_reported AS repeatViralLoadResult
            FROM
                hiv_eac he
                    INNER JOIN
                    (SELECT * FROM
                                  (SELECT hes.*,
                                          ROW_NUMBER() OVER
                                          (PARTITION BY hes.person_uuid
                                          ORDER BY hes.eac_session_date, id DESC) as row_number
                                   FROM
                                       hiv_eac_session hes
                                   WHERE
                                       status = 'FIRST EAC'
                                     AND archived = 0
                                   ) e
                              WHERE
                                  e.row_number = 1 ) AS max_date_eac
                        ON max_date_eac.eac_id = he.uuid
                    LEFT JOIN
                    (SELECT
                         person_uuid,
                         hes.eac_id,
                         COUNT(person_uuid) AS COUNT
                     FROM hiv_eac_session hes
                     GROUP BY
                         hes.eac_id,
                         hes.person_uuid) AS completed_eac
                        ON completed_eac.person_uuid = max_date_eac.person_uuid AND completed_eac.eac_id = he.uuid
                    LEFT JOIN
                    (SELECT * FROM
                                  (SELECT hes.*,
                                           ROW_NUMBER() OVER (PARTITION BY hes.person_uuid ORDER BY hes.eac_session_date) AS row_number
                                    FROM
                                        hiv_eac he
                                            INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid
                                    WHERE
                                        he.status = 'COMPLETED'
                                      AND he.archived = 0) e
                              WHERE
                                  e.row_number = 1) AS last_eac_complete
                        ON last_eac_complete.eac_id = max_date_eac.eac_id
                               AND last_eac_complete.person_uuid = max_date_eac.person_uuid
                    LEFT JOIN
                    (SELECT * FROM
                                  (SELECT hes.*,
                                          ROW_NUMBER() OVER (PARTITION BY hes.person_uuid ORDER BY hes.eac_session_date, id DESC) AS row_number
                                   FROM
                                       hiv_eac_session hes
                                   WHERE
                                       hes.status NOT ilike 'FIRST%'
                                     AND status NOT ilike 'SECOND%'
                                     AND status NOT ilike 'THIRD%'
                                     AND hes.archived = 0) e
                              WHERE e.row_number = 1) AS ext_date
                        ON ext_date.eac_id = he.uuid AND ext_date.person_uuid = he.person_uuid
                    LEFT JOIN
                    (SELECT * FROM
                                  (SELECT l.patient_uuid,
                                          l.date_result_reported,
                                          l.result_reported,
                                          ROW_NUMBER() OVER (PARTITION BY l.patient_uuid ORDER BY l.date_result_reported ASC) AS row_number
                                   FROM laboratory_result l
                                       INNER JOIN
                                       (SELECT lr.patient_uuid,
                                               MIN(lr.date_result_reported) AS date_result_reported
                                        FROM
                                            laboratory_result lr
                                                INNER JOIN public.laboratory_test  lt on lr.test_id = lt.id
                                                INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication
                                                INNER JOIN
                                                (SELECT * FROM
                                                              (SELECT hes.*,
                                                                      ROW_NUMBER() OVER (PARTITION BY hes.person_uuid ORDER BY hes.eac_session_date, he.id DESC) AS row_number
                                                               FROM hiv_eac he
                                                                   INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid
                                                               WHERE
                                                                   he.status = 'COMPLETED'
                                                                 AND hes.eac_session_date < ?3
                                                                 AND he.archived = 0) e
                                                          WHERE
                                                              e.row_number = 1) AS last_eac_complete
                                                    ON last_eac_complete.person_uuid = lr.patient_uuid
                                                    AND lr.date_result_reported > last_eac_complete.eac_session_date
                                                    AND lt.lab_test_id = 16
                                                    AND last_eac_complete.eac_session_date < ?3
                                                    AND  lt.viral_load_indication !=719
                                        GROUP BY
                                            lr.patient_uuid) r
                                           ON l.date_result_reported = r.date_result_reported AND l.patient_uuid = r.patient_uuid) l
                              WHERE
                                  l.row_number = 1) r ON r.patient_uuid = he.person_uuid
            WHERE
                he.archived = 0 ),
    biometric AS (
        SELECT
            DISTINCT ON (he.person_uuid)
            he.person_uuid AS person_uuid60,
            CASE WHEN biometric_count.count > 5 THEN biometric_count.enrollment_date ELSE NULL END AS dateBiometricsEnrolled,
            CASE WHEN biometric_count.count > 5 THEN biometric_count.count ELSE NULL END AS numberOfFingersCaptured,
            CASE WHEN recapture_count.count > 5 THEN recapture_count.recapture_date ELSE NULL END AS dateBiometricsRecaptured,
            CASE WHEN recapture_count.count > 5 THEN recapture_count.count ELSE NULL END AS numberOfFingersRecaptured,
            bst.biometric_status AS biometricStatus,
            bst.status_date
        FROM
            hiv_enrollment he
        LEFT JOIN (
            SELECT
                b.person_uuid,
                CASE WHEN COUNT(b.person_uuid) > 10 THEN 10 ELSE COUNT(b.person_uuid) END,
                MAX(enrollment_date) enrollment_date
            FROM
                biometric b
            WHERE
                archived = 0
                AND (recapture = 0 or recapture is null)
            GROUP BY
                b.person_uuid
        ) biometric_count ON biometric_count.person_uuid = he.person_uuid
        LEFT JOIN (
            SELECT
                r.person_uuid,
                CASE WHEN COUNT(r.person_uuid) > 10 THEN 10 ELSE COUNT(r.person_uuid) END,
                MAX(enrollment_date) recapture_date
            FROM
                biometric r
            WHERE
                archived = 0
                AND recapture = 1
            GROUP BY
                r.person_uuid
        ) recapture_count ON recapture_count.person_uuid = he.person_uuid
        LEFT JOIN (
            SELECT DISTINCT ON (person_id)
                person_id,
                biometric_status,
                MAX(status_date) OVER (PARTITION BY person_id ORDER BY status_date DESC) AS status_date
            FROM
                hiv_status_tracker
            WHERE
                archived=0 AND facility_id = ?1
        ) bst ON bst.person_id = he.person_uuid
        WHERE
            he.archived = 0
    ),
    current_regimen AS (
        SELECT DISTINCT ON (regiment_table.person_uuid)
            regiment_table.person_uuid AS person_uuid70,
            start_or_regimen AS dateOfCurrentRegimen,
            regiment_table.max_visit_date,
            regiment_table.regimen
        FROM (
            SELECT
                MIN(visit_date) start_or_regimen,
                MAX(visit_date) max_visit_date,
                regimen,
                person_uuid
            FROM (
                SELECT
                    hap.id,
                    hap.person_uuid,
                    hap.visit_date,
                    hivreg.description AS regimen,
                    ROW_NUMBER() OVER(ORDER BY person_uuid, visit_date) rn1,
                    ROW_NUMBER() OVER(PARTITION BY hivreg.description ORDER BY person_uuid, visit_date) rn2
                FROM
                    public.hiv_art_pharmacy AS hap
                INNER JOIN (
                    SELECT
                        MAX(hapr.id) AS id,
                        art_pharmacy_id,
                        regimens_id,
                        hr.description
                    FROM
                        public.hiv_art_pharmacy_regimens AS hapr
                    INNER JOIN hiv_regimen AS hr ON hapr.regimens_id = hr.id
                    WHERE
                        hr.regimen_type_id IN (1,2,3,4,14)
                    GROUP BY
                        art_pharmacy_id,
                        regimens_id,
                        hr.description
                ) AS hapr ON hap.id = hapr.art_pharmacy_id AND hap.archived = 0
                INNER JOIN hiv_regimen AS hivreg ON hapr.regimens_id = hivreg.id
                INNER JOIN hiv_regimen_type AS hivregtype ON hivreg.regimen_type_id = hivregtype.id
                    AND hivreg.regimen_type_id IN (1,2,3,4,14)
                ORDER BY
                    person_uuid,
                    visit_date
            ) t
            GROUP BY
                person_uuid,
                regimen,
                rn1 - rn2
            ORDER BY
                MIN(visit_date)
        ) AS regiment_table
        INNER JOIN (
            SELECT
                DISTINCT MAX(visit_date) AS max_visit_date,
                person_uuid
            FROM
                hiv_art_pharmacy
            WHERE
                archived = 0
            GROUP BY
                person_uuid
        ) AS hap ON regiment_table.person_uuid = hap.person_uuid
        WHERE
            regiment_table.max_visit_date = hap.max_visit_date
        GROUP BY
            regiment_table.person_uuid,
            regiment_table.regimen,
            regiment_table.max_visit_date,
            start_or_regimen
    ),
    ipt AS (
        SELECT
            DISTINCT ON (hap.person_uuid)
            hap.person_uuid AS personUuid80,
            ipt_type.regimen_name AS iptType,
            hap.visit_date AS dateOfIptStart,
            COALESCE(NULLIF(CAST(hap.ipt->>'completionStatus' AS text), ''), '') AS iptCompletionStatus,
            (
                CASE
                    WHEN MAX(CAST(complete.date_completed AS DATE)) > NOW() THEN NULL
                    WHEN MAX(CAST(complete.date_completed AS DATE)) IS NULL
                        AND CAST((hap.visit_date + 168) AS DATE) < NOW() THEN CAST((hap.visit_date + 168) AS DATE)
                    ELSE MAX(CAST(complete.date_completed AS DATE))
                END
            ) AS iptCompletionDate
        FROM
            hiv_art_pharmacy hap
        INNER JOIN (
            SELECT
                DISTINCT person_uuid,
                MAX(visit_date) AS MAXDATE
            FROM
                hiv_art_pharmacy
            WHERE
                (ipt ->> 'type' ILIKE '%INITIATION%' OR ipt ->> 'type' ILIKE 'START_REFILL')
                AND archived = 0
            GROUP BY
                person_uuid
            ORDER BY
                MAXDATE ASC
        ) AS max_ipt ON max_ipt.MAXDATE = hap.visit_date
            AND max_ipt.person_uuid = hap.person_uuid
        INNER JOIN (
            SELECT
                DISTINCT h.person_uuid,
                h.visit_date,
                CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name,
                CAST(pharmacy_object ->> 'duration' AS VARCHAR) AS duration,
                hrt.description
            FROM
                hiv_art_pharmacy h,
                jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object)
                RIGHT JOIN hiv_regimen hr ON hr.description = CAST(pharmacy_object ->> 'regimenName' AS VARCHAR)
                RIGHT JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id
            WHERE
                hrt.id IN (15)
        ) AS ipt_type ON ipt_type.person_uuid = max_ipt.person_uuid
            AND ipt_type.visit_date = max_ipt.MAXDATE
        LEFT JOIN (
            SELECT
                hap.person_uuid,
                hap.visit_date,
                TO_DATE(NULLIF(NULLIF(TRIM(hap.ipt->>'dateCompleted'), ''), 'null'), 'YYYY-MM-DD') AS date_completed
            FROM
                hiv_art_pharmacy hap
            INNER JOIN (
                SELECT
                    DISTINCT person_uuid,
                    MAX(visit_date) AS MAXDATE
                FROM
                    hiv_art_pharmacy
                WHERE
                    ipt ->> 'dateCompleted' IS NOT NULL
                GROUP BY
                    person_uuid
                ORDER BY
                    MAXDATE ASC
            ) AS complete_ipt ON CAST(complete_ipt.MAXDATE AS DATE) = hap.visit_date
                AND complete_ipt.person_uuid = hap.person_uuid
        ) complete ON complete.person_uuid = hap.person_uuid
        WHERE
            hap.archived = 0
            AND hap.visit_date < ?3
        GROUP BY
            hap.person_uuid,
            ipt_type.regimen_name,
            hap.ipt,
            hap.visit_date
    ),
    cervical_cancer AS (
        SELECT
            DISTINCT ON (ho.person_uuid)
            ho.person_uuid AS person_uuid90,
            ho.date_of_observation AS dateOfCervicalCancerScreening,
            ho.data ->> 'screenTreatmentMethodDate' AS treatmentMethodDate,
            cc_type.display AS cervicalCancerScreeningType,
            cc_method.display AS cervicalCancerScreeningMethod,
            cc_trtm.display AS cervicalCancerTreatmentScreened,
            cc_result.display AS resultOfCervicalCancerScreening
        FROM
            hiv_observation ho
        LEFT JOIN (
            SELECT
                DISTINCT person_uuid,
                MAX(date_of_observation) AS MAXDATE
            FROM
                hiv_observation
            WHERE
                archived = 0
                AND date_of_observation < ?3
            GROUP BY
                person_uuid
            ORDER BY
                MAXDATE ASC
        ) AS max_cc ON max_cc.MAXDATE = ho.date_of_observation
            AND max_cc.person_uuid = ho.person_uuid
        INNER JOIN base_application_codeset cc_type ON cc_type.code = CAST(ho.data ->> 'screenType' AS VARCHAR)
        INNER JOIN base_application_codeset cc_method ON cc_method.code = CAST(ho.data ->> 'screenMethod' AS VARCHAR)
        LEFT JOIN base_application_codeset cc_result ON cc_result.code = CAST(ho.data ->> 'screeningResult' AS VARCHAR)
        LEFT JOIN base_application_codeset cc_trtm ON cc_trtm.code = CAST(ho.data ->> 'screenTreatment' AS VARCHAR)
    ),
    ovc AS (
        SELECT DISTINCT ON (person_uuid)
            person_uuid AS personUuid100,
            ovc_number AS ovcNumber,
            house_hold_number AS householdNumber
        FROM
            hiv_enrollment
    ),
    previous_previous AS (
        SELECT DISTINCT ON (pharmacy.person_uuid)
            pharmacy.person_uuid AS prePrePersonUuid,
            (
                CASE
                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'
                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'
                                                                       OR stat.hiv_status ILIKE '%out%'
                                                                       OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.hiv_status
                    ELSE pharmacy.status
                END
            ) AS status,
            (
                CASE
                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN stat.status_date
                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR
                                                                   stat.hiv_status ILIKE '%out%' OR
                                                                   stat.hiv_status ILIKE '%Invalid %')) THEN stat.status_date
                    ELSE pharmacy.visit_date
                END
            ) AS status_date,
            stat.cause_of_death,
            stat.va_cause_of_death
        FROM (
            SELECT
                (
                    CASE
                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?5 THEN 'IIT'
                        ELSE 'Active'
                    END
                ) status,
                (
                    CASE
                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?5 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'
                        ELSE hp.visit_date
                    END
                ) AS visit_date,
                hp.person_uuid, MAXDATE
            FROM
                hiv_art_pharmacy hp
            INNER JOIN (
                SELECT hap.person_uuid, hap.visit_date AS MAXDATE,
                       ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3
                FROM public.hiv_art_pharmacy hap
                INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id
                INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0
                INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id
                INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id
                WHERE r.regimen_type_id IN (1,2,3,4,14)
                AND hap.archived = 0
                AND hap.visit_date < ?3
            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid AND MAX.rnkkk3 = 1
            WHERE hp.archived = 0 AND hp.visit_date <= ?5
        ) pharmacy
        LEFT JOIN (
            SELECT
                hst.hiv_status,
                hst.person_id,
                hst.cause_of_death,
                hst.va_cause_of_death,
                hst.status_date
            FROM (
                SELECT * FROM (
                    SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death, hiv_status,
                                    ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC) AS row_number
                    FROM hiv_status_tracker WHERE archived = 0 AND status_date <= ?5
                ) s
                WHERE s.row_number = 1
            ) hst
            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id
            WHERE hst.status_date <= ?5
        ) stat ON stat.person_id = pharmacy.person_uuid
    ),
    previous AS (
        SELECT DISTINCT ON (pharmacy.person_uuid)
            pharmacy.person_uuid AS prePersonUuid,
            (
                CASE
                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'
                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR
                                                                   stat.hiv_status ILIKE '%out%' OR
                                                                   stat.hiv_status ILIKE '%Invalid %')) THEN stat.hiv_status
                    ELSE pharmacy.status
                END
            ) AS status,
            (
                CASE
                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN stat.status_date
                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'
                                                                       OR stat.hiv_status ILIKE '%out%'
                                                                       OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.status_date
                    ELSE pharmacy.visit_date
                END
            ) AS status_date,
            stat.cause_of_death,
            stat.va_cause_of_death
        FROM (
            SELECT
                (
                    CASE
                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?4 THEN 'IIT'
                        ELSE 'Active'
                    END
                ) status,
                (
                    CASE
                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?4 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'
                        ELSE hp.visit_date
                    END
                ) AS visit_date,
                hp.person_uuid, MAXDATE
            FROM
                hiv_art_pharmacy hp
            INNER JOIN (
                SELECT hap.person_uuid, hap.visit_date AS MAXDATE,
                       ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3
                FROM public.hiv_art_pharmacy hap
                INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id
                INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0
                INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id
                INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id
                WHERE r.regimen_type_id IN (1,2,3,4,14)
                AND hap.archived = 0
                AND hap.visit_date < ?4
            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid AND MAX.rnkkk3 = 1
            WHERE hp.archived = 0 AND hp.visit_date <= ?4
        ) pharmacy
        LEFT JOIN (
            SELECT
                hst.hiv_status,
                hst.person_id,
                hst.cause_of_death,
                hst.va_cause_of_death,
                hst.status_date
            FROM (
                SELECT * FROM (
                    SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death, hiv_status,
                                    ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC) AS row_number
                    FROM hiv_status_tracker WHERE archived = 0 AND status_date <= ?4
                ) s
                WHERE s.row_number = 1
            ) hst
            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id
            WHERE hst.status_date <= ?4
        ) stat ON stat.person_id = pharmacy.person_uuid
    ),
    current_status AS (
        SELECT DISTINCT ON (pharmacy.person_uuid)
            pharmacy.person_uuid AS cuPersonUuid,
            (
                CASE
                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'
                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'
                                                                       OR stat.hiv_status ILIKE '%out%'
                                                                       OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.hiv_status
                    ELSE pharmacy.status
                END
            ) AS status,
            (
                CASE
                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN stat.status_date
                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'
                                                                       OR stat.hiv_status ILIKE '%out%'
                                                                       OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.status_date
                    ELSE pharmacy.visit_date
                END
            ) AS status_date,
            stat.cause_of_death,
            stat.va_cause_of_death
        FROM (
            SELECT
                (
                    CASE
                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN 'IIT'
                        ELSE 'Active'
                    END
                ) status,
                (
                    CASE
                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'
                        ELSE hp.visit_date
                    END
                ) AS visit_date,
                hp.person_uuid, MAXDATE
            FROM
                hiv_art_pharmacy hp
            INNER JOIN (
                SELECT hap.person_uuid, hap.visit_date AS MAXDATE,
                       ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3
                FROM public.hiv_art_pharmacy hap
                INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id
                INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0
                INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id
                INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id
                WHERE r.regimen_type_id IN (1,2,3,4,14)
                AND hap.archived = 0
                AND hap.visit_date < ?3
            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid AND MAX.rnkkk3 = 1
            WHERE hp.archived = 0 AND hp.visit_date < ?3
        ) pharmacy
        LEFT JOIN (
            SELECT
                hst.hiv_status,
                hst.person_id,
                hst.cause_of_death,
                hst.va_cause_of_death,
                hst.status_date
            FROM (
                SELECT * FROM (
                    SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death, hiv_status,
                                    ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC) AS row_number
                    FROM hiv_status_tracker WHERE archived = 0 AND status_date <= ?3
                ) s
                WHERE s.row_number = 1
            ) hst
            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id
            WHERE hst.status_date < ?3
        ) stat ON stat.person_id = pharmacy.person_uuid
    ),
    naive_vl_data AS (
        SELECT
            pp.uuid AS nvl_person_uuid,
            EXTRACT(YEAR FROM AGE(NOW(), pp.date_of_birth)) AS age,
            ph.visit_date,
            ph.regimen
        FROM
            patient_person pp
        INNER JOIN (
            SELECT DISTINCT *
            FROM (
                SELECT
                    pharm.*,
                    ROW_NUMBER() OVER (PARTITION BY pharm.person_uuid ORDER BY pharm.visit_date DESC) AS row_number
                FROM
                    (SELECT DISTINCT *
                     FROM hiv_art_pharmacy hap
                     INNER JOIN hiv_art_pharmacy_regimens hapr ON hapr.art_pharmacy_id = hap.id
                     INNER JOIN hiv_regimen hr ON hr.id = hapr.regimens_id
                     INNER JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id
                     INNER JOIN hiv_regimen_resolver hrr ON hrr.regimensys = hr.description
                     WHERE
                         hap.archived = 0
                         AND hrt.id IN (1,2,3,4,14)
                         AND hap.facility_id = ?1
                    ) pharm
            ) ph
            WHERE ph.row_number = 1
        ) ph ON ph.person_uuid = pp.uuid
        WHERE
            pp.uuid NOT IN (
                SELECT patient_uuid
                FROM (
                    SELECT
                        COUNT(ls.patient_uuid),
                        ls.patient_uuid
                    FROM
                        laboratory_sample ls
                    INNER JOIN laboratory_test lt ON lt.id = ls.test_id AND lt.lab_test_id = 16
                    WHERE
                        ls.archived = 0
                        AND ls.facility_id = ?1
                    GROUP BY
                        ls.patient_uuid
                ) t
            )
    ),
    crytococal_antigen AS (
        SELECT
            *
        FROM
            (
                SELECT
                    DISTINCT ON (lr.patient_uuid) lr.patient_uuid AS personuuid12,
                    CAST(lr.date_result_reported AS DATE) AS dateOfLastCrytococalAntigen,
                    lr.result_reported AS lastCrytococalAntigen,
                    ROW_NUMBER() OVER (
                        PARTITION BY lr.patient_uuid
                        ORDER BY lr.date_result_reported DESC
                    ) AS rowNum
                FROM
                    public.laboratory_test lt
                    INNER JOIN laboratory_result lr ON lr.test_id = lt.id
                WHERE
                    (lab_test_id = 52 OR lab_test_id = 69 OR lab_test_id = 70)
                    AND lr.date_result_reported IS NOT NULL
                    AND lr.date_result_reported <= ?3
                    AND lr.date_result_reported >= ?2
                    AND lr.result_reported IS NOT NULL
                    AND lr.archived = 0
                    AND lr.facility_id = ?1
            ) dt
        WHERE
            rowNum = 1
    ),
    case_manager AS (
        SELECT DISTINCT ON (cmp.person_uuid) person_uuid AS caseperson, cmp.case_manager_id, CONCAT(cm.first_name, ' ', cm.last_name) AS caseManager
        FROM (
            SELECT
                person_uuid,
                case_manager_id,
                ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row_number
            FROM
                case_manager_patients
        ) cmp
        INNER JOIN case_manager cm ON cm.id = cmp.case_manager_id
        WHERE
            cmp.row_number = 1
            AND cm.facility_id = ?1
    ),
    client_verification AS (
        SELECT *
        FROM (
            SELECT
                person_uuid,
                data->'attempt'->0->>'outcome' AS clientVerificationStatus,
                CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) AS dateOfOutcome,
                ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) DESC) AS row_number
            FROM
                public.hiv_observation
            WHERE
                type = 'Client Verification'
                AND archived = 0
                AND CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) <= ?3
                AND CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) >= ?2
                AND facility_id = ?1
        ) clientVerification
        WHERE
            row_number = 1
            AND dateOfOutcome IS NOT NULL
    ) 
SELECT DISTINCT ON (bd.personUuid) personUuid AS uniquePersonUuid,
       bd.*,
       CONCAT(bd.datimId, '_', bd.personUuid) AS ndrPatientIdentifier,
       p_lga.*,
       scd.*,
       cvlr.*,
       pdr.*,
       b.*,
       c.*,
       e.*,
       ca.dateOfCurrentRegimen,
       ca.person_uuid70,
       ipt.dateOfIptStart,
       ipt.iptCompletionDate,
       ipt.iptCompletionStatus,
       ipt.iptType,
       cc.*,
       ov.*,
       tbTment.*,
       tbSample.*,
       tbResult.*,
       tbS.*,
       tbl.*,
       crypt.*,
       ct.cause_of_death AS causeOfDeath,
       ct.va_cause_of_death AS vaCauseOfDeath,
       (
           CASE
               WHEN prepre.status ILIKE '%DEATH%' THEN 'Died'
               WHEN prepre.status ILIKE '%out%' THEN 'Transferred Out'
               WHEN pre.status ILIKE '%DEATH%' THEN 'Died'
               WHEN pre.status ILIKE '%out%' THEN 'Transferred Out'
               WHEN (
                   prepre.status ILIKE '%IIT%'
                   OR prepre.status ILIKE '%stop%'
               )
                   AND (pre.status ILIKE '%ACTIVE%') THEN 'Active Restart'
               WHEN prepre.status ILIKE '%ACTIVE%'
                   AND pre.status ILIKE '%ACTIVE%' THEN 'Active'
               ELSE REPLACE(pre.status, '_', ' ')
           END
       ) AS previousStatus,
       CAST((
           CASE
               WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date
               WHEN prepre.status ILIKE '%out%' THEN prepre.status_date
               WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date
               WHEN pre.status ILIKE '%out%' THEN pre.status_date
               WHEN (
                   prepre.status ILIKE '%IIT%'
                   OR prepre.status ILIKE '%stop%'
               )
                   AND (pre.status ILIKE '%ACTIVE%') THEN pre.status_date
               WHEN prepre.status ILIKE '%ACTIVE%'
                   AND pre.status ILIKE '%ACTIVE%' THEN pre.status_date
               ELSE pre.status_date
           END
       ) AS DATE) AS previousStatusDate,
       (
           CASE
               WHEN prepre.status ILIKE '%DEATH%' THEN 'Died'
               WHEN prepre.status ILIKE '%out%' THEN 'Transferred Out'
               WHEN pre.status ILIKE '%DEATH%' THEN 'Died'
               WHEN pre.status ILIKE '%out%' THEN 'Transferred Out'
               WHEN ct.status ILIKE '%IIT%' THEN 'IIT'
               WHEN ct.status ILIKE '%out%' THEN 'Transferred Out'
               WHEN ct.status ILIKE '%DEATH%' THEN 'Died'
               WHEN (
                   pre.status ILIKE '%IIT%'
                   OR pre.status ILIKE '%stop%'
               )
                   AND (ct.status ILIKE '%ACTIVE%') THEN 'Active Restart'
               WHEN pre.status ILIKE '%ACTIVE%'
                   AND ct.status ILIKE '%ACTIVE%' THEN 'Active'
               ELSE REPLACE(ct.status, '_', ' ')
           END
       ) AS currentStatus,
       CAST((
           CASE
               WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date
               WHEN prepre.status ILIKE '%out%' THEN prepre.status_date
               WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date
               WHEN pre.status ILIKE '%out%' THEN pre.status_date
               WHEN ct.status ILIKE '%IIT%' THEN
                   CASE
                       WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%out%' OR pre.status ILIKE '%stop%') THEN pre.status_date
                       ELSE ct.status_date
                   END
               WHEN ct.status ILIKE '%stop%' THEN
                   CASE
                       WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%out%' OR pre.status ILIKE '%IIT%') THEN pre.status_date
                       ELSE ct.status_date
                   END
               WHEN ct.status ILIKE '%out%' THEN
                   CASE
                       WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%stop%' OR pre.status ILIKE '%IIT%') THEN pre.status_date
                       ELSE ct.status_date
                   END
               WHEN (
                   pre.status ILIKE '%IIT%'
                   OR pre.status ILIKE '%stop%'
               )
                   AND (ct.status ILIKE '%ACTIVE%') THEN ct.status_date
               WHEN pre.status ILIKE '%ACTIVE%'
                   AND ct.status ILIKE '%ACTIVE%' THEN ct.status_date
               ELSE ct.status_date
           END
       ) AS DATE) AS currentStatusDate,
       cvl.clientVerificationStatus,
       (
           CASE
               WHEN prepre.status ILIKE '%DEATH%' THEN FALSE
               WHEN prepre.status ILIKE '%out%' THEN FALSE
               WHEN pre.status ILIKE '%DEATH%' THEN FALSE
               WHEN pre.status ILIKE '%out%' THEN FALSE
               WHEN ct.status ILIKE '%IIT%' THEN FALSE
               WHEN ct.status ILIKE '%out%' THEN FALSE
               WHEN ct.status ILIKE '%DEATH%' THEN FALSE
               WHEN ct.status ILIKE '%stop%' THEN FALSE
               WHEN (nvd.age >= 15
                   AND nvd.regimen ILIKE '%DTG%'
                   AND bd.artstartdate + 91 < ?3) THEN TRUE
               WHEN (nvd.age >= 15
                   AND nvd.regimen NOT ILIKE '%DTG%'
                   AND bd.artstartdate + 181 < ?3) THEN TRUE
               WHEN (nvd.age <= 15 AND bd.artstartdate + 181 < ?3) THEN TRUE
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL
                   AND scd.dateofviralloadsamplecollection IS NULL
                   AND cvlr.dateofcurrentviralload IS NULL
                   AND CAST(bd.artstartdate AS DATE) + 181 < ?3 THEN TRUE
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL
                   AND scd.dateofviralloadsamplecollection IS NOT NULL
                   AND cvlr.dateofcurrentviralload IS NULL
                   AND CAST(bd.artstartdate AS DATE) + 91 < ?3 THEN TRUE
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000
                   AND (scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload OR scd.dateofviralloadsamplecollection IS NULL)
                   AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3 THEN TRUE
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000
                   AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload OR cvlr.dateofcurrentviralload IS NULL)
                   AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN TRUE
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000
                   AND (scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload OR scd.dateofviralloadsamplecollection IS NULL)
                   AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3 THEN TRUE
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000
                   AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload OR cvlr.dateofcurrentviralload IS NULL)
                   AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN TRUE
               ELSE FALSE
           END
       ) AS vlEligibilityStatus,
       CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) AS test,
       (
           CASE
               WHEN prepre.status ILIKE '%DEATH%' THEN NULL
               WHEN prepre.status ILIKE '%out%' THEN NULL
               WHEN pre.status ILIKE '%DEATH%' THEN NULL
               WHEN pre.status ILIKE '%out%' THEN NULL
               WHEN ct.status ILIKE '%IIT%' THEN NULL
               WHEN ct.status ILIKE '%out%' THEN NULL
               WHEN ct.status ILIKE '%DEATH%' THEN NULL
               WHEN ct.status ILIKE '%stop%' THEN NULL
               WHEN (nvd.age >= 15
                   AND nvd.regimen ILIKE '%DTG%'
                   AND bd.artstartdate + 91 < ?3) THEN CAST(bd.artstartdate + 91 AS DATE)
               WHEN (nvd.age >= 15
                   AND nvd.regimen NOT ILIKE '%DTG%'
                   AND bd.artstartdate + 181 < ?3) THEN CAST(bd.artstartdate + 181 AS DATE)
               WHEN (nvd.age <= 15 AND bd.artstartdate + 181 < ?3) THEN CAST(bd.artstartdate + 181 AS DATE)
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL
                   AND scd.dateofviralloadsamplecollection IS NULL
                   AND cvlr.dateofcurrentviralload IS NULL
                   AND CAST(bd.artstartdate AS DATE) + 181 < ?3 THEN CAST(bd.artstartdate AS DATE) + 181
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL
                   AND scd.dateofviralloadsamplecollection IS NOT NULL
                   AND cvlr.dateofcurrentviralload IS NULL
                   AND CAST(bd.artstartdate AS DATE) + 91 < ?3 THEN CAST(bd.artstartdate AS DATE) + 91
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000
                   AND (scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload OR scd.dateofviralloadsamplecollection IS NULL)
                   AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3 THEN CAST(cvlr.dateofcurrentviralload AS DATE) + 181
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000
                   AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload OR cvlr.dateofcurrentviralload IS NULL)
                   AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN CAST(scd.dateofviralloadsamplecollection AS DATE) + 91
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000
                   AND (scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload OR scd.dateofviralloadsamplecollection IS NULL)
                   AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3 THEN CAST(cvlr.dateofcurrentviralload AS DATE) + 91
               WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000
                   AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload OR cvlr.dateofcurrentviralload IS NULL)
                   AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN CAST(scd.dateofviralloadsamplecollection AS DATE) + 91
               ELSE NULL
           END
       ) AS dateOfVlEligibilityStatus,
       (
           CASE
               WHEN cd.cd4lb IS NOT NULL THEN  cd.cd4lb
               WHEN ccd.cd_4 IS NOT NULL THEN CAST(ccd.cd_4 as VARCHAR)
               ELSE NULL
           END
       ) as lastCd4Count,
       (
           CASE
               WHEN cd.dateOfCd4Lb IS NOT NULL THEN  CAST(cd.dateOfCd4Lb as DATE)
               WHEN ccd.visit_date IS NOT NULL THEN CAST(ccd.visit_date as DATE)
               ELSE NULL
           END
       ) as dateOfLastCd4Count,
       INITCAP(cm.caseManager) AS caseManager
FROM bio_data bd
LEFT JOIN patient_lga p_lga ON p_lga.personUuid11 = bd.personUuid
LEFT JOIN pharmacy_details_regimen pdr ON pdr.person_uuid40 = bd.personUuid
LEFT JOIN current_clinical c ON c.person_uuid10 = bd.personUuid
LEFT JOIN sample_collection_date scd ON scd.person_uuid120 = bd.personUuid
LEFT JOIN current_vl_result cvlr ON cvlr.person_uuid130 = bd.personUuid
LEFT JOIN labCD4 cd ON cd.cd4_person_uuid = bd.personUuid
LEFT JOIN careCardCD4 ccd ON ccd.cccd4_person_uuid = bd.personUuid
LEFT JOIN eac e ON e.person_uuid50 = bd.personUuid
LEFT JOIN biometric b ON b.person_uuid60 = bd.personUuid
LEFT JOIN current_regimen ca ON ca.person_uuid70 = bd.personUuid
LEFT JOIN ipt ipt ON ipt.personUuid80 = bd.personUuid
LEFT JOIN cervical_cancer cc ON cc.person_uuid90 = bd.personUuid
LEFT JOIN ovc ov ON ov.personUuid100 = bd.personUuid
LEFT JOIN current_status ct ON ct.cuPersonUuid = bd.personUuid
LEFT JOIN previous pre ON pre.prePersonUuid = ct.cuPersonUuid
LEFT JOIN previous_previous prepre ON prepre.prePrePersonUuid = ct.cuPersonUuid
LEFT JOIN naive_vl_data nvd ON nvd.nvl_person_uuid = bd.personUuid
LEFT JOIN tb_sample_collection tbSample ON tbSample.personTbSample = bd.personUuid
LEFT JOIN tbTreatment tbTment ON tbTment.tbTreatmentPersonUuid = bd.personUuid
LEFT JOIN current_tb_result tbResult ON tbResult.personTbResult = bd.personUuid
LEFT JOIN crytococal_antigen crypt ON crypt.personuuid12 = bd.personUuid
LEFT JOIN tbstatus tbS ON tbS.personUuid133 = bd.personUuid
LEFT JOIN tblam tbl ON tbl.personuuidtblam = bd.personUuid
LEFT JOIN case_manager cm ON cm.caseperson = bd.personUuid
LEFT JOIN client_verification cvl ON cvl.person_uuid = bd.personUuid