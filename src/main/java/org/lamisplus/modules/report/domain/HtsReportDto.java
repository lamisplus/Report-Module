package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public  interface HtsReportDto {
      String getDatimCode();
      String getFacility();
      String getState();
      String getLga();
      String getPatientId();
      String getClientCode();
      String getFirstName();
      String getSurname();
      String getOtherName();
      String getSex();
      Integer getAge();
      LocalDate getDateOfBirth();
      String getPhoneNumber();
      String getClientAddress();
      String getMaritalStatus();
      String getLgaOfResidence();
      String getStateOfResidence();
      String getEducation();
      String getOccupation();
      LocalDate getDateVisit();
      String getFirstTimeVisit();
      Integer getNumberOfChildren();
      Integer getNumberOfWives();
      String getIndexClient();

      String getPreviouslyTested();
      String getTargetGroup();
      String getReferredFrom();
      String getTestingSetting();
      String getCounselingType();
      String getPregnancyStatus();
      String getBreastFeeding();
      String getIndexType();
      String getIfRecencyTestingOptIn();
      String getRecencyId();
      String getRecencyTestType();
      LocalDate getRecencyTestDate();
      String getRecencyInterpretation();
      String getFinalRecencyResult();

    //added started
      LocalDate getViralLoadSampleCollectionDate();
      String getViralLoadConfirmationResult();
    //End
      String getViralLoadResult();
      LocalDate getViralLoadConfirmationDate();
      String getAssessmentCode();
      String getModality();
      String getSyphilisTestResult();
      String getHepatitisBTestResult();
      String getHepatitisCTestResult();
      String getCd4Type();
      String getCd4TestResult();
      String getHivTestResult();
      String getFinalHivTestResult();
      LocalDate getDateOfHivTesting();
      String getPrepOffered();
      String getPrepAccepted();
      String getHtsLatitude();
      String getHtsLongitude();
      String getNumberOfCondomsGiven();
      String getNumberOfLubricantsGiven();
}
