package com.seray.util;

import java.util.UUID;

/**
 * Author：李程
 * CreateTime：2019/9/4 18:41
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class CodeFormat {

    public static String createCode() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
