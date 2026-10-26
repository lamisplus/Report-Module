package org.lamisplus.modules.report.service;

import org.lamisplus.modules.report.domain.dto.ReportProjectionRow;
import org.lamisplus.modules.report.domain.dto.ReportRowDTO;
import org.lamisplus.modules.report.domain.dto.ReportSectionDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Turns a flat list of ReportProjectionRow (whatever report it came from)
 * into the ReportSectionDTO/ReportRowDTO shape both renderers consume. This
 * is the piece HtsReportBuilderService used to do inline for HTS
 * specifically - pulled out so any new report's builder service can call
 * the same three methods instead of re-implementing the grouping logic.
 */
@Component
public class ProjectionRowAssembler {

    /** Keeps rows matching this section, and (if given) this test-result value. */
    public <T extends ReportProjectionRow> List<T> filter(List<T> rows, String section, String testResult) {
        return rows.stream()
                .filter(r -> section.equalsIgnoreCase(safe(r.getSection())))
                .filter(r -> testResult == null || testResult.equalsIgnoreCase(safe(r.getTestResult())))
                .collect(Collectors.toList());
    }

    /** Wraps buildRows(...) with the title/sectionType metadata GenericExcelReportService/GenericPdfReportService need. */
    public <T extends ReportProjectionRow> ReportSectionDTO buildSection(
            String title, String sectionType, List<T> projections) {

        return ReportSectionDTO.builder()
                .title(title)
                .sectionType(sectionType)
                .rows(buildRows(projections))
                .build();
    }

    /**
     * Groups flat (row, column, value) triples back into one ReportRowDTO
     * per distinct (testResult, rowLabel) pair, with columnKey->value
     * collected into that row's values map.
     *
     * If a projection's rowLabel is null/blank, it defaults to "TOTAL" -
     * prefer having the SQL supply an explicit row_label (e.g. via
     * GROUPING SETS, as in the HTS queries) over relying on this default.
     */
    public <T extends ReportProjectionRow> List<ReportRowDTO> buildRows(List<T> projections) {
        Map<String, ReportRowDTO> rows = new LinkedHashMap<>();

        for (T projection : projections) {
            String rowLabel = safe(projection.getRowLabel());
            if (rowLabel.isEmpty()) rowLabel = "TOTAL";

            String key = safe(projection.getTestResult()) + "|" + rowLabel;

            ReportRowDTO row = rows.get(key);
            if (row == null) {
                row = ReportRowDTO.builder()
                        .rowLabel(rowLabel)
                        .testResult(projection.getTestResult())
                        .values(new LinkedHashMap<String, BigDecimal>())
                        .build();
                rows.put(key, row);
            }

            row.getValues().put(
                    safe(projection.getColumnKey()),
                    BigDecimal.valueOf(projection.getValue() == null ? 0L : projection.getValue())
            );
        }

        return new ArrayList<>(rows.values());
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
