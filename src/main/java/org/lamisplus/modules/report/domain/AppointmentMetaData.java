package org.lamisplus.modules.report.domain;

import lombok.Builder;
import lombok.Data;
import org.lamisplus.modules.patient.domain.entity.Person;

import java.time.LocalDate;

@Data
@Builder
public class AppointmentMetaData {
    private LocalDate visitDate;
    private LocalDate nextAppointment;
    private Long facilityId;
   private Person person;
}
