package com.istef.minibilling2024.service.billingService;

import java.time.ZonedDateTime;

 interface TimeSpanCalculator {
    long measurementTimeSpan(ZonedDateTime dateStart, ZonedDateTime dateEnd);
    long intermediateTimeSpan(ZonedDateTime dateStart, ZonedDateTime dateEnd);
}
