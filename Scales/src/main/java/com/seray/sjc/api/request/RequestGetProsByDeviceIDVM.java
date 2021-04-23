package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:50
 * E-mail：licheng@kedacom.com
 * Describe：世界村获取业务数据请求参数
 */
public class RequestGetProsByDeviceIDVM implements Serializable {

    @SerializedName("device_id")
    @Expose
    public String device_id;

}
