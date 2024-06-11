package com.istef.minibilling2024.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.istef.minibilling2024.BillingApp;
import com.istef.minibilling2024.exception.JasonParseException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class DateToZuluStartOfDayDeserializer extends StdDeserializer<ZonedDateTime> {

    public DateToZuluStartOfDayDeserializer() {
        this(null);
    }

    public DateToZuluStartOfDayDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ZonedDateTime deserialize(
            JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String date = jsonparser.getText();
        try {
            return parseDateToStartOfDayUTC(date, BillingApp.PRICELIST_ZONE_ID);
        } catch (JasonParseException e) {
            throw new IOException(e);
        }
    }

    private static ZonedDateTime parseDateToStartOfDayUTC(String date, ZoneId zoneId)
            throws JasonParseException {
        try {
            return LocalDate.parse(date)
                    .atStartOfDay(zoneId)
                    .withZoneSameInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new JasonParseException(e.getMessage());
        }
    }
}