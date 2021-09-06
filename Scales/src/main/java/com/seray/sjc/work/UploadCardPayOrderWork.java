//package com.seray.sjc.work;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.seray.sjc.annotation.UploadStatus;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.util.LogUtil;
//
//import androidx.work.Data;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
///**
// * Author：李程
// * CreateTime：2019/5/8 10:47
// * E-mail：licheng@kedacom.com
// * Describe：世界村预付卡消费记录上传任务
// */
//public class UploadCardPayOrderWork extends Worker {
//
//    public static final String DATA_JSON_KEY = "CardPayOrder";
//
//    public UploadCardPayOrderWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        Data inputData = getInputData();
//
//        String cardPayOrderJsonStr = inputData.getString(DATA_JSON_KEY);
//        if (cardPayOrderJsonStr == null || cardPayOrderJsonStr.isEmpty()) {
//            return Result.failure();
//        }
//
////        try {
////            // 解析订单
////            Gson gson = new Gson();
////            CardPayOrder order = gson.fromJson(cardPayOrderJsonStr, CardPayOrder.class);
////
////            String transOrderCode = order.getTransOrderCode();
////
////            LLog.i("预付卡消费记录上传开始！" + transOrderCode);
////
////            // 提交订单
////            Response<ApiDataRsp> response = HttpServicesFactory.getHttpServiceApi()
////                    .postCardOrder(order)
////                    .execute();
////
////            if (response.isSuccessful()) {
////                ApiDataRsp body = response.body();
////                if (body != null) {
////                    if (body.success) {
////                        LLog.i("预付卡消费记录上传成功！" + transOrderCode);
////                        // 更新本地订单上传状态
////                        updateOrderUploadStatus(transOrderCode);
////                        LLog.i("预付卡消费记录上传状态更新成功！" + transOrderCode);
////                        return Result.success();
////                    } else {
////                        String code = body.code;
////                        if (code.equals("0001") || code.equals("0002")) {
////                            // 对已明确失败的订单 取消尝试重新提交 尽可能的减少任务栈的堆积
////                            return Result.failure();
////                        }
////                    }
////                }
////            }
////        } catch (IOException e) {
////            LogUtil.e("预付卡消费记录异常！");
////            return Result.failure();
////        }
//        LogUtil.e("预付卡消费记录上传失败！");
//        return Result.failure();
//    }
//
//    private void updateOrderUploadStatus(String transOrderCode) {
//        CardOrderDao cardOrderDao = AppDatabase.getInstance().getCardOrderDao();
//        cardOrderDao.updateUploadStatus(transOrderCode, UploadStatus.SUCCESS);
//    }
//}
