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
        register("RECENCY_RESULT", recencyLayout());
        register("ACUTE_HIV", acuteHivLayout());
        register("KEY_POPULATION", keyPopulationLayout());
        register("INDEX_POSITIVE", indexHivLayout());
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
                        group("Out-patient", "outM", "outF"),
                        group("Others", "othersM", "othersF"),
                        single("Pregnant Women", "pregnantF"),
                        group("Community Others", "commM", "commF"),
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
                        group("Out-patient", "outM", "outF"),
                        group("Others", "othersM", "othersF"),
                        single("Pregnant Women", "pregnantF"),
                        group("Community Others", "commM", "commF"),
                        group("Total", "totalM", "totalF")
                ))
                .build();
    }

    private static SectionLayoutDef acuteHivLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Acute HIV Infection")
                .columnGroups(Arrays.asList(
                        group("In-patient", "inpatientM", "inpatientM"),
                        group("CT", "ctM", "ctF"),
                        group("Out-patient", "outM", "outF"),
                        group("Others", "othersM", "othersF"),
                        single("Pregnant Women", "pregnantF"),
                        group("Community Others", "commM", "commF"),
                        group("Total", "totalM", "totalF")
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
                        single("Male", "male"),
                        single("Female", "female")
                ))
                .build();
    }

    private static SectionLayoutDef indexPartnerLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Result")
                .columnGroups(Arrays.asList(
                        group("Biological", "maleBio", "femaleBio"),
                        group("Partner", "malePartner", "femalePartner"),
                        group("Social", "maleSocial", "femaleSocial"),
                        single("Total", "totalIndex")
                ))
                .build();
    }

    private static SectionLayoutDef hivstLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Entry Point")
                .columnGroups(Arrays.asList(
                        group("Self", "maleSelf", "femaleSelf"),
                        group(" Partner", "malePartner", "femalePartner"),
                        group("Caregiver", "maleCaregiver", "femaleCaregiver"),
                        group("Social Network", "maleSocial", "femaleSocial"),
                        single("Total", "totalHivst")
                ))
                .build();
    }

    private static SectionLayoutDef hivstReactiveLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Indicator")
                .columnGroups(Arrays.asList(
                        single("<15", "hivstLess"),
                        single("15+", "hivstGreater"),
                        single("Total", "totalHivSt")
                ))
                .build();
    }

    private static SectionLayoutDef hivstResultLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Result Indicator")
                .columnGroups(Arrays.asList(
                        single("<15", "less15"),
                        single("15+", "gt15"),
                        single("Total", "total")
                ))
                .build();
    }
}
