WITH client_data AS (
    SELECT
        pa.anc_no AS anc_id,
        pp.date_of_birth,
        DATE_PART('year', AGE(pp.date_of_birth)) AS age,
        pa.tested_syphilis,
        pa.test_result_syphilis,
        pa.treated_syphilis,
        pa.previously_known_hiv_status,
        pa.static_hiv_status,
        pe.entry_point,
        hts_rs.modality,
        hc.test1,
        hc.hiv_test_result2,
        pe.hepatitisb,
        pe.art_start_time,
        hc.recency,
        pe.hiv_status,
        pd.place_of_delivery,
        pd.date_of_delivery,
        pd.non_hbv_exposed_infant_given_hb_within_24hrs,
        pd.booking_status,
        pd.child_status,
        pd.hiv_exposed_infant_given_hb_within24hrs,
        pira.infant_arv_time,
        pira.age_at_ctx,
        pip.age_at_test,
        pip.results
    FROM
        public.patient_person pp
            LEFT JOIN
        public.pmtct_anc pa ON pp.uuid = pa.person_uuid
            LEFT JOIN
        public.pmtct_enrollment pe ON pp.uuid = pe.person_uuid
            LEFT JOIN
        public.hts_risk_stratification hts_rs ON pp.uuid = hts_rs.person_uuid
            LEFT JOIN
        public.hts_client hc ON pp.uuid = hc.person_uuid
            LEFT JOIN
        public.pmtct_delivery pd ON pp.uuid = pd.person_uuid
            LEFT JOIN
        public.pmtct_infant_arv pira ON pp.uuid = pira.uuid
            LEFT JOIN
        public.pmtct_infant_pcr pip ON pp.uuid = pip.uuid
    WHERE
        pa.person_uuid IS NOT NULL

)
SELECT
    jsonb_build_object(
            'Number of New ANC Clients', jsonb_build_object(
            '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10),
            '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14),
            '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19),
            '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24),
            '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29),
            '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34),
            '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39),
            '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44),
            '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49),
            '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50),
            'Total', COUNT(DISTINCT anc_id)
                                         ),
            'Number of New ANC Clients Tested for Syphilis', jsonb_build_object(
                    'Yes', COUNT(DISTINCT anc_id) FILTER (WHERE tested_syphilis = 'yes'),
                    'No', COUNT(DISTINCT anc_id) FILTER (WHERE tested_syphilis = 'no')
                                                             ),
            'Number of New ANC Clients Tested Positive for Syphilis', jsonb_build_object(
                    'Positive', COUNT(DISTINCT anc_id) FILTER (WHERE test_result_syphilis = 'positive'),
                    'Negative', COUNT(DISTINCT anc_id) FILTER (WHERE test_result_syphilis = 'negative')
                                                                      ),
            'Number of ANC Clients Treated for Syphilis', jsonb_build_object(
                    'Yes', COUNT(DISTINCT anc_id) FILTER (WHERE treated_syphilis = 'yes'),
                    'No', COUNT(DISTINCT anc_id) FILTER (WHERE treated_syphilis = 'no')
                                                          ),
            'Number of Pregnant Women with Previously Known HIV Positive Infection ANC', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive')
                                                                                         ),
            'Number of Pregnant Women with Previously Known HIV Positive Infection L&D', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND entry_point = 'PMTCT_ENTRY_POINT_L&D'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE entry_point = 'PMTCT_ENTRY_POINT_L&D')
                                                                                         ),
            'Number of Pregnant Women with Previously Known HIV Positive Infection <72hrs Postpartum', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM')
                                                                                                       ),
            'Total Number of Pregnant Women with Previously Known HIV Positive Infection (ANC,L&D,<72hrs Postpartum)', jsonb_build_object(
                    '<10',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '10-14',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '15-19',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '20-24',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '25-29',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '30-34',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '35-39',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '40-44',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '45-49',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    '50+',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM'),
                    'Total',
                    COUNT(DISTINCT anc_id) FILTER (WHERE previously_known_hiv_status = 'yes' AND static_hiv_status = 'positive') +
                COUNT(DISTINCT anc_id) FILTER (WHERE entry_point = 'PMTCT_ENTRY_POINT_L&D') +
                COUNT(DISTINCT anc_id) FILTER (WHERE entry_point = 'PMTCT_ENTRY_POINT_POST-PARTUM')
                                                                                                                       ),
            'Number of Pregnant Women HIV Tested and Received Results ANC', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL)
                                                                            ),
            'Number of Pregnant Women HIV Tested and Received Results L&D', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL)
                                                                            ),
            'Number of Pregnant Women HIV Tested and Received Results <72hrs Postpartum', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL)
                                                                                          ),
            'Total Number of Pregnant Women HIV Tested and Received Results', jsonb_build_object(
                    '<10',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '10-14',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '15-19',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '20-24',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '25-29',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '30-34',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '35-39',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '40-44',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '45-49',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    '50+',
                    COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL),
                    'Total',
                    COUNT(DISTINCT anc_id) FILTER (WHERE modality = 'PMTCT (ANC1 Only)' AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND test1 IS NOT NULL) +
                COUNT(DISTINCT anc_id) FILTER (WHERE modality = 'Post ANC1 Pregnant/L&D > 72hrs' AND test1 IS NOT NULL)
                                                                              ),
            'Number of Pregnant Women Tested HIV Positive ANC', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality = 'PMTCT (ANC1 Only)' AND hiv_test_result2 = 'positive')
                                                                ),
            'Number of Pregnant Women Tested HIV Positive L&D', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive')
                                                                ),
            'Number of Pregnant Women Tested HIV Positive L&D', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive')
                                                                ),
            'Number of New ANC Clients Tested for HBV (ANC, L&D, <72hrs Postpartum)', jsonb_build_object(
                    'Positive', COUNT(DISTINCT anc_id) FILTER (WHERE entry_point IN ('PMTCT_ENTRY_POINT_ANC', 'PMTCT_ENTRY_POINT_POST-PARTUM', 'PMTCT_ENTRY_POINT_L&D') AND hepatitisb = 'positive'),
                    'Negative', COUNT(DISTINCT anc_id) FILTER (WHERE entry_point IN ('PMTCT_ENTRY_POINT_ANC', 'PMTCT_ENTRY_POINT_POST-PARTUM', 'PMTCT_ENTRY_POINT_L&D') AND hepatitisb = 'negative'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE entry_point IN ('PMTCT_ENTRY_POINT_ANC', 'PMTCT_ENTRY_POINT_POST-PARTUM', 'PMTCT_ENTRY_POINT_L&D') AND hepatitisb IS NOT NULL)
                                                                                      ),
            'Number of New ANC Clients Tested for HBV with Positive HIV Status', jsonb_build_object(
                    'Positive', COUNT(DISTINCT anc_id) FILTER (WHERE static_hiv_status = 'positive' AND hepatitisb = 'positive'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE static_hiv_status = 'positive' AND hepatitisb IS NOT NULL)
                                                                                 ),
            'Number of HIV Positive Pregnant Women Tested for Recent Infection', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'sampleTestDate') IS NOT NULL)
                                                                                 ),
            'Number of HIV Positive Pregnant Women Tested for Recent Infection with Result Recent', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent')
                                                                                                    ),
            'Number of HIV Positive Pregnant Women Tested for Recent Infection with Result Long Term', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Long Term')
                                                                                                       ),
            'Number of HIV Positive Pregnant Women with Confirmed Recent Result through Viral Load Testing', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '1000'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '<1000')
                                                                                                             ),
            'Number of HIV Positive Pregnant Women with Confirmed Long-Term Result through Viral Load Testing', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification') = '>=1000'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE modality IN ('PMTCT (ANC1 Only)', 'Post ANC1 Pregnant/L&D ≤ 72hrs', 'Post ANC1 Pregnant/L&D > 72hrs') AND hiv_test_result2 = 'positive' AND (recency ->> 'recencyInterpretation') = 'RTRI Recent' AND (recency ->> 'viralLoadResultClassification')= '>=1000')
                                                                                                                ),
            'Number of HIV Positive Pregnant Women Already on ART Prior to This Pregnancy', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE art_start_time = 'TIMING_MOTHERS_ART_INITIATION_PRIOR_TO_THIS_PREGNANCY_')
                                                                                            ),
            'Number of HIV Positive Pregnant Women Newly Started on ART During ANC <36wks of Pregnancy', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_<_36_WEEKS_GESTATION_PERIOD')
                                                                                                         ),
            'Number of HIV Positive Pregnant Women Newly Started on ART During ANC >36wks of Pregnancy', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_DURING_PREGNANCY_>_36_WEEKS_GESTATION_PERIOD')
                                                                                                         ),
            'Number of HIV Positive Pregnant Women Newly Started on ART During Labour', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AT_L&D')
                                                                                        ),
            'Number of HIV Positive Pregnant Women Newly Started on ART During Post Partum (<72 hrs)', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3)),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) <= 3))
                                                                                                       ),
            'Number of HIV Positive Pregnant Women Newly Started on ART During Post Partum (>72 hrs - <6 months)', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6)),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('day', AGE(current_date, date_of_birth)) > 3 AND DATE_PART('month', AGE(current_date, date_of_birth)) < 6))
                                                                                                                   ),
            'Number of HIV Positive Pregnant Women Newly Started on ART During Post Partum (>6 - 12 months)', jsonb_build_object(
                    '<10', COUNT(DISTINCT anc_id) FILTER (WHERE age < 10 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '10-14', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 10 AND 14 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '15-19', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 15 AND 19 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '20-24', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 20 AND 24 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '25-29', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 25 AND 29 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '30-34', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 30 AND 34 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '35-39', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 35 AND 39 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '40-44', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 40 AND 44 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '45-49', COUNT(DISTINCT anc_id) FILTER (WHERE age BETWEEN 45 AND 49 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    '50+', COUNT(DISTINCT anc_id) FILTER (WHERE age >= 50 AND art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12)),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE art_start_time = 'TIMING_MOTHERS_ART_INITIATION_INITIATED_ART_AFTER_DELIVERY_(POST-PARTUM)' AND (DATE_PART('month', AGE(current_date, date_of_birth)) BETWEEN 6 AND 12))
                                                                                                              ),
            'Total Deliveries at Facility (Booked and Unbooked Pregnant Women)', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE date_of_delivery IS NOT NULL AND booking_status IN ('BOOKING_STATUS_UNBOOKED', 'BOOKING_STATUS_BOOKED'))
                                                                                 ),

            'Number of Booked HIV Positive Pregnant Women Who Delivered at Facility', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE date_of_delivery IS NOT NULL AND booking_status = 'BOOKING_STATUS_BOOKED' AND place_of_delivery = 'PLACE_OF_DELIVERY__HEALTH_FACILITY:_HUB' AND hiv_status = 'positive')
                                                                                      ),

            'Number of Unbooked HIV Positive Pregnant Women Who Delivered at Facility', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE date_of_delivery IS NOT NULL AND booking_status = 'BOOKING_STATUS_UNBOOKED' AND place_of_delivery = 'PLACE_OF_DELIVERY__HEALTH_FACILITY:_HUB' AND hiv_status = 'positive')
                                                                                        ),

            'Number of Live Births by HIV Positive Women Who Delivered at Facility', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE child_status = 'CHILD_STATUS_DELIVERY_ALIVE' AND hiv_status = 'positive')
                                                                                     ),

            'Number of Infants Delivered to Hepatitis B Positive Pregnant Women at Facility', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE date_of_delivery IS NOT NULL AND hepatitisb = 'positive')
                                                                                              ),

            'Number of Babies Born to Hepatitis B Positive Mothers Who Received Immunoglobulin Within 24 hrs of Delivery', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE date_of_delivery IS NOT NULL AND hiv_exposed_infant_given_hb_within24hrs = 'yes' AND hepatitisb = 'positive')
                                                                                                                           ),

            'Number of HIV Exposed Infants Who Received HBV Monovalent Vaccine Within 24hrs of Delivery at Facility', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_exposed_infant_given_hb_within24hrs = 'yes' AND hiv_status = 'positive')
                                                                                                                      ),
            'Number of HIV-Exposed Infants Born to HIV Positive Women Who Received ARV Prophylaxis Within 72 hrs of Delivery', jsonb_build_object(
                    'Facility', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND place_of_delivery = 'PLACE_OF_DELIVERY__HEALTH_FACILITY:_HUB' AND non_hbv_exposed_infant_given_hb_within_24hrs = 'yes' AND infant_arv_time = 'within 72 hour'),
                    'Outside Facility', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND place_of_delivery <> 'PLACE_OF_DELIVERY__HEALTH_FACILITY:_HUB' AND non_hbv_exposed_infant_given_hb_within_24hrs = 'yes' AND infant_arv_time = 'within 72 hour'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND non_hbv_exposed_infant_given_hb_within_24hrs = 'yes' AND infant_arv_time = 'within 72 hour')
                                                                                                                               ),
            'Number of HIV-Exposed Infants Born to HIV Positive Women Who Received ARV Prophylaxis After 72 hrs of Delivery', jsonb_build_object(
                    'Facility', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND place_of_delivery = 'PLACE_OF_DELIVERY__HEALTH_FACILITY:_HUB' AND infant_arv_time = 'after 72 hour'),
                    'Outside Facility', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND place_of_delivery <> 'PLACE_OF_DELIVERY__HEALTH_FACILITY:_HUB' AND infant_arv_time = 'after 72 hour'),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND infant_arv_time = 'after 72 hour')
                                                                                                                              ),
            'Number of Infants Born to HIV-Infected Women Started on CTX Prophylaxis Within Two Months of Birth', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_ctx = '=<2months')
                                                                                                                  ),
            'Number of Infants Born to HIV-Infected Women Started on CTX Prophylaxis Within Two Months of Birth Based on Age at Test', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '<72 hrs' AND date_of_delivery IS NOT NULL)
                                                                                                                                       ),
            'Number of Infants Born to HIV Positive Women Whose Blood Samples Were Taken for DNA PCR Test Within 72 hrs of Birth', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '<72 hrs' AND date_of_delivery IS NOT NULL)
                                                                                                                                   ),
            'Number of Infants Born to HIV Positive Women Whose Blood Samples Were Taken for DNA PCR Test Between >72 hrs - <2 Months of Birth', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '>72 hrs - < 2 months' AND date_of_delivery IS NOT NULL)
                                                                                                                                                 ),
            'Number of Infants Born to HIV Positive Women Whose Blood Samples Were Taken for DNA PCR Test Between 2-12 Months of Birth', jsonb_build_object(
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test <= '2-12 months' AND date_of_delivery IS NOT NULL)
                                                                                                                                         ),
            'Number of HIV PCR Results Received for Babies Whose Samples Were Taken Within 72 hrs of Birth', jsonb_build_object(
                    'Positive', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '<72 hrs' AND results = 'positive' AND date_of_delivery IS NOT NULL),
                    'Negative', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '<72 hrs' AND results = 'negative' AND date_of_delivery IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '<72 hrs' AND date_of_delivery IS NOT NULL)
                                                                                                             ),
            'Number of HIV PCR Results Received for Babies Whose Samples Were Taken Between >72 hrs - <2 Months of Birth', jsonb_build_object(
                    'Positive', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '>72 hrs - <2 months' AND results = 'positive' AND date_of_delivery IS NOT NULL),
                    'Negative', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '>72 hrs - <2 months' AND results = 'negative' AND date_of_delivery IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '>72 hrs - <2 months' AND date_of_delivery IS NOT NULL)
                                                                                                                           ),
            'Number of HIV PCR Results Received for Babies Whose Samples Were Taken Between 2-12 Months of Birth', jsonb_build_object(
                    'Positive', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '2-12 months' AND results = 'positive' AND date_of_delivery IS NOT NULL),
                    'Negative', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '2-12 months' AND results = 'negative' AND date_of_delivery IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '2-12 months' AND date_of_delivery IS NOT NULL)
                                                                                                                   ),
            'Number of HIV Exposed Babies Who Tested for HIV Within 18-24 Months of Birth by Rapid Test', jsonb_build_object(
                    'Positive', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '18-24 months' AND results = 'positive' AND date_of_delivery IS NOT NULL),
                    'Negative', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '18-24 months' AND results = 'negative' AND date_of_delivery IS NOT NULL),
                    'Total', COUNT(DISTINCT anc_id) FILTER (WHERE hiv_status = 'positive' AND age_at_test = '18-24 months' AND date_of_delivery IS NOT NULL)
                                                                                                          )

    ) AS result_json
FROM
    client_data;
