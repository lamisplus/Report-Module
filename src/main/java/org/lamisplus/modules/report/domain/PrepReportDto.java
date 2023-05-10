package org.lamisplus.modules.report.domain;

import java.time.LocalDate;


public interface PrepReportDto {
    public String getDatimId();
    public String getFacilityName();
    public String getState();
    public String getLga();
    public String getUniqueId();
    public String getPersonUuid();
    public String getHospitalNumber();
    public String getFirstName();
    public String getSurname();
    public String getOtherName();
    public String getSex();
    public Integer getAge();
    public LocalDate getDateOfBirth();
    public String getPhone();
    public String getAddress();
    public String getMaritalStatus();
    public String getResidentialLga();
    public String getResidentialState();
    public String getEducation();
    public String getOccupation();
    public LocalDate getDateOfRegistration();
    public String getBaseLineRegimen();
    
    public Integer getBaselineSystolicBp();
    public Integer getBaselineDiastolicBp();
    public String getBaselineWeight();
    public String getBaselineHeight();
    public String getHIVStatusAtPrepInitiation();
    public String getIndicationForPrep();
    /*    public String getRiskType();
		public String getEntryPoint();
		public String getFacilityReferredTo();
		public String getCurrentRegimenStart();
		public String getCurrentPrepStatus();
		public LocalDate getCurrentPrepStatusDate();*/
    public LocalDate getCurrentUrinalysisDate();
    public String getCurrentUrinalysis();
    public LocalDate getBaseLineUrinalysisDate();
    public String getBaseLineUrinalysis();
    
    public String getCurrentRegimen();
    public String getDateOfLastPickup();
    public String getCurrentSystolicBp();
    public String getCurrentDiastolicBp();
    public String getCurrentWeight();
    public String getCurrentHeight();
    public String getCurrentHIVStatus();
    public String getPregnancyStatus();
    
    public LocalDate getPrepCommencementDate();
    public String getCurrentStatus();
    
    public LocalDate getDateOfCurrentStatus();
    public LocalDate getDateOfCurrentHIVStatus();
    
    public String getBaseLineHepatitisB();
    public String getBaseLineHepatitisC();
    public String getInterruptionReason();
    public LocalDate getInterruptionDate();
    public String getBaseLineCreatinine();
    public LocalDate getHivEnrollmentDate();
    String getTargetGroup();
    
}

