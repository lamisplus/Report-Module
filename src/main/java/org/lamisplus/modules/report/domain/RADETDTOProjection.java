package org.lamisplus.modules.report.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface RADETDTOProjection {
	//biodata
	String getState();

	String getLga();

	String getFacilityName();

	String getUniqueId();
	String getLgaOfResidence();

	String getDatimId();

	String getPersonUuid();

	String getHospitalNumber();

	Date getDateOfBirth();

	Integer getAge();

	String getGender();

	String getTargetGroup();

	String getEnrollmentSetting();

	Date getArtStartDate();

	String getRegimenAtStart();

	String getRegimenLineAtStart();

	//cc
	String getPregnancyStatus();

	String getCurrentClinicalStage();

	Double getCurrentWeight();


	//vl
	String getViralLoadIndication();

	LocalDate getDateOfViralLoadSampleCollection();

	String getCurrentViralLoad();
	LocalDate getDateOfCurrentViralLoad();
	LocalDate getDateOfCurrentViralLoadSample();

	//cd4
	String getLastCd4Count();

	LocalDate getDateOfLastCd4Count();

	//Refill
	String getCurrentRegimenLine();

	String getCurrentARTRegimen();

	Double getMonthsOfARVRefill();

	LocalDate getLastPickupDate();

	LocalDate getNextPickupDate();

	// art status

	LocalDate getCurrentStatusDate();

	String getCurrentStatus();

	LocalDate getPreviousStatusDate();

	String getPreviousStatus();



	//Biometric status
	LocalDate getDateBiometricsEnrolled();

	Integer getNumberOfFingersCaptured();

	//eac
	LocalDate getDateOfCommencementOfEAC();
	Integer getNumberOfEACSessionCompleted();
	LocalDate  getDateOfLastEACSessionCompleted();
	LocalDate getDateOfExtendEACCompletion();
	LocalDateTime getDateOfRepeatViralLoadResult();
	LocalDateTime getDateOfRepeatViralLoadEACSampleCollection();
	String getRepeatViralLoadResult();
	String getTbStatus();
	LocalDate getDateOfTbScreened();
	LocalDate getDateOfCurrentRegimen();
	LocalDate getDateOfIptStart();
	LocalDate getIptCompletionDate();
	String getIptType();
	String getResultOfCervicalCancerScreening();
	String getCervicalCancerScreeningType();

	String getCervicalCancerScreeningMethod();
	String getCervicalCancerTreatmentScreened();
	LocalDate getDateOfCervicalCancerScreening();


	String getOvcNumber();
	String  getHouseholdNumber();

	String getCareEntry();

	String getCauseOfDeath();

	boolean getVlEligibilityStatus();
	LocalDate getDateOfVlEligibilityStatus();

	String  getTbDiagnosticTestType();
	LocalDate getDateOfTbSampleCollection();

	String  getTbDiagnosticResult();

	String getDsdModel();
	LocalDate getDateofTbDiagnosticResultReceived();

	//TB Treatment
	String  getTbTreatementType();
	String  getTbTreatmentOutcome();
	LocalDate getTbTreatmentStartDate();
	LocalDate getTbCompletionDate();
	String getIptCompletionStatus();

	//Crytococal Antigen
	Date getDateOfLastCrytococalAntigen();
	String getLastCrytococalAntigen();

	String getCaseManager();
	String getVaCauseOfDeath();
	String getTreatmentMethodDate();
	LocalDate getDateOfRegistration();
	LocalDate getDateOfEnrollment();
	LocalDate getDateOfLastTbLam();
	String  getTbLamResult();
}