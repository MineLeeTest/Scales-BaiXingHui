package com.seray.message;

import android.support.annotation.NonNull;

/**
 * 支付成功通知清除缓存订单
 */
public class ClearCartMsg {

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    @NonNull
    public Object getMsg() {
        return msg;
    }

    public void setMsg(@NonNull String msg) {
        this.msg = msg;
    }

    /**
     * 标识订单来源
     */
    @NonNull
    private String code;
    @NonNull
    private Object msg;

    public ClearCartMsg() {
        super();
    }

    public ClearCartMsg(@NonNull String code, @NonNull Object msg) {
        this.code = code;
        this.msg = msg;
    }
}

