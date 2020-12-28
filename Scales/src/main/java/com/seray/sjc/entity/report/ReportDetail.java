package com.seray.sjc.entity.report;

import android.arch.persistence.room.ColumnInfo;
/**
 * 商品详情对象
 */
public class ReportDetail {
    /**
     * 商品名称
     */
    @ColumnInfo(name = "goodsName")
    public String goodsName;

    /**
     * 成交数量
     */
    @ColumnInfo(name = "dealAmtSum")
    public double dealAmtSum;

    /**
     *支付类型
     */
    @ColumnInfo(name = "priceType")
    public String priceType;

    /**
     * 成交金额
     */
    @ColumnInfo(name = "dealCntSum")
    public double dealCntSum;

    @Override
    public String toString() {
        return "ReportDetail{" +
                "goodsName='" + goodsName + '\'' +
                ", dealAmtSum=" + dealAmtSum +
                ", priceType='" + priceType + '\'' +
                ", dealCntSum=" + dealCntSum +
                '}';
    }
}
