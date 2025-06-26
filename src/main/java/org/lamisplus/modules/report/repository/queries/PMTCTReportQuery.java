package org.lamisplus.modules.report.repository.queries;

public class PMTCTReportQuery {

    public static final String PMTCT_REPORT = "WITH pmtctHts AS (\n" +
            "SELECT DISTINCT ON (p.uuid)p.uuid AS personUuid, p.id, p.hospital_number AS hospitalNumber,  p.date_of_birth AS motherDob, EXTRACT(YEAR from AGE(CAST(?3 AS DATE),  date_of_birth)) AS motherAge,  p.marital_status->>'display' AS maritalStatus, p.date_of_registration as dateOfRegistration,\n" +
            "facility_state.name AS state, facility_lga.name AS lgaName, facility.name AS facilityName\n" +
            "FROM patient_person p\n" +
            "INNER JOIN (\n" +
            "SELECT * FROM (SELECT p.id, CONCAT(CAST(address_object->>'city' AS VARCHAR), ' ', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\\\\\\\\\\\\\\\\', ''), ']', ''), '[', ''), 'null',''), '\\\\\\\\\\\\\\', '')) AS address,\n" +
            " CASE WHEN address_object->>'stateId'  ~ '^\\\\\\\\d+(\\\\\\\\.\\\\\\\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId,\n" +
            "CASE WHEN address_object->>'district'  ~ '^\\\\\\\\d+(\\\\\\\\.\\\\\\\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId\n" +
            "FROM patient_person p,\n" +
            "jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result\n" +
            ") r ON r.id=p.id\n" +
            "LEFT JOIN base_organisation_unit facility ON facility.id=p.facility_id\n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id\n" +
            "LEFT JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id\n" +
            "LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)\n" +
            "LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)\n" +
            "LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=p.facility_id AND boui.name='DATIM_ID'\n" +
            "WHERE p.archived = 0 AND sex Ilike '%Female%'),\n" +
            "hts_client AS (\n" +
            "SELECT * FROM (\n" +
            "SELECT hc.person_uuid as person_uuid_hts_client,\n" +
            "(CASE WHEN (hiv_test_result2 IS NULL OR hiv_test_result2 = '') THEN hiv_test_result ELSE hiv_test_result2 END) as hivTestResult,\n" +
            "hc.risk_stratification_code as risk_stratification_code_hts_client,\n" +
            "CASE WHEN hc.hepatitis_testing->>'hepatitisBTestResult' IN ('Yes', 'No')  THEN hc.date_visit ELSE NULL END AS hepatitisBTestDate, \n" +
            "(CASE WHEN hepatitis_testing->>'hepatitisBTestResult' = 'Yes' THEN 'Positive' WHEN hepatitis_testing->>'hepatitisBTestResult' = 'No' THEN 'Negative' ELSE hepatitis_testing->>'hepatitisBTestResult' END) AS hepatitisBTestResult,\n" +
            "CASE WHEN hc.hepatitis_testing->>'hepatitisCTestResult' IN ('Yes', 'No')  THEN hc.date_visit ELSE NULL END AS hepatitisCTestDate, \n" +
            "(CASE WHEN hepatitis_testing->>'hepatitisCTestResult' = 'Yes' THEN 'Positive' WHEN hepatitis_testing->>'hepatitisCTestResult' = 'No' THEN 'Negative' ELSE hepatitis_testing->>'hepatitisCTestResult' END) AS hepatitisCTestResult,\n" +
            "recency->>'optOutRTRI' AS optOutRTRI,\n" +
            "CASE\n" +
            "WHEN recency->>'optOutRTRI' = 'false' THEN 'No'\n" +
            "WHEN recency->>'optOutRTRI' = 'true' THEN 'Yes'\n" +
            "ELSE recency->>'optOutRTRI'\n" +
            "END AS optOutRTRIStatus,\n" +
            "recency->>'rencencyId' AS rencencyId,\n" +
            "recency->>'sampleType' AS sampleType,\n" +
            "recency->>'optOutRTRITestDate' AS rencencyTestDate, recency->>'optOutRTRITestName' AS rencencyTestType,\n" +
            "recency->>'rencencyInterpretation' AS rencencyInterpretation,\n" +
            "recency->>'finalRecencyResult' AS finalRecencyResult,\n" +
            "(CASE WHEN (hiv_test_result2 IS NOT NULL OR hiv_test_result2 != '') THEN\n" +
            "date_visit ELSE NULL END) as dateOfVisit,\n" +
            "(select display from base_application_codeset where code = hts_risk.testing_setting) AS testingSetting, (select display from base_application_codeset where code = hts_risk.entry_point) AS entryPoint,\n" +
            "he.date_started AS hivEnrollmentDate,he.date_of_registration as dateOfRegistrationOnHiv,he.date_confirmed_hiv AS dateConfirmHiv, he.date_started AS dateStarted, he.unique_id AS hivUniqueId,\n" +
            "pmtctenroll.pmtct_enrollment_date AS pmtctEnrollmentDate, pmtctdov.date_of_viral_load AS dateOfViralLoad, \n" +
            "labResult.result_reported AS resultReported, labResult.date_result_reported AS dateResultReported,\n" +
            "(CASE WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting IN ('FACILITY_HTS_TEST_SETTING_ANC', 'FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D', 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING') THEN ''\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'CT'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_FP' THEN 'FP'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting IN ('FACILITY_HTS_TEST_SETTING_SNS', 'FACILITY_HTS_TEST_SETTING_INDEX', 'FACILITY_HTS_TEST_SETTING_EMERGENCY', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_PEDIATRIC', 'FACILITY_HTS_TEST_SETTING_MALNUTRITION','FACILITY_HTS_TEST_SETTING_PREP_TESTING', 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY', 'FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)')  THEN 'Others'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS' THEN 'Standalone'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'Pregnant Women (Community)'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_INDEX', 'COMMUNITY_HTS_TEST_SETTING_OTHERS','COMMUNITY_HTS_TEST_SETTING_SNS', 'COMMUNITY_HTS_TEST_SETTING_CT', 'COMMUNITY_HTS_TEST_SETTING_OVC') THEN 'Others (Community)'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_OUTREACH', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Outreach (Community)'\n" +
            "END) AS gonModalities,  \n" +
            "(CASE WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_ANC' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting IN ('FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D' ) THEN 'PMTCT (Post ANC1: Pregnancy/L&D)'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' THEN 'PMTCT (Post ANC1: Breastfeeding)'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB_STAT/OtherPITC'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting IN ('FACILITY_HTS_TEST_SETTING_FP', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS','FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)') THEN 'Other PITC'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting ='FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting ='FACILITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting ='FACILITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting ='FACILITY_HTS_TEST_SETTING_EMERGENCY' THEN 'Emergency'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_PEDIATRIC' THEN 'Pediatric'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_MALNUTRITION' THEN 'Malnutrition'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_PREP_TESTING' THEN 'PrEP_CT HTS'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hts_risk.testing_setting = 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_OTHERS', 'COMMUNITY_HTS_TEST_SETTING_OVC', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Other Community Platforms'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN hts_risk.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hts_risk.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_OUTREACH' THEN 'Mobile'\n" +
            "END)AS pepfarModalities, hts_retest.visitDateIntial, hts_retest.hivResultInital,\n" +
            "retestingOpt.reVisitDate, retestingOpt.reHivResult,\n" +
            "(CASE WHEN AGE(hts_retest.visitDateIntial, retestingOpt.reVisitDate) <= INTERVAL '2 years' THEN retestingOpt.reVisitDate ELSE NULL \n" +
            "END) AS maternalRetestingDate,\n" +
            "(CASE WHEN AGE(hts_retest.visitDateIntial, retestingOpt.reVisitDate) <= INTERVAL '2 years' THEN retestingOpt.reHivResult ELSE NULL \n" +
            "END) AS maternalRetestingResult,\n" +
            "ROW_NUMBER() OVER ( PARTITION BY hc.person_uuid ORDER BY date_visit DESC, hc.date_created DESC) AS rnk,\n" +
            "date_visit, hc.facility_id\n" +
            "FROM hts_client hc\n" +
            "LEFT JOIN hts_risk_stratification hts_risk ON hc.risk_stratification_code = hts_risk.code AND hts_risk.archived = 0\n" +
            "LEFT JOIN hiv_enrollment he ON hc.person_uuid = he.person_uuid\n" +
            "LEFT JOIN pmtct_enrollment pmtctenroll ON hc.person_uuid = pmtctenroll.person_uuid\n" +
            "LEFT JOIN pmtct_mother_visitation pmtctdov ON hc.person_uuid = pmtctdov.person_uuid\n" +
            "LEFT JOIN laboratory_order labOrder ON hc.person_uuid = labOrder.patient_uuid\n" +
            "LEFT JOIN laboratory_test labTest ON labOrder.id = labTest.lab_order_id AND labTest.lab_test_id = 16\n" +
            "LEFT JOIN laboratory_result labResult ON labResult.test_id = labTest.id AND labResult.archived = 0 \n" +
            "LEFT JOIN (\n" +
            "SELECT hct.person_uuid, hct.date_visit AS visitDateIntial, (CASE WHEN (hct.hiv_test_result2 IS NULL OR hct.hiv_test_result2 = '') THEN hct.hiv_test_result ELSE hct.hiv_test_result2 END) AS hivResultInital, risk.testing_setting, hct.risk_stratification_code, \n" +
            "ROW_NUMBER() OVER (PARTITION BY hct.person_uuid ORDER BY date_visit DESC) AS rowNums\n" +
            "FROM hts_client hct\n" +
            "LEFT JOIN hts_risk_stratification risk ON hct.risk_stratification_code = risk.code AND risk.archived = 0\n" +
            "WHERE hct.pregnant IN (73,74,75) AND risk.testing_setting IN ('FACILITY_HTS_TEST_SETTING_ANC', 'COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW', 'COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES', 'FACILITY_HTS_TEST_SETTING_L&D', 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING', 'COMMUNITY_PMTCT_SPOKE_HEALTH_FACILITY')\n" +
            "AND hct.date_visit BETWEEN ?2 AND ?3\n" +
            ") as hts_retest ON hc.person_uuid = hts_retest.person_uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT hct.person_uuid, hct.date_visit AS reVisitDate, (CASE WHEN (hct.hiv_test_result2 IS NULL OR hct.hiv_test_result2 = '') THEN hct.hiv_test_result ELSE hct.hiv_test_result2 END) AS reHivResult,risk.testing_setting, hct.risk_stratification_code, \n" +
            "ROW_NUMBER() OVER (PARTITION BY hct.person_uuid ORDER BY date_visit DESC) AS rowNums\n" +
            "FROM hts_client hct\n" +
            "LEFT JOIN hts_risk_stratification risk ON hct.risk_stratification_code = risk.code AND risk.archived = 0\n" +
            "WHERE hct.pregnant IN (73,74,75) AND risk.testing_setting IN ('FACILITY_HTS_TEST_SETTING_RETESTING')\n" +
            ") AS retestingOpt ON hc.person_uuid = retestingOpt.person_uuid \n"+
            "WHERE hc.archived = 0 AND hts_risk.testing_setting IN ('FACILITY_HTS_TEST_SETTING_ANC', 'COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW', 'COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES', 'FACILITY_HTS_TEST_SETTING_L&D', 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING','FACILITY_HTS_TEST_SETTING_RETESTING','COMMUNITY_PMTCT_SPOKE_HEALTH_FACILITY')\n" +
            "GROUP BY hc.person_uuid, hc.date_visit, hc.hiv_test_result, hc.hiv_test_result2,hc.risk_stratification_code,hc.hepatitis_testing,hc.date_created,hc.recency, hts_risk.testing_setting, hts_risk.entry_point, hc.facility_id,\n" +
            "he.date_started,he.date_of_registration,he.date_confirmed_hiv, he.date_started, pmtctenroll.pmtct_enrollment_date, pmtctdov.date_of_viral_load, labResult.result_reported,labResult.date_result_reported, he.unique_id, hts_retest.visitDateIntial, hts_retest.hivResultInital, retestingOpt.reVisitDate, retestingOpt.reHivResult\n" +
            ") rr WHERE rnk = 1 AND facility_id = ?1 AND date_visit BETWEEN ?2 AND ?3\n" +
            "),\n" +
            "ancClient AS (\n" +
            "SELECT * FROM (\n" +
            "SELECT person_uuid AS person_uuid_anc,\n" +
            "(CASE WHEN community_setting = 'PMTCT (ANC1 Only)' THEN 'PMTCT (ANC1 Only)' ELSE (select display from base_application_codeset where code = community_setting) END) AS ancSettingAnc,\n" +
            "previously_known_hiv_status AS previouslyKnownHivStatus,\n" +
            "first_anc_date AS firstAncDate,\n" +
            "gaweeks AS gaweeksAnc,\n" +
            "gravida AS gravidaAnc,\n" +
            "parity As parityAnc,\n" +
            "tested_syphilis AS testedSyphilisAnc,\n" +
            "test_result_syphilis AS testResultSyphilisAnc,\n" +
            "CASE\n" +
            "WHEN treated_syphilis = 'Yes' THEN 'Treated'\n" +
            "WHEN referred_syphilis_treatment = 'Yes' THEN 'Referred for Treatment'\n" +
            "ELSE 'No treatment'\n" +
            "END as syphilisTreatmentStatus,\n" +
            "partner_information->>'age' AS age,\n" +
            "partner_information->>'syphillisStatus' AS syphillisStatus,\n" +
            "partner_information->>'acceptHivTest' AS acceptHivTest,\n" +
            "partner_information->>'referredTo' AS referredTo,\n" +
            "pmtct_hts_info->>'hivRestested' AS hivRestested,\n" +
            "pmtct_hts_info->>'acceptedHIVTesting' AS acceptedHIVTesting,\n" +
            "pmtct_hts_info->>'dateTestedHivPositive' AS dateTestedHivPositive,\n" +
            "pmtct_hts_info->>'receivedHivRetestedResult' AS receivedHivRetestedResult,\n" +
            "pmtct_hts_info->>'previouslyKnownHIVPositive' AS previouslyKnownHIVPositive,\n" +
            "anc_no AS ancNo, treated_hepatitisb AS treatedHepatitisB,\n" +
            "static_hiv_status AS staticHivStatus,\n" +
            "ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY first_anc_date DESC) AS rnk1,\n" +
            "MAX(created_date) AS max_created_date_anc\n" +
            "FROM pmtct_anc\n" +
            "GROUP BY person_uuid, anc_setting, first_anc_date, gaweeks, gravida, parity, tested_syphilis, test_result_syphilis, partner_information, anc_no, static_hiv_status, \n" +
            "pmtct_hts_info,syphilisTreatmentStatus,previously_known_hiv_status, treated_hepatitisb, community_setting ) anc WHERE rnk1 = 1 AND firstAncDate BETWEEN ?2 AND ?3\n" +
            "),\n" +
            "delivery AS (\n" +
            "SELECT * FROM (\n" +
            "SELECT person_uuid AS personUuidDelivery,\n" +
            "hbstatus AS hbstatusDelivery, date_of_delivery,\n" +
            "ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_delivery DESC) AS rnkk\n" +
            "FROM pmtct_delivery\n" +
            "GROUP BY person_uuid, hbstatus, date_of_delivery\n" +
            ") del where rnkk = 1 AND date_of_delivery BETWEEN ?2 AND ?3\n" +
            ")\n" +
            "select * from pmtctHts\n" +
            "INNER JOIN hts_client hts ON hts.person_uuid_hts_client = pmtctHts.PersonUuid AND hc.hiv_test_result IS NOT NULL AND hc.hiv_test_result !='' \n" +
            "LEFT JOIN ancClient anc ON hts.person_uuid_hts_client = anc.person_uuid_anc\n" +
            "LEFT JOIN delivery del ON hts.person_uuid_hts_client = del.personUuidDelivery\n";
}
