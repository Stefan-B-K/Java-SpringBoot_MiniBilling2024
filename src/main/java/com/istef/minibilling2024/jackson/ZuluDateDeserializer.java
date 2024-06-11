package com.istef.minibilling2024.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.ZonedDateTime;

public class ZuluDateDeserializer
        extends StdDeserializer<ZonedDateTime> {

    public ZuluDateDeserializer() {
        this(null);
    }

    public ZuluDateDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ZonedDateTime deserialize(JsonParser jsonparser, DeserializationContext context)
            throws IOException {

        String date = jsonparser.getText();
        return ZonedDateTime.parse(date);
    }
}