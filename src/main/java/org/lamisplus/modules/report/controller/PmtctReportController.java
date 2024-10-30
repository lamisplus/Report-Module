package org.lamisplus.modules.report.controller;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.service.PmtctService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PmtctReportController {
    final static String REPORT_URL_VERSION_ONE = "/api/v1";
    private final PmtctService pmtctService;

    @GetMapping(REPORT_URL_VERSION_ONE + "/pmtct-msf/report")
    public ResponseEntity<JsonNode> getPMTCTMSFReport() throws IOException {
        return ResponseEntity.ok(pmtctService.fetchPMTCTMSFReport());
    }
}

