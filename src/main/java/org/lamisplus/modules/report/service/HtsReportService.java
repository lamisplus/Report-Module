package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.domain.HtsReportDto;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HtsReportService {
    private final ReportRepository reportRepository;

    public List<HtsReportDto> getHtsReport(Long facilityId, LocalDate start, LocalDate end){
        //System.out.println("start - " + start + " end - " + end);
        //System.out.println("facility id - " + facilityId);
        return reportRepository.getHtsReport();
        //System.out.println("hts report size - " + hts.size());
        //return hts;
    }
}
