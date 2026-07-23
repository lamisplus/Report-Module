package org.lamisplus.modules.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.dto.ReportProjectionDTO;
import org.lamisplus.modules.report.service.HtsReportBuilderService;
import org.lamisplus.modules.report.service.MsfService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Api(tags = "HTS MSF Reports")
public class MsfReportController {

    private static final String EXCEL_MEDIA_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private final HtsReportBuilderService builderService;
    private final MsfService excelService;

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
    @GetMapping(
            value = "/hts_msf",
            produces = EXCEL_MEDIA_TYPE
    )
    public ResponseEntity<ByteArrayResource> export(
            @RequestParam("facilityId") Long facilityId,
            @RequestParam("month") String month) {


        ReportProjectionDTO dto = builderService.build(facilityId, month);

        byte[] excelBytes = excelService.generate(dto);

        String fileName = String.format(
                "HTS_MSF_%s.xlsx",
                month
        );

        ByteArrayResource resource = new ByteArrayResource(excelBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CACHE_CONTROL,
                        "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentLength(excelBytes.length)
                .contentType(MediaType.parseMediaType(EXCEL_MEDIA_TYPE))
                .body(resource);
    }
}