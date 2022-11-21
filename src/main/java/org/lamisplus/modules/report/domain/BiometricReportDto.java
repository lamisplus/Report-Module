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
public class BiometricReportDto {
	private Long facilityId;
	private String datimId;
	private String facilityName;
	private String state;
	private String lga;
	private String patientId;
	private String hospitalNum;
	private String uniqueID;
	private LocalDate dateBirth;
	private Integer age;
	private String currentStatus;
	private String sex;
	private String address;
	private String name;
	private LocalDate enrollDate;
	private Long fingers;
	private String valid;
}
