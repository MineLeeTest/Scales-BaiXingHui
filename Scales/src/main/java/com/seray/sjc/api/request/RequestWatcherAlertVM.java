package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:37
 * E-mail：licheng@kedacom.com
 * Describe：世界村设备激活请求参数
 */
@Data
public class RequestWatcherAlertVM implements Serializable {

    @Expose
    public Integer buyer_id;//买家id
    @Expose
    public String buyer_name;//买家姓名
    @Expose
    public Integer buyer_card_id;//买家卡片id

    @Expose
    public Integer seller_id;//卖家id
    @Expose
    public String seller_name;//卖家姓名
    @Expose
    public Integer seller_card_id;//卖家卡片id

    @Expose
    public String weight_data;//称重数据

    @Override
    public String toString() {
        return "RequestWatcherAlertVM{" +
                "buyer_id=" + buyer_id +
                ", buyer_name=" + buyer_name +
                ", buyer_card_id=" + buyer_card_id +
                ", seller_id=" + seller_id +
                ", seller_name=" + seller_name +
                ", seller_card_id=" + seller_card_id +
                ", weight_data='" + weight_data + '\'' +
                '}';
    }
}
