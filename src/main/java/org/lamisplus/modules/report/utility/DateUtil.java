package org.lamisplus.modules.report.utility;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public String ConvertDateToString(LocalDate localDate){
        if(localDate == null)return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    public String ConvertDateTimeToString(LocalDateTime datetime){
        if(datetime == null) return null;
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return datetime.format(timeFormatter);
    }
}
