package org.lamisplus.modules.report.domain;

import java.time.LocalDate;
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

	// art status

	LocalDate getCurrentStatusDate();

	String getCurrentStatus();

	LocalDate getPreviousStatusDate();

	String getPreviousStatus();

	String getClientVerificationOutCome();


	LocalDate getDateBiometricsEnrolled();
	Integer getNumberOfFingersCaptured();
	LocalDate getDateBiometricsRecaptured();
	Integer getNumberOfFingersRecaptured();
	String getBiometricStatus();
	//eac
	LocalDate getDateOfCommencementOfEAC();
	Integer getNumberOfEACSessionCompleted();
	LocalDate  getDateOfLastEACSessionCompleted();
	LocalDate getDateOfExtendEACCompletion();
	LocalDate getDateOfRepeatViralLoadResult();
	LocalDate getDateOfRepeatViralLoadEACSampleCollection();
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
//	LocalDate getDateOfTbSampleCollection();
	LocalDate getDateOfTbSampleCollected();
	String  getTbDiagnosticResult();

	String getModelDevolvedTo();
	LocalDate getDateOfDevolvement();
	String getCurrentDSDModel();
	LocalDate getDateOfCurrentDSD();
	String getCurrentDsdOutlet();

	LocalDate getDateReturnToSite();
	LocalDate getDateTbSampleCollected();
	LocalDate getDateofTbDiagnosticResultReceived();

	String  getTbTreatementType();
	String  getTbTreatmentOutcome();
	LocalDate getTbTreatmentStartDate();
	LocalDate getTbCompletionDate();
	String getIptCompletionStatus();
	String getTbScreeningType();

	String getCaseManager();
	String getVaCauseOfDeath();
	String getTreatmentMethodDate();
	LocalDate getDateOfRegistration();
	LocalDate getDateOfEnrollment();

	String getNdrPatientIdentifier();
}