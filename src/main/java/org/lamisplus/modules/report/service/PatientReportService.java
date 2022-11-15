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
	
	
	public List<PatientLineListDto> getPatientLineList(Long facilityId) {
		return hivEnrollmentRepository.findAll()
				.parallelStream()
				.filter(hivEnrollment -> hivEnrollment.getFacilityId().equals(facilityId))
				.filter(Objects::nonNull)
				.filter(hivEnrollment -> hivEnrollment.getStatusAtRegistrationId() != null)
				.filter(Objects::nonNull)
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
		int age = Period.between(dateBirth, currentDate).getYears();
		JsonNode maritalStatus = person.getMaritalStatus();
		String fieldName = "display";
		StringBuilder maritalStatusValue = new StringBuilder();
		if(maritalStatus != null){
			String maritalStatusValue1 = maritalStatus.isNull() ? "" : maritalStatus.get(fieldName).asText();
			maritalStatusValue.append(maritalStatusValue1);
		}
		JsonNode education = person.getEducation();
		StringBuilder educationValue = new StringBuilder();
		if(education != null) {
			String educationValue1 = education.isNull() ? "" : education.get(fieldName).asText();
			educationValue.append(educationValue1);
		}
		JsonNode occupation = person.getEmploymentStatus();
		StringBuilder occupationValue = new StringBuilder();
		if (occupation != null) {
			String occupationValue1 = occupation.isNull() ? "" : occupation.get(fieldName).asText();
			occupationValue.append(occupationValue1);
			
		}
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
		boolean finalArchived = archived != 0;
		Long statusAtRegistrationId = hivEnrollment.getStatusAtRegistrationId();
		StringBuilder statusAtRegistration = new StringBuilder();
		if(statusAtRegistrationId != null && statusAtRegistrationId > 0){
			String statusAtRegistration1 =
					applicationCodesetService.getApplicationCodeset(statusAtRegistrationId).getDisplay();
			statusAtRegistration.append(statusAtRegistration1);
		}
		Long entryPointId = hivEnrollment.getEntryPointId();
		StringBuilder careEntryPoint = new StringBuilder();
		if (entryPointId != null && entryPointId > 0){
			String careEntryPoint1 = applicationCodesetService.getApplicationCodeset(entryPointId).getDisplay();
			careEntryPoint.append(careEntryPoint1);
		}
		LocalDate dateConfirmedHiv = hivEnrollment.getDateConfirmedHiv();
		Optional<ARTClinical> artCommenceOptional = artClinicalRepository.findTopByPersonAndIsCommencementIsTrueAndArchived(person, 0);
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
				.dateBirth(dateBirth)
				.maritalStatus(maritalStatusValue.toString())
				.education(educationValue.toString())
				.occupation(occupationValue.toString())
				.lgaOfResidence(lgaOfResidency.getName())
				.stateOfResidence(stateOfResidency.getName())
				.patientId(hivEnrollment.getUuid())
				.uniqueID(hivEnrollment.getUniqueId())
				.hospitalNum(person.getHospitalNumber())
				.lga(lgaOrgUnitOfFacility.getName())
				.state(state.getName())
				.sex(person.getSex())
				.statusAtRegistration(statusAtRegistration.toString())
				.careEntryPoint(careEntryPoint.toString())
				.dateOfRegistration(hivEnrollment.getDateOfRegistration())
				.dateOfConfirmedHIVTest(dateConfirmedHiv)
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
			if (clinicalStageId != null && clinicalStageId > 0) {
				ApplicationCodesetDTO clinicalStage = applicationCodesetService.getApplicationCodeset(clinicalStageId);
				patientLineListDto.setLastClinicStage(clinicalStage.getDisplay());
			}
			patientLineListDto.setDateOfLastClinic(artClinical.getVisitDate());
			patientLineListDto.setDateOfNextClinic(artClinical.getNextAppointment());
		});
	}
	
	private void processAndSetPharmacyDetails(Person person, LocalDate currentDate, PatientLineListDto patientLineListDto) {
		List<ArtPharmacy> pharmacies = artPharmacyRepository.getArtPharmaciesByPersonAndArchived(person, 0);
		Optional<ArtPharmacy> currentRefill = pharmacies.stream()
				.sorted(Comparator.comparing(ArtPharmacy::getVisitDate))
				.sorted(Comparator.comparing(ArtPharmacy::getId).reversed())
				.findFirst();
		
		currentRefill.ifPresent(currentRefill1 -> {
			LocalDate nextAppointment = currentRefill1.getNextAppointment();
			patientLineListDto.setDateOfNextRefill(nextAppointment);
			patientLineListDto.setDateOfLastRefill(currentRefill1.getVisitDate());
			patientLineListDto.setLastRefillDuration(currentRefill1.getRefillPeriod());
			String devolveType = currentRefill1.getDsdModel();
			if (devolveType != null && !(devolveType.isEmpty())) {
				processAndSetDevolveDate(person, patientLineListDto);
				patientLineListDto.setDmocType(devolveType);
			}
			Set<Regimen> regimens = currentRefill1.getRegimens();
			regimens.forEach(regimen -> setCurrentRegimen(patientLineListDto, regimen));
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
	
	private static void setCurrentRegimen(PatientLineListDto patientLineListDto, Regimen regimen) {
		String description = regimen.getRegimenType().getDescription();
		if (description.contains("Line")) {
			patientLineListDto.setCurrentRegimenLine(regimen.getRegimenType().getDescription());
			patientLineListDto.setCurrentRegimen(regimen.getDescription());
		}
	}
	
	private void processAndSetDevolveDate(Person person, PatientLineListDto patientLineListDto) {
		Optional<ArtPharmacy> first = artPharmacyRepository.getArtPharmaciesByPersonAndArchived(person, 0)
				.stream()
				.filter(artPharmacy -> artPharmacy.getDsdModel() != null)
				.sorted(Comparator.comparing(ArtPharmacy::getId))
				.sorted(Comparator.comparing(ArtPharmacy::getVisitDate))
				.findFirst();
		first.ifPresent(firstDevolve -> patientLineListDto.setDateDevolved(firstDevolve.getVisitDate()));
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
			Long whoStagingId = artClinical.getWhoStagingId();
			if (whoStagingId != null && whoStagingId > 0) {
				ApplicationCodesetDTO clinicalStage = applicationCodesetService.getApplicationCodeset(whoStagingId);
				patientLineListDto.setBaselineClinicStage(clinicalStage.getDisplay());
			}
			Long functionalStatusId = artClinical.getFunctionalStatusId();
			if (functionalStatusId != null && functionalStatusId > 0)  {
				ApplicationCodesetDTO functionalStatus = applicationCodesetService.getApplicationCodeset(functionalStatusId);
				patientLineListDto.setBaselineFunctionalStatus(functionalStatus.getDisplay());
			}
			long regimenId = artClinical.getRegimenId();
			Optional<Regimen> firstRegimen = regimenRepository.findById(regimenId);
			firstRegimen.ifPresent(regimen -> {
				String regimenLine = regimen.getRegimenType().getDescription();
				String regimenName = regimen.getDescription();
				LOG.info("regimenLine {}", regimenLine);
				LOG.info("regimenName {}", regimenName);
				patientLineListDto.setFirstRegimenLine(regimenLine);
				patientLineListDto.setFirstRegimen(regimenName);
			});
			VitalSign vitalSign = artClinical.getVitalSign();
			patientLineListDto.setBaselineHeight(vitalSign.getHeight());
			patientLineListDto.setBaselineWeight(vitalSign.getBodyWeight());
			patientLineListDto.setBaselineCD4(artClinical.getCd4() == null ? null : artClinical.getCd4().doubleValue());
			patientLineListDto.setBaselineCDP(artClinical.getCd4Percentage() == null ? null : artClinical.getCd4Percentage().doubleValue());
			patientLineListDto.setDiastolicBP(vitalSign.getDiastolic());
			patientLineListDto.setSystolicBP(vitalSign.getSystolic());
			patientLineListDto.setArtStartDate(artClinical.getVisitDate());
			
		});
	}
	
	
	public List<PatientLineListDto> getPatientData(Long facility) {
		return getPatientLineList(facility);
	}
}

