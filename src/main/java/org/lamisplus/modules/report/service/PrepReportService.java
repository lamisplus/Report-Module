package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.domain.PrepReportDto;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.lamisplus.modules.report.utility.DateUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrepReportService {
    private final ReportRepository reportRepository;
    private final DateUtil dateUtil;
    
    public List<PrepReportDto> getPrepReport(Long facilityId, LocalDate start, LocalDate end){
        return reportRepository.getPrepReport(facilityId, start, dateUtil.datePadding(end));
    }
}
