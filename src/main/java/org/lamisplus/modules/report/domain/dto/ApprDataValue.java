package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApprDataValue {

    private String dataElement;
    private String period;                // e.g. "2025W19"
    private String orgUnit;
    private String categoryOptionCombo;
    private Long   value;                 // keep as String to align with DHIS2 payloads
    private String attributeOptionCombo;  // optional but present
}
