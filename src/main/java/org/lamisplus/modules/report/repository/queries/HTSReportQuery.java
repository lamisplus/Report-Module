package org.lamisplus.modules.report.repository.queries;

public class HTSReportQuery {

    private HTSReportQuery(){}

    public static final String HTS_REPORT_QUERY  = "WITH htsReport AS (\n" +
            "SELECT hc.client_code AS clientCode, htsCounts.numberOfCounts AS htsCount,\n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'first_name') ELSE INITCAP(pp.first_name) END) AS firstName,  \n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'surname') ELSE INITCAP(pp.surname) END) AS surname,  \n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'middile_name') ELSE INITCAP(pp.other_name) END) AS otherName,  \n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'gender') ELSE INITCAP(pp.sex) END) AS sex,  \n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN CAST(hc.extra->>'age' AS INTEGER)   \n" +
            "ELSE CAST(EXTRACT(YEAR from AGE(?3,  pp.date_of_birth)) AS INTEGER )  \n" +
            "END) AS age,  \n" +
            "(CASE WHEN hc.person_uuid IS NOT NULL THEN pp.date_of_birth  \n" +
            "WHEN hc.person_uuid IS NULL AND LENGTH(hc.extra->>'date_of_birth') > 0  \n" +
            "AND hc.extra->>'date_of_birth' != '' THEN CAST(NULLIF(hc.extra->>'date_of_birth', '') AS DATE) ELSE NULL END) AS dateOfBirth,  \n" +
            " (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'phone_number'  \n" +
            "ELSE pp.contact_point->'contactPoint'->0->'value'->>0 END) AS phoneNumber,  \n" +
            " (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'marital_status'  \n" +
            "ELSE pp.marital_status->>'display' END) AS maritalStatus,  \n" +
            "(CASE WHEN hc.person_uuid IS NULL  \n" +
            "THEN hc.extra->>'lga_of_residence' ELSE lgaOfResidence.lgaOfResidence END) AS LGAOfResidence,  \n" +
            "(CASE WHEN hc.person_uuid IS NULL   \n" +
            " THEN hc.extra->>'state_of_residence' ELSE res_state.name END) AS StateOfResidence,  \n" +
            " facility.name AS facility,  \n" +
            " state.name AS state,  \n" +
            " lga.name AS lga,  \n" +
            " pp.uuid AS patientId,  \n" +
            "pp.education->>'display' as education,   \n" +
            "pp.employment_status->>'display' as occupation,  \n" +
            "boui.code as datimCode,  \n" +
            "hc.others->>'latitude' AS HTSLatitude,  \n" +
            "hc.others->>'longitude' AS HTSLongitude,    \n" +
            "(CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'client_address' ELSE r.address END) AS clientAddress,  \n" +
            "hc.date_visit AS dateVisit,  \n" +
            "htsPrevious.date_visit AS previousVisitDate, htsPrevious.hiv_test_result AS previousTestResult,\n" +
            "(CASE WHEN hc.first_time_visit IS true THEN 'Yes' ELSE 'No' END) firstTimeVisit,  \n" +
            "hc.num_children AS numberOfChildren,  \n" +
            "hc.num_wives AS numberOfWives,  \n" +
            "(CASE WHEN hc.index_client IS true THEN 'Yes' ELSE 'No' END) indexClient,  \n" +
            "(CASE WHEN hc.hiv_test_result = 'Positive' THEN 'No'\n" +
            " WHEN hc.prep_offered IS true THEN 'Yes' ELSE 'No' END)  AS prepOffered,  \n" +
            "(CASE WHEN hc.hiv_test_result = 'Positive' THEN 'No'\n" +
            "WHEN hc.prep_accepted IS true THEN 'Yes' ELSE 'No' END) AS prepAccepted,  \n" +
            "(CASE WHEN hc.previously_tested IS true THEN 'Yes' ELSE 'No' END) AS previouslyTested,   \n" +
            "tg.display AS targetGroup,  \n" +
            "rf.display AS referredFrom,  \n" +
            "(select display from base_application_codeset where code = hrs.testing_setting) AS testingSetting,  \n" +
            "tc.display AS counselingType,  \n" +
            "preg.display AS pregnancyStatus, (select display from base_application_codeset where code = hrs.entry_point) AS entryPoint, \n" +
            "(CASE \n" +
            "WHEN preg.display='Breastfeeding' THEN 'Yes'  \n" +
            "WHEN preg.display IS NULL THEN NULL \n" +
            "ELSE 'No' \n" +
            "END) AS breastFeeding,\n" +
            "it.display AS indexType,  \n" +
            "(CASE WHEN hc.recency->>'optOutRTRI' ILIKE 'true' THEN 'Yes'\n" +
            "WHEN hc.recency->>'optOutRTRI' ILIKE 'false' THEN 'No'  \n" +
            "WHEN hc.recency->>'optOutRTRI' != NULL THEN hc.recency->>'optOutRTRI' \n" +
            "ELSE NULL END) AS IfRecencyTestingOptIn,  \n" +
            "hc.recency->>'rencencyId' AS RecencyID,  \n" +
            "hc.recency->>'optOutRTRITestName' AS recencyTestType,  \n" +
            "(CASE WHEN hc.recency->>'optOutRTRITestDate' IS NOT NULL \n" +
            " AND hc.recency->>'optOutRTRITestDate' != '' AND LENGTH(hc.recency->>'optOutRTRITestDate') > 0  \n" +
            " THEN CAST(NULLIF(hc.recency->>'optOutRTRITestDate', '') AS DATE)\n" +
            " WHEN hc.recency->>'sampleTestDate' IS NOT NULL \n" +
            " AND hc.recency->>'sampleTestDate' != '' AND LENGTH(hc.recency->>'sampleTestDate') > 0  \n" +
            " THEN CAST(NULLIF(hc.recency->>'sampleTestDate', '') AS DATE) ELSE NULL END) AS recencyTestDate,  \n" +
            "(CASE WHEN hc.recency->>'receivedResultDate' IS NOT NULL \n" +
            "  AND hc.recency->>'receivedResultDate' != '' AND LENGTH(hc.recency->>'receivedResultDate') > 0  \n" +
            "  THEN CAST(NULLIF(hc.recency->>'receivedResultDate', '') AS DATE) ELSE NULL END) AS viralLoadReceivedResultDate,  \n" +
            "(CASE \n" +
            " WHEN hc.recency->>'rencencyInterpretation' IS NOT NULL \n" +
            " AND hc.recency->>'rencencyInterpretation' ILIKE '%Long%' THEN 'RTRI Longterm'\n" +
            " WHEN hc.recency->>'rencencyInterpretation' IS NOT NULL \n" +
            " AND hc.recency->>'rencencyInterpretation' ILIKE '%Recent%' THEN 'RTRI Recent' \n" +
            " ELSE hc.recency->>'rencencyInterpretation' END) AS recencyInterpretation,  \n" +
            "hc.recency->>'finalRecencyResult' AS finalRecencyResult,  \n" +
            "hc.recency->>'viralLoadResultClassification' AS viralLoadResult, \n" +
            "CAST(NULLIF(hc.recency->>'sampleCollectedDate', '') AS DATE) AS viralLoadSampleCollectionDate,\n" +
            "hc.recency->>'viralLoadConfirmationResult' AS viralLoadConfirmationResult,\n" +
            "CAST(NULLIF(hc.recency->>'viralLoadConfirmationTestDate', '') AS DATE) AS viralLoadConfirmationDate,  \n" +
            "hc.risk_stratification_code AS Assessmentcode,  \n" +
            "modality_code.display AS modality,  \n" +
            "(CASE WHEN hc.syphilis_testing->>'syphilisTestResult' ILIKE 'Yes'   \n" +
            "THEN 'Reactive' WHEN hc.syphilis_testing->>'syphilisTestResult' ILIKE 'No' THEN 'Non-Reactive' ELSE '' END) As syphilisTestResult,  \n" +
            "(CASE WHEN hc.hepatitis_testing->>'hepatitisBTestResult' ILIKE 'Yes'   \n" +
            " THEN 'Positive' WHEN hc.hepatitis_testing->>'hepatitisBTestResult' ILIKE 'No' THEN 'Negative' ELSE '' END) AS hepatitisBTestResult,  \n" +
            "(CASE WHEN hc.hepatitis_testing->>'hepatitisCTestResult' ILIKE 'Yes'   \n" +
            " THEN 'Positive' WHEN hc.hepatitis_testing->>'hepatitisCTestResult' ILIKE 'No' THEN 'Negative' ELSE '' END) AS hepatitisCTestResult,  \n" +
            "hc.cd4->>'cd4Count' AS CD4Type,  \n" +
            "hc.cd4->>'cd4SemiQuantitative' AS CD4TestResult,  \n" +
            "(CASE WHEN hc.hiv_test_result2 = 'Positive' THEN 'Positive'\n" +
            "WHEN  hc.hiv_test_result ='Negative' THEN 'Negative'\n" +
            "WHEN  hc.hiv_test_result ='Positive' AND hc.hiv_test_result2='Negative' THEN 'Negative'\n" +
            "WHEN  hc.hiv_test_result ='Positive' AND hc.hiv_test_result2 IS NULL THEN 'Positive'\n" +
            "WHEN hc.test1->>'result' ILIKE 'Yes' THEN 'Positive' ELSE 'Negative' END) AS finalHIVTestResult,  \n" +
            "he.person_uuid AS patientUuid,\n" +
            "(CASE WHEN LENGTH(hc.test1->>'date') > 0 AND hc.test1->>'date' !=''  THEN CAST(NULLIF(hc.test1->>'date', '') AS DATE)  \n" +
            "WHEN hc.date_visit IS NOT NULL THEN hc.date_visit ELSE NULL END)dateOfHIVTesting,  \n" +
            "CAST(post_test_counseling->>'condomProvidedToClientCount' AS VARCHAR) AS numberOfCondomsGiven,  \n" +
            "CAST(post_test_counseling->>'lubricantProvidedToClientCount' AS VARCHAR) AS numberOfLubricantsGiven,\n" +
            "CAST (riskScore.totalRiskScore AS VARCHAR) AS totalRiskScore, hc.source, hc.referred_for_sti AS refferedForSti, \n" +
            "hc.others->>'adhocCode' AS TesterName,\n" +
            "(CASE WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting IN ('FACILITY_HTS_TEST_SETTING_ANC', 'FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D', 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING') THEN ''\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'CT'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_FP' THEN 'FP'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting IN ('FACILITY_HTS_TEST_SETTING_SNS', 'FACILITY_HTS_TEST_SETTING_INDEX', 'FACILITY_HTS_TEST_SETTING_EMERGENCY', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_PEDIATRIC', 'FACILITY_HTS_TEST_SETTING_MALNUTRITION','FACILITY_HTS_TEST_SETTING_PREP_TESTING', 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY', 'FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)')  THEN 'Others'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS' THEN 'Standalone'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'Pregnant Women (Community)'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_INDEX', 'COMMUNITY_HTS_TEST_SETTING_OTHERS','COMMUNITY_HTS_TEST_SETTING_SNS', 'COMMUNITY_HTS_TEST_SETTING_CT', 'COMMUNITY_HTS_TEST_SETTING_OVC') THEN 'Others (Community)'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_OUTREACH', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Outreach (Community)'\n" +
            "END) AS gonModalities,  \n" +
            "(CASE WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_ANC' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting IN ('FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D' ) THEN 'PMTCT (Post ANC1: Pregnancy/L&D)'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' THEN 'PMTCT (Post ANC1: Breastfeeding)'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB_STAT/OtherPITC'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting IN ('FACILITY_HTS_TEST_SETTING_FP', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS','FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)') THEN 'Other PITC'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting ='FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting ='FACILITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting ='FACILITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting ='FACILITY_HTS_TEST_SETTING_EMERGENCY' THEN 'Emergency'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_PEDIATRIC' THEN 'Pediatric'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_MALNUTRITION' THEN 'Malnutrition'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_PREP_TESTING' THEN 'PrEP_CT HTS'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_FACILITY' AND hrs.testing_setting = 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting IN ('COMMUNITY_HTS_TEST_SETTING_OTHERS', 'COMMUNITY_HTS_TEST_SETTING_OVC', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Other Community Platforms'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN hrs.entry_point = 'HTS_ENTRY_POINT_COMMUNITY' AND hrs.testing_setting = 'COMMUNITY_HTS_TEST_SETTING_OUTREACH' THEN 'Mobile'\n" +
            "END)AS pepfarModalities, hc.risk_assessment->>'mlScore' AS mlScore, hc.risk_assessment->>'mlStatus' AS mlStatus,\n" +
            "ROW_NUMBER() OVER ( PARTITION BY hc.person_uuid ORDER BY hc.date_visit DESC) AS rnkk\n" +
            "FROM hts_client hc  \n" +
            "LEFT JOIN base_application_codeset tg ON tg.code = hc.target_group  \n" +
            "LEFT JOIN base_application_codeset it ON it.id = hc.relation_with_index_client\n" +
            "LEFT JOIN base_application_codeset rf ON rf.id = hc.referred_from  \n" +
            "LEFT JOIN base_application_codeset ts ON ts.code = hc.testing_setting  \n" +
            "LEFT JOIN base_application_codeset tc ON tc.id = hc.type_counseling  \n" +
            "LEFT JOIN base_application_codeset preg ON preg.id = hc.pregnant  \n" +
            "LEFT JOIN base_application_codeset relation ON relation.id = hc.relation_with_index_client\n" +
            "LEFT JOIN hts_risk_stratification hrs ON hrs.code = hc.risk_stratification_code  \n" +
            "LEFT JOIN base_application_codeset modality_code ON modality_code.code = hrs.modality  \n" +
            "LEFT JOIN patient_person pp ON pp.uuid=hc.person_uuid  \n" +
            "LEFT JOIN (SELECT * FROM (SELECT\n" +
            "p.id,\n" +
            "p.address ->>'{address,0,city}' as clientcity,\n" +
            "p.address ->> '{address,0,line,0}' as clientaddress,\n" +
            "(jsonb_array_elements(p.address->'address')->>'stateId') AS stateid,\n" +
            "(jsonb_array_elements(p.address->'address')->>'city') as address\n" +
            "FROM patient_person p) as result WHERE stateid ~ '^[0-9]+$' ) r ON r.id=pp.id\n" +
            "LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)   \n" +
            "LEFT JOIN base_organisation_unit facility ON facility.id=hc.facility_id  \n" +
            "LEFT JOIN base_organisation_unit state ON state.id=facility.parent_organisation_unit_id  \n" +
            "LEFT JOIN base_organisation_unit lga ON lga.id=state.parent_organisation_unit_id  \n" +
            "LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=hc.facility_id AND boui.name='DATIM_ID' \n" +
            "LEFT JOIN (select DISTINCT ON (personUuid) personUuid as personUuid11, \n" +
            "case when (addr ~ '^[0-9.]+$') =TRUE \n" +
            " then (select name from base_organisation_unit where id = cast(addr as int)) ELSE\n" +
            "(select name from base_organisation_unit where id = cast(facilityLga as int)) end as lgaOfResidence \n" +
            "from (\n" +
            " select pp.uuid AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga, (jsonb_array_elements(pp.address->'address')->>'district') as addr from patient_person pp\n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER) \n" +
            ") dt ) lgaOfResidence ON lgaOfResidence.personUuid11 = hc.person_uuid\n" +
            "LEFT JOIN hiv_enrollment he ON he.person_uuid = hc.person_uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, COUNT(person_uuid) AS numberOfCounts from hts_client where archived = 0\n" +
            "group by 1\n" +
            ") htsCounts ON htsCounts.person_uuid = hc.person_uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT * FROM (\n" +
            "select person_uuid,date_visit, hiv_test_result,   \n" +
            "ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_visit DESC) AS rnk\n" +
            "FROM hts_client \n" +
            "WHERE archived = 0\n" +
            "GROUP BY person_uuid,date_visit, hiv_test_result \n" +
            ") pre where rnk = 2\n" +
            ") htsPrevious ON htsPrevious.person_uuid = hc.person_uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, diagnosedWithTb + lastHivTestDone + whatWasTheResult + lastHivTestHadAnal + lastHivTestVaginalOral\n" +
            "+ lastHivTestInjectedDrugs + lastHivTestBasedOnRequest + lastHivTestForceToHaveSex + lastHivTestBloodTransfusion + lastHivTestPainfulUrination\n" +
            " AS totalRiskScore FROM(\n" +
            "SELECT DISTINCT ON (person_uuid) person_uuid,\n" +
            "CASE WHEN risk_assessment->>'diagnosedWithTb' = 'true' THEN 1 ELSE 0 END AS diagnosedWithTb,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestDone' = 'true' THEN 1 ELSE 0 END AS lastHivTestDone,\n" +
            "CASE WHEN risk_assessment->>'whatWasTheResult' = 'true' THEN 1 ELSE 0 END AS whatWasTheResult,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestHadAnal' = 'true' THEN 1 ELSE 0 END AS lastHivTestHadAnal,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestVaginalOral' = 'true' THEN 1 ELSE 0 END AS lastHivTestVaginalOral,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestInjectedDrugs' = 'true' THEN 1 ELSE 0 END AS lastHivTestInjectedDrugs,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestBasedOnRequest' = 'true' THEN 1 ELSE 0 END AS lastHivTestBasedOnRequest,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestForceToHaveSex' = 'true' THEN 1 ELSE 0 END AS lastHivTestForceToHaveSex,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestBloodTransfusion' = 'true' THEN 1 ELSE 0 END AS lastHivTestBloodTransfusion,\n" +
            "CASE WHEN risk_assessment->>'lastHivTestPainfulUrination' = 'true' THEN 1 ELSE 0 END AS lastHivTestPainfulUrination\n" +
            "FROM hts_risk_stratification\n" +
            ") totalRisk \n" +
            "group by person_uuid, totalrisk.diagnosedwithtb, totalrisk.lastHivTestDone ,totalrisk.whatWasTheResult,totalrisk.lastHivTestHadAnal,totalrisk.lastHivTestVaginalOral,\n" +
            "totalrisk.lastHivTestInjectedDrugs,totalrisk.lastHivTestBasedOnRequest, totalrisk.lastHivTestForceToHaveSex, totalrisk.lastHivTestBloodTransfusion, totalrisk.lastHivTestPainfulUrination\n" +
            ") riskScore ON riskScore.person_uuid = hc.person_uuid\n" +
            "WHERE hc.archived=0 AND hc.facility_id=?1 AND hc.date_visit >=?2 AND hc.date_visit <= ?3\n" +
            " GROUP BY hc.client_code, \n" +
            "        hc.person_uuid, htsPrevious.hiv_test_result, \n" +
            "        hc.extra, \n" +
            "        pp.first_name, \n" +
            "pp.marital_status,\n" +
            "        pp.surname, \n" +
            "        pp.other_name, \n" +
            "        pp.sex, \n" +
            "        pp.date_of_birth, \n" +
            "        pp.contact_point,\n" +
            "        facility.name, \n" +
            "        state.name, \n" +
            "        lga.name, \n" +
            "        he.person_uuid,\n" +
            "        htsPrevious.date_visit,\n" +
            "        tg.display,\n" +
            "        rf.display,\n" +
            "        ts.display,\n" +
            "        tc.display,\n" +
            "        preg.display,\n" +
            "        it.display,\n" +
            "        hc.recency->>'optOutRTRI',\n" +
            "        hc.recency->>'rencencyId',\n" +
            "        hc.recency->>'optOutRTRITestName',\n" +
            "        hc.recency->>'finalRecencyResult',\n" +
            "        hc.recency->>'viralLoadResultClassification',\n" +
            "        hc.source, hc.risk_assessment,\n" +
            "        hc.referred_for_sti,\n" +
            "        hc.others->>'adhocCode', hc.testing_setting, hrs.testing_setting,\n" +
            "lgaofresidence.lgaofresidence,\n" +
            "res_state.name, pp.uuid, pp.education, pp.employment_status, boui.code, hc.others, r.address, hc.date_visit, hc.first_time_visit, hc.num_children,\n" +
            "hc.num_wives, hc.index_client,hc.hiv_test_result, hc.prep_offered, hc.prep_accepted, hc.previously_tested, hrs.entry_point,\n" +
            "hc.recency, hc.risk_stratification_code, modality_code.display, hc.syphilis_testing,hc.hepatitis_testing, hc.cd4, hc.hiv_test_result2,\n" +
            "hc.test1, hc.post_test_counseling, riskscore.totalriskscore, htscounts.numberofcounts)\n" +
            "SELECT * FROM htsReport\n" +
            "WHERE rnkk = 1";
}
