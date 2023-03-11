package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.domain.PrepReportDto;
import org.lamisplus.modules.report.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {


    @Query(value = "SELECT hc.client_code AS clientCode, " +
            "   (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'first_name') ELSE INITCAP(pp.first_name) END) AS firstName, " +
            "   (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'surname') ELSE INITCAP(pp.surname) END) AS surname, " +
            "   (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'middile_name') ELSE INITCAP(pp.other_name) END) AS otherName, " +
            "   (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'gender') ELSE INITCAP(pp.sex) END) AS sex, " +
 "(CASE WHEN hc.person_uuid IS NULL THEN CAST(hc.extra->>'age' AS INTEGER)  " +
            "   ELSE CAST(EXTRACT(YEAR from AGE(NOW(),  pp.date_of_birth)) AS INTEGER ) " +
            "   END) AS age, " +
            "   (CASE WHEN hc.person_uuid IS NOT NULL THEN pp.date_of_birth " +
            "  WHEN hc.person_uuid IS NULL AND LENGTH(hc.extra->>'date_of_birth') > 0 THEN CAST(hc.extra->>'date_of_birth' AS DATE)  " +
            "  ELSE NULL END) AS dateOfBirth, " +
            " (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'phone_number' " +
            "  ELSE pp.contact_point->'contactPoint'->0->'value'->>0 END) AS phoneNumber, " +
            " (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'marital_status' " +
            "  ELSE pp.marital_status->>'display' END) AS maritalStatus, " +
            "  (CASE WHEN hc.person_uuid IS NULL " +
            "   THEN hc.extra->>'lga_of_residence' ELSE res_lga.name END) AS LGAOfResidence, " +
            "   (CASE WHEN hc.person_uuid IS NULL  " +
            " THEN hc.extra->>'state_of_residence' ELSE res_state.name END) AS StateOfResidence, " +
            " facility.name AS facility, " +
            "   pp.education->>'display' as education,  " +
            "   pp.employment_status->>'display' as occupation, " +
            "   boui.code as datimCode, " +
            "   hc.others->>'latitude' AS HTSLatitude, " +
            "   hc.others->>'longitude' AS HTSLongitude,   " +
            "   (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'client_address' ELSE r.address END) AS clientAddress, " +
            "   hc.date_visit AS dateVisited, " +
            "   (CASE WHEN hc.first_time_visit IS true THEN 'Yes' ELSE 'No' END) firstTimeVisit, " +
            "   hc.num_children AS numberOfChildren, " +
            "hc.num_wives AS numberOfWives, " +
            "(CASE WHEN hc.index_client IS true THEN 'Yes' ELSE 'No' END) indexClient, " +
            "hc.prep_offered AS prepOffered, " +
            "hc.prep_accepted AS prepAccepted, " +
            "(CASE WHEN hc.previously_tested IS true THEN 'Yes' ELSE 'No' END) AS previouslyTested,  " +
            "tg.display AS targetGroup, " +
            "rf.display AS referredFrom, " +
            "ts.display AS testingSetting, " +
            "tc.display AS counselingType, " +
            "preg.display AS pregnacyStatus, " +
            "hc.breast_feeding AS breastFeeding, " +
            "relation.display AS indexType, " +
            "hc.recency->>'optOutRTRI' AS IfRecencyTestingOptIn, " +
            "hc.recency->>'rencencyId' AS RecencyID, " +
            "hc.recency->>'optOutRTRITestName' AS recencyTestType, " +
            "hc.recency->>'optOutRTRITestDate' AS recencyTestDate, " +
            "hc.recency->>'rencencyInterpretation' AS recencyInterpretation, " +
            "hc.recency->>'finalRecencyResult' AS finalRecencyResult, " +
            "hc.recency->>'viralLoadResultClassification' AS viralLoadResultClassification, " +
            "'' AS viralLoadConfirmationDate, " +
            "hc.risk_stratification_code AS Assessmentcode, " +
            "modality_code.display AS modality, " +
            "(CASE WHEN hc.syphilis_testing->>'syphilisTestResult' ILIKE 'Yes'  " +
            "THEN 'Reactive' ELSE 'Non-Reactive' END) As syphilisTestResult, " +
            "(CASE WHEN hc.hepatitis_testing->>'hepatitisBTestResult' ILIKE 'Yes'  " +
            " THEN 'Positive' ELSE 'Negative' END) AS hepatitisBTestResult, " +
            "(CASE WHEN hc.hepatitis_testing->>'hepatitisCTestResult' ILIKE 'Yes'  " +
            " THEN 'Positive' ELSE 'Negative' END) AS hepatitisCTestResult, " +
            "hc.cd4->>'cd4Count' AS CD4Type, " +
            "hc.cd4->>'cd4SemiQuantitative' AS CD4TestResult, " +
            "(CASE WHEN hc.test1->>'result' ILIKE 'Yes' THEN 'Positive' ELSE 'Negative' END)AS hivTestResult, " +
            "hc.hiv_test_result AS finalHIVTestResult, " +
            "(CASE WHEN LENGTH(hc.test1->>'date') > 0 THEN CAST(hc.test1->>'date' AS DATE)  " +
            "ELSE NULL END)dateOfHIVTesting " +
            "FROM hts_client hc " +
            "LEFT JOIN base_application_codeset tg ON tg.code = hc.target_group " +
            "LEFT JOIN base_application_codeset rf ON rf.id = hc.referred_from " +
            "LEFT JOIN base_application_codeset ts ON ts.code = hc.testing_setting " +
            "LEFT JOIN base_application_codeset tc ON tc.id = hc.type_counseling " +
            "LEFT JOIN base_application_codeset preg ON preg.id = hc.pregnant " +
            "LEFT JOIN base_application_codeset relation ON relation.id = hc.relation_with_index_client " +
            "LEFT JOIN hts_risk_stratification hrs ON hrs.code = hc.risk_stratification_code " +
            "LEFT JOIN base_application_codeset modality_code ON modality_code.code = hrs.modality " +
            "LEFT JOIN patient_person pp ON pp.uuid=hc.person_uuid " +
            "LEFT JOIN (SELECT * FROM (SELECT p.id, REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\"', ''), ']', ''), '[', '') AS address,  " +
            "  CASE WHEN address_object->>'stateId'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId, " +
            "  CASE WHEN address_object->>'district'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId " +
            "   FROM patient_person p, " +
            "jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result ) r ON r.id=pp.id " +
            "LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT) " +
            "LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT) " +
            "LEFT JOIN base_organisation_unit facility ON facility.id=hc.facility_id " +
            "LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=hc.facility_id " +
            "WHERE hc.archived=0 AND hc.facility_id=?1 AND hc.date_visit >=?2 AND hc.date_visit < ?3", nativeQuery = true)
    List<HtsReportDto> getHtsReport(Long facilityId, LocalDate start, LocalDate end);

    @Query(value = "SELECT DISTINCT ON (p.uuid)p.uuid AS person_uuid, p.id, p.uuid,p.hospital_number as hospitalNumber,    " +
            " p.surname, p.first_name as firstName,  " +
            " EXTRACT(YEAR from AGE(NOW(),  date_of_birth)) as age,   " +
            " p.other_name as otherName, p.sex as gender, p.date_of_birth as dateOfBirth,    " +
            " p.date_of_registration as dateOfRegistration, p.marital_status->>'display' as maritalStatus,    " +
            " education->>'display' as education, p.employment_status->>'display' as occupation,    " +
            " facility.name as facilityName, facility_lga.name as lga, facility_state.name as state,    " +
            " boui.code as datimId, res_state.name as residentialState, res_lga.name as residentialLga,   " +
            " r.address as address, p.contact_point->'contactPoint'->0->'value'->>0 AS phone,   " +
            " baseline_reg.regimen AS baselineRegimen,   " +
            " baseline_pc.systolic AS baselineSystolicBP,   " +
            " baseline_pc.diastolic AS baselineDiastolicBP,   " +
            " baseline_pc.weight AS baselinetWeight,   " +
            " baseline_pc.height AS baselineHeight,   " +
            "  (CASE WHEN baseline_hiv_status.display IS NULL AND base_eli_test.base_eli_hiv_result IS NOT NULL " +
            " THEN base_eli_test.base_eli_hiv_result ELSE " +
            " REPLACE(baseline_hiv_status.display, 'HIV ', '') END) AS HIVStatusAtPrEPInitiation," +
            " (CASE WHEN prepe.extra->>'onDemandIndication' IS NOT NULL THEN prepe.extra->>'onDemandIndication'   " +
            "             WHEN riskt.display IS NOT NULL THEN riskt.display ELSE NULL END) AS indicationForPrEP,   " +
            " current_reg.regimen AS currentRegimen,   " +
            " current_pc.encounter_date AS DateOfLastPickup,   " +
            " current_pc.systolic AS currentSystolicBP,   " +
            " current_pc.diastolic AS currentDiastolicBP,   " +
            " current_pc.weight AS currentWeight,   " +
            " current_pc.height AS currentHeight,   " +
            "(CASE WHEN current_hiv_status.display IS NULL AND eli_hiv_result IS NOT NULL THEN eli_hiv_result  " +
            "ELSE REPLACE(current_hiv_status.display, 'HIV ', '') END) AS currentHivStatus,   " +
            " (CASE WHEN current_pc.pregnant IS NOT NULL AND current_pc.pregnant='true' THEN 'Pregnant'   " +
            " ELSE 'Not Pregnant' END) AS pregnancyStatus   " +
            " FROM patient_person p   " +
            " INNER JOIN (   " +
            " SELECT * FROM (SELECT p.id, REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\"', ''), ']', ''), '[', '') AS address,    " +
            "              CASE WHEN address_object->>'stateId'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId,   " +
            "              CASE WHEN address_object->>'district'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId   " +
            "      FROM patient_person p,   " +
            "            jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result   " +
            " ) r ON r.id=p.id   " +
            "LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS eli_hiv_result, max.visit_date, max.person_uuid FROM prep_eligibility pe " +
            " INNER JOIN (SELECT DISTINCT MAX(visit_date)visit_date, person_uuid FROM prep_eligibility " +
            " GROUP BY person_uuid)max ON max.visit_date=pe.visit_date  " +
            " AND max.person_uuid=pe.person_uuid)eli_test ON eli_test.person_uuid=p.uuid " +
            "LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS base_eli_hiv_result, min.visit_date, min.person_uuid " +
            " FROM prep_eligibility pe " +
            " INNER JOIN (SELECT DISTINCT MIN(visit_date)visit_date, person_uuid FROM prep_eligibility " +
            " GROUP BY person_uuid)min ON min.visit_date=pe.visit_date " +
            " AND min.person_uuid=pe.person_uuid)base_eli_test ON base_eli_test.person_uuid=p.uuid" +
            " INNER JOIN base_organisation_unit facility ON facility.id=facility_id   " +
            " INNER JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id   " +
            " INNER JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id   " +
            " LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)   " +
            " LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)   " +
            " INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=facility_id   " +
            " INNER JOIN prep_enrollment prepe ON prepe.person_uuid = p.uuid   " +
            " LEFT JOIN base_application_codeset riskt ON riskt.code = prepe.risk_type   " +
            " LEFT JOIN (SELECT DISTINCT pc.* FROM prep_clinic pc   " +
            "   INNER JOIN (SELECT DISTINCT MAX(encounter_date)encounter_date, person_uuid FROM prep_clinic   " +
            "   GROUP BY person_uuid)max ON max.encounter_date=pc.encounter_date    " +
            "   AND max.person_uuid=pc.person_uuid)current_pc ON current_pc.person_uuid=p.uuid   " +
            "   LEFT JOIN prep_regimen current_reg ON current_reg.id = current_pc.regimen_id   " +
            "   LEFT JOIN base_application_codeset current_hiv_status ON current_hiv_status.code = current_pc.hiv_test_result   " +
            "   INNER JOIN (SELECT pc.* FROM prep_clinic pc   " +
            "   INNER JOIN (SELECT DISTINCT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic   " +
            "   GROUP BY person_uuid)min ON min.encounter_date=pc.encounter_date    " +
            "   AND min.person_uuid=pc.person_uuid)baseline_pc ON baseline_pc.person_uuid=p.uuid   " +
            "   LEFT JOIN prep_regimen baseline_reg ON baseline_reg.id = baseline_pc.regimen_id   " +
            "   LEFT JOIN base_application_codeset baseline_hiv_status ON baseline_hiv_status.code=baseline_pc.hiv_test_result   " +
            " WHERE p.archived=0 AND p.facility_id=?1 AND p.date_of_registration >=?2 AND p.date_of_registration < ?3", nativeQuery = true)
    List<PrepReportDto> getPrepReport(Long facilityId, LocalDate start, LocalDate end);
}