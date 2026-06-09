package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface FamilyIndexReportDtoProjection {

        String getState();
        String getLga();
        String getFacilityName();
        String getDatimId();
        String getSurname();
        String getFirstName();
        String getHospitalNumber();
        String getClientCode();
        LocalDate getDateOfService();

//        String getPatientUuid();

        String getEntryPoint();
        String getClientCategory();
        String getOfferedPns();
        String getDateOfElicitation();
        String getAcceptedPns();
        String getContactCode();
        String getNameOfIndexClient();
        String getRelationshipToIndex();
        String getSex();
        Integer getAge();
        String getPhone();
        String getAddress();
        String getNotificationMethod();
        Integer getAttempts();
        String getKnownHivPositive();
        LocalDate getDateTestedHiv();
        String getHivTestResult();
        String getOnArt();
        LocalDate getDateEnrolledArt();
        String getUan();
        LocalDate getDateEnrolledOvc();
        String getOvcId();
}
