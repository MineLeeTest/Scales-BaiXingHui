package com.seray.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.message.HeartBeatMsg;
import com.seray.sjc.api.SjcApi;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.RequestHeartBeatVM;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.HeartBeatDeviceDzcDTO;
import com.seray.sjc.api.result.ProductDZCDTO;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.entity.device.ProductADB;
import com.seray.util.AESTools;
import com.seray.util.HardwareNetwork;
import com.seray.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class HeartBeatService extends Service {

    private ScheduledExecutorService batteryThread = null;

    private HeartBeatRunnable mRunnable = null;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        batteryThread = Executors.newScheduledThreadPool(1);
        context = this;
        mRunnable = new HeartBeatRunnable();
    }

    private final long TIMES = 30;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        batteryThread.scheduleAtFixedRate(mRunnable, 1, TIMES, TimeUnit.MINUTES);
//        batteryThread.scheduleAtFixedRate(mRunnable, 60, TIMES, TimeUnit.SECONDS);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        batteryThread.shutdownNow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private class HeartBeatRunnable implements Runnable {
        @Override
        public void run() {
            ResultData resultData = getHeartBeat();
            EventBus.getDefault().post(new HeartBeatMsg(resultData));
        }
    }


    private ResultData getHeartBeat() {
        ResultData resultData = new ResultData();
        try {
            resultData = HardwareNetwork.getHeartBeatReq(context);
            RequestHeartBeatVM requestHeartBeatVM = (RequestHeartBeatVM) resultData.getMsg();
            requestHeartBeatVM.setBattery_info(Misc.newInstance().readBattery() + "");

            Call<ApiDataRsp<HeartBeatDeviceDzcDTO>> request = HttpServicesFactory.getHttpServiceApi()
                    .heart_beat(requestHeartBeatVM, requestHeartBeatVM.getDevice_dzc_id() + "");
            LogUtil.i("----request-->" + request.request().url() + "----vm->" + requestHeartBeatVM.toString());
            Response<ApiDataRsp<HeartBeatDeviceDzcDTO>> response = request.execute();
            LogUtil.i("----response----->");

            if (!response.isSuccessful()) {
                LogUtil.i("请求失败：" + response.toString());
                resultData.setRetMsg("5013", response.toString());
                return resultData;
            }
            ApiDataRsp apiDataRsp = response.body();
            LogUtil.i("----response---data-->" + apiDataRsp.toString());

            if (!apiDataRsp.getSuccess()) {
                LogUtil.i(apiDataRsp.getCode() + "-" + apiDataRsp.getError_msg());
                resultData.setRetMsg("5015", response.toString());
                return resultData;
            }
            HeartBeatDeviceDzcDTO heartBeatDeviceDzcDTO = (HeartBeatDeviceDzcDTO) apiDataRsp.getMsgs();
            LogUtil.i("----response---data-->" + heartBeatDeviceDzcDTO.toString());
            if ("sync_products".equals(heartBeatDeviceDzcDTO.getOpreate_info()) && !CacheHelper.data_version.equals(heartBeatDeviceDzcDTO.getData_version())) {
                //执行更新商品操作
                LogUtil.i("开始更新商品！！！！！");
                resultData = getProsNow();
                if (resultData.isSuccess()) {
                    //更新成功后，标记数据版本
                    CacheHelper.updateHeartBeat(heartBeatDeviceDzcDTO);
                    resultData.setRetMsg("90000", "发送心跳数据成功，更新商品成功！");
                    return resultData;
                } else {
                    LogUtil.e("--getProsNow()--failed----->" + resultData.toString());
                    resultData.setRetMsg("90111", "发送心跳数据成功，更新商品失败！");
                    return resultData;
                }
            }
            resultData.setTrueMsg("发送心跳数据成功");
            return resultData;
        } catch (Exception e) {
            LogUtil.e("心跳包失败------------》" + e.getMessage());
            return resultData.setRetMsg(resultData, "5012", "发送心跳数据包异常：" + e.getMessage());
        }
    }


    /**
     * 获取商品列表
     */
    private ResultData getProsNow() {
        //获取电子秤硬件信息
        ResultData resultData = new ResultData();
        resultData = AESTools.encrypt(resultData, CacheHelper.device_aes_key, System.currentTimeMillis() + "," + CacheHelper.device_id);
        if (!resultData.isSuccess()) {
            return resultData;
        }
        String sign = resultData.getMsg() + "";
        //发起注册请求
        SjcApi sjcApi = HttpServicesFactory.getHttpServiceApi();
        Call<ApiDataRsp<List<ProductDZCDTO>>> request = sjcApi.pro_find(CacheHelper.device_id + "", sign);
        try {
            Response<ApiDataRsp<List<ProductDZCDTO>>> response = request.execute();
            if (!response.isSuccessful()) {
                return resultData.setRetMsg(resultData, "6001", "请求失败：" + response.toString());
            }

            ApiDataRsp apiDataRsp = response.body();
            if (apiDataRsp == null) {
                return resultData.setRetMsg(resultData, "6001", "请求返回的body为null");
            }

            if (!apiDataRsp.getSuccess()) {
                return resultData.setRetMsg(resultData, apiDataRsp.getCode(), apiDataRsp.getError_msg());
            }

            List<ProductDZCDTO> productDZCDTOS = (List<ProductDZCDTO>) apiDataRsp.getMsgs();
            if (!CacheHelper.updatePros(productDZCDTOS)) {
                return resultData.setRetMsg(resultData, "6002", "将商品列表信息存储到本地失败！");
            }

//            List<ProductADB> list = AppDatabase.getInstance().getProductDao().loadAll();
            resultData.setTrueMsg("更新商品成功！");
            return resultData;
        } catch (IOException e) {
            return resultData.setRetMsg(resultData, "6044", "更新商品异常：" + e.getMessage());
        }
    }
}
