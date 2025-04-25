package org.lamisplus.modules.report.repository.queries;

public class ARTPATIENTReportQuery {

    public static final String ART_PATIENT_REPORT_QUERY =  "WITH bio_data AS (\n" +
            "SELECT \n" +
            "DISTINCT ON (p.uuid) p.uuid as personUuid, \n" +
            "p.id, \n" +
            "CAST(p.archived AS BOOLEAN) as archived, \n" +
            "p.uuid, \n" +
            "p.hospital_number as hospitalNumber, \n" +
            "p.surname, \n" +
            "p.first_name as firstName, \n" +
            "EXTRACT(\n" +
            "  YEAR \n" +
            "  from \n" +
            "AGE(NOW(), date_of_birth)\n" +
            ") as age, \n" +
            "p.other_name as otherName, \n" +
            "p.sex as sex, \n" +
            "p.date_of_birth as dateOfBirth, \n" +
            "p.date_of_registration as dateOfRegistration, \n" +
            "p.marital_status ->> 'display' as maritalStatus, \n" +
            "education ->> 'display' as education, \n" +
            "p.employment_status ->> 'display' as occupation, \n" +
            "facility.name as facilityName, \n" +
            "facility_lga.name as lga, \n" +
            "facility_state.name as state, \n" +
            "boui.code as datimId, \n" +
            "res_state.name as residentialState, \n" +
            "res_lga.name as residentialLga, \n" +
            "r.address as address, \n" +
            "p.contact_point -> 'contactPoint' -> 0 -> 'value' ->> 0 AS phone \n" +
            "  FROM \n" +
            "patient_person p \n" +
            "INNER JOIN (\n" +
            "  SELECT \n" +
            "* \n" +
            "  FROM \n" +
            "(\n" +
            "   SELECT\n" +
            "p.id,\n" +
            "CASE WHEN address_object->>'city' IS NOT NULL\n" +
            " THEN CONCAT_WS(' ', address_object->>'city', \n" +
            "REPLACE(REPLACE(COALESCE(NULLIF(address_object->>'line', '\\\\\\\\'), ''), ']', ''), '[', ''), \n" +
            "\n" +
            "NULLIF(NULLIF(address_object->>'stateId', 'null'), '')) \n" +
            " ELSE NULL \n" +
            "END AS address,\n" +
            "CASE WHEN address_object->>'stateId' ~ '^[0-9.]+$' \n" +
            " THEN address_object->>'stateId' \n" +
            " ELSE NULL \n" +
            "END AS stateId,\n" +
            "CASE WHEN address_object->>'district' ~ '^[0-9.]+$'  \n" +
            " THEN address_object->>'district' \n" +
            " ELSE NULL \n" +
            "END AS lgaId\n" +
            "FROM patient_person p\n" +
            "CROSS JOIN jsonb_array_elements(p.address->'address') AS l(address_object)\n" +
            ")  as result\n" +
            " ) r ON r.id=p.id\n" +
            " INNER JOIN base_organisation_unit facility ON facility.id=facility_id\n" +
            " INNER JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id\n" +
            "\n" +
            " INNER JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id\n" +
            "\n" +
            " LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateId AS BIGINT)\n" +
            "\n" +
            " LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(CASE WHEN r.lgaId ~ '^[0-9.]+$' THEN r.lgaId ELSE NULL END AS BIGINT)\n" +
            "\n" +
            " INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=facility.id AND boui.name='DATIM_ID'\n" +
            "\n" +
            " INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            " WHERE p.archived=0 AND h.archived=0 AND h.facility_id=?1\n" +
            "),\n" +
            " enrollment_details AS (\n" +
            " SELECT h.person_uuid,h.unique_id as uniqueId,  sar.display as statusAtRegistration, date_confirmed_hiv as dateOfConfirmedHiv,\n" +
            "\n" +
            " ep.display AS entryPoint, date_of_registration as dateOfRegistration\n" +
            " FROM hiv_enrollment h\n" +
            " LEFT JOIN base_application_codeset sar ON sar.id=h.status_at_registration_id\n" +
            " LEFT JOIN base_application_codeset ep ON ep.id=h.entry_point_id\n" +
            " WHERE h.archived=0 AND h.facility_id=?1\n" +
            " ),\n" +
            " laboratory_details AS ( SELECT DISTINCT ON (lo.patient_uuid)\n" +
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
            "        LEFT JOIN laboratory_labtest ll ON ll.id = lt.lab_test_id AND ll.lab_test_name = 'Viral Load'\n" +
            "\n" +
            "        LEFT JOIN laboratory_sample ls ON ls.test_id = lt.id AND ls.patient_uuid = lo.patient_uuid\n" +
            "\n" +
            "        LEFT JOIN laboratory_result lr ON lr.test_id = lt.id AND lr.patient_uuid = lo.patient_uuid\n" +
            "\n" +
            "        LEFT JOIN base_application_codeset bac_viral_load ON bac_viral_load.id = lt.viral_load_indication\n" +
            "\n" +
            "WHERE\n" +
            "    lo.archived = 0\n" +
            "  AND h.archived = 0\n" +
            "  AND lo.facility_id = ?1\n" +
            "ORDER BY\n" +
            "    lo.patient_uuid, lo.order_date DESC\n" +
            " ),\n" +
            " pharmacy_details AS (\n" +
            " SELECT DISTINCT ON (hartp.person_uuid)hartp.person_uuid as person_uuid, r.visit_date as dateOfLastRefill,\n" +
            "\n" +
            " hartp.next_appointment as dateOfNextRefill, hartp.refill_period as lastRefillDuration,\n" +
            "\n" +
            " hartp.dsd_model_type as DSDType, r.description as currentRegimenLine, r.regimen_name as currentRegimen,\n" +
            "\n" +
            " (CASE\n" +
            " WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '\n" +
            " OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.hiv_status\n" +
            " WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' < CURRENT_DATE\n" +
            " THEN ' IIT ' ELSE ' ACTIVE '\n" +
            " END)AS currentStatus,\n" +
            " (CASE\n" +
            " WHEN stat.hiv_status ILIKE ' % STOP % ' OR stat.hiv_status ILIKE ' % DEATH % '\n" +
            " OR stat.hiv_status ILIKE ' % OUT % ' THEN stat.status_date\n" +
            " WHEN hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ' <= CURRENT_DATE\n" +
            " THEN CAST((hartp.visit_date + hartp.refill_period + INTERVAL ' 28 day ') AS date) ELSE hartp.visit_date\n" +
            "\n" +
            " END)AS dateOfCurrentStatus\n" +
            " FROM hiv_art_pharmacy hartp\n" +
            " INNER JOIN (SELECT distinct r.* FROM (SELECT h.person_uuid, h.visit_date, CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name,\n" +
            "\n" +
            " hrt.description FROM hiv_art_pharmacy h,\n" +
            " jsonb_array_elements(h.extra->'regimens') with ordinality p(pharmacy_object)\n" +
            " INNER JOIN hiv_regimen hr ON hr.description=CAST(pharmacy_object ->> 'regimenName' AS VARCHAR)\n" +
            "\n" +
            " INNER JOIN hiv_regimen_type hrt ON hrt.id=hr.regimen_type_id\n" +
            " WHERE hrt.id IN (1,2,3,4,14))r\n" +
            " INNER JOIN (SELECT hap.person_uuid, MAX(visit_date) AS MAXDATE FROM hiv_art_pharmacy hap\n" +
            "\n" +
            " INNER JOIN hiv_enrollment h ON h.person_uuid=hap.person_uuid  WHERE h.archived=0\n" +
            " GROUP BY hap.person_uuid ORDER BY MAXDATE ASC ) max ON\n" +
            " max.MAXDATE=r.visit_date AND r.person_uuid=max.person_uuid) r\n" +
            " ON r.visit_date=hartp.visit_date AND r.person_uuid=hartp.person_uuid\n" +
            " INNER JOIN hiv_enrollment he ON he.person_uuid=r.person_uuid\n" +
            " LEFT JOIN (SELECT sh1.person_id, sh1.hiv_status, sh1.status_date\n" +
            " FROM hiv_status_tracker sh1\n" +
            " INNER JOIN\n" +
            " (\n" +
            "SELECT person_id as p_id, MAX(hst.id) AS MAXID\n" +
            "FROM hiv_status_tracker hst INNER JOIN hiv_enrollment h ON h.person_uuid=person_id\n" +
            "\n" +
            "GROUP BY person_id\n" +
            " ORDER BY person_id ASC\n" +
            " ) sh2 ON sh1.person_id = sh2.p_id AND sh1.id = sh2.MAXID\n" +
            " ORDER BY sh1.person_id ASC) stat ON stat.person_id=hartp.person_uuid\n" +
            " WHERE he.archived=0 AND hartp.archived=0 AND hartp.facility_id=?1 ORDER BY hartp.person_uuid ASC\n" +
            "\n" +
            " ),\n" +
            " art_commencement_vitals AS (\n" +
            " SELECT DISTINCT ON (tvs.person_uuid) tvs.person_uuid , body_weight as baseLineWeight, height as baseLineHeight,\n" +
            "\n" +
            " CONCAT(diastolic, ' / ', systolic) as baseLineBp, diastolic as diastolicBp,\n" +
            " systolic as systolicBp, clinical_stage.display as baseLineClinicalStage,\n" +
            " func_status.display as baseLineFunctionalStatus,\n" +
            " hv.description as firstRegimen, hrt.description as firstRegimenLine,\n" +
            " CASE WHEN cd_4=0 THEN null ELSE cd_4 END  AS baseLineCd4,\n" +
            " CASE WHEN cd_4_percentage=0 THEN null ELSE cd_4_percentage END AS cd4Percentage,\n" +
            " hac.visit_date as artStartDate\n" +
            " FROM triage_vital_sign tvs\n" +
            " INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid\n" +
            " AND hac.is_commencement=true AND hac.person_uuid = tvs.person_uuid\n" +
            " INNER JOIN hiv_enrollment h ON hac.hiv_enrollment_uuid = h.uuid AND hac.person_uuid=tvs.person_uuid\n" +
            "\n" +
            " INNER JOIN patient_person p ON p.uuid=h.person_uuid\n" +
            " RIGHT JOIN hiv_regimen hv ON hv.id=hac.regimen_id\n" +
            " RIGHT JOIN hiv_regimen_type hrt ON hrt.id=hac.regimen_type_id\n" +
            " RIGHT JOIN base_application_codeset clinical_stage ON clinical_stage.id=hac.clinical_stage_id\n" +
            "\n" +
            " RIGHT JOIN base_application_codeset func_status ON func_status.id=hac.functional_status_id\n" +
            "\n" +
            "   WHERE hac.archived=0  AND h.archived=0 AND h.facility_id=?1\n" +
            " ),\n" +
            "             current_clinical AS (\n" +
            " SELECT tvs.person_uuid, hac.adherence_level as adherenceLevel, hac.next_appointment as dateOfNextClinic, body_weight as currentWeight, height as currentHeight,\n" +
            "\n" +
            "  diastolic as currentDiastolic, systolic as currentSystolic, bac.display as currentClinicalStage,\n" +
            "\n" +
            "  CONCAT(diastolic, ' / ', systolic) as currentBp, current_clinical_date.MAXDATE as dateOfLastClinic\n" +
            "\n" +
            " FROM triage_vital_sign tvs\n" +
            " INNER JOIN ( SELECT person_uuid, MAX(capture_date) AS MAXDATE FROM triage_vital_sign\n" +
            "\n" +
            " GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_triage\n" +
            " ON current_triage.MAXDATE=tvs.capture_date AND current_triage.person_uuid=tvs.person_uuid\n" +
            "\n" +
            " INNER JOIN hiv_art_clinical hac ON tvs.uuid=hac.vital_sign_uuid\n" +
            " INNER JOIN ( SELECT person_uuid, MAX(hac.visit_date) AS MAXDATE FROM hiv_art_clinical hac\n" +
            "\n" +
            " GROUP BY person_uuid ORDER BY MAXDATE ASC ) AS current_clinical_date\n" +
            " ON current_clinical_date.MAXDATE=hac.visit_date AND current_clinical_date.person_uuid=hac.person_uuid\n" +
            "\n" +
            " INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid\n" +
            " INNER JOIN base_application_codeset bac ON bac.id=hac.clinical_stage_id\n" +
            " WHERE hac.archived=0 AND he.archived=0 AND he.facility_id=?1\n" +
            " ),\n" +
            " caseManagerPatient AS (\n" +
            " SELECT DISTINCT ON (person_uuid) person_uuid, case_manager_id, CONCAT(cm.first_name, ' ', cm.last_name) AS cmName from case_manager_patients cmp\n" +
            " LEFT JOIN case_manager cm ON cm.id = cmp.case_manager_id\n" +
            " )\n" +
            " \n" +
            "             SELECT\n" +
            "             DISTINCT ON (b.personUuid)b.personUuid AS personUuid,\n" +
            "             b.archived,\n" +
            "             b.hospitalNumber,\n" +
            "             b.surname,\n" +
            "             b.firstName,\n" +
            "             b.age,\n" +
            "             b.otherName,\n" +
            "             b.sex,\n" +
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
            "             a.artStartDate, cmp.cmName\n" +
            "             FROM enrollment_details e\n" +
            "             INNER JOIN bio_data b ON e.person_uuid=b.personUuid\n" +
            "             LEFT JOIN art_commencement_vitals a ON a.person_uuid=e.person_uuid\n" +
            "             LEFT JOIN pharmacy_details p ON p.person_uuid=e.person_uuid\n" +
            "             LEFT JOIN laboratory_details l ON l.person_uuid=e.person_uuid\n" +
            "             LEFT JOIN current_clinical c ON c.person_uuid=e.person_uuid\n" +
            "\t\t\t LEFT JOIN caseManagerPatient cmp ON cmp.person_uuid= e.person_uuid";
}
