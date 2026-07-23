package org.lamisplus.modules.report.domain.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSectionDTO {

    private String title;

    private String sectionType;

    private List<ReportRowDTO> rows;
}