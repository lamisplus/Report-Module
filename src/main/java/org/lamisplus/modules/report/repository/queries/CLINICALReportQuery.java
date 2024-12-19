package org.lamisplus.modules.report.repository.queries;

public class CLINICALReportQuery {

    private CLINICALReportQuery() {}

    public static final String CLINICAL_REPORT_QUERY = "SELECT  DISTINCT (p.uuid) AS patientId, \n" +
            "p.hospital_number AS hospitalNumber, \n" +
            "EXTRACT(YEAR FROM AGE(NOW(), date_of_birth)) AS age, \n" +
            "INITCAP(p.sex) AS gender, \n" +
            "p.date_of_birth AS dateOfBirth, \n" +
            "facility.name AS facilityName, \n" +
            "facility_lga.name AS lga, \n" +
            "facility_state.name AS state, \n" +
            "boui.code AS datimId, \n" +
            "tvs.*, \n" +
            "tvs.body_weight as BodyWeight,  \n" +
            "(CASE\n" +
            "WHEN hac.pregnancy_status = 'Not Pregnant' THEN hac.pregnancy_status\n" +
            "WHEN hac.pregnancy_status = 'Pregnant' THEN hac.pregnancy_status\n" +
            "WHEN hac.pregnancy_status = 'Breastfeeding' THEN hac.pregnancy_status\n" +
            "WHEN hac.pregnancy_status = 'Post Partum' THEN hac.pregnancy_status\n" +
            "WHEN preg.display IS NOT NULL THEN hac.pregnancy_status\n" +
            "ELSE NULL END ) AS pregnancyStatus, \n" +
            "hac.next_appointment as nextAppointment , \n" +
            "hac.visit_date as visitDate, \n" +
            "funStatus.display as funtionalStatus, \n" +
            "clnicalStage.display as clinicalStage, \n" +
            "tbStatus.display as tbStatus \n" +
            "FROM \n" +
            "patient_person p \n" +
            "INNER JOIN base_organisation_unit facility ON facility.id = facility_id\n" +
            "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id \n" +
            "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id \n" +
            "INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID' \n" +
            "INNER JOIN hiv_art_clinical hac ON hac.person_uuid = p.uuid  \n" +
            "LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status\n" +
            "INNER JOIN base_application_codeset funStatus ON funStatus.id = hac.functional_status_id\n" +
            "INNER JOIN base_application_codeset clnicalStage ON clnicalStage.id = hac.clinical_stage_id\n" +
            "INNER JOIN base_application_codeset tbStatus ON tbStatus.id = CAST(regexp_replace(hac.tb_status, '[^0-9]', '', 'g') AS INTEGER)  \n" +
            "INNER JOIN triage_vital_sign tvs ON tvs.uuid = hac.vital_sign_uuid AND hac.archived = 0 \n" +
            "WHERE hac.archived = 0 and hac.tb_status <> '' AND hac.facility_id =?1";
}
