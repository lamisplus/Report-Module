package org.lamisplus.modules.report.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.base.domain.entities.OrganisationUnit;
import org.lamisplus.modules.base.domain.entities.OrganisationUnitIdentifier;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.dto.BiometricReport;
import org.lamisplus.modules.hiv.repositories.HIVEacRepository;
import org.lamisplus.modules.patient.domain.entity.Person;
import org.lamisplus.modules.report.domain.BiometricReportDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BiometricReportService {
	
	private final OrganisationUnitService organisationUnitService;
	
	private final HIVEacRepository hivEacRepository;
	
	List<BiometricReportDto> getBiometricReportDtoList(Long facilityId, LocalDate start, LocalDate end) {
		OrganisationUnit facility = organisationUnitService.getOrganizationUnit(facilityId);
		Long lgaIdOfTheFacility = facility.getParentOrganisationUnitId();
		OrganisationUnit lgaOrgUnitOfFacility = organisationUnitService.getOrganizationUnit(lgaIdOfTheFacility);
		Long stateId = lgaOrgUnitOfFacility.getParentOrganisationUnitId();
		OrganisationUnit state = organisationUnitService.getOrganizationUnit(stateId);
		return processAndBuildBiometricReport(start, end, facility, lgaOrgUnitOfFacility, state);
	}
	
	private List<BiometricReportDto> processAndBuildBiometricReport(
			LocalDate start, LocalDate end,
			OrganisationUnit facility,
			OrganisationUnit lgaOrgUnitOfFacility,
			OrganisationUnit state) {
		List<BiometricReport> biometricReports = hivEacRepository.getBiometricReports(facility.getId(), start, end);
		if(!biometricReports.isEmpty())
		 return biometricReports.stream()
				   .filter(Objects::nonNull)
				   .map(b -> getBiometricReportDto(facility, lgaOrgUnitOfFacility,state, b))
				   .collect(Collectors.toList());
		return null;
		
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
						addressDetails.append(" ").append(node.asText());
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
			BiometricReport info
			) {
		String datimId = facility.getOrganisationUnitIdentifiers()
				.stream()
				.filter(identifier -> identifier.getName().equalsIgnoreCase("DATIM_ID"))
				.map(OrganisationUnitIdentifier::getCode)
				.findFirst().orElse("");
		
		BiometricReportDto biometricReportDto = new BiometricReportDto();
		biometricReportDto.setLga(lgaOrgUnitOfFacility.getName());
		biometricReportDto.setFacilityId(facility.getId());
		biometricReportDto.setDatimId(datimId);
		biometricReportDto.setState(state.getName());
		biometricReportDto.setAddress(info.getAddress());
		biometricReportDto.setPhone(info.getPhone());
		biometricReportDto.setFacilityName(facility.getName());
		biometricReportDto.setSex(info.getSex());
		biometricReportDto.setHospitalNum(info.getHospitalNumber());
		biometricReportDto.setDateBirth(info.getDob());
		biometricReportDto.setUniqueID(info.getHospitalNumber());
		biometricReportDto.setAge(info.getAge());
		biometricReportDto.setPatientId(info.getId()+"");
		biometricReportDto.setName(info.getName());
		biometricReportDto.setFingers(info.getFinger());
		biometricReportDto.setEnrollDate(info.getEnrollment());
		biometricReportDto.setValid("YES");
		return biometricReportDto;
	}
}
