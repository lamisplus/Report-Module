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
    String getOutcome();
    String getDsdModel();
    String getComment();
    String getReturnedToCare();
    String getReferredTo();
    String getDiscontinuation();
    String getDateOfDiscontinuation();
    String getReasonForDiscontinuation();
    String getNoAttempts();
    String getHospitalNumber();

    String getNoInitBiometric();
    String getNoRecapture();
    String getDuplicatedDemographic();
    String getLastVisitIsOver18M();
    String getIncompleteVisitData();
    String getRepeatEncounterNoPrint();
    String getLongIntervalsARVPickup();
    String getSameSexDOBARTStartDate();
    String getPickupByProxy();
    String getOtherSpecifyForCV();

}
