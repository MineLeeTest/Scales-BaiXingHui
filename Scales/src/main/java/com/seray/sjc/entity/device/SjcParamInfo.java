package com.seray.sjc.entity.device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/4/18 21:20
 * E-mail：licheng@kedacom.com
 * Describe：世界村下发参数设置信息
 */
public class SjcParamInfo {

    /**
     * 多媒体资源地址KEY
     */
    public static final String RESOURCE_URL = "resourceUrl";

    /**
     * 溯源地址KEY
     */
    public static final String TRACING_URL = "suyuanUrl";

    /**
     * 心跳包间隔时间KEY
     */
    public static final String HEART_BEAT_TIME = "heartBeatTime";

    /**
     * 电子秤业务数据同步间隔时间KEY
     */
    public static final String SYNC_DOWN_TIME = "syncDownTime";

    /**
     * 果蔬识别最小阈值KEY 单位：%
     * 服务器暂无此字段返回值
     */
    public static final String MinRecognizeValue = "minRecognizeValue";

    @SerializedName(value = "paramKey")
    @Expose
    private String paramKey;

    @SerializedName(value = "paramValue")
    @Expose
    private String paramValue;

    public SjcParamInfo() {
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getParamValue() {
        return paramValue;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    @Override
    public String toString() {
        return "SjcParamInfo{" +
                "paramKey='" + paramKey + '\'' +
                ", paramValue='" + paramValue + '\'' +
                '}';
    }
}
