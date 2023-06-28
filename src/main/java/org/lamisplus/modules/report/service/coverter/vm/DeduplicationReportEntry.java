package org.lamisplus.modules.report.service.coverter.vm;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeduplicationReportEntry {
    private String enrolledPatientId;
    private String duplicatePatientId;
    private String enrolledPatientHospitalNumber;
    private String duplicatePatientHospitalNumber;
    private String enrolledPatientUniqueId;
    private String duplicatePatientUniqueId;
    private String enrolledPatientSurname;
    private String duplicatePatientSurname;
    private String enrolledPatientFirstName;
    private String duplicatePatientFirstName;
    private String enrolledPatientSex;
    private String duplicatePatientSex;
    private String enrolledPatientDateOfBirth;
    private String duplicatePatientDateOfBirth;
    private String enrolledPatientFingerType;
    private String duplicatePatientFingerType;
    private Integer matchingScore;
}
