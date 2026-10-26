package org.lamisplus.modules.report.repository.queries;

public class PEPReportQuery {

    public static final String PEP_REPORT_QUERY = "WITH bio_data AS (SELECT DISTINCT (p.uuid) AS personUuid ,p.hospital_number AS hospitalNumber,\n" +
            "EXTRACT(YEAR FROM  AGE(?3, date_of_birth)) AS age,\n" +
            "INITCAP(p.sex) AS gender,\n" +
            "p.date_of_birth AS dateOfBirth,\n" +
            "facility.name AS facilityName,\n" +
            "facility_lga.name AS lga,\n" +
            "facility_state.name AS state,\n" +
            "boui.code AS datimId,\n" +
            "p.date_of_registration as dateOfRegistration\n" +
            "FROM patient_person p\n" +
            "INNER JOIN base_organisation_unit facility ON facility.id = facility_id\n" +
            "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID'\n" +
            "WHERE p.facility_id = ?1 AND p.archived = 0\n" +
            "),\n" +
            "prophylaxisInit AS (\n" +
            "SELECT uuid, person_uuid, prophylaxis_screening_uuid, hts_encounter_uuid, enroltypecode.display enrollmentType, date_enrolled dateEnrolled, \n" +
            "popcode.display populationType, regimencode.display prepRegimen, date_prep_started datePrepStarted \n" +
            "FROM (\n" +
            "SELECT uuid, person_uuid, prophylaxis_screening_uuid, hts_encounter_uuid, enrollment_type, date_enrolled, population_type, prep_regimen, date_prep_started,\n" +
            "ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY date_enrolled DESC) rnnk\n" +
            "FROM prophylaxis_initiation\n" +
            "WHERE enrollment_type = 'PREP_PEP_ENROLLMENT_TYPE_PEP' AND archived IS FALSE AND facility_id = ?1 AND date_enrolled BETWEEN ?2 AND ?3) init\n" +
            "LEFT JOIN base_application_codeset enroltypecode ON enroltypecode.code = enrollment_type\n" +
            "LEFT JOIN base_application_codeset popcode ON popcode.code = population_type\n" +
            "LEFT JOIN base_application_codeset regimencode ON regimencode.code = prep_regimen\n" +
            "WHERE rnnk = 1\n" +
            "),\n" +
            "pepFollowup AS (\n" +
            "SELECT followupuuid, person_uuid, prophylaxis_initiation_uuid, encounter_date, hts_encounter_uuid, codemode.display modeOfExposure, codehivStatus.display hivStatusAtExposure, codeduration.display durationBeforePep, codesti.display stiScreening,\n" +
            "adherence.display adherenceLevel, note.display notedSideEffects, risk.display riskReduction, hts.date_of_visit firstVisitDate, hts.observation->>'finalHivTestResult' firstFinalHivTestResult,\n" +
            "hts1.date_of_visit secondVisitDate, hts1.observation->>'finalHivTestResult' secondHivTestResult,\n" +
            "hts2.date_of_visit thirdVisitDate, hts2.observation->>'finalHivTestResult' thirdHivTestResult, date_stop_pep dateStopPep, next_appointment nextAppointment\n" +
            "FROM ( \n" +
            "SELECT uuid followupuuid, person_uuid, prophylaxis_initiation_uuid, encounter_date, hts_encounter_uuid, mode_of_exposure, hiv_status_at_exposure, duration_before_pep, syndromic_sti_screening->>0 stiScreening,\n" +
            "adherence_level, pep_noted_side_effects->>0 noteSideEffect, risk_reduction_services, date_stop_pep, followup_hiv_test_results->>0 as firstVisitResult, followup_hiv_test_results->>1 secondVisitResult,\n" +
            "followup_hiv_test_results->>2 thirdVisitResult, next_appointment,\n" +
            "ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY encounter_date DESC) rnnk\n" +
            "FROM pep_followup_visit  WHERE archived IS FALSE AND facility_id = ?1 AND encounter_date BETWEEN ?2 AND ?3) followup\n" +
            "LEFT JOIN base_application_codeset codemode ON codemode.code = mode_of_exposure\n" +
            "LEFT JOIN base_application_codeset codehivStatus ON codehivStatus.code = hiv_status_at_exposure\n" +
            "LEFT JOIN base_application_codeset codeduration ON codeduration.code = duration_before_pep\n" +
            "LEFT JOIN base_application_codeset codesti ON codesti.code = stiScreening\n" +
            "LEFT JOIN base_application_codeset adherence ON adherence.code = adherence_level\n" +
            "LEFT JOIN base_application_codeset note ON note.code = noteSideEffect\n" +
            "LEFT JOIN base_application_codeset risk ON risk.code = risk_reduction_services\n" +
            "LEFT JOIN hts_encounter hts ON hts.uuid = firstVisitResult\n" +
            "LEFT JOIN hts_encounter hts1 ON hts1.uuid = secondVisitResult\n" +
            "LEFT JOIN hts_encounter hts2 ON hts2.uuid = thirdVisitResult\n" +
            "WHERE rnnk = 1\n" +
            "),\n" +
            "prophyhilaxisInterrupt AS (\n" +
            "SELECT uuid, person_uuid, prophylaxis_initiation_uuid, hts_encounter_uuid, interruption_date interruptionDate, codeReason.display interruptionReason, link_to_art link2Art, codeEarlyVL.display earlyDetectVLResult,\n" +
            "codePep.display pepCompletion\n" +
            "FROM(\n" +
            "SELECT uuid, person_uuid, prophylaxis_initiation_uuid, hts_encounter_uuid, interruption_date, interruption_reason, link_to_art, early_detect_viral_load_result, \n" +
            "ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY interruption_date DESC) rnnk,\n" +
            "pep_completion\n" +
            "FROM prophylaxis_interruptions WHERE archived IS FALSE AND facility_id = ?1 AND interruption_date BETWEEN ?2 AND ?3\n" +
            ") interrupt\n" +
            "LEFT JOIN base_application_codeset codePep ON codePep.code = pep_completion\n" +
            "LEFT JOIN base_application_codeset codeEarlyVL ON codeEarlyVL.code = early_detect_viral_load_result\n" +
            "LEFT JOIN base_application_codeset codeReason ON codeReason.code = interruption_reason\n" +
            "WHERE rnnk = 1\n" +
            "\n" +
            "),\n" +
            "htsRecord AS (\n" +
            "SELECT patient_uuid, preg.display pregnancyStatus, finalHivTestResult FROM (\n" +
            "SELECT uuid, patient_uuid, observation->>'pregnancyStatus' pregnancyStatus, observation->>'finalHivTestResult' finalHivTestResult, date_of_visit,\n" +
            "ROW_NUMBER() OVER (PARTITION BY patient_uuid ORDER BY date_of_visit DESC) rnnk\n" +
            "FROM hts_encounter where archived IS FALSE AND facility_id = ?1 AND date_of_visit BETWEEN ?2 AND ?3\n" +
            ") htsRec \n" +
            "LEFT JOIN base_application_codeset preg ON preg.code = pregnancyStatus\n" +
            "WHERE rnnk = 1\n" +
            ")\n" +
            "SELECT \n" +
            "b.state, b.lga, b.facilityName, b.datimId, b.personUuid, b.gender, b.age, pi.enrollmentType, pf.modeOfExposure, pf.hivStatusAtExposure, pf.durationBeforePep, pi.dateEnrolled, pi.populationType, \n" +
            "hts.pregnancyStatus, hts.finalHivTestResult,\n" +
            "pf.stiScreening, pi.prepRegimen, pf.adherenceLevel, pf.notedSideEffects, pf.riskReduction, pi.datePrepStarted, pf.dateStopPep, pinterrupt.pepCompletion, pf.firstVisitDate, pf.firstFinalHivTestResult, pf.secondVisitDate,\n" +
            "pf.secondHivTestResult, pf.thirdVisitDate, pf.thirdHivTestResult, pf.nextAppointment, pinterrupt.interruptionDate, pinterrupt.earlyDetectVLResult, pinterrupt.interruptionReason, pinterrupt.link2Art\n" +
            "\n" +
            "FROM prophylaxisInit pi\n" +
            "JOIN bio_data b ON b.personUuid = pi.person_uuid\n" +
            "LEFT JOIN pepFollowup pf ON pf.person_uuid = pi.person_uuid\n" +
            "LEFT JOIN prophyhilaxisInterrupt pin ON pin.person_uuid = pi.person_uuid\n" +
            "LEFT JOIN prophyhilaxisInterrupt pinterrupt ON pinterrupt.person_uuid = pi.person_uuid\n" +
            "LEFT JOIN htsRecord hts ON hts.patient_uuid = pi.person_uuid";

}
