package com.seray.sjc.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author licheng
 * @since 2019/6/5 14:36
 */
@IntDef({
        DisplayType.DISPLAY_WEIGHT,
        DisplayType.DISPLAY_PAY,
        DisplayType.DISPLAY_AD
})
@Retention(RetentionPolicy.SOURCE)
@Documented
@Target({ElementType.PARAMETER})
public @interface DisplayType {

    /**
     * 称重
     */
    int DISPLAY_WEIGHT = 0;

    /**
     * 支付
     */
    int DISPLAY_PAY = 1;

    /**
     * 广告
     */
    int DISPLAY_AD = 2;


}
