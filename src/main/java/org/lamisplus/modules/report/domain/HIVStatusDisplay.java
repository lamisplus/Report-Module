package org.lamisplus.modules.report.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class HIVStatusDisplay {
	private LocalDate date;
	private String description;
	private LocalDate quarterEndDate;
}
