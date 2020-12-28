package com.seray.sjc.annotation;

import android.support.annotation.StringDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author：李程
 * CreateTime：2019/4/14 00:37
 * E-mail：licheng@kedacom.com
 * Describe：世界村交易类型
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@StringDef(value = {
        TransType.NORMAL,
        TransType.FORCE_RECORD
})
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface TransType {

    /**
     * 正常交易
     */
    String NORMAL = "1";

    /**
     * 强制记录
     */
    String FORCE_RECORD = "4";

}
