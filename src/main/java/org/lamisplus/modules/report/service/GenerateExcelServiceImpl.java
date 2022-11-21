package org.lamisplus.modules.report.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.OrganisationUnitIdentifier;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.entity.ArtPharmacy;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.hiv.repositories.RegimenRepository;
import org.lamisplus.modules.report.domain.BiometricReportDto;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.domain.RadetDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class GenerateExcelServiceImpl implements GenerateExcelService {
	
	private final PatientReportService patientReportService;
	
	private final RadetService radetService;
	
	private final OrganisationUnitService organisationUnitService;
	
	private final ArtPharmacyRepository artPharmacyRepository;
	
	private final RegimenRepository regimenRepository;
	
	private final BiometricReportService biometricReportService;
	
	private  final  ExcelService  excelService;
	
	
	@Override
	public ByteArrayOutputStream generatePatientLine(HttpServletResponse response, Long facilityId) {
		LOG.info("Start generating patient line list for facility: " + getFacilityName(facilityId));
		try {
			List<PatientLineListDto> data = patientReportService.getPatientLineList(facilityId);
			LOG.info("fullData 1: " + data.size());
			List<Map<Integer, String>> fullData = GenerateExcelDataHelper.fillPatientLineListDataMapper(data);
			System.out.println("fullData 2: " + fullData.size());
			return excelService.generate(Constants.PATIENT_LINE_LIST, fullData, Constants.PATIENT_LINE_LIST_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating PATIENT LINE LIST!!!");
			e.printStackTrace();
		}
		LOG.info("End generate patient line list ");
		return null;
	}
	
	@Override
	public ByteArrayOutputStream generateRadet(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating patient Radet for facility:" + getFacilityName(facilityId));
		try {
			Set<RadetDto> radetData = radetService.getRadetDtos(facilityId, start, end, radetService.getRadetEligibles());
			List<Map<Integer, String>> data = GenerateExcelDataHelper.fillRadetDataMapper(radetData);
			return excelService.generate(Constants.RADET_SHEET, data, Constants.RADET_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating RADET !!!");
			e.printStackTrace();
		}
		LOG.info("End generate patient Radet");
		return null;
	}
	
	@Override
	public ByteArrayOutputStream generatePharmacyReport(Long facilityId) {
		LOG.info("generating Pharmacy");
		try {
			String facilityName = getFacilityName(facilityId);
			String datimId = getDatimId(facilityId);
			List<ArtPharmacy> pharmacies = artPharmacyRepository.findAll()
					.stream()
					.filter(artPharmacy -> artPharmacy.getFacilityId().equals(facilityId))
					.collect(Collectors.toList());
			LOG.info("Pharmacy data {}", pharmacies);
			
			List<Map<Integer, String>> data = GenerateExcelDataHelper.fillPharmacyDataMapper(pharmacies, facilityName,datimId, regimenRepository);
			LOG.info("Pharmacy final data {}", data);
			return excelService.generate(Constants.PHARMACY_SHEET, data, Constants.PHARMACY_HEADER);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating Pharmacy");
			e.printStackTrace();
		}
		LOG.info("End generate Pharmacy");
		return null;
	}
	
	@Override
	public ByteArrayOutputStream generateBiometricReport(Long facilityId, LocalDate start, LocalDate end){
		try {
			LOG.info("start to generate biometric report");
			List<BiometricReportDto> biometricReportDtoList = biometricReportService.getBiometricReportDtoList(facilityId, start, end);
			List<Map<Integer, String>> biometricData = GenerateExcelDataHelper.fillBiometricDataMapper(biometricReportDtoList);
			LOG.info("biometric report size {}", biometricData.size());
			return excelService.generate(Constants.BIOMETRIC_SHEET_SHEET, biometricData, Constants.BIOMETRIC_HEADER);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating biometric data", e);
		}
		LOG.info("End generate biometric report");
		return null;
	}
	
	public String getDatimId(Long facilityId) {
		return organisationUnitService.getOrganizationUnit(facilityId)
				.getOrganisationUnitIdentifiers()
				.parallelStream()
				.filter(identifier -> identifier.getName().equalsIgnoreCase("DATIM_ID"))
				.map(OrganisationUnitIdentifier::getCode)
				.findFirst().orElse("");
}
	
	@Override
	public String getFacilityName(Long facilityId) {
		return organisationUnitService.getOrganizationUnit(facilityId).getName();
	}
	
	
}

