//package com.seray.sjc.work;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//
//import com.seray.instance.ResultData;
//import com.seray.sjc.api.net.HttpServicesFactory;
//import com.seray.sjc.api.request.RequestHeartBeatVM;
//import com.seray.sjc.api.result.ApiDataRsp;
//import com.seray.sjc.api.result.HeartBeatDeviceDzcDTO;
//import com.seray.util.HardwareNetwork;
//import com.seray.util.LogUtil;
//
//import androidx.work.Data;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//
///**
// * 心跳包任务
// */
//public class UploadHeartWork extends Worker {
//
//    private Context context;
//
//    public UploadHeartWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        this.context = context;
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        try {
//            ResultData resultData = HardwareNetwork.getHeartBeatReq(context);
//            if (!resultData.isSuccess()) {
//                LogUtil.i(resultData.toString());
//                return Result.failure();
//            }
//            RequestHeartBeatVM requestHeartBeatVM = (RequestHeartBeatVM) resultData.getMsg();
//            Call<ApiDataRsp<HeartBeatDeviceDzcDTO>> request = HttpServicesFactory.getHttpServiceApi()
//                    .heart_beat(requestHeartBeatVM);
//            LogUtil.i("----request.request().url()-->" + request.request().url() + "----vm->" + requestHeartBeatVM.toString());
//            request.enqueue(new Callback<ApiDataRsp<HeartBeatDeviceDzcDTO>>() {
//                @Override
//                public void onResponse(Call<ApiDataRsp<HeartBeatDeviceDzcDTO>> call, Response<ApiDataRsp<HeartBeatDeviceDzcDTO>> response) {
//                    LogUtil.i("-----response.toString(------->" + response.toString());
//                    if (!response.isSuccessful()) {
//                        LogUtil.i("请求失败：" + response.toString());
//                        return;
//                    }
//                    ApiDataRsp apiDataRsp = response.body();
//                    if (apiDataRsp == null) {
//                        LogUtil.i("请求返回的body为null");
//                        return;
//                    }
//
//                    LogUtil.i("----UploadHeartWork----->" + apiDataRsp.toString());
//                    if (!apiDataRsp.getSuccess()) {
//                        LogUtil.i(apiDataRsp.getCode() + "-" + apiDataRsp.getError_msg());
//                        return;
//                    }
//                    HeartBeatDeviceDzcDTO heartBeatDeviceDzcDTO = (HeartBeatDeviceDzcDTO) apiDataRsp.getMsgs();
//                    if ("sync_products".equals(heartBeatDeviceDzcDTO.getOpreate_info())) {
//                        //执行更新商品操作
//                        LogUtil.i("开始更新商品！！！！！");
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ApiDataRsp<HeartBeatDeviceDzcDTO>> call, Throwable t) {
//                    LogUtil.e("---UploadHeartWork--onFailure()------>" + (TextUtils.isEmpty(t.getMessage()) ? "心跳包失败" : "心跳包失败::" + t.getMessage()));
//                }
//            });
//        } catch (Exception e) {
//            LogUtil.e("心跳包失败------------》" + e.getMessage());
//            return Result.failure();
//        }
//        return Result.success();
//    }
//}
