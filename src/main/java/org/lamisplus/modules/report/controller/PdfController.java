package org.lamisplus.modules.report.controller;

import org.audit4j.core.util.Log;
import org.lamisplus.modules.report.domain.ClientServiceDto;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.lamisplus.modules.report.service.DemoMsfService;
import org.lamisplus.modules.report.service.GenerateExcelDataHelper;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private DemoMsfService pdfGenerationService;

    private static final Logger logger = Logger.getLogger(DemoMsfService.class.getName());

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadPdf() {
        try {
            List<ClientServiceDto> clientServiceData = reportRepository.generateClientServiceList(2025L); // Replace with actual data source
            List<Map<Integer, Object>> rowData = GenerateExcelDataHelper.fillClientServiceListDataMapper(clientServiceData);

            if (rowData.isEmpty()) {
                throw new IllegalArgumentException("No data available for PDF generation.");
            }

            byte[] pdfBytes = pdfGenerationService.generatePdf(rowData);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "client_service_report.pdf");

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