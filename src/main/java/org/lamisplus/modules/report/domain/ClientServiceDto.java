package org.lamisplus.modules.report.domain;

public interface ClientServiceDto {
    Long getPatientId();

    String getDateOfObservation();
    String getFacilityName();
    String getFacilityState();

    String getDsdModel();

    String getComment();

    String getOutcome();

    String getDateOfAttempt();

    String getVerificationStatus();

    String getVerificationAttempts();

    String getSerialEnrollmentNo();

    String getReferredTo();

    String getDiscontinuation();

    String getReturnedToCare();
    String getDateOfDiscontinuation();
    String getReasonForDiscontinuation();

    String getAnyOfTheFollowing();

}
