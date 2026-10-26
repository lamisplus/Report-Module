package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * One leaf column in a section's table, e.g. { header: "M", key: "inpatient_m" }.
 * `key` must match the key used in ReportRowDTO#getValues() for that row.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDef {
    private String header;
    private String key;
}
