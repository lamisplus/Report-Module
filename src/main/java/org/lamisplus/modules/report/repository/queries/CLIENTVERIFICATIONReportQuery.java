package org.lamisplus.modules.report.repository.queries;

public class CLIENTVERIFICATIONReportQuery {

    public static final String CLIENT_VERIFICATION_QUERY = "WITH clientVerification AS (SELECT DISTINCT ON (h.person_uuid) h.person_uuid AS personUuid, \n" +
            "CASE WHEN facility_state.name IS NULL THEN '' ELSE facility_state.name END AS facilityState, \n" +
            "u.name AS facilityName, \n" +
            "p.hospital_number AS hospitalNumber,\n" +
            "h.data->>'serialEnrollmentNo' AS serialEnrollmentNo, \n" +
            "h.date_of_observation AS dateOfObservation, \n" +
            "obj.value->>'dateOfAttempt' AS dateOfAttempt, \n" +
            "obj.value->>'verificationAttempts' AS verificationAttempts, \n" +
            "obj.value->>'verificationStatus' AS verificationStatus, \n" +
            "obj.value->>'outcome' AS outcome, \n" +
            "CASE WHEN pt.dsd_model IS NULL THEN '' ELSE pt.dsd_model END  AS dsdModel, \n" +
            "obj.value->>'comment' AS comment, \n" +
            "h.data->>'returnedToCare' AS returnedToCare, \n" +
            "h.data->>'referredTo' AS referredTo, \n" +
            "h.data->>'discontinuation' AS discontinuation, \n" +
            "h.data->>'dateOfDiscontinuation' AS dateOfDiscontinuation, \n" +
            "CASE WHEN pt.reason_for_discountinuation IS NULL THEN '' ELSE pt.reason_for_discountinuation END  AS reasonForDiscontinuation, \n" +
            "COALESCE(string_agg(CAST(any_element.value AS text), ', '), '') AS anyOfTheFollowingList, \n" +
            "ROW_NUMBER() OVER ( PARTITION BY h.person_uuid ORDER BY CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) DESC) AS rnk,\n" +
            "MAX(attemptsCounts.rnkk) AS noAttempts, cvTriggers.*\n" +
            "FROM hiv_observation h \n" +
            "JOIN base_organisation_unit u ON h.facility_id = u.id \n" +
            "CROSS JOIN jsonb_array_elements(h.data->'attempt') as obj \n" +
            "LEFT JOIN jsonb_array_elements_text(h.data->'anyOfTheFollowing') any_element ON true \n" +
            "LEFT JOIN patient_person p ON p.uuid = h.person_uuid \n" +
            "LEFT JOIN base_organisation_unit facility ON facility.id = p.facility_id \n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id \n" +
            "LEFT JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id \n" +
            "LEFT JOIN hiv_patient_tracker pt ON pt.person_uuid = h.person_uuid\n" +
            "LEFT JOIN (\n" +
            "select person_uuid,\n" +
            "CAST (data->'attempt'->0->>'dateOfAttempt' AS DATE) AS dateOfOutcome,\n" +
            "ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) DESC) AS rnkk\n" +
            "from public.hiv_observation where type = 'Client Verification' \n" +
            "AND archived = 0\n" +
            ") attemptsCounts ON attemptsCounts.person_uuid = h.person_uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid,\n" +
            "MAX(CASE WHEN rn = 1 AND 'No initial fingerprint was captured' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS noInitBiometric,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Duplicated demographic and clinical variables' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS duplicatedDemographic,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Records of repeated clinical encounters, with no fingerprint recapture.' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS noRecapture,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Last clinical visit is over 15 months prior' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS lastVisitIsOver18M,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Incomplete visit data on the care card or pharmacy forms or EMR ' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS incompleteVisitData,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Records with same services e.g ART start date and at least 3 consecutive last ART pickup dats, VL result etc' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS repeatEncounterNoPrint,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Long intervals between ARV pick-ups (pick-ups more than one year apart in the same facility)' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS longIntervalsARVPickup,\n" +
            "-- MAX(CASE WHEN rn = 1 AND 'Long intervals between ARV pick-ups (pick-ups more than one year apart in the same facility)' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'Long intervals between ARV pick-ups (pick-ups more than one year apart in the same facility' END) AS batchPickupDates,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Same sex, DOB and ART start date' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS sameSexDOBARTStartDate,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Consistently had drug pickup by proxy without viral load sample collection for two quarters' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS pickupByProxy,\n" +
            "MAX(CASE WHEN rn = 1 AND 'Others' IN (SELECT jsonb_array_elements_text(anyThing)) THEN 'YES' ELSE 'NO' END) AS otherSpecifyForCV\n" +
            "FROM (\n" +
            "SELECT person_uuid,\n" +
            "data->'anyOfTheFollowing' AS anyThing,\n" +
            "date_of_observation,\n" +
            "data,\n" +
            "ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) DESC) AS rn\n" +
            "FROM hiv_observation ho\n" +
            "LEFT JOIN patient_person pp ON pp.uuid = ho.person_uuid\n" +
            "WHERE type = 'Client Verification'\n" +
            "AND pp.archived = 0\n" +
            "AND ho.archived = 0\n" +
            "AND pp.facility_id = ?1\n" +
            ") cc\n" +
            "GROUP BY person_uuid\n" +
            ") cvTriggers ON cvTriggers.person_uuid = h.person_uuid\n" +
            "WHERE h.type = 'Client Verification'  \n" +
            "AND h.facility_id = ?1 \n" +
            "AND h.archived = 0 \n" +
            "GROUP BY \n" +
            "h.id, h.person_uuid, h.date_of_observation, u.name, facility_state.name, pt.dsd_model, obj.value, \n" +
            "h.data->>'serialEnrollmentNo', h.data->>'referredTo', \n" +
            "h.data->>'discontinuation', h.data->>'returnedToCare', \n" +
            "h.data->>'dateOfDiscontinuation', pt.reason_for_discountinuation, p.hospital_number, cvTriggers.person_uuid,\n" +
            "cvTriggers.noInitBiometric, cvTriggers.noRecapture, cvTriggers.duplicatedDemographic, cvTriggers.lastVisitIsOver18M,\n" +
            "cvTriggers.incompleteVisitData, cvTriggers.repeatEncounterNoPrint,cvTriggers.longIntervalsARVPickup, cvTriggers.sameSexDOBARTStartDate,\n" +
            "cvTriggers.pickupByProxy, cvTriggers.otherSpecifyForCV)\n" +
            "SELECT * FROM clientVerification\n" +
            "where rnk = 1";
}
