//package com.seray.sjc.work;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//
//import com.seray.sjc.annotation.UploadStatus;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.sjc.entity.order.SjcDetail;
//import com.seray.sjc.entity.order.SjcSubtotal;
//import com.seray.util.LogUtil;
//
//import java.util.List;
//
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
///**
// * Author：李程
// * CreateTime：2019/5/4 11:36
// * E-mail：licheng@kedacom.com
// * Describe：批量订单上传任务
// */
//public class CheckUnUploadWork extends Worker {
//
//    private static final int ONE_TIME_UPLOAD_MAX_COUNT = 50;
//
//    private static final int TIMES = 5;
//
//    private SjcSubtotalDao mSjcSubtotalDao;
//
//    public CheckUnUploadWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//        mSjcSubtotalDao = AppDatabase.getInstance().getSubtotalDao();
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//
//        int currentTime = 0;
//
//        do {
//            // 未上传且支付成功的订单集合
//            List<SjcSubtotal> subtotals = mSjcSubtotalDao.getNoUploadOrdersOneTime(ONE_TIME_UPLOAD_MAX_COUNT);
//            if (subtotals == null || subtotals.isEmpty()) break;
//
//            LLog.i("单次任务实际上传订单数目：" + subtotals.size());
//
//            String[] totalTransOrderCode = new String[subtotals.size()];
//            int offset = 0;
//            // 组装订单明细详情
//            SjcDetailDao detailDao = AppDatabase.getInstance().getDetailDao();
//
//            for (SjcSubtotal subtotal : subtotals) {
//                String transOrderCode = subtotal.getTransOrderCode();
//                totalTransOrderCode[offset++] = transOrderCode;
//                List<SjcDetail> detailList = detailDao.loadByTransOrderCode(transOrderCode);
//                subtotal.setTransItemList(detailList);
//            }
//
////            try {
////                Response<ApiDataRsp<String[]>> response = HttpServicesFactory.getHttpServiceApi()
////                        .postOrders(subtotals)
////                        .execute();
////
////                if (response.isSuccessful()) {
////                    ApiDataRsp<String[]> body = response.body();
////                    if (body != null && body.success) {
////                        String[] errorOrderCodes = body.msgs;
////                        if (errorOrderCodes != null && errorOrderCodes.length > 0) {
////                            LLog.i("批量上传订单部分失败！");
////                        } else {
////                            LLog.i("批量上传订单部分全部成功！");
////                        }
////                        updateOrderUploadStatus(totalTransOrderCode, errorOrderCodes);
////                        LLog.i("批量上传订单上传状态更新成功！");
////                    }
////                }
////            } catch (IOException e) {
////                LogUtil.e("批量上传订单异常！" + e.getMessage());
////            } finally {
////                currentTime++;
////            }
//
//        } while (currentTime < TIMES);
//
//        return Result.success();
//    }
//
//    private void updateOrderUploadStatus(String[] totalOrderCodes, String[] errorOrderCodes) {
//        for (String transOrderCode : totalOrderCodes) {
//            boolean isSuccess = true;
//            if (errorOrderCodes == null || errorOrderCodes.length == 0) {
//                updateOrderUploadStatus(transOrderCode);
//                continue;
//            }
//            for (String errorCode : errorOrderCodes) {
//                if (transOrderCode.equals(errorCode)) {
//                    isSuccess = false;
//                    break;
//                }
//            }
//            if (isSuccess) {
//                updateOrderUploadStatus(transOrderCode);
//            }
//        }
//    }
//
//    private void updateOrderUploadStatus(String transOrderCode) {
//        if (mSjcSubtotalDao == null) {
//            mSjcSubtotalDao = AppDatabase.getInstance().getSubtotalDao();
//        }
//        mSjcSubtotalDao.updateUploadStatus(transOrderCode, UploadStatus.SUCCESS);
//    }
//}
