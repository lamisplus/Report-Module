WITH clientVerification AS (SELECT DISTINCT ON (h.person_uuid) h.person_uuid AS personUuid, 
CASE WHEN facility_state.name IS NULL THEN '' ELSE facility_state.name END AS facilityState, 
u.name AS facilityName, 
p.hospital_number AS hospitalNumber,
h.data->>'serialEnrollmentNo' AS serialEnrollmentNo, 
h.date_of_observation AS dateOfObservation, 
obj.value->>'dateOfAttempt' AS dateOfAttempt, 
obj.value->>'verificationAttempts' AS verificationAttempts, 
obj.value->>'verificationStatus' AS verificationStatus, 
obj.value->>'outcome' AS outcome, 
CASE WHEN pt.dsd_model IS NULL THEN '' ELSE pt.dsd_model END  AS dsdModel, 
obj.value->>'comment' AS comment, 
h.data->>'returnedToCare' AS returnedToCare, 
h.data->>'referredTo' AS referredTo, 
h.data->>'discontinuation' AS discontinuation, 
h.data->>'dateOfDiscontinuation' AS dateOfDiscontinuation, 
CASE WHEN pt.reason_for_discountinuation IS NULL THEN '' ELSE pt.reason_for_discountinuation END  AS reasonForDiscontinuation, 
COALESCE(string_agg(CAST(any_element.value AS text), ', '), '') AS anyOfTheFollowingList, 
ROW_NUMBER() OVER ( PARTITION BY h.person_uuid ORDER BY CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) DESC) AS rnk,
MAX(attemptsCounts.rnkk) AS noAttempts
FROM hiv_observation h 
JOIN base_organisation_unit u ON h.facility_id = u.id 
CROSS JOIN jsonb_array_elements(h.data->'attempt') as obj 
LEFT JOIN jsonb_array_elements_text(h.data->'anyOfTheFollowing') any_element ON true 
LEFT JOIN patient_person p ON p.uuid = h.person_uuid 
LEFT JOIN base_organisation_unit facility ON facility.id = p.facility_id 
LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id 
LEFT JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id 
LEFT JOIN hiv_patient_tracker pt ON pt.person_uuid = h.person_uuid
LEFT JOIN (
select person_uuid,
CAST (data->'attempt'->0->>'dateOfAttempt' AS DATE) AS dateOfOutcome,
ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) DESC) AS rnkk
from public.hiv_observation where type = 'Client Verification' 
AND archived = 0
) attemptsCounts ON attemptsCounts.person_uuid = h.person_uuid
WHERE h.type = 'Client Verification'  
--AND h.facility_id = ?1 
AND h.archived = 0 
GROUP BY 
h.id, h.person_uuid, h.date_of_observation, u.name, facility_state.name, pt.dsd_model, obj.value, 
h.data->>'serialEnrollmentNo', h.data->>'referredTo', 
h.data->>'discontinuation', h.data->>'returnedToCare', 
h.data->>'dateOfDiscontinuation', pt.reason_for_discountinuation, p.hospital_number)
SELECT * FROM clientVerification
where rnk = 1