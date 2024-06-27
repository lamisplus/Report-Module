package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.*;
import org.lamisplus.modules.hiv.domain.dto.PatientLineDto;
import org.lamisplus.modules.report.domain.dto.ClinicDataDto;
import org.lamisplus.modules.report.domain.entity.Report;
import org.lamisplus.modules.report.repository.queries.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

        @Query(value = HTSReportQuery.HTS_REPORT_QUERY, nativeQuery = true)
        List<HtsReportDto> getHtsReport(Long facilityId, LocalDate start, LocalDate end);


    @Query(value = "SELECT DISTINCT ON (p.uuid)p.uuid AS PersonUuid, p.id, p.uuid,p.hospital_number as hospitalNumber,       \n" +
            "                        INITCAP(p.surname) AS surname, INITCAP(p.first_name) as firstName, he.date_started AS hivEnrollmentDate,    \n" +
            "                        EXTRACT(YEAR from AGE(NOW(),  date_of_birth)) as age,      \n" +
            "                        p.other_name as otherName, p.sex as sex, p.date_of_birth as dateOfBirth,       \n" +
            "                        p.date_of_registration as dateOfRegistration, p.marital_status->>'display' as maritalStatus,       \n" +
            "                        education->>'display' as education, p.employment_status->>'display' as occupation,       \n" +
            "                        facility.name as facilityName, facility_lga.name as lga, facility_state.name as state,       \n" +
            "                        boui.code as datimId, res_state.name as residentialState, res_lga.name as residentialLga,      \n" +
            "                        r.address as address, p.contact_point->'contactPoint'->0->'value'->>0 AS phone,      \n" +
            "                        baseline_reg.regimen AS baselineRegimen,      \n" +
            "                        baseline_pc.systolic AS baselineSystolicBP,      \n" +
            "                        baseline_pc.diastolic AS baselineDiastolicBP,      \n" +
            "                        baseline_pc.weight AS baselinetWeight,      \n" +
            "                        baseline_pc.height AS baselineHeight,    \n" +
            "                        (CASE WHEN tg.display IS NULL THEN etg.display ELSE tg.display END) AS targetGroup,    \n" +
            "                        baseline_pc.encounter_date AS prepCommencementDate,    \n" +
            "                        baseline_pc.urinalysis->>'result' AS baseLineUrinalysis,   \n" +
            "                        CAST(baseline_pc.urinalysis->>'testDate' AS DATE) AS baseLineUrinalysisDate,   \n" +
            "                        (CASE WHEN baseline_creatinine.other_tests_done->>'name'='Creatinine'    \n" +
            "                        THEN baseline_creatinine.other_tests_done->>'result' ELSE NULL END) AS baseLineCreatinine,   \n" +
            "                       (CASE WHEN baseline_creatinine.other_tests_done->>'name'='Creatinine'    \n" +
            "                        THEN baseline_creatinine.other_tests_done->>'testDate' ELSE NULL END) AS baseLineCreatinineTestDate,   \n" +
            "                        baseline_pc.hepatitis->>'result' AS baseLineHepatitisB,   \n" +
            "                        baseline_pc.hepatitis->>'result' AS baseLineHepatitisC,   \n" +
            "                        current_pi.reason_stopped AS InterruptionReason,   \n" +
            "                        current_pi.encounter_date AS InterruptionDate,   \n" +
            "                         (CASE WHEN baseline_hiv_status.display IS NULL AND base_eli_test.base_eli_hiv_result IS NOT NULL    \n" +
            "                        THEN base_eli_test.base_eli_hiv_result ELSE    \n" +
            "                        REPLACE(baseline_hiv_status.display, 'HIV ', '') END) AS HIVStatusAtPrEPInitiation,   \n" +
            "                        (CASE WHEN prepe.extra->>'onDemandIndication' IS NOT NULL THEN prepe.extra->>'onDemandIndication'      \n" +
            "                        WHEN riskt.display IS NOT NULL THEN riskt.display ELSE NULL END) AS indicationForPrEP,      \n" +
            "                        current_reg.regimen AS currentRegimen,      \n" +
            "                        current_pc.encounter_date AS DateOfLastPickup,      \n" +
            "                        current_pc.systolic AS currentSystolicBP,      \n" +
            "                        current_pc.diastolic AS currentDiastolicBP,      \n" +
            "                        current_pc.weight AS currentWeight,      \n" +
            "                        current_pc.height AS currentHeight,   \n" +
            "                        current_pc.urinalysis->>'result' AS currentUrinalysis,   \n" +
            "                        CAST(current_pc.urinalysis->>'testDate' AS DATE) AS currentUrinalysisDate,   \n" +
            "                        (CASE WHEN current_hiv_status.display IS NULL AND eli_hiv_result IS NOT NULL THEN eli_hiv_result \n" +
            "              WHEN current_hiv_status.display IS NOT NULL THEN REPLACE(current_hiv_status.display, 'HIV ', '') \n" +
            "                        WHEN he.date_started IS NOT NULL THEN 'Positive' ELSE NULL    \n" +
            "              END) AS currentHivStatus,      \n" +
            "              current_pc.encounter_date AS DateOfCurrentHIVStatus, \n" +
            "              (CASE WHEN p.sex='Male' THEN NULL \n" +
            "            WHEN current_pc.pregnant IS NOT NULL AND current_pc.pregnant='true' THEN (SELECT display FROM base_application_codeset WHERE code = current_pc.pregnant)      \n" +
            "                        ELSE 'Not Pregnant' END) AS pregnancyStatus, \n" +
            "             (CASE  \n" +
            "             WHEN prepi.interruption_date  > prepc.encounter_date THEN bac.display \n" +
            "              WHEN prepc.status IS NOT NULL THEN prepc.status \n" +
            "             ELSE NULL END) AS CurrentStatus, \n" +
            "             (CASE  \n" +
            "             WHEN prepi.interruption_date  > prepc.encounter_date THEN prepi.interruption_date \n" +
            "              WHEN prepc.status IS NOT NULL THEN (prepc.encounter_date  + COALESCE(prepc.duration, 0)) \n" +
            "             ELSE NULL END) AS DateOfCurrentStatus \n" +
            "                        FROM patient_person p      \n" +
            "                        INNER JOIN (\n" +
            "            SELECT * FROM (SELECT p.id, REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\\\\', ''), ']', ''), '[', ''), 'null',''), '\\\"', '') AS address, \n" +
            "             CASE WHEN address_object->>'stateId'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'stateId' ELSE null END  AS stateId,\n" +
            "             CASE WHEN address_object->>'stateId'  ~ '^\\d+(\\.\\d+)?$' THEN address_object->>'district' ELSE null END  AS lgaId \n" +
            "            FROM patient_person p,\n" +
            "            jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result\n" +
            "              ) r ON r.id=p.id \n" +
            "             INNER JOIN (SELECT MAX(date_started) date_started, person_uuid,target_group  FROM prep_enrollment \n" +
            "              GROUP BY person_uuid,target_group) penrol ON penrol.person_uuid=p.uuid \n" +
            "\t\t\t  LEFT JOIN (SELECT MAX(visit_date) max_date, person_uuid,target_group AS eli_target  FROM prep_eligibility \n" +
            "              GROUP BY person_uuid,target_group) e_target ON e_target.person_uuid=p.uuid\n" +
            "                        LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS eli_hiv_result, max.visit_date, max.person_uuid FROM prep_eligibility pe    \n" +
            "                        INNER JOIN (SELECT DISTINCT MAX(visit_date)visit_date, person_uuid FROM prep_eligibility    \n" +
            "                        GROUP BY person_uuid)max ON max.visit_date=pe.visit_date     \n" +
            "                        AND max.person_uuid=pe.person_uuid)eli_test ON eli_test.person_uuid=p.uuid    \n" +
            "                        LEFT JOIN (SELECT pe.drug_use_history->>'hivTestResultAtvisit' AS base_eli_hiv_result, min.visit_date, min.person_uuid    \n" +
            "                        FROM prep_eligibility pe    \n" +
            "                        INNER JOIN (SELECT DISTINCT MIN(visit_date)visit_date, person_uuid FROM prep_eligibility    \n" +
            "                        GROUP BY person_uuid)min ON min.visit_date=pe.visit_date    \n" +
            "                        AND min.person_uuid=pe.person_uuid)base_eli_test ON base_eli_test.person_uuid=p.uuid   \n" +
            "                        LEFT JOIN base_organisation_unit facility ON facility.id=facility_id      \n" +
            "                        LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id      \n" +
            "                        LEFT JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id      \n" +
            "                        LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)      \n" +
            "                        LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)      \n" +
            "                        LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=facility_id AND boui.name='DATIM_ID'      \n" +
            "                        INNER JOIN prep_enrollment prepe ON prepe.person_uuid = p.uuid      \n" +
            "                        LEFT JOIN base_application_codeset riskt ON riskt.code = prepe.risk_type      \n" +
            "                        LEFT JOIN base_application_codeset tg ON tg.code = penrol.target_group\n" +
            "\t\t\t\t\t\tLEFT JOIN base_application_codeset etg ON etg.code = e_target.eli_target  \t\t\t\t\t\t\n" +
            "                        LEFT JOIN (SELECT DISTINCT pc.* FROM prep_clinic pc      \n" +
            "                        INNER JOIN (SELECT DISTINCT MAX(encounter_date)encounter_date, person_uuid FROM prep_clinic      \n" +
            "                        GROUP BY person_uuid)max ON max.encounter_date=pc.encounter_date       \n" +
            "                        AND max.person_uuid=pc.person_uuid)current_pc ON current_pc.person_uuid=p.uuid    \n" +
            "                          LEFT JOIN (SELECT DISTINCT pi.* FROM prep_interruption pi      \n" +
            "                        LEFT JOIN (SELECT DISTINCT MAX(encounter_date)encounter_date, person_uuid FROM prep_interruption      \n" +
            "                        GROUP BY person_uuid)max ON max.encounter_date=pi.encounter_date       \n" +
            "                        AND max.person_uuid=pi.person_uuid)current_pi ON current_pi.person_uuid=p.uuid    \n" +
            "                        LEFT JOIN prep_regimen current_reg ON current_reg.id = current_pc.regimen_id      \n" +
            "                        LEFT JOIN base_application_codeset current_hiv_status ON current_hiv_status.code = current_pc.hiv_test_result      \n" +
            "                        LEFT JOIN (SELECT pc.* FROM prep_clinic pc      \n" +
            "                        INNER JOIN (SELECT DISTINCT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic      \n" +
            "                        GROUP BY person_uuid)min ON min.encounter_date=pc.encounter_date       \n" +
            "                        AND min.person_uuid=pc.person_uuid)baseline_pc ON baseline_pc.person_uuid=p.uuid      \n" +

            "                        LEFT JOIN (SELECT pc.* FROM prep_clinic pc      \n" +
            "                        INNER JOIN (SELECT DISTINCT MIN(encounter_date)encounter_date, person_uuid FROM prep_clinic      \n" +
            "                        WHERE other_tests_done->>'name' = 'Creatinine' GROUP BY person_uuid)min ON min.encounter_date=pc.encounter_date       \n" +
            "                        AND min.person_uuid=pc.person_uuid" +
            "                        )baseline_creatinine ON baseline_creatinine.person_uuid=p.uuid      \n" +

            "                        LEFT JOIN prep_regimen baseline_reg ON baseline_reg.id = baseline_pc.regimen_id      \n" +
            "                        LEFT JOIN base_application_codeset baseline_hiv_status ON baseline_hiv_status.code=baseline_pc.hiv_test_result \n" +
            "             LEFT JOIN hiv_enrollment he ON he.person_uuid = p.uuid \n" +
            "             LEFT JOIN (  \n" +
            "                        SELECT pi.id, pi.person_uuid, pi.interruption_date , pi.interruption_type   \n" +
            "                        FROM prep_interruption pi   \n" +
            "                        INNER JOIN (SELECT DISTINCT pi.person_uuid, MAX(pi.interruption_date)interruption_date   \n" +
            "                        FROM prep_interruption pi WHERE pi.archived=0   \n" +
            "                        GROUP BY pi.person_uuid)pit ON pit.interruption_date=pi.interruption_date   \n" +
            "                        AND pit.person_uuid=pi.person_uuid   \n" +
            "                        WHERE pi.archived=0   \n" +
            "                        GROUP BY pi.id, pi.person_uuid, pi.interruption_date, pi.interruption_type )prepi ON prepi.person_uuid=p.uuid \n" +
            "             LEFT JOIN (SELECT pc.person_uuid, MAX(pc.encounter_date) as encounter_date, pc.duration,  \n" +
            "                        (CASE WHEN (pc.encounter_date  + pc.duration) > CAST (NOW() AS DATE) THEN 'Active' \n" +
            "                        ELSE  'Defaulted' END) status FROM prep_clinic pc \n" +
            "                        INNER JOIN (SELECT DISTINCT MAX(pc.encounter_date) encounter_date, pc.person_uuid \n" +
            "                        FROM prep_clinic pc GROUP BY pc.person_uuid) max_p ON max_p.encounter_date=pc.encounter_date \n" +
            "                        AND max_p.person_uuid=pc.person_uuid  \n" +
            "             WHERE pc.archived=0 \n" +
            "                        GROUP BY pc.person_uuid, pc.duration, status)prepc ON prepc.person_uuid=p.uuid \n" +
            "             LEFT JOIN base_application_codeset bac ON bac.code=prepi.interruption_type \n" +
            "                        WHERE p.archived=0 AND p.facility_id=?1 AND p.date_of_registration >=?2 AND p.date_of_registration <= ?3", nativeQuery = true)
    List<PrepReportDto> getPrepReport(Long facilityId, LocalDate start, LocalDate end);


    @Query(value = RADETReportQueries.RADET_REPORT_QUERY, nativeQuery = true)
    List<RADETDTOProjection> getRadetData(Long facilityId, LocalDate start, LocalDate end,
                                          LocalDate previous, LocalDate previousPrevious, LocalDate dateOfStartOfCurrentQuarter);

        @Query(value = "SELECT  DISTINCT (p.uuid) AS patientId, \n" +
                        "                            p.hospital_number AS hospitalNumber, \n" +
                        "                            EXTRACT( \n" +
                        "                                    YEAR \n" +
                        "                                    FROM \n" +
                        "                                    AGE(NOW(), date_of_birth) \n" +
                        "                                ) AS age, \n" +
                        "                            INITCAP(p.sex) AS gender, \n" +
                        "                            p.date_of_birth AS dateOfBirth, \n" +
                        "                            facility.name AS facilityName, \n" +
                        "                            facility_lga.name AS lga, \n" +
                        "                            facility_state.name AS state, \n" +
                        "                            boui.code AS datimId, \n" +
                        "            tvs.*, \n" +
                        "            tvs.body_weight as BodyWeight,  \n" +
                        "           (CASE\n" +
                        "    WHEN hac.pregnancy_status = 'Not Pregnant' THEN hac.pregnancy_status\n" +
                        "    WHEN hac.pregnancy_status = 'Pregnant' THEN hac.pregnancy_status\n" +
                        "    WHEN hac.pregnancy_status = 'Breastfeeding' THEN hac.pregnancy_status\n" +
                        "    WHEN hac.pregnancy_status = 'Post Partum' THEN hac.pregnancy_status\n" +
                        "    WHEN preg.display IS NOT NULL THEN hac.pregnancy_status\n" +
                        "    ELSE NULL END ) AS pregnancyStatus, \n" +
                        "            hac.next_appointment as nextAppointment , \n" +
                        "            hac.visit_date as visitDate, \n" +
                        "            funStatus.display as funtionalStatus, \n" +
                        "            clnicalStage.display as clinicalStage, \n" +
                        "            tbStatus.display as tbStatus \n" +
                        "            FROM \n" +
                        "                 patient_person p \n" +
                        "                       INNER JOIN base_organisation_unit facility ON facility.id = facility_id \n"
                        +
                        "                       INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id \n"
                        +
                        "                       INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id \n"
                        +
                        "                       INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID' \n"
                        +
                        "                       INNER JOIN hiv_art_clinical hac ON hac.person_uuid = p.uuid  \n" +
                        " \t\t   LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status\n" +
                        "           INNER JOIN base_application_codeset funStatus ON funStatus.id = hac.functional_status_id \n"
                        +
                        "           INNER JOIN base_application_codeset clnicalStage ON clnicalStage.id = hac.clinical_stage_id \n"
                        +
                        "           INNER JOIN base_application_codeset tbStatus ON tbStatus.id = CAST(regexp_replace(hac.tb_status, '[^0-9]', '', 'g') AS INTEGER)  \n"
                        +
                        "           INNER JOIN triage_vital_sign tvs ON tvs.uuid = hac.vital_sign_uuid \n" +
                        "                       AND hac.archived = 0 \n" +
                        "               WHERE   hac.archived = 0 \n" +
                        "           AND hac.facility_id =?1", nativeQuery = true)
        List<ClinicDataDto> getClinicData(Long facilityId);

        @Query(value = "WITH bio_data AS (\n" +
                        "\t  SELECT \n" +
                        "\t\tDISTINCT ON (p.uuid) p.uuid as personUuid, \n" +
                        "\t\tp.id, \n" +
                        "\t\tCAST(p.archived AS BOOLEAN) as archived, \n" +
                        "\t\tp.uuid, \n" +
                        "\t\tp.hospital_number as hospitalNumber, \n" +
                        "\t\tp.surname, \n" +
                        "\t\tp.first_name as firstName, \n" +
                        "\t\tEXTRACT(\n" +
                        "\t\t  YEAR \n" +
                        "\t\t  from \n" +
                        "\t\t\tAGE(NOW(), date_of_birth)\n" +
                        "\t\t) as age, \n" +
                        "\t\tp.other_name as otherName, \n" +
                        "\t\tp.sex as gender, \n" +
                        "\t\tp.date_of_birth as dateOfBirth, \n" +
                        "\t\tp.date_of_registration as dateOfRegistration, \n" +
                        "\t\tp.marital_status ->> 'display' as maritalStatus, \n" +
                        "\t\teducation ->> 'display' as education, \n" +
                        "\t\tp.employment_status ->> 'display' as occupation, \n" +
                        "\t\tfacility.name as facilityName, \n" +
                        "\t\tfacility_lga.name as lga, \n" +
                        "\t\tfacility_state.name as state, \n" +
                        "\t\tboui.code as datimId, \n" +
                        "\t\tres_state.name as residentialState, \n" +
                        "\t\tres_lga.name as residentialLga, \n" +
                        "\t\tr.address as address, \n" +
                        "\t\tp.contact_point -> 'contactPoint' -> 0 -> 'value' ->> 0 AS phone \n" +
                        "\t  FROM \n" +
                        "\t\tpatient_person p \n" +
                        "\t\tINNER JOIN (\n" +
                        "\t\t\t  SELECT \n" +
                        "\t\t\t\t* \n" +
                        "\t\t\t  FROM \n" +
                        "\t\t\t\t(\n" +
                        "\t\t\t\t   SELECT\n" +
                        "\t\t\t\t\t\tp.id,\n" +
                        "\t\t\t\t\t\tCASE WHEN address_object->>'city' IS NOT NULL\n" +
                        "\t\t\t\t\t\t\t THEN CONCAT_WS(' ', address_object->>'city', \n" +
                        "\t\t\t\t\t\t\t\t\t\t\tREPLACE(REPLACE(COALESCE(NULLIF(address_object->>'line', '\\\\'), ''), ']', ''), '[', ''), \n"
                        +
                        "\t\t\t\t\t\t\t\t\t\t\tNULLIF(NULLIF(address_object->>'stateId', 'null'), '')) \n" +
                        "\t\t\t\t\t\t\t ELSE NULL \n" +
                        "\t\t\t\t\t\tEND AS address,\n" +
                        "\t\t\t\t\t\tCASE WHEN address_object->>'stateId' ~ '^\\d+(\\.\\d+)?$' \n" +
                        "\t\t\t\t\t\t\t THEN address_object->>'stateId' \n" +
                        "\t\t\t\t\t\t\t ELSE NULL \n" +
                        "\t\t\t\t\t\tEND AS stateId,\n" +
                        "\t\t\t\t\t\tCASE WHEN address_object->>'stateId' ~ '^\\d+(\\.\\d+)?$' \n" +
                        "\t\t\t\t\t\t\t THEN address_object->>'district' \n" +
                        "\t\t\t\t\t\t\t ELSE NULL \n" +
                        "\t\t\t\t\t\tEND AS lgaId\n" +
                        "\t\t\t\t\tFROM patient_person p\n" +
                        "\t\t\t\t\tCROSS JOIN jsonb_array_elements(p.address->'address') AS l(address_object)\n" +
                        "\t\t\t\t)  as result\n" +
                        "\t\t\t ) r ON r.id=p.id\n" +
                        "\t\t\t\t INNER JOIN base_organisation_unit facility ON facility.id=facility_id\n" +
                        "\t\t\t\t INNER JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id\n"
                        +
                        "\t\t\t\t INNER JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id\n"
                        +
                        "\t\t\t\t LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateId AS BIGINT)\n"
                        +
                        "\t\t\t\t LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(CASE WHEN r.lgaId ~ E'^\\\\d+$' THEN r.lgaId ELSE NULL END AS BIGINT)\n"
                        +
                        "\t\t\t\t INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=?1 AND boui.name='DATIM_ID'\n"
                        +
                        "\t\t\t\t INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
                        "\t\t\t\t WHERE p.archived=0 AND h.archived=0 AND h.facility_id=?1\n" +
                        "\t\t\t\t),\n" +
                        "\t\t\t enrollment_details AS (\n" +
                        "\t\t\t\t SELECT h.person_uuid,h.unique_id as uniqueId,  sar.display as statusAtRegistration, date_confirmed_hiv as dateOfConfirmedHiv,\n"
                        +
                        "\t\t\t\t ep.display as entryPoint, date_of_registration as dateOfRegistration\n" +
                        "\t\t\t\t FROM hiv_enrollment h\n" +
                        "\t\t\t\t LEFT JOIN base_application_codeset sar ON sar.id=h.status_at_registration_id\n" +
                        "\t\t\t\t LEFT JOIN base_application_codeset ep ON ep.id=h.entry_point_id\n" +
                        "\t\t\t\t WHERE h.archived=0 AND h.facility_id=?1\n" +
                        "\t\t\t ),\n" +
                        "\t\t\t laboratory_details AS ( SELECT DISTINCT ON (lo.patient_uuid)\n" +
                        "    lo.patient_uuid AS person_uuid,\n" +
                        "    ll.lab_test_name AS test,\n" +
                        "    bac_viral_load.display AS viralLoadType,\n" +
                        "    ls.date_sample_collected AS dateSampleCollected,\n" +
                        "    lr.result_reported AS lastViralLoad,\n" +
                        "    lr.date_result_reported AS dateOfLastViralLoad\n" +
                        "FROM\n" +
                        "    laboratory_order lo\n" +
                        "        INNER JOIN hiv_enrollment h ON h.person_uuid = lo.patient_uuid\n" +
                        "        LEFT JOIN laboratory_test lt ON lt.lab_order_id = lo.id\n" +
                        "        LEFT JOIN laboratory_labtest ll ON ll.id = lt.lab_test_id AND ll.lab_test_name = 'Viral Load'\n"
                        +
                        "        LEFT JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.patient_uuid = lo.patient_uuid\n"
                        +
                        "        LEFT JOIN laboratory_result lr ON lr.test_id = lt.id AND lr.patient_uuid = lo.patient_uuid\n"
                        +
                        "        LEFT JOIN base_application_codeset bac_viral_load ON bac_viral_load.id = lt.viral_load_indication\n"
                        +
                        "WHERE\n" +
                        "    lo.archived = 0\n" +
                        "  AND h.archived = 0\n" +
                        "  AND lo.facility_id = ?1\n" +
                        "ORDER BY\n" +
                        "    lo.patient_uuid, lo.order_date DESC" +
                        "\t\t\t ),\n" +
                        "\t\t\t pharmacy_details AS (\n" +
                        "\t\t\t\t SELECT DISTINCT ON (hartp.person_uuid)hartp.person_uuid as person_uuid, r.visit_date as dateOfLastRefill,\n"
                        +
                        "\t\t\t\t hartp.next_appointment as dateOfNextRefill, hartp.refill_period as lastRefillDuration,\n"
                        +
                        "\t\t\t\t hartp.dsd_model_type as DSDType, r.description as currentRegimenLine, r.regimen_name as currentRegimen,\n"
                        +
                        "\t\t\t\t (CASE\n" +
                        "\t\t\t\t WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '\n" +
                        "\t\t\t\t OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.hiv_status\n" +
                        "\t\t\t\t WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' < CURRENT_DATE\n" +
                        "\t\t\t\t THEN ' IIT ' ELSE ' ACTIVE '\n" +
                        "\t\t\t\t END)AS currentStatus,\n" +
                        "\t\t\t\t (CASE\n" +
                        "\t\t\t\t WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '\n" +
                        "\t\t\t\t OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.status_date\n" +
                        "\t\t\t\t WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' <= CURRENT_DATE\n" +
                        "\t\t\t\t THEN CAST((hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ') AS date) ELSE hartp.visit_date\n"
                        +
                        "\t\t\t\t END)AS dateOfCurrentStatus\n" +
                        "\t\t\t\t FROM hiv_art_pharmacy hartp\n" +
                        "\t\t\t\t INNER JOIN (SELECT distinct r.* FROM (SELECT h.person_uuid, h.visit_date, CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name,\n"
                        +
                        "\t\t\t\t hrt.description FROM hiv_art_pharmacy h,\n" +
                        "\t\t\t\t jsonb_array_elements(h.extra->'regimens') with ordinality p(pharmacy_object)\n" +
                        "\t\t\t\t INNER JOIN hiv_regimen hr ON hr.description=CAST(pharmacy_object ->> 'regimenName' AS VARCHAR)\n"
                        +
                        "\t\t\t\t INNER JOIN hiv_regimen_type hrt ON hrt.id=hr.regimen_type_id\n" +
                        "\t\t\t\t WHERE hrt.id IN (1,2,3,4,14))r\n" +
                        "\t\t\t\t INNER JOIN (SELECT hap.person_uuid, MAX(visit_date) AS MAXDATE FROM hiv_art_pharmacy hap\n"
                        +
                        "\t\t\t\t INNER JOIN hiv_enrollment h ON h.person_uuid=hap.person_uuid  WHERE h.archived=0\n" +
                        "\t\t\t\t GROUP BY hap.person_uuid ORDER BY MAXDATE ASC ) max ON\n" +
                        "\t\t\t\t max.MAXDATE=r.visit_date AND r.person_uuid=max.person_uuid) r\n" +
                        "\t\t\t\t ON r.visit_date=hartp.visit_date AND r.person_uuid=hartp.person_uuid\n" +
                        "\t\t\t\t INNER JOIN hiv_enrollment he ON he.person_uuid=r.person_uuid\n" +
                        "\t\t\t\t LEFT JOIN (SELECT sh1.person_id, sh1.hiv_status, sh1.status_date\n" +
                        "\t\t\t\t FROM hiv_status_tracker sh1\n" +
                        "\t\t\t\t INNER JOIN\n" +
                        "\t\t\t\t (\n" +
                        "\t\t\t\t\tSELECT person_id as p_id, MAX(hst.id) AS MAXID\n" +
                        "\t\t\t\t\tFROM hiv_status_tracker hst INNER JOIN hiv_enrollment h ON h.person_uuid=person_id\n"
                        +
                        "\t\t\t\t\tGROUP BY person_id\n" +
                        "\t\t\t\t ORDER BY person_id ASC\n" +
                        "\t\t\t\t ) sh2 ON sh1.person_id = sh2.p_id AND sh1.id = sh2.MAXID\n" +
                        "\t\t\t\t ORDER BY sh1.person_id ASC) stat ON stat.person_id=hartp.person_uuid\n" +
                        "\t\t\t\t WHERE he.archived=0 AND hartp.archived=0 AND hartp.facility_id=?1 ORDER BY hartp.person_uuid ASC\n"
                        +
                        "\t\t\t ),\n" +
                        "\t\t\t art_commencement_vitals AS (\n" +
                        "\t\t\t\t SELECT DISTINCT ON (tvs.person_uuid) tvs.person_uuid , body_weight as baseLineWeight, height as baseLineHeight,\n"
                        +
                        "\t\t\t\t CONCAT(diastolic, ' / ', systolic) as baseLineBp, diastolic as diastolicBp,\n" +
                        "\t\t\t\t systolic as systolicBp, clinical_stage.display as baseLineClinicalStage,\n" +
                        "\t\t\t\t func_status.display as baseLineFunctionalStatus,\n" +
                        "\t\t\t\t hv.description as firstRegimen, hrt.description as firstRegimenLine,\n" +
                        "\t\t\t\t CASE WHEN cd_4=0 THEN null ELSE cd_4 END  AS baseLineCd4,\n" +
                        "\t\t\t\t CASE WHEN cd_4_percentage=0 THEN null ELSE cd_4_percentage END AS cd4Percentage,\n" +
                        "\t\t\t\t hac.visit_date as artStartDate\n" +
                        "\t\t\t\t FROM triage_vital_sign tvs\n" +
                        "\t\t\t\t INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid\n" +
                        "\t\t\t\t AND hac.is_commencement=true AND hac.person_uuid = tvs.person_uuid\n" +
                        "\t\t\t\t INNER JOIN hiv_enrollment h ON hac.hiv_enrollment_uuid = h.uuid AND hac.person_uuid=tvs.person_uuid\n"
                        +
                        "\t\t\t\t INNER JOIN patient_person p ON p.uuid=h.person_uuid\n" +
                        "\t\t\t\t RIGHT JOIN hiv_regimen hv ON hv.id=hac.regimen_id\n" +
                        "\t\t\t\t RIGHT JOIN hiv_regimen_type hrt ON hrt.id=hac.regimen_type_id\n" +
                        "\t\t\t\t RIGHT JOIN base_application_codeset clinical_stage ON clinical_stage.id=hac.clinical_stage_id\n"
                        +
                        "\t\t\t\t RIGHT JOIN base_application_codeset func_status ON func_status.id=hac.functional_status_id\n"
                        +
                        "\t\t\t\t   WHERE hac.archived=0  AND h.archived=0 AND h.facility_id=?1\n" +
                        "\t\t\t ),\n" +
                        "             current_clinical AS (\n" +
                        "\t\t\t\t SELECT tvs.person_uuid, hac.adherence_level as adherenceLevel, hac.next_appointment as dateOfNextClinic, body_weight as currentWeight, height as currentHeight,\n"
                        +
                        "\t\t\t\t  diastolic as currentDiastolic, systolic as currentSystolic, bac.display as currentClinicalStage,\n"
                        +
                        "\t\t\t\t  CONCAT(diastolic, ' / ', systolic) as currentBp, current_clinical_date.MAXDATE as dateOfLastClinic\n"
                        +
                        "\t\t\t\t FROM triage_vital_sign tvs\n" +
                        "\t\t\t\t INNER JOIN ( SELECT person_uuid, MAX(capture_date) AS MAXDATE FROM triage_vital_sign\n"
                        +
                        "\t\t\t\t GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_triage\n" +
                        "\t\t\t\t ON current_triage.MAXDATE=tvs.capture_date AND current_triage.person_uuid=tvs.person_uuid\n"
                        +
                        "\t\t\t\t INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid\n" +
                        "\t\t\t\t INNER JOIN ( SELECT person_uuid, MAX(hac.visit_date) AS MAXDATE FROM hiv_art_clinical hac\n"
                        +
                        "\t\t\t\t GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_clinical_date\n" +
                        "\t\t\t\t ON current_clinical_date.MAXDATE=hac.visit_date AND current_clinical_date.person_uuid=hac.person_uuid\n"
                        +
                        "\t\t\t\t INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid\n" +
                        "\t\t\t\t INNER JOIN base_application_codeset bac ON bac.id=hac.clinical_stage_id\n" +
                        "\t\t\t\t WHERE hac.archived=0 AND he.archived=0 AND he.facility_id=?1\n" +
                        "\t\t\t )\n" +
                        "             SELECT\n" +
                        "             DISTINCT ON (b.personUuid)b.personUuid AS personUuid,\n" +
                        "             b.archived,\n" +
                        "             b.hospitalNumber,\n" +
                        "             b.surname,\n" +
                        "             b.firstName,\n" +
                        "             b.age,\n" +
                        "             b.otherName,\n" +
                        "             b.gender,\n" +
                        "             b.dateOfBirth,\n" +
                        "             b.maritalStatus,\n" +
                        "             b.education,\n" +
                        "             b.occupation,\n" +
                        "             b.facilityName,\n" +
                        "             b.lga,\n" +
                        "             b.state,\n" +
                        "             b.datimId,\n" +
                        "             b.residentialState,\n" +
                        "             b.residentialLga,\n" +
                        "             b.address,\n" +
                        "             b.phone,\n" +
                        "             c.currentWeight,\n" +
                        "             c.currentHeight,\n" +
                        "             c.currentDiastolic as currentDiastolicBp,\n" +
                        "             c.currentSystolic as currentSystolicBP,                \n" +
                        "             c.currentBp,\n" +
                        "             c.dateOfLastClinic,\n" +
                        "             c.dateOfNextClinic,\n" +
                        "             c.adherenceLevel,\n" +
                        "             c.currentClinicalStage as lastClinicStage,\n" +
                        "             e.statusAtRegistration,\n" +
                        "             e.dateOfConfirmedHiv as dateOfConfirmedHIVTest,\n" +
                        "             e.entryPoint as careEntryPoint,\n" +
                        "             e.uniqueId,\n" +
                        "             e.dateOfRegistration,\n" +
                        "             p.dateOfNextRefill,\n" +
                        "             p.lastRefillDuration,\n" +
                        "             p.dateOfLastRefill,\n" +
                        "             p.DSDType,\n" +
                        "             p.currentRegimen,\n" +
                        "             p.currentRegimenLine,\n" +
                        "             p.currentStatus,\n" +
                        "             p.dateOfCurrentStatus as dateOfCurrentStatus,\n" +
                        "             l.test,                  \n" +
                        "             l.viralLoadType,\n" +
                        "             l.dateSampleCollected as dateOfSampleCollected ,\n" +
                        "             l.lastViralLoad,\n" +
                        "             l.dateOfLastViralLoad,\n" +
                        "             a.baseLineWeight,\n" +
                        "             a.baseLineHeight,\n" +
                        "             a.baseLineBp,\n" +
                        "             a.diastolicBp,                       \n" +
                        "             a.systolicBp,\n" +
                        "             a.baseLineClinicalStage as baselineClinicStage,\n" +
                        "             a.baseLineFunctionalStatus,                      \n" +
                        "             a.firstRegimen,\n" +
                        "             a.firstRegimenLine,\n" +
                        "             a.baseLineCd4,                      \n" +
                        "             a.cd4Percentage,                          \n" +
                        "             a.artStartDate\n" +
                        "             FROM enrollment_details e\n" +
                        "             INNER JOIN bio_data b ON e.person_uuid=b.personUuid\n" +
                        "             LEFT JOIN art_commencement_vitals a ON a.person_uuid=e.person_uuid\n" +
                        "             LEFT JOIN pharmacy_details p ON p.person_uuid=e.person_uuid\n" +
                        "             LEFT JOIN laboratory_details l ON l.person_uuid=e.person_uuid\n" +
                        "             LEFT JOIN current_clinical c ON c.person_uuid=e.person_uuid", nativeQuery = true)
        List<PatientLineDto> getPatientLineByFacilityId(Long facilityId);

    @Query(value = "SELECT result.id, result.surname,\n" +
            "result.hospital_number as hospitalNumber, result.date_of_birth as dob, result.phone, result.age, result.name, result.sex,\n" +
            "result.facility_id, result.address, count(b.person_uuid) as finger, b.enrollment_date as enrollment\n" +
            "FROM (SELECT p.id, EXTRACT(YEAR from AGE(NOW(),  p.date_of_birth)) as age, p.contact_point->'contactPoint'->0->'value'->>0 as phone, \n" +
            "      concat(p.surname ,' ', p.first_name) as name, p.hospital_number, p.date_of_birth, p.sex,\n" +
            "      p.facility_id, p.surname, p.uuid, p.archived, " +
            "  CONCAT(address_object->>'city', REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(CAST(address_object->>'line' AS text), '\\\\', ''), ']', ''), '[', ''), 'null',''), '\"', '')) AS address " +
            "      FROM patient_person p,\n" +
            "jsonb_array_elements(p.address-> 'address') with ordinality l(address_object)) as result\n" +
            "inner join biometric b on b.person_uuid = result.uuid  \n" +
            "where result.facility_id = ?1 and result.archived = 0 and  \n" +
            "b.enrollment_date between ?2 and ?3 GROUP by result.surname, b.enrollment_date,\n" +
            "result.hospital_number, result.id, result.date_of_birth, result.age, result.name, result.sex,\n" +
            "result.facility_id, result.phone, result.address;", nativeQuery = true
    )
    List<BiometricReport> getBiometricReports(Long facilityId, LocalDate startDate, LocalDate endDate);

    @Query(value = CLIENTVERIFICATIONReportQuery.CLIENT_VERIFICATION_QUERY, nativeQuery = true)
    List<ClientServiceDto> generateClientServiceList(Long facilityId);


        @Query(value = TBReportQuery.TB_REPORT_QUERY, nativeQuery = true)
        List<TBReportProjection> generateTBReport(Long facilityId, LocalDate start, LocalDate end);

        @Query(value = EACReportQuery.EAC_REPORT_QUERY, nativeQuery = true)
        List<EACReportProjection> generateEACReport(Long facilityId, LocalDate start, LocalDate end);

        @Query(value = NCDReportQuery.NCD_REPORT_QUERY, nativeQuery = true)
        List<NCDReportProjection> generateNCDReport(Long facilityId, LocalDate start, LocalDate end);

//        @Query(value = AHDReportQuery.AHD_QUERY, nativeQuery = true)
//        List<AHDDTOProjection> generateAHDReport (Long facilityId, LocalDate start, LocalDate end);
}