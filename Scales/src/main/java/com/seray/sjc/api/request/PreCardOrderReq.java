package com.seray.sjc.api.request;

import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/18 22:40
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class PreCardOrderReq {

    public String termId;

    // 电子秤生成的交易单号
    public String transOrderCode;

    // 交易时间	格式:2019-04-16 18:00:00
    public String transDate;

    // 记录类型	默认“TD” TD：正常交易  TB：黑卡交易
    public String recordType;

    // 	卡片交易序列号 片消费接口返回
    public String transSeqNo;

    // 发卡机构代码 卡片消费接口返回
    public String issuingCompany;

    // 城市代码	卡片消费接口返回
    public String cityCode;

    // 卡号	卡片消费接口返回
    public String aliasCardId;

    // 卡类型  卡片消费接口返回
    public String cardType;

    // 卡种	卡片消费接口返回
    public String cardClass;

    // 消费金额	金额格式：10.00
    public BigDecimal transAmt;

    // 消费前卡余额	金额格式：200.00
    public BigDecimal beforeCardBalance;

    // 消费后卡余额	金额格式：190.00
    public BigDecimal afterCardBalance;

    //  PSAM终端号	卡片消费接口返回
    public String psamTermId;

    //  PSAM卡号	  卡片消费接口返回
    public String psamCardId;

    // 	PSAM卡交易序列号	 卡片消费接口返回
    public String psamTransSeqNo;

    // 交易认证码	  卡片消费接口返回的Tac码
    public String transCertifyCode;

}
