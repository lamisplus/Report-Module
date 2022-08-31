package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.lamisplus.modules.report.service.ExcellUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RadetService {


    public ByteArrayOutputStream generateRadet(Long facilityId, LocalDate start, LocalDate end) {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); Workbook workbook = new XSSFWorkbook()) {
            Sheet sh = workbook.createSheet("Patient-line-list");
            List<String> columnHeadings = getRadetColumnHeadings();
            Font headerFont = getFont(workbook);
            CellStyle headerStyle = getCellStyle(workbook, headerFont);
            Row headerRow = sh.createRow(0);
            createHeader(columnHeadings, headerStyle, headerRow);
            fillData(workbook, sh, facilityId);
            workbook.write(baos);
            LOG.info("Completed {}", "Completed");
            return baos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fillData(Workbook workbook, Sheet sh, Long facilityId) {

    }




    @NotNull
    private List<String> getRadetColumnHeadings() {
        return Arrays.asList(
                "S/No.",
                "State",
                "LGA",
                "Facility Name",
                "Patient Id",
                "Hospital Num",
                "Unique ID",
                "Sex",
                "Current Weight (kg)",
                "Date Birth (yyyy-mm-dd)",
                "ART Start Date (yyyy-mm-dd)",
                "Last Pickup Date (yyyy-mm-dd)",
                "Months of ARV Refill",
                "Date of TPT Start (yyyy-mm-dd)",
                "TPT Type",
                "TPT Completion date (yyyy-mm-dd)",
                "Regimen Line at ART Start",
                "Regimen at ART Start",
                "Current Regimen Line",
                "Current ART Regimen",
                "Date of Regimen Switch/ Substitution",
                "Pregnancy Status",
                "Date of Full Disclosure (yyyy-mm-dd)",
                "Date Enrolled on OTZ (yyyy-mm-dd)",
                "Number of Support Group (OTZ Club) meeting attended",
                "Number of OTZ Modules completed",
                "Date of Viral Load Sample Collection (yyyy-mm-dd)",
                "Current Viral Load (c/ml)",
                "Date of Current Viral Load (yyyy-mm-dd)",
                "Viral Load Indication",
                "VL Result After VL Sample Collection (c/ml)",
                "Date of VL Result After VL Sample Collection (yyyy-mm-dd)",
                "Previous ART Status",
                "Confirmed Date of Previous ART Status",
                "Current ART Status",
                "Date of Current ART Status",
                "RTT",
                "If Dead, Cause of Dead",
                "VA Cause of Dead",
                "If Transferred out, new Facility",
                "ART Enrollment Setting",
                "Date Commenced DMOC (yyyy-mm-dd)",
                "Type of DMOC",
                "Date of Return of DMOC Client to Facility (yyyy-mm-dd)",
                "Date of Commencement of EAC (yyyy-mm-dd)",
                "Number of EAC Sessions Completed",
                "Date of 3rd EAC Completion (yyyy-mm-dd)",
                "Date of Extended EAC Completion (yyyy-mm-dd)",
                "Date of Repeat Viral Load - Post EAC VL Sample Collected (yyyy-mm-dd)",
                "Co-morbidities",
                "Date of Cervical Cancer Screening (yyyy-mm-dd)",
                "Cervical Cancer Screening Type",
                "Cervical Cancer Screening Method",
                "Result of Cervical Cancer Screening",
                "Date of Precancerous Lesions Treatment (yyyy-mm-dd)",
                "Date Returned to Facility (yyyy-mm-dd)",
                "Precancerous Lesions Treatment Methods",
                "Date Biometrics Enrolled (yyyy-mm-dd)",
                "Valid Biometrics Enrolled?",
                "Case-manager"
        );
    }
}
