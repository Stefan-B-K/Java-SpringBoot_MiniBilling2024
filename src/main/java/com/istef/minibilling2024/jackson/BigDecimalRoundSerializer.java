package com.istef.minibilling2024.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalRoundSerializer
        extends JsonSerializer<BigDecimal> implements ContextualSerializer {

    private int precision = 0;

    public BigDecimalRoundSerializer(int precision) {
        this.precision = precision;
    }

    public BigDecimalRoundSerializer() {
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (precision == 0) {
            gen.writeNumber(value);
            return;
        }

        gen.writeNumber(value.setScale(precision, RoundingMode.UP));
    }


    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
                                              BeanProperty property) {

        Precision precision = property.getAnnotation(Precision.class);
        if (precision != null) {
            return new BigDecimalRoundSerializer(precision.precision());
        }
        return this;
    }
}
