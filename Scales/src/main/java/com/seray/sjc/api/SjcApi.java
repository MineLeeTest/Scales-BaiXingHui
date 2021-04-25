package com.seray.sjc.api;

import com.seray.sjc.api.request.GetUserByCardNoVM;
import com.seray.sjc.api.request.RequestHeartBeatVM;
import com.seray.sjc.api.request.RequestRegisterVM;
import com.seray.sjc.api.result.HeartBeatDeviceDzcDTO;
import com.seray.sjc.api.result.DeviceRegisterDTO;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.ProductDZCDTO;
import com.seray.sjc.api.result.UserVipCardDetailDTO;

import java.lang.annotation.Target;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:42
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public interface SjcApi {
    /**
     * 设备激活
     *
     * @param requestRegisterVM 设备激活请求体
     * @return 设备基本参数
     */
    @POST("/device_public/register_device")
    Call<ApiDataRsp<DeviceRegisterDTO>> register_device(@Body RequestRegisterVM requestRegisterVM);

    /**
     * 心跳包
     */
    @POST("/device_public/heart_beat")
    Call<ApiDataRsp<HeartBeatDeviceDzcDTO>> heart_beat(@Body RequestHeartBeatVM requestHeartBeatVM);

    /**
     * 获取商品列表
     */
    @POST("/dzc/pro_find")
    Call<ApiDataRsp<List<ProductDZCDTO>>> pro_find(@Header("device_id") String device_id, @Header("sign") String sign);

    /**
     * 获取商品列表
     */
    @POST("/dzc/getuserbycardno")
    Call<ApiDataRsp<UserVipCardDetailDTO>> getuserbycardno(@Header("device_id") String device_id, @Header("sign") String sign, @Body GetUserByCardNoVM getUserByCardNoVM);
}
