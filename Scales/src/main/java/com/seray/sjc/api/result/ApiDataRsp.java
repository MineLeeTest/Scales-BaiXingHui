package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:40
 * E-mail：licheng@kedacom.com
 * Describe：世界村接口标准格式
 */
public class ApiDataRsp {

    ApiDataRsp() {
    }

    // 返回码 0000 表示成功，其他表示失败
    @SerializedName("code")
    @Expose
    public String code;
    // 返回码描述
    @SerializedName("errorMsg")
    @Expose
    public String errorMsg;

    // 返回码描述
    @SerializedName("uuid")
    @Expose
    public String uuid;

    // 业务数据
    @SerializedName("msg")
    @Expose
    public Object msg;

    // 返回码描述
    @SerializedName("success")
    @Expose
    public Boolean success;

    @Override
    public String toString() {
        return "ApiDataRsp{" +
                "code='" + code + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", uuid='" + uuid + '\'' +
                ", msg=" + msg +
                ", success=" + success +
                '}';
    }
}
