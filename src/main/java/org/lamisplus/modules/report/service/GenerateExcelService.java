package org.lamisplus.modules.report.service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;



public interface GenerateExcelService {
 
 ByteArrayOutputStream generatePatientLine(HttpServletResponse response, Long facilityId);
 ByteArrayOutputStream generateRadet(Long facilityId, LocalDate start, LocalDate end);
 ByteArrayOutputStream generatePharmacyReport(Long facilityId) throws IOException;
 ByteArrayOutputStream generateBiometricReport(Long facilityId,  LocalDate start, LocalDate end) throws IOException;
 ByteArrayOutputStream generateLabReport(Long facilityId) throws IOException;
 ByteArrayOutputStream generateClinicReport(Long facilityId) throws IOException;
 String getFacilityName(Long facilityId);
 ByteArrayOutputStream generateHts(Long facilityId, LocalDate start, LocalDate end);

 ByteArrayOutputStream generatePrep(Long facilityId, LocalDate start, LocalDate end);

 ByteArrayOutputStream generateKpPrevReport (Long facilityId, LocalDate start, LocalDate end);


 ByteArrayOutputStream generateLongitudinalPrepReport(Long facilityId, LocalDate start, LocalDate end);
 ByteArrayOutputStream generateHivstReport(Long facilityId, LocalDate start, LocalDate end);
 ByteArrayOutputStream generateHtsRegisterReport(Long facilityId, LocalDate start, LocalDate end);

 ByteArrayOutputStream generateIndexQueryLine(Long facilityId, LocalDate start, LocalDate end);

 ByteArrayOutputStream generateClientServiceList(HttpServletResponse response, Long facility);

 ByteArrayOutputStream generateTBReport(Long facilityId, LocalDate start, LocalDate end);
 ByteArrayOutputStream generateEACReport(Long facilityId, LocalDate start, LocalDate end);

 ByteArrayOutputStream generateAhdReport(Long facilityId, LocalDate start, LocalDate end);

 ByteArrayOutputStream generateAdrReport(Long facilityId, LocalDate start, LocalDate end);

 ByteArrayOutputStream generateNCDReport(Long facilityId, LocalDate start, LocalDate end);
  ByteArrayOutputStream getReports(String reportId, Long facilityId, LocalDate start, LocalDate end) throws SQLException;

 ByteArrayOutputStream generateFamilyIndex(Long facilityId);


}
