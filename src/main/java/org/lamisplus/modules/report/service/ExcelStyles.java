package org.lamisplus.modules.report.service;

import lombok.Getter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

@Getter
public class ExcelStyles {

    private final CellStyle title;
    private final CellStyle sectionTitle;
    private final CellStyle header;
    private final CellStyle label;
    private final CellStyle value;
    private final CellStyle total;

    public ExcelStyles(Workbook workbook) {

        /*
         * TITLE
         */
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);

        title = workbook.createCellStyle();
        title.setFont(titleFont);
        title.setAlignment(HorizontalAlignment.CENTER);

        /*
         * SECTION TITLE
         */
        Font sectionFont = workbook.createFont();
        sectionFont.setBold(true);
        sectionFont.setFontHeightInPoints((short) 12);

        sectionTitle = workbook.createCellStyle();
        sectionTitle.setFont(sectionFont);

        /*
         * HEADER
         */
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        header = workbook.createCellStyle();
        header.setFont(headerFont);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);

        header.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex());

        header.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);

        setBorders(header);

        /*
         * LABEL
         */
        label = workbook.createCellStyle();
        setBorders(label);

        /*
         * VALUE
         */
        value = workbook.createCellStyle();
        value.setAlignment(HorizontalAlignment.CENTER);
        setBorders(value);

        /*
         * TOTAL
         */
        Font totalFont = workbook.createFont();
        totalFont.setBold(true);

        total = workbook.createCellStyle();
        total.setFont(totalFont);
        total.setAlignment(HorizontalAlignment.CENTER);

        total.setFillForegroundColor(
                IndexedColors.GREY_25_PERCENT.getIndex());

        total.setFillPattern(
                FillPatternType.SOLID_FOREGROUND);

        setBorders(total);
    }

    private void setBorders(CellStyle style) {

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}