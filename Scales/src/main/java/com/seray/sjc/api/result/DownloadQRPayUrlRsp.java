package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:40
 * E-mail：licheng@kedacom.com
 * Describe：世界村二维码支付获取二维码结果
 */
public class DownloadQRPayUrlRsp {

    // 二维码串 加签字段
    @SerializedName("qrUrl")
    @Expose
    public String qrUrl;

    // 上传的交易流水号 加签字段
    @SerializedName("transOrderCode")
    @Expose
    public String transOrderCode;

    // 响应加签信息
    @SerializedName("signMsg")
    @Expose
    public String signMsg;

    @Override
    public String toString() {
        return "DownloadQRPayUrlRsp{" +
                "qrUrl='" + qrUrl + '\'' +
                ", transOrderCode='" + transOrderCode + '\'' +
                ", signMsg='" + signMsg + '\'' +
                '}';
    }
}
