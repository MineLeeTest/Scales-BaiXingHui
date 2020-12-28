package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:40
 * E-mail：licheng@kedacom.com
 * Describe：世界村接口标准格式
 */
public class ApiDataRsp<T> {

//    Map<String, String> RSP_CODE_MAP = new HashMap<>();

    ApiDataRsp() {
//        init();
    }

//    private void init() {
//        RSP_CODE_MAP.put("0000", "成功");
//        RSP_CODE_MAP.put("0001", "系统未知异常");
//        RSP_CODE_MAP.put("0002", "参数验证异常");
//    }

    // 返回码 0000 表示成功，其他表示失败
    @SerializedName("code")
    @Expose
    public String code;

    // 返回码描述
    @SerializedName("desc")
    @Expose
    public String desc;

    // 业务数据
    @SerializedName("info")
    @Expose
    public T info;

    public boolean isSuccess() {
        return "0000".equals(this.code);
    }

    public String getErrorMsg() {
        return this.desc;
    }

    @Override
    public String toString() {
        return "ApiDataRsp{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                ", info=" + info +
                '}';
    }
}
