package org.lamisplus.modules.report.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HtsReportDto {

    private String datimCode;
    private String facility;
    private String clientCode;
    private String firstName;
    private String surname;
    private String otherName;
    private String sex;
    private Integer age;
    private LocalDate dateOfBirth;
    private String phoneNumber;
    private String clientAddress;
    private String maritalStatus;
    private String lgaOfResidence;
    private String stateOfResidence;

    private String education;
    private String occupation;

    private LocalDate dateVisit;
    private String firstTimeVisit;
    private Integer numberOfChildren;
    private Integer numberOfWives;
    private String indexClient;

    private String previouslyTested;
    private String targetGroup;
    private String referredFrom;
    private String testingSetting;

    private String counselingType;
    private String pregnancyStatus;
    private String breastFeeding;
    private String indexType;
    private String ifRecencyTestingOptIn;
    private String recencyId;
    private String recencyTestType;
    private String recencyTestDate;
    private String recencyInterpretation;
    private String finalRecencyResult;
    private String viralLoadResultClassification;
    private String viralLoadConfirmationDate;
    private String assessmentCode;
    private String modality;
    private String syphilisTestResult;
    private String hepatitisBTestResult;
    private String hepatitisCTestResult;
    private String cd4Type;
    private String cd4TestResult;
    private String hivTestResult;
    private String finalHivTestResult;
    private LocalDate dateOfHivTesting;
    private String prepOffered;
    private String prepAccepted;
    private String htsLatitude;
    private String htsLongitude;
}
