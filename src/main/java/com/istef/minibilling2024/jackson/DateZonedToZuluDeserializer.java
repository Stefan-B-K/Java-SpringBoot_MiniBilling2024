package com.istef.minibilling2024.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.istef.minibilling2024.exception.JasonParseException;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

public class DateZonedToZuluDeserializer extends StdDeserializer<ZonedDateTime> {

    public DateZonedToZuluDeserializer() {
        this(null);
    }

    public DateZonedToZuluDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ZonedDateTime deserialize(
            JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String date = jsonparser.getText();
        try {
            return parseZonedToUTC(date);
        } catch (JasonParseException e) {
            throw new IOException(e);
        }
    }

    private static ZonedDateTime parseZonedToUTC(String dateTime)
            throws JasonParseException {
        try {
            return ZonedDateTime.parse(dateTime)
                    .withZoneSameInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new JasonParseException(e.getMessage());
        }

    }
}