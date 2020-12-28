package com.seray.sjc.entity.order;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.seray.scales.App;
import com.seray.cache.CacheHelper;
import com.seray.sjc.annotation.TransType;
import com.seray.sjc.annotation.UploadStatus;
import com.seray.sjc.converters.MoneyConverter;
import com.seray.sjc.converters.MoneyDecimalTypeAdapter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:20
 * E-mail：licheng@kedacom.com
 * Describe：世界村交易小计
 */
@Entity(tableName = SjcSubtotal.TABLE_NAME)
public class SjcSubtotal implements Serializable {

    public static final String TABLE_NAME = "Subtotal";

    public static final String TRANS_ORDER_CODE = "transOrderCode";

    public static final String BASE_CASH_PAY_TYPE = "0101";

    // 终端ID
    @SerializedName("termId")
    @Expose
    private String termId;

    // 交易单号
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = SjcSubtotal.TRANS_ORDER_CODE)
    @SerializedName("transOrderCode")
    @Expose
    private String transOrderCode = UUID.randomUUID().toString();

    // 交易时间
    @SerializedName("transDate")
    @Expose
    private String transDate;

    // 交易类型
    @SerializedName("transType")
    @TransType
    @Expose
    private String transType;

    // 付款方式
    @SerializedName("payType")
    @Expose
    private String payType;

    // 交易金额
    @SerializedName("transAmt")
    @TypeConverters(value = MoneyConverter.class)
    @JsonAdapter(MoneyDecimalTypeAdapter.class)
    @Expose
    private BigDecimal transAmt;

    // 设备软件版本号
    @SerializedName("deviceVersion")
    @Expose
    private String deviceVersion;

    // 交易商品信息
    @Ignore
    @SerializedName("transItemList")
    @Expose
    private List<SjcDetail> transItemList;

    // 上传状态 0：未上传 1：上传成功 2：上传失败
    @UploadStatus
    private int uploadStatus = UploadStatus.NO;

    // 支付状态 -1：支付失败 0：支付中 1：支付取消 2：支付成功
    private int payStatus = 0;

    public SjcSubtotal() {

    }

    @Ignore
    public SjcSubtotal(@NonNull String transOrderCode,
                       String transDate,
                       @TransType String transType,
                       BigDecimal transAmt) {
        this.transOrderCode = transOrderCode;
        this.transDate = transDate;
        this.transType = transType;
        this.transAmt = transAmt;
        this.payType = BASE_CASH_PAY_TYPE;
        this.termId = CacheHelper.TermId;
        this.deviceVersion = App.VersionName;
    }

    public int getPayStatus() {
        return payStatus;
    }

    /**
     * 设置支付状态
     *
     * @param payStatus 支付状态 -1：支付失败 0：支付中 1：支付取消 2：支付成功
     */
    public void setPayStatus(int payStatus) {
        this.payStatus = payStatus;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(@TransType String transType) {
        this.transType = transType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public BigDecimal getTransAmt() {
        return transAmt;
    }

    public void setTransAmt(BigDecimal transAmt) {
        this.transAmt = transAmt;
    }

    public String getDeviceVersion() {
        return deviceVersion;
    }

    public void setDeviceVersion(String deviceVersion) {
        this.deviceVersion = deviceVersion;
    }

    public List<SjcDetail> getTransItemList() {
        return transItemList;
    }

    public void setTransItemList(List<SjcDetail> transItemList) {
        this.transItemList = transItemList;
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
        return "SjcSubtotal{" +
                "termId='" + termId + '\'' +
                ", transOrderCode='" + transOrderCode + '\'' +
                ", transDate='" + transDate + '\'' +
                ", transType='" + transType + '\'' +
                ", payType='" + payType + '\'' +
                ", transAmt=" + transAmt +
                ", deviceVersion='" + deviceVersion + '\'' +
                ", transItemList=" + transItemList +
                ", uploadStatus=" + uploadStatus +
                ", payStatus=" + payStatus +
                '}';
    }
}
