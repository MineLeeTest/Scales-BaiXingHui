package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.seray.cache.CacheHelper;
import com.seray.scales.BuildConfig;

import java.io.Serializable;

import lombok.Data;

@Data
public class RequestHeartBeatVM implements Serializable {
    @Expose
    private Integer device_dzc_id = CacheHelper.device_id;

    // 设备软件版本号
    @Expose
    private String app_version = BuildConfig.VERSION_NAME;

    // sim卡的编号
    @Expose
    private String sim_imsi;

    // 安卓机的硬件IMEI
    @Expose
    private String dzc_imei;

    // app_data
    @Expose
    private String app_data;

    // 所属公司
    @Expose
    private Integer company_id;

    // sim卡的编号
    @Expose
    private String info;

    //电池信息
    @Expose
    private String battery_info;

    @Override
    public String toString() {
        return "RequestHeartBeatVM{" +
                "device_dzc_id=" + device_dzc_id +
                ", app_version='" + app_version + '\'' +
                ", sim_imsi='" + sim_imsi + '\'' +
                ", dzc_imei='" + dzc_imei + '\'' +
                ", company_id=" + company_id +
                ", info='" + info + '\'' +
                '}';
    }
}
