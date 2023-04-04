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
		System.out.println("facilityId: AMOS vl1 " + facilityId +" "  +start + " " + end.plusDays(1));
		System.out.println("previous : "+previousQuarterEnd);
		System.out.println("previousPreviousQuarterEnd : "+previousPreviousQuarterEnd);
		List<RADETDTOProjection> radetData =
				repository.getRadetData(facilityId, start, end.plusDays(1), previousQuarterEnd, previousPreviousQuarterEnd);
		long txCurrent =
				radetData.stream()
						.filter(radetRow -> radetRow.getCurrentStatus().contains(ACTIVE))
						.count();
		
		System.out.println("Total TX_CURR = " + txCurrent);
		Map<String, Map<String, Map<String, Map<String, Long>>>> txCurr =
				radetData.stream()
				.filter(radetRow -> radetRow.getCurrentStatus().contains(ACTIVE))
				.collect(Collectors.groupingBy(
						RADETDTOProjection::getFacilityName,
						Collectors.groupingBy(
								RADETDTOProjection::getGender,
								Collectors.groupingBy(
										radetdtoProjection -> getAgeRange(radetdtoProjection.getAge()),
										Collectors.groupingBy(
												RADETDTOProjection::getCurrentStatus,
												Collectors.counting()
										)
								)
						)
				));
		System.out.println("TX_curr = " + txCurr);
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
