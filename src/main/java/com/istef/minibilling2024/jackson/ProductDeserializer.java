package com.istef.minibilling2024.jackson;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.istef.minibilling2024.entity.Product;

import java.io.IOException;

public class ProductDeserializer extends StdDeserializer<Product> {

    public ProductDeserializer() {
        this(null);
    }

    public ProductDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Product deserialize(JsonParser jsonparser, DeserializationContext ctxt) throws IOException {
        String product = jsonparser.getText();
        return Product.init(product);
    }
}