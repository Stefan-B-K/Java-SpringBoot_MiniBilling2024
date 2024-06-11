package com.istef.minibilling2024.service.billingService;

import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
class TimeSpanCalculatorDays implements TimeSpanCalculator {


    @Override
    public long measurementTimeSpan(ZonedDateTime dateStart, ZonedDateTime dateEnd) {
        return dateStart.toLocalDate().until(dateEnd.toLocalDate(), ChronoUnit.DAYS) + 1;
    }

    @Override
    public long intermediateTimeSpan(ZonedDateTime dateStart, ZonedDateTime dateEnd) {
        return dateStart.until(dateEnd, ChronoUnit.DAYS) + 1;
    }
}
