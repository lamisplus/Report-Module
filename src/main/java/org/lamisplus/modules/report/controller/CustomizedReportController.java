package org.lamisplus.modules.report.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.lamisplus.modules.report.domain.dto.CustomizedReportDTO;
import org.lamisplus.modules.report.domain.entity.CustomizedReport;
import org.lamisplus.modules.report.service.CustomizedReportService;
import org.lamisplus.modules.report.service.GenerateExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/customized-reports")
@Api(tags = "Customized Report", description = "API for managing customized reports")
public class CustomizedReportController {

    private final CustomizedReportService service;


    public CustomizedReportController(CustomizedReportService service) {
        this.service = service;
    }

    @GetMapping
    @ApiOperation(value = "Get all customized reports", notes = "Retrieve a list of all customized reports")
    public List<CustomizedReportDTO> getAllReports() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get a customized report by ID", notes = "Retrieve a customized report by its ID")
    public ResponseEntity<CustomizedReportDTO> getReportById(@PathVariable UUID id) {
        CustomizedReportDTO report = service.findById(id);
        return report != null ? new ResponseEntity<>(report, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @ApiOperation(value = "Create a new customized report", notes = "Create a new customized report")
    public ResponseEntity<?> createReport(@RequestBody CustomizedReportDTO report) {
        if (service.isQueryInvalid(report.getQuery())) {
            return new ResponseEntity<>("Query is not valid", HttpStatus.BAD_REQUEST);
        }
        CustomizedReport savedReport = service.save(report);
        return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update an existing customized report", notes = "Update an existing customized report by its ID")
    public ResponseEntity<?> updateReport(@PathVariable UUID id, @RequestBody CustomizedReportDTO report) {
        if (service.isQueryInvalid(report.getQuery())) {
            return new ResponseEntity<>("Query is not valid", HttpStatus.BAD_REQUEST);
        }
        CustomizedReportDTO customizedReportDTO = service.findById(id);
        if (customizedReportDTO == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        report.setId(id);
        CustomizedReport updatedReport = service.save(report);
        return new ResponseEntity<>(updatedReport, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a customized report", notes = "Delete a customized report by its ID")
    public ResponseEntity<Void> deleteReport(@PathVariable UUID id, @RequestBody CustomizedReportDTO report) {
        if (service.findById(id) == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        service.deleteById(report);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/generate-report")
    public ResponseEntity<?> generateCustomizedReport (HttpServletResponse response,
                                   @RequestParam("query") String query,
                                   @RequestParam("reportName") String reportName) throws IOException {

        if (service.isQueryInvalid(query)) {
            return new ResponseEntity<>("Query is not valid", HttpStatus.BAD_REQUEST);
        }
        ByteArrayOutputStream baos = service.generateCustomizedReport(query,  reportName);
        setStream(baos, response);
        return new ResponseEntity<>(HttpStatus.OK);
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
