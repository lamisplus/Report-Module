WITH bio_data AS (SELECT DISTINCT (p.uuid) AS personUuid,
                                  facility_state.name AS state,
                                  facility_lga.name AS lga,
                                  p.facility_id as facilityId,
                                  facility.name AS facilityName,
                                  p.uuid as patientId,
                                  p.hospital_number AS hospitalNumber,
                                  h.unique_id as uniqueId,
                                  p.surname,
                                  p.other_name,
                                  p.date_of_birth AS dateOfBirth,
                                  EXTRACT(YEAR FROM  AGE(NOW(), date_of_birth)) AS age,
                                  INITCAP(p.sex) AS Sex,
                                  p.marital_status ->> 'display' as maritalStatus,
    p.education ->> 'display' as education,
    p.employment_status ->> 'display' as occupation,
    p.address -> 'address' -> 0 -> 'line' ->> 0 as address,
    p.contact_point -> 'contactPoint' -> 0 -> 'value' as phoneNumber,
    boui.code AS datimId,
    tgroup.display AS targetGroup,
    eSetting.display AS enrollmentSetting,
    hac.visit_date AS artStartDate,
    hr.description AS regimenAtStart,
    p.date_of_registration as dateOfRegistration,
    h.date_of_registration as dateOfEnrollment,
    h.ovc_number AS ovcUniqueId,
    h.house_hold_number AS householdUniqueNo,
    ecareEntry.display AS careEntry,
    hrt.description AS regimenLineAtStart
FROM patient_person p
    INNER JOIN base_organisation_unit facility ON facility.id = facility_id
    INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id
    INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id
    INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID'
    INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid
    LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id
    LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id
    LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id
    INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid
    AND hac.archived = 0
    INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id
    INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id
WHERE
    h.archived = 0
  AND p.archived = 0
  AND h.facility_id = ?1
  AND hac.is_commencement = TRUE
  AND hac.visit_date >= ?2
  AND hac.visit_date < ?3
    ),

--Residence lga and state
    patient_residence AS (SELECT DISTINCT ON (personUuid)
    personUuid AS personUuid11,
    CASE WHEN (lgaAddr ~ '^[0-9\\.]+$') = TRUE
    THEN (SELECT name FROM base_organisation_unit WHERE id = cast(lgaAddr AS INT))
    ELSE (SELECT name FROM base_organisation_unit WHERE id = cast(facilityLga AS INT)) END AS lgaOfResidence,
    CASE WHEN (stateAddr ~ '^[0-9\\.]+$') = TRUE
    THEN (SELECT name FROM base_organisation_unit WHERE id = cast(stateAddr AS INT))
    ELSE (SELECT name FROM base_organisation_unit WHERE id = cast(facilityLga AS INT)) END AS stateOfResidence
FROM (SELECT pp.uuid AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga,
    (jsonb_array_elements(pp.address->'address')->>'district') AS lgaAddr,
    (jsonb_array_elements(pp.address->'address')->>'stateId') AS stateAddr
    FROM patient_person pp
    LEFT JOIN base_organisation_unit facility_lga
    ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER))
    dt),
    pregnancy_status as (
select distinct on (person_uuid) person_uuid, visit_date,
    case
    when pregnancy_status = 'PREGANACY_STATUS_PREGNANT' OR pregnancy_status = 'Pregnant' THEN 'Pregnant'
    end as pregnancyStatus
from hiv_art_clinical order by person_uuid, visit_date desc
    ),
    pharmacy_details_regimen AS (SELECT * FROM
    (SELECT *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid40 ORDER BY pr1.lastPickupDate DESC) AS rnk3
    FROM
    (SELECT p.person_uuid AS person_uuid40,
    COALESCE(ds_model.display, p.dsd_model_type) AS dsdModel,
    p.visit_date AS lastPickupDate,
    r.description AS currentARTRegimen,
    rt.description AS currentRegimenLine,
    p.next_appointment AS nextPickupDate,
    CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfARVRefill
    FROM public.hiv_art_pharmacy p
    INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = p.id
    INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id
    INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id
    LEFT JOIN base_application_codeset ds_model on ds_model.code = p.dsd_model_type
    WHERE r.regimen_type_id in (1,2,3,4,14)
    AND  p.archived = 0
    AND  p.facility_id = ?1
    AND  p.visit_date >= ?2
    AND  p.visit_date  < ?3) AS pr1
    ) AS pr2
WHERE pr2.rnk3 = 1
    ),
    current_status AS (
SELECT DISTINCT ON (pharmacy.person_uuid)
    pharmacy.person_uuid AS cuPersonUuid,
    (
    CASE
    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'
    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'
    OR stat.hiv_status ILIKE '%out%'
    OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.hiv_status
    ELSE pharmacy.status
    END
    ) AS status,
    (
    CASE
    WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN stat.status_date
    WHEN (stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%'
    OR stat.hiv_status ILIKE '%out%'
    OR stat.hiv_status ILIKE '%Invalid %')) THEN stat.status_date
    ELSE pharmacy.visit_date
    END
    ) AS status_date
FROM (
    SELECT
    (
    CASE
    WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN 'IIT'
    ELSE 'Active'
    END
    ) status,
    (
    CASE
    WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' < ?3 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'
    ELSE hp.visit_date
    END
    ) AS visit_date,
    hp.person_uuid, MAXDATE
    FROM
    hiv_art_pharmacy hp
    INNER JOIN (
    SELECT hap.person_uuid, hap.visit_date AS MAXDATE,
    ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3
    FROM public.hiv_art_pharmacy hap
    INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id
    INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0
    INNER JOIN public.hiv_regimen r ON r.id = pr.regimens_id
    INNER JOIN public.hiv_regimen_type rt ON rt.id = r.regimen_type_id
    WHERE r.regimen_type_id IN (1,2,3,4,14)
    AND hap.archived = 0
    AND hap.visit_date < ?3
    ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid AND MAX.rnkkk3 = 1
    WHERE hp.archived = 0 AND hp.visit_date < ?3
    ) pharmacy
    LEFT JOIN (
    SELECT
    hst.hiv_status,
    hst.person_id,
    hst.status_date
    FROM (
    SELECT * FROM (
    SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death, hiv_status,
    ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC) AS row_number
    FROM hiv_status_tracker WHERE archived = 0 AND status_date <= ?3
    ) s
    WHERE s.row_number = 1
    ) hst
    INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id
    WHERE hst.status_date < ?3
    ) stat ON stat.person_id = pharmacy.person_uuid),
    prev_hypertensive AS (
SELECT * FROM (
    SELECT DISTINCT ON (person_uuid)
    person_uuid AS personUuid,
    MIN(date_of_observation) AS date_prev_hypertensive
    FROM
    hiv_observation
    WHERE
    type = 'Chronic Care'
    AND (data->'chronicCondition'->>'firstTimeHypertensive' = 'Yes')
    AND archived = 0
    GROUP BY
    person_uuid
    ORDER BY
    person_uuid,
    date_prev_hypertensive
    ) AS ph
    ),
    new_hypertensive AS (
SELECT * FROM (
    SELECT DISTINCT ON (person_uuid)
    person_uuid AS personUuid,
    MAX(date_of_observation) AS date_newly_hypertensive
    FROM
    hiv_observation
    WHERE
    type = 'Chronic Care'
    AND (data->'chronicCondition'->>'hypertensive' = 'Yes')
    AND archived = 0
    GROUP BY
    person_uuid
    ORDER BY
    person_uuid,
    date_newly_hypertensive desc
    ) AS nh
    )
select * from bio_data bd
                  left join patient_residence pr on bd.personUuid = pr.personUuid11
                  left join pregnancy_status ps on bd.personUuid = ps.person_uuid
                  left join pharmacy_details_regimen pdr on pdr.person_uuid40 = bd.personUuid
                  left join current_status cs on cs.cuPersonUuid = bd.personUuid
                  left join prev_hypertensive prev_hyp on prev_hyp.personUuid = bd.personUuid
                  left join new_hypertensive new_hyp on new_hyp.personUuid = bd.personUuid;