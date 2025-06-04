package org.lamisplus.modules.report.repository.queries;

public class TBLongitudinalReportQuery {

    public static final String TB_LONGITUDINAL_REPORT_QUERY = "SELECT facility.name as facilityName, facility_lga.name as lga, facility_state.name as state, p.uuid AS personUuid, p.hospital_number AS hospitalNumber,\n" +
            "he.unique_id AS uniqueId, INITCAP(p.sex) AS gender, p.date_of_birth AS dateOfBirth, EXTRACT(YEAR FROM  AGE(?3, date_of_birth)) AS age, he.date_started dateStarted,\n" +
            "clientObservation.dateOfObservation, clientObservation.tbScreeningType, clientObservation.cadScore, clientObservation.tbStatus,\n" +
            "clientObservation.specimenType, clientObservation.dateSpecimenSent, clientObservation.diagnosticTestDone, clientObservation.clinicallyEvaulated, clientObservation.dateOfObservation dateOfEvaluation,\n" +
            "clientObservation.resultOfClinicalEvaluation, clientObservation.tbType, clientObservation.dateOfChestXrayResultTestDone,clientObservation.chestXrayResult, clientObservation.tbTreatmentStartDate,\n" +
            "clientObservation.treatmentOutcome, clientObservation.tbCompletionDate, clientObservation.eligibleForTPT, clientObservation.contractionForTpt, clientObservation.contractionOptions,\n" +
            "tptClients.dateOfIptStart, tptClients.regimenName, COALESCE(clientObservation.iptCompletionDate, iptCompletionFromPharmacy.iptCompletionDate) iptCompletionDate, COALESCE(CAST(clientObservation.iptCompletionStatus AS VARCHAR), CAST(iptCompletionFromPharmacy.iptCompletionStatus AS VARCHAR)) iptCompletionStatus, \n" +
            "clientObservation.weightAt, tbLabSample.tbDiagnosticTestType, tbLabSample.dateofDiagnosticTestSampleCollected,\n" +
            "tbLabSample.tbDiagnosticResult, tbLabSample.dateofTbDiagnosticResultReceived, tbLabSample.dateDiagnosticEvaluation, (CASE WHEN (clientObservation.clinicallyEvaulated = 'Yes' AND clientObservation.tbScreeningType ILIKE '%Chest X-Ray with CAD and/or Symptom screening%' AND clientObservation.chestXrayDone = 'Yes' AND negativeResult.tbDiagnosticResult IS NOT NULL AND clientObservation.cadScore >= 40) THEN CAST(clientObservation.dateOfChestXrayResultTestDone AS DATE) ELSE NULL END)  AS dateTbScoreCad, " +
            "(CASE WHEN (clientObservation.clinicallyEvaulated = 'Yes' AND clientObservation.tbScreeningType ILIKE '%Chest X-Ray with CAD and/or Symptom screening%' AND clientObservation.chestXrayResult = 'Yes' AND clientObservation.cadScore >= 40 AND negativeResult.tbDiagnosticResult IS NOT NULL) THEN clientObservation.chestXrayResult ELSE NULL END) AS resultTbScoreCad,\n" +
            "clientObservation.rank1\n" +
            "FROM patient_person p\n" +
            "INNER JOIN hiv_enrollment he ON he.person_uuid = p.uuid\n" +
            " LEFT JOIN base_organisation_unit facility ON facility.id=p.facility_id\n" +
            "LEFT JOIN base_organisation_unit facility_lga ON facility_lga.id=facility.parent_organisation_unit_id\n" +
            "LEFT JOIN base_organisation_unit facility_state ON facility_state.id=facility_lga.parent_organisation_unit_id\n" +
            "LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=p.facility_id AND boui.name='DATIM_ID'\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid personUuid, date_of_observation dateOfObservation, data->'tbIptScreening'->>'tbScreeningType' tbScreeningType, \n" +
            "CAST(data->'tptMonitoring'->>'cadScore' AS INTEGER) cadScore, data->'tbIptScreening'->>'status' tbStatus, NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'dateSpecimenSent', '') AS DATE), NULL) dateSpecimenSent, data->'tbIptScreening'->>'specimenType' specimenType,\n" +
            "INITCAP(data->'tbIptScreening'->>'diagnosticTestDone') diagnosticTestDone, INITCAP(data->'tptMonitoring'->>'clinicallyEvaulated') clinicallyEvaulated, data->'tbIptScreening'->>'resultOfClinicalEvaluation' resultOfClinicalEvaluation,\n" +
            "data->'tbIptScreening'->>'tbType' tbType, NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'dateOfChestXrayResultTestDone', '') AS DATE), NULL) dateOfChestXrayResultTestDone, data->'tbIptScreening'->>'chestXrayResult' chestXrayResult, NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'tbTreatmentStartDate', '') AS DATE), NULL) tbTreatmentStartDate,\n" +
            "data->'tbIptScreening'->>'completedTbTreatment' completedTbTreatment,  data->'tbIptScreening'->>'treatmentOutcome' treatmentOutcome, NULLIF(CAST(NULLIF(data->'tbIptScreening'->>'completionDate', '') AS DATE), NULL) tbCompletionDate,\n" +
            "COALESCE(data->'tbIptScreening'->>'eligibleForTPT', data->'tptMonitoring'->>'eligibilityTpt', NULL) eligibleForTPT, data->'tptMonitoring'->>'contractionForTpt' contractionForTpt, data->'tbIptScreening'->>'chestXrayDone' chestXrayDone,\n" +
            "TRIM(BOTH ',' FROM\n" +
            "COALESCE(\n" +
            "CASE WHEN (data->'tptMonitoring'->>'liverSymptoms' IS NOT NULL AND data->'tptMonitoring'->>'liverSymptoms' != '' AND data->'tptMonitoring'->>'liverSymptoms' != 'No') THEN ',Liver Symptoms' ELSE '' END ||\n" +
            "CASE WHEN (data->'tptMonitoring'->>'chronicAlcohol' IS NOT NULL AND data->'tptMonitoring'->>'chronicAlcohol' != '' AND data->'tptMonitoring'->>'chronicAlcohol' != 'No') THEN ',Chronic Alcohol' ELSE '' END ||\n" +
            "CASE WHEN (data->'tptMonitoring'->>'neurologicSymptoms' IS NOT NULL AND data->'tptMonitoring'->>'neurologicSymptoms' != '' AND data->'tptMonitoring'->>'neurologicSymptoms' != 'No') THEN ',Neurologic Symptoms' ELSE '' END ||\n" +
            "CASE WHEN (data->'tptMonitoring'->>'rash' IS NOT NULL AND data->'tptMonitoring'->>'rash' != '' AND data->'tptMonitoring'->>'rash' != 'No') THEN ',Rash' ELSE '' END,\n" +
            "'')) AS contractionOptions, COALESCE(NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'dateTptEnded', '') AS DATE), NULL), NULLIF(CAST(NULLIF(data->'tptMonitoring'->>'dateOfTptCompleted', '') AS DATE), NULL)) iptCompletionDate, \n" +
            "CAST(data->'tptMonitoring'->>'outComeOfIpt' AS VARCHAR) iptCompletionStatus, data->'tptMonitoring'->>'weight' weightAt,\n" +
            "ROW_NUMBER () OVER (PARTITION BY person_uuid ORDER BY date_of_observation DESC) as rank1\n" +
            "FROM hiv_observation where type= 'Chronic Care' AND date_of_observation BETWEEN ?2 AND ?3\n" +
            ") clientObservation ON clientObservation.personUuid = he.person_uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT person_uuid, visit_date as dateOfIptStart, regimen_name regimenName\n" +
            "FROM ( \n" +
            "SELECT h.person_uuid, h.visit_date, CAST(pharmacy_object ->> 'regimenName' AS VARCHAR) AS regimen_name, \n" +
            "ROW_NUMBER() OVER (PARTITION BY h.person_uuid ORDER BY h.visit_date ASC) AS rnk \n" +
            "FROM hiv_art_pharmacy h \n" +
            "INNER JOIN jsonb_array_elements(h.extra -> 'regimens') WITH ORDINALITY p(pharmacy_object) ON TRUE \n" +
            "INNER JOIN hiv_regimen hr ON hr.description = CAST(p.pharmacy_object ->> 'regimenName' AS VARCHAR) \n" +
            "INNER JOIN hiv_regimen_type hrt ON hrt.id = hr.regimen_type_id  AND hrt.id = 15 AND hrt.id NOT IN (1,2,3,4,14, 16) \n" +
            "WHERE hrt.id = 15 AND h.archived = 0 AND h.visit_date BETWEEN ?2 AND ?3\n" +
            ") AS ic \n" +
            "WHERE ic.rnk = 1\n" +
            ") tptClients ON tptClients.person_uuid = he.person_uuid\n" +
            "LEFT JOIN (\n" +
            "select person_uuid, date_completed as iptCompletionDate, iptCompletionStatus from ( \n" +
            "select person_uuid, cast(ipt->>'dateCompleted' as date) as date_completed, \n" +
            "CAST(REPLACE(ipt->>'completionStatus', ',','') AS VARCHAR) AS iptCompletionStatus, \n" +
            "row_number () over (partition by person_uuid order by cast(ipt->>'dateCompleted' as date) desc) as rnk \n" +
            "from hiv_art_pharmacy where (ipt->>'dateCompleted' is not null and ipt->>'dateCompleted' != 'null' and ipt->>'dateCompleted' != '' AND TRIM(ipt->>'dateCompleted') <> '') \n" +
            "and archived = 0) ic where ic.rnk = 1\n" +
            ") iptCompletionFromPharmacy ON iptCompletionFromPharmacy.person_uuid = he.person_uuid\n" +
            "LEFT JOIN (\n" +
            "SELECT (CASE WHEN llt.lab_test_name ILIKE '%TB LAMP%' THEN 'TB-LAM' ELSE llt.lab_test_name END) AS tbDiagnosticTestType, llt.id, CAST(sm.date_sample_collected AS DATE) dateofDiagnosticTestSampleCollected, \n" +
            "(CASE WHEN lr.result_reported ILIKE '%Negative%' THEN 'Negative' ELSE result_reported END) as tbDiagnosticResult, CAST(lr.date_result_reported AS DATE) as dateofTbDiagnosticResultReceived, sm.patient_uuid,\n" +
            "CAST(lr.date_assayed AS DATE) dateDiagnosticEvaluation, ROW_NUMBER() OVER (PARTITION BY sm.patient_uuid ORDER BY sm.date_sample_collected DESC) rnkkkk\n" +
            "FROM public.laboratory_sample  sm\n" +
            "INNER JOIN public.laboratory_test lt ON lt.id = sm.test_id\n" +
            "INNER JOIN  laboratory_labtest llt on llt.id = lt.lab_test_id\n" +
            "JOIN laboratory_result lr ON lr.test_id = sm.test_id\n" +
            "WHERE lt.lab_test_id IN (65, 51, 64, 67, 72, 71, 86, 58, 73)\n" +
            "AND sm.archived = 0 \n" +
            "AND lr.archived = 0\n" +
            "AND date_sample_collected IS NOT null \n" +
            "AND sm.date_sample_collected <= ?3\n" +
            ") tbLabSample ON tbLabSample.patient_uuid = clientObservation.personUuid AND tbLabSample.dateofDiagnosticTestSampleCollected = clientObservation.dateOfObservation\n" +
            "LEFT JOIN (\n" +
            "SELECT sm.patient_uuid as personTbResult, CASE WHEN (CAST(lr.date_result_reported AS DATE) > NOW() AND lr.result_reported IS NOT NULL) THEN NULL ELSE lr.result_reported END  as tbDiagnosticResult,\n" +
            "CASE WHEN CAST(lr.date_result_reported AS DATE) > NOW() THEN NULL ELSE CAST(lr.date_result_reported AS DATE) END  as dateofTbDiagnosticResultReceived,cast(sm.date_sample_collected as date) AS dateOfTbSampleCollected,\n" +
            "lt.lab_test_id, sm.date_sample_collected, ROW_NUMBER() OVER (PARTITION BY  sm.patient_uuid ORDER BY sm.date_sample_collected DESC) AS rnkkk\n" +
            "     FROM laboratory_sample sm\n" +
            " INNER JOIN laboratory_test lt on lt.id = sm.test_id\n" +
            " LEFT JOIN laboratory_result lr ON lt.id = lr.test_id\n" +
            " WHERE lt.lab_test_id IN (86, 65, 67, 64, 58, 51, 73, 72, 71) and sm.archived = 0 AND sm.date_sample_collected IS NOT NULL AND (lr.result_reported ILIKE '%negative%' OR lr.result_reported ILIKE '%MTB not detected%')\n" +
            ") negativeResult ON negativeResult.personTbResult = clientObservation.personUuid AND negativeResult.dateOfTbSampleCollected = clientObservation.dateOfObservation\n"+
            "WHERE p.facility_id = ?1 AND clientObservation.dateOfObservation IS NOT NULL\n";
}
