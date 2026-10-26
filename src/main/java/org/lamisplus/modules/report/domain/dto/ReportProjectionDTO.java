package org.lamisplus.modules.report.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportProjectionDTO {

    private String reportTitle;

    private String reportingPeriod;

    private String state;

    private String lga;

    private String facilityName;

    private List<ReportSectionDTO> sections;
}