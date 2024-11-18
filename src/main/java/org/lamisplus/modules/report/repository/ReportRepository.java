package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.*;
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

        @Query(value = FamilyIndexReportQuery.FAMILY_INDEX_REPORT_QUERY, nativeQuery = true)
        List<FamilyIndexReportDtoProjection> getFamilyIndexReport (Long facilityId);


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

        @Query(value = CLINICALReportQuery.CLINICAL_REPORT_QUERY, nativeQuery = true)
        List<ClinicDataDto> getClinicData(Long facilityId);

        @Query(value = ARTPATIENTReportQuery.ART_PATIENT_REPORT_QUERY, nativeQuery = true)
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


    @Query(value = LABORATORYReportQuery.LABORATORY_REPORT_QUERY, nativeQuery = true)
    List<LabReport> getLabReports(Long facilityId);

    @Query(nativeQuery = true, value = PHARMACYReportQuery.PHARMACY_REPORT_QUERY)
    List<PharmacyReport> getArtPharmacyReport(Long facilityId);


        @Query(value = TBReportQuery.TB_REPORT_QUERY, nativeQuery = true)
        List<TBReportProjection> generateTBReport(Long facilityId, LocalDate start, LocalDate end);

        @Query(value = EACReportQuery.EAC_REPORT_QUERY, nativeQuery = true)
        List<EACReportProjection> generateEACReport(Long facilityId, LocalDate start, LocalDate end);

        @Query(value = NCDReportQuery.NCD_REPORT_QUERY, nativeQuery = true)
        List<NCDReportProjection> generateNCDReport(Long facilityId, LocalDate start, LocalDate end);

//        @Query(value = AHDReportQuery.AHD_QUERY, nativeQuery = true)
//        List<AHDDTOProjection> generateAHDReport (Long facilityId, LocalDate start, LocalDate end);
}