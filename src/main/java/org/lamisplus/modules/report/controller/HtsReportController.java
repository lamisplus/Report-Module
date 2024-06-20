package org.lamisplus.modules.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.service.Constants;
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
	@ApiOperation(value = "Generate HTS Report", notes = "This Api generates HTS report", code = 200)
	public void htsLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
							@RequestParam("startDate") LocalDate start,
							@RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting HTS report");

		//messagingTemplate.convertAndSend("/topic/hts", "start");

		ByteArrayOutputStream baos = generateExcelService.generateHts(facility, start, end);

		setStream(baos, response);
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating HTS report");
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
	@ApiOperation(value = "Generate Index Elicitation Report", notes = "This Api generates Index Elicitation report", code = 200)
	public void indexElicitationLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
							@RequestParam("startDate") LocalDate start,
							@RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting index-elicitation report");

		//messagingTemplate.convertAndSend("/topic/hts", "start");

		ByteArrayOutputStream baos = generateExcelService.generateIndexQueryLine(facility, start, end);

		setStream(baos, response);
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating index-elicitation  report");

		//messagingTemplate.convertAndSend("/topic/hts", "end");
	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/ahd-reporting")
	@ApiOperation(value = "Generate AHD Report", notes = "This Api generates AHD report", code = 200)
	public void generateAhdReport (HttpServletResponse response, @RequestParam("facilityId") Long facility,
								   @RequestParam("startDate") LocalDate start,
								   @RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting AHD report");

//		messagingTemplate.convertAndSend("/topic/ahd", "start");

		ByteArrayOutputStream baos = generateExcelService.generateAhdReport( facility, start, end);

		setStream(baos, response);
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating AHD report");

//		messagingTemplate.convertAndSend("/topic/hts", "end");
	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/adr-reporting")
	@ApiOperation(value = "Generate ADR Report", notes = "This Api generates ADR report", code = 200)
	public void generateAdrReport (HttpServletResponse response, @RequestParam("facilityId") Long facility,
								   @RequestParam("startDate") LocalDate start,
								   @RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting ADR report");

//		messagingTemplate.convertAndSend("/topic/ahd", "start");

		ByteArrayOutputStream baos = generateExcelService.generateAdrReport( facility, start, end);

		setStream(baos, response);
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating ADR report");

//		messagingTemplate.convertAndSend("/topic/hts", "end");
	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/hts-register")
	@ApiOperation(value = "Generate HTS Register Report", notes = "This Api generates HTS Register report", code = 200)
	public void longitudinalPrepLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility,
										 @RequestParam("startDate") LocalDate start,
										 @RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting HTS register report");
		ByteArrayOutputStream baos = generateExcelService.generateHtsRegisterReport(facility, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating HTS Register report");

	}

	@PostMapping(REPORT_URL_VERSION_ONE + "/hivst-report")
	@ApiOperation(value = "Generate HIVST Report", notes = "This Api generates HIVST report", code = 200)
	public void generateHIVSTReport (HttpServletResponse response, @RequestParam("facilityId") Long facility,
										 @RequestParam("startDate") LocalDate start,
										 @RequestParam("endDate") LocalDate end) throws IOException {
		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting HIVST report");

		ByteArrayOutputStream baos = generateExcelService.generateHivstReport(facility, start, end);

		setStream(baos, response);

		messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating HIVST report");

	}

}
