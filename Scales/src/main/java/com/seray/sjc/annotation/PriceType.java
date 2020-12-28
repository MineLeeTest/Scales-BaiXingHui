package com.seray.sjc.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author：李程
 * CreateTime：2019/4/14 00:23
 * E-mail：licheng@kedacom.com
 * Describe：世界村商品计价方式（明细通用）
 */
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {
        PriceType.BY_PRICE,
        PriceType.BY_PIECE
})
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER,ElementType.METHOD})
public @interface PriceType {

    /**
     * 计价
     */
    String BY_PRICE = "1";

    /**
     * 计件
     */
    String BY_PIECE = "2";
}
