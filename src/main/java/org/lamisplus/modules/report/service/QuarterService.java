package org.lamisplus.modules.report.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.lamisplus.modules.report.domain.dto.Quarter;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuarterService {
    public Quarter getPreviousQuarter(LocalDate endDate) {
        int value = endDate.getMonth().getValue();
        if (value >= 10) {
            //q1
            return getQuarter(7, endDate.getYear(), "Q4");
        }
        if (value <= 3) {
            //q2
            return getQuarter(10, endDate.getYear() - 1, "Q1");
        }
        if (value <= 6) {
            //q3
            return getQuarter(1, endDate.getYear(), "Q2");
        }
        //q4
        return getQuarter(4, endDate.getYear(), "Q3");

    }


    public Quarter getCurrentQuarter(LocalDate endDate) {
        int value = endDate.getMonth().getValue();
        if (value >= 10) {
            //q1
            return getQuarter(10, endDate.getYear(), "Q1");
        }
        if (value <= 3) {
            //q2
            return getQuarter(1, endDate.getYear(), "Q2");
        }
        if (value <= 6) {
            //q3
            return getQuarter(4, endDate.getYear(), "Q3");
        }
        //q4
        return getQuarter(7, endDate.getYear(), "Q4");

    }

    private Quarter getQuarter(int startMonth, int year, String quarterName) {
        LocalDate start = LocalDate.of(year, startMonth, 1);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, startMonth + 1);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        LocalDate end = LocalDate.of(year, startMonth + 2, lastDay);
        return new Quarter(start, end, quarterName);

    }
}
