package org.lamisplus.modules.report.repository.queries;

public class FamilyIndexReportQuery {

    public static final String FAMILY_INDEX_REPORT_QUERY = "WITH htsEncounter AS (\n" +
            "SELECT CAST(he.patient_uuid AS TEXT) AS patientUuid, hec.client_code clientCode, he.date_of_service dateOfService, basEntry.display AS entryPoint, basCategory.display AS clientCategory,\n" +
            "basOfferedPns.display AS offeredPns, basAcceptedPns.display AS acceptedPns, hc.contact_code,\n" +
            "CONCAT(hc.first_name, ' ', hc.middle_name, ' ', hc.surname) AS nameOfIndexClient, basRelationship.display AS relationshipToIndex, basSex.display AS sex,\n" +
            "hc.age, hc.phone, hc.address, basNotification.display AS notificationMethod, hc.attempts, basKnownHiv.display AS knownHivPositive, hc.date_tested_hiv dateTestedHiv,\n" +
            "basHivResult.display AS hivTestResult, hc.date_enrolled_art dateEnrolledArt, hc.on_art onArt, '' AS uan, hc.date_enrolled_ovc dateEnrolledOvc, hc.ovc_id ovcId\n" +
            "FROM hts_ict_encounter he\n" +
            "LEFT JOIN hts_ict_contact hc ON hc.ict_encounter_id = he.id\n" +
            "LEFT JOIN hts_encounter hec ON hec.uuid = he.hts_encounter_uuid\n" +
            "LEFT JOIN base_application_codeset basEntry ON basEntry.code = he.setting\n" +
            "LEFT JOIN base_application_codeset basCategory ON basCategory.code = he.client_category\n" +
            "LEFT JOIN base_application_codeset basOfferedPns ON basOfferedPns.code = he.offered_pns\n" +
            "LEFT JOIN base_application_codeset basAcceptedPns ON basAcceptedPns.code = he.accepted_pns\n" +
            "LEFT JOIN base_application_codeset basRelationship ON basRelationship.code = hc.relationship_to_index\n" +
            "LEFT JOIN base_application_codeset basSex ON basSex.code = hc.sex\n" +
            "LEFT JOIN base_application_codeset basNotification ON basNotification.code = hc.notification_method\n" +
            "LEFT JOIN base_application_codeset basKnownHiv ON basKnownHiv.code = hc.known_hiv_positive\n" +
            "LEFT JOIN base_application_codeset basHivResult ON basHivResult.code = hc.hiv_test_result\n" +
            "WHERE he.archived IS FALSE AND hc.archived IS FALSE AND he.facility_id = ?1\n" +
            "),\n" +
            "bio_data AS (\n" +
            "SELECT DISTINCT ON (p.uuid) p.uuid AS personUuid, p.hospital_number AS hospitalNumber,\n" +
            "CAST(EXTRACT(YEAR FROM AGE(CAST(NOW() AS DATE), p.date_of_birth)) AS INTEGER) AS age, INITCAP(p.sex) AS gender,\n" +
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
            "case when (addr ~ '^[0-9\\\\.]+$') =TRUE\n" +
            " then (select name from base_organisation_unit where id = cast(addr as int)) ELSE\n" +
            "(select name from base_organisation_unit where id = cast(facilityLga as int)) end as lgaOfResidence,\n" +
            "(select name from base_organisation_unit where id = (CASE WHEN stateResidence ~ '^[0-9]+$' THEN CAST(stateResidence AS INTEGER) ELSE null END)) AS stateOfResidence\n" +
            "from (\n" +
            " select CAST(pp.uuid AS UUID) AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga, (jsonb_array_elements(pp.address->'address')->>'stateId') stateResidence, (jsonb_array_elements(pp.address->'address')->>'district') as addr from patient_person pp\n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER)\n" +
            ") dt)\n" +
            "SELECT bio.state, bio.lga, bio.facilityName, bio.datimId, bio.surname, bio.firstName,  bio.hospitalNumber,\n" +
            "het.*\n" +
            "FROM bio_data bio\n" +
            "INNER JOIN htsEncounter het ON het.patientUuid = bio.personUuid";
}
