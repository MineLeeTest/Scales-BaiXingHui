package com.seray.sjc.entity.product;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:59
 * E-mail：licheng@kedacom.com
 * Describe：世界村商品
 */
@Entity(tableName = SjcProduct.TABLE_NAME,
        foreignKeys = @ForeignKey(entity = SjcCategory.class,
                parentColumns = SjcCategory.CATEGORY_ID,
                childColumns = SjcCategory.CATEGORY_ID,
                onDelete = ForeignKey.CASCADE),
        indices = @Index(value = {SjcCategory.CATEGORY_ID})
)
public class SjcProduct implements Serializable {

    public static final String TABLE_NAME = "Product";

    public static final String COLUMN_GOODS_CODE = "goodsCode";

    public static final String COLUMN_GOODS_NAME = "goodsName";

    // 商品ID
    @PrimaryKey
    @NonNull
    @SerializedName("goodsId")
    @Expose
    private String goodsId = UUID.randomUUID().toString();

    // 商品编码（用于打印显示）
    @ColumnInfo(name = SjcProduct.COLUMN_GOODS_CODE)
    @SerializedName("thGoodsCode")
    @Expose
    private String goodsCode;

    // 商品分类
    @ColumnInfo(name = SjcCategory.CATEGORY_ID)
    @SerializedName("categoryId")
    @Expose
    private String categoryId;

    // 商品名称
    @ColumnInfo(name = SjcProduct.COLUMN_GOODS_NAME)
    @SerializedName("goodsName")
    @Expose
    private String goodsName;

    // 图片地址
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    // 商品排序
    @SerializedName("goodsSort")
    @Expose
    private int goodsSort;

    // 价格类型 1：计价；2：计件
    @SerializedName("priceType")
    @Expose
    @PriceType
    private String priceType;

    // 参考单价
    @SerializedName("referencePrice")
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    @Expose
    private BigDecimal referencePrice;

    // 销售单价
    @SerializedName("salePrice")
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    @Expose
    private BigDecimal salePrice;

    // 销售单位
    @SerializedName("saleUnit")
    @Expose
    private String saleUnit;

    // 选中
    @Ignore
    private boolean isSelected;

    public SjcProduct() {
    }

    @NonNull
    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(@NonNull String goodsId) {
        this.goodsId = goodsId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getGoodsSort() {
        return goodsSort;
    }

    public void setGoodsSort(int goodsSort) {
        this.goodsSort = goodsSort;
    }

    @PriceType
    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(@PriceType String priceType) {
        this.priceType = priceType;
    }

    public BigDecimal getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(BigDecimal referencePrice) {
        this.referencePrice = referencePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getSaleUnit() {
        return saleUnit;
    }

    public void setSaleUnit(String saleUnit) {
        this.saleUnit = saleUnit;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    @Override
    public String toString() {
        return "SjcProduct{" +
                "goodsId='" + goodsId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", goodsSort=" + goodsSort +
                ", priceType='" + priceType + '\'' +
                ", referencePrice=" + referencePrice +
                ", salePrice=" + salePrice +
                ", saleUnit='" + saleUnit + '\'' +
                ", isSelected=" + isSelected +
                ", goodsCode='" + goodsCode + '\'' +
                '}';
    }
}
