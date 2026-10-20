package org.lamisplus.modules.report.repository.queries;

public class PMTCTMSFQuery {

    public static final String PMTCT_MONTHLY_MSF_QUERY = "WITH pmtctSummary AS (\n" +
            "SELECT\n" +
            "he.observation->>'previouslyKnownHivPositive' previouslyKnownHivPositive, \n" +
            "CASE WHEN he.observation->>'previouslyKnownHivPositive' = 'Yes' THEN 'Yes' ELSE NULL END hivknownpositiveatentry, \n" +
            "CASE WHEN he.observation->>'finalHivTestResult' = 'Positive' AND he.observation->>'previouslyKnownHivPositive' = 'No' THEN 'Yes' ELSE 'No' END AS hivewlytestedpositive, \n" +
            "CASE WHEN he.observation->>'finalHivTestResult' = 'Negative' AND he.observation->>'previouslyKnownHivPositive' = 'No' THEN 'Yes' ELSE 'No' END AS hivnewlytestednegative, \n" +
            "he.observation->'syphilisInfo'->>'testResult' syphilisTestResult,\n" +
            "CASE WHEN he.observation->'hbvInfo'->>'testResult' = 'Positive' AND he.observation->'hbvInfo'->>'knownPositive' = 'Yes' THEN 'Yes' ELSE 'No' END AS hepatitisKnownPositive,\n" +
            "he.observation->'hbvInfo'->>'testResult' hbvInfoTestResult,\n" +
            "CASE WHEN he.observation->>'suspectedAcuteInfection' = 'YES_NO_YES' THEN 'Yes' ELSE 'No' END AS suspectedAcuteInfection,\n" +
            "he.observation->>'finalHivTestResult' finalHivTestResult,\n" +
            "he.observation->'syphilisInfo'->>'testResult' testResult,\n" +
            "he.observation->>'facilitySetting' facilitySetting,\n" +
            "he.observation->>'testingType' retest,\n" +
            "CASE\n" +
            "    WHEN EXTRACT(YEAR FROM AGE(p.date_of_birth)) < 15 THEN '< 15'\n" +
            "    ELSE '>= 15'\n" +
            "END AS ageGroup\n" +
            "FROM hts_encounter he\n" +
            "INNER JOIN patient_person p ON p.uuid = he.patient_uuid\n" +
            "WHERE he.pmtct_hts IS TRUE AND he.archived IS FALSE AND p.archived = 0 AND he.date_of_visit BETWEEN ?2 AND ?3 AND he.facility_id = ?1\n" +
            "),\n" +
            "pmtctAnc AS (\n" +
            "SELECT patient_uuid, referred_from_spokes_site,\n" +
            "CASE\n" +
            "    WHEN EXTRACT(YEAR FROM AGE(p.date_of_birth)) < 15 THEN '< 15'\n" +
            "    ELSE '>= 15'\n" +
            "END AS ageGroup\n" +
            "FROM pmtct_anc pa\n" +
            "INNER JOIN patient_person p ON p.uuid = pa.patient_uuid\n" +
            "WHERE pa.archived IS FALSE AND p.archived = 0 AND pa.date_of_enrollment BETWEEN ?2 AND ?3 AND pa.facility_id = ?1\n" +
            "),\n" +
            "pmtctEnrollment AS (\n" +
            "-- pmtct_enrollment\n" +
            "SELECT patient_uuid, \n" +
            "syphilis_details->>'testResult' syphilisTestResult,\n" +
            "art_start_time, syphilis_details, hbv_details->>'testResult' hbvtestResult, hbv_details->>'treatmentType' treatmentType,\n" +
            "hbv_details, pmtct_enrollment_date,\n" +
            "CASE\n" +
            "    WHEN EXTRACT(YEAR FROM AGE(p.date_of_birth)) < 15 THEN '< 15'\n" +
            "    ELSE '>= 15'\n" +
            "END AS ageGroup\n" +
            "FROM pmtct_enrollment pe\n" +
            "INNER JOIN patient_person p ON p.uuid = pe.patient_uuid\n" +
            "WHERE pe.archived IS FALSE AND p.archived = 0 AND pmtct_enrollment_date BETWEEN ?2 AND ?3 AND pe.facility_id = ?1\n" +
            "),\n" +
            "pmtctMotherVisit AS (\n" +
            "SELECT DISTINCT ON (pm.patient_uuid) pm.patient_uuid, pm.ga_of_viral_load, pm.date_of_viral_load, pm.result_of_viral_load,\n" +
            "time_of_viral_load, visit_status,\n" +
            "CASE\n" +
            "    WHEN EXTRACT(YEAR FROM AGE(p.date_of_birth)) < 15 THEN '< 15'\n" +
            "    ELSE '>= 15'\n" +
            "END AS ageGroup\n" +
            "FROM pmtct_mother_visitation pm\n" +
            "INNER JOIN patient_person p ON p.uuid = pm.patient_uuid\n" +
            "WHERE pm.archived IS FALSE AND p.archived = 0 AND date_of_visit BETWEEN ?2 AND ?3 AND pm.facility_id = ?1\n" +
            "),\n" +
            "pmtctBooking AS (\n" +
            "SELECT DISTINCT ON (patient_uuid) patient_uuid, booking_status, date_of_delivery, number_of_infants_alive, number_of_infants_dead FROM pmtct_delivery\n" +
            "WHERE archived IS FALSE AND date_of_delivery BETWEEN ?2 AND ?3 AND facility_id = ?1\n" +
            "),\n" +
            "infantInfomation AS (\n" +
            "SELECT DISTINCT ON (infant_patient_uuid) infant_patient_uuid, infant_arv_data->>'infantArvType' infantArvType, infant_arv_data->>'timingOfAvrAfter72Hours' timingOfAvrAfter72Hours, \n" +
            "infant_arv_data->>'timingOfAvrWithin72Hours' timingOfAvrWithin72Hours, infant_arv_data->>'infantArvTime' infantArvTime, date_of_delivery\n" +
            "FROM pmtct_infant_information\n" +
            "WHERE archived IS FALSE AND date_of_delivery BETWEEN ?2 AND ?3 AND facility_id = ?1\n" +
            "),\n" +
            "infantPcr AS (\n" +
            "SELECT infant_outcome_at18_months, infant_pcr_data->>'ageAtTest' ageAtTest, CAST(infant_pcr_data->>'dateSampleCollected' AS DATE) dateSampleCollected, infant_pcr_data->>'results'  results, infant_pcr_data \n" +
            "FROM pmtct_infant_visit \n" +
            "WHERE infant_pcr_data IS NOT NULL AND archived IS FALSE AND visit_date BETWEEN ?2 AND ?3 AND facility_id = ?1\n" +
            "),\n" +
            "infantVisit AS (\n" +
            "SELECT DISTINCT ON (infant_hospital_number) infant_hospital_number, infant_outcome_at18_months\n" +
            "\tFROM pmtct_infant_visit WHERE archived IS FALSE AND infant_outcome_at18_months <> '' AND visit_date BETWEEN ?2 AND ?3 AND facility_id = ?1\n" +
            "),\n" +
            "prepIn AS (\n" +
            "SELECT DISTINCT ON (pi.person_uuid) pi.person_uuid, he.observation->>'pregnancyStatus' pregnancyStatus\n" +
            "FROM prophylaxis_initiation pi\n" +
            "JOIN hts_encounter he ON he.uuid = pi.hts_encounter_uuid\n" +
            "WHERE pi.archived IS FALSE AND he.archived IS FALSE AND he.observation->>'pregnancyStatus' IS NOT NULL AND pi.date_prep_started BETWEEN ?2 AND ?3 AND pi.facility_id = ?1\n" +
            "),\n" +
            "report_rows AS (\n" +
            "    SELECT *\n" +
            "    FROM (\n" +
            "        VALUES\n" +
            "            (1,'HIV','HIV Known Positive at Entry'),\n" +
            "            (2,'HIV','HIV newly Tested Positive'),\n" +
            "            (3,'HIV','HIV Newly Tested Negative'),\n" +
            "            (4,'SYPHILIS','Newly tested Positive'),\n" +
            "            (5,'SYPHILIS','Newly tested Negative'),\n" +
            "            (6,'HEPATITIS_B','Known Positive at entry'),\n" +
            "            (7,'HEPATITIS_B','Newly tested Positive'),\n" +
            "            (8,'HEPATITIS_B','Newly tested Negative')\n" +
            "    ) t(sort_order, section, rowlabel)\n" +
            "),\n" +
            "preg_bres AS (\n" +
            "    SELECT * FROM (VALUES\n" +
            "        (1, 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),\n" +
            "        (2, 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),\n" +
            "        (3, 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),\n" +
            "\t\t(4, 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),\n" +
            "\t\t(5, 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)')\n" +
            "    ) AS t(sort_order, preg_br)\n" +
            "),\n" +
            "retestes AS (\n" +
            "    SELECT * FROM (VALUES\n" +
            "        (1, 'Seroconverted'),\n" +
            "        (2, 'Remained Negative')\n" +
            "    ) AS t(sort_order, retest)\n" +
            "),\n" +
            "viralloads AS (\n" +
            "    SELECT * FROM (VALUES\n" +
            "        (1, 'Suppressed'),\n" +
            "        (2, 'Unsuppresed')\n" +
            "    ) AS t(sort_order, viralload)\n" +
            "),\n" +
            "anc_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE agegroup = '< 15') AS less_15,\n" +
            "        COUNT(*) FILTER (WHERE agegroup = '>= 15') AS greater_15,\n" +
            "COUNT(*) FILTER (WHERE referred_from_spokes_site <> '') AS referred_spoke,\n" +
            "        COUNT(*) AS total\n" +
            "    FROM pmtctAnc\n" +
            "),\n" +
            "anc_summary AS (\n" +
            "    SELECT\n" +
            "        'ANC' AS section,\n" +
            "        'Number of  new ANC Clients' AS rowLabel,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM anc_summary_wide w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'less_15', w.less_15),\n" +
            "            (2, 'greater_15', w.greater_15),\n" +
            "            (3, 'total', w.total)\n" +
            "    ) kv(ord,columnKey,value)\n" +
            "),\n" +
            "spoke_summary AS (\n" +
            "    SELECT\n" +
            "        'SPOKE' AS section,\n" +
            "        'referred from spoke' AS rowLabel,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM anc_summary_wide w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'referred_spoke', w.referred_spoke)\n" +
            "    ) kv(ord,columnKey,value)\n" +
            "),\n" +
            "hts_summary_wide AS (\n" +
            "    SELECT\n" +
            "        r.sort_order,\n" +
            "        r.section,\n" +
            "        r.rowlabel,\n" +
            "        COUNT(*) FILTER (WHERE p.facilitySetting = 'FACILITY_HTS_TEST_SETTING_ANC' AND p.ageGroup = '< 15')  AS anc_less_15,\n" +
            "        COUNT(*) FILTER (WHERE p.facilitySetting = 'FACILITY_HTS_TEST_SETTING_ANC' AND p.ageGroup = '>= 15') AS anc_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE p.facilitySetting = 'FACILITY_HTS_TEST_SETTING_L&D' AND p.ageGroup = '< 15')  AS ld_less_15,\n" +
            "        COUNT(*) FILTER (WHERE p.facilitySetting = 'FACILITY_HTS_TEST_SETTING_L&D' AND p.ageGroup = '>= 15') AS ld_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE p.facilitySetting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' AND p.ageGroup = '< 15')  AS natal_less_15,\n" +
            "        COUNT(*) FILTER (WHERE p.facilitySetting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' AND p.ageGroup = '>= 15') AS natal_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE p.facilitySetting IN (\n" +
            "            'FACILITY_HTS_TEST_SETTING_ANC',\n" +
            "            'FACILITY_HTS_TEST_SETTING_L&D',\n" +
            "            'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING'\n" +
            "        ) AND p.ageGroup IS NOT NULL) AS total\n" +
            "    FROM report_rows r\n" +
            "    LEFT JOIN pmtctSummary p ON (\n" +
            "           (r.sort_order = 1 AND p.hivknownpositiveatentry = 'Yes')\n" +
            "        OR (r.sort_order = 2 AND p.hivewlytestedpositive  = 'Yes')\n" +
            "        OR (r.sort_order = 3 AND p.hivnewlytestednegative = 'Yes')\n" +
            "\n" +
            "        OR (r.sort_order = 4 AND p.syphilisTestResult = 'Positive')\n" +
            "        OR (r.sort_order = 5 AND p.syphilisTestResult = 'Negative')\n" +
            "\n" +
            "        OR (r.sort_order = 6 AND p.hepatitisKnownPositive = 'Yes')\n" +
            "        OR (r.sort_order = 7 AND p.hbvInfoTestResult = 'Positive' AND p.hepatitisKnownPositive = 'No')\n" +
            "        OR (r.sort_order = 8 AND p.hbvInfoTestResult = 'Negative')\n" +
            "    )\n" +
            "    GROUP BY r.sort_order, r.section, r.rowlabel\n" +
            "),\n" +
            "hts_summary AS (\n" +
            "    SELECT\n" +
            "        w.sort_order,\n" +
            "        w.section,\n" +
            "        w.rowlabel,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM hts_summary_wide w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'anc_less_15',       w.anc_less_15),\n" +
            "            (2, 'anc_greater_15',    w.anc_greater_15),\n" +
            "            (3, 'ld_less_15',        w.ld_less_15),\n" +
            "            (4, 'ld_greater_15',     w.ld_greater_15),\n" +
            "            (5, 'natal_less_15',     w.natal_less_15),\n" +
            "            (6, 'natal_greater_15',  w.natal_greater_15),\n" +
            "            (7, 'total',             w.total)\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "acute_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_ANC' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '< 15')  AS anc_less_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_ANC' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '>= 15') AS anc_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_L&D' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '< 15')  AS ld_less_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_L&D' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '>= 15') AS ld_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '< 15')  AS natal_less_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '>= 15') AS natal_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE suspectedAcuteInfection = 'Yes') AS total\n" +
            "    FROM pmtctSummary\n" +
            "),\n" +
            "acute_summary AS (\n" +
            "    SELECT\n" +
            "        'ACUTE' AS section,\n" +
            "        'Suspected Acute' AS rowLabel,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM acute_summary_wide w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'anc_less_15',       w.anc_less_15),\n" +
            "            (2, 'anc_greater_15',    w.anc_greater_15),\n" +
            "            (3, 'ld_less_15',        w.ld_less_15),\n" +
            "            (4, 'ld_greater_15',     w.ld_greater_15),\n" +
            "            (5, 'natal_less_15',     w.natal_less_15),\n" +
            "            (6, 'natal_greater_15',  w.natal_greater_15),\n" +
            "            (7, 'total',             w.total)\n" +
            "    ) kv(ord,columnKey,value)\n" +
            "),\n" +
            "retest_summary_wide AS (\n" +
            "    SELECT r.retest, \n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_ANC' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '< 15')  AS anc_less_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_ANC' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '>= 15') AS anc_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_L&D' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '< 15')  AS ld_less_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_L&D' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '>= 15') AS ld_greater_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '< 15')  AS natal_less_15,\n" +
            "        COUNT(*) FILTER (WHERE facilitySetting = 'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING' AND suspectedAcuteInfection = 'Yes' AND ageGroup = '>= 15') AS natal_greater_15,\n" +
            "         COUNT(*) FILTER (WHERE p.facilitySetting IN (\n" +
            "            'FACILITY_HTS_TEST_SETTING_ANC',\n" +
            "            'FACILITY_HTS_TEST_SETTING_L&D',\n" +
            "            'FACILITY_HTS_TEST_SETTING_POST_NATAL_WARD_BREASTFEEDING'\n" +
            "        ) AND p.ageGroup IS NOT NULL) AS total\n" +
            "    FROM  retestes r\n" +
            "\tLEFT JOIN pmtctSummary p ON (\n" +
            "           (r.sort_order = 1 AND p.retest = 'RETESTING' AND p.finalhivtestresult !='Positive')\n" +
            "        OR (r.sort_order = 2 AND p.retest = 'RETESTING' AND p.finalhivtestresult ='Positive')\n" +
            "    )\n" +
            "\tGROUP BY  r.retest\n" +
            "),\n" +
            "retest_summary AS (\n" +
            "    SELECT\n" +
            "        'RETEST' AS section,\n" +
            "        w.retest AS rowLabel,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM retest_summary_wide w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'anc_less_15',       w.anc_less_15),\n" +
            "            (2, 'anc_greater_15',    w.anc_greater_15),\n" +
            "            (3, 'ld_less_15',        w.ld_less_15),\n" +
            "            (4, 'ld_greater_15',     w.ld_greater_15),\n" +
            "            (5, 'natal_less_15',     w.natal_less_15),\n" +
            "            (6, 'natal_greater_15',  w.natal_greater_15),\n" +
            "            (7, 'total',             w.total)\n" +
            "    ) kv(ord,columnKey,value)\n" +
            "),\n" +
            "hivtiming_summary_wide AS (\n" +
            "    SELECT art_start_time, \n" +
            "        COUNT(*) FILTER (WHERE ageGroup = '< 15')  AS timeless_15,\n" +
            "        COUNT(*) FILTER (WHERE ageGroup = '>= 15') AS timegreater_15,\n" +
            "\t\tCOUNT(*) AS total\n" +
            "    FROM  pmtctEnrollment \n" +
            "\tGROUP BY art_start_time\n" +
            "),\n" +
            "hivtiming_summary AS (\n" +
            "       SELECT\n" +
            "        'ART_TIMING' AS section,\n" +
            "        CASE pg.preg_br\n" +
            "            WHEN 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_' THEN 'Prior on ART'\n" +
            "            WHEN 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD' THEN 'Initiated <36 weeks GA'\n" +
            "            WHEN 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD' THEN 'Initiated >36 weeks GA'\n" +
            "            WHEN 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D' THEN 'Initiated at L&D'\n" +
            "            WHEN 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' THEN 'Initiated during Breastfeeding'\n" +
            "        END AS rowLabel,\n" +
            "        pg.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM preg_bres pg\n" +
            "    LEFT JOIN hivtiming_summary_wide w ON w.art_start_time = pg.preg_br\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'timeless_15',       w.timeless_15),\n" +
            "            (2, 'timegreater_15',    w.timegreater_15),\n" +
            "            (3, 'total',             w.total)\n" +
            "    ) kv(ord,columnKey,value)\n" +
            "),\n" +
            "syphilis_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE syphilisTestResult = 'Positive' AND agegroup = '< 15') AS less_15,\n" +
            "        COUNT(*) FILTER (WHERE syphilisTestResult = 'Positive' AND agegroup = '>= 15') AS greater_15,\n" +
            "        COUNT(*) FILTER (WHERE syphilisTestResult = 'Positive') AS total,\n" +
            "\t\tCOUNT(*) FILTER (WHERE hbvtestresult = 'Positive' AND treatmenttype != 'Treated' AND agegroup = '< 15') AS hbvnewless_15,\n" +
            "        COUNT(*) FILTER (WHERE hbvtestresult = 'Positive' AND treatmenttype != 'Treated' AND agegroup = '>= 15') AS hbvnewgreater_15,\n" +
            "        COUNT(*) FILTER (WHERE hbvtestresult = 'Positive' AND treatmenttype != 'Treated') AS hbvnewtotal,\n" +
            "\t\tCOUNT(*) FILTER (WHERE hbvtestresult = 'Positive' AND treatmenttype = 'Treated' AND agegroup = '< 15') AS hbvtreatedless_15,\n" +
            "        COUNT(*) FILTER (WHERE hbvtestresult = 'Positive' AND treatmenttype = 'Treated' AND agegroup = '>= 15') AS hbvtreatedgreater_15,\n" +
            "        COUNT(*) FILTER (WHERE hbvtestresult = 'Positive' AND treatmenttype = 'Treated') AS hbvtreatedtotal\n" +
            "    FROM pmtctEnrollment\n" +
            "),\n" +
            "syphilis_summary AS (\n" +
            "    SELECT\n" +
            "        'Syphilis_treated' AS section,\n" +
            "        'Syphilis treated' AS rowLabel,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM syphilis_summary_wide w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'less_15', w.less_15),\n" +
            "            (2, 'greater_15', w.greater_15),\n" +
            "            (3, 'total', w.total)\n" +
            "    ) kv(ord,columnKey,value)\n" +
            "),\n" +
            "\n" +
            "hbv_summary AS (\n" +
            "SELECT\n" +
            "    'HBV' AS section,\n" +
            "    v.rowLabel,\n" +
            "    kv.columnKey,\n" +
            "    kv.value\n" +
            "FROM syphilis_summary_wide w\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "    ('Prior on HBV Prophylaxis/Treatment', w.hbvtreatedless_15, w.hbvtreatedgreater_15, w.hbvtreatedtotal),\n" +
            "    ('New on Prophylaxis', w.hbvnewless_15, w.hbvnewgreater_15, w.hbvnewtotal)\n" +
            ") v (\n" +
            "    rowLabel,\n" +
            "    less15,\n" +
            "    greater15,\n" +
            "    total\n" +
            ")\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        (1, 'less15', v.less15),\n" +
            "        (2, 'greater15', v.greater15),\n" +
            "        (3, 'total', v.total)\n" +
            ") kv(ord,columnKey,value)\n" +
            "),\n" +
            "booked_summary_wide AS (\n" +
            "SELECT\n" +
            "    SUM(number_of_infants_alive) FILTER (WHERE booking_status = 'BOOKING_STATUS_BOOKED') AS booked_alive,\n" +
            "    SUM(number_of_infants_alive) FILTER (WHERE booking_status = 'BOOKING_STATUS_UNBOOKED') AS unbooked_alive,\n" +
            "    SUM(number_of_infants_dead) FILTER (WHERE booking_status = 'BOOKING_STATUS_BOOKED') AS booked_dead,\n" +
            "    SUM(number_of_infants_dead) FILTER (WHERE booking_status = 'BOOKING_STATUS_UNBOOKED') AS unbooked_dead,\n" +
            "    SUM(number_of_infants_alive) FILTER (WHERE booking_status IN ('BOOKING_STATUS_BOOKED', 'BOOKING_STATUS_UNBOOKED' )) AS total_alive,\n" +
            "    SUM(number_of_infants_dead) FILTER (WHERE booking_status IN ('BOOKING_STATUS_BOOKED', 'BOOKING_STATUS_UNBOOKED')) AS total_dead\n" +
            "FROM pmtctBooking\n" +
            "),\n" +
            "booked_summary AS (\n" +
            "SELECT\n" +
            "    'DELIVERY' AS section,\n" +
            "    v.rowLabel,\n" +
            "    kv.columnKey,\n" +
            "    kv.value\n" +
            "FROM booked_summary_wide w\n" +
            "CROSS JOIN LATERAL (\n" +
            "\n" +
            "    VALUES\n" +
            "        ('Livebirth', w.booked_alive, w.unbooked_alive, w.total_alive ),\n" +
            "        ('Stillbirth', w.booked_dead, w.unbooked_dead, w.total_dead )) v\n" +
            "\t\t(rowLabel, booked, unbooked, total)\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        (1,'booked',v.booked),\n" +
            "        (2,'unbooked',v.unbooked),\n" +
            "        (3,'total',v.total)\n" +
            ") kv(ord,columnKey,value)\n" +
            "),\n" +
            "exposedinfant_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE infantarvtime = 'Within 72 hour' AND infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_NVP') AS nvpless_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE infantarvtime = 'After 72 hour' AND infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_NVP') AS nvpgreater_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE infantarvtime = 'Within 72 hour' AND infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_NVP_+_AZT_') AS aztless_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE infantarvtime = 'After 72 hour' AND infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_NVP_+_AZT_') AS aztgreater_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE infantarvtime = 'Within 72 hour' AND infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_OTHER_(SPECIFY)') AS untless_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE infantarvtime = 'After 72 hour' AND infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_OTHER_(SPECIFY)') AS ungreater_72hr,\n" +
            "        COUNT(*) FILTER (WHERE infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_NVP') AS nvptotal,\n" +
            "\t\tCOUNT(*) FILTER (WHERE infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_NVP_+_AZT_') AS azptotal,\n" +
            "\t\tCOUNT(*) FILTER (WHERE infantarvtype = 'INFANT_ARV_PROPHYLAXIS_TYPE_OTHER_(SPECIFY)') AS thirdtotal\n" +
            "FROM infantInfomation\n" +
            "),\n" +
            "exposedinfant_summary AS (\n" +
            "SELECT\n" +
            "    'EXPOSED_INFANT' AS section,\n" +
            "    v.rowLabel,\n" +
            "    kv.columnKey,\n" +
            "    kv.value\n" +
            "FROM exposedinfant_summary_wide w\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        ('Single Regimen', w.nvpless_72hr, w.nvpgreater_72hr, w.nvptotal),\n" +
            "        ('Dual Regimen', w.aztless_72hr, w.aztgreater_72hr, w.azptotal),\n" +
            "        ('Triple Regimen', w.untless_72hr, w.ungreater_72hr, w.thirdtotal)\n" +
            ") v(rowLabel, within72hr, after72hr, total)\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        (1, 'within72hr', v.within72hr),\n" +
            "        (2, 'after72hr', v.after72hr),\n" +
            "        (3, 'total', v.total)\n" +
            ") kv(ord,columnKey,value)\n" +
            "),\n" +
            "pcr_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_<_72_HRS') AS sampleless_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS') AS sampleless_2month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = '2ND_PCR_CHILD_TEST_AGE_2-12_MONTHS') AS sampleless_12month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_>12_MONTHS') AS samplegreater_12month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_<_72_HRS' AND results = 'INFANT_PCR_RESULT_POSITIVE') AS posresultless_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_<_72_HRS' AND results = 'INFANT_PCR_RESULT_NEGATIVE') AS negresultless_72hr,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS' AND results = 'INFANT_PCR_RESULT_POSITIVE') AS posresultless_2month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS' AND results = 'INFANT_PCR_RESULT_NEGATIVE') AS negresultless_2month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = '2ND_PCR_CHILD_TEST_AGE_2-12_MONTHS' AND results = 'INFANT_PCR_RESULT_POSITIVE') AS posresultless_12month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = '2ND_PCR_CHILD_TEST_AGE_2-12_MONTHS' AND results = 'INFANT_PCR_RESULT_NEGATIVE') AS negresultless_12month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_>12_MONTHS' AND results = 'INFANT_PCR_RESULT_POSITIVE') AS posresultgreater_12month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest = 'CHILD_TEST_AGE_>12_MONTHS' AND results = 'INFANT_PCR_RESULT_NEGATIVE') AS negresultgreater_12month,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest IN  ('CHILD_TEST_AGE_<_72_HRS', 'CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS', '2ND_PCR_CHILD_TEST_AGE_2-12_MONTHS', 'CHILD_TEST_AGE_>12_MONTHS')) AS totalsamples,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest IN  ('CHILD_TEST_AGE_<_72_HRS', 'CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS', '2ND_PCR_CHILD_TEST_AGE_2-12_MONTHS', 'CHILD_TEST_AGE_>12_MONTHS') AND results = 'INFANT_PCR_RESULT_POSITIVE') AS totalpositives,\n" +
            "\t\tCOUNT(*) FILTER (WHERE ageattest IN  ('CHILD_TEST_AGE_<_72_HRS', 'CHILD_TEST_AGE_>72_HRS_-_<_2_MONTHS', '2ND_PCR_CHILD_TEST_AGE_2-12_MONTHS', 'CHILD_TEST_AGE_>12_MONTHS') AND results = 'INFANT_PCR_RESULT_NEGATIVE') AS totalnegatives\n" +
            "FROM infantPcr\n" +
            "),\n" +
            "pcr_sample_summary AS (\n" +
            "    SELECT\n" +
            "        'PCR_SAMPLE' AS section,\n" +
            "        'Samples' AS rowLabel,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM pcr_summary_wide w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            (1, 'sampleless_72hr', w.sampleless_72hr),\n" +
            "            (2, 'sampleless_2month', w.sampleless_2month),\n" +
            "\t\t\t(3, 'sampleless_12month', w.sampleless_12month),\n" +
            "\t\t\t(4, 'samplegreater_12month', w.samplegreater_12month),\n" +
            "            (5, 'totalsamples', w.totalsamples)\n" +
            "    ) kv(ord,columnKey,value)\n" +
            "),\n" +
            "pcr_summary AS (\n" +
            "SELECT\n" +
            "    'DNA_PCR' AS section,\n" +
            "    v.rowLabel,\n" +
            "    kv.columnKey,\n" +
            "    kv.value\n" +
            "FROM pcr_summary_wide w\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        ('Positive', w.posresultless_72hr, w.posresultless_2month, w.posresultless_12month, w.posresultgreater_12month, w.totalpositives),\n" +
            "        ('Negative', w.negresultless_72hr, w.negresultless_2month, w.negresultless_12month, w.negresultgreater_12month, w.totalnegatives)\n" +
            ") v (rowLabel, less72hr, less2month, less12month, greater12month, total)\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        (1, 'less72hr', v.less72hr),\n" +
            "        (2, 'less2month', v.less2month),\n" +
            "        (3, 'less12month', v.less12month),\n" +
            "        (4, 'greater12month', v.greater12month),\n" +
            "        (5, 'total', v.total)\n" +
            ") kv(ord, columnKey, value)\n" +
            "),\n" +
            "viralogy_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND time_of_viral_load = 'Other Time' AND agegroup = '< 15') AS supresless32week_less15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND time_of_viral_load = 'Other Time'  AND agegroup = '>= 15') AS supresless32week_greater15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND time_of_viral_load = 'Other Time' AND agegroup = '< 15') AS unsupresless32week_less15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND time_of_viral_load = 'Other Time' AND agegroup = '>= 15') AS unsupresless32week_greater15,\n" +
            "\n" +
            "\t    COUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND time_of_viral_load = 'Between 32 and 36' AND agegroup = '< 15') AS supres32_36week_less15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND time_of_viral_load = 'Between 32 and 36'  AND agegroup = '>= 15') AS supres32_36week_greater15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND time_of_viral_load = 'Between 32 and 36' AND agegroup = '< 15') AS unsupres32_36week_less15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND time_of_viral_load = 'Between 32 and 36' AND agegroup = '>= 15') AS unsupres32_36week_greater15,\n" +
            "\n" +
            "\t    COUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND visit_status = 'VISIT_STATUS_PMTCT_STILL_BREASTFEEDING' AND agegroup = '< 15') AS supres_bresfed_less15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND visit_status = 'VISIT_STATUS_PMTCT_STILL_BREASTFEEDING'  AND agegroup = '>= 15') AS supresbresfed_greater15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND visit_status = 'VISIT_STATUS_PMTCT_STILL_BREASTFEEDING' AND agegroup = '< 15') AS unsupresbresfed_less15,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND visit_status = 'VISIT_STATUS_PMTCT_STILL_BREASTFEEDING' AND agegroup = '>= 15') AS unsupresbresfed_greater15,\n" +
            "\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND time_of_viral_load = 'Other Time') AS supresless32week_total,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND time_of_viral_load = 'Other Time') AS unsupresless32week_total,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND time_of_viral_load = 'Between 32 and 36') AS supres32_36week_total,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND time_of_viral_load = 'Between 32 and 36') AS unsupres32_36week_total,\n" +
            "\t    COUNT(*) FILTER (WHERE result_of_viral_load < 1000 AND visit_status = 'VISIT_STATUS_PMTCT_STILL_BREASTFEEDING') AS supres_bresfed_total,\n" +
            "\t\tCOUNT(*) FILTER (WHERE result_of_viral_load > 999 AND visit_status = 'VISIT_STATUS_PMTCT_STILL_BREASTFEEDING') AS unsupresbresfed_total\n" +
            "FROM pmtctMotherVisit\n" +
            "),\n" +
            "viralogy_summary AS (\n" +
            "SELECT\n" +
            "    'VIRALOGIC' AS section,\n" +
            "    v.stage,\n" +
            "    v.rowLabel,\n" +
            "    kv.columnKey,\n" +
            "    kv.value\n" +
            "FROM viralogy_summary_wide w\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "    ('Pregnancy <32 weeks', 'Suppressed - Pregnancy <32 weeks', w.supresless32week_less15, w.supresless32week_greater15, w.supresless32week_total),\n" +
            "    ('Pregnancy <32 weeks', 'Unsuppressed - Pregnancy <32 weeks', w.unsupresless32week_less15, w.unsupresless32week_greater15, w.unsupresless32week_total),\n" +
            "    ('Pregnancy (32-36 weeks)', 'Suppressed - Pregnancy (32-36 weeks)', w.supres32_36week_less15, w.supres32_36week_greater15, w.supres32_36week_total),\n" +
            "    ('Pregnancy (32-36 weeks)', 'Unsuppressed - Pregnancy (32-36 weeks)', w.unsupres32_36week_less15, w.unsupres32_36week_greater15,w.unsupres32_36week_total),\n" +
            "\t('Breastfeeding', 'Suppressed - Breastfeeding', w.supres_bresfed_less15, w.supresbresfed_greater15, w.supres_bresfed_total),\n" +
            "    ('Breastfeeding', 'Unsuppressed - Breastfeeding', w.unsupresbresfed_less15, w.unsupresbresfed_greater15, w.unsupresbresfed_total)\n" +
            ") v(stage, rowLabel, less15, greater15, total)\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        (1, 'less15', v.less15),\n" +
            "        (2, 'greater15', v.greater15),\n" +
            "        (3, 'total', v.total)\n" +
            ") kv(ord, columnKey, value)\n" +
            "),\n" +
            "infant_outcome_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE infant_outcome_at18_months = 'INFANT_OUTCOME_AT_18_MONTHS_HIV_POSITIVE')  AS posoutcome,\n" +
            "        COUNT(*) FILTER (WHERE infant_outcome_at18_months = 'INFANT_OUTCOME_AT_18_MONTHS_HIV_NEGATIVE') AS negoutcome,\n" +
            "        COUNT(*) FILTER (WHERE infant_outcome_at18_months ILIKE '%INFANT_OUTCOME_AT_18_MONTHS_HIV_STATUS_UNKNOWN%')  AS unknownoutcome,\n" +
            "        COUNT(*) AS totaloutcome\n" +
            "    FROM infantVisit\n" +
            "),\n" +
            "\n" +
            "infant_outcome_summary AS (\n" +
            "\n" +
            "SELECT\n" +
            "    'INFANT_OUTCOME' AS section,\n" +
            "    'infant outcome' AS rowLabel,\n" +
            "\tv.columnKey,\n" +
            "    v.value\n" +
            "\n" +
            "FROM infant_outcome_summary_wide w\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "        (1, 'positive', w.posoutcome),\n" +
            "        (2, 'negative', w.negoutcome),\n" +
            "        (3, 'unknown',  w.unknownoutcome),\n" +
            "        (4, 'total',    w.totaloutcome)\n" +
            ") v(ord, columnKey, value)\n" +
            "),\n" +
            "prep_summary_wide AS (\n" +
            "    SELECT\n" +
            "        COUNT(*) FILTER (WHERE pregnancyStatus = 'PREGANACY_STATUS_PREGNANT') AS pregnant,\n" +
            "        COUNT(*) FILTER (WHERE pregnancyStatus = 'PREGANACY_STATUS_BREASTFEEDING') AS breastfeeding\n" +
            "        -- COUNT(*) FILTER (WHERE  pregnancyStatus IN ('PREGANACY_STATUS_BREASTFEEDING','PREGANACY_STATUS_PREGNANT'))AS total\n" +
            "    FROM prepIn\n" +
            "\t),\n" +
            "prep_summary AS (\n" +
            "SELECT\n" +
            "    'PREP' AS section,\n" +
            "    v.rowLabel,\n" +
            "\tv.rowLabel AS columnKey,\n" +
            "    v.value\n" +
            "FROM prep_summary_wide w\n" +
            "CROSS JOIN LATERAL (\n" +
            "    VALUES\n" +
            "    (1, 'Pregnant', w.pregnant),\n" +
            "    (2, 'Breastfeeding', w.breastfeeding)\n" +
            ") v(ord, rowLabel, value)\n" +
            ")\n" +
            "SELECT section, rowLabel, columnKey, value\n" +
            "FROM (\n" +
            "    SELECT 1 AS sort_order, section, rowLabel, columnKey, value FROM anc_summary\n" +
            "    UNION ALL\n" +
            "    SELECT sort_order, section, rowLabel, columnKey, value FROM hts_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM acute_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM retest_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM prep_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM hivtiming_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM syphilis_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM spoke_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM hbv_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM booked_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM exposedinfant_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM pcr_sample_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM pcr_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM viralogy_summary\n" +
            "\tUNION ALL\n" +
            "\tSELECT 0 AS sort_order, section, rowLabel, columnKey, value FROM infant_outcome_summary\n" +
            "\t\n" +
            ") final\n" +
            "ORDER BY\n" +
            "    CASE section\n" +
            "WHEN 'ANC' THEN 1\n" +
            "WHEN 'HIV' THEN 2\n" +
            "WHEN 'SYPHILIS' THEN 3\n" +
            "WHEN 'HEPATITIS_B' THEN 4\n" +
            "WHEN 'ACUTE' THEN 5\n" +
            "WHEN 'RETEST' THEN 6\n" +
            "WHEN 'PREP' THEN 7\n" +
            "WHEN 'ART_TIMING' THEN 8\n" +
            "WHEN 'Syphilis' THEN 9\n" +
            "WHEN 'HBV' THEN 10\n" +
            "WHEN 'DELIVERY' THEN 11\n" +
            "WHEN 'EXPOSED_INFANT' THEN 12\n" +
            "WHEN 'PCR SAMPLE' THEN 13\n" +
            "WHEN 'DNA_PCR' THEN 14\n" +
            "WHEN 'VIRALOGIC' THEN 15\n" +
            "WHEN 'INFANT_OUTCOME' THEN 16\n" +
            "    END,\n" +
            "    sort_order";
}
