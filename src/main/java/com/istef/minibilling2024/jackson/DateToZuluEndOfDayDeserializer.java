package com.istef.minibilling2024.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.istef.minibilling2024.BillingApp;
import com.istef.minibilling2024.exception.JasonParseException;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeParseException;

public class DateToZuluEndOfDayDeserializer extends StdDeserializer<ZonedDateTime> {

    public DateToZuluEndOfDayDeserializer() {
        this(null);
    }

    public DateToZuluEndOfDayDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ZonedDateTime deserialize(
            JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String date = jsonparser.getText();
        try {
            return parseDateToEndOfDayUTC(date, BillingApp.PRICELIST_ZONE_ID);
        } catch (JasonParseException e) {
            throw new IOException(e);
        }
    }

    private static ZonedDateTime parseDateToEndOfDayUTC(String date, ZoneId zoneId)
            throws JasonParseException {
        try {
            return LocalDate.parse(date)
                    .atTime(LocalTime.parse("23:59:59"))
                    .atZone(zoneId)
                    .withZoneSameInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new JasonParseException(e.getMessage());
        }

    }
}