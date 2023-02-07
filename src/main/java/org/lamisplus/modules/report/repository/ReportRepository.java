package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {


    @Query(value = "SELECT hc.client_code AS clientCode,\n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'first_name' ELSE pp.first_name END) AS firstName,\n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'surname' ELSE pp.surname END) AS surname,\n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'middile_name' ELSE pp.other_name END) AS otherName,\n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'gender') ELSE INITCAP(pp.sex) END) AS sex,\n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN CAST(hc.extra->>'age' AS INTEGER) \n" +
            " ELSE CAST(EXTRACT(YEAR from AGE(NOW(),  pp.date_of_birth)) AS INTEGER ) \n" +
            " END) AS age,\n" +
            " (CASE WHEN hc.person_uuid IS NULL THEN CAST(hc.extra->>'date_of_birth' AS DATE) \n" +
            "  ELSE pp.date_of_birth END) AS dateOfBirth,\n" +
            " (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'phone_number' \n" +
            "  ELSE pp.contact_point->'contactPoint'->0->'value'->>0 END) AS phoneNumber,\n" +
            " \n" +
            " (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'marital_status' \n" +
            "  ELSE pp.marital_status->>'display' END) AS maritalStatus,\n" +
            "   \n" +
            "   (CASE WHEN hc.person_uuid IS NULL \n" +
            "\tTHEN hc.extra->>'lga_of_residence' ELSE res_lga.name END) AS LGAOfResidence,\n" +
            "\t\n" +
            "\t(CASE WHEN hc.person_uuid IS NULL \n" +
            "\tTHEN hc.extra->>'state_of_residence' ELSE res_state.name END) AS StateOfResidence,\n" +
            "\t\n" +
            "\tfacility.name AS facility,\n" +
            "\t\n" +
            "\tpp.education->>'display' as education, \n" +
            "\tpp.employment_status->>'display' as occupation,\n" +
            "\tboui.code as datimCode,\n" +
            "\thc.others->>'latitude' AS HTSLatitude,\n" +
            "\thc.others->>'longitude' AS HTSLongitude,\t\n" +
            "\t\n" +
            "\t(CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'client_address' ELSE r.address END) AS clientAddress,\n" +
            "hc.date_visit AS dateVisit,\n" +
            "(CASE WHEN hc.first_time_visit IS true THEN 'Yes' ELSE 'No' END) firstTimeVisit,\n" +
            "hc.num_children AS numberOfChildren,\n" +
            "hc.num_wives AS numberOfWives,\n" +
            "(CASE WHEN hc.index_client IS true THEN 'Yes' ELSE 'No' END) indexClient,\n" +
            "hc.prep_offered AS prepOffered,\n" +
            "hc.prep_accepted AS prepAccepted,\n" +
            "(CASE WHEN hc.previously_tested IS true THEN 'Yes' ELSE 'No' END) AS previouslyTested, \n" +
            "tg.display AS targetGroup,\n" +
            "rf.display AS referredFrom,\n" +
            "ts.display AS testingSetting,\n" +
            "tc.display AS counselingType,\n" +
            "preg.display AS pregnancyStatus,\n" +
            "hc.breast_feeding AS breastFeeding,\n" +
            "relation.display AS indexType,\n" +
            "hc.recency->>'optOutRTRI' AS IfRecencyTestingOptIn,\n" +
            "hc.recency->>'rencencyId' AS RecencyID,\n" +
            "hc.recency->>'optOutRTRITestName' AS recencyTestType,\n" +
            "hc.recency->>'optOutRTRITestDate' AS recencyTestDate,\n" +
            "hc.recency->>'rencencyInterpretation' AS recencyInterpretation,\n" +
            "hc.recency->>'finalRecencyResult' AS finalRecencyResult,\n" +
            "hc.recency->>'viralLoadResultClassification' AS viralLoadResultClassification,\n" +
            "'' AS viralLoadConfirmationDate,\n" +
            "hc.risk_stratification_code AS Assessmentcode,\n" +
            "modality_code.display AS modality,\n" +
            "\n" +
            "(CASE WHEN hc.syphilis_testing->>'syphilisTestResult' ILIKE 'Yes' \n" +
            "THEN 'Reactive' ELSE 'Non-Reactive' END) As syphilisTestResult,\n" +
            "\n" +
            "(CASE WHEN hc.hepatitis_testing->>'hepatitisBTestResult' ILIKE 'Yes' \n" +
            " THEN 'Positive' ELSE 'Negative' END) AS hepatitisBTestResult,\n" +
            "\n" +
            "(CASE WHEN hc.hepatitis_testing->>'hepatitisCTestResult' ILIKE 'Yes' \n" +
            " THEN 'Positive' ELSE 'Negative' END) AS hepatitisCTestResult,\n" +
            "\n" +
            "hc.cd4->>'cd4Count' AS CD4Type,\n" +
            "hc.cd4->>'cd4SemiQuantitative' AS CD4TestResult,\n" +
            "(CASE WHEN hc.test1->>'result' ILIKE 'Yes' THEN 'Positive' ELSE 'Negative' END)AS hivTestResult,\n" +
            "hc.hiv_test_result AS finalHIVTestResult,\n" +
            "CAST(hc.test1->>'date' AS DATE) dateOfHIVTesting\n" +
            "FROM hts_client hc\n" +
            "LEFT JOIN base_application_codeset tg ON tg.code = hc.target_group\n" +
            "LEFT JOIN base_application_codeset rf ON rf.id = hc.referred_from\n" +
            "LEFT JOIN base_application_codeset ts ON ts.code = hc.testing_setting\n" +
            "LEFT JOIN base_application_codeset tc ON tc.id = hc.type_counseling\n" +
            "LEFT JOIN base_application_codeset preg ON preg.id = hc.pregnant\n" +
            "LEFT JOIN base_application_codeset relation ON relation.id = hc.relation_with_index_client\n" +
            "LEFT JOIN hts_risk_stratification hrs ON hrs.code = hc.risk_stratification_code\n" +
            "LEFT JOIN base_application_codeset modality_code ON modality_code.code = hrs.modality\n" +
            "LEFT JOIN patient_person pp ON pp.uuid=hc.person_uuid\n" +
            "LEFT JOIN (\n" +
            "\t\t\t\t  SELECT * FROM (SELECT p.id, REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\"', ''), ']', ''), '[', '') AS address, \n" +
            "\t\t\t\tCASE WHEN address_object->>'stateId'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId,\n" +
            "\t\t\t\tCASE WHEN address_object->>'district'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId\n" +
            "      \t\t\tFROM patient_person p,\n" +
            "jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result\n" +
            "\t\t\t\t  ) r ON r.id=pp.id\n" +
            "LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)\n" +
            "LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)\n" +
            "LEFT JOIN base_organisation_unit facility ON facility.id=hc.facility_id\n" +
            "LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=hc.facility_id\n" +
            "WHERE hc.archived=?1 AND hc.facility_id=?2 AND hc.date_visit >=?3 AND hc.date_visit < ?4", nativeQuery = true)
    List<HtsReportDto> getHtsReport(Integer archived, Long facilityId, LocalDate from, LocalDate to);
}