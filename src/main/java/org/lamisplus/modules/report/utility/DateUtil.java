package org.lamisplus.modules.report.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class DateUtil {

    public LocalDate datePadding(LocalDate date){
        LocalDate currentDate = LocalDate.now();
        LocalDate suppliedDate = date;
        LOG.info("current date is {}", currentDate);
        LOG.info("supplied date is {}", date);
        //Add one day if current day to carter for patient registered today
        if(currentDate.isEqual(suppliedDate)) date=date.plusDays(1);
        return date;
    }
}
