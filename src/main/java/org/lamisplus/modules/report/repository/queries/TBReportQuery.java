package org.lamisplus.modules.report.repository.queries;

public class TBReportQuery {

    public static final String TB_REPORT_QUERY = "with bio_data as (" +
            "    SELECT  facility_lga.name AS lga, p.other_name, p.surname, p.first_name, " +
            "            facility_state.name AS state, p.uuid, p.hospital_number, h.unique_id as uniqueId,EXTRACT(YEAR FROM  AGE(NOW(), date_of_birth)) AS age, " +
            "            INITCAP(p.sex) AS gender,p.date_of_birth, facility.name AS facility_name, boui.code AS datimId,tgroup.display AS targetGroup, eSetting.display AS enrollment_setting, " +
            "            hac.visit_date AS art_start_date, hr.description AS regimen_at_start, p.date_of_registration " +
            "      FROM patient_person p " +
            "            INNER JOIN base_organisation_unit facility ON facility.id = facility_id " +
            "            INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id " +
            "            INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id " +
            "            INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID' " +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid " +
            "            LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id " +
            "            LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id " +
            "            LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id " +
            "            INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid " +
            "                                                   AND hac.archived = 0 " +
            "            INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id " +
            "            INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id " +
            "      WHERE " +
            "          h.archived = 0 " +
            "        AND p.archived = 0 " +
            "        AND h.facility_id = ?1 " +
            "        AND hac.is_commencement = TRUE " +
            "        AND hac.visit_date >= ?2 " +
            "        AND hac.visit_date < ?3 " +
            "), " +
            "tb_status as ( " +
            "    with tbscreening_cs as ( " +
            "        with cs as ( " +
            "            SELECT id, person_uuid, date_of_observation AS date_of_tb_Screened, data->'tbIptScreening'->>'status' AS tb_status, " +
            "                data->'tbIptScreening'->>'tbScreeningType' AS tb_screening_type, data->'tbIptScreening'->>'eligibleForTPT' as eligible_for_tpt, " +
            "                ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS rowNums " +
            "        FROM hiv_observation " +
            "        WHERE type = 'Chronic Care' and data is not null and archived = 0 " +
            "            and date_of_observation between ?2 and ?3 " +
            "            and facility_id = ?1 " +
            "        ) " +
            "        select * from cs where rowNums = 1 " +
            "    ), " +
            "    tbscreening_hac as ( " +
            "        with h as (" +
            "            select h.id, h.person_uuid, h.visit_date, cast(h.tb_screen->>'tbStatusId' as bigint) as tb_status_id, " +
            "               b.display as h_status, " +
            "               ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY visit_date DESC) AS rowNums " +
            "            from hiv_art_clinical h " +
            "            join base_application_codeset b on b.id = cast(h.tb_screen->>'tbStatusId' as bigint) " +
            "            where h.archived = 0 and h.visit_date between ?2 and ?3 and facility_id = ?1 " +
            "        ) " +
            "        select * from h where rowNums = 1 " +
            "    ) " +
            "    select " +
            "         tcs.person_uuid, " +
            "         case " +
            "             when tcs.tb_status is not null then tcs.tb_status " +
            "             when tcs.tb_status is null and th.h_status is not null then th.h_status " +
            "         end as tb_status, " +
            "         case " +
            "             when tcs.tb_status is not null then tcs.date_of_tb_screened" +
            "             when tcs.tb_status is null and th.h_status is not null then th.visit_date " +
            "         end as date_of_tb_screened, " +
            "        tcs.tb_screening_type " +
            "        from tbscreening_cs tcs " +
            "             left join tbscreening_hac th on th.person_uuid = tcs.person_uuid " +
            ")," +
            "tb_treatement_start as ( " +
            "    with tbt as ( " +
            "        SELECT " +
            "             COALESCE(NULLIF(CAST(data->'tbIptScreening'->>'treatementType' AS text), ''), '') AS tb_treatement_type, " +
            "             NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'tbTreatmentStartDate', '') AS DATE), NULL) AS tb_treatment_start_date, " +
            "             data->'tbIptScreening'->>'eligibleForTPT' as eligible_for_tpt, person_uuid, " +
            "             ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS row_number " +
            "        FROM public.hiv_observation WHERE type = 'Chronic Care' AND facility_id = ?1 and archived = 0 " +
            "    ) " +
            "    select person_uuid, tb_treatement_type, tb_treatment_start_date, eligible_for_tpt " +
            "    from tbt where row_number = 1 " +
            "), " +
            "tb_treatement_completion as ( " +
            "    select person_uuid, tb_treatment_outcome, tb_completion_date from (select CAST(data->'tbIptScreening'->>'treatmentOutcome' AS text) AS tb_treatment_outcome, " +
            "             NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'completionDate', '') AS DATE), NULL) AS tb_completion_date, person_uuid, " +
            "             ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS row_number " +
            "    FROM public.hiv_observation WHERE type = 'Chronic Care' AND facility_id = ?1 and archived = 0) ttc where row_number = 1 " +
            "        and tb_completion_date is not null " +
            "), " +
            "current_tb_result AS ( " +
            "    with cur_tb as ( " +
            "            select sm.patient_uuid, sm.result_reported AS tb_diagnostic_result, " +
            "            CAST(sm.date_result_reported AS DATE) AS date_of_tb_diagnostic_result_received, " +
            "            CASE lt.lab_test_id " +
            "                WHEN 65 THEN 'Gene Xpert' " +
            "                WHEN 51 THEN 'TB-LAM' " +
            "                WHEN 66 THEN 'Chest X-ray' " +
            "                WHEN 64 THEN 'AFB microscopy' " +
            "                WHEN 67 THEN 'Gene Xpert' " +
            "                WHEN 58 THEN 'TB-LAM' " +
            "            END AS tb_diagnostic_test_type, " +
            "            ROW_NUMBER() OVER (PARTITION BY sm.patient_uuid ORDER BY sm.date_result_reported DESC) AS rnk " +
            "        FROM " +
            "            laboratory_result sm " +
            "            INNER JOIN public.laboratory_test lt ON sm.test_id = lt.id " +
            "        WHERE " +
            "            lt.lab_test_id IN (65, 51, 66, 64) " +
            "            AND sm.archived = 0 " +
            "            AND sm.date_result_reported IS NOT NULL " +
            "            AND sm.facility_id = ?1 " +
            "            AND sm.date_result_reported <= ?3 " +
            "    ) " +
            "    select patient_uuid, tb_diagnostic_result, date_of_tb_diagnostic_result_received, tb_diagnostic_test_type from cur_tb where rnk = 1 " +
            "), " +
            "ipt_start as ( " +
            "    with tpt as ( " +
            "        select hap.person_uuid, hap.visit_date AS date_of_ipt_start, " +
            "            CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name, " +
            "            CAST(pharmacy_object ->> 'duration' AS VARCHAR) AS duration, " +
            "            hrt.description, " +
            "            row_number() over (partition by hap.person_uuid order by hap.visit_date desc) rnk " +
            "        from hiv_art_pharmacy hap, " +
            "              jsonb_array_elements(hap.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object) " +
            "        RIGHT JOIN hiv_regimen hr ON hr.description = CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) " +
            "                RIGHT JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id " +
            "        where hap.archived = 0 and hap.facility_id = ?1 " +
            "          and CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) in ('Isoniazid and Rifapentine-(3HP)', 'Isoniazid 300mg', 'Isoniazid 100mg') " +
            "            and (ipt ->> 'type' ILIKE '%INITIATION%' OR ipt ->> 'type' ILIKE 'START_REFILL') and hrt.id IN (15) " +
            "    ) " +
            "    select person_uuid, date_of_ipt_start, regimen_name from tpt where rnk = 1 " +
            "), " +
            "ipt_c as ( " +
            "    select * from (select hap.person_uuid, TO_DATE(NULLIF(NULLIF(TRIM(hap.ipt->>'dateCompleted'), ''), 'null'), 'YYYY-MM-DD') AS date_completed_ipt, " +
            "           COALESCE(NULLIF(CAST(hap.ipt->>'completionStatus' AS text), ''), '') AS ipt_completion_status " +
            "    from hiv_art_pharmacy hap where hap.archived = 0 and (ipt->>'dateCompleted' IS NOT NULL or ipt->>'dateCompleted' != '')) as t " +
            "             where date_completed_ipt is not null " +
            ")," +
            "weight as (\n" +
            "    select * from (select CAST(ho.data -> 'tbIptScreening' ->> 'weightAtStartTPT' AS text) AS weight_at_start_tpt, ho.person_uuid\n" +
            "                   from hiv_observation ho\n" +
            "                   WHERE type = 'Chronic Care'\n" +
            "                     and archived = 0\n" +
            "                     and TO_DATE(NULLIF(NULLIF(TRIM(ho.data -> 'tbIptScreening' ->> 'dateTPTStart'), ''), 'null'),\n" +
            "                                 'YYYY-MM-DD') is not null) w where weight_at_start_tpt is not null\n" +
            ")\n " +
            "SELECT " +
            "    bio.uuid AS personUuid, bio.lga, bio.state, bio.hospital_number as hospitalNumber, bio.other_name as otherName, " +
            "    bio.uniqueId, bio.age, bio.gender, bio.date_of_birth as dateOfBirth, bio.surname, bio.first_name as firstName, " +
            "    bio.facility_name as facilityName, bio.datimId, bio.targetGroup, " +
            "    bio.enrollment_setting, bio.art_start_date AS artStartDate, " +
            "    bio.regimen_at_start AS regimen_at_start, bio.date_of_registration, " +
            "    tb.tb_status AS tbStatus, tb.tb_screening_type AS tbScreeningType, " +
            "    tb.date_of_tb_screened as dateOfTbScreened, tb_treatement_start.eligible_for_tpt as eligibleForTpt, " +
            "    tb_treatement_start.tb_treatment_start_date AS tbTreatmentStartDate, " +
            "    tb_treatement_start.tb_treatement_type AS tbTreatmentType, " +
            "    tb_treatement_completion.tb_completion_date AS tbTreatmentCompletionDate, " +
            "    tb_treatement_completion.tb_treatment_outcome AS tbTreatmentOutcome, " +
            "    current_tb_result.tb_diagnostic_result AS tbDiagnosticResult, " +
            "    current_tb_result.date_of_tb_diagnostic_result_received AS dateOfTbDiagnosticResultReceived, " +
            "    current_tb_result.tb_diagnostic_test_type AS tbDiagnosticTestType, " +
            "    ipt_start.date_of_ipt_start AS dateOfIptStart, ipt_start.regimen_name as regimenName, " +
            "    ipt_c.date_completed_ipt AS iptCompletionDate, " +
            "    ipt_c.ipt_completion_status AS iptCompletionStatus , weight.weight_at_start_tpt as weightAtStartTpt " +
            "FROM " +
            "    bio_data bio " +
            "LEFT JOIN tb_status tb ON bio.uuid = tb.person_uuid " +
            "LEFT JOIN tb_treatement_start ON bio.uuid = tb_treatement_start.person_uuid " +
            "LEFT JOIN tb_treatement_completion ON bio.uuid = tb_treatement_completion.person_uuid " +
            "LEFT JOIN current_tb_result ON bio.uuid = current_tb_result.patient_uuid " +
            "LEFT JOIN ipt_start ON bio.uuid = ipt_start.person_uuid " +
            "LEFT JOIN weight ON bio.uuid = weight.person_uuid " +
            "LEFT JOIN ipt_c on ipt_c.person_uuid = bio.uuid";
}
