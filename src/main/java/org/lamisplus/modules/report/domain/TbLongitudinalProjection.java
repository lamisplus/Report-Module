package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface TbLongitudinalProjection {

    String getFacilityName();
    String getLga();
    String getState();
    String getPersonUuid();
    String getHospitalNumber();
    String getUniqueId();
    String getGender();
    LocalDate getDateOfBirth();
    Integer getAge();
    LocalDate getDateStarted();
    LocalDate getDateOfObservation();
    String getTbScreeningType();
    Integer getCadScore();
    String getTbStatus();
    String getSpecimenType();
    LocalDate getDateSpecimenSent();
    String getDiagnosticTestDone();
    String getClinicallyEvaulated();
    LocalDate getDateOfEvaluation();
    String getResultOfClinicalEvaluation();
    String getTbType();
    LocalDate getDateOfChestXrayResultTestDone();
    String getChestXrayResult();
    LocalDate getTbTreatmentStartDate();
    String getTreatmentOutcome();
    LocalDate getTbCompletionDate();
    String getEligibleForTPT();
    String getContractionForTpt();
    String getContractionOptions();
    LocalDate getDateOfIptStart();
    String getRegimenName();
    LocalDate getIptCompletionDate();
    String getIptCompletionStatus();
    String getWeightAt();
    String getTbDiagnosticTestType();
    LocalDate getDateofDiagnosticTestSampleCollected();
    String getTbDiagnosticResult();
    LocalDate getDateofTbDiagnosticResultReceived();
    LocalDate getDateDiagnosticEvaluation();
    LocalDate getDateTbScoreCad();
    LocalDate getResultTbScoreCad();
}
