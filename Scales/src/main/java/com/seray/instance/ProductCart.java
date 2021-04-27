package com.seray.instance;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
public class ProductCart implements Serializable {

    private Integer product_id;//商品id
    private String tvProNameStr;//商品名称
    private String price;//单价
    private String real_price;//实际单价
    private String tare;//皮重
    private String weight;//总重
    private BigDecimal mTvSubtotalStr;//商品总价
    private Integer buyer_id;//买家id
    private Integer buyer_card_id;//买家卡片id
    private Integer seller_id;//卖家id
    private Integer seller_card_id;//卖家卡片id
    private Integer device_id;//电子秤id

    public ProductCart() {
        super();
    }

    public ProductCart(Integer product_id, String tvProNameStr, String price, String real_price, String tare, String weight, BigDecimal mTvSubtotalStr, Integer buyer_id, Integer buyer_card_id, Integer seller_id, Integer seller_card_id, Integer device_id) {
        this.product_id = product_id;
        this.tvProNameStr = tvProNameStr;
        this.price = price;
        this.real_price = real_price;
        this.tare = tare;
        this.weight = weight;
        this.mTvSubtotalStr = mTvSubtotalStr;
        this.buyer_id = buyer_id;
        this.seller_id = seller_id;
        this.device_id = device_id;
    }
}