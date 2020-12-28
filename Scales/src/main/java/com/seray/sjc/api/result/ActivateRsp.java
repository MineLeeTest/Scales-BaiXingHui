package com.seray.sjc.api.result;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:46
 * E-mail：licheng@kedacom.com
 * Describe：世界村设备激活返回值
 */
public class ActivateRsp implements Serializable {

    public static final String TERM_ID = "termId";
    public static final String TERM_CODE = "termCode";

    // 终端ID
    @SerializedName("termId")
    @Expose
    public String termId;

    // 终端编码
    @SerializedName("termCode")
    @Expose
    public String termCode;

    @Override
    public String toString() {
        return "ActivateRsp{" +
                "termId='" + termId + '\'' +
                ", termCode='" + termCode + '\'' +
                '}';
    }

    public Map<String, String> toMap() {
        Map<String, String> data = new HashMap<>();
        if (!TextUtils.isEmpty(termId)) {
            data.put(TERM_ID, termId);
        }
        if (!TextUtils.isEmpty(termCode)) {
            data.put(TERM_CODE, termCode);
        }
        return data;
    }
}
