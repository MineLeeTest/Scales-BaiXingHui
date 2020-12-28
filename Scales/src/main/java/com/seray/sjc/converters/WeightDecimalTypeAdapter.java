package com.seray.sjc.converters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/14 00:12
 * E-mail：licheng@kedacom.com
 * Describe：世界村BigDecimal字段序列化与发序列化助手
 */
public class WeightDecimalTypeAdapter extends TypeAdapter<BigDecimal> {

    @Override
    public void write(JsonWriter out, BigDecimal value) throws IOException {
        if (value == null) {
            out.value(0.000d);
        } else {
            out.value(value.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
        }
    }

    @Override
    public BigDecimal read(JsonReader in) throws IOException {
        double nextDouble = in.nextDouble();
        return new BigDecimal(nextDouble).setScale(3, BigDecimal.ROUND_HALF_UP);
    }
}
