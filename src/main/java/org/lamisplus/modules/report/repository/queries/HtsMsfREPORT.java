package org.lamisplus.modules.report.repository.queries;

public class HtsMsfREPORT {

    public static final String HTS_MONTHLY_MSF_QUERY = "WITH base AS (\n" +
            "    SELECT\n" +
            "        pp.date_of_birth,\n" +
            "        INITCAP(pp.sex)                          AS sex,\n" +
            "        h.setting,\n" +
            "        h.observation->>'recencyTest'            AS recencyTest,\n" +
            "        h.observation->>'facilitySetting'        AS facilitySetting,\n" +
            "        h.observation->>'finalHivTestResult'     AS finalHivTestResult,\n" +
            "        h.observation->>'pregnancyStatus'        AS pregnancyStatus,\n" +
            "        h.observation->>'keyPopulationType'      AS keyPopulationType\n" +
            "    FROM hts_encounter h\n" +
            "    INNER JOIN patient_person pp\n" +
            "        ON pp.uuid = h.patient_uuid\n" +
            "       AND pp.archived = 0\n" +
            "    WHERE h.archived = FALSE AND h.facility_id = ?1 \n" +
            "      AND h.patient_uuid IS NOT NULL\n" +
            "      AND h.observation->>'facilitySetting' IS NOT NULL\n" +
            "      AND h.date_of_visit BETWEEN ?2 AND ?3\n" +
            "      AND COALESCE(h.observation->>'finalHivTestResult','') <> ''\n" +
            "),\n" +
            "\n" +
            "s1_src AS (\n" +
            "    SELECT\n" +
            "        sex, facilitySetting, finalHivTestResult,\n" +
            "        CASE\n" +
            "            WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 1 AND 4   THEN '1 - 4'\n" +
            "            WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 5 AND 9   THEN '5 - 9'\n" +
            "            WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 10 AND 14 THEN '10 - 14'\n" +
            "            WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 15 AND 19 THEN '15 - 19'\n" +
            "            WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 20 AND 24 THEN '20 - 24'\n" +
            "            WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 25 AND 49 THEN '25 - 49'\n" +
            "            ELSE '50+'\n" +
            "        END AS ageGroup\n" +
            "    FROM base\n" +
            "    WHERE facilitySetting IS NOT NULL\n" +
            "      AND COALESCE(finalHivTestResult, '') <> ''\n" +
            "),\n" +
            "s1_wide AS (\n" +
            "    SELECT\n" +
            "        finalHivTestResult AS testResult,\n" +
            "        CASE WHEN GROUPING(ageGroup) = 1 THEN 'TOTAL' ELSE ageGroup END AS rowLabel,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Male'   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT') AS inpatientM,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Female' AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT') AS inpatientF,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Male'   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT')             AS ctM,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Female' AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT')             AS ctF,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Male')   AS totalM,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Female') AS totalF\n" +
            "    FROM s1_src\n" +
            "    GROUP BY GROUPING SETS ((finalHivTestResult, ageGroup), (finalHivTestResult))\n" +
            "),\n" +
            "section1 AS (\n" +
            "    SELECT CAST('RESULTS_BY_AGE' AS text) AS section, w.testResult, w.rowLabel, kv.columnKey, kv.value\n" +
            "    FROM s1_wide w\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        ('inpatientM', w.inpatientM),\n" +
            "        ('inpatientF', w.inpatientF),\n" +
            "        ('ctM',        w.ctM),\n" +
            "        ('ctF',        w.ctF),\n" +
            "        ('totalM',     w.totalM),\n" +
            "        ('totalF',     w.totalF)\n" +
            "    ) AS kv(columnKey, value)\n" +
            "),\n" +
            "\n" +
            "s2_wide AS (\n" +
            "    SELECT\n" +
            "        finalHivTestResult AS testResult,\n" +
            "        -- CASE WHEN GROUPING(ageGroup) = 1 THEN 'TOTAL' ELSE ageGroup END AS rowLabel,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Male'   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT') AS inpatientM,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Female' AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT') AS inpatientF,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Male'   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT')             AS ctM,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Female' AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT')             AS ctF,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Male')   AS totalM,\n" +
            "        COUNT(*) FILTER (WHERE sex = 'Female') AS totalF\n" +
            "\n" +
            "    FROM base\n" +
            "    WHERE finalHivTestResult = 'Positive'\n" +
            "      AND (UPPER(recencyTest) LIKE '%ACUTE%' OR UPPER(recencyTest) LIKE '%AHI%')\n" +
            "\t  GROUP BY GROUPING SETS ((finalHivTestResult), ())\n" +
            "),\n" +
            "section2 AS (\n" +
            "    SELECT CAST('ACUTE_HIV' AS text) AS section, CAST('Positive' AS text) AS testResult, CAST(NULL AS text) AS rowLabel,\n" +
            "           kv.columnKey, kv.value\n" +
            "    FROM s2_wide w\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        ('inpatientM', w.inpatientM),\n" +
            "        ('inpatientF', w.inpatientF),\n" +
            "        ('ctM',        w.ctM),\n" +
            "        ('ctF',        w.ctF),\n" +
            "        ('totalM',     w.totalM),\n" +
            "        ('totalF',     w.totalF)\n" +
            "    ) AS kv(columnKey, value)\n" +
            "),\n" +
            "\n" +
            "s3_src AS (\n" +
            "    SELECT\n" +
            "        sex, setting, pregnancyStatus, facilitySetting,\n" +
            "        CASE\n" +
            "            WHEN UPPER(recencyTest) LIKE '%RECENT%' THEN 'Recent'\n" +
            "            WHEN UPPER(recencyTest) LIKE '%LONG%'   THEN 'Long-term'\n" +
            "            ELSE NULL\n" +
            "        END AS recency_group\n" +
            "    FROM base\n" +
            "    WHERE COALESCE(finalHivTestResult, '') <> ''\n" +
            "      AND finalHivTestResult = 'Positive'\n" +
            "      AND recencyTest IS NOT NULL\n" +
            "),\n" +
            "s3_wide AS (\n" +
            "    SELECT\n" +
            "        CASE WHEN GROUPING(recency_group) = 1 THEN 'TOTAL' ELSE recency_group END AS rowLabel,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'HTS_ENTRY_POINT_FACILITY' AND sex = 'Male'   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT'), 0) AS inpatientM,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'HTS_ENTRY_POINT_FACILITY' AND sex = 'Female' AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT'), 0) AS inpatientF,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'HTS_ENTRY_POINT_FACILITY' AND sex = 'Male'   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT'), 0) AS ctM,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'HTS_ENTRY_POINT_FACILITY' AND sex = 'Female' AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_CT'), 0) AS ct_f,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'Out-patient' AND sex = 'Male'),   0) AS outpatient_m,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'Out-patient' AND sex = 'Female'), 0) AS outpatient_f,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'Others' AND sex = 'Male'),   0) AS others_m,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'Others' AND sex = 'Female'), 0) AS others_f,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' AND sex = 'Female'), 0) AS pregnant_women,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'Community Others' AND sex = 'Male'),   0) AS community_m,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE setting = 'Community Others' AND sex = 'Female'), 0) AS community_f,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE sex = 'Male'),   0) AS totalM,\n" +
            "        COALESCE(COUNT(*) FILTER (WHERE sex = 'Female'), 0) AS totalF\n" +
            "    FROM s3_src\n" +
            "    GROUP BY GROUPING SETS ((recency_group), ())\n" +
            "),\n" +
            "section3 AS (\n" +
            "    SELECT CAST('RECENCY' AS text) AS section, CAST(NULL AS text) AS testResult, w.rowLabel, kv.columnKey, kv.value\n" +
            "    FROM s3_wide w\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        ('inpatientM',     w.inpatientM),\n" +
            "        ('inpatientF',     w.inpatientF),\n" +
            "        ('ctM',            w.ctM),\n" +
            "        ('ct_f',            w.ct_f),\n" +
            "        ('outpatient_m',    w.outpatient_m),\n" +
            "        ('outpatient_f',    w.outpatient_f),\n" +
            "        ('others_m',        w.others_m),\n" +
            "        ('others_f',        w.others_f),\n" +
            "        ('pregnant_women',  w.pregnant_women),\n" +
            "        ('community_m',     w.community_m),\n" +
            "        ('community_f',     w.community_f),\n" +
            "        ('totalM',         w.totalM),\n" +
            "        ('totalF',         w.totalF)\n" +
            "    ) AS kv(columnKey, value)\n" +
            "),\n" +
            "\n" +
            "s4_wide AS (\n" +
            "    SELECT\n" +
            "        finalHivTestResult AS testResult,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'MSM'        AND sex = 'Male')   AS msm_m,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'MSM'        AND sex = 'Female') AS msm_f,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'PWID'       AND sex = 'Male')   AS pwid_m,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'PWID'       AND sex = 'Female') AS pwid_f,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'SEX_WORKER' AND sex = 'Male')   AS sex_worker_m,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'SEX_WORKER' AND sex = 'Female') AS sex_worker_f,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'PPOCS'      AND sex = 'Male')   AS ppocs_m,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'PPOCS'      AND sex = 'Female') AS ppocs_f,\n" +
            "        COUNT(*) FILTER (WHERE keyPopulationType = 'AGYW') AS agyw,\n" +
            "        COUNT(*) AS total\n" +
            "    FROM base\n" +
            "    WHERE keyPopulationType IS NOT NULL\n" +
            "    GROUP BY finalHivTestResult\n" +
            "),\n" +
            "section4 AS (\n" +
            "    SELECT CAST('KEY_POPULATION' AS text) AS section, w.testResult, CAST(NULL AS text) AS rowLabel, kv.columnKey, kv.value\n" +
            "    FROM s4_wide w\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        ('msm_m',        w.msm_m),\n" +
            "        ('msm_f',        w.msm_f),\n" +
            "        ('pwid_m',       w.pwid_m),\n" +
            "        ('pwid_f',       w.pwid_f),\n" +
            "        ('sex_worker_m', w.sex_worker_m),\n" +
            "        ('sex_worker_f', w.sex_worker_f),\n" +
            "        ('ppocs_m',      w.ppocs_m),\n" +
            "        ('ppocs_f',      w.ppocs_f),\n" +
            "        ('agyw',         w.agyw),\n" +
            "        ('total',        w.total)\n" +
            "    ) AS kv(columnKey, value)\n" +
            "),\n" +
            "combined AS (\n" +
            "    SELECT * FROM section1\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM section2\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM section3\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM section4\n" +
            ")\n" +
            "\n" +
            "SELECT *\n" +
            "FROM combined\n" +
            "ORDER BY\n" +
            "    CASE section\n" +
            "        WHEN 'RESULTS_BY_AGE'   THEN 1\n" +
            "        WHEN 'ACUTE_HIV'        THEN 2\n" +
            "        WHEN 'RECENCY'          THEN 3\n" +
            "        WHEN 'KEY_POPULATION'   THEN 4\n" +
            "    END,\n" +
            "    CASE testResult\n" +
            "        WHEN 'Negative' THEN 1\n" +
            "        WHEN 'Positive' THEN 2\n" +
            "        ELSE 3\n" +
            "    END,\n" +
            "    CASE rowLabel\n" +
            "        WHEN '1 - 4'      THEN 1\n" +
            "        WHEN '5 - 9'      THEN 2\n" +
            "        WHEN '10 - 14'    THEN 3\n" +
            "        WHEN '15 - 19'    THEN 4\n" +
            "        WHEN '20 - 24'    THEN 5\n" +
            "        WHEN '25 - 49'    THEN 6\n" +
            "        WHEN '50+'        THEN 7\n" +
            "        WHEN 'Recent'     THEN 1\n" +
            "        WHEN 'Long-term'  THEN 2\n" +
            "        WHEN 'TOTAL'      THEN 99\n" +
            "        ELSE 50\n" +
            "    END;\n";
}
