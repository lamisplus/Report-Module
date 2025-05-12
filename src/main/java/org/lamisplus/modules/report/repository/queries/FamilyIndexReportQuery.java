package org.lamisplus.modules.report.repository.queries;

public class FamilyIndexReportQuery {

    public static final String FAMILY_INDEX_REPORT_QUERY = "WITH familyIndex AS (\n" +
            "    SELECT \n" +
            "        facility.name AS facilityName, \n" +
            "        facility_state.name AS state, \n" +
            "        facility_lga.name AS lga,  \n" +
            "        p.uuid AS PersonUuid, \n" +
            "\t\tp.facility_id AS facilityId,\n" +
            "        p.hospital_number AS hospitalNumber,\n" +
            "        EXTRACT(YEAR FROM AGE(NOW(), p.date_of_birth)) AS age,  \n" +
            "        boui.code AS datimId, \n" +
            "        p.date_of_birth AS dateOfBirth,\n" +
            "        INITCAP(p.sex) AS sex, \n" +
            "        CONCAT(p.first_name, ' ', p.surname) AS patientName, \n" +
            "        p.other_name AS otherName,\n" +
            "        (select display from base_application_codeset where id = CAST (fhts.marital_status AS INTEGER)) AS maritalStatus,\n" +
            "        fhts.date_index_client_confirmed_hiv_positive_test_result AS dateConfirmedHiv,\n" +
            "\t    fhts.visit_date AS dateOfferIndex, \n" +
            "\t    fhts.date_client_enrolled_on_treatment AS dateEnrolled,\n" +
            "        fhts.recency_testing AS recencyTesting, \n" +
            "        fhts.willing_to_have_children_tested_else_where AS acceptedTesting, \n" +
            "\t\t(select display from base_application_codeset where code = hts_rst.entry_point) AS entryPoint,\n" +
            "        hfi.age AS elicitedAge,\n" +
            "\t\t'' AS elicitedClientName,\n" +
            "        (select display from base_application_codeset where code = hft.tracker_sex) AS elicitedClientSex, \n" +
            "\t\t'' AS elicitedClientAddress,\n" +
            "\t\t'' AS elicitedClientPhoneNumber,\n" +
            "        hft.date_tested AS elicitedClientTestedHiv, \n" +
            "\t\thft.hiv_test_result AS elicitedClientHivResult,\n" +
            "        hft.date_enrolledonart AS elicitedClientDateEnrolled,\n" +
            "        (select display from base_application_codeset where code = hfi.family_relationship) AS relationshipWithIndex,\n" +
            "\t\t'' AS modeOfNotification,\n" +
            "\t\thft.known_hiv_positive AS elicitedClientKnownPositive, \n" +
            "        hft.date_visit AS dateOfElicitation, \n" +
            "        '' AS elicitedClientUniqueId, \n" +
            "        hft.date_enrolled_in_ovc AS dateEnrolledInOvc, hfi.contact_id AS contactId,\n" +
            "        hft.ovc_id AS ovcId, (select display from base_application_codeset where code = hft.attempt) AS noOfAttempts\n" +
            "    FROM hts_family_index hfi\n" +
            "    JOIN hts_family_index_testing fhts ON fhts.uuid = hfi.family_index_testing_uuid\n" +
            "    JOIN hts_client hts ON hts.uuid = fhts.hts_client_uuid\n" +
            "\tLEFT JOIN hts_risk_stratification hts_rst ON hts.risk_stratification_code = hts_rst.code\n" +
            "    JOIN hts_family_index_testing_tracker hft ON hft.family_index_uuid = hfi.uuid\n" +
            "    JOIN patient_person p ON p.uuid = hts.person_uuid\n" +
            "    --JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            "    LEFT JOIN base_organisation_unit facility ON facility.id = p.facility_id\n" +
            "    LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "    LEFT JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "    LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = p.facility_id AND boui.name = 'DATIM_ID'\n" +
            "\tWHERE hfi.archived = 0\n" +
            "),\n" +
            "partnerIndex AS (\n" +
            "    SELECT \n" +
            "        facility.name AS facilityName, \n" +
            "        facility_state.name AS state, \n" +
            "        facility_lga.name AS lga,  \n" +
            "        p.uuid AS PersonUuid, \n" +
            "\t\tp.facility_id AS facilityId,\n" +
            "        p.hospital_number AS hospitalNumber,\n" +
            "        EXTRACT(YEAR FROM AGE(NOW(), p.date_of_birth)) AS age,  \n" +
            "        boui.code AS datimId, \n" +
            "        p.date_of_birth AS dateOfBirth,\n" +
            "        INITCAP(p.sex) AS sex, \n" +
            "        CONCAT(p.first_name, ' ', p.surname) AS patientName, \n" +
            "        p.other_name AS otherName,\n" +
            "        (select display from base_application_codeset where id = CAST (fhts.marital_status AS INTEGER))  AS maritalStatus,\n" +
            "        fhts.date_index_client_confirmed_hiv_positive_test_result AS dateConfirmedHiv,\n" +
            "        fhts.visit_date AS dateOfferIndex,\n" +
            "        fhts.date_client_enrolled_on_treatment AS dateEnrolled,\n" +
            "        fhts.recency_testing AS recencyTesting, \n" +
            "        pns.accepted_pns AS acceptedTesting, \n" +
            "\t\t(select display from base_application_codeset where code = hts_rst.entry_point) AS entryPoint,\n" +
            "        CASE WHEN pns.hts_client_information->>'partnerAge' ~ '^[0-9]+$' THEN CAST(pns.hts_client_information->>'partnerAge' AS INTEGER) ELSE 0 END AS elicitedAge,\n" +
            "        pns.hts_client_information->>'partnerName' AS elicitedClientName, \n" +
            "        (select display from base_application_codeset where id = CASE WHEN pns.hts_client_information->>'partnerSex' ~ '^[0-9]+$' THEN CAST(pns.hts_client_information->>'partnerSex' AS INTEGER) ELSE 0 END) AS elicitedClientSex, \n" +
            "        pns.hts_client_information->>'partnerAddress' AS elicitedClientAddress, \n" +
            "        pns.contact_tracing->>'partnerPhoneNumber' AS elicitedClientPhoneNumber,\n" +
            "        pns.date_partner_tested AS elicitedClientTestedHiv, \n" +
            "        pns.hiv_test_result AS elicitedClientHivResult, \n" +
            "        pns.date_enrollment_on_art AS elicitedClientDateEnrolled,\n" +
            "        (select display from base_application_codeset where id = CASE WHEN pns.hts_client_information->>'relativeToIndexClient' ~ '^[0-9]+$' THEN CAST(pns.hts_client_information->>'relativeToIndexClient' AS INTEGER) ELSE 0 END) AS relationshipWithIndex,\n" +
            "        (select display from base_application_codeset where id = CASE WHEN pns.notification_method ~ '^[0-9]+$' THEN CAST(pns.notification_method AS INTEGER) ELSE 0 END ) AS modeOfNotification, \n" +
            "        pns.known_hiv_positive AS elicitedClientKnownPositive, \n" +
            "\t\tCAST (pns.date_of_elicitation AS DATE) AS dateOfElicitation,\n" +
            "        '' AS elicitedClientUniqueId,\n" +
            "        CAST (null AS DATE) AS dateEnrolledInOvc, pns.partner_id AS contactId,\n" +
            "        '' AS ovcId, '' AS noOfAttempts\n" +
            "    FROM hts_pns_index_client_partner pns\n" +
            "    JOIN hts_family_index_testing fhts ON fhts.hts_client_uuid = pns.hts_client_uuid\n" +
            "    JOIN hts_client hts ON hts.uuid = pns.hts_client_uuid\n" +
            "    JOIN patient_person p ON p.uuid = hts.person_uuid\n" +
            "\tLEFT JOIN hts_risk_stratification hts_rst ON hts.risk_stratification_code = hts_rst.code\n" +
            "   -- JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            "    LEFT JOIN base_organisation_unit facility ON facility.id = pns.facility_id\n" +
            "    LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "    LEFT JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "    LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = pns.facility_id AND boui.name = 'DATIM_ID'\n" +
            "\tWHERE pns.archived = 0 AND pns.hts_client_information->>'partnerAge' != '' AND pns.hts_client_information->>'partnerSex' !=''\n" +
            ")\n" +
            "SELECT * FROM (\n" +
            "SELECT * FROM familyIndex\n" +
            "UNION ALL\n" +
            "SELECT * FROM partnerIndex\n" +
            "\t) allReport\n" +
            "WHERE facilityId = ?1\n" +
            "ORDER BY hospitalNumber;\n";
}
