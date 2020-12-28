package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/4/18 22:11
 * E-mail：licheng@kedacom.com
 * Describe：世界村二维码支付查询和取消请求参数
 */
public class ControlQRPayReq {

    // 终端ID 加签字段
    @SerializedName("termId")
    @Expose
    public String termId;

    // 交易流水号 加签字段
    @SerializedName("transOrderCode")
    @Expose
    public String transOrderCode;

    // 加签信息
    @SerializedName("signMsg")
    @Expose
    public String signMsg;

}
