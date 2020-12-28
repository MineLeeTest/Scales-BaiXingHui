package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TermHeartReq {
    //终端id
    @SerializedName("termId")
    @Expose
    public String  termId;

    //心跳时间
    @SerializedName("heartbeatTime")
    @Expose
    public String heartbeatTime;

    //软件版本
    @SerializedName("deviceVersion")
    @Expose
    public String deviceVersion;
}
