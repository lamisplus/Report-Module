package org.lamisplus.modules.report.service;

import lombok.Getter;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Visual theme for generated reports: a real color palette instead of the
 * default grey, zebra-striped data rows, a formatted-number style, and a
 * distinct total-row treatment. Built once per workbook and reused across
 * every cell (POI workbooks cap out around 64,000 distinct styles, so never
 * create a CellStyle per cell).
 */
@Getter
public class ExcelTheme {

    // brand palette - tweak these three lines to reskin every report
    private static final byte[] PRIMARY_DARK = rgb(21, 61, 102);   // deep navy - title bar
    private static final byte[] PRIMARY = rgb(46, 106, 168);       // header blue
    private static final byte[] ACCENT_LIGHT = rgb(223, 234, 245); // pale blue - zebra band
    private static final byte[] TOTAL_FILL = rgb(255, 230, 179);   // warm amber - total rows

    private final CellStyle title;
    private final CellStyle metaLabel;
    private final CellStyle metaValue;
    private final CellStyle sectionTitle;
    private final CellStyle header;
    private final CellStyle label;
    private final CellStyle labelBand;
    private final CellStyle value;
    private final CellStyle valueBand;
    private final CellStyle total;
    private final CellStyle footerLabel;
    private final CellStyle signatureLine;
    private final CellStyle versionText;

    public ExcelTheme(XSSFWorkbook workbook) {

        Font whiteBold16 = font(workbook, true, 16, IndexedColors.WHITE.getIndex());
        title = workbook.createCellStyle();
        title.setFont(whiteBold16);
        title.setAlignment(HorizontalAlignment.CENTER);
        title.setVerticalAlignment(VerticalAlignment.CENTER);
        fill(title, PRIMARY_DARK);

        Font boldDark = font(workbook, true, 11, IndexedColors.BLACK.getIndex());
        metaLabel = workbook.createCellStyle();
        metaLabel.setFont(boldDark);
        metaLabel.setAlignment(HorizontalAlignment.RIGHT);

        Font plain = font(workbook, false, 11, IndexedColors.BLACK.getIndex());
        metaValue = workbook.createCellStyle();
        metaValue.setFont(plain);
        metaValue.setAlignment(HorizontalAlignment.LEFT);

        Font sectionFont = font(workbook, true, 12, IndexedColors.WHITE.getIndex());
        sectionTitle = workbook.createCellStyle();
        sectionTitle.setFont(sectionFont);
        sectionTitle.setAlignment(HorizontalAlignment.LEFT);
        sectionTitle.setVerticalAlignment(VerticalAlignment.CENTER);
        fill(sectionTitle, PRIMARY);

        Font headerFont = font(workbook, true, 10, IndexedColors.WHITE.getIndex());
        header = workbook.createCellStyle();
        header.setFont(headerFont);
        header.setAlignment(HorizontalAlignment.CENTER);
        header.setVerticalAlignment(VerticalAlignment.CENTER);
        header.setWrapText(true);
        fill(header, PRIMARY);
        border(header, BorderStyle.THIN, IndexedColors.WHITE.getIndex());

        label = workbook.createCellStyle();
        label.setVerticalAlignment(VerticalAlignment.CENTER);
        border(label, BorderStyle.THIN, IndexedColors.GREY_50_PERCENT.getIndex());

        labelBand = workbook.createCellStyle();
        labelBand.cloneStyleFrom(label);
        fill(labelBand, ACCENT_LIGHT);

        String numberFormat = "#,##0";
        short fmt = workbook.createDataFormat().getFormat(numberFormat);

        value = workbook.createCellStyle();
        value.setAlignment(HorizontalAlignment.CENTER);
        value.setDataFormat(fmt);
        border(value, BorderStyle.THIN, IndexedColors.GREY_50_PERCENT.getIndex());

        valueBand = workbook.createCellStyle();
        valueBand.cloneStyleFrom(value);
        fill(valueBand, ACCENT_LIGHT);

        Font totalFont = font(workbook, true, 11, IndexedColors.BLACK.getIndex());
        total = workbook.createCellStyle();
        total.setFont(totalFont);
        total.setAlignment(HorizontalAlignment.CENTER);
        total.setDataFormat(fmt);
        fill(total, TOTAL_FILL);
        border(total, BorderStyle.MEDIUM, IndexedColors.GREY_80_PERCENT.getIndex());

        Font footerLabelFont = font(workbook, true, 9, IndexedColors.BLACK.getIndex());
        footerLabel = workbook.createCellStyle();
        footerLabel.setFont(footerLabelFont);
        footerLabel.setAlignment(HorizontalAlignment.LEFT);
        footerLabel.setVerticalAlignment(VerticalAlignment.BOTTOM);

        signatureLine = workbook.createCellStyle();
        signatureLine.setVerticalAlignment(VerticalAlignment.BOTTOM);
        signatureLine.setBorderBottom(BorderStyle.THIN);
        signatureLine.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        Font versionFont = workbook.createFont();
        versionFont.setItalic(true);
        versionFont.setFontHeightInPoints((short) 9);
        versionText = workbook.createCellStyle();
        versionText.setFont(versionFont);
        versionText.setAlignment(HorizontalAlignment.RIGHT);
    }

    /** Data-row style, alternating by row position within the section (1-based). */
    public CellStyle valueStyle(int rowPositionInSection, boolean isTotal) {
        if (isTotal) return total;
        return rowPositionInSection % 2 == 0 ? valueBand : value;
    }

    public CellStyle labelStyle(int rowPositionInSection, boolean isTotal) {
        if (isTotal) return total;
        return rowPositionInSection % 2 == 0 ? labelBand : label;
    }

    private static Font font(XSSFWorkbook wb, boolean bold, int size, short colorIndex) {
        Font f = wb.createFont();
        f.setBold(bold);
        f.setFontHeightInPoints((short) size);
        f.setColor(colorIndex);
        return f;
    }

    private static void fill(CellStyle style, byte[] rgb) {
        XSSFCellStyle xssf = (XSSFCellStyle) style;
        xssf.setFillForegroundColor(new XSSFColor(rgb, null));
        xssf.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    }

    private static void border(CellStyle style, BorderStyle borderStyle, short colorIndex) {
        style.setBorderTop(borderStyle);
        style.setBorderBottom(borderStyle);
        style.setBorderLeft(borderStyle);
        style.setBorderRight(borderStyle);
        style.setTopBorderColor(colorIndex);
        style.setBottomBorderColor(colorIndex);
        style.setLeftBorderColor(colorIndex);
        style.setRightBorderColor(colorIndex);
    }

    private static byte[] rgb(int r, int g, int b) {
        return new byte[]{(byte) r, (byte) g, (byte) b};
    }
}
