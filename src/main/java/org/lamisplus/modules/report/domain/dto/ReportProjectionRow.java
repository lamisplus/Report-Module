package org.lamisplus.modules.report.domain.dto;

/**
 * The flat "long format" shape a report's SQL should return: one row per
 * (section, row, column) cell. Any Spring Data projection interface for a
 * new report can extend this and immediately work with
 * ProjectionRowAssembler and, downstream, both GenericExcelReportService
 * and GenericPdfReportService - no new plumbing needed per report.
 *
 * Existing example: HtsMsfProjection should extend this (its methods
 * already match this shape).
 */
public interface ReportProjectionRow {
    String getSection();
    String getTestResult();  // nullable - not every report has a result-type split
    String getRowLabel();    // nullable - the query can supply "TOTAL" or similar directly
    String getColumnKey();
    Long getValue();
}
