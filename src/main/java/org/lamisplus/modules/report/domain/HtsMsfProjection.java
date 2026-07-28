package org.lamisplus.modules.report.domain;

import org.lamisplus.modules.report.domain.dto.ReportProjectionRow;

/**
 * HTS MSF projection returned by Spring Data native query.
 *
 * Because it extends ReportProjectionRow,
 * it can be consumed by:
 *
 * - ProjectionRowAssembler
 * - GenericExcelReportService
 * - GenericPdfReportService
 *
 * without additional mapping code.
 */
public interface HtsMsfProjection extends ReportProjectionRow {

    @Override
    String getSection();

    @Override
    String getTestResult();

    @Override
    String getRowLabel();

    @Override
    String getColumnKey();

    @Override
    Long getValue();
}