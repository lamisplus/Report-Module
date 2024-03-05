package org.lamisplus.modules.report.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.domain.entities.OrganisationUnitIdentifier;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.dto.LabReport;
import org.lamisplus.modules.hiv.domain.dto.PatientLineDto;
import org.lamisplus.modules.hiv.domain.dto.PharmacyReport;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.hiv.repositories.HIVEacRepository;

import org.lamisplus.modules.report.domain.*;
import org.lamisplus.modules.report.config.Application;
import org.lamisplus.modules.report.domain.BiometricReportDto;
import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.domain.PrepReportDto;
import org.lamisplus.modules.report.domain.RADETDTOProjection;
import org.lamisplus.modules.report.domain.dto.ClinicDataDto;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.lamisplus.modules.report.utility.DateUtil;
import org.lamisplus.modules.report.utility.ResultSetExtract;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
@AllArgsConstructor
@Service
public class GenerateExcelServiceImpl implements GenerateExcelService {
	private final ReportRepository reportRepository;
	
	private final PatientReportService patientReportService;
	
	private final RadetService radetService;
	
	private final OrganisationUnitService organisationUnitService;
	
	private final ArtPharmacyRepository artPharmacyRepository;

	private final HtsReportService htsReportService;

	private final PrepReportService prepReportService;
	
	
	private final BiometricReportService biometricReportService;
	
	private final ExcelService excelService;
	
	private final HIVEacRepository hivEacRepository;
	
	private final GenerateExcelDataHelper excelDataHelper;

	private final ResultSetExtract resultSetExtract;
	private final DateUtil dateUtil;


	@Override
	public ByteArrayOutputStream generatePatientLine(HttpServletResponse response, Long facilityId) {
		LOG.info("Start generating patient line list for facility: " + getFacilityName(facilityId));
		try {
			List<PatientLineDto> data = patientReportService.getPatientLine(facilityId);
//			LOG.info("fullData 1: " + data);
			List<Map<Integer, Object>> fullData = GenerateExcelDataHelper.fillPatientLineListDataMapper(data);
			LOG.info("fullData 2: " + data.size());
			return excelService.generate(Constants.PATIENT_LINE_LIST, fullData, Constants.PATIENT_LINE_LIST_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating PATIENT LINE LIST!!!");
			e.printStackTrace();
		}
		LOG.info("End generate patient line list ");
		return null;
	}

	@Override
	public ByteArrayOutputStream generateClientServiceList(HttpServletResponse response, Long facilityId) {
		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
		try {
			List<ClientServiceDto> data = reportRepository.generateClientServiceList(facilityId);
			LOG.info("fullData 1: " + Arrays.toString(data.toArray()));
			List<Map<Integer, Object>> fullData = GenerateExcelDataHelper.fillClientServiceListDataMapper(data);
			LOG.info("fullData 2: " + data.size());
			return excelService.generate(Constants.CLIENT_SERVICE_LIST, fullData, Constants.CLIENT_SERVICE_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating CLIENT SERVICE LIST!!!");
			e.printStackTrace();
		}
		LOG.info("End generate client service list ");
		return null;
	}

	@Override
	public ByteArrayOutputStream generateRadet(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating patient RADET for facility:" + getFacilityName(facilityId));
		try {
			List<RADETDTOProjection> radetDtos = radetService.getRadetDtos(facilityId, start, end);
			LOG.info("RADET Size {}", radetDtos.size());
			List<Map<Integer, Object>> data = excelDataHelper.fillRadetDataMapper(radetDtos,end);
			return excelService.generate(Constants.RADET_SHEET, data, Constants.RADET_HEADER);
		} catch (Exception e) {
			LOG.error("An error Occurred when generating RADET...");
			LOG.error("Error message: " + e.getMessage());
			e.printStackTrace();
		}
		LOG.info("End generate patient Radet");
		return null;
	}
	
	@Override
	public ByteArrayOutputStream generatePharmacyReport(Long facilityId) {
		LOG.info("generating Pharmacy");
		try {
			List<PharmacyReport> pharmacies = artPharmacyRepository.getArtPharmacyReport(facilityId);
			LOG.info("Pharmacy data {}", pharmacies.size());
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillPharmacyDataMapper(pharmacies);
			return excelService.generate(Constants.PHARMACY_SHEET, data, Constants.PHARMACY_HEADER);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating Pharmacy");
			e.printStackTrace();
		}
		LOG.info("End generate Pharmacy");
		return null;
	}
	
	@Override
	public ByteArrayOutputStream generateBiometricReport(Long facilityId, LocalDate start, LocalDate end) {
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);

			String query = String.format(Application.biometric, facilityId, startDate, endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.biometricName, fullData, headers);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating biometric data", e);
		}
		LOG.info("End generate biometric report");
		return null;
	}
	
	@Override
	public ByteArrayOutputStream generateLabReport(Long facilityId) throws IOException {
		LOG.info("generating Lab report");
		try {
			List<LabReport> labReports = hivEacRepository.getLabReports(facilityId);
			LOG.info("Lab data {}", labReports.size());
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillLabDataMapper(labReports);
			return excelService.generate(Constants.LAB_SHEET_NAME, data, Constants.LAB_HEADER);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating Lab report");
			e.printStackTrace();
		}
		LOG.info("End generate Lab report");
		return null;
	}
	
	@Override
	public ByteArrayOutputStream generateClinicReport(Long facilityId) throws IOException {
		LOG.info("generating Clinic report");
		try {
			List<ClinicDataDto> clinicData = reportRepository.getClinicData(facilityId);
			LOG.info("Lab data {}", clinicData.size());
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillClinicDataMapper(clinicData);
			return excelService.generate(Constants.CLINIC_NAME, data, Constants.CLINIC_HEADER);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating Clinic  report");
			e.printStackTrace();
		}
		LOG.info("End generate Clinic report");
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

	@Override
	public ByteArrayOutputStream generateHts(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating hts for facility:" + getFacilityName(facilityId));
		try {
			List<HtsReportDto> htsReport = htsReportService.getHtsReport(facilityId, start, end);
			LOG.error("Hts Size: {}", htsReport.size());
			List<Map<Integer, Object>> data = excelDataHelper.fillHtsDataMapper(htsReport);
			return excelService.generate(Constants.HTS_SHEET, data, Constants.HTS_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating HTS !!!");
			e.printStackTrace();
		}
		LOG.info("End generate patient HTS");
		return null;
	}

	@Override
	public ByteArrayOutputStream generatePrep(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating prep for facility:" + getFacilityName(facilityId));
		try {
			List<PrepReportDto> prepReport = prepReportService.getPrepReport(facilityId, start, end);
			LOG.error("Prep Size: {}", prepReport.size());
			List<Map<Integer, Object>> data = excelDataHelper.fillPrepDataMapper(prepReport);
			return excelService.generate(Constants.PREP_SHEET, data, Constants.PrEP_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating HTS !!!");
			e.printStackTrace();
		}
		LOG.info("End generate patient HTS");
		return null;
	}

	@Override
	public ByteArrayOutputStream generateIndexQueryLine(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating Index line list for facility: " + getFacilityName(facilityId));
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);

			String query = String.format(Application.indexElicitation, facilityId, startDate, endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.indexElicitationName, fullData, headers);

		} catch (Exception e) {
			LOG.error("Error Occurred when generating INDEX LINE LIST!!!");
			e.printStackTrace();
		}
		LOG.info("End generate INDEX line list ");
		return null;
	}
}

