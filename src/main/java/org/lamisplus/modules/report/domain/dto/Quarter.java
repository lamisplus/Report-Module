package org.lamisplus.modules.report.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Quarter {
    private LocalDate start;
    private LocalDate end;
    private String   name;
}
