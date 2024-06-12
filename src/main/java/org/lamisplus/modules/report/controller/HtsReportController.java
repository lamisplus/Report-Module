package org.lamisplus.modules.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.service.GenerateExcelService;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;

@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "HTS Report", description = "Suite of Endpoints that generated HTS Related Reports")
public class HtsReportController {

	final static String REPORT_URL_VERSION_ONE = "/api/v1";
	private final SimpMessageSendingOperations messagingTemplate;
	private final GenerateExcelService generateExcelService;
	

	@PostMapping(REPORT_URL_VERSION_ONE + "/hts-reporting")
	public void htsLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
							@RequestParam("startDate") LocalDate start,
							@RequestParam("endDate") LocalDate end) throws IOException {

		//messagingTemplate.convertAndSend("/topic/hts", "start");

		ByteArrayOutputStream baos = generateExcelService.generateHts(facility, start, end);

		setStream(baos, response);

		//messagingTemplate.convertAndSend("/topic/hts", "end");
	}

	private void setStream(ByteArrayOutputStream baos, HttpServletResponse response) throws IOException {
		response.setHeader("Content-Type", "application/octet-stream");
		response.setHeader("Content-Length", Integer.toString(baos.size()));
		OutputStream outputStream = response.getOutputStream();
		outputStream.write(baos.toByteArray());
		outputStream.close();
		response.flushBuffer();
	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/index-elicitation-reporting")
	public void indexElicitationLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
							@RequestParam("startDate") LocalDate start,
							@RequestParam("endDate") LocalDate end) throws IOException {

		//messagingTemplate.convertAndSend("/topic/hts", "start");

		ByteArrayOutputStream baos = generateExcelService.generateIndexQueryLine(facility, start, end);

		setStream(baos, response);

		//messagingTemplate.convertAndSend("/topic/hts", "end");
	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/ahd-reporting")
	public void generateAhdReport (HttpServletResponse response, @RequestParam("facilityId") Long facility,
								   @RequestParam("startDate") LocalDate start,
								   @RequestParam("endDate") LocalDate end) throws IOException {

//		messagingTemplate.convertAndSend("/topic/ahd", "start");

		ByteArrayOutputStream baos = generateExcelService.generateAhdReport( facility, start, end);

		setStream(baos, response);

//		messagingTemplate.convertAndSend("/topic/hts", "end");
	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/hts-register")
	public void longitudinalPrepLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
										 @RequestParam("startDate") LocalDate start,
										 @RequestParam("endDate") LocalDate end) throws IOException {

		ByteArrayOutputStream baos = generateExcelService.generateHtsRegisterReport(facility, start, end);

		setStream(baos, response);

	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/hivst-report")
	@ApiOperation(value = "Generate HIVST Report", notes = "This Api generates HIVST report", code = 200)
	public void generateHIVSTReport (HttpServletResponse response, @RequestParam("facilityId") Long facility,
										 @RequestParam("startDate") LocalDate start,
										 @RequestParam("endDate") LocalDate end) throws IOException {

		ByteArrayOutputStream baos = generateExcelService.generateHivstReport(facility, start, end);

		setStream(baos, response);

	}

}
