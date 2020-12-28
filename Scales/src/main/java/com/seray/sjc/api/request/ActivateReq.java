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
    @SerializedName("termCode")
    @Expose
    public String termCode;

    // 密码
    @SerializedName("termPwd")
    @Expose
    public String termPwd;

    // 设备sn号
    @SerializedName("deviceSn")
    @Expose
    public String deviceSn;

    // 设备软件版本号
    @SerializedName("deviceVersion")
    @Expose
    public String deviceVersion;

    @Override
    public String toString() {
        return "ActivateReq{" +
                "termCode='" + termCode + '\'' +
                ", termPwd='" + termPwd + '\'' +
                ", deviceSn='" + deviceSn + '\'' +
                ", deviceVersion='" + deviceVersion + '\'' +
                '}';
    }
}
