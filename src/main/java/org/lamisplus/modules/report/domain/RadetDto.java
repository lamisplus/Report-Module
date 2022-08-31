package org.lamisplus.modules.report.domain;

import lombok.Data;

import java.util.Date;

@Data
public class RadetDto {
    private String state;
    private String lga;
    private String facilityName;
    private String patientId;
    private String hospitalNum;
    private String uniqueID;
    private Date dateBirth;
    private Integer age;
    private String sex;
}
