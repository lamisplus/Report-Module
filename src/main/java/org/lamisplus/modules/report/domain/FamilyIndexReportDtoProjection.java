package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface FamilyIndexReportDtoProjection {

    String getFacilityName();
    String getState();
    String getLga();
    String getPersonUuid();
    String getHospitalNumber();
    Integer getAge();
    String getDatimId();
    LocalDate getDateOfBirth();
    String getSex();
    String getPatientName();
    String getOtherName();
    String getMaritalStatus();
    LocalDate getDateConfirmedHiv();
    LocalDate getDateOfferIndex();
    String getDateEnrolled();
    String getRecencyTesting();
    String getAcceptedTesting();

    String getEntryPoint();
    Integer getElicitedAge();
    //String getElicitedClientName();
    String getElicitedClientSex();
    String getElicitedClientAddress();
    String getElicitedClientPhoneNumber();
    String getElicitedClientTestedHiv();
    String getElicitedClientHivResult();
    LocalDate getElicitedClientDateEnrolled();
    String getRelationshipWithIndex();
    String getModeOfNotification();
    String getElicitedClientKnownPositive();
    LocalDate getDateOfElicitation();
    String getElicitedClientUniqueId();
    String getDateEnrolledInOvc();
    String getOvcId();
    String getNoOfAttempts();
}
