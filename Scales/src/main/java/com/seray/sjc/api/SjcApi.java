package com.seray.sjc.api;

import com.seray.sjc.api.request.GetUserByCardNoVM;
import com.seray.sjc.api.request.RequestHeartBeatVM;
import com.seray.sjc.api.request.RequestOrderVM;
import com.seray.sjc.api.request.RequestRegisterVM;
import com.seray.sjc.api.request.RequestWatcherAlertVM;
import com.seray.sjc.api.result.HeartBeatDeviceDzcDTO;
import com.seray.sjc.api.result.DeviceRegisterDTO;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.ProductDZCDTO;
import com.seray.sjc.api.result.UserVipCardDetailDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

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
    Call<ApiDataRsp<HeartBeatDeviceDzcDTO>> heart_beat(@Body RequestHeartBeatVM requestHeartBeatVM, @Header("device_id") String device_id);

    /**
     * 获取商品列表
     */
    @POST("/dzc/pro_find")
    Call<ApiDataRsp<List<ProductDZCDTO>>> pro_find(@Header("device_id") String device_id, @Header("sign") String sign);

    /**
     * 创建订单
     */
    @POST("/dzc/order_create")
    Call<ApiDataRsp<String>> order_create(@Header("device_id") String device_id, @Header("sign") String sign, @Body RequestOrderVM vm);

    /**
     * 获取用户信息
     */
    @POST("/dzc/getuserbycardno")
    Call<ApiDataRsp<UserVipCardDetailDTO>> getuserbycardno(@Header("device_id") String device_id, @Header("sign") String sign, @Body GetUserByCardNoVM getUserByCardNoVM);


    /**
     * 哨兵上传异常经营数据
     */
    @POST("/dzc/watcher_alert")
    Call<ApiDataRsp<String>> watcher_alert(@Header("device_id") String device_id, @Header("sign") String sign, @Body RequestWatcherAlertVM vm);


    @GET
    Call<ResponseBody> getAppVersion(@Url String url);


}
