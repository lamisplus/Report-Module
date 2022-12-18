package org.lamisplus.modules.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.domain.AppointmentReportDto;
import org.lamisplus.modules.report.domain.HIVStatusDisplay;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.service.*;
import org.springframework.http.ResponseEntity;
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
		messagingTemplate.convertAndSend("/topic/patient-line-list/status", "start");
		ByteArrayOutputStream baos = generateExcelService.generatePatientLine(response, facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/patient-line-list/status", "end");
	}
	
	@GetMapping("/patient-line-list/{facilityId}")
	public void patientLineList1(HttpServletResponse response, @PathVariable("facilityId") Long facility) {
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
		
		messagingTemplate.convertAndSend("/topic/radet", "start");
		
		ByteArrayOutputStream baos = generateExcelService.generateRadet(facility, start, end);
		
		setStream(baos, response);
		
		messagingTemplate.convertAndSend("/topic/radet", "end");
	}
	
	@GetMapping("/pharmacy/{facilityId}")
	public void generatePharmacy(HttpServletResponse response, @PathVariable("facilityId") Long facility) throws IOException {
		messagingTemplate.convertAndSend("/topic/pharmacy", "start");
		ByteArrayOutputStream baos = generateExcelService.generatePharmacyReport(facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/pharmacy", "end");
	}
	
	@GetMapping("/laboratory/{facilityId}")
	public void generateLab(HttpServletResponse response, @PathVariable("facilityId") Long facility) throws IOException {
		messagingTemplate.convertAndSend("/topic/laboratory", "start");
		ByteArrayOutputStream baos = generateExcelService.generateLabReport(facility);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/pharmacy", "end");
	}
	
	@GetMapping("/biometric")
	public void generateBiometric(HttpServletResponse response,
	                              @RequestParam("facilityId") Long facility,
	                              @RequestParam("startDate") LocalDate start,
	                              @RequestParam("endDate") LocalDate end
	) throws IOException {
		messagingTemplate.convertAndSend("/topic/biometric", "start");
		ByteArrayOutputStream baos = generateExcelService.generateBiometricReport(facility, start, end);
		setStream(baos, response);
		messagingTemplate.convertAndSend("/topic/biometric", "end");
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
	
}
