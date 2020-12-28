package com.seray.sjc.entity.device;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author：李程
 * CreateTime：2019/4/18 21:39
 * E-mail：licheng@kedacom.com
 * Describe：世界村终端信息内容
 */
public class TermInfo implements Serializable {

    public static final String TERM_NAME = "termName";
    public static final String SHOP_NAME = "shopName";
    public static final String MARKET_NAME = "marketName";
    public static final String BOOTH_CODE = "boothCode";
    public static final String BOOTH_NAME = "boothName";

    // 终端名称
    @SerializedName("termName")
    @Expose
    private String termName;

    // 商铺(门店)名称
    @SerializedName("shopName")
    @Expose
    private String shopName;

    // 市场(公司,商场)名称
    @SerializedName("marketName")
    @Expose
    private String marketName;

    // 摊位编码
    @SerializedName("boothCode")
    @Expose
    private String boothCode;

    // 摊位名称
    @SerializedName("boothName")
    @Expose
    private String boothName;

    public TermInfo() {
    }

    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getBoothCode() {
        return boothCode;
    }

    public void setBoothCode(String boothCode) {
        this.boothCode = boothCode;
    }

    public String getBoothName() {
        return boothName;
    }

    public void setBoothName(String boothName) {
        this.boothName = boothName;
    }

    public Map<String, String> toMap() {
        Map<String, String> data = new HashMap<>();
        if (!TextUtils.isEmpty(termName)) {
            data.put(TERM_NAME, termName);
        }
        if (!TextUtils.isEmpty(shopName)) {
            data.put(SHOP_NAME, shopName);
        }
        if (!TextUtils.isEmpty(marketName)) {
            data.put(MARKET_NAME, marketName);
        }
        if (!TextUtils.isEmpty(boothCode)) {
            data.put(BOOTH_CODE, boothCode);
        }
        if (!TextUtils.isEmpty(boothName)) {
            data.put(BOOTH_NAME, boothName);
        }
        return data;
    }

    @Override
    public String toString() {
        return "TermInfo{" +
                "termName='" + termName + '\'' +
                ", shopName='" + shopName + '\'' +
                ", marketName='" + marketName + '\'' +
                ", boothCode='" + boothCode + '\'' +
                ", boothName='" + boothName + '\'' +
                '}';
    }
}
