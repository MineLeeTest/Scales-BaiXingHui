package com.seray.sjc.api;

import com.seray.sjc.api.request.ActivateReq;
import com.seray.sjc.api.request.BusinessReq;
import com.seray.sjc.api.request.ControlQRPayReq;
import com.seray.sjc.api.request.DownloadQRPayUrlReq;
import com.seray.sjc.api.request.ImageRecognizeReq;
import com.seray.sjc.api.request.OpenMachineAlarmReq;
import com.seray.sjc.api.request.TermHeartReq;
import com.seray.sjc.api.result.ActivateRsp;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.BusinessRsp;
import com.seray.sjc.api.result.ControlQRPayResult;
import com.seray.sjc.api.result.DownloadQRPayUrlRsp;
import com.seray.sjc.api.result.RecognizeResult;
import com.seray.sjc.entity.order.SjcSubtotal;
import com.seray.sjc.entity.card.CardPayOrder;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:42
 * E-mail：licheng@kedacom.com
 * Describe：世界村数据对接
 */
public interface SjcApi {

    /**
     * 设备激活
     *
     * @param activateReq 设备激活请求体
     * @return 设备基本参数
     */
    @POST("term_service/activate/termActivate")
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp<ActivateRsp>> activate(@Body ActivateReq activateReq);

    /**
     * 获取业务数据，包含分类、商品、广告图、摊位和市场信息
     *
     * @param businessReq 获取业务数据请求体
     * @return 业务数据
     */
    @POST("term_service/term/syncBaseInfo")
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp<BusinessRsp>> getBusinessData(@Body BusinessReq businessReq);

    /**
     * 上传单笔交易数据
     *
     * @param subtotal 交易小计
     * @return 上传结果
     */
    @POST("term_service/data/receiveTrade")
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp> postOrder(@Body SjcSubtotal subtotal);

    /**
     * 上传多笔交易数据
     *
     * @param subtotals 交易小计
     * @return 上传结果
     */
    @POST("term_service/data/receiveTradeList")
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp<String[]>> postOrders(@Body List<SjcSubtotal> subtotals);

    /**
     * 上传预付卡交易报文
     *
     * @param order 预付卡交易报文
     * @return 上传结果
     */
    @POST("term_service/cardTrans/uploadConsum")
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp> postCardOrder(@Body CardPayOrder order);

    /**
     * 获取聚合支付二维码
     *
     * @param downloadQRPayUrlReq 请求体
     * @return 聚合支付交易流水号和二维码链接
     */
    @POST
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp<DownloadQRPayUrlRsp>> downloadQRPayUrl(
            @Url String url,
            @Body DownloadQRPayUrlReq downloadQRPayUrlReq);

    /**
     * 聚合支付的查询
     *
     * @param controlQRPayReq 请求体
     * @return 支付结果
     */
    @POST
    @Headers("Content-Type:application/json")
    Call<ControlQRPayResult> queryQRPayResult(
            @Url String url,
            @Body ControlQRPayReq controlQRPayReq);

    /**
     * 聚合支付的取消
     *
     * @param controlQRPayReq 请求体
     * @return 取消结果
     */
    @POST
    @Headers("Content-Type:application/json")
    Call<ControlQRPayResult> cancelQRPay(
            @Url String url,
            @Body ControlQRPayReq controlQRPayReq);

    /**
     * 心跳包
     */
    @POST("term_service/termHeartbeat/upload")
    @Headers("Content-Type:application/json")
    Call<Void> uploadTermHeat(@Body TermHeartReq termHeartReq);

    /**
     * 果蔬识别
     *
     * @param request 图像BASE64转码
     * @return 识别结果
     */
    @POST("term_service/image/recoginze")
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp<List<RecognizeResult>>> productImageRecognize(@Body ImageRecognizeReq request);

    /**
     * 拆解警告
     *
     * @param request 告警消息
     */
    @POST("term_service/termAlarm/dismantleAlarm")
    @Headers("Content-Type:application/json")
    Call<ApiDataRsp> postOpenMachineMessage(@Body OpenMachineAlarmReq request);

}
