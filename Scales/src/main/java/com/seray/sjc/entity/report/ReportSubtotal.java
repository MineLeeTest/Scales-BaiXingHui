package com.seray.sjc.entity.report;

import android.arch.persistence.room.ColumnInfo;

/**
 * 商品小计对象
 */
public class ReportSubtotal {
    /**
     * 交易额
     */
    @ColumnInfo(name = "transAmtSum")
    public double transAmtSum;

    /**
     * 交易类型
     */
    @ColumnInfo(name = "payType")
    public String payType;

    /**
     * 交易数量
     */
    @ColumnInfo(name = "count")
    public long count;

    @Override
    public String toString() {
        return "ReportSubtotal{" +
                "transAmtSum=" + transAmtSum +
                ", payType='" + payType + '\'' +
                ", count=" + count +
                '}';
    }
}
