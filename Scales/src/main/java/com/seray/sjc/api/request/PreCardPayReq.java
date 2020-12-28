package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/18 22:33
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class PreCardPayReq {

    // 交易金额
    @SerializedName("pay_amount")
    @Expose
    public BigDecimal payAmount;

    // 终端允许消费的卡种 cardclassList[“001”,”003”]，允许卡种001、003的卡消费
    @SerializedName("cardclassList")
    @Expose
    public String[] cardClassList;

}
