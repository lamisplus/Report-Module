package org.lamisplus.modules.report.repository.queries;

public class NCDReportQuery {
    public static final String NCD_REPORT_QUERY = "WITH bio_data AS (SELECT DISTINCT (p.uuid) AS personUuid,\n" +
            "                                    facility_state.name AS state,\n" +
            "                                    facility_lga.name AS lga,\n" +
            "                                    p.facility_id as facilityId,\n" +
            "                                    facility.name AS facilityName,\n" +
            "                                    p.uuid as patientId,\n" +
            "                                    p.hospital_number AS hospitalNumber,\n" +
            "                                    h.unique_id as uniqueId,\n" +
            "                                    p.surname,\n" +
            "                                    p.other_name,\n" +
            "                                    p.date_of_birth AS dateOfBirth,\n" +
            "                                    EXTRACT(YEAR FROM  AGE(NOW(), date_of_birth)) AS age,\n" +
            "                                    INITCAP(p.sex) AS Sex,\n" +
            "                                    p.marital_status ->> 'display' as maritalStatus,\n" +
            "                                    p.education ->> 'display' as education,\n" +
            "                                    p.employment_status ->> 'display' as occupation,\n" +
            "                                    p.address->'address'->0->'line'->>0 as address,\n" +
            "                                    p.contact_point -> 'contactPoint' -> 0 -> 'value' as phoneNumber,\n" +
            "                                    boui.code AS datimId,\n" +
            "                                    tgroup.display AS targetGroup,\n" +
            "                                    eSetting.display AS enrollmentSetting,\n" +
            "                                    hac.visit_date AS artStartDate,\n" +
            "                                    hr.description AS regimenAtStart,\n" +
            "                                    p.date_of_registration as dateOfRegistration,\n" +
            "                                    h.date_of_registration as dateOfEnrollment,\n" +
            "                                    h.ovc_number AS ovcUniqueId,\n" +
            "                                    h.house_hold_number AS householdUniqueNo,\n" +
            "                                    ecareEntry.display AS careEntry,\n" +
            "                                    hrt.description AS regimenLineAtStart\n" +
            "                  FROM patient_person p\n" +
            "                        INNER JOIN base_organisation_unit facility ON facility.id = facility_id\n" +
            "                        INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "                        INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "                        INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID'\n" +
            "                        INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            "                        LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id\n" +
            "                        LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id\n" +
            "                        LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id\n" +
            "                        INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid\n" +
            "                                                               AND hac.archived = 0\n" +
            "                        INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id\n" +
            "                        INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id\n" +
            "                  WHERE\n" +
            "                      h.archived = 0\n" +
            "                    AND p.archived = 0\n" +
            "                    AND h.facility_id = ?1\n" +
            "                    AND hac.is_commencement = TRUE\n" +
            "                    AND hac.visit_date >= ?2\n" +
            "                    AND hac.visit_date < ?3\n" +
            "                  ),\n" +
            "\n" +
            "--Residence lga and state\n" +
            "    patient_residence AS (SELECT DISTINCT ON (personUuid)\n" +
            "                                   personUuid AS personUuid11,\n" +
            "                                   CASE WHEN (lgaAddr ~ '^[0-9\\\\.]+$') = TRUE\n" +
            "                                       THEN (SELECT name FROM base_organisation_unit WHERE id = cast(lgaAddr AS INT))\n" +
            "                                       ELSE (SELECT name FROM base_organisation_unit WHERE id = cast(facilityLga AS INT)) END AS lgaOfResidence,\n" +
            "                                   CASE WHEN (stateAddr ~ '^[0-9\\\\.]+$') = TRUE\n" +
            "                                       THEN (SELECT name FROM base_organisation_unit WHERE id = cast(stateAddr AS INT))\n" +
            "                                       ELSE (SELECT name FROM base_organisation_unit WHERE id = cast(facilityLga AS INT)) END AS stateOfResidence\n" +
            "                               FROM (SELECT pp.uuid AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga,\n" +
            "                                            (jsonb_array_elements(pp.address->'address')->>'district') AS lgaAddr,\n" +
            "                                            (jsonb_array_elements(pp.address->'address')->>'stateId') AS stateAddr\n" +
            "                                     FROM patient_person pp\n" +
            "                                         LEFT JOIN base_organisation_unit facility_lga\n" +
            "                                             ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER))\n" +
            "                                   dt),\n" +
            "    pregnancy_status as (\n" +
            "        select distinct on (person_uuid) person_uuid, visit_date,\n" +
            "                                         case\n" +
            "                                             when pregnancy_status = 'PREGANACY_STATUS_PREGNANT' OR pregnancy_status = 'Pregnant' THEN 'Pregnant'\n" +
            "                                             end as pregnancyStatus\n" +
            "        from hiv_art_clinical order by person_uuid, visit_date desc\n" +
            "    ),\n" +
            "    pharmacy_details_regimen AS (SELECT * FROM\n" +
            "                                              (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid40 ORDER BY pr1.lastPickupDate DESC) AS rnk3\n" +
            "                                               FROM\n" +
            "                                                   (SELECT p.person_uuid AS person_uuid40,\n" +
            "                                                            COALESCE(ds_model.display, p.dsd_model_type) AS dsdModel,\n" +
            "                                                            p.visit_date AS lastPickupDate,\n" +
            "                                                            r.description AS currentARTRegimen,\n" +
            "                                                            rt.description AS currentRegimenLine,\n" +
            "                                                            p.next_appointment AS nextPickupDate,\n" +
            "                                                            CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfARVRefill\n" +
            "                                                     FROM public.hiv_art_pharmacy p\n" +
            "                                                         INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id\n" +
            "                                                         INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id\n" +
            "                                                         INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id\n" +
            "                                                         LEFT JOIN base_application_codeset ds_model on ds_model.code = p.dsd_model_type\n" +
            "                                                     WHERE r.regimen_type_id in (1,2,3,4,14)\n" +
            "                                                     AND  p.archived = 0\n" +
            "                                                     AND  p.facility_id = ?1\n" +
            "                                                     AND  p.visit_date >= ?2\n" +
            "                                                     AND  p.visit_date  < ?3) AS pr1\n" +
            "                                               ) AS pr2\n" +
            "                                          WHERE pr2.rnk3 = 1\n" +
            "                                          ),\n" +
            "    current_status AS (\n" +
            "        SELECT DISTINCT ON (pharmacy.person_uuid)\n" +
            "            pharmacy.person_uuid AS cuPersonUuid,\n" +
            "            (\n" +
            "                CASE\n" +
            "                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'\n" +
            "                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'\n" +
            "                                                                       OR stat.hiv_status ILIKE '%out%'\n" +
            "                                                                       OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.hiv_status\n" +
            "                    ELSE pharmacy.status\n" +
            "                END\n" +
            "            ) AS status,\n" +
            "            (\n" +
            "                CASE\n" +
            "                    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN stat.status_date\n" +
            "                    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'\n" +
            "                                                                       OR stat.hiv_status ILIKE '%out%'\n" +
            "                                                                       OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.status_date\n" +
            "                    ELSE pharmacy.visit_date\n" +
            "                END\n" +
            "            ) AS status_date\n" +
            "        FROM (\n" +
            "            SELECT\n" +
            "                (\n" +
            "                    CASE\n" +
            "                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN 'IIT'\n" +
            "                        ELSE 'Active'\n" +
            "                    END\n" +
            "                ) status,\n" +
            "                (\n" +
            "                    CASE\n" +
            "                        WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'\n" +
            "                        ELSE hp.visit_date\n" +
            "                    END\n" +
            "                ) AS visit_date,\n" +
            "                hp.person_uuid, MAXDATE\n" +
            "            FROM\n" +
            "                hiv_art_pharmacy hp\n" +
            "            INNER JOIN (\n" +
            "                SELECT hap.person_uuid, hap.visit_date AS MAXDATE,\n" +
            "                       ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3\n" +
            "                FROM public.hiv_art_pharmacy hap\n" +
            "                INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id\n" +
            "                INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0\n" +
            "                INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id\n" +
            "                INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id\n" +
            "                WHERE r.regimen_type_id IN (1,2,3,4,14)\n" +
            "                AND hap.archived = 0\n" +
            "                AND hap.visit_date < ?3\n" +
            "            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid AND MAX.rnkkk3 = 1\n" +
            "            WHERE hp.archived = 0 AND hp.visit_date < ?3\n" +
            "        ) pharmacy\n" +
            "        LEFT JOIN (\n" +
            "            SELECT\n" +
            "                hst.hiv_status,\n" +
            "                hst.person_id,\n" +
            "                hst.status_date\n" +
            "            FROM (\n" +
            "                SELECT * FROM (\n" +
            "                    SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death, hiv_status,\n" +
            "                                    ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC) AS row_number\n" +
            "                    FROM hiv_status_tracker WHERE archived = 0 AND status_date <= ?3\n" +
            "                ) s\n" +
            "                WHERE s.row_number = 1\n" +
            "            ) hst\n" +
            "            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "            WHERE hst.status_date < ?3\n" +
            "        ) stat ON stat.person_id = pharmacy.person_uuid),\n" +
            "    prev_hypertensive AS (\n" +
            "    SELECT * FROM (\n" +
            "            SELECT DISTINCT ON (person_uuid)\n" +
            "                person_uuid AS personUuid,\n" +
            "                MIN(date_of_observation) AS date_prev_hypertensive\n" +
            "            FROM\n" +
            "                hiv_observation\n" +
            "            WHERE\n" +
            "                type = 'Chronic Care'\n" +
            "                AND (data->'chronicCondition'->>'firstTimeHypertensive' = 'Yes')\n" +
            "                AND archived = 0\n" +
            "            GROUP BY\n" +
            "                person_uuid\n" +
            "            ORDER BY\n" +
            "                person_uuid,\n" +
            "                date_prev_hypertensive\n" +
            "        ) AS ph\n" +
            "    ),\n" +
            "    new_hypertensive AS (\n" +
            "        SELECT * FROM (\n" +
            "            SELECT DISTINCT ON (person_uuid)\n" +
            "                person_uuid AS personUuid,\n" +
            "                MAX(date_of_observation) AS date_newly_hypertensive\n" +
            "            FROM\n" +
            "                hiv_observation\n" +
            "            WHERE\n" +
            "                type = 'Chronic Care'\n" +
            "                AND (data->'chronicCondition'->>'hypertensive' = 'Yes')\n" +
            "                AND archived = 0\n" +
            "            GROUP BY\n" +
            "                person_uuid\n" +
            "            ORDER BY\n" +
            "                person_uuid,\n" +
            "                date_newly_hypertensive desc\n" +
            "        ) AS nh\n" +
            "    ),\n" +
            "    baseline_weight_and_pressure AS (\n" +
            "        SELECT DISTINCT ON (h.person_uuid)\n" +
            "            h.person_uuid AS pUuid,\n" +
            "            NULL AS baselineWaistCircumference,\n" +
            "            h.data->'physicalExamination'->>'bodyWeight' AS baselineWeight,\n" +
            "            CASE\n" +
            "                WHEN (h.data->'physicalExamination'->>'height') IS NOT NULL AND (h.data->'physicalExamination'->>'height') ~ '^\\\\d+\\\\.?\\\\d*$'\n" +
            "                THEN CAST(h.data->'physicalExamination'->>'height' AS DECIMAL(5, 2))\n" +
            "                ELSE NULL\n" +
            "            END AS baselineHeight,\n" +
            "            CASE\n" +
            "                WHEN (h.data->'physicalExamination'->>'height') IS NOT NULL AND (h.data->'physicalExamination'->>'height') ~ '^\\\\d+\\\\.?\\\\d*$' AND (h.data->'physicalExamination'->>'bodyWeight') IS NOT NULL AND (h.data->'physicalExamination'->>'bodyWeight') ~ '^\\\\d+\\\\.?\\\\d*$'\n" +
            "                THEN ROUND(\n" +
            "                    CAST(h.data->'physicalExamination'->>'bodyWeight' AS DECIMAL(5, 0)) /\n" +
            "                    POWER(CAST(h.data->'physicalExamination'->>'height' AS DECIMAL(5, 2)) / 100, 2), 2\n" +
            "                )\n" +
            "                ELSE NULL\n" +
            "            END AS baselineBMI,\n" +
            "            h.data->'physicalExamination'->>'systolic' AS baselineSystolic,\n" +
            "            h.data->'physicalExamination'->>'diastolic' AS baselineDiastolic\n" +
            "        FROM\n" +
            "            hiv_observation h\n" +
            "        JOIN (\n" +
            "            SELECT\n" +
            "                person_uuid,\n" +
            "                MIN(date_of_observation) AS min_date\n" +
            "            FROM\n" +
            "                hiv_observation\n" +
            "            WHERE\n" +
            "                hiv_observation.date_of_observation IS NOT NULL\n" +
            "            GROUP BY\n" +
            "                person_uuid\n" +
            "        ) AS min_dates ON h.person_uuid = min_dates.person_uuid AND h.date_of_observation = min_dates.min_date\n" +
            "        WHERE\n" +
            "            h.date_of_observation = min_dates.min_date AND h.archived = 0\n" +
            "    ),\n" +
            "    current_weight_and_pressure AS (\n" +
            "        SELECT DISTINCT ON (tvs.person_uuid)\n" +
            "               tvs.person_uuid,\n" +
            "               tvs.body_weight,\n" +
            "               tvs.capture_date AS currentWeightDate,\n" +
            "               tvs.height,\n" +
            "               tvs.capture_date AS currentHeightDate,\n" +
            "               NULL AS currentWaistCircumference,\n" +
            "               NULL AS currentWaistCircumferenceDate,\n" +
            "               NULL AS waistHipRatio,\n" +
            "               NULL AS WaistHipRatioDate,\n" +
            "               CASE\n" +
            "                   WHEN tvs.height != 0 THEN -- Add condition to check if height is not zero\n" +
            "                       ROUND(\n" +
            "                           CAST(tvs.body_weight AS DECIMAL(5, 0)) /\n" +
            "                           POWER(CAST(tvs.height AS DECIMAL(5, 0)) / 100, 2), 2\n" +
            "                       )\n" +
            "                   ELSE\n" +
            "                       NULL -- Handle division by zero by returning NULL for BMI\n" +
            "               END AS currentBMI,\n" +
            "               CASE WHEN tvs.body_weight IS NOT NULL THEN tvs.capture_date ELSE NULL END AS currentBMIDate,\n" +
            "               tvs.diastolic,\n" +
            "               CASE WHEN tvs.diastolic IS NOT NULL THEN tvs.capture_date ELSE NULL END AS currentDiastolicDate,\n" +
            "               tvs.systolic,\n" +
            "               CASE WHEN tvs.systolic IS NOT NULL THEN tvs.capture_date ELSE NULL END AS currentSystolicDate\n" +
            "        FROM triage_vital_sign tvs\n" +
            "        JOIN (\n" +
            "            SELECT person_uuid, MAX(capture_date) AS max_date\n" +
            "            FROM triage_vital_sign\n" +
            "            GROUP BY person_uuid\n" +
            "        ) max_dates ON tvs.person_uuid = max_dates.person_uuid\n" +
            "        WHERE tvs.capture_date = max_dates.max_date\n" +
            "    ),\n" +
            "    baseline_tests as (\n" +
            "        WITH base AS (\n" +
            "            SELECT lt.patient_uuid,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 31 THEN lr.result_report END) AS baselineFastingBloodSugar,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 13 THEN lr.result_report END) AS baselineRandomBloodSugar,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 23 THEN lr.result_report END) AS baselineBloodTotalCholesterol,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 25 THEN lr.result_report END) AS baselineHDL,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 24 THEN lr.result_report END) AS baselineLDL,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 17 THEN lr.result_report END) AS baselineSodium,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 11 THEN lr.result_report END) AS baselinePotassium,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 20 THEN lr.result_report END) AS baselineUrea,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 12 THEN lr.result_report END) AS baselineCreatinine\n" +
            "            FROM laboratory_test lt\n" +
            "            JOIN laboratory_result lr ON lr.patient_uuid = lt.patient_uuid AND lr.test_id = lt.id\n" +
            "            JOIN laboratory_labtest llt ON lt.lab_test_id = llt.id\n" +
            "            WHERE lt.archived = 0 AND lr.archived = 0 AND lr.date_result_received < ?3\n" +
            "            GROUP BY lt.patient_uuid\n" +
            "        )\n" +
            "        SELECT * FROM base\n" +
            "    ),\n" +
            "    current_tests as (\n" +
            "        WITH current AS (\n" +
            "            SELECT lt.patient_uuid,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 31 THEN lr.result_report END) AS currentFastingBloodSugar,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 31 THEN lr.date_result_received END) AS dateCurrentFastingBloodSugar,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 13 THEN lr.result_report END) AS currentRandomBloodSugar,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 13 THEN lr.date_result_received END) AS dateCurrentRandomBloodSugar,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 23 THEN lr.result_report END) AS currentBloodTotalCholesterol,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 23 THEN lr.date_result_received END) AS dateCurrentBloodTotalCholesterol,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 25 THEN lr.result_report END) AS currentHDL,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 25 THEN lr.date_result_received END) AS dateCurrentHDL,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 24 THEN lr.result_report END) AS currentLDL,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 24 THEN lr.date_result_received END) AS dateCurrentLDL,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 17 THEN lr.result_report END) AS currentSodium,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 17 THEN lr.date_result_received END) AS dateCurrentSodium,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 11 THEN lr.result_report END) AS currentPotassium,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 11 THEN lr.date_result_received END) AS dateCurrentPotassium,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 20 THEN lr.result_report END) AS currentUrea,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 20 THEN lr.date_result_received END) AS dateCurrentUrea,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 12 THEN lr.result_report END) AS currentCreatinine,\n" +
            "                   MAX(CASE WHEN lt.lab_test_id = 12 THEN lr.date_result_received END) AS dateCurrentCreatinine\n" +
            "            FROM laboratory_test lt\n" +
            "            JOIN laboratory_labtest llt ON lt.lab_test_id = llt.id\n" +
            "            JOIN laboratory_result lr ON lr.patient_uuid = lt.patient_uuid AND lr.test_id = lt.id\n" +
            "            WHERE lt.archived = 0 AND lr.archived = 0 AND lr.date_result_received < ?3\n" +
            "            GROUP BY lt.patient_uuid\n" +
            "        )\n" +
            "        SELECT * FROM current\n" +
            "    ),\n" +
            "    current_vl_result AS (\n" +
            "        SELECT * FROM\n" +
            "           (SELECT CAST(ls.date_sample_collected AS DATE ) AS dateOfViralLoadSample,\n" +
            "                    sm.patient_uuid AS person_uuid130 ,\n" +
            "                    sm.facility_id AS vlFacility,\n" +
            "                    sm.archived AS vlArchived,\n" +
            "                    acode.display AS viralLoadIndication,\n" +
            "                    sm.result_reported AS currentViralLoad,\n" +
            "                    CAST(sm.date_result_reported AS DATE) AS dateOfCurrentViralLoad,\n" +
            "                    ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) AS rank2\n" +
            "            FROM public.laboratory_result  sm\n" +
            "                INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "                INNER JOIN public.laboratory_sample ls on ls.test_id = lt.id\n" +
            "                INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication\n" +
            "            WHERE lt.lab_test_id = 16\n" +
            "            AND  lt.viral_load_indication !=719\n" +
            "            AND sm. date_result_reported IS NOT NULL\n" +
            "            AND sm.date_result_reported <= ?3\n" +
            "            AND sm.result_reported IS NOT NULL\n" +
            "            ) AS vl_result\n" +
            "       WHERE vl_result.rank2 = 1\n" +
            "       AND (vl_result.vlArchived = 0 OR vl_result.vlArchived IS null)\n" +
            "       AND  vl_result.vlFacility = ?1\n" +
            "    ),\n" +
            "    start_htn_regimen AS (\n" +
            "        SELECT * FROM\n" +
            "              (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid40 ORDER BY pr1.htnStartDate) AS rnk3\n" +
            "                           FROM\n" +
            "                               (SELECT p.person_uuid AS person_uuid40,\n" +
            "                                        p.visit_date AS htnStartDate,\n" +
            "                                        r.description AS htnStartRegimen\n" +
            "                                 FROM hiv_art_pharmacy p\n" +
            "                                     INNER JOIN hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id\n" +
            "                                     INNER JOIN hiv_regimen r on r.id = pr.regimens_id\n" +
            "                                     INNER JOIN hiv_regimen_type rt on rt.id = r.regimen_type_id\n" +
            "                                 WHERE r.regimen_type_id in (61)\n" +
            "                                 AND  p.archived = 0\n" +
            "                                 AND  p.facility_id = ?1\n" +
            "                                 AND  p.visit_date >= ?2\n" +
            "                                 AND  p.visit_date  < ?3\n" +
            "                               ) AS pr1\n" +
            "              ) AS pr2\n" +
            "          WHERE pr2.rnk3 = 1\n" +
            "    ),\n" +
            "    current_htn_regimen AS (\n" +
            "        SELECT person_uuid41, lastHTNPickupDate, currentHTNRegimen, monthsOfHTNRefill, \n" +
            "           NULL AS currentHTNStatus,\n" +
            "           NULL AS dateCurrentHTNStatus,\n" +
            "           NULL AS reasonsLTFU_IIT FROM\n" +
            "          (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid41 ORDER BY pr1.lastHTNPickupDate DESC) AS rnk41\n" +
            "                       FROM\n" +
            "                           (SELECT p.person_uuid AS person_uuid41,\n" +
            "                                    p.visit_date AS lastHTNPickupDate,\n" +
            "                                    r.description AS currentHTNRegimen,\n" +
            "                                    CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfHTNRefill\n" +
            "                             FROM hiv_art_pharmacy p\n" +
            "                                 INNER JOIN hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id\n" +
            "                                 INNER JOIN hiv_regimen r on r.id = pr.regimens_id\n" +
            "                                 INNER JOIN hiv_regimen_type rt on rt.id = r.regimen_type_id\n" +
            "                             WHERE r.regimen_type_id in (61)\n" +
            "                             AND  p.archived = 0\n" +
            "                             AND  p.facility_id = ?1\n" +
            "                             AND  p.visit_date >= ?2\n" +
            "                             AND  p.visit_date  < ?3) AS pr1\n" +
            "                       ) AS pr2\n" +
            "                  WHERE pr2.rnk41 = 1\n" +
            "    ),\n" +
            "    regimenSwitchSubstitutionDate AS (\n" +
            "        WITH regimen AS (\n" +
            "            SELECT p.person_uuid AS person_uuid40,\n" +
            "                   p.visit_date AS lastPickupDate,\n" +
            "                   r.description AS currentARTRegimen\n" +
            "            FROM public.hiv_art_pharmacy p\n" +
            "            INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id\n" +
            "            INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id\n" +
            "            INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id\n" +
            "            WHERE r.regimen_type_id IN (61)\n" +
            "            AND p.archived = 0\n" +
            "            AND p.facility_id = ?1\n" +
            "            AND p.visit_date >= ?2\n" +
            "            AND p.visit_date < ?3\n" +
            "        ),\n" +
            "        changes AS (\n" +
            "            SELECT person_uuid40,\n" +
            "                   lastPickupDate,\n" +
            "                   currentARTRegimen,\n" +
            "                   CASE\n" +
            "                       WHEN lag(currentARTRegimen) OVER (PARTITION BY person_uuid40 ORDER BY lastPickupDate) = currentARTRegimen THEN null\n" +
            "                       ELSE lastPickupDate\n" +
            "                   END AS change_date,\n" +
            "                   ROW_NUMBER() OVER (PARTITION BY person_uuid40 ORDER BY lastPickupDate DESC) AS change_rank\n" +
            "            FROM regimen\n" +
            "        )\n" +
            "        SELECT person_uuid40, change_date AS dateRegimenSwitch\n" +
            "        FROM changes\n" +
            "        WHERE change_rank = 1 AND change_date IS NOT NULL\n" +
            "    )\n" +
            "select * from bio_data bd\n" +
            "        left join patient_residence pr on bd.personUuid = pr.personUuid11\n" +
            "        left join pregnancy_status ps on bd.personUuid = ps.person_uuid\n" +
            "        left join current_status cs on cs.cuPersonUuid = bd.personUuid\n" +
            "        left join regimenSwitchSubstitutionDate rSSD on rSSD.person_uuid40 = bd.personUuid\n" +
            "        left join pharmacy_details_regimen pdr on pdr.person_uuid40 = bd.personUuid\n" +
            "        left join prev_hypertensive prev_hyp on prev_hyp.personUuid = bd.personUuid\n" +
            "        left join new_hypertensive new_hyp on new_hyp.personUuid = bd.personUuid\n" +
            "        left join baseline_weight_and_pressure bp on bp.pUuid = bd.personUuid\n" +
            "        left join baseline_tests blt on blt.patient_uuid = bd.personUuid\n" +
            "        left join start_htn_regimen shr on shr.person_uuid40 = bd.personUuid\n" +
            "        left join current_htn_regimen chr on chr.person_uuid41 = bd.personUuid\n" +
            "        left join current_weight_and_pressure cp on cp.person_uuid = bd.personUuid\n" +
            "        left join current_tests cut on cut.patient_uuid = bd.personUuid\n" +
            "        left join current_vl_result cvl on cvl.person_uuid130 = bd.personUuid;\n";
}

