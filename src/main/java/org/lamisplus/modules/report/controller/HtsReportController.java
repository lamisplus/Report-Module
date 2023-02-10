package org.lamisplus.modules.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.hiv.domain.dto.HIVStatusDisplay;
import org.lamisplus.modules.hiv.service.StatusManagementService;
import org.lamisplus.modules.report.domain.AppointmentReportDto;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.service.AppointmentReportService;
import org.lamisplus.modules.report.service.Constants;
import org.lamisplus.modules.report.service.GenerateExcelService;
import org.lamisplus.modules.report.service.PatientReportService;
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
@RequiredArgsConstructor
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
}
