package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:46
 * E-mail：licheng@kedacom.com
 * Describe：世界村设备激活返回值
 */
@Data
public class DeviceRegisterDTO implements Serializable {

    @SerializedName("device_dzc_id")
    @Expose
    private Integer device_dzc_id;

    @SerializedName("use_aes_key")
    @Expose
    private String use_aes_key;

    @SerializedName("company_name")
    @Expose
    private String company_name;

}
