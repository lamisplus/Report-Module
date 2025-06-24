package org.lamisplus.modules.report.repository.queries;

public class RADETReportQueries {

    private RADETReportQueries() {}


    public static final String RADET_REPORT_QUERY = "WITH bio_data AS (SELECT DISTINCT (p.uuid) AS personUuid,p.hospital_number AS hospitalNumber, h.unique_id as uniqueId,\n" +
            "EXTRACT(YEAR FROM  AGE(?3, date_of_birth)) AS age,\n" +
            "INITCAP(p.sex) AS gender,\n" +
            "p.date_of_birth AS dateOfBirth,\n" +
            "facility.name AS facilityName,\n" +
            "facility_lga.name AS lga,\n" +
            "facility_state.name AS state,\n" +
            "boui.code AS datimId,\n" +
            "tgroup.display AS targetGroup,\n" +
            "eSetting.display AS enrollmentSetting,\n" +
            "hac.visit_date AS artStartDate,\n" +
            "hr.description AS regimenAtStart,\n" +
            "p.date_of_registration as dateOfRegistration,\n" +
            "h.date_of_registration as dateOfEnrollment,\n" +
            "h.ovc_number AS ovcUniqueId,\n" +
            "h.house_hold_number AS householdUniqueNo,\n" +
            "ecareEntry.display AS careEntry,\n" +
            "hrt.description AS regimenLineAtStart\n" +
            "FROM patient_person p\n" +
            "INNER JOIN base_organisation_unit facility ON facility.id = facility_id\n" +
            "INNER JOIN base_organisation_unit facility_lga ON facility_lga.id = facility.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit facility_state ON facility_state.id = facility_lga.parent_organisation_unit_id\n" +
            "INNER JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id = facility_id AND boui.name='DATIM_ID'\n" +
            "INNER JOIN hiv_enrollment h ON h.person_uuid = p.uuid\n" +
            "LEFT JOIN base_application_codeset tgroup ON tgroup.id = h.target_group_id\n" +
            "LEFT JOIN base_application_codeset eSetting ON eSetting.id = h.enrollment_setting_id\n" +
            "LEFT JOIN base_application_codeset ecareEntry ON ecareEntry.id = h.entry_point_id\n" +
            "INNER JOIN hiv_art_clinical hac ON hac.hiv_enrollment_uuid = h.uuid\n" +
            "AND hac.archived = 0\n" +
            "INNER JOIN hiv_regimen hr ON hr.id = hac.regimen_id\n" +
            "INNER JOIN hiv_regimen_type hrt ON hrt.id = hac.regimen_type_id AND hac.regimen_type_id IN (1,2,3,4,14, 16)\n" +
            "WHERE\n" +
            "h.archived = 0\n" +
            "AND p.archived = 0\n" +
            "AND h.facility_id = ?1\n" +
            "AND hac.is_commencement = TRUE\n" +
            "AND hac.visit_date >= ?2\n" +
            "AND hac.visit_date <= ?3\n" +
            "),\n" +
            "patient_lga as (select DISTINCT ON (personUuid) personUuid as personUuid11, \n" +
            "case when (addr ~ '^[0-9\\\\\\\\\\\\.]+$') =TRUE \n" +
            " then (select name from base_organisation_unit where id = cast(addr as int)) ELSE\n" +
            "(select name from base_organisation_unit where id = cast(facilityLga as int)) end as lgaOfResidence \n" +
            "from (\n" +
            " select pp.uuid AS personUuid, facility_lga.parent_organisation_unit_id AS facilityLga, (jsonb_array_elements(pp.address->'address')->>'district') as addr from patient_person pp\n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id = CAST (pp.organization->'id' AS INTEGER) \n" +
            ") dt),\n" +
            "current_clinical AS (SELECT DISTINCT ON (tvs.person_uuid) tvs.person_uuid AS person_uuid10,\n" +
            "body_weight AS currentWeight,\n" +
            "tbs.display AS tbStatus1,\n" +
            "bac.display AS currentClinicalStage,\n" +
            "(CASE \n" +
            "WHEN INITCAP(pp.sex) = 'Male' THEN NULL\n" +
            "WHEN preg.display IS NOT NULL THEN preg.display\n" +
            "ELSE hac.pregnancy_status\n" +
            "END ) AS pregnancyStatus, \n" +
            "CASE\n" +
            "WHEN hac.tb_screen IS NOT NULL THEN hac.visit_date\n" +
            "ELSE NULL\n" +
            "END AS dateOfTbScreened1\n" +
            "  FROM\n" +
            " triage_vital_sign tvs\n" +
            "INNER JOIN (\n" +
            "SELECT\n" +
            "  person_uuid,\n" +
            "  MAX(capture_date) AS MAXDATE\n" +
            "FROM\n" +
            "  triage_vital_sign WHERE archived = 0 \n" +
            "GROUP BY\n" +
            "  person_uuid\n" +
            "ORDER BY\n" +
            "  MAXDATE ASC\n" +
            " ) AS current_triage ON current_triage.MAXDATE = tvs.capture_date\n" +
            "AND current_triage.person_uuid = tvs.person_uuid\n" +
            "INNER JOIN hiv_art_clinical hac ON tvs.uuid = hac.vital_sign_uuid\n" +
            "LEFT JOIN patient_person pp ON tvs.person_uuid = pp.uuid\n" +
            "INNER JOIN (\n" +
            "SELECT\n" +
            "  person_uuid,\n" +
            "  MAX(hac.visit_date) AS MAXDATE\n" +
            "FROM\n" +
            "  hiv_art_clinical hac WHERE hac.archived = 0 \n" +
            "GROUP BY\n" +
            "  person_uuid\n" +
            "ORDER BY\n" +
            "  MAXDATE ASC\n" +
            " ) AS current_clinical_date ON current_clinical_date.MAXDATE = hac.visit_date\n" +
            "AND current_clinical_date.person_uuid = hac.person_uuid\n" +
            "INNER JOIN hiv_enrollment he ON he.person_uuid = hac.person_uuid\n" +
            "LEFT JOIN base_application_codeset bac ON bac.id = hac.clinical_stage_id\n" +
            "LEFT JOIN base_application_codeset preg ON preg.code = hac.pregnancy_status\n" +
            "LEFT JOIN base_application_codeset tbs ON tbs.id = CASE WHEN hac.tb_status ~ '^[0-9]+$' THEN CAST(hac.tb_status AS INTEGER) ELSE 0 END  \n" +
            "  WHERE\n" +
            "hac.archived = 0\n" +
            "AND he.archived = 0\n" +
            "AND hac.visit_date <= ?3 \n" +
            "AND he.facility_id = ?1\n" +
            "),\n" +
            "sample_collection_date AS (\n" +
            "  SELECT sample.date_sample_collected as dateOfViralLoadSampleCollection, patient_uuid as person_uuid120  FROM (\n" +
            "SELECT lt.viral_load_indication, sm.facility_id,CAST(sm.date_sample_collected AS DATE), sm.patient_uuid, sm.archived, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_sample_collected DESC) as rnkk\n" +
            "FROM public.laboratory_sample  sm\n" +
            "  INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id\n" +
            "WHERE lt.lab_test_id=16 AND sm.archived = 0 \n" +
            "AND  lt.viral_load_indication !=719\n" +
            "AND sm.date_sample_collected IS NOT null\n" +
            " )as sample\n" +
            "WHERE sample.rnkk = 1 AND sample.date_sample_collected <= ?3 \n" +
            "AND (sample.archived is null OR sample.archived = 0) \n" +
            "AND sample.facility_id = ?1 ), \n" +
            "tbstatus as ( \n" +
            "WITH cs AS (\n" +
            "WITH FilteredObservations AS (\n" +
            " SELECT id, person_uuid, date_of_observation AS dateOfTbScreened, \n" +
            "(CASE \n" +
            "WHEN data->'tbIptScreening'->>'status' = 'Presumptive TB and referred for evaluation' \n" +
            "THEN 'Presumptive TB' \n" +
            "ELSE data->'tbIptScreening'->>'status' \n" +
            " END) AS tbStatus,\n" +
            "CASE \n" +
            "WHEN EXTRACT(MONTH FROM CAST (date_of_observation AS DATE)) BETWEEN 10 AND 12 \n" +
            "OR EXTRACT(MONTH FROM CAST (date_of_observation AS DATE)) BETWEEN 1 AND 3 \n" +
            "THEN 'October - March' \n" +
            "WHEN EXTRACT(MONTH FROM CAST (date_of_observation AS DATE)) BETWEEN 4 AND 9 \n" +
            "THEN 'April - September' \n" +
            " END AS reportingPeriod,\n" +
            "EXTRACT(YEAR FROM date_of_observation) AS yearOfReporting,\n" +
            " data->'tbIptScreening'->>'tbScreeningType' AS tbScreeningType,\n" +
            " ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY date_of_observation DESC) AS rowNums\n" +
            " FROM \n" +
            " hiv_observation\n" +
            " WHERE \n" +
            " type = 'Chronic Care' \n" +
            " AND data IS NOT NULL AND archived = 0 \n" +
            "AND date_of_observation BETWEEN (CAST (?3 AS DATE) - INTERVAL '6 MONTHS') AND CAST(?3 AS DATE) \n" +
            "),\n" +
            "FilteredLatestObservations AS (\n" +
            " SELECT id, person_uuid, dateOfTbScreened, tbStatus, tbScreeningType, reportingPeriod, yearOfReporting\n" +
            " FROM \n" +
            " FilteredObservations\n" +
            " WHERE rowNums = 1\n" +
            "),\n" +
            "ReportingPeriod AS (\n" +
            " SELECT \n" +
            " CASE \n" +
            "WHEN EXTRACT(MONTH FROM CAST (?3 AS DATE)) BETWEEN 10 AND 12 \n" +
            "OR EXTRACT(MONTH FROM CAST (?3 AS DATE)) BETWEEN 1 AND 3 \n" +
            "THEN 'October - March' \n" +
            "WHEN EXTRACT(MONTH FROM CAST (?3 AS DATE)) BETWEEN 4 AND 9 \n" +
            "THEN 'April - September' \n" +
            " END AS currentReportingPeriod\n" +
            "),\n" +
            "PresumptiveCheck AS ( \n" +
            "SELECT DISTINCT person_uuid, ho.date_of_observation dateScreened, ho.data->'tbIptScreening'->>'status' AS tbStatus, ho.data->'tbIptScreening'->>'tbScreeningType' tbScreeningType, \n" +
            "CASE \n" +
            "WHEN EXTRACT(MONTH FROM CAST (ho.date_of_observation AS DATE)) BETWEEN 10 AND 12 \n" +
            "OR EXTRACT(MONTH FROM CAST (ho.date_of_observation AS DATE)) BETWEEN 1 AND 3 \n" +
            "THEN 'October - March' \n" +
            "WHEN EXTRACT(MONTH FROM CAST (ho.date_of_observation AS DATE)) BETWEEN 4 AND 9 \n" +
            "THEN 'April - September' \n" +
            " END AS preSumpReportingPeriod\n" +
            "FROM hiv_observation ho\n" +
            "  WHERE ho.type = 'Chronic Care'\n" +
            "AND ho.data IS NOT NULL \n" +
            "AND ho.archived = 0 AND ho.date_of_observation BETWEEN (CAST (?3 AS DATE) - INTERVAL '6 MONTHS') AND CAST(?3 AS DATE)\n" +
            "AND ho.data->'tbIptScreening'->>'status' ILIKE 'Presumptive TB%'\n" +
            ")\n" +
            "SELECT \n" +
            "lo.id,\n" +
            "lo.person_uuid,\n" +
            "CASE WHEN rp.currentReportingPeriod = pc.preSumpReportingPeriod THEN pc.tbStatus\n" +
            "WHEN lo.reportingPeriod = rp.currentReportingPeriod  THEN lo.tbStatus ELSE NULL END AS tbStatus,\n" +
            "CASE WHEN rp.currentReportingPeriod = pc.preSumpReportingPeriod THEN pc.dateScreened\n" +
            "WHEN lo.reportingPeriod = rp.currentReportingPeriod  THEN lo.dateOfTbScreened ELSE NULL END AS dateOfTbScreened,\n" +
            "CASE WHEN rp.currentReportingPeriod = pc.preSumpReportingPeriod THEN pc.tbScreeningType\n" +
            "WHEN lo.reportingPeriod = rp.currentReportingPeriod  THEN lo.tbScreeningType ELSE NULL END AS tbScreeningType\n" +
            "  FROM \n" +
            "FilteredLatestObservations lo\n" +
            "LEFT JOIN PresumptiveCheck pc ON  pc.person_uuid = lo.person_uuid\n" +
            "CROSS JOIN ReportingPeriod rp\n" +
            ")\n" +
            "SELECT * FROM cs ),\n" +
            "tblam AS (\n" +
            "  SELECT \n" +
            "* \n" +
            "  FROM (\n" +
            " SELECT \n" +
            " CAST(lr.date_result_reported AS DATE) AS dateOfLastTbLam, \n" +
            " lr.patient_uuid as personuuidtblam, \n" +
            " lr.result_reported as tbLamResult, \n" +
            " ROW_NUMBER () OVER (\n" +
            "PARTITION BY lr.patient_uuid \n" +
            "ORDER BY \n" +
            "lr.date_result_reported DESC\n" +
            " ) as rank2333 \n" +
            " FROM \n" +
            " laboratory_result lr \n" +
            " INNER JOIN public.laboratory_test lt on lr.test_id = lt.id \n" +
            " WHERE \n" +
            " lt.lab_test_id = 51 \n" +
            " AND lr.date_result_reported IS NOT NULL \n" +
            " AND lr.date_result_reported <= ?3 \n" +
            " AND lr.date_result_reported >= ?2 \n" +
            " AND lr.result_reported is NOT NULL \n" +
            " AND lr.archived = 0 \n" +
            " AND lr.facility_id = ?1\n" +
            ") as tblam \n" +
            "  WHERE \n" +
            "tblam.rank2333 = 1\n" +
            "),\n" +
            "current_vl_result AS (SELECT * FROM (\n" +
            "  SELECT CAST(ls.date_sample_collected AS DATE ) AS dateOfCurrentViralLoadSample, sm.patient_uuid as person_uuid130 , sm.facility_id as vlFacility, sm.archived as vlArchived, acode.display as viralLoadIndication, sm.result_reported as currentViralLoad,CAST(sm.date_result_reported AS DATE) as dateOfCurrentViralLoad,\n" +
            "ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) as rank2\n" +
            "  FROM public.laboratory_result  sm\n" +
            " INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "  INNER JOIN public.laboratory_sample ls on ls.test_id = lt.id\n" +
            " INNER JOIN public.base_application_codeset  acode on acode.id =  lt.viral_load_indication\n" +
            "  WHERE lt.lab_test_id = 16\n" +
            "AND  lt.viral_load_indication !=719\n" +
            "AND sm. date_result_reported IS NOT NULL\n" +
            "AND sm.result_reported is NOT NULL\n" +
            ")as vl_result\n" +
            "WHERE vl_result.rank2 = 1 AND vl_result.dateOfCurrentViralLoad <= ?3\n" +
            "AND (vl_result.vlArchived = 0 OR vl_result.vlArchived is null)\n" +
            "AND  vl_result.vlFacility = ?1\n" +
            "), \n" +
            "careCardCD4 AS (SELECT visit_date, coalesce(cast(cd_4 as varchar), cd4_semi_quantitative) as cd_4, person_uuid AS cccd4_person_uuid\n" +
            "  FROM public.hiv_art_clinical\n" +
            "  WHERE is_commencement is true\n" +
            "AND  archived = 0\n" +
            "AND  cd_4 != 0\n" +
            "AND visit_date <= ?3\n" +
            "AND facility_id = ?1\n" +
            "),\n" +
            "labCD4 AS (SELECT * FROM (\n" +
            "SELECT sm.patient_uuid AS cd4_person_uuid,  sm.result_reported as cd4Lb,sm.date_result_reported as dateOfCD4Lb, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_result_reported DESC) as rnk\n" +
            "FROM public.laboratory_result  sm\n" +
            "INNER JOIN public.laboratory_test  lt on sm.test_id = lt.id\n" +
            "WHERE lt.lab_test_id IN (1, 50) \n" +
            "AND sm. date_result_reported IS NOT NULL\n" +
            "AND sm.archived = 0\n" +
            "AND sm.facility_id = ?1\n" +
            "AND sm.date_result_reported <= ?3\n" +
            " )as cd4_result\n" +
            "WHERE  cd4_result.rnk = 1\n" +
            "),\n" +
            "tb_sample_collection AS (SELECT sample.created_by,CAST(sample.date_sample_collected AS DATE) as dateOfTbSampleCollection, patient_uuid as personTbSample  FROM (\n" +
            "SELECT llt.lab_test_name,sm.created_by, lt.viral_load_indication, sm.facility_id,sm.date_sample_collected, sm.patient_uuid, sm.archived, ROW_NUMBER () OVER (PARTITION BY sm.patient_uuid ORDER BY date_sample_collected DESC) as rnkk\n" +
            "FROM public.laboratory_sample  sm\n" +
            "  INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id\n" +
            "  INNER JOIN  laboratory_labtest llt on llt.id = lt.lab_test_id\n" +
            "WHERE lt.lab_test_id IN (65, 66, 51, 64, 67, 72, 71, 86, 58, 73)\n" +
            " AND sm.archived = 0 AND date_sample_collected IS NOT null \n" +
            " AND sm.date_sample_collected <= ?3\n" +
            " AND sm.facility_id = ?1\n" +
            " )as sample\n" +
            " WHERE sample.rnkk = 1\n" +
            "),\n" +
            "current_tb_result AS (WITH tb_test as (SELECT personTbResult, dateofTbDiagnosticResultReceived, dateOfTbSampleCollected, tbDiagnosticResult,\n" +
            "coalesce(\n" +
            "MAX(CASE WHEN lab_test_id = 66 THEN 'Chest X-ray' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 65 THEN 'Gene Xpert' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 51 THEN 'TB-LAM' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 64 THEN 'AFB Smear Microscopy' END),\n" +
            "MAX(CASE WHEN lab_test_id = 67 THEN 'Gene Xpert' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 72 THEN 'TrueNAT' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 71 THEN 'TB-LAM' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 86 THEN 'Cobas' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 73 THEN 'TB-LAM' END) ,\n" +
            "MAX(CASE WHEN lab_test_id = 58 THEN 'TB-LAM' END)\n" +
            ") as tbDiagnosticTestType\n" +
            " FROM (\n" +
            "SELECT sm.patient_uuid as personTbResult, CASE WHEN (CAST(lr.date_result_reported AS DATE) > ?3 AND lr.result_reported IS NOT NULL) THEN NULL ELSE lr.result_reported END  as tbDiagnosticResult,\n" +
            "CASE WHEN CAST(lr.date_result_reported AS DATE) > ?3 THEN NULL ELSE CAST(lr.date_result_reported AS DATE) END  as dateofTbDiagnosticResultReceived,cast(sm.date_sample_collected as date) AS dateOfTbSampleCollected,\n" +
            "lt.lab_test_id, sm.date_sample_collected, ROW_NUMBER() OVER (PARTITION BY  sm.patient_uuid ORDER BY sm.date_sample_collected DESC) AS rnkkk\n" +
            "FROM laboratory_sample sm\n" +
            " INNER JOIN laboratory_test lt on lt.id = sm.test_id\n" +
            " LEFT JOIN laboratory_result lr ON lt.id = lr.test_id\n" +
            " WHERE lt.lab_test_id IN (65, 51, 64, 67, 72, 71, 86, 58, 73, 66) and sm.archived = 0 AND sm.date_sample_collected IS NOT NULL\n" +
            "  AND sm.facility_id = ?1\n" +
            ") AS tbSubQ where rnkkk = 1 \n" +
            "GROUP BY tbSubQ.personTbResult, tbSubQ.dateofTbDiagnosticResultReceived, tbSubQ.dateOfTbSampleCollected, tbDiagnosticResult)\n" +
            "SELECT * FROM tb_test),\n" +
            "tbTreatment AS (\n" +
            "SELECT * FROM (SELECT\n" +
            " COALESCE(NULLIF(CAST(data->'tbIptScreening'->>'treatementType' AS text), ''), '') as tbTreatementType,\n" +
            " NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'tbTreatmentStartDate', '') AS DATE), NULL)as tbTreatmentStartDate,\n" +
            " CAST(data->'tbIptScreening'->>'treatmentOutcome' AS text) as tbTreatmentOutcome,\n" +
            " NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'completionDate', '') AS DATE), NULL) as tbCompletionDate,\n" +
            " person_uuid as tbTreatmentPersonUuid,\n" +
            " ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY date_of_observation DESC)\n" +
            "FROM public.hiv_observation WHERE type = 'Chronic Care'\n" +
            "AND facility_id = ?1 and archived = 0\n" +
            ") tbTreatment WHERE row_number = 1\n" +
            "AND tbTreatmentStartDate IS NOT NULL),\n" +
            "tbTreatmentNew AS (\n" +
            "WITH tb_start AS (\n" +
            "SELECT * FROM (\n" +
            "SELECT\n" +
            " person_uuid AS person_uuid,\n" +
            "date_of_observation as screeningDate,\n" +
            " NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'tbTreatmentStartDate' , '') AS DATE), NULL) AS tbTreatmentStartDate,\n" +
            " data->'tbIptScreening'->>'tbTestResult' AS tbDiagnosticResult,\n" +
            "data->'tbIptScreening'->>'chestXrayResult' as chestXrayResult,\n" +
            " data->'tbIptScreening'->>'diagnosticTestType' AS tbDiagnosticTestType,\n" +
            "COALESCE(NULLIF(data->'tptMonitoring'->>'tbType',''), NULLIF(data->'tbIptScreening'->>'tbType','')) AS tbTreatmentType,\n" +
            "NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'dateSpecimenSent', '') AS DATE), NULL) AS specimenSentDate,\n" +
            "data->'tbIptScreening'->>'status' as screeningStatus,\n" +
            "data->'tbIptScreening'->>'dateOfDiagnosticTest' as dateOfDiagnosticTest, \n" +
            "data->'tbIptScreening'->>'tbScreeningType' AS tbScreeningType, CAST(NULLIF(data->'tptMonitoring'->>'cadScore', '') AS INTEGER) AS cadScore,\n" +
            "data->'tptMonitoring'->>'clinicallyEvaulated' AS clinicallyEvaulated,\n" +
            "data->'tbIptScreening'->>'chestXrayDone' AS chestXrayDone,\n" +
            "data->'tbIptScreening'->>'chestXrayResultTest' AS chestXrayResultTest,\n" +
            "data->'tbIptScreening'->>'dateOfChestXrayResultTestDone' AS dateOfChestXrayResultTestDone,\n" +
            "ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY date_of_observation DESC) as rnk3\n" +
            "FROM\n" +
            " hiv_observation\n" +
            "WHERE archived = 0 AND date_of_observation BETWEEN ?2 AND ?3 AND \n" +
            " (\n" +
            "(data->'tbIptScreening'->>'status' LIKE '%Presumptive TB' \n" +
            " or data->'tbIptScreening'->>'status' = 'No signs or symptoms of TB'))\n" +
            ")  subTc WHERE rnk3 = 1 ),\n" +
            "tb_completion AS (\n" +
            "SELECT\n" +
            " person_uuid AS person_uuid,\n" +
            " NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'completionDate', '') AS DATE), NULL)  AS completionDate,\n" +
            " data->'tbIptScreening'->>'treatmentOutcome' AS treatmentOutcome\n" +
            "FROM\n" +
            " hiv_observation\n" +
            "WHERE\n" +
            " (data->'tbIptScreening'->>'treatmentOutcome' IS NOT NULL AND data->'tbIptScreening'->>'treatmentOutcome' != '')\n" +
            "AND archived =0 )\n" +
            "SELECT\n" +
            "COALESCE(ts.person_uuid, tc.person_uuid) AS person_uuid_tb, \n" +
            "ts.tbTreatmentStartDate,\n" +
            "COALESCE(ts.tbDiagnosticResult,ts.chestXrayResult) as tbDiagnosticResult,\n" +
            "ts.tbDiagnosticTestType,\n" +
            "ts.tbScreeningType,\n" +
            "ts.screeningStatus,\n" +
            "ts.tbTreatmentType,\n" +
            "ts.screeningDate,\n" +
            "ts.specimenSentDate,\n" +
            "dateOfDiagnosticTest,\n" +
            "tc.completionDate,\n" +
            "tc.treatmentOutcome, ts.cadScore, ts.clinicallyEvaulated, ts.chestXrayDone, ts.chestXrayResultTest, ts.dateOfChestXrayResultTestDone \n" +
            "FROM\n" +
            "tb_start ts\n" +
            "FULL OUTER JOIN\n" +
            "tb_completion tc\n" +
            "ON\n" +
            "ts.person_uuid = tc.person_uuid  order by screeningDate desc\n" +
            "),\n" +
            "pharmacy_details_regimen AS (\n" +
            "  select * from (\n" +
            "select *, ROW_NUMBER() OVER (PARTITION BY pr1.person_uuid40 ORDER BY pr1.lastPickupDate DESC) as rnk3\n" +
            "from (\n" +
            "SELECT p.person_uuid as person_uuid40, COALESCE(ds_model.display, p.dsd_model_type) as dsdModel, p.visit_date as lastPickupDate,\n" +
            "r.description as currentARTRegimen, rt.description as currentRegimenLine,\n" +
            "p.next_appointment as nextPickupDate,\n" +
            "CAST(p.refill_period /30.0 AS DECIMAL(10,1)) AS monthsOfARVRefill\n" +
            "from public.hiv_art_pharmacy p\n" +
            "  INNER JOIN public.hiv_art_pharmacy_regimens pr\n" +
            " ON pr.art_pharmacy_id = p.id\n" +
            "  INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id\n" +
            "  INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id\n" +
            "left JOIN base_application_codeset ds_model on ds_model.code = p.dsd_model_type \n" +
            "WHERE r.regimen_type_id in (1,2,3,4,14, 16)\n" +
            "  AND  p.archived = 0\n" +
            "  AND  p.facility_id = ?1\n" +
            "  AND  p.visit_date >= ?2\n" +
            "  AND  p.visit_date  <= ?3\n" +
            " ) as pr1\n" +
            ") as pr2\n" +
            "  where pr2.rnk3 = 1\n" +
            "),\n" +
            "negativeTbDiagnosticResults AS (\n" +
            "SELECT sm.patient_uuid as personTbResult, CASE WHEN (CAST(lr.date_result_reported AS DATE) > ?3 AND lr.result_reported IS NOT NULL) THEN NULL ELSE lr.result_reported END  as tbDiagnosticResult,\n" +
            "CASE WHEN CAST(lr.date_result_reported AS DATE) > ?3 THEN NULL ELSE CAST(lr.date_result_reported AS DATE) END  as dateofTbDiagnosticResultReceived,cast(sm.date_sample_collected as date) AS dateOfTbSampleCollected,\n" +
            "lt.lab_test_id, sm.date_sample_collected, ROW_NUMBER() OVER (PARTITION BY  sm.patient_uuid ORDER BY sm.date_sample_collected DESC) AS rnkkk\n" +
            "FROM laboratory_sample sm\n" +
            " INNER JOIN laboratory_test lt on lt.id = sm.test_id\n" +
            " LEFT JOIN laboratory_result lr ON lt.id = lr.test_id\n" +
            " WHERE lt.lab_test_id IN (86, 65, 67, 64, 58, 51, 73, 72, 71) and sm.archived = 0 AND sm.date_sample_collected IS NOT NULL AND (lr.result_reported ILIKE '%negative%' OR lr.result_reported ILIKE '%MTB not detected%')\n" +
            "),\n" +
            "eac as ( \n" +
            "with first_eac as ( \n" +
            " select * from (with current_eac as (\n" +
            "select id, person_uuid, uuid, status, \n" +
            " ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row \n" +
            "from hiv_eac where archived = 0) \n" +
            " select ce.id, ce.person_uuid, hes.eac_session_date, \n" +
            " ROW_NUMBER() OVER (PARTITION BY hes.person_uuid ORDER BY hes.eac_session_date ASC ) AS row from hiv_eac_session hes \n" +
            "join current_eac ce on ce.uuid = hes.eac_id where ce.row = 1 and hes.archived = 0 \n" +
            "  and hes.eac_session_date between ?2 and ?3 \n" +
            "  and hes.status in ('FIRST EAC')) as fes where row = 1), \n" +
            "last_eac as ( \n" +
            " select * from (with current_eac as ( \n" +
            "select id, person_uuid, uuid, status, \n" +
            " ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row \n" +
            "from hiv_eac where archived = 0) \n" +
            " select ce.id, ce.person_uuid, hes.eac_session_date, \n" +
            " ROW_NUMBER() OVER (PARTITION BY hes.person_uuid ORDER BY hes.eac_session_date DESC ) AS row from hiv_eac_session hes \n" +
            "join current_eac ce on ce.uuid = hes.eac_id where ce.row = 1 and hes.archived = 0 \n" +
            "  and hes.eac_session_date between ?2 and ?3 \n" +
            "  and hes.status in ('FIRST EAC', 'SECOND EAC', 'THIRD EAC')) as les where row = 1), \n" +
            "eac_count as (SELECT person_uuid, no_eac_session FROM (\n" +
            "SELECT person_uuid, eac_id,  no_eac_session, eac_session_date, ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY eac_session_date DESC ) AS rnkk FROM (\n" +
            "SELECT person_uuid, visit_id, eac_id, eac_session_date,COUNT(eac_id) OVER (PARTITION BY eac_id) AS no_eac_session\n" +
            "FROM hiv_eac_session WHERE archived = 0 AND eac_session_date between ?2 and ?3 AND status in ('FIRST EAC', 'SECOND EAC', 'THIRD EAC') order by eac_session_date DESC) subQ \n" +
            ") countEac WHERE rnkk = 1 ), \n" +
            "extended_eac as (\n" +
            " select * from (with current_eac as ( \n" +
            "select id, person_uuid, uuid, status, \n" +
            " ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY id DESC) AS row \n" +
            "from hiv_eac where archived = 0) \n" +
            " select ce.id, ce.person_uuid, hes.eac_session_date, \n" +
            " ROW_NUMBER() OVER (PARTITION BY hes.person_uuid ORDER BY hes.eac_session_date DESC ) AS row from hiv_eac_session hes \n" +
            "join current_eac ce on ce.uuid = hes.eac_id where ce.row = 1 and hes.archived = 0 and hes.status is not null and hes.eac_session_date between ?2 and ?3 \n" +
            "  and hes.status not in ('FIRST EAC', 'SECOND EAC', 'THIRD EAC')) as exe where row = 1), \n" +
            "post_eac_vl as ( \n" +
            " select * from(select lt.patient_uuid, cast(ls.date_sample_collected as date), lr.result_reported, cast(lr.date_result_reported as date), \n" +
            "ROW_NUMBER() OVER (PARTITION BY lt.patient_uuid ORDER BY ls.date_sample_collected DESC) AS row \n" +
            " from laboratory_test lt \n" +
            " left join laboratory_sample ls on ls.test_id = lt.id \n" +
            " left join laboratory_result lr on lr.test_id = lt.id \n" +
            "where lt.viral_load_indication = 302 and lt.archived = 0 and ls.archived = 0 \n" +
            " and ls.date_sample_collected between ?2 and ?3) pe where row = 1 ) \n" +
            "select fe.person_uuid as person_uuid50, fe.eac_session_date as dateOfCommencementOfEAC, le.eac_session_date as dateOfLastEACSessionCompleted, \n" +
            "ec.no_eac_session as numberOfEACSessionCompleted, exe.eac_session_date as dateOfExtendEACCompletion, \n" +
            "pvl.result_reported as repeatViralLoadResult, pvl.date_result_reported as DateOfRepeatViralLoadResult, \n" +
            "pvl.date_sample_collected as dateOfRepeatViralLoadEACSampleCollection \n" +
            "from first_eac fe \n" +
            "left join last_eac le on le.person_uuid = fe.person_uuid \n" +
            "left join eac_count ec on ec.person_uuid = fe.person_uuid \n" +
            "left join extended_eac exe on exe.person_uuid = fe.person_uuid \n" +
            "left join post_eac_vl pvl on pvl.patient_uuid = fe.person_uuid), \n" +
            "dsd1 as ( \n" +
            "select person_uuid as person_uuid_dsd_1, dateOfDevolvement, modelDevolvedTo \n" +
            "from (select d.person_uuid, d.date_devolved as dateOfDevolvement, bmt.display as modelDevolvedTo, \n" +
            "ROW_NUMBER() OVER (PARTITION BY d.person_uuid ORDER BY d.date_devolved ASC ) AS row from dsd_devolvement d \n" +
            "left join base_application_codeset bmt on bmt.code = d.dsd_type \n" +
            "where d.archived = 0 and d.date_devolved between  ?2 and ?3) d1 where row = 1), \n" +
            "dsd2 as ( \n" +
            "select d2.person_uuid as person_uuid_dsd_2, d2.dateOfCurrentDSD, d2.currentDSDModel, d2.dateReturnToSite, bac.display as currentDsdOutlet, dsdOutlet \n" +
            "from (select d.person_uuid, d.date_devolved as dateOfCurrentDSD, bmt.display as currentDSDModel, d.date_return_to_site AS dateReturnToSite, outlet_name as dsdOutlet, \n" +
            " ROW_NUMBER() OVER (PARTITION BY d.person_uuid ORDER BY d.date_devolved DESC ) AS row from dsd_devolvement d \n" +
            "left join base_application_codeset bmt on bmt.code = d.dsd_type \n" +
            "where d.archived = 0 and d.date_devolved between ?2 and ?3) d2 \n" +
            "left join base_application_codeset bac on bac.code = d2.dsdOutlet where d2.row = 1), \n" +
            "biometric AS (\n" +
            "SELECT \n" +
            "DISTINCT ON (he.person_uuid) he.person_uuid AS person_uuid60, \n" +
            "biometric_count.enrollment_date AS dateBiometricsEnrolled, \n" +
            "biometric_count.count AS numberOfFingersCaptured,\n" +
            "recapture_count.recapture_date AS dateBiometricsRecaptured,\n" +
            "recapture_count.count AS numberOfFingersRecaptured,\n" +
            "bst.biometric_status AS biometricStatus, \n" +
            "bst.status_date\n" +
            "FROM \n" +
            "hiv_enrollment he \n" +
            "LEFT JOIN (SELECT  b.person_uuid, CASE WHEN COUNT(b.person_uuid) > 10 THEN 10 ELSE COUNT(b.person_uuid) END, \n" +
            "MAX(enrollment_date) enrollment_date \n" +
            "  FROM \n" +
            "biometric b \n" +
            "  WHERE archived = 0 AND (recapture = 0 or recapture is null) \n" +
            "  GROUP BY b.person_uuid\n" +
            ") biometric_count ON biometric_count.person_uuid = he.person_uuid \n" +
            "LEFT JOIN (\n" +
            "SELECT b.person_uuid, max_capture.max_capture_date AS recapture_date, b.recapture,\n" +
            " CASE WHEN COUNT(b.person_uuid) > 10 THEN 10 ELSE COUNT(b.person_uuid) END\n" +
            "FROM biometric b\n" +
            "LEFT JOIN (select person_uuid,max(enrollment_date) max_capture_date from biometric group by person_uuid) max_capture\n" +
            "ON b.person_uuid=max_capture.person_uuid\n" +
            "where b.enrollment_date=max_capture.max_capture_date AND b.archived=0 AND b.recapture !=0 and b.recapture is NOT null \n" +
            "group by 1,2,3\n" +
            "order by b.person_uuid\n" +
            ") recapture_count ON recapture_count.person_uuid = he.person_uuid \n" +
            "LEFT JOIN (\n" +
            "SELECT DISTINCT ON (person_id) person_id, biometric_status,\n" +
            "MAX(status_date) OVER (PARTITION BY person_id ORDER BY status_date DESC) AS status_date \n" +
            "FROM hiv_status_tracker \n" +
            "WHERE archived=0 AND facility_id=?1 \n" +
            ") bst ON bst.person_id = he.person_uuid \n" +
            "WHERE he.archived = 0), \n" +
            "current_regimen AS (SELECT DISTINCT ON (regiment_table.person_uuid) regiment_table.person_uuid AS person_uuid70,\n" +
            " start_or_regimen AS dateOfCurrentRegimen,\n" +
            " regiment_table.max_visit_date,\n" +
            " regiment_table.regimen\n" +
            "  FROM\n" +
            " (SELECT MIN(visit_date) start_or_regimen, MAX(visit_date) max_visit_date, regimen, person_uuid \n" +
            " FROM\n" +
            "  ( SELECT hap.id, hap.person_uuid, hap.visit_date, hivreg.description AS regimen, ROW_NUMBER() OVER(ORDER BY person_uuid, visit_date) rn1,\n" +
            "ROW_NUMBER() OVER(PARTITION BY hivreg.description ORDER BY person_uuid, visit_date) rn2\n" +
            " FROM\n" +
            "public.hiv_art_pharmacy AS hap\n" +
            "  INNER JOIN (SELECT\n" +
            " MAX(hapr.id) AS id,\n" +
            " art_pharmacy_id,\n" +
            " regimens_id,\n" +
            " hr.description\n" +
            "  FROM\n" +
            " public.hiv_art_pharmacy_regimens AS hapr\n" +
            "INNER JOIN hiv_regimen AS hr ON hapr.regimens_id = hr.id\n" +
            "  WHERE\n" +
            "hr.regimen_type_id IN (1,2,3,4,14, 16)\n" +
            "  GROUP BY\n" +
            " art_pharmacy_id, regimens_id, hr.description ) AS hapr ON hap.id = hapr.art_pharmacy_id and hap.archived=0\n" +
            "  INNER JOIN hiv_regimen AS hivreg ON hapr.regimens_id = hivreg.id\n" +
            "  INNER JOIN hiv_regimen_type AS hivregtype ON hivreg.regimen_type_id = hivregtype.id\n" +
            "  AND hivreg.regimen_type_id IN (1,2,3,4,14,16)\n" +
            " ORDER BY person_uuid, visit_date) t\n" +
            "GROUP BY person_uuid, regimen, rn1 - rn2 ORDER BY\n" +
            "  MIN(visit_date)\n" +
            " ) AS regiment_table\n" +
            "INNER JOIN (\n" +
            "SELECT\n" +
            "  DISTINCT MAX(visit_date) AS max_visit_date,\n" +
            " person_uuid\n" +
            "FROM hiv_art_pharmacy\n" +
            "WHERE archived=0\n" +
            "GROUP BY\n" +
            "  person_uuid\n" +
            " ) AS hap ON regiment_table.person_uuid = hap.person_uuid\n" +
            "  WHERE\n" +
            "regiment_table.max_visit_date = hap.max_visit_date\n" +
            "  GROUP BY\n" +
            " regiment_table.person_uuid,\n" +
            " regiment_table.regimen,\n" +
            " regiment_table.max_visit_date,\n" +
            " start_or_regimen),\n" +
            "iptNew AS (WITH tpt_completed AS (\n" +
            "SELECT * FROM (\n" +
            "SELECT person_uuid AS person_uuid, data->'tptMonitoring'->>'endedTpt' AS endedTpt, NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'dateTptEnded', '') AS DATE), NULL) AS tptCompletionDate,\n" +
            " data->'tptMonitoring'->>'outComeOfIpt' AS tptCompletionStatus, data->'tbIptScreening'->>'outcome' AS completion_tptPreventionOutcome, \n" +
            " ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY date_of_observation  DESC) rowNum\n" +
            "FROM hiv_observation\n" +
            "WHERE data->'tptMonitoring'->>'endedTpt' = 'Yes' AND data->'tbIptScreening'->>'outcome' IS NOT NULL AND data->'tbIptScreening'->>'outcome' != ''\n" +
            " AND archived = 0 ) subTc WHERE rowNum = 1\n" +
            "),\n" +
            "pt_screened AS (SELECT person_uuid AS person_uuid, data->'tptMonitoring'->>'tptRegimen' AS tptType, NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'dateTptStarted', '') AS DATE), NULL) AS tptStartDate,\n" +
            " data->'tptMonitoring'->>'eligibilityTpt' AS eligibilityTpt\n" +
            "FROM hiv_observation\n" +
            "WHERE (data->'tptMonitoring'->>'eligibilityTpt' IS NOT NULL AND  data->'tptMonitoring'->>'eligibilityTpt' != '') \n" +
            " AND (data->'tbIptScreening'->>'outcome' IS NOT NULL AND data->'tbIptScreening'->>'outcome' != '' AND data->'tbIptScreening'->>'outcome' != 'Currently on TPT') )\n" +
            "SELECT COALESCE(tc.person_uuid, ts.person_uuid) AS person_uuid, ts.tptType, ts.tptStartDate, ts.eligibilityTpt, tc.endedTpt, tc.tptCompletionDate, tc.tptCompletionStatus\n" +
            "FROM\n" +
            "pt_screened ts\n" +
            "FULL OUTER JOIN\n" +
            "tpt_completed tc\n" +
            "ON\n" +
            "ts.person_uuid = tc.person_uuid), \n" +
            "ipt as ( \n" +
            "with ipt_c as ( select person_uuid, date_completed as iptCompletionDate, iptCompletionStatus from ( \n" +
            "select person_uuid, cast(ipt->>'dateCompleted' as date) as date_completed, \n" +
            "COALESCE(NULLIF(CAST(ipt->>'completionStatus' AS text), ''), '') AS iptCompletionStatus, \n" +
            "row_number () over (partition by person_uuid order by cast(ipt->>'dateCompleted' as date) desc) as rnk \n" +
            "from hiv_art_pharmacy where (ipt->>'dateCompleted' is not null and ipt->>'dateCompleted' != 'null' and ipt->>'dateCompleted' != '' AND TRIM(ipt->>'dateCompleted') <> '') \n" +
            "and archived = 0) ic where ic.rnk = 1), \n" +
            "ipt_s as (SELECT person_uuid, visit_date as dateOfIptStart, regimen_name as iptType \n" +
            "FROM (SELECT h.person_uuid, h.visit_date, CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name, \n" +
            "ROW_NUMBER() OVER (PARTITION BY h.person_uuid ORDER BY h.visit_date ASC) AS rnk \n" +
            "FROM hiv_art_pharmacy h \n" +
            "INNER JOIN jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object) ON TRUE \n" +
            "INNER JOIN hiv_regimen hr ON hr.description = CAST(p.pharmacy_object ->> 'regimenName' AS VARCHAR) \n" +
            "INNER JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id  AND hrt.id = 15 AND hrt.id NOT IN (1,2,3,4,14, 16) \n" +
            "WHERE hrt.id = 15 AND h.archived = 0 \n" +
            ") AS ic WHERE ic.rnk = 1 ), \n" +
            "ipt_c_cs as ( \n" +
            "SELECT person_uuid, iptStartDate, iptCompletionSCS, iptCompletionDSC \n" +
            "FROM ( SELECT person_uuid,  CASE WHEN (data->'tbIptScreening'->>'dateTPTStart') IS NULL OR (data->'tbIptScreening'->>'dateTPTStart') = '' \n" +
            "OR (data->'tbIptScreening'->>'dateTPTStart') = ' '  THEN NULL ELSE CAST((data->'tbIptScreening'->>'dateTPTStart') AS DATE) END as iptStartDate, \n" +
            "data->'tptMonitoring'->>'outComeOfIpt' as iptCompletionSCS, \n" +
            "CASE WHEN (data->'tptMonitoring'->>'date') = 'null' OR (data->'tptMonitoring'->>'date') = '' OR (data->'tptMonitoring'->>'date') = ' '  THEN NULL \n" +
            " ELSE cast(data->'tptMonitoring'->>'date' as date)  END as iptCompletionDSC, ROW_NUMBER() OVER (PARTITION BY person_uuid ORDER BY \n" +
            " CASE WHEN (data->'tptMonitoring'->>'date') = 'null' OR (data->'tptMonitoring'->>'date') = '' OR (data->'tptMonitoring'->>'date') = ' '  THEN NULL ELSE cast(data->'tptMonitoring'->>'date' as date) END  DESC) AS ipt_c_sc_rnk \n" +
            "FROM hiv_observation \n" +
            "WHERE type = 'Chronic Care' \n" +
            "AND archived = 0 \n" +
            "AND (data->'tptMonitoring'->>'date') IS NOT NULL \n" +
            "AND (data->'tptMonitoring'->>'date') != 'null' \n" +
            ") AS ipt_ccs \n" +
            "WHERE ipt_c_sc_rnk = 1) \n" +
            "select ipt_c.person_uuid as personuuid80, CASE WHEN coalesce(ipt_c_cs.iptCompletionDSC, ipt_c.iptCompletionDate) > ?3 THEN NULL ELSE coalesce(ipt_c_cs.iptCompletionDSC, ipt_c.iptCompletionDate) END as iptCompletionDate, \n" +
            "coalesce(ipt_c_cs.iptCompletionSCS, ipt_c.iptCompletionStatus) as iptCompletionStatus, COALESCE(ipt_s.dateOfIptStart, ipt_c_cs.iptStartDate) AS dateOfIptStart, ipt_s.iptType \n" +
            "from ipt_c \n" +
            "left join ipt_s on ipt_s.person_uuid = ipt_c.person_uuid \n" +
            "left join ipt_c_cs on ipt_s.person_uuid = ipt_c_cs.person_uuid ), \n" +
            "ipt_s as ( SELECT person_uuid, visit_date as dateOfIptStart, regimen_name as iptType \n" +
            "FROM ( SELECT h.person_uuid, h.visit_date, CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name, \n" +
            "ROW_NUMBER() OVER (PARTITION BY h.person_uuid ORDER BY h.visit_date ASC) AS rnk \n" +
            "FROM hiv_art_pharmacy h \n" +
            "INNER JOIN jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object) ON TRUE \n" +
            "INNER JOIN hiv_regimen hr ON hr.description = CAST(p.pharmacy_object ->> 'regimenName' AS VARCHAR) \n" +
            "INNER JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id  AND hrt.id = 15 AND hrt.id NOT IN (1,2,3,4,14, 16) \n" +
            "WHERE hrt.id = 15 AND h.archived = 0 \n" +
            ") AS ic WHERE ic.rnk = 1 ),\n" +
            " cervical_cancer AS (select * from (select  ho.person_uuid AS person_uuid90, ho.date_of_observation AS dateOfCervicalCancerScreening, \n" +
            "ho.data ->> 'screenTreatmentMethodDate' AS treatmentMethodDate,cc_type.display AS cervicalCancerScreeningType, \n" +
            "cc_method.display AS cervicalCancerScreeningMethod, cc_trtm.display AS cervicalCancerTreatmentScreened, \n" +
            "cc_result.display AS resultOfCervicalCancerScreening, \n" +
            "ROW_NUMBER() OVER (PARTITION BY ho.person_uuid ORDER BY ho.date_of_observation DESC) AS row \n" +
            "from hiv_observation ho \n" +
            "LEFT JOIN base_application_codeset cc_type ON cc_type.code = CAST(ho.data ->> 'screenType' AS VARCHAR) \n" +
            " LEFT JOIN base_application_codeset cc_method ON cc_method.code = CAST(ho.data ->> 'screenMethod' AS VARCHAR) \n" +
            " LEFT JOIN base_application_codeset cc_result ON cc_result.code = CAST(ho.data ->> 'screeningResult' AS VARCHAR) \n" +
            " LEFT JOIN base_application_codeset cc_trtm ON cc_trtm.code = CAST(ho.data ->> 'screenTreatment' AS VARCHAR) \n" +
            "where ho.archived = 0 and type = 'Cervical cancer') as cc where row = 1), \n" +
            " ovc AS ( SELECT DISTINCT ON (person_uuid) person_uuid AS personUuid100, ovc_number AS ovcNumber, house_hold_number AS householdNumber\n" +
            "  FROM hiv_enrollment), \n" +
            "previous_previous AS (\n" +
            "  SELECT DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS prePrePersonUuid,\n" +
            "(CASE WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'\n" +
            " WHEN(stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %' )\n" +
            ")THEN stat.hiv_status\n" +
            " ELSE pharmacy.status\n" +
            " END) AS status,\n" +
            "(CASE WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%'  THEN stat.status_date\n" +
            " WHEN( stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %' )\n" +
            ") THEN stat.status_date ELSE pharmacy.visit_date END) AS status_date, stat.cause_of_death, stat.va_cause_of_death\n" +
            "  FROM\n" +
            " (SELECT (CASE WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' <= ?5 THEN 'IIT' ELSE 'Active' END ) status,\n" +
            "  (CASE\n" +
            "WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' <= ?5  THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'\n" +
            "ELSE hp.visit_date\n" +
            "END\n" +
            " ) AS visit_date,\n" +
            "  hp.person_uuid, MAXDATE\n" +
            "FROM\n" +
            "  hiv_art_pharmacy hp\n" +
            " INNER JOIN (\n" +
            "  SELECT hap.person_uuid, hap.visit_date AS  MAXDATE, ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3\n" +
            "FROM public.hiv_art_pharmacy hap \n" +
            " INNER JOIN public.hiv_art_pharmacy_regimens pr \n" +
            " ON pr.art_pharmacy_id = hap.id \n" +
            "INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0 \n" +
            "INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "WHERE r.regimen_type_id in (1,2,3,4,14, 16) \n" +
            "AND hap.archived = 0  \n" +
            "AND hap.visit_date <= ?3\n" +
            " ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid \n" +
            " AND MAX.rnkkk3 = 1\n" +
            "WHERE\n" +
            " hp.archived = 0\n" +
            "AND hp.visit_date <= ?5\n" +
            " ) pharmacy\n" +
            "LEFT JOIN (\n" +
            "SELECT\n" +
            "  hst.hiv_status,\n" +
            "  hst.person_id,\n" +
            "  hst.cause_of_death,\n" +
            "hst.va_cause_of_death,\n" +
            "  hst.status_date\n" +
            "FROM\n" +
            "  (SELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death,va_cause_of_death,\n" +
            " hiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "FROM hiv_status_tracker WHERE archived=0 AND status_date <= ?5 )s\n" +
            " WHERE s.row_number=1) hst\n" +
            " INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "WHERE hst.status_date <= ?5\n" +
            " ) stat ON stat.person_id = pharmacy.person_uuid ),\n" +
            "previous AS (SELECT DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS prePersonUuid,\n" +
            "(CASE\n" +
            " WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'\n" +
            " WHEN(stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %')\n" +
            ")THEN stat.hiv_status\n" +
            " ELSE pharmacy.status\n" +
            " END) AS status,\n" +
            "(CASE\n" +
            " WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%'  THEN stat.status_date\n" +
            " WHEN(\n" +
            " stat.status_date > pharmacy.maxdate\n" +
            "AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %')\n" +
            ") THEN stat.status_date\n" +
            " ELSE pharmacy.visit_date\n" +
            " END) AS status_date,\n" +
            "stat.cause_of_death, stat.va_cause_of_death\n" +
            "  FROM\n" +
            " (SELECT (CASE\n" +
            "WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' <= ?4 THEN 'IIT'\n" +
            "ELSE 'Active'\n" +
            "END\n" +
            " ) status,\n" +
            "  (CASE\n" +
            "WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' <=  ?4 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'\n" +
            "ELSE hp.visit_date\n" +
            "END\n" +
            " ) AS visit_date,\n" +
            "  hp.person_uuid, MAXDATE\n" +
            "FROM\n" +
            "  hiv_art_pharmacy hp\n" +
            " INNER JOIN (\n" +
            "  SELECT hap.person_uuid, hap.visit_date AS  MAXDATE, ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3\n" +
            "FROM public.hiv_art_pharmacy hap \n" +
            " INNER JOIN public.hiv_art_pharmacy_regimens pr \n" +
            " ON pr.art_pharmacy_id = hap.id \n" +
            "INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0 \n" +
            "INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "WHERE r.regimen_type_id in (1,2,3,4,14, 16) \n" +
            "AND hap.archived = 0  \n" +
            "AND hap.visit_date <= ?4\n" +
            " ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid \n" +
            " AND MAX.rnkkk3 = 1\n" +
            "WHERE\n" +
            " hp.archived = 0\n" +
            "AND hp.visit_date <= ?4\n" +
            " ) pharmacy\n" +
            "LEFT JOIN (\n" +
            "SELECT\n" +
            "  hst.hiv_status,\n" +
            "  hst.person_id,\n" +
            "  hst.cause_of_death, \n" +
            "  hst.va_cause_of_death,\n" +
            "  hst.status_date\n" +
            "FROM\n" +
            "  (SELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death,va_cause_of_death,\n" +
            " hiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            "FROM hiv_status_tracker WHERE archived=0 AND status_date <=  ?4 )s\n" +
            " WHERE s.row_number=1\n" +
            "  ) hst\n" +
            " INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "WHERE hst.status_date <=  ?4\n" +
            " ) stat ON stat.person_id = pharmacy.person_uuid),\n" +
            "current_status AS ( SELECT  DISTINCT ON (pharmacy.person_uuid) pharmacy.person_uuid AS cuPersonUuid,\n" +
            " (CASE\n" +
            "WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%' THEN 'Died'\n" +
            "WHEN( stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %' OR stat.hiv_status ILIKE '%ART Transfer In%'))\n" +
            " THEN stat.hiv_status\n" +
            "ELSE pharmacy.status\n" +
            "END) AS status,\n" +
            " (CASE\n" +
            "WHEN stat.hiv_status ILIKE '%DEATH%' OR stat.hiv_status ILIKE '%Died%'  THEN stat.status_date\n" +
            "WHEN(stat.status_date > pharmacy.maxdate AND (stat.hiv_status ILIKE '%stop%' OR stat.hiv_status ILIKE '%out%' OR stat.hiv_status ILIKE '%Invalid %' OR stat.hiv_status ILIKE '%ART Transfer In%')) THEN stat.status_date\n" +
            "ELSE pharmacy.visit_date\n" +
            "END\n" +
            ") AS status_date,\n" +
            " stat.cause_of_death, stat.va_cause_of_death\n" +
            " FROM\n" +
            "(SELECT(CASE\n" +
            "  WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' <= ?3 THEN 'IIT'\n" +
            "  ELSE 'Active'\n" +
            "  END\n" +
            ") status,\n" +
            " (CASE\n" +
            "  WHEN hp.visit_date + hp.refill_period + INTERVAL '29 day' <= ?3 THEN hp.visit_date + hp.refill_period + INTERVAL '29 day'\n" +
            "  ELSE hp.visit_date\n" +
            "  END) AS visit_date,\n" +
            " hp.person_uuid, MAXDATE \n" +
            "  FROM\n" +
            " hiv_art_pharmacy hp\n" +
            "INNER JOIN (SELECT hap.person_uuid, hap.visit_date AS  MAXDATE, ROW_NUMBER() OVER (PARTITION BY hap.person_uuid ORDER BY hap.visit_date DESC) as rnkkk3\n" +
            "FROM public.hiv_art_pharmacy hap \n" +
            " INNER JOIN public.hiv_art_pharmacy_regimens pr \n" +
            " ON pr.art_pharmacy_id = hap.id \n" +
            "INNER JOIN hiv_enrollment h ON h.person_uuid = hap.person_uuid AND h.archived = 0 \n" +
            "INNER JOIN public.hiv_regimen r on r.id = pr.regimens_id \n" +
            "INNER JOIN public.hiv_regimen_type rt on rt.id = r.regimen_type_id \n" +
            "WHERE r.regimen_type_id in (1,2,3,4,14, 16) \n" +
            "AND hap.archived = 0  \n" +
            "AND hap.visit_date <= ?3\n" +
            " ) MAX ON MAX.MAXDATE = hp.visit_date AND MAX.person_uuid = hp.person_uuid \n" +
            " AND MAX.rnkkk3 = 1\n" +
            "WHERE\n" +
            "hp.archived = 0\n" +
            "AND hp.visit_date <= ?3\n" +
            ") pharmacy\n" +
            "  LEFT JOIN (SELECT\n" +
            " hst.hiv_status,\n" +
            " hst.person_id,\n" +
            " hst.cause_of_death,\n" +
            " hst.va_cause_of_death,\n" +
            " hst.status_date\n" +
            "  FROM\n" +
            " (SELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death,\n" +
            "hiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            " FROM hiv_status_tracker WHERE archived=0 AND status_date <= ?3 )s\n" +
            "WHERE s.row_number=1\n" +
            " ) hst\n" +
            "INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "  WHERE hst.status_date <= ?3\n" +
            ") stat ON stat.person_id = pharmacy.person_uuid\n" +
            "),\n" +
            "naive_vl_data AS (SELECT pp.uuid AS nvl_person_uuid, EXTRACT(YEAR FROM AGE(NOW(), pp.date_of_birth) ) as age, ph.visit_date, ph.regimen\n" +
            "  FROM patient_person pp\n" +
            " INNER JOIN (SELECT DISTINCT * FROM (SELECT pharm.*,\n" +
            " ROW_NUMBER() OVER (PARTITION BY pharm.person_uuid ORDER BY pharm.visit_date DESC)\n" +
            " FROM\n" +
            "(SELECT DISTINCT * FROM hiv_art_pharmacy hap\n" +
            "  INNER JOIN hiv_art_pharmacy_regimens hapr\n" +
            "  INNER JOIN hiv_regimen hr ON hr.id=hapr.regimens_id\n" +
            "  INNER JOIN hiv_regimen_type hrt ON hrt.id=hr.regimen_type_id\n" +
            "  INNER JOIN hiv_regimen_resolver hrr ON hrr.regimensys=hr.description\n" +
            " ON hapr.art_pharmacy_id=hap.id\n" +
            " WHERE hap.archived=0 AND hrt.id IN (1,2,3,4,14, 16) AND hap.facility_id = ?1 ) pharm\n" +
            ")ph WHERE ph.row_number=1\n" +
            "  )ph ON ph.person_uuid=pp.uuid\n" +
            "  WHERE pp.uuid NOT IN (\n" +
            " SELECT patient_uuid FROM (\n" +
            " SELECT COUNT(ls.patient_uuid), ls.patient_uuid FROM laboratory_sample ls\n" +
            "  INNER JOIN laboratory_test lt ON lt.id=ls.test_id AND lt.lab_test_id=16\n" +
            " WHERE ls.archived=0 AND ls.facility_id=?1\n" +
            " GROUP BY ls.patient_uuid)t )\n" +
            "),\n" +
            "crytococal_antigen as (\n" +
            " select * from \n" +
            "(select DISTINCT ON (lr.patient_uuid) lr.patient_uuid as personuuid12, \n" +
            " CAST(lr.date_result_reported AS DATE) AS dateOfLastCrytococalAntigen, \n" +
            " lr.result_reported AS lastCrytococalAntigen , \n" +
            " ROW_NUMBER() OVER (\n" +
            "PARTITION BY lr.patient_uuid \n" +
            "ORDER BY \n" +
            "lr.date_result_reported DESC\n" +
            " ) as rowNum \n" +
            " from \n" +
            " public.laboratory_test lt \n" +
            " inner join laboratory_result lr on lr.test_id = lt.id \n" +
            " where \n" +
            " lab_test_id = 52 OR lab_test_id = 69 OR lab_test_id = 70\n" +
            " AND lr.date_result_reported IS NOT NULL \n" +
            " AND lr.date_result_reported <= ?3 \n" +
            " AND lr.date_result_reported >= ?2 \n" +
            " AND lr.result_reported is NOT NULL \n" +
            " AND lr.archived = 0 \n" +
            " AND lr.facility_id = ?1\n" +
            ") dt \n" +
            "  where \n" +
            "rowNum = 1), \n" +
            "vaCauseOfDeath AS (\n" +
            "SELECT\n" +
            " hst.hiv_status,\n" +
            " hst.person_id,\n" +
            " hst.cause_of_death,\n" +
            " hst.va_cause_of_death,\n" +
            " hst.status_date\n" +
            "  FROM\n" +
            " (SELECT * FROM (SELECT DISTINCT (person_id) person_id, status_date, cause_of_death, va_cause_of_death,\n" +
            "hiv_status, ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY status_date DESC)\n" +
            " FROM hiv_status_tracker WHERE hiv_status ilike '%Died%' AND archived=0 AND status_date <= ?3 )s\n" +
            "WHERE s.row_number=1) hst\n" +
            "INNER JOIN hiv_enrollment he ON he.person_uuid = hst.person_id\n" +
            "  WHERE hst.status_date <= ?3\n" +
            "),\n" +
            "case_manager AS (\n" +
            " SELECT DISTINCT ON (cmp.person_uuid)person_uuid AS caseperson, cmp.case_manager_id, CONCAT(cm.first_name, ' ', cm.last_name) AS caseManager FROM (SELECT person_uuid, case_manager_id,\n" +
            " ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY id DESC)\n" +
            " FROM case_manager_patients) cmp  INNER JOIN case_manager cm ON cm.id=cmp.case_manager_id\n" +
            " WHERE cmp.row_number=1 AND cm.facility_id=?1), \n" +
            "client_verification AS (\n" +
            " SELECT * FROM (select person_uuid, data->'attempt'->0->>'outcome' AS clientVerificationOutCome,\n" +
            "data->'attempt'->0->>'verificationStatus' AS clientVerificationStatus,\n" +
            "CAST (data->'attempt'->0->>'dateOfAttempt' AS DATE) AS dateOfOutcome,\n" +
            "ROW_NUMBER() OVER ( PARTITION BY person_uuid ORDER BY CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) DESC)\n" +
            "from public.hiv_observation where type = 'Client Verification' \n" +
            "AND archived = 0 AND CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) <= ?3 AND CAST(data->'attempt'->0->>'dateOfAttempt' AS DATE) >= ?2 AND facility_id = ?1\n" +
            ") clientVerification WHERE row_number = 1 AND dateOfOutcome IS NOT NULL ) \n" +
            "SELECT DISTINCT ON (bd.personUuid) personUuid AS uniquePersonUuid,\n" +
            "bd.*,\n" +
            "CONCAT(bd.datimId, '_', bd.personUuid) AS ndrPatientIdentifier, \n" +
            "p_lga.*,\n" +
            "scd.*,\n" +
            "cvlr.*,\n" +
            "pdr.*,\n" +
            "b.*,\n" +
            "c.*,\n" +
            "e.*,\n" +
            "ca.dateOfCurrentRegimen,\n" +
            "ca.person_uuid70,\n" +
            "iptStart.dateOfIptStart AS dateOfIptStart,\n" +
            "COALESCE(CAST (iptN.tptCompletionDate AS DATE), ipt.iptCompletionDate) AS iptCompletionDate, \n" +
            "(CASE WHEN COALESCE(iptN.tptCompletionStatus, ipt.iptCompletionStatus) = 'IPT Completed' THEN 'Treatment completed' ELSE COALESCE(iptN.tptCompletionStatus, ipt.iptCompletionStatus) END) AS iptCompletionStatus,\n" +
            "iptStart.iptType AS iptType,\n" +
            "cc.*,\n" +
            "dsd1.*, dsd2.*,  \n" +
            "ov.*,\n" +
            "(CASE WHEN COALESCE(tbTmentNew.tbTreatmentType, tbTment.tbTreatementType) IN ('New', 'Relapse', 'Relapsed') THEN 'New/Relapse' ELSE COALESCE(tbTmentNew.tbTreatmentType, tbTment.tbTreatementType) END)  AS tbTreatementType, tbTmentNew.cadScore,\n" +
            "COALESCE(tbTmentNew.tbTreatmentStartDate, tbTment.tbTreatmentStartDate) AS tbTreatmentStartDate,\n" +
            "COALESCE(tbTmentNew.treatmentOutcome, tbTment.tbTreatmentOutcome) AS tbTreatmentOutcome,\n" +
            "COALESCE(tbTmentNew.completionDate, tbTment.tbCompletionDate) AS tbCompletionDate,\n" +
            "COALESCE(tbTmentNew.person_uuid_tb, tbTment.tbTreatmentPersonUuid) AS tbTreatmentPersonUuid,\n" +
            "(CASE WHEN (tbTmentNew.clinicallyEvaulated = 'Yes' AND tbTmentNew.tbScreeningType ILIKE '%Chest X-Ray with CAD and/or Symptom screening%' AND tbTmentNew.chestXrayDone = 'Yes' AND negativeTb.tbDiagnosticResult IS NOT NULL AND tbTmentNew.cadScore >= 40) THEN CAST(tbTmentNew.dateOfChestXrayResultTestDone AS DATE) ELSE NULL END)  AS dateTbScoreCad, (CASE WHEN (tbTmentNew.clinicallyEvaulated = 'Yes' AND tbTmentNew.tbScreeningType ILIKE '%Chest X-Ray with CAD and/or Symptom screening%' AND tbTmentNew.chestXrayDone = 'Yes' AND tbTmentNew.cadScore >= 40 AND negativeTb.tbDiagnosticResult IS NOT NULL) THEN tbTmentNew.chestXrayResultTest ELSE NULL END) AS resultTbScoreCad,\n" +
            "tbSample.*,\n" +
            "tbResult.*,\n" +
            "tbS.*,\n" +
            "tbl.*,\n" +
            "crypt.*, \n" +
            "COALESCE (vaod.cause_of_death, ct.cause_of_death) AS causeOfDeath,\n" +
            "COALESCE (vaod.va_cause_of_death, ct.va_cause_of_death) AS vaCauseOfDeath,\n" +
            "(CASE WHEN prepre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "WHEN prepre.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "WHEN pre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "WHEN pre.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "WHEN (prepre.status ILIKE '%IIT%' OR prepre.status ILIKE '%stop%') AND (pre.status ILIKE '%ACTIVE%') THEN 'Active Restart'\n" +
            "WHEN prepre.status ILIKE '%ACTIVE%' AND pre.status ILIKE '%ACTIVE%' THEN 'Active' ELSE REPLACE(pre.status, '_', ' ') END ) AS previousStatus,\n" +
            "CAST((CASE WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
            "WHEN prepre.status ILIKE '%out%' THEN prepre.status_date\n" +
            "WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
            "WHEN pre.status ILIKE '%out%' THEN pre.status_date\n" +
            "WHEN (prepre.status ILIKE '%IIT%' OR prepre.status ILIKE '%stop%' ) AND (pre.status ILIKE '%ACTIVE%') THEN pre.status_date WHEN prepre.status ILIKE '%ACTIVE%' AND pre.status ILIKE '%ACTIVE%' THEN pre.status_date ELSE pre.status_date\n" +
            "END ) AS DATE)AS previousStatusDate,\n" +
            "(CASE WHEN ((pre.status ILIKE '%IIT%' OR pre.status ILIKE '%stop%') AND (ct.status ILIKE '%ACTIVE%')) THEN 'Active Restart'\n" +
            "WHEN ct.status ILIKE '%ACTIVE%' THEN 'Active'\n" +
            "WHEN ct.status ILIKE '%ART Transfer In%' THEN ''\n" +
            "WHEN prepre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "WHEN prepre.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "WHEN pre.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "WHEN pre.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "WHEN ct.status ILIKE '%IIT%' THEN 'IIT'\n" +
            "WHEN ct.status ILIKE '%out%' THEN 'Transferred Out'\n" +
            "WHEN ct.status ILIKE '%DEATH%' THEN 'Died'\n" +
            "WHEN pre.status ILIKE '%ACTIVE%' AND ct.status ILIKE '%ACTIVE%' THEN 'Active'\n" +
            "ELSE REPLACE(ct.status, '_', ' ') END ) AS currentStatus,\n" +
            "CAST((CASE WHEN ct.status ILIKE '%ACTIVE%' THEN ct.status_date\n" +
            "WHEN ct.status ILIKE '%ART Transfer In%' THEN ct.status_date\n" +
            "WHEN prepre.status ILIKE '%DEATH%' THEN prepre.status_date\n" +
            "WHEN prepre.status ILIKE '%out%' THEN prepre.status_date\n" +
            "WHEN pre.status ILIKE '%DEATH%' THEN pre.status_date\n" +
            "WHEN pre.status ILIKE '%out%' THEN pre.status_date\n" +
            "WHEN ct.status ILIKE '%IIT%' THEN\n" +
            "CASE WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%out%' OR pre.status ILIKE '%stop%') THEN pre.status_date ELSE ct.status_date END\n" +
            "WHEN ct.status ILIKE '%stop%' THEN \n" +
            "CASE \n" +
            "WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%out%' OR pre.status ILIKE '%IIT%') THEN pre.status_date\n" +
            "ELSE ct.status_date\n" +
            "END\n" +
            "WHEN ct.status ILIKE '%out%' THEN\n" +
            "CASE WHEN (pre.status ILIKE '%DEATH%' OR pre.status ILIKE '%stop%' OR pre.status ILIKE '%IIT%') THEN pre.status_date\n" +
            "ELSE ct.status_date END\n" +
            "WHEN (pre.status ILIKE '%IIT%' OR pre.status ILIKE '%stop%') AND (ct.status ILIKE '%ACTIVE%') THEN ct.status_date\n" +
            "WHEN pre.status ILIKE '%ACTIVE%' AND ct.status ILIKE '%ACTIVE%' THEN ct.status_date ELSE ct.status_date END )AS DATE) AS currentStatusDate,\n" +
            "cvl.clientVerificationStatus, cvl.clientVerificationOutCome,\n" +
            "(CASE WHEN prepre.status ILIKE '%DEATH%' THEN FALSE\n" +
            "WHEN prepre.status ILIKE '%out%' THEN FALSE\n" +
            "WHEN pre.status ILIKE '%DEATH%' THEN FALSE\n" +
            "WHEN pre.status ILIKE '%out%' THEN FALSE\n" +
            "WHEN ct.status ILIKE '%IIT%' THEN FALSE\n" +
            "WHEN ct.status ILIKE '%out%' THEN FALSE\n" +
            "WHEN ct.status ILIKE '%DEATH%' THEN FALSE\n" +
            "WHEN ct.status ILIKE '%stop%' THEN FALSE\n" +
            "WHEN (nvd.age >= 15 AND nvd.regimen ILIKE '%DTG%'\n" +
            "AND bd.artstartdate + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%') THEN TRUE\n" +
            "WHEN (nvd.age >= 15 AND nvd.regimen NOT ILIKE '%DTG%'\n" +
            "AND bd.artstartdate + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%') THEN TRUE\n" +
            "WHEN (nvd.age <= 15 AND bd.artstartdate + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%') THEN TRUE\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "AND scd.dateofviralloadsamplecollection IS NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "AND CAST(bd.artstartdate AS DATE) + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN TRUE\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "AND scd.dateofviralloadsamplecollection IS NOT NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "AND CAST(bd.artstartdate AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN TRUE\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "AND( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "OR  scd.dateofviralloadsamplecollection IS NULL )\n" +
            "AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN TRUE\n" +
            "WHEN  CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "OR cvlr.dateofcurrentviralload IS NULL )\n" +
            "AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN TRUE\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000\n" +
            "AND ( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload OR scd.dateofviralloadsamplecollection IS NULL)\n" +
            "AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN TRUE\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000 AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload OR cvlr.dateofcurrentviralload IS NULL)\n" +
            "AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN TRUE\n" +
            "ELSE FALSE END ) AS vlEligibilityStatus,\n" +
            "CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) AS test,\n" +
            "(CASE WHEN prepre.status ILIKE '%DEATH%' THEN NULL\n" +
            "WHEN prepre.status ILIKE '%out%' THEN NULL\n" +
            "WHEN pre.status ILIKE '%DEATH%' THEN NULL\n" +
            "WHEN pre.status ILIKE '%out%' THEN NULL\n" +
            "WHEN ct.status ILIKE '%IIT%' THEN NULL\n" +
            "WHEN ct.status ILIKE '%out%' THEN NULL\n" +
            "WHEN ct.status ILIKE '%DEATH%' THEN NULL\n" +
            "WHEN ct.status ILIKE '%stop%' THEN NULL\n" +
            "WHEN (nvd.age >= 15\n" +
            "AND nvd.regimen ILIKE '%DTG%'\n" +
            "AND bd.artstartdate + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%')\n" +
            "THEN CAST(bd.artstartdate + 91 AS DATE)\n" +
            "WHEN (nvd.age >= 15\n" +
            "AND nvd.regimen NOT ILIKE '%DTG%'\n" +
            "AND bd.artstartdate + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%')\n" +
            "THEN CAST(bd.artstartdate + 181 AS DATE)\n" +
            "WHEN (nvd.age <= 15 AND bd.artstartdate + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%')\n" +
            "THEN CAST(bd.artstartdate + 181 AS DATE)\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "AND scd.dateofviralloadsamplecollection IS NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "AND CAST(bd.artstartdate AS DATE) + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN\n" +
            "CAST(bd.artstartdate AS DATE) + 181\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) IS NULL\n" +
            "AND scd.dateofviralloadsamplecollection IS NOT NULL AND\n" +
            "cvlr.dateofcurrentviralload IS NULL\n" +
            "AND CAST(bd.artstartdate AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN\n" +
            "CAST(bd.artstartdate AS DATE) + 91\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "AND( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload\n" +
            "OR  scd.dateofviralloadsamplecollection IS NULL )\n" +
            "AND CAST(cvlr.dateofcurrentviralload AS DATE) + 181 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%'\n" +
            "THEN CAST(cvlr.dateofcurrentviralload AS DATE) + 181\n" +
            "WHEN  CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) < 1000\n" +
            "AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload\n" +
            "OR cvlr.dateofcurrentviralload IS NULL)\n" +
            "AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN\n" +
            "CAST(scd.dateofviralloadsamplecollection AS DATE) + 91\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000\n" +
            "AND ( scd.dateofviralloadsamplecollection < cvlr.dateofcurrentviralload OR scd.dateofviralloadsamplecollection IS NULL)\n" +
            "AND CAST(cvlr.dateofcurrentviralload AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN\n" +
            "CAST(cvlr.dateofcurrentviralload AS DATE) + 91\n" +
            "WHEN CAST(NULLIF(REGEXP_REPLACE(cvlr.currentviralload, '[^0-9]', '', 'g'), '') AS INTEGER) > 1000 AND (scd.dateofviralloadsamplecollection > cvlr.dateofcurrentviralload OR cvlr.dateofcurrentviralload IS NULL)\n" +
            "AND CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 < ?3 AND ct.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' AND prepre.status ILIKE '%ACTIVE%' THEN\n" +
            "CAST(scd.dateofviralloadsamplecollection AS DATE) + 91 ELSE NULL END) AS dateOfVlEligibilityStatus,\n" +
            "(CASE WHEN cd.cd4lb IS NOT NULL THEN  cd.cd4lb\n" +
            "WHEN  ccd.cd_4 IS NOT NULL THEN CAST(ccd.cd_4 as VARCHAR)\n" +
            "ELSE NULL END) as lastCd4Count,\n" +
            "(CASE WHEN cd.dateOfCd4Lb IS NOT NULL THEN  CAST(cd.dateOfCd4Lb as DATE)\n" +
            "WHEN ccd.visit_date IS NOT NULL THEN CAST(ccd.visit_date as DATE)\n" +
            "ELSE NULL END) as dateOfLastCd4Count, \n" +
            "INITCAP(cm.caseManager) AS caseManager \n" +
            "FROM bio_data bd\n" +
            " LEFT JOIN patient_lga p_lga on p_lga.personUuid11 = bd.personUuid \n" +
            " LEFT JOIN pharmacy_details_regimen pdr ON pdr.person_uuid40 = bd.personUuid\n" +
            " LEFT JOIN current_clinical c ON c.person_uuid10 = bd.personUuid\n" +
            " LEFT JOIN sample_collection_date scd ON scd.person_uuid120 = bd.personUuid\n" +
            " LEFT JOIN current_vl_result  cvlr ON cvlr.person_uuid130 = bd.personUuid\n" +
            " LEFT JOIN  labCD4 cd on cd.cd4_person_uuid = bd.personUuid\n" +
            " LEFT JOIN  careCardCD4 ccd on ccd.cccd4_person_uuid = bd.personUuid\n" +
            " LEFT JOIN eac e ON e.person_uuid50 = bd.personUuid\n" +
            " LEFT JOIN biometric b ON b.person_uuid60 = bd.personUuid\n" +
            " LEFT JOIN current_regimen  ca ON ca.person_uuid70 = bd.personUuid\n" +
            " LEFT JOIN ipt ipt ON ipt.personUuid80 = bd.personUuid\n" +
            " LEFT JOIN iptNew iptN ON iptN.person_uuid = bd.personUuid\n" +
            " LEFT JOIN cervical_cancer cc ON cc.person_uuid90 = bd.personUuid\n" +
            " LEFT JOIN ovc ov ON ov.personUuid100 = bd.personUuid\n" +
            " LEFT JOIN current_status ct ON ct.cuPersonUuid = bd.personUuid\n" +
            " LEFT JOIN previous pre ON pre.prePersonUuid = ct.cuPersonUuid\n" +
            " LEFT JOIN previous_previous prepre ON prepre.prePrePersonUuid = ct.cuPersonUuid\n" +
            " LEFT JOIN naive_vl_data nvd ON nvd.nvl_person_uuid = bd.personUuid\n" +
            " LEFT JOIN tb_sample_collection tbSample ON tbSample.personTbSample = bd.personUuid\n" +
            " LEFT JOIN  tbTreatment tbTment ON tbTment.tbTreatmentPersonUuid = bd.personUuid\n" +
            " LEFT JOIN  tbTreatmentNew tbTmentNew ON tbTmentNew.person_uuid_tb = bd.personUuid\n" +
            " LEFT JOIN  current_tb_result tbResult ON tbResult.personTbResult = bd.personUuid\n" +
            " LEFT JOIN crytococal_antigen crypt on crypt.personuuid12= bd.personUuid\n" +
            " LEFT JOIN  tbstatus tbS on tbS.person_uuid = bd.personUuid \n" +
            " LEFT JOIN  tblam tbl  on tbl.personuuidtblam = bd.personUuid \n" +
            " LEFT JOIN  dsd1 dsd1  on dsd1.person_uuid_dsd_1 = bd.personUuid \n" +
            " LEFT JOIN  dsd2 dsd2  on dsd2.person_uuid_dsd_2 = bd.personUuid \n" +
            " LEFT JOIN case_manager cm on cm.caseperson= bd.personUuid\n" +
            " LEFT JOIN client_verification cvl on cvl.person_uuid = bd.personUuid \n" +
            " LEFT JOIN vaCauseOfDeath vaod ON vaod.person_id = bd.personUuid \n" +
            " LEFT JOIN negativeTbDiagnosticResults negativeTb ON negativeTb.personTbResult = bd.personUuid AND negativeTb.dateOfTbSampleCollected = tbTmentNew.specimenSentDate \n" +
            " LEFT JOIN ipt_s iptStart ON iptStart.person_uuid = bd.personUuid";
}
