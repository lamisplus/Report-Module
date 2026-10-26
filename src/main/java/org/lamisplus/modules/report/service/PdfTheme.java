package org.lamisplus.modules.report.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * PDF equivalent of ExcelTheme - same palette, same idea of a small number
 * of cached styles reused across every cell rather than built inline.
 *
 * iTextPDF (com.itextpdf:itextpdf, 5.x) is AGPL-licensed unless you hold a
 * commercial license from iText Group - check that's compatible with how
 * this module is distributed before shipping it. OpenPDF (LGPL/MPL, a
 * fork of this same 5.x codebase) is a drop-in alternative if not; the
 * only difference is the package name (com.lowagie.text vs
 * com.itextpdf.text) and the Maven coordinate.
 */
public class PdfTheme {

    static final BaseColor PRIMARY_DARK = new BaseColor(21, 61, 102);
    static final BaseColor PRIMARY = new BaseColor(46, 106, 168);
    static final BaseColor ACCENT_LIGHT = new BaseColor(223, 234, 245);
    static final BaseColor TOTAL_FILL = new BaseColor(255, 230, 179);
    static final BaseColor BORDER = new BaseColor(140, 140, 140);

    final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.WHITE);
    final Font metaLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.BLACK);
    final Font metaValueFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);
    final Font sectionTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);
    final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
    final Font labelFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    final Font valueFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
    final Font totalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);

    PdfPCell titleCell(String text, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, titleFont));
        cell.setColspan(colspan);
        cell.setBackgroundColor(PRIMARY_DARK);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(28f);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    PdfPCell sectionTitleCell(String text, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, sectionTitleFont));
        cell.setColspan(colspan);
        cell.setBackgroundColor(PRIMARY);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(4f);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    PdfPCell headerCell(String text, int colspan, int rowspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, headerFont));
        cell.setColspan(colspan);
        cell.setRowspan(rowspan);
        cell.setBackgroundColor(PRIMARY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3f);
        cell.setBorderColor(BaseColor.WHITE);
        return cell;
    }

    PdfPCell metaLabelCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, metaLabelFont));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        return cell;
    }

    PdfPCell metaValueCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text == null ? "" : text, metaValueFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        return cell;
    }

    PdfPCell labelCell(String text, int position, boolean isTotal) {
        PdfPCell cell = new PdfPCell(new Phrase(text, isTotal ? totalFont : labelFont));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3f);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(bandColor(position, isTotal));
        return cell;
    }

    PdfPCell valueCell(String text, int position, boolean isTotal) {
        PdfPCell cell = new PdfPCell(new Phrase(text, isTotal ? totalFont : valueFont));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(3f);
        cell.setBorderColor(BORDER);
        cell.setBackgroundColor(bandColor(position, isTotal));
        return cell;
    }

    final Font footerLabelFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.BLACK);
    final Font versionFont = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, BaseColor.GRAY);

    /** Plain text label in the sign-off block, e.g. "Completed by: Name:" - no borders, sits above the signature line. */
    PdfPCell footerLabelCell(String text, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(text, footerLabelFont));
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setPaddingBottom(3f);
        return cell;
    }

    /** Blank cell with only a bottom border - the line someone signs/writes on when the form is printed. */
    PdfPCell signatureLineCell(int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setColspan(colspan);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(BaseColor.BLACK);
        cell.setFixedHeight(20f);
        return cell;
    }

    private BaseColor bandColor(int position, boolean isTotal) {
        if (isTotal) return TOTAL_FILL;
        return position % 2 == 0 ? ACCENT_LIGHT : BaseColor.WHITE;
    }
}
