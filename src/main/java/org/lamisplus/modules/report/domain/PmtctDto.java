package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface PmtctDto {
        String getState();
        String getLga();
        String getFacilityName();
        String getPersonUuid();
        String getHospitalNumber();
        Integer getAge();
        String getMaritalStatus();
        String getSetting();
        String getFacilitySetting();

        LocalDate getDate();
        Integer getGravida();
        Integer getParity();

        String getPreviouslyKnownHivPositive();
        String getTypeOfHivTest();
        String getHivEarlyDetect();
        String getHivEarlyDetectViralLoad();
        LocalDate getDateOfVisit();
        String getFinalHivTestResult();
        String getKnownPositive();
        String getHbTestResult();
        String getHbTreatment();
        LocalDate getDateTestedForHepatitisC();
        String getHepatitisC();
        String getSyphilisTestResult();
        String getSyphilisTreatment();
        LocalDate getDateOfMaternalRestesting();
        String getMaternalRestesting();
        String getTbScreeningStatus();
        String getTbReferred();
        String getPartnerTestedHiv();
        String getPartnerTestedSyphilis();
        String getPartnerTestedHb();
        String getPartnerReferal();

        String getGonModalities();
        String getPepfarModalities();

}
