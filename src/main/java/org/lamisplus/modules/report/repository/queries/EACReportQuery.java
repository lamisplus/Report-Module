package org.lamisplus.modules.report.repository.queries;

public class EACReportQuery {

    public static final String EAC_REPORT_QUERY = "with eac_clients as ( \n" +
            "     WITH bio_data AS ( \n" +
            "    SELECT \n" +
            "        facility_lga.name AS lga, facility_state.name AS state, p.uuid as patientId, p.hospital_number as hospitalNumber, h.unique_id as uniqueId, \n" +
            "        EXTRACT(YEAR FROM AGE(?3, p.date_of_birth)) AS age, INITCAP(p.sex) AS sex, p.date_of_birth as dateOfBirth, boo.name as lgaOfResidence, \n" +
            "        facility.name AS facilityName, boui.code AS datimId, hac.visit_date AS artStartDate, hr.description AS regimenAtArtStart, p.date_of_registration\n" +
            "    FROM \n" +
            "        patient_person p \n" +
            "    INNER JOIN \n" +
            "        base_organisation_unit facility ON facility.id = p.facility_id \n" +
            "    INNER JOIN \n" +
            "        base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id \n" +
            "    INNER JOIN \n" +
            "        base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id \n" +
            "    INNER JOIN \n" +
            "        base_organisation_unit_identifier boui ON boui.organisation_unit_id = p.facility_id AND boui.name='DATIM_ID' \n" +
            "    INNER JOIN \n" +
            "        hiv_enrollment h ON h.person_uuid = p.uuid \n" +
            "    LEFT JOIN \n" +
            "        base_application_codeset tgroup ON tgroup.id = h.target_group_id \n" +
            "    LEFT JOIN \n" +
            "        base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id \n" +
            "    LEFT JOIN \n" +
            "        hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid \n" +
            "           AND hac.archived = 0 \n" +
            "           AND hac.is_commencement = TRUE \n" +
            "           AND hac.visit_date >= ?2 \n" +
            "           AND hac.visit_date < ?3\n" +
            "    LEFT JOIN \n" +
            "        hiv_regimen hr ON hr.id = hac.regimen_id \n" +
            "    LEFT JOIN base_organisation_unit boo on boo.id = \n" +
            "        CASE \n" +
            "            WHEN (string_to_array(p.address->'address'->0->>'district', ','))[1] ~ '^\\\\d+$'THEN cast(p.address->'address'->0->>'district' as bigint) \n" +
            "            ELSE NULL \n" +
            "        END \n" +
            "    WHERE \n" +
            "        p.archived = 0 \n" +
            "        AND p.facility_id = ?1 \n" +
            "    ) \n" +
            "    SELECT bd.*, \n" +
            "\teac.dateOfCommencementOfFirstEAC, eac.dateOfCommencementOfSecondEAC, eac.dateOfCommencementOfThirdEAC, eac.dateOfCommencementOfFourthEAC, eac.dateOfCommencementOfFifthEAC, eac.dateOfCommencementOfSixthEAC,\n" +
            "\teac.numberOfEACSessionCompleted, eac.dateOfRepeatViralLoadPostEACSampleCollected, eac.repeatViralLoadResultPostEAC, eac.dateOfRepeatViralLoadResultPostEACVL\n" +
            "\tFROM bio_data bd \n" +
            "\tJOIN (\n" +
            "SELECT enrolledEac.facility_id, enrolledEac.person_uuid personUuid50, enrolledEac.uuid,\n" +
            "firstEac.sessionDate dateOfCommencementOfFirstEAC, secondEac.sessionDate dateOfCommencementOfSecondEAC, thirdEac.sessionDate dateOfCommencementOfThirdEAC, fourthEac.sessionDate dateOfCommencementOfFourthEAC, fifthEac.sessionDate dateOfCommencementOfFifthEAC, sixthEac.sessionDate dateOfCommencementOfSixthEAC,\n" +
            "(\n" +
            "CASE WHEN eacSession.status = 'FIRST EAC' THEN  0\n" +
            "WHEN eacSession.status = 'SECOND EAC' THEN 1\n" +
            "WHEN eacSession.status = 'THIRD EAC' THEN 2\n" +
            "WHEN eacSession.status = 'FOURTH EAC' THEN 3\n" +
            "WHEN eacSession.status = 'FIFTH EAC' THEN 4\n" +
            "WHEN eacSession.status = 'SIXTH EAC' THEN 5\n" +
            "END\n" +
            ") numberOfEACSessionCompleted, COALESCE (fifthEac.sessionDate,sixthEac.sessionDate) dateOfExtendEACCompletion, postEacVl.date_sample_collected,\n" +
            "(CASE WHEN postEacVl.date_sample_collected >= fourthEac.sessionDate THEN postEacVl.date_sample_collected END) dateOfRepeatViralLoadPostEACSampleCollected, (CASE WHEN postEacVl.date_sample_collected >= fourthEac.sessionDate THEN postEacVl.result_reported END) repeatViralLoadResultPostEAC, \n" +
            "(CASE WHEN postEacVl.date_sample_collected >= fourthEac.sessionDate THEN postEacVl.date_result_reported END) dateOfRepeatViralLoadResultPostEACVL, eacSession.status, eacSession.eac_session_date\n" +
            "FROM \n" +
            "hiv_eac enrolledEac\n" +
            "INNER JOIN (\n" +
            "SELECT person_uuid, eac_id, eac_session_date, status, ROW_NUMBER() OVER (PARTITION BY eac_id, person_uuid ORDER BY eac_session_date DESC) eacRank\n" +
            "FROM hiv_eac_session\n" +
            "WHERE archived = 0 AND status IS NOT NULL AND eac_session_date BETWEEN ?2 AND ?3\n" +
            ") eacSession ON eacSession.eac_id = enrolledEac.uuid AND eacSession.eacRank = 1\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, eac_id, eac_session_date sessionDate, status FROM hiv_eac_session WHERE archived = 0 AND status = 'FIRST EAC'\n" +
            ") firstEac ON firstEac.eac_id = eacSession.eac_id\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, eac_id, eac_session_date sessionDate, status FROM hiv_eac_session WHERE archived = 0 AND status = 'SECOND EAC'\n" +
            ") secondEac ON secondEac.eac_id = eacSession.eac_id\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, eac_id, eac_session_date sessionDate, status FROM hiv_eac_session WHERE archived = 0 AND status = 'THIRD EAC'\n" +
            ") thirdEac ON thirdEac.eac_id = eacSession.eac_id\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, eac_id, eac_session_date sessionDate, status FROM hiv_eac_session WHERE archived = 0 AND status = 'FOURTH EAC' \n" +
            ") fourthEac ON fourthEac.eac_id = eacSession.eac_id\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, eac_id, eac_session_date sessionDate, status FROM hiv_eac_session WHERE archived = 0 AND status = 'FIFTH EAC'\n" +
            ") fifthEac ON fifthEac.eac_id = eacSession.eac_id\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, eac_id, eac_session_date sessionDate, status FROM hiv_eac_session WHERE archived = 0 AND status = 'SIXTH EAC'\n" +
            ") sixthEac ON sixthEac.eac_id = eacSession.eac_id\n" +
            "LEFT JOIN (\n" +
            "select * from(\n" +
            "  SELECT CAST(ls.date_sample_collected AS DATE ) AS date_sample_collected, sm.patient_uuid as patient_uuid , sm.facility_id as vlFacility, sm.archived as vlArchived, acode.display as viralLoadIndication, sm.result_reported as result_reported, CAST(sm.date_result_reported AS DATE) as date_result_reported,\n" +
            "ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY ls.date_sample_collected DESC) as row\n" +
            "  FROM public.laboratory_result  sm\n" +
            " INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "  INNER JOIN public.laboratory_sample ls on ls.test_id = lt.id\n" +
            " INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication\n" +
            "  WHERE lt.lab_test_id = 16 AND CAST(ls.date_sample_collected AS DATE) BETWEEN ?2 AND ?3\n" +
            "AND  lt.viral_load_indication IN (302, 305, 304) AND CAST(sm. date_result_reported AS DATE) <= ?3\n" +
            ") pe where row = 1\n" +
            ") postEacVl ON postEacVl.patient_uuid = enrolledEac.person_uuid\n" +
            "WHERE archived = 0\n" +
            "\t) eac ON eac.personUuid50 = bd.patientId\n" +
            "\t),\n" +
            "  \n" +
            "post_eac_vl2 as ( \n" +
            "    WITH current_eac AS ( \n" +
            "        SELECT person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row \n" +
            "        FROM hiv_eac \n" +
            "        WHERE archived = 0 \n" +
            "    ), \n" +
            "    eac_session_date AS ( \n" +
            "        SELECT hes.person_uuid, MAX(hes.eac_session_date) AS eac_session_date \n" +
            "        FROM hiv_eac_session hes \n" +
            "        JOIN current_eac ce ON ce.uuid = hes.eac_id \n" +
            "        WHERE \n" +
            "            ce.row = 1 \n" +
            "            AND hes.archived = 0 and hes.eac_session_date is not null \n" +
            "            AND hes.eac_session_date BETWEEN ?2 AND ?3\n" +
            "            AND hes.status IN ('SEVENTH EAC', 'EIGHTH EAC', 'NINTH EAC') \n" +
            "        GROUP BY \n" +
            "            hes.person_uuid \n" +
            "    ), \n" +
            "    vl AS ( \n" +
            "        select * from(\n" +
            "  SELECT CAST(ls.date_sample_collected AS DATE ) AS date_sample_collected, sm.patient_uuid as patient_uuid , sm.facility_id as vlFacility, sm.archived as vlArchived, acode.display as viralLoadIndication, sm.result_reported as result_reported, CAST(sm.date_result_reported AS DATE) as date_result_reported,\n" +
            "  ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY ls.date_sample_collected DESC) as row\n" +
            "  FROM public.laboratory_result  sm\n" +
            "  INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "  INNER JOIN public.laboratory_sample ls on ls.test_id = lt.id\n" +
            "   INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication\n" +
            "  WHERE lt.lab_test_id = 16 AND CAST(ls.date_sample_collected AS DATE) BETWEEN ?2 AND ?3\n" +
            "AND  lt.viral_load_indication IN (302, 305, 304) \n" +
            "AND CAST(sm. date_result_reported AS DATE) <= ?3\n" +
            ") pe where row = 1    ) \n" +
            "    SELECT \n" +
            "        pev.person_uuid as person_uuid12, \n" +
            "        pev.dateOfRepeatViralLoadResultPostSwitchEACVL, \n" +
            "        pev.dateOfRepeatViralLoadPostSwitchEACSampleCollected, \n" +
            "        pev.repeatViralLoadResultPostSwitchEAC \n" +
            "    FROM ( \n" +
            "        SELECT \n" +
            "            ed.person_uuid, \n" +
            "            vl.date_result_reported AS dateOfRepeatViralLoadResultPostSwitchEACVL, \n" +
            "            vl.date_sample_collected AS dateOfRepeatViralLoadPostSwitchEACSampleCollected, \n" +
            "            vl.result_reported AS repeatViralLoadResultPostSwitchEAC, \n" +
            "            ROW_NUMBER() OVER (PARTITION BY vl.patient_uuid ORDER BY vl.date_result_reported DESC) AS row \n" +
            "        FROM eac_session_date ed \n" +
            "        JOIN vl ON vl.patient_uuid = ed.person_uuid AND vl.date_result_reported <= ed.eac_session_date \n" +
            "    ) pev WHERE pev.row = 1 \n" +
            "), \n" +
            "regimen_at_start as ( \n" +
            "        select sr1.person_uuid as person_uuid13, sr1.dateOfStartOfRegimenBeforeUnsuppressedVLR, \n" +
            "           sr1.regimenBeforeUnsuppression, sr1.regimenLineBeforeUnsuppression from (with current_eac as ( \n" +
            "        select person_uuid, uuid, date_of_last_viral_load, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row \n" +
            "                    from hiv_eac where archived = 0 \n" +
            "    ), \n" +
            "    regimen as ( \n" +
            "       SELECT hap.person_uuid, hap.visit_date AS dateOfStartOfRegimenBeforeUnsuppressedVLR, \n" +
            "                    r.description AS regimenLineBeforeUnsuppression, \n" +
            "                    rt.description AS regimenBeforeUnsuppression \n" +
            "            FROM hiv_art_pharmacy hap \n" +
            "             INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id \n" +
            "             INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "             INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "                     WHERE r.regimen_type_id in (1,2,3,4,14) and hap.visit_date between ?2 and ?3\n" +
            "            AND archived = 0 ORDER BY visit_date \n" +
            "    ) \n" +
            "    select ce.person_uuid, r.dateOfStartOfRegimenBeforeUnsuppressedVLR, \n" +
            "           r.regimenBeforeUnsuppression, r.regimenLineBeforeUnsuppression, \n" +
            "           ROW_NUMBER() OVER (PARTITION BY r.person_uuid ORDER BY r.dateOfStartOfRegimenBeforeUnsuppressedVLR) AS row1 \n" +
            "    from current_eac ce \n" +
            "    join regimen r on r.person_uuid = ce.person_uuid and r.dateOfStartOfRegimenBeforeUnsuppressedVLR < ce.date_of_last_viral_load where ce.row = 1 \n" +
            "    ) sr1 where sr1.row1 = 1 \n" +
            "), \n" +
            "last_pick as ( \n" +
            "    select sr.person_uuid as person_uuid14, sr.lastPickupDateBeforeUnsuppressedVLR, \n" +
            "           sr.monthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR from (with current_eac as ( \n" +
            "        select person_uuid, uuid, date_of_last_viral_load, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row \n" +
            "                    from hiv_eac where archived = 0 \n" +
            "    ), \n" +
            "    regimen as ( \n" +
            "       SELECT hap.person_uuid, hap.visit_date AS lastPickupDateBeforeUnsuppressedVLR, \n" +
            "                    CAST(hap.refill_period /30.0 AS DECIMAL(10,1)) AS monthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR \n" +
            "            FROM hiv_art_pharmacy hap \n" +
            "             INNER JOIN public.hiv_art_pharmacy_regimens pr ON pr.art_pharmacy_id = hap.id \n" +
            "             INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "             INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "                     WHERE r.regimen_type_id in (1,2,3,4,14) and hap.visit_date between ?2 and ?3\n" +
            "            AND archived = 0 ORDER BY visit_date \n" +
            "    ) \n" +
            "    select ce.person_uuid, r.lastPickupDateBeforeUnsuppressedVLR, \n" +
            "           r.monthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR, \n" +
            "           ROW_NUMBER() OVER (PARTITION BY r.person_uuid ORDER BY r.lastPickupDateBeforeUnsuppressedVLR DESC) AS row1 \n" +
            "    from current_eac ce \n" +
            "    join regimen r on r.person_uuid = ce.person_uuid and r.lastPickupDateBeforeUnsuppressedVLR < ce.date_of_last_viral_load where ce.row = 1 \n" +
            "    ) sr where sr.row1 = 1 \n" +
            "), \n" +
            "vl_unsuppressed as ( \n" +
            "    select \n" +
            "        fuvl.person_uuid as person_uuid15, fuvl.result_reported as mostRecentUnsuppressedVLR, \n" +
            "        fuvl.date_result_reported as dateOfUnsuppressedVLR, \n" +
            "        fuvl.date_sample_collected as dateOfVLSCOfUnsuppressedVLR, \n" +
            "        fuvl.indication as unsuppressedVLRIndication \n" +
            "        from (with date_first_eac as ( \n" +
            "        with current_eac as ( \n" +
            "                  select person_uuid, uuid, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row \n" +
            "                    from hiv_eac where archived = 0 \n" +
            "                ) \n" +
            "        select ce.person_uuid, hes.eac_session_date as dateOfCommencementOfFirstEAC from current_eac ce \n" +
            "            join hiv_eac_session hes on hes.eac_id = ce.uuid \n" +
            "                 where ce.row = 1 and hes.archived = 0 and hes.status = 'FIRST EAC' \n" +
            "    ), \n" +
            "    vl as ( \n" +
            "        SELECT \n" +
            "                lt.patient_uuid, \n" +
            "                CAST(ls.date_sample_collected AS DATE) AS date_sample_collected, \n" +
            "                lr.result_reported, \n" +
            "                CAST(lr.date_result_reported AS DATE) AS date_result_reported, \n" +
            "                bac.display as indication \n" +
            "            FROM laboratory_test lt \n" +
            "            LEFT JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.archived = 0 \n" +
            "            LEFT JOIN laboratory_result lr ON lr.test_id = lt.id AND lr.date_result_reported BETWEEN ?2 AND ?3 and lr.archived = 0 \n" +
            "            LEFT JOIN base_application_codeset bac on bac.id = lt.viral_load_indication \n" +
            "            WHERE lt.viral_load_indication not in (302, 719) and lt.viral_load_indication is not null AND lt.archived = 0 \n" +
            "            and lr.archived = 0 and ls.archived = 0 \n" +
            "    ) \n" +
            "    select \n" +
            "        dfe.person_uuid, vl.result_reported, vl.date_result_reported, vl.date_sample_collected, vl.indication, \n" +
            "        ROW_NUMBER() OVER (PARTITION BY vl.patient_uuid ORDER BY vl.date_result_reported DESC) AS row1 \n" +
            "    from date_first_eac dfe \n" +
            "    join vl on vl.patient_uuid = dfe.person_uuid and vl.date_result_reported <= dfe.dateOfCommencementOfFirstEAC) fuvl where row1 = 1 \n" +
            "), \n" +
            "case_manager AS (SELECT DISTINCT ON (cmp.person_uuid)person_uuid AS caseperson, cmp.case_manager_id, CONCAT(cm.first_name, ' ', cm.last_name) AS caseManager FROM (SELECT person_uuid, case_manager_id, \n" +
            "ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY id DESC) \n" +
            "FROM case_manager_patients) cmp  INNER JOIN case_manager cm ON cm.id=cmp.case_manager_id  \n" +
            "WHERE cmp.row_number=1 AND cm.facility_id= ?1) \n" +
            "SELECT DISTINCT * \n" +
            "FROM eac_clients \n" +
            "LEFT JOIN post_eac_vl2 ON eac_clients.patientId = post_eac_vl2.person_uuid12 \n" +
            "LEFT JOIN regimen_at_start ON eac_clients.patientId = regimen_at_start.person_uuid13 \n" +
            "LEFT JOIN last_pick ON eac_clients.patientId = last_pick.person_uuid14 \n" +
            "LEFT JOIN case_manager cm ON cm.caseperson = eac_clients.patientId \n" +
            "LEFT JOIN vl_unsuppressed ON eac_clients.patientId = vl_unsuppressed.person_uuid15";
}