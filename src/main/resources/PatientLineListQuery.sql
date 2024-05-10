-- change facility_id = 1959 to database facility_id


WITH bio_data AS (
  SELECT 
DISTINCT ON (p.uuid) p.uuid as personUuid, 
p.id, 
CAST(p.archived AS BOOLEAN) as archived, 
p.uuid, 
p.hospital_number as hospitalNumber, 
p.surname, 
p.first_name as firstName, 
EXTRACT(
  YEAR 
  from 
AGE(NOW(), date_of_birth)
) as age, 
p.other_name as otherName, 
p.sex as gender, 
p.date_of_birth as dateOfBirth, 
p.date_of_registration as dateOfRegistration, 
p.marital_status ->> 'display' as maritalStatus, 
education ->> 'display' as education, 
p.employment_status ->> 'display' as occupation, 
facility.name as facilityName, 
facility_lga.name as lga, 
facility_state.name as state, 
boui.code as datimId, 
res_state.name as residentialState, 
res_lga.name as residentialLga, 
r.address as address, 
p.contact_point -> 'contactPoint' -> 0 -> 'value' ->> 0 AS phone 
  FROM 
patient_person p 
INNER JOIN (
  SELECT 
* 
  FROM 
(
   SELECT
p.id,
CASE WHEN address_object->>'city' IS NOT NULL
 THEN CONCAT_WS(' ', address_object->>'city', 
REPLACE(REPLACE(COALESCE(NULLIF(address_object->>'line', '\\\\'), ''), ']', ''), '[', ''), 
NULLIF(NULLIF(address_object->>'stateId', 'null'), '')) 
 ELSE NULL 
END AS address,
CASE WHEN address_object->>'stateId' ~ '^\\d+(\\.\\d+)?$' 
 THEN address_object->>'stateId' 
 ELSE NULL 
END AS stateId,
CASE WHEN address_object->>'stateId' ~ '^\\d+(\\.\\d+)?$' 
 THEN address_object->>'district' 
 ELSE NULL 
END AS lgaId
FROM patient_person p
CROSS JOIN jsonb_array_elements(p.address->'address') AS l(address_object)
)  as result
 ) r ON r.id=p.id
 INNER JOIN base_organisation_unit facility ON facility.id=facility_id
 INNER JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id

 INNER JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id

 LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateId AS BIGINT)

 LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(CASE WHEN r.lgaId ~ E'^\\\\d+$' THEN r.lgaId ELSE NULL END AS BIGINT)

 INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=1959 AND boui.name='DATIM_ID'

 INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid
 WHERE p.archived=0 AND h.archived=0 AND h.facility_id=1959
),
 enrollment_details AS (
 SELECT h.person_uuid,h.unique_id as uniqueId,  sar.display as statusAtRegistration, date_confirmed_hiv as dateOfConfirmedHiv,

 ep.display as entryPoint, date_of_registration as dateOfRegistration
 FROM hiv_enrollment h
 LEFT JOIN base_application_codeset sar ON sar.id=h.status_at_registration_id
 LEFT JOIN base_application_codeset ep ON ep.id=h.entry_point_id
 WHERE h.archived=0 AND h.facility_id=1959
 ),
 laboratory_details AS ( SELECT DISTINCT ON (lo.patient_uuid)
    lo.patient_uuid AS person_uuid,
    ll.lab_test_name AS test,
    bac_viral_load.display AS viralLoadType,
    ls.date_sample_collected AS dateSampleCollected,
    lr.result_reported AS lastViralLoad,
    lr.date_result_reported AS dateOfLastViralLoad
FROM
    laboratory_order lo
        INNER JOIN hiv_enrollment h ON h.person_uuid = lo.patient_uuid
        LEFT JOIN laboratory_test lt ON lt.lab_order_id = lo.id
        LEFT JOIN laboratory_labtest ll ON ll.id = lt.lab_test_id AND ll.lab_test_name = 'Viral Load'

        LEFT JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.patient_uuid = lo.patient_uuid

        LEFT JOIN laboratory_result lr ON lr.test_id = lt.id AND lr.patient_uuid = lo.patient_uuid

        LEFT JOIN base_application_codeset bac_viral_load ON bac_viral_load.id = lt.viral_load_indication

WHERE
    lo.archived = 0
  AND h.archived = 0
  AND lo.facility_id = 1959
ORDER BY
    lo.patient_uuid, lo.order_date DESC
 ),
 pharmacy_details AS (
 SELECT DISTINCT ON (hartp.person_uuid)hartp.person_uuid as person_uuid, r.visit_date as dateOfLastRefill,

 hartp.next_appointment as dateOfNextRefill, hartp.refill_period as lastRefillDuration,

 hartp.dsd_model_type as DSDType, r.description as currentRegimenLine, r.regimen_name as currentRegimen,

 (CASE
 WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '
 OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.hiv_status
 WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' < CURRENT_DATE
 THEN ' IIT ' ELSE ' ACTIVE '
 END)AS currentStatus,
 (CASE
 WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '
 OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.status_date
 WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' <= CURRENT_DATE
 THEN CAST((hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ') AS date) ELSE hartp.visit_date

 END)AS dateOfCurrentStatus
 FROM hiv_art_pharmacy hartp
 INNER JOIN (SELECT distinct r.* FROM (SELECT h.person_uuid, h.visit_date, CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name,

 hrt.description FROM hiv_art_pharmacy h,
 jsonb_array_elements(h.extra->'regimens') with ordinality p(pharmacy_object)
 INNER JOIN hiv_regimen hr ON hr.description=CAST(pharmacy_object ->> 'regimenName' AS VARCHAR)

 INNER JOIN hiv_regimen_type hrt ON hrt.id=hr.regimen_type_id
 WHERE hrt.id IN (1,2,3,4,14))r
 INNER JOIN (SELECT hap.person_uuid, MAX(visit_date) AS MAXDATE FROM hiv_art_pharmacy hap

 INNER JOIN hiv_enrollment h ON h.person_uuid=hap.person_uuid  WHERE h.archived=0
 GROUP BY hap.person_uuid ORDER BY MAXDATE ASC ) max ON
 max.MAXDATE=r.visit_date AND r.person_uuid=max.person_uuid) r
 ON r.visit_date=hartp.visit_date AND r.person_uuid=hartp.person_uuid
 INNER JOIN hiv_enrollment he ON he.person_uuid=r.person_uuid
 LEFT JOIN (SELECT sh1.person_id, sh1.hiv_status, sh1.status_date
 FROM hiv_status_tracker sh1
 INNER JOIN
 (
SELECT person_id as p_id, MAX(hst.id) AS MAXID
FROM hiv_status_tracker hst INNER JOIN hiv_enrollment h ON h.person_uuid=person_id

GROUP BY person_id
 ORDER BY person_id ASC
 ) sh2 ON sh1.person_id = sh2.p_id AND sh1.id = sh2.MAXID
 ORDER BY sh1.person_id ASC) stat ON stat.person_id=hartp.person_uuid
 WHERE he.archived=0 AND hartp.archived=0 AND hartp.facility_id=1959 ORDER BY hartp.person_uuid ASC

 ),
 art_commencement_vitals AS (
 SELECT DISTINCT ON (tvs.person_uuid) tvs.person_uuid , body_weight as baseLineWeight, height as baseLineHeight,

 CONCAT(diastolic, ' / ', systolic) as baseLineBp, diastolic as diastolicBp,
 systolic as systolicBp, clinical_stage.display as baseLineClinicalStage,
 func_status.display as baseLineFunctionalStatus,
 hv.description as firstRegimen, hrt.description as firstRegimenLine,
 CASE WHEN cd_4=0 THEN null ELSE cd_4 END  AS baseLineCd4,
 CASE WHEN cd_4_percentage=0 THEN null ELSE cd_4_percentage END AS cd4Percentage,
 hac.visit_date as artStartDate
 FROM triage_vital_sign tvs
 INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid
 AND hac.is_commencement=true AND hac.person_uuid = tvs.person_uuid
 INNER JOIN hiv_enrollment h ON hac.hiv_enrollment_uuid = h.uuid AND hac.person_uuid=tvs.person_uuid

 INNER JOIN patient_person p ON p.uuid=h.person_uuid
 RIGHT JOIN hiv_regimen hv ON hv.id=hac.regimen_id
 RIGHT JOIN hiv_regimen_type hrt ON hrt.id=hac.regimen_type_id
 RIGHT JOIN base_application_codeset clinical_stage ON clinical_stage.id=hac.clinical_stage_id

 RIGHT JOIN base_application_codeset func_status ON func_status.id=hac.functional_status_id

   WHERE hac.archived=0  AND h.archived=0 AND h.facility_id=1959
 ),
             current_clinical AS (
 SELECT tvs.person_uuid, hac.adherence_level as adherenceLevel, hac.next_appointment as dateOfNextClinic, body_weight as currentWeight, height as currentHeight,

  diastolic as currentDiastolic, systolic as currentSystolic, bac.display as currentClinicalStage,

  CONCAT(diastolic, ' / ', systolic) as currentBp, current_clinical_date.MAXDATE as dateOfLastClinic

 FROM triage_vital_sign tvs
 INNER JOIN ( SELECT person_uuid, MAX(capture_date) AS MAXDATE FROM triage_vital_sign

 GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_triage
 ON current_triage.MAXDATE=tvs.capture_date AND current_triage.person_uuid=tvs.person_uuid

 INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid
 INNER JOIN ( SELECT person_uuid, MAX(hac.visit_date) AS MAXDATE FROM hiv_art_clinical hac

 GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_clinical_date
 ON current_clinical_date.MAXDATE=hac.visit_date AND current_clinical_date.person_uuid=hac.person_uuid

 INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid
 INNER JOIN base_application_codeset bac ON bac.id=hac.clinical_stage_id
 WHERE hac.archived=0 AND he.archived=0 AND he.facility_id=1959
 )
             SELECT
             DISTINCT ON (b.personUuid)b.personUuid AS personUuid,
             b.archived,
             b.hospitalNumber,
             b.surname,
             b.firstName,
             b.age,
             b.otherName,
             b.gender,
             b.dateOfBirth,
             b.maritalStatus,
             b.education,
             b.occupation,
             b.facilityName,
             b.lga,
             b.state,
             b.datimId,
             b.residentialState,
             b.residentialLga,
             b.address,
             b.phone,
             c.currentWeight,
             c.currentHeight,
             c.currentDiastolic as currentDiastolicBp,
             c.currentSystolic as currentSystolicBP,                
             c.currentBp,
             c.dateOfLastClinic,
             c.dateOfNextClinic,
             c.adherenceLevel,
             c.currentClinicalStage as lastClinicStage,
             e.statusAtRegistration,
             e.dateOfConfirmedHiv as dateOfConfirmedHIVTest,
             e.entryPoint as careEntryPoint,
             e.uniqueId,
             e.dateOfRegistration,
             p.dateOfNextRefill,
             p.lastRefillDuration,
             p.dateOfLastRefill,
             p.DSDType,
             p.currentRegimen,
             p.currentRegimenLine,
             p.currentStatus,
             p.dateOfCurrentStatus as dateOfCurrentStatus,
             l.test,                  
             l.viralLoadType,
             l.dateSampleCollected as dateOfSampleCollected ,
             l.lastViralLoad,
             l.dateOfLastViralLoad,
             a.baseLineWeight,
             a.baseLineHeight,
             a.baseLineBp,
             a.diastolicBp,                       
             a.systolicBp,
             a.baseLineClinicalStage as baselineClinicStage,
             a.baseLineFunctionalStatus,                      
             a.firstRegimen,
             a.firstRegimenLine,
             a.baseLineCd4,                      
             a.cd4Percentage,                          
             a.artStartDate
             FROM enrollment_details e
             INNER JOIN bio_data b ON e.person_uuid=b.personUuid
             LEFT JOIN art_commencement_vitals a ON a.person_uuid=e.person_uuid
             LEFT JOIN pharmacy_details p ON p.person_uuid=e.person_uuid
             LEFT JOIN laboratory_details l ON l.person_uuid=e.person_uuid
             LEFT JOIN current_clinical c ON c.person_uuid=e.person_uuid