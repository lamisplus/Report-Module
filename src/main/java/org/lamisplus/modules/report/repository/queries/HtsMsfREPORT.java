package org.lamisplus.modules.report.repository.queries;

public class HtsMsfREPORT {

    public static final String HTS_MONTHLY_MSF_QUERY = "WITH base AS (\n" +
            " SELECT\n" +
            "  pp.date_of_birth,\n" +
            "  INITCAP(pp.sex) AS sex, h.observation->>'typeOfSession' typeOfSession,\n" +
            "  h.setting, h.observation->>'indexTesting' AS indexTesting,\n" +
            "  h.observation->>'recencyTest'   AS recencyTest,\n" +
            "  h.observation->>'facilitySetting'  AS facilitySetting,\n" +
            "  h.observation->>'finalHivTestResult'  AS finalHivTestResult,\n" +
            "  h.observation->>'pregnancyStatus'  AS pregnancyStatus,\n" +
            "  h.observation->>'keyPopulationType'   AS keyPopulationType\n" +
            " FROM hts_encounter h\n" +
            " INNER JOIN patient_person pp\n" +
            "  ON pp.uuid = h.patient_uuid\n" +
            "    AND pp.archived = 0\n" +
            " WHERE h.archived = FALSE AND h.facility_id =  ?1 \n" +
            "   AND h.patient_uuid IS NOT NULL\n" +
            "   AND h.observation->>'facilitySetting' IS NOT NULL\n" +
            "   AND h.date_of_visit BETWEEN ?2 AND ?3\n" +
            "   AND COALESCE(h.observation->>'finalHivTestResult','') <> ''\n" +
            "),\n" +
            "indexClientPositive AS (\n" +
            "SELECT relationship_to_index, hiv_test_result, known_hiv_positive, sex\n" +
            "FROM hts_ict_contact\n" +
            "where date_tested_hiv BETWEEN ?2 AND ?3 AND archived = FALSE --AND facility_id = 1925 \n" +
            "),\n" +
            "hivstCte AS (\n" +
            "SELECT hst.patient_uuid, p.sex, hst.date_of_visit, COALESCE(NULLIF(hst.observation->>'facilitySetting',''), hst.observation->>'communityEntryPoint') entryPoint, hst.setting,\n" +
            "hst.observation->>'categoryOfClients' categoryOfClients, hstR.reactive_gt_15, hstR.reactive_le_15\n" +
            "FROM hivst_encounter hst\n" +
            "LEFT JOIN hivst_result hstR ON hstR.encounter_id = hst.id\n" +
            "LEFT JOIN patient_person p ON p.uuid = hst.patient_uuid AND p.archived = 0\n" +
            "WHERE hst.archived IS FALSE AND hst.facility_id =  ?1 AND hst.date_of_visit BETWEEN ?2 AND ?3\n" +
            "),\n" +
            "hivst_groups AS (\n" +
            "    SELECT * FROM (\n" +
            "        VALUES\n" +
            "            (1, 'HTS_ENTRY_POINT_FACILITY'),\n" +
            "            (2, 'HTS_ENTRY_POINT_COMMUNITY'),\n" +
            "            (3, 'PRIVATE')\n" +
            "    ) AS t(sort_order, hivst_group)\n" +
            "),\n" +
            "suspectedAcute AS (\n" +
            " SELECT * FROM (VALUES ('Suspected Acute Infection')) AS t(acute)\n" +
            "),\n" +
            "index_groups AS (\n" +
            " SELECT * FROM (VALUES ('HIV_TEST_RESULT_POSITIVE'), ('HIV_TEST_RESULT_NEGATIVE')) AS t(index_group)\n" +
            "),\n" +
            "age_groups AS (\n" +
            " SELECT * FROM (VALUES\n" +
            "  ('1 - 4'), ('5 - 9'), ('10 - 14'), ('15 - 19'),\n" +
            "  ('20 - 24'), ('25 - 49'), ('50+'), ('TOTAL')\n" +
            " ) AS t(ageGroup)\n" +
            "),\n" +
            "test_results AS (SELECT * FROM (VALUES ('Negative'), ('Positive') ) t(testResult)\n" +
            "),\n" +
            "age_result_groups AS (SELECT tr.testResult, ag.ageGroup FROM test_results tr CROSS JOIN age_groups ag\n" +
            "),\n" +
            "recency_groups AS (\n" +
            " SELECT * FROM (VALUES ('Recent'), ('Long-term')) AS t(recency_group)\n" +
            "),\n" +
            "s1_src AS (\n" +
            " SELECT\n" +
            "  sex, facilitySetting, finalHivTestResult, pregnancyStatus, recencyTest,indexTesting, typeOfSession,\n" +
            "  CASE\n" +
            "   WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 1 AND 4   THEN '1 - 4'\n" +
            "   WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 5 AND 9   THEN '5 - 9'\n" +
            "   WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 10 AND 14 THEN '10 - 14'\n" +
            "   WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 15 AND 19 THEN '15 - 19'\n" +
            "   WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 20 AND 24 THEN '20 - 24'\n" +
            "   WHEN DATE_PART('year', AGE(date_of_birth)) BETWEEN 25 AND 49 THEN '25 - 49'\n" +
            "   ELSE '50+'\n" +
            "  END AS ageGroup,\n" +
            "  CASE\n" +
            "   WHEN DATE_PART('year', AGE(date_of_birth)) < 15   THEN 'less15'\n" +
            "   ELSE '15+'\n" +
            "  END AS ageBy15\n" +
            " FROM base\n" +
            " WHERE facilitySetting IS NOT NULL\n" +
            "   AND COALESCE(finalHivTestResult, '') <> ''\n" +
            "),\n" +
            "s1_wide AS (\n" +
            " SELECT\n" +
            "  finalHivTestResult AS testResult, ageGroup, ageBy15, typeOfSession,\n" +
            "  CASE\n" +
            "   WHEN UPPER(recencyTest) LIKE '%RECENT%' THEN 'Recent'\n" +
            "   WHEN UPPER(recencyTest) LIKE '%LONG%'   THEN 'Long-term'\n" +
            "   ELSE NULL\n" +
            "  END AS recency_group,\n" +
            "  CASE WHEN GROUPING(ageGroup) = 1 THEN 'TOTAL' ELSE ageGroup END AS rowLabel,\n" +
            "  COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Male'\n" +
            "   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT'\n" +
            "), 0) AS inpatientM,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Female'\n" +
            "   AND facilitySetting = 'FACILITY_HTS_TEST_SETTING_WARD_INPATIENT'\n" +
            "), 0) AS inpatientF,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Male'\n" +
            "   AND facilitySetting IN (\n" +
            "    'FACILITY_HTS_TEST_SETTING_CT',\n" +
            "    'PEPFAR_HTS_SETTINGS_VCT',\n" +
            "    'FACILITY_HTS_TEST_SETTING_PREP_TESTING'\n" +
            "   )\n" +
            "), 0) AS ctM,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Female'\n" +
            "   AND facilitySetting IN (\n" +
            "    'FACILITY_HTS_TEST_SETTING_CT',\n" +
            "    'PEPFAR_HTS_SETTINGS_VCT',\n" +
            "    'FACILITY_HTS_TEST_SETTING_PREP_TESTING'\n" +
            "   )\n" +
            "), 0) AS ctF,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Male'\n" +
            "   AND facilitySetting IN (\n" +
            "    'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS',\n" +
            "    'FACILITY_HTS_TEST_SETTING_TB',\n" +
            "    'FACILITY_HTS_TEST_SETTING_STI',\n" +
            "    'FACILITY_HTS_TEST_SETTING_PEDIATRIC',\n" +
            "    'FACILITY_HTS_TEST_SETTING_MALNUTRITION'\n" +
            "   )\n" +
            "), 0) AS outM,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Female'\n" +
            "   AND facilitySetting IN (\n" +
            "    'FACILITY_HTS_TEST_SETTING_STANDALONE_HTS',\n" +
            "    'FACILITY_HTS_TEST_SETTING_TB',\n" +
            "    'FACILITY_HTS_TEST_SETTING_STI',\n" +
            "    'FACILITY_HTS_TEST_SETTING_PEDIATRIC',\n" +
            "    'FACILITY_HTS_TEST_SETTING_MALNUTRITION'\n" +
            "   )\n" +
            "), 0) AS outF,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Male'\n" +
            "   AND facilitySetting IN (\n" +
            "    'FACILITY_HTS_TEST_SETTING_BLOOD_BANK',\n" +
            "    'FACILITY_HTS_TEST_SETTING_EMERGENCY',\n" +
            "    'FACILITY_HTS_TEST_SETTING_FP',\n" +
            "    'FACILITY_HTS_TEST_SETTING_INDEX',\n" +
            "    'FACILITY_HTS_TEST_SETTING_OTHERS',\n" +
            "    'FACILITY_HTS_TEST_SETTING_SNS'\n" +
            "   )\n" +
            "), 0) AS othersM,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Female'\n" +
            "   AND facilitySetting IN (\n" +
            "    'FACILITY_HTS_TEST_SETTING_BLOOD_BANK',\n" +
            "    'FACILITY_HTS_TEST_SETTING_EMERGENCY',\n" +
            "    'FACILITY_HTS_TEST_SETTING_FP',\n" +
            "    'FACILITY_HTS_TEST_SETTING_INDEX',\n" +
            "    'FACILITY_HTS_TEST_SETTING_OTHERS',\n" +
            "    'FACILITY_HTS_TEST_SETTING_SNS'\n" +
            "   )\n" +
            "), 0) AS othersF,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Female'\n" +
            "   AND pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%'\n" +
            "   AND facilitySetting IN (\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_CONGREGATIONAL_SETTING',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_DELIVERY_HOMES',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_TBA_ORTHODOX',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_TBA_RT'\n" +
            "   )\n" +
            "), 0) AS pregnantF,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Male'\n" +
            "   AND facilitySetting IN (\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_OTHERS',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_SNS',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_OVC',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_OUTREACH',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_CT',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_INDEX'\n" +
            "   )\n" +
            "), 0) AS commM,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (\n" +
            " WHERE sex = 'Female'\n" +
            "   AND facilitySetting IN (\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_OTHERS',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_SNS',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_OVC',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_OUTREACH',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_CT',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_STANDALONE_HTS',\n" +
            "    'COMMUNITY_HTS_TEST_SETTING_INDEX'\n" +
            "   )\n" +
            "), 0) AS commF,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (WHERE sex = 'Male'), 0) AS totalM,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE sex = 'Female'), 0) AS totalF\n" +
            " FROM s1_src\n" +
            " GROUP BY GROUPING SETS ((finalHivTestResult, ageGroup), (finalHivTestResult, recencyTest, ageBy15, typeOfSession))\n" +
            "),\n" +
            "section1 AS (\n" +
            " SELECT 'RESULTS_BY_AGE' AS section, arg.testResult,  COALESCE(w.rowLabel, arg.ageGroup) AS rowLabel, kv.columnKey, kv.value\n" +
            " FROM age_result_groups arg\n" +
            " LEFT JOIN s1_wide w ON w.testResult = arg.testResult AND w.rowLabel = arg.ageGroup\n" +
            " CROSS JOIN LATERAL (\n" +
            "  VALUES\n" +
            "   ('inpatientM', COALESCE(w.inpatientM, 0)),\n" +
            "   ('inpatientF', COALESCE(w.inpatientF, 0)),\n" +
            "   ('ctM',  COALESCE(w.ctM, 0)),\n" +
            "   ('ctF',  COALESCE(w.ctF, 0)),\n" +
            "   ('outM',    COALESCE(w.outM, 0)),\n" +
            "   ('outF',    COALESCE(w.outF, 0)),\n" +
            "   ('othersM', COALESCE(w.othersM, 0)),\n" +
            "   ('othersF', COALESCE(w.othersF, 0)),\n" +
            "   ('pregnantF',  COALESCE(w.pregnantF, 0)),\n" +
            "   ('commM',   COALESCE(w.commM, 0)),\n" +
            "   ('commF',   COALESCE(w.commF, 0)),\n" +
            "   ('totalM',  COALESCE(w.totalM, 0)),\n" +
            "   ('totalF',  COALESCE(w.totalF, 0))\n" +
            " ) kv(columnKey, value)\n" +
            "),\n" +
            "section2 AS (\n" +
            "SELECT CAST('ACUTE_HIV' AS text) AS section, ac.acute AS testResult, 'TOTAL' AS rowLabel, kv.columnKey, kv.value\n" +
            " FROM suspectedAcute ac\n" +
            "LEFT JOIN s1_wide w ON w.testResult = ac.acute AND w.rowLabel = 'TOTAL'\n" +
            " CROSS JOIN LATERAL (VALUES\n" +
            "('inpatientM', COALESCE(w.inpatientM, 0)),\n" +
            "  ('inpatientF', COALESCE(w.inpatientF, 0)),\n" +
            "  ('ctM',  COALESCE(w.ctM, 0)),\n" +
            "  ('ctF',  COALESCE(w.ctF, 0)),\n" +
            "('outM', COALESCE(w.outM, 0)),\n" +
            "('outF', COALESCE(w.outF, 0)),\n" +
            "('othersM', COALESCE(w.othersM, 0)),\n" +
            "('othersF', COALESCE(w.othersF, 0)),\n" +
            "('pregnantF', COALESCE(w.pregnantF, 0)),\n" +
            "('commM', COALESCE(w.commM, 0)),\n" +
            "('commF', COALESCE(w.commF, 0)),\n" +
            "  ('totalM',  COALESCE(w.totalM, 0)),\n" +
            "  ('totalF',  COALESCE(w.totalF, 0))\n" +
            " ) AS kv(columnKey, value)\n" +
            "),\n" +
            "section3 AS (\n" +
            " SELECT CAST('RECENCY_RESULT' AS text) AS section, CAST(rg.recency_group AS text) AS testResult, CAST(rg.recency_group AS text) AS rowLabel, kv.columnKey, kv.value\n" +
            " FROM recency_groups rg\n" +
            "LEFT JOIN s1_wide w ON rg.recency_group = w.recency_group\n" +
            " CROSS JOIN LATERAL (VALUES\n" +
            "  ('inpatientM', COALESCE(w.inpatientM, 0)),\n" +
            "  ('inpatientF', COALESCE(w.inpatientF, 0)),\n" +
            "  ('ctM',  COALESCE(w.ctM, 0)),\n" +
            "  ('ctF',  COALESCE(w.ctF, 0)),\n" +
            "('outM', COALESCE(w.outM, 0)),\n" +
            "('outF', COALESCE(w.outF, 0)),\n" +
            "('othersM', COALESCE(w.othersM, 0)),\n" +
            "('othersF', COALESCE(w.othersF, 0)),\n" +
            "('pregnantF', COALESCE(w.pregnantF, 0)),\n" +
            "('commM', COALESCE(w.commM, 0)),\n" +
            "('commF', COALESCE(w.commF, 0)),\n" +
            "  ('totalM',  COALESCE(w.totalM, 0)),\n" +
            "  ('totalF',  COALESCE(w.totalF, 0))\n" +
            " ) AS kv(columnKey, value)\n" +
            "),\n" +
            "section4 AS (\n" +
            " SELECT 'INDEX_POSITIVE' AS section, 'Positive' AS testResult, 'TOTAL' AS rowLabel, kv.columnKey, kv.value\n" +
            " FROM (\n" +
            "  SELECT\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE sex = 'Male' AND finalHivTestResult = 'Positive' AND indexTesting = 'YES_NO_YES'), 0) AS male,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE sex = 'Female' AND finalHivTestResult = 'Positive' AND indexTesting = 'YES_NO_YES'), 0) AS female\n" +
            "  FROM s1_src\n" +
            " ) w\n" +
            " CROSS JOIN LATERAL (\n" +
            "  VALUES\n" +
            "   ('male', COALESCE(w.male, 0)),\n" +
            "   ('female', COALESCE(w.female, 0))\n" +
            " ) AS kv(columnKey, value)\n" +
            "), \n" +
            "section5 AS (\n" +
            " SELECT 'INDEX_PARTNER' AS section,\n" +
            "    CASE\n" +
            "     WHEN index_group = 'HIV_TEST_RESULT_POSITIVE' THEN 'Positive'\n" +
            "     WHEN index_group = 'HIV_TEST_RESULT_NEGATIVE' THEN 'Negative'\n" +
            "    END AS testResult,\n" +
            "    CASE\n" +
            "     WHEN index_group = 'HIV_TEST_RESULT_POSITIVE' THEN 'Positive'\n" +
            "     WHEN index_group = 'HIV_TEST_RESULT_NEGATIVE' THEN 'Negative'\n" +
            "    END AS rowLabel,\n" +
            "    kv.columnKey,\n" +
            "    kv.value\n" +
            " FROM (\n" +
            "  SELECT ig.index_group,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_POSITIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_BIOLOGICAL_CHILD', 'RELATIONSHIP_CONTACT_MOTHER', 'RELATIONSHIP_CONTACT_FATHER') AND indx.sex IN ('SEX_MALE', '376')), 0) AS maleBioPos,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_POSITIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_BIOLOGICAL_CHILD', 'RELATIONSHIP_CONTACT_MOTHER', 'RELATIONSHIP_CONTACT_FATHER') AND indx.sex IN ('SEX_FEMALE', '377')), 0) AS femaleBioPos,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_NEGATIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_BIOLOGICAL_CHILD', 'RELATIONSHIP_CONTACT_MOTHER', 'RELATIONSHIP_CONTACT_FATHER') AND indx.sex IN ('SEX_MALE', '376')), 0) AS maleBioNeg,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_NEGATIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_BIOLOGICAL_CHILD', 'RELATIONSHIP_CONTACT_MOTHER', 'RELATIONSHIP_CONTACT_FATHER') AND indx.sex IN ('SEX_FEMALE', '377')), 0) AS femaleBioNeg,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_POSITIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_SPOUSE', 'RELATIONSHIP_CONTACT_LIVE-IN_PARTNERS') AND indx.sex IN ('SEX_MALE', '376')), 0) AS malePartnerPos,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_POSITIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_SPOUSE', 'RELATIONSHIP_CONTACT_LIVE-IN_PARTNERS') AND indx.sex IN ('SEX_FEMALE', '377')), 0) AS femalePartnerPos,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_NEGATIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_SPOUSE', 'RELATIONSHIP_CONTACT_LIVE-IN_PARTNERS') AND indx.sex IN ('SEX_MALE', '376')), 0) AS malePartnerNeg,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_NEGATIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_SPOUSE', 'RELATIONSHIP_CONTACT_LIVE-IN_PARTNERS') AND indx.sex IN ('SEX_FEMALE', '377')), 0) AS femalePartnerNeg,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_POSITIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_INFREQUENT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIENDGIRLFRIEND<OPTION>', 'RELATIONSHIP_CONTACT_REGULAR_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIEND_GIRLFRIEND', 'RELATIONSHIP_CONTACT_SOCIAL_NETWORK') AND indx.sex IN ('SEX_MALE', '376')), 0) AS maleSocialPos,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_POSITIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_INFREQUENT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIENDGIRLFRIEND<OPTION>', 'RELATIONSHIP_CONTACT_REGULAR_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIEND_GIRLFRIEND', 'RELATIONSHIP_CONTACT_SOCIAL_NETWORK') AND indx.sex IN ('SEX_FEMALE', '377')), 0) AS femaleSocialPos,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_NEGATIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_INFREQUENT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIENDGIRLFRIEND<OPTION>', 'RELATIONSHIP_CONTACT_REGULAR_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIEND_GIRLFRIEND', 'RELATIONSHIP_CONTACT_SOCIAL_NETWORK') AND indx.sex IN ('SEX_MALE', '376')), 0) AS maleSocialNeg,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_NEGATIVE' AND indx.relationship_to_index IN ('RELATIONSHIP_CONTACT_INFREQUENT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIENDGIRLFRIEND<OPTION>', 'RELATIONSHIP_CONTACT_REGULAR_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_CASUAL_PARTNER', 'RELATIONSHIP_CONTACT_BOYFRIEND_GIRLFRIEND', 'RELATIONSHIP_CONTACT_SOCIAL_NETWORK') AND indx.sex IN ('SEX_FEMALE', '377')), 0) AS femaleSocialNeg,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_POSITIVE'), 0) AS totalPos,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE indx.hiv_test_result = 'HIV_TEST_RESULT_NEGATIVE'), 0) AS totalNeg\n" +
            "  FROM index_groups ig\n" +
            "LEFT JOIN indexClientPositive indx ON ig.index_group = indx.hiv_test_result\n" +
            "GROUP BY ig.index_group\n" +
            " ) w\n" +
            " CROSS JOIN LATERAL (\n" +
            " VALUES\n" +
            "(\n" +
            " 'maleBio',\n" +
            " CASE\n" +
            "  WHEN w.index_group = 'HIV_TEST_RESULT_POSITIVE'\n" +
            "  THEN w.maleBioPos\n" +
            "  ELSE w.maleBioNeg\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'femaleBio',\n" +
            " CASE\n" +
            "  WHEN w.index_group = 'HIV_TEST_RESULT_POSITIVE'\n" +
            "  THEN w.femaleBioPos\n" +
            "  ELSE w.femaleBioNeg\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'malePartner',\n" +
            " CASE\n" +
            "  WHEN w.index_group = 'HIV_TEST_RESULT_POSITIVE'\n" +
            "  THEN w.malePartnerPos\n" +
            "  ELSE w.malePartnerNeg\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'femalePartner',\n" +
            " CASE\n" +
            "  WHEN w.index_group = 'HIV_TEST_RESULT_POSITIVE'\n" +
            "  THEN w.femalePartnerPos\n" +
            "  ELSE w.femalePartnerNeg\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'maleSocial',\n" +
            " CASE\n" +
            "  WHEN w.index_group = 'HIV_TEST_RESULT_POSITIVE'\n" +
            "  THEN w.maleSocialPos\n" +
            "  ELSE w.maleSocialNeg\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'femaleSocial',\n" +
            " CASE\n" +
            "  WHEN w.index_group = 'HIV_TEST_RESULT_POSITIVE'\n" +
            "  THEN w.femaleSocialPos\n" +
            "  ELSE w.femaleSocialNeg\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'totalIndex',\n" +
            " CASE\n" +
            "  WHEN w.index_group = 'HIV_TEST_RESULT_POSITIVE'\n" +
            "  THEN w.totalPos\n" +
            "  ELSE w.totalNeg\n" +
            " END\n" +
            ")\n" +
            ") kv(columnKey, value)\n" +
            "), \n" +
            "section6 AS (\n" +
            " SELECT 'HIVST' AS section,\n" +
            "    CASE\n" +
            "     WHEN hivst_group = 'HTS_ENTRY_POINT_COMMUNITY' THEN 'Community'\n" +
            "     WHEN hivst_group = 'HTS_ENTRY_POINT_FACILITY' THEN 'Facility'\n" +
            "   WHEN hivst_group = 'PRIVATE' THEN 'Private Sector'\n" +
            "    END AS testResult,\n" +
            "    CASE\n" +
            "     WHEN hivst_group = 'HTS_ENTRY_POINT_COMMUNITY' THEN 'Community'\n" +
            "     WHEN hivst_group = 'HTS_ENTRY_POINT_FACILITY' THEN 'Facility'\n" +
            "   WHEN hivst_group = 'PRIVATE' THEN 'Private Sector'\n" +
            "    END AS rowLabel,\n" +
            "    kv.columnKey,\n" +
            "    kv.value\n" +
            " FROM (\n" +
            "  SELECT ig.hivst_group,\n" +
            "   COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SELF' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Male' ), 0) AS maleFacSelf,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SELF' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Female' ), 0) AS femaleFacSelf,\n" +
            " COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SELF' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Male' ), 0) AS maleComSelf,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SELF' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Female' ), 0) AS femaleComSelf,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SELF' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Male' ), 0) AS malePrivateSelf,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SELF' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Female' ), 0) AS femalePrivateSelf,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_PARTNER' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Male' ), 0) AS maleFacPartner,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_PARTNER' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Female' ), 0) AS femaleFacSPartner,\n" +
            " COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_PARTNER' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Male' ), 0) AS maleComPartner,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_PARTNER' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Female' ), 0) AS femaleComPartner,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_PARTNER' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Male' ), 0) AS malePrivatePartener,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_PARTNER' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Female' ), 0) AS femalePrivatePartner,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_CAREGIVER_ASSISTED' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Male' ), 0) AS maleFacAssist,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_CAREGIVER_ASSISTED' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Female' ), 0) AS femaleFacAssist,\n" +
            " COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_CAREGIVER_ASSISTED' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Male' ), 0) AS maleComAssist,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_CAREGIVER_ASSISTED' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Female' ), 0) AS femaleComAssist,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_CAREGIVER_ASSISTED' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Male' ), 0) AS malePrivateAssist,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_CAREGIVER_ASSISTED' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Female' ), 0) AS femalePrivateAssist,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SOCIAL_NETWORK' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Male' ), 0) AS maleFacNetwork,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SOCIAL_NETWORK' AND hivst.setting = 'HTS_ENTRY_POINT_FACILITY' AND hivst.sex = 'Female' ), 0) AS femaleFacNetwork,\n" +
            " COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SOCIAL_NETWORK' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Male' ), 0) AS maleComNetwork,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SOCIAL_NETWORK' AND hivst.setting = 'HTS_ENTRY_POINT_COMMUNITY' AND hivst.sex = 'Female' ), 0) AS femaleComNetwork,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SOCIAL_NETWORK' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Male' ), 0) AS malePrivateNetwork,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.categoryOfClients = 'HIVST_KIT_USER_SOCIAL_NETWORK' AND hivst.setting = 'PRIVATE' AND hivst.sex = 'Female' ), 0) AS femalePrivateNetwork,\n" +
            "\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.setting = 'HTS_ENTRY_POINT_FACILITY'), 0) AS totalFac,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.setting = 'COMMUNITY_HTS_TEST_SETTING_CT'), 0) AS totalCom,\n" +
            "COALESCE(COUNT(*) FILTER (WHERE hivst.setting = 'PRIVATE'), 0) AS totalPrivate\n" +
            "FROM hivst_groups ig\n" +
            "LEFT JOIN hivstCte hivst\n" +
            "ON ig.hivst_group = hivst.entryPoint\n" +
            "GROUP BY ig.sort_order, ig.hivst_group\n" +
            "ORDER BY ig.sort_order\n" +
            " ) w\n" +
            " CROSS JOIN LATERAL (\n" +
            "VALUES\n" +
            "(\n" +
            " 'maleSelf',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.maleFacSelf\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.maleComSelf\n" +
            "  ELSE w.malePrivateSelf\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'femaleSelf',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.femaleFacSelf\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.femaleComSelf\n" +
            "  ELSE w.femalePrivateSelf\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'malePartner',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.maleFacPartner\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.maleComPartner\n" +
            "  ELSE w.malePrivatePartener\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'femalePartner',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.femaleFacSPartner\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.femaleComPartner\n" +
            "  ELSE w.femalePrivatePartner\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'maleCaregiver',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.maleFacAssist\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.maleComAssist\n" +
            "  ELSE w.malePrivateAssist\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'femaleCaregiver',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.femaleFacAssist\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.femaleComAssist\n" +
            "  ELSE w.femalePrivateAssist\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'maleSocial',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.maleFacNetwork\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.maleComNetwork\n" +
            "  ELSE w.malePrivateNetwork\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'femaleSocial',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.femaleFacNetwork\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.femaleComNetwork\n" +
            "  ELSE w.femalePrivateNetwork\n" +
            " END\n" +
            "),\n" +
            "(\n" +
            " 'totalHivst',\n" +
            " CASE\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_FACILITY'\n" +
            "   THEN w.totalFac\n" +
            "  WHEN w.hivst_group = 'HTS_ENTRY_POINT_COMMUNITY'\n" +
            "   THEN w.totalCom\n" +
            "  ELSE w.totalPrivate\n" +
            " END\n" +
            ")\n" +
            ") kv(columnKey, value)\n" +
            "), \n" +
            "section8 AS (\n" +
            "    SELECT 'HIVST_RESULT' AS section, 'Reactive HIVST results' AS testResult, 'Reactive HIVST results' AS rowLabel, kv.columnKey, kv.value\n" +
            "    FROM (\n" +
            "        SELECT\n" +
            "            COALESCE(SUM(reactive_le_15), 0) AS hivstLess,\n" +
            "            COALESCE(SUM(reactive_gt_15), 0) AS hivstGreater,\n" +
            "            COALESCE(SUM(reactive_le_15) + SUM(reactive_gt_15), 0) AS totalHivSt\n" +
            "        FROM hivstCte\n" +
            "    ) w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            ('hivstLess',    w.hivstLess),\n" +
            "            ('hivstGreater', w.hivstGreater),\n" +
            "            ('totalHivSt',   w.totalHivSt)\n" +
            "    ) AS kv(columnKey, value)\n" +
            "),  \n" +
            "section9 AS (\n" +
            "    SELECT 'HIVST_RESULT_LINKED' AS section, w.testResult, w.testResult AS rowLabel, kv.columnKey, kv.value\n" +
            "    FROM (\n" +
            "        SELECT tr.testResult, \n" +
            "            COALESCE(COUNT(*) FILTER (WHERE sw.typeOfSession = 'COUNSELING_TYPE_PREVIOUSLY_SELF-TESTED' AND sw.ageBy15 = 'less15' ), 0) AS less15,\n" +
            "            COALESCE(COUNT(*) FILTER (WHERE sw.typeOfSession = 'COUNSELING_TYPE_PREVIOUSLY_SELF-TESTED' AND sw.ageBy15 = '15+' ), 0) AS gt15\n" +
            "        FROM test_results tr\n" +
            "        LEFT JOIN (\n" +
            "            SELECT finalHivTestResult AS testResult, ageBy15, typeOfSession\n" +
            "            FROM s1_src\n" +
            "        ) sw\n" +
            "            ON sw.testResult = tr.testResult\n" +
            "        GROUP BY tr.testResult\n" +
            "    ) w\n" +
            "    CROSS JOIN LATERAL (\n" +
            "        VALUES\n" +
            "            ('less15', COALESCE(w.less15, 0)),\n" +
            "            ('gt15',   COALESCE(w.gt15, 0)),\n" +
            "            ('total',  COALESCE(w.less15 + w.gt15, 0))\n" +
            "    ) kv(columnKey, value)\n" +
            "),\n" +
            "s4_wide AS (\n" +
            " SELECT\n" +
            "  finalHivTestResult AS testResult,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'MSM'  AND sex = 'Male')   AS msm_m,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'MSM'  AND sex = 'Female') AS msm_f,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'PWID'    AND sex = 'Male')   AS pwid_m,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'PWID'    AND sex = 'Female') AS pwid_f,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'SEX_WORKER' AND sex = 'Male')   AS sex_worker_m,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'SEX_WORKER' AND sex = 'Female') AS sex_worker_f,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'PPOCS'   AND sex = 'Male')   AS ppocs_m,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'PPOCS'   AND sex = 'Female') AS ppocs_f,\n" +
            "  COUNT(*) FILTER (WHERE keyPopulationType = 'AGYW') AS agyw,\n" +
            "  COUNT(*) AS total\n" +
            " FROM base\n" +
            " WHERE keyPopulationType IS NOT NULL\n" +
            " GROUP BY finalHivTestResult\n" +
            "),\n" +
            "section7 AS (\n" +
            "SELECT CAST('KEY_POPULATION' AS text) AS section, tr.testResult AS testResult, tr.testResult AS rowLabel, kv.columnKey, kv.value\n" +
            " FROM test_results tr \n" +
            " LEFT JOIN s4_wide w ON w.testResult = tr.testResult\n" +
            " CROSS JOIN LATERAL (VALUES\n" +
            "  ('msm_m',  COALESCE(w.msm_m, 0)),\n" +
            "  ('msm_f',  COALESCE(w.msm_f, 0)),\n" +
            "  ('pwid_m',    COALESCE(w.pwid_m, 0)),\n" +
            "  ('pwid_f',    COALESCE(w.pwid_f, 0)),\n" +
            "  ('sex_worker_m', COALESCE(w.sex_worker_m, 0)),\n" +
            "  ('sex_worker_f', COALESCE(w.sex_worker_f, 0)),\n" +
            "  ('ppocs_m',   COALESCE(w.ppocs_m, 0)),\n" +
            "  ('ppocs_f',   COALESCE(w.ppocs_f, 0)),\n" +
            "  ('agyw',   COALESCE(w.agyw, 0)),\n" +
            "  ('total',  COALESCE(w.total, 0))\n" +
            " ) AS kv(columnKey, value)\n" +
            "),\n" +
            "combined AS (\n" +
            " SELECT * FROM section1\n" +
            " UNION ALL\n" +
            " SELECT * FROM section2\n" +
            " UNION ALL\n" +
            " SELECT * FROM section3\n" +
            " UNION ALL\n" +
            " SELECT * FROM section4\n" +
            " UNION ALL\n" +
            " SELECT * FROM section5\n" +
            " UNION ALL\n" +
            " SELECT * FROM section6\n" +
            " UNION ALL\n" +
            " SELECT * FROM section7\n" +
            " UNION ALL\n" +
            " SELECT * FROM section8\n" +
            " UNION ALL\n" +
            " SELECT * FROM section9\n" +
            ")\n" +
            "\n" +
            "SELECT *\n" +
            "FROM combined\n" +
            "ORDER BY\n" +
            " CASE section\n" +
            "  WHEN 'RESULTS_BY_AGE'   THEN 1\n" +
            "  WHEN 'ACUTE_HIV'  THEN 2\n" +
            "  WHEN 'RECENCY'    THEN 3\n" +
            "  WHEN 'KEY_POPULATION'   THEN 4\n" +
            " END,\n" +
            " CASE testResult\n" +
            "  WHEN 'Negative' THEN 1\n" +
            "  WHEN 'Positive' THEN 2\n" +
            "  ELSE 3\n" +
            " END,\n" +
            " CASE rowLabel\n" +
            "  WHEN '1 - 4'   THEN 1\n" +
            "  WHEN '5 - 9'   THEN 2\n" +
            "  WHEN '10 - 14' THEN 3\n" +
            "  WHEN '15 - 19' THEN 4\n" +
            "  WHEN '20 - 24' THEN 5\n" +
            "  WHEN '25 - 49' THEN 6\n" +
            "  WHEN '50+'  THEN 7\n" +
            "  WHEN 'Recent'  THEN 1\n" +
            "  WHEN 'Long-term'  THEN 2\n" +
            "  WHEN 'TOTAL'   THEN 99\n" +
            "  ELSE 50\n" +
            " END;";
}
