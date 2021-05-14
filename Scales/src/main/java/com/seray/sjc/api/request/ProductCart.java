package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductCart implements Serializable {


    @Expose
    @SerializedName("product_id")
    private Integer product_id;//商品id
    @Expose
    @SerializedName("product_name")
    private String product_name;//商品名称
    @Expose
    @SerializedName("price_original")
    private BigDecimal price_original;//单价
    @Expose
    @SerializedName("price_real")
    private BigDecimal price_real;//实际单价
    @Expose
    @SerializedName("tare")
    private BigDecimal tare;//皮重
    @Expose
    @SerializedName("weight")
    private BigDecimal weight;//总重
    @Expose
    @SerializedName("money_total")
    private BigDecimal money_total;//商品总价

    public ProductCart() {
        super();
    }

    public ProductCart(Integer product_id, String product_name, BigDecimal price_original, BigDecimal price_real, BigDecimal tare, BigDecimal weight, BigDecimal money_total) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.price_original = price_original;
        this.price_real = price_real;
        this.tare = tare;
        this.weight = weight;
        this.money_total = money_total;
    }
}
