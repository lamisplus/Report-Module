package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface TBReportProjection {
    String getPersonUuid();
    String getLga();
    String getState();
    String getHospitalNumber();
    LocalDate getArtStartDate();
//    String getSurname();
//    String getFirstName();
    String getUniqueId();
    int getAge();
    String getGender();
    LocalDate getDateOfBirth();
    String getFacilityName();
    String getDatimId();
    String getTbIptScreening();
    String getTargetGroup();
    String getEnrollmentSetting();
    LocalDate getDateOfIptStart();
    String getRegimenAtStart();
    LocalDate getDateOfRegistration();
    LocalDate getDateOfTbScreened();
    String getEligibleForTpt();
    String getTbStatus();
    String getTbScreeningType();
    LocalDate getTbTreatmentStartDate();
    String getTbTreatmentType();
    LocalDate getTbTreatmentCompletionDate();
    String getTbTreatmentOutcome();
    String getTbDiagnosticResult();
    LocalDate getDateOfTbDiagnosticResultReceived();
    String getTbDiagnosticTestType();

//    LocalDate getIptCompletionDate();

    String getIptCompletionStatus();
    String getEligibleForTPT();
    String getRegimenName();
    LocalDate getIptCompletionDate();
    String getWeightAtStartTpt();
}
