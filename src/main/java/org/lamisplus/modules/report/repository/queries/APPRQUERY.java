package org.lamisplus.modules.report.repository.queries;

public class APPRQUERY {



    public static final String APPR_REPORT_QUERY ="WITH params AS (\n" +
            "    SELECT\n" +
            "        CAST(?2   AS DATE)                                             AS eop,\n" +
            "        CAST(?1 AS DATE)                                               AS sop,\n" +
            "        CAST(CAST(?2   AS DATE) - INTERVAL '3 months' AS DATE)         AS eop_3m,\n" +
            "        CAST(CAST(?2   AS DATE) - INTERVAL '6 months' AS DATE)         AS eop_6m,\n" +
            "        CAST(CAST(?2   AS DATE) - INTERVAL '9 months' AS DATE)                       AS eop_9m,\n" +
            "        CAST(CAST(?2   AS DATE) - INTERVAL '12 months' + INTERVAL '1 day' AS DATE)   AS eop_12m,\n" +
            "        CAST(CAST(?2   AS DATE) - INTERVAL '7 days' AS DATE)                         AS eop_7d\n" +
            "),\n" +
            "radet_flags AS (\n" +
            "    SELECT\n" +
            "        r.apprcode                                                    AS org_unit,\n" +
            "        r.attributecombo                                              AS attrib,\n" +
            "        r.period,\n" +
            "        r.age,\n" +
            "        UPPER(r.gender)                                               AS gender,\n" +
            "\n" +
            "        r.currentstatus ILIKE '%ACTIVE%'                              AS is_active,\n" +
            "        r.currentstatus ILIKE '%ACTIVE RESTART%'                      AS is_restart,\n" +
            "        (r.currentstatus ILIKE '%DEATH%'\n" +
            "         OR  r.currentstatus ILIKE '%DEAD%'\n" +
            "         OR  r.currentstatus ILIKE '%DIED%')                          AS is_died,\n" +
            "        r.currentstatus ILIKE '%IIT%'                                 AS is_iit,\n" +
            "        (r.currentstatus ILIKE '%TRANSFER OUT%'\n" +
            "         OR  r.currentstatus ILIKE '%TRANSFERRED OUT%')               AS is_transfer_out,\n" +
            "        (r.currentstatus ILIKE '%ACTIVE%'\n" +
            "         OR  r.currentstatus ILIKE '%DIED%'\n" +
            "         OR  r.currentstatus ILIKE '%IIT%'\n" +
            "         OR  r.currentstatus ILIKE '%STOPPED%'\n" +
            "         OR  r.currentstatus ILIKE '%TRANSFERRED OUT%')               AS is_any_status,\n" +
            "        r.previousstatus ILIKE '%ACTIVE%'                             AS prev_active,\n" +
            "        (r.clientverificationoutcome ILIKE 'valid%'\n" +
            "         OR  r.clientverificationoutcome = ''\n" +
            "         OR  r.clientverificationoutcome IS NULL)                     AS verified,\n" +
            "        r.artstartdate,\n" +
            "        r.currentstatusdate,\n" +
            "        r.previousstatus,\n" +
            "        r.previousstatusdate,\n" +
            "        r.dateofcurrentviralload,\n" +
            "        r.dateofcurrentviralloadsample,\n" +
            "        r.dateofviralloadsamplecollection,\n" +
            "        r.dateodtbscreened,\n" +
            "        r.dateoftbsamplecollection,\n" +
            "        r.dateoftbdiagnosticresultreceived,\n" +
            "        r.tbtreatmentstartdate,\n" +
            "        r.dateofiptstart,\n" +
            "        r.iptcompletiondate,\n" +
            "        r.dateofcervicalcancerscreening,\n" +
            "        r.dateofvleligibilitystatus,\n" +
            "        r.vleligibilitystatus,\n" +
            "        r.monthsofarvrefill,\n" +
            "        r.cleaned_currentviralload,\n" +
            "        r.cleaned_lastcd4count,\n" +
            "        r.careentry,\n" +
            "        r.pregnancystatus,\n" +
            "        r.tbstatus,\n" +
            "        r.tbscreeningtype,\n" +
            "        r.tbdiagnostictesttype,\n" +
            "        r.cleaned_tbdiagnosticresult_interpretation,\n" +
            "        r.modeldevolveto,\n" +
            "        r.cervicalcancerscreeningtype,\n" +
            "        r.resultofcervicalcancerscreening,\n" +
            "        r.cervicalcancerscreeningmethod,\n" +
            "        r.cervicalcancertreatmentscreened,\n" +
            "        r.ipttype,\n" +
            "        r.iptcompletionstatus,\n" +
            "        r.cleaned_causeofdeath,\n" +
            "       CAST(NULLIF(r.treatmentmethoddate, '') AS DATE) AS treatmentmethoddate\n" +
            "    FROM public.radet_table r\n" +
            "    CROSS JOIN params p      -- params is 1 row; makes p.* available here\n" +
            "),\n" +
            "agg_tx_curr AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags\n" +
            "    WHERE  is_active AND verified\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tx_new_cd4 AS (\n" +
            "    SELECT r.org_unit, r.attrib, r.period, r.gender, r.age,\n" +
            "           co_arg.cd4_min, co_arg.cd4_max, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    JOIN   public.category_option co_arg\n" +
            "           ON co_arg.cd4_min IS NOT NULL\n" +
            "          AND r.cleaned_lastcd4count > co_arg.cd4_min\n" +
            "          AND r.cleaned_lastcd4count < co_arg.cd4_max AND r.age > 4\n" +
            "    WHERE  r.verified\n" +
            "      AND  r.artstartdate BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  (r.careentry NOT ILIKE '%Transfer-in%')\n" +
            "      AND  r.cleaned_lastcd4count IS NOT NULL\n" +
            "    GROUP  BY r.org_unit, r.attrib, r.period, r.gender, r.age,\n" +
            "              co_arg.cd4_min, co_arg.cd4_max\n" +
            "),\n" +
            "agg_tx_new_nocd4 AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.verified\n" +
            "      AND  r.artstartdate BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  (r.careentry NOT ILIKE '%Transfer-in%')\n" +
            "      AND  (r.age BETWEEN 0 AND 4 OR (r.age > 4 AND r.cleaned_lastcd4count IS NULL))\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_pvls_d AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcurrentviralload    BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  r.dateofcurrentviralloadsample BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_pvls_n AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcurrentviralload    BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  r.dateofcurrentviralloadsample BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "      AND  r.cleaned_currentviralload < 1000\n" +
            "      AND  r.cleaned_currentviralload IS NOT NULL\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_rtt_cd4 AS (\n" +
            "    SELECT r.org_unit, r.attrib, r.period, r.gender, r.age,\n" +
            "           co_arg.cd4_min, co_arg.cd4_max, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    JOIN   public.category_option co_arg\n" +
            "           ON co_arg.cd4_min IS NOT NULL\n" +
            "          AND r.cleaned_lastcd4count > co_arg.cd4_min\n" +
            "          AND r.cleaned_lastcd4count < co_arg.cd4_max\n" +
            "    WHERE  r.is_restart AND r.verified\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  r.cleaned_lastcd4count IS NOT NULL AND r.age > 4\n" +
            "    GROUP  BY r.org_unit, r.attrib, r.period, r.gender, r.age,\n" +
            "              co_arg.cd4_min, co_arg.cd4_max\n" +
            "),\n" +
            "agg_rtt_nocd4 AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_restart AND r.verified\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  (r.age BETWEEN 0 AND 4 OR (r.age > 4 AND r.cleaned_lastcd4count IS NULL))\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_ml_died AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age,\n" +
            "           r.cleaned_causeofdeath, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_died AND r.verified\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  (r.prev_active\n" +
            "            OR ((r.previousstatus = '' OR r.previousstatus IS NULL)\n" +
            "                 AND r.artstartdate BETWEEN p.eop_7d AND p.eop))\n" +
            "      AND  r.previousstatusdate < p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age, r.cleaned_causeofdeath\n" +
            "),\n" +
            "agg_ml_iit AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age,\n" +
            "           (p.eop - r.artstartdate) AS iit_gap, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_iit AND r.prev_active AND r.verified\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop AND  r.previousstatusdate < p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age, (p.eop - r.artstartdate)\n" +
            "),\n" +
            "agg_ml_died_prev AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_died AND r.prev_active AND r.verified\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop AND  r.previousstatusdate < p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "\n" +
            "agg_ml_transfer AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_transfer_out AND r.prev_active AND r.verified\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop AND  r.previousstatusdate < p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_ml_custom AS (\n" +
            "    SELECT r.apprcode org_unit, r.attributecombo attrib, r.period, r.gender, r.age,\n" +
            "           r.currentstatus, COUNT(*) AS cnt\n" +
            "    FROM   public.radet_table r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.previousstatus ILIKE '%ACTIVE%'\n" +
            "      AND  (r.clientverificationoutcome ILIKE 'valid%'\n" +
            "            OR r.clientverificationoutcome = '' OR r.clientverificationoutcome IS NULL)\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop AND  r.previousstatusdate < p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age, r.currentstatus\n" +
            "),\n" +
            "agg_rtt_iit_dur AS (\n" +
            "    SELECT org_unit, attrib, period,\n" +
            "           (r.currentstatusdate - r.previousstatusdate) AS gap, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_restart AND r.verified\n" +
            "      AND  r.currentstatusdate BETWEEN p.eop_7d AND p.eop\n" +
            "    GROUP  BY org_unit, attrib, period,\n" +
            "              (r.currentstatusdate - r.previousstatusdate)\n" +
            "),\n" +
            "agg_tx_curr_mmd AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age,\n" +
            "           r.monthsofarvrefill, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.monthsofarvrefill IS NOT NULL\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age, r.monthsofarvrefill\n" +
            "),\n" +
            "agg_tb_d_old_pos AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.artstartdate < p.eop_6m\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  ((r.tbtreatmentstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "             AND r.tbstatus ILIKE '%Currently on TB treatment%')\n" +
            "            OR r.tbstatus ILIKE ANY (ARRAY['%Presumptive TB%',\n" +
            "                                           '%Presumptive TB and referred for evaluation%',\n" +
            "                                           '%Confirmed TB%']))\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_d_new_pos AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.artstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  ((r.tbtreatmentstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "             AND r.tbstatus ILIKE '%Currently on TB treatment%')\n" +
            "            OR r.tbstatus ILIKE ANY (ARRAY['%Presumptive TB%',\n" +
            "                                           '%Presumptive TB and referred for evaluation%',\n" +
            "                                           '%Confirmed TB%']))\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_d_old_neg AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.artstartdate < p.eop_6m\n" +
            "      AND  r.tbstatus ILIKE ANY (ARRAY['%No Sign%','%Currently on TPT%'])\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_d_new_neg AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.artstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  (r.tbstatus ILIKE '%No Sign%' OR r.tbstatus ILIKE '%Currently on TPT%')\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_spec_ret AS (\n" +
            "    SELECT org_unit, attrib, period, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  r.dateodtbscreened         BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbsamplecollection BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbdiagnosticresultreceived BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.tbstatus ILIKE ANY (ARRAY['%Presumptive TB%','%Confirmed TB%','%Currently on TB treatment%'])\n" +
            "      AND  (r.tbdiagnostictesttype IS NOT NULL AND r.tbdiagnostictesttype != '')\n" +
            "      AND  r.cleaned_tbdiagnosticresult_interpretation ILIKE ANY (ARRAY['%Positive%'])\n" +
            "    GROUP  BY org_unit, attrib, period\n" +
            "),\n" +
            "agg_tb_spec_sent AS (\n" +
            "    SELECT org_unit, attrib, period, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  r.dateodtbscreened         BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbsamplecollection BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.tbstatus ILIKE ANY (ARRAY['%presumptive%','%Confirmed TB%','%Currently on TB treatment%'])\n" +
            "    GROUP  BY org_unit, attrib, period\n" +
            "),\n" +
            "agg_tb_test_type AS (\n" +
            "    SELECT org_unit, attrib, period, r.tbdiagnostictesttype, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened         BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbsamplecollection BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbdiagnosticresultreceived BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.tbstatus ILIKE ANY (ARRAY['%presumptive%','%Confirmed TB%','%Currently on TB treatment%'])\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  r.cleaned_tbdiagnosticresult_interpretation ILIKE ANY (ARRAY['%Positive%','%Negative%'])\n" +
            "      AND  r.tbdiagnostictesttype IS NOT NULL\n" +
            "    GROUP  BY org_unit, attrib, period, r.tbdiagnostictesttype\n" +
            "),\n" +
            "agg_cxca_scrn AS (\n" +
            "    SELECT org_unit, attrib, period, age,\n" +
            "           r.cervicalcancerscreeningtype, r.resultofcervicalcancerscreening,\n" +
            "           COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcervicalcancerscreening BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  r.gender = 'Female'\n" +
            "      AND  (r.cervicalcancerscreeningmethod IS NOT NULL\n" +
            "            AND r.cervicalcancerscreeningmethod != '')\n" +
            "    GROUP  BY org_unit, attrib, period, age,\n" +
            "              r.cervicalcancerscreeningtype, r.resultofcervicalcancerscreening\n" +
            "),\n" +
            "agg_cxca_tx AS (\n" +
            "    SELECT org_unit, attrib, period, age,\n" +
            "           r.cervicalcancerscreeningtype, r.cervicalcancertreatmentscreened,\n" +
            "           COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcervicalcancerscreening BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  (r.cervicalcancerscreeningmethod IS NOT NULL\n" +
            "            AND r.cervicalcancerscreeningmethod != '')\n" +
            "      AND  r.treatmentmethoddate BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  r.gender = 'Female'\n" +
            "      AND  r.resultofcervicalcancerscreening ILIKE '%Positive%'\n" +
            "    GROUP  BY org_unit, attrib, period, age,\n" +
            "              r.cervicalcancerscreeningtype, r.cervicalcancertreatmentscreened\n" +
            "),\n" +
            "agg_tb_prev_d_new AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_any_status AND r.verified\n" +
            "      AND  r.dateofiptstart BETWEEN p.eop_12m AND p.eop_6m\n" +
            "      AND  r.artstartdate   BETWEEN p.eop_12m AND p.eop_6m\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_prev_d_ex AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_any_status AND r.verified\n" +
            "      AND  r.dateofiptstart BETWEEN p.eop_12m AND p.eop_6m\n" +
            "      AND  r.artstartdate  <= p.eop_12m\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_prev_n_new AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_any_status AND r.verified\n" +
            "      AND  r.dateofiptstart BETWEEN p.eop_12m AND p.eop_6m\n" +
            "      AND  r.artstartdate   BETWEEN p.eop_12m AND p.eop_6m\n" +
            "      AND  ((r.ipttype NOT ILIKE '%3HP%' AND r.iptcompletiondate BETWEEN p.eop_6m AND p.eop)\n" +
            "            OR (r.ipttype ILIKE '%3HP%' AND r.iptcompletiondate BETWEEN p.eop_9m AND p.eop))\n" +
            "      AND  (r.iptcompletionstatus ILIKE '%completed%' OR r.iptcompletionstatus ILIKE '%success%')\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_prev_n_ex AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_any_status AND r.verified\n" +
            "      AND  r.dateofiptstart BETWEEN p.eop_12m AND p.eop_6m\n" +
            "      AND  r.artstartdate  <= p.eop_12m\n" +
            "      AND  ((r.ipttype NOT ILIKE '%3HP%' AND r.iptcompletiondate BETWEEN p.eop_6m AND p.eop)\n" +
            "            OR (r.ipttype ILIKE '%3HP%' AND r.iptcompletiondate BETWEEN p.eop_9m AND p.eop))\n" +
            "      AND  (r.iptcompletionstatus ILIKE '%completed%' OR  r.iptcompletionstatus ILIKE '%success%')\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tx_new_preg AS (\n" +
            "    SELECT org_unit, attrib, period, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.verified\n" +
            "      AND  r.pregnancystatus ILIKE '%Breastfeeding%'\n" +
            "      AND  r.pregnancystatus NOT ILIKE '%NOT%'\n" +
            "      AND  r.artstartdate BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  r.careentry NOT ILIKE '%Transfer-in%'\n" +
            "    GROUP  BY org_unit, attrib, period\n" +
            "),\n" +
            "agg_pvls_d_preg AS (\n" +
            "    SELECT org_unit, attrib, period, r.pregnancystatus, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcurrentviralload    BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  r.dateofcurrentviralloadsample BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "      AND  r.gender = 'Female'\n" +
            "      AND  (r.pregnancystatus NOT ILIKE '%NOT%'  OR r.pregnancystatus != '' OR r.pregnancystatus IS NOT NULL)\n" +
            "    GROUP  BY org_unit, attrib, period, r.pregnancystatus\n" +
            "),\n" +
            "agg_pvls_n_preg AS (\n" +
            "    SELECT org_unit, attrib, period, r.pregnancystatus, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcurrentviralload    BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  r.dateofcurrentviralloadsample BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "      AND  r.cleaned_currentviralload < 1000\n" +
            "      AND  r.cleaned_currentviralload IS NOT NULL\n" +
            "      AND  r.gender = 'Female'\n" +
            "      AND  (r.pregnancystatus NOT ILIKE '%NOT%' OR r.pregnancystatus != '' OR r.pregnancystatus IS NOT NULL)\n" +
            "    GROUP  BY org_unit, attrib, period, r.pregnancystatus\n" +
            "),\n" +
            "agg_tb_d_scrn_type AS (\n" +
            "    SELECT org_unit, attrib, period, r.tbscreeningtype, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  ((r.tbtreatmentstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "             AND r.tbstatus ILIKE '%Currently on TB treatment%')\n" +
            "            OR r.tbstatus ILIKE ANY (ARRAY['%Presumptive TB%',\n" +
            "                                           '%Presumptive TB and referred for evaluation%',\n" +
            "                                           '%Confirmed TB%','%Currently on TPT%',\n" +
            "                                           '%No signs or symptoms of TB%']))\n" +
            "    GROUP  BY org_unit, attrib, period, r.tbscreeningtype\n" +
            "),\n" +
            "agg_tx_curr_dsd AS (\n" +
            "    SELECT org_unit, attrib, period, r.modeldevolveto, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.modeldevolveto IS NOT NULL AND r.modeldevolveto != ''\n" +
            "    GROUP  BY org_unit, attrib, period, r.modeldevolveto\n" +
            "),\n" +
            "agg_pvls_d_dsd AS (\n" +
            "    SELECT org_unit, attrib, period, r.modeldevolveto, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcurrentviralload    BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  r.dateofcurrentviralloadsample BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "      AND  r.modeldevolveto IS NOT NULL AND r.modeldevolveto != ''\n" +
            "    GROUP  BY org_unit, attrib, period, r.modeldevolveto\n" +
            "),\n" +
            "agg_pvls_n_dsd AS (\n" +
            "    SELECT org_unit, attrib, period, r.modeldevolveto, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateofcurrentviralload    BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  r.dateofcurrentviralloadsample BETWEEN p.eop_12m AND p.eop\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "      AND  r.cleaned_currentviralload < 1000\n" +
            "      AND  r.cleaned_currentviralload IS NOT NULL\n" +
            "      AND  r.modeldevolveto IS NOT NULL AND r.modeldevolveto != ''\n" +
            "    GROUP  BY org_unit, attrib, period, r.modeldevolveto\n" +
            "),\n" +
            "agg_tx_ever AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    WHERE  r.is_any_status AND r.verified\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_pvls_elig AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_pvls_sample AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  (p.eop - r.artstartdate) >= 180\n" +
            "      AND  r.dateofviralloadsamplecollection BETWEEN p.eop_12m AND p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_vl_sample_wk AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.vleligibilitystatus IS TRUE\n" +
            "      AND  r.dateofvleligibilitystatus      BETWEEN p.eop_7d AND p.eop\n" +
            "      AND  r.dateofviralloadsamplecollection BETWEEN p.eop_7d AND p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_vl_elig_wk AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.vleligibilitystatus IS TRUE\n" +
            "      AND  r.dateofvleligibilitystatus BETWEEN p.eop_7d AND p.eop\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_n_old AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened         BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbsamplecollection BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbdiagnosticresultreceived BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.artstartdate < p.eop_6m\n" +
            "      AND  r.tbtreatmentstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.tbstatus ILIKE ANY (ARRAY['%presumptive%','%Confirmed TB%','%Currently on TB treatment%'])\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  r.cleaned_tbdiagnosticresult_interpretation ILIKE ANY (ARRAY['%Positive%'])\n" +
            "      AND  r.tbdiagnostictesttype IS NOT NULL\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "agg_tb_n_new AS (\n" +
            "    SELECT org_unit, attrib, period, gender, age, COUNT(*) AS cnt\n" +
            "    FROM   radet_flags r\n" +
            "    CROSS  JOIN params p\n" +
            "    WHERE  r.is_active AND r.verified\n" +
            "      AND  r.dateodtbscreened         BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbsamplecollection BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.dateoftbdiagnosticresultreceived BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.artstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.tbtreatmentstartdate BETWEEN p.eop_6m AND p.eop\n" +
            "      AND  r.tbstatus ILIKE ANY (ARRAY['%presumptive%','%Confirmed TB%','%Currently on TB treatment%'])\n" +
            "      AND  (r.tbscreeningtype IS NOT NULL AND r.tbscreeningtype != '')\n" +
            "      AND  r.cleaned_tbdiagnosticresult_interpretation ILIKE ANY (ARRAY['%Positive%'])\n" +
            "      AND  r.tbdiagnostictesttype IS NOT NULL\n" +
            "    GROUP  BY org_unit, attrib, period, gender, age\n" +
            "),\n" +
            "\n" +
            "ou_period AS (\n" +
            "    SELECT DISTINCT apprcode AS org_unit, attributecombo AS attrib, period, ipname, facilityname, state, lga\n" +
            "    FROM   public.radet_table\n" +
            "),\n" +
            "final AS (\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'lO30dVqdhrL'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tx_curr a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'HJtyGifV4OI'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tx_new_cd4 a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "          AND  a.cd4_min = co.cd4_min AND a.cd4_max = co.cd4_max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'HJtyGifV4OI'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tx_new_nocd4 a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'r9nGYnFbT51'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_d a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'jxaQYJGzpfI'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_n a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'toiOIKgtle8'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_rtt_cd4 a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "          AND  a.cd4_min = co.cd4_min AND a.cd4_max = co.cd4_max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'toiOIKgtle8'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_rtt_nocd4 a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'BLLyjf1RlJA'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_ml_died a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "          AND  a.cleaned_causeofdeath ILIKE co.cause_of_death\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'm80DW9SjvsC'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_ml_iit a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "          AND  a.iit_gap BETWEEN co.iit_days_min AND co.iit_days_max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'Ot4j5cD437l'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_rtt_iit_dur a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.gap BETWEEN co.iit_days_min AND co.iit_days_max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'NEqrzFHDI1U'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tx_curr_mmd a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "          AND  a.monthsofarvrefill BETWEEN co.months_min AND co.months_max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'Wve38eJ8kgk'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_d_old_pos a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'Wve38eJ8kgk'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_d_new_pos a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'Wve38eJ8kgk'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_d_old_neg a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'Wve38eJ8kgk'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_d_new_neg a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'FQwNvHS0V66'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_spec_ret a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'z0VVeKuUIPs'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_spec_sent a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'MbabSqlSAcX'\n" +
            "    JOIN   public.category_option co\n" +
            "           ON  co.data_element_id = de.id\n" +
            "          AND  co.tb_test_type NOT ILIKE '%Other%'\n" +
            "    LEFT   JOIN agg_tb_test_type a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.tbdiagnostictesttype ILIKE co.tb_test_type\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'MbabSqlSAcX'\n" +
            "    JOIN   public.category_option co\n" +
            "           ON  co.data_element_id = de.id\n" +
            "          AND  co.tb_test_type ILIKE '%Other%'\n" +
            "    LEFT   JOIN agg_tb_test_type a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.tbdiagnostictesttype NOT ILIKE '%Smear%'\n" +
            "          AND  a.tbdiagnostictesttype NOT ILIKE '%Xpert%'\n" +
            "          AND  a.tbdiagnostictesttype NOT ILIKE '%Tru%'\n" +
            "          AND  a.tbdiagnostictesttype NOT ILIKE '%ray%'\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'cW5jgBzL3pX'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_cxca_scrn a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "          AND  a.cervicalcancerscreeningtype ILIKE co.screening_type\n" +
            "          AND  a.resultofcervicalcancerscreening ILIKE co.screening_result\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'ixfUm3GsTQP'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_cxca_tx a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "          AND  a.cervicalcancerscreeningtype ILIKE co.screening_type\n" +
            "          AND  a.cervicalcancertreatmentscreened ILIKE co.treatment_type\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'k2evNoBFHBL'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_prev_d_new a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'k2evNoBFHBL'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_prev_d_ex a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'oIVpdKLVIMq'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_prev_n_new a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'oIVpdKLVIMq'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_prev_n_ex a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'cPbjkxXC9yC'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tx_new_preg a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'tR0IfJwY6MI'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_d_preg a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.pregnancystatus ILIKE co.screening_result\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'aJZGdGpHOyp'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_n_preg a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.pregnancystatus ILIKE co.screening_result\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'CKCzNdE83RT'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_d_scrn_type a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.tbscreeningtype ILIKE co.screening_type\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'Rt2xSEinPkL'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tx_curr_dsd a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.modeldevolveto ILIKE co.dsd_model\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'vnirlTOs11i'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_d_dsd a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.modeldevolveto ILIKE co.dsd_model\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'hHQll2DSXM1'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_n_dsd a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  a.modeldevolveto ILIKE co.dsd_model\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'ibtXLuhXoXc'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tx_ever a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'jKZLTQPumKx'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_elig a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'prLqOcJY87r'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_pvls_sample a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'eJmIb96Ee4W'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_vl_sample_wk a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'iZIUDkhuxp4'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_vl_elig_wk a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'vyQrzD6QrWv'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_n_old a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    UNION ALL\n" +
            "\n" +
            "    SELECT de.data_element_uid, de.data_element AS data_element_name,\n" +
            "           op.period, op.org_unit, op.attrib, co.category_option_uid,\n" +
            "           COALESCE(SUM(a.cnt), 0) AS value, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            "    FROM   ou_period op\n" +
            "    JOIN   public.data_element de   ON de.data_element_uid = 'vyQrzD6QrWv'\n" +
            "    JOIN   public.category_option co ON co.data_element_id = de.id\n" +
            "    LEFT   JOIN agg_tb_n_new a\n" +
            "           ON  a.org_unit = op.org_unit AND a.attrib = op.attrib AND a.period = op.period\n" +
            "          AND  UPPER(a.gender) = UPPER(co.sex)\n" +
            "          AND  a.age BETWEEN co.min AND co.max\n" +
            "    GROUP  BY de.data_element_uid, de.data_element, op.period,\n" +
            "              op.org_unit, op.attrib, co.category_option_uid, op.facilityname, op.state, op.lga, ipname, co.category_option\n" +
            ")\n" +
            "SELECT\n" +
            "    data_element_uid    AS dataElement,\n" +
            "    period,\n" +
            "    org_unit            AS orgUnit,\n" +
            "    category_option_uid AS categoryOptionCombo,\n" +
            "    SUM(value)          AS value,\n" +
            "    attrib              AS attributeOptionCombo,\n" +
            "data_element_name AS data_element_name, category_option AS categoryOptionName,\n" +
            "facilityname, state, lga, ipname\n" +
            "FROM  final \n" +
            "GROUP BY data_element_uid, period,\n" +
            "org_unit, category_option_uid, attrib, facilityname, state, lga, ipname, data_element_name, category_option\n" +
            "ORDER BY org_unit, data_element_uid, category_option_uid";
}
