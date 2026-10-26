package org.lamisplus.modules.report.repository.queries;

public class PrEPMsfREPORT {

    public static final String PREP_MONTHLY_MSF_QUERY = "WITH prepSummary AS (\n" +
            "    SELECT ps.person_uuid, ps.population_type, p.date_of_birth, INITCAP(p.sex) AS sex,\n" +
            "           he.observation->>'pregnancyStatus' AS pregnancyStatus,\n" +
            "           CASE\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 15 AND 24 THEN '15 - 24'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 25 AND 49 THEN '25 - 49'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) >= 50 THEN '50+'\n" +
            "           END AS ageGroup\n" +
            "    FROM prophylaxis_screening ps\n" +
            "    LEFT JOIN patient_person p ON p.uuid = ps.person_uuid\n" +
            "    LEFT JOIN hts_encounter he ON he.patient_uuid = ps.person_uuid\n" +
            "    WHERE ps.archived = FALSE AND ps.facility_id = ?1\n" +
            "      AND ps.visit_date BETWEEN ?2 AND ?3\n" +
            "      AND he.date_of_visit BETWEEN ?2 AND ?3\n" +
            "      AND p.archived = 0\n" +
            "),\n" +
            "prepInit AS (\n" +
            "    SELECT pi.person_uuid, pi.population_type, pi.prep_type_at_start, p.date_of_birth, INITCAP(p.sex) AS sex,\n" +
            "           he.observation->>'pregnancyStatus' AS pregnancyStatus,\n" +
            "           CASE\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 15 AND 24 THEN '15 - 24'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 25 AND 49 THEN '25 - 49'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) >= 50 THEN '50+'\n" +
            "           END AS ageGroup\n" +
            "    FROM prophylaxis_initiation pi\n" +
            "\tLEFT JOIN prophylaxis_interruptions pit ON pit.prophylaxis_initiation_uuid = pi.uuid\n" +
            "    LEFT JOIN patient_person p ON p.uuid = pi.person_uuid\n" +
            "    LEFT JOIN hts_encounter he ON he.patient_uuid = pi.person_uuid\n" +
            "    WHERE pi.archived = FALSE AND pi.facility_id = ?1\n" +
            "      AND pi.date_prep_started BETWEEN ?2 AND ?3\n" +
            "      AND he.date_of_visit BETWEEN ?2 AND ?3\n" +
            "      AND p.archived = 0\n" +
            "),\n" +
            "prepFollowup AS (\n" +
            "    SELECT DISTINCT ON (pf.person_uuid)\n" +
            "           pf.person_uuid, pf.population_type, pf.prep_type, p.date_of_birth, INITCAP(p.sex) AS sex,\n" +
            "           he.observation->>'pregnancyStatus' AS pregnancyStatus,\n" +
            "           CASE\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 15 AND 24 THEN '15 - 24'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 25 AND 49 THEN '25 - 49'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) >= 50 THEN '50+'\n" +
            "           END AS ageGroup\n" +
            "    FROM prep_followup_visit pf\n" +
            "    LEFT JOIN patient_person p ON p.uuid = pf.person_uuid\n" +
            "    LEFT JOIN hts_encounter he ON he.patient_uuid = pf.person_uuid\n" +
            "    WHERE pf.archived = FALSE AND pf.facility_id = ?1\n" +
            "      AND pf.encounter_date BETWEEN ?2 AND ?3\n" +
            "      AND he.date_of_visit BETWEEN ?2 AND ?3\n" +
            "      AND p.archived = 0\n" +
            "    ORDER BY pf.person_uuid, pf.encounter_date DESC\n" +
            "),\n" +
            "pepFollowup AS (\n" +
            "   SELECT pfv.person_uuid, pfv.mode_of_exposure, INITCAP(p.sex) AS sex,pfv.encounter_date, lr.result_reported, CAST(date_result_reported AS DATE) date_result_reported,\n" +
            "CASE\n" +
            "    WHEN REPLACE(lr.result_reported, ',', '') ~ '^[0-9]+(\\.[0-9]+)?$'\n" +
            "    AND CAST(REPLACE(lr.result_reported, ',', '') AS DOUBLE PRECISION) >= 1000\n" +
            "        THEN 'Target Detected'\n" +
            "    ELSE 'Target Not Detected'\n" +
            "END AS vlCategory,\n" +
            "\t\t   CASE\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 15 AND 24 THEN '15 - 24'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 25 AND 49 THEN '25 - 49'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) >= 50 THEN '50+'\n" +
            "           END AS ageGroup\n" +
            "FROM pep_followup_visit pfv\n" +
            "LEFT JOIN laboratory_result lr ON lr.patient_uuid = pfv.person_uuid\n" +
            "INNER JOIN public.laboratory_test lt ON lr.test_id = lt.id AND lt.lab_test_id = 16 AND lt.viral_load_indication != 719\n" +
            "LEFT JOIN patient_person p ON p.uuid = pfv.person_uuid\n" +
            "LEFT JOIN hts_encounter he ON he.patient_uuid = pfv.hts_encounter_uuid AND he.date_of_visit BETWEEN ?2 AND ?3\n" +
            "WHERE pfv.archived = FALSE AND pfv.facility_id = ?1\n" +
            "      AND pfv.encounter_date BETWEEN ?2 AND ?3\n" +
            "\t  -- AND CAST(date_result_reported AS DATE) BETWEEN ?2 AND ?2\n" +
            "      AND p.archived = 0\n" +
            "),\n" +
            "prepSeroConverted AS (\n" +
            "SELECT pit.person_uuid, pi.population_type, pi.prep_type_at_start prep_type, p.date_of_birth, INITCAP(p.sex) AS sex,\n" +
            "           he.observation->>'pregnancyStatus' AS pregnancyStatus,\n" +
            "           CASE\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 15 AND 24 THEN '15 - 24'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) BETWEEN 25 AND 49 THEN '25 - 49'\n" +
            "               WHEN DATE_PART('year', AGE(p.date_of_birth)) >= 50 THEN '50+'\n" +
            "           END AS ageGroup\n" +
            "FROM prophylaxis_interruptions pit\n" +
            "LEFT JOIN prophylaxis_initiation pi ON pi.uuid = pit.prophylaxis_initiation_uuid\n" +
            "LEFT JOIN patient_person p ON p.uuid = pit.person_uuid\n" +
            "LEFT JOIN hts_encounter he ON he.uuid = pit.hts_encounter_uuid\n" +
            "WHERE pit.archived = FALSE AND pit.facility_id = ?1\n" +
            "      AND pit.date_sero_converted BETWEEN ?2 AND ?3\n" +
            "      AND p.archived = 0\n" +
            "),\n" +
            "mode_of_exposures AS (\n" +
            "    SELECT * FROM (VALUES\n" +
            "        (1, 'PEP_MODE_OF_EXPOSURE_OCCUPATIONAL'),\n" +
            "        (2, 'PEP_MODE_OF_EXPOSURE_NON-OCCUPATIONAL'),\n" +
            "        (3, 'PEP_MODE_OF_EXPOSURE_SUSPECTED_ACUTE_HIV_INFECTION')\n" +
            "    ) AS t(sort_order, mode_of_exposure)\n" +
            "),\n" +
            "viralLoad_groups AS (\n" +
            "    SELECT * FROM (VALUES\n" +
            "        (1, 'Target Not Detected'),\n" +
            "        (2, 'Target Detected')\n" +
            "    ) AS t(sort_order, vl_groups)\n" +
            "),\n" +
            "population_groups AS (\n" +
            "    SELECT * FROM (VALUES\n" +
            "        (1, 'POPULATION_TYPE_SERODISCORDANT_COUPLES_(SDC)'),\n" +
            "        (2, 'POPULATION_TYPE_SEX_WORKERS'),\n" +
            "        (3, 'POPULATION_TYPE_INJECTING_DRUG_USERS'),\n" +
            "        (4, 'POPULATION_TYPE_INDIVIDUALS_WHO_ENGAGE_IN_ANAL_SEX_ON_A_PROLONGED_AND_REGULAR_BASIS'),\n" +
            "        (5, 'POPULATION_TYPE_EXPOSED_ADOLESCENTS_AND_YOUNG_PEOPLE'),\n" +
            "        (6, 'POPULATION_TYPE_TRANSGENDER'),\n" +
            "        (7, 'POPULATION_TYPE_GEN_POP')\n" +
            "    ) AS t(sort_order, population_type)\n" +
            "),\n" +
            "prep_type_groups AS (\n" +
            "    SELECT * FROM (VALUES\n" +
            "        (1, 'PREP_TYPE_ORAL',        'Oral'),\n" +
            "        (2, 'PREP_TYPE_INJECTIBLES', 'Injectable'),\n" +
            "        (3, 'PREP_TYPE_RING',        'Ring'),\n" +
            "        (4, 'PREP_TYPE_OTHERS',      'Others')\n" +
            "    ) AS t(sort_order, prep_type_code, prep_type_label)\n" +
            "),\n" +
            "prep_summary_wide AS (\n" +
            "    SELECT population_type,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '15 - 24') AS male_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '25 - 49') AS male_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '50+')     AS male_50,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '15 - 24') AS female_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '25 - 49') AS female_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '50+')     AS female_50,\n" +
            "           COUNT(*) AS total\n" +
            "    FROM prepSummary\n" +
            "    GROUP BY population_type\n" +
            "),\n" +
            "section_prep_eligible AS (\n" +
            "    SELECT\n" +
            "        'PREP_ELIGIBLE' AS section,\n" +
            "        CASE pg.population_type\n" +
            "            WHEN 'POPULATION_TYPE_SERODISCORDANT_COUPLES_(SDC)' THEN 'Serodiscordant Couples(SDC)'\n" +
            "            WHEN 'POPULATION_TYPE_SEX_WORKERS' THEN 'Sex Workers'\n" +
            "            WHEN 'POPULATION_TYPE_INJECTING_DRUG_USERS' THEN 'Injecting Drug Users'\n" +
            "            WHEN 'POPULATION_TYPE_INDIVIDUALS_WHO_ENGAGE_IN_ANAL_SEX_ON_A_PROLONGED_AND_REGULAR_BASIS' THEN 'Individuals who engage in anal sex on a prolonged and regular basis'\n" +
            "            WHEN 'POPULATION_TYPE_EXPOSED_ADOLESCENTS_AND_YOUNG_PEOPLE' THEN 'Exposed adolescents and young people'\n" +
            "            WHEN 'POPULATION_TYPE_TRANSGENDER' THEN 'Transgender'\n" +
            "            WHEN 'POPULATION_TYPE_GEN_POP' THEN 'Other population'\n" +
            "        END AS rowLabel,\n" +
            "        pg.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM population_groups pg\n" +
            "    LEFT JOIN prep_summary_wide w ON w.population_type = pg.population_type\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "prep_init_wide AS (\n" +
            "    SELECT population_type, prep_type_at_start,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '15 - 24') AS male_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '25 - 49') AS male_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '50+')     AS male_50,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '15 - 24') AS female_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '25 - 49') AS female_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '50+')     AS female_50,\n" +
            "           COUNT(*) AS total\n" +
            "    FROM prepInit\n" +
            "    GROUP BY population_type, prep_type_at_start\n" +
            "),\n" +
            "section_prep_new_init AS (\n" +
            "    SELECT\n" +
            "        'PREP_NEW_INIT' AS section,\n" +
            "        CASE pg.population_type\n" +
            "            WHEN 'POPULATION_TYPE_SERODISCORDANT_COUPLES_(SDC)' THEN 'Serodiscordant Couples(SDC)'\n" +
            "            WHEN 'POPULATION_TYPE_SEX_WORKERS' THEN 'Sex Workers'\n" +
            "            WHEN 'POPULATION_TYPE_INJECTING_DRUG_USERS' THEN 'Injecting Drug Users'\n" +
            "            WHEN 'POPULATION_TYPE_INDIVIDUALS_WHO_ENGAGE_IN_ANAL_SEX_ON_A_PROLONGED_AND_REGULAR_BASIS' THEN 'Individuals who engage in anal sex on a prolonged and regular basis'\n" +
            "            WHEN 'POPULATION_TYPE_EXPOSED_ADOLESCENTS_AND_YOUNG_PEOPLE' THEN 'Exposed adolescents and young people'\n" +
            "            WHEN 'POPULATION_TYPE_TRANSGENDER' THEN 'Transgender'\n" +
            "            WHEN 'POPULATION_TYPE_GEN_POP' THEN 'Other population'\n" +
            "        END AS rowLabel,\n" +
            "        pg.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM population_groups pg\n" +
            "    LEFT JOIN prep_init_wide w ON w.population_type = pg.population_type\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "prep_new_init_pregnant AS (\n" +
            "    SELECT\n" +
            "        'PREP_NEW_INIT' AS section,\n" +
            "        'Pregnant/Breastfeeding' AS rowLabel,\n" +
            "        8 AS sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM (\n" +
            "        SELECT\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='15 - 24' AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_15_24,\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='25 - 49' AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_25_49,\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='50+'     AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_50\n" +
            "        FROM prepInit\n" +
            "    ) w\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24', 0),\n" +
            "        (2, 'male25_49', 0),\n" +
            "        (3, 'male50', 0),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total', COALESCE(w.female_15_24,0)+COALESCE(w.female_25_49,0)+COALESCE(w.female_50,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "section_prep_type AS (\n" +
            "    SELECT\n" +
            "        'PREP_TYPE' AS section,\n" +
            "        pt.prep_type_label AS rowLabel,\n" +
            "        pt.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM prep_type_groups pt\n" +
            "    LEFT JOIN prep_init_wide w ON w.prep_type_at_start = pt.prep_type_code\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "prep_followup_wide AS (\n" +
            "    SELECT population_type, prep_type, \n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '15 - 24') AS male_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '25 - 49') AS male_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '50+')     AS male_50,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '15 - 24') AS female_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '25 - 49') AS female_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '50+')     AS female_50,\n" +
            "           COUNT(*) AS total\n" +
            "    FROM prepFollowup\n" +
            "    GROUP BY population_type, prep_type\n" +
            "),\n" +
            "section_prep_followup AS (\n" +
            "    SELECT\n" +
            "        'PREP_FOLLOWUP' AS section,\n" +
            "        CASE pg.population_type\n" +
            "            WHEN 'POPULATION_TYPE_SERODISCORDANT_COUPLES_(SDC)' THEN 'Serodiscordant Couples(SDC)'\n" +
            "            WHEN 'POPULATION_TYPE_SEX_WORKERS' THEN 'Sex Workers'\n" +
            "            WHEN 'POPULATION_TYPE_INJECTING_DRUG_USERS' THEN 'Injecting Drug Users'\n" +
            "            WHEN 'POPULATION_TYPE_INDIVIDUALS_WHO_ENGAGE_IN_ANAL_SEX_ON_A_PROLONGED_AND_REGULAR_BASIS' THEN 'Individuals who engage in anal sex on a prolonged and regular basis'\n" +
            "            WHEN 'POPULATION_TYPE_EXPOSED_ADOLESCENTS_AND_YOUNG_PEOPLE' THEN 'Exposed adolescents and young people'\n" +
            "            WHEN 'POPULATION_TYPE_TRANSGENDER' THEN 'Transgender'\n" +
            "            WHEN 'POPULATION_TYPE_GEN_POP' THEN 'Other population'\n" +
            "        END AS rowLabel,\n" +
            "        pg.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM population_groups pg\n" +
            "    LEFT JOIN prep_followup_wide w ON w.population_type = pg.population_type\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "prep_followup_pregnant AS (\n" +
            "    SELECT\n" +
            "        'PREP_FOLLOWUP' AS section,\n" +
            "        'Pregnant/Breastfeeding' AS rowLabel,\n" +
            "        8 AS sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM (\n" +
            "        SELECT\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='15 - 24' AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_15_24,\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='25 - 49' AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_25_49,\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='50+'     AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_50\n" +
            "       FROM prepFollowup\n" +
            "    ) w\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24', 0),\n" +
            "        (2, 'male25_49', 0),\n" +
            "        (3, 'male50', 0),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total', COALESCE(w.female_15_24,0)+COALESCE(w.female_25_49,0)+COALESCE(w.female_50,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "section_prep_type_followup AS (\n" +
            "    SELECT\n" +
            "        'PREP_TYPE_FOLLOWUP' AS section,\n" +
            "        pt.prep_type_label AS rowLabel,\n" +
            "        pt.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM prep_type_groups pt\n" +
            "    LEFT JOIN prep_followup_wide w ON w.prep_type = pt.prep_type_code\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "prep_sero_wide AS (\n" +
            "    SELECT population_type, prep_type, \n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '15 - 24') AS male_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '25 - 49') AS male_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '50+')     AS male_50,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '15 - 24') AS female_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '25 - 49') AS female_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '50+')     AS female_50,\n" +
            "           COUNT(*) AS total\n" +
            "    FROM prepSeroConverted\n" +
            "    GROUP BY population_type, prep_type\n" +
            "),\n" +
            "section_prep_sero AS (\n" +
            "    SELECT\n" +
            "        'PREP_SEROCONVERTED' AS section,\n" +
            "        CASE pg.population_type\n" +
            "            WHEN 'POPULATION_TYPE_SERODISCORDANT_COUPLES_(SDC)' THEN 'Serodiscordant Couples(SDC)'\n" +
            "            WHEN 'POPULATION_TYPE_SEX_WORKERS' THEN 'Sex Workers'\n" +
            "            WHEN 'POPULATION_TYPE_INJECTING_DRUG_USERS' THEN 'Injecting Drug Users'\n" +
            "            WHEN 'POPULATION_TYPE_INDIVIDUALS_WHO_ENGAGE_IN_ANAL_SEX_ON_A_PROLONGED_AND_REGULAR_BASIS' THEN 'Individuals who engage in anal sex on a prolonged and regular basis'\n" +
            "            WHEN 'POPULATION_TYPE_EXPOSED_ADOLESCENTS_AND_YOUNG_PEOPLE' THEN 'Exposed adolescents and young people'\n" +
            "            WHEN 'POPULATION_TYPE_TRANSGENDER' THEN 'Transgender'\n" +
            "            WHEN 'POPULATION_TYPE_GEN_POP' THEN 'Other population'\n" +
            "        END AS rowLabel,\n" +
            "        pg.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM population_groups pg\n" +
            "    LEFT JOIN prep_sero_wide w ON w.population_type = pg.population_type\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "prep_sero_pregnant AS (\n" +
            "    SELECT\n" +
            "        'PREP_SEROCONVERTED' AS section,\n" +
            "        'Pregnant/Breastfeeding' AS rowLabel,\n" +
            "        8 AS sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM (\n" +
            "        SELECT\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='15 - 24' AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_15_24,\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='25 - 49' AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_25_49,\n" +
            "            COUNT(*) FILTER (WHERE sex='Female' AND ageGroup='50+'     AND (pregnancyStatus ILIKE '%PREGANACY_STATUS_PREGNANT%' OR pregnancyStatus ILIKE '%PREGANACY_STATUS_BREASTFEEDING%')) AS female_50\n" +
            "       FROM prepSeroConverted\n" +
            "    ) w\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24', 0),\n" +
            "        (2, 'male25_49', 0),\n" +
            "        (3, 'male50', 0),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total', COALESCE(w.female_15_24,0)+COALESCE(w.female_25_49,0)+COALESCE(w.female_50,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "section_prep_type_sero AS (\n" +
            "    SELECT\n" +
            "        'PREP_TYPE_SEROCONVERTED' AS section,\n" +
            "        pt.prep_type_label AS rowLabel,\n" +
            "        pt.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM prep_type_groups pt\n" +
            "    LEFT JOIN prep_sero_wide w ON w.prep_type = pt.prep_type_code\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "pep_summary_wide AS (\n" +
            "    SELECT mode_of_exposure,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '15 - 24') AS male_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '25 - 49') AS male_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '50+')     AS male_50,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '15 - 24') AS female_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '25 - 49') AS female_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '50+')     AS female_50,\n" +
            "           COUNT(*) AS total\n" +
            "    FROM pepFollowup\n" +
            "    GROUP BY mode_of_exposure\n" +
            "),\n" +
            "section_pep_followup AS (\n" +
            "    SELECT\n" +
            "        'PEP_FOLLOWUP' AS section,\n" +
            "        CASE moe.mode_of_exposure\n" +
            "            WHEN 'PEP_MODE_OF_EXPOSURE_OCCUPATIONAL' THEN 'Occupational'\n" +
            "            WHEN 'PEP_MODE_OF_EXPOSURE_NON-OCCUPATIONAL' THEN 'Non-occupational'\n" +
            "            WHEN 'PEP_MODE_OF_EXPOSURE_SUSPECTED_ACUTE_HIV_INFECTION' THEN 'Suspected Acute HIV Infection'\n" +
            "        END AS rowLabel,\n" +
            "        moe.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM mode_of_exposures moe\n" +
            "    LEFT JOIN pep_summary_wide w ON w.mode_of_exposure = moe.mode_of_exposure\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            "),\n" +
            "pep_viral_load_wide AS (\n" +
            "    SELECT vlCategory,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '15 - 24') AS male_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '25 - 49') AS male_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Male'   AND ageGroup = '50+')     AS male_50,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '15 - 24') AS female_15_24,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '25 - 49') AS female_25_49,\n" +
            "           COUNT(*) FILTER (WHERE sex = 'Female' AND ageGroup = '50+')     AS female_50,\n" +
            "           COUNT(*) AS total\n" +
            "    FROM pepFollowup\n" +
            "    GROUP BY vlCategory\n" +
            "),\n" +
            "section_pep_viral_load AS (\n" +
            "    SELECT\n" +
            "        'PEP_VIRAL_LOAD' AS section,\n" +
            "        CASE pg.vl_groups\n" +
            "            WHEN 'Target Not Detected' THEN 'Target Not Detected'\n" +
            "            WHEN 'Target Detected' THEN 'Target Detected'\n" +
            "        END AS rowLabel,\n" +
            "        pg.sort_order,\n" +
            "        kv.ord,\n" +
            "        kv.columnKey,\n" +
            "        kv.value\n" +
            "    FROM viralLoad_groups pg\n" +
            "    LEFT JOIN pep_viral_load_wide w ON w.vlCategory = pg.vl_groups\n" +
            "    CROSS JOIN LATERAL (VALUES\n" +
            "        (1, 'male15_24',   COALESCE(w.male_15_24,0)),\n" +
            "        (2, 'male25_49',   COALESCE(w.male_25_49,0)),\n" +
            "        (3, 'male50',      COALESCE(w.male_50,0)),\n" +
            "        (4, 'female15_24', COALESCE(w.female_15_24,0)),\n" +
            "        (5, 'female25_49', COALESCE(w.female_25_49,0)),\n" +
            "        (6, 'female50',    COALESCE(w.female_50,0)),\n" +
            "        (7, 'total',       COALESCE(w.total,0))\n" +
            "    ) kv(ord, columnKey, value)\n" +
            ")\n" +
            "SELECT section, rowLabel, columnKey, value\n" +
            "FROM (\n" +
            "    SELECT * FROM section_prep_eligible\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM section_prep_new_init\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM prep_new_init_pregnant\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM section_prep_type\n" +
            "\tUNION ALL\n" +
            "    SELECT * FROM section_prep_followup\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM prep_followup_pregnant\n" +
            "    UNION ALL\n" +
            "    SELECT * FROM section_prep_type_followup\n" +
            "    UNION ALL \n" +
            "\tSELECT * FROM section_pep_followup\n" +
            "\tUNION ALL\n" +
            "    SELECT * FROM section_prep_sero\n" +
            "\tUNION ALL\n" +
            "    SELECT * FROM prep_sero_pregnant\n" +
            "\tUNION ALL\n" +
            "    SELECT * FROM section_prep_type_sero\n" +
            "\tUNION ALL\n" +
            "\tSELECT * FROM section_pep_viral_load\n" +
            "\t\n" +
            ") final\n" +
            "ORDER BY\n" +
            "    CASE section\n" +
            "        WHEN 'PREP_ELIGIBLE' THEN 1\n" +
            "        WHEN 'PREP_NEW_INIT' THEN 2\n" +
            "\t\tWHEN 'PREP_TYPE' THEN 3\n" +
            "\t\tWHEN 'PREP_FOLLOWUP' THEN 4\n" +
            "        WHEN 'PREP_TYPE_FOLLOWUP' THEN 5\n" +
            "\t\tWHEN 'PREP_SEROCONVERTED' THEN 6\n" +
            "        WHEN 'PREP_TYPE_SEROCONVERTED' THEN 7\n" +
            "        WHEN 'PEP_FOLLOWUP' THEN 8\n" +
            "\t\tWHEN 'PEP_VIRAL_LOAD' THEN 9\n" +
            "    END,\n" +
            "    sort_order,\n" +
            "    ord;";
}
