package com.istef.minibilling2024.service.billingService;

import com.istef.minibilling2024.entity.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


class LinesGenerator {

    private final TimeSpanCalculator timeSpanCalculator;


    public LinesGenerator() {
        this.timeSpanCalculator = new TimeSpanCalculatorDays();
    }


    List<Line> generateLines(int index, Reading oldReading, List<Reading> newReadings,
                             List<Price> priceList, Product product, Invoice invoice) {

        Reading prevReading = oldReading;
        List<Line> newLines = new ArrayList<>();

        for (Reading reading : newReadings) {

            BigDecimal measurement = BigDecimal.valueOf(reading.getValue() - prevReading.getValue());

            ZonedDateTime datePrevReading = prevReading.getDate();
            ZonedDateTime dateReading = reading.getDate();
            long timeSpan = timeSpanCalculator.measurementTimeSpan(datePrevReading, dateReading);

            BigDecimal totalDistributed = BigDecimal.ZERO;
            for (Price price : priceList) {
                ZonedDateTime datePriceFrom = price.getValidFromDateUTC();
                ZonedDateTime datePriceTo = price.getValidToDateUTC();

                // the measurement is within the validity of one price
                if (!datePriceFrom.isAfter(datePrevReading) && !datePriceTo.isBefore(dateReading)) {
                    index = addToLastLine(index, newLines, measurement,
                            datePrevReading, dateReading, price,
                            product, invoice);
                    break;
                }

                // the price overlaps with (or is entirely within) the measurement
                if (!datePriceTo.isBefore(datePrevReading) && datePriceTo.isBefore(dateReading)) {
                    BigDecimal quantity;

                    if (datePriceFrom.isBefore(datePrevReading)) {  // overlaps
                        long timeSpanQPP = timeSpanCalculator.intermediateTimeSpan(datePrevReading, datePriceTo);
                        quantity = BigDecimal
                                .valueOf((double) timeSpanQPP / timeSpan).setScale(2, RoundingMode.UP)
                                .multiply(measurement)
                                .setScale(2, RoundingMode.UP);
                        index = addToLastLine(index, newLines, quantity,
                                datePrevReading, datePriceTo, price,
                                product, invoice);
                    } else {    // is entirely within
                        long timeSpanQPP = timeSpanCalculator.intermediateTimeSpan(datePriceFrom, datePriceTo);
                        quantity = BigDecimal
                                .valueOf((double) timeSpanQPP / timeSpan).setScale(2, RoundingMode.UP)
                                .multiply(measurement)
                                .setScale(2, RoundingMode.UP);
                        newLines.add(new Line(index++,
                                quantity,
                                datePriceFrom,
                                datePriceTo,
                                price.getValue(),
                                product,
                                price.getPriceListId(),
                                invoice));
                    }
                    totalDistributed = totalDistributed.add(quantity);
                    continue;
                }

                // the last price to overlap with the measurement
                if (datePriceFrom.isBefore(dateReading) && !datePriceTo.isBefore(dateReading)) {

                    newLines.add(new Line(index++,
                            measurement.subtract(totalDistributed).setScale(2, RoundingMode.HALF_UP),
                            price.getValidFromDateUTC(),
                            dateReading,
                            price.getValue(),
                            product,
                            price.getPriceListId(),
                            invoice));
                    break;
                }
            }
            prevReading = reading;
        }

        return newLines;
    }

    private int addToLastLine(int index, List<Line> lines, BigDecimal measurement,
                              ZonedDateTime dateFirstReading, ZonedDateTime dateSecondReading,
                              Price price, Product product, Invoice invoice) {
        if (lines.isEmpty()) {
            lines.add(new Line(index++,
                    measurement,
                    dateFirstReading,
                    dateSecondReading,
                    price.getValue(),
                    product,
                    price.getPriceListId(),
                    invoice));
        } else {
            Line lastLine = lines.get(lines.size() - 1);
            lastLine.setQuantity(lastLine.getQuantity()
                    .add(measurement).setScale(2, RoundingMode.HALF_UP));
            lastLine.setEndDateUtc(dateSecondReading);
        }
        return index;
    }
}
