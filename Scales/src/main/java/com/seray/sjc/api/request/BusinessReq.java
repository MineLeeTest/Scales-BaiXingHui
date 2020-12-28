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
public class BusinessReq implements Serializable {

    // 终端ID
    @SerializedName("termId")
    @Expose
    public String termId;

    // 上次同步数据结果 0:从未同步;1:同步成功;2:同步失败
    @SerializedName("lastSynResult")
    @Expose
    public String lastSynResult;

    // 上次同步数据时间 从未同步传当前时间
    @SerializedName("lastSynDate")
    @Expose()
//    @Expose(serialize = false, deserialize = false)
    public String lastSynDate;

    public BusinessReq() {
    }

    @Override
    public String toString() {
        return "BusinessReq{" +
                "termId='" + termId + '\'' +
                ", lastSynResult='" + lastSynResult + '\'' +
                ", lastSynDate='" + lastSynDate + '\'' +
                '}';
    }
}
