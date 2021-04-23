package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lombok.Data;


@Data
public class HeartBeatDeviceDzcDTO implements Serializable {
    @SerializedName("device_dzc_id")
    @Expose
    private Integer device_dzc_id;

    @SerializedName("app_version")
    @Expose
    private String app_version;

    @SerializedName("data_version")
    @Expose
    private String data_version;

    @SerializedName("opreate_info")
    @Expose
    private String opreate_info;

}