package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.report.domain.RADETDTOProjection;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class RadetService {
	
	private final ReportRepository repository;
	private final QuarterService quarterService;
	
	
	
	
	@NotNull
	public List<RADETDTOProjection> getRadetDtos(Long facilityId, LocalDate start, LocalDate end) {
		LocalDate previousQuarterEnd = quarterService.getPreviousQuarter(end).getEnd();
		LocalDate previousPreviousQuarterEnd = quarterService.getPreviousQuarter(previousQuarterEnd).getEnd();
		System.out.println("facilityId: AMOS vl1 " + facilityId +" "  +start + " " + end.plusDays(1));
		return repository.getRadetData(facilityId, start, end.plusDays(1), previousQuarterEnd, previousPreviousQuarterEnd);
	}
	
	
}
