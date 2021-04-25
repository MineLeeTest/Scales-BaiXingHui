//package com.seray.sjc.pay;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.media.AudioManager;
//import android.media.SoundPool;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.Editable;
//import android.text.TextUtils;
//import android.text.TextWatcher;
//import android.util.TypedValue;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.bumptech.glide.load.resource.drawable.GlideDrawable;
//import com.bumptech.glide.request.RequestListener;
//import com.bumptech.glide.request.target.Target;
//import com.google.gson.Gson;
//import com.google.zxing.WriterException;
//import com.seray.cache.CacheHelper;
//import com.seray.scales.App;
//import com.seray.scales.BaseActivity;
//import com.seray.scales.R;
//import com.seray.sjc.AppExecutors;
//import com.seray.sjc.SjcConfig;
//import com.seray.sjc.annotation.DisplayType;
//import com.seray.sjc.api.net.HttpServicesFactory;
//import com.seray.sjc.api.result.ApiDataRsp;
//import com.seray.sjc.api.result.DownloadQRPayUrlRsp;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.sjc.poster.DisplayPoster;
//import com.seray.sjc.util.SignUtils;
//import com.seray.sjc.util.SjcUtil;
//import com.seray.util.FileHelp;
//import com.seray.util.LogUtil;
//import com.seray.util.NumFormatUtil;
//import com.wang.avi.AVLoadingIndicatorView;
//
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.util.HashMap;
//import java.util.List;
//
//import androidx.work.Data;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
///**
// * 支付页面
// */
//public class SjcPayActivity extends BaseActivity implements View.OnClickListener {
//
//    //支付结果延迟
//    private static final long REQ_PAY_RESULT_DELAY = 2000;
//    @BindView(R.id.qc_code)
//    ImageView mQcCode;
//    @BindView(R.id.pay_layout)
//    LinearLayout mPayLayout;
//    @BindView(R.id.progress_bar)
//    AVLoadingIndicatorView mProgressBar;
//    @BindView(R.id.payfor_tv1_2)
//    TextView mPayAmount;
//    @BindView(R.id.pay_type_name)
//    TextView mPayTypeName;
//    @BindView(R.id.payfor_et)
//    TextView mInputAmountView;
//    @BindView(R.id.payfor_tv3_2)
//    TextView mFreeAmountView;
//    @BindView(R.id.cancel_pay)
//    Button mCancelPay;
//    @BindView(R.id.pay_success)
//    Button mSuccessPay;
//    @BindView(R.id.pay_type_layout)
//    LinearLayout mPayTypeLayout;
//
//    // 是否点击了取消二维码支付
//    boolean cancelQCPay = false;
//
//    private Handler mHandler = new Handler();
//
////    private OrderInfo orderInfo;//订单信息
//
//    private String mFromActivity;// 来源 区分单笔和累计
//    private Runnable reqPayResRunnable = this::getPayCodePayResult;
//
//    // 当前使用的支付方式
////    private PayTypeInfo payType = new PayTypeInfo("0101", "现金");
//
//    private String rawTransOrderCode; // 原始订单号
//
//    private boolean staticQRPay;// 是否是静态二维码支付
//
//    private boolean payResultQuerying;// 正在查询支付结果
//
//    private int reqPayCount; // 请求支付次数
//
//    private SoundPool soundPool;
//    private int paySuccessSound = -1; // 支付成功语音
//    private int maxVolume = -1; // 系统最大语音
//    private int currentVolume = -1; // 系统当前语音
//    private AudioManager audioManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_pay_for_order);
//        EventBus.getDefault().register(this);
//        ButterKnife.bind(this);
////        if (CacheHelper.PayTypeInfoList == null || CacheHelper.PayTypeInfoList.isEmpty()) {
////            showMessage("系统数据异常，请同步系统数据后再试");
////            delayClose();
////            return;
////        }
//        mPayLayout.setOnClickListener(v -> {
//        }); // 消费点击事件
//        initData();
//        DisplayPoster.notifyDisplaySyncShow();
////        setDisplaySyncShow();
//    }
//
//    private void initData() {
//        Intent intent = getIntent();
//        mFromActivity = intent.getStringExtra(SjcConfig.KEY_CLEAR_ORDER_ACTIVITY);
//        Serializable serializableExtra = intent.getSerializableExtra(OrderInfo.class.getSimpleName());
//        if (serializableExtra != null) {
//            orderInfo = (OrderInfo) serializableExtra;
//        } else {
//            delayClose();
//            return;
//        }
//        // 初始化音量及播放语音
//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if (audioManager != null) {
//            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
//            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
//        }
//        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
//        soundPool.load(this, R.raw.pay_suceesee, 1);
//        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> paySuccessSound = sampleId);
//
//        SjcSubtotal subtotal = orderInfo.getSjcSubtotal();
//        // 计算支付金额
//        this.rawTransOrderCode = subtotal.getTransOrderCode();
//        BigDecimal transAmt = subtotal.getTransAmt();
//        mPayAmount.setText(String.valueOf(transAmt.doubleValue()));
//
//        mInputAmountView.setText(String.valueOf(transAmt.doubleValue()));
//
//        for (PayTypeInfo pt : CacheHelper.PayTypeInfoList) {
//            mPayTypeLayout.addView(generatePayButton(pt));
//        }
////        inputListener(mInputAmountView);
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//
//        String payTypeId = (String) v.getTag();
//        if (TextUtils.isEmpty(payTypeId))
//            return;
//
//        SjcSubtotal subtotal = this.orderInfo.getSjcSubtotal();
//
//        // 修改订单号
//        subtotal.setTransOrderCode(this.reqPayCount > 0 ? (this.rawTransOrderCode + "_" + this.reqPayCount) : this.rawTransOrderCode);
//
//        if (SjcSubtotal.BASE_CASH_PAY_TYPE.equals(payTypeId)) {
//            subtotal.setPayStatus(2);
//            insertOrderInfo(true);
//            // 打印订单
////            printOrderInfo();
//            delayClose();
//            return;
//        }
//
//        for (PayTypeInfo payType : CacheHelper.PayTypeInfoList) {
//            if (payType.getPayType().equals(payTypeId)) {
//                this.payType = payType;
//                subtotal.setPayType(this.payType.getPayType());
//                this.mPayTypeName.setText(this.payType.getPayName());
//                if (!TextUtils.isEmpty(payType.getApiPriKey())) {
//                    // 聚合支付
//                    this.staticQRPay = false;
//                    uniteQRPayType();
//                } else if (!TextUtils.isEmpty(payType.getInterfaceUrl())) {
//                    // 静态码支付
//                    this.staticQRPay = true;
//                    staticQRPayType(payType.getInterfaceUrl());
//                } else if (!TextUtils.isEmpty(payType.getCardClass())) {
//                    // 预付卡支付
//                    BigDecimal transAmt = subtotal.getTransAmt();
//                    String transOrderCode = subtotal.getTransOrderCode();
//                    CpuCardManagerDialog dialog = CpuCardManagerDialog.getInstance(transOrderCode, transAmt);
//                    dialog.show(getSupportFragmentManager(), "CpuCardDialog");
//                } else {
//                    // 未知的支付方式
//                    showMessage("该支付方式不可用，请使用其他支付方式");
//                }
//                return;
//            }
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_DOWN) {
//            super.onClick(null);
//            String txt = mInputAmountView.getText().toString();
//            if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
//                int i = txt.indexOf(".");
//                if (i < 0 || i > txt.length() - 3) {
//                    txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
//                }
//            } else if (keyCode == KeyEvent.KEYCODE_E) {
//                // 点
//                if (!txt.contains("."))
//                    txt += ".";
//            } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
//                // 退格
//                if (!txt.isEmpty()) {
//                    txt = txt.substring(0, txt.length() - 1);
//                }
//            } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
//                // 一键清除
//                txt = "";
//            } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
//                return true;
//            } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
//                int visibility = mPayLayout.getVisibility();
//                if (visibility == View.VISIBLE) {
//                    cancelPay();
//                    return true;
//                } else {
//                    notifyDisplayService();
//                    finish();
//                    return false;
//                }
//            }
//            mInputAmountView.setText(txt.trim());
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//
//    @OnClick(R.id.back)
//    public void onClickBack() {
//        super.onClick(null);
//        notifyDisplayService();
//        finish();
//    }
//
//    // 生成支付按钮
//    private Button generatePayButton(PayTypeInfo payType) {
//        Button btn = new Button(this);
//        btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
//        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
//        btn.setTextColor(getResources().getColor(R.color.black1));
//        btn.setBackground(getResources().getDrawable(R.drawable.bg_btn_white));
//        btn.setText(payType.getPayName());
//        btn.setPadding(0, 10, 0, 10);
//        ((LinearLayout.LayoutParams) btn.getLayoutParams()).topMargin = 30;
//        btn.setTag(payType.getPayType());
//        btn.setOnClickListener(this);
//        return btn;
//    }
//
//    // 聚合二维码支付
//    public void uniteQRPayType() {
//        // 等待生成二维码
//        mPayLayout.setVisibility(View.VISIBLE);
//        mCancelPay.setVisibility(View.INVISIBLE);
//        mSuccessPay.setVisibility(View.GONE);
//        mProgressBar.setVisibility(View.VISIBLE);
//        mQcCode.setVisibility(View.INVISIBLE);
//        getPayCodeUrl();
//    }
//
//    // 静态二维码支付
//    public void staticQRPayType(String imgUrl) {
//        String qRUrl = CacheHelper.ResourceBaseUrl + imgUrl;
//        mPayLayout.setVisibility(View.VISIBLE);
//        mProgressBar.setVisibility(View.VISIBLE);
//        Glide.with(this)
//                .load(qRUrl)
//                // 跳过磁盘缓存和内存缓存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true)
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target,
//                                               boolean isFirstResource) {
//                        mPayLayout.setVisibility(View.GONE);
//                        showMessage("获取二维码失败");
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model,
//                                                   Target<GlideDrawable> target, boolean isFromMemoryCache,
//                                                   boolean isFirstResource) {
//                        mQcCode.setVisibility(View.VISIBLE);
//                        mSuccessPay.setVisibility(View.VISIBLE);
//                        mCancelPay.setVisibility(View.VISIBLE);
//                        mProgressBar.setVisibility(View.GONE);
//                        reqPayCount++; // 订单号+1
//                        insertOrderInfo(false); // 插入订单信息
//                        return false;
//                    }
//                })
//                .into(mQcCode);
//
//    }
//
//    // 取消聚合聚合二维码支付或者静态码支付
//    @OnClick(R.id.cancel_pay)
//    public void cancelPay() {
//        super.onClick(null);
//        if (staticQRPay) { // 静态二维码支付取消
//            cancelQCPaySuccess();
//        } else { // 动态二维码支付取消
//            if (payResultQuerying) { // 正在请求支付结果
//                mCancelPay.setVisibility(View.INVISIBLE);
//                mProgressBar.setVisibility(View.VISIBLE);
//                mQcCode.setVisibility(View.INVISIBLE);
//                cancelQCPay = true;
//            } else { // 未正在请求支付结果
//                mHandler.removeCallbacks(reqPayResRunnable);
//                cancelQCPay(); // 发送取消支付请求
//            }
//        }
//    }
//
//    // 静态二维码支付完成
//    @OnClick(R.id.pay_success)
//    public void successPay() {
//        super.onClick(null);
//        if (!staticQRPay)
//            return;
//        mPayLayout.setVisibility(View.GONE);
//        // 更新订单信息
//        updateOrderInfo(2);
//        // 打印订单
////        printOrderInfo();
//        delayClose();
//    }
//
//    /**
//     * 预付卡支付成功
//     * 仅在刷卡支付成功时回调
//     * 执行订单保存即可
//     *
//     * @param cardPayOrder 预付卡订单报文 用于打印报文单据
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void receiveCardPaySuccessMessage(CardPayOrder cardPayOrder) {
//        // 播放支付成功语音
//        playPaySuccessSound();
//        // 更新订单支付状态
//        SjcSubtotal subtotal = this.orderInfo.getSjcSubtotal();
//        subtotal.setPayStatus(2);
//        // 保存并提交订单
//        insertOrderInfo(true);
//        // 组装报文信息 用于订单打印
//        this.orderInfo.setTag(cardPayOrder);
//        // 打印订单
////        printOrderInfo();
//        delayClose();
//    }
//
//    private void delayClose() {
//        notifyDisplayService();
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (App.activityList.size() > 1) {
//                    for (int i = 1; i < App.activityList.size(); i++) {
//                        App.activityList.get(i).finish();
//                    }
//                }
//            }
//        }, 800);
//    }
//
//    private void notifyDisplayService() {
//        if (CacheHelper.isOpenBackDisplay) {
//            DisplayPoster.notifyDisplayAsyncShow(DisplayType.DISPLAY_WEIGHT);
//        } else {
//            DisplayPoster.notifyDisplayAsyncShow(DisplayType.DISPLAY_AD);
//        }
//    }
//
//    /**
//     * 查询二维码支付支付结果
//     */
//    private void getPayCodePayResult() {
//        payResultQuerying = true; // 开始请求支付结果
//        ControlQRPayReq req = new ControlQRPayReq();
//        req.termId = CacheHelper.device_id;
//        LogUtil.i("getPayCodePayResult", "termId = " + req.termId);
//        req.transOrderCode = this.orderInfo.getSjcSubtotal().getTransOrderCode();
//        LogUtil.i("getPayCodePayResult", "transOrderCode = " + req.transOrderCode);
//        HashMap<String, String> params = new HashMap<>();
//        params.put("termId", req.termId);
//        params.put("transOrderCode", req.transOrderCode);
//
//        try {
//            req.signMsg = SignUtils.sign(params, null, payType.getApiPriKey());
//            LogUtil.i("getPayCodePayResult", "signMsg = " + req.signMsg);
//        } catch (IOException e) {
//            LogUtil.e(e.getMessage());
//            generateQRCodeFail("计算数据签名失败，请重试。");
//            // 继续查询
//            mHandler.postDelayed(reqPayResRunnable, REQ_PAY_RESULT_DELAY);
//            return;
//        }
//
//        HttpServicesFactory.getHttpServiceApi().queryQRPayResult(payType.getInterfaceUrl() + "/queryPay", req)
//                .enqueue(new Callback<ControlQRPayResult>() {
//                    @Override
//                    public void onResponse(Call<ControlQRPayResult> call,
//                                           Response<ControlQRPayResult> response) {
//
//                        payResultQuerying = false;
//
//                        ControlQRPayResult body = response.body();
//
//                        LogUtil.i("queryQRPayResult", "rsp = " + (body == null ? "null" : body.toString()));
//
//                        if (body != null && body.success && (body.msgs != null && body.msgs.payStatus.equals("1"))) {
//                            cancelQCPay = false;
//                            QCCodePaySuccess();
//                        } else {
//                            //已经取消支付了
//                            if (cancelQCPay) {
//                                cancelQCPay = false;
//                                cancelQCPay();
//                            } else {
//                                // 继续请求支付结果
//                                mHandler.postDelayed(reqPayResRunnable, REQ_PAY_RESULT_DELAY);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ControlQRPayResult> call, Throwable t) {
//                        // 网络出错时，依然可以取消支付，防止由于网络问题导致界面卡在取消支付界面
//                        payResultQuerying = false;
//                        showMessage(R.string.network_error);
//                        if (cancelQCPay) {
//                            cancelQCPay = false;
//                            cancelQCPay();
//                        } else {
//                            // 继续请求支付结果
//                            mHandler.postDelayed(reqPayResRunnable, REQ_PAY_RESULT_DELAY);
//                        }
//                    }
//                });
//    }
//
//    // 二维码支付成功
//    private void QCCodePaySuccess() {
//        // 播放支付成功语音
//        playPaySuccessSound();
//        mPayLayout.setVisibility(View.GONE);
//        // 更新订单信息
//        updateOrderInfo(2);
//        // 打印订单
////        printOrderInfo();
//        delayClose();
//    }
//
//    //取消二维码支付
//    private void cancelQCPay() {
//        // 发送取消支付请求
//        ControlQRPayReq req = new ControlQRPayReq();
//        req.termId = CacheHelper.device_id;
//        req.transOrderCode = this.orderInfo.getSjcSubtotal().getTransOrderCode();
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("termId", req.termId);
//        params.put("transOrderCode", req.transOrderCode);
//
//        try {
//            req.signMsg = SignUtils.sign(params, null, payType.getApiPriKey());
//        } catch (IOException e) {
//            LogUtil.e(e.getMessage());
//            generateQRCodeFail("计算数据签名失败，请重试。");
//            return;
//        }
//
//        HttpServicesFactory.getHttpServiceApi()
//                .cancelQRPay(payType.getInterfaceUrl() + "/cancelPay", req)
//                .enqueue(new Callback<ControlQRPayResult>() {
//                    @Override
//                    public void onResponse(Call<ControlQRPayResult> call, Response<ControlQRPayResult> response) {
//                        if (response.isSuccessful()) {
//                            ControlQRPayResult body = response.body();
//                            if (body != null && body.success) {
//                                // 取消支付成功
//                                cancelQCPaySuccess();
//                                return;
//                            }
//                        }
//                        // 取消支付失败
//                        cancelQCPayFail();
//                    }
//
//                    @Override
//                    public void onFailure(Call<ControlQRPayResult> call, Throwable t) {
//                        cancelQCPayFail();
//                    }
//                });
//    }
//
//    // 二维码支付取消操作成功 包含静态二维码取消支付
//    private void cancelQCPaySuccess() {
//        mPayLayout.setVisibility(View.GONE);
//        // 明确结果时 删除缓存数据和数据库订单
//        String transOrderCode = this.orderInfo.getSjcSubtotal().getTransOrderCode();
//        deleteOrder(transOrderCode);
//    }
//
//    // 二维码支付取消操作失败
//    private void cancelQCPayFail() {
//        mPayLayout.setVisibility(View.GONE);
//        showMessage("支付取消失败");
//        updateOrderInfo(-1); // 更新订单信息
//    }
//
//    /**
//     * 请求生成聚合二维码
//     */
//    private void getPayCodeUrl() {
//        DownloadQRPayUrlReq qrPayReq = new DownloadQRPayUrlReq();
//        qrPayReq.termId = CacheHelper.device_id;
//        LogUtil.i("getPayCodeUrl", "termId = " + qrPayReq.termId);
//        qrPayReq.transOrderCode = this.orderInfo.getSjcSubtotal().getTransOrderCode();
//        LogUtil.i("getPayCodeUrl", "transOrderCode = " + qrPayReq.transOrderCode);
//        qrPayReq.payMent = this.orderInfo.getSjcSubtotal().getTransAmt();
//        LogUtil.i("getPayCodeUrl", "payMent = " + qrPayReq.payMent.toString());
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("termId", CacheHelper.device_id);
//        params.put("transOrderCode", qrPayReq.transOrderCode);
//        params.put("payMent", String.valueOf(qrPayReq.payMent.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()));
//
//        String apiPriKey = payType.getApiPriKey();
//        LogUtil.i("getPayCodeUrl", "apiPriKey = " + apiPriKey);
//
//        try {
//            qrPayReq.signMsg = SignUtils.sign(params, null, apiPriKey);
//            LogUtil.i("getPayCodeUrl", "signMsg = " + qrPayReq.signMsg);
//        } catch (IOException e) {
//            LogUtil.e(e.getMessage());
//            generateQRCodeFail("计算数据签名失败，请重试。");
//            return;
//        }
//
//        HttpServicesFactory.getHttpServiceApi().downloadQRPayUrl(payType.getInterfaceUrl() + "/callPay", qrPayReq)
//                .enqueue(new Callback<ApiDataRsp<DownloadQRPayUrlRsp>>() {
//                    @Override
//                    public void onResponse(Call<ApiDataRsp<DownloadQRPayUrlRsp>> call, Response<ApiDataRsp<DownloadQRPayUrlRsp>> response) {
//                        if (response.body() == null) {
//                            generateQRCodeFail(getString(R.string.req_qr_code_error_network));
//                            LogUtil.e("downloadQRPayUrl", "body is null");
//                            return;
//                        }
//
//                        if (!response.body().success) {
//                            generateQRCodeFail(response.body().error_msg);
//                            LogUtil.e("downloadQRPayUrl", response.body().error_msg);
//                            return;
//                        }
//                        LogUtil.i("downloadQRPayUrl", response.body().msgs.toString());
//
//                        reqPayCount++; // 订单号+1
//                        showQrCode(response.body().msgs);
//                    }
//
//                    @Override
//                    public void onFailure(Call<ApiDataRsp<DownloadQRPayUrlRsp>> call, Throwable t) {
//                        generateQRCodeFail(getString(R.string.req_qr_code_error_network));
//                        LogUtil.e("downloadQRPayUrl", t.getMessage());
//                    }
//                });
//    }
//
//    /**
//     * 生成二维码失败
//     *
//     * @param err 错误描述
//     */
//    private void generateQRCodeFail(String err) {
//        // 隐藏支付二维码 显示错误信息
//        mPayLayout.setVisibility(View.GONE);
//        showMessage(err);
//    }
//
//    /**
//     * 生成二维码成功
//     *
//     * @param bitmap 二维码
//     */
//    private void generateQRCodeSuccess(Bitmap bitmap) {
//        mQcCode.setImageBitmap(bitmap);
//        mQcCode.setVisibility(View.VISIBLE);
//        mProgressBar.setVisibility(View.INVISIBLE);
//        mCancelPay.setVisibility(View.VISIBLE);
//        // 插入订单信息
//        insertOrderInfo(false);
//        // 保存订单临时状态
//        saveCacheOrderInfo();
//        // 2s 后开始请求支付结果
//        mHandler.postDelayed(reqPayResRunnable, REQ_PAY_RESULT_DELAY);
//    }
//
//    // 显示支付二维码
//    public void showQrCode(DownloadQRPayUrlRsp qrPay) {
//        int qrCodeSize = SjcUtil.dp2px(this, 260);
//        new Thread(() -> {
//            Bitmap qrCodeBitmap = null;
//            try { //生成二维码
//                qrCodeBitmap = SjcUtil.createQRCode(qrPay.qrUrl, qrCodeSize);
//            } catch (WriterException e) {
//                e.printStackTrace();
//            }
//            Bitmap finalQrCodeBitmap = qrCodeBitmap;
//            runOnUiThread(() -> {
//                if (finalQrCodeBitmap != null) {
//                    generateQRCodeSuccess(finalQrCodeBitmap);
//                } else {
//                    generateQRCodeFail(getString(R.string.req_qr_code_error_generate));
//                }
//            });
//        }).start();
//    }
//
////    /**
////     * 订单打印
////     */
////    private void printOrderInfo() {
////        mHandler.post(() -> mCustomPrinter.printOrder(orderInfo, () -> {
////            // 打印缓存
////            LocalFileTag tag = CacheHelper.addOrderToCache(orderInfo);
////            if (!tag.success()) {
////                showMessage(tag.getContent());
////            }
////            if (mFromActivity != null) {
////                EventBus.getDefault().post(new ClearOrderMsg(mFromActivity));
////            }
////        }));
////    }
//
//    /**
//     * 删除 支付取消（成功）、支付失败等无效订单
//     *
//     * @param transOrderCode 流水号
//     */
//    private void deleteOrder(final String transOrderCode) {
//        AppExecutors.getInstance().insertIO().submit(() -> {
//            // 删除数据库记录
//            SjcSubtotalDao subtotalDao = AppDatabase.getInstance().getSubtotalDao();
//            subtotalDao.delete(transOrderCode);
//            // 删除缓存记录
//            removeCacheOrderInfo();
//        });
//    }
//
//    /**
//     * 更新订单信息
//     *
//     * @param payStatus 支付状态    -1：支付失败 0：支付中 1：支付取消 2：支付成功
//     */
//    private void updateOrderInfo(final int payStatus) {
//        AppExecutors.getInstance().insertIO().submit(() -> {
//            AppDatabase database = AppDatabase.getInstance();
//            SjcSubtotal subtotal = this.orderInfo.getSjcSubtotal();
//            String transOrderCode = subtotal.getTransOrderCode();
//            subtotal.setPayStatus(payStatus);
//            try {
//                database.getSubtotalDao().updatePayStatus(transOrderCode, payStatus);
//            } catch (Exception e) {
//                LogUtil.e(e.getMessage());
//            } finally {
//                if (payStatus == 2) {// 支付成功时 上传订单
//                    removeCacheOrderInfo();
//                    Gson gson = new Gson();
//                    String jsonDataStr = gson.toJson(this.orderInfo);
//                    uploadOrder(jsonDataStr);
//                } else {
//                    saveCacheOrderInfo(); // 更新订单临时信息
//                }
//            }
//        });
//    }
//
//    /**
//     * 插入订单信息
//     */
//    private void insertOrderInfo(final boolean isUpload) {
//        AppExecutors.getInstance().insertIO().submit(() -> {
//            SjcSubtotal subtotal = this.orderInfo.getSjcSubtotal();
//            List<SjcDetail> sjcDetails = this.orderInfo.getSjcDetails();
//            for (SjcDetail detail : sjcDetails) {
//                detail.setTransOrderCode(subtotal.getTransOrderCode());
//            }
//            AppDatabase database = AppDatabase.getInstance();
//            database.beginTransaction();
//            try {
//                database.getSubtotalDao().save(subtotal);
//                database.getDetailDao().save(sjcDetails);
//                database.setTransactionSuccessful();
//                String transOrderCode = subtotal.getTransOrderCode();
//                LogUtil.d("订单插入数据库成功！" + transOrderCode);
//            } catch (Exception e) {
//                LogUtil.e("订单插入数据库失败！" + e.getMessage());
//            } finally {
//                database.endTransaction();
//                if (isUpload) {
//                    // 上传订单
//                    Gson gson = new Gson();
//                    String jsonDataStr = gson.toJson(this.orderInfo);
//                    uploadOrder(jsonDataStr);
//                }
//            }
//        });
//    }
//
//    /**
//     * 创建并提交订单上传任务
//     *
//     * @param jsonDataStr 订单JSON字串
//     */
//    private void uploadOrder(final String jsonDataStr) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                @SuppressLint("RestrictedApi")
//                Data inputData = new Data.Builder()
//                        .put(UploadOrderWork.JSON_DATA_KEY, jsonDataStr)
//                        .build();
//                OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UploadOrderWork.class)
//                        .setInputData(inputData)
//                        .build();
//                WorkManager.getInstance().enqueue(request);
//            }
//        });
//    }
//
//    // 临时保存订单信息
//    private void saveCacheOrderInfo() {
//        FileHelp.writeToOrderInfo(orderInfo);
//    }
//
//    // 删除临时保存的订单信息
//    private void removeCacheOrderInfo() {
//        File file = new File(FileHelp.ORDER_INFO);
//        try {
//            if (file.exists() && file.isFile())
//                if (file.delete()) {
//                    LogUtil.d("缓存订单删除成功！" + this.orderInfo.getSjcSubtotal().getTransOrderCode());
//                } else {
//                    LogUtil.e("缓存订单删除失败！" + this.orderInfo.getSjcSubtotal().getTransOrderCode());
//                }
//        } catch (Exception e) {
//            LogUtil.e("缓存订单删除失败！" + this.orderInfo.getSjcSubtotal().getTransOrderCode() + "\n" + e.getMessage());
//        }
//    }
//
//    private void inputListener(final TextView input) {
//
//        input.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//                BigDecimal transAmt = orderInfo.getSjcSubtotal().getTransAmt();
//                CharSequence s1 = s;
//
//                NumFormatUtil instance = NumFormatUtil.getInstance();
//
//                BigDecimal bigTotalAmount = instance.getDecimalAmount(transAmt == null ? "0.00" :
//
//                        transAmt.toString());
//
//                BigDecimal bigPayAmount;
//
//                BigDecimal bigFreeAmount = BigDecimal.ZERO;
//
//                int index = s1.toString().indexOf(".");
//
//                if (s1.length() > 0) {
//
//                    if (index == 0) {
//                        s1 = "0" + s1;
//                        input.setText(s1);
//                    }
//
//                    bigPayAmount = instance.getDecimalAmount(s1.toString());
//                    bigFreeAmount = bigTotalAmount.subtract(bigPayAmount);
//
//                }
//
//                String payType = SjcPayActivity.this.payType.getPayType();
//                // 现金支付支持输入数值大于总金额
//                if (payType.equals("0101")) {
//                    mFreeAmountView.setText(String.valueOf(bigFreeAmount.negate()));
//                } else { // 银联或会员卡支付不支持输入数值大于总金额
//                    if (bigFreeAmount.compareTo(BigDecimal.ZERO) < 0) {
//                        showMessage(R.string.pay_tips_money_error);
//                        input.setText("");
//                        mFreeAmountView.setText(String.valueOf(bigTotalAmount));
//                    } else {
//                        mFreeAmountView.setText(String.valueOf(bigFreeAmount));
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//    }
//
//    /**
//     * 播放支付成功声音
//     */
//    private void playPaySuccessSound() {
//        try {
//            if (paySuccessSound != -1 && audioManager != null) {
//                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
//                soundPool.play(paySuccessSound, 1, 1, 1, 0, 1);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        if (audioManager != null && currentVolume != -1) // 重置音量
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
//        if (soundPool != null)
//            soundPool.release();
//        if (reqPayResRunnable != null)
//            mHandler.removeCallbacks(reqPayResRunnable);
//        super.onDestroy();
//        EventBus.getDefault().unregister(this);
//    }
//}
