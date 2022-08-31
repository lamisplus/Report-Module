package org.lamisplus.modules.report.service;

import org.apache.poi.ss.usermodel.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExcellUtil {
    public static void createHeader(List<String> columnHeadings, CellStyle headerStyle, Row headerRow) {
        for (int i = 0; i < columnHeadings.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnHeadings.get(i));
            cell.setCellStyle(headerStyle);
        }
    }


    @NotNull
    public static CellStyle getCellStyle(Workbook workbook, Font headerFont) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        return headerStyle;
    }


    @NotNull
    public static Font getFont(Workbook workbook) {
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setColor(IndexedColors.BLACK.index);
        return headerFont;
    }
}
