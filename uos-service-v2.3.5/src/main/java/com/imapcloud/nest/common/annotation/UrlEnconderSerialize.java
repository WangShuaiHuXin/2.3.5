package com.imapcloud.nest.common.annotation;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.net.URLEncoder;

public class UrlEnconderSerialize extends JsonSerializer<String>{

    @Override
    public void serialize(String url, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if(url!=null) {
            jsonGenerator.writeString(URLEncoder.encode(url));
        }
    }
}
