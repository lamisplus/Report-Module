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
	
	
	
	
	@NotNull
	public List<RADETDTOProjection> getRadetDtos(Long facilityId, LocalDate start, LocalDate end) {
		System.out.println("facilityId:" + facilityId +" "  +start + " " + end);
		return repository.getRadetData(facilityId,start,end);
	}
	
	
}
