package org.lamisplus.modules.report.domain;

public interface ClientServiceDto {
    Long getPatientId();

    String getComment();

    String getOutcome();

    String getDateOfAttempt();

    String getVerificationStatus();

    String getVerificationAttempts();

    String getSerialEnrollmentNo();

    String getReferredTo();

    String getDiscontinuation();

    String getDateReturnedToCare();

    String getDateOfDiscontinuation();

}
