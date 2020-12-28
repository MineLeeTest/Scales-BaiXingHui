package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/5/12 13:05
 * E-mail：licheng@kedacom.com
 * Describe：世界村拆机告警请求类
 */
public class OpenMachineAlarmReq {

    /**
     * 终端ID
     */
    @Expose
    @SerializedName("termId")
    public String termId;

    /**
     * 告警时间 时间格式 2019-01-01 08:06:06
     */
    @Expose
    @SerializedName("alarmTime")
    public String alarmTime;

    public OpenMachineAlarmReq(String termId, String alarmTime) {
        this.termId = termId;
        this.alarmTime = alarmTime;
    }

    @Override
    public String toString() {
        return "OpenMachineAlarmReq{" +
                "termId='" + termId + '\'' +
                ", alarmTime='" + alarmTime + '\'' +
                '}';
    }
}
