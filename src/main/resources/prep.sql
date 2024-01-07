SELECT DISTINCT ON (p.uuid)p.uuid AS PersonUuid, p.id, p.uuid,p.hospital_number as hospitalNumber,
    INITCAP(p.surname) AS surname, INITCAP(p.first_name) as firstName, he.date_started AS hivEnrollmentDate,
    EXTRACT(YEAR from AGE(NOW(),  date_of_birth)) as age,
    p.other_name as otherName, p.sex as sex, p.date_of_birth as dateOfBirth,
    p.date_of_registration as dateOfRegistration, p.marital_status->>'display' as maritalStatus,
    education->>'display' as education, p.employment_status->>'display' as occupation,
    facility.name as facilityName, facility_lga.name as lga, facility_state.name as state,
    boui.code as datimId,
    (SELECT name FROM base_organisation_unit WHERE id = CAST(p.address->'address'->0 ->'stateId' ->> 0 AS BIGINT)) as residentialState,
    (SELECT name FROM base_organisation_unit WHERE id = CAST(p.address->'address'->0 ->'district' ->> 0 AS BIGINT)) as residentialLga,
    r.address as address,
    (CASE WHEN contact_point->'contactPoint'->0->>'type'='phone' THEN contact_point->'contactPoint'->0->>'value' ELSE null END) AS phone,

    baseline_reg.regimen AS baselineRegimen,
    baseline_pc.systolic AS baselineSystolicBP,
    baseline_pc.diastolic AS baselineDiastolicBP,
    baseline_pc.weight AS baselineWeight,
    baseline_pc.height AS baselineHeight,
-- (CASE WHEN tg.display IS NULL THEN tg.display ELSE tg.display END) AS targetGroup,
    (CASE WHEN tg.display IS NULL THEN null ELSE tg.display END) AS targetGroup,
    baseline_pc.encounter_date AS prepCommencementDate,
    baseline_pc.urinalysis->>'result' AS baseLineUrinalysis,
    CAST(baseline_pc.urinalysis->>'testDate' AS DATE) AS baseLineUrinalysisDate,
    (CASE WHEN baseline_creatinine.other_tests_done->>'name'='Creatinine'
    THEN baseline_creatinine.other_tests_done->>'result' ELSE NULL END) AS baseLineCreatinine,
    (CASE WHEN baseline_creatinine.other_tests_done->>'name'='Creatinine'
    THEN baseline_creatinine.other_tests_done->>'testDate' ELSE NULL END) AS baseLineCreatinineTestDate,
    (CASE WHEN baseline_pc.hepatitis->>'result' LIKE 'Hepatitis B%'
    THEN baseline_pc.hepatitis->>'result' ELSE NULL END) AS baseLineHepatitisB,
    (CASE WHEN baseline_pc.hepatitis->>'result' LIKE 'Hepatitis C%'
    THEN baseline_pc.hepatitis->>'result' ELSE NULL END) AS baseLineHepatitisC,
    -- baseline_pc.hepatitis->>'result' AS baseLineHepatitisB, --
