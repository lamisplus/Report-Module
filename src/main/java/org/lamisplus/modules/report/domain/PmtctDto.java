package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface PmtctDto {

    String getFacilityName();
    String getState();
    String getLga();
    String getPersonUuid();
    String getAncNumber();
    String getHospitalNumber();
    String getMotherDob();
    Integer getMotherAge();
    String getMaritalStatus();
    LocalDate getDateOfRegistration();
    String getHivTestResult();
    String getAncSettingAnc();
    String getPreviouslyKnownHivStatus();
    String getFirstAncDate();
    String getGaweeksAnc();
    String getGravidaAnc();
    String getParityAnc();
    String getTestedSyphilisAnc();
    String getTestResultSyphilisAnc();
    String getSyphilisTreatmentStatus();
    String getSyphillisStatus();
    String getAcceptHivTest();
    String getReferredTo();
    String getHivRestested();
    String getAcceptedHIVTesting();
    String getDateTestedHivPositive();
    String getReceivedHivRetestedResult();
    String getPreviouslyKnownHIVPositive();
    String getAncNo();

    String getHepatitisBTestResult();
    String getHepatitisCTestResult();
    String getOptOutRTRI();
    String getOptOutRTRIStatus();
    String getRencencyId();
    String getSampleType();
    String getRencencyTestDate();
    String getRencencyInterpretation();
    String getFinalRecencyResult();
    String getDateOfVisit();
    String getTestingSetting();
    String getEntryPoint();
    String getHivEnrollmentDate();
    String getDateOfRegistrationOnHiv();
    String getDateConfirmHiv();
    String getDateStarted();
    String getPmtctEnrollmentDate();
    String getDateOfViralLoad();
    String getResultReported();
    String getDateResultReported();
    String getGonModalities();
    String getPepfarModalities();
    String getRetestingVisitDate();
    String getRetestResult();
    String getHivUniqueId();


}
