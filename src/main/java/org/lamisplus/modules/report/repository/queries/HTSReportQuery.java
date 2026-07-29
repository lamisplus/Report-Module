package org.lamisplus.modules.report.repository.queries;

public class HTSReportQuery {

    private HTSReportQuery(){}

    public static final String HTS_REPORT_QUERY  = "WITH htsEncounter AS (\n" +
            "SELECT patientUuid, clientCode, dateOfVisit, finalHivTestResult, previouslyTestedThisYear, basEntry.display entryPoint, baspreviouslyTestedNegative.display previouslyTestedNegative,\n" +
            "basFacility.display AS facilitySetting, basSession.display AS typeOfSession, basPreg.display AS pregnancyStatus, basIndexTest.display AS indexTesting,\n" +
            "latestHts.indexClientCode, basIndexRel.display AS indexRelationship, basHivTest.display AS typeOfHivTestDone, basRecency.display AS recencyTest,\n" +
            "basSyph.display AS syphilisTestResult, basKits.display AS hivTestKitsProvided, basCategory.display AS categoryOfClients, basCondom.display AS condomsProvided,\n" +
            "CAST(latestHts.source AS TEXT) AS source, latestHts.longitude, latestHts.latitude, latestHts.facilityId, completedBy, CAST(CASE WHEN finalHivTestResult ILIKE '%Pos%' THEN patientUuid ELSE NULL END AS TEXT) AS positiveUuid,\n" +
            "CAST((CASE WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting IN ('FACILITY_HTS_TEST_SETTING_ANC', 'FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D', 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING') THEN ''\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'CT'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_FP' THEN 'FP'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting IN ('FACILITY_HTS_TEST_SETTING_SNS', 'FACILITY_HTS_TEST_SETTING_INDEX', 'FACILITY_HTS_TEST_SETTING_EMERGENCY', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_PEDIATRIC', 'FACILITY_HTS_TEST_SETTING_MALNUTRITION','FACILITY_HTS_TEST_SETTING_PREP_TESTING', 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY', 'FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)')  THEN 'Others'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS' THEN 'Standalone'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'Pregnant Women (Community)'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting IN ('COMMUNITY_HTS_TEST_SETTING_INDEX', 'COMMUNITY_HTS_TEST_SETTING_OTHERS','COMMUNITY_HTS_TEST_SETTING_SNS', 'COMMUNITY_HTS_TEST_SETTING_CT', 'COMMUNITY_HTS_TEST_SETTING_OVC') THEN 'Others (Community)'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting IN ('COMMUNITY_HTS_TEST_SETTING_OUTREACH', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Outreach (Community)'\n" +
            "END) AS TEXT) AS gonModalities,  \n" +
            "CAST((CASE WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_ANC' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting IN ('FACILITY_HTS_TEST_SETTING_RETESTING', 'FACILITY_HTS_TEST_SETTING_L&D' ) THEN 'PMTCT (Post ANC1: Pregnancy/L&D)'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' THEN 'PMTCT (Post ANC1: Breastfeeding)'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT' THEN 'Inpatient'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_TB' THEN 'TB_STAT/OtherPITC'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting IN ('FACILITY_HTS_TEST_SETTING_FP', 'FACILITY_HTS_TEST_SETTING_BLOOD_BANK', 'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS','FACILITY_HTS_TEST_SETTING_OTHERS_(SPECIFY)', 'FACILITY_HTS_TEST_SETTING_OTHERS') THEN 'Other PITC'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting ='FACILITY_HTS_TEST_SETTING_STI' THEN 'STI'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting ='FACILITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting ='FACILITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting ='FACILITY_HTS_TEST_SETTING_EMERGENCY' THEN 'Emergency'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_PEDIATRIC' THEN 'Pediatric'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_MALNUTRITION' THEN 'Malnutrition'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_PREP_TESTING' THEN 'PrEP_CT HTS'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_FACILITY' AND latestHts.facilitySetting = 'FACILITY_HTS_TEST_SETTING_SPOKE_HEALTH_FACILITY' THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting IN ('COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING', 'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES','COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX', 'COMMUNITY_HTS_TEST_SETTING_TBA_RT-HCW') THEN 'PMTCT (ANC1 Only)'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting = 'COMMUNITY_HTS_TEST_SETTING_INDEX' THEN 'Index'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting IN ('COMMUNITY_HTS_TEST_SETTING_OTHERS', 'COMMUNITY_HTS_TEST_SETTING_OVC', 'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS') THEN 'Other Community Platforms'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting = 'COMMUNITY_HTS_TEST_SETTING_SNS' THEN 'SNS'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting = 'COMMUNITY_HTS_TEST_SETTING_CT' THEN 'VCT'\n" +
            "WHEN latestHts.entryPoint = 'HTS_ENTRY_POINT_COMMUNITY' AND latestHts.facilitySetting = 'COMMUNITY_HTS_TEST_SETTING_OUTREACH' THEN 'Mobile'\n" +
            "END) AS TEXT) AS pepfarModalities\n" +
            "FROM (\n" +
            "SELECT CAST(patient_uuid AS TEXT) patientUuid, client_code clientCode, CAST(date_of_visit AS DATE) dateOfVisit, setting entryPoint, \n" +
            "observation->>'previouslyTestedNegative' previouslyTestedNegative,\n" +
            "COALESCE(NULLIF(observation->>'facilitySetting', ''),observation->>'communityEntryPoint') facilitySetting,\n" +
            "observation->>'typeOfSession' typeOfSession, \n" +
            "CASE WHEN observation->>'pregnancyStatus' = 'PREGANACY_STATUS_PREGNANT BREASTFEEDING_NO' THEN 'PREGANACY_STATUS_PREGNANT'\n" +
            "WHEN observation->>'pregnancyStatus' = 'PREGANACY_STATUS_NOT_PREGNANT BREASTFEEDING_NO' THEN 'PREGANACY_STATUS_NOT_PREGNANT'\n" +
            "WHEN observation->>'pregnancyStatus' = 'PREGANACY_STATUS_BREASTFEEDING BREASTFEEDING_NO' THEN 'PREGANACY_STATUS_BREASTFEEDING'\n" +
            "WHEN observation->>'pregnancyStatus' = 'PREGANACY_STATUS_POST_PARTUM BREASTFEEDING_NO' THEN 'PREGANACY_STATUS_POST_PARTUM'\n" +
            "WHEN observation->>'pregnancyStatus' = 'BREASTFEEDING_NO' THEN NULL ELSE NULL END AS fixPstatus,\n" +
            "observation->>'pregnancyStatus' pregnancyStatus,\n" +
            "observation->>'indexTesting' indexTesting,\n" +
            "observation->>'indexClientCode' indexClientCode,\n" +
            "observation->>'indexRelationship' indexRelationship,\n" +
            "observation->>'typeOfHivTestDone' typeOfHivTestDone,\n" +
            "observation->>'finalHivTestResult' finalHivTestResult,\n" +
            "observation->>'recencyTest' recencyTest,\n" +
            "observation->>'syphilisTestResult' syphilisTestResult,\n" +
            "observation->>'previouslyTestedThisYear' previouslyTestedThisYear,\n" +
            "observation->>'hivTestKitsProvided' hivTestKitsProvided,\n" +
            "observation->>'categoryOfClients' categoryOfClients,\n" +
            "observation->>'condomsProvided' condomsProvided,\n" +
            "observation->>'completedBy' completedBy, \n" +
            "source, longitude, latitude, facility_id facilityId,\n" +
            "ROW_NUMBER() OVER (PARTITION BY patient_uuid ORDER BY date_of_visit DESC) rnk\n" +
            "FROM hts_encounter\n" +
            "WHERE archived IS FALSE AND date_of_visit BETWEEN ?2 AND ?3 AND facility_id = ?1\n" +
            ") latestHts \n" +
            "LEFT JOIN base_application_codeset basEntry ON basEntry.code = latestHts.entryPoint\n" +
            "LEFT JOIN base_application_codeset baspreviouslyTestedNegative ON baspreviouslyTestedNegative.code = latestHts.previouslyTestedNegative\n" +
            "LEFT JOIN base_application_codeset basFacility ON basFacility.code = latestHts.facilitySetting\n" +
            "LEFT JOIN base_application_codeset basSession ON basSession.code = latestHts.typeOfSession\n" +
            "LEFT JOIN base_application_codeset basPreg ON basPreg.code = latestHts.fixPstatus\n" +
            "LEFT JOIN base_application_codeset basIndexTest ON basIndexTest.code = latestHts.indexTesting\n" +
            "LEFT JOIN base_application_codeset basIndexRel ON basIndexRel.code = latestHts.indexRelationship\n" +
            "LEFT JOIN base_application_codeset basHivTest ON basHivTest.code = latestHts.typeOfHivTestDone\n" +
            "LEFT JOIN base_application_codeset basRecency ON basRecency.code = latestHts.recencyTest\n" +
            "LEFT JOIN base_application_codeset basSyph ON basSyph.code = latestHts.syphilisTestResult\n" +
            "LEFT JOIN base_application_codeset basKits ON basKits.code = latestHts.hivTestKitsProvided\n" +
            "LEFT JOIN base_application_codeset basCategory ON basCategory.code = latestHts.categoryOfClients\n" +
            "LEFT JOIN base_application_codeset basCondom ON basCondom.code = latestHts.condomsProvided\n" +
            "WHERE rnk = 1\n" +
            "),\n" +
            "previousHts AS (\n" +
            "SELECT CAST(patient_uuid AS TEXT), dateOfPreviousHts, previousHtsResult FROM (\n" +
            "select patient_uuid, CAST(date_of_visit AS DATE) dateOfPreviousHts, observation->>'finalHivTestResult' previousHtsResult,   \n" +
            "ROW_NUMBER() OVER ( PARTITION BY patient_uuid ORDER BY date_of_visit DESC) AS rnk\n" +
            "FROM hts_encounter\n" +
            "WHERE archived IS FALSE AND observation->>'finalHivTestResult' IS NOT NULL AND observation->>'finalHivTestResult' <> '' AND date_of_visit BETWEEN ?2 AND ?3\n" +
            ") pre where rnk = 2\n" +
            "),\n" +
            "htsCounts AS (\n" +
            "SELECT CAST(patient_uuid AS TEXT), CAST(COUNT(patient_uuid) AS INTEGER) AS numberOfCounts from hts_encounter where archived IS FALSE AND observation->>'finalHivTestResult' IS NOT NULL\n" +
            "AND date_of_visit BETWEEN ?2 AND ?3\n" +
            "group by 1\n" +
            "), \n" +
            "bio_data AS (\n" +
            "SELECT DISTINCT ON (p.uuid) CAST(p.uuid AS TEXT) AS personUuid, p.hospital_number AS hospitalNumber,\n" +
            "CAST(EXTRACT(YEAR FROM AGE(CAST(?3 AS DATE), p.date_of_birth)) AS INTEGER) AS age, INITCAP(p.sex) AS sex,\n" +
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
            "WHERE p.archived = 0 AND p.facility_id = ?1),\n" +
            "patientResidencial as (select DISTINCT ON (personUuid) personUuid as personUuid11,\n" +
            "case when (addr ~ '^[0-9]+$') =TRUE\n" +
            " then CAST((select name from base_organisation_unit where id = cast(addr as int)) AS TEXT) ELSE\n" +
            "CAST((select name from base_organisation_unit where id = cast(facilityLga as int)) AS TEXT) end as lgaOfResidence,\n" +
            "CAST((select name from base_organisation_unit where id = (CASE WHEN stateResidence ~ '^[0-9]+$' THEN CAST(stateResidence AS INTEGER) ELSE null END)) AS TEXT) AS stateOfResidence\n" +
            "from (\n" +
            " select CAST(pp.uuid AS TEXT) AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga, (jsonb_array_elements(pp.address->'address')->>'stateId') stateResidence, (jsonb_array_elements(pp.address->'address')->>'district') as addr from patient_person pp\n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER)\n" +
            ") dt)\n" +
            "SELECT bio.personUuid, bio.hospitalNumber, bio.age, bio.sex, bio.dateOfBirth, bio.facilityName, bio.lga,\n" +
            "bio.state, bio.datimId, bio.firstName, bio.surname, bio.otherName, bio.phoneNumber, bio.maritalStatus, bio.address,\n" +
            "bio.occupation, bio.educationStatus, res.lgaOfResidence, res.stateOfResidence, en.clientCode, en.dateOfVisit, en.finalHivTestResult,\n" +
            "en.previouslyTestedThisYear, en.entryPoint, en.previouslyTestedNegative, en.facilitySetting AS testingSetting, en.typeOfSession,\n" +
            "en.pregnancyStatus, en.indexTesting, en.indexClientCode, en.indexRelationship, en.typeOfHivTestDone, en.recencyTest,\n" +
            "en.syphilisTestResult, en.hivTestKitsProvided, en.categoryOfClients, en.condomsProvided, en.source, en.longitude, en.latitude,\n" +
            "en.facilityId, en.completedBy, en.gonModalities, en.pepfarModalities, en.positiveUuid, pre.dateOfPreviousHts, pre.previousHtsResult,\n" +
            "htsCount.numberOfCounts, CAST(CASE WHEN htsCount.numberOfCounts > 1 THEN 'No' ELSE 'Yes' END AS TEXT ) AS firstTimeVisit FROM bio_data bio\n" +
            "INNER JOIN htsEncounter en ON en.patientUuid = bio.personUuid\n" +
            "LEFT JOIN previousHts pre ON pre.patient_uuid = en.patientUuid\n" +
            "LEFT JOIN htsCounts htsCount ON htsCount.patient_uuid = en.patientUuid\n" +
            "LEFT JOIN patientResidencial res ON res.personUuid11 = en.patientUuid\n";
}
