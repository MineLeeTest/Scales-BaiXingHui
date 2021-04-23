package com.seray.instance;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import com.seray.sjc.annotation.PriceType;
import com.seray.sjc.entity.device.ProductADB;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:22
 * E-mail：licheng@kedacom.com
 * Describe：世界村交易明细
 */

public class SjcDetail implements Serializable {

    public static final String TABLE_NAME = "Detail";

    private long id;

    // 交易单号
    private String transOrderCode;

    // 商品ID
    private String goodsInfoId;

    // 商品编码（用于打印显示）
    private String goodsCode;

    // 商品名称
    private String goodsName;

    // 销售单位
    private String saleUnit;

    // 价格类型 1：计价；2：计件
    private String priceType;

    // 销售单价
    private BigDecimal salePrice;

    // 成交单价
    private BigDecimal dealPrice;

    // 成交数量
    private BigDecimal dealCnt;

    // 去皮
    private BigDecimal netWeight;

    // 折扣价格
    private BigDecimal discountAmt;

    // 成交总价
    private BigDecimal dealAmt;

    public SjcDetail() {
    }

    public static SjcDetail getSjcDetail(@NonNull String transOrderCode, @NonNull ProductADB product) {
        SjcDetail detail = getSjcDetail(transOrderCode);
//        detail.setGoodsInfoId(product.getProduct_id());
//        detail.setGoodsName(product.getPro_name());
//        detail.setGoodsCode(product.get());
//        detail.setSalePrice(product.getSalePrice());
//        detail.setSaleUnit(product.getSaleUnit());
//        detail.setPriceType(product.getPriceType());
        return detail;
    }

    public static SjcDetail getSjcDetail(@NonNull String transOrderCode) {
        SjcDetail detail = new SjcDetail();
        detail.setTransOrderCode(transOrderCode);
        return detail;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTransOrderCode() {
        return transOrderCode;
    }

    public void setTransOrderCode(String transOrderCode) {
        this.transOrderCode = transOrderCode;
    }

    public String getGoodsInfoId() {
        return goodsInfoId;
    }

    public void setGoodsInfoId(String goodsInfoId) {
        this.goodsInfoId = goodsInfoId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(String saleUnit) {
        this.saleUnit = saleUnit;
    }

    @PriceType
    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(@PriceType String priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public BigDecimal getDealCnt() {
        return dealCnt;
    }

    public void setDealCnt(BigDecimal dealCnt) {
        this.dealCnt = dealCnt;
    }

    public BigDecimal getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(BigDecimal netWeight) {
        this.netWeight = netWeight;
    }

    public BigDecimal getDiscountAmt() {
        return discountAmt;
    }

    public void setDiscountAmt(BigDecimal discountAmt) {
        this.discountAmt = discountAmt;
    }

    public BigDecimal getDealAmt() {
        return dealAmt;
    }

    public void setDealAmt(BigDecimal dealAmt) {
        this.dealAmt = dealAmt;
    }

    @Override
    public String toString() {
        return "SjcDetail{" +
                "id=" + id +
                ", transOrderCode='" + transOrderCode + '\'' +
                ", goodsInfoId='" + goodsInfoId + '\'' +
                ", goodsCode='" + goodsCode + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", saleUnit='" + saleUnit + '\'' +
                ", priceType='" + priceType + '\'' +
                ", salePrice=" + salePrice +
                ", dealPrice=" + dealPrice +
                ", dealCnt=" + dealCnt +
                ", netWeight=" + netWeight +
                ", discountAmt=" + discountAmt +
                ", dealAmt=" + dealAmt +
                '}';
    }
}
