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

    private final ReportRepository reportRepository;

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