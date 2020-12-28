package com.seray.sjc.entity.card;

import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/5/4 22:58
 * E-mail：licheng@kedacom.com
 * Describe：世界村用户卡信息
 */
public final class UserCardInfo {

    // 卡号
    public String CARD_ID;

    // 卡类型
    public String CARD_TYPE;

    // 卡种
    public String CARD_CLASS;

    // 发卡城市代码
    public String CITY_CODE;

    // 发卡机构代码
    public String ISSUING_COMPANY;

    // 卡余额
    public BigDecimal BALANCE;

    @Override
    public String toString() {
        return "UserCardInfo{" +
                "CARD_ID='" + CARD_ID + '\'' +
                ", CARD_TYPE='" + CARD_TYPE + '\'' +
                ", CARD_CLASS='" + CARD_CLASS + '\'' +
                ", CITY_CODE='" + CITY_CODE + '\'' +
                ", ISSUING_COMPANY='" + ISSUING_COMPANY + '\'' +
                ", BALANCE=" + BALANCE +
                '}';
    }
}
