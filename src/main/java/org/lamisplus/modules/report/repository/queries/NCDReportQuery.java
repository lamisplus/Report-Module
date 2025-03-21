package org.lamisplus.modules.report.repository.queries;

public class NCDReportQuery {

        public static final String NCD_REPORT_QUERY = "WITH bio_data AS (SELECT DISTINCT (p.uuid) AS personUuid, " +
                "                                    facility_state.name AS state, " +
                "                                    facility_lga.name AS lga, " +
                "                                    p.facility_id as facilityId, " +
                "                                    facility.name AS facilityName, " +
                "                                    p.uuid as patientId, " +
                "                                    p.hospital_number AS hospitalNumber, " +
                "                                    h.unique_id as uniqueId, " +
                "                                    p.surname, " +
                "                                    p.other_name, " +
                "                                    p.date_of_birth AS dateOfBirth, " +
                "                                    EXTRACT(YEAR FROM  AGE(?3, date_of_birth)) AS age, " +
                "                                    INITCAP(p.sex) AS Sex, " +
                "                                    p.marital_status ->> 'display' as maritalStatus, " +
                "                                    p.education ->> 'display' as education, " +
                "                                    p.employment_status ->> 'display' as occupation, " +
                "                                    p.address->'address'->0->'line'->>0 as address, " +
                "                                    p.contact_point->'contactPoint'->0->>'value' as phoneNumber, " +
                "                                    boui.code AS datimId, " +
                "                                    tgroup.display AS targetGroup, " +
                "                                    eSetting.display AS enrollmentSetting, " +
                "                                    hac.visit_date AS artStartDate, " +
                "                                    hr.description AS regimenAtStart, " +
                "                                    p.date_of_registration as dateOfRegistration, " +
                "                                    h.date_of_registration as dateOfEnrollment, " +
                "                                    h.ovc_number AS ovcUniqueId, " +
                "                                    h.house_hold_number AS householdUniqueNo, " +
                "                                    ecareEntry.display AS careEntry, " +
                "                                    hrt.description AS regimenLineAtStart " +
                "                  FROM patient_person p " +
                "                        INNER JOIN base_organisation_unit facility ON facility.id = facility_id " +
                "                        INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id " +
                "                        INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id " +
                "                        INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID' " +
                "                        INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid " +
                "                        LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id " +
                "                        LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id " +
                "                        LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id " +
                "                        INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid " +
                "                                                               AND hac.archived = 0 " +
                "                        INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id " +
                "                        INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id " +
                "                  WHERE " +
                "                      h.archived = 0 " +
                "                    AND p.archived = 0 " +
                "                    AND h.facility_id = ?1 " +
                "                    AND hac.is_commencement = TRUE " +
                "                    AND hac.visit_date >= ?2 " +
                "                    AND hac.visit_date < ?3 " +
                "                  ), " +
                "    patient_residence AS (SELECT DISTINCT ON (personUuid) " +
                "                                   personUuid AS personUuid11, " +
                "                                   CASE WHEN (lgaAddr ~ '^[0-9\\\\.]+$') = TRUE " +
                "                                       THEN (SELECT name FROM base_organisation_unit WHERE id = cast(lgaAddr AS INT)) " +
                "                                       ELSE (SELECT name FROM base_organisation_unit WHERE id = cast(facilityLga AS INT)) END AS lgaOfResidence, " +
                "                                   CASE WHEN (stateAddr ~ '^[0-9\\\\.]+$') = TRUE " +
                "                                       THEN (SELECT name FROM base_organisation_unit WHERE id = cast(stateAddr AS INT)) " +
                "                                       ELSE (SELECT name FROM base_organisation_unit WHERE id = cast(facilityLga AS INT)) END AS stateOfResidence " +
                "                               FROM (SELECT pp.uuid AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga, " +
                "                                            (jsonb_array_elements(pp.address->'address')->>'district') AS lgaAddr, " +
                "                                            (jsonb_array_elements(pp.address->'address')->>'stateId') AS stateAddr " +
                "                                     FROM patient_person pp " +
                "                                         LEFT JOIN base_organisation_unit facility_lga " +
                "                                             ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER)) " +
                "                                   dt), " +
                "    pregnancy_status as ( " +
                "        select distinct on (person_uuid) person_uuid as person_uuid_ps, visit_date, " +
                "                                         case " +
                "                                             when pregnancy_status = 'PREGANACY_STATUS_PREGNANT' OR pregnancy_status = 'Pregnant' THEN 'Pregnant' " +
                "                                             end as pregnancyStatus " +
                "        from hiv_art_clinical order by person_uuid, visit_date desc " +
                "    ), " +
                "    pharmacy_details_regimen AS (SELECT * FROM " +
                "                                              (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid41 ORDER BY pr1.lastPickupDate DESC) AS rnk30 " +
                "                                               FROM " +
                "                                                   (SELECT p.person_uuid AS person_uuid41, " +
                "                                                            COALESCE(ds_model.display, p.dsd_model_type) AS dsdModel, " +
                "                                                            p.visit_date AS lastPickupDate, " +
                "                                                            r.description AS currentARTRegimen, " +
                "                                                            rt.description AS currentRegimenLine, " +
                "                                                            p.next_appointment AS nextPickupDate, " +
                "                                                            CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfARVRefill " +
                "                                                     FROM public.hiv_art_pharmacy p " +
                "                                                         INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id " +
                "                                                         INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id " +
                "                                                         INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id " +
                "                                                         LEFT JOIN base_application_codeset ds_model on ds_model.code = p.dsd_model_type " +
                "                                                     WHERE r.regimen_type_id in (1,2,3,4,14) " +
                "                                                     AND  p.archived = 0 " +
                "                                                     AND  p.facility_id = ?1 " +
                "                                                     AND  p.visit_date >= ?2 " +
                "                                                     AND  p.visit_date  < ?3) AS pr1 " +
                "                                               ) AS pr2 " +
                "                                          WHERE pr2.rnk30 = 1 " +
                "                                          ), " +
                "    current_status AS ( " +
                "        SELECT DISTINCT ON (pharmacy.person_uuid) " +
                "            pharmacy.person_uuid AS cuPersonUuid, " +
                "            ( " +
                "                CASE " +
                "                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died' " +
                "                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' " +
                "                          OR stat.hiv_status ILIKE '%out%' "+
                "                          OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.hiv_status " +
                "                    ELSE pharmacy.status " +
                "                END " +
                "            ) AS status, " +
                "            ( " +
                "                CASE " +
                "                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN stat.status_date " +
                "                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' " +
                "                               OR stat.hiv_status ILIKE '%out%' " +
                "                               OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.status_date " +
                "                    ELSE pharmacy.visit_date " +
                "                END " +
                "            ) AS status_date " +
                "        FROM ( " +
                "            SELECT " +
                "                ( " +
                "                    CASE " +
                "                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN 'IIT' " +
                "                        ELSE 'Active' " +
                "                    END " +
                "                ) status, " +
                "                ( " +
                "                    CASE " +
                "                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day' " +
                "                        ELSE hp.visit_date " +
                "                    END " +
                "                ) AS visit_date, " +
                "                hp.person_uuid, MAXDATE " +
                "            FROM " +
                "                hiv_art_pharmacy hp " +
                "            INNER JOIN ( " +
                "                SELECT hap.person_uuid, hap.visit_date AS MAXDATE, " +
                "                       ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3 " +
                "                FROM public.hiv_art_pharmacy hap " +
                "                INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id " +
                "                INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0 " +
                "                INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id " +
                "                INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id " +
                "                WHERE r.regimen_type_id IN (1,2,3,4,14) " +
                "                AND hap.archived = 0 " +
                "                AND hap.visit_date < ?3 " +
                "            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid AND MAX.rnkkk3 = 1 " +
                "            WHERE hp.archived = 0 AND hp.visit_date < ?3 " +
                "        ) pharmacy " +
                "        LEFT JOIN ( " +
                "            SELECT " +
                "                hst.hiv_status, " +
                "                hst.person_id, " +
                "                hst.status_date " +
                "            FROM ( " +
                "                SELECT * FROM ( " +
                "                    SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death, hiv_status, " +
                "                                    ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC) AS row_number " +
                "                    FROM hiv_status_tracker WHERE archived = 0 AND status_date <= ?3 " +
                "                ) s " +
                "                WHERE s.row_number = 1 " +
                "            ) hst " +
                "            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id " +
                "            WHERE hst.status_date < ?3 " +
                "        ) stat ON stat.person_id = pharmacy.person_uuid), " +
                "    prev_hypertensive AS ( " +
                "    SELECT * FROM ( " +
                "            SELECT DISTINCT ON (person_uuid) " +
                "                person_uuid AS personUuidPH, " +
                "                MIN(date_of_observation) AS datePrevHypertensive " +
                "            FROM " +
                "                hiv_observation " +
                "            WHERE " +
                "                type = 'Chronic Care' " +
                "                AND (data->'chronicCondition'->>'firstTimeHypertensive' = 'Yes') " +
                "                AND archived = 0 " +
                "            GROUP BY " +
                "                person_uuid " +
                "            ORDER BY " +
                "                person_uuid, " +
                "                datePrevHypertensive " +
                "        ) AS ph " +
                "    ), " +
                "    new_hypertensive AS ( " +
                "        SELECT * FROM ( " +
                "            SELECT DISTINCT ON (person_uuid) " +
                "                person_uuid AS personUuidNH, " +
                "                MAX(date_of_observation) AS dateNewlyHypertensive " +
                "            FROM " +
                "                hiv_observation " +
                "            WHERE " +
                "                type = 'Chronic Care' " +
                "                AND (data->'chronicCondition'->>'hypertensive' = 'Yes') " +
                "                AND archived = 0 " +
                "            GROUP BY " +
                "                person_uuid " +
                "            ORDER BY " +
                "                person_uuid, " +
                "                dateNewlyHypertensive desc " +
                "        ) AS nh " +
                "    ), " +
                "    baseline_weight_and_pressure AS ( " +
                "        SELECT DISTINCT ON (h.person_uuid) " +
                "            h.person_uuid AS pUuid, " +
                "            NULL AS baselineWaistCircumference, " +
                "            h.data->'physicalExamination'->>'bodyWeight' AS baselineWeight, " +
                "            CASE " +
                "                WHEN (h.data->'physicalExamination'->>'height') IS NOT NULL AND (h.data->'physicalExamination'->>'height') ~ '^\\\\d+\\\\.?\\\\d*$' " +
                "                THEN CAST(h.data->'physicalExamination'->>'height' AS DECIMAL(5, 2)) " +
                "                ELSE NULL " +
                "            END AS baselineHeight, " +
                "            CASE " +
                "                WHEN (h.data->'physicalExamination'->>'height') IS NOT NULL AND (h.data->'physicalExamination'->>'height') ~ '^\\\\d+\\\\.?\\\\d*$' AND (h.data->'physicalExamination'->>'bodyWeight') IS NOT NULL AND (h.data->'physicalExamination'->>'bodyWeight') ~ '^\\\\d+\\\\.?\\\\d*$' " +
                "                THEN ROUND( " +
                "                    CAST(h.data->'physicalExamination'->>'bodyWeight' AS DECIMAL(5, 0)) / " +
                "                    POWER(CAST(h.data->'physicalExamination'->>'height' AS DECIMAL(5, 2)) / 100, 2), 2 " +
                "                ) " +
                "                ELSE NULL " +
                "            END AS baselineBMI, " +
                "            h.data->'physicalExamination'->>'systolic' AS baselineSystolic, " +
                "            h.data->'physicalExamination'->>'diastolic' AS baselineDiastolic " +
                "        FROM " +
                "            hiv_observation h " +
                "        JOIN ( " +
                "            SELECT " +
                "                person_uuid, " +
                "                MIN(date_of_observation) AS min_date " +
                "            FROM " +
                "                hiv_observation " +
                "            WHERE " +
                "                hiv_observation.date_of_observation IS NOT NULL " +
                "            GROUP BY " +
                "                person_uuid " +
                "        ) AS min_dates ON h.person_uuid = min_dates.person_uuid AND h.date_of_observation = min_dates.min_date " +
                "        WHERE " +
                "            h.date_of_observation = min_dates.min_date AND h.archived = 0 " +
                "    ), " +
                "    current_weight_and_pressure AS ( " +
                "        SELECT DISTINCT ON (tvs.person_uuid) " +
                "               tvs.person_uuid as person_uuid_cw, " +
                "               tvs.body_weight, " +
                "               tvs.capture_date AS currentWeightDate, " +
                "               tvs.height, " +
                "               tvs.capture_date AS currentHeightDate, " +
                "               NULL AS currentWaistCircumference, " +
                "               NULL AS currentWaistCircumferenceDate, " +
                "               NULL AS waistHipRatio, " +
                "               NULL AS WaistHipRatioDate, " +
                "               (CASE \n" +
                "               WHEN (tvs.height IS NULL OR tvs.height != 0) THEN NULL ELSE\n" +
                "               ROUND(\n" +
                "               CAST(NULLIF(tvs.body_weight, 0) AS DECIMAL(5, 0)) / \n" +
                "               POWER( CAST(NULLIF(tvs.height, 0) AS DECIMAL(5, 0)) / 100, 2 ),  2)\n" +
                "                   END) AS currentBMI, " +
                "               CASE WHEN tvs.body_weight IS NOT NULL THEN tvs.capture_date ELSE NULL END AS currentBMIDate, " +
                "               tvs.diastolic, " +
                "               CASE WHEN tvs.diastolic IS NOT NULL THEN tvs.capture_date ELSE NULL END AS currentDiastolicDate, " +
                "               tvs.systolic, " +
                "               CASE WHEN tvs.systolic IS NOT NULL THEN tvs.capture_date ELSE NULL END AS currentSystolicDate " +
                "        FROM triage_vital_sign tvs " +
                "        JOIN ( " +
                "            SELECT person_uuid, MAX(capture_date) AS max_date " +
                "            FROM triage_vital_sign " +
                "            GROUP BY person_uuid " +
                "        ) max_dates ON tvs.person_uuid = max_dates.person_uuid " +
                "        WHERE tvs.capture_date = max_dates.max_date " +
                "    ), " +
                "    baseline_tests as ( " +
                "        WITH base AS ( " +
                "            SELECT lt.patient_uuid as patient_uuid_bt, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 31 THEN lr.result_report END) AS baselineFastingBloodSugar, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 13 THEN lr.result_report END) AS baselineRandomBloodSugar, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 23 THEN lr.result_report END) AS baselineBloodTotalCholesterol, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 25 THEN lr.result_report END) AS baselineHDL, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 24 THEN lr.result_report END) AS baselineLDL, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 17 THEN lr.result_report END) AS baselineSodium, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 11 THEN lr.result_report END) AS baselinePotassium, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 20 THEN lr.result_report END) AS baselineUrea, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 12 THEN lr.result_report END) AS baselineCreatinine " +
                "            FROM laboratory_test lt " +
                "            JOIN laboratory_result lr ON lr.patient_uuid = lt.patient_uuid AND lr.test_id = lt.id " +
                "            JOIN laboratory_labtest llt ON lt.lab_test_id = llt.id " +
                "            WHERE lt.archived = 0 AND lr.archived = 0 AND lr.date_result_received < ?3 " +
                "            GROUP BY lt.patient_uuid " +
                "        ) " +
                "        SELECT * FROM base " +
                "    ), " +
                "    current_tests as ( " +
                "        WITH current AS ( " +
                "            SELECT lt.patient_uuid as patient_uuid_ct, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 31 THEN lr.result_report END) AS currentFastingBloodSugar, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 31 THEN lr.date_result_received END) AS dateCurrentFastingBloodSugar, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 13 THEN lr.result_report END) AS currentRandomBloodSugar, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 13 THEN lr.date_result_received END) AS dateCurrentRandomBloodSugar, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 23 THEN lr.result_report END) AS currentBloodTotalCholesterol, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 23 THEN lr.date_result_received END) AS dateCurrentBloodTotalCholesterol, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 25 THEN lr.result_report END) AS currentHDL, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 25 THEN lr.date_result_received END) AS dateCurrentHDL, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 24 THEN lr.result_report END) AS currentLDL, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 24 THEN lr.date_result_received END) AS dateCurrentLDL, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 17 THEN lr.result_report END) AS currentSodium, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 17 THEN lr.date_result_received END) AS dateCurrentSodium, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 11 THEN lr.result_report END) AS currentPotassium, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 11 THEN lr.date_result_received END) AS dateCurrentPotassium, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 20 THEN lr.result_report END) AS currentUrea, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 20 THEN lr.date_result_received END) AS dateCurrentUrea, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 12 THEN lr.result_report END) AS currentCreatinine, " +
                "                   MAX(CASE WHEN lt.lab_test_id = 12 THEN lr.date_result_received END) AS dateCurrentCreatinine " +
                "            FROM laboratory_test lt " +
                "            JOIN laboratory_labtest llt ON lt.lab_test_id = llt.id " +
                "            JOIN laboratory_result lr ON lr.patient_uuid = lt.patient_uuid AND lr.test_id = lt.id " +
                "            WHERE lt.archived = 0 AND lr.archived = 0 AND lr.date_result_received < ?3 " +
                "            GROUP BY lt.patient_uuid " +
                "        ) " +
                "        SELECT * FROM current " +
                "    ), " +
                "    current_vl_result AS ( " +
                "        SELECT * FROM " +
                "           (SELECT CAST(ls.date_sample_collected AS DATE ) AS dateOfViralLoadSample, " +
                "                    sm.patient_uuid AS person_uuid130 , " +
                "                    sm.facility_id AS vlFacility, " +
                "                    sm.archived AS vlArchived, " +
                "                    acode.display AS viralLoadIndication, " +
                "                    sm.result_reported AS currentViralLoad, " +
                "                    CAST(sm.date_result_reported AS DATE) AS dateOfCurrentViralLoad, " +
                "                    ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) AS rank2 " +
                "            FROM public.laboratory_result  sm " +
                "                INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id " +
                "                INNER JOIN public.laboratory_sample ls on ls.test_id = lt.id " +
                "                INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication " +
                "            WHERE lt.lab_test_id = 16 " +
                "            AND  lt.viral_load_indication !=719 " +
                "            AND sm. date_result_reported IS NOT NULL " +
                "            AND sm.date_result_reported <= ?3 " +
                "            AND sm.result_reported IS NOT NULL " +
                "            ) AS vl_result " +
                "       WHERE vl_result.rank2 = 1 " +
                "       AND (vl_result.vlArchived = 0 OR vl_result.vlArchived IS null) " +
                "       AND  vl_result.vlFacility = ?1 " +
                "    ), " +
                "    start_htn_regimen AS ( " +
                "        SELECT * FROM " +
                "              (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid42 ORDER BY pr1.htnStartDate) AS rnk31 " +
                "                           FROM " +
                "                               (SELECT p.person_uuid AS person_uuid42, " +
                "                                        p.visit_date AS htnStartDate, " +
                "                                        r.description AS htnStartRegimen " +
                "                                 FROM hiv_art_pharmacy p " +
                "                                     INNER JOIN hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id " +
                "                                     INNER JOIN hiv_regimen r on r.id = pr.regimens_id " +
                "                                     INNER JOIN hiv_regimen_type rt on rt.id = r.regimen_type_id " +
                "                                 WHERE r.regimen_type_id in (61) " +
                "                                 AND  p.archived = 0 " +
                "                                 AND  p.facility_id = ?1 " +
                "                                 AND  p.visit_date >= ?2 " +
                "                                 AND  p.visit_date  < ?3 " +
                "                               ) AS pr1 " +
                "              ) AS pr2 " +
                "          WHERE pr2.rnk31 = 1 " +
                "    ), " +
                "    current_htn_regimen AS ( " +
                "        SELECT person_uuid43, lastHTNPickupDate, currentHTNRegimen, monthsOfHTNRefill,  " +
                "           NULL AS currentHTNStatus, " +
                "           NULL AS dateCurrentHTNStatus, " +
                "           NULL AS reasonsLTFU_IIT FROM " +
                "          (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid43 ORDER BY pr1.lastHTNPickupDate DESC) AS rnk41 " +
                "                       FROM " +
                "                           (SELECT p.person_uuid AS person_uuid43, " +
                "                                    p.visit_date AS lastHTNPickupDate, " +
                "                                    r.description AS currentHTNRegimen, " +
                "                                    CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfHTNRefill " +
                "                             FROM hiv_art_pharmacy p " +
                "                                 INNER JOIN hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id " +
                "                                 INNER JOIN hiv_regimen r on r.id = pr.regimens_id " +
                "                                 INNER JOIN hiv_regimen_type rt on rt.id = r.regimen_type_id " +
                "                             WHERE r.regimen_type_id in (61) " +
                "                             AND  p.archived = 0 " +
                "                             AND  p.facility_id = ?1 " +
                "                             AND  p.visit_date >= ?2 " +
                "                             AND  p.visit_date  < ?3) AS pr1 " +
                "                       ) AS pr2 " +
                "                  WHERE pr2.rnk41 = 1 " +
                "    ), " +
                "    regimenSwitchSubstitutionDate AS ( " +
                "        WITH regimen AS ( " +
                "            SELECT p.person_uuid AS person_uuid40, " +
                "                   p.visit_date AS lastPickupDate, " +
                "                   r.description AS currentARTRegimen " +
                "            FROM public.hiv_art_pharmacy p " +
                "            INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id " +
                "            INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id " +
                "            INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id " +
                "            WHERE r.regimen_type_id IN (61) " +
                "            AND p.archived = 0 " +
                "            AND p.facility_id = ?1 " +
                "            AND p.visit_date >= ?2 " +
                "            AND p.visit_date < ?3 " +
                "        ), " +
                "        changes AS ( " +
                "            SELECT person_uuid40, " +
                "                   lastPickupDate, " +
                "                   currentARTRegimen, " +
                "                   CASE " +
                "                       WHEN lag(currentARTRegimen) OVER (PARTITION BY person_uuid40 ORDER BY lastPickupDate) = currentARTRegimen THEN null " +
                "                       ELSE lastPickupDate " +
                "                   END AS change_date, " +
                "                   ROW_NUMBER() OVER (PARTITION BY person_uuid40 ORDER BY lastPickupDate DESC) AS change_rank " +
                "            FROM regimen " +
                "        ) " +
                "        SELECT person_uuid40, change_date AS dateRegimenSwitch " +
                "        FROM changes " +
                "        WHERE change_rank = 1 AND change_date IS NOT NULL " +
                "    ) " +
                "select * from bio_data bd " +
                "        left join patient_residence pr on bd.personUuid = pr.personUuid11 " +
                "        left join pregnancy_status ps on bd.personUuid = ps.person_uuid_ps " +
                "        left join current_status cs on cs.cuPersonUuid = bd.personUuid " +
                "        left join regimenSwitchSubstitutionDate rSSD on rSSD.person_uuid40 = bd.personUuid " +
                "        left join pharmacy_details_regimen pdr on pdr.person_uuid41 = bd.personUuid " +
                "        left join prev_hypertensive prev_hyp on prev_hyp.personUuidPH = bd.personUuid " +
                "        left join new_hypertensive new_hyp on new_hyp.personUuidNH = bd.personUuid " +
                "        left join baseline_weight_and_pressure bp on bp.pUuid = bd.personUuid " +
                "        left join baseline_tests blt on blt.patient_uuid_bt = bd.personUuid " +
                "        left join start_htn_regimen shr on shr.person_uuid42 = bd.personUuid " +
                "        left join current_htn_regimen chr on chr.person_uuid43 = bd.personUuid " +
                "        left join current_weight_and_pressure cp on cp.person_uuid_cw = bd.personUuid " +
                "        left join current_tests cut on cut.patient_uuid_ct = bd.personUuid " +
                "        left join current_vl_result cvl on cvl.person_uuid130 = bd.personUuid ";

}
