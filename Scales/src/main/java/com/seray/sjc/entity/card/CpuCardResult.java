package com.seray.sjc.entity.card;

/**
 * Author：李程
 * CreateTime：2019/5/4 23:01
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public final class CpuCardResult<T> {

    public T data;

    public String errorCode;

    public String errorMessage;

    @Override
    public String toString() {
        return "CpuCardResult{" +
                "data=" + data +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
