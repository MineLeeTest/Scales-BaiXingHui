package com.seray.sjc.entity.card;

import java.math.BigDecimal;
/**
 * Author：李程
 * CreateTime：2019/5/5 21:27
 * E-mail：licheng@kedacom.com
 * Describe：世界村PSAM卡信息
 */
public final class PsamCardInfo {

    // 卡号
    public String PSAM_ID;

    // 终端号
    public String PSAM_TERM_NO;

    // 消费前卡余额
    public BigDecimal ED_BALANCE;

    // 卡片交易序列号
    public String TSN;

    // 卡交易序列号
    public String PSAM_TTN;

    // 交易认证码
    public String TAC;

    @Override
    public String toString() {
        return "PsamCardInfo{" +
                "PSAM_ID='" + PSAM_ID + '\'' +
                ", PSAM_TERM_NO='" + PSAM_TERM_NO + '\'' +
                ", ED_BALANCE='" + ED_BALANCE + '\'' +
                ", TSN='" + TSN + '\'' +
                ", PSAM_TTN='" + PSAM_TTN + '\'' +
                ", TAC='" + TAC + '\'' +
                '}';
    }
}
