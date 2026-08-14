package org.lamisplus.modules.report.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.domain.HtsMsfProjection;
import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
import org.lamisplus.modules.report.domain.dto.ReportSectionDTO;
import org.lamisplus.modules.report.domain.entity.Month;
import org.lamisplus.modules.report.repository.MonthRepository;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.lamisplus.modules.report.utility.BaseInformation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builds the HTS 004 ReportProjectionDTO: fetches HtsMsfProjection rows for
 * one facility/month, then turns them into sections via
 * ProjectionRowAssembler. This is the ONLY report-specific piece - which
 * sections exist, what they're titled, and how each one filters the raw
 * data. Rendering (Excel/PDF) is entirely GenericExcelReportService /
 * GenericPdfReportService, unchanged and untouched here.
 *
 * The section list below is data, not code - adding a new HTS section is
 * one more SPECS entry, not a new method, the same way SectionLayoutRegistry
 * treats layouts as data rather than branches.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HtsReportBuilderService {

    private static final String REPORT_TITLE = "NATIONAL HTS MONTHLY SUMMARY FORM         HTS 004";

    /**
     * One entry per section on the form: display title, the layout it
     * renders with (matches a SectionLayoutRegistry key or falls back to
     * auto-inferred columns), which "section" value in the raw projection
     * data it pulls from, and an optional testResult filter.
     */
    private static final List<SectionSpec> SPECS = Arrays.asList(
            new SectionSpec(
                    "Number of people who tested HIV negative and received their results",
                    "AGE_GROUP", "RESULTS_BY_AGE", "Negative"),
            new SectionSpec(
                    "Number of people who tested HIV positive and received their results",
                    "AGE_GROUP", "RESULTS_BY_AGE", "Positive"),
            new SectionSpec(
                    "Number of newly diagnosed HIV-positive persons with Acute HIV Infection",
                    "ACUTE_HIV", "ACUTE_HIV", "Suspected Acute Infection"),
            new SectionSpec(
                    "Number of newly diagnosed HIV-positive persons tested for recency",
                    "RECENCY_RESULT", "RECENCY_RESULT", null),
            new SectionSpec(
                    "Number of persons at high risk of HIV infection who were tested for HIV and received their results",
                    "KEY_POPULATION", "KEY_POPULATION", null),
            new SectionSpec(
                    "Number of HIV-positive clients offered and accepted index testing",
                    "INDEX_POSITIVE", "INDEX_POSITIVE", null),
            new SectionSpec(
                    "Number of elicited contacts of index clients who received HTS and received their test results",
                    "INDEX_PARTNER", "INDEX_PARTNER", null),
            new SectionSpec(
                    "Number of elicited contacts of index clients who received HTS and received their test results",
                    "HIVST", "HIVST", null),
            new SectionSpec(
                    "Number of individuals reporting reactive HIVST results",
                    "HIVST_RESULT", "HIVST_RESULT", null),
            new SectionSpec(
                    "Number of individuals reporting reactive HIVST results linked to HTS for confirmatory test and received their results",
                    "HIVST_RESULT_LINKED", "HIVST_RESULT_LINKED", null)
    );

    private final ReportRepository reportRepository;
    private final BaseInformation baseInformation;
    private final MonthRepository monthRepository;
    private final ProjectionRowAssembler assembler;

    public ReportProjectionDTO build(Long facilityId, String month) {

        Month monthSelected = monthRepository.findByMonth(month)
                .orElseThrow(() -> new RuntimeException("Month not found"));

        LocalDate startDate = monthSelected.getStart();
        LocalDate endDate = monthSelected.getEnd();

        List<HtsMsfProjection> records = reportRepository.findTestingResults(facilityId, startDate, endDate);
        LOG.info("HTS MSF: {} raw rows for facility {} / {}", records.size(), facilityId, month);

        return ReportProjectionDTO.builder()
                .reportTitle(REPORT_TITLE)
                .reportingPeriod(startDate.getMonth().name() + " " + startDate.getYear())
                .facilityName(baseInformation.getFacilityName(facilityId))
                .state(baseInformation.getStateName(facilityId))
                .lga(baseInformation.getLocalGovernmentName(facilityId))
                .sections(buildSections(records))
                .build();
    }

    private List<ReportSectionDTO> buildSections(List<HtsMsfProjection> records) {
        return SPECS.stream()
                .map(spec -> assembler.buildSection(
                        spec.title,
                        spec.layoutType,
                        assembler.filter(records, spec.dataSection, spec.testResult)))
                .collect(Collectors.toList());
    }

    @AllArgsConstructor
    private static class SectionSpec {
        final String title;
        final String layoutType;
        final String dataSection;
        final String testResult; // nullable
    }
}
