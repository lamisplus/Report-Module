package org.lamisplus.modules.report.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.audit4j.core.util.Log;
import org.lamisplus.modules.report.service.*;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

import org.lamisplus.modules.report.utility.BaseInformation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequiredArgsConstructor
@Api(value = "MSF Report", description = "Suite of Endpoints that generated MSF Related Reports")
public class MsfController {
    private final PdfService pdfService;
    private final BaseInformation baseInformation;
    private final PrEPMSFService prEPMSFService;
    static final String REPORT_URL_VERSION_ONE = "/api/v1";
    private final SimpMessageSendingOperations messagingTemplate;
    private final GenerateExcelService generateExcelService;


    @PostMapping(REPORT_URL_VERSION_ONE + "/prep-msf")
    @ApiOperation(value = "Generate PrEP MSF Report", notes = "This Api generates PrEP MSF report", code = 200)
    public ResponseEntity<byte[]> generatePrEPMsf(HttpServletResponse response, @RequestParam("facilityId") Long facilityId,
                            @RequestParam("startDate") LocalDate startDate,
                            @RequestParam("endDate") LocalDate endDate,
                            @RequestParam("status") Boolean status) throws IOException {
        messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Starting PrEP MSF report");
        String facilityName = baseInformation.getFacilityName(facilityId);
        String lgaName = baseInformation.getLocalGovernmentName(facilityId);
        String stateName = baseInformation.getStateName(facilityId);
        String title = "PrEP Monthly Summary Form - CAB-LA Pilot Facilities";
        List<String> headerLabels = Constants.PREP_MSF_HEADERS;
        LocalDate dateSelected = startDate;
        Month month = startDate.getMonth();


        // Define data values for the right column
        List<? extends Serializable> headProperty = Arrays.asList(facilityName, lgaName, stateName, String.valueOf(month), String.valueOf(dateSelected.getYear()), "");

        try {
            JsonNode rowData = prEPMSFService.fetchPrEPMSFReport(facilityId, startDate, endDate);

            if (rowData.isEmpty()) {
                throw new IllegalArgumentException("No data available for PDF generation.");
            }

            // Convert JsonNode to List<Map<String, Object>> for PDF generation
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, Object>> rowDataList = mapper.convertValue(
                    rowData.get("prep_msf_query"),
                    new TypeReference<List<Map<String, Object>>>() {}
            );

            byte[] pdfBytes = pdfService.generatePdf(rowDataList, (List<String>) headProperty,headerLabels, title);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "prep_msf_report.pdf");

            messagingTemplate.convertAndSend(Constants.REPORT_GENERATION_PROGRESS_TOPIC, "Done generating PrEP msf report");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            Log.error("Error generating PDF: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }


    }
}