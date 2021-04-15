//package com.seray.sjc.work;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.seray.machineopenmonitor.MachineJNIUtil;
//import com.seray.cache.CacheHelper;
//import com.seray.sjc.api.net.HttpServicesFactory;
//import com.seray.sjc.api.request.OpenMachineAlarmReq;
//import com.seray.sjc.api.result.ApiDataRsp;
//import com.seray.util.LogUtil;
//import com.seray.util.NumFormatUtil;
//
//import androidx.work.Data;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
///**
// * @author licheng
// * @since 2019/5/28 14:31
// */
//public class CheckBrokenWork extends Worker {
//
//    public static final String KEY_MESSAGE = "Message";
//
//    public CheckBrokenWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        if (CacheHelper.device_id == null || CacheHelper.device_id.isEmpty()) {
//            Data output = new Data.Builder()
//                    .putString(KEY_MESSAGE, "未激活，拆机检测已终止！")
//                    .build();
//            return Result.failure(output);
//        }
//        try {
//            MachineJNIUtil machineJNIUtil = new MachineJNIUtil();
//            int ioValue = machineJNIUtil.getIoValue();
//            if (ioValue == 1) {// 已拆机
//                OpenMachineAlarmReq request = new OpenMachineAlarmReq(
//                        CacheHelper.device_id,
//                        NumFormatUtil.getDateDetail());
//                HttpServicesFactory.getHttpServiceApi()
//                        .postOpenMachineMessage(request)
//                        .enqueue(new Callback<ApiDataRsp>() {
//                            @Override
//                            public void onResponse(Call<ApiDataRsp> call, Response<ApiDataRsp> response) {
//                                if (response.isSuccessful()) {
//                                    ApiDataRsp body = response.body();
//                                    if (body != null && body.success) {
//                                        LogUtil.i("拆机告警上传成功！");
//                                        return;
//                                    }
//                                }
//                                LogUtil.e("拆机告警上传失败！" + response.code());
//                            }
//
//                            @Override
//                            public void onFailure(Call<ApiDataRsp> call, Throwable t) {
//                                LogUtil.e("拆机告警上传失败！" + t.getMessage());
//                            }
//                        });
//            }
//        } catch (Exception e) {
//            LogUtil.e(e.getMessage());
//        }
//        return Result.success();
//    }
//}