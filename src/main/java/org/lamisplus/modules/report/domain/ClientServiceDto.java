package org.lamisplus.modules.report.domain;
public interface ClientServiceDto {
    String getFacilityState();
    String getFacilityName();
    String getSerialEnrollmentNo();
    Long getPatientId();
    String getDateOfObservation();
    String getAnyOfTheFollowing();
    String getDateOfAttempt();
    String getVerificationAttempts();
    String getVerificationStatus();
    String getDsdModel();
    String getOutcome();
    String getComment();
    String getReturnedToCare();
    String getReferredTo();
    String getDiscontinuation();
    String getDateOfDiscontinuation();
    String getReasonForDiscontinuation();

}
