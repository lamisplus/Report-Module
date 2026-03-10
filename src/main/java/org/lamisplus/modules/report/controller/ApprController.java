package org.lamisplus.modules.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.service.GenerateExcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ApprController {

    private final GenerateExcelService generateExcelService;


    @GetMapping("/weeks")
    public ResponseEntity<List<String>> getWeeksForAppr(
            @RequestParam Long year
    ) {
        List<String> weeks = generateExcelService.getAllWeekForAppr(year);
        return ResponseEntity.ok(weeks);
    }

}


