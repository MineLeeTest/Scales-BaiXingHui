package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVipCardDetailDTO implements Serializable {
    @SerializedName("user_vip_id")
    @Expose
    private Integer user_vip_id;

    @SerializedName("user_vip_card_id")
    @Expose
    private Integer user_vip_card_id;

    @SerializedName("create_time")
    @Expose
    private String create_time;

    @SerializedName("enables")
    @Expose
    private Boolean enables;

    @SerializedName("card_no")
    @Expose
    private String card_no;

    @SerializedName("card_name")
    @Expose
    private String card_name;

    @SerializedName("user_name")
    @Expose
    private String user_name;

    @SerializedName("user_phone")
    @Expose
    private String user_phone;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("idno")
    @Expose
    private String idno;

    @SerializedName("user_type")
    @Expose
    private String user_type;

    @SerializedName("balance")
    @Expose
    private String balance;

}
