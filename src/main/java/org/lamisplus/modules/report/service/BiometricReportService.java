package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.dto.BiometricRadetDto;
import org.lamisplus.modules.hiv.repositories.HIVEacRepository;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.patient.repository.PersonRepository;
import org.lamisplus.modules.report.domain.BiometricReportDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BiometricReportService {
	
	private final OrganisationUnitService organisationUnitService;
	
	private final PersonRepository personRepository;
	
	private final HIVEacRepository hivEacRepository;
	
	List<BiometricReportDto> getBiometricReportDtoList(Long facilityId, LocalDate start, LocalDate end) {
		OrganisationUnit facility = organisationUnitService.getOrganizationUnit(facilityId);
		Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId();
		OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit(lgaIdOfTheFacility);
		Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId();
		OrganisationUnit state = organisationUnitService.getOrganizationUnit(stateId);
		List<Person> people = personRepository.findAll()
				.stream()
				.filter(person -> person.getFacilityId().equals(facilityId))
				.collect(Collectors.toList());
		List<BiometricReportDto> biometricReportDtoList = new ArrayList<>();
		people.forEach(person -> processAndBuildBiometricReport(biometricReportDtoList, start, end, facility, lgaOrgUnitOfFacility, state, person));
		return biometricReportDtoList;
		
		
	}
	
	private void processAndBuildBiometricReport(
			List<BiometricReportDto> biometricReportDtoList,
			LocalDate start, LocalDate end,
			OrganisationUnit facility,
			OrganisationUnit lgaOrgUnitOfFacility,
			OrganisationUnit state, Person person) {
		List<BiometricRadetDto> biometricInfo = hivEacRepository.getPatientBiometricInfo(person.getUuid(), start, end);
		if (!biometricInfo.isEmpty()) {
			JsonNode address1 = getAddress1(person);
			StringBuilder addressDetails = new StringBuilder();
			String finalAddress = getFinalAddress(address1, addressDetails);
			BiometricReportDto biometricReportDto =
					getBiometricReportDto(facility, lgaOrgUnitOfFacility, state, person, biometricInfo, finalAddress);
			biometricReportDtoList.add(biometricReportDto);
		}
	}
	
	@NotNull
	private static String getFinalAddress(JsonNode address1, StringBuilder addressDetails) {
		if (address1.isArray()) {
			JsonNode addressObject = address1.get(0);
			if (addressObject.hasNonNull("stateId") && addressObject.hasNonNull("district")) {
				addressDetails.append(addressObject.get("city").asText());
				JsonNode town = addressObject.get("line");
				if (!town.isNull() && town.isArray()) {
					for (JsonNode node : town) {
						addressDetails.append(" " + node.asText());
					}
				}
			}
		}
		String firstChar = addressDetails.substring(0, 1).toUpperCase();
		return firstChar + addressDetails.substring(1).toLowerCase();
	}
	
	
	private static JsonNode getAddress1(Person person) {
		JsonNode address = person.getAddress();
		return address.get("address");
	}
	
	@NotNull
	private static BiometricReportDto getBiometricReportDto(
			OrganisationUnit facility,
			OrganisationUnit lgaOrgUnitOfFacility,
			OrganisationUnit state,
			Person person, List<BiometricRadetDto> biometricInfo,
			String finalAddress) {
		LocalDate currentDate = LocalDate.now();
		int age = Period.between(person.getDateOfBirth(), currentDate).getYears();
		BiometricReportDto biometricReportDto = new BiometricReportDto();
		biometricReportDto.setLga(lgaOrgUnitOfFacility.getName());
		biometricReportDto.setFacilityId(facility.getId());
		biometricReportDto.setState(state.getName());
		biometricReportDto.setAddress(finalAddress);
		biometricReportDto.setFacilityName(facility.getName());
		biometricReportDto.setSex(person.getSex());
		biometricReportDto.setHospitalNum(person.getHospitalNumber());
		biometricReportDto.setDateBirth(person.getDateOfBirth());
		biometricReportDto.setUniqueID(person.getHospitalNumber());
		biometricReportDto.setAge(age);
		biometricReportDto.setPatientId(person.getUuid());
		String name = person.getFirstName() + " " + person.getOtherName();
		biometricReportDto.setName(name);
		biometricReportDto.setFingers(biometricInfo.size());
		BiometricRadetDto biometricRadetDto = biometricInfo.get(0);
		biometricReportDto.setEnrollDate(biometricRadetDto.getDateCaptured());
		biometricReportDto.setValid("YES");
		return biometricReportDto;
	}
}
