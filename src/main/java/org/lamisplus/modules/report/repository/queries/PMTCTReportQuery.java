package org.lamisplus.modules.report.repository.queries;

public class PMTCTReportQuery {

    public static final String PMTCT_REPORT = "WITH pmtctRegister AS (\n" +
            "SELECT DISTINCT ON (patient_uuid)\n" +
            "    patient_uuid,\n" +
            "    date,\n" +
            "    gravida,\n" +
            "    parity\n" +
            "FROM (\n" +
            "    SELECT patient_uuid, date_of_enrollment AS date, gravida, parity\n" +
            "    FROM pmtct_anc\n" +
            "    WHERE archived IS FALSE AND facility_id = ?1 AND date_of_enrollment BETWEEN ?2 AND ?3\n" +
            "\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT patient_uuid, pmtct_enrollment_date AS date, gravida, NULL AS parity\n" +
            "    FROM pmtct_enrollment\n" +
            "    WHERE archived IS FALSE AND facility_id = ?1 AND pmtct_enrollment_date BETWEEN ?2 AND ?3\n" +
            ") t\n" +
            "ORDER BY patient_uuid, date DESC\n" +
            "),\n" +
            "htsTest AS (\n" +
            "SELECT facility_id, patient_uuid, date_of_visit dateOfVisit, client_code, bacSetting.display setting, setting entryPoint,\n" +
            "COALESCE(bacFacilitySetting.display, bacCommunitySetting.display) facilitySetting, observation->>'previouslyKnownHivPositive' previouslyKnownHivPositive,\n" +
            "bacTypeOfHiv.display typeOfHivTest,\n" +
            "observation->>'hivEarlyDetect' hivEarlyDetect,\n" +
            "observation->>'hivEarlyDetectViralLoad' hivEarlyDetectViralLoad,\n" +
            "observation->>'finalHivTestResult' finalHivTestResult,\n" +
            "observation->>'knownPositive' knownPositive,\n" +
            "observation->'hbvInfo'->>'testResult' hbTestResult,\n" +
            "observation->'hbvInfo'->>'treatment' hbTreatment,\n" +
            "observation->'syphilisInfo'->>'treatment' syphilisTreatment,\n" +
            "observation->'syphilisInfo'->>'testResult' syphilisTestResult,\n" +
            "bacTbStatus.display tbScreeningStatus,\n" +
            "observation->>'hepatitisC' hepatitisC,\n" +
            "observation->>'tbReferred' tbReferred,\n" +
            "observation->'partnerInfo'->>'testedHiv' partnerTestedHiv,\n" +
            "observation->'partnerInfo'->>'testedSyphilis' partnerTestedSyphilis,\n" +
            "observation->'partnerInfo'->>'testedHbv' partnerTestedHb,\n" +
            "bac.display partnerReferal,\n" +
            "observation->>'testingType' testingType,\n" +
            "CASE WHEN observation->>'testingType' = 'RETESTING' THEN observation->>'finalHivTestResult' ELSE NULL END AS maternalRestesting,\n" +
            "CASE WHEN observation->>'testingType' = 'RETESTING' THEN date_of_visit ELSE NULL END dateOfMaternalRestesting,\n" +
            "observation->>'communityEntryPoint' communityEntryPoint, observation->>'facilitySetting' facilitySettingRaw\n" +
            "FROM (\n" +
            "SELECT facility_id, patient_uuid, date_of_visit, client_code,\n" +
            "setting, observation, ROW_NUMBER() OVER (PARTITION BY patient_uuid ORDER BY date_of_visit DESC) rnnk\n" +
            "FROM hts_encounter WHERE archived IS FALSE AND facility_id = ?1 AND date_of_visit BETWEEN ?2 AND ?3\n" +
            ")subQ \n" +
            "LEFT JOIN base_application_codeset bac ON bac.code = observation->'partnerInfo'->>'referral'\n" +
            "LEFT JOIN base_application_codeset bacTbStatus ON bacTbStatus.code = observation->>'tbScreeningStatus'\n" +
            "LEFT JOIN base_application_codeset bacTypeOfHiv ON bacTypeOfHiv.code = observation->>'typeOfHivTest'\n" +
            "LEFT JOIN base_application_codeset bacSetting ON bacSetting.code = setting\n" +
            "LEFT JOIN base_application_codeset bacFacilitySetting ON bacFacilitySetting.code = observation->>'facilitySetting'\n" +
            "LEFT JOIN base_application_codeset bacCommunitySetting ON bacCommunitySetting.code = observation->>'communityEntryPoint'\n" +
            "where rnnk =1\n" +
            "),\n" +
            "bio_data AS (\n" +
            "SELECT DISTINCT ON (p.uuid) p.uuid AS personUuid, p.hospital_number AS hospitalNumber,\n" +
            "CAST(EXTRACT(YEAR FROM AGE(CAST(?3 AS DATE), p.date_of_birth)) AS INTEGER) AS age, INITCAP(p.sex) AS gender,\n" +
            "p.date_of_birth AS dateOfBirth, facility.name AS facilityName, facility_lga.name AS lga, facility_state.name AS state,\n" +
            "boui.code AS datimId, p.first_name AS firstName, p.surname AS surname, p.other_name AS otherName, p.contact_point->'contactPoint'->0->>'value' AS phoneNumber,\n" +
            "p.marital_status->>'display' AS maritalStatus, p.address->'address'->0->>'city' AS address,\n" +
            "p.employment_status->>'display' AS occupation, p.education->>'display' AS educationStatus\n" +
            "FROM patient_person p\n" +
            "INNER JOIN base_organisation_unit facility ON facility.id = p.facility_id\n" +
            "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit_identifier boui\n" +
            "ON boui.organisation_unit_id = p.facility_id AND boui.name = 'DATIM_ID'\n" +
            "WHERE p.archived = 0 AND p.facility_id = ?1\n" +
            "),\n" +
            "patientResidencial as (select DISTINCT ON (personUuid) personUuid as personUuid11,\n" +
            "case when (addr ~ '^[0-9\\\\\\\\.]+$') =TRUE\n" +
            " then (select name from base_organisation_unit where id = cast(addr as int)) ELSE\n" +
            "(select name from base_organisation_unit where id = cast(facilityLga as int)) end as lgaOfResidence,\n" +
            "(select name from base_organisation_unit where id = (CASE WHEN stateResidence ~ '^[0-9]+$' THEN CAST(stateResidence AS INTEGER) ELSE null END)) AS stateOfResidence\n" +
            "from (\n" +
            " select CAST(pp.uuid AS UUID) AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga, (jsonb_array_elements(pp.address->'address')->>'stateId') stateResidence, (jsonb_array_elements(pp.address->'address')->>'district') as addr from patient_person pp\n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER)\n" +
            ") dt)\n" +
            "SELECT bio.state, bio.lga, bio.facilityName, bio.personUuid, bio.hospitalNumber, bio.age, bio.maritalStatus, hts.setting, hts.facilitySetting, \n" +
            "pr.date, pr.gravida, pr.parity, hts.previouslyKnownHivPositive, hts.typeOfHivTest, hts.hivEarlyDetect, hts.hivEarlyDetectViralLoad, hts.dateOfVisit, \n" +
            "hts.finalHivTestResult, hts.knownPositive, hts.hbTestResult, hts.hbTreatment, CASE WHEN hts.hepatitisC IS NOT NULL THEN hts.dateOfVisit ELSE NULL END AS dateTestedForHepatitisC, hts.hepatitisC, hts.syphilisTestResult, hts.syphilisTreatment, hts.dateOfMaternalRestesting,\n" +
            "hts.maternalRestesting, hts.tbScreeningStatus, hts.tbReferred, hts.partnerTestedHiv, hts.partnerTestedSyphilis, hts.partnerTestedHb, hts.partnerReferal,\n" +
            "CAST((CASE WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw IN ('FACILITY_HTS_TEST_SETTING_ANC', 'FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D', 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING') THEN ''\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'CT'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_FP' THEN 'FP'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw IN ('FACILITY_HTS_TEST_SETTING_SNS', 'FACILITY_HTS_TEST_SETTING_INDEX', 'FACILITY_HTS_TEST_SETTING_EMERGENCY', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_PEDIATRIC', 'FACILITY_HTS_TEST_SETTING_MALNUTRITION','FACILITY_HTS_TEST_SETTING_PREP_TESTING', 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY', 'FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)')  THEN 'Others'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS' THEN 'Standalone'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'Pregnant Women (Community)'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw IN ('COMMUNITY_HTS_TEST_SETTING_INDEX', 'COMMUNITY_HTS_TEST_SETTING_OTHERS','COMMUNITY_HTS_TEST_SETTING_SNS', 'COMMUNITY_HTS_TEST_SETTING_CT', 'COMMUNITY_HTS_TEST_SETTING_OVC') THEN 'Others (Community)'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw IN ('COMMUNITY_HTS_TEST_SETTING_OUTREACH', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Outreach (Community)'\n" +
            "END) AS TEXT) AS gonModalities,  \n" +
            "CAST((CASE WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_ANC' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw IN ('FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D' ) THEN 'PMTCT (Post ANC1: Pregnancy/L&D)'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' THEN 'PMTCT (Post ANC1: Breastfeeding)'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB_STAT/OtherPITC'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw IN ('FACILITY_HTS_TEST_SETTING_FP', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS','FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)', 'FACILITY_HTS_TEST_SETTING_OTHERS') THEN 'Other PITC'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw ='FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw ='FACILITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw ='FACILITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw ='FACILITY_HTS_TEST_SETTING_EMERGENCY' THEN 'Emergency'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_PEDIATRIC' THEN 'Pediatric'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_MALNUTRITION' THEN 'Malnutrition'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_PREP_TESTING' THEN 'PrEP_CT HTS'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND hts.facilitySettingRaw = 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw = 'COMMUNITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw IN ('COMMUNITY_HTS_TEST_SETTING_OTHERS', 'COMMUNITY_HTS_TEST_SETTING_OVC', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Other Community Platforms'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw = 'COMMUNITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw = 'COMMUNITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN hts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND hts.facilitySettingRaw = 'COMMUNITY_HTS_TEST_SETTING_OUTREACH' THEN 'Mobile'\n" +
            "END) AS TEXT) AS pepfarModalities\n" +
            "FROM pmtctRegister pr\n" +
            "LEFT JOIN htsTest hts ON hts.patient_uuid = CAST(pr.patient_uuid AS UUID)\n" +
            "INNER JOIN bio_data bio ON bio.personUuid = pr.patient_uuid";
}
