package org.lamisplus.modules.report.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
import org.lamisplus.modules.report.domain.dto.ReportRowDTO;
import org.lamisplus.modules.report.domain.dto.ReportSectionDTO;
import org.lamisplus.modules.report.domain.dto.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Renders the same ReportProjectionDTO as GenericExcelReportService, as a
 * PDF instead of an .xlsx. Uses the same ReportLayoutResolver, so a layout
 * registered once in SectionLayoutRegistry drives both output formats -
 * nothing report-specific lives in this class.
 *
 * Built on classic iTextPDF (com.itextpdf:itextpdf, 5.x) - AGPL-licensed
 * unless you hold a commercial iText license. See PdfTheme for the OpenPDF
 * drop-in alternative if that's not compatible with how this is shipped.
 */
@Service
@RequiredArgsConstructor
public class GenericPdfReportService {

    private static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#,##0");
    private static final String FORM_VERSION = "Version November 2025";

    private final ReportLayoutResolver layoutResolver;

    public byte[] generate(ReportProjectionDTO report) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 24, 24, 24, 24);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            PdfTheme theme = new PdfTheme();

            document.add(titleBlock(report, theme));
            document.add(metaBlock(report, theme));

            for (ReportSectionDTO section : report.getSections()) {
                SectionLayoutDef layout = layoutResolver.resolve(section);
                document.add(sectionTable(section, layout, theme));
            }

            document.add(signOffTable(theme, widestColumnCount(report)));

            Paragraph version = new Paragraph(FORM_VERSION, theme.versionFont);
            version.setAlignment(Element.ALIGN_RIGHT);
            version.setSpacingBefore(4f);
            document.add(version);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF report", e);
        }
    }

    private PdfPTable titleBlock(ReportProjectionDTO report, PdfTheme theme) {
        PdfPTable table = new PdfPTable(1);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);
        table.addCell(theme.titleCell(report.getReportTitle(), 1));
        return table;
    }

    private PdfPTable metaBlock(ReportProjectionDTO report, PdfTheme theme) {
        PdfPTable table = new PdfPTable(new float[]{1.2f, 4f});
        table.setWidthPercentage(60);
        table.setSpacingBefore(2f);
        table.setSpacingAfter(6f);

        table.addCell(theme.metaLabelCell("Facility:"));
        table.addCell(theme.metaValueCell(report.getFacilityName()));
        table.addCell(theme.metaLabelCell("State:"));
        table.addCell(theme.metaValueCell(report.getState()));
        table.addCell(theme.metaLabelCell("LGA:"));
        table.addCell(theme.metaValueCell(report.getLga()));
        table.addCell(theme.metaLabelCell("Reporting Period:"));
        table.addCell(theme.metaValueCell(report.getReportingPeriod()));
        return table;
    }

    private PdfPTable sectionTable(ReportSectionDTO section, SectionLayoutDef layout, PdfTheme theme) {
        List<ColumnDef> resolved = layoutResolver.flatten(layout);
        int numColumns = resolved.size() + 1;

        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);
        table.setSpacingBefore(0f);
        table.setSpacingAfter(0f);
        float[] widths = new float[numColumns];
        widths[0] = 2.4f;
        for (int i = 1; i < numColumns; i++) widths[i] = 1f;
        try {
            table.setWidths(widths);
        } catch (com.itextpdf.text.DocumentException e) {
            throw new RuntimeException("Failed to set column widths", e);
        }

        // section title bar spans the full width of THIS section's table
        table.addCell(theme.sectionTitleCell(section.getTitle(), numColumns));

        // header row 1: row-label header (rowspan 2) + group labels / ungrouped headers
        table.addCell(theme.headerCell(layout.getRowLabelHeader(), 1, 2));
        for (ColumnGroupDef group : layout.getColumnGroups()) {
            if (group.isUngroupedSingle()) {
                table.addCell(theme.headerCell(group.getColumns().get(0).getHeader(), 1, 2));
            } else {
                table.addCell(theme.headerCell(group.getGroupLabel(), group.getColumns().size(), 1));
            }
        }
        // header row 2: sub-headers for grouped columns only (ungrouped ones already reserved their cell above)
        for (ColumnGroupDef group : layout.getColumnGroups()) {
            if (group.isUngroupedSingle()) continue;
            for (ColumnDef c : group.getColumns()) {
                table.addCell(theme.headerCell(c.getHeader(), 1, 1));
            }
        }

        int position = 0;
        for (ReportRowDTO dto : section.getRows()) {
            position++;
            boolean isTotal = "TOTAL".equalsIgnoreCase(dto.getRowLabel()) || "Total".equalsIgnoreCase(dto.getRowLabel());

            table.addCell(theme.labelCell(dto.getRowLabel(), position, isTotal));
            for (ColumnDef column : resolved) {
                BigDecimal value = dto.getValues() == null ? null : dto.getValues().get(column.getKey());
                String text = NUMBER_FORMAT.format(value == null ? BigDecimal.ZERO : value);
                table.addCell(theme.valueCell(text, position, isTotal));
            }
        }

        return table;
    }

    /** "Completed by / Verified by" sign-off block, same content as the paper form's footer. */
    private PdfPTable signOffTable(PdfTheme theme, int reportWidth) {
        int width = Math.max(reportWidth, 16);

        PdfPTable table = new PdfPTable(width);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(0f);

        addSignOffRow(table, theme, width, "Completed by: Name:", "Designation:", "Signature:", "Date:");
        addSignOffRow(table, theme, width, "Verified by Name:", "Designation:", "Signature:", "Date:");

        return table;
    }

    private void addSignOffRow(PdfPTable table, PdfTheme theme, int width,
                               String nameLabel, String designationLabel, String signatureLabel, String dateLabel) {

        int nameLabelSpan = Math.max(2, width * 3 / 16);
        int nameLineSpan = Math.max(2, width * 3 / 16);
        int desigLineSpan = Math.max(1, width * 2 / 16);
        int sigLineSpan = Math.max(1, width * 2 / 16);
        int used = nameLabelSpan + nameLineSpan + 1 + desigLineSpan + 1 + sigLineSpan + 1;
        int dateLineSpan = Math.max(1, width - used);

        table.addCell(theme.footerLabelCell(nameLabel, nameLabelSpan));
        table.addCell(theme.signatureLineCell(nameLineSpan));
        table.addCell(theme.footerLabelCell(designationLabel, 1));
        table.addCell(theme.signatureLineCell(desigLineSpan));
        table.addCell(theme.footerLabelCell(signatureLabel, 1));
        table.addCell(theme.signatureLineCell(sigLineSpan));
        table.addCell(theme.footerLabelCell(dateLabel, 1));
        table.addCell(theme.signatureLineCell(dateLineSpan));
    }

    private int widestColumnCount(ReportProjectionDTO report) {
        int widest = 0;
        for (ReportSectionDTO section : report.getSections()) {
            SectionLayoutDef layout = layoutResolver.resolve(section);
            widest = Math.max(widest, layoutResolver.flatten(layout).size());
        }
        return widest + 1;
    }
}
