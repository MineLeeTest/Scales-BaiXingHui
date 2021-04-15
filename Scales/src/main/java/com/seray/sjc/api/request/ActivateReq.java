package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:37
 * E-mail：licheng@kedacom.com
 * Describe：世界村设备激活请求参数
 */
public class ActivateReq {

    // 称号
    @Expose
    public String dzc_imei;

    // 密码
    @Expose
    public String sim_imsi;

    // 设备sn号
    @Expose
    public String app_version;

    // 设备软件版本号
    @Expose
    public Integer company_id;


}
