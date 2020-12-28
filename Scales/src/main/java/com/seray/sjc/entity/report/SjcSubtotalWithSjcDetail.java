package com.seray.sjc.entity.report;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.seray.sjc.entity.order.SjcDetail;
import com.seray.sjc.entity.order.SjcSubtotal;

import java.io.Serializable;
import java.util.List;

/**
 * 商品小计和商品详情查询类
 */
public class SjcSubtotalWithSjcDetail implements Serializable {
    @Embedded
    private SjcSubtotal sjcSubtotal;

    @Relation(parentColumn = "transOrderCode",entityColumn = "transOrderCode")
    private List<SjcDetail> sjcDetails;

    public SjcSubtotal getSjcSubtotal() {
        return sjcSubtotal;
    }

    public void setSjcSubtotal(SjcSubtotal sjcSubtotal) {
        this.sjcSubtotal = sjcSubtotal;
    }

    public List<SjcDetail> getSjcDetails() {
        return sjcDetails;
    }

    public void setSjcDetails(List<SjcDetail> sjcDetails) {
        this.sjcDetails = sjcDetails;
    }

    @Override
    public String toString() {
        return "SjcSubtotalWithSjcDetail{" +
                "sjcSubtotal=" + sjcSubtotal +
                ", sjcDetails=" + sjcDetails +
                '}';
    }
}
