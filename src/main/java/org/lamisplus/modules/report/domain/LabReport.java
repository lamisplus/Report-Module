package org.lamisplus.modules.report.domain;

import java.time.LocalDateTime;

public interface LabReport {
	Long getFacilityId();
	String getFacility();
	String getDatimId();
	String getPatientId();
	String getHospitalNum();
	String getTest();
	String getResult();
	LocalDateTime getSampleCollectionDate();
	LocalDateTime getDateReported();
}
