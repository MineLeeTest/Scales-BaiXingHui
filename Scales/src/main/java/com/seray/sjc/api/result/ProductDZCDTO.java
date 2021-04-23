package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

@Data
public class ProductDZCDTO implements Serializable {
    @SerializedName("product_id")
    @Expose
    private Integer product_id;

    @SerializedName("sequences")
    @Expose
    private Integer sequences;

    @SerializedName("pro_name")
    @Expose
    private String pro_name;

    @SerializedName("price")
    @Expose
    private Double price;

    @SerializedName("max_price")
    @Expose
    private Double max_price;

    @SerializedName("min_price")
    @Expose
    private Double min_price;

    @SerializedName("img_url")
    @Expose
    private String img_url;


}
