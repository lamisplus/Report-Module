package org.lamisplus.modules.report.repository;
import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.domain.PrepReportDto;
import org.lamisplus.modules.report.domain.RADETDTOProjection;
import org.lamisplus.modules.report.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {


@Query(value = "SELECT hc.client_code AS clientCode, " +
" (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'first_name') ELSE INITCAP(pp.first_name) END) AS firstName, " +
" (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'surname') ELSE INITCAP(pp.surname) END) AS surname, " +
" (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'middile_name') ELSE INITCAP(pp.other_name) END) AS otherName, " +
" (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'gender') ELSE INITCAP(pp.sex) END) AS sex, " +
"(CASE WHEN hc.person_uuid IS NULL THEN CAST(hc.extra->>'age' AS INTEGER)" +
" ELSE CAST(EXTRACT(YEAR from AGE(NOW(),pp.date_of_birth)) AS INTEGER ) " +
" END) AS age, " +
" (CASE WHEN hc.person_uuid IS NOT NULL THEN pp.date_of_birth " +
"WHEN hc.person_uuid IS NULL AND LENGTH(hc.extra->>'date_of_birth') > 0 THEN CAST(hc.extra->>'date_of_birth' AS DATE)" +
"ELSE NULL END) AS dateOfBirth, " +
" (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'phone_number' " +
"ELSE pp.contact_point->'contactPoint'->0->'value'->>0 END) AS phoneNumber, " +
" (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'marital_status' " +
"ELSE pp.marital_status->>'display' END) AS maritalStatus, " +
"(CASE WHEN hc.person_uuid IS NULL " +
" THEN hc.extra->>'lga_of_residence' ELSE res_lga.name END) AS LGAOfResidence, " +
" (CASE WHEN hc.person_uuid IS NULL" +
" THEN hc.extra->>'state_of_residence' ELSE res_state.name END) AS StateOfResidence, " +
" facility.name AS facility, " +
" pp.education->>'display' as education," +
" pp.employment_status->>'display' as occupation, " +
" boui.code as datimCode, " +
" hc.others->>'latitude' AS HTSLatitude, " +
" hc.others->>'longitude' AS HTSLongitude, " +
" (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'client_address' ELSE r.address END) AS clientAddress, " +
" hc.date_visit AS dateVisited, " +
" (CASE WHEN hc.first_time_visit IS true THEN 'Yes' ELSE 'No' END) firstTimeVisit, " +
" hc.num_children AS numberOfChildren, " +
"hc.num_wives AS numberOfWives, " +
"(CASE WHEN hc.index_client IS true THEN 'Yes' ELSE 'No' END) indexClient, " +
"hc.prep_offered AS prepOffered, " +
"hc.prep_accepted AS prepAccepted, " +
"(CASE WHEN hc.previously_tested IS true THEN 'Yes' ELSE 'No' END) AS previouslyTested," +
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
"(CASE WHEN hc.syphilis_testing->>'syphilisTestResult' ILIKE 'Yes'" +
"THEN 'Reactive' ELSE 'Non-Reactive' END) As syphilisTestResult, " +
"(CASE WHEN hc.hepatitis_testing->>'hepatitisBTestResult' ILIKE 'Yes'" +
" THEN 'Positive' ELSE 'Negative' END) AS hepatitisBTestResult, " +
"(CASE WHEN hc.hepatitis_testing->>'hepatitisCTestResult' ILIKE 'Yes'" +
" THEN 'Positive' ELSE 'Negative' END) AS hepatitisCTestResult, " +
"hc.cd4->>'cd4Count' AS CD4Type, " +
"hc.cd4->>'cd4SemiQuantitative' AS CD4TestResult, " +
"(CASE WHEN hc.test1->>'result' ILIKE 'Yes' THEN 'Positive' ELSE 'Negative' END)AS hivTestResult, " +
"hc.hiv_test_result AS finalHIVTestResult, " +
"(CASE WHEN LENGTH(hc.test1->>'date') > 0 THEN CAST(hc.test1->>'date' AS DATE)" +
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
"LEFT JOIN (SELECT * FROM (SELECT p.id, REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\"', ''), ']', ''), '[', '') AS address," +
"CASE WHEN address_object->>'stateId'~ '^\\d+(\\.\\d+)?$' THEN address_object->>'stateId' ELSE null ENDAS stateId, " +
"CASE WHEN address_object->>'district'~ '^\\d+(\\.\\d+)?$' THEN address_object->>'district' ELSE null ENDAS lgaId " +
" FROM patient_person p, " +
"jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result ) r ON r.id=pp.id " +
"LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT) " +
"LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT) " +
"LEFT JOIN base_organisation_unit facility ON facility.id=hc.facility_id " +
"LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=hc.facility_id " +
"WHERE hc.archived=0 AND hc.facility_id= ?1 AND hc.date_visit >=?2 AND hc.date_visit < ?3", nativeQuery = true)
List<HtsReportDto> getHtsReport(Long facilityId, LocalDate start, LocalDate end);

@Query(value = "SELECT p.id, p.uuid as person_uuid, p.uuid,p.hospital_number as hospitalNumber," +
"p.surname, p.first_name as firstName, " +
"EXTRACT(YEAR from AGE(NOW(),date_of_birth)) as age, " +
"p.other_name as otherName, p.sex as gender, p.date_of_birth as dateOfBirth," +
"p.date_of_registration as dateOfRegistration, p.marital_status->>'display' as maritalStatus," +
"education->>'display' as education, p.employment_status->>'display' as occupation," +
"facility.name as facilityName, facility_lga.name as lga, facility_state.name as state," +
"boui.code as datimId, res_state.name as residentialState, res_lga.name as residentialLga, " +
"r.address as address, p.contact_point->'contactPoint'->0->'value'->>0 AS phone, " +
"baseline_reg.regimen AS baselineRegimen, " +
"baseline_pc.systolic AS baselineSystolicBP, " +
"baseline_pc.diastolic AS baselineDiastolicBP, " +
"baseline_pc.weight AS baselinetWeight, " +
"baseline_pc.height AS baselineHeight, " +
"baseline_hiv_status.display AS HIVStatusAtPrEPInitiation, " +
"(CASE WHEN prepe.extra->>'onDemandIndication' IS NOT NULL THEN prepe.extra->>'onDemandIndication' " +
" WHEN riskt.display IS NOT NULL THEN riskt.display ELSE NULL END) AS indicationForPrEP, " +
"current_reg.regimen AS currentRegimen, " +
"current_pc.encounter_date AS DateOfLastPickup, " +
"current_pc.systolic AS currentSystolicBP, " +
"current_pc.diastolic AS currentDiastolicBP, " +
"current_pc.weight AS currentWeight, " +
"current_pc.height AS currentHeight, " +
"current_hiv_status.display AS currentHivStatus, " +
"(CASE WHEN current_pc.pregnant IS NOT NULL AND current_pc.pregnant='true' THEN 'Pregnant' " +
"ELSE 'Not Pregnant' END) AS pregnancyStatus " +
"FROM patient_person p " +
"INNER JOIN ( " +
"SELECT * FROM (SELECT p.id, REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\"', ''), ']', ''), '[', '') AS address," +
"CASE WHEN address_object->>'stateId'~ '^\\d+(\\.\\d+)?$' THEN address_object->>'stateId' ELSE null ENDAS stateId, " +
"CASE WHEN address_object->>'district'~ '^\\d+(\\.\\d+)?$' THEN address_object->>'district' ELSE null ENDAS lgaId " +
" FROM patient_person p, " +
"jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result " +
") r ON r.id=p.id " +
" INNER JOIN base_organisation_unit facility ON facility.id=facility_id " +
"INNER JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id " +
"INNER JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id " +
"LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT) " +
"LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT) " +
" INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=facility_id " +
" INNER JOIN prep_enrollment prepe ON prepe.person_uuid = p.uuid " +
" LEFT JOIN base_application_codeset riskt ON riskt.code = prepe.risk_type " +
" LEFT JOIN (SELECT pc.* FROM prep_clinic pc " +
"INNER JOIN (SELECT MAX(encounter_date)encounter_date, person_uuid FROM prep_clinic " +
"GROUP BY person_uuid)max ON max.encounter_date=pc.encounter_date" +
"AND max.person_uuid=pc.person_uuid)current_pc ON current_pc.person_uuid=p.uuid " +
"LEFT JOIN prep_regimen current_reg ON current_reg.id = current_pc.regimen_id " +
"LEFT JOIN base_application_codeset current_hiv_status ON current_hiv_status.code = current_pc.hiv_test_result " +
"LEFT JOIN (SELECT pc.* FROM prep_clinic pc " +
"INNER JOIN (SELECT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic " +
"GROUP BY person_uuid)min ON min.encounter_date=pc.encounter_date" +
"AND min.person_uuid=pc.person_uuid)baseline_pc ON baseline_pc.person_uuid=p.uuid " +
"LEFT JOIN prep_regimen baseline_reg ON baseline_reg.id = baseline_pc.regimen_id " +
"LEFT JOIN base_application_codeset baseline_hiv_status ON baseline_hiv_status.code=baseline_pc.hiv_test_result " +
" WHERE p.archived=0 AND p.facility_id= ?1 AND p.date_of_registration >= ?2 AND p.date_of_registration < ?3", nativeQuery = true)
List<PrepReportDto> getPrepReport(Long facilityId, LocalDate start, LocalDate end);

 
 @Query(value = "WITH bio_data AS (\n" +
 "SELECT\n" +
 "DISTINCT (p.uuid) AS personUuid,\n" +
 "p.hospital_number AS hospitalNumber,\n" +
 "EXTRACT(\n" +
 "YEAR\n" +
 "FROM\n" +
 "AGE(NOW(), date_of_birth)\n" +
 ") AS age,\n" +
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
 "h.ovc_number AS ovcUniqueId,\n" +
 "h.house_hold_number AS householdUniqueNo,\n" +
 "ecareEntry.display AS careEntry,\n" +
 "hrt.description AS regimenLineAtStart\n" +
 "FROM\n" +
 "patient_person p\n" +
 "INNER JOIN base_organisation_unit facility ON facility.id = facility_id\n" +
 "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
 "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
 "INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id\n" +
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
 "AND h.facility_id = ?1\n" +
 "AND hac.is_commencement = TRUE\n" +
 "AND hac.visit_date >= ?2\n" +
 "AND hac.visit_date <= ?3\n" +
 "),\n" +
 "current_clinical AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (tvs.person_uuid) tvs.person_uuid AS person_uuid10,\n" +
 "body_weight AS currentWeight,\n" +
 "tbs.display AS tbStatus,\n" +
 "bac.display AS currentClinicalStage,\n" +
 "preg.display AS pregnancyStatus,\n" +
 "CASE\n" +
 "WHEN hac.tb_screen IS NOT NULL THEN hac.visit_date\n" +
 "ELSE NULL\n" +
 "END AS dateOfTbScreened\n" +
 "FROM\n" +
 "triage_vital_sign tvs\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "person_uuid,\n" +
 "MAX(capture_date) AS MAXDATE\n" +
 "FROM\n" +
 "triage_vital_sign\n" +
 "GROUP BY\n" +
 "person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") AS current_triage ON current_triage.MAXDATE = tvs.capture_date\n" +
 "AND current_triage.person_uuid = tvs.person_uuid\n" +
 "INNER JOIN hiv_art_clinical hac ON tvs.uuid = hac.vital_sign_uuid\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "person_uuid,\n" +
 "MAX(hac.visit_date) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_art_clinical hac\n" +
 "GROUP BY\n" +
 "person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") AS current_clinical_date ON current_clinical_date.MAXDATE = hac.visit_date\n" +
 "AND current_clinical_date.person_uuid = hac.person_uuid\n" +
 "INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid\n" +
 "LEFT JOIN base_application_codeset bac ON bac.id = hac.clinical_stage_id\n" +
 "LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status\n" +
 "LEFT JOIN base_application_codeset tbs ON tbs.id = hac.tb_status\\:\\: INTEGER\n" +
 "WHERE\n" +
 "hac.archived = 0\n" +
 "AND hac.is_commencement = TRUE\n" +
 "AND he.archived = 0\n" +
 "AND he.facility_id = ?1\n" +
 "),\n" +
 "laboratory_details_viral_load AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (lo.patient_uuid) lo.patient_uuid AS person_uuid20,\n" +
 "bac_viral_load.display viralLoadIndication,\n" +
 "ls.date_sample_collected AS dateOfViralLoadSampleCollection,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN lr.result_reported = '0'\n" +
 "OR lr.result_reported = '00' THEN NULL\n" +
 "WHEN lr.result_reported ILIKE '%<%'\n" +
 "OR lr.result_reported ILIKE '%>%' THEN REPLACE(REPLACE(lr.result_reported, '<', ''), '>', '')\n" +
 "ELSE lr.result_reported\n" +
 "END\n" +
 ") AS currentViralLoad,\n" +
 "lr.date_result_reported AS dateOfCurrentViralLoad\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "lo.*,\n" +
 "ROW_NUMBER () OVER (\n" +
 "PARTITION BY lo.patient_id\n" +
 "ORDER BY\n" +
 "order_date DESC\n" +
 ")\n" +
 "FROM\n" +
 "laboratory_order lo\n" +
 ") l\n" +
 "WHERE\n" +
 "l.row_number = 1\n" +
 "AND l.archived = 0\n" +
 ") lo\n" +
 "INNER JOIN laboratory_test lt ON lt.lab_order_id = lo.id\n" +
 "AND lt.archived = 0\n" +
 "LEFT JOIN base_application_codeset bac_viral_load ON bac_viral_load.id = lt.viral_load_indication\n" +
 "INNER JOIN laboratory_labtest ll ON ll.id = lt.lab_test_id\n" +
 "AND ll.lab_test_name = 'Viral Load'\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "lr.*,\n" +
 "ROW_NUMBER () OVER (\n" +
 "PARTITION BY lr.patient_uuid\n" +
 "ORDER BY\n" +
 "date_result_received DESC\n" +
 ")\n" +
 "FROM\n" +
 "laboratory_result lr\n" +
 ") l\n" +
 "WHERE\n" +
 "l.row_number = 1\n" +
 "AND l.archived = 0\n" +
 ") lr ON lr.patient_uuid = lo.patient_uuid\n" +
 "AND lr.test_id = lt.id\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "ls.*,\n" +
 "ROW_NUMBER () OVER (\n" +
 "PARTITION BY ls.patient_uuid\n" +
 "ORDER BY\n" +
 "date_sample_collected DESC\n" +
 ")\n" +
 "FROM\n" +
 "laboratory_sample ls\n" +
 ") l\n" +
 "WHERE\n" +
 "l.row_number = 1\n" +
 "AND l.archived = 0\n" +
 ") ls ON ls.patient_uuid = lo.patient_uuid\n" +
 "AND lr.test_id = lt.id\n" +
 "WHERE\n" +
 "lo.archived = 0\n" +
 "AND lo.order_date <= ?3\n" +
 "AND lo.facility_id = ?1\n" +
 "),\n" +
 "laboratory_details_cd4 AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (lo.patient_uuid) lo.patient_uuid AS person_uuid30,\n" +
 "lr.result_reported AS lastCD4Count,\n" +
 "lr.date_result_reported AS dateOfLastCD4Count\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "l.*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "lo.*,\n" +
 "ROW_NUMBER () OVER (\n" +
 "PARTITION BY lo.patient_id\n" +
 "ORDER BY\n" +
 "order_date DESC\n" +
 ")\n" +
 "FROM\n" +
 "laboratory_order lo\n" +
 ") l\n" +
 "WHERE\n" +
 "l.row_number = 1\n" +
 "AND l.archived = 0\n" +
 ") lo\n" +
 "INNER JOIN laboratory_test lt ON lt.lab_order_id = lo.id\n" +
 "AND lt.archived = 0\n" +
 "INNER JOIN laboratory_labtest ll ON ll.id = lt.lab_test_id\n" +
 "AND ll.lab_test_name = 'CD4'\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "lr.*,\n" +
 "ROW_NUMBER () OVER (\n" +
 "PARTITION BY lr.patient_uuid\n" +
 "ORDER BY\n" +
 "date_result_received DESC\n" +
 ")\n" +
 "FROM\n" +
 "laboratory_result lr\n" +
 ") l\n" +
 "WHERE\n" +
 "l.row_number = 1\n" +
 ") lr ON lr.patient_uuid = lo.patient_uuid\n" +
 "AND lr.test_id = lt.id\n" +
 "WHERE\n" +
 "lo.archived = 0\n" +
 "AND lo.order_date <= ?3\n" +
 "AND lo.facility_id = ?1\n" +
 "),\n" +
 "pharmacy_details_regimen AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (hartp.person_uuid) hartp.person_uuid AS person_uuid40,\n" +
 "r.visit_date AS lastPickupDate,\n" +
 "hartp.next_appointment AS nextPickupDate,\n" +
 "hartp.refill_period / 30\\:\\: INTEGER AS monthsOfARVRefill,\n" +
 "r.description AS currentARTRegimen,\n" +
 "r.regimen_name AS currentRegimenLine\n" +
 "FROM\n" +
 "hiv_art_pharmacy hartp\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "DISTINCT r.*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "h.person_uuid,\n" +
 "h.visit_date,\n" +
 "pharmacy_object ->> 'regimenName'\\:\\: VARCHAR AS regimen_name,\n" +
 "hrt.description\n" +
 "FROM\n" +
 "hiv_art_pharmacy h,\n" +
 "jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object)\n" +
 "LEFT JOIN hiv_regimen hr ON hr.description = pharmacy_object ->> 'regimenName'\\:\\: VARCHAR\n" +
 "LEFT JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id\n" +
 "WHERE\n" +
 "hrt.id IN (1,2,3,4)\n" +
 "AND h.archived = 0\n" +
 "AND visit_date >= ?2\n" +
 "AND visit_date <= ?3\n" +
 ") r\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "hap.person_uuid,\n" +
 "MAX(visit_date) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_art_pharmacy hap\n" +
 "INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid\n" +
 "AND h.archived = 0\n" +
 "WHERE\n" +
 "hap.archived = 0\n" +
 "AND visit_date >= ?2\n" +
 "AND visit_date <= ?3\n" +
 "GROUP BY\n" +
 "hap.person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") MAX ON MAX.MAXDATE = r.visit_date\n" +
 "AND r.person_uuid = MAX.person_uuid\n" +
 ") r ON r.visit_date = hartp.visit_date\n" +
 "AND r.person_uuid = hartp.person_uuid\n" +
 "INNER JOIN hiv_enrollment he ON he.person_uuid = r.person_uuid\n" +
 "WHERE\n" +
 "he.archived = 0\n" +
 "AND hartp.archived = 0\n" +
 "AND hartp.facility_id = ?1\n" +
 "ORDER BY\n" +
 "hartp.person_uuid ASC\n" +
 "),\n" +
 "eac AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (he.person_uuid) he.person_uuid AS person_uuid50,\n" +
 "max_date_eac.eac_session_date AS dateOfCommencementOfEAC,\n" +
 "COUNT AS numberOfEACSessionCompleted,\n" +
 "last_eac_complete.eac_session_date AS dateOfLastEACSessionCompleted,\n" +
 "ext_date.eac_session_date AS dateOfExtendEACCompletion,\n" +
 "r.date_result_reported AS DateOfRepeatViralLoadResult,\n" +
 "r.result_reported AS repeatViralLoadResult\n" +
 "FROM\n" +
 "hiv_eac he\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "hes.*,\n" +
 "ROW_NUMBER() OVER (\n" +
 "PARTITION BY hes.person_uuid\n" +
 "ORDER BY\n" +
 "hes.eac_session_date,\n" +
 "id DESC\n" +
 ")\n" +
 "FROM\n" +
 "hiv_eac_session hes\n" +
 "WHERE\n" +
 "status = 'FIRST EAC'\n" +
 "AND archived = 0\n" +
 ") e\n" +
 "WHERE\n" +
 "e.row_number = 1\n" +
 ") AS max_date_eac ON max_date_eac.eac_id = he.uuid\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "person_uuid,\n" +
 "hes.eac_id,\n" +
 "COUNT(person_uuid) AS COUNT\n" +
 "FROM\n" +
 "hiv_eac_session hes\n" +
 "GROUP BY\n" +
 "hes.eac_id,\n" +
 "hes.person_uuid\n" +
 ") AS completed_eac ON completed_eac.person_uuid = max_date_eac.person_uuid\n" +
 "AND completed_eac.eac_id = he.uuid\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "hes.*,\n" +
 "ROW_NUMBER() OVER (\n" +
 "PARTITION BY hes.person_uuid\n" +
 "ORDER BY\n" +
 "hes.eac_session_date\n" +
 ")\n" +
 "FROM\n" +
 "hiv_eac he\n" +
 "INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid\n" +
 "WHERE\n" +
 "he.status = 'COMPLETED'\n" +
 "AND he.archived = 0\n" +
 ") e\n" +
 "WHERE\n" +
 "e.row_number = 1\n" +
 ") AS last_eac_complete ON last_eac_complete.eac_id = max_date_eac.eac_id\n" +
 "AND last_eac_complete.person_uuid = max_date_eac.person_uuid\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "hes.*,\n" +
 "ROW_NUMBER() OVER (\n" +
 "PARTITION BY hes.person_uuid\n" +
 "ORDER BY\n" +
 "hes.eac_session_date,\n" +
 "id DESC\n" +
 ")\n" +
 "FROM\n" +
 "hiv_eac_session hes\n" +
 "WHERE\n" +
 "hes.status NOT ilike 'FIRST%'\n" +
 "AND status NOT ilike 'SECOND%'\n" +
 "AND status NOT ilike 'THIRD%'\n" +
 "AND hes.archived = 0\n" +
 ") e\n" +
 "WHERE\n" +
 "e.row_number = 1\n" +
 ") AS ext_date ON ext_date.eac_id = he.uuid\n" +
 "AND ext_date.person_uuid = he.person_uuid\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "l.patient_uuid,\n" +
 "l.date_result_reported,\n" +
 "l.result_reported,\n" +
 "ROW_NUMBER() OVER (\n" +
 "PARTITION BY l.patient_uuid\n" +
 "ORDER BY\n" +
 "l.date_result_reported ASC\n" +
 ")\n" +
 "FROM\n" +
 "laboratory_result l\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "lr.patient_uuid,\n" +
 "MIN(lr.date_result_reported) AS date_result_reported\n" +
 "FROM\n" +
 "laboratory_result lr\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "*\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "hes.*,\n" +
 "ROW_NUMBER() OVER (\n" +
 "PARTITION BY hes.person_uuid\n" +
 "ORDER BY\n" +
 "hes.eac_session_date,\n" +
 "he.id DESC\n" +
 ")\n" +
 "FROM\n" +
 "hiv_eac he\n" +
 "INNER JOIN hiv_eac_session hes ON hes.eac_id = he.uuid\n" +
 "WHERE\n" +
 "he.status = 'COMPLETED'\n" +
 "AND he.archived = 0\n" +
 ") e\n" +
 "WHERE\n" +
 "e.row_number = 1\n" +
 ") AS last_eac_complete ON last_eac_complete.person_uuid = lr.patient_uuid\n" +
 "AND lr.date_result_reported > last_eac_complete.eac_session_date\n" +
 "GROUP BY\n" +
 "lr.patient_uuid\n" +
 ") r ON l.date_result_reported = r.date_result_reported\n" +
 "AND l.patient_uuid = r.patient_uuid\n" +
 ") l\n" +
 "WHERE\n" +
 "l.row_number = 1\n" +
 ") r ON r.patient_uuid = he.person_uuid\n" +
 "WHERE\n" +
 "he.archived = 0\n" +
 "),\n" +
 "biometric AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (he.person_uuid) he.person_uuid AS person_uuid60,\n" +
 "biometric_count.enrollment_date AS dateBiometricsEnrolled,\n" +
 "biometric_count.count AS numberOfFingersCaptured\n" +
 "FROM\n" +
 "hiv_enrollment he\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "b.person_uuid,\n" +
 "COUNT(b.person_uuid),\n" +
 "MAX(enrollment_date) enrollment_date\n" +
 "FROM\n" +
 "biometric b\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "GROUP BY\n" +
 "b.person_uuid\n" +
 ") biometric_count ON biometric_count.person_uuid = he.person_uuid\n" +
 "WHERE\n" +
 "he.archived = 0\n" +
 "),\n" +
 "current_ART_start AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (regiment_table.person_uuid) regiment_table.person_uuid AS person_uuid70,\n" +
 "start_or_regimen AS dateOfCurrentRegimen,\n" +
 "regiment_table.max_visit_date,\n" +
 "regiment_table.regimen\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "MIN(visit_date) start_or_regimen,\n" +
 "MAX(visit_date) max_visit_date,\n" +
 "regimen,\n" +
 "person_uuid\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "hap.id,\n" +
 "hap.person_uuid,\n" +
 "hap.visit_date,\n" +
 "hivreg.description AS regimen,\n" +
 "ROW_NUMBER() OVER(\n" +
 "ORDER BY\n" +
 "person_uuid,\n" +
 "visit_date\n" +
 ") rn1,\n" +
 "ROW_NUMBER() OVER(\n" +
 "PARTITION BY hivreg.description\n" +
 "ORDER BY\n" +
 "person_uuid,\n" +
 "visit_date\n" +
 ") rn2\n" +
 "FROM\n" +
 "public.hiv_art_pharmacy AS hap\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "MAX(hapr.id) AS id,\n" +
 "art_pharmacy_id,\n" +
 "regimens_id,\n" +
 "hr.description\n" +
 "FROM\n" +
 "public.hiv_art_pharmacy_regimens AS hapr\n" +
 "INNER JOIN hiv_regimen AS hr ON hapr.regimens_id = hr.id\n" +
 "WHERE\n" +
 "hr.regimen_type_id IN (1,2,3,4)\n" +
 "GROUP BY\n" +
 "art_pharmacy_id,\n" +
 "regimens_id,\n" +
 "hr.description\n" +
 ") AS hapr ON hap.id = hapr.art_pharmacy_id\n" +
 "INNER JOIN hiv_regimen AS hivreg ON hapr.regimens_id = hivreg.id\n" +
 "INNER JOIN hiv_regimen_type AS hivregtype ON hivreg.regimen_type_id = hivregtype.id\n" +
 "AND hivreg.regimen_type_id IN (1,2,3,4) \n" +
 "ORDER BY\n" +
 "person_uuid,\n" +
 "visit_date\n" +
 ") t\n" +
 "GROUP BY\n" +
 "person_uuid,\n" +
 "regimen,\n" +
 "rn1 - rn2\n" +
 "ORDER BY\n" +
 "MIN(visit_date)\n" +
 ") AS regiment_table\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "MAX(visit_date) AS max_visit_date,\n" +
 "person_uuid\n" +
 "FROM\n" +
 "public.hiv_art_pharmacy\n" +
 "GROUP BY\n" +
 "person_uuid\n" +
 ") AS hap ON regiment_table.person_uuid = hap.person_uuid\n" +
 "WHERE\n" +
 "regiment_table.max_visit_date = hap.max_visit_date\n" +
 "GROUP BY\n" +
 "regiment_table.person_uuid,\n" +
 "regiment_table.regimen,\n" +
 "regiment_table.max_visit_date,\n" +
 "start_or_regimen\n" +
 "),\n" +
 "ipt AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (hap.person_uuid) hap.person_uuid AS personUuid80,\n" +
 "ipt_type.regimen_name AS iptType,\n" +
 "hap.visit_date AS dateOfIptStart,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN MAX(complete.date_completed\\:\\: DATE) > NOW()\\:\\: DATE THEN NULL\n" +
 "WHEN MAX(complete.date_completed\\:\\: DATE) IS NULL\n" +
 "AND (hap.visit_date + 168)\\:\\:DATE < NOW()\\:\\: DATE THEN (hap.visit_date + 168)\\:\\: DATE\n" +
 "ELSE MAX(complete.date_completed\\:\\:DATE)\n" +
 "END\n" +
 ") AS iptCompletionDate\n" +
 "FROM\n" +
 "hiv_art_pharmacy hap\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "DISTINCT person_uuid,\n" +
 "MAX(visit_date) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_art_pharmacy\n" +
 "WHERE\n" +
 "(ipt ->> 'type' ilike '%INITIATION%')\n" +
 "AND archived = 0\n" +
 "GROUP BY\n" +
 "person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") AS max_ipt ON max_ipt.MAXDATE = hap.visit_date\n" +
 "AND max_ipt.person_uuid = hap.person_uuid\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "DISTINCT h.person_uuid,\n" +
 "h.visit_date,\n" +
 "pharmacy_object ->> 'regimenName'\\:\\: VARCHAR AS regimen_name,\n" +
 "pharmacy_object ->> 'duration'\\:\\: VARCHAR AS duration,\n" +
 "hrt.description\n" +
 "FROM\n" +
 "hiv_art_pharmacy h,\n" +
 "jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object)\n" +
 "INNER JOIN hiv_regimen hr ON hr.description = pharmacy_object ->> 'regimenName'\\:\\: VARCHAR\n" +
 "INNER JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id\n" +
 "WHERE\n" +
 "hrt.id IN (15)\n" +
 ") AS ipt_type ON ipt_type.person_uuid = max_ipt.person_uuid\n" +
 "AND ipt_type.visit_date = max_ipt.MAXDATE\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "hap.person_uuid,\n" +
 "hap.visit_date,\n" +
 "TO_DATE(hap.ipt ->> 'dateCompleted', 'YYYY-MM-DD') AS date_completed\n" +
 "FROM\n" +
 "hiv_art_pharmacy hap\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "DISTINCT person_uuid,\n" +
 "MAX(visit_date) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_art_pharmacy\n" +
 "WHERE\n" +
 "ipt ->> 'dateCompleted' IS NOT NULL\n" +
 "GROUP BY\n" +
 "person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") AS complete_ipt ON complete_ipt.MAXDATE\\:\\: DATE = hap.visit_date\n" +
 "AND complete_ipt.person_uuid = hap.person_uuid\n" +
 ") complete ON complete.person_uuid = hap.person_uuid\n" +
 "WHERE\n" +
 "hap.archived = 0\n" +
 "GROUP BY\n" +
 "hap.person_uuid,\n" +
 "ipt_type.regimen_name,\n" +
 "hap.visit_date\n" +
 "),\n" +
 "cervical_cancer AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (ho.person_uuid) ho.person_uuid AS person_uuid90,\n" +
 "ho.date_of_observation AS dateOfCervicalCancerScreening,\n" +
 "cc_type.display AS cervicalCancerScreeningType,\n" +
 "cc_method.display AS cervicalCancerScreeningMethod,\n" +
 "cc_result.display AS resultOfCervicalCancerScreening\n" +
 "FROM\n" +
 "hiv_observation ho\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "person_uuid,\n" +
 "MAX(date_of_observation) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_observation\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "GROUP BY\n" +
 "person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") AS max_cc ON max_cc.MAXDATE = ho.date_of_observation\n" +
 "AND max_cc.person_uuid = ho.person_uuid\n" +
 "INNER JOIN base_application_codeset cc_type ON cc_type.code = ho.data ->> 'screenType'\\:\\: VARCHAR\n" +
 "INNER JOIN base_application_codeset cc_method ON cc_method.code = ho.data ->> 'screenMethod'\\:\\: VARCHAR\n" +
 "INNER JOIN base_application_codeset cc_result ON cc_result.code = ho.data ->> 'screeningResult'\\:\\: VARCHAR\n" +
 "),\n" +
 "ovc AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (person_uuid) person_uuid AS personUuid100,\n" +
 "ovc_number AS ovcNumber,\n" +
 "house_hold_number AS householdNumber\n" +
 "FROM\n" +
 "hiv_enrollment\n" +
 "),\n" +
 "previous_previous AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN stat.hiv_status ILIKE '%STOP%'\n" +
 "OR stat.hiv_status ILIKE '%DEATH%'\n" +
 "OR stat.hiv_status ILIKE '%OUT%' THEN stat.hiv_status\n" +
 "ELSE pharmacy.status\n" +
 "END\n" +
 ") AS status,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN stat.hiv_status ILIKE '%STOP%'\n" +
 "OR stat.hiv_status ILIKE '%DEATH%'\n" +
 "OR stat.hiv_status ILIKE '%OUT%' THEN stat.status_date\n" +
 "ELSE pharmacy.visit_date\n" +
 "END\n" +
 ") AS status_date,\n" +
 "stat.cause_of_death\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "(\n" +
 "CASE\n" +
 "WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - INTERVAL '3 months' - INTERVAL '1 day',\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE THEN 'IIT'\n" +
 "ELSE 'ACTIVE'\n" +
 "END\n" +
 ") status,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - INTERVAL '3 months' - INTERVAL '1 day',\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE THEN hp.visit_date + hp.refill_period + INTERVAL '28 day'\n" +
 "ELSE hp.visit_date\n" +
 "END\n" +
 ") AS visit_date,\n" +
 "hp.person_uuid\n" +
 "FROM\n" +
 "hiv_art_pharmacy hp\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "hap.person_uuid,\n" +
 "MAX(visit_date) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_art_pharmacy hap\n" +
 "INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid\n" +
 "AND h.archived = 0\n" +
 "WHERE\n" +
 "hap.archived = 0\n" +
 "AND hap.visit_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - INTERVAL '3 months' - INTERVAL '1 day',\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 "GROUP BY\n" +
 "hap.person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") MAX ON MAX.MAXDATE = hp.visit_date\n" +
 "AND MAX.person_uuid = hp.person_uuid\n" +
 "WHERE\n" +
 "hp.archived = 0\n" +
 "AND hp.visit_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - INTERVAL '3 months' - INTERVAL '1 day',\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 ") pharmacy\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "hiv_status,\n" +
 "status.person_id,\n" +
 "hst.cause_of_death,\n" +
 "hst.status_date\n" +
 "FROM\n" +
 "hiv_status_tracker hst\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "person_id,\n" +
 "MAX(status_date) max_status\n" +
 "FROM\n" +
 "hiv_status_tracker\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "AND status_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - INTERVAL '3 months' - INTERVAL '1 day',\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 "GROUP BY\n" +
 "person_id\n" +
 ") status ON status.person_id = hst.person_id\n" +
 "AND status.max_status = hst.status_date\n" +
 "INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
 "WHERE\n" +
 "hst.id IN (\n" +
 "SELECT\n" +
 "MAX(id)\n" +
 "FROM\n" +
 "hiv_status_tracker\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "GROUP BY\n" +
 "person_id\n" +
 ")\n" +
 "AND hst.status_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - INTERVAL '3 months' - INTERVAL '1 day',\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 ") stat ON stat.person_id = pharmacy.person_uuid\n" +
 "),\n" +
 "previous AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN stat.hiv_status ILIKE '%STOP%'\n" +
 "OR stat.hiv_status ILIKE '%DEATH%'\n" +
 "OR stat.hiv_status ILIKE '%OUT%' THEN stat.hiv_status\n" +
 "ELSE pharmacy.status\n" +
 "END\n" +
 ") AS status,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN stat.hiv_status ILIKE '%STOP%'\n" +
 "OR stat.hiv_status ILIKE '%DEATH%'\n" +
 "OR stat.hiv_status ILIKE '%OUT%' THEN stat.status_date\n" +
 "ELSE pharmacy.visit_date\n" +
 "END\n" +
 ") AS status_date,\n" +
 "stat.cause_of_death\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "(\n" +
 "CASE\n" +
 "WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - 1,\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE THEN 'IIT'\n" +
 "ELSE 'ACTIVE'\n" +
 "END\n" +
 ") status,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - 1,\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE THEN hp.visit_date + hp.refill_period + INTERVAL '28 day'\n" +
 "ELSE hp.visit_date\n" +
 "END\n" +
 ") AS visit_date,\n" +
 "hp.person_uuid\n" +
 "FROM\n" +
 "hiv_art_pharmacy hp\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "hap.person_uuid,\n" +
 "MAX(visit_date) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_art_pharmacy hap\n" +
 "INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid\n" +
 "AND h.archived = 0\n" +
 "WHERE\n" +
 "hap.archived = 0\n" +
 "AND hap.visit_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - 1,\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 "GROUP BY\n" +
 "hap.person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") MAX ON MAX.MAXDATE = hp.visit_date\n" +
 "AND MAX.person_uuid = hp.person_uuid\n" +
 "WHERE\n" +
 "hp.archived = 0\n" +
 "AND hp.visit_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - 1,\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 ") pharmacy\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "hiv_status,\n" +
 "status.person_id,\n" +
 "hst.cause_of_death,\n" +
 "hst.status_date\n" +
 "FROM\n" +
 "hiv_status_tracker hst\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "person_id,\n" +
 "MAX(status_date) max_status\n" +
 "FROM\n" +
 "hiv_status_tracker\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "AND status_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - 1,\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 "GROUP BY\n" +
 "person_id\n" +
 ") status ON status.person_id = hst.person_id\n" +
 "AND status.max_status = hst.status_date\n" +
 "INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
 "WHERE\n" +
 "hst.id IN (\n" +
 "SELECT\n" +
 "MAX(id)\n" +
 "FROM\n" +
 "hiv_status_tracker\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "GROUP BY\n" +
 "person_id\n" +
 ")\n" +
 "AND hst.status_date <= (\n" +
 "SELECT\n" +
 "to_char(\n" +
 "date_trunc('quarter', DATE ?3)\\:\\: DATE - 1,\n" +
 "'yyyy-mm-dd'\n" +
 ")\n" +
 ")\\:\\: DATE\n" +
 ") stat ON stat.person_id = pharmacy.person_uuid\n" +
 "),\n" +
 "current_status AS (\n" +
 "SELECT\n" +
 "DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN stat.hiv_status ILIKE '%STOP%'\n" +
 "OR stat.hiv_status ILIKE '%DEATH%'\n" +
 "OR stat.hiv_status ILIKE '%OUT%' THEN stat.hiv_status\n" +
 "ELSE pharmacy.status\n" +
 "END\n" +
 ") AS status,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN stat.hiv_status ILIKE '%STOP%'\n" +
 "OR stat.hiv_status ILIKE '%DEATH%'\n" +
 "OR stat.hiv_status ILIKE '%OUT%' THEN stat.status_date\n" +
 "ELSE pharmacy.visit_date\n" +
 "END\n" +
 ") AS status_date,\n" +
 "stat.cause_of_death\n" +
 "FROM\n" +
 "(\n" +
 "SELECT\n" +
 "(\n" +
 "CASE\n" +
 "WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < ?3\\:\\: DATE THEN 'IIT'\n" +
 "ELSE 'ACTIVE'\n" +
 "END\n" +
 ") status,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN hp.visit_date + hp.refill_period + INTERVAL '28 day' < ?3\\:\\: DATE THEN hp.visit_date + hp.refill_period + INTERVAL '28 day'\n" +
 "ELSE hp.visit_date\n" +
 "END\n" +
 ") AS visit_date,\n" +
 "hp.person_uuid\n" +
 "FROM\n" +
 "hiv_art_pharmacy hp\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "hap.person_uuid,\n" +
 "MAX(visit_date) AS MAXDATE\n" +
 "FROM\n" +
 "hiv_art_pharmacy hap\n" +
 "INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid\n" +
 "AND h.archived = 0\n" +
 "WHERE\n" +
 "hap.archived = 0\n" +
 "AND hap.visit_date <= ?3\\:\\: DATE\n" +
 "GROUP BY\n" +
 "hap.person_uuid\n" +
 "ORDER BY\n" +
 "MAXDATE ASC\n" +
 ") MAX ON MAX.MAXDATE = hp.visit_date\n" +
 "AND MAX.person_uuid = hp.person_uuid\n" +
 "WHERE\n" +
 "hp.archived = 0\n" +
 "AND hp.visit_date <= ?3\\:\\: DATE\n" +
 ") pharmacy\n" +
 "LEFT JOIN (\n" +
 "SELECT\n" +
 "hiv_status,\n" +
 "status.person_id,\n" +
 "hst.cause_of_death,\n" +
 "hst.status_date\n" +
 "FROM\n" +
 "hiv_status_tracker hst\n" +
 "INNER JOIN (\n" +
 "SELECT\n" +
 "person_id,\n" +
 "MAX(status_date) max_status\n" +
 "FROM\n" +
 "hiv_status_tracker\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "AND status_date <= ?3\\:\\: DATE\n" +
 "GROUP BY\n" +
 "person_id\n" +
 ") status ON status.person_id = hst.person_id\n" +
 "AND status.max_status = hst.status_date\n" +
 "INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
 "WHERE\n" +
 "hst.id IN (\n" +
 "SELECT\n" +
 "MAX(id)\n" +
 "FROM\n" +
 "hiv_status_tracker\n" +
 "WHERE\n" +
 "archived = 0\n" +
 "GROUP BY\n" +
 "person_id\n" +
 ")\n" +
 "AND hst.status_date <= ?3\\:\\: DATE\n" +
 ") stat ON stat.person_id = pharmacy.person_uuid\n" +
 ")\n" +
 "SELECT\n" +
 "DISTINCT ON (bd.personUuid) personUuid,\n" +
 "bd.*,\n" +
 "ldvl.*,\n" +
 "ldc.*,\n" +
 "pdr.*,\n" +
 "b.*,\n" +
 "c.*,\n" +
 "e.*,\n" +
 "ca.dateOfCurrentRegimen,\n" +
 "ca.person_uuid70,\n" +
 "ipt.dateOfIptStart,\n" +
 "ipt.iptCompletionDate,\n" +
 "ipt.iptType,\n" +
 "cc.*,\n" +
 "ov.*,\n" +
 "ct.cause_of_death AS causeOfDeath,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN prepre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
 "WHEN prepre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
 "WHEN pre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
 "WHEN pre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
 "WHEN (\n" +
 "prepre.status ILIKE '%IIT%'\n" +
 "OR prepre.status ILIKE '%STOP%'\n" +
 ")\n" +
 "AND (pre.status ILIKE '%ACTIVE%') THEN 'ACTIVE RESTART'\n" +
 "WHEN prepre.status ILIKE '%ACTIVE%'\n" +
 "AND pre.status ILIKE '%ACTIVE%' THEN 'ACTIVE'\n" +
 "ELSE REPLACE(pre.status, '_', ' ')\n" +
 "END\n" +
 ") AS previousStatus,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
 "WHEN prepre.status ILIKE '%OUT%' THEN prepre.status_date\n" +
 "WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
 "WHEN pre.status ILIKE '%OUT%' THEN pre.status_date\n" +
 "WHEN (\n" +
 "prepre.status ILIKE '%IIT%'\n" +
 "OR prepre.status ILIKE '%STOP%'\n" +
 ")\n" +
 "AND (pre.status ILIKE '%ACTIVE%') THEN pre.status_date\n" +
 "WHEN prepre.status ILIKE '%ACTIVE%'\n" +
 "AND pre.status ILIKE '%ACTIVE%' THEN pre.status_date\n" +
 " ELSE pre.status_date\n" +
 "END\n" +
 ") AS previousStatusDate,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN prepre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
 " WHEN prepre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
 "WHEN pre.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
 "WHEN pre.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
 "WHEN ct.status ILIKE '%IIT%' THEN 'IIT'\n" +
 "WHEN ct.status ILIKE '%OUT%' THEN 'TRANSFER OUT'\n" +
 "WHEN ct.status ILIKE '%DEATH%' THEN 'DEATH'\n" +
 "WHEN (\n" +
 "pre.status ILIKE '%IIT%'\n" +
 "OR pre.status ILIKE '%STOP%'\n" +
 ")\n" +
 "AND (ct.status ILIKE '%ACTIVE%') THEN 'ACTIVE RESTART'\n" +
 "WHEN pre.status ILIKE '%ACTIVE%'\n" +
 "AND ct.status ILIKE '%ACTIVE%' THEN 'ACTIVE'\n" +
 "ELSE REPLACE(ct.status, '_', ' ')\n" +
 "END\n" +
 ") AS currentStatus,\n" +
 "(\n" +
 "CASE\n" +
 "WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
 "WHEN prepre.status ILIKE '%OUT%' THEN prepre.status_date\n" +
 "WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
 "WHEN pre.status ILIKE '%OUT%' THEN pre.status_date\n" +
 "WHEN ct.status ILIKE '%IIT%' THEN ct.status_date\n" +
 "WHEN (\n" +
 "pre.status ILIKE '%IIT%'\n" +
 "OR pre.status ILIKE '%STOP%'\n" +
 ")\n" +
 "AND (ct.status ILIKE '%ACTIVE%') THEN ct.status_date\n" +
 "WHEN pre.status ILIKE '%ACTIVE%'\n" +
 "AND ct.status ILIKE '%ACTIVE%' THEN ct.status_date\n" +
 "ELSE ct.status_date\n" +
 "END\n" +
 ") AS currentStatusDate\n" +
 "FROM\n" +
 "bio_data bd\n" +
 "LEFT JOIN pharmacy_details_regimen pdr ON pdr.person_uuid40 = bd.personUuid\n" +
 "LEFT JOIN current_clinical c ON c.person_uuid10 = bd.personUuid\n" +
 "LEFT JOIN laboratory_details_viral_load ldvl ON ldvl.person_uuid20 = bd.personUuid\n" +
 "LEFT JOIN laboratory_details_cd4 ldc ON ldc.person_uuid30 = bd.personUuid\n" +
 "LEFT JOIN eac e ON e.person_uuid50 = bd.personUuid\n" +
 "LEFT JOIN biometric b ON b.person_uuid60 = bd.personUuid\n" +
 "LEFT JOIN current_ART_start ca ON ca.person_uuid70 = bd.personUuid\n" +
 "LEFT JOIN ipt ipt ON ipt.personUuid80 = bd.personUuid\n" +
 "LEFT JOIN cervical_cancer cc ON cc.person_uuid90 = bd.personUuid\n" +
 "LEFT JOIN ovc ov ON ov.personUuid100 = bd.personUuid\n" +
 "LEFT JOIN current_status ct ON ct.person_uuid = bd.personUuid\n" +
 "LEFT JOIN previous pre ON pre.person_uuid = ct.person_uuid\n" +
 "LEFT JOIN previous_previous prepre ON prepre.person_uuid = ct.person_uuid",
 nativeQuery = true)
 List<RADETDTOProjection> getRadetData(Long facilityId, LocalDate start, LocalDate end);
}