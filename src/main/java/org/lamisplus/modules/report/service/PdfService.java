package org.lamisplus.modules.report.service;

import com.itextpdf.text.Font;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;


@Slf4j
@AllArgsConstructor
@Service
public class PdfService {

    public byte[] generatePdf(List<Map<String, Object>> data,
                              List<String> headProperty,
                              List<String> headerLabels,
                              String title) throws DocumentException {

        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();

        addSummaryForm(document, headProperty, headerLabels, title);

        PdfPTable table = createTableWithData(data);

        document.add(table);

        document.close();

        return baos.toByteArray();
    }


    public void addSummaryForm(Document document, List<String> headProperty, List<String> headerLabels, String titleDocument) throws DocumentException {

        Font titleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Font labelFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);

        Paragraph title = new Paragraph(titleDocument, titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3});

        List<String> labels = headerLabels;

        List<String> dataValues = headProperty;

        for (int i = 0; i < labels.size(); i++) {
            PdfPCell labelCell = new PdfPCell(new Phrase(labels.get(i), labelFont));
            labelCell.setBorder(Rectangle.NO_BORDER);
            labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

            PdfPCell dataCell = new PdfPCell(new Phrase(dataValues.get(i), dataFont));
            dataCell.setBorderWidth(2f);
            dataCell.setFixedHeight(20f);
            dataCell.setBorderColor(BaseColor.BLACK);

            table.addCell(labelCell);
            table.addCell(dataCell);
        }

        document.add(table);
    }

    private PdfPTable createTableWithData(List<Map<String, Object>> rowData)  {

        PdfPTable table = new PdfPTable((Constants.IDENTIFIERS.size() + (Constants.AGE_GROUPS.size() * 2) + 1));
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        addTableHeader(table);

        addRows(table, rowData);

        return table;
    }

    private void addTableHeader(PdfPTable table)  {
        Font fontBold = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.BLACK);  // Bold font for main headers
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);  // Normal font for age group cells
        List<String> IDENTIFIERS = Constants.IDENTIFIERS;
        List<String> GENDER_HEADERS = Constants.GENDER_HEADERS;
        List<String> AGE_GROUPS = Constants.AGE_GROUPS;

        for (String identifier : IDENTIFIERS) {
            PdfPCell cell = new PdfPCell(new Phrase(identifier, fontBold));
            cell.setRowspan(2);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }

        for (String gender : GENDER_HEADERS) {
            PdfPCell cell = new PdfPCell(new Phrase(gender, fontBold));
            int colspan = gender.equals("Total") ? 1 : AGE_GROUPS.size();
            cell.setColspan(colspan);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(new BaseColor(102, 0, 102));
            table.addCell(cell);
        }

        for (String gender : GENDER_HEADERS) {
            if (!gender.equals("Total")) {
                for (String ageGroup : AGE_GROUPS) {
                    PdfPCell cell = new PdfPCell(new Phrase(ageGroup, fontNormal));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(new BaseColor(153, 204, 255));  // Light blue background for age groups
                    table.addCell(cell);
                }
            } else {
                PdfPCell totalCell = new PdfPCell(new Phrase(""));
                table.addCell(totalCell);
            }
        }
    }

    private void addRows(PdfPTable table, List<Map<String, Object>> rowData) {
        Font dataFont = new Font(Font.FontFamily.HELVETICA, 7);

        int columnIndex = 0;

        for (Map<String, Object> row : rowData) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                Object cellValue = entry.getValue();
                String cellData = (cellValue == null) ? "N/A" : cellValue.toString();

                PdfPCell dataCell = new PdfPCell(new Phrase(cellData, dataFont));
                dataCell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                dataCell.setPadding(5);
                if (cellValue == null) {
                    dataCell.setBackgroundColor(BaseColor.BLACK);
                }
                table.addCell(dataCell);
                columnIndex++;
            }
        }
    }
}
