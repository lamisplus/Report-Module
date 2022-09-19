package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.dto.ApplicationCodesetDTO;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.service.ApplicationCodesetService;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.entity.ARTClinical;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.domain.entity.HivEnrollment;
import org.lamisplus.modules.hiv.domain.entity.Regimen;
import org.lamisplus.modules.hiv.repositories.ARTClinicalRepository;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.hiv.repositories.HivEnrollmentRepository;
import org.lamisplus.modules.hiv.repositories.RegimenRepository;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.triage.domain.entity.VitalSign;
import org.lamisplus.modules.triage.repository.VitalSignRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PatientReportService {

    private final HivEnrollmentRepository hivEnrollmentRepository;

    private final OrganisationUnitService organisationUnitService;

    private final ApplicationCodesetService applicationCodesetService;

    private final ARTClinicalRepository artClinicalRepository;

    private final VitalSignRepository vitalSignRepository;

    private final ArtPharmacyRepository artPharmacyRepository;


    private final RegimenRepository regimenRepository;

    public ByteArrayOutputStream generatePatientLineList(Long facilityId) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); Workbook workbook = new XSSFWorkbook()) {
            Sheet sh = workbook.createSheet("Patient-line-list");
            List<String> columnHeadings = getPatientLineListColumnHeadings();
            Font headerFont = ExcellUtil.getFont(workbook);
            CellStyle headerStyle = ExcellUtil.getCellStyle(workbook, headerFont);
            Row headerRow = sh.createRow(0);
            ExcellUtil.createHeader(columnHeadings, headerStyle, headerRow);
            fillData(workbook, sh, facilityId);
            workbook.write(baos);
            LOG.info("Completed {}", "Completed");
            return baos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @NotNull
    private List<String> getPatientLineListColumnHeadings() {
        return Arrays.asList(
                "Facility Id",
                "Facility Name",
                "LGA",
                "State",
                "Patient Id",
                "Hospital Num",
                "Unique ID",
                "Surname",
                "Other Name",
                "Date Birth",
                "Age",
                "Gender",
                "Marital Status",
                "Education",
                "Occupation",
                "State of Residence",
                "Lga of Residence",
                "Address",
                "Phone",
                "Archived",
                "Care Entry Point",
                "Date of Confirmed HIV Test (yyyy-mm-dd)",
                "Date Registration (yyyy-mm-dd)",
                "Status at Registration",
                "ART Start Date (yyyy-mm-dd)",
                "Baseline CD4",
                "Baseline CDP",
                "Systolic BP",
                "Diastolic BP",
                "Baseline Weight (kg)",
                "Baseline Height (cm)",
                "Baseline Clinic Stage",
                "Baseline Functional Status",
                "Date Current Status (yyyy-mm-dd)",
                "Current Status",
                "Current Weight (kg)",
                "Current Height (cm)",
                "Current Systolic BP",
                "Current Diastolic BP",
                "Adherence",
                // "Waist Circumference(cm)",
                "First Regimen Line",
                "First Regimen",
                // "First NRTI",
                // "First NNRTI",
                "Current Regimen Line",
                "Current Regimen",
                // "Current NRTI",
                // "Current NNRTI",
                "Date Substituted/Switched (yyyy-mm-dd)",
                "Date of Last Refill",
                "Last Refill Duration (days)",
                "Date of Next Refill (yyyy-mm-dd)",
                "DMOC Type",
                "Date Devolved (yyyy-mm-dd)",
                "Last Clinic Stage",
                "Date of Last Clinic (yyyy-mm-dd)",
                "Date of Next Clinic (yyyy-mm-dd)",
                // "Last CD4",
                // "Last CD4p",
                // "Date of Last CD4 (yyyy-mm-dd)",
                //"Last Visitec CD4",
                //"Date of Last Visitec CD4 (yyyy-mm-dd)",
                //"Last TB-LAM",
                //"Date of Last TB-LAM (yyyy-mm-dd)",
                // "Last Cryptococcal Antigen",
                //"Date of Last Cryptococcal Antigen (yyyy-mm-dd)",
                "Last Viral Load",
                "Date of Last Viral Load (yyyy-mm-dd)",
                "Viral Load Due Date",
                "Viral Load Indication",
                // "Date Returned to Facility (yyyy-mm-dd)",
                // "Co-morbidities",
                "Case-manager"
        );
    }

    private void fillData(Workbook workbook, Sheet sh, Long facilityId) {
        List<PatientLineListDto> data = getPatientLineList(facilityId);
        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        CellStyle numericStyle = workbook.createCellStyle();
        numericStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0"));
        int rowNum = 1;
        for (PatientLineListDto datum : data) {
            rowNum = populateTheExcelCellValues(sh, rowNum, datum);
        }
    }

    private int populateTheExcelCellValues(Sheet sh, int rowNum, PatientLineListDto datum) {
        LOG.info("rowNum-before {}", rowNum);
        Row row = sh.createRow(rowNum++);
        LOG.info("rowNum-after{}", rowNum);
        DateFormat dateFormatExcel = new SimpleDateFormat("yyyy-MM-dd");
        populateDemographicColunms(datum, row, dateFormatExcel);
        populateClinicVisitColums(datum, row, dateFormatExcel);
        row.createCell(39).setCellValue("");
        String regimenLine = datum.getFirstRegimenLine() == null ? "" : datum.getFirstRegimenLine();
        String regimen = datum.getFirstRegimen() == null ? "" : datum.getFirstRegimen();
        row.createCell(40).setCellValue(regimenLine);
        row.createCell(41).setCellValue(regimen);
        row.createCell(42).setCellValue(datum.getCurrentRegimenLine() == null ? "" : datum.getCurrentRegimenLine());
        row.createCell(43).setCellValue(datum.getCurrentRegimen() == null ? "" : datum.getCurrentRegimen());
        if (datum.getDateOfLastRefill() != null) {
            row.createCell(45).setCellValue(dateFormatExcel.format(datum.getDateOfLastRefill()));
        }
        if (datum.getLastRefillDuration() != null) {
            row.createCell(46).setCellValue(datum.getLastRefillDuration());
        }
        if (datum.getDateOfNextRefill() != null) {
            row.createCell(47).setCellValue(dateFormatExcel.format(datum.getDateOfNextRefill()));
        }
        row.createCell(48).setCellValue(datum.getDmocType() == null ? "" : datum.getDmocType());
        Date dateDevolved = datum.getDateDevolved();
        if (dateDevolved != null) {
            row.createCell(49).setCellValue(dateFormatExcel.format(dateDevolved));
        }
        row.createCell(50).setCellValue(datum.getLastClinicStage() == null ? "" : datum.getLastClinicStage());
        Date dateOfLastClinic = datum.getDateOfLastClinic();
        if (dateOfLastClinic != null) {
            row.createCell(51).setCellValue(dateFormatExcel.format(dateOfLastClinic));
        }
        Date dateOfNextClinic = datum.getDateOfNextClinic();
        if (dateOfNextClinic != null) {
            row.createCell(52).setCellValue(dateFormatExcel.format(dateOfNextClinic));

        }
        return rowNum;
    }


    private void populateClinicVisitColums(PatientLineListDto datum, Row row, DateFormat dateFormatExcel) {
        Date artStartDate = datum.getArtStartDate();
        if (artStartDate != null) {
            row.createCell(24).setCellValue(dateFormatExcel.format(artStartDate));
        }
        processAndSetCD4Detail(row, datum);
        Double systolicBP = datum.getSystolicBP();
        if (systolicBP != null) {
            row.createCell(27).setCellValue(systolicBP);
        }
        Double diastolicBP = datum.getDiastolicBP();
        if (diastolicBP != null) {
            row.createCell(28).setCellValue(diastolicBP);
        }
        Double baselineWeight = datum.getBaselineWeight();
        if (baselineWeight != null) {
            row.createCell(29).setCellValue(baselineWeight);
        }
        Double baselineHeight = datum.getBaselineHeight();
        if (baselineHeight != null) {
            row.createCell(30).setCellValue(baselineHeight);
        }
        row.createCell(31).setCellValue(datum.getBaselineClinicStage() == null ? "" : datum.getBaselineClinicStage());
        row.createCell(32).setCellValue(datum.getBaselineFunctionalStatus() == null ? "" : datum.getBaselineFunctionalStatus());
        if (datum.getDateCurrentStatus() != null) {
            row.createCell(33).setCellValue(dateFormatExcel.format(datum.getDateCurrentStatus()));
        }
        row.createCell(34).setCellValue(datum.getCurrentStatus() == null ? "" : datum.getCurrentStatus());

        Double currentWeight = datum.getCurrentWeight();
        if (currentWeight != null) {
            row.createCell(35).setCellValue(currentWeight);
        }
        Double currentHeight = datum.getCurrentHeight();
        if (currentHeight != null) {
            row.createCell(36).setCellValue(currentHeight);
        }
        Double currentSystolicBP = datum.getCurrentSystolicBP();
        if (currentSystolicBP != null) {
            row.createCell(37).setCellValue(currentSystolicBP);
        }
        Double currentDiastolicBP = datum.getCurrentDiastolicBP();
        if (currentDiastolicBP != null) {
            row.createCell(38).setCellValue(currentDiastolicBP);
        }
    }


    private static void populateDemographicColunms(PatientLineListDto datum, Row row, DateFormat dateFormatExcel) {
        row.createCell(0).setCellValue(datum.getFacilityId());
        row.createCell(1).setCellValue(datum.getFacilityName());
        row.createCell(2).setCellValue(datum.getLga());
        row.createCell(3).setCellValue(datum.getState());
        row.createCell(4).setCellValue(datum.getPatientId());
        row.createCell(5).setCellValue(datum.getHospitalNum());
        row.createCell(6).setCellValue(datum.getUniqueID());
        row.createCell(7).setCellValue(datum.getSurname());
        row.createCell(8).setCellValue(datum.getOtherName());
        row.createCell(9).setCellValue(dateFormatExcel.format(datum.getDateBirth()));
        row.createCell(10).setCellValue(datum.getAge());
        row.createCell(11).setCellValue(datum.getSex());
        row.createCell(12).setCellValue(datum.getMaritalStatus());
        row.createCell(13).setCellValue(datum.getEducation());
        row.createCell(14).setCellValue(datum.getOccupation());
        row.createCell(15).setCellValue(datum.getStateOfResidence());
        row.createCell(16).setCellValue(datum.getLgaOfResidence());
        row.createCell(17).setCellValue(datum.getAddress());
        row.createCell(18).setCellValue(datum.getPhone());
        row.createCell(19).setCellValue(datum.getArchived());
        row.createCell(20).setCellValue(datum.getCareEntryPoint());
        row.createCell(21).setCellValue(dateFormatExcel.format(datum.getDateOfConfirmedHIVTest()));
        row.createCell(22).setCellValue(dateFormatExcel.format(datum.getDateOfRegistration()));
        row.createCell(23).setCellValue(datum.getStatusAtRegistration());
    }

    private void processAndSetCD4Detail(Row row, PatientLineListDto person) {
        Double baselineCD4 = person.getBaselineCD4();
        if (baselineCD4 != null) {
            row.createCell(25).setCellValue(baselineCD4);
        }
        Double baselineCDP = person.getBaselineCDP();
        if (baselineCDP != null) {

            row.createCell(26).setCellValue(baselineCDP);
        }
    }

    private Instant getInstant(LocalDate dateBirth) {
        return dateBirth.atStartOfDay(ZoneId.systemDefault()).toInstant();

    }





    private List<PatientLineListDto> getPatientLineList(Long facilityId) {
        return hivEnrollmentRepository.findAll()
                .stream()
                .filter(hivEnrollment -> hivEnrollment.getFacilityId().equals(facilityId))
                .map(this::getPatientLineListDto)
                .collect(Collectors.toList());


    }

    private PatientLineListDto getPatientLineListDto(HivEnrollment hivEnrollment) {
        Long facilityId = hivEnrollment.getFacilityId();
        OrganisationUnit facility = organisationUnitService.getOrganizationUnit(facilityId);
        Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId();
        OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit(lgaIdOfTheFacility);
        Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId();
        OrganisationUnit state = organisationUnitService.getOrganizationUnit(stateId);
        Person person = hivEnrollment.getPerson();
        LocalDate dateBirth = person.getDateOfBirth();
        LocalDate currentDate = LocalDate.now();
        Instant dateBirthOfInstant = getInstant(dateBirth);
        int age = Period.between(dateBirth, currentDate).getYears();
        Date finalDateOfBirth = Date.from(dateBirthOfInstant);
        JsonNode maritalStatus = person.getMaritalStatus();
        String fieldName = "display";
        String maritalStatusValue = maritalStatus.isNull() ? "" : maritalStatus.get(fieldName).asText();
        JsonNode education = person.getEducation();
        String educationValue = education.isNull() ? "" : education.get(fieldName).asText();
        JsonNode occupation = person.getEmploymentStatus();
        String occupationValue = education.isNull() ? "" : occupation.get(fieldName).asText();
        JsonNode address = person.getAddress();
        JsonNode address1 = address.get("address");
        Long stateOfResidenceId = null;
        Long lgaOfResidenceId = null;
        StringBuilder addressDetails = new StringBuilder();
        if (address1.isArray()) {
            JsonNode addressObject = address1.get(0);
            if (addressObject.hasNonNull("stateId") && addressObject.hasNonNull("district")) {
                stateOfResidenceId = addressObject.get("stateId").asLong();
                lgaOfResidenceId = addressObject.get("district").asLong();
                addressDetails.append(addressObject.get("city").asText());
                JsonNode town = addressObject.get("line");
                if (!town.isNull() && town.isArray()) {
                    for (JsonNode node : town) {
                        addressDetails.append(" " + node.asText());
                    }
                }
            }
        }
        OrganisationUnit stateOfResidency = organisationUnitService.getOrganizationUnit(stateOfResidenceId);
        OrganisationUnit lgaOfResidency = organisationUnitService.getOrganizationUnit(lgaOfResidenceId);
        JsonNode contactPoint = person.getContactPoint();
        StringBuilder phone = new StringBuilder();
        if (contactPoint.hasNonNull("contactPoint") && contactPoint.get("contactPoint").isArray()) {
            JsonNode phoneObject = contactPoint.get("contactPoint").get(1);
            String phoneValue = phoneObject == null ? "" : phoneObject.get("value").asText();
            phone.append(phoneValue);
        }
        String firstChar = addressDetails.substring(0, 1).toUpperCase();
        String finalAddress = firstChar + addressDetails.substring(1).toLowerCase();
        Integer archived = person.getArchived();
        Boolean finalArchived = true;
        if (archived == 0) {
            finalArchived = false;
        }
        Long statusAtRegistrationId = hivEnrollment.getStatusAtRegistrationId();
        String statusAtRegistration = applicationCodesetService.getApplicationCodeset(statusAtRegistrationId).getDisplay();
        Long entryPointId = hivEnrollment.getEntryPointId();
        String careEntryPoint = applicationCodesetService.getApplicationCodeset(entryPointId).getDisplay();
        Date dateAtRegistration = Date.from(getInstant(hivEnrollment.getDateOfRegistration()));
        LocalDate dateConfirmedHiv = hivEnrollment.getDateConfirmedHiv();
        Date dateConfirmedHivTest = null;
        if (dateConfirmedHiv != null) {
            dateConfirmedHivTest = Date.from(getInstant(dateConfirmedHiv));
        }
        Optional<ARTClinical> artCommenceOptional = artClinicalRepository.findByPersonAndIsCommencementIsTrueAndArchived(person, 0);
        PatientLineListDto patientLineListDto = PatientLineListDto
                .builder()
                .facilityId(facilityId)
                .facilityName(facility.getName())
                .surname(person.getSurname())
                .otherName(person.getFirstName())
                .address(finalAddress)
                .phone(phone.toString())
                .archived(finalArchived)
                .age(age)
                .dateBirth(finalDateOfBirth)
                .maritalStatus(maritalStatusValue)
                .education(educationValue)
                .occupation(occupationValue)
                .lgaOfResidence(lgaOfResidency.getName())
                .stateOfResidence(stateOfResidency.getName())
                .patientId(hivEnrollment.getUuid())
                .uniqueID(hivEnrollment.getUniqueId())
                .hospitalNum(person.getHospitalNumber())
                .lga(lgaOrgUnitOfFacility.getName())
                .state(state.getName())
                .sex(person.getSex())
                .statusAtRegistration(statusAtRegistration)
                .careEntryPoint(careEntryPoint)
                .dateOfRegistration(dateAtRegistration)
                .dateOfConfirmedHIVTest(dateConfirmedHivTest)
                .build();
        processAndSetBaseline(artCommenceOptional, patientLineListDto);
        processAndSetCurrentVitalSignInfo(person, patientLineListDto);
        processAndSetPharmacyDetails(person, currentDate, patientLineListDto);
        processAndSetCurrentClinicalVisit(person, patientLineListDto);
        return patientLineListDto;
    }

    private void processAndSetCurrentClinicalVisit(Person person, PatientLineListDto patientLineListDto) {
        List<ARTClinical> clinicVisits = artClinicalRepository.findAllByPersonAndIsCommencementIsFalseAndArchived(person, 0);
        Optional<ARTClinical> lastClinicVisit = clinicVisits.stream()
                .sorted(Comparator.comparing(ARTClinical::getVisitDate))
                .sorted(Comparator.comparing(ARTClinical::getId).reversed())
                .findFirst();
        lastClinicVisit.ifPresent(artClinical -> {
            LOG.info("current clinic visit {}", artClinical.getVisitDate());
            Long clinicalStageId = artClinical.getClinicalStageId();
            if (clinicalStageId != null) {
                ApplicationCodesetDTO clinicalStage = applicationCodesetService.getApplicationCodeset(clinicalStageId);
                patientLineListDto.setLastClinicStage(clinicalStage.getDisplay());
            }
            patientLineListDto.setDateOfLastClinic(Date.from(getInstant(artClinical.getVisitDate())));
            patientLineListDto.setDateOfNextClinic(Date.from(getInstant(artClinical.getNextAppointment())));
        });
    }

    private void processAndSetPharmacyDetails(Person person, LocalDate currentDate, PatientLineListDto patientLineListDto) {
        List<ArtPharmacy> pharmacies = artPharmacyRepository.getArtPharmaciesByPersonAndArchived(person, 0);
        Optional<ArtPharmacy> currentRefill = pharmacies.stream()
                .sorted(Comparator.comparing(ArtPharmacy::getVisitDate))
                .sorted(Comparator.comparing(ArtPharmacy::getId).reversed())
                .findFirst();
        currentRefill.ifPresent(currentRefill1 -> {
            Set<Regimen> regimens = currentRefill1.getRegimens();
            LocalDate nextAppointment = currentRefill1.getNextAppointment();
            regimens.forEach(regimen -> {
                String description = regimen.getRegimenType().getDescription();
                Instant instantNextAppointment = getInstant(nextAppointment);
                patientLineListDto.setDateOfNextRefill(Date.from(instantNextAppointment));
                Instant instantVisitDate = getInstant(currentRefill1.getVisitDate());
                patientLineListDto.setDateOfLastRefill(Date.from(instantVisitDate));
                patientLineListDto.setLastRefillDuration(currentRefill1.getRefillPeriod());
                String devolveType = currentRefill1.getDsdModel();
                if (devolveType != null && !(devolveType.isEmpty())) {
                    processAndSetDevolveDate(person, patientLineListDto);
                    patientLineListDto.setDmocType(devolveType);
                }
                if (description.contains("Line")) {
                    patientLineListDto.setCurrentRegimenLine(regimen.getRegimenType().getDescription());
                    patientLineListDto.setCurrentRegimen(regimen.getDescription());
                }
            });

            patientLineListDto.setCurrentStatus("Active");
            LOG.info("current date {}", currentDate);
            LOG.info("next appointment date {}", nextAppointment);
            if (nextAppointment.isBefore(currentDate)) {
                ///
                long days = ChronoUnit.DAYS.between(nextAppointment, currentDate);
                if (days >= 29) {
                    patientLineListDto.setCurrentStatus("IIT");
                }
                LOG.info("number of days after appointment {}", days);
            }

        });
    }

    private void processAndSetDevolveDate(Person person, PatientLineListDto patientLineListDto) {
        Optional<ArtPharmacy> first = artPharmacyRepository.getArtPharmaciesByPersonAndArchived(person, 0)
                .stream()
                .filter(artPharmacy -> artPharmacy.getDsdModel() != null)
                .sorted(Comparator.comparing(ArtPharmacy::getId))
                .sorted(Comparator.comparing(ArtPharmacy::getVisitDate))
                .findFirst();
        first.ifPresent(firstDevolve -> {
            Instant instant = getInstant(firstDevolve.getVisitDate());
            patientLineListDto.setDateDevolved(Date.from(instant));
        });
    }


    private void processAndSetCurrentVitalSignInfo(Person person, PatientLineListDto patientLineListDto) {
        List<VitalSign> vitalSigns = vitalSignRepository.getVitalSignByPersonAndArchived(person, 0);
        Optional<VitalSign> currentVitalSign = vitalSigns.stream()
                .sorted(Comparator.comparing(VitalSign::getCaptureDate))
                .sorted(Comparator.comparing(VitalSign::getId).reversed())
                .findFirst();
        currentVitalSign.ifPresent(vitalSign -> {
            patientLineListDto.setCurrentSystolicBP(vitalSign.getSystolic());
            patientLineListDto.setCurrentDiastolicBP(vitalSign.getDiastolic());
            patientLineListDto.setCurrentHeight(vitalSign.getHeight());
            patientLineListDto.setCurrentWeight(vitalSign.getBodyWeight());
        });
    }


    private void processAndSetBaseline(Optional<ARTClinical> artCommenceOptional, PatientLineListDto patientLineListDto) {
        artCommenceOptional.ifPresent(artClinical -> {
            ApplicationCodesetDTO clinicalStage = applicationCodesetService.getApplicationCodeset(artClinical.getWhoStagingId());
            ApplicationCodesetDTO functionalStatus = applicationCodesetService.getApplicationCodeset(artClinical.getFunctionalStatusId());
            Optional<Regimen> firstRegimen = regimenRepository.findById(artClinical.getRegimenId());
            firstRegimen.ifPresent(regimen -> {
                String regimenLine = regimen.getRegimenType().getDescription();
                String regimenName = regimen.getDescription();
                LOG.info("regimenLine {}", regimenLine);
                LOG.info("regimenName {}", regimenName);
                patientLineListDto.setFirstRegimenLine(regimenLine);
                patientLineListDto.setFirstRegimen(regimenName);
            });
            VitalSign vitalSign = artClinical.getVitalSign();
            Date artStartDate = Date.from(getInstant(artClinical.getVisitDate()));
            patientLineListDto.setBaselineHeight(vitalSign.getHeight());
            patientLineListDto.setBaselineWeight(vitalSign.getBodyWeight());
            patientLineListDto.setBaselineCD4(artClinical.getCd4() == null ? null : artClinical.getCd4().doubleValue());
            patientLineListDto.setBaselineCDP(artClinical.getCd4Percentage() == null ? null : artClinical.getCd4Percentage().doubleValue());
            patientLineListDto.setDiastolicBP(vitalSign.getDiastolic());
            patientLineListDto.setSystolicBP(vitalSign.getSystolic());
            patientLineListDto.setArtStartDate(artStartDate);
            patientLineListDto.setBaselineClinicStage(clinicalStage.getDisplay());
            patientLineListDto.setBaselineFunctionalStatus(functionalStatus.getDisplay());
        });
    }


    public List<PatientLineListDto> getPatientData(Long facility) {
        return getPatientLineList(facility);
    }
}

