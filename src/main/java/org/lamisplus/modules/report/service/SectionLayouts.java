// SectionLayouts.java — shared builder helpers, used by every report module
package org.lamisplus.modules.report.service;

import org.lamisplus.modules.report.domain.dto.ColumnDef;
import org.lamisplus.modules.report.domain.dto.ColumnGroupDef;

import java.util.Arrays;
import java.util.Collections;

/**
 * Small DSL for building SectionLayoutDef column groups. Not report-specific -
 * shared by HtsSectionLayouts, PrepSectionLayouts, and any future report's
 * layout class.
 */

public final class SectionLayouts {

    private SectionLayouts() {}

    /** A labeled group with an arbitrary number of columns. */
    public static ColumnGroupDef group(String label, ColumnDef... columns) {
        return ColumnGroupDef.builder()
                .groupLabel(label)
                .columns(Arrays.asList(columns))
                .build();
    }

    /** Convenience for the common Male/Female two-column group. */
    public static ColumnGroupDef mfGroup(String label, String maleKey, String femaleKey) {
        return group(label, col("M", maleKey), col("F", femaleKey));
    }

    /** A group with no group-level label - just one column. */
    public static ColumnGroupDef single(String header, String key) {
        return ColumnGroupDef.builder()
                .groupLabel(null)
                .columns(Collections.singletonList(col(header, key)))
                .build();
    }

    public static ColumnDef col(String header, String key) {
        return ColumnDef.builder().header(header).key(key).build();
    }
}