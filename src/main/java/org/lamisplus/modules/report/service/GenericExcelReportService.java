package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.PageOrder;
import org.apache.poi.ss.usermodel.PrintOrientation;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lamisplus.modules.report.domain.dto.*;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;


/**
 * Renders any ReportProjectionDTO to a formatted .xlsx. Genuinely generic:
 * it has no idea what "AGE_GROUP" or "KEY_POPULATION" mean - it asks
 * SectionLayoutRegistry for a layout by sectionType, and if none is
 * registered it derives one on the fly from whatever value keys show up in
 * that section's rows. New reports register a layout (or don't, and get a
 * plain auto-rendered table) - this class never needs another method.
 */
@Service
@RequiredArgsConstructor
public class GenericExcelReportService {

    private final ReportLayoutResolver layoutResolver;

    private static final String FORM_VERSION = "Version November 2025";

    public byte[] generate(ReportProjectionDTO report) {
        try (
                XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            XSSFSheet sheet = workbook.createSheet(safeSheetName(report.getReportTitle()));
            ExcelTheme theme = new ExcelTheme(workbook);

            sheet.setDisplayGridlines(false);
            sheet.setDisplayRowColHeadings(false);
            applyPrintSetup(sheet);

            int totalColumns = Math.max(widestSectionColumnCount(report), 16);

            int rowIndex = 0;
            rowIndex = writeReportHeader(sheet, theme, report, rowIndex, totalColumns);
            int freezeAfterRow = rowIndex;

            for (ReportSectionDTO section : report.getSections()) {
                SectionLayoutDef layout = layoutResolver.resolve(section);
                rowIndex = writeSection(sheet, theme, section, layout, rowIndex, totalColumns);
            }

            rowIndex++; // one blank row separating the data from the sign-off block
            writeSignOffFooter(sheet, theme, rowIndex, totalColumns);

            applyColumnWidths(sheet, totalColumns);
            sheet.createFreezePane(1, freezeAfterRow);

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    private int writeReportHeader(Sheet sheet, ExcelTheme theme, ReportProjectionDTO report, int rowIndex, int width) {
        Row titleRow = sheet.createRow(rowIndex++);
        titleRow.setHeightInPoints(26f);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(report.getReportTitle());
        titleCell.setCellStyle(theme.getTitle());
        for (int c = 1; c < width; c++) {
            Cell filler = titleRow.createCell(c);
            filler.setCellStyle(theme.getTitle());
        }
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, width - 1));

        rowIndex = writeMetaRow(sheet, theme, rowIndex, "Facility:", report.getFacilityName());
        rowIndex = writeMetaRow(sheet, theme, rowIndex, "State:", report.getState());
        rowIndex = writeMetaRow(sheet, theme, rowIndex, "LGA:", report.getLga());
        rowIndex = writeMetaRow(sheet, theme, rowIndex, "Reporting Period:", report.getReportingPeriod());

        return rowIndex;
    }

    private int writeMetaRow(Sheet sheet, ExcelTheme theme, int rowIndex, String label, String value) {
        Row row = sheet.createRow(rowIndex++);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(theme.getMetaLabel());
        Cell valueCell = row.createCell(1);
        valueCell.setCellValue(value == null ? "" : value);
        valueCell.setCellStyle(theme.getMetaValue());
        return rowIndex;
    }

    private int writeSection(Sheet sheet, ExcelTheme theme, ReportSectionDTO section,
                             SectionLayoutDef layout, int rowIndex, int totalColumns) {

        List<ColumnDef> resolved = layoutResolver.flatten(layout);

        // Every section shares the same physical column grid (1 row-label
        // column + totalColumns-1 value columns), but a narrow section (e.g.
        // 2 real columns) has far fewer logical fields than the widest one
        // (e.g. 13). Rather than rendering those fields at native width and
        // dumping the leftover width into one blank cell at the end, each
        // logical column is stretched to span several physical columns so
        // the section's own grid - and its "Total" column in particular -
        // lines up with the widest section directly above/below it.
        int[] spans = computeColumnSpans(resolved.size(), totalColumns - 1);

        Row titleRow = sheet.createRow(rowIndex++);
        titleRow.setHeightInPoints(18f);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(section.getTitle());
        titleCell.setCellStyle(theme.getSectionTitle());
        for (int c = 1; c < totalColumns; c++) {
            titleRow.createCell(c).setCellStyle(theme.getSectionTitle());
        }
        sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0, totalColumns - 1));

        int groupRowNum = rowIndex++;
        int subHeaderRowNum = rowIndex++;
        Row groupRow = sheet.createRow(groupRowNum);
        Row subHeaderRow = sheet.createRow(subHeaderRowNum);

        Cell rowLabelHeader = groupRow.createCell(0);
        rowLabelHeader.setCellValue(layout.getRowLabelHeader());
        rowLabelHeader.setCellStyle(theme.getHeader());
        subHeaderRow.createCell(0).setCellStyle(theme.getHeader());
        sheet.addMergedRegion(new CellRangeAddress(groupRowNum, subHeaderRowNum, 0, 0));

        int col = 1;
        int leafIndex = 0;
        for (ColumnGroupDef group : layout.getColumnGroups()) {
            if (group.isUngroupedSingle()) {
                ColumnDef c = group.getColumns().get(0);
                int span = spans[leafIndex++];
                Cell cell = groupRow.createCell(col);
                cell.setCellValue(c.getHeader());
                cell.setCellStyle(theme.getHeader());
                subHeaderRow.createCell(col).setCellStyle(theme.getHeader());
                for (int cc = col + 1; cc < col + span; cc++) {
                    groupRow.createCell(cc).setCellStyle(theme.getHeader());
                    subHeaderRow.createCell(cc).setCellStyle(theme.getHeader());
                }
                sheet.addMergedRegion(new CellRangeAddress(groupRowNum, subHeaderRowNum, col, col + span - 1));
                col += span;
            } else {
                int groupStart = col;
                Cell groupCell = groupRow.createCell(col);
                groupCell.setCellValue(group.getGroupLabel());
                groupCell.setCellStyle(theme.getHeader());
                for (ColumnDef c : group.getColumns()) {
                    int span = spans[leafIndex++];
                    Cell subCell = subHeaderRow.createCell(col);
                    subCell.setCellValue(c.getHeader());
                    subCell.setCellStyle(theme.getHeader());
                    for (int cc = col + 1; cc < col + span; cc++) {
                        subHeaderRow.createCell(cc).setCellStyle(theme.getHeader());
                    }
                    if (span > 1) {
                        sheet.addMergedRegion(new CellRangeAddress(subHeaderRowNum, subHeaderRowNum, col, col + span - 1));
                    }
                    col += span;
                }
                for (int gc = groupStart + 1; gc < col; gc++) {
                    groupRow.createCell(gc).setCellStyle(theme.getHeader());
                }
                sheet.addMergedRegion(new CellRangeAddress(groupRowNum, groupRowNum, groupStart, col - 1));
            }
        }

        int position = 0;
        for (ReportRowDTO dto : section.getRows()) {
            position++;
            boolean isTotal = "TOTAL".equalsIgnoreCase(dto.getRowLabel()) || "Total".equalsIgnoreCase(dto.getRowLabel());

            Row row = sheet.createRow(rowIndex++);
            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(dto.getRowLabel());
            labelCell.setCellStyle(theme.labelStyle(position, isTotal));

            int c = 1;
            for (int i = 0; i < resolved.size(); i++) {
                int span = spans[i];
                writeValue(row, c, span, dto, resolved.get(i).getKey(), theme, position, isTotal, sheet);
                c += span;
            }
        }

        return rowIndex;
    }

    /**
     * Splits {@code availableWidth} physical columns across {@code columnCount}
     * logical fields as evenly as possible (e.g. 15 physical columns / 9 fields
     * -&gt; six 2-wide fields and three 1-wide fields), spreading any remainder
     * across evenly-spaced fields rather than piling it onto the first few, so
     * columns near the end of a row aren't visibly narrower than columns near
     * the start.
     */
    private int[] computeColumnSpans(int columnCount, int availableWidth) {
        int[] spans = new int[columnCount];
        if (columnCount == 0) {
            return spans;
        }
        int base = availableWidth / columnCount;
        int remainder = availableWidth % columnCount;
        java.util.Arrays.fill(spans, base);
        for (int i = 0; i < remainder; i++) {
            int idx = (int) ((long) i * columnCount / remainder);
            spans[idx]++;
        }
        return spans;
    }

    private void writeValue(Row row, int column, int span, ReportRowDTO dto, String key,
                            ExcelTheme theme, int position, boolean isTotal, Sheet sheet) {
        BigDecimal value = dto.getValues() == null ? null : dto.getValues().get(key);
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? 0D : value.doubleValue());
        cell.setCellStyle(theme.valueStyle(position, isTotal));
        for (int c = column + 1; c < column + span; c++) {
            row.createCell(c).setCellStyle(theme.valueStyle(position, isTotal));
        }
        if (span > 1) {
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), column, column + span - 1));
        }
    }

    private int widestSectionColumnCount(ReportProjectionDTO report) {
        int widest = 0;
        for (ReportSectionDTO section : report.getSections()) {
            SectionLayoutDef layout = layoutResolver.resolve(section);
            widest = Math.max(widest, layoutResolver.flatten(layout).size());
        }
        return widest + 1; // + row label column
    }

    private void applyColumnWidths(Sheet sheet, int columnCount) {
        sheet.setColumnWidth(0, 26 * 256); // row label column, wide enough for age-group/recency text
        for (int c = 1; c < columnCount; c++) {
            sheet.setColumnWidth(c, 11 * 256);
        }
    }

    private void applyPrintSetup(XSSFSheet sheet) {
        sheet.getPrintSetup().setPaperSize(org.apache.poi.ss.usermodel.PrintSetup.A4_PAPERSIZE);
        sheet.getPrintSetup().setOrientation(PrintOrientation.LANDSCAPE);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setPageOrder(PageOrder.DOWN_THEN_OVER);
        sheet.setMargin(Sheet.LeftMargin, 0.3);
        sheet.setMargin(Sheet.RightMargin, 0.3);
    }

    /**
     * "Completed by / Verified by" sign-off block from the bottom of the
     * paper form, plus the form's version stamp. Written once per report,
     * after every section - not tied to any section's data.
     */
    private void writeSignOffFooter(Sheet sheet, ExcelTheme theme, int rowIndex, int width) {
        rowIndex = writeSignOffRow(sheet, theme, rowIndex, width,
                "Completed by: Name:", "Designation:", "Signature:", "Date:");
        rowIndex = writeSignOffRow(sheet, theme, rowIndex, width,
                "Verified by Name:", "Designation:", "Signature:", "Date:");

        Row versionRow = sheet.createRow(rowIndex);
        Cell versionCell = versionRow.createCell(width - 1);
        versionCell.setCellValue(FORM_VERSION);
        versionCell.setCellStyle(theme.getVersionText());
    }

    private int writeSignOffRow(Sheet sheet, ExcelTheme theme, int rowIndex, int width,
                                String nameLabel, String designationLabel, String signatureLabel, String dateLabel) {

        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(20f);
        int rowNum = row.getRowNum();

        int nameLabelSpan = Math.max(2, width * 3 / 16);
        int nameLineSpan = Math.max(2, width * 3 / 16);
        int desigLineSpan = Math.max(1, width * 2 / 16);
        int sigLineSpan = Math.max(1, width * 2 / 16);
        int used = nameLabelSpan + nameLineSpan + 1 + desigLineSpan + 1 + sigLineSpan + 1;
        int dateLineSpan = Math.max(1, width - used);

        int col = 0;
        col = writeMergedLabel(sheet, row, rowNum, col, nameLabelSpan, nameLabel, theme.getFooterLabel());
        col = writeMergedLine(sheet, row, rowNum, col, nameLineSpan, theme.getSignatureLine());
        col = writeMergedLabel(sheet, row, rowNum, col, 1, designationLabel, theme.getFooterLabel());
        col = writeMergedLine(sheet, row, rowNum, col, desigLineSpan, theme.getSignatureLine());
        col = writeMergedLabel(sheet, row, rowNum, col, 1, signatureLabel, theme.getFooterLabel());
        col = writeMergedLine(sheet, row, rowNum, col, sigLineSpan, theme.getSignatureLine());
        col = writeMergedLabel(sheet, row, rowNum, col, 1, dateLabel, theme.getFooterLabel());
        writeMergedLine(sheet, row, rowNum, col, dateLineSpan, theme.getSignatureLine());

        return rowIndex + 1;
    }

    private int writeMergedLabel(Sheet sheet, Row row, int rowNum, int col, int span, String text, org.apache.poi.ss.usermodel.CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(text);
        cell.setCellStyle(style);
        for (int c = col + 1; c < col + span; c++) {
            row.createCell(c).setCellStyle(style);
        }
        if (span > 1) {
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, col, col + span - 1));
        }
        return col + span;
    }

    private int writeMergedLine(Sheet sheet, Row row, int rowNum, int col, int span, org.apache.poi.ss.usermodel.CellStyle style) {
        for (int c = col; c < col + span; c++) {
            row.createCell(c).setCellStyle(style);
        }
        if (span > 1) {
            sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, col, col + span - 1));
        }
        return col + span;
    }

    private String safeSheetName(String title) {
        String s = (title == null || title.trim().isEmpty()) ? "Report" : title.trim();
        s = s.replaceAll("[\\[\\]:*?/\\\\]", " ");
        return s.length() > 31 ? s.substring(0, 31) : s;
    }
}
