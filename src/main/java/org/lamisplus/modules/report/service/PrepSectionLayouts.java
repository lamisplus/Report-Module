// PrepSectionLayouts.java — PrEP's own layouts, same registry, no HTS coupling
package org.lamisplus.modules.report.service;

import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.dto.SectionLayoutDef;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static org.lamisplus.modules.report.service.SectionLayouts.*;

@Component
@RequiredArgsConstructor
public class PrepSectionLayouts {

    private final SectionLayoutRegistry registry;

    @PostConstruct
    void register() {
        registry.register("PREP_ELIGIBLE", populationBreakdownLayout("Population Type"));
        registry.register("PREP_NEW_INIT", populationBreakdownLayout("Population Type"));
        registry.register("PREP_TYPE", prepTypeLayout());
        registry.register("PREP_FOLLOWUP", populationPrepFollowUpBreakdownLayout("Population Type"));
        registry.register("PREP_TYPE_FOLLOWUP", prepFollowUpTypeLayout());
        registry.register("PREP_SEROCONVERTED", populationPrepSeroBreakdownLayout("Population Type"));
        registry.register("PREP_TYPE_SEROCONVERTED", prepSeroTypeLayout());
        registry.register("PEP_FOLLOWUP", populationPepFollowUpBreakdownLayout("Mode of Exposure"));
        registry.register("PEP_VIRAL_LOAD", populationPepViralLoadBreakdownLayout("Viral Load Result"));

    }

    // Shared by PREP_ELIGIBLE and PREP_NEW_INIT: both use the same
    // male15_24/male25_49/male50 + female15_24/female25_49/female50 + total
    // columns coming out of the SQL projection.
    private static SectionLayoutDef populationBreakdownLayout(String rowLabelHeader) {
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

    private static SectionLayoutDef prepTypeLayout() {
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