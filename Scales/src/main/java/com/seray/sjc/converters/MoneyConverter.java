package com.seray.sjc.converters;

import android.arch.persistence.room.TypeConverter;

import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/13 22:57
 * E-mail：licheng@kedacom.com
 * Describe：世界村订单金额类数据转换
 */
public final class MoneyConverter {

    @TypeConverter
    public static BigDecimal getMoneyBigDecimal(String value) {
        BigDecimal decimal = new BigDecimal(value);
        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    @TypeConverter
    public static String getMoneyStringValue(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.toString();
        }
        return value.toString();
    }
}
