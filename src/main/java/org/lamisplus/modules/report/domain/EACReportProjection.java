package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface EACReportProjection {
    String getState();
    String getLga();
    String getLgaOfResidence();
    String getFacilityName();
    String getDatimId();
    String getPatientId();
    String getHospitalNumber();
    String getUniqueId();
    String getSex();
    String getTargetGroup();
    LocalDate getDateOfBirth();
    LocalDate getArtStartDate();
    String getRegimenAtArtStart();
    LocalDate getDateOfStartOfRegimenBeforeUnsuppressedVLR();
    String getRegimenLineBeforeUnsuppression();
    String getRegimenBeforeUnsuppression();
    LocalDate getLastPickupDateBeforeUnsuppressedVLR();
    Integer getMonthOfARVRefillOfLastPickupDateBeforeUnsuppressedVLR();
    LocalDate getDateOfVLSCOfUnsuppressedVLR();
    String getMostRecentUnsuppressedVLR();
    LocalDate getDateOfUnsuppressedVLR();
    String getUnsuppressedVLRIndication();
    String getCurrentArtStatus();
    LocalDate getDateOfCurrentArtStatus();
    LocalDate getDateOfCommencementOfFirstEAC();
    LocalDate getDateOfFirstEACSessionCompleted();
    String getMethodOfFirstEACSession();
    LocalDate getDateOfCommencementOfSecondEAC();
    LocalDate getDateOfSecondEACSessionCompleted();
    String getMethodOfSecondEACSession();
    LocalDate getDateOfCommencementOfThirdEAC();
    LocalDate getDateOfThirdEACSessionCompleted();
    String getMethodOfThirdEACSession();
    LocalDate getDateOfCommencementOfFourthEAC();
    LocalDate getDateOfFourthEACSessionCompleted();
    String getMethodOfFourthEACSession();

    Integer getNumberOfEACSessionsCompleted();
    LocalDate getDateOfRepeatViralLoadPostEACSampleCollected();
    Double getRepeatViralLoadResultPostEAC();
    LocalDate getDateOfRepeatViralLoadResultPostEACVL();
    String getReferredToSwitchCommittee();
    String getEligibleForSwitch();
    LocalDate getDateOfRepeatViralLoadPostSwitchEACSampleCollected();
    Double getRepeatViralLoadResultPostSwitchEAC();
    LocalDate getDateOfRepeatViralLoadResultPostSwitchEACVL();
    String getMethodOfPostSwitchEACSession();
    String getReferredToSwitchCommitteePostSwitchEAC();
    LocalDate getDateReferredToSwitchCommittedPostSwitchEAC();
    String getEligibleForSwitchPostSwitchEAC();
    LocalDate getDateSwitched();
    LocalDate getStartDateOfSwitchedRegimen();
    String getSwitchedARTRegimenLine();
    String getSwitchedARTRegimen();
    String getCaseManager();
}
