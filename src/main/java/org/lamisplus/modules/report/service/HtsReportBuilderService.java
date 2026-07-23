package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.domain.HtsMsfProjection;
import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
import org.lamisplus.modules.report.domain.dto.ReportRowDTO;
import org.lamisplus.modules.report.domain.dto.ReportSectionDTO;
import org.lamisplus.modules.report.domain.entity.Month;
import org.lamisplus.modules.report.domain.entity.Period;
import org.lamisplus.modules.report.repository.MonthRepository;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.lamisplus.modules.report.utility.BaseInformation;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HtsReportBuilderService {

    private final ReportRepository reportRepository;
    private final BaseInformation baseInformation;
    private final MonthRepository monthRepository;



    public ReportProjectionDTO build(Long facilityId, String month) {

        Month monthSelected = monthRepository.findByMonth(month)
                .orElseThrow(() -> new RuntimeException("Month not found"));

        LocalDate startDate = monthSelected.getStart();
        LocalDate endDate   = monthSelected.getEnd();
        String facilityName = baseInformation.getFacilityName(facilityId);
        String lgaName = baseInformation.getLocalGovernmentName(facilityId);
        String stateName = baseInformation.getStateName(facilityId);

        List<HtsMsfProjection> records =
                reportRepository.findTestingResults(facilityId,
                        startDate,
                        endDate
                );
        LOG.info("Records returned: {}", records.size());

        return ReportProjectionDTO.builder()
                .reportTitle("NATIONAL HTS MONTHLY SUMMARY FORM")
                .reportingPeriod(
                        startDate.getMonth().name()
                                + " "
                                + startDate.getYear()
                )
                .facilityName(facilityName)
                .state(stateName)
                .lga(lgaName)

                .sections(Arrays.asList(

                        buildSection(
                                "Number of people who tested HIV negative and received their results",
                                "AGE_GROUP",
                                filter(
                                        records,
                                        "RESULTS_BY_AGE",
                                        "Negative")
                        ),

                        buildSection(
                                "Number of people who tested HIV positive and received their results",
                                "AGE_GROUP",
                                filter(
                                        records,
                                        "RESULTS_BY_AGE",
                                        "Positive")
                        ),

                        buildSection(
                                "Number of newly diagnosed HIV-positive persons with Acute HIV Infection",
                                "ACUTE_HIV",
                                filter(
                                        records,
                                        "ACUTE_HIV",
                                        null)
                        ),

                        buildSection(
                                "Number of newly diagnosed HIV-positive persons tested for recency",
                                "RECENCY",
                                filter(
                                        records,
                                        "RECENCY",
                                        null)
                        ),

                        buildSection(
                                "Number of persons at high risk of HIV infection who were tested for HIV and received their results",
                                "KEY_POPULATION",
                                filter(
                                        records,
                                        "KEY_POPULATION",
                                        null)
                        )
                ))
                .build();
    }

    private List<HtsMsfProjection> filter(
            List<HtsMsfProjection> records,
            String section,
            String testResult) {


        records.stream()
                .limit(20)
                .forEach(r -> LOG.info(
                        "{} | {} | {} | {} | {}",
                        r.getSection(),
                        r.getTestResult(),
                        r.getRowLabel(),
                        r.getColumnKey(),
                        r.getValue()
                ));

        return records.stream()
                .filter(r ->
                        section.equalsIgnoreCase(
                                safe(r.getSection())))
                .filter(r ->
                        testResult == null
                                || testResult.equalsIgnoreCase(
                                safe(r.getTestResult())))
                .collect(Collectors.toList());
    }


    private ReportSectionDTO buildSection(
            String title,
            String sectionType,
            List<HtsMsfProjection> projections) {

        return ReportSectionDTO.builder()
                .title(title)
                .sectionType(sectionType)
                .rows(buildRows(projections))
                .build();
    }

    private List<ReportRowDTO> buildRows(
            List<HtsMsfProjection> projections) {

        Map<String, ReportRowDTO> rows =
                new LinkedHashMap<>();

        for (HtsMsfProjection projection : projections) {

            LOG.info(
                    "section={} row={} column={} value={}",
                    projection.getSection(),
                    projection.getRowLabel(),
                    projection.getColumnKey(),
                    projection.getValue()
            );

            System.out.println(projection.getValue());
            String rowLabel =
                    safe(projection.getRowLabel());

            if (rowLabel.isEmpty()) {

                rowLabel = defaultRowLabel(
                        projection.getSection(),
                        projection.getTestResult());
            }

            String key =
                    safe(projection.getTestResult())
                            + "|"
                            + rowLabel;

            ReportRowDTO row = rows.get(key);

            if (row == null) {

                row = ReportRowDTO.builder()
                        .rowLabel(rowLabel)
                        .testResult(
                                projection.getTestResult())
                        .values(
                                new LinkedHashMap<String, BigDecimal>())
                        .build();

                rows.put(key, row);
            }

            row.getValues().put(
                    safe(projection.getColumnKey()),
                    BigDecimal.valueOf(
                            projection.getValue() == null
                                    ? 0L
                                    : projection.getValue()
                    )
            );
        }

        return new ArrayList<>(rows.values());
    }

    private String defaultRowLabel(
            String section,
            String testResult) {

        if ("ACUTE_HIV".equalsIgnoreCase(section)) {
            return "Acute HIV Infection";
        }

        if ("KEY_POPULATION".equalsIgnoreCase(section)) {
            return testResult == null
                    ? "Unknown"
                    : testResult;
        }

        return "TOTAL";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}