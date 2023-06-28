package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientData {
    public String state;
    public String lga;
    public String facility;
    public String patientId;
    public String uniqueId;
    public String maritalStatus;
    public LocalDate dateOfBirth;
    public Integer age;
    public LocalDate artStartDate;

}
