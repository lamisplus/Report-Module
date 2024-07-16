package org.lamisplus.modules.report.service;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.audit4j.core.util.Log;
import org.lamisplus.modules.base.domain.entities.OrganisationUnitIdentifier;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.hiv.domain.dto.LabReport;
import org.lamisplus.modules.hiv.domain.dto.PatientLineDto;
import org.lamisplus.modules.hiv.domain.dto.PharmacyReport;
import org.lamisplus.modules.hiv.repositories.ArtPharmacyRepository;
import org.lamisplus.modules.hiv.repositories.HIVEacRepository;

import org.lamisplus.modules.report.domain.*;
import org.lamisplus.modules.report.config.Application;
import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.domain.RADETDTOProjection;
import org.lamisplus.modules.report.domain.dto.ClinicDataDto;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.lamisplus.modules.report.utility.DateUtil;
import org.lamisplus.modules.report.utility.ResultSetExtract;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.FluxSink;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	private final SimpMessageSendingOperations messagingTemplate;
	private final QuarterService quarterService;


	@Override
	public ByteArrayOutputStream generatePatientLine(HttpServletResponse response, Long facilityId) {
		LOG.info("Start generating patient line list for facility: " + getFacilityName(facilityId));
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			List<PatientLineDto> data = patientReportService.getPatientLine(facilityId);
//			LOG.info("fullData 1: " + data);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = GenerateExcelDataHelper.fillPatientLineListDataMapper(data);
			LOG.info("fullData 2: " + data.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			return excelService.generate(Constants.PATIENT_LINE_LIST, fullData, Constants.PATIENT_LINE_LIST_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating PATIENT LINE LIST!!!");
			e.printStackTrace();
		}
		LOG.info("End generate patient line list ");
		return null;
	}

	@Override
	@SneakyThrows
	public ByteArrayOutputStream generateClientServiceList(HttpServletResponse response, Long facilityId) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
		try {
			List<ClientServiceDto> data = reportRepository.generateClientServiceList(facilityId);
//			LOG.info("fullData 1: " + Arrays.toString(data.toArray()));
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = GenerateExcelDataHelper.fillClientServiceListDataMapper(data);
			LOG.info("fullData 2: " + data.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			return excelService.generate(Constants.CLIENT_SERVICE_LIST, fullData, Constants.CLIENT_SERVICE_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating CLIENT SERVICE LIST!!!");
		}
		LOG.info("End generate client service list ");
		return null;
	}


//	@Override
//	public ByteArrayOutputStream generateTBReport(Long facilityId, LocalDate start, LocalDate end) {
//		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
//		try {
//			List<TBReportProjection> tbReportProjections = reportRepository.generateTBReport(facilityId, start, end);
//			LOG.info("RADET Size {}", tbReportProjections.size());
//
//			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillTBReportDataMapper(tbReportProjections);
//			return excelService.generate(Constants.RADET_SHEET, data, Constants.RADET_HEADER);
//		} catch (Exception e) {
//			LOG.error("An error Occurred when generating TB report...");
//			LOG.error("Error message: " + e.getMessage());
//			e.printStackTrace();
//		}
//		LOG.info("End generate patient TB report");
//		return null;
//	}


	@Override
	@SneakyThrows
	public ByteArrayOutputStream generateTBReport(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			List<TBReportProjection> tbReportProjections = reportRepository.generateTBReport(facilityId, start, end);
			LOG.info("TB Size {}", tbReportProjections.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillTBReportDataMapper(tbReportProjections, end);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			return excelService.generate(Constants.TB_SHEET, data, Constants.TB_REPORT_HEADER);
		} catch (Exception e) {
			LOG.error("An error Occurred when generating TB report...");
			LOG.error("Error message: " + e.getMessage());
			e.printStackTrace();
		}
		LOG.info("End generate patient TB report");
		return null;
	}

	@Override
	@SneakyThrows
	public ByteArrayOutputStream generateNCDReport(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			List<NCDReportProjection> ncdReportProjections = reportRepository.generateNCDReport(facilityId, start, end);
			LOG.info("TB Size {}", ncdReportProjections.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillNCDReportDataMapper(ncdReportProjections, end);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			return excelService.generate(Constants.NCD_SHEET, data, Constants.NCD_REPORT_HEADER);
		} catch (Exception e) {
			LOG.error("An error Occurred when generating NCD report...");
			LOG.error("Error message: " + e.getMessage());
			e.printStackTrace();
		}
		LOG.info("End generate patient NCD report");
		return null;
	}

	@Override
	@SneakyThrows
	public ByteArrayOutputStream generateEACReport(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			List<EACReportProjection> eacReportProjections = reportRepository.generateEACReport(facilityId, start, end);
			LOG.info("EAC Size {}", eacReportProjections.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillEACReportDataMapper(eacReportProjections, end);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			return excelService.generate(Constants.EAC_SHEET, data, Constants.EAC_REPORT_HEADER);
		} catch (Exception e) {
			LOG.error("An error Occurred when generating EAC report...");
			LOG.error("Error message: " + e.getMessage());
			e.printStackTrace();
		}
		LOG.info("End generate patient EAC report");
		return null;
	}

	@Override
	@SneakyThrows
	public ByteArrayOutputStream generateRadet(Long facilityId, LocalDate start, LocalDate end) {
		LOG.info("Start generating patient RADET for facility:" + getFacilityName(facilityId));
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			List<RADETDTOProjection> radetDtos = radetService.getRadetDtos(facilityId, start, end);

			LOG.info("RADET Size {}", radetDtos.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = excelDataHelper.fillRadetDataMapper(radetDtos,end);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
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
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillPharmacyDataMapper(pharmacies);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
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
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);

			String query = String.format(Application.biometric, facilityId, startDate, endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
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
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		LOG.info("generating Lab report");
		try {
			List<LabReport> labReports = hivEacRepository.getLabReports(facilityId);
			LOG.info("Lab data {}", labReports.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillLabDataMapper(labReports);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
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
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		LOG.info("generating Clinic report");
		try {
			List<ClinicDataDto> clinicData = reportRepository.getClinicData(facilityId);
			LOG.info("Lab data {}", clinicData.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillClinicDataMapper(clinicData);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
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
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		LOG.info("Start generating hts for facility:" + getFacilityName(facilityId));
		try {
			List<HtsReportDto> htsReport = htsReportService.getHtsReport(facilityId, start, end);
			LOG.error("Hts Size: {}", htsReport.size());
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> data = excelDataHelper.fillHtsDataMapper(htsReport);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			return excelService.generate(Constants.HTS_SHEET, data, Constants.HTS_HEADER);
		} catch (Exception e) {
			LOG.error("Error Occurred when generating HTS !!!");
			e.printStackTrace();
		}
		LOG.info("End generate patient HTS");
		return null;
	}

	/*@Override
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
	}*/

	@Override
	public ByteArrayOutputStream generatePrep(Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			if(Application.prep != null){
				LOG.info("PrEP query not available check query.yml file ");
			}
			String query = Application.prep;
			query = query.replace("?1", String.valueOf(facilityId))
					.replace("?2", startDate)
					.replace("?3", endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.prepName, fullData, headers);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating prep data", e);
		}
		LOG.info("End generate prep report");
		return null;
	}


	@Override
	public ByteArrayOutputStream generateAhdReport (Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LocalDate previousQuarterEnd = quarterService.getPreviousQuarter(end).getEnd();
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			LOG.info("previousQuarter date {}", previousQuarterEnd);
			if(Application.ahd != null){
				LOG.info("AHD query not available check query.yml file");
			}
			String query = Application.ahd;
			query = query.replace("?1", String.valueOf(facilityId))
					.replace("?2", startDate)
					.replace("?3", endDate)
					.replace("?4", previousQuarterEnd.toString());


			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.aHDName, fullData, headers);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating AHD data", e);
		}
		LOG.info("End generate AHD report");
		return null;
	}

	@Override
	public ByteArrayOutputStream generateAdrReport (Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			if(Application.ahd != null){
				LOG.info("ADR query not available check query.yml file");
			}
			String query = Application.adr;
			query = query.replace("?1", String.valueOf(facilityId))
					.replace("?2", startDate)
					.replace("?3", endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.adrName, fullData, headers);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating ADR data", e);
		}
		LOG.info("End generate ADR report");
		return null;
	}


	@Override
	public ByteArrayOutputStream generateKpPrevReport(Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			LOG.info("facility Id {}", facilityId);
			if(Application.kpPrev != null){
				LOG.info("Kp-Prev query not available check query.yml file");
			}

//			String query = String.format(Application.kpPrev, facilityId, startDate, endDate);

			String query = Application.kpPrev;
			query = query.replace("?1", String.valueOf(facilityId))
					.replace("?2", startDate)
					.replace("?3", endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.kpPrevName, fullData, headers);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating KP-Prev data", e);
		}
		LOG.info("End generate Kp Prev report");
		return null;
	}


	@Override
	public ByteArrayOutputStream generateHivstReport(Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			if(Application.hivst != null){
				LOG.info("HIVST query not available check query.yml file");
			}
			String query = Application.hivst;
			query = query.replace("?1", String.valueOf(facilityId))
					.replace("?2", startDate)
					.replace("?3", endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.hivstName, fullData, headers);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating Hivst report", e);
		}
		LOG.info("End generate Hivst report");
		return null;
	}


	@Override
	public ByteArrayOutputStream generateLongitudinalPrepReport(Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			if(Application.longitudinal != null){
				LOG.info("Longitudinal PrEP query not available check query.yml file");
			}
			String query = Application.longitudinal;
			query = query.replace("?1", String.valueOf(facilityId))
					.replace("?2", startDate)
					.replace("?3", endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
			List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
			LOG.info("query size is : {}" + fullData.size());

			return excelService.generate(Application.longitudinalPrepName, fullData, headers);
		} catch (Exception e) {
			LOG.info("Error Occurred when generating Longitudinal PrEP data", e);
		}
		LOG.info("End generate Longitudinal PrEP report");
		return null;
	}


	@Override
	public ByteArrayOutputStream generateHtsRegisterReport(Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			if(Application.htsRegister != null){
				LOG.info("HTS Register query found in query.yml file.");
				String query = Application.htsRegister;
				query = query.replace("?1", String.valueOf(facilityId))
						.replace("?2", startDate)
						.replace("?3", endDate);
				ResultSet resultSet = resultSetExtract.getResultSet(query);
				messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
				List<String> headers = resultSetExtract.getHeaders(resultSet);
				messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
				List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
				LOG.info("query size is : {}" + fullData.size());

				return excelService.generate(Application.htsRegisterName, fullData, headers);
			} else {
				LOG.info("HTS Register query not available. Check query.yml file.");
			}
		} catch (Exception e) {
			LOG.info("Error Occurred when generating HTS Register data", e);

		}
		LOG.info("End generate Longitudinal PrEP report");
		return null;
	}


	@Override
	public ByteArrayOutputStream generateIndexQueryLine(Long facilityId, LocalDate start, LocalDate end) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		LOG.info("Start generating Index line list for facility: " + getFacilityName(facilityId));
		try {
			String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
			String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
			LOG.info("start date {}", startDate);
			LOG.info("end date {}", endDate);
			if(Application.indexElicitation != null){
				LOG.info("indexElicitation query not available check query.yml file");
			}

			String query = String.format(Application.indexElicitation, facilityId, startDate, endDate);

			ResultSet resultSet = resultSetExtract.getResultSet(query);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
			List<String> headers = resultSetExtract.getHeaders(resultSet);
			messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
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

//	@Override
//	public ByteArrayOutputStream generateNCDReport(Long facilityId, LocalDate start, LocalDate end) {
//		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
//		try {
//			List<NCDReportProjection> ncdReportProjections = reportRepository.generateNCDReport(facilityId, start, end);
//			LOG.info("TB Size {}", ncdReportProjections.size());
//			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillNCDReportDataMapper(ncdReportProjections, end);
//			return excelService.generate(Constants.NCD_SHEET, data, Constants.NCD_REPORT_HEADER);
//		} catch (Exception e) {
//			LOG.error("An error Occurred when generating NCD report...");
//			LOG.error("Error message: " + e.getMessage());
//			e.printStackTrace();
//		}
//		LOG.info("End generate patient NCD report");
//		return null;
//	}

//	@Override
//	public ByteArrayOutputStream generateEACReport(Long facilityId, LocalDate start, LocalDate end) {
//		LOG.info("Start generating client service list for facility: " + getFacilityName(facilityId));
//		try {
//			List<EACReportProjection> eacReportProjections = reportRepository.generateEACReport(facilityId, start, end);
//			LOG.info("EAC Size {}", eacReportProjections.size());
//			List<Map<Integer, Object>> data = GenerateExcelDataHelper.fillEACReportDataMapper(eacReportProjections, end);
//			return excelService.generate(Constants.EAC_SHEET, data, Constants.EAC_REPORT_HEADER);
//		} catch (Exception e) {
//			LOG.error("An error Occurred when generating EAC report...");
//			LOG.error("Error message: " + e.getMessage());
//			e.printStackTrace();
//		}
//		LOG.info("End generate patient EAC report");
//		return null;
//	}


	public ByteArrayOutputStream getReports(String reportId, Long facilityId, LocalDate start, LocalDate end) throws SQLException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving records from database ...");
		String startDate = dateUtil.ConvertDateToString(start == null ? LocalDate.of(1985, 1, 1) : start);
		String endDate = dateUtil.ConvertDateToString(end == null ? LocalDate.now() : end);
		LOG.info("start date {}", startDate);
		LOG.info("end date {}", endDate);
		String query = "";
		String reportName = "";
		switch (reportId) {
			case "82d80564-6d3e-433e-8441-25db7fe1f2af":
			query = Application.pmtctHts.replace("?1", facilityId.toString()).replace("?2", startDate).replace("?3", endDate);
				reportName = Application.pmtctHtsName;
				System.out.println(query);
				break;
			case "2b6fe1b9-9af0-4af7-9f59-b9cfcb906158":
				query = Application.pmtctMaternalCohort;
				query = query.replace("?1", String.valueOf(facilityId)).replace("?2", startDate).replace("?3", endDate);
//				query = String.format(Application.pmtctMaternalCohort, facilityId, startDate, endDate);
				reportName = Application.pmtctMaternalCohortName;
				System.out.println(query);
				break;
			case "e5f5685b-d355-498f-bc71-191b4037726c":
				query = Application.mhpss;
				query = query.replace("?1", String.valueOf(facilityId)).replace("?2", startDate).replace("?3", endDate);
				reportName = Application.mhpssName;
				System.out.println(query);
				System.out.println(reportName);
				break;
			default:
				LOG.info("Report not available...");
				return null;
		}
		if(query != null || query.equals("")){
			LOG.info("pmtct query not available check query.yml file");
		}
		ResultSet resultSet = resultSetExtract.getResultSet(query);
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Retrieving report headers ...");
		List<String> headers = resultSetExtract.getHeaders(resultSet);
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Mapping result set ...");
		List<Map<Integer, Object>> fullData = resultSetExtract.getQueryValues(resultSet, null);
		LOG.info("query size is : {}" + fullData.size());
		return excelService.generate(reportName, fullData, headers);
	}
}

