// PrEPReportBuilderService.java — delegates to GenericReportBuilderService, PREP_TYPE uncommented
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
 * Builds the PrEP & PEP Monthly Summary Form. The only report-specific
 * pieces are the title, the SPECS list, and which repository call fetches
 * raw rows - the month lookup / fetch / section-assembly sequence lives in
 * GenericReportBuilderService, shared with HtsReportBuilderService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrEPReportBuilderService {

    private static final String REPORT_TITLE = "PrEP & PEP Monthly Summary Form";
    private final BaseInformation baseInformation;
    private final MonthRepository monthRepository;
    private final ProjectionRowAssembler assembler;

    private static final List<SectionSpec> SPECS = Arrays.asList(
            new SectionSpec(
                    "1. No. of individuals who were eligible for PrEP in the reporting month",
                    "PREP_ELIGIBLE", "PREP_ELIGIBLE", null),
            new SectionSpec(
                    "2. No. of individuals who were newly started on PrEP in the reporting month",
                    "PREP_NEW_INIT", "PREP_NEW_INIT", null),
            new SectionSpec(
                    "PrEP Type",
                    "PREP_TYPE", "PREP_TYPE", null),
            new SectionSpec(
                    "3. Number of individuals who received PrEP at least once in the reporting period",
                    "PREP_FOLLOWUP", "PREP_FOLLOWUP", null),
            new SectionSpec(
                    "PrEP Type",
                    "PREP_TYPE_FOLLOWUP", "PREP_TYPE_FOLLOWUP", null),
            new SectionSpec(
                    "4. Number of individuals who seroconverted in the reporting period",
                    "PREP_SEROCONVERTED", "PREP_SEROCONVERTED", null),
            new SectionSpec(
                    "PrEP Type",
                    "PREP_TYPE_SEROCONVERTED", "PREP_TYPE_SEROCONVERTED", null),
            new SectionSpec(
                    "5. Number of individuals who received PEP in the reporting period",
                    "PEP_FOLLOWUP", "PEP_FOLLOWUP", null),
            new SectionSpec(
                    "6. Number of Early Detect Viral Load Results received during the reporting period",
                    "PEP_VIRAL_LOAD", "PEP_VIRAL_LOAD", null)
    );

//    private final ReportRepository reportRepository;
//    private final GenericReportBuilderService genericBuilder;
//
//    public ReportProjectionDTO build(Long facilityId, String month) {
//        ReportDefinition definition = new ReportDefinition(
//                REPORT_TITLE,
//                SPECS,
//                (fid, start, end) -> reportRepository.findPrEPMsf(fid, start, end)
//        );
//        return genericBuilder.build(definition, facilityId, month);
//    }


    private final ReportRepository reportRepository;
//    private final BaseInformation baseInformation;
//    private final MonthRepository monthRepository;
//    private final ProjectionRowAssembler assembler;

    public ReportProjectionDTO build(Long facilityId, String month) {

        Month monthSelected = monthRepository.findByMonth(month)
                .orElseThrow(() -> new RuntimeException("Month not found"));

        LocalDate startDate = monthSelected.getStart();
        LocalDate endDate = monthSelected.getEnd();

        List<HtsMsfProjection> records = reportRepository.findPrEPMsf(facilityId, startDate, endDate);
        LOG.info("PrEP MSF: {} raw rows for facility {} / {}", records.size(), facilityId, month);

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
//package org.lamisplus.modules.report.service;
//
//import lombok.AllArgsConstructor;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.lamisplus.modules.report.domain.HtsMsfProjection;
//import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
//import org.lamisplus.modules.report.domain.dto.ReportSectionDTO;
//import org.lamisplus.modules.report.domain.entity.Month;
//import org.lamisplus.modules.report.repository.MonthRepository;
//import org.lamisplus.modules.report.repository.ReportRepository;
//import org.lamisplus.modules.report.utility.BaseInformation;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * Builds the HTS 004 ReportProjectionDTO: fetches HtsMsfProjection rows for
// * one facility/month, then turns them into sections via
// * ProjectionRowAssembler. This is the ONLY report-specific piece - which
// * sections exist, what they're titled, and how each one filters the raw
// * data. Rendering (Excel/PDF) is entirely GenericExcelReportService /
// * GenericPdfReportService, unchanged and untouched here.
// *
// * The section list below is data, not code - adding a new HTS section is
// * one more SPECS entry, not a new method, the same way SectionLayoutRegistry
// * treats layouts as data rather than branches.
// */
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class PrEPReportBuilderService {
//
//    private static final String REPORT_TITLE = "PrEP & PEP Monthly Summary Form";
//
//    /**
//     * One entry per section on the form: display title, the layout it
//     * renders with (matches a SectionLayoutRegistry key or falls back to
//     * auto-inferred columns), which "section" value in the raw projection
//     * data it pulls from, and an optional testResult filter.
//     */
//    private static final List<SectionSpec> SPECS = Arrays.asList(
//            new SectionSpec(
//                    "No. of individuals who were eligible for PrEP in the reporting month",
//                    "PREP_ELIGIBLE", "PREP_ELIGIBLE", ""),
//            new SectionSpec(
//                    "No. of individuals who were newly started on PrEP in the reporting month",
//                    "PREP_NEW_INIT", "PREP_NEW_INIT", "")
////            new SectionSpec(
////                    "PrEP Type",
////                    "ACUTE_HIV", "ACUTE_HIV", "Suspected Acute Infection"),
////            new SectionSpec(
////                    "Number of individuals who received PrEP at least once in the reporting period",
////                    "RECENCY_RESULT", "RECENCY_RESULT", null),
////            new SectionSpec(
////                    "PrEP Type",
////                    "KEY_POPULATION", "KEY_POPULATION", null),
////            new SectionSpec(
////                    "Number of individuals who seroconverted in the reporting period",
////                    "INDEX_POSITIVE", "INDEX_POSITIVE", null),
////            new SectionSpec(
////                    "PrEP Type",
////                    "INDEX_PARTNER", "INDEX_PARTNER", null),
////            new SectionSpec(
////                    "Number of individuals who received PEP in the reporting period",
////                    "HIVST", "HIVST", null),
////            new SectionSpec(
////                    "Number of Early Detect Viral Load Results received during the reporting period ",
////                    "HIVST_RESULT", "HIVST_RESULT", null)
//
//    );
//
//    private final ReportRepository reportRepository;
//    private final BaseInformation baseInformation;
//    private final MonthRepository monthRepository;
//    private final ProjectionRowAssembler assembler;
//
//    public ReportProjectionDTO build(Long facilityId, String month) {
//
//        Month monthSelected = monthRepository.findByMonth(month)
//                .orElseThrow(() -> new RuntimeException("Month not found"));
//
//        LocalDate startDate = monthSelected.getStart();
//        LocalDate endDate = monthSelected.getEnd();
//
//        List<HtsMsfProjection> records = reportRepository.findPrEPMsf(facilityId, startDate, endDate);
//        LOG.info("PrEP MSF: {} raw rows for facility {} / {}", records.size(), facilityId, month);
//
//        return ReportProjectionDTO.builder()
//                .reportTitle(REPORT_TITLE)
//                .reportingPeriod(startDate.getMonth().name() + " " + startDate.getYear())
//                .facilityName(baseInformation.getFacilityName(facilityId))
//                .state(baseInformation.getStateName(facilityId))
//                .lga(baseInformation.getLocalGovernmentName(facilityId))
//                .sections(buildSections(records))
//                .build();
//    }
//
//    private List<ReportSectionDTO> buildSections(List<HtsMsfProjection> records) {
//        return SPECS.stream()
//                .map(spec -> assembler.buildSection(
//                        spec.title,
//                        spec.layoutType,
//                        assembler.filter(records, spec.dataSection, spec.testResult)))
//                .collect(Collectors.toList());
//    }
//
//    @AllArgsConstructor
//    private static class SectionSpec {
//        final String title;
//        final String layoutType;
//        final String dataSection;
//        final String testResult; // nullable
//    }
//}
