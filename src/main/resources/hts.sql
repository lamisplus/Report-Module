SELECT hc.client_code AS clientCode,
       (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'first_name') ELSE INITCAP(pp.first_name) END) AS firstName,
       (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'surname') ELSE INITCAP(pp.surname) END) AS surname,
       (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'middile_name') ELSE INITCAP(pp.other_name) END) AS otherName,
       (CASE WHEN hc.person_uuid IS NULL THEN INITCAP(hc.extra->>'gender') ELSE INITCAP(pp.sex) END) AS sex,
       (CASE WHEN hc.person_uuid IS NULL THEN CAST(hc.extra->>'age' AS INTEGER)
             ELSE CAST(EXTRACT(YEAR from AGE(NOW(),  pp.date_of_birth)) AS INTEGER )
           END) AS age,
       (CASE WHEN hc.person_uuid IS NOT NULL THEN pp.date_of_birth
             WHEN hc.person_uuid IS NULL AND LENGTH(hc.extra->>'date_of_birth') > 0
                 AND hc.extra->>'date_of_birth' != '' THEN CAST(NULLIF(hc.extra->>'date_of_birth', '') AS DATE)
             ELSE NULL END) AS dateOfBirth,
       (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'phone_number'
             ELSE pp.contact_point->'contactPoint'->0->'value'->>0 END) AS phoneNumber,
       (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'marital_status'
             ELSE pp.marital_status->>'display' END) AS maritalStatus,
--modified
       (CASE WHEN hc.person_uuid IS NULL
                 THEN hc.extra->>'lga_of_residence' ELSE lga.name END) AS LGAOfResidence,
--modified
       (CASE WHEN hc.person_uuid IS NULL
                 THEN hc.extra->>'state_of_residence' ELSE state.name END) AS StateOfResidence,
       facility.name AS facility,
       state.name AS state,
       lga.name AS lga,
       pp.uuid AS patientId,
       pp.education->>'display' as education,

    pp.employment_status->>'display' as occupation,
    boui.code as datimCode,
    hc.others->>'latitude' AS HTSLatitude,
    hc.others->>'longitude' AS HTSLongitude,
    (CASE WHEN hc.person_uuid IS NULL THEN hc.extra->>'client_address' ELSE r.address END) AS clientAddress,
    hc.date_visit AS dateVisit,
    (CASE WHEN hc.first_time_visit IS true THEN 'Yes' ELSE 'No' END) firstTimeVisit,
    hc.num_children AS numberOfChildren,
    hc.num_wives AS numberOfWives,
    (CASE WHEN hc.index_client IS true THEN 'Yes' ELSE 'No' END) indexClient,
    (CASE WHEN hc.prep_offered IS true THEN 'Yes' ELSE 'No' END)  AS prepOffered,
    (CASE WHEN hc.prep_accepted IS true THEN 'Yes' ELSE 'No' END) AS prepAccepted,
    (CASE WHEN hc.previously_tested IS true THEN 'Yes' ELSE 'No' END) AS previouslyTested,
    tg.display AS targetGroup,
    rf.display AS referredFrom,
    ts.display AS testingSetting,
    tc.display AS counselingType,
    preg.display AS pregnacyStatus,
--modifiied
    it.display AS indexType,
--hc.breast_feeding AS breastFeeding,
--     (CASE
--     WHEN preg.display='Breastfeeding' THEN 'Yes'
--     WHEN preg.display IS NULL THEN NULL
--     ELSE 'No'
--     END) AS breastFeeding,
--relation.display AS indexType,
    (CASE WHEN hc.recency->>'optOutRTRI' ILIKE 'true' THEN 'Yes'
    WHEN hc.recency->>'optOutRTRI' ILIKE 'false' THEN 'No'
    WHEN hc.recency->>'optOutRTRI' != NULL THEN hc.recency->>'optOutRTRI'
    ELSE NULL END) AS IfRecencyTestingOptIn,
    hc.recency->>'rencencyId' AS RecencyID,
    hc.recency->>'optOutRTRITestName' AS recencyTestType,
    (CASE WHEN hc.recency->>'optOutRTRITestDate' IS NOT NULL
    AND hc.recency->>'optOutRTRITestDate' != '' AND LENGTH(hc.recency->>'optOutRTRITestDate') > 0
    THEN CAST(NULLIF(hc.recency->>'optOutRTRITestDate', '') AS DATE)
    WHEN hc.recency->>'sampleTestDate' IS NOT NULL
    AND hc.recency->>'sampleTestDate' != '' AND LENGTH(hc.recency->>'sampleTestDate') > 0
    THEN CAST(NULLIF(hc.recency->>'sampleTestDate', '') AS DATE) ELSE NULL END) AS recencyTestDate,
    (CASE
    WHEN hc.recency->>'rencencyInterpretation' IS NOT NULL
    AND hc.recency->>'rencencyInterpretation' ILIKE '%Long%' THEN 'RTRI Longterm'
    WHEN hc.recency->>'rencencyInterpretation' IS NOT NULL
    AND hc.recency->>'rencencyInterpretation' ILIKE '%Recent%' THEN 'RTRI Recent'
    ELSE hc.recency->>'rencencyInterpretation' END) AS recencyInterpretation,
    hc.recency->>'finalRecencyResult' AS finalRecencyResult,
    hc.recency->>'viralLoadResultClassification' AS viralLoadResult,
    CAST(NULLIF(hc.recency->>'sampleCollectedDate', '') AS DATE) AS viralLoadSampleCollectionDate,
    hc.recency->>'viralLoadConfirmationResult' AS viralLoadConfirmationResult,
    CAST(NULLIF(hc.recency->>'dateSampleSentToPCRLab', '') AS DATE) AS viralLoadConfirmationDate,
