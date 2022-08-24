package org.lamisplus.modules.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.domain.AppointmentReportDto;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.lamisplus.modules.report.service.AppointmentReportService;
import org.lamisplus.modules.report.service.PatientReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/reporting")
@RequiredArgsConstructor
public class PatientReportController {

    private final SimpMessageSendingOperations messagingTemplate;

    private final PatientReportService patientReportService;

    private final AppointmentReportService appointmentReportService;


    @PostMapping("/patient-line-list")
    public void patientLineList(HttpServletResponse response, @RequestParam("facilityId") Long facility) throws IOException {
        messagingTemplate.convertAndSend ("/topic/patient-line-list/status", "start");
        ByteArrayOutputStream baos = patientReportService.generatePatientLineList (facility);
        setStream (baos, response);
        messagingTemplate.convertAndSend ("/topic/patient-line-list/status", "end");
    }

    @GetMapping("/patient-line-list")
    public ResponseEntity<List<PatientLineListDto>> patientLineList(@RequestParam("facilityId") Long facility) {
        return ResponseEntity.ok (patientReportService.getPatientData (facility));
    }

    @GetMapping("/miss-refill")
    public ResponseEntity<List<AppointmentReportDto>> patientLineList(
            @RequestParam("facilityId") Long facility,
            @RequestParam("startDate") LocalDate start,
            @RequestParam("endDate") LocalDate end) {
        return ResponseEntity.ok (appointmentReportService.getMissRefillAppointment (facility, start, end));
    }
    @GetMapping("/miss-clinic")
    public ResponseEntity<List<AppointmentReportDto>> getMissClinicVisit(
            @RequestParam("facilityId") Long facility,
            @RequestParam("startDate") LocalDate start,
            @RequestParam("endDate") LocalDate end) {
        return ResponseEntity.ok (appointmentReportService.getMissClinicAppointment (facility, start, end));
    }


    private void setStream(ByteArrayOutputStream baos, HttpServletResponse response) throws IOException {
        response.setHeader ("Content-Type", "application/octet-stream");
        response.setHeader ("Content-Length", Integer.toString (baos.size ()));
        OutputStream outputStream = response.getOutputStream ();
        outputStream.write (baos.toByteArray ());
        outputStream.close ();
        response.flushBuffer ();
    }
}
