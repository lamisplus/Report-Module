package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.lamisplus.modules.report.service.coverter.vm.DeduplicationReportEntry;
import org.lamisplus.modules.report.utils.ReportUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeduplicationReportService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @SneakyThrows
    public ByteArrayOutputStream generateReport(Long patientId) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Workbook workbook = new SXSSFWorkbook(100);  // turn off auto-flushing and accumulate all rows in memory
        Sheet sheet = workbook.createSheet();
        CellStyle style = ReportUtils.getCellStyle(workbook);

        int rowNum = 0;
        int cellNum = 0;
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(cellNum);
        cell.setCellValue("S/N");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient ID");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient ID");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient Hospital Number");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient Hospital Number");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient Unique Id");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient Unique Id");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient Surname");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient Surname");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient First Name");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient First Name");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient Sex");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient Sex");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient Date Of Birth");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient Date Of Birth");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Enrolled Patient Finger Type");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Duplicate Patient Finger Type");
        cell.setCellStyle(style);
        cell = row.createCell(++cellNum);
        cell.setCellValue("Matching Score");
        cell.setCellStyle(style);

        List<DeduplicationReportEntry> entries = new ArrayList<>();

        String query = "select mp.enrolled_patient_id, mp.duplicate_patient_id, " +
                "    epp.hospital_number as enrolled_patient_hospital_number, " +
                "    dpp.hospital_number as duplicate_patient_hospital_number, " +
                "    eh.unique_id as enrolled_patient_unique_id, " +
                "    dh.unique_id as duplicate_patient_unique_id, " +
                "    epp.surname as enrolled_patient_surname, " +
                "    dpp.surname as duplicate_patient_surname, " +
                "    epp.first_name as enrolled_patient_first_name, " +
                "    dpp.first_name as duplicate_patient_first_name, " +
                "    epp.sex as enrolled_patient_sex, " +
                "    dpp.sex as duplicate_patient_sex, " +
                "    epp.date_of_birth as enrolled_patient_date_of_birth, " +
                "    dpp.date_of_birth as duplicate_patient_date_of_birth, " +
                "    mp.enrolled_patient_finger_type, mp.duplicate_patient_finger_type, " +
                "    mp.score " +
                "from matched_pair mp " +
                "join patient_person epp on epp.uuid = mp.enrolled_patient_id " +
                "join patient_person dpp on dpp.uuid = mp.duplicate_patient_id " +
                "join hiv_enrollment eh on eh.person_uuid = mp.enrolled_patient_id " +
                "join hiv_enrollment dh on dh.person_uuid = mp.duplicate_patient_id " +
                "where mp.enrolled_patient_id = (select uuid from patient_person where id = :patientId)";

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("patientId", patientId);

        namedParameterJdbcTemplate.query(query, namedParameters, rs -> {
            while (rs.next()) {
                String enrolledPatientId = rs.getString("enrolled_patient_id");
                String duplicatePatientId = rs.getString("duplicate_patient_id");
                String enrolledPatientHospitalNumber = rs.getString("enrolled_patient_hospital_number");
                String duplicatePatientHospitalNumber = rs.getString("duplicate_patient_hospital_number");
                String enrolledPatientUniqueId = rs.getString("enrolled_patient_unique_id");
                String duplicatePatientUniqueId = rs.getString("duplicate_patient_unique_id");
                String enrolledPatientSurname = rs.getString("enrolled_patient_surname");
                String duplicatePatientSurname = rs.getString("duplicate_patient_surname");
                String enrolledPatientFirstName = rs.getString("enrolled_patient_first_name");
                String duplicatePatientFirstName = rs.getString("duplicate_patient_first_name");
                String enrolledPatientSex = rs.getString("enrolled_patient_sex");
                String duplicatePatientSex = rs.getString("duplicate_patient_sex");
                String enrolledPatientDateOfBirth = rs.getString("enrolled_patient_date_of_birth");
                String duplicatePatientDateOfBirth = rs.getString("duplicate_patient_date_of_birth");
                String enrolledPatientFingerType = rs.getString("enrolled_patient_finger_type");
                String duplicatePatientFingerType = rs.getString("duplicate_patient_finger_type");
                Integer matchingScore = rs.getInt("score");



                DeduplicationReportEntry deduplicationReportEntry = DeduplicationReportEntry
                        .builder()
                        .enrolledPatientId(enrolledPatientId)
                        .duplicatePatientId(duplicatePatientId)
                        .enrolledPatientHospitalNumber(enrolledPatientHospitalNumber)
                        .duplicatePatientHospitalNumber(duplicatePatientHospitalNumber)
                        .enrolledPatientUniqueId(enrolledPatientUniqueId)
                        .duplicatePatientUniqueId(duplicatePatientUniqueId)
                        .enrolledPatientSurname(enrolledPatientSurname)
                        .duplicatePatientSurname(duplicatePatientSurname)
                        .enrolledPatientFirstName(enrolledPatientFirstName)
                        .duplicatePatientFirstName(duplicatePatientFirstName)
                        .enrolledPatientSex(enrolledPatientSex)
                        .duplicatePatientSex(duplicatePatientSex)
                        .enrolledPatientDateOfBirth(enrolledPatientDateOfBirth)
                        .duplicatePatientDateOfBirth(duplicatePatientDateOfBirth)
                        .enrolledPatientFingerType(enrolledPatientFingerType)
                        .duplicatePatientFingerType(duplicatePatientFingerType)
                        .matchingScore(matchingScore)
                        .build();
                entries.add(deduplicationReportEntry);
            }
            return null;
        });

        buildSheet(workbook, sheet, entries);
        try {
            workbook.write(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos;
    }

    private void buildSheet(Workbook workbook, Sheet sheet, List<DeduplicationReportEntry> entries) {
        AtomicInteger rowNum = new AtomicInteger(0);
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        entries.forEach(entry -> {
            int cellNum = 0;
            Row row = sheet.createRow(rowNum.incrementAndGet());
            Cell cell = row.createCell(cellNum);
            cell.setCellValue(rowNum.get());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientId());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientId());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientHospitalNumber());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientHospitalNumber());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientUniqueId());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientUniqueId());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientSurname());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientSurname());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientFirstName());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientFirstName());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientSex());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientSex());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientDateOfBirth());
            cell.setCellStyle(dateStyle);
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientDateOfBirth());
            cell.setCellStyle(dateStyle);
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getEnrolledPatientFingerType());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getDuplicatePatientFingerType());
            cell = row.createCell(++cellNum);
            cell.setCellValue(entry.getMatchingScore());

        });
    }
}
