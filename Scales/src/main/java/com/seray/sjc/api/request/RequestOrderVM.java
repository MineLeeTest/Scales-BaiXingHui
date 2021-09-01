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
public class RequestOrderVM implements Serializable {

    @Expose
    public BigDecimal all_price;//总价

    @Expose
    public String buyer_pwd;//交易密码

    @Expose
    public Integer buyer_id;//买家id

    @Expose
    public Integer buyer_card_id;//买家卡片id

    @Expose
    public Integer seller_id;//卖家id

    @Expose
    public Integer seller_card_id;//卖家卡片id

    @Expose
    public Integer device_id;//电子秤id

    @Expose
    @SerializedName("productCartList")
    public List<ProductCart> productCartList;


}
