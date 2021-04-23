package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import lombok.Data;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:40
 * E-mail：licheng@kedacom.com
 * Describe：世界村接口标准格式
 */
@Data
public class ApiDataRsp<T> implements Serializable {

    // 返回码 0000 表示成功，其他表示失败
    @SerializedName("code")
    @Expose
    private String code;

    // 返回码描述
    @SerializedName("error_msg")
    @Expose
    private String error_msg;

    // 返回码描述
    @SerializedName("uuid")
    @Expose
    private String uuid;

    // 业务数据
    @SerializedName("msg")
    @Expose
    private T msgs;

    // 返回码描述
    @SerializedName("success")
    @Expose
    private Boolean success;

}