-- baseline_pc.hepatitis->>'result' AS baseLineHepatitisC,--
    current_pi.reason_stopped AS InterruptionReason,
    current_pi.interruption_date AS InterruptionDate,
    (CASE WHEN baseline_hiv_status.display IS NULL AND base_eli_test.base_eli_hiv_result IS NOT NULL
    THEN base_eli_test.base_eli_hiv_result ELSE
    REPLACE(baseline_hiv_status.display, 'HIV ', '') END) AS HIVStatusAtPrEPInitiation,
    (CASE WHEN prepe.extra->'prep'->>'onDemandIndication' IS NOT NULL THEN prepe.extra->>'onDemandIndication'
    WHEN riskt.display IS NOT NULL THEN riskt.display ELSE NULL END) AS indicationForPrEP,
    current_reg.regimen AS currentRegimen,
    current_pc.encounter_date AS DateOfLastPickup,
    current_pc.systolic AS currentSystolicBP,
    current_pc.diastolic AS currentDiastolicBP,
    current_pc.weight AS currentWeight,
    current_pc.height AS currentHeight,
    current_pc.urinalysis->>'result' AS currentUrinalysis,
    CAST(current_pc.urinalysis->>'testDate' AS DATE) AS currentUrinalysisDate,
    (CASE WHEN current_hiv_status.display IS NULL AND eli_hiv_result IS NOT NULL THEN eli_hiv_result
    WHEN current_hiv_status.display IS NOT NULL THEN REPLACE(current_hiv_status.display, 'HIV ', '')
    WHEN he.date_started IS NOT NULL THEN 'Positive' ELSE NULL
    END) AS currentHivStatus,
    current_pc.encounter_date AS DateOfCurrentHIVStatus,
    (CASE WHEN p.sex='Male' THEN NULL
    WHEN current_pc.pregnant IS NOT NULL AND current_pc.pregnant='true' THEN 'Pregnant'
    ELSE 'Not Pregnant' END) AS pregnancyStatus,
    (CASE
    WHEN prepi.interruption_date  > prepc.encounter_date THEN bac.display
    WHEN prepc.status IS NOT NULL THEN prepc.status
    ELSE NULL END) AS CurrentStatus,
    (CASE
    WHEN prepi.interruption_date  > prepc.encounter_date THEN prepi.interruption_date
    WHEN prepc.status IS NOT NULL THEN (prepc.encounter_date  + COALESCE(prepc.duration, 0))
    ELSE NULL END) AS DateOfCurrentStatus
FROM patient_person p
    INNER JOIN (
    SELECT * FROM (SELECT p.id, CONCAT(CAST(address_object->>'city' AS VARCHAR), ' ', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\\\\\\\\', ''), ']', ''), '[', ''), 'null',''), '\\\\\\\', '')) AS address,
    CASE WHEN address_object->>'stateId'  ~ '^\\\\d+(\\\\.\\\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId,
    CASE WHEN address_object->>'district'  ~ '^\\\\d+(\\\\.\\\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId
    FROM patient_person p,
    jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result
    ) r ON r.id=p.id
    -- INNER JOIN (SELECT MAX(date_started) date_started, person_uuid,target_group  FROM prep_enrollment
-- GROUP BY person_uuid,target_group) penrol ON penrol.person_uuid=p.uuid
    LEFT JOIN (SELECT target_group, person_uuid  FROM hts_client) penrol ON penrol.person_uuid=p.uuid
    LEFT JOIN (SELECT MAX(visit_date) max_date, person_uuid,target_group AS eli_target  FROM prep_eligibility
    GROUP BY person_uuid,target_group) e_target ON e_target.person_uuid=p.uuid
    LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS eli_hiv_result, max.visit_date, max.person_uuid FROM prep_eligibility pe
    INNER JOIN (SELECT DISTINCT MAX(visit_date)visit_date, person_uuid FROM prep_eligibility
    GROUP BY person_uuid)max ON max.visit_date=pe.visit_date
    AND max.person_uuid=pe.person_uuid)eli_test ON eli_test.person_uuid=p.uuid
    LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS base_eli_hiv_result, min.visit_date, min.person_uuid
    FROM prep_eligibility pe
    INNER JOIN (SELECT DISTINCT MIN(visit_date)visit_date, person_uuid FROM prep_eligibility
    GROUP BY person_uuid)min ON min.visit_date=pe.visit_date
    AND min.person_uuid=pe.person_uuid)base_eli_test ON base_eli_test.person_uuid=p.uuid
    LEFT JOIN base_organisation_unit facility ON facility.id=facility_id
    LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id
    LEFT JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id
    LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)
    LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)
    LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=facility_id AND boui.name='DATIM_ID'
    INNER JOIN prep_enrollment prepe ON prepe.person_uuid = p.uuid
    LEFT JOIN base_application_codeset riskt ON riskt.code = prepe.risk_type
    LEFT JOIN base_application_codeset tg ON tg.code = penrol.target_group
    LEFT JOIN base_application_codeset etg ON etg.code = e_target.eli_target
    LEFT JOIN (SELECT DISTINCT pc.* FROM prep_clinic pc
    INNER JOIN (SELECT DISTINCT MAX(encounter_date)encounter_date, person_uuid FROM prep_clinic
    GROUP BY person_uuid)max ON max.encounter_date=pc.encounter_date
    AND max.person_uuid=pc.person_uuid WHERE date_prep_start IS NULL)current_pc ON current_pc.person_uuid=p.uuid
    LEFT JOIN (SELECT DISTINCT pi.* FROM prep_interruption pi
    LEFT JOIN (SELECT DISTINCT MAX(encounter_date)encounter_date, person_uuid FROM prep_interruption
    GROUP BY person_uuid)max ON max.encounter_date=pi.encounter_date
    AND max.person_uuid=pi.person_uuid)current_pi ON current_pi.person_uuid=p.uuid
    LEFT JOIN prep_regimen current_reg ON current_reg.id = current_pc.regimen_id
    LEFT JOIN base_application_codeset current_hiv_status ON current_hiv_status.code = current_pc.hiv_test_result
    LEFT JOIN (SELECT pc.* FROM prep_clinic pc
    INNER JOIN (SELECT DISTINCT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic
    GROUP BY person_uuid)min ON min.encounter_date=pc.encounter_date
    AND min.person_uuid=pc.person_uuid WHERE date_prep_start IS NOT NULL)baseline_pc ON baseline_pc.person_uuid=p.uuid
    LEFT JOIN (SELECT pc.* FROM prep_clinic pc
    INNER JOIN (SELECT DISTINCT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic
    GROUP BY person_uuid)min ON min.person_uuid=pc.person_uuid
    WHERE pc.other_tests_done->>'name' = 'Creatinine' )baseline_creatinine ON baseline_creatinine.person_uuid=p.uuid
    LEFT JOIN prep_regimen baseline_reg ON baseline_reg.id = baseline_pc.regimen_id
    LEFT JOIN base_application_codeset baseline_hiv_status ON baseline_hiv_status.code=baseline_pc.hiv_test_result
    LEFT JOIN hiv_enrollment he ON he.person_uuid = p.uuid
    LEFT JOIN (
    SELECT pi.id, pi.person_uuid, pi.interruption_date , pi.interruption_type
    FROM prep_interruption pi
    INNER JOIN (SELECT DISTINCT pi.person_uuid, MAX(pi.interruption_date)interruption_date
    FROM prep_interruption pi WHERE pi.archived=0
    GROUP BY pi.person_uuid)pit ON pit.interruption_date=pi.interruption_date
    AND pit.person_uuid=pi.person_uuid
    WHERE pi.archived=0
    GROUP BY pi.id, pi.person_uuid, pi.interruption_date, pi.interruption_type )prepi ON prepi.person_uuid=p.uuid
    LEFT JOIN (SELECT pc.person_uuid, MAX(pc.encounter_date) as encounter_date, pc.duration,
    (CASE WHEN (pc.encounter_date  + pc.duration) > CAST (NOW() AS DATE) THEN 'Active'
    ELSE  'Defaulted' END) status FROM prep_clinic pc
    INNER JOIN (SELECT DISTINCT MAX(pc.encounter_date) encounter_date, pc.person_uuid
    FROM prep_clinic pc GROUP BY pc.person_uuid) max_p ON max_p.encounter_date=pc.encounter_date
    AND max_p.person_uuid=pc.person_uuid
    WHERE pc.archived=0
    GROUP BY pc.person_uuid, pc.duration, status)prepc ON prepc.person_uuid=p.uuid
    LEFT JOIN base_application_codeset bac ON bac.code=prepi.interruption_type
WHERE p.archived=0