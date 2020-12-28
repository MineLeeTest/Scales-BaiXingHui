package com.seray.sjc.entity.card;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.seray.sjc.annotation.UploadStatus;
import com.seray.sjc.converters.MoneyConverter;
import com.seray.sjc.converters.MoneyDecimalTypeAdapter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Author：李程
 * CreateTime：2019/5/8 10:09
 * E-mail：licheng@kedacom.com
 * Describe：预付卡交易订单
 */
@Entity(tableName = CardPayOrder.TABLE_NAME)
public class CardPayOrder implements Serializable {

    public static final String TABLE_NAME = "CardPayOrder";

    public static final String TRANS_ORDER_CODE = "transOrderCode";

    // 终端ID
    @SerializedName("termId")
    @Expose
    private String termId;

    // 交易单号
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = CardPayOrder.TRANS_ORDER_CODE)
    @SerializedName("transOrderCode")
    @Expose
    private String transOrderCode = UUID.randomUUID().toString();

    // 交易时间
    @SerializedName("transDate")
    @Expose
    private String transDate;

    // 记录类型  TD：正常交易 TB：黑卡交易 默认“TD”
    @SerializedName("recordType")
    @Expose
    private String recordType;

    // 卡片交易序列号  卡片消费接口返回
    @SerializedName("transSeqNo")
    @Expose
    private String transSeqNo;

    // 发卡机构代码  卡片消费接口返回
    @SerializedName("issuingCompany")
    @Expose
    private String issuingCompany;

    // 城市代码  卡片消费接口返回
    @SerializedName("cityCode")
    @Expose
    private String cityCode;

    // 卡号  卡片消费接口返回
    @SerializedName("aliasCardId")
    @Expose
    private String aliasCardId;

    // 卡类型  卡片消费接口返回
    @SerializedName("cardType")
    @Expose
    private String cardType;

    // 卡种  卡片消费接口返回
    @SerializedName("cardClass")
    @Expose
    private String cardClass;

    // PSAM终端号  卡片消费接口返回
    @SerializedName("psamTermId")
    @Expose
    private String psamTermId;

    // PSAM卡号  卡片消费接口返回
    @SerializedName("psamCardId")
    @Expose
    private String psamCardId;

    // PSAM卡交易序列号  卡片消费接口返回
    @SerializedName("psamTransSeqNo")
    @Expose
    private String psamTransSeqNo;

    // 交易认证码  卡片消费接口返回
    @SerializedName("transCertifyCode")
    @Expose
    private String transCertifyCode;

    // 消费金额 金额格式：10.00
    @SerializedName("transAmt")
    @Expose
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    private BigDecimal transAmt;

    // 消费前卡余额 金额格式：200.00
    @SerializedName("beforeCardBalance")
    @Expose
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    private BigDecimal beforeCardBalance;

    // 消费后卡余额 金额格式：190.00
    @SerializedName("afterCardBalance")
    @Expose
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    private BigDecimal afterCardBalance;

    // 上传状态 0：未上传 1：上传成功
    @UploadStatus
    private int uploadStatus = UploadStatus.NO;

    public CardPayOrder() {
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    @NonNull
    public String getTransOrderCode() {
        return transOrderCode;
    }

    public void setTransOrderCode(@NonNull String transOrderCode) {
        this.transOrderCode = transOrderCode;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getRecordType() {
        return recordType;
    }

    public void setRecordType(String recordType) {
        this.recordType = recordType;
    }

    public String getTransSeqNo() {
        return transSeqNo;
    }

    public void setTransSeqNo(String transSeqNo) {
        this.transSeqNo = transSeqNo;
    }

    public String getIssuingCompany() {
        return issuingCompany;
    }

    public void setIssuingCompany(String issuingCompany) {
        this.issuingCompany = issuingCompany;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAliasCardId() {
        return aliasCardId;
    }

    public void setAliasCardId(String aliasCardId) {
        this.aliasCardId = aliasCardId;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardClass() {
        return cardClass;
    }

    public void setCardClass(String cardClass) {
        this.cardClass = cardClass;
    }

    public String getPsamTermId() {
        return psamTermId;
    }

    public void setPsamTermId(String psamTermId) {
        this.psamTermId = psamTermId;
    }

    public String getPsamCardId() {
        return psamCardId;
    }

    public void setPsamCardId(String psamCardId) {
        this.psamCardId = psamCardId;
    }

    public String getPsamTransSeqNo() {
        return psamTransSeqNo;
    }

    public void setPsamTransSeqNo(String psamTransSeqNo) {
        this.psamTransSeqNo = psamTransSeqNo;
    }

    public String getTransCertifyCode() {
        return transCertifyCode;
    }

    public void setTransCertifyCode(String transCertifyCode) {
        this.transCertifyCode = transCertifyCode;
    }

    public BigDecimal getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(BigDecimal transAmt) {
        this.transAmt = transAmt;
    }

    public BigDecimal getBeforeCardBalance() {
        return beforeCardBalance;
    }

    public void setBeforeCardBalance(BigDecimal beforeCardBalance) {
        this.beforeCardBalance = beforeCardBalance;
    }

    public BigDecimal getAfterCardBalance() {
        return afterCardBalance;
    }

    public void setAfterCardBalance(BigDecimal afterCardBalance) {
        this.afterCardBalance = afterCardBalance;
    }

    @UploadStatus
    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(@UploadStatus int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    @Override
    public String toString() {
        return "CardPayOrder{" +
                "termId='" + termId + '\'' +
                ", transOrderCode='" + transOrderCode + '\'' +
                ", transDate='" + transDate + '\'' +
                ", recordType='" + recordType + '\'' +
                ", transSeqNo='" + transSeqNo + '\'' +
                ", issuingCompany='" + issuingCompany + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", aliasCardId='" + aliasCardId + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardClass='" + cardClass + '\'' +
                ", psamTermId='" + psamTermId + '\'' +
                ", psamCardId='" + psamCardId + '\'' +
                ", psamTransSeqNo='" + psamTransSeqNo + '\'' +
                ", transCertifyCode='" + transCertifyCode + '\'' +
                ", transAmt=" + transAmt +
                ", beforeCardBalance=" + beforeCardBalance +
                ", afterCardBalance=" + afterCardBalance +
                ", uploadStatus=" + uploadStatus +
                '}';
    }
}
