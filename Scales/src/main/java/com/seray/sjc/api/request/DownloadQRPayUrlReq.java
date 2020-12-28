package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.seray.sjc.converters.MoneyDecimalTypeAdapter;

import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:41
 * E-mail：licheng@kedacom.com
 * Describe：世界村二维码支付获取二维码请求参数
 */
public class DownloadQRPayUrlReq {

    // 终端ID 加签字段
    @SerializedName("termId")
    @Expose
    public String termId;

    // 交易流水号 加签字段
    @SerializedName("transOrderCode")
    @Expose
    public String transOrderCode;

    // 支付金额 元 加签字段
    @SerializedName("payMent")
    @Expose
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    public BigDecimal payMent;

    // 加签信息
    @SerializedName("signMsg")
    @Expose
    public String signMsg;

}
