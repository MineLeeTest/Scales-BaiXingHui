package com.seray.sjc.entity.order;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.seray.sjc.annotation.PriceType;
import com.seray.sjc.converters.MoneyConverter;
import com.seray.sjc.converters.MoneyDecimalTypeAdapter;
import com.seray.sjc.converters.WeightConverter;
import com.seray.sjc.converters.WeightDecimalTypeAdapter;
import com.seray.sjc.entity.product.SjcProduct;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:22
 * E-mail：licheng@kedacom.com
 * Describe：世界村交易明细
 */
@Entity(tableName = SjcDetail.TABLE_NAME,
        foreignKeys = @ForeignKey(entity = SjcSubtotal.class,
                parentColumns = SjcSubtotal.TRANS_ORDER_CODE,
                childColumns = SjcSubtotal.TRANS_ORDER_CODE,
                onDelete = ForeignKey.CASCADE),
        indices = @Index(value = {SjcSubtotal.TRANS_ORDER_CODE})
)
public class SjcDetail implements Serializable {

    public static final String TABLE_NAME = "Detail";

    @PrimaryKey(autoGenerate = true)
    private long id;

    // 交易单号
    @ColumnInfo(name = SjcSubtotal.TRANS_ORDER_CODE)
    @SerializedName("transOrderCode")
    @Expose
    private String transOrderCode;

    // 商品ID
    @SerializedName("goodsId")
    @Expose
    private String goodsInfoId;

    // 商品编码（用于打印显示）
    @ColumnInfo(name = SjcProduct.COLUMN_GOODS_CODE)
    @Expose(serialize = false, deserialize = false)
    private String goodsCode;

    // 商品名称
    @SerializedName("goodsName")
    @Expose
    private String goodsName;

    // 销售单位
    @SerializedName("saleUnit")
    @Expose
    private String saleUnit;

    // 价格类型 1：计价；2：计件
    @SerializedName("priceType")
    @PriceType
    @Expose
    private String priceType;

    // 销售单价
    @SerializedName("salePrice")
    @TypeConverters(value = MoneyConverter.class)
    @Expose
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    private BigDecimal salePrice;

    // 成交单价
    @SerializedName("dealPrice")
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    @Expose
    private BigDecimal dealPrice;

    // 成交数量
    @SerializedName("dealCnt")
    @TypeConverters(value = WeightConverter.class)
    @JsonAdapter(WeightDecimalTypeAdapter.class)
    @Expose
    private BigDecimal dealCnt;

    // 去皮
    @SerializedName("netWeight")
    @TypeConverters(value = WeightConverter.class)
    @JsonAdapter(WeightDecimalTypeAdapter.class)
    @Expose
    private BigDecimal netWeight;

    // 折扣价格
    @SerializedName("discountAmt")
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    @Expose
    private BigDecimal discountAmt;

    // 成交总价
    @SerializedName("dealAmt")
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    @Expose
    private BigDecimal dealAmt;

    public SjcDetail() {
    }

    public static SjcDetail getSjcDetail(@NonNull String transOrderCode, @NonNull SjcProduct product) {
        SjcDetail detail = getSjcDetail(transOrderCode);
        detail.setGoodsInfoId(product.getGoodsId());
        detail.setGoodsName(product.getGoodsName());
        detail.setGoodsCode(product.getGoodsCode());
        detail.setSalePrice(product.getSalePrice());
        detail.setSaleUnit(product.getSaleUnit());
        detail.setPriceType(product.getPriceType());
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
