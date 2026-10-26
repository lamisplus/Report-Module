package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Full rendering recipe for one ReportSectionDTO#sectionType: what the row
 * label column header says, and what column groups follow it. Register one
 * of these per section type in SectionLayoutRegistry - the renderer itself
 * (GenericExcelReportService) never needs to know section types exist.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionLayoutDef {
    private String rowLabelHeader; // e.g. "Age Group", "Result", "Indicator"
    private List<ColumnGroupDef> columnGroups;
}
