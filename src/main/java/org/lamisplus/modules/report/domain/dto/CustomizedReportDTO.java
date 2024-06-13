package org.lamisplus.modules.report.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CustomizedReportDTO {
    public UUID id;
    public String reportName;
    public String query;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Boolean disabled;
}
