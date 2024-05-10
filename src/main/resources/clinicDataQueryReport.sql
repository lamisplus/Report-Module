SELECT  DISTINCT (p.uuid) AS patientId, 
p.hospital_number AS hospitalNumber, 
EXTRACT( 
YEAR 
FROM 
AGE(NOW(), date_of_birth) 
) AS age, 
INITCAP(p.sex) AS gender, 
p.date_of_birth AS dateOfBirth, 
facility.name AS facilityName, 
facility_lga.name AS lga, 
facility_state.name AS state, 
boui.code AS datimId, 
    tvs.*, 
    tvs.body_weight as BodyWeight,  
   (CASE
    WHEN hac.pregnancy_status = 'Not Pregnant' THEN hac.pregnancy_status
    WHEN hac.pregnancy_status = 'Pregnant' THEN hac.pregnancy_status
    WHEN hac.pregnancy_status = 'Breastfeeding' THEN hac.pregnancy_status
    WHEN hac.pregnancy_status = 'Post Partum' THEN hac.pregnancy_status
    WHEN preg.display IS NOT NULL THEN hac.pregnancy_status
    ELSE NULL END ) AS pregnancyStatus, 
    hac.next_appointment as nextAppointment , 
    hac.visit_date as visitDate, 
    funStatus.display as funtionalStatus, 
    clnicalStage.display as clinicalStage, 
    tbStatus.display as tbStatus 
    FROM 
 patient_person p 
       INNER JOIN base_organisation_unit facility ON facility.id = facility_id 
       INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id 
      
       INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id 
       INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID' 
       INNER JOIN hiv_art_clinical hac ON hac.person_uuid = p.uuid  
    LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status
   INNER JOIN base_application_codeset funStatus ON funStatus.id = hac.functional_status_id   
   INNER JOIN base_application_codeset clnicalStage ON clnicalStage.id = hac.clinical_stage_id 
   INNER JOIN base_application_codeset tbStatus ON tbStatus.id = CAST(regexp_replace(hac.tb_status, '[^0-9]', '', 'g') AS INTEGER)    
   INNER JOIN triage_vital_sign tvs ON tvs.uuid = hac.vital_sign_uuid 
       AND hac.archived = 0 
       WHERE   hac.archived = 0 
  -- AND hac.facility_id =?1