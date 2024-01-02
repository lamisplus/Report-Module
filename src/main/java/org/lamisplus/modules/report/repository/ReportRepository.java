package org.lamisplus.modules.report.repository;

import lombok.extern.java.Log;
import org.lamisplus.modules.report.domain.*;
import org.lamisplus.modules.hiv.domain.dto.PatientLineDto;
import org.lamisplus.modules.report.domain.dto.ClinicDataDto;
import org.lamisplus.modules.report.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {


    @Query(value = "SELECT hc.client_code AS clientCode,      " +
            " (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'first_name') ELSE INITCAP(pp.first_name) END) AS firstName,      " +
            " (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'surname') ELSE INITCAP(pp.surname) END) AS surname,      " +
            " (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'middile_name') ELSE INITCAP(pp.other_name) END) AS otherName,      " +
            " (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'gender') ELSE INITCAP(pp.sex) END) AS sex,      " +
            "             (CASE WHEN hc.person_uuid IS NULL THEN CAST(hc.extra->>'age' AS INTEGER)       " +
            " ELSE CAST(EXTRACT(YEAR from AGE(NOW(),  pp.date_of_birth)) AS INTEGER )      " +
            " END) AS age,      " +
            " (CASE WHEN hc.person_uuid IS NOT NULL THEN pp.date_of_birth      " +
            " WHEN hc.person_uuid IS NULL AND LENGTH(hc.extra->>'date_of_birth') > 0   " +
            "             AND hc.extra->>'date_of_birth' != '' THEN CAST(NULLIF(hc.extra->>'date_of_birth', '') AS DATE)       " +
            " ELSE NULL END) AS dateOfBirth,      " +
            "  (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'phone_number'      " +
            " ELSE pp.contact_point->'contactPoint'->0->'value'->>0 END) AS phoneNumber,      " +
            "  (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'marital_status'      " +
            " ELSE pp.marital_status->>'display' END) AS maritalStatus,      " +
            " (CASE WHEN hc.person_uuid IS NULL      " +
            " THEN hc.extra->>'lga_of_residence' ELSE res_lga.name END) AS LGAOfResidence,      " +
            " (CASE WHEN hc.person_uuid IS NULL       " +
            "  THEN hc.extra->>'state_of_residence' ELSE pp.address ->>'{address,0,city}' END) AS StateOfResidence,      " +
            "  facility.name AS facility,      " +
            "  state.name AS state,      " +
            "  lga.name AS lga,      " +
            "  pp.uuid AS patientId,      " +
            " pp.education->>'display' as education,       " +
            " pp.employment_status->>'display' as occupation,      " +
            " boui.code as datimCode,      " +
            " hc.others->>'latitude' AS HTSLatitude,      " +
            " hc.others->>'longitude' AS HTSLongitude,        " +
            " (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'client_address' ELSE r.address END) AS clientAddress,      " +
            " hc.date_visit AS dateVisit,      " +
            " (CASE WHEN hc.first_time_visit IS true THEN 'Yes' ELSE 'No' END) firstTimeVisit,      " +
            " hc.num_children AS numberOfChildren,      " +
            " hc.num_wives AS numberOfWives,      " +
            " (CASE WHEN hc.index_client IS true THEN 'Yes' ELSE 'No' END) indexClient,      " +
            " (CASE WHEN hc.prep_offered IS true THEN 'Yes' ELSE 'No' END)  AS prepOffered,      " +
            " (CASE WHEN hc.prep_accepted IS true THEN 'Yes' ELSE 'No' END) AS prepAccepted,      " +
            " (CASE WHEN hc.previously_tested IS true THEN 'Yes' ELSE 'No' END) AS previouslyTested,       " +
            " tg.display AS targetGroup,      " +
            " rf.display AS referredFrom,      " +
            " ts.display AS testingSetting,      " +
            " tc.display AS counselingType,      " +
            " preg.display AS pregnancyStatus,      " +
            " (CASE  " +
            "WHEN preg.display='Breastfeeding' THEN 'Yes'   " +
            "WHEN preg.display IS NULL THEN NULL  " +
            "ELSE 'No'  " +
            "END) AS breastFeeding, " +
            " it.display AS indexType,      " +
            " (CASE WHEN hc.recency->>'optOutRTRI' ILIKE 'true' THEN 'Yes'    " +
            " WHEN hc.recency->>'optOutRTRI' ILIKE 'false' THEN 'No'   " +
            " WHEN hc.recency->>'optOutRTRI' != NULL THEN hc.recency->>'optOutRTRI'  " +
            " ELSE NULL END) AS IfRecencyTestingOptIn,      " +
            " hc.recency->>'rencencyId' AS RecencyID,      " +
            " hc.recency->>'optOutRTRITestName' AS recencyTestType,      " +
            " (CASE WHEN hc.recency->>'optOutRTRITestDate' IS NOT NULL     " +
            "              AND hc.recency->>'optOutRTRITestDate' != '' AND LENGTH(hc.recency->>'optOutRTRITestDate') > 0      " +
            "              THEN CAST(NULLIF(hc.recency->>'optOutRTRITestDate', '') AS DATE)    " +
            "              WHEN hc.recency->>'sampleTestDate' IS NOT NULL     " +
            "              AND hc.recency->>'sampleTestDate' != '' AND LENGTH(hc.recency->>'sampleTestDate') > 0   " +
            "              THEN CAST(NULLIF(hc.recency->>'sampleTestDate', '') AS DATE) ELSE NULL END) AS recencyTestDate,      " +
            " (CASE     " +
            "               WHEN hc.recency->>'rencencyInterpretation' IS NOT NULL     " +
            "               AND hc.recency->>'rencencyInterpretation' ILIKE '%Long%' THEN 'RTRI Longterm'    " +
            "               WHEN hc.recency->>'rencencyInterpretation' IS NOT NULL     " +
            "               AND hc.recency->>'rencencyInterpretation' ILIKE '%Recent%' THEN 'RTRI Recent'     " +
            "               ELSE hc.recency->>'rencencyInterpretation' END) AS recencyInterpretation,      " +
            " hc.recency->>'finalRecencyResult' AS finalRecencyResult,      " +
            " hc.recency->>'viralLoadResultClassification' AS viralLoadResult,     " +
            "              CAST(NULLIF(hc.recency->>'sampleCollectedDate', '') AS DATE) AS viralLoadSampleCollectionDate,    " +
            "              hc.recency->>'viralLoadConfirmationResult' AS viralLoadConfirmationResult,    " +
            "              CAST(NULLIF(hc.recency->>'viralLoadConfirmationTestDate', '') AS DATE) AS viralLoadConfirmationDate,      " +
            " hc.risk_stratification_code AS Assessmentcode,      " +
            " modality_code.display AS modality,      " +
            " (CASE WHEN hc.syphilis_testing->>'syphilisTestResult' ILIKE 'Yes'       " +
            " THEN 'Reactive' ELSE 'Non-Reactive' END) As syphilisTestResult,      " +
            " (CASE WHEN hc.hepatitis_testing->>'hepatitisBTestResult' ILIKE 'Yes'       " +
            "  THEN 'Positive' ELSE 'Negative' END) AS hepatitisBTestResult,      " +
            " (CASE WHEN hc.hepatitis_testing->>'hepatitisCTestResult' ILIKE 'Yes'       " +
            "  THEN 'Positive' ELSE 'Negative' END) AS hepatitisCTestResult,      " +
            " hc.cd4->>'cd4Count' AS CD4Type,      " +
            " hc.cd4->>'cd4SemiQuantitative' AS CD4TestResult,      " +
            " (CASE WHEN hc.test1->>'result' ILIKE 'Yes' THEN 'Positive' ELSE 'Negative' END)AS hivTestResult,      " +
            " hc.hiv_test_result AS finalHIVTestResult,      " +
            " (CASE WHEN LENGTH(hc.test1->>'date') > 0 AND hc.test1->>'date' !=''   " +
            "             THEN CAST(NULLIF(hc.test1->>'date', '') AS DATE)      " +
            " WHEN hc.date_visit IS NOT NULL THEN hc.date_visit       " +
            " ELSE NULL END)dateOfHIVTesting,      " +
            " CAST(post_test_counseling->>'condomProvidedToClientCount' AS VARCHAR) AS numberOfCondomsGiven,      " +
            " CAST(post_test_counseling->>'lubricantProvidedToClientCount' AS VARCHAR) AS numberOfLubricantsGiven    " +
            " FROM hts_client hc      " +
            " LEFT JOIN base_application_codeset tg ON tg.code = hc.target_group      " +
            "LEFT JOIN base_application_codeset it ON it.id = hc.relation_with_index_client" +
            " LEFT JOIN base_application_codeset rf ON rf.id = hc.referred_from      " +
            " LEFT JOIN base_application_codeset ts ON ts.code = hc.testing_setting      " +
            " LEFT JOIN base_application_codeset tc ON tc.id = hc.type_counseling      " +
            " LEFT JOIN base_application_codeset preg ON preg.id = hc.pregnant      " +
            " LEFT JOIN base_application_codeset relation ON relation.id = hc.relation_with_index_client      " +
            " LEFT JOIN hts_risk_stratification hrs ON hrs.code = hc.risk_stratification_code      " +
            " LEFT JOIN base_application_codeset modality_code ON modality_code.code = hrs.modality      " +
            " LEFT JOIN patient_person pp ON pp.uuid=hc.person_uuid      " +
            " LEFT JOIN (SELECT * FROM (SELECT " +
            " p.id," +
            " p.address ->>'{address,0,city}' as clientcity," +
            " p.address ->> '{address,0,line,0}' as clientaddress," +
            " p.address ->>'{address,0,district}' as lgaid," +
            " p.address ->> '{address,0,stateId}' as stateid, " +
            " (jsonb_array_elements(p.address->'address')->>'city') as address " +
            " FROM patient_person p) as result ) r ON r.id=pp.id" +

            " LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)      " +
            " LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)      " +
            " LEFT JOIN base_organisation_unit facility ON facility.id=hc.facility_id      " +
            " LEFT JOIN base_organisation_unit lga ON lga.id=facility.parent_organisation_unit_id      " +
            " LEFT JOIN base_organisation_unit state ON state.id=lga.parent_organisation_unit_id      " +
            " LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=hc.facility_id AND boui.name='DATIM_ID'   " +
            "WHERE hc.archived=0 AND hc.facility_id=?1 AND hc.date_visit >=?2 AND hc.date_visit <= ?3", nativeQuery = true)
    List<HtsReportDto> getHtsReport(Long facilityId, LocalDate start, LocalDate end);


    @Query(value = "SELECT DISTINCT ON (p.uuid)p.uuid AS PersonUuid, p.id, p.uuid,p.hospital_number as hospitalNumber,       \n" +
            "                                    INITCAP(p.surname) AS surname, INITCAP(p.first_name) as firstName, he.date_started AS hivEnrollmentDate,    \n" +
            "                                    EXTRACT(YEAR from AGE(NOW(),  date_of_birth)) as age,      \n" +
            "                                    p.other_name as otherName, p.sex as sex, p.date_of_birth as dateOfBirth,       \n" +
            "                                    p.date_of_registration as dateOfRegistration, p.marital_status->>'display' as maritalStatus,       \n" +
            "                                    education->>'display' as education, p.employment_status->>'display' as occupation,       \n" +
            "                                    facility.name as facilityName, facility_lga.name as lga, facility_state.name as state,       \n" +
            "                                    boui.code as datimId, (SELECT name FROM base_organisation_unit WHERE id = CAST(p.address->'address'->0 ->'stateId' ->> 0 AS BIGINT)) as residentialState, (SELECT name FROM base_organisation_unit WHERE id = CAST(p.address->'address'->0 ->'district' ->> 0 AS BIGINT)) as residentialLga,      \n" +
            "                                    r.address as address, p.contact_point->'contactPoint'->0->'value'->>0 AS phone,      \n" +
            "                                    baseline_reg.regimen AS baselineRegimen,      \n" +
            "                                    baseline_pc.systolic AS baselineSystolicBP,      \n" +
            "                                    baseline_pc.diastolic AS baselineDiastolicBP,      \n" +
            "                                    baseline_pc.weight AS baselineWeight,      \n" +
            "                                    baseline_pc.height AS baselineHeight,    \n" +
            "                                    (CASE WHEN tg.display IS NULL THEN null ELSE tg.display END) AS targetGroup,    \n" +
            "                                    baseline_pc.encounter_date AS prepCommencementDate,    \n" +
            "                                    baseline_pc.urinalysis->>'result' AS baseLineUrinalysis,   \n" +
            "                                    CAST(baseline_pc.urinalysis->>'testDate' AS DATE) AS baseLineUrinalysisDate,   \n" +
            "                                    (CASE WHEN baseline_creatinine.other_tests_done->>'name'='Creatinine'    \n" +
            "                                    THEN baseline_creatinine.other_tests_done->>'result' ELSE NULL END) AS baseLineCreatinine,  \n" +
            "                                   (CASE WHEN baseline_creatinine.other_tests_done->>'name'='Creatinine'    \n" +
            "                                    THEN baseline_creatinine.other_tests_done->>'testDate' ELSE NULL END) AS baseLineCreatinineTestDate,  \n" +
            "\t\t\t\t\t\t\t\t\t(CASE WHEN baseline_pc.hepatitis->>'result' LIKE 'Hepatitis B%'   \n" +
            "                                    THEN baseline_pc.hepatitis->>'result' ELSE NULL END) AS baseLineHepatitisB, \n" +
            "\t\t\t\t\t\t\t\t\t(CASE WHEN baseline_pc.hepatitis->>'result' LIKE 'Hepatitis C%'   \n" +
            "                                    THEN baseline_pc.hepatitis->>'result' ELSE NULL END) AS baseLineHepatitisC, \n" +
            "                                    -- baseline_pc.hepatitis->>'result' AS baseLineHepatitisB, --  \n" +
            "                                    -- baseline_pc.hepatitis->>'result' AS baseLineHepatitisC,--   \n" +
            "                                    current_pi.reason_stopped AS InterruptionReason,   \n" +
            "                                    current_pi.interruption_date AS InterruptionDate,   \n" +
            "                                     (CASE WHEN baseline_hiv_status.display IS NULL AND base_eli_test.base_eli_hiv_result IS NOT NULL    \n" +
            "                                    THEN base_eli_test.base_eli_hiv_result ELSE    \n" +
            "                                    REPLACE(baseline_hiv_status.display, 'HIV ', '') END) AS HIVStatusAtPrEPInitiation,   \n" +
            "                                    (CASE WHEN prepe.extra->'prep'->>'onDemandIndication' IS NOT NULL THEN prepe.extra->>'onDemandIndication'      \n" +
            "                                    WHEN riskt.display IS NOT NULL THEN riskt.display ELSE NULL END) AS indicationForPrEP,      \n" +
            "                                    current_reg.regimen AS currentRegimen,      \n" +
            "                                    current_pc.encounter_date AS DateOfLastPickup,      \n" +
            "                                    current_pc.systolic AS currentSystolicBP,     \n" +
            "                                    current_pc.diastolic AS currentDiastolicBP,      \n" +
            "                                    current_pc.weight AS currentWeight,      \n" +
            "                                    current_pc.height AS currentHeight,   \n" +
            "                                    current_pc.urinalysis->>'result' AS currentUrinalysis,   \n" +
            "                                    CAST(current_pc.urinalysis->>'testDate' AS DATE) AS currentUrinalysisDate,   \n" +
            "                                    (CASE WHEN current_hiv_status.display IS NULL AND eli_hiv_result IS NOT NULL THEN eli_hiv_result \n" +
            "                          WHEN current_hiv_status.display IS NOT NULL THEN REPLACE(current_hiv_status.display, 'HIV ', '') \n" +
            "                                    WHEN he.date_started IS NOT NULL THEN 'Positive' ELSE NULL    \n" +
            "                          END) AS currentHivStatus,      \n" +
            "                          current_pc.encounter_date AS DateOfCurrentHIVStatus, \n" +
            "                          (CASE WHEN p.sex='Male' THEN NULL \n" +
            "                        WHEN current_pc.pregnant IS NOT NULL AND current_pc.pregnant='true' THEN 'Pregnant'      \n" +
            "                                    ELSE 'Not Pregnant' END) AS pregnancyStatus, \n" +
            "                         (CASE  \n" +
            "                         WHEN prepi.interruption_date  > prepc.encounter_date THEN bac.display \n" +
            "                          WHEN prepc.status IS NOT NULL THEN prepc.status \n" +
            "                         ELSE NULL END) AS CurrentStatus, \n" +
            "                         (CASE  \n" +
            "                         WHEN prepi.interruption_date  > prepc.encounter_date THEN prepi.interruption_date \n" +
            "                          WHEN prepc.status IS NOT NULL THEN (prepc.encounter_date  + COALESCE(prepc.duration, 0)) \n" +
            "                         ELSE NULL END) AS DateOfCurrentStatus \n" +
            "                                    FROM patient_person p      \n" +
            "                                    INNER JOIN (\n" +
            "                        SELECT * FROM (SELECT p.id, CONCAT(CAST(address_object->>'city' AS VARCHAR), ' ', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\\\\\\\\', ''), ']', ''), '[', ''), 'null',''), '\\\\\\\"', '')) AS address, \n" +
            "                         CASE WHEN address_object->>'stateId'  ~ '^\\\\d+(\\\\.\\\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId,\n" +
            "                         CASE WHEN address_object->>'district'  ~ '^\\\\d+(\\\\.\\\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId \n" +
            "                        FROM patient_person p,\n" +
            "                        jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result\n" +
            "                          ) r ON r.id=p.id \n" +
            "                         LEFT JOIN (SELECT target_group, person_uuid  FROM hts_client) penrol ON penrol.person_uuid=p.uuid \n" +
            "              LEFT JOIN (SELECT MAX(visit_date) max_date, person_uuid,target_group AS eli_target  FROM prep_eligibility \n" +
            "                          GROUP BY person_uuid,target_group) e_target ON e_target.person_uuid=p.uuid\n" +
            "                                    LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS eli_hiv_result, max.visit_date, max.person_uuid FROM prep_eligibility pe    \n" +
            "                                    INNER JOIN (SELECT DISTINCT MAX(visit_date)visit_date, person_uuid FROM prep_eligibility    \n" +
            "                                    GROUP BY person_uuid)max ON max.visit_date=pe.visit_date     \n" +
            "                                    AND max.person_uuid=pe.person_uuid)eli_test ON eli_test.person_uuid=p.uuid    \n" +
            "                                    LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS base_eli_hiv_result, min.visit_date, min.person_uuid    \n" +
            "                                    FROM prep_eligibility pe    \n" +
            "                                    INNER JOIN (SELECT DISTINCT MIN(visit_date)visit_date, person_uuid FROM prep_eligibility    \n" +
            "                                    GROUP BY person_uuid)min ON min.visit_date=pe.visit_date    \n" +
            "                                    AND min.person_uuid=pe.person_uuid)base_eli_test ON base_eli_test.person_uuid=p.uuid   \n" +
            "                                    LEFT JOIN base_organisation_unit facility ON facility.id=facility_id      \n" +
            "                                    LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id      \n" +
            "                                    LEFT JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id      \n" +
            "                                    LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)      \n" +
            "                                    LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)      \n" +
            "                                    LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=facility_id AND boui.name='DATIM_ID'      \n" +
            "                                    INNER JOIN prep_enrollment prepe ON prepe.person_uuid = p.uuid      \n" +
            "                                    LEFT JOIN base_application_codeset riskt ON riskt.code = prepe.risk_type      \n" +
            "                                    LEFT JOIN base_application_codeset tg ON tg.code = penrol.target_group\n" +
            "            LEFT JOIN base_application_codeset etg ON etg.code = e_target.eli_target  \n" +
            "                                    LEFT JOIN (SELECT DISTINCT pc.* FROM prep_clinic pc      \n" +
            "                                    INNER JOIN (SELECT DISTINCT MAX(encounter_date)encounter_date, person_uuid FROM prep_clinic      \n" +
            "                                    GROUP BY person_uuid)max ON max.encounter_date=pc.encounter_date       \n" +
            "                                    AND max.person_uuid=pc.person_uuid WHERE date_prep_start IS NULL)current_pc ON current_pc.person_uuid=p.uuid    \n" +
            "                                      LEFT JOIN (SELECT DISTINCT pi.* FROM prep_interruption pi     \n" +
            "                                    LEFT JOIN (SELECT DISTINCT MAX(encounter_date)encounter_date, person_uuid FROM prep_interruption     \n" +
            "                                    GROUP BY person_uuid)max ON max.encounter_date=pi.encounter_date       \n" +
            "                                    AND max.person_uuid=pi.person_uuid)current_pi ON current_pi.person_uuid=p.uuid    \n" +
            "                                    LEFT JOIN prep_regimen current_reg ON current_reg.id = current_pc.regimen_id      \n" +
            "                                    LEFT JOIN base_application_codeset current_hiv_status ON current_hiv_status.code = current_pc.hiv_test_result      \n" +
            "                                    LEFT JOIN (SELECT pc.* FROM prep_clinic pc      \n" +
            "                                    INNER JOIN (SELECT DISTINCT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic      \n" +
            "                                    GROUP BY person_uuid)min ON min.encounter_date=pc.encounter_date       \n" +
            "                                    AND min.person_uuid=pc.person_uuid WHERE date_prep_start IS NOT NULL)baseline_pc ON baseline_pc.person_uuid=p.uuid      \n" +
            "                                        LEFT JOIN (SELECT pc.* FROM prep_clinic pc      \n" +
            "                                    INNER JOIN (SELECT DISTINCT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic      \n" +
            "                                    GROUP BY person_uuid)min ON min.person_uuid=pc.person_uuid \n" +
            "                                    WHERE pc.other_tests_done->>'name' = 'Creatinine' )baseline_creatinine ON baseline_creatinine.person_uuid=p.uuid" +
            "                                    LEFT JOIN prep_regimen baseline_reg ON baseline_reg.id = baseline_pc.regimen_id      \n" +
            "                                    LEFT JOIN base_application_codeset baseline_hiv_status ON baseline_hiv_status.code=baseline_pc.hiv_test_result \n" +
            "                         LEFT JOIN hiv_enrollment he ON he.person_uuid = p.uuid \n" +
            "                         LEFT JOIN (  \n" +
            "                                    SELECT pi.id, pi.person_uuid, pi.interruption_date , pi.interruption_type  \n" +
            "                                    FROM prep_interruption pi   \n" +
            "                                    INNER JOIN (SELECT DISTINCT pi.person_uuid, MAX(pi.interruption_date)interruption_date   \n" +
            "                                    FROM prep_interruption pi WHERE pi.archived=0   \n" +
            "                                    GROUP BY pi.person_uuid)pit ON pit.interruption_date=pi.interruption_date   \n" +
            "                                    AND pit.person_uuid=pi.person_uuid   \n" +
            "                                    WHERE pi.archived=0   \n" +
            "                                    GROUP BY pi.id, pi.person_uuid, pi.interruption_date, pi.interruption_type )prepi ON prepi.person_uuid=p.uuid \n" +
            "                         LEFT JOIN (SELECT pc.person_uuid, MAX(pc.encounter_date) as encounter_date, pc.duration,  \n" +
            "                                    (CASE WHEN (pc.encounter_date  + pc.duration) > CAST (NOW() AS DATE) THEN 'Active' \n" +
            "                                    ELSE  'Defaulted' END) status FROM prep_clinic pc \n" +
            "                                    INNER JOIN (SELECT DISTINCT MAX(pc.encounter_date) encounter_date, pc.person_uuid \n" +
            "                                    FROM prep_clinic pc GROUP BY pc.person_uuid) max_p ON max_p.encounter_date=pc.encounter_date \n" +
            "                                    AND max_p.person_uuid=pc.person_uuid  \n" +
            "                         WHERE pc.archived=0 \n" +
            "                                    GROUP BY pc.person_uuid, pc.duration, status)prepc ON prepc.person_uuid=p.uuid \n" +
            "                         LEFT JOIN base_application_codeset bac ON bac.code=prepi.interruption_type \n" +
            "WHERE p.archived=0 AND p.facility_id=?1 AND p.date_of_registration >=?2 AND p.date_of_registration <= ?3", nativeQuery = true)
    List<PrepReportDto> getPrepReport(Long facilityId, LocalDate start, LocalDate end);

    @Query(value = "WITH bio_data AS (SELECT DISTINCT (p.uuid) AS personUuid,p.hospital_number AS hospitalNumber, h.unique_id as uniqueId,\n" +
            "EXTRACT(YEAR FROM  AGE(NOW(), date_of_birth)) AS age,\n" +
            "INITCAP(p.sex) AS gender,\n" +
            "p.date_of_birth AS dateOfBirth,\n" +
            "facility.name AS facilityName,\n" +
            "facility_lga.name AS lga,\n" +
            "facility_state.name AS state,\n" +
            "boui.code AS datimId,\n" +
            "tgroup.display AS targetGroup,\n" +
            "eSetting.display AS enrollmentSetting,\n" +
            "hac.visit_date AS artStartDate,\n" +
            "hr.description AS regimenAtStart,\n" +
            "p.date_of_registration as dateOfRegistration,"+
            "h.date_of_registration as dateOfEnrollment,"+
            "h.ovc_number AS ovcUniqueId,\n" +
            "h.house_hold_number AS householdUniqueNo,\n" +
            "ecareEntry.display AS careEntry,\n" +
            "hrt.description AS regimenLineAtStart\n" +
            "FROM patient_person p\n" +
            "INNER JOIN base_organisation_unit facility ON facility.id = facility_id\n" +
            "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID'\n" +
            "INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            "LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id\n" +
            "LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id\n" +
            "LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id\n" +
            "INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid\n" +
            "AND hac.archived = 0\n" +
            "INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id\n" +
            "INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id\n" +
            "WHERE\n" +
            "h.archived = 0\n" +
            "AND p.archived = 0\n" +
            "AND h.facility_id = ?1\n" +
            "AND hac.is_commencement = TRUE\n" +
            "AND hac.visit_date >= ?2\n" +
            "AND hac.visit_date < ?3\n" +
            "),\n" +
            "\n" +
            "patient_lga as (select DISTINCT ON (personUuid) personUuid as personUuid11, \n" +
            "case when (addr ~ '^[0-9\\.]+$') =TRUE \n" +
            "then (select name from base_organisation_unit where id = cast(addr as int)) end as lgaOfResidence \n" +
            "from (\n" +
            "select uuid AS personUuid, (jsonb_array_elements(address->'address')->>'district') as addr from patient_person \n" +
            ") dt)," +
            "\n" +
            "current_clinical AS (SELECT DISTINCT ON (tvs.person_uuid) tvs.person_uuid AS person_uuid10,\n" +
            "       body_weight AS currentWeight,\n" +
            "       tbs.display AS tbStatus1,\n" +
            "       bac.display AS currentClinicalStage,\n" +
            "       (CASE\n" +
            "WHEN preg.display IS NOT NULL THEN preg.display\n" +
            "ELSE hac.pregnancy_status  END ) AS pregnancyStatus,\n" +
            "       CASE\n" +
            "           WHEN hac.tb_screen IS NOT NULL THEN hac.visit_date\n" +
            "           ELSE NULL\n" +
            "           END AS dateOfTbScreened1\n" +
            "         FROM\n" +
            " triage_vital_sign tvs\n" +
            "     INNER JOIN (\n" +
            "     SELECT\n" +
            "         person_uuid,\n" +
            "         MAX(capture_date) AS MAXDATE\n" +
            "     FROM\n" +
            "         triage_vital_sign\n" +
            "     GROUP BY\n" +
            "         person_uuid\n" +
            "     ORDER BY\n" +
            "         MAXDATE ASC\n" +
            " ) AS current_triage ON current_triage.MAXDATE = tvs.capture_date\n" +
            "     AND current_triage.person_uuid = tvs.person_uuid\n" +
            "     INNER JOIN hiv_art_clinical hac ON tvs.uuid = hac.vital_sign_uuid\n" +
            "     INNER JOIN (\n" +
            "     SELECT\n" +
            "         person_uuid,\n" +
            "         MAX(hac.visit_date) AS MAXDATE\n" +
            "     FROM\n" +
            "         hiv_art_clinical hac\n" +
            "     GROUP BY\n" +
            "         person_uuid\n" +
            "     ORDER BY\n" +
            "         MAXDATE ASC\n" +
            " ) AS current_clinical_date ON current_clinical_date.MAXDATE = hac.visit_date\n" +
            "     AND current_clinical_date.person_uuid = hac.person_uuid\n" +
            "     INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid\n" +
            "     LEFT JOIN base_application_codeset bac ON bac.id = hac.clinical_stage_id\n" +
            "     LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status\n" +
            "     LEFT JOIN base_application_codeset tbs ON tbs.id = CAST(hac.tb_status AS INTEGER)\n" +
            "         WHERE\n" +
            "           hac.archived = 0\n" +
            "           AND he.archived = 0\n" +
            "           AND hac.visit_date < ?3 \n" +
            "           AND he.facility_id = ?1\n" +
            "     ),\n" +
            "\n" +
            "     sample_collection_date AS (\n" +
            "         SELECT CAST(sample.date_sample_collected AS DATE ) as dateOfViralLoadSampleCollection, patient_uuid as person_uuid120  FROM (\n" +
            "     SELECT lt.viral_load_indication, sm.facility_id,sm.date_sample_collected, sm.patient_uuid, sm.archived, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_sample_collected DESC) as rnkk\n" +
            "     FROM public.laboratory_sample  sm\n" +
            "  INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id\n" +
            "     WHERE lt.lab_test_id=16\n" +
            "       AND  lt.viral_load_indication !=719\n" +
            "       AND date_sample_collected IS NOT null\n" +
            "       AND date_sample_collected <= ?3\n" +
            " )as sample\n" +
            "         WHERE sample.rnkk = 1\n" +
            "           AND (sample.archived is null OR sample.archived = 0)\n" +
            "           AND sample.facility_id = ?1 ),\n" +
            "tbstatus as (\n" +
            "  select \n" +
            "    personUuid133, \n" +
            "    dateOfTbScreened, \n" +
            "    tbStatus,\n" +
            "    tbStatusOutcome \n" +
            "  from \n" +
            "    (\n" +
            "     SELECT \n" +
            "\t\tDISTINCT ON (hac.person_uuid) hac.person_uuid AS personUuid133,\n" +
            "\t\tho.date_of_observation AS dateOfTbScreened,\n" +
            "\t\tho.data->'tbIptScreening'->>'status' AS tbStatus, \n" +
            "\t\tho.data->'tbIptScreening'->>'outcome' AS tbStatusOutcome,\n" +
            "\t\tROW_NUMBER() OVER (\n" +
            "\t\t  PARTITION BY hac.person_uuid\n" +
            "\t\t  ORDER BY ho.date_of_observation DESC\n" +
            "\t\t) AS rowNums\n" +
            "\tFROM\n" +
            "\t\thiv_art_clinical hac\n" +
            "\t\tLEFT JOIN hiv_observation ho ON ho.person_uuid = hac.person_uuid\n" +
            "\tWHERE\n" +
            "ho.type = 'Chronic Care'\n" +
            " and ho.data is not null \n" +
            "        and hac.archived = 0 \n" +
            "        and ho.date_of_observation between ?2\n" +
            "        and ?3 \n" +
            "        and hac.facility_id = ?1\n" +
            "    ) dt \n" +
            "  where \n" +
            "    dt.rowNums = 1\n" +
            ")," +
            "tblam AS (\n" +
            "  SELECT \n" +
            "    * \n" +
            "  FROM \n" +
            "    (\n" +
            "      SELECT \n" +
            "        CAST(lr.date_result_reported AS DATE) AS dateOfLastTbLam, \n" +
            "        lr.patient_uuid as personuuidtblam, \n" +
            "        lr.result_reported as tbLamResult, \n" +
            "        ROW_NUMBER () OVER (\n" +
            "          PARTITION BY lr.patient_uuid \n" +
            "          ORDER BY \n" +
            "            lr.date_result_reported DESC\n" +
            "        ) as rank2333 \n" +
            "      FROM \n" +
            "        laboratory_result lr \n" +
            "        INNER JOIN public.laboratory_test lt on lr.test_id = lt.id \n" +
            "      WHERE \n" +
            "        lt.lab_test_id = 51 \n" +
            "        AND lr.date_result_reported IS NOT NULL \n" +
            "        AND lr.date_result_reported <= ?3 \n" +
            "        AND lr.date_result_reported >= ?2 \n" +
            "        AND lr.result_reported is NOT NULL \n" +
            "        AND lr.archived = 0 \n" +
            "        AND lr.facility_id = ?1\n" +
            "    ) as tblam \n" +
            "  WHERE \n" +
            "    tblam.rank2333 = 1\n" +
            ")," +
            "current_vl_result AS (SELECT * FROM (\n" +
            "         SELECT CAST(ls.date_sample_collected AS DATE ) AS dateOfCurrentViralLoadSample, sm.patient_uuid as person_uuid130 , sm.facility_id as vlFacility, sm.archived as vlArchived, acode.display as viralLoadIndication, sm.result_reported as currentViralLoad,CAST(sm.date_result_reported AS DATE) as dateOfCurrentViralLoad,\n" +
            "     ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) as rank2\n" +
            "         FROM public.laboratory_result  sm\n" +
            "      INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "\t  INNER JOIN public.laboratory_sample ls on ls.test_id = lt.id\n" +
            "      INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication\n" +
            "         WHERE lt.lab_test_id = 16\n" +
            "           AND  lt.viral_load_indication !=719\n" +
            "           AND sm. date_result_reported IS NOT NULL\n" +
            "           AND sm.date_result_reported <= ?3\n" +
            "           AND sm.result_reported is NOT NULL\n" +
            "     )as vl_result\n" +
            "   WHERE vl_result.rank2 = 1\n" +
            "     AND (vl_result.vlArchived = 0 OR vl_result.vlArchived is null)\n" +
            "     AND  vl_result.vlFacility = ?1\n" +
            "     ), " +
            "     careCardCD4 AS (SELECT visit_date, coalesce(cast(cd_4 as varchar), cd4_semi_quantitative) as cd_4, person_uuid AS cccd4_person_uuid\n" +
            "         FROM public.hiv_art_clinical\n" +
            "         WHERE is_commencement is true\n" +
            "           AND  archived = 0\n" +
            "           AND  cd_4 != 0\n" +
            "           AND visit_date <= ?3\n" +
            "           AND facility_id = ?1\n" +
            "     ),\n" +
            "\n" +
            "labCD4 AS (SELECT * FROM (\n" +
            "SELECT sm.patient_uuid AS cd4_person_uuid,  sm.result_reported as cd4Lb,sm.date_result_reported as dateOfCD4Lb, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) as rnk\n" +
            "FROM public.laboratory_result  sm\n" +
            "INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "WHERE lt.lab_test_id IN (1, 50) \n" +
            "AND sm. date_result_reported IS NOT NULL\n" +
            "AND sm.archived = 0\n" +
            "AND sm.facility_id = ?1\n" +
            "AND sm.date_result_reported <= ?3\n" +
            "      )as cd4_result\n" +
            "    WHERE  cd4_result.rnk = 1\n" +
            "     ),\n" +
            "\n" +
            "     tb_sample_collection AS (SELECT sample.created_by,CAST(sample.date_sample_collected AS DATE) as dateOfTbSampleCollection, patient_uuid as personTbSample  FROM (\n" +
            "SELECT llt.lab_test_name,sm.created_by, lt.viral_load_indication, sm.facility_id,sm.date_sample_collected, sm.patient_uuid, sm.archived, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_sample_collected DESC) as rnkk\n" +
            "FROM public.laboratory_sample  sm\n" +
            "         INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id\n" +
            "         INNER JOIN  laboratory_labtest llt on llt.id = lt.lab_test_id\n" +
            "WHERE lt.lab_test_id IN (65,51,66,64)\n" +
            "        AND sm.archived = 0\n" +
            "        AND sm. date_sample_collected <= ?3\n" +
            "        AND sm.facility_id = ?1\n" +
            "        )as sample\n" +
            "      WHERE sample.rnkk = 1\n" +
            "     ),\n" +
            "\n" +
            "     current_tb_result AS (WITH tb_test as (SELECT personTbResult, dateofTbDiagnosticResultReceived,\n" +
            "   coalesce(\n" +
            "           MAX(CASE WHEN lab_test_id = 65 THEN tbDiagnosticResult END) ,\n" +
            "           MAX(CASE WHEN lab_test_id = 51 THEN tbDiagnosticResult END) ,\n" +
            "           MAX(CASE WHEN lab_test_id = 66 THEN tbDiagnosticResult END),\n" +
            "           MAX(CASE WHEN lab_test_id = 64 THEN tbDiagnosticResult END),\n" +
            "           MAX(CASE WHEN lab_test_id = 67 THEN tbDiagnosticResult END),\n" +
            "           MAX(CASE WHEN lab_test_id = 68 THEN tbDiagnosticResult END)\n" +
            "       ) as tbDiagnosticResult ,\n" +
            "   coalesce(\n" +
            "           MAX(CASE WHEN lab_test_id = 65 THEN 'Gene Xpert' END) ,\n" +
            "           MAX(CASE WHEN lab_test_id = 51 THEN 'TB-LAM' END) ,\n" +
            "           MAX(CASE WHEN lab_test_id = 66 THEN 'Chest X-ray' END),\n" +
            "           MAX(CASE WHEN lab_test_id = 64 THEN 'AFB microscopy' END),\n" +
            "           MAX(CASE WHEN lab_test_id = 67 THEN 'Gene Xpert' END) ,\n" +
            "           MAX(CASE WHEN lab_test_id = 58 THEN 'TB-LAM' END)\n" +
            "       ) as tbDiagnosticTestType\n" +
            "\n" +
            "        FROM (\n" +
            "     SELECT  sm.patient_uuid as personTbResult, sm.result_reported as tbDiagnosticResult,\n" +
            " CAST(sm.date_result_reported AS DATE) as dateofTbDiagnosticResultReceived,\n" +
            " lt.lab_test_id\n" +
            "     FROM laboratory_result  sm\n" +
            "  INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "     WHERE lt.lab_test_id IN (65,51,66,64) and sm.archived = 0\n" +
            "       AND sm.date_result_reported is not null\n" +
            "       AND sm.facility_id = ?1\n" +
            "       AND sm.date_result_reported <= ?3\n" +
            " ) as dt\n" +
            "        GROUP BY dt.personTbResult, dt.dateofTbDiagnosticResultReceived)\n" +
            "   select * from (select *, row_number() over (partition by personTbResult\n" +
            "         order by dateofTbDiagnosticResultReceived desc ) as rnk from tb_test) as dt\n" +
            "   where rnk = 1\n" +
            "     ),\n" +
            "\n" +
            "     tbTreatment AS\n" +
            "         (SELECT * FROM (SELECT\n" +
            "     COALESCE(NULLIF(CAST(data->'tbIptScreening'->>'treatementType' AS text), ''), '') as tbTreatementType,\n" +
            "     NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'tbTreatmentStartDate', '') AS DATE), NULL)as tbTreatmentStartDate,\n" +
            "     CAST(data->'tbIptScreening'->>'treatmentOutcome' AS text) as tbTreatmentOutcome,\n" +
            "     NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'completionDate', '') AS DATE), NULL) as tbCompletionDate,\n" +
            "     person_uuid as tbTreatmentPersonUuid,\n" +
            "     ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_observation DESC)\n" +
            " FROM public.hiv_observation WHERE type = 'Chronic Care'\n" +
            "       AND facility_id = ?1\n" +
            ") tbTreatment WHERE row_number = 1\n" +
            "    AND tbTreatmentStartDate IS NOT NULL),\n" +
            "\n" +
            "     pharmacy_details_regimen AS (\n" +
            "         select * from (\n" +
            "   select *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid40 ORDER BY pr1.lastPickupDate DESC) as rnk3\n" +
            "   from (\n" +
            "SELECT p.person_uuid as person_uuid40, COALESCE(ds_model.display, p.dsd_model_type) as dsdModel, p.visit_date as lastPickupDate,\n" +
            "       r.description as currentARTRegimen, rt.description as currentRegimenLine,\n" +
            "       p.next_appointment as nextPickupDate,\n" +
            "       CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfARVRefill\n" +
            "from public.hiv_art_pharmacy p\n" +
            "         INNER JOIN public.hiv_art_pharmacy_regimens pr\n" +
            "        ON pr.art_pharmacy_id = p.id\n" +
            "         INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id\n" +
            "         INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id\n" +
            "left JOIN base_application_codeset ds_model on ds_model.code = p.dsd_model_type " +
            "WHERE r.regimen_type_id in (1,2,3,4,14)\n" +
            "  AND  p.archived = 0\n" +
            "  AND  p.facility_id = ?1\n" +
            "  AND  p.visit_date >= ?2\n" +
            "  AND  p.visit_date  < ?3\n" +
            "        ) as pr1\n" +
            "           ) as pr2\n" +
            "         where pr2.rnk3 = 1\n" +
            "     ),\n" +
            "\n" +
            "     eac AS (\n" +
            "         SELECT\n" +
            " DISTINCT ON (he.person_uuid) he.person_uuid AS person_uuid50,\n" +
            "      max_date_eac.eac_session_date AS dateOfCommencementOfEAC,\n" +
            "      COUNT AS numberOfEACSessionCompleted,\n" +
            "      last_eac_complete.eac_session_date AS dateOfLastEACSessionCompleted,\n" +
            "      ext_date.eac_session_date AS dateOfExtendEACCompletion,\n" +
            "      r.date_result_reported AS DateOfRepeatViralLoadResult,\n" +
            "      r.result_reported AS repeatViralLoadResult\n" +
            "         FROM\n" +
            " hiv_eac he\n" +
            "     INNER JOIN (\n" +
            "     SELECT\n" +
            "         *\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT\n" +
            "     hes.*,\n" +
            "     ROW_NUMBER() OVER (\n" +
            "         PARTITION BY hes.person_uuid\n" +
            "         ORDER BY\n" +
            " hes.eac_session_date,\n" +
            " id DESC\n" +
            "         )\n" +
            " FROM\n" +
            "     hiv_eac_session hes\n" +
            " WHERE\n" +
            "         status = 'FIRST EAC'\n" +
            "   AND archived = 0\n" +
            "         ) e\n" +
            "     WHERE\n" +
            " e.row_number = 1\n" +
            " ) AS max_date_eac ON max_date_eac.eac_id = he.uuid\n" +
            "     LEFT JOIN (\n" +
            "     SELECT\n" +
            "         person_uuid,\n" +
            "         hes.eac_id,\n" +
            "         COUNT(person_uuid) AS COUNT\n" +
            "     FROM\n" +
            "         hiv_eac_session hes\n" +
            "     GROUP BY\n" +
            "         hes.eac_id,\n" +
            "         hes.person_uuid\n" +
            " ) AS completed_eac ON completed_eac.person_uuid = max_date_eac.person_uuid\n" +
            "     AND completed_eac.eac_id = he.uuid\n" +
            "     LEFT JOIN (\n" +
            "     SELECT\n" +
            "         *\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT\n" +
            "     hes.*,\n" +
            "     ROW_NUMBER() OVER (\n" +
            "         PARTITION BY hes.person_uuid\n" +
            "         ORDER BY\n" +
            " hes.eac_session_date\n" +
            "         )\n" +
            " FROM\n" +
            "     hiv_eac he\n" +
            "         INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid\n" +
            " WHERE\n" +
            "         he.status = 'COMPLETED'\n" +
            "   AND he.archived = 0\n" +
            "         ) e\n" +
            "     WHERE\n" +
            " e.row_number = 1\n" +
            " ) AS last_eac_complete ON last_eac_complete.eac_id = max_date_eac.eac_id\n" +
            "     AND last_eac_complete.person_uuid = max_date_eac.person_uuid\n" +
            "     LEFT JOIN (\n" +
            "     SELECT\n" +
            "         *\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT\n" +
            "     hes.*,\n" +
            "     ROW_NUMBER() OVER (\n" +
            "         PARTITION BY hes.person_uuid\n" +
            "         ORDER BY\n" +
            " hes.eac_session_date,\n" +
            " id DESC\n" +
            "         )\n" +
            " FROM\n" +
            "     hiv_eac_session hes\n" +
            " WHERE\n" +
            "         hes.status NOT ilike 'FIRST%'\n" +
            "   AND status NOT ilike 'SECOND%'\n" +
            "   AND status NOT ilike 'THIRD%'\n" +
            "   AND hes.archived = 0\n" +
            "         ) e\n" +
            "     WHERE\n" +
            " e.row_number = 1\n" +
            " ) AS ext_date ON ext_date.eac_id = he.uuid\n" +
            "     AND ext_date.person_uuid = he.person_uuid\n" +
            "     LEFT JOIN (\n" +
            "     SELECT\n" +
            "         *\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT\n" +
            "     l.patient_uuid,\n" +
            "     l.date_result_reported,\n" +
            "     l.result_reported,\n" +
            "     ROW_NUMBER() OVER (\n" +
            "         PARTITION BY l.patient_uuid\n" +
            "         ORDER BY\n" +
            " l.date_result_reported ASC\n" +
            "         )\n" +
            " FROM\n" +
            "     laboratory_result l\n" +
            "         INNER JOIN (\n" +
            "         SELECT\n" +
            " lr.patient_uuid,\n" +
            " MIN(lr.date_result_reported) AS date_result_reported\n" +
            "         FROM\n" +
            " laboratory_result lr\n" +
            "     INNER JOIN public.laboratory_test  lt on lr.test_id = lt.id\n" +
            "     INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication" +
            "     INNER JOIN (\n" +
            "     SELECT\n" +
            "         *\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT\n" +
            "     hes.*,\n" +
            "     ROW_NUMBER() OVER (\n" +
            "         PARTITION BY hes.person_uuid\n" +
            "         ORDER BY\n" +
            " hes.eac_session_date,\n" +
            " he.id DESC\n" +
            "         )\n" +
            " FROM\n" +
            "     hiv_eac he\n" +
            "         INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid\n" +
            " WHERE\n" +
            "         he.status = 'COMPLETED'\n" +
            "         AND hes.eac_session_date < ?3" +
            "   AND he.archived = 0\n" +
            "         ) e\n" +
            "     WHERE\n" +
            " e.row_number = 1\n" +
            " ) AS last_eac_complete ON last_eac_complete.person_uuid = lr.patient_uuid\n" +
            "     AND lr.date_result_reported > last_eac_complete.eac_session_date\n" +
            "     AND lt.lab_test_id = 16\n" +
            "     AND last_eac_complete.eac_session_date < ?3\n" +
            "     AND  lt.viral_load_indication !=719" +
            "         GROUP BY\n" +
            " lr.patient_uuid\n" +
            "     ) r ON l.date_result_reported = r.date_result_reported\n" +
            "         AND l.patient_uuid = r.patient_uuid\n" +
            "         ) l\n" +
            "     WHERE\n" +
            " l.row_number = 1\n" +
            " ) r ON r.patient_uuid = he.person_uuid\n" +
            "         WHERE\n" +
            "     he.archived = 0\n" +
            "     ),\n" +
            "\n" +
            "biometric AS (\n" +
            "            SELECT \n" +
            "              DISTINCT ON (he.person_uuid) he.person_uuid AS person_uuid60, \n" +
            "              biometric_count.enrollment_date AS dateBiometricsEnrolled, \n" +
            "              biometric_count.count AS numberOfFingersCaptured,\n" +
            "              recapture_count.recapture_date AS dateBiometricsRecaptured,\n" +
            "              recapture_count.count AS numberOfFingersRecaptured,\n" +
            "              bst.biometric_status AS biometricStatus, \n" +
            "              bst.status_date\n" +
            "            FROM \n" +
            "              hiv_enrollment he \n" +
            "              LEFT JOIN (\n" +
            "                SELECT \n" +
            "                  b.person_uuid, \n" +
            "                  CASE WHEN COUNT(b.person_uuid) > 10 THEN 10 ELSE COUNT(b.person_uuid) END, \n" +
            "                  MAX(enrollment_date) enrollment_date \n" +
            "                FROM \n" +
            "                  biometric b \n" +
            "                WHERE \n" +
            "                  archived = 0 \n" +
            "                  AND recapture = 0 \n" +
            "                GROUP BY \n" +
            "                  b.person_uuid\n" +
            "              ) biometric_count ON biometric_count.person_uuid = he.person_uuid \n" +
            "              LEFT JOIN (\n" +
            "                SELECT \n" +
            "                  r.person_uuid, \n" +
            "                  CASE WHEN COUNT(r.person_uuid) > 10 THEN 10 ELSE COUNT(r.person_uuid) END, \n" +
            "                  MAX(enrollment_date) recapture_date \n" +
            "                FROM \n" +
            "                  biometric r \n" +
            "                WHERE \n" +
            "                  archived = 0 \n" +
            "                  AND recapture = 1 \n" +
            "                GROUP BY \n" +
            "                  r.person_uuid\n" +
            "              ) recapture_count ON recapture_count.person_uuid = he.person_uuid \n" +
            "              LEFT JOIN (\n" +
            "            \n" +
            "            SELECT DISTINCT ON (person_id) person_id, biometric_status,\n" +
            "--              (CASE WHEN biometric_status IS NULL OR biometric_status=''\n" +
            "--               THEN hiv_status ELSE biometric_status END) AS biometric_status, \n" +
            "            MAX(status_date) OVER (PARTITION BY person_id ORDER BY status_date DESC) AS status_date \n" +
            "\t\t\tFROM hiv_status_tracker \n" +
            "            WHERE archived=0 AND facility_id=?1\n" +
            "            \n" +
            "              ) bst ON bst.person_id = he.person_uuid \n" +
            "            WHERE \n" +
            "              he.archived = 0\n" +
            "            ), " +
            "     current_regimen AS (\n" +
            "         SELECT\n" +
            " DISTINCT ON (regiment_table.person_uuid) regiment_table.person_uuid AS person_uuid70,\n" +
            "      start_or_regimen AS dateOfCurrentRegimen,\n" +
            "      regiment_table.max_visit_date,\n" +
            "      regiment_table.regimen\n" +
            "         FROM\n" +
            " (\n" +
            "     SELECT\n" +
            "         MIN(visit_date) start_or_regimen,\n" +
            "         MAX(visit_date) max_visit_date,\n" +
            "         regimen,\n" +
            "         person_uuid\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT\n" +
            "     hap.id,\n" +
            "     hap.person_uuid,\n" +
            "     hap.visit_date,\n" +
            "     hivreg.description AS regimen,\n" +
            "     ROW_NUMBER() OVER(\n" +
            "         ORDER BY\n" +
            " person_uuid,\n" +
            " visit_date\n" +
            "         ) rn1,\n" +
            "     ROW_NUMBER() OVER(\n" +
            "         PARTITION BY hivreg.description\n" +
            "         ORDER BY\n" +
            " person_uuid,\n" +
            " visit_date\n" +
            "         ) rn2\n" +
            " FROM\n" +
            "     public.hiv_art_pharmacy AS hap\n" +
            "         INNER JOIN (\n" +
            "         SELECT\n" +
            " MAX(hapr.id) AS id,\n" +
            " art_pharmacy_id,\n" +
            " regimens_id,\n" +
            " hr.description\n" +
            "         FROM\n" +
            " public.hiv_art_pharmacy_regimens AS hapr\n" +
            "     INNER JOIN hiv_regimen AS hr ON hapr.regimens_id = hr.id\n" +
            "         WHERE\n" +
            "     hr.regimen_type_id IN (1,2,3,4,14)\n" +
            "         GROUP BY\n" +
            " art_pharmacy_id,\n" +
            " regimens_id,\n" +
            " hr.description\n" +
            "     ) AS hapr ON hap.id = hapr.art_pharmacy_id and hap.archived=0\n" +
            "         INNER JOIN hiv_regimen AS hivreg ON hapr.regimens_id = hivreg.id\n" +
            "         INNER JOIN hiv_regimen_type AS hivregtype ON hivreg.regimen_type_id = hivregtype.id\n" +
            "         AND hivreg.regimen_type_id IN (1,2,3,4,14)\n" +
            " ORDER BY\n" +
            "     person_uuid,\n" +
            "     visit_date\n" +
            "         ) t\n" +
            "     GROUP BY\n" +
            "         person_uuid,\n" +
            "         regimen,\n" +
            "         rn1 - rn2\n" +
            "     ORDER BY\n" +
            "         MIN(visit_date)\n" +
            " ) AS regiment_table\n" +
            "     INNER JOIN (\n" +
            "     SELECT\n" +
            "         DISTINCT MAX(visit_date) AS max_visit_date,\n" +
            "      person_uuid\n" +
            "     FROM hiv_art_pharmacy\n" +
            "WHERE archived=0" +
            "     GROUP BY\n" +
            "         person_uuid\n" +
            " ) AS hap ON regiment_table.person_uuid = hap.person_uuid\n" +
            "         WHERE\n" +
            "     regiment_table.max_visit_date = hap.max_visit_date\n" +
            "         GROUP BY\n" +
            " regiment_table.person_uuid,\n" +
            " regiment_table.regimen,\n" +
            " regiment_table.max_visit_date,\n" +
            " start_or_regimen\n" +
            "     ),\n" +
            "ipt AS (\n" +
            "              SELECT\n" +
            "             DISTINCT ON (hap.person_uuid) hap.person_uuid AS personUuid80,\n" +
            "            ipt_type.regimen_name AS iptType,\n" +
            "            hap.visit_date AS dateOfIptStart,\n" +
            "            COALESCE(NULLIF(CAST(hap.ipt->>'completionStatus' AS text), ''), '') as iptCompletionStatus,\n" +
            "            (\n" +
            "                CASE\n" +
            "               WHEN MAX(CAST(complete.date_completed AS DATE)) > NOW() THEN NULL\n" +
            "               WHEN MAX(CAST(complete.date_completed AS DATE)) IS NULL\n" +
            "            AND CAST((hap.visit_date + 168) AS DATE) < NOW() THEN CAST((hap.visit_date + 168) AS DATE)\n" +
            "               ELSE MAX(CAST(complete.date_completed AS DATE))\n" +
            "               END\n" +
            "                ) AS iptCompletionDate\n" +
            "              FROM\n" +
            "             hiv_art_pharmacy hap\n" +
            "                 INNER JOIN (\n" +
            "                 SELECT\n" +
            "              DISTINCT person_uuid,\n" +
            "                  MAX(visit_date) AS MAXDATE\n" +
            "                 FROM\n" +
            "              hiv_art_pharmacy\n" +
            "                 WHERE\n" +
            "              (ipt ->> 'type' ilike '%INITIATION%' or ipt ->> 'type' ilike 'START_REFILL')\n" +
            "            AND archived = 0\n" +
            "                 GROUP BY\n" +
            "              person_uuid\n" +
            "                 ORDER BY\n" +
            "              MAXDATE ASC\n" +
            "             ) AS max_ipt ON max_ipt.MAXDATE = hap.visit_date\n" +
            "                 AND max_ipt.person_uuid = hap.person_uuid\n" +
            "                 INNER JOIN (\n" +
            "                 SELECT\n" +
            "              DISTINCT h.person_uuid,\n" +
            "                  h.visit_date,\n" +
            "                  CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name,\n" +
            "                  CAST(pharmacy_object ->> 'duration' AS VARCHAR) AS duration,\n" +
            "                  hrt.description\n" +
            "                 FROM\n" +
            "              hiv_art_pharmacy h,\n" +
            "              jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object)\n" +
            "             RIGHT JOIN hiv_regimen hr ON hr.description = CAST(pharmacy_object ->> 'regimenName' AS VARCHAR)\n" +
            "             RIGHT JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id\n" +
            "                 WHERE\n" +
            "             hrt.id IN (15)\n" +
            "             ) AS ipt_type ON ipt_type.person_uuid = max_ipt.person_uuid\n" +
            "                 AND ipt_type.visit_date = max_ipt.MAXDATE\n" +
            "                 LEFT JOIN (\n" +
            "                 SELECT\n" +
            "              hap.person_uuid,\n" +
            "              hap.visit_date,\n" +
            "             TO_DATE(NULLIF(NULLIF(TRIM(hap.ipt->>'dateCompleted'), ''), 'null'), 'YYYY-MM-DD') AS date_completed\n" +
            "                 FROM\n" +
            "              hiv_art_pharmacy hap\n" +
            "             INNER JOIN (\n" +
            "             SELECT\n" +
            "                 DISTINCT person_uuid,\n" +
            "              MAX(visit_date) AS MAXDATE\n" +
            "             FROM\n" +
            "                 hiv_art_pharmacy\n" +
            "             WHERE\n" +
            "              ipt ->> 'dateCompleted' IS NOT NULL\n" +
            "             GROUP BY\n" +
            "                 person_uuid\n" +
            "             ORDER BY\n" +
            "                 MAXDATE ASC\n" +
            "              ) AS complete_ipt ON CAST(complete_ipt.MAXDATE AS DATE) = hap.visit_date\n" +
            "             AND complete_ipt.person_uuid = hap.person_uuid\n" +
            "             ) complete ON complete.person_uuid = hap.person_uuid\n" +
            "              WHERE\n" +
            "                 hap.archived = 0\n" +
            "                 AND hap.visit_date < ?3 \n" +
            "              GROUP BY\n" +
            "             hap.person_uuid,\n" +
            "             ipt_type.regimen_name,\n" +
            "             hap.ipt,\n" +
            "             hap.visit_date\n" +
            "                ) ,\n" +
            " cervical_cancer AS (\n" +
            "              SELECT\n" +
            "             DISTINCT ON (ho.person_uuid) ho.person_uuid AS person_uuid90,\n" +
            "                  ho.date_of_observation AS dateOfCervicalCancerScreening,\n" +
            "                  ho.data ->> 'screenTreatmentMethodDate' AS treatmentMethodDate,\n" +
            "                  cc_type.display AS cervicalCancerScreeningType,\n" +
            "                  cc_method.display AS cervicalCancerScreeningMethod,\n" +
            "                  cc_trtm.display AS cervicalCancerTreatmentScreened,\n" +
            "                  cc_result.display AS resultOfCervicalCancerScreening\n" +
            "              FROM\n" +
            "             hiv_observation ho\n" +
            "                 LEFT JOIN (\n" +
            "                 SELECT\n" +
            "              DISTINCT person_uuid,\n" +
            "                  MAX(date_of_observation) AS MAXDATE\n" +
            "                 FROM\n" +
            "              hiv_observation\n" +
            "                 WHERE\n" +
            "             archived = 0\n" +
            "            AND date_of_observation < ?3\n" +
            "                 GROUP BY\n" +
            "              person_uuid\n" +
            "                 ORDER BY\n" +
            "              MAXDATE ASC\n" +
            "             ) AS max_cc ON max_cc.MAXDATE = ho.date_of_observation\n" +
            "                 AND max_cc.person_uuid = ho.person_uuid\n" +
            "                 INNER JOIN base_application_codeset cc_type ON cc_type.code = CAST(ho.data ->> 'screenType' AS VARCHAR)\n" +
            "                 INNER JOIN base_application_codeset cc_method ON cc_method.code = CAST(ho.data ->> 'screenMethod' AS VARCHAR)\n" +
            "                 LEFT JOIN base_application_codeset cc_result ON cc_result.code = CAST(ho.data ->> 'screeningResult' AS VARCHAR)\n" +
            "                 LEFT JOIN base_application_codeset cc_trtm   ON  cc_trtm.code = CAST(ho.data ->> 'screenTreatment' AS VARCHAR)\n" +
            "               ), " +
            " ovc AS (\n" +
            "         SELECT\n" +
            " DISTINCT ON (person_uuid) person_uuid AS personUuid100,\n" +
            "   ovc_number AS ovcNumber,\n" +
            "   house_hold_number AS householdNumber\n" +
            "         FROM\n" +
            " hiv_enrollment\n" +
            "     ), \n" +
            "   previous_previous AS (\n" +
            "         SELECT\n" +
            " DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS prePrePersonUuid,\n" +
            "(\n" +
            "    CASE\n" +
            "        WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'\n" +
            "        WHEN(\n" +
            "        stat.status_date > pharmacy.maxdate\n" +
            "    AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %' )\n" +
            ")THEN stat.hiv_status\n" +
            "        ELSE pharmacy.status\n" +
            "        END\n" +
            "    ) AS status,\n" +
            "\n" +
            "(\n" +
            "    CASE\n" +
            "        WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%'  THEN stat.status_date\n" +
            "        WHEN(\n" +
            "        stat.status_date > pharmacy.maxdate\n" +
            "    AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %' )\n" +
            ") THEN stat.status_date\n" +
            "        ELSE pharmacy.visit_date\n" +
            "        END\n" +
            "    ) AS status_date,\n" +
            "\n" +
            "stat.cause_of_death, stat.va_cause_of_death\n" +
            "\n" +
            "         FROM\n" +
            " (\n" +
            "     SELECT\n" +
            "         (\n" +
            " CASE\n" +
            "     WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?5 THEN 'IIT'\n" +
            "     ELSE 'Active'\n" +
            "     END\n" +
            " ) status,\n" +
            "         (\n" +
            " CASE\n" +
            "     WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?5  THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'\n" +
            "     ELSE hp.visit_date\n" +
            "     END\n" +
            " ) AS visit_date,\n" +
            "         hp.person_uuid, MAXDATE\n" +
            "     FROM\n" +
            "         hiv_art_pharmacy hp\n" +
            " INNER JOIN (\n" +
            "         SELECT hap.person_uuid, hap.visit_date AS  MAXDATE, ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3\n" +
            "           FROM public.hiv_art_pharmacy hap \n" +
            "                    INNER JOIN public.hiv_art_pharmacy_regimens pr \n" +
            "                    ON pr.art_pharmacy_id = hap.id \n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0 \n" +
            "            INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "            INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "            WHERE r.regimen_type_id in (1,2,3,4,14) \n" +
            "            AND hap.archived = 0                \n" +
            "            AND hap.visit_date < ?3\n" +
            "             ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid \n" +
            "      AND MAX.rnkkk3 = 1" +
            "     WHERE\n" +
            " hp.archived = 0\n" +
            "       AND hp.visit_date <= ?5\n" +
            " ) pharmacy\n" +
            "\n" +
            "     LEFT JOIN (\n" +
            "     SELECT\n" +
            "         hst.hiv_status,\n" +
            "         hst.person_id,\n" +
            "         hst.cause_of_death," +
            "          hst.va_cause_of_death,\n" +
            "         hst.status_date\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death,va_cause_of_death,\n" +
            "        hiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "    FROM hiv_status_tracker WHERE archived=0 AND status_date <= ?5 )s\n" +
            " WHERE s.row_number=1\n" +
            "         ) hst\n" +
            " INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "     WHERE hst.status_date <= ?5\n" +
            " ) stat ON stat.person_id = pharmacy.person_uuid\n" +
            "\n" +
            "     ),\n" +
            "\n" +
            "\n" +
            "     previous AS (\n" +
            "         SELECT\n" +
            " DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS prePersonUuid,\n" +
            "(\n" +
            "    CASE\n" +
            "        WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'\n" +
            "        WHEN(\n" +
            "        stat.status_date > pharmacy.maxdate\n" +
            "    AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %')\n" +
            ")THEN stat.hiv_status\n" +
            "        ELSE pharmacy.status\n" +
            "        END\n" +
            "    ) AS status,\n" +
            "\n" +
            "(\n" +
            "    CASE\n" +
            "        WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%'  THEN stat.status_date\n" +
            "        WHEN(\n" +
            "        stat.status_date > pharmacy.maxdate\n" +
            "    AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %')\n" +
            ") THEN stat.status_date\n" +
            "        ELSE pharmacy.visit_date\n" +
            "        END\n" +
            "    ) AS status_date,\n" +
            "\n" +
            "stat.cause_of_death, stat.va_cause_of_death\n" +
            "\n" +
            "         FROM\n" +
            " (\n" +
            "     SELECT\n" +
            "         (\n" +
            " CASE\n" +
            "     WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?4 THEN 'IIT'\n" +
            "     ELSE 'Active'\n" +
            "     END\n" +
            " ) status,\n" +
            "         (\n" +
            " CASE\n" +
            "     WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' <  ?4 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'\n" +
            "     ELSE hp.visit_date\n" +
            "     END\n" +
            " ) AS visit_date,\n" +
            "         hp.person_uuid, MAXDATE\n" +
            "     FROM\n" +
            "         hiv_art_pharmacy hp\n" +
            " INNER JOIN (\n" +
            "         SELECT hap.person_uuid, hap.visit_date AS  MAXDATE, ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3\n" +
            "           FROM public.hiv_art_pharmacy hap \n" +
            "                    INNER JOIN public.hiv_art_pharmacy_regimens pr \n" +
            "                    ON pr.art_pharmacy_id = hap.id \n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0 \n" +
            "            INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "            INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "            WHERE r.regimen_type_id in (1,2,3,4,14) \n" +
            "            AND hap.archived = 0                \n" +
            "            AND hap.visit_date < ?4\n" +
            "             ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid \n" +
            "      AND MAX.rnkkk3 = 1" +
            "     WHERE\n" +
            " hp.archived = 0\n" +
            "       AND hp.visit_date <=  ?4\n" +
            " ) pharmacy\n" +
            "\n" +
            "     LEFT JOIN (\n" +
            "     SELECT\n" +
            "         hst.hiv_status,\n" +
            "         hst.person_id,\n" +
            "         hst.cause_of_death, " +
            "         hst.va_cause_of_death,\n" +
            "         hst.status_date\n" +
            "     FROM\n" +
            "         (\n" +
            " SELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death,va_cause_of_death,\n" +
            "        hiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "    FROM hiv_status_tracker WHERE archived=0 AND status_date <=  ?4 )s\n" +
            " WHERE s.row_number=1\n" +
            "         ) hst\n" +
            " INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "     WHERE hst.status_date <=  ?4\n" +
            " ) stat ON stat.person_id = pharmacy.person_uuid\n" +
            "     ),\n" +
            "\n" +
            "     current_status AS ( SELECT  DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS cuPersonUuid,\n" +
            "        (\n" +
            "CASE\n" +
            "    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'\n" +
            "    WHEN( stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %'))\n" +
            "        THEN stat.hiv_status\n" +
            "    ELSE pharmacy.status\n" +
            "    END\n" +
            ") AS status,\n" +
            "        (\n" +
            "CASE\n" +
            "    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%'  THEN stat.status_date\n" +
            "    WHEN(stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.status_date\n" +
            "    ELSE pharmacy.visit_date\n" +
            "    END\n" +
            ") AS status_date,\n" +
            "        stat.cause_of_death, stat.va_cause_of_death\n" +
            " FROM\n" +
            "     (\n" +
            "         SELECT\n" +
            " (\n" +
            "     CASE\n" +
            "         WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN 'IIT'\n" +
            "         ELSE 'Active'\n" +
            "         END\n" +
            "     ) status,\n" +
            " (\n" +
            "     CASE\n" +
            "         WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'\n" +
            "         ELSE hp.visit_date\n" +
            "         END\n" +
            "     ) AS visit_date,\n" +
            " hp.person_uuid, MAXDATE \n" +
            "         FROM\n" +
            " hiv_art_pharmacy hp\n" +
            "     INNER JOIN (\n" +
            "         SELECT hap.person_uuid, hap.visit_date AS  MAXDATE, ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3\n" +
            "           FROM public.hiv_art_pharmacy hap \n" +
            "                    INNER JOIN public.hiv_art_pharmacy_regimens pr \n" +
            "                    ON pr.art_pharmacy_id = hap.id \n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0 \n" +
            "            INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "            INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "            WHERE r.regimen_type_id in (1,2,3,4,14) \n" +
            "            AND hap.archived = 0                \n" +
            "            AND hap.visit_date < ?3\n" +
            "             ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid \n" +
            "      AND MAX.rnkkk3 = 1" +
            "     WHERE\n" +
            "     hp.archived = 0\n" +
            "     AND hp.visit_date < ?3\n" +
            "     ) pharmacy\n" +
            "\n" +
            "         LEFT JOIN (\n" +
            "         SELECT\n" +
            " hst.hiv_status,\n" +
            " hst.person_id,\n" +
            " hst.cause_of_death," +
            " hst.va_cause_of_death,\n" +
            " hst.status_date\n" +
            "         FROM\n" +
            " (\n" +
            "     SELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death,\n" +
            "hiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "        FROM hiv_status_tracker WHERE archived=0 AND status_date <= ?3 )s\n" +
            "     WHERE s.row_number=1\n" +
            " ) hst\n" +
            "     INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "         WHERE hst.status_date < ?3\n" +
            "     ) stat ON stat.person_id = pharmacy.person_uuid\n" +
            "     ),\n" +
            "\n" +
            "     naive_vl_data AS (\n" +
            "         SELECT pp.uuid AS nvl_person_uuid,\n" +
            "    EXTRACT(YEAR FROM AGE(NOW(), pp.date_of_birth) ) as age, ph.visit_date, ph.regimen\n" +
            "         FROM patient_person pp\n" +
            "      INNER JOIN (\n" +
            " SELECT DISTINCT * FROM (SELECT pharm.*,\n" +
            "        ROW_NUMBER() OVER (PARTITION BY pharm.person_uuid ORDER BY pharm.visit_date DESC)\n" +
            " FROM\n" +
            "     (SELECT DISTINCT * FROM hiv_art_pharmacy hap\n" +
            "         INNER JOIN hiv_art_pharmacy_regimens hapr\n" +
            "         INNER JOIN hiv_regimen hr ON hr.id=hapr.regimens_id\n" +
            "         INNER JOIN hiv_regimen_type hrt ON hrt.id=hr.regimen_type_id\n" +
            "         INNER JOIN hiv_regimen_resolver hrr ON hrr.regimensys=hr.description\n" +
            "        ON hapr.art_pharmacy_id=hap.id\n" +
            "      WHERE hap.archived=0 AND hrt.id IN (1,2,3,4,14) AND hap.facility_id = ?1 ) pharm\n" +
            ")ph WHERE ph.row_number=1\n" +
            "         )ph ON ph.person_uuid=pp.uuid\n" +
            "         WHERE pp.uuid NOT IN (\n" +
            " SELECT patient_uuid FROM (\n" +
            "      SELECT COUNT(ls.patient_uuid), ls.patient_uuid FROM laboratory_sample ls\n" +
            "  INNER JOIN laboratory_test lt ON lt.id=ls.test_id AND lt.lab_test_id=16\n" +
            "      WHERE ls.archived=0 AND ls.facility_id=?1\n" +
            "      GROUP BY ls.patient_uuid\n" +
            "  )t )\n" +
            "     ),\n" +
            "\n" +
            "crytococal_antigen as (\n" +
            " select \n" +
            "    *\n" +
            "  from \n" +
            "    (\n" +
            "      select \n" +
            "        DISTINCT ON (lr.patient_uuid) lr.patient_uuid as personuuid12, \n" +
            "        CAST(lr.date_result_reported AS DATE) AS dateOfLastCrytococalAntigen, \n" +
            "        lr.result_reported AS lastCrytococalAntigen , \n" +
            "        ROW_NUMBER() OVER (\n" +
            "          PARTITION BY lr.patient_uuid \n" +
            "          ORDER BY \n" +
            "            lr.date_result_reported DESC\n" +
            "        ) as rowNum \n" +
            "      from \n" +
            "        public.laboratory_test lt \n" +
            "        inner join laboratory_result lr on lr.test_id = lt.id \n" +
            "      where \n" +
            "        lab_test_id = 52 OR lab_test_id = 69 OR lab_test_id = 70\n" +
            "        AND lr.date_result_reported IS NOT NULL \n" +
            "        AND lr.date_result_reported <= ?3 \n" +
            "        AND lr.date_result_reported >= ?2 \n" +
            "        AND lr.result_reported is NOT NULL \n" +
            "        AND lr.archived = 0 \n" +
            "        AND lr.facility_id = ?1\n" +
            "    ) dt \n" +
            "  where \n" +
            "    rowNum = 1\n" +
            "), " +
            "case_manager AS (\n" +
            " SELECT DISTINCT ON (cmp.person_uuid)person_uuid AS caseperson, cmp.case_manager_id, CONCAT(cm.first_name, ' ', cm.last_name) AS caseManager FROM (SELECT person_uuid, case_manager_id,\n" +
            " ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY id DESC)\n" +
            " FROM case_manager_patients) cmp  INNER JOIN case_manager cm ON cm.id=cmp.case_manager_id\n" +
            " WHERE cmp.row_number=1 AND cm.facility_id=?1)" +
            "SELECT DISTINCT ON (bd.personUuid) personUuid AS uniquePersonUuid,\n" +
            "           bd.*,\n" +
            "CONCAT(bd.datimId, '_', bd.personUuid) AS ndrPatientIdentifier, " +
            "           p_lga.*,\n" +
            "           scd.*,\n" +
            "           cvlr.*,\n" +
            "           pdr.*,\n" +
            "           b.*,\n" +
            "           c.*,\n" +
            "           e.*,\n" +
            "           ca.dateOfCurrentRegimen,\n" +
            "           ca.person_uuid70,\n" +
            "           ipt.dateOfIptStart,\n" +
            "           ipt.iptCompletionDate,\n" +
            "           ipt.iptCompletionStatus,\n" +
            "           ipt.iptType,\n" +
            "           cc.*,\n" +
            "           ov.*,\n" +
            "           tbTment.*,\n" +
            "           tbSample.*,\n" +
            "           tbResult.*,\n" +
            "           tbS.*,\n" +
            "           tbl.*,\n" +
            "           crypt.*, \n" +
            "           ct.cause_of_death AS causeOfDeath,\n" +
            "           ct.va_cause_of_death AS vaCauseOfDeath,\n" +
            "           (\n" +
            "   CASE\n" +
            "       WHEN prepre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "       WHEN prepre.status ILIKE '%out%' THEN ''\n" +
            "       WHEN pre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "       WHEN pre.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "       WHEN (\n" +
            "prepre.status ILIKE '%IIT%'\n" +
            "        OR prepre.status ILIKE '%stop%'\n" +
            "    )\n" +
            "           AND (pre.status ILIKE '%ACTIVE%') THEN 'Active Restart'\n" +
            "       WHEN prepre.status ILIKE '%ACTIVE%'\n" +
            "           AND pre.status ILIKE '%ACTIVE%' THEN 'Active'\n" +
            "       ELSE REPLACE(pre.status, '_', ' ')\n" +
            "       END\n" +
            "   ) AS previousStatus,\n" +
            "           CAST((\n" +
            "   CASE\n" +
            "       WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
            "       WHEN prepre.status ILIKE '%out%' THEN prepre.status_date\n" +
            "       WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
            "       WHEN pre.status ILIKE '%out%' THEN pre.status_date\n" +
            "       WHEN (\n" +
            "prepre.status ILIKE '%IIT%'\n" +
            "        OR prepre.status ILIKE '%stop%'\n" +
            "    )\n" +
            "           AND (pre.status ILIKE '%ACTIVE%') THEN pre.status_date\n" +
            "       WHEN prepre.status ILIKE '%ACTIVE%'\n" +
            "           AND pre.status ILIKE '%ACTIVE%' THEN pre.status_date\n" +
            "       ELSE pre.status_date\n" +
            "       END\n" +
            "   ) AS DATE)AS previousStatusDate,\n" +
            "           (\n" +
            "   CASE\n" +
            "       WHEN prepre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "       WHEN prepre.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "       WHEN pre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "       WHEN pre.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "       WHEN ct.status ILIKE '%IIT%' THEN 'IIT'\n" +
            "       WHEN ct.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "       WHEN ct.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "       WHEN (\n" +
            "pre.status ILIKE '%IIT%'\n" +
            "        OR pre.status ILIKE '%stop%'\n" +
            "    )\n" +
            "           AND (ct.status ILIKE '%ACTIVE%') THEN 'Active Restart'\n" +
            "       WHEN pre.status ILIKE '%ACTIVE%'\n" +
            "           AND ct.status ILIKE '%ACTIVE%' THEN 'Active'\n" +
            "       ELSE REPLACE(ct.status, '_', ' ')\n" +
            "       END\n" +
            "   ) AS currentStatus,\n" +
            "           CAST((\n" +
            "   CASE\n" +
            "       WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
            "       WHEN prepre.status ILIKE '%out%' THEN prepre.status_date\n" +
            "       WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
            "       WHEN pre.status ILIKE '%out%' THEN pre.status_date\n" +
            "       WHEN ct.status ILIKE '%IIT%' THEN\n" +
            "           CASE\n" +
            "   WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%out%' OR pre.status ILIKE '%stop%') THEN pre.status_date\n" +
            "   ELSE ct.status_date --check the pre to see the status and return date appropriate\n" +
            "   END\n" +
            "       WHEN ct.status ILIKE '%stop%' THEN\n" +
            "           CASE\n" +
            "   WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%out%' OR pre.status ILIKE '%IIT%') THEN pre.status_date\n" +
            "   ELSE ct.status_date --check the pre to see the status and return date appropriate\n" +
            "   END\n" +
            "       WHEN ct.status ILIKE '%out%' THEN\n" +
            "           CASE\n" +
            "   WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%stop%' OR pre.status ILIKE '%IIT%') THEN pre.status_date\n" +
            "   ELSE ct.status_date --check the pre to see the status and return date appropriate\n" +
            "   END\n" +
            "       WHEN (\n" +
            "pre.status ILIKE '%IIT%'\n" +
            "        OR pre.status ILIKE '%stop%'\n" +
            "    )\n" +
            "           AND (ct.status ILIKE '%ACTIVE%') THEN ct.status_date\n" +
            "       WHEN pre.status ILIKE '%ACTIVE%'\n" +
            "           AND ct.status ILIKE '%ACTIVE%' THEN ct.status_date\n" +
            "       ELSE ct.status_date\n" +
            "       END\n" +
            "   )AS DATE) AS currentStatusDate,\n" +
            "           (\n" +
            "   CASE\n" +
            "       WHEN prepre.status ILIKE '%DEATH%' THEN FALSE\n" +
            "       WHEN prepre.status ILIKE '%out%' THEN FALSE\n" +
            "       WHEN pre.status ILIKE '%DEATH%' THEN FALSE\n" +
            "       WHEN pre.status ILIKE '%out%' THEN FALSE\n" +
            "       WHEN ct.status ILIKE '%IIT%' THEN FALSE\n" +
            "       WHEN ct.status ILIKE '%out%' THEN FALSE\n" +
            "       WHEN ct.status ILIKE '%DEATH%' THEN FALSE\n" +
            "       WHEN ct.status ILIKE '%stop%' THEN FALSE\n" +
            "       WHEN (nvd.age >= 15\n" +
            "           AND nvd.regimen ILIKE '%DTG%'\n" +
            "           AND bd.artstartdate + 91 < ?3) THEN TRUE\n" +
            "       WHEN (nvd.age >= 15\n" +
            "           AND nvd.regimen NOT ILIKE '%DTG%'\n" +
            "           AND bd.artstartdate + 181 < ?3) THEN TRUE\n" +
            "       WHEN (nvd.age <= 15 AND bd.artstartdate + 181 < ?3) THEN TRUE\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "           AND scd.dateofviralloadsamplecollection IS NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "           AND CAST(bd.artstartdate AS DATE) + 181 < ?3 THEN TRUE\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "           AND scd.dateofviralloadsamplecollection IS NOT NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "           AND CAST(bd.artstartdate AS DATE) + 91 < ?3 THEN TRUE\n" +
            "\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "           AND( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "   OR  scd.dateofviralloadsamplecollection IS NULL )\n" +
            "           AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3 THEN TRUE\n" +
            "\n" +
            "       WHEN  CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "           AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "   OR cvlr.dateofcurrentviralload IS NULL\n" +
            "     )\n" +
            "           AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN TRUE\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000\n" +
            "           AND ( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "   OR\n" +
            "     scd.dateofviralloadsamplecollection IS NULL\n" +
            "    )\n" +
            "           AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3 THEN TRUE\n" +
            "\n" +
            "       WHEN\n" +
            "       CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000\n" +
            "   AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "   OR cvlr.dateofcurrentviralload IS NULL)\n" +
            "   AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN TRUE\n" +
            "\n" +
            "       ELSE FALSE\n" +
            "       END\n" +
            "   ) AS vlEligibilityStatus,\n" +
            "           CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) AS test,\n" +
            "\n" +
            "           (\n" +
            "   CASE\n" +
            "       WHEN prepre.status ILIKE '%DEATH%' THEN NULL\n" +
            "       WHEN prepre.status ILIKE '%out%' THEN NULL\n" +
            "       WHEN pre.status ILIKE '%DEATH%' THEN NULL\n" +
            "       WHEN pre.status ILIKE '%out%' THEN NULL\n" +
            "       WHEN ct.status ILIKE '%IIT%' THEN NULL\n" +
            "       WHEN ct.status ILIKE '%out%' THEN NULL\n" +
            "       WHEN ct.status ILIKE '%DEATH%' THEN NULL\n" +
            "       WHEN ct.status ILIKE '%stop%' THEN NULL\n" +
            "       WHEN (nvd.age >= 15\n" +
            "           AND nvd.regimen ILIKE '%DTG%'\n" +
            "           AND bd.artstartdate + 91 < ?3)\n" +
            "           THEN CAST(bd.artstartdate + 91 AS DATE)\n" +
            "       WHEN (nvd.age >= 15\n" +
            "           AND nvd.regimen NOT ILIKE '%DTG%'\n" +
            "           AND bd.artstartdate + 181 < ?3)\n" +
            "           THEN CAST(bd.artstartdate + 181 AS DATE)\n" +
            "       WHEN (nvd.age <= 15 AND bd.artstartdate + 181 < ?3)\n" +
            "           THEN CAST(bd.artstartdate + 181 AS DATE)\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "           AND scd.dateofviralloadsamplecollection IS NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "           AND CAST(bd.artstartdate AS DATE) + 181 < ?3 THEN\n" +
            "   CAST(bd.artstartdate AS DATE) + 181\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "           AND scd.dateofviralloadsamplecollection IS NOT NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "           AND CAST(bd.artstartdate AS DATE) + 91 < ?3 THEN\n" +
            "   CAST(bd.artstartdate AS DATE) + 91\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "           AND( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "   OR  scd.dateofviralloadsamplecollection IS NULL )\n" +
            "           AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3\n" +
            "           THEN CAST(cvlr.dateofcurrentviralload AS DATE) + 181\n" +
            "\n" +
            "\n" +
            "\n" +
            "       WHEN  CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "           AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "   OR cvlr.dateofcurrentviralload IS NULL\n" +
            "     )\n" +
            "           AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN\n" +
            "   CAST(scd.dateofviralloadsamplecollection AS DATE) + 91\n" +
            "\n" +
            "       WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000\n" +
            "           AND ( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "   OR\n" +
            "     scd.dateofviralloadsamplecollection IS NULL\n" +
            "    )\n" +
            "           AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3 THEN\n" +
            "   CAST(cvlr.dateofcurrentviralload AS DATE) + 91\n" +
            "\n" +
            "       WHEN\n" +
            "       CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000\n" +
            "   AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "   OR cvlr.dateofcurrentviralload IS NULL)\n" +
            "   AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN\n" +
            "   CAST(scd.dateofviralloadsamplecollection AS DATE) + 91\n" +
            "\n" +
            "       ELSE NULL\n" +
            "       END\n" +
            "   ) AS dateOfVlEligibilityStatus,\n" +
            "           (CASE WHEN cd.cd4lb IS NOT NULL THEN  cd.cd4lb" +
            "                 WHEN  ccd.cd_4 IS NOT NULL THEN CAST(ccd.cd_4 as VARCHAR)\n" +
            "     ELSE NULL END) as lastCd4Count,\n" +
            "           (CASE WHEN cd.dateOfCd4Lb IS NOT NULL THEN  CAST(cd.dateOfCd4Lb as DATE)" +
            "                   WHEN ccd.visit_date IS NOT NULL THEN CAST(ccd.visit_date as DATE)\n" +
            "     ELSE NULL END) as dateOfLastCd4Count, \n" +
            "INITCAP(cm.caseManager) AS caseManager " +
            "FROM bio_data bd\n" +
            "        LEFT JOIN patient_lga p_lga on p_lga.personUuid11 = bd.personUuid \n" +
            "        LEFT JOIN pharmacy_details_regimen pdr ON pdr.person_uuid40 = bd.personUuid\n" +
            "        LEFT JOIN current_clinical c ON c.person_uuid10 = bd.personUuid\n" +
            "        LEFT JOIN sample_collection_date scd ON scd.person_uuid120 = bd.personUuid\n" +
            "        LEFT JOIN current_vl_result  cvlr ON cvlr.person_uuid130 = bd.personUuid\n" +
            "        LEFT JOIN  labCD4 cd on cd.cd4_person_uuid = bd.personUuid\n" +
            "        LEFT JOIN  careCardCD4 ccd on ccd.cccd4_person_uuid = bd.personUuid\n" +
            "        LEFT JOIN eac e ON e.person_uuid50 = bd.personUuid\n" +
            "        LEFT JOIN biometric b ON b.person_uuid60 = bd.personUuid\n" +
            "        LEFT JOIN current_regimen  ca ON ca.person_uuid70 = bd.personUuid\n" +
            "        LEFT JOIN ipt ipt ON ipt.personUuid80 = bd.personUuid\n" +
            "        LEFT JOIN cervical_cancer cc ON cc.person_uuid90 = bd.personUuid\n" +
            "        LEFT JOIN ovc ov ON ov.personUuid100 = bd.personUuid\n" +
            "        LEFT JOIN current_status ct ON ct.cuPersonUuid = bd.personUuid\n" +
            "        LEFT JOIN previous pre ON pre.prePersonUuid = ct.cuPersonUuid\n" +
            "        LEFT JOIN previous_previous prepre ON prepre.prePrePersonUuid = ct.cuPersonUuid\n" +
            "        LEFT JOIN naive_vl_data nvd ON nvd.nvl_person_uuid = bd.personUuid\n" +
            "        LEFT JOIN tb_sample_collection tbSample ON tbSample.personTbSample = bd.personUuid\n" +
            "        LEFT JOIN  tbTreatment tbTment ON tbTment.tbTreatmentPersonUuid = bd.personUuid\n" +
            "        LEFT JOIN  current_tb_result tbResult ON tbResult.personTbResult = bd.personUuid\n" +
            "        LEFT JOIN crytococal_antigen crypt on crypt.personuuid12= bd.personUuid" +
            "        LEFT JOIN  tbstatus tbS on tbS.personUuid133 = bd.personUuid" +
            "        LEFT JOIN  tblam tbl  on tbl.personuuidtblam = bd.personUuid" +
            "       LEFT JOIN case_manager cm on cm.caseperson= bd.personUuid"
            , nativeQuery = true)
    List<RADETDTOProjection> getRadetData(Long facilityId, LocalDate start, LocalDate end,
                                          LocalDate previous, LocalDate previousPrevious, LocalDate dateOfStartOfCurrentQuarter);

    @Query(value = "SELECT  DISTINCT (p.uuid) AS patientId, \n" +
            "                            p.hospital_number AS hospitalNumber, \n" +
            "                            EXTRACT( \n" +
            "                                    YEAR \n" +
            "                                    FROM \n" +
            "                                    AGE(NOW(), date_of_birth) \n" +
            "                                ) AS age, \n" +
            "                            INITCAP(p.sex) AS gender, \n" +
            "                            p.date_of_birth AS dateOfBirth, \n" +
            "                            facility.name AS facilityName, \n" +
            "                            facility_lga.name AS lga, \n" +
            "                            facility_state.name AS state, \n" +
            "                            boui.code AS datimId, \n" +
            "            tvs.*, \n" +
            "            tvs.body_weight as BodyWeight,  \n" +
            "           (CASE\n" +
            "    WHEN hac.pregnancy_status = 'Not Pregnant' THEN hac.pregnancy_status\n" +
            "    WHEN hac.pregnancy_status = 'Pregnant' THEN hac.pregnancy_status\n" +
            "    WHEN hac.pregnancy_status = 'Breastfeeding' THEN hac.pregnancy_status\n" +
            "    WHEN hac.pregnancy_status = 'Post Partum' THEN hac.pregnancy_status\n" +
            "    WHEN preg.display IS NOT NULL THEN hac.pregnancy_status\n" +
            "    ELSE NULL END ) AS pregnancyStatus, \n" +
            "            hac.next_appointment as nextAppointment , \n" +
            "            hac.visit_date as visitDate, \n" +
            "            funStatus.display as funtionalStatus, \n" +
            "            clnicalStage.display as clinicalStage, \n" +
            "            tbStatus.display as tbStatus \n" +
            "            FROM \n" +
            "                 patient_person p \n" +
            "                       INNER JOIN base_organisation_unit facility ON facility.id = facility_id \n" +
            "                       INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id \n" +
            "                       INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id \n" +
            "                       INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID' \n" +
            "                       INNER JOIN hiv_art_clinical hac ON hac.person_uuid = p.uuid  \n" +
            " \t\t   LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status\n" +
            "           INNER JOIN base_application_codeset funStatus ON funStatus.id = hac.functional_status_id \n" +
            "           INNER JOIN base_application_codeset clnicalStage ON clnicalStage.id = hac.clinical_stage_id \n" +
            "           INNER JOIN base_application_codeset tbStatus ON tbStatus.id = CAST(regexp_replace(hac.tb_status, '[^0-9]', '', 'g') AS INTEGER)  \n" +
            "           INNER JOIN triage_vital_sign tvs ON tvs.uuid = hac.vital_sign_uuid \n" +
            "                       AND hac.archived = 0 \n" +
            "               WHERE   hac.archived = 0 \n" +
            "           AND hac.facility_id =?1", nativeQuery = true)
    List<ClinicDataDto> getClinicData(Long facilityId);

    @Query(value = "WITH bio_data AS (\n" +
            "\t  SELECT \n" +
            "\t\tDISTINCT ON (p.uuid) p.uuid as personUuid, \n" +
            "\t\tp.id, \n" +
            "\t\tCAST(p.archived AS BOOLEAN) as archived, \n" +
            "\t\tp.uuid, \n" +
            "\t\tp.hospital_number as hospitalNumber, \n" +
            "\t\tp.surname, \n" +
            "\t\tp.first_name as firstName, \n" +
            "\t\tEXTRACT(\n" +
            "\t\t  YEAR \n" +
            "\t\t  from \n" +
            "\t\t\tAGE(NOW(), date_of_birth)\n" +
            "\t\t) as age, \n" +
            "\t\tp.other_name as otherName, \n" +
            "\t\tp.sex as gender, \n" +
            "\t\tp.date_of_birth as dateOfBirth, \n" +
            "\t\tp.date_of_registration as dateOfRegistration, \n" +
            "\t\tp.marital_status ->> 'display' as maritalStatus, \n" +
            "\t\teducation ->> 'display' as education, \n" +
            "\t\tp.employment_status ->> 'display' as occupation, \n" +
            "\t\tfacility.name as facilityName, \n" +
            "\t\tfacility_lga.name as lga, \n" +
            "\t\tfacility_state.name as state, \n" +
            "\t\tboui.code as datimId, \n" +
            "\t\tres_state.name as residentialState, \n" +
            "\t\tres_lga.name as residentialLga, \n" +
            "\t\tr.address as address, \n" +
            "\t\tp.contact_point -> 'contactPoint' -> 0 -> 'value' ->> 0 AS phone \n" +
            "\t  FROM \n" +
            "\t\tpatient_person p \n" +
            "\t\tINNER JOIN (\n" +
            "\t\t\t  SELECT \n" +
            "\t\t\t\t* \n" +
            "\t\t\t  FROM \n" +
            "\t\t\t\t(\n" +
            "\t\t\t\t   SELECT\n" +
            "\t\t\t\t\t\tp.id,\n" +
            "\t\t\t\t\t\tCASE WHEN address_object->>'city' IS NOT NULL\n" +
            "\t\t\t\t\t\t\t THEN CONCAT_WS(' ', address_object->>'city', \n" +
            "\t\t\t\t\t\t\t\t\t\t\tREPLACE(REPLACE(COALESCE(NULLIF(address_object->>'line', '\\\\'), ''), ']', ''), '[', ''), \n" +
            "\t\t\t\t\t\t\t\t\t\t\tNULLIF(NULLIF(address_object->>'stateId', 'null'), '')) \n" +
            "\t\t\t\t\t\t\t ELSE NULL \n" +
            "\t\t\t\t\t\tEND AS address,\n" +
            "\t\t\t\t\t\tCASE WHEN address_object->>'stateId' ~ '^\\d+(\\.\\d+)?$' \n" +
            "\t\t\t\t\t\t\t THEN address_object->>'stateId' \n" +
            "\t\t\t\t\t\t\t ELSE NULL \n" +
            "\t\t\t\t\t\tEND AS stateId,\n" +
            "\t\t\t\t\t\tCASE WHEN address_object->>'stateId' ~ '^\\d+(\\.\\d+)?$' \n" +
            "\t\t\t\t\t\t\t THEN address_object->>'district' \n" +
            "\t\t\t\t\t\t\t ELSE NULL \n" +
            "\t\t\t\t\t\tEND AS lgaId\n" +
            "\t\t\t\t\tFROM patient_person p\n" +
            "\t\t\t\t\tCROSS JOIN jsonb_array_elements(p.address->'address') AS l(address_object)\n" +
            "\t\t\t\t)  as result\n" +
            "\t\t\t ) r ON r.id=p.id\n" +
            "\t\t\t\t INNER JOIN base_organisation_unit facility ON facility.id=facility_id\n" +
            "\t\t\t\t INNER JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id\n" +
            "\t\t\t\t INNER JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id\n" +
            "\t\t\t\t LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateId AS BIGINT)\n" +
            "\t\t\t\t LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(CASE WHEN r.lgaId ~ E'^\\\\d+$' THEN r.lgaId ELSE NULL END AS BIGINT)\n" +
            "\t\t\t\t INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=?1 AND boui.name='DATIM_ID'\n" +
            "\t\t\t\t INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            "\t\t\t\t WHERE p.archived=0 AND h.archived=0 AND h.facility_id=?1\n" +
            "\t\t\t\t),\n" +
            "\t\t\t enrollment_details AS (\n" +
            "\t\t\t\t SELECT h.person_uuid,h.unique_id as uniqueId,  sar.display as statusAtRegistration, date_confirmed_hiv as dateOfConfirmedHiv,\n" +
            "\t\t\t\t ep.display as entryPoint, date_of_registration as dateOfRegistration\n" +
            "\t\t\t\t FROM hiv_enrollment h\n" +
            "\t\t\t\t LEFT JOIN base_application_codeset sar ON sar.id=h.status_at_registration_id\n" +
            "\t\t\t\t LEFT JOIN base_application_codeset ep ON ep.id=h.entry_point_id\n" +
            "\t\t\t\t WHERE h.archived=0 AND h.facility_id=?1\n" +
            "\t\t\t ),\n" +
            "\t\t\t laboratory_details AS (\n" +
            "\t\t\t\t SELECT DISTINCT ON(lo.patient_uuid) lo.patient_uuid as person_uuid, ll.lab_test_name as test,\n" +
            "\t\t\t\t bac_viral_load.display viralLoadType, ls.date_sample_collected as dateSampleCollected,\n" +
            "\t\t\t\t lr.result_reported as lastViralLoad, lr.date_result_reported as dateOfLastViralLoad\n" +
            "\t\t\t\t FROM laboratory_order lo\n" +
            "\t\t\t\t LEFT JOIN ( SELECT patient_uuid, MAX(order_date) AS MAXDATE FROM laboratory_order lo\n" +
            "\t\t\t\t GROUP BY patient_uuid ORDER BY MAXDATE ASC ) AS current_lo\n" +
            "\t\t\t\t ON current_lo.patient_uuid=lo.patient_uuid AND current_lo.MAXDATE=lo.order_date\n" +
            "\t\t\t\t LEFT JOIN laboratory_test lt ON lt.lab_order_id=lo.id AND lt.patient_uuid = lo.patient_uuid\n" +
            "\t\t\t\t LEFT JOIN base_application_codeset bac_viral_load ON bac_viral_load.id=lt.viral_load_indication\n" +
            "\t\t\t\t LEFT JOIN laboratory_labtest ll ON ll.id=lt.lab_test_id\n" +
            "\t\t\t\t INNER JOIN hiv_enrollment h ON h.person_uuid=current_lo.patient_uuid\n" +
            "\t\t\t\t LEFT JOIN laboratory_sample ls ON ls.test_id=lt.id AND ls.patient_uuid = lo.patient_uuid\n" +
            "\t\t\t\t LEFT JOIN laboratory_result lr ON lr.test_id=lt.id AND lr.patient_uuid = lo.patient_uuid\n" +
            "\t\t\t\t WHERE ll.lab_test_name = ' Viral Load ' AND h.archived=0 AND lo.archived=0 AND lo.facility_id=?1\n" +
            "\t\t\t ),\n" +
            "\t\t\t pharmacy_details AS (\n" +
            "\t\t\t\t SELECT DISTINCT ON (hartp.person_uuid)hartp.person_uuid as person_uuid, r.visit_date as dateOfLastRefill,\n" +
            "\t\t\t\t hartp.next_appointment as dateOfNextRefill, hartp.refill_period as lastRefillDuration,\n" +
            "\t\t\t\t hartp.dsd_model_type as DSDType, r.description as currentRegimenLine, r.regimen_name as currentRegimen,\n" +
            "\t\t\t\t (CASE\n" +
            "\t\t\t\t WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '\n" +
            "\t\t\t\t OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.hiv_status\n" +
            "\t\t\t\t WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' < CURRENT_DATE\n" +
            "\t\t\t\t THEN ' IIT ' ELSE ' ACTIVE '\n" +
            "\t\t\t\t END)AS currentStatus,\n" +
            "\t\t\t\t (CASE\n" +
            "\t\t\t\t WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '\n" +
            "\t\t\t\t OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.status_date\n" +
            "\t\t\t\t WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' <= CURRENT_DATE\n" +
            "\t\t\t\t THEN CAST((hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ') AS date) ELSE hartp.visit_date\n" +
            "\t\t\t\t END)AS dateOfCurrentStatus\n" +
            "\t\t\t\t FROM hiv_art_pharmacy hartp\n" +
            "\t\t\t\t INNER JOIN (SELECT distinct r.* FROM (SELECT h.person_uuid, h.visit_date, CAST(pharmacy_object ->> ' regimenName ' AS VARCHAR) AS regimen_name,\n" +
            "\t\t\t\t hrt.description FROM hiv_art_pharmacy h,\n" +
            "\t\t\t\t jsonb_array_elements(h.extra->' regimens ') with ordinality p(pharmacy_object)\n" +
            "\t\t\t\t INNER JOIN hiv_regimen hr ON hr.description=CAST(pharmacy_object ->> ' regimenName ' AS VARCHAR)\n" +
            "\t\t\t\t INNER JOIN hiv_regimen_type hrt ON hrt.id=hr.regimen_type_id\n" +
            "\t\t\t\t WHERE hrt.id IN (1,2,3,4,14))r\n" +
            "\t\t\t\t INNER JOIN (SELECT hap.person_uuid, MAX(visit_date) AS MAXDATE FROM hiv_art_pharmacy hap\n" +
            "\t\t\t\t INNER JOIN hiv_enrollment h ON h.person_uuid=hap.person_uuid  WHERE h.archived=0\n" +
            "\t\t\t\t GROUP BY hap.person_uuid ORDER BY MAXDATE ASC ) max ON\n" +
            "\t\t\t\t max.MAXDATE=r.visit_date AND r.person_uuid=max.person_uuid) r\n" +
            "\t\t\t\t ON r.visit_date=hartp.visit_date AND r.person_uuid=hartp.person_uuid\n" +
            "\t\t\t\t INNER JOIN hiv_enrollment he ON he.person_uuid=r.person_uuid\n" +
            "\t\t\t\t LEFT JOIN (SELECT sh1.person_id, sh1.hiv_status, sh1.status_date\n" +
            "\t\t\t\t FROM hiv_status_tracker sh1\n" +
            "\t\t\t\t INNER JOIN\n" +
            "\t\t\t\t (\n" +
            "\t\t\t\t\tSELECT person_id as p_id, MAX(hst.id) AS MAXID\n" +
            "\t\t\t\t\tFROM hiv_status_tracker hst INNER JOIN hiv_enrollment h ON h.person_uuid=person_id\n" +
            "\t\t\t\t\tGROUP BY person_id\n" +
            "\t\t\t\t ORDER BY person_id ASC\n" +
            "\t\t\t\t ) sh2 ON sh1.person_id = sh2.p_id AND sh1.id = sh2.MAXID\n" +
            "\t\t\t\t ORDER BY sh1.person_id ASC) stat ON stat.person_id=hartp.person_uuid\n" +
            "\t\t\t\t WHERE he.archived=0 AND hartp.archived=0 AND hartp.facility_id=?1 ORDER BY hartp.person_uuid ASC\n" +
            "\t\t\t ),\n" +
            "\t\t\t art_commencement_vitals AS (\n" +
            "\t\t\t\t SELECT DISTINCT ON (tvs.person_uuid) tvs.person_uuid , body_weight as baseLineWeight, height as baseLineHeight,\n" +
            "\t\t\t\t CONCAT(diastolic, ' / ', systolic) as baseLineBp, diastolic as diastolicBp,\n" +
            "\t\t\t\t systolic as systolicBp, clinical_stage.display as baseLineClinicalStage,\n" +
            "\t\t\t\t func_status.display as baseLineFunctionalStatus,\n" +
            "\t\t\t\t hv.description as firstRegimen, hrt.description as firstRegimenLine,\n" +
            "\t\t\t\t CASE WHEN cd_4=0 THEN null ELSE cd_4 END  AS baseLineCd4,\n" +
            "\t\t\t\t CASE WHEN cd_4_percentage=0 THEN null ELSE cd_4_percentage END AS cd4Percentage,\n" +
            "\t\t\t\t hac.visit_date as artStartDate\n" +
            "\t\t\t\t FROM triage_vital_sign tvs\n" +
            "\t\t\t\t INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid\n" +
            "\t\t\t\t AND hac.is_commencement=true AND hac.person_uuid = tvs.person_uuid\n" +
            "\t\t\t\t INNER JOIN hiv_enrollment h ON hac.hiv_enrollment_uuid = h.uuid AND hac.person_uuid=tvs.person_uuid\n" +
            "\t\t\t\t INNER JOIN patient_person p ON p.uuid=h.person_uuid\n" +
            "\t\t\t\t RIGHT JOIN hiv_regimen hv ON hv.id=hac.regimen_id\n" +
            "\t\t\t\t RIGHT JOIN hiv_regimen_type hrt ON hrt.id=hac.regimen_type_id\n" +
            "\t\t\t\t RIGHT JOIN base_application_codeset clinical_stage ON clinical_stage.id=hac.clinical_stage_id\n" +
            "\t\t\t\t RIGHT JOIN base_application_codeset func_status ON func_status.id=hac.functional_status_id\n" +
            "\t\t\t\t   WHERE hac.archived=0  AND h.archived=0 AND h.facility_id=?1\n" +
            "\t\t\t ),\n" +
            "             current_clinical AS (\n" +
            "\t\t\t\t SELECT tvs.person_uuid, hac.adherence_level as adherenceLevel, hac.next_appointment as dateOfNextClinic, body_weight as currentWeight, height as currentHeight,\n" +
            "\t\t\t\t  diastolic as currentDiastolic, systolic as currentSystolic, bac.display as currentClinicalStage,\n" +
            "\t\t\t\t  CONCAT(diastolic, ' / ', systolic) as currentBp, current_clinical_date.MAXDATE as dateOfLastClinic\n" +
            "\t\t\t\t FROM triage_vital_sign tvs\n" +
            "\t\t\t\t INNER JOIN ( SELECT person_uuid, MAX(capture_date) AS MAXDATE FROM triage_vital_sign\n" +
            "\t\t\t\t GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_triage\n" +
            "\t\t\t\t ON current_triage.MAXDATE=tvs.capture_date AND current_triage.person_uuid=tvs.person_uuid\n" +
            "\t\t\t\t INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid\n" +
            "\t\t\t\t INNER JOIN ( SELECT person_uuid, MAX(hac.visit_date) AS MAXDATE FROM hiv_art_clinical hac\n" +
            "\t\t\t\t GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_clinical_date\n" +
            "\t\t\t\t ON current_clinical_date.MAXDATE=hac.visit_date AND current_clinical_date.person_uuid=hac.person_uuid\n" +
            "\t\t\t\t INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid\n" +
            "\t\t\t\t INNER JOIN base_application_codeset bac ON bac.id=hac.clinical_stage_id\n" +
            "\t\t\t\t WHERE hac.archived=0 AND he.archived=0 AND he.facility_id=?1\n" +
            "\t\t\t )\n" +
            "             SELECT\n" +
            "             DISTINCT ON (b.personUuid)b.personUuid AS personUuid,\n" +
            "             b.archived,\n" +
            "             b.hospitalNumber,\n" +
            "             b.surname,\n" +
            "             b.firstName,\n" +
            "             b.age,\n" +
            "             b.otherName,\n" +
            "             b.gender,\n" +
            "             b.dateOfBirth,\n" +
            "             b.maritalStatus,\n" +
            "             b.education,\n" +
            "             b.occupation,\n" +
            "             b.facilityName,\n" +
            "             b.lga,\n" +
            "             b.state,\n" +
            "             b.datimId,\n" +
            "             b.residentialState,\n" +
            "             b.residentialLga,\n" +
            "             b.address,\n" +
            "             b.phone,\n" +
            "             c.currentWeight,\n" +
            "             c.currentHeight,\n" +
            "             c.currentDiastolic as currentDiastolicBp,\n" +
            "             c.currentSystolic as currentSystolicBP,                \n" +
            "             c.currentBp,\n" +
            "             c.dateOfLastClinic,\n" +
            "             c.dateOfNextClinic,\n" +
            "             c.adherenceLevel,\n" +
            "             c.currentClinicalStage as lastClinicStage,\n" +
            "             e.statusAtRegistration,\n" +
            "             e.dateOfConfirmedHiv as dateOfConfirmedHIVTest,\n" +
            "             e.entryPoint as careEntryPoint,\n" +
            "             e.uniqueId,\n" +
            "             e.dateOfRegistration,\n" +
            "             p.dateOfNextRefill,\n" +
            "             p.lastRefillDuration,\n" +
            "             p.dateOfLastRefill,\n" +
            "             p.DSDType,\n" +
            "             p.currentRegimen,\n" +
            "             p.currentRegimenLine,\n" +
            "             p.currentStatus,\n" +
            "             p.dateOfCurrentStatus as dateOfCurrentStatus,\n" +
            "             l.test,                  \n" +
            "             l.viralLoadType,\n" +
            "             l.dateSampleCollected as dateOfSampleCollected ,\n" +
            "             l.lastViralLoad,\n" +
            "             l.dateOfLastViralLoad,\n" +
            "             a.baseLineWeight,\n" +
            "             a.baseLineHeight,\n" +
            "             a.baseLineBp,\n" +
            "             a.diastolicBp,                       \n" +
            "             a.systolicBp,\n" +
            "             a.baseLineClinicalStage as baselineClinicStage,\n" +
            "             a.baseLineFunctionalStatus,                      \n" +
            "             a.firstRegimen,\n" +
            "             a.firstRegimenLine,\n" +
            "             a.baseLineCd4,                      \n" +
            "             a.cd4Percentage,                          \n" +
            "             a.artStartDate\n" +
            "             FROM enrollment_details e\n" +
            "             INNER JOIN bio_data b ON e.person_uuid=b.personUuid\n" +
            "             LEFT JOIN art_commencement_vitals a ON a.person_uuid=e.person_uuid\n" +
            "             LEFT JOIN pharmacy_details p ON p.person_uuid=e.person_uuid\n" +
            "             LEFT JOIN laboratory_details l ON l.person_uuid=e.person_uuid\n" +
            "             LEFT JOIN current_clinical c ON c.person_uuid=e.person_uuid",
            nativeQuery = true)
    List<PatientLineDto> getPatientLineByFacilityId(Long facilityId);

    @Query(value = "SELECT result.id, result.surname,\n" +
            "result.hospital_number as hospitalNumber, result.date_of_birth as dob, result.phone, result.age, result.name, result.sex,\n" +
            "result.facility_id, result.address, count(b.person_uuid) as finger, b.enrollment_date as enrollment\n" +
            "FROM (SELECT p.id, EXTRACT(YEAR from AGE(NOW(),  p.date_of_birth)) as age, p.contact_point->'contactPoint'->0->'value'->>0 as phone, \n" +
            "      concat(p.surname ,' ', p.first_name) as name, p.hospital_number, p.date_of_birth, p.sex,\n" +
            "      p.facility_id, p.surname, p.uuid, p.archived, " +
            "  CONCAT(address_object->>'city', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\\\\', ''), ']', ''), '[', ''), 'null',''), '\"', '')) AS address " +
            "      FROM patient_person p,\n" +
            "jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result\n" +
            "inner join biometric b on b.person_uuid = result.uuid  \n" +
            "where result.facility_id = ?1 and result.archived = 0 and  \n" +
            "b.enrollment_date between ?2 and ?3 GROUP by result.surname, b.enrollment_date,\n" +
            "result.hospital_number, result.id, result.date_of_birth, result.age, result.name, result.sex,\n" +
            "result.facility_id, result.phone, result.address;", nativeQuery = true
    )
    List<BiometricReport> getBiometricReports(Long facilityId, LocalDate startDate, LocalDate endDate);

    @Query(value = "SELECT " +
            "h.id as patientId, " +
            "h.date_of_observation AS dateOfObservation, " +
            "u.name AS facilityName, " +
            "obj.value->>'comment' AS comment, " +
            "obj.value->>'outcome' AS outcome, " +
            "obj.value->>'dateOfAttempt' AS dateOfAttempt, " +
            "obj.value->>'verificationStatus' AS verificationStatus, " +
            "obj.value->>'verificationAttempts' AS verificationAttempts, " +
            "obj.value->>'serialEnrollmentNo' AS serialEnrollmentNo, " +
            "h.data->>'referedTo' AS referredTo, " +
            "h.data->>'discontinuation' AS discontinuation, " +
            "h.data->>'returnedToCare' AS dateReturnedToCare, " +
            "h.data->>'dateOfDiscontinuation' AS dateOfDiscontinuation " +
            "FROM hiv_observation h " +
            "INNER JOIN base_organisation_unit u ON h.facility_id = u.id, " +
            "jsonb_array_elements(h.data->'attempt') as obj " +
            "WHERE h.type = 'Client Verification' AND h.facility_id = ?1 AND h.archived = 0;", nativeQuery = true )
    List<ClientServiceDto>  generateClientServiceList(Long facilityId);
}

