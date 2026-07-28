package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Example of a brand-new report's projection row. It has nothing to do
 * with HTS - it just implements the same shared shape, which is all
 * ProjectionRowAssembler and the two renderers need.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtRefillProjection implements ReportProjectionRow {
    private String section;      // e.g. "REFILLS_BY_REGIMEN"
    private String testResult;   // unused here - always null
    private String rowLabel;     // regimen name, or "TOTAL"
    private String columnKey;    // "male" | "female"
    private Long value;
}
