package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.seray.cache.CacheHelper;
import com.seray.scales.BuildConfig;
import com.seray.sjc.entity.device.Config;

public class TermHeartReq {

    public Integer device_dzc_id = CacheHelper.device_id;

    // 设备软件版本号
    public String app_version = BuildConfig.VERSION_NAME;

    // sim卡的编号
    public String sim_imsi;

    // 安卓机的硬件IMEI
    public String dzc_imei;

    // 所属公司
    public Integer company_id;

    // sim卡的编号
    public String info;
}
