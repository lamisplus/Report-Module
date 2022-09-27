package org.lamisplus.modules.report.service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;

public interface GenerateExcelService {
 
 ByteArrayOutputStream generatePatientLine(HttpServletResponse response, Long facilityId);
 String getFacilityName(Long facilityId);
}
