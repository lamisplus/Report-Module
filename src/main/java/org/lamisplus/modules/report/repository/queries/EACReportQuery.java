package org.lamisplus.modules.report.repository.queries;

public class EACReportQuery {

    public static final String EAC_REPORT_QUERY = "with eac_clients as ( " +
            "    WITH bio_data AS ( " +
            "    SELECT " +
            "        facility_lga.name AS lga, facility_state.name AS state, " +
            "        p.uuid as patientId, p.hospital_number as hospitalNumber, h.unique_id as uniqueId, " +
            "        EXTRACT(YEAR FROM AGE(?3, p.date_of_birth)) AS age, " +
            "        INITCAP(p.sex) AS sex, p.date_of_birth as dateOfBirth, " +
            "        facility.name AS facilityName, boui.code AS datimId, " +
            "        tgroup.display AS targetGroup, eSetting.display AS enrollmentSetting, " +
            "        hac.visit_date AS artStartDate, hr.description AS regimenAtArtStart, " +
            "        p.date_of_registration, p.surname, p.first_name,  boo.name as lgaOfResidence " +
            "    FROM " +
            "        patient_person p " +
            "    INNER JOIN " +
            "        base_organisation_unit facility ON facility.id = p.facility_id " +
            "    INNER JOIN " +
            "        base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id " +
            "    INNER JOIN " +
            "        base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id " +
            "    INNER JOIN " +
            "        base_organisation_unit_identifier boui ON boui.organisation_unit_id = p.facility_id AND boui.name='DATIM_ID' " +
            "    INNER JOIN " +
            "        hiv_enrollment h ON h.person_uuid = p.uuid " +
            "    LEFT JOIN " +
            "        base_application_codeset tgroup ON tgroup.id = h.target_group_id " +
            "    LEFT JOIN " +
            "        base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id " +
            "    LEFT JOIN " +
            "        hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid " +
            "           AND hac.archived = 0 " +
            "           AND hac.is_commencement = TRUE " +
            "           AND hac.visit_date >= ?2 " +
            "           AND hac.visit_date < ?3 " +
            "    LEFT JOIN " +
            "        hiv_regimen hr ON hr.id = hac.regimen_id " +
            "    LEFT JOIN base_organisation_unit boo on boo.id = " +
            "        CASE " +
            "            WHEN (string_to_array(p.address->'address'->0->>'district', ','))[1] ~ '^\\d+$'THEN cast(p.address->'address'->0->>'district' as bigint) " +
            "            ELSE NULL " +
            "        END " +
            "    WHERE " +
            "        p.archived = 0 " +
            "        AND p.facility_id = ?1 " +
            "    ) " +
            "    SELECT bd.* FROM bio_data bd " +
            "    JOIN " +
            "        ( " +
            "            select * from (with current_eac as ( " +
            "              select person_uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "            select * from current_eac where row = 1) as c_eac " +
            "        ) c_eac ON c_eac.person_uuid = bd.patientId " +
            "), " +
            "first_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid1, hes.eac_session_date as dateOfCommencementOfFirstEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'FIRST EAC' " +
            "), " +
            "second_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid2, hes.eac_session_date as dateOfCommencementOfSecondEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'SECOND EAC' " +
            "), " +
            "third_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid3, hes.eac_session_date as dateOfCommencementOfThirdEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'THIRD EAC' " +
            "), " +
            "fourth_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid4, hes.eac_session_date as dateOfCommencementOfFourthEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'FOURTH EAC' " +
            "), " +
            "fifth_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid5, hes.eac_session_date as dateOfCommencementOfFifthEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'FIFTH EAC' " +
            "), " +
            "sixth_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid6, hes.eac_session_date as dateOfCommencementOfSixthEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'SIXTH EAC' " +
            "), " +
            "seventh_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid7, hes.eac_session_date as dateOfCommencementOfSeventhPostSwitchCommitteeEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'SEVENTH EAC' " +
            "), " +
            "eighth_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid8, hes.eac_session_date as dateOfEighthPostSwitchCommitteeEACSessionCompleted from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'EIGHTH EAC' " +
            "), " +
            "ninth_eac as ( " +
            "    with current_eac as ( " +
            "              select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                from hiv_eac where archived = 0 " +
            "            ) " +
            "    select ce.person_uuid as person_uuid9, hes.eac_session_date as dateOfCommencementOfNinthPostSwitchCommitteeEAC from current_eac ce " +
            "        join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "             where ce.row = 1 and hes.archived = 0 and hes.status = 'NINTH EAC' " +
            "), " +
            "eac_count as (SELECT person_uuid person_uuid10, no_eac_session numberOfEACSessionsCompleted FROM (\n" +
            "SELECT person_uuid, eac_id,  no_eac_session, eac_session_date, ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY eac_session_date DESC ) AS rnkk FROM (\n" +
            "SELECT person_uuid, visit_id, eac_id, eac_session_date,COUNT(eac_id) OVER (PARTITION BY eac_id) AS no_eac_session\n" +
            "FROM hiv_eac_session WHERE archived = 0 AND eac_session_date between ?2 and ?3 AND status in ('FIRST EAC', 'SECOND EAC', 'THIRD EAC','FOURTH EAC', 'FIFTH EAC', 'SIXTH EAC') order by eac_session_date DESC) subQ \n" +
            ") countEac WHERE rnkk = 1), " +
            "post_eac_vl1 as ( " +
            "    WITH current_eac AS ( " +
            "        SELECT person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "        FROM hiv_eac " +
            "        WHERE archived = 0 " +
            "    ), " +
            "    eac_session_date AS ( " +
            "        SELECT hes.person_uuid, MAX(hes.eac_session_date) AS eac_session_date " +
            "        FROM hiv_eac_session hes " +
            "        JOIN current_eac ce ON ce.uuid = hes.eac_id " +
            "        WHERE " +
            "            ce.row = 1 " +
            "            AND hes.archived = 0 and hes.eac_session_date isnull " +
            "            AND hes.eac_session_date BETWEEN ?2 AND ?3 " +
            "            AND hes.status IN ('FIRST EAC', 'SECOND EAC', 'THIRD EAC', 'FOURTH EAC', 'FIFTH EAC', 'SIXTH EAC') " +
            "        GROUP BY " +
            "            hes.person_uuid " +
            "    ), " +
            "    vl AS ( " +
            "        SELECT " +
            "            lt.patient_uuid, " +
            "            CAST(ls.date_sample_collected AS DATE) AS date_sample_collected, " +
            "            lr.result_reported, " +
            "            CAST(lr.date_result_reported AS DATE) AS date_result_reported, " +
            "            ROW_NUMBER() OVER (PARTITION BY lt.patient_uuid ORDER BY lr.date_result_reported DESC) AS row "
            +
            "        FROM laboratory_test lt " +
            "        LEFT JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.archived = 0 " +
            "        LEFT JOIN laboratory_result lr ON lr.test_id = lt.id AND lr.date_result_reported BETWEEN ?2 AND ?3 " +
            "        WHERE lt.viral_load_indication = 302 AND lt.archived = 0 " +
            "    ) " +
            "    SELECT " +
            "        pev.person_uuid as person_uuid11, " +
            "        pev.dateOfRepeatViralLoadResultPostEACVL, " +
            "        pev.dateOfRepeatViralLoadPostEACSampleCollected, " +
            "        pev.repeatViralLoadResultPostEAC " +
            "    FROM ( " +
            "        SELECT " +
            "            ed.person_uuid, " +
            "            vl.date_result_reported AS dateOfRepeatViralLoadResultPostEACVL, " +
            "            vl.date_sample_collected AS dateOfRepeatViralLoadPostEACSampleCollected, " +
            "            vl.result_reported AS repeatViralLoadResultPostEAC, " +
            "            ROW_NUMBER() OVER (PARTITION BY vl.patient_uuid ORDER BY vl.date_result_reported DESC) AS row " +
            "        FROM eac_session_date ed " +
            "        JOIN vl ON vl.patient_uuid = ed.person_uuid AND vl.date_result_reported <= ed.eac_session_date " +
            "    ) pev WHERE pev.row = 1 " +
            "), " +
            "post_eac_vl2 as ( " +
            "    WITH current_eac AS ( " +
            "        SELECT person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "        FROM hiv_eac " +
            "        WHERE archived = 0 " +
            "    ), " +
            "    eac_session_date AS ( " +
            "        SELECT hes.person_uuid, MAX(hes.eac_session_date) AS eac_session_date " +
            "        FROM hiv_eac_session hes " +
            "        JOIN current_eac ce ON ce.uuid = hes.eac_id " +
            "        WHERE " +
            "            ce.row = 1 " +
            "            AND hes.archived = 0 and hes.eac_session_date isnull " +
            "            AND hes.eac_session_date BETWEEN ?2 AND ?3 " +
            "            AND hes.status IN ('SEVENTH EAC', 'EIGHTH EAC', 'NINTH EAC') " +
            "        GROUP BY " +
            "            hes.person_uuid " +
            "    ), " +
            "    vl AS ( " +
            "        SELECT " +
            "            lt.patient_uuid, " +
            "            CAST(ls.date_sample_collected AS DATE) AS date_sample_collected, " +
            "            lr.result_reported, " +
            "            CAST(lr.date_result_reported AS DATE) AS date_result_reported, " +
            "            ROW_NUMBER() OVER (PARTITION BY lt.patient_uuid ORDER BY lr.date_result_reported DESC) AS row " +
            "        FROM laboratory_test lt " +
            "        LEFT JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.archived = 0 " +
            "        LEFT JOIN laboratory_result lr ON lr.test_id = lt.id AND lr.date_result_reported BETWEEN ?2 AND ?3 " +
            "        WHERE lt.viral_load_indication = 302 AND lt.archived = 0 " +
            "    ) " +
            "    SELECT " +
            "        pev.person_uuid as person_uuid12, " +
            "        pev.dateOfRepeatViralLoadResultPostSwitchEACVL, " +
            "        pev.dateOfRepeatViralLoadPostSwitchEACSampleCollected, " +
            "        pev.repeatViralLoadResultPostSwitchEAC " +
            "    FROM ( " +
            "        SELECT " +
            "            ed.person_uuid, " +
            "            vl.date_result_reported AS dateOfRepeatViralLoadResultPostSwitchEACVL, " +
            "            vl.date_sample_collected AS dateOfRepeatViralLoadPostSwitchEACSampleCollected, " +
            "            vl.result_reported AS repeatViralLoadResultPostSwitchEAC, " +
            "            ROW_NUMBER() OVER (PARTITION BY vl.patient_uuid ORDER BY vl.date_result_reported DESC) AS row " +
            "        FROM eac_session_date ed " +
            "        JOIN vl ON vl.patient_uuid = ed.person_uuid AND vl.date_result_reported <= ed.eac_session_date " +
            "    ) pev WHERE pev.row = 1 " +
            "), " +
            "regimen_at_start as ( " +
            "        select sr1.person_uuid as person_uuid13, sr1.dateOfStartOfRegimenBeforeUnsuppressedVLR, " +
            "           sr1.regimenBeforeUnsuppression, sr1.regimenLineBeforeUnsuppression from (with current_eac as ( " +
            "        select person_uuid, uuid, date_of_last_viral_load, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                    from hiv_eac where archived = 0 " +
            "    ), " +
            "    regimen as ( " +
            "       SELECT hap.person_uuid, hap.visit_date AS dateOfStartOfRegimenBeforeUnsuppressedVLR, " +
            "                    r.description AS regimenLineBeforeUnsuppression, " +
            "                    rt.description AS regimenBeforeUnsuppression " +
            "            FROM hiv_art_pharmacy hap " +
            "             INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id " +
            "             INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id " +
            "             INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id " +
            "                     WHERE r.regimen_type_id in (1,2,3,4,14) and hap.visit_date between ?2 and ?3 " +
            "            AND archived = 0 ORDER BY visit_date " +
            "    ) " +
            "    select ce.person_uuid, r.dateOfStartOfRegimenBeforeUnsuppressedVLR, " +
            "           r.regimenBeforeUnsuppression, r.regimenLineBeforeUnsuppression, " +
            "           ROW_NUMBER() OVER (PARTITION BY r.person_uuid ORDER BY r.dateOfStartOfRegimenBeforeUnsuppressedVLR) AS row1 " +
            "    from current_eac ce " +
            "    join regimen r on r.person_uuid = ce.person_uuid and r.dateOfStartOfRegimenBeforeUnsuppressedVLR < ce.date_of_last_viral_load where ce.row = 1 " +
            "    ) sr1 where sr1.row1 = 1 " +
            "), " +
            "last_pick as ( " +
            "    select sr.person_uuid as person_uuid14, sr.lastPickupDateBeforeUnsuppressedVLR, " +
            "           sr.monthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR from (with current_eac as ( " +
            "        select person_uuid, uuid, date_of_last_viral_load, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                    from hiv_eac where archived = 0 " +
            "    ), " +
            "    regimen as ( " +
            "       SELECT hap.person_uuid, hap.visit_date AS lastPickupDateBeforeUnsuppressedVLR, " +
            "                    CAST(hap.refill_period /30.0 AS DECIMAL(10,1)) AS monthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR " +
            "            FROM hiv_art_pharmacy hap " +
            "             INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id " +
            "             INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id " +
            "             INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id " +
            "                     WHERE r.regimen_type_id in (1,2,3,4,14) and hap.visit_date between ?2 and ?3 " +
            "            AND archived = 0 ORDER BY visit_date " +
            "    ) " +
            "    select ce.person_uuid, r.lastPickupDateBeforeUnsuppressedVLR, " +
            "           r.monthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR, " +
            "           ROW_NUMBER() OVER (PARTITION BY r.person_uuid ORDER BY r.lastPickupDateBeforeUnsuppressedVLR DESC) AS row1 " +
            "    from current_eac ce " +
            "    join regimen r on r.person_uuid = ce.person_uuid and r.lastPickupDateBeforeUnsuppressedVLR < ce.date_of_last_viral_load where ce.row = 1 " +
            "    ) sr where sr.row1 = 1 " +
            "), " +
            "vl_unsuppressed as ( " +
            "    select " +
            "        fuvl.person_uuid as person_uuid15, fuvl.result_reported as mostRecentUnsuppressedVLR, " +
            "        fuvl.date_result_reported as dateOfUnsuppressedVLR, " +
            "        fuvl.date_sample_collected as dateOfVLSCOfUnsuppressedVLR, " +
            "        fuvl.indication as unsuppressedVLRIndication " +
            "        from (with date_first_eac as ( " +
            "        with current_eac as ( " +
            "                  select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row " +
            "                    from hiv_eac where archived = 0 " +
            "                ) " +
            "        select ce.person_uuid, hes.eac_session_date as dateOfCommencementOfFirstEAC from current_eac ce " +
            "            join hiv_eac_session hes on hes.eac_id = ce.uuid " +
            "                 where ce.row = 1 and hes.archived = 0 and hes.status = 'FIRST EAC' " +
            "    ), " +
            "    vl as ( " +
            "        SELECT " +
            "                lt.patient_uuid, " +
            "                CAST(ls.date_sample_collected AS DATE) AS date_sample_collected, " +
            "                lr.result_reported, " +
            "                CAST(lr.date_result_reported AS DATE) AS date_result_reported, " +
            "                bac.display as indication " +
            "            FROM laboratory_test lt " +
            "            LEFT JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.archived = 0 " +
            "            LEFT JOIN laboratory_result lr ON lr.test_id = lt.id AND lr.date_result_reported BETWEEN ?2 AND ?3 and lr.archived = 0 " +
            "            LEFT JOIN base_application_codeset bac on bac.id = lt.viral_load_indication " +
            "            WHERE lt.viral_load_indication not in (302, 719) and lt.viral_load_indication is not null AND lt.archived = 0 " +
            "            and lr.archived = 0 and ls.archived = 0 " +
            "    ) " +
            "    select " +
            "        dfe.person_uuid, vl.result_reported, vl.date_result_reported, vl.date_sample_collected, vl.indication, " +
            "        ROW_NUMBER() OVER (PARTITION BY vl.patient_uuid ORDER BY vl.date_result_reported DESC) AS row1 " +
            "    from date_first_eac dfe " +
            "    join vl on vl.patient_uuid = dfe.person_uuid and vl.date_result_reported <= dfe.dateOfCommencementOfFirstEAC) fuvl where row1 = 1 " +
            "), " +
            "case_manager AS (SELECT DISTINCT ON (cmp.person_uuid)person_uuid AS caseperson, cmp.case_manager_id, CONCAT(cm.first_name, ' ', cm.last_name) AS caseManager FROM (SELECT person_uuid, case_manager_id, " +
            "ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY id DESC) " +
            "FROM case_manager_patients) cmp  INNER JOIN case_manager cm ON cm.id=cmp.case_manager_id " + 
            "WHERE cmp.row_number=1 AND cm.facility_id=?1) " +
            "SELECT DISTINCT * " +
            "FROM eac_clients " +
            "LEFT JOIN first_eac ON eac_clients.patientId = first_eac.person_uuid1 " +
            "LEFT JOIN second_eac ON eac_clients.patientId = second_eac.person_uuid2 " +
            "LEFT JOIN third_eac ON eac_clients.patientId = third_eac.person_uuid3 " +
            "LEFT JOIN fourth_eac ON eac_clients.patientId = fourth_eac.person_uuid4 " +
            "LEFT JOIN fifth_eac ON eac_clients.patientId = fifth_eac.person_uuid5 " +
            "LEFT JOIN sixth_eac ON eac_clients.patientId = sixth_eac.person_uuid6 " +
            "LEFT JOIN seventh_eac ON eac_clients.patientId = seventh_eac.person_uuid7 " +
            "LEFT JOIN eighth_eac ON eac_clients.patientId = eighth_eac.person_uuid8 " +
            "LEFT JOIN ninth_eac ON eac_clients.patientId = ninth_eac.person_uuid9 " +
            "LEFT JOIN eac_count ON eac_clients.patientId = eac_count.person_uuid10 " +
            "LEFT JOIN post_eac_vl1 ON eac_clients.patientId = post_eac_vl1.person_uuid11 " +
            "LEFT JOIN post_eac_vl2 ON eac_clients.patientId = post_eac_vl2.person_uuid12 " +
            "LEFT JOIN regimen_at_start ON eac_clients.patientId = regimen_at_start.person_uuid13 " +
            "LEFT JOIN last_pick ON eac_clients.patientId = last_pick.person_uuid14 " +
            "LEFT JOIN case_manager cm ON cm.caseperson = eac_clients.patientId " +
            "LEFT JOIN vl_unsuppressed ON eac_clients.patientId = vl_unsuppressed.person_uuid15";
}