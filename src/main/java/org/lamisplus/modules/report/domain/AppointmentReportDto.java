package org.lamisplus.modules.report.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReportDto {
    private Long facilityId;
    private String facilityName;
    private String state;
    private String lga;
    private String patientId;
    private String hospitalNum;
    private String uniqueID;
    private String name;
    private LocalDate dateBirth;
    private Integer age;
    private String sex;
    private String stateOfResidence;
    private String lgaOfResidence;
    private String address;
    private String phone;
    private String currentStatus;
    private LocalDate artStartDate;
    private LocalDate dateOfLastVisit;
    private LocalDate dateOfNextVisit;
    private String caseManager;

}
