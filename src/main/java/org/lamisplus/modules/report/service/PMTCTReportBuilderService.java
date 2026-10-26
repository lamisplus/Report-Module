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
 * Builds the PMTCT Monthly Summary Form. The only report-specific
 * pieces are the title, the SPECS list, and which repository call fetches
 * raw rows - the month lookup / fetch / section-assembly sequence lives in
 * GenericReportBuilderService, shared with HtsReportBuilderService.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PMTCTReportBuilderService {

    private static final String REPORT_TITLE = "PMTCT Monthly Summary Form";
    private final BaseInformation baseInformation;
    private final MonthRepository monthRepository;
    private final ProjectionRowAssembler assembler;

    private static final List<SectionSpec> SPECS = Arrays.asList(
            new SectionSpec(
                    "1. ",
                    "ANC", "ANC", null),
            new SectionSpec(
                    "2. Number of pregnant and breastfeeding women with known Status (HIV, HBV, Syphilis) Only first contact with PMTCT during the current pregnancy",
                    "HIV", "HIV", null),
            new SectionSpec(
                    "",
                    "SYPHILIS", "SYPHILIS", null),
            new SectionSpec(
                    "",
                    "HEPATITIS_B", "HEPATITIS_B", null),
            new SectionSpec(
                    "3. Number of Pregnant and Breastfeeding women with suspected acute HIV infection",
                    "ACUTE", "ACUTE", null),
//            new SectionSpec(
//                    "PrEP Type",
//                    "PREP_TYPE_FOLLOWUP", "PREP_TYPE_FOLLOWUP", null),
            new SectionSpec(
                    "4. Number of HIV positive pregnant & breastfeeding women referred from the spoke sites, confirmed HIV positive at the comprehensive sites",
                    "SPOKE", "SPOKE", null),
//            new SectionSpec(
//                    "PrEP Type",
//                    "PREP_TYPE_SEROCONVERTED", "PREP_TYPE_SEROCONVERTED", null),
            new SectionSpec(
                    "5. Number of pregnant and breastfeeding women retested after initial HIV negative test",
                    "RETEST", "RETEST", null),
            new SectionSpec(
                    "6. Number of Pregnant and breastfeeding Women Initiated on PrEP",
                    "PREP", "PREP", null),
        new SectionSpec(
                    "7. Number of HIV positive pregnant and breastfeeding women on ART",
                    "ART_TIMING", "ART_TIMING", null),
            new SectionSpec(
                    "8. Number of Syphilis positive pregnant women who were treated for Syphilis",
                    "Syphilis_treated", "Syphilis_treated", null),
            new SectionSpec(
                    "9. Number of pregnant women living with HBV on HBV Prophylaxis/Treatment for PMTCT",
                    "HBV", "HBV", null),
            new SectionSpec(
                    "10. Number of pregnant and breastfeeding women on ART with Virologic Status",
                    "VIRALOGIC", "VIRALOGIC", null),
            new SectionSpec(
                    "11. Number of HIV positive pregnant women who delivered at facility",
                    "DELIVERY", "DELIVERY", null),
            new SectionSpec(
                    "12. Number of HIV-exposed infants (HEIs) who received ARV prophylaxis",
                    "EXPOSED_INFANT", "EXPOSED_INFANT", null),
            new SectionSpec(
                    "13. Number of HIV-exposed infants (HEIs) whose blood samples were taken for DNA PCR test",
                    "PCR_SAMPLE", "PCR_SAMPLE", null),
            new SectionSpec(
                    "14. Number of HIV PCR test results received for HIV-exposed infants",
                    "DNA_PCR", "DNA_PCR", null),
            new SectionSpec(
                    "15. Number of HIV-Exposed Children Aged 18 Months with Documented Final outcome Status",
                    "INFANT_OUTCOME", "INFANT_OUTCOME", null)
    );

    private final ReportRepository reportRepository;

    public ReportProjectionDTO build(Long facilityId, String month) {

        Month monthSelected = monthRepository.findByMonth(month)
                .orElseThrow(() -> new RuntimeException("Month not found"));

        LocalDate startDate = monthSelected.getStart();
        LocalDate endDate = monthSelected.getEnd();

        List<HtsMsfProjection> records = reportRepository.getPMTCTMSFReport(facilityId, startDate, endDate);
        LOG.info("PMTCT MSF: {} raw rows for facility {} / {}", records.size(), facilityId, month);

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
