package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:37
 * E-mail：licheng@kedacom.com
 * Describe：世界村设备激活请求参数
 */
@Data
public class RequestRegisterVM {

    // 称号
    @Expose
    private String dzc_imei;

    // 密码
    @Expose
    private String sim_imsi;

    // 设备sn号
    @Expose
    private  String app_version;

    // 设备软件版本号
    @Expose
    private Integer company_id;

    @Override
    public String toString() {
        return "RequestRegisterVM{" +
                "dzc_imei='" + dzc_imei + '\'' +
                ", sim_imsi='" + sim_imsi + '\'' +
                ", app_version='" + app_version + '\'' +
                ", company_id=" + company_id +
                '}';
    }
}
