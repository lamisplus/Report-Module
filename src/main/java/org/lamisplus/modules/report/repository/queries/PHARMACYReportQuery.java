package org.lamisplus.modules.report.repository.queries;

public class PHARMACYReportQuery {


    public static final String PHARMACY_REPORT_QUERY = "SELECT result.id,\n" +
            "result.facility_id AS facilityId,\n" +
            "oi.code AS datimId,\n" +
            "org.name AS facilityName,\n" +
            "p.uuid AS patientId,\n" +
            "p.hospital_number AS hospitalNum,\n" +
            "hrt.description AS regimenLine,\n" +
            "result.mmd_type AS mmdType,\n" +
            "result.next_appointment AS nextAppointment,\n" +
            "dsd.dsd_model AS dsdModel,\n" +
            "result.visit_date AS dateVisit,\n" +
            "result.duration AS refillPeriod,\n" +
            "result.regimen_name AS regimens\n" +
            "FROM (\n" +
            "SELECT\n" +
            "    h.*,\n" +
            "    pharmacy_object ->> 'duration' AS duration,\n" +
            "    CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name\n" +
            "FROM hiv_art_pharmacy h,\n" +
            "    jsonb_array_elements(h.extra->'regimens') WITH ORDINALITY p(pharmacy_object) WHERE archived = 0\n" +
            ") AS result\n" +
            "INNER JOIN patient_person p ON p.uuid = result.person_uuid\n" +
            "LEFT JOIN ( SELECT * FROM (\n" +
            "SELECT person_uuid, dsd_model, date_devolved,\n" +
            "ROW_NUMBER()  OVER (PARTITION BY person_uuid ORDER BY date_devolved DESC) AS rnnk\n" +
            "FROM dsd_devolvement WHERE archived = 0)subQ WHERE rnnk = 1\n" +
            ") dsd ON p.uuid = dsd.person_uuid\n" +
            "INNER JOIN base_organisation_unit org ON org.id = result.facility_id\n" +
            "INNER JOIN base_organisation_unit_identifier oi ON oi.organisation_unit_id = result.facility_id AND oi.name = 'DATIM_ID'\n" +
            "INNER JOIN hiv_regimen hr ON hr.description = result.regimen_name\n" +
            "INNER JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id\n" +
            "WHERE result.facility_id = :facilityId\n" +
            "ORDER BY p.uuid, result.next_appointment, result.visit_date DESC";
}
