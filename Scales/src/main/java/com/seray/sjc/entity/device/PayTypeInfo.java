package com.seray.sjc.entity.device;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

@Entity(tableName = PayTypeInfo.TABLE_NAME)
public class PayTypeInfo {

    public static final String TABLE_NAME = "PayTypeInfo";

    @NonNull
    @PrimaryKey
    @SerializedName("payType")
    @Expose
    private String payType = UUID.randomUUID().toString();

    @SerializedName("payName")
    @Expose
    private String payName;

    @SerializedName("iconUrl")
    @Expose
    private String iconUrl;

    @SerializedName("apiPriKey")
    @Expose
    private String apiPriKey;

    @SerializedName("interfaceUrl")
    @Expose
    private String interfaceUrl;

    @SerializedName("cardClass")
    @Expose
    private String cardClass;

    public PayTypeInfo() {
    }

    @Ignore
    public PayTypeInfo(@NonNull String payType, String payName) {
        this.payType = payType;
        this.payName = payName;
    }

    @NonNull
    public String getPayType() {
        return payType;
    }

    public void setPayType(@NonNull String payType) {
        this.payType = payType;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getApiPriKey() {
        return apiPriKey;
    }

    public void setApiPriKey(String apiPriKey) {
        this.apiPriKey = apiPriKey;
    }

    public String getInterfaceUrl() {
        return interfaceUrl;
    }

    public void setInterfaceUrl(String interfaceUrl) {
        this.interfaceUrl = interfaceUrl;
    }

    public String getCardClass() {
        return cardClass;
    }

    public void setCardClass(String cardClass) {
        this.cardClass = cardClass;
    }

    @Override
    public String toString() {
        return "PayTypeInfo{" +
                "payType='" + payType + '\'' +
                ", payName='" + payName + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", apiPriKey='" + apiPriKey + '\'' +
                ", interfaceUrl='" + interfaceUrl + '\'' +
                ", cardClass='" + cardClass + '\'' +
                '}';
    }
}
