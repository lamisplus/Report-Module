package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.dto.SectionLayoutDef;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

import static org.lamisplus.modules.report.service.SectionLayouts.*;

@Component
@RequiredArgsConstructor
public class PMTCTSectionLayouts {

    private final SectionLayoutRegistry registry;

    @PostConstruct
    void register() {
        registry.register("ANC", ancBreakdownLayout("Number of  new ANC Clients"));
        registry.register("HIV", hivBreakdownLayout(""));
        registry.register("SYPHILIS", syphilisLayout());
        registry.register("HEPATITIS_B", hepatitisBLayout());
        registry.register("ACUTE", suspectedAcuteLayout());
        registry.register("RETEST", retestingLayout());
        registry.register("SPOKE", spokeBreakdownLayout(""));
        registry.register("PREP", prepLayout());
        registry.register("ART_TIMING", arttimingvBreakdownLayout(""));
        registry.register("Syphilis_treated", treatedSyphilisBreakdownLayout(""));
        registry.register("HBV", hbvBreakdownLayout(""));
        registry.register("VIRALOGIC", viralogicBreakdownLayout(""));
        registry.register("DELIVERY", deliveryBreakdownLayout(""));
        registry.register("EXPOSED_INFANT", exposeInfantBreakdownLayout(""));
        registry.register("PCR_SAMPLE", pcrSampleBreakdownLayout(""));
        registry.register("DNA_PCR", dnaPcrBreakdownLayout(""));
        registry.register("INFANT_OUTCOME", infantOutcomeBreakdownLayout(""));
//        registry.register("PREP_FOLLOWUP", populationPrepFollowUpBreakdownLayout("Population Type"));
//        registry.register("PREP_TYPE_FOLLOWUP", prepFollowUpTypeLayout());
//        registry.register("PREP_SEROCONVERTED", populationPrepSeroBreakdownLayout("Population Type"));
//        registry.register("PREP_TYPE_SEROCONVERTED", prepSeroTypeLayout());
//        registry.register("PEP_FOLLOWUP", populationPepFollowUpBreakdownLayout("Mode of Exposure"));
//        registry.register("PEP_VIRAL_LOAD", populationPepViralLoadBreakdownLayout("Viral Load Result"));

    }

