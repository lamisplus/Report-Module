package org.lamisplus.modules.report.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PatientLineDto {
	
	Long getFacilityId();
	
	String getDatimId();
	
	String getFacilityName();
	
	String getState();
	
	String getLga();
	
	String getPersonUuid();
	
	String getHospitalNumber();

	LocalDate getDateOfBirth();
	
	Integer getAge();
	
	String getGender();
	
	String getStatus();
	
	String getMaritalStatus();
	
	String getEducation();
	
	String getOccupation();
	
	String getResidentialState();
	
	String getResidentialLga();

	Boolean getArchived();
	
	String getCareEntryPoint();
	
	LocalDate getDateOfConfirmedHIVTest();
	
	LocalDate getDateOfRegistration();
	
	String getStatusAtRegistration();
	
	String getCurrentStatus();
	
	LocalDate getDateOfCurrentStatus();
	
	LocalDate getArtStartDate();
	
	Double getBaselineCD4();
	
	Double getBaselineCDP();
	
	Double getBaselineWeight();
	
	Double getBaselineHeight();
	
	String getBaselineClinicStage();
	
	String getBaselineFunctionalStatus();
	
	Double getSystolicBP();
	
	Double getDiastolicBP();
	
	Double getCurrentDiastolicBP();
	
	Double getCurrentSystolicBP();
	
	Double getCurrentWeight();
	
	Double getCurrentHeight();
	
	String getFirstRegimenLine();
	
	String getFirstRegimen();
	
	String getCurrentRegimenLine();
	
	String getCurrentRegimen();
	
	LocalDate getDateOfLastRefill();
	
	Integer getLastRefillDuration();
	
	LocalDate getDateOfNextRefill();
	
	LocalDate getDateDevolved();
	
	String getDmocType();
	
	String getLastClinicStage();
	
	LocalDate getDateOfLastClinic();
	
	LocalDate getDateOfNextClinic();
	
	LocalDateTime getDateOfSampleCollected();
	
	String getLastViralLoad();
	
	LocalDateTime getDateOfLastViralLoad();
	
	String getViralLoadType();
	String getUniqueId();
	String getAdherenceLevel();

	String getCmName();
	
}
			
