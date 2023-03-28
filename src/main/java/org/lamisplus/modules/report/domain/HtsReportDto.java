package org.lamisplus.modules.report.domain;

import lombok.Data;

import java.time.LocalDate;

public interface HtsReportDto {
    public String getDatimCode();
    public String getFacility();
    public String getClientCode();
    public String getFirstName();
    public String getSurname();
    public String getOtherName();
    public String getSex();
    public Integer getAge();
    public LocalDate getDateOfBirth();
    public String getPhoneNumber();
    public String getClientAddress();
    public String getMaritalStatus();
    public String getLgaOfResidence();
    public String getStateOfResidence();
    public String getEducation();
    public String getOccupation();
    public LocalDate getDateVisit();
    public String getFirstTimeVisit();
    public Integer getNumberOfChildren();
    public Integer getNumberOfWives();
    public String getIndexClient();

    public String getPreviouslyTested();
    public String getTargetGroup();
    public String getReferredFrom();
    public String getTestingSetting();
    public String getCounselingType();
    public String getPregnancyStatus();
    public String getBreastFeeding();
    public String getIndexType();
    public String getIfRecencyTestingOptIn();
    public String getRecencyId();
    public String getRecencyTestType();
    public String getRecencyTestDate();
    public String getRecencyInterpretation();
    public String getFinalRecencyResult();
    public String getViralLoadResultClassification();
    public String getViralLoadConfirmationDate();
    public String getAssessmentCode();
    public String getModality();
    public String getSyphilisTestResult();
    public String getHepatitisBTestResult();
    public String getHepatitisCTestResult();
    public String getCd4Type();
    public String getCd4TestResult();
    public String getHivTestResult();
    public String getFinalHivTestResult();
    public LocalDate getDateOfHivTesting();
    public String getPrepOffered();
    public String getPrepAccepted();
    public String getHtsLatitude();
    public String getHtsLongitude();

    public String getNumberOfCondomsGiven();
    public String getNumberOfLubricantsGiven();
}
