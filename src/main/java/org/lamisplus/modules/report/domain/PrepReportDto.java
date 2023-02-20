package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface PrepReportDto {
    public String getDatimId();
    public String getFacilityName();
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
    public String getCurrentRegimen();
    public String getDateOfLastPickup();
    public String getCurrentSystolicBp();
    public String getCurrentDiastolicBp();
    public String getCurrentWeight();
    public String getCurrentHeight();
    public String getCurrentHIVStatus();
    public String getPregnancyStatus();
}