    private static SectionLayoutDef ancBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("Antenatal Care Summary (Source: NHMIS ANC Register)",
                                col("< 15", "less_15"),
                                col(">= 15", "greater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef spokeBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        single("Referred from the spoke sites", "referred_spoke")
                ))
                .build();
    }

    private static SectionLayoutDef treatedSyphilisBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("< 15", "less_15"),
                                col(">= 15", "greater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef hbvBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("< 15", "less_15"),
                                col(">= 15", "greater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef viralogicBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("< 15", "less_15"),
                                col(">= 15", "greater_15")),
                        single("Total", "total")
                ))
                .build();
    }
    private static SectionLayoutDef deliveryBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("Booked", "booked"),
                                col("Unbooked", "unbooked")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef exposeInfantBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("<= 72 Hrs", "within72hr"),
                                col("> 72 Hrs", "after72hr")),
                        single("Total", "total")
                ))
                .build();
    }


    private static SectionLayoutDef pcrSampleBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("At birth <= 72 hour", "sampleless_72hr"),
                                col("> 72 - <2 months", "sampleless_2month"),
                                col("2 - 12months", "sampleless_12month"),
                                col("> 12 months", "samplegreater_12month")),
                        single("Total", "totalsamples")
                ))
                .build();
    }


    private static SectionLayoutDef dnaPcrBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("At birth <= 72 hour", "less72hr"),
                                col("> 72 - <2 months", "less2month"),
                                col("2 - 12months", "less12month"),
                                col("> 12 months", "greater12month")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef infantOutcomeBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("",
                                col("Positive", "positive"),
                                col("Negative", "negative"),
                                col("Unknown", "unknown")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef hivBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader("HIV")
                .columnGroups(Arrays.asList(
                        group("ANC",
                                col("< 15", "anc_less_15"),
                                col(">= 15", "anc_greater_15")),
                        group("L & D",
                                col("< 15", "ld_less_15"),
                                col(">= 15", "ld_greater_15")),
                        group("Breastfeeding",
                                col("< 15", "natal_less_15"),
                                col(">= 15", "natal_greater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef syphilisLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("SYPHILIS")
                .columnGroups(Arrays.asList(
                        group("ANC",
                                col("< 15", "anc_less_15"),
                                col(">= 15", "anc_greater_15")),
                        group("L & D",
                                col("< 15", "ld_less_15"),
                                col(">= 15", "ld_greater_15")),
                        group("Breastfeeding",
                                col("< 15", "natal_less_15"),
                                col(">= 15", "natal_greater_15")),
                        single("Total", "total")
                ))
                .build();
    }
    private static SectionLayoutDef retestingLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("RETESTING")
                .columnGroups(Arrays.asList(
                        group("ANC",
                                col("< 15", "anc_less_15"),
                                col(">= 15", "anc_greater_15")),
                        group("L & D",
                                col("< 15", "ld_less_15"),
                                col(">= 15", "ld_greater_15")),
                        group("Breastfeeding",
                                col("< 15", "natal_less_15"),
                                col(">= 15", "natal_greater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef prepLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("")
                .columnGroups(Arrays.asList(
                        group("Pregnancy Status",
                                col("Pregnant", "Pregnant"),
                                col("Breastfeeding", "Breastfeeding"))
                ))
                .build();
    }

    private static SectionLayoutDef suspectedAcuteLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("Suspected acute HIV infection")
                .columnGroups(Arrays.asList(
                        group("ANC",
                                col("< 15", "anc_less_15"),
                                col(">= 15", "anc_greater_15")),
                        group("L & D",
                                col("< 15", "ld_less_15"),
                                col(">= 15", "ld_greater_15")),
                        group("Breastfeeding",
                                col("< 15", "natal_less_15"),
                                col(">= 15", "natal_greater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef hepatitisBLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("HEPATITIS B")
                .columnGroups(Arrays.asList(
                        group("ANC",
                                col("< 15", "anc_less_15"),
                                col(">= 15", "anc_greater_15")),
                        group("L & D",
                                col("< 15", "ld_less_15"),
                                col(">= 15", "ld_greater_15")),
                        group("Breastfeeding",
                                col("< 15", "natal_less_15"),
                                col(">= 15", "natal_greater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef arttimingvBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader("")
                .columnGroups(Arrays.asList(
                        group("Art timing",
                                col("< 15", "timeless_15"),
                                col(">= 15", "timegreater_15")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef populationPrepFollowUpBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("Male",
                                col("15-24", "male15_24"),
                                col("25-49", "male25_49"),
                                col("50+", "male50")),
                        group("Female",
                                col("15-24", "female15_24"),
                                col("25-49", "female25_49"),
                                col("50+", "female50")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef populationPrepSeroBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("Male",
                                col("15-24", "male15_24"),
                                col("25-49", "male25_49"),
                                col("50+", "male50")),
                        group("Female",
                                col("15-24", "female15_24"),
                                col("25-49", "female25_49"),
                                col("50+", "female50")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef prepFollowUpTypeLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("PrEP Type")
                .columnGroups(Arrays.asList(
                        group("Male",
                                col("15-24", "male15_24"),
                                col("25-49", "male25_49"),
                                col("50+", "male50")),
                        group("Female",
                                col("15-24", "female15_24"),
                                col("25-49", "female25_49"),
                                col("50+", "female50")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef prepSeroTypeLayout() {
        return SectionLayoutDef.builder()
                .rowLabelHeader("PrEP Type")
                .columnGroups(Arrays.asList(
                        group("Male",
                                col("15-24", "male15_24"),
                                col("25-49", "male25_49"),
                                col("50+", "male50")),
                        group("Female",
                                col("15-24", "female15_24"),
                                col("25-49", "female25_49"),
                                col("50+", "female50")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef populationPepFollowUpBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("Male",
                                col("15-24", "male15_24"),
                                col("25-49", "male25_49"),
                                col("50+", "male50")),
                        group("Female",
                                col("15-24", "female15_24"),
                                col("25-49", "female25_49"),
                                col("50+", "female50")),
                        single("Total", "total")
                ))
                .build();
    }

    private static SectionLayoutDef populationPepViralLoadBreakdownLayout(String rowLabelHeader) {
        return SectionLayoutDef.builder()
                .rowLabelHeader(rowLabelHeader)
                .columnGroups(Arrays.asList(
                        group("Male",
                                col("15-24", "male15_24"),
                                col("25-49", "male25_49"),
                                col("50+", "male50")),
                        group("Female",
                                col("15-24", "female15_24"),
                                col("25-49", "female25_49"),
                                col("50+", "female50")),
                        single("Total", "total")
                ))
                .build();
    }

}