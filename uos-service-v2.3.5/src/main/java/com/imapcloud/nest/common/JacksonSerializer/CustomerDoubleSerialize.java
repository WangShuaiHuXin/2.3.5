package com.imapcloud.nest.common.JacksonSerializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.imapcloud.nest.utils.DoubleUtil;

import java.io.IOException;

/**
 * 自定义JSON取两位小数
 *
 * @author wmin
 */
public class CustomerDoubleSerialize extends JsonSerializer<Double> {

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value != null) {
            gen.writeNumber(DoubleUtil.roundKeepDec(2, value));
        } else {
            gen.writeNumber(0);
        }
    }
}
