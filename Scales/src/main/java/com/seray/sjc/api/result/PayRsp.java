package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:47
 * E-mail：licheng@kedacom.com
 * Describe：世界村二维码支付查询结果
 */
public class PayRsp {

    // 支付状态（0：未支付 1：已支付） 加签字段
    @SerializedName("payStatus")
    @Expose
    public String payStatus;

    // 响应加签信息
    @SerializedName("signMsg")
    @Expose
    public String signMsg;

    @Override
    public String toString() {
        return "PayRsp{" +
                "payStatus='" + payStatus + '\'' +
                ", signMsg='" + signMsg + '\'' +
                '}';
    }
}