--CAST(NULLIF(hc.recency->>'viralLoadConfirmationTestDate', '') AS DATE) AS viralLoadConfirmationDate,
    hc.risk_stratification_code AS Assessmentcode,
    modality_code.display AS modality,
    (CASE WHEN hc.syphilis_testing->>'syphilisTestResult' ILIKE 'Yes'
    THEN 'Reactive' ELSE 'Non-Reactive' END) As syphilisTestResult,
    (CASE WHEN hc.hepatitis_testing->>'hepatitisBTestResult' ILIKE 'Yes'
    THEN 'Positive' ELSE 'Negative' END) AS hepatitisBTestResult,
    (CASE WHEN hc.hepatitis_testing->>'hepatitisCTestResult' ILIKE 'Yes'
    THEN 'Positive' ELSE 'Negative' END) AS hepatitisCTestResult,
    hc.cd4->>'cd4Count' AS CD4Type,
--hc.cd4->>'cd4SemiQuantitative' AS CD4TestResult,
    (CASE
    WHEN hc.cd4->>'cd4Count'= 'Semi-Quantitative' THEN hc.cd4->>'cd4SemiQuantitative'
    WHEN hc.cd4->>'cd4Count'= 'Flow Cyteometry' THEN hc.cd4->>'cd4FlowCyteometry'
    ELSE NULL
    END) AS CD4TestResult,
    (CASE WHEN hc.test1->>'result' ILIKE 'Yes' THEN 'Positive' ELSE 'Negative' END)AS hivTestResult,
    hc.hiv_test_result AS finalHIVTestResult,
    (CASE WHEN LENGTH(hc.test1->>'date') > 0 AND hc.test1->>'date' !=''
    THEN CAST(NULLIF(hc.test1->>'date', '') AS DATE)
    WHEN hc.date_visit IS NOT NULL THEN hc.date_visit
    ELSE NULL END)dateOfHIVTesting,
    CAST(post_test_counseling->>'condomProvidedToClientCount' AS VARCHAR) AS numberOfCondomsGiven,
    CAST(post_test_counseling->>'lubricantProvidedToClientCount' AS VARCHAR) AS numberOfLubricantsGiven


---From tables
FROM hts_client hc
    LEFT JOIN base_application_codeset tg ON tg.code = hc.target_group
--newly added
    LEFT JOIN base_application_codeset it ON it.id = hc.relation_with_index_client
    LEFT JOIN base_application_codeset rf ON rf.id = hc.referred_from
    LEFT JOIN base_application_codeset ts ON ts.code = hc.testing_setting
    LEFT JOIN base_application_codeset tc ON tc.id = hc.type_counseling
    LEFT JOIN base_application_codeset preg ON preg.id = hc.pregnant
    LEFT JOIN base_application_codeset relation ON relation.id = hc.relation_with_index_client
    LEFT JOIN hts_risk_stratification hrs ON hrs.code = hc.risk_stratification_code
    LEFT JOIN base_application_codeset modality_code ON modality_code.code = hrs.modality
    LEFT JOIN patient_person pp ON pp.uuid=hc.person_uuid
--modified
    LEFT JOIN (SELECT * FROM (SELECT
    p.id,
    p.address #>>'{address,0,city}' as clientcity,
    p.address #>> '{address,0,line,0}' as clientaddress,
    p.address #>>'{address,0,district}' as lgaid,
    p.address #>> '{address,0,stateId}' as stateid,
--concat (p.address #>>'{address,0,city}',' :',p.address #>> '{address,0,line,0}') "address"
    (jsonb_array_elements(p.address->'address')->>'city') as address

    FROM patient_person p) as result ) r ON r.id=pp.id
    LEFT JOIN base_organisation_unit res_state ON res_state.id=CAST(r.stateid AS BIGINT)
    LEFT JOIN base_organisation_unit res_lga ON res_lga.id=CAST(r.lgaid AS BIGINT)
    LEFT JOIN base_organisation_unit facility ON facility.id=hc.facility_id
    LEFT JOIN base_organisation_unit state ON state.id=facility.parent_organisation_unit_id
    LEFT JOIN base_organisation_unit lga ON lga.id=state.parent_organisation_unit_id
    LEFT JOIN base_organisation_unit_identifier boui ON boui.organisation_unit_id=hc.facility_id AND boui.name='DATIM_ID'
WHERE hc.archived=0

