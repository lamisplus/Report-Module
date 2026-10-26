package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A group of columns sharing one merged header, e.g. "In-patient" over
 * ["M","F"]. Set groupLabel to null (with a single column) for a plain
 * column with no group header above it, e.g. "AGYW" or "Total" in the key
 * population table - it will still get a header cell, just vertically
 * merged across both header rows instead of horizontally merged with
 * siblings.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnGroupDef {
    private String groupLabel; // null => single ungrouped column
    private List<ColumnDef> columns;

    public boolean isUngroupedSingle() {
        return groupLabel == null && columns != null && columns.size() == 1;
    }
}
