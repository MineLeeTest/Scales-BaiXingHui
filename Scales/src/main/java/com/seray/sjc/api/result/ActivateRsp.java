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
    // 终端ID
    @SerializedName("device_dzc_id")
    @Expose
    public Integer device_id;
}
