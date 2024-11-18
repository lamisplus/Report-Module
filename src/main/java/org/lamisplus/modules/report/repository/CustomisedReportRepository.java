package org.lamisplus.modules.report.repository;

import org.lamisplus.modules.report.domain.entity.CustomizedReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomisedReportRepository extends JpaRepository<CustomizedReport, UUID> {
}
