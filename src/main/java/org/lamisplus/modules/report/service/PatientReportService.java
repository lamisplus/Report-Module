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
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream (); Workbook workbook = new XSSFWorkbook ()) {

            Sheet sh = workbook.createSheet ("Patient-line-list");
            List<String> columnHeadings = getPatientLineListColumnHeadings ();
            Font headerFont = getFont (workbook);
            CellStyle headerStyle = getCellStyle (workbook, headerFont);
            Row headerRow = sh.createRow (0);
            createHeader (columnHeadings, headerStyle, headerRow);
            fillData (workbook, sh, facilityId);
            FileOutputStream fileOut = new FileOutputStream ("runtime/patient_line_list.xlsx");
            workbook.write (fileOut);
            workbook.write (baos);
            LOG.info ("Completed {}", "Completed");
            return baos;
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return null;
    }


    @NotNull
    private List<String> getPatientLineListColumnHeadings() {
        return Arrays.asList (
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
        List<PatientLineListDto> data = getPatientLineList (facilityId);
        LOG.info ("data {}", data);
        CreationHelper creationHelper = workbook.getCreationHelper ();
        CellStyle dateStyle = workbook.createCellStyle ();
        dateStyle.setDataFormat (creationHelper.createDataFormat ().getFormat ("yyyy-MM-dd"));
        CellStyle numericStyle = workbook.createCellStyle ();
        numericStyle.setDataFormat ((short) BuiltinFormats.getBuiltinFormat ("#,##0"));

        //numericStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("#,##0.00"));
        // 72 colums
        int rowNum = 1;
        for (int i = 0; i < data.size (); i++) {
            LOG.info ("rowNum-before {}" + (rowNum));
            Row row = sh.createRow (rowNum++);
            LOG.info ("rowNum-after{}" + (rowNum));
            DateFormat dateFormatExcel = new SimpleDateFormat ("yyyy-MM-dd");
            PatientLineListDto person = data.get (i);
            row.createCell (0).setCellValue (person.getFacilityId ());
            row.createCell (1).setCellValue (person.getFacilityName ());
            row.createCell (2).setCellValue (person.getLga ());
            row.createCell (3).setCellValue (person.getState ());
            row.createCell (4).setCellValue (person.getPatientId ());
            row.createCell (5).setCellValue (person.getHospitalNum ());
            row.createCell (6).setCellValue (person.getUniqueID ());
            row.createCell (7).setCellValue (person.getSurname ());
            row.createCell (8).setCellValue (person.getOtherName ());
            row.createCell (9).setCellValue (dateFormatExcel.format (person.getDateBirth ()));
            row.createCell (10).setCellValue (person.getAge ());
            row.createCell (11).setCellValue (person.getSex ());
            row.createCell (12).setCellValue (person.getMaritalStatus ());
            row.createCell (13).setCellValue (person.getEducation ());
            row.createCell (14).setCellValue (person.getOccupation ());
            row.createCell (15).setCellValue (person.getStateOfResidence ());
            row.createCell (16).setCellValue (person.getLgaOfResidence ());
            row.createCell (17).setCellValue (person.getAddress ());
            row.createCell (18).setCellValue (person.getPhone ());
            row.createCell (19).setCellValue (person.getArchived ());
            row.createCell (20).setCellValue (person.getCareEntryPoint ());
            row.createCell (21).setCellValue (dateFormatExcel.format (person.getDateOfConfirmedHIVTest ()));
            row.createCell (22).setCellValue (dateFormatExcel.format (person.getDateOfRegistration ()));
            row.createCell (23).setCellValue (person.getStatusAtRegistration ());
            row.createCell (24).setCellValue (dateFormatExcel.format (person.getArtStartDate ()));
            processAndSetCD4Detail (row, person);
            row.createCell (27).setCellValue (person.getSystolicBP ());
            row.createCell (28).setCellValue (person.getDiastolicBP ());
            row.createCell (29).setCellValue (person.getBaselineWeight ());
            row.createCell (30).setCellValue (person.getBaselineHeight ());
            row.createCell (31).setCellValue (person.getBaselineClinicStage ());
            row.createCell (32).setCellValue (person.getBaselineFunctionalStatus ());
            if (person.getDateCurrentStatus () != null) {
                row.createCell (33).setCellValue (dateFormatExcel.format (person.getDateCurrentStatus ()));
            }
            row.createCell (34).setCellValue (person.getCurrentStatus ());
            row.createCell (35).setCellValue (person.getCurrentWeight ());
            row.createCell (36).setCellValue (person.getCurrentHeight ());
            row.createCell (37).setCellValue (person.getCurrentSystolicBP ());
            row.createCell (38).setCellValue (person.getCurrentDiastolicBP ());
            row.createCell (39).setCellValue ("");
            String regimenLine = person.getFirstRegimenLine () == null ? "" : person.getFirstRegimenLine ();
            String regimen = person.getFirstRegimen () == null ? "" : person.getFirstRegimen ();
            row.createCell (40).setCellValue (regimenLine);
            row.createCell (41).setCellValue (regimen);
            row.createCell (42).setCellValue (person.getCurrentRegimenLine ());
            row.createCell (43).setCellValue (person.getCurrentRegimen ());
//            row.createCell (44).setCellValue (set substitution date);
            if (person.getDateOfLastRefill () != null) {
                row.createCell (45).setCellValue (dateFormatExcel.format (person.getDateOfLastRefill ()));
            }
            if (person.getLastRefillDuration () != null) {
                row.createCell (46).setCellValue (person.getLastRefillDuration ());
            }
            if (person.getDateOfNextRefill () != null) {
                row.createCell (47).setCellValue (dateFormatExcel.format (person.getDateOfNextRefill ()));
            }
            row.createCell (48).setCellValue (person.getDmocType () == null ? "" : person.getDmocType ());
            Date dateDevolved = person.getDateDevolved ();
            if (dateDevolved != null) {
                row.createCell (49).setCellValue (dateFormatExcel.format (dateDevolved));
            }
            row.createCell (50).setCellValue (person.getLastClinicStage () == null ? "" : person.getLastClinicStage ());
            Date dateOfLastClinic = person.getDateOfLastClinic ();
            if (dateOfLastClinic != null) {
                row.createCell (51).setCellValue (dateFormatExcel.format (dateOfLastClinic));

            }
            Date dateOfNextClinic = person.getDateOfNextClinic ();
            if (dateOfNextClinic != null) {
                row.createCell (52).setCellValue (dateFormatExcel.format (dateOfNextClinic));

            }
            // row.createCell (53).setCellValue (person.getSex ());
//            row.createCell (54).setCellValue (person.getSex ());
//            row.createCell (55).setCellValue (person.getSex ());
//            row.createCell (56).setCellValue (person.getSex ());
//            row.createCell (57).setCellValue (person.getSex ());
//            row.createCell (58).setCellValue (person.getSex ());
//            row.createCell (59).setCellValue (person.getSex ());
//            row.createCell (60).setCellValue (person.getSex ());
//            row.createCell (61).setCellValue (person.getSex ());
//            row.createCell (62).setCellValue (person.getSex ());
//            row.createCell (63).setCellValue (person.getSex ());
//            row.createCell (64).setCellValue (person.getSex ());
//            row.createCell (65).setCellValue (person.getSex ());
//            row.createCell (66).setCellValue (person.getSex ());
//            row.createCell (67).setCellValue (person.getSex ());
//            row.createCell (68).setCellValue (person.getSex ());
//            row.createCell (69).setCellValue (person.getSex ());
//            row.createCell (70).setCellValue (person.getSex ());
//            row.createCell (71).setCellValue (person.getSex ());
//            row.createCell (72).setCellValue (person.getSex ());
//            Cell dateCell = row.createCell (person.);
//            dateCell.setCellValue (i.getItemSoldDate ());
            // dateCell.setCellStyle (dateStyle);

        }
    }

    private void processAndSetCD4Detail(Row row, PatientLineListDto person) {
        Double baselineCD4 = person.getBaselineCD4 ();
        if (baselineCD4 != null) {
            row.createCell (25).setCellValue (baselineCD4);
        }
        Double baselineCDP = person.getBaselineCDP ();
        if (baselineCDP != null) {

            row.createCell (26).setCellValue (baselineCDP);
        }
    }

    private Instant getInstant(LocalDate dateBirth) {
        return dateBirth.atStartOfDay (ZoneId.systemDefault ()).toInstant ();

    }


    private void createHeader(List<String> columnHeadings, CellStyle headerStyle, Row headerRow) {
        for (int i = 0; i < columnHeadings.size (); i++) {
            Cell cell = headerRow.createCell (i);
            cell.setCellValue (columnHeadings.get (i));
            cell.setCellStyle (headerStyle);
        }
    }


    @NotNull
    private CellStyle getCellStyle(Workbook workbook, Font headerFont) {
        CellStyle headerStyle = workbook.createCellStyle ();
        headerStyle.setFont (headerFont);
        headerStyle.setFillPattern (FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor (IndexedColors.GREY_25_PERCENT.index);
        return headerStyle;
    }


    @NotNull
    private Font getFont(Workbook workbook) {
        Font headerFont = workbook.createFont ();
        headerFont.setBold (true);
        headerFont.setFontHeightInPoints ((short) 12);
        headerFont.setColor (IndexedColors.BLACK.index);
        return headerFont;
    }


    private List<PatientLineListDto> getPatientLineList(Long facilityId) {
        return hivEnrollmentRepository.findAll ()
                .stream ()
                .filter (hivEnrollment -> hivEnrollment.getFacilityId ().equals (facilityId))
                .map (this::getPatientLineListDto)
                .collect (Collectors.toList ());


    }

    private PatientLineListDto getPatientLineListDto(HivEnrollment hivEnrollment) {
        Long facilityId = hivEnrollment.getFacilityId ();
        OrganisationUnit facility = organisationUnitService.getOrganizationUnit (facilityId);
        Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId ();
        OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit (lgaIdOfTheFacility);
        Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId ();
        OrganisationUnit state = organisationUnitService.getOrganizationUnit (stateId);
        Person person = hivEnrollment.getPerson ();
        LocalDate dateBirth = person.getDateOfBirth ();
        LocalDate currentDate = LocalDate.now ();
        Instant dateBirthOfInstant = getInstant (dateBirth);
        int age = Period.between (dateBirth, currentDate).getYears ();
        Date finalDateOfBirth = Date.from (dateBirthOfInstant);
        JsonNode maritalStatus = person.getMaritalStatus ();
        String fieldName = "display";
        String maritalStatusValue = maritalStatus.isNull () ? "" : maritalStatus.get (fieldName).asText ();
        JsonNode education = person.getEducation ();
        String educationValue = education.isNull () ? "" : education.get (fieldName).asText ();
        JsonNode occupation = person.getEmploymentStatus ();
        String occupationValue = education.isNull () ? "" : occupation.get (fieldName).asText ();
        JsonNode address = person.getAddress ();
        JsonNode address1 = address.get ("address");
        Long stateOfResidenceId = null;
        Long lgaOfResidenceId = null;
        StringBuilder addressDetails = new StringBuilder ();
        if (address1.isArray ()) {
            JsonNode addressObject = address1.get (0);
            if (addressObject.hasNonNull ("stateId") && addressObject.hasNonNull ("district")) {
                stateOfResidenceId = addressObject.get ("stateId").asLong ();
                lgaOfResidenceId = addressObject.get ("district").asLong ();
                addressDetails.append (addressObject.get ("city").asText ());
                JsonNode town = addressObject.get ("line");
                if (! town.isNull () && town.isArray ()) {
                    for (JsonNode node : town) {
                        addressDetails.append (" " + node.asText ());
                    }
                }
            }
        }
        OrganisationUnit stateOfResidency = organisationUnitService.getOrganizationUnit (stateOfResidenceId);
        OrganisationUnit lgaOfResidency = organisationUnitService.getOrganizationUnit (lgaOfResidenceId);
        JsonNode contactPoint = person.getContactPoint ();
        StringBuilder phone = new StringBuilder ();
        if (contactPoint.hasNonNull ("contactPoint") && contactPoint.get ("contactPoint").isArray ()) {
            JsonNode phoneObject = contactPoint.get ("contactPoint").get (1);
            String phoneValue = phoneObject.isNull () ? "" : phoneObject.get ("value").asText ();
            phone.append (phoneValue);
        }
        String firstChar = addressDetails.substring (0, 1).toUpperCase ();
        String finalAddress = firstChar + addressDetails.substring (1).toLowerCase ();
        Integer archived = person.getArchived ();
        Boolean finalArchived = true;
        if (archived == 0) {
            finalArchived = false;
        }
        Long statusAtRegistrationId = hivEnrollment.getStatusAtRegistrationId ();
        String statusAtRegistration = applicationCodesetService.getApplicationCodeset (statusAtRegistrationId).getDisplay ();
        Long entryPointId = hivEnrollment.getEntryPointId ();
        String careEntryPoint = applicationCodesetService.getApplicationCodeset (entryPointId).getDisplay ();
        Date dateAtRegistration = Date.from (getInstant (hivEnrollment.getDateOfRegistration ()));
        Date dateConfirmedHivTest = Date.from (getInstant (hivEnrollment.getDateConfirmedHiv ()));
        Optional<ARTClinical> artCommenceOptional = artClinicalRepository.findByPersonAndIsCommencementIsTrueAndArchived (person, 0);
        PatientLineListDto patientLineListDto = PatientLineListDto
                .builder ()
                .facilityId (facilityId)
                .facilityName (facility.getName ())
                .surname (person.getSurname ())
                .otherName (person.getFirstName ())
                .address (finalAddress)
                .phone (phone.toString ())
                .archived (finalArchived)
                .age (age)
                .dateBirth (finalDateOfBirth)
                .maritalStatus (maritalStatusValue)
                .education (educationValue)
                .occupation (occupationValue)
                .lgaOfResidence (lgaOfResidency.getName ())
                .stateOfResidence (stateOfResidency.getName ())
                .patientId (hivEnrollment.getUuid ())
                .uniqueID (hivEnrollment.getUniqueId ())
                .hospitalNum (person.getHospitalNumber ())
                .lga (lgaOrgUnitOfFacility.getName ())
                .state (state.getName ())
                .sex (person.getSex ())
                .statusAtRegistration (statusAtRegistration)
                .careEntryPoint (careEntryPoint)
                .dateOfRegistration (dateAtRegistration)
                .dateOfConfirmedHIVTest (dateConfirmedHivTest)
                .build ();
        processAndSetBaseline (artCommenceOptional, patientLineListDto);
        processAndSetCurrentVitalSignInfo (person, patientLineListDto);
        processAndSetPharmacyDetails (person, currentDate, patientLineListDto);
        processAndSetCurrentClinicalVisit (person, patientLineListDto);
        return patientLineListDto;
    }

    private void processAndSetCurrentClinicalVisit(Person person, PatientLineListDto patientLineListDto) {
        List<ARTClinical> clinicVisits = artClinicalRepository.findAllByPersonAndIsCommencementIsFalseAndArchived (person, 0);
        Optional<ARTClinical> lastClinicVisit = clinicVisits.stream ()
                .sorted (Comparator.comparing (ARTClinical::getVisitDate))
                .sorted (Comparator.comparing (ARTClinical::getId).reversed ())
                .findFirst ();
        lastClinicVisit.ifPresent (artClinical -> {
            LOG.info ("current clinic visit {}", artClinical.getVisitDate ());
            Long clinicalStageId = artClinical.getClinicalStageId ();
            ApplicationCodesetDTO clinicalStage = applicationCodesetService.getApplicationCodeset (clinicalStageId);
            patientLineListDto.setLastClinicStage (clinicalStage.getDisplay ());
            patientLineListDto.setDateOfLastClinic (Date.from (getInstant (artClinical.getVisitDate ())));
            patientLineListDto.setDateOfNextClinic (Date.from (getInstant (artClinical.getNextAppointment ())));
        });
    }

    private void processAndSetPharmacyDetails(Person person, LocalDate currentDate, PatientLineListDto patientLineListDto) {
        List<ArtPharmacy> pharmacies = artPharmacyRepository.getArtPharmaciesByPersonAndArchived (person, 0);
        Optional<ArtPharmacy> currentRefill = pharmacies.stream ()
                .sorted (Comparator.comparing (ArtPharmacy::getVisitDate))
                .sorted (Comparator.comparing (ArtPharmacy::getId).reversed ())
                .findFirst ();
        currentRefill.ifPresent (currentRefill1 -> {
            Set<Regimen> regimens = currentRefill1.getRegimens ();
            regimens.forEach (regimen -> {
                String description = regimen.getRegimenType ().getDescription ();
                Instant instantNextAppointment = getInstant (currentRefill1.getNextAppointment ());
                patientLineListDto.setDateOfNextRefill (Date.from (instantNextAppointment));
                Instant instantVisitDate = getInstant (currentRefill1.getVisitDate ());
                patientLineListDto.setDateOfLastRefill (Date.from (instantVisitDate));
                patientLineListDto.setLastRefillDuration (currentRefill1.getRefillPeriod ());
                String devolveType = currentRefill1.getDsdModel ();
                if (devolveType != null && ! (devolveType.isEmpty ())) {
                    processAndSetDevolveDate (person, patientLineListDto);
                    patientLineListDto.setDmocType (devolveType);
                }
                if (description.contains ("Line")) {
                    patientLineListDto.setCurrentRegimenLine (regimen.getRegimenType ().getDescription ());
                    patientLineListDto.setCurrentRegimen (regimen.getDescription ());
                }
            });
            int days = Period.between (currentRefill1.getNextAppointment (), currentDate).getDays ();
            LOG.info ("number of days after appointment {}", days);
            if (days < 0) {
                int abs = Math.abs (days);
                if (abs >= 28) {
                    LOG.info ("number of miss appointment days {}", abs);
                    patientLineListDto.setCurrentStatus ("IIT");
                }
            } else {
                patientLineListDto.setCurrentStatus ("Active");
            }

        });
    }

    private void processAndSetDevolveDate(Person person, PatientLineListDto patientLineListDto) {
        Optional<ArtPharmacy> first = artPharmacyRepository.getArtPharmaciesByPersonAndArchived (person, 0)
                .stream ()
                .filter (artPharmacy -> artPharmacy.getDsdModel () != null)
                .sorted (Comparator.comparing (ArtPharmacy::getId))
                .sorted (Comparator.comparing (ArtPharmacy::getVisitDate))
                .findFirst ();
        first.ifPresent (firstDevolve -> {
            Instant instant = getInstant (firstDevolve.getVisitDate ());
            patientLineListDto.setDateDevolved (Date.from (instant));
        });
    }


    private void processAndSetCurrentVitalSignInfo(Person person, PatientLineListDto patientLineListDto) {
        List<VitalSign> vitalSigns = vitalSignRepository.getVitalSignByPersonAndArchived (person, 0);
        Optional<VitalSign> currentVitalSign = vitalSigns.stream ()
                .sorted (Comparator.comparing (VitalSign::getEncounterDate))
                .sorted (Comparator.comparing (VitalSign::getId).reversed ())
                .findFirst ();
        currentVitalSign.ifPresent (vitalSign -> {
            patientLineListDto.setCurrentSystolicBP (vitalSign.getSystolic ());
            patientLineListDto.setCurrentDiastolicBP (vitalSign.getDiastolic ());
            patientLineListDto.setCurrentHeight (vitalSign.getHeight ());
            patientLineListDto.setCurrentWeight (vitalSign.getBodyWeight ());
        });
    }


    private void processAndSetBaseline(Optional<ARTClinical> artCommenceOptional, PatientLineListDto patientLineListDto) {
        artCommenceOptional.ifPresent (artClinical -> {
            ApplicationCodesetDTO clinicalStage = applicationCodesetService.getApplicationCodeset (artClinical.getWhoStagingId ());
            ApplicationCodesetDTO functionalStatus = applicationCodesetService.getApplicationCodeset (artClinical.getFunctionalStatusId ());
            Optional<Regimen> firstRegimen = regimenRepository.findById (artClinical.getRegimenId ());
            firstRegimen.ifPresent (regimen -> {
                String regimenLine = regimen.getRegimenType ().getDescription ();
                String regimenName = regimen.getDescription ();
                LOG.info ("regimenLine {}", regimenLine);
                LOG.info ("regimenName {}", regimenName);
                patientLineListDto.setFirstRegimenLine (regimenLine);
                patientLineListDto.setFirstRegimen (regimenName);
            });
            VitalSign vitalSign = artClinical.getVitalSign ();
            Date artStartDate = Date.from (getInstant (artClinical.getVisitDate ()));
            patientLineListDto.setBaselineHeight (vitalSign.getHeight ());
            patientLineListDto.setBaselineWeight (vitalSign.getBodyWeight ());
            patientLineListDto.setBaselineCD4 (artClinical.getCd4 () == null ? null : artClinical.getCd4 ().doubleValue ());
            patientLineListDto.setBaselineCDP (artClinical.getCd4Percentage () == null ? null : artClinical.getCd4Percentage ().doubleValue ());
            patientLineListDto.setDiastolicBP (vitalSign.getDiastolic ());
            patientLineListDto.setSystolicBP (vitalSign.getSystolic ());
            patientLineListDto.setArtStartDate (artStartDate);
            patientLineListDto.setBaselineClinicStage (clinicalStage.getDisplay ());
            patientLineListDto.setBaselineFunctionalStatus (functionalStatus.getDisplay ());
        });
    }


    public List<PatientLineListDto> getPatientData(Long facility) {
        return getPatientLineList (facility);
    }
}

