package org.lamisplus.modules.report.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.base.service.OrganisationUnitService;
import org.lamisplus.modules.report.domain.PatientLineListDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@AllArgsConstructor
@Service
public class GenerateExcelServiceImpl implements GenerateExcelService {
	
	private final PatientReportService patientReportService;
	
	private final OrganisationUnitService organisationUnitService;
	
	
	@Override
	public ByteArrayOutputStream generatePatientLine(HttpServletResponse response, Long facilityId) {
		LOG.info("Start generate All Final Results Excel");
		try {
			List<PatientLineListDto> data = patientReportService.getPatientLineList(facilityId);
			ExcelService generator = new ExcelService();
			List<Map<Integer, String>> fullData = GenerateExcelDataHelper.fillPatientLineListDataMapper(data);
			return generator.generate(Constants.PATIENT_LINE_LIST, fullData, Constants.PATIENT_LINE_LIST_HEADER);
		} catch (IOException e) {
			LOG.error("Error Occurred when generating EXCEL!!!");
		}
		
		LOG.info("End generate All Final Results Excel");
		return null;
	}
	@Override
	public String getFacilityName(Long facilityId) {
		return organisationUnitService.getOrganizationUnit(facilityId).getName();
	}
}

