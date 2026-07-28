package org.lamisplus.modules.report.service;


import org.lamisplus.modules.report.domain.dto.ColumnDef;
import org.lamisplus.modules.report.domain.dto.ColumnGroupDef;
import org.lamisplus.modules.report.domain.dto.SectionLayoutDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps ReportSectionDTO#sectionType -> SectionLayoutDef. This is what makes
 * GenericExcelReportService generic: adding a new report type is a call to
 * register(...) (e.g. from another module's @PostConstruct), not a new
 * writeXxxSection method.
 *
 * If a sectionType has no registered layout, the renderer falls back to
 * auto-detecting columns from whatever keys appear in that section's row
 * values - so even an unregistered report type renders reasonably, just
 * without curated group headers.
 */
@Component
public class SectionLayoutRegistry {

    private final Map<String, SectionLayoutDef> layouts = new LinkedHashMap<>();

    public SectionLayoutRegistry() {
        register("AGE_GROUP", ageGroupLayout());
        register("RECENCY", recencyLayout());
        register("ACUTE_HIV", acuteHivLayout());
        register("KEY_POPULATION", keyPopulationLayout());
        register("INDEX", indexHivLayout());
        register("INDEX_PARTNER", indexPartnerLayout());
        register("HIVST", hivstLayout());
        register("HIVST_RESULT", hivstReactiveLayout());
        register("HIVST_RESULT_LINKED", hivstResultLayout());
    }

    public void register(String sectionType, SectionLayoutDef layout) {
        layouts.put(sectionType.toUpperCase(), layout);
    }

    public SectionLayoutDef find(String sectionType) {
        return sectionType == null ? null : layouts.get(sectionType.toUpperCase());
    }

    private static ColumnGroupDef group(String label, String... keySuffixPairs) {
        // keySuffixPairs: maleKey, femaleKey
        return ColumnGroupDef.builder()
                .groupLabel(label)
                .columns(Arrays.asList(
                        ColumnDef.builder().header("M").key(keySuffixPairs[0]).build(),
                        ColumnDef.builder().header("F").key(keySuffixPairs[1]).build()
                ))
                .build();
    }

    private static ColumnGroupDef single(String header, String key) {
        return ColumnGroupDef.builder()
                .groupLabel(null)
                .columns(Collections.singletonList(ColumnDef.builder().header(header).key(key).build()))
                .build();
    }

    private static SectionLayoutDef ageGroupLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Age Group")
                .columnGroups(Arrays.asList(
                        group("In-patient", "inpatientM", "inpatientM"),
                        group("CT", "ctM", "ctF"),
                        group("Out-patient", "outpatient_m", "outpatient_f"),
                        group("Others", "othersM", "othersF"),
                        single("Pregnant Women", "pregnantWomen"),
                        group("Community Others", "communityM", "communityF"),
                        group("Total", "totalM", "totalF")
                ))
                .build();
    }

    private static SectionLayoutDef recencyLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Recency Result")
                .columnGroups(Arrays.asList(
                        group("In-patient", "inpatientM", "inpatientM"),
                        group("CT", "ctM", "ctF"),
                        group("Out-patient", "outpatient_m", "outpatient_f"),
                        group("Others", "othersM", "othersF"),
                        group("Pregnant Women", "pregnantWomen", "pregnantWomen"),
                        group("Community Others", "communityM", "communityF"),
                        group("Total", "totalM", "totalF")
                ))
                .build();
    }

    private static SectionLayoutDef acuteHivLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Indicator")
                .columnGroups(Arrays.asList(
                        single("Male", "total_m"),
                        single("Female", "total_f")
                ))
                .build();
    }

    private static SectionLayoutDef keyPopulationLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Result")
                .columnGroups(Arrays.asList(
                        single("MSM", "msm_f"), //change the variable
                        group("PWID", "pwid_m", "pwid_f"),
                        group("Sex Worker", "sex_worker_m", "sex_worker_f"),
                        group("PPOCS", "ppocs_m", "ppocs_f"),
                        single("AGYW", "agyw"),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef indexHivLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Indicator")
                .columnGroups(Arrays.asList(
                        single("Male", "total_m"),
                        single("Female", "total_f")
                ))
                .build();
    }

    private static SectionLayoutDef indexPartnerLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Result")
                .columnGroups(Arrays.asList(
                        group("Biological", "pwid_m", "pwid_f"),
                        group("Partner", "sex_worker_m", "sex_worker_f"),
                        group("Social", "ppocs_m", "ppocs_f"),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef hivstLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Entry Point")
                .columnGroups(Arrays.asList(
                        group("Self", "pwid_m", "pwid_f"),
                        group(" Partner", "sex_worker_m", "sex_worker_f"),
                        group("Caregiver", "ppocs_m", "ppocs_f"),
                        group("Social Network", "ppocs_m", "ppocs_f"),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef hivstReactiveLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Indicator")
                .columnGroups(Arrays.asList(
                        single("<15", "total_m"),
                        single("15+", "total_f")
                ))
                .build();
    }

    private static SectionLayoutDef hivstResultLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Indicator")
                .columnGroups(Arrays.asList(
                        single("<15", "total_m"),
                        single("15+", "total_f")
                ))
                .build();
    }
}
