package com.istef.minibilling2024.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.istef.minibilling2024.entity.User;

import java.io.IOException;

public class UserRefSerializer extends JsonSerializer<User> {

    public UserRefSerializer() {
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {

        gen.writeString(user.getRefNumber());
    }

}
