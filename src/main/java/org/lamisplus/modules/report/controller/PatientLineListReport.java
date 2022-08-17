package org.lamisplus.modules.report.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.service.PatientDataConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/api/v1/reporting")
@RequiredArgsConstructor
public class PatientLineListReport {

    private final SimpMessageSendingOperations messagingTemplate;

    private final PatientDataConverter patientDataConverter;


    @PostMapping("/patient-line-list")
    public void patientLineList(HttpServletResponse response) throws IOException, ParseException {
        messagingTemplate.convertAndSend ("/topic/patient-line-list/status", "start");
        ByteArrayOutputStream baos = patientDataConverter.generatePatientLineList ();
        setStream (baos, response);
        messagingTemplate.convertAndSend ("/topic/patient-line-list/status", "end");
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
