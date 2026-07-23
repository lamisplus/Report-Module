package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
import org.lamisplus.modules.report.domain.dto.ReportRowDTO;
import org.lamisplus.modules.report.domain.dto.ReportSectionDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class MsfService {

    public byte[] generate(ReportProjectionDTO report) {

        try (
                XSSFWorkbook workbook =
                        new XSSFWorkbook();
                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()
        ) {

            Sheet sheet =
                    workbook.createSheet("HTS MSF Report");

            ExcelStyles styles =
                    new ExcelStyles(workbook);

            int rowIndex = 0;

            rowIndex =
                    writeReportHeader(
                            sheet,
                            styles,
                            report,
                            rowIndex);

            for (ReportSectionDTO section :
                    report.getSections()) {

                rowIndex++;

                rowIndex =
                        writeSection(
                                sheet,
                                styles,
                                section,
                                rowIndex);
            }

            for (int i = 0; i < 30; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to generate report",
                    e);
        }
    }

    private int writeReportHeader(
            Sheet sheet,
            ExcelStyles styles,
            ReportProjectionDTO report,
            int rowIndex) {

        Row titleRow =
                sheet.createRow(rowIndex++);

        Cell titleCell =
                titleRow.createCell(0);

        titleCell.setCellValue(
                report.getReportTitle());

        titleCell.setCellStyle(
                styles.getTitle());

        sheet.addMergedRegion(
                new CellRangeAddress(
                        titleRow.getRowNum(),
                        titleRow.getRowNum(),
                        0,
                        15));

        Row stateRow = sheet.createRow(rowIndex++);
        stateRow.createCell(0).setCellValue("State:");
        stateRow.createCell(1).setCellValue(
                report.getState() == null ? "" : report.getState());

        Row lgaRow = sheet.createRow(rowIndex++);
        lgaRow.createCell(0).setCellValue("LGA:");
        lgaRow.createCell(1).setCellValue(
                report.getLga() == null ? "" : report.getLga());

        Row facilityRow = sheet.createRow(rowIndex++);
        facilityRow.createCell(0).setCellValue("Facility:");
        facilityRow.createCell(1).setCellValue(
                report.getFacilityName() == null ? "" : report.getFacilityName());

        Row periodRow = sheet.createRow(rowIndex++);
        periodRow.createCell(0).setCellValue("Reporting Period:");
        periodRow.createCell(1).setCellValue(report.getReportingPeriod());

        rowIndex++;

        return rowIndex;
    }

    private int writeSection(
            Sheet sheet,
            ExcelStyles styles,
            ReportSectionDTO section,
            int rowIndex) {

        if ("AGE_GROUP".equalsIgnoreCase(
                section.getSectionType())) {

            return writeAgeGroupSection(
                    sheet,
                    styles,
                    section,
                    rowIndex);
        }

        if ("RECENCY".equalsIgnoreCase(
                section.getSectionType())) {

            return writeRecencySection(
                    sheet,
                    styles,
                    section,
                    rowIndex);
        }

        if ("ACUTE_HIV".equalsIgnoreCase(
                section.getSectionType())) {

            return writeAcuteSection(
                    sheet,
                    styles,
                    section,
                    rowIndex);
        }

        if ("KEY_POPULATION".equalsIgnoreCase(
                section.getSectionType())) {

            return writeKeyPopulationSection(
                    sheet,
                    styles,
                    section,
                    rowIndex);
        }

        return rowIndex;
    }

    private int writeAgeGroupSection(
            Sheet sheet,
            ExcelStyles styles,
            ReportSectionDTO section,
            int rowIndex) {

        // SECTION TITLE
        Row titleRow = sheet.createRow(rowIndex++);

        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(section.getTitle());
        titleCell.setCellStyle(styles.getSectionTitle());

        sheet.addMergedRegion(
                new CellRangeAddress(
                        titleRow.getRowNum(),
                        titleRow.getRowNum(),
                        0,
                        10
                )
        );

        // HEADER ROWS
        Row groupRow = sheet.createRow(rowIndex++);
        Row subHeaderRow = sheet.createRow(rowIndex++);

        // Age Group
        Cell ageGroupHeader = groupRow.createCell(0);
        ageGroupHeader.setCellValue("Age Group");
        ageGroupHeader.setCellStyle(styles.getHeader());

        sheet.addMergedRegion(
                new CellRangeAddress(
                        groupRow.getRowNum(),
                        subHeaderRow.getRowNum(),
                        0,
                        0
                )
        );

        String[] groups = {
                "In-patient",
                "CT",
                "Out-patient",
                "Others",
                "Community Others"
        };

        int column = 1;

        for (String group : groups) {

            Cell groupHeader = groupRow.createCell(column);
            groupHeader.setCellValue(group);
            groupHeader.setCellStyle(styles.getHeader());

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            groupRow.getRowNum(),
                            groupRow.getRowNum(),
                            column,
                            column + 1
                    )
            );

            Cell maleHeader = subHeaderRow.createCell(column);
            maleHeader.setCellValue("M");
            maleHeader.setCellStyle(styles.getHeader());

            Cell femaleHeader = subHeaderRow.createCell(column + 1);
            femaleHeader.setCellValue("F");
            femaleHeader.setCellStyle(styles.getHeader());

            column += 2;
        }

        // DATA ROWS
        for (ReportRowDTO dto : section.getRows()) {

            Row row = sheet.createRow(rowIndex++);

            boolean totalRow =
                    "TOTAL".equalsIgnoreCase(dto.getRowLabel())
                            || "Total".equalsIgnoreCase(dto.getRowLabel());

            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(dto.getRowLabel());
            labelCell.setCellStyle(
                    totalRow
                            ? styles.getHeader()
                            : styles.getLabel()
            );

            writeValue(row,1,dto, "inpatientM", styles, totalRow);
            writeValue(row,2,dto,"inpatientF", styles, totalRow);

            writeValue(row,3,dto,"ctM", styles, totalRow);
            writeValue(row,4,dto,"ctF", styles, totalRow);

            writeValue(row,5,dto,"outpatientM", styles, totalRow);
            writeValue(row,6,dto,"outpatientF", styles, totalRow);

            writeValue(row,7,dto,"othersM", styles, totalRow);
            writeValue(row,8,dto,"othersF", styles, totalRow);

            writeValue(row,9,dto,"communityM", styles, totalRow);
            writeValue(row,10,dto,"communityF", styles, totalRow);
        }

        return rowIndex;
    }

    private int writeRecencySection(
            Sheet sheet,
            ExcelStyles styles,
            ReportSectionDTO section,
            int rowIndex) {

        Row titleRow = sheet.createRow(rowIndex++);

        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(section.getTitle());
        titleCell.setCellStyle(styles.getSectionTitle());

        sheet.addMergedRegion(
                new CellRangeAddress(
                        titleRow.getRowNum(),
                        titleRow.getRowNum(),
                        0,
                        2
                )
        );

        Row headerRow = sheet.createRow(rowIndex++);

        Cell c0 = headerRow.createCell(0);
        c0.setCellValue("Recency Result");
        c0.setCellStyle(styles.getHeader());

        Cell c1 = headerRow.createCell(1);
        c1.setCellValue("Male");
        c1.setCellStyle(styles.getHeader());

        Cell c2 = headerRow.createCell(2);
        c2.setCellValue("Female");
        c2.setCellStyle(styles.getHeader());

        for (ReportRowDTO dto : section.getRows()) {

            Row row = sheet.createRow(rowIndex++);

            boolean totalRow =
                    "TOTAL".equalsIgnoreCase(dto.getRowLabel());

            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(dto.getRowLabel());
            labelCell.setCellStyle(
                    totalRow
                            ? styles.getHeader()
                            : styles.getLabel());

            writeValue(row, 1, dto, "totalM", styles, totalRow);
            writeValue(row, 2, dto, "totalF", styles, totalRow);
        }

        return rowIndex;
    }

    private int writeAcuteSection(
            Sheet sheet,
            ExcelStyles styles,
            ReportSectionDTO section,
            int rowIndex) {

        Row titleRow = sheet.createRow(rowIndex++);

        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(section.getTitle());
        titleCell.setCellStyle(styles.getSectionTitle());

        sheet.addMergedRegion(
                new CellRangeAddress(
                        titleRow.getRowNum(),
                        titleRow.getRowNum(),
                        0,
                        2
                )
        );

        Row headerRow = sheet.createRow(rowIndex++);

        Cell h1 = headerRow.createCell(0);
        h1.setCellValue("Indicator");
        h1.setCellStyle(styles.getHeader());

        Cell h2 = headerRow.createCell(1);
        h2.setCellValue("Male");
        h2.setCellStyle(styles.getHeader());

        Cell h3 = headerRow.createCell(2);
        h3.setCellValue("Female");
        h3.setCellStyle(styles.getHeader());

        for (ReportRowDTO dto : section.getRows()) {

            Row row = sheet.createRow(rowIndex++);

            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(dto.getRowLabel());
            labelCell.setCellStyle(styles.getLabel());

            writeValue(row, 1, dto, "total_m", styles, false);
            writeValue(row, 2, dto, "total_f", styles, false);
        }

        return rowIndex;
    }

    private int writeKeyPopulationSection(
            Sheet sheet,
            ExcelStyles styles,
            ReportSectionDTO section,
            int rowIndex) {

        Row titleRow = sheet.createRow(rowIndex++);

        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(section.getTitle());
        titleCell.setCellStyle(styles.getSectionTitle());

        sheet.addMergedRegion(
                new CellRangeAddress(
                        titleRow.getRowNum(),
                        titleRow.getRowNum(),
                        0,
                        10
                )
        );

        Row groupRow = sheet.createRow(rowIndex++);
        Row subRow = sheet.createRow(rowIndex++);

        Cell resultHeader = groupRow.createCell(0);
        resultHeader.setCellValue("Result");
        resultHeader.setCellStyle(styles.getHeader());

        sheet.addMergedRegion(
                new CellRangeAddress(
                        groupRow.getRowNum(),
                        subRow.getRowNum(),
                        0,
                        0
                )
        );

        String[] groups = {
                "MSM",
                "PWID",
                "Sex Worker",
                "PPOCS"
        };

        int col = 1;

        for (String group : groups) {

            Cell groupHeader =
                    groupRow.createCell(col);

            groupHeader.setCellValue(group);
            groupHeader.setCellStyle(styles.getHeader());

            sheet.addMergedRegion(
                    new CellRangeAddress(
                            groupRow.getRowNum(),
                            groupRow.getRowNum(),
                            col,
                            col + 1
                    )
            );

            Cell male =
                    subRow.createCell(col);

            male.setCellValue("M");
            male.setCellStyle(styles.getHeader());

            Cell female =
                    subRow.createCell(col + 1);

            female.setCellValue("F");
            female.setCellStyle(styles.getHeader());

            col += 2;
        }

        Cell agywHeader =
                groupRow.createCell(col);

        agywHeader.setCellValue("AGYW");
        agywHeader.setCellStyle(styles.getHeader());

        Cell totalHeader =
                groupRow.createCell(col + 1);

        totalHeader.setCellValue("Total");
        totalHeader.setCellStyle(styles.getHeader());

        for (ReportRowDTO dto : section.getRows()) {

            Row row = sheet.createRow(rowIndex++);

            boolean totalRow =
                    "TOTAL".equalsIgnoreCase(dto.getRowLabel());

            Cell labelCell = row.createCell(0);
            labelCell.setCellValue(dto.getRowLabel());
            labelCell.setCellStyle(
                    totalRow
                            ? styles.getHeader()
                            : styles.getLabel());

            writeValue(row, 1, dto, "msm_m", styles, totalRow);
            writeValue(row, 2, dto, "msm_f", styles, totalRow);

            writeValue(row, 3, dto, "pwid_m", styles, totalRow);
            writeValue(row, 4, dto, "pwid_f", styles, totalRow);

            writeValue(row, 5, dto, "sex_worker_m", styles, totalRow);
            writeValue(row, 6, dto, "sex_worker_f", styles, totalRow);

            writeValue(row, 7, dto, "ppocs_m", styles, totalRow);
            writeValue(row, 8, dto, "ppocs_f", styles, totalRow);

            writeValue(row, 9, dto, "agyw", styles, totalRow);
            writeValue(row, 10, dto, "total", styles, totalRow);
        }

        return rowIndex;
    }

    private void writeValue(
            Row row,
            int column,
            ReportRowDTO dto,
            String key,
            ExcelStyles styles,
            boolean totalRow) {

        BigDecimal value =
                dto.getValues().get(key);

        Cell cell = row.createCell(column);

        cell.setCellValue(
                value == null
                        ? 0D
                        : value.doubleValue());

        cell.setCellStyle(
                totalRow
                        ? styles.getHeader()
                        : styles.getValue()
        );
    }
}