package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.lamisplus.modules.report.domain.RADETDTOProjection;
import org.lamisplus.modules.report.repository.ReportRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class RadetService {
	
	public static final String ACTIVE = "ACTIVE";
	private final ReportRepository repository;
	private final QuarterService quarterService;
	
	
	
	
	@NotNull
	public List<RADETDTOProjection> getRadetDtos(Long facilityId, LocalDate start, LocalDate end) {
		LocalDate previousQuarterEnd = quarterService.getPreviousQuarter(end).getEnd();
		LocalDate previousPreviousQuarterEnd = quarterService.getPreviousQuarter(previousQuarterEnd).getEnd();
		LocalDate currentQterStartDate = quarterService.getCurrentQuarter(end).getStart();
		LOG.info("facilityId: " + facilityId +" "  +start + "  to " + end.plusDays(1));
		LOG.info("previous : "+previousQuarterEnd);
		LOG.info("previousPreviousQuarterEnd : "+previousPreviousQuarterEnd);
		LOG.info("Fetching RADET records...");
		List<RADETDTOProjection> radetData =
				repository.getRadetData(facilityId, start, end.plusDays(1),
						previousQuarterEnd, previousPreviousQuarterEnd, currentQterStartDate);
		LOG.info("Done fetching RADET records total size : "+radetData.size());
		return radetData;
	}
	private static String getAgeRange(int age) {
		if (age >= 1 && age <= 15) {
			return "1 to 15";
		} else if (age >= 16 && age <= 20) {
			return "16 to 20";
		} else if (age >= 21 && age <= 30) {
			return "21 to 30";
		} else {
			return "Above 30";
		}
	}
	
}
