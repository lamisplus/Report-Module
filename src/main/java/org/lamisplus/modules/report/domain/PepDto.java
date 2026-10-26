package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface PepDto {

        String getState();

        String getLga();

        String getFacilityName();

        String getDatimId();

        String getPersonUuid();

        String getGender();

        Integer getAge();

        String getEnrollmentType();

        String getModeOfExposure();

        String getHivStatusAtExposure();

        String getDurationBeforePep();

        LocalDate getDateEnrolled();

        String getPopulationType();

        String getPregnancyStatus();

        String getFinalHivTestResult();

        String getStiScreening();

        String getPrepRegimen();

        String getAdherenceLevel();

        String getNotedSideEffects();

        String getRiskReduction();

        LocalDate getDatePrepStarted();

        LocalDate getDateStopPep();

        String getPepCompletion();

        LocalDate getFirstVisitDate();

        String getFirstFinalHivTestResult();

        LocalDate getSecondVisitDate();

        String getSecondHivTestResult();

        LocalDate getThirdVisitDate();

        String getThirdHivTestResult();

        LocalDate getNextAppointment();

        LocalDate getInterruptionDate();

        String getEarlyDetectVLResult();

        String getInterruptionReason();

        String getLink2Art();
}
