package org.lamisplus.modules.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.service.Constants;
import org.lamisplus.modules.report.service.GenerateExcelService;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PrepReportController {

	final static String REPORT_URL_VERSION_ONE = "/api/v1";
	private final SimpMessageSendingOperations messagingTemplate;
	private final GenerateExcelService generateExcelService;
	

	@PostMapping(REPORT_URL_VERSION_ONE + "/prep-reporting")
	public void prepLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
							@RequestParam("startDate") LocalDate start,
							@RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting PrEP-PBFW Report");

		messagingTemplate.convertAndSend("/topic/prep", "start");

		ByteArrayOutputStream baos = generateExcelService.generatePrep(facility, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend("/topic/prep", "end");

		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done Generating PrEP-PBFW Report");

	}


	@PostMapping(REPORT_URL_VERSION_ONE + "/longitudinal-prep")
	public void longitudinalPrepLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
							 @RequestParam("startDate") LocalDate start,
							 @RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting PrEP Longitudinal report");

		ByteArrayOutputStream baos = generateExcelService.generateLongitudinalPrepReport(facility, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done Generating PrEP Longitudinal report");


	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/kp-prev-report")
	public void kpPrev (HttpServletResponse response, @RequestParam("facilityId") Long facility,
										 @RequestParam("startDate") LocalDate start,
										 @RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting Kp Prev report");

		ByteArrayOutputStream baos = generateExcelService.generateKpPrevReport (facility, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done Generating Kp Prev report");

	}


	private void setStream(ByteArrayOutputStream baos, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Length", Integer.toString(baos.size()));
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(baos.toByteArray());
		outputStream.close();
		response.flushBuffer();
	}
}
