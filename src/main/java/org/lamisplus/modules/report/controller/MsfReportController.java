package org.lamisplus.modules.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
import org.lamisplus.modules.report.service.GenericExcelReportService;
import org.lamisplus.modules.report.service.GenericPdfReportService;
import org.lamisplus.modules.report.service.HtsReportBuilderService;
import org.lamisplus.modules.report.service.PrEPReportBuilderService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Api(tags = "MSF Reports")
public class MsfReportController {

    private static final String EXCEL_MEDIA_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String PDF_MEDIA_TYPE = "application/pdf";

    private final HtsReportBuilderService builderService;
    private final PrEPReportBuilderService  prEPBuilderService;
    private final GenericExcelReportService excelService;
    private final GenericPdfReportService pdfService;

    @ApiOperation(
            value = "Download HTS Monthly Summary Form",
            notes = "Generates and downloads the HTS Monthly Summary Form as an Excel (.xlsx) file",
            produces = EXCEL_MEDIA_TYPE
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Excel report generated successfully"),
            @ApiResponse(code = 400, message = "Invalid date supplied"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/hts_msf")
    public ResponseEntity<ByteArrayResource> export(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("month") String month,
            @RequestParam(defaultValue = "excel") String format) {

        ReportProjectionDTO dto = builderService.build(facilityId, month);

        byte[] fileBytes;
        String fileName;
        String mediaType;

        switch (format.toLowerCase()) {
            case "pdf":
                fileBytes = pdfService.generate(dto);
                fileName = String.format("HTS_MSF_%s.pdf", month);
                mediaType = PDF_MEDIA_TYPE;
                break;

            case "excel":
            case "xlsx":
                fileBytes = excelService.generate(dto);
                fileName = String.format("HTS_MSF_%s.xlsx", month);
                mediaType = EXCEL_MEDIA_TYPE;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported format. Use 'excel' or 'pdf'");
        }

        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CACHE_CONTROL,
                        "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentLength(fileBytes.length)
                .contentType(MediaType.parseMediaType(mediaType))
                .body(resource);
    }

    @ApiOperation(
            value = "Download PrEP Monthly Summary Form",
            notes = "Generates and downloads the PrEP Monthly Summary Form as an Excel (.xlsx) file",
            produces = EXCEL_MEDIA_TYPE
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Excel report generated successfully"),
            @ApiResponse(code = 400, message = "Invalid date supplied"),
            @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/prep_msf")
    public ResponseEntity<ByteArrayResource> exportPrep(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("month") String month,
            @RequestParam(defaultValue = "excel") String format) {

        ReportProjectionDTO dto = prEPBuilderService.build(facilityId, month);

        byte[] fileBytes;
        String fileName;
        String mediaType;

        switch (format.toLowerCase()) {
            case "pdf":
                fileBytes = pdfService.generate(dto);
                fileName = String.format("PrEP_MSF_%s.pdf", month);
                mediaType = PDF_MEDIA_TYPE;
                break;

            case "excel":
            case "xlsx":
                fileBytes = excelService.generate(dto);
                fileName = String.format("PrEP_MSF_%s.xlsx", month);
                mediaType = EXCEL_MEDIA_TYPE;
                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported format. Use 'excel' or 'pdf'");
        }

        ByteArrayResource resource = new ByteArrayResource(fileBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CACHE_CONTROL,
                        "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentLength(fileBytes.length)
                .contentType(MediaType.parseMediaType(mediaType))
                .body(resource);
    }
}
