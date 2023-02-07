package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@Service
public class HtsReportService {
    private final ReportRepository reportRepository;

    public List<HtsReportDto> getHtsReport(Long facilityId, LocalDate start, LocalDate end){
        return reportRepository.getHtsReport(0, facilityId, start, end);
    }
}