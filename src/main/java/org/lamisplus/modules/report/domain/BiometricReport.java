package org.lamisplus.modules.report.domain;

import java.time.LocalDate;

public interface BiometricReport {
    Integer getAge();

    Long getId();

    Long getFinger();

    LocalDate getDob();

    LocalDate getEnrollment();

    String getName();

    String getSex();

    String getHospitalNumber();

    String getAddress();

    String getPhone();
    String getBiometricStatus();
}