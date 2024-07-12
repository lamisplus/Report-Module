package org.lamisplus.modules.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.hiv.domain.dto.HIVStatusDisplay;
import org.lamisplus.modules.hiv.service.StatusManagementService;
import org.lamisplus.modules.report.domain.AppointmentReportDto;

import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/reporting")
@RequiredArgsConstructor
public class PatientReportController {
	
	private final SimpMessageSendingOperations messagingTemplate;
	
	private final PatientReportService patientReportService;
	
	private final AppointmentReportService appointmentReportService;
	
	
	private final GenerateExcelService generateExcelService;
	
	private final StatusManagementService statusManagementService;


	
	@PostMapping("/patient-line-list")
	public void patientLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting Patient line list report");
		messagingTemplate.convertAndSend("/topic/patient-line-list/status", "start");
		ByteArrayOutputStream baos = generateExcelService.generatePatientLine(response, facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/patient-line-list/status", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating Patient line list report");
	}

	@GetMapping("/client-service-list/{facilityId}")
	public void clientServiceList(HttpServletResponse response, @PathVariable("facilityId") Long facility) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting Client service list report");
		messagingTemplate.convertAndSend("/topic/client-service-list", "start");
		ByteArrayOutputStream baos = generateExcelService.generateClientServiceList(response, facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/client-service-list", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating client service report");
	}
	
	@GetMapping("/patient-line-list/{facilityId}")
	public void patientLineList1(HttpServletResponse response, @PathVariable("facilityId") Long facility) {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting patient line list report");
		String facilityName = generateExcelService.getFacilityName(facility);
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=radet_" + facilityName + Constants.EXCEL_EXTENSION_XLSX;
		response.setHeader(headerKey, headerValue);
		generateExcelService.generatePatientLine(response, facility);
	}
	
	@GetMapping("/radet")
	public void getRadet(
			HttpServletResponse response,
			@RequestParam("facilityId") Long facility,
			@RequestParam("startDate") LocalDate start,
			@RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting radet report");
		
		messagingTemplate.convertAndSend("/topic/radet", "start");
		
		ByteArrayOutputStream baos = generateExcelService.generateRadet(facility, start, end);
		
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/radet", "end");

		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating radet report");
	}

	@GetMapping("/tb-report")
	public void getTBReport(
			HttpServletResponse response,
			@RequestParam("facilityId") Long facilityId,
			@RequestParam("start") LocalDate start,
			@RequestParam("end") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting TB report");

		messagingTemplate.convertAndSend("/topic/tb-report", "start");

		ByteArrayOutputStream baos = generateExcelService.generateTBReport(facilityId, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend("/topic/tb-report", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating TB report");
	}

	@GetMapping("/eac-report")
	public void getEACReport(
			HttpServletResponse response,
			@RequestParam("facilityId") Long facilityId,
			@RequestParam("start") LocalDate start,
			@RequestParam("end") LocalDate end) throws IOException {

		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting EAC report");
		messagingTemplate.convertAndSend("/topic/eac-report", "start");
		LOG.info("Parameters {} ***** {} ******* {}", facilityId, start, end);
		ByteArrayOutputStream baos = generateExcelService.generateEACReport(facilityId, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend("/topic/eac-report", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating EAC report");
	}

	@GetMapping("/ncd-report")
	public void getNCDReport(
			HttpServletResponse response,
			@RequestParam("facilityId") Long facilityId,
			@RequestParam("start") LocalDate start,
			@RequestParam("end") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting NCD report");

		messagingTemplate.convertAndSend("/topic/ncd-report", "start");

		ByteArrayOutputStream baos = generateExcelService.generateNCDReport(facilityId, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend("/topic/ncd-report", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating NCD report");
	}
	
	@GetMapping("/pharmacy/{facilityId}")
	public void generatePharmacy(HttpServletResponse response, @PathVariable("facilityId") Long facility) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting Pharmacy report");
		messagingTemplate.convertAndSend("/topic/pharmacy", "start");
		ByteArrayOutputStream baos = generateExcelService.generatePharmacyReport(facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/pharmacy", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating Pharmacy report");
	}
	
	@GetMapping("/laboratory/{facilityId}")
	public void generateLab(HttpServletResponse response, @PathVariable("facilityId") Long facility) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting Laboratory report");
		messagingTemplate.convertAndSend("/topic/laboratory", "start");
		ByteArrayOutputStream baos = generateExcelService.generateLabReport(facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/pharmacy", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done Generating Laboratory report");

	}
	
	@GetMapping("/biometric")
	public void generateBiometric(HttpServletResponse response,
	                              @RequestParam("facilityId") Long facility,
	                              @RequestParam("startDate") LocalDate start,
	                              @RequestParam("endDate") LocalDate end
	) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting Biometric report");
		messagingTemplate.convertAndSend("/topic/biometric", "start");
		ByteArrayOutputStream baos = generateExcelService.generateBiometricReport(facility, start, end);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/biometric", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating Biometric report");
	}
	
	@GetMapping("/patient-line-list")
	public ResponseEntity<List<PatientLineListDto>> patientLineList(@RequestParam("facilityId") Long facility) throws ExecutionException, InterruptedException {
		return ResponseEntity.ok(patientReportService.getPatientData(facility));
	}
	
	@GetMapping("/miss-refill")
	public ResponseEntity<Set<AppointmentReportDto>> patientLineList(
			@RequestParam("facilityId") Long facility,
			@RequestParam("startDate") LocalDate start,
			@RequestParam("endDate") LocalDate end) {
		return ResponseEntity.ok(appointmentReportService.getMissRefillAppointment(facility, start, end));
	}
	
	@GetMapping("/miss-clinic")
	public ResponseEntity<Set<AppointmentReportDto>> getMissClinicVisit(
			@RequestParam("facilityId") Long facility,
			@RequestParam("startDate") LocalDate start,
			@RequestParam("endDate") LocalDate end) {
		return ResponseEntity.ok(appointmentReportService.getMissClinicAppointment(facility, start, end));
	}
	
	@GetMapping("/clinic-appointment")
	public ResponseEntity<Set<AppointmentReportDto>> getClinicAppointment(
			@RequestParam("facilityId") Long facility,
			@RequestParam("startDate") LocalDate start,
			@RequestParam("endDate") LocalDate end) {
		return ResponseEntity.ok(appointmentReportService.getClinicAppointment(facility, start, end));
	}
	
	@GetMapping("/refill-appointment")
	public ResponseEntity<Set<AppointmentReportDto>> getRefillAppointment(
			@RequestParam("facilityId") Long facility,
			@RequestParam("startDate") LocalDate start,
			@RequestParam("endDate") LocalDate end) {
		return ResponseEntity.ok(appointmentReportService.getRefillAppointment(facility, start, end));
	}


	@SendTo(Constants.REPORT_GENERATION_PROGRESS_TOPIC)
	public String broadcastMessage(@Payload String message) {
		return message;
	}

	private void setStream(ByteArrayOutputStream baos, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Length", Integer.toString(baos.size()));
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(baos.toByteArray());
		outputStream.close();
		response.flushBuffer();
	}
	
	@GetMapping("/client-status-summary")
	public ResponseEntity<List<HIVStatusDisplay>> getClientStatusHistory(
			@RequestParam("personUuid") String personUuid,
			@RequestParam("endDate") LocalDate startingDate) {
		List<HIVStatusDisplay> result = new ArrayList<>();
		statusManagementService.getClientStatusSummary(personUuid, startingDate,result);
		List<HIVStatusDisplay> sorted = result.stream()
						.filter(Objects::nonNull)
						.sorted(Comparator.comparing(HIVStatusDisplay::getQuarterEndDate).reversed())
						.collect(Collectors.toList());
		return ResponseEntity.ok(sorted);
	}
	
	@GetMapping("/clinic-data/{facilityId}")
	public void generateClinicData(HttpServletResponse response, @PathVariable("facilityId") Long facility) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting clinic Data report");
		messagingTemplate.convertAndSend("/topic/clinic-data", "start");
		ByteArrayOutputStream baos = generateExcelService.generateClinicReport(facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/clinic-data", "end");
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating Clinic report");
	}
	
	
}
