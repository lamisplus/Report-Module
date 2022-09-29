package org.lamisplus.modules.report.service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

public interface GenerateExcelService {
 
 ByteArrayOutputStream generatePatientLine(HttpServletResponse response, Long facilityId);
 ByteArrayOutputStream generateRadet(Long facilityId, LocalDate start, LocalDate end);
 String getFacilityName(Long facilityId);
}
