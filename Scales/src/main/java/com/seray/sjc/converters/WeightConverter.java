package com.seray.sjc.converters;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/14 00:09
 * E-mail：licheng@kedacom.com
 * Describe：世界村订单重量类数据转换
 */
public final class WeightConverter {

    @TypeConverter
    public static BigDecimal getWeightBigDecimal(String value) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(3, BigDecimal.ROUND_HALF_UP);
    }

    @TypeConverter
    public static String getWeightStringValue(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.toString();
        }
        return value.toString();
    }
}
