package org.lamisplus.modules.report.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportRowDTO {

    private String rowLabel;

    private String testResult;

    private Map<String, BigDecimal> values;
}
