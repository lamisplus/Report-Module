package org.lamisplus.modules.report.domain.dto;

import javafx.stage.Stage;
import liquibase.pro.packaged.m;
import org.lamisplus.modules.patient.domain.Patient;
import org.lamisplus.modules.patient.domain.entity.Visit;

import java.time.LocalDate;

public interface ClinicDataDto {
	Long getFacilityId();
	String getFacilityName();
	String getState	();
	String getLga	();
	String getDatimId();
	
	String getPatientId();
	String getHospitalNumber();
	LocalDate getVisitDate();
	String getClinicalStage ();
	String getFunctionalStatus();
	String getTbStatus();
	Double getBodyWeight();
	Double getHeight();
	Double getSystolic();
	String getDiastolic();
	String getPregnancyStatus();
	
	//pregnancyStatus
	LocalDate getNextAppointment();
	
}
