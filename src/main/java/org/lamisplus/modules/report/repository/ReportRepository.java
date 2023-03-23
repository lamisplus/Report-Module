package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.domain.PrepReportDto;
import org.lamisplus.modules.report.domain.RADETDTOProjection;
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
            " WHEN riskt.display IS NOT NULL THEN riskt.display ELSE NULL END) AS indicationForPrEP,   " +
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
            "  CASE WHEN address_object->>'stateId'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId,   " +
            "  CASE WHEN address_object->>'district'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId   " +
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




//            nativeQuery = true)
    @Query(value = "WITH bio_data AS (\n" +
            "    SELECT\n" +
            "        DISTINCT (p.uuid) AS personUuid,\n" +
            "                 p.hospital_number AS hospitalNumber,\n" +
            "                 EXTRACT(\n" +
            "                         YEAR\n" +
            "                         FROM\n" +
            "                         AGE(NOW(), date_of_birth)\n" +
            "                     ) AS age,\n" +
            "                 INITCAP(p.sex) AS gender,\n" +
            "                 p.date_of_birth AS dateOfBirth,\n" +
            "                 facility.name AS facilityName,\n" +
            "                 facility_lga.name AS lga,\n" +
            "                 facility_state.name AS state,\n" +
            "                 boui.code AS datimId,\n" +
            "                 tgroup.display AS targetGroup,\n" +
            "                 eSetting.display AS enrollmentSetting,\n" +
            "                 hac.visit_date AS artStartDate,\n" +
            "                 hr.description AS regimenAtStart,\n" +
            "                 h.ovc_number AS ovcUniqueId,\n" +
            "                 h.house_hold_number AS householdUniqueNo,\n" +
            "                 ecareEntry.display AS careEntry,\n" +
            "                 hrt.description AS regimenLineAtStart\n" +
            "    FROM\n" +
            "        patient_person p\n" +
            "            INNER JOIN base_organisation_unit facility ON facility.id = facility_id\n" +
            "            INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "            INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "            INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id\n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            "            LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id\n" +
            "            LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id\n" +
            "            LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id\n" +
            "            INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid\n" +
            "            AND hac.archived = 0\n" +
            "            INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id\n" +
            "            INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id\n" +
            "    WHERE\n" +
            "            h.archived = 0\n" +
            "      AND h.facility_id = ?1\n" +
            "      AND hac.is_commencement = TRUE\n" +
            "      AND hac.visit_date >= ?2\n" +
            "      AND hac.visit_date < ?3\n" +
            "),\n" +
            "     current_clinical AS (\n" +
            "         SELECT\n" +
            "             DISTINCT ON (tvs.person_uuid) tvs.person_uuid AS person_uuid10,\n" +
            "    body_weight AS currentWeight,\n" +
            "    tbs.display AS tbStatus,\n" +
            "    bac.display AS currentClinicalStage,\n" +
            "    preg.display AS pregnancyStatus,\n" +
            "    CASE\n" +
            "    WHEN hac.tb_screen IS NOT NULL THEN hac.visit_date\n" +
            "    ELSE NULL\n" +
            "END AS dateOfTbScreened\n" +
            "            FROM\n" +
            "            triage_vital_sign tvs\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            person_uuid,\n" +
            "            MAX(capture_date) AS MAXDATE\n" +
            "            FROM\n" +
            "            triage_vital_sign\n" +
            "            GROUP BY\n" +
            "            person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) AS current_triage ON current_triage.MAXDATE = tvs.capture_date\n" +
            "            AND current_triage.person_uuid = tvs.person_uuid\n" +
            "            INNER JOIN hiv_art_clinical hac ON tvs.uuid = hac.vital_sign_uuid\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            person_uuid,\n" +
            "            MAX(hac.visit_date) AS MAXDATE\n" +
            "            FROM\n" +
            "            hiv_art_clinical hac\n" +
            "            GROUP BY\n" +
            "            person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) AS current_clinical_date ON current_clinical_date.MAXDATE = hac.visit_date\n" +
            "            AND current_clinical_date.person_uuid = hac.person_uuid\n" +
            "            INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid\n" +
            "            LEFT JOIN base_application_codeset bac ON bac.id = hac.clinical_stage_id\n" +
            "            LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status\n" +
            "            LEFT JOIN base_application_codeset tbs ON tbs.id = CAST(hac.tb_status AS INTEGER)\n" +
            "            WHERE\n" +
            "            hac.archived = 0\n" +
            "            AND he.archived = 0\n" +
            "            AND he.facility_id = ?1\n" +
            "            ),\n" +
            "\n" +
            "             sample_collection_date AS (SELECT sample.date_sample_collected as DateOfViralLoadSampleCollection, patient_uuid as person_uuid120  FROM (\n" +
            "             SELECT lt.viral_load_indication, sm.facility_id,sm.date_sample_collected, sm.patient_uuid, sm.archived, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_sample_collected DESC) as rnkk\n" +
            "             FROM public.laboratory_sample  sm\n" +
            "             INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id\n" +
            "\t\t\t\t WHERE lt.lab_test_id=16\n" +
            "             )as sample\n" +
            "            WHERE sample.rnkk = 1\n" +
            "            AND sample.archived = 0\n" +
            "            AND date_sample_collected <= ?3\n" +
            "            AND sample.facility_id = ?1 ),\n" +
            "\n" +
            "            current_vl_result AS (SELECT * FROM (\n" +
            "             SELECT  sm.patient_uuid as person_uuid130 , sm.facility_id, sm.archived, acode.display as viralLoadIndication, sm.result_reported as currentViralLoad,sm.date_result_reported as dateOfCurrentViralLoad,\n" +
            "             ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) as rnk2\n" +
            "             FROM public.laboratory_result  sm\n" +
            "             INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "             INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication\n" +
            "             WHERE lt.lab_test_id = 16\n" +
            "             AND sm. date_result_reported IS NOT NULL\n" +
            "             )as vl_result\n" +
            "            WHERE vl_result.rnk2 = 1\n" +
            "            AND  vl_result.archived = 0\n" +
            "            AND  vl_result.dateOfCurrentViralLoad <= ?3\n" +
            "            AND  vl_result.facility_id = ?1\n" +
            "            ),\n" +
            "\n" +
            "\t\t\tcareCardCD4 AS (SELECT * FROM (SELECT visit_date, cd_4, person_uuid AS cccd4_person_uuid,\n" +
            "\t\t\tROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY visit_date DESC) as rnk\n" +
            "\t\t\tFROM public.hiv_art_clinical\n" +
            "\t\t\tWHERE cd_4 IS  NOT null\n" +
            "\t\t\tAND cd_4 > 0\n" +
            "\t\t\tAND visit_date <= ?3\n" +
            "\t\t\tAND facility_id = ?1) cccd\n" +
            "\t\t\tWHERE cccd.rnk = 1\n" +
            "\t\t\t),\n" +
            "\n" +
            "\t\tlabCD4 AS (SELECT* FROM (SELECT * FROM (\n" +
            "\t\t\t SELECT sm.patient_uuid AS cd4_person_uuid, sm.archived, sm.facility_id, sm.result_reported as cd4Lb,sm.date_result_reported as dateOfCD4Lb, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) as rnk\n" +
            "\t\t\t FROM public.laboratory_result  sm\n" +
            "\t\t\t INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "\t\t\t WHERE lt.lab_test_id = 1\n" +
            "\t\t\t\t AND sm. date_result_reported IS NOT NULL\n" +
            "\t\t\t )as vl_result\n" +
            "\t\tWHERE vl_result.rnk = 1\n" +
            "\t\tAND  vl_result .archived = 0\n" +
            "\t\tAND  vl_result.dateOfCD4Lb <= ?3\n" +
            "\t\t) as ll),\n" +
            "\n" +
            "            pharmacy_details_regimen AS (\n" +
            "            SELECT  * from (\n" +
            "            SELECT p.person_uuid as person_uuid40, p.visit_date as lastPickupDate,\n" +
            "            r.description as currentARTRegimen, rt.description as currentRegimenLine,\n" +
            "            p.next_appointment as nextPickupDate,\n" +
            "            CAST(p.refill_period / 30  AS INTEGER) AS monthsOfARVRefill,\n" +
            "            ROW_NUMBER() OVER (PARTITION BY p.person_uuid ORDER BY p.visit_date DESC) as rnk3\n" +
            "            from public.hiv_art_pharmacy p\n" +
            "            INNER JOIN public.hiv_art_pharmacy_regimens pr\n" +
            "            ON pr.art_pharmacy_id = p.id\n" +
            "            INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id\n" +
            "            INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id\n" +
            "            WHERE r.regimen_type_id in (1,2,3,4,14)\n" +
            "            AND  p.archived = 0\n" +
            "            AND  p.facility_id = ?1\n" +
            "            AND  p.visit_date >= ?2\n" +
            "            AND  p.visit_date  < ?3\n" +
            "            ) as pr\n" +
            "            WHERE pr.rnk3 = 1\n" +
            "            ),\n" +
            "            eac AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (he.person_uuid) he.person_uuid AS person_uuid50,\n" +
            "            max_date_eac.eac_session_date AS dateOfCommencementOfEAC,\n" +
            "            COUNT AS numberOfEACSessionCompleted,\n" +
            "            last_eac_complete.eac_session_date AS dateOfLastEACSessionCompleted,\n" +
            "            ext_date.eac_session_date AS dateOfExtendEACCompletion,\n" +
            "            r.date_result_reported AS DateOfRepeatViralLoadResult,\n" +
            "            r.result_reported AS repeatViralLoadResult\n" +
            "            FROM\n" +
            "            hiv_eac he\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            *\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            hes.*,\n" +
            "            ROW_NUMBER() OVER (\n" +
            "            PARTITION BY hes.person_uuid\n" +
            "            ORDER BY\n" +
            "            hes.eac_session_date,\n" +
            "            id DESC\n" +
            "            )\n" +
            "            FROM\n" +
            "            hiv_eac_session hes\n" +
            "            WHERE\n" +
            "            status = 'FIRST EAC'\n" +
            "            AND archived = 0\n" +
            "            ) e\n" +
            "            WHERE\n" +
            "            e.row_number = 1\n" +
            "            ) AS max_date_eac ON max_date_eac.eac_id = he.uuid\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            person_uuid,\n" +
            "            hes.eac_id,\n" +
            "            COUNT(person_uuid) AS COUNT\n" +
            "            FROM\n" +
            "            hiv_eac_session hes\n" +
            "            GROUP BY\n" +
            "            hes.eac_id,\n" +
            "            hes.person_uuid\n" +
            "            ) AS completed_eac ON completed_eac.person_uuid = max_date_eac.person_uuid\n" +
            "            AND completed_eac.eac_id = he.uuid\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            *\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            hes.*,\n" +
            "            ROW_NUMBER() OVER (\n" +
            "            PARTITION BY hes.person_uuid\n" +
            "            ORDER BY\n" +
            "            hes.eac_session_date\n" +
            "            )\n" +
            "            FROM\n" +
            "            hiv_eac he\n" +
            "            INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid\n" +
            "            WHERE\n" +
            "            he.status = 'COMPLETED'\n" +
            "            AND he.archived = 0\n" +
            "            ) e\n" +
            "            WHERE\n" +
            "            e.row_number = 1\n" +
            "            ) AS last_eac_complete ON last_eac_complete.eac_id = max_date_eac.eac_id\n" +
            "            AND last_eac_complete.person_uuid = max_date_eac.person_uuid\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            *\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            hes.*,\n" +
            "            ROW_NUMBER() OVER (\n" +
            "            PARTITION BY hes.person_uuid\n" +
            "            ORDER BY\n" +
            "            hes.eac_session_date,\n" +
            "            id DESC\n" +
            "            )\n" +
            "            FROM\n" +
            "            hiv_eac_session hes\n" +
            "            WHERE\n" +
            "            hes.status NOT ilike 'FIRST%'\n" +
            "            AND status NOT ilike 'SECOND%'\n" +
            "            AND status NOT ilike 'THIRD%'\n" +
            "            AND hes.archived = 0\n" +
            "            ) e\n" +
            "            WHERE\n" +
            "            e.row_number = 1\n" +
            "            ) AS ext_date ON ext_date.eac_id = he.uuid\n" +
            "            AND ext_date.person_uuid = he.person_uuid\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            *\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            l.patient_uuid,\n" +
            "            l.date_result_reported,\n" +
            "            l.result_reported,\n" +
            "            ROW_NUMBER() OVER (\n" +
            "            PARTITION BY l.patient_uuid\n" +
            "            ORDER BY\n" +
            "            l.date_result_reported ASC\n" +
            "            )\n" +
            "            FROM\n" +
            "            laboratory_result l\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            lr.patient_uuid,\n" +
            "            MIN(lr.date_result_reported) AS date_result_reported\n" +
            "            FROM\n" +
            "            laboratory_result lr\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            *\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            hes.*,\n" +
            "            ROW_NUMBER() OVER (\n" +
            "            PARTITION BY hes.person_uuid\n" +
            "            ORDER BY\n" +
            "            hes.eac_session_date,\n" +
            "            he.id DESC\n" +
            "            )\n" +
            "            FROM\n" +
            "            hiv_eac he\n" +
            "            INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid\n" +
            "            WHERE\n" +
            "            he.status = 'COMPLETED'\n" +
            "            AND he.archived = 0\n" +
            "            ) e\n" +
            "            WHERE\n" +
            "            e.row_number = 1\n" +
            "            ) AS last_eac_complete ON last_eac_complete.person_uuid = lr.patient_uuid\n" +
            "            AND lr.date_result_reported > last_eac_complete.eac_session_date\n" +
            "            GROUP BY\n" +
            "            lr.patient_uuid\n" +
            "            ) r ON l.date_result_reported = r.date_result_reported\n" +
            "            AND l.patient_uuid = r.patient_uuid\n" +
            "            ) l\n" +
            "            WHERE\n" +
            "            l.row_number = 1\n" +
            "            ) r ON r.patient_uuid = he.person_uuid\n" +
            "            WHERE\n" +
            "            he.archived = 0\n" +
            "            ),\n" +
            "\n" +
            "            biometric AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (he.person_uuid) he.person_uuid AS person_uuid60,\n" +
            "            biometric_count.enrollment_date AS dateBiometricsEnrolled,\n" +
            "            biometric_count.count AS numberOfFingersCaptured\n" +
            "            FROM\n" +
            "            hiv_enrollment he\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            b.person_uuid,\n" +
            "            COUNT(b.person_uuid),\n" +
            "            MAX(enrollment_date) enrollment_date\n" +
            "            FROM\n" +
            "            biometric b\n" +
            "            WHERE\n" +
            "            archived = 0\n" +
            "            GROUP BY\n" +
            "            b.person_uuid\n" +
            "            ) biometric_count ON biometric_count.person_uuid = he.person_uuid\n" +
            "            WHERE\n" +
            "            he.archived = 0\n" +
            "            ),\n" +
            "\n" +
            "            current_ART_start AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (regiment_table.person_uuid) regiment_table.person_uuid AS person_uuid70,\n" +
            "            start_or_regimen AS dateOfCurrentRegimen,\n" +
            "            regiment_table.max_visit_date,\n" +
            "            regiment_table.regimen\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            MIN(visit_date) start_or_regimen,\n" +
            "            MAX(visit_date) max_visit_date,\n" +
            "            regimen,\n" +
            "            person_uuid\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            hap.id,\n" +
            "            hap.person_uuid,\n" +
            "            hap.visit_date,\n" +
            "            hivreg.description AS regimen,\n" +
            "            ROW_NUMBER() OVER(\n" +
            "            ORDER BY\n" +
            "            person_uuid,\n" +
            "            visit_date\n" +
            "            ) rn1,\n" +
            "            ROW_NUMBER() OVER(\n" +
            "            PARTITION BY hivreg.description\n" +
            "            ORDER BY\n" +
            "            person_uuid,\n" +
            "            visit_date\n" +
            "            ) rn2\n" +
            "            FROM\n" +
            "            public.hiv_art_pharmacy AS hap\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            MAX(hapr.id) AS id,\n" +
            "            art_pharmacy_id,\n" +
            "            regimens_id,\n" +
            "            hr.description\n" +
            "            FROM\n" +
            "            public.hiv_art_pharmacy_regimens AS hapr\n" +
            "            INNER JOIN hiv_regimen AS hr ON hapr.regimens_id = hr.id\n" +
            "            WHERE\n" +
            "            hr.regimen_type_id IN (1,2,3,4,14)\n" +
            "            GROUP BY\n" +
            "            art_pharmacy_id,\n" +
            "            regimens_id,\n" +
            "            hr.description\n" +
            "            ) AS hapr ON hap.id = hapr.art_pharmacy_id\n" +
            "            INNER JOIN hiv_regimen AS hivreg ON hapr.regimens_id = hivreg.id\n" +
            "            INNER JOIN hiv_regimen_type AS hivregtype ON hivreg.regimen_type_id = hivregtype.id\n" +
            "            AND hivreg.regimen_type_id IN (1,2,3,4,14)\n" +
            "            ORDER BY\n" +
            "            person_uuid,\n" +
            "            visit_date\n" +
            "            ) t\n" +
            "            GROUP BY\n" +
            "            person_uuid,\n" +
            "            regimen,\n" +
            "            rn1 - rn2\n" +
            "            ORDER BY\n" +
            "            MIN(visit_date)\n" +
            "            ) AS regiment_table\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT MAX(visit_date) AS max_visit_date,\n" +
            "            person_uuid\n" +
            "            FROM\n" +
            "            public.hiv_art_pharmacy\n" +
            "            GROUP BY\n" +
            "            person_uuid\n" +
            "            ) AS hap ON regiment_table.person_uuid = hap.person_uuid\n" +
            "            WHERE\n" +
            "            regiment_table.max_visit_date = hap.max_visit_date\n" +
            "            GROUP BY\n" +
            "            regiment_table.person_uuid,\n" +
            "            regiment_table.regimen,\n" +
            "            regiment_table.max_visit_date,\n" +
            "            start_or_regimen\n" +
            "            ),\n" +
            "\n" +
            "            ipt AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (hap.person_uuid) hap.person_uuid AS personUuid80,\n" +
            "            ipt_type.regimen_name AS iptType,\n" +
            "            hap.visit_date AS dateOfIptStart,\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN MAX(CAST(complete.date_completed AS DATE)) > NOW() THEN NULL\n" +
            "            WHEN MAX(CAST(complete.date_completed AS DATE)) IS NULL\n" +
            "            AND CAST((hap.visit_date + 168) AS DATE) < NOW() THEN CAST((hap.visit_date + 168) AS DATE)\n" +
            "            ELSE MAX(CAST(complete.date_completed AS DATE))\n" +
            "            END\n" +
            "            ) AS iptCompletionDate\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hap\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT person_uuid,\n" +
            "            MAX(visit_date) AS MAXDATE\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy\n" +
            "            WHERE\n" +
            "            (ipt ->> 'type' ilike '%INITIATION%')\n" +
            "            AND archived = 0\n" +
            "            GROUP BY\n" +
            "            person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) AS max_ipt ON max_ipt.MAXDATE = hap.visit_date\n" +
            "            AND max_ipt.person_uuid = hap.person_uuid\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT h.person_uuid,\n" +
            "            h.visit_date,\n" +
            "            CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name,\n" +
            "            CAST(pharmacy_object ->> 'duration' AS VARCHAR) AS duration,\n" +
            "            hrt.description\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy h,\n" +
            "            jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object)\n" +
            "            INNER JOIN hiv_regimen hr ON hr.description = CAST(pharmacy_object ->> 'regimenName' AS VARCHAR)\n" +
            "            INNER JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id\n" +
            "            WHERE\n" +
            "            hrt.id IN (15)\n" +
            "            ) AS ipt_type ON ipt_type.person_uuid = max_ipt.person_uuid\n" +
            "            AND ipt_type.visit_date = max_ipt.MAXDATE\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            hap.person_uuid,\n" +
            "            hap.visit_date,\n" +
            "            TO_DATE(hap.ipt ->> 'dateCompleted', 'YYYY-MM-DD') AS date_completed\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hap\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT person_uuid,\n" +
            "            MAX(visit_date) AS MAXDATE\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy\n" +
            "            WHERE\n" +
            "            ipt ->> 'dateCompleted' IS NOT NULL\n" +
            "            GROUP BY\n" +
            "            person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) AS complete_ipt ON CAST(complete_ipt.MAXDATE AS DATE) = hap.visit_date\n" +
            "            AND complete_ipt.person_uuid = hap.person_uuid\n" +
            "            ) complete ON complete.person_uuid = hap.person_uuid\n" +
            "            WHERE\n" +
            "            hap.archived = 0\n" +
            "            GROUP BY\n" +
            "            hap.person_uuid,\n" +
            "            ipt_type.regimen_name,\n" +
            "            hap.visit_date\n" +
            "            ),\n" +
            "\n" +
            "            cervical_cancer AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (ho.person_uuid) ho.person_uuid AS person_uuid90,\n" +
            "            ho.date_of_observation AS dateOfCervicalCancerScreening,\n" +
            "            cc_type.display AS cervicalCancerScreeningType,\n" +
            "            cc_method.display AS cervicalCancerScreeningMethod,\n" +
            "            cc_result.display AS resultOfCervicalCancerScreening\n" +
            "            FROM\n" +
            "            hiv_observation ho\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT person_uuid,\n" +
            "            MAX(date_of_observation) AS MAXDATE\n" +
            "            FROM\n" +
            "            hiv_observation\n" +
            "            WHERE\n" +
            "            archived = 0\n" +
            "            GROUP BY\n" +
            "            person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) AS max_cc ON max_cc.MAXDATE = ho.date_of_observation\n" +
            "            AND max_cc.person_uuid = ho.person_uuid\n" +
            "            INNER JOIN base_application_codeset cc_type ON cc_type.code = CAST(ho.data ->> 'screenType' AS VARCHAR)\n" +
            "            INNER JOIN base_application_codeset cc_method ON cc_method.code = CAST(ho.data ->> 'screenMethod' AS VARCHAR)\n" +
            "            INNER JOIN base_application_codeset cc_result ON cc_result.code = CAST(ho.data ->> 'screeningResult' AS VARCHAR)\n" +
            "            ),\n" +
            "\n" +
            "            ovc AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (person_uuid) person_uuid AS personUuid100,\n" +
            "            ovc_number AS ovcNumber,\n" +
            "            house_hold_number AS householdNumber\n" +
            "            FROM\n" +
            "            hiv_enrollment\n" +
            "            ),\n" +
            "\n" +
            "   previous_previous AS (\n" +
            "     SELECT\n" +
            "            DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS prePrePersonUuid,\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN (stat.hiv_status ILIKE '%STOP%'\n" +
            "            OR stat.hiv_status ILIKE '%DEATH%'\n" +
            "            OR stat.hiv_status ILIKE '%OUT%')\n" +
            "\t\t\t\tAND stat.status_date > pharmacy.visit_date THEN stat.hiv_status\n" +
            "            ELSE pharmacy.status\n" +
            "            END\n" +
            "            ) AS status,\n" +
            "\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN (stat.hiv_status ILIKE '%STOP%'\n" +
            "            OR stat.hiv_status ILIKE '%DEATH%'\n" +
            "            OR stat.hiv_status ILIKE '%OUT%')\n" +
            "\t\t\t\tAND stat.status_date > pharmacy.visit_date THEN stat.status_date\n" +
            "            ELSE pharmacy.visit_date\n" +
            "            END\n" +
            "            ) AS status_date,\n" +
            "\n" +
            "            stat.cause_of_death\n" +
            "\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < ?5 THEN 'IIT'\n" +
            "            ELSE 'ACTIVE'\n" +
            "            END\n" +
            "            ) status,\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < ?5  THEN hp.visit_date + hp.refill_period + INTERVAL '28 day'\n" +
            "            ELSE hp.visit_date\n" +
            "            END\n" +
            "            ) AS visit_date,\n" +
            "            hp.person_uuid\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hp\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT hap.person_uuid,\n" +
            "            MAX(visit_date) AS MAXDATE\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hap\n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid\n" +
            "            AND h.archived = 0\n" +
            "            WHERE\n" +
            "            hap.archived = 0\n" +
            "            AND hap.visit_date <= ?5\n" +
            "            GROUP BY\n" +
            "            hap.person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid\n" +
            "            WHERE\n" +
            "            hp.archived = 0\n" +
            "            AND hp.visit_date <= ?5\n" +
            "            ) pharmacy\n" +
            "\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            hst.hiv_status,\n" +
            "            hst.person_id,\n" +
            "            hst.cause_of_death,\n" +
            "            hst.status_date\n" +
            "            FROM\n" +
            "            (\n" +
            "\t\tSELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death,\n" +
            "\t\t\thiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "\t\t\tFROM hiv_status_tracker WHERE archived=0 AND status_date <= ?5 )s\n" +
            "\t\t\tWHERE s.row_number=1\n" +
            "            ) hst\n" +
            "            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "            WHERE hst.status_date <= ?5\n" +
            "            ) stat ON stat.person_id = pharmacy.person_uuid\n" +
            "\n" +
            " ),\n" +
            "\n" +
            "\n" +
            "           previous AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS prePersonUuid,\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN (stat.hiv_status ILIKE '%STOP%'\n" +
            "            OR stat.hiv_status ILIKE '%DEATH%'\n" +
            "            OR stat.hiv_status ILIKE '%OUT%')\n" +
            "\t\t\t\tAND stat.status_date > pharmacy.visit_date THEN stat.hiv_status\n" +
            "            ELSE pharmacy.status\n" +
            "            END\n" +
            "            ) AS status,\n" +
            "\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN (stat.hiv_status ILIKE '%STOP%'\n" +
            "            OR stat.hiv_status ILIKE '%DEATH%'\n" +
            "            OR stat.hiv_status ILIKE '%OUT%')\n" +
            "\t\t\t\tAND stat.status_date > pharmacy.visit_date THEN stat.status_date\n" +
            "            ELSE pharmacy.visit_date\n" +
            "            END\n" +
            "            ) AS status_date,\n" +
            "\n" +
            "            stat.cause_of_death\n" +
            "\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < ?4 THEN 'IIT'\n" +
            "            ELSE 'ACTIVE'\n" +
            "            END\n" +
            "            ) status,\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' <  ?4 THEN hp.visit_date + hp.refill_period + INTERVAL '28 day'\n" +
            "            ELSE hp.visit_date\n" +
            "            END\n" +
            "            ) AS visit_date,\n" +
            "            hp.person_uuid\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hp\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT hap.person_uuid,\n" +
            "            MAX(visit_date) AS MAXDATE\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hap\n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid\n" +
            "            AND h.archived = 0\n" +
            "            WHERE\n" +
            "            hap.archived = 0\n" +
            "            AND hap.visit_date <=  ?4\n" +
            "            GROUP BY\n" +
            "            hap.person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid\n" +
            "            WHERE\n" +
            "            hp.archived = 0\n" +
            "            AND hp.visit_date <=  ?4\n" +
            "            ) pharmacy\n" +
            "\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            hst.hiv_status,\n" +
            "            hst.person_id,\n" +
            "            hst.cause_of_death,\n" +
            "            hst.status_date\n" +
            "            FROM\n" +
            "            (\n" +
            "\t\tSELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death,\n" +
            "\t\t\thiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "\t\t\tFROM hiv_status_tracker WHERE archived=0 AND status_date <=  ?4 )s\n" +
            "\t\t\tWHERE s.row_number=1\n" +
            "            ) hst\n" +
            "            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "            WHERE hst.status_date <=  ?4\n" +
            "            ) stat ON stat.person_id = pharmacy.person_uuid\n" +
            "            ),\n" +
            "\n" +
            "            current_status AS (\n" +
            "            SELECT\n" +
            "            DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS cuPersonUuid,\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN (stat.hiv_status ILIKE '%STOP%'\n" +
            "            OR stat.hiv_status ILIKE '%DEATH%'\n" +
            "            OR stat.hiv_status ILIKE '%OUT%')\n" +
            "\t\t\t\tAND stat.status_date > pharmacy.visit_date THEN stat.hiv_status\n" +
            "            ELSE pharmacy.status\n" +
            "            END\n" +
            "            ) AS status,\n" +
            "\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN (stat.hiv_status ILIKE '%STOP%'\n" +
            "            OR stat.hiv_status ILIKE '%DEATH%'\n" +
            "            OR stat.hiv_status ILIKE '%OUT%')\n" +
            "\t\t\t\tAND stat.status_date > pharmacy.visit_date THEN stat.status_date\n" +
            "            ELSE pharmacy.visit_date\n" +
            "            END\n" +
            "            ) AS status_date,\n" +
            "\n" +
            "            stat.cause_of_death\n" +
            "\n" +
            "            FROM\n" +
            "            (\n" +
            "            SELECT\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < ?3 THEN 'IIT'\n" +
            "            ELSE 'ACTIVE'\n" +
            "            END\n" +
            "            ) status,\n" +
            "            (\n" +
            "            CASE\n" +
            "            WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < ?3 THEN hp.visit_date + hp.refill_period + INTERVAL '28 day'\n" +
            "            ELSE hp.visit_date\n" +
            "            END\n" +
            "            ) AS visit_date,\n" +
            "            hp.person_uuid\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hp\n" +
            "            INNER JOIN (\n" +
            "            SELECT\n" +
            "            DISTINCT hap.person_uuid,\n" +
            "            MAX(visit_date) AS MAXDATE\n" +
            "            FROM\n" +
            "            hiv_art_pharmacy hap\n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid\n" +
            "            AND h.archived = 0\n" +
            "            WHERE\n" +
            "            hap.archived = 0\n" +
            "            AND hap.visit_date <= ?3\n" +
            "            GROUP BY\n" +
            "            hap.person_uuid\n" +
            "            ORDER BY\n" +
            "            MAXDATE ASC\n" +
            "            ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid\n" +
            "            WHERE\n" +
            "            hp.archived = 0\n" +
            "            AND hp.visit_date <= ?3\n" +
            "            ) pharmacy\n" +
            "\n" +
            "            LEFT JOIN (\n" +
            "            SELECT\n" +
            "            hst.hiv_status,\n" +
            "            hst.person_id,\n" +
            "            hst.cause_of_death,\n" +
            "            hst.status_date\n" +
            "            FROM\n" +
            "            (\n" +
            "\t\tSELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death,\n" +
            "\t\t\thiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "\t\t\tFROM hiv_status_tracker WHERE archived=0 AND status_date <= ?3 )s\n" +
            "\t\t\tWHERE s.row_number=1\n" +
            "            ) hst\n" +
            "            INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "            WHERE hst.status_date <= ?3\n" +
            "            ) stat ON stat.person_id = pharmacy.person_uuid\n" +
            "            ),\n" +
            "\n" +
            "\t\t\tnaive_vl_data AS (\n" +
            "\t\t\t\tSELECT pp.uuid AS nvl_person_uuid,\n" +
            "\t\t\t\tEXTRACT(YEAR FROM AGE(NOW(), pp.date_of_birth) ) as age, ph.visit_date, ph.regimen\n" +
            "\t\t\t\tFROM patient_person pp\n" +
            "\t\t\t\tINNER JOIN (\n" +
            "\t\t\t\t\tSELECT DISTINCT * FROM (SELECT pharm.*,\n" +
            "\t\t\t\t\tROW_NUMBER() OVER (PARTITION BY pharm.person_uuid ORDER BY pharm.visit_date DESC)\n" +
            "\t\t\t\t\tFROM\n" +
            "\t\t\t\t\t(SELECT DISTINCT * FROM hiv_art_pharmacy hap\n" +
            "\t\t\t\t\tINNER JOIN hiv_art_pharmacy_regimens hapr\n" +
            "\t\t\t\t\t INNER JOIN hiv_regimen hr ON hr.id=hapr.regimens_id\n" +
            "\t\t\t\t\t INNER JOIN hiv_regimen_type hrt ON hrt.id=hr.regimen_type_id\n" +
            "\t\t\t\t\t INNER JOIN hiv_regimen_resolver hrr ON hrr.regimensys=hr.description\n" +
            "\t\t\t\t\t ON hapr.art_pharmacy_id=hap.id\n" +
            "\t\t\t\t\tWHERE hap.archived=0 AND hrt.id IN (1,2,3,4,14) AND hap.facility_id = ?1 ) pharm\n" +
            "\t\t\t\t\t\t\t\t   )ph WHERE ph.row_number=1\n" +
            "\t\t\t\t)ph ON ph.person_uuid=pp.uuid\n" +
            "\t\t\t\tWHERE pp.uuid NOT IN (\n" +
            "\t\t\t\tSELECT patient_uuid FROM (\n" +
            "\t\t\t\t\tSELECT COUNT(ls.patient_uuid), ls.patient_uuid FROM laboratory_sample ls\n" +
            "\t\t\t\t\tINNER JOIN laboratory_test lt ON lt.id=ls.test_id AND lt.lab_test_id=16\n" +
            "\t\t\t\t\tWHERE ls.archived=0 AND ls.facility_id = ?1\n" +
            "\t\t\t\t\tGROUP BY ls.patient_uuid\n" +
            "\t\t\t\t)t )\n" +
            "\t\t\t\t)\n" +
            "\n" +
            "\n" +
            "SELECT DISTINCT ON (bd.personUuid) personUuid AS uniquePersonUuid,\n" +
            "    bd.*,\n" +
            "    scd.*,\n" +
            "    cvlr.*,\n" +
            "    pdr.*,\n" +
            "    b.*,\n" +
            "    c.*,\n" +
            "    e.*,\n" +
            "    ca.dateOfCurrentRegimen,\n" +
            "    ca.person_uuid70,\n" +
            "    ipt.dateOfIptStart,\n" +
            "    ipt.iptCompletionDate,\n" +
            "    ipt.iptType,\n" +
            "    cc.*,\n" +
            "    ov.*,\n" +
            "    ct.cause_of_death AS causeOfDeath,\n" +
            "    (\n" +
            "    CASE\n" +
            "    WHEN prepre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
            "    WHEN prepre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
            "    WHEN pre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
            "    WHEN pre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
            "    WHEN (\n" +
            "    prepre.status ILIKE '%IIT%'\n" +
            "    OR prepre.status ILIKE '%STOP%'\n" +
            "    )\n" +
            "    AND (pre.status ILIKE '%ACTIVE%') THEN 'ACTIVE RESTART'\n" +
            "    WHEN prepre.status ILIKE '%ACTIVE%'\n" +
            "    AND pre.status ILIKE '%ACTIVE%' THEN 'ACTIVE'\n" +
            "    ELSE REPLACE(pre.status, '_', ' ')\n" +
            "    END\n" +
            "    ) AS previousStatus,\n" +
            "    CAST((\n" +
            "    CASE\n" +
            "    WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
            "    WHEN prepre.status ILIKE '%OUT%' THEN prepre.status_date\n" +
            "    WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
            "    WHEN pre.status ILIKE '%OUT%' THEN pre.status_date\n" +
            "    WHEN (\n" +
            "    prepre.status ILIKE '%IIT%'\n" +
            "    OR prepre.status ILIKE '%STOP%'\n" +
            "    )\n" +
            "    AND (pre.status ILIKE '%ACTIVE%') THEN pre.status_date\n" +
            "    WHEN prepre.status ILIKE '%ACTIVE%'\n" +
            "    AND pre.status ILIKE '%ACTIVE%' THEN pre.status_date\n" +
            "    ELSE pre.status_date\n" +
            "    END\n" +
            "    ) AS DATE)AS previousStatusDate,\n" +
            "    (\n" +
            "    CASE\n" +
            "    WHEN prepre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
            "    WHEN prepre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
            "    WHEN pre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
            "    WHEN pre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
            "    WHEN ct.status ILIKE '%IIT%' THEN 'IIT'\n" +
            "    WHEN ct.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
            "    WHEN ct.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
            "    WHEN (\n" +
            "    pre.status ILIKE '%IIT%'\n" +
            "    OR pre.status ILIKE '%STOP%'\n" +
            "    )\n" +
            "    AND (ct.status ILIKE '%ACTIVE%') THEN 'ACTIVE RESTART'\n" +
            "    WHEN pre.status ILIKE '%ACTIVE%'\n" +
            "    AND ct.status ILIKE '%ACTIVE%' THEN 'ACTIVE'\n" +
            "    ELSE REPLACE(ct.status, '_', ' ')\n" +
            "    END\n" +
            "    ) AS currentStatus,\n" +
            "    CAST((\n" +
            "    CASE\n" +
            "    WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
            "    WHEN prepre.status ILIKE '%OUT%' THEN prepre.status_date\n" +
            "    WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
            "    WHEN pre.status ILIKE '%OUT%' THEN pre.status_date\n" +
            "    WHEN ct.status ILIKE '%IIT%' THEN ct.status_date\n" +
            "    WHEN (\n" +
            "    pre.status ILIKE '%IIT%'\n" +
            "    OR pre.status ILIKE '%STOP%'\n" +
            "    )\n" +
            "    AND (ct.status ILIKE '%ACTIVE%') THEN ct.status_date\n" +
            "    WHEN pre.status ILIKE '%ACTIVE%'\n" +
            "    AND ct.status ILIKE '%ACTIVE%' THEN ct.status_date\n" +
            "    ELSE ct.status_date\n" +
            "    END\n" +
            "    )AS DATE) AS currentStatusDate,\n" +
            "\n" +
            "    (\n" +
            "    CASE\n" +
            "    WHEN prepre.status ILIKE '%DEATH%' THEN FALSE\n" +
            "    WHEN prepre.status ILIKE '%OUT%' THEN FALSE\n" +
            "    WHEN pre.status ILIKE '%DEATH%' THEN FALSE\n" +
            "    WHEN pre.status ILIKE '%OUT%' THEN FALSE\n" +
            "    WHEN ct.status ILIKE '%IIT%' THEN FALSE\n" +
            "    WHEN ct.status ILIKE '%OUT%' THEN FALSE\n" +
            "    WHEN ct.status ILIKE '%DEATH%' THEN FALSE\n" +
            "    WHEN ct.status ILIKE '%STOP%' THEN FALSE\n" +
            "    WHEN (nvd.age >= 15\n" +
            "    AND nvd.regimen='TDF-3TC-DTG'\n" +
            "    AND nvd.visit_date + 91 < ?3) THEN TRUE\n" +
            "    WHEN (nvd.age >= 15\n" +
            "    AND nvd.regimen !='TDF-3TC-DTG'\n" +
            "    AND nvd.visit_date + 181 < ?3) THEN TRUE\n" +
            "    WHEN (nvd.age <= 15 AND nvd.visit_date + 181 < ?3) THEN TRUE\n" +
            "\n" +
            "    WHEN CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) < 1000\n" +
            "    AND scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "    AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3 THEN TRUE\n" +
            "\n" +
            "    WHEN  CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) < 1000\n" +
            "    AND scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "    AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN TRUE\n" +
            "\n" +
            "    WHEN CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) > 1000\n" +
            "    AND scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "    AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3 THEN TRUE\n" +
            "\n" +
            "    WHEN\n" +
            "    CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) > 1000\n" +
            "    AND scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "    AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 THEN TRUE\n" +
            "\n" +
            "    ELSE FALSE\n" +
            "    END\n" +
            "    ) AS vlEligibilityStatus,\n" +
            "    CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) AS test,\n" +
            "\n" +
            "    (\n" +
            "    CASE\n" +
            "    WHEN prepre.status ILIKE '%DEATH%' THEN NULL\n" +
            "    WHEN prepre.status ILIKE '%OUT%' THEN NULL\n" +
            "    WHEN pre.status ILIKE '%DEATH%' THEN NULL\n" +
            "    WHEN pre.status ILIKE '%OUT%' THEN NULL\n" +
            "    WHEN ct.status ILIKE '%IIT%' THEN NULL\n" +
            "    WHEN ct.status ILIKE '%OUT%' THEN NULL\n" +
            "    WHEN ct.status ILIKE '%DEATH%' THEN NULL\n" +
            "    WHEN ct.status ILIKE '%STOP%' THEN NULL\n" +
            "    WHEN (nvd.age >= 15\n" +
            "    AND nvd.regimen='TDF-3TC-DTG'\n" +
            "    AND nvd.visit_date + 91 < ?3)\n" +
            "    THEN CAST(nvd.visit_date + 91 AS DATE)\n" +
            "    WHEN (nvd.age >= 15\n" +
            "    AND nvd.regimen !='TDF-3TC-DTG'\n" +
            "    AND nvd.visit_date + 181 < ?3)\n" +
            "    THEN CAST(nvd.visit_date + 181 AS DATE)\n" +
            "    WHEN (nvd.age <= 15 AND nvd.visit_date + 181 < ?3)\n" +
            "    THEN CAST(nvd.visit_date + 181 AS DATE)\n" +
            "\n" +
            "    WHEN CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) < 1000\n" +
            "    AND scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "    AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3\n" +
            "    THEN CAST(cvlr.dateofcurrentviralload AS DATE) + 181\n" +
            "\n" +
            "    WHEN  CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) < 1000\n" +
            "    AND scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "    AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3\n" +
            "    THEN CAST(scd.dateofviralloadsamplecollection AS DATE) + 91\n" +
            "\n" +
            "    WHEN CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) > 1000\n" +
            "    AND scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "    AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3\n" +
            "    THEN CAST(cvlr.dateofcurrentviralload AS DATE) + 91\n" +
            "\n" +
            "    WHEN\n" +
            "    CAST(regexp_replace(cvlr.currentviralload, '[^0-9]+', '', 'g') AS INTEGER) > 1000\n" +
            "    AND scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "    AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3\n" +
            "    THEN CAST(scd.dateofviralloadsamplecollection AS DATE) + 91\n" +
            "\n" +
            "    ELSE NULL\n" +
            "    END\n" +
            "    ) AS dateOfVlEligibilityStatus,\n" +
            "    (CASE WHEN  ccd.cd_4 IS NOT NULL THEN CAST(ccd.cd_4 as VARCHAR)\n" +
            "    WHEN  cd.cd4lb IS NOT NULL THEN  cd.cd4lb\n" +
            "    ELSE NULL END) as lastCd4Count,\n" +
            "    (CASE WHEN  ccd.visit_date IS NOT NULL THEN CAST(ccd.visit_date as DATE)\n" +
            "    WHEN  cd.dateOfCd4Lb IS NOT NULL THEN  CAST(cd.dateOfCd4Lb as DATE)\n" +
            "    ELSE NULL END) as dateOfLastCd4Count\n" +
            "FROM\n" +
            "    bio_data bd\n" +
            "    LEFT JOIN pharmacy_details_regimen pdr ON pdr.person_uuid40 = bd.personUuid\n" +
            "    LEFT JOIN current_clinical c ON c.person_uuid10 = bd.personUuid\n" +
            "    LEFT JOIN sample_collection_date scd ON scd.person_uuid120 = bd.personUuid\n" +
            "    LEFT JOIN current_vl_result  cvlr ON cvlr.person_uuid130 = bd.personUuid\n" +
            "    LEFT JOIN  labCD4 cd on cd.cd4_person_uuid = bd.personUuid\n" +
            "    LEFT JOIN  careCardCD4 ccd on ccd.cccd4_person_uuid = bd.personUuid\n" +
            "    LEFT JOIN eac e ON e.person_uuid50 = bd.personUuid\n" +
            "    LEFT JOIN biometric b ON b.person_uuid60 = bd.personUuid\n" +
            "    LEFT JOIN current_ART_start ca ON ca.person_uuid70 = bd.personUuid\n" +
            "    LEFT JOIN ipt ipt ON ipt.personUuid80 = bd.personUuid\n" +
            "    LEFT JOIN cervical_cancer cc ON cc.person_uuid90 = bd.personUuid\n" +
            "    LEFT JOIN ovc ov ON ov.personUuid100 = bd.personUuid\n" +
            "    LEFT JOIN current_status ct ON ct.cuPersonUuid = bd.personUuid\n" +
            "    LEFT JOIN previous pre ON pre.prePersonUuid = ct.cuPersonUuid\n" +
            "    LEFT JOIN previous_previous prepre ON prepre.prePrePersonUuid = ct.cuPersonUuid\n" +
            "    LEFT JOIN naive_vl_data nvd ON nvd.nvl_person_uuid = bd.personUuid", nativeQuery = true)
    List<RADETDTOProjection> getRadetData(Long facilityId, LocalDate start, LocalDate end, LocalDate previous, LocalDate previousPrevious);
}

