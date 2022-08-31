package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.service.ApplicationCodesetService;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.repositories.ARTClinicalRepository;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.report.domain.RadetDto;
import org.lamisplus.modules.triage.repository.VitalSignRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.lamisplus.modules.report.service.ExcellUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class RadetService {

    private final ArtPharmacyRepository artPharmacyRepository;

    private final OrganisationUnitService organisationUnitService;

    private final ApplicationCodesetService applicationCodesetService;

    private final ARTClinicalRepository artClinicalRepository;

    private final VitalSignRepository vitalSignRepository;

    public ByteArrayOutputStream generateRadet(Long facilityId, LocalDate start, LocalDate end) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); Workbook workbook = new XSSFWorkbook()) {
            Sheet sh = workbook.createSheet("Patient-line-list");
            List<String> columnHeadings = getRadetColumnHeadings();
            Font headerFont = getFont(workbook);
            CellStyle headerStyle = getCellStyle(workbook, headerFont);
            Row headerRow = sh.createRow(0);
            createHeader(columnHeadings, headerStyle, headerRow);
            fillData(workbook, sh, facilityId, start, end);
            workbook.write(baos);
            FileOutputStream fileOut = new FileOutputStream ("runtime/radet.xlsx");
            workbook.write (fileOut);
            LOG.info("Completed {}", "Completed");
            return baos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void fillData(Workbook workbook, Sheet sh, Long facilityId, LocalDate start, LocalDate end) {
        Set<Person> radetPatients = artPharmacyRepository.findAll()
                .stream()
                .map(ArtPharmacy::getPerson)
                .collect(Collectors.toSet());
        Set<RadetDto> radetData = artPharmacyRepository.findAll()
                .stream()
                .filter(artPharmacy -> radetPatients.contains(radetPatients))
                .filter(artPharmacy -> artPharmacy.getVisitDate().equals(start)
                        || artPharmacy.getVisitDate().equals(end)
                        && (artPharmacy.getVisitDate().isAfter(start) && artPharmacy.getVisitDate().isBefore(end)))
                .map(artPharmacy -> getRadetDto(facilityId, artPharmacy))
                .collect(Collectors.toSet());
        radetData.forEach(radetData1 -> {
            int rowNum = 1;
            LOG.info("rowNum-before {}", rowNum);
            Row row = sh.createRow(rowNum++);
            LOG.info("rowNum-after{}", rowNum);
            DateFormat dateFormatExcel = new SimpleDateFormat("yyyy-MM-dd");
            populateDemographicColunms(radetData1, row, dateFormatExcel);
        });


    }

    private RadetDto getRadetDto(Long facilityId, ArtPharmacy artPharmacy) {
        OrganisationUnit facility = organisationUnitService.getOrganizationUnit(facilityId);
        String facilityName = facility.getName();
        Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId();
        OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit(lgaIdOfTheFacility);
        Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId();
        OrganisationUnit state = organisationUnitService.getOrganizationUnit(stateId);
        RadetDto radetDto = new RadetDto();
        radetDto.setState(state.getName());
        radetDto.setLga(lgaOrgUnitOfFacility.getName());
        radetDto.setFacilityName(facilityName);
        Person person = artPharmacy.getPerson();
        radetDto.setPatientId(person.getUuid());
        radetDto.setHospitalNum(person.getHospitalNumber());
        radetDto.setDateBirth(Date.valueOf(person.getDateOfBirth()));
        radetDto.setAge(1);
        radetDto.setSex(person.getSex());
        return radetDto;


    }


    private static void populateDemographicColunms(RadetDto datum, Row row, DateFormat dateFormatExcel) {
        row.createCell(1).setCellValue(datum.getFacilityName());
        row.createCell(2).setCellValue(datum.getLga());
        row.createCell(3).setCellValue(datum.getState());
        row.createCell(4).setCellValue(datum.getPatientId());
        row.createCell(5).setCellValue(datum.getHospitalNum());
        row.createCell(6).setCellValue(datum.getUniqueID());
        row.createCell(9).setCellValue(dateFormatExcel.format(datum.getDateBirth()));
        row.createCell(10).setCellValue(datum.getAge());
        row.createCell(11).setCellValue(datum.getSex());
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
                "Age",
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
