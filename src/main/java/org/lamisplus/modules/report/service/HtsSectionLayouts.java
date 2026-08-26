// HtsSectionLayouts.java — HTS's own layouts, registered on startup, not baked into the registry
package org.lamisplus.modules.report.service;

import javax.annotation.PostConstruct; // or javax.annotation, matching your Spring version
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.dto.SectionLayoutDef;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

import static org.lamisplus.modules.report.service.SectionLayouts.*;

@Component
@RequiredArgsConstructor
public class HtsSectionLayouts {

    private final SectionLayoutRegistry registry;

    @PostConstruct
    void register() {
        registry.register("AGE_GROUP", ageGroupLayout());
        registry.register("RECENCY_RESULT", recencyLayout());
        registry.register("ACUTE_HIV", acuteHivLayout());
        registry.register("KEY_POPULATION", keyPopulationLayout());
        registry.register("INDEX_POSITIVE", indexHivLayout());
        registry.register("INDEX_PARTNER", indexPartnerLayout());
        registry.register("HIVST", hivstLayout());
        registry.register("HIVST_RESULT", hivstReactiveLayout());
        registry.register("HIVST_RESULT_LINKED", hivstResultLayout());
    }

    private static SectionLayoutDef ageGroupLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Age Group")
                .columnGroups(Arrays.asList(
                        mfGroup("In-patient", "inpatientM", "inpatientM"), // NOTE: likely a bug carried over - should probably be "inpatientF"
                        mfGroup("CT", "ctM", "ctF"),
                        mfGroup("Out-patient", "outM", "outF"),
                        mfGroup("Others", "othersM", "othersF"),
                        single("Pregnant Women", "pregnantF"),
                        mfGroup("Community Others", "commM", "commF"),
                        mfGroup("Total", "totalM", "totalF")
                ))
                .build();
    }

    private static SectionLayoutDef recencyLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Recency Result")
                .columnGroups(Arrays.asList(
                        mfGroup("In-patient", "inpatientM", "inpatientM"),
                        mfGroup("CT", "ctM", "ctF"),
                        mfGroup("Out-patient", "outM", "outF"),
                        mfGroup("Others", "othersM", "othersF"),
                        single("Pregnant Women", "pregnantF"),
                        mfGroup("Community Others", "commM", "commF"),
                        mfGroup("Total", "totalM", "totalF")
                ))
                .build();
    }

    private static SectionLayoutDef acuteHivLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Acute HIV Infection")
                .columnGroups(Arrays.asList(
                        mfGroup("In-patient", "inpatientM", "inpatientM"),
                        mfGroup("CT", "ctM", "ctF"),
                        mfGroup("Out-patient", "outM", "outF"),
                        mfGroup("Others", "othersM", "othersF"),
                        single("Pregnant Women", "pregnantF"),
                        mfGroup("Community Others", "commM", "commF"),
                        mfGroup("Total", "totalM", "totalF")
                ))
                .build();
    }

    private static SectionLayoutDef keyPopulationLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Result")
                .columnGroups(Arrays.asList(
                        single("MSM", "msm_f"),
                        mfGroup("PWID", "pwid_m", "pwid_f"),
                        mfGroup("Sex Worker", "sex_worker_m", "sex_worker_f"),
                        mfGroup("PPOCS", "ppocs_m", "ppocs_f"),
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
                        mfGroup("Biological", "maleBio", "femaleBio"),
                        mfGroup("Partner", "malePartner", "femalePartner"),
                        mfGroup("Social", "maleSocial", "femaleSocial"),
                        single("Total", "totalIndex")
                ))
                .build();
    }

    private static SectionLayoutDef hivstLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Entry Point")
                .columnGroups(Arrays.asList(
                        mfGroup("Self", "maleSelf", "femaleSelf"),
                        mfGroup(" Partner", "malePartner", "femalePartner"),
                        mfGroup("Caregiver", "maleCaregiver", "femaleCaregiver"),
                        mfGroup("Social Network", "maleSocial", "femaleSocial"),
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