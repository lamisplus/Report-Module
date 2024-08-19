package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.dto.ApplicationCodesetDTO;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.domain.entities.OrganisationUnitIdentifier;
import org.lamisplus.modules.base.module.ModuleService;
import org.lamisplus.modules.base.service.ApplicationCodesetService;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.dto.ViralLoadRadetDto;
import org.lamisplus.modules.hiv.domain.entity.ARTClinical;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.domain.entity.HivEnrollment;
import org.lamisplus.modules.hiv.domain.entity.Regimen;
import org.lamisplus.modules.hiv.repositories.*;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.report.domain.PatientLineDto;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.lamisplus.modules.triage.domain.entity.VitalSign;
import org.lamisplus.modules.triage.repository.VitalSignRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.*;
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
	
	private final HIVEacRepository hIVEacRepository;
	
	private final ModuleService moduleService;

	private final ReportRepository reportRepository;
	
	
	public List<PatientLineListDto> getPatientLineList(Long facilityId) {
		return hivEnrollmentRepository.findAll()
				.parallelStream()
				.filter(hivEnrollment -> hivEnrollment.getFacilityId().equals(facilityId))
				.filter(hivEnrollment -> hivEnrollment.getStatusAtRegistrationId() != null)
				.map(this::getPatientLineListDto)
				.collect(Collectors.toList());
		
	}
	
	public List<org.lamisplus.modules.report.domain.PatientLineDto> getPatientLine(Long facilityId) {
		System.out.println("start: fetching records from db: ");
		List<PatientLineDto> patientLineDtoList = reportRepository.getPatientLineByFacilityId(facilityId);
		System.out.println("Total size:  " + patientLineDtoList.size());
		return patientLineDtoList;
	}
	
	
	private PatientLineListDto getPatientLineListDto(HivEnrollment hivEnrollment) {
		PatientLineListDto patientLineListDto = new PatientLineListDto();
		Long facilityId = hivEnrollment.getFacilityId();
		patientLineListDto.setFacilityId(facilityId);
		OrganisationUnit facility = organisationUnitService.getOrganizationUnit(facilityId);
		patientLineListDto.setFacilityName(facility.getName());
		 processAndSetDatimId(facility, patientLineListDto);
		Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId();
		processAndSetFacilityLgaAndState(patientLineListDto, lgaIdOfTheFacility);
		processAndSetBiodata(hivEnrollment, patientLineListDto);
		Person person = hivEnrollment.getPerson();
		processAndSetBaseline(patientLineListDto, person);
		processAndSetCurrentVitalSignInfo(person, patientLineListDto);
		processAndSetPharmacyDetails(person, LocalDate.now(), patientLineListDto);
	    processAndSetCurrentClinicalVisit(person, patientLineListDto);
		processAndSetVl(patientLineListDto, person.getId());
		return patientLineListDto;
	}
	
	@NotNull
	private void processAndSetFacilityLgaAndState(PatientLineListDto patientLineListDto, Long lgaIdOfTheFacility) {
		OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit(lgaIdOfTheFacility);
		patientLineListDto.setLga(lgaOrgUnitOfFacility.getName());
		Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId();
		OrganisationUnit state = organisationUnitService.getOrganizationUnit(stateId);
		patientLineListDto.setState(state.getName());
	}
	
	@NotNull
	private static void processAndSetDatimId(OrganisationUnit facility, PatientLineListDto patientLineListDto) {
		String datimId = facility.getOrganisationUnitIdentifiers()
				.parallelStream()
				.filter(identifier -> identifier.getName().equalsIgnoreCase("DATIM_ID"))
				.map(OrganisationUnitIdentifier::getCode)
				.findFirst().orElse("");
		patientLineListDto.setDatimId(datimId);
		
	}
	
	private void processAndSetBiodata(HivEnrollment hivEnrollment, PatientLineListDto patientLineListDto) {
		Person person = hivEnrollment.getPerson();
		LocalDate dateBirth = person.getDateOfBirth();
		LocalDate currentDate = LocalDate.now();
		int age = Period.between(dateBirth, currentDate).getYears();
		patientLineListDto.setAge(age);
		patientLineListDto.setDateBirth(dateBirth);
		patientLineListDto.setPatientId(person.getUuid());
		patientLineListDto.setSex(person.getSex());
		patientLineListDto.setUniqueID(hivEnrollment.getUniqueId());
		patientLineListDto.setHospitalNum(person.getHospitalNumber());
		patientLineListDto.setDateOfConfirmedHIVTest(hivEnrollment.getDateConfirmedHiv());
		patientLineListDto.setDateOfRegistration(hivEnrollment.getDateOfRegistration());
		patientLineListDto.setSurname(person.getSurname());
		patientLineListDto.setOtherName(person.getFirstName());
		JsonNode maritalStatus = person.getMaritalStatus();
		String fieldName = "display";
		StringBuilder maritalStatusValue = new StringBuilder();
		if (maritalStatus != null) {
			String maritalStatusValue1 = maritalStatus.isNull() ? "" : maritalStatus.get(fieldName).asText();
			maritalStatusValue.append(maritalStatusValue1);
			patientLineListDto.setMaritalStatus(maritalStatusValue.toString());
		}
		JsonNode education = person.getEducation();
		StringBuilder educationValue = new StringBuilder();
		if (education != null) {
			String educationValue1 = education.isNull() ? "" : education.get(fieldName).asText();
			educationValue.append(educationValue1);
			patientLineListDto.setEducation(educationValue.toString());
		}
		JsonNode occupation = person.getEmploymentStatus();
		StringBuilder occupationValue = new StringBuilder();
		if (occupation != null) {
			String occupationValue1 = occupation.isNull() ? "" : occupation.get(fieldName).asText();
			occupationValue.append(occupationValue1);
			patientLineListDto.setOccupation(occupationValue.toString());
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
		patientLineListDto.setStateOfResidence(stateOfResidency.getName());
		OrganisationUnit lgaOfResidency = organisationUnitService.getOrganizationUnit(lgaOfResidenceId);
		patientLineListDto.setLgaOfResidence(lgaOfResidency.getName());
		JsonNode contactPoint = person.getContactPoint();
		StringBuilder phone = new StringBuilder();
		if (contactPoint.hasNonNull("contactPoint") && contactPoint.get("contactPoint").isArray()) {
			JsonNode phoneObject = contactPoint.get("contactPoint").get(1);
			String phoneValue = phoneObject == null ? "" : phoneObject.get("value").asText();
			phone.append(phoneValue);
			patientLineListDto.setPhone(phone.toString());
		}
		if(addressDetails.length() > 2){
			String firstChar = addressDetails.substring(0, 1).toUpperCase();
			String finalAddress = firstChar + addressDetails.substring(1).toLowerCase();
			patientLineListDto.setAddress(finalAddress);
		}
		patientLineListDto.setArchived(person.getArchived() != 0);
		Long statusAtRegistrationId = hivEnrollment.getStatusAtRegistrationId();
		StringBuilder statusAtRegistration = new StringBuilder();
		if (statusAtRegistrationId != null && statusAtRegistrationId > 0) {
			String statusAtRegistration1 =
					applicationCodesetService.getApplicationCodeset(statusAtRegistrationId).getDisplay();
			statusAtRegistration.append(statusAtRegistration1);
			patientLineListDto.setStatusAtRegistration(statusAtRegistration.toString());
		}
		Long entryPointId = hivEnrollment.getEntryPointId();
		StringBuilder careEntryPoint = new StringBuilder();
		if (entryPointId != null && entryPointId > 0) {
			String careEntryPoint1 = applicationCodesetService.getApplicationCodeset(entryPointId).getDisplay();
			careEntryPoint.append(careEntryPoint1);
			patientLineListDto.setCareEntryPoint(careEntryPoint.toString());
		}
	}
	
	private void processAndSetCurrentClinicalVisit(Person person, PatientLineListDto patientLineListDto) {
		List<ARTClinical> clinicVisits = artClinicalRepository.findAllByPersonAndIsCommencementIsFalseAndArchived(person, 0);
		Optional<ARTClinical> lastClinicVisit = clinicVisits.stream()
				.sorted(Comparator.comparing(ARTClinical::getVisitDate))
				.sorted(Comparator.comparing(ARTClinical::getId).reversed())
				.findFirst();
		lastClinicVisit.ifPresent(artClinical -> {
			//LOG.info("current clinic visit {}", artClinical.getVisitDate());
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
			//LOG.info("current date {}", currentDate);
			//LOG.info("next appointment date {}", nextAppointment);
			if (nextAppointment.isBefore(currentDate)) {
				///
				long days = ChronoUnit.DAYS.between(nextAppointment, currentDate);
				if (days >= 29) {
					patientLineListDto.setCurrentStatus("IIT");
				}
				//LOG.info("number of days after appointment {}", days);
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
	
	
	private void processAndSetBaseline(PatientLineListDto patientLineListDto, Person person) {
		Optional<ARTClinical> artCommenceOptional =
				artClinicalRepository.findTopByPersonAndIsCommencementIsTrueAndArchived(person, 0);
		artCommenceOptional.ifPresent(artClinical -> {
			Long whoStagingId = artClinical.getWhoStagingId();
			if (whoStagingId != null && whoStagingId > 0) {
				ApplicationCodesetDTO clinicalStage = applicationCodesetService.getApplicationCodeset(whoStagingId);
				patientLineListDto.setBaselineClinicStage(clinicalStage.getDisplay());
			}
			Long functionalStatusId = artClinical.getFunctionalStatusId();
			if (functionalStatusId != null && functionalStatusId > 0) {
				ApplicationCodesetDTO functionalStatus = applicationCodesetService.getApplicationCodeset(functionalStatusId);
				patientLineListDto.setBaselineFunctionalStatus(functionalStatus.getDisplay());
			}
			long regimenId = artClinical.getRegimenId();
			Optional<Regimen> firstRegimen = regimenRepository.findById(regimenId);
			firstRegimen.ifPresent(regimen -> {
				String regimenLine = regimen.getRegimenType().getDescription();
				String regimenName = regimen.getDescription();
				//LOG.info("regimenLine {}", regimenLine);
				//LOG.info("regimenName {}", regimenName);
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
	//current viral load
	
	private void processAndSetVl(PatientLineListDto patientLineListDto, Long personId) {
		boolean labExist = moduleService.exist("Lab");
		if (labExist) {
			//Log.info(" in lab info {}", labExist);
			LocalDateTime start = LocalDateTime.of(1984, 1, 1, 0, 0);
			
			Optional<ViralLoadRadetDto> viralLoadDetails =
					hIVEacRepository.getPatientCurrentViralLoadDetails(personId, start, LocalDateTime.now());
			viralLoadDetails.ifPresent(currentViralLoad -> {
				//Log.info("current viral load indication {}", currentViralLoad.getIndicationId() + " : result :=> " + currentViralLoad.getResult());
				Long indicationId = currentViralLoad.getIndicationId();
				if (indicationId != null && indicationId > 0) {
					String viralLoadIndication =
							applicationCodesetService.getApplicationCodeset(indicationId).getDisplay();
					patientLineListDto.setVlIndication(viralLoadIndication);
				}
				if (currentViralLoad.getDateSampleCollected() != null) {
					patientLineListDto.setDateOfSampleCollection(currentViralLoad.getDateSampleCollected());
				}
				if (currentViralLoad.getResultDate() != null) {
					patientLineListDto.setDateCurrentVl(currentViralLoad.getResultDate());
				}
				patientLineListDto.setCurrentVl(Double.valueOf(currentViralLoad.getResult()));
			});
		}
	}
	
	@Async
	public List<PatientLineListDto> getPatientData(Long facility) {
		return getPatientLineList(facility);
	}
	
	
}

