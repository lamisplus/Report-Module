package org.lamisplus.modules.report.domain;


public interface ClientServiceDto {
    String getFacilityState();
    String getFacilityName();
    String getSerialEnrollmentNo();
    String getPersonUuid();
    String getDateOfObservation();
    String getAnyOfTheFollowingList();
    String getDateOfAttempt();
    String getVerificationAttempts();
    String getVerificationStatus();
    String getDsdModel();
    String getComment();
    String getReturnedToCare();
    String getReferredTo();
    String getDiscontinuation();
    String getDateOfDiscontinuation();
    String getReasonForDiscontinuation();

}
