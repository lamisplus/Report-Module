package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.lamisplus.modules.report.utils.ReportUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PMTCTReportService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private ReportUtils reportUtils;

    @SneakyThrows
    public ByteArrayOutputStream generateReport(Long facilityIds, LocalDate cohortStart, LocalDate cohortEnd) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DateFormat dateFormatExcel = new SimpleDateFormat("yyyy-MM-dd");
        Workbook workbook = new SXSSFWorkbook(100);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();
        CellStyle style = ReportUtils.getCellStyle(workbook);

        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));

        try {
            int[] rowNum = {0};
            int[] cellNum = {0};
            Row[] row = {sheet.createRow(rowNum[0]++)};
            Cell[] cell = {row[0].createCell(cellNum[0]++)};
            cell[0].setCellValue("State");
            cell[0].setCellStyle(style);
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("LGA");
            cell[0].setCellStyle(style);
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Facility");
            cell[0].setCellStyle(style);
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Patient ID");
            cell[0].setCellStyle(style);
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Hospital Num");
            cell[0].setCellStyle(style);
            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Unique ID");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of Birth");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Marital Status");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("LMP Date");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Pregnancy Status");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("ART Start Date");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of ANC Registration");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("ANC Setting (Facility, Community)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Gravidity");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Parity");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Gestational Age (Weeks) @ First ANC visit");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("GA at last visit");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Last VL result before 32 weeks GA");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Viral load at first ANC");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of last VL before 32 weeks GA");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("VL Indication");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Due Date for VL sample collection @32 weeks");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of Sample collection at 32 - 36 weeks");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("VL result @ 32-36 weeks");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Expected Date of Delivery");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of Delivery (mm-dd-yyyy)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Place of Delivery");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Sex - Child");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Birth Weight");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of ARV Prophylaxis Commencement");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Type of prophylaxis (ePNP or regular)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of First DBS (@ Birth)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Result of first DBS");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date of CTX (Cotrimoxazole) commencement");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Second DBS: Date of Second  DBS (@6weeks)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Second DBS: Result of second DBS");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Third DBS: Date of third DBS (6 weeks after cessation of breastfeeding)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Third DBS: Result of third DBS ");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Sample collection date for Confirmatory DBS (if DBS positive)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Result of Confirmatory DBS");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date (Child Final Outcome)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Result (Child Final Outcome)");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Date Linked to ART");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Child Unique ID");
            cell[0].setCellStyle(style);

            cell[0] = row[0].createCell(cellNum[0]++);
            cell[0].setCellValue("Comment");
            cell[0].setCellStyle(style);

        } catch (Exception e){
            LOG.error("Error found **** {}", e.getMessage());
        }

        return baos;
    }
}
