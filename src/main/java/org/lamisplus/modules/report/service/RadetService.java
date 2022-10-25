package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.audit4j.core.util.Log;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.module.ModuleService;
import org.lamisplus.modules.base.service.ApplicationCodesetService;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.dto.BiometricRadetDto;
import org.lamisplus.modules.hiv.domain.dto.ViralLoadRadetDto;
import org.lamisplus.modules.hiv.domain.entity.*;
import org.lamisplus.modules.hiv.repositories.*;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.report.domain.RadetDto;
import org.lamisplus.modules.triage.domain.entity.VitalSign;
import org.lamisplus.modules.triage.repository.VitalSignRepository;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RadetService {
	
	private final ArtPharmacyRepository artPharmacyRepository;
	
	private final OrganisationUnitService organisationUnitService;
	
	private final ApplicationCodesetService applicationCodesetService;
	
	private final ARTClinicalRepository artClinicalRepository;
	
	private final VitalSignRepository vitalSignRepository;
	
	private final RegimenRepository regimenRepository;
	
	
	private final HivEnrollmentRepository hivEnrollmentRepository;
	
	
	private final HIVEacRepository hIVEacRepository;
	
	private final HIVEacSessionRepository hIVEacSessionRepository;
	
	private final ModuleService moduleService;
	
	
	@NotNull
	public Set<Person> getRadetEligibles() {
		return artPharmacyRepository.findAll()
				.stream()
				.map(ArtPharmacy::getPerson)
				.collect(Collectors.toSet());
	}
	
	@NotNull
	public Set<RadetDto> getRadetDtos(Long facilityId, LocalDate start, LocalDate end, Set<Person> radetPatients) {
		return artPharmacyRepository.findAll()
				.stream()
				.filter(artPharmacy -> radetPatients.contains(artPharmacy.getPerson()))
				.filter(artPharmacy -> artPharmacy.getVisitDate().equals(start)
						|| artPharmacy.getVisitDate().equals(end)
						|| (artPharmacy.getVisitDate().isAfter(start) && artPharmacy.getVisitDate().isBefore(end)))
				.map(artPharmacy -> buildRadetDto(facilityId, artPharmacy, start.minusDays(1), end.plusDays(1)))
				.collect(Collectors.toSet());
	}
	
	private RadetDto buildRadetDto(Long facilityId, ArtPharmacy artPharmacy, LocalDate start, LocalDate end) {
		
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
		int years = Period.between(person.getDateOfBirth(), LocalDate.now()).getYears();
		radetDto.setDateBirth(Date.valueOf(person.getDateOfBirth()));
		radetDto.setAge(years);
		radetDto.setSex(person.getSex());
		radetDto.setPatientId(person.getUuid());
		radetDto.setHospitalNum(person.getHospitalNumber());
		
		//enrollment
		Optional<HivEnrollment> enrollment =
				hivEnrollmentRepository.getHivEnrollmentByPersonAndArchived(person, 0);
		enrollment.ifPresent(e -> {
			radetDto.setUniqueID(e.getUniqueId());
			Long enrollmentSettingId = e.getEnrollmentSettingId();
			String enrollmentSetting = applicationCodesetService.getApplicationCodeset(enrollmentSettingId).getDisplay();
			radetDto.setArtEnrollmentSetting(enrollmentSetting);
		});
		
		
		//art commence
		Optional<ARTClinical> artCommencement =
				artClinicalRepository.findByPersonAndIsCommencementIsTrueAndArchived(person, 0);
		artCommencement.ifPresent(c -> {
			radetDto.setArtStartDate(c.getVisitDate());
			long regimenId = c.getRegimenId();
			Optional<Regimen> regimen = getRegimen(regimenId);
			regimen.ifPresent(r ->
			{
				radetDto.setRegimenAtStart(r.getDescription());
				radetDto.setRegimenLineAtStart(r.getRegimenType().getDescription());
			});
		});
		
		// current vital sign
		Optional<VitalSign> currentVitalSign = vitalSignRepository.getVitalSignByPersonAndArchived(person, 0)
				.stream()
				.sorted(Comparator.comparing(VitalSign::getCaptureDate))
				.sorted(Comparator.comparing(VitalSign::getId).reversed())
				.filter(vitalSign -> {
					LocalDateTime captureDate = vitalSign.getCaptureDate();
					return captureDate.isAfter(start.atStartOfDay()) && captureDate.isBefore(end.atStartOfDay());
				})
				.findFirst();
		currentVitalSign.ifPresent(v -> radetDto.setCurrentWeight(v.getBodyWeight()));
		
		
		//current pharmacy
		Optional<ArtPharmacy> currentPharmacyVisit = artPharmacyRepository.getArtPharmaciesByPersonAndArchived(person, 0)
				.stream()
				.sorted(Comparator.comparing(ArtPharmacy::getVisitDate))
				.sorted(Comparator.comparing(ArtPharmacy::getId).reversed())
				.filter(cPharmacy -> {
					LocalDateTime visitDate = cPharmacy.getVisitDate().atStartOfDay();
					return visitDate.isAfter(start.atStartOfDay()) && visitDate.isBefore(end.atStartOfDay());
				})
				.findFirst();
		
		currentPharmacyVisit.ifPresent(p -> {
			radetDto.setMonthOfArvRefills(0);
			int refillPeriod = artPharmacyRepository.sumRefillPeriodsByPersonAndDateRange
					(person.getUuid(), start.plusDays(1), end.minusDays(1));
			if (refillPeriod > 0) {
				radetDto.setMonthOfArvRefills(refillPeriod / 30);
			}
			radetDto.setLastPickupDate(p.getVisitDate());
			Set<Regimen> regimens = p.getRegimens();
			regimens.forEach(regimen -> setCurrentRegimen(radetDto, regimen, p.getVisitDate()));
		});
		
		//current eac
		Optional<HIVEac> currentHivEac = hIVEacRepository.getAllByPersonAndArchived(person, 0)
				.stream()
				.sorted(Comparator.comparing(HIVEac::getDateOfLastViralLoad))
				.sorted(Comparator.comparing(HIVEac::getId).reversed())
				.filter(cEac -> {
					LocalDateTime visitDate = cEac.getDateOfLastViralLoad().atStartOfDay();
					return visitDate.isAfter(start.atStartOfDay()) && visitDate.isBefore(end.atStartOfDay());
				})
				.findFirst();
		currentHivEac.ifPresent(eac -> {
			List<HIVEacSession> eacSessions = hIVEacSessionRepository.getHIVEacSesByEac(eac);
			radetDto.setNumberOfEACSessionsCompleted(eacSessions.size());
			if (!eacSessions.isEmpty()) {
				LocalDate eacCommenceDate = eacSessions.get(0).getFollowUpDate();
				radetDto.setDateOfCommencementOfEAC(eacCommenceDate);
				if (eacSessions.size() > 1) {
					HIVEacSession hivEacSession = eacSessions.get(1);
					if (hivEacSession != null) {
						radetDto.setDateOf3rdEACCompletion(hivEacSession.getFollowUpDate());
					}
				}
				if (eacSessions.size() > 2) {
					HIVEacSession extHivEacSession = eacSessions.get(2);
					if (extHivEacSession != null) {
						radetDto.setDateOfExtendedEACCompletion(extHivEacSession.getFollowUpDate());
					}
				}
			}
			
		});
		//current viral load
		boolean labExist = moduleService.exist("Lab");
		if (labExist) {
			Log.info(" in lab info {}", labExist);
			Optional<ViralLoadRadetDto> viralLoadDetails =
					hIVEacRepository.getPatientCurrentViralLoadDetails(person.getId(), start.plusDays(1), end.minusDays(1));
			viralLoadDetails.ifPresent(currentViralLoad -> {
				Log.info("current viral load indication {}", currentViralLoad.getIndicationId() + " : ressult :=> " + currentViralLoad.getResult());
				String viralLoadIndication =
						applicationCodesetService.getApplicationCodeset(currentViralLoad.getIndicationId()).getDisplay();
				radetDto.setViralLoadIndication(viralLoadIndication);
				radetDto.setDateOfViralLoadSampleCollection(currentViralLoad.getDateSampleCollected());
				radetDto.setCurrentViralLoad(currentViralLoad.getResult());
				radetDto.setDateOfCurrentViralLoad(currentViralLoad.getResultDate());
			});
		}
		//current biometrics
		boolean bioExist = moduleService.exist("Bio");
		Log.info(" out side biometric info {}", bioExist);
		if (bioExist) {
			Log.info(" in biometric info {}", bioExist);
			List<BiometricRadetDto> biometricFingers =
					hIVEacRepository.getPatientBiometricInfo(person.getUuid(), start.plusDays(1), end.minusDays(1));
			if (!biometricFingers.isEmpty()) {
				Log.info(" number capture {}", biometricFingers.size());
				BiometricRadetDto biometricRadetDto = biometricFingers.get(0);
				Log.info(" number capture date:  {}", biometricRadetDto.getDateCaptured());
				radetDto.setDateBiometricsEnrolled(biometricRadetDto.getDateCaptured());
				radetDto.setValidBiometricsEnrolled("YES");
			}
		}
		return radetDto;
	}
	
	@NotNull
	private Optional<Regimen> getRegimen(long regimenId) {
		return regimenRepository.findById(regimenId);
	}
	
	
	private static void setCurrentRegimen(RadetDto radetDto, Regimen regimen, LocalDate visitDate) {
		String description = regimen.getRegimenType().getDescription();
		if (description.contains("Line")) {
			radetDto.setCurrentRegimenLine(regimen.getRegimenType().getDescription());
			radetDto.setCurrentRegimen(regimen.getDescription());
		}
		if (description.contains("IPT")) {
			radetDto.setIptStartDate(visitDate);
			radetDto.setIptType(regimen.getDescription());
		}
		
	}
	
	
}
