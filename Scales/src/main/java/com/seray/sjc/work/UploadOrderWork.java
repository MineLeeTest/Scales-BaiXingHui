package com.seray.sjc.work;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.seray.sjc.annotation.TransType;
import com.seray.sjc.annotation.UploadStatus;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.SjcSubtotalDao;
import com.seray.sjc.entity.order.OrderInfo;
import com.seray.sjc.entity.order.SjcSubtotal;
import com.seray.util.LogUtil;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import retrofit2.Response;

/**
 * Author：李程
 * CreateTime：2019/5/3 23:32
 * E-mail：licheng@kedacom.com
 * Describe：世界村订单上传任务
 */
public class UploadOrderWork extends Worker {

    public static final String JSON_DATA_KEY = "BasicOrderInfo";

    public UploadOrderWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data inputData = getInputData();
        String dataJsonStr = inputData.getString(JSON_DATA_KEY);

        if (dataJsonStr == null || dataJsonStr.isEmpty()) {
            return Result.failure();
        }

        try {
            Gson gson = new Gson();
            // 解析订单
            OrderInfo orderInfo = gson.fromJson(dataJsonStr, OrderInfo.class);
            SjcSubtotal subtotal = orderInfo.getSjcSubtotal();

            // 验证支付状态 以防混淆
            int payStatus = subtotal.getPayStatus();
            String transType = subtotal.getTransType();

            if (transType.equals(TransType.FORCE_RECORD) || (transType.equals(TransType.NORMAL) && payStatus == 2)) {

                subtotal.setTransItemList(orderInfo.getSjcDetails());

                String transOrderCode = subtotal.getTransOrderCode();

                LogUtil.i("订单上传开始！" + transOrderCode);

                // 提交订单
                Response<ApiDataRsp> response = HttpServicesFactory.getHttpServiceApi()
                        .postOrder(subtotal)
                        .execute();

                if (response.isSuccessful()) {
                    ApiDataRsp body = response.body();
                    if (body != null) {
                        if (body.isSuccess()) {
                            LogUtil.i("订单上传成功！" + transOrderCode);
                            // 更新本地订单上传状态
                            updateLocalOrder(transOrderCode);
                            LogUtil.i("本地订单上传状态更新成功！" + transOrderCode);
                            return Result.success();
                        } else {
                            String code = body.code;
                            if (code.equals("0001") || code.equals("0002")) {
                                // 对已明确失败的订单 取消尝试重新提交 尽可能的减少任务栈的堆积
                                return Result.failure();
                            }
                        }
                    }
                }
            } else {
                // 直接取消此次任务
                return Result.failure();
            }
        } catch (Exception e) {
            LogUtil.e("订单上传异常！");
            return Result.failure();
        }
        LogUtil.e("订单上传失败！");
        return Result.failure();
    }

    /**
     * 更新本地订单上传状态
     *
     * @param transOrderCode 订单号
     */
    private void updateLocalOrder(String transOrderCode) {
        SjcSubtotalDao subtotalDao = AppDatabase.getInstance().getSubtotalDao();
        subtotalDao.updateUploadStatus(transOrderCode, UploadStatus.SUCCESS);
    }
}
