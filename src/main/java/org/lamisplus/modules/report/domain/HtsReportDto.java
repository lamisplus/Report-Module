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
      String getIndexClient();

      String getPreviouslyTested();
      String getTargetGroup();
      String getReferredFrom();
      String getTestingSetting();
      String getCounselingType();
      String getPregnancyStatus();
      String getEntryPoint();
      String getBreastFeeding();
      String getIndexType();
      String getIfRecencyTestingOptIn();
      String getRecencyId();
      String getRecencyTestType();
      LocalDate getRecencyTestDate();
      String getRecencyInterpretation();
      String getFinalRecencyResult();

      LocalDate getViralLoadSampleCollectionDate();
      String getViralLoadConfirmationResult();
      LocalDate getViralLoadConfirmationDate();
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
      //String getNumberOfLubricantsGiven();
       LocalDate getViralLoadReceivedResultDate();
       String getSource();
       String getPatientUuid();
       String getTotalRiskScore();
       String getTesterName();
       String getRefferedForSti();
       Integer getHtsCount();

       LocalDate getPreviousVisitDate();

       String getPreviousTestResult();

       String getGonModalities();
       String getPepfarModalities();
       String getMlScore();
       String getMlStatus();
       LocalDate getSyphilisTestDate();
    

}
