package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.dto.*;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Resolves a ReportSectionDTO to the SectionLayoutDef that should render it
 * - registered layout if one exists, otherwise a layout inferred from
 * whatever value keys appear in that section's rows. Shared by every
 * output format (Excel, PDF, ...) so "how a section is shaped" is defined
 * once, not once per renderer.
 */
@Component
@RequiredArgsConstructor
public class ReportLayoutResolver {

    private final SectionLayoutRegistry layoutRegistry;

    public SectionLayoutDef resolve(ReportSectionDTO section) {
        SectionLayoutDef registered = layoutRegistry.find(section.getSectionType());
        return registered != null ? registered : infer(section);
    }

    /** Flattens a layout's column groups into a single ordered list of leaf columns. */
    public List<ColumnDef> flatten(SectionLayoutDef layout) {
        List<ColumnDef> flat = new ArrayList<>();
        for (ColumnGroupDef group : layout.getColumnGroups()) {
            flat.addAll(group.getColumns());
        }
        return flat;
    }

    private SectionLayoutDef infer(ReportSectionDTO section) {
        Set<String> keys = new LinkedHashSet<>();
        for (ReportRowDTO row : section.getRows()) {
            if (row.getValues() != null) keys.addAll(row.getValues().keySet());
        }
        List<ColumnGroupDef> groups = new ArrayList<>();
        for (String key : keys) {
            groups.add(ColumnGroupDef.builder()
                    .groupLabel(null)
                    .columns(Collections.singletonList(
                            ColumnDef.builder().header(humanize(key)).key(key).build()))
                    .build());
        }
        return SectionLayoutDef.builder().rowLabelHeader("Row").columnGroups(groups).build();
    }

    private String humanize(String key) {
        String spaced = key.replace('_', ' ');
        return spaced.isEmpty() ? spaced : Character.toUpperCase(spaced.charAt(0)) + spaced.substring(1);
    }
}
