package com.seray.message;

import android.support.annotation.NonNull;

/**
 * 支付成功通知清除缓存订单
 */
public class ClearOrderMsg {

    /**
     * 标识订单来源
     */
    @NonNull
    private String msg;

    public ClearOrderMsg(@NonNull String msg) {
        this.msg = msg;
    }

    @NonNull
    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ClearOrderMsg{" +
                "msg='" + msg + '\'' +
                '}';
    }
}

