package org.lamisplus.modules.report.repository.queries;

public class LABORATORYReportQuery {


    public static final String LABORATORY_REPORT_QUERY = "select a.facility_id as facilityId\n" +
            ", (select x.name from base_organisation_unit x where x.id=a.facility_id limit 1) as facility\n" +
            ", a.patient_uuid as patientId\n" +
            ", (select x.hospital_number from patient_person x where x.uuid=a.patient_uuid limit 1) as hospitalNum\n" +
            ", c.lab_test_name as test\n" +
            ", d.date_sample_collected as sampleCollectionDate\n" +
            ", oi.code as datimId\n" +
            ", b.result_reported as result\n" +
            ", b.date_result_reported as dateReported\n" +
            "from laboratory_test a\n" +
            "inner join laboratory_result b on a.id=b.test_id\n" +
            "inner join laboratory_labtest c on a.lab_test_id=c.id\n" +
            "INNER JOIN base_organisation_unit_identifier oi ON oi.organisation_unit_id=a.facility_id AND oi.name = 'DATIM_ID'\n" +
            "inner join laboratory_sample d on a.id=d.test_id\n" +
            "where c.lab_test_name = 'Viral Load' and b.result_reported != '' and a.facility_id =?1";
}
