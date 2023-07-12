package org.lamisplus.modules.report.domain;

import java.time.LocalDate;


public interface PrepReportDto {
    String getDatimId();
    String getFacilityName();
    String getState();
    String getLga();
    String getUniqueId();
    String getPersonUuid();
    String getHospitalNumber();
    String getFirstName();
    String getSurname();
    String getOtherName();
    String getSex();
    Integer getAge();
    LocalDate getDateOfBirth();
    String getPhone();
    String getAddress();
    String getMaritalStatus();
    String getResidentialLga();
    String getResidentialState();
    String getEducation();
    String getOccupation();
    LocalDate getDateOfRegistration();
    String getBaseLineRegimen();

    Integer getBaselineSystolicBp();
    Integer getBaselineDiastolicBp();
    String getBaselineWeight();
    String getBaselineHeight();
    String getHIVStatusAtPrepInitiation();
    String getIndicationForPrep();
    /*      String getRiskType();
      String getEntryPoint();
      String getFacilityReferredTo();
      String getCurrentRegimenStart();
      String getCurrentPrepStatus();
      LocalDate getCurrentPrepStatusDate();*/
    LocalDate getCurrentUrinalysisDate();
    String getCurrentUrinalysis();
    LocalDate getBaseLineUrinalysisDate();
    String getBaseLineUrinalysis();

    String getCurrentRegimen();
    String getDateOfLastPickup();
    String getCurrentSystolicBp();
    String getCurrentDiastolicBp();
    String getCurrentWeight();
    String getCurrentHeight();
    String getCurrentHIVStatus();
    String getPregnancyStatus();

    LocalDate getPrepCommencementDate();
    String getCurrentStatus();

    LocalDate getDateOfCurrentStatus();
    LocalDate getDateOfCurrentHIVStatus();

    String getBaseLineHepatitisB();
    String getBaseLineHepatitisC();
    String getInterruptionReason();
    LocalDate getInterruptionDate();
    String getBaseLineCreatinine();
    LocalDate getHivEnrollmentDate();
    String getTargetGroup();
    
}

