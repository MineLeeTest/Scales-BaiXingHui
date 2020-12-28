package com.seray.sjc.entity.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderInfo implements Serializable {

    // 小计
    private SjcSubtotal sjcSubtotal;

    // 明细列表
    private List<SjcDetail> sjcDetails;

    // 报文
    private Object tag;

    public OrderInfo(SjcSubtotal sjcSubtotal, List<SjcDetail> sjcDetails) {
        this.sjcSubtotal = sjcSubtotal;
        this.sjcDetails = new ArrayList<>(sjcDetails);
    }

    public OrderInfo(SjcSubtotal sjcSubtotal, SjcDetail sjcDetail) {
        this.sjcSubtotal = sjcSubtotal;
        this.sjcDetails = new ArrayList<>();
        this.sjcDetails.add(sjcDetail);
    }

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

    public Object getTag() {
        return tag;
    }

    public void setTag(Object bankPayTag) {
        this.tag = bankPayTag;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "sjcSubtotal=" + sjcSubtotal +
                ", sjcDetails=" + sjcDetails +
                ", tag=" + tag +
                '}';
    }
}
