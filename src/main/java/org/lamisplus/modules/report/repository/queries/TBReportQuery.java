package org.lamisplus.modules.report.repository.queries;

public class TBReportQuery {

    public static final String TB_REPORT_QUERY = "with bio_data as (\n" +
            "    SELECT  facility_lga.name AS lga, p.other_name, p.surname, p.first_name, \n" +
            "            facility_state.name AS state, p.uuid, p.hospital_number, h.unique_id as uniqueId,EXTRACT(YEAR FROM  AGE(NOW(), date_of_birth)) AS age, \n" +
            "            INITCAP(p.sex) AS gender,p.date_of_birth, facility.name AS facility_name, boui.code AS datimId,tgroup.display AS targetGroup, eSetting.display AS enrollment_setting, \n" +
            "            hac.visit_date AS art_start_date, hr.description AS regimen_at_start, p.date_of_registration \n" +
            "    FROM patient_person p \n" +
            "            INNER JOIN base_organisation_unit facility ON facility.id = facility_id \n" +
            "            INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id \n" +
            "            INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id \n" +
            "            INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID' \n" +
            "            INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid \n" +
            "            LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id \n" +
            "            LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id \n" +
            "            LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id \n" +
            "            INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid \n" +
            "                                                   AND hac.archived = 0 \n" +
            "            INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id \n" +
            "            INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id \n" +
            "      WHERE \n" +
            "          h.archived = 0 \n" +
            "        AND p.archived = 0 \n" +
            "        AND h.facility_id = ?1 \n" +
            "        AND hac.is_commencement = TRUE \n" +
            "        AND hac.visit_date >=  ?2\n" +
            "        AND hac.visit_date < ?3 \n" +
            "), \n" +
            "tb_status as ( \n" +
            "    with tbscreening_cs as ( \n" +
            "        with cs as ( \n" +
            "            SELECT id, person_uuid, date_of_observation AS date_of_tb_Screened, data->'tbIptScreening'->>'status' AS tb_status, \n" +
            "                data->'tbIptScreening'->>'tbScreeningType' AS tb_screening_type, data->'tbIptScreening'->>'eligibleForTPT' as eligible_for_tpt, \n" +
            "                ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS rowNums \n" +
            "        FROM hiv_observation \n" +
            "        WHERE type = 'Chronic Care' and data is not null and archived = 0 \n" +
            "            and date_of_observation between ?2 and ?3 \n" +
            "            and facility_id = ?1 \n" +
            "        ) \n" +
            "        select * from cs where rowNums = 1 \n" +
            "    ), \n" +
            "    tbscreening_hac as ( \n" +
            "        with h as (\n" +
            "            select h.id, h.person_uuid, h.visit_date, cast(h.tb_screen->>'tbStatusId' as bigint) as tb_status_id, \n" +
            "               b.display as h_status, \n" +
            "               ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY visit_date DESC) AS rowNums \n" +
            "            from hiv_art_clinical h \n" +
            "            join base_application_codeset b on b.id = cast(h.tb_screen->>'tbStatusId' as bigint) \n" +
            "            where h.archived = 0 and h.visit_date between ?2 and ?3 and facility_id = ?1 \n" +
            "        ) \n" +
            "        select * from h where rowNums = 1 \n" +
            "    ) \n" +
            "    select \n" +
            "         tcs.person_uuid, \n" +
            "         case \n" +
            "             when tcs.tb_status is not null then tcs.tb_status \n" +
            "             when tcs.tb_status is null and th.h_status is not null then th.h_status \n" +
            "         end as tb_status, \n" +
            "         case \n" +
            "             when tcs.tb_status is not null then tcs.date_of_tb_screened\n" +
            "             when tcs.tb_status is null and th.h_status is not null then th.visit_date \n" +
            "         end as date_of_tb_screened, \n" +
            "        tcs.tb_screening_type \n" +
            "        from tbscreening_cs tcs \n" +
            "             left join tbscreening_hac th on th.person_uuid = tcs.person_uuid \n" +
            "),\n" +
            "tb_treatement_start as ( \n" +
            "    with tbt as ( \n" +
            "        SELECT \n" +
            "             COALESCE(NULLIF(CAST(data->'tptMonitoring'->>'treatementType' AS text), ''), '') AS tb_treatement_type, \n" +
            "             NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'tbTreatmentStartDate', '') AS DATE), NULL) AS tb_treatment_start_date, \n" +
            "\n" +
            "             data->'tbIptScreening'->>'eligibleForTPT' as eligible_for_tpt, person_uuid, \n" +
            "             ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS row_number \n" +
            "        FROM public.hiv_observation WHERE type = 'Chronic Care' AND facility_id = ?1 and archived = 0 \n" +
            "    ) \n" +
            "    select person_uuid, tb_treatement_type, tb_treatment_start_date, eligible_for_tpt \n" +
            "    from tbt where row_number = 1 \n" +
            "), \n" +
            "tb_treatement_completion as ( \n" +
            "  select person_uuid, tb_treatment_outcome, tb_completion_date from (select CAST(data->'tptMonitoring'->>'treatmentOutcome' AS text) AS tb_treatment_outcome, \n" +
            "         NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'completionDate', '') AS DATE), NULL) AS tb_completion_date, person_uuid, \n" +
            "         ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS row_number \n" +
            "FROM public.hiv_observation WHERE type = 'Chronic Care' AND facility_id = ?1 \n" +
            "and archived = 0) ttc where row_number = 1 \n" +
            "    and tb_completion_date is not null \n" +
            "\n" +
            "), \n" +
            "current_tb_result AS ( \n" +
            "    with cur_tb as ( \n" +
            "            select sm.patient_uuid, sm.result_reported AS tb_diagnostic_result, \n" +
            "            CAST(sm.date_result_reported AS DATE) AS date_of_tb_diagnostic_result_received, \n" +
            "            CASE lt.lab_test_id \n" +
            "\t\t\tWHEN 65 THEN 'Gene Xpert'\n" +
            "\t\t\tWHEN 51 THEN 'TB-LAM'\n" +
            "\t\t\tWHEN 64 THEN 'AFB Smear Microscopy'\n" +
            "\t\t\tWHEN 67 THEN 'Gene Xpert'\n" +
            "\t\t\tWHEN 72 THEN 'TrueNAT'\n" +
            "\t\t\tWHEN 71 THEN 'LF-LAM'\n" +
            "\t\t\tWHEN 86 THEN 'Cobas'\n" +
            "\t\t\tWHEN 58 THEN 'TB-LAM'\n" +
            "            END AS tb_diagnostic_test_type, \n" +
            "            ROW_NUMBER() OVER (PARTITION BY sm.patient_uuid ORDER BY sm.date_result_reported DESC) AS rnk \n" +
            "        FROM \n" +
            "            laboratory_result sm \n" +
            "            INNER JOIN public.laboratory_test lt ON sm.test_id = lt.id \n" +
            "        WHERE \n" +
            "            lt.lab_test_id IN (65, 51, 66, 64, 67, 72, 71, 86, 58) \n" +
            "            AND sm.archived = 0 \n" +
            "            AND sm.date_result_reported IS NOT NULL \n" +
            "            AND sm.facility_id = ?1 \n" +
            "            AND sm.date_result_reported <= ?3 \n" +
            "    ) \n" +
            "    select patient_uuid, tb_diagnostic_result, date_of_tb_diagnostic_result_received, tb_diagnostic_test_type from cur_tb where rnk = 1 \n" +
            "), \n" +
            "ipt_start as ( \n" +
            "    with tpt as ( \n" +
            "        select hap.person_uuid, hap.visit_date AS date_of_ipt_start, \n" +
            "            CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name, \n" +
            "            CAST(pharmacy_object ->> 'duration' AS VARCHAR) AS duration, \n" +
            "            hrt.description, \n" +
            "            row_number() over (partition by hap.person_uuid order by hap.visit_date desc) rnk \n" +
            "        from hiv_art_pharmacy hap, \n" +
            "              jsonb_array_elements(hap.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object) \n" +
            "        RIGHT JOIN hiv_regimen hr ON hr.description = CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) \n" +
            "                RIGHT JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id \n" +
            "        where hap.archived = 0 and hap.facility_id = ?1 \n" +
            "          and CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) in ('Isoniazid and Rifapentine-(3HP)', 'Isoniazid 300mg', 'Isoniazid 100mg') \n" +
            "            and (ipt ->> 'type' ILIKE '%INITIATION%' OR ipt ->> 'type' ILIKE 'START_REFILL') and hrt.id IN (15) \n" +
            "    ) \n" +
            "    select person_uuid, date_of_ipt_start, regimen_name from tpt where rnk = 1 \n" +
            "), \n" +
            "tbTreatmentNew AS (\n" +
            "WITH tb_start AS (\n" +
            "    SELECT\n" +
            "        person_uuid AS person_uuid,\n" +
            "    date_of_observation as screeningDate,\n" +
            "        NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'tbTreatmentStartDate' , '') AS DATE), NULL) AS tbTreatmentStartDate,\n" +
            "        data->'tbIptScreening'->>'tbTestResult' AS tbDiagnosticResult,\n" +
            "    data->'tbIptScreening'->>'chestXrayResult' as chestXrayResult,\n" +
            "        data->'tbIptScreening'->>'diagnosticTestType' AS tbDiagnosticTestType,\n" +
            "    data->'tbIptScreening'->>'tbType' AS tbTreatmentType,\n" +
            "    NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'dateSpecimenSent', '') AS DATE), NULL) AS specimenSentDate,\n" +
            "    data->'tbIptScreening'->>'status' as screeningStatus,\n" +
            "    data->'tbIptScreening'->>'dateOfDiagnosticTest' as dateOfDiagnosticTest, \n" +
            "        data->'tbIptScreening'->>'tbScreeningType' AS tbScreeningType\n" +
            "    FROM\n" +
            "        hiv_observation\n" +
            "    WHERE archived = 0 AND\n" +
            "        (\n" +
            "(data->'tbIptScreening'->>'status' = 'Presumptive TB and referred for evaluation' \n" +
            " or data->'tbIptScreening'->>'status' = 'No signs or symptoms of TB')\n" +
            "and\n" +
            "        (data->'tbIptScreening'->>'outcome' = 'Presumptive TB' or data->'tbIptScreening'->>'outcome'='Not Presumptive' )\n" +
            ")\n" +
            "),\n" +
            "tb_completion AS (\n" +
            "    SELECT\n" +
            "        person_uuid AS person_uuid,\n" +
            "        NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'completionDate', '') AS DATE), NULL)  AS completionDate,\n" +
            "        data->'tbIptScreening'->>'treatmentOutcome' AS treatmentOutcome\n" +
            "    FROM\n" +
            "        hiv_observation\n" +
            "    WHERE\n" +
            "        (data->'tbIptScreening'->>'completionDate' IS NOT NULL AND data->'tbIptScreening'->>'completionDate' != '') AND\n" +
            "        (data->'tbIptScreening'->>'treatmentOutcome' IS NOT NULL AND data->'tbIptScreening'->>'treatmentOutcome' != '')\n" +
            "    AND archived =0\n" +
            ")\n" +
            "\n" +
            "SELECT\n" +
            "    COALESCE(ts.person_uuid, tc.person_uuid) AS person_uuid_tb,  -- Use COALESCE to get the person_uuid from either table\n" +
            "    ts.tbTreatmentStartDate,\n" +
            "    COALESCE(ts.tbDiagnosticResult,ts.chestXrayResult) as tbDiagnosticResult,\n" +
            "    ts.tbDiagnosticTestType,\n" +
            "    ts.tbScreeningType,\n" +
            "ts.screeningStatus,\n" +
            "ts.tbTreatmentType,\n" +
            "ts.screeningDate,\n" +
            "ts.specimenSentDate,\n" +
            "dateOfDiagnosticTest,\n" +
            "    tc.completionDate,\n" +
            "    tc.treatmentOutcome\n" +
            "FROM\n" +
            "    tb_start ts\n" +
            "FULL OUTER JOIN\n" +
            "    tb_completion tc\n" +
            "ON\n" +
            "    ts.person_uuid = tc.person_uuid  order by screeningDate desc\n" +
            "),\n" +
            "\tiptNew AS (\n" +
            "WITH tpt_completed AS (\n" +
            "\tSELECT * FROM (\n" +
            "    SELECT\n" +
            "        person_uuid AS person_uuid,\n" +
            "        data->'tptMonitoring'->>'endedTpt' AS endedTpt,\n" +
            "        NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'dateTptEnded', '') AS DATE), NULL) AS tptCompletionDate,\n" +
            "        data->'tptMonitoring'->>'outComeOfIpt' AS tptCompletionStatus,\n" +
            "        data->'tbIptScreening'->>'outcome' AS completion_tptPreventionOutcome, \n" +
            "\t \tROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY date_of_observation  DESC) rowNum\n" +
            "    FROM\n" +
            "        hiv_observation\n" +
            "    WHERE\n" +
            "        data->'tptMonitoring'->>'endedTpt' = 'Yes' AND \n" +
            "        data->'tptMonitoring'->>'dateTptEnded' IS NOT NULL AND\n" +
            "        data->'tptMonitoring'->>'dateTptEnded' != ''\n" +
            "        AND archived = 0\n" +
            "\t\t) subTc WHERE rowNum = 1\n" +
            "),\n" +
            "\n" +
            "pt_screened AS (\n" +
            "    SELECT\n" +
            "        person_uuid AS person_uuid,  -- Use person_uuid to uniquely identify each record\n" +
            "        data->'tptMonitoring'->>'tptRegimen' AS tptType,\n" +
            "        NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'dateTptStarted', '') AS DATE), NULL) AS tptStartDate,\n" +
            "        data->'tptMonitoring'->>'eligibilityTpt' AS eligibilityTpt,\n" +
            "\tROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY date_of_observation  DESC) rowNum1\n" +
            "    FROM\n" +
            "        hiv_observation\n" +
            "    WHERE\n" +
            "        (data->'tptMonitoring'->>'eligibilityTpt' IS NOT NULL AND  data->'tptMonitoring'->>'eligibilityTpt' != '') \n" +
            "        AND \n" +
            "        (data->'tbIptScreening'->>'outcome' IS NOT NULL AND data->'tbIptScreening'->>'outcome' != '' \n" +
            "        AND data->'tbIptScreening'->>'outcome' != 'Currently on TPT') AND data->'tptMonitoring'->>'dateTptStarted' != '' AND data->'tptMonitoring'->>'dateTptStarted' IS NOT NULL\n" +
            ")\n" +
            "SELECT\n" +
            "    COALESCE(tc.person_uuid, ts.person_uuid) AS person_uuid,  -- Use COALESCE to get the person_uuid from either table\n" +
            "    ts.tptType,\n" +
            "ts.tptStartDate,\n" +
            "    ts.eligibilityTpt,\n" +
            "    tc.endedTpt,\n" +
            "    tc.tptCompletionDate,\n" +
            "    tc.tptCompletionStatus\n" +
            "FROM\n" +
            "    pt_screened ts\n" +
            "FULL OUTER JOIN\n" +
            "    tpt_completed tc\n" +
            "ON\n" +
            "    ts.person_uuid = tc.person_uuid\n" +
            "), \n" +
            "\n" +
            "ipt_cA as ( \n" +
            "    with ipt_c as ( \n" +
            "       select person_uuid, date_completed as iptCompletionDate, iptCompletionStatus from ( \n" +
            "                select person_uuid, cast(ipt->>'dateCompleted' as date) as date_completed, \n" +
            "                COALESCE(NULLIF(CAST(ipt->>'completionStatus' AS text), ''), '') AS iptCompletionStatus, \n" +
            "                row_number () over (partition by person_uuid order by cast(ipt->>'dateCompleted' as  date) desc) as rnk \n" +
            "                from hiv_art_pharmacy where (ipt->>'dateCompleted' is not null and ipt->>'dateCompleted' != 'null' and ipt->>'dateCompleted' != '' AND TRIM(ipt->>'dateCompleted') <> '') \n" +
            "                and archived = 0) ic where ic.rnk = 1\n" +
            "    ), \n" +
            "\n" +
            "    ipt_c_cs as ( \n" +
            "       SELECT person_uuid, iptCompletionSCS, iptCompletionDSC \n" +
            "       FROM ( \n" +
            "       SELECT person_uuid, \n" +
            "           data->'tptMonitoring'->>'outComeOfIpt' as iptCompletionSCS, \n" +
            "           CASE \n" +
            "               WHEN (data->'tptMonitoring'->>'date') = 'null' OR (data->'tptMonitoring'->>'date') = '' OR (data->'tptMonitoring'->>'date') = ' '  THEN NULL \n" +
            "               ELSE cast(data->'tptMonitoring'->>'date' as date) \n" +
            "           END as iptCompletionDSC, \n" +
            "           ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY \n" +
            "               CASE  \n" +
            "               WHEN (data->'tptMonitoring'->>'date') = 'null' OR (data->'tptMonitoring'->>'date') = '' OR (data->'tptMonitoring'->>'date') = ' '  THEN NULL \n" +
            "               ELSE cast(data->'tptMonitoring'->>'date' as date) \n" +
            "           END  DESC) AS ipt_c_sc_rnk \n" +
            "           FROM hiv_observation \n" +
            "           WHERE type = 'Chronic Care' \n" +
            "           AND archived = 0 \n" +
            "           AND (data->'tptMonitoring'->>'date') IS NOT NULL \n" +
            "           AND (data->'tptMonitoring'->>'date') != 'null' \n" +
            "           ) AS ipt_ccs \n" +
            "          WHERE ipt_c_sc_rnk = 1\n" +
            "\n" +
            "    ) \n" +
            "    select ipt_c.person_uuid as person_uuid, coalesce(ipt_c_cs.iptCompletionDSC, ipt_c.iptCompletionDate) as dateCompletedTpt, \n" +
            "    coalesce(ipt_c_cs.iptCompletionSCS, ipt_c.iptCompletionStatus) as iptCompletionStatus \n" +
            "    from ipt_c \n" +
            "    left join ipt_c_cs on ipt_c.person_uuid = ipt_c_cs.person_uuid ), \n" +
            "weight as (\n" +
            "    select * from (select CAST(ho.data -> 'tbIptScreening' ->> 'weightAtStartTPT' AS text) AS weight_at_start_tpt, ho.person_uuid\n" +
            "                   from hiv_observation ho\n" +
            "                   WHERE type = 'Chronic Care'\n" +
            "                     and archived = 0\n" +
            "                     and TO_DATE(NULLIF(NULLIF(TRIM(ho.data -> 'tbIptScreening' ->> 'dateTPTStart'), ''), 'null'),\n" +
            "                                 'YYYY-MM-DD') is not null) w where weight_at_start_tpt is not null\n" +
            ") \n" +
            "SELECT DISTINCT ON (bio.uuid)\n" +
            "    bio.uuid AS personUuid, bio.lga, bio.state, bio.hospital_number as hospitalNumber, bio.other_name as otherName, \n" +
            "    bio.uniqueId, bio.age, bio.gender, bio.date_of_birth as dateOfBirth, bio.surname, bio.first_name as firstName, \n" +
            "    bio.facility_name as facilityName, bio.datimId, bio.targetGroup, \n" +
            "    bio.enrollment_setting, bio.art_start_date AS artStartDate, \n" +
            "    bio.regimen_at_start AS regimen_at_start, bio.date_of_registration, \n" +
            "    COALESCE(tbTmentNew.screeningStatus, tb.tb_status) AS tbStatus, COALESCE(tbTmentNew.tbScreeningType, tb.tb_screening_type) AS tbScreeningType, \n" +
            "    COALESCE(tbTmentNew.screeningDate, tb.date_of_tb_screened) as dateOfTbScreened, COALESCE(iptN.eligibilityTpt,tb_treatement_start.eligible_for_tpt) as eligibleForTpt, \n" +
            "    COALESCE(tbTmentNew.tbTreatmentStartDate, tb_treatement_start.tb_treatment_start_date) AS tbTreatmentStartDate, \n" +
            "    COALESCE(tbTmentNew.tbTreatmentType,tb_treatement_start.tb_treatement_type) AS tbTreatmentType, \n" +
            "    COALESCE(tbTmentNew.completionDate , tb_treatement_completion.tb_completion_date) AS tbTreatmentCompletionDate, \n" +
            "    COALESCE(tbTmentNew.treatmentOutcome, tb_treatement_completion.tb_treatment_outcome) AS tbTreatmentOutcome, \n" +
            "    COALESCE(tbTmentNew.tbDiagnosticResult, current_tb_result.tb_diagnostic_result) AS tbDiagnosticResult, \n" +
            "    current_tb_result.date_of_tb_diagnostic_result_received AS dateOfTbDiagnosticResultReceived, \n" +
            "    COALESCE(tbTmentNew.tbDiagnosticTestType, current_tb_result.tb_diagnostic_test_type) AS tbDiagnosticTestType, \n" +
            "    COALESCE(iptN.tptStartDate, ipt_start.date_of_ipt_start) AS dateOfIptStart, COALESCE(iptN.tptType, ipt_start.regimen_name) as regimenName, \n" +
            "    COALESCE(iptN.tptCompletionDate, ipt_cA.dateCompletedTpt) AS iptCompletionDate, \n" +
            "    COALESCE(iptN.tptCompletionStatus, ipt_cA.iptCompletionStatus) AS iptCompletionStatus , weight.weight_at_start_tpt as weightAtStartTpt \n" +
            "FROM \n" +
            "    bio_data bio \n" +
            "LEFT JOIN tb_status tb ON bio.uuid = tb.person_uuid \n" +
            "LEFT JOIN tb_treatement_start ON bio.uuid = tb_treatement_start.person_uuid \n" +
            "LEFT JOIN tb_treatement_completion ON bio.uuid = tb_treatement_completion.person_uuid \n" +
            "LEFT JOIN current_tb_result ON bio.uuid = current_tb_result.patient_uuid \n" +
            "LEFT JOIN ipt_start ON bio.uuid = ipt_start.person_uuid \n" +
            "LEFT JOIN weight ON bio.uuid = weight.person_uuid \n" +
            "LEFT JOIN ipt_cA on ipt_cA.person_uuid = bio.uuid\n" +
            "LEFT JOIN iptNew iptN ON  bio.uuid= iptN.person_uuid\n" +
            "LEFT JOIN  tbTreatmentNew tbTmentNew ON tbTmentNew.person_uuid_tb = bio.uuid";

}
