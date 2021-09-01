package com.seray.scales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seray.cache.AppConfig;
import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.inter.BackDisplayBase;
import com.seray.inter.LandBackDisplay;
import com.seray.inter.TableBackDisplay;
import com.seray.log.LLog;
import com.seray.message.BatteryMsg;
import com.seray.message.ClearCartMsg;
import com.seray.message.HeartBeatMsg;
import com.seray.message.ShutdownMsg;
import com.seray.service.DisplayService;
import com.seray.service.HeartBeatService;
import com.seray.service.ShutdownService;
import com.seray.sjc.annotation.DisplayType;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.GetUserByCardNoVM;
import com.seray.sjc.api.request.ProductCart;
import com.seray.sjc.api.request.RequestOrderVM;
import com.seray.sjc.api.request.RequestWatcherAlertVM;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.UserVipCardDetailDTO;
import com.seray.sjc.entity.device.ProductADB;
import com.seray.util.AESTools;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomInputTareDialog;
import com.seray.view.CustomTipDialog;
import com.skyworth.splicing.ICCardSerialPortUtil;
import com.tools.ByteUtil;
import com.tscale.scalelib.jniscale.JNIScale;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.convert.impl.ByteArrayConverter;
import cn.hutool.core.util.HexUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScaleActivity extends BaseActivity implements ICCardSerialPortUtil.OnDataReceiveListener {

    private TextView mTimeView,//日期时间
            mTvTare,//商品的皮重
            mTvWeight, //商品的重量
            mTvUnitPrice, //商品单价
            mTvSubtotal,//商品总价
            tvProName,//商品名称
            tvAllPrice,//购物车总价
            buyer,//买家
            seller,//卖家
            mMaxUnitView;//标题显示
    private Button pay;//付款按钮

    private HandlerScale handlerScale = new HandlerScale(new WeakReference<>(ScaleActivity.this));
    private HandlerICCard handlerICCard = new HandlerICCard(new WeakReference<>(ScaleActivity.this));
    private LinearLayout flippart;
    private ImageView mBatteryIv;
    private List<ProductADB> mProductList = new ArrayList<>();
    private List<ProductCart> productCartList = null;
    private RequestOrderVM vm = null;
    private ArrayList<Button> btnList = new ArrayList<>();
    private ProductADB proTradeNow = null;
    private JNIScale mScale;
    private NumFormatUtil mNumUtil = NumFormatUtil.getInstance();
    private BackDisplayBase backDisplay = null;
    private int RECUR_FLAG = 1;
    ICCardSerialPortUtil icCardSerialPortUtil = new ICCardSerialPortUtil();


    //展示商品列表
    private void showPros() {
        List<ProductADB> productADBList = CacheHelper.getPros();
        if (productADBList == null || productADBList.size() < 1) {
            showMessage("本地数据库中没有找到商品！");
            return;
        }
        List<Button> btnList = new ArrayList<Button>();
        for (int i = 0; i < flippart.getChildCount(); i++) {
            View view = flippart.getChildAt(i);
            if (view instanceof LinearLayout) {
                LinearLayout ll = ((LinearLayout) view);
                for (int m = 0; m < ll.getChildCount(); m++) {
                    View btn = ll.getChildAt(m);
                    if (btn instanceof Button) {
                        if (R.id.morebutton != btn.getId()) {
                            btnList.add((Button) btn);
                        }
                    }
                }
            }
        }
        for (int j = 0; j < productADBList.size(); j++) {
            if (j < btnList.size()) {
                btnList.get(j).setText(productADBList.get(j).getPro_name());
                btnList.get(j).setTag(productADBList.get(j));
                btnList.get(j).setOnClickListener(v -> {
                    mMisc.beep();
                    ProductADB productADB = (ProductADB) v.getTag();
                    mTvUnitPrice.setText(productADB.getPrice() + "");
                    tvProName.setText(productADB.getPro_name());
                    //标记该商品已经被选中
                    proTradeNow = productADB;
                    countPirce();
                });
            }
        }

    }

    //开启定时循环
    Runnable runnableTimer = () -> {
        handlerScale.sendEmptyMessage(1);
    };

    //计算总价
    private void countPirce() {
        //获取总重的数据
        String mTvWeightStr = mTvWeight.getText().toString().trim();
        BigDecimal weight = NumFormatUtil.isNumeric(mTvWeightStr) ? new BigDecimal(mTvWeightStr) : new BigDecimal(0);

        //获取皮重的数据
        String mtvTareStr = mTvTare.getText().toString().trim();
        BigDecimal tare = NumFormatUtil.isNumeric(mtvTareStr) ? new BigDecimal(mtvTareStr) : new BigDecimal(0);

        //单价
        String unitPriceStr = mTvUnitPrice.getText().toString().trim();
        BigDecimal unitPrice = NumFormatUtil.isNumeric(unitPriceStr) ? new BigDecimal(unitPriceStr) : new BigDecimal(0);

        //计算总价
        BigDecimal sum = mNumUtil.getDecimalSum((weight.subtract(tare)), unitPrice);
        mTvSubtotal.setText(String.valueOf(sum));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(getSkipIntent(HeartBeatService.class));
        stopService(getSkipIntent(ShutdownService.class));
        stopService(getSkipIntent(DisplayService.class));
        timerThreads.shutdownNow();

        handlerScale.removeCallbacksAndMessages(null);
        handlerICCard.removeCallbacksAndMessages(null);
        icCardSerialPortUtil.endRead();
        mController.cleanPresentations();
        mProductList.clear();
        mProductList = null;
        btnList.clear();
        btnList = null;

        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scalepage);
        //注册订阅事件
        EventBus.getDefault().register(this);
        //后台发送服务-心跳数据订阅服务
        startService(getSkipIntent(HeartBeatService.class));
        //后台发送服务-定时关机服务
        startService(getSkipIntent(ShutdownService.class));
        //初始化称重模块
        initJNI();
        //初始化控件
        initViews();
        //展示数据库中的商品
        showPros();

        //循环定时任务-轮询称重模块数据
        timerThreads.scheduleAtFixedRate(runnableTimer, 1500, 200, TimeUnit.MILLISECONDS);
        //开启IC卡读取功能
        icCardSerialPortUtil.startRead(this);
    }

    //初始化控件
    private void initViews() {
        mTimeView = findViewById(R.id.timer);//日期时间

        mTvUnitPrice = findViewById(R.id.unitprice); //商品单价
        mTvWeight = findViewById(R.id.weight);   //商品的重量
        mTvTare = findViewById(R.id.tare);//商品的皮重
        mTvSubtotal = findViewById(R.id.subtotal);  //商品总价
        tvProName = findViewById(R.id.tvProName);//商品名称
        buyer = findViewById(R.id.buyer);
        seller = findViewById(R.id.seller);
        flippart = findViewById(R.id.flippart);
        mMaxUnitView = findViewById(R.id.maxUnit);
        mBatteryIv = findViewById(R.id.battery);
        pay = findViewById(R.id.pay);
        tvAllPrice = findViewById(R.id.tvAllPrice);

        findViewById(R.id.morebutton).setOnClickListener(btnMorePros);
        //配置自动计算功能
        mTvTare.addTextChangedListener(new TWShowBackDisplay(1)); //商品的皮重
        mTvWeight.addTextChangedListener(new TWShowBackDisplay(5));//商品的重量
        tvProName.addTextChangedListener(new TWShowBackDisplay(2)); //商品的名称
        mTvUnitPrice.addTextChangedListener(new TWShowBackDisplay(3));//商品单价
        mTvSubtotal.addTextChangedListener(new TWShowBackDisplay(4));//商品总价
        pay.addTextChangedListener(new TWShowBackDisplay(6));//当前购物车数量
        tvAllPrice.addTextChangedListener(new TWShowBackDisplay(7));//购物车总价
        buyer.addTextChangedListener(new TWShowBackDisplay(8));//买家信息
        TextView versionView = findViewById(R.id.version);
        String content = "V" + App.VersionName;
        versionView.setText(content);
    }

    private Button.OnClickListener btnMorePros = v -> {
        mMisc.beep();
        startActivity(SearchActivity.class);
    };

    //初始化称重模块
    private void initJNI() {
        try {
            mScale = JNIScale.getScale();
            boolean isDirExists = FileHelp.isSerayDirExists();
            if (!isDirExists) {
                App.getApplication().setScreenOffTime(60 * 60 * 1000);
            }
            if (AppConfig.isSR200()) {
                // 开启广告后显示服务
                Intent intent = new Intent(ScaleActivity.this, DisplayService.class);
                startService(intent);
                mController.showPresentation(DisplayType.DISPLAY_WEIGHT);
                backDisplay = TableBackDisplay.getInstance(CacheHelper.isOpenBackDisplay, DisplayType.DISPLAY_WEIGHT);
            } else {
                backDisplay = LandBackDisplay.getInstance(CacheHelper.isOpenBackDisplay);
            }
            backDisplay.initBackDisplay();
            if (CacheHelper.isOpenJin) {
                backDisplay.showWeightType(2);
            } else {
                backDisplay.showWeightType(0);
                backDisplay.showPlace("非本市");
                backDisplay.showCustomerName("买家请刷卡");
            }
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    //接收电量消息 每分钟一次
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBattery(@NonNull BatteryMsg msg) {
        LogUtil.i("--------BatteryMsg---->" + msg.toString());
        //展示时间
        mTimeView.setText(NumFormatUtil.getFormatDate() + "-" + msg.getBattery());

        //展示电量
        int level = msg.getLevel();
        switch (level) {
            case 4:
                mBatteryIv.setImageResource(R.drawable.four_electric);
                break;
            case 3:
                mBatteryIv.setImageResource(R.drawable.three_electric);
                break;
            case 2:
                mBatteryIv.setImageResource(R.drawable.two_electric);
                break;
            case 1:
                mBatteryIv.setImageResource(R.drawable.one_electric);
                break;
            case 0:
                mBatteryIv.setImageResource(R.drawable.need_charge);
                break;
        }
    }

    //接收定时关机消息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveShutdownMsg(ShutdownMsg shutdownMsg) {
        if ("11122".equals(shutdownMsg.getCode())) {
            showMessage(shutdownMsg.getMsg() + "");
            speakNow(shutdownMsg.getMsg() + "");
        }
        if ("222111".equals(shutdownMsg.getCode())) {
            try {
                Runtime.getRuntime().exec("adb shell");
                Runtime.getRuntime().exec("reboot -p");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //接收心跳数据信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveHeartBeatMsg(HeartBeatMsg heartBeatMsg) {
        if ("90000".equals(heartBeatMsg.getCode())) {
            showMessage("更新商品成功！");
            showPros();
        }
        LogUtil.i("--------HeartBeatMsg---->" + heartBeatMsg.toString());
    }

    //接收商品页面中的选择选项
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveProductChoice(ProductADB productADB) {
        mTvUnitPrice.setText(productADB.getPrice() + "");
        tvProName.setText(productADB.getPro_name());
        //标记该商品已经被选中
        proTradeNow = productADB;
        countPirce();
    }

    //接收购物车页面中的上传订单返回项目
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveCartOrderActivity(ClearCartMsg msg) {
        //清空本次交易产生的称重数据
        weightsCache = null;

        //订单上传成功，则充值按钮
        if ("9000".equals(msg.getCode())) {
            reset(false);
        }
        if ("8000".equals(msg.getCode())) {
            List<ProductCart> productADBList = (List<ProductCart>) msg.getMsg();
            productCartList = productADBList;
            //当前商品列表总价
            pay.setText("支付\n(" + productCartList.size() + "笔)");
            tvAllPrice.setText(NumFormatUtil.countPrice(productCartList) + "");
        }
    }

    //监听右侧键盘点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        showMessage("-->" + keyCode);
        switch (keyCode) {
            //客户1
            case KeyEvent.KEYCODE_A:
                speakNow("欢迎使用盛阳一户一秤系统！");
                return true;
            //客户2
            case KeyEvent.KEYCODE_D:
                return true;
            //客户3
            case KeyEvent.KEYCODE_B:
                return true;
            //菜单---菜单
            case KeyEvent.KEYCODE_MENU:
                CustomInputTareDialog tareDialog = new CustomInputTareDialog(ScaleActivity.this, "请输入管理员密码", "请输入管理员密码", Boolean.TRUE);
                tareDialog.show();
                tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, weight) -> {
                    dialog.dismiss();
                    if (getPwd().equals(weight)) {
                        startActivity(SettingActivity.class);
                    }
                });
                tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
                return true;
            //开关
            case KeyEvent.KEYCODE_F3:
                return true;

            //计件---重置
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY:
                sbtnReset(null);
                return true;
            // 语音--去皮
            case KeyEvent.KEYCODE_DEL:
                sbtnTare(null);
                return true;

            // 去皮--？？
            case KeyEvent.KEYCODE_F1:
                return true;
            //置零---改价
            case KeyEvent.KEYCODE_F2:
                sbtnPrice(null);
                break;

            // 取消--？？
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:
                return true;
            //商品-加入购物车
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
                sbtnCart(null);
                return true;

            // 返回--？？
            case KeyEvent.KEYCODE_BACK:
                return true;
            //打印---支付
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                sbtnPay(null);
                return true;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        return keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT
                || keyCode == KeyEvent.KEYCODE_SHIFT_LEFT
                || keyCode > KeyEvent.KEYCODE_E && keyCode <= KeyEvent.KEYCODE_Z
                || keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9
                || keyCode == KeyEvent.KEYCODE_PERIOD
                || keyCode == KeyEvent.KEYCODE_MINUS
                || keyCode == KeyEvent.KEYCODE_SLASH
                || keyCode == KeyEvent.KEYCODE_SEMICOLON
                || keyCode == KeyEvent.KEYCODE_EQUALS
                || super.dispatchKeyEvent(event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        this.RECUR_FLAG = getIntent().getIntExtra("FNS", RECUR_FLAG);
    }

    @Override
    protected void onResume() {
        mMaxUnitView.setText(String.format("%skg-%s", mScale.getMainUnitFull(), CacheHelper.device_id));
        super.onResume();
    }

    //接收读卡器返回数据
    @Override
    public void onICCardDataReceive(ICCardSerialPortUtil.DataReceType type, byte[] buffer, int size) {
        Message message = new Message();
        message.what = -1;
        System.out.println(ByteUtil.bytesToHexString(buffer));
        if (ICCardSerialPortUtil.DataReceType.success == type) {
            if (buffer.length != 12) {
                message.obj = "卡号长度有误：" + buffer.length;
                System.out.println(ByteUtil.bytesToHexString(buffer));
            } else {
                String cardID = ByteUtil.getCardNo(buffer);
                if (null == cardID) {
                    message.obj = "解析卡号失败！";
                } else {
                    ResultData resultData = getUserByICCard(cardID);
                    message.what = 9;
                    message.obj = resultData;
                }
            }
        } else {
            message.obj = "读取卡号失败！";
        }
        handlerICCard.sendMessage(message);
    }

    //发送http请求，获取IC卡会员信息
    private ResultData getUserByICCard(String cardID) {
        ResultData resultData = new ResultData();
        try {
            GetUserByCardNoVM getUserByCardNoVM = new GetUserByCardNoVM();
            resultData = AESTools.encrypt(resultData, CacheHelper.device_aes_key, System.currentTimeMillis() + "," + CacheHelper.device_id);
            if (!resultData.isSuccess()) {
                return resultData;
            }
            String sign = resultData.getMsg() + "";
            getUserByCardNoVM.setCardNo(cardID);
            System.out.println("--getuserbycardno--request---start------>" + CacheHelper.device_id);
            Response<ApiDataRsp<UserVipCardDetailDTO>> response = HttpServicesFactory.getHttpServiceApi().getuserbycardno(CacheHelper.device_id + "", sign, getUserByCardNoVM).execute();
            System.out.println("--getuserbycardno--request---end------>");

            if (!response.isSuccessful()) {
                resultData.setRetMsg(response.code() + "", "-" + response.errorBody().toString());
            } else {
                resultData.setTrueMsg(response.body());
            }
            return resultData;
        } catch (Exception e) {
            resultData.setRetMsg("2144", e.getMessage());
            return resultData;
        }
    }


    //处理读卡模块返回数据
    private static class HandlerICCard extends Handler {
        WeakReference<ScaleActivity> mWeakReference;

        HandlerICCard(WeakReference<ScaleActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScaleActivity activity = mWeakReference.get();
            if (msg.what == -1) {
                activity.showMessage(msg.obj.toString());
                return;
            }
            if (msg.what == 9) {
                ResultData resultData = (ResultData) msg.obj;
                if (!resultData.isSuccess()) {
                    activity.showMessage(resultData.getCode() + "-" + resultData.getMsg());
                } else {
                    ApiDataRsp<UserVipCardDetailDTO> apiDataRsp = (ApiDataRsp<UserVipCardDetailDTO>) resultData.getMsg();
                    if (apiDataRsp.getSuccess()) {
                        UserVipCardDetailDTO userVipCardDetailDTO = apiDataRsp.getMsgs();
                        if ("买家".equals(userVipCardDetailDTO.getUser_type())) {
                            activity.buyer.setText(userVipCardDetailDTO.getUser_name() + "-" + userVipCardDetailDTO.getBalance());
                            activity.buyer.setTag(userVipCardDetailDTO);
                        } else if ("卖家".equals(userVipCardDetailDTO.getUser_type())) {
                            activity.seller.setText(userVipCardDetailDTO.getUser_name());
                            activity.seller.setTag(userVipCardDetailDTO);
                        } else {
                            activity.showMessage("没有【" + userVipCardDetailDTO.getUser_type() + "】该种类型的角色！");
                        }
                    } else {
                        activity.showMessage(apiDataRsp.getCode() + "-" + apiDataRsp.getError_msg());
                    }
                }
            }
        }
    }


    //直接输入总价点击事件
    public void changeMoney(View view) {
        mMisc.beep();
        //规格包装的商品不用计价，直接输入总价
        if (proTradeNow != null && proTradeNow.getProduct_id() == 46) {
            CustomInputTareDialog tareDialog = new CustomInputTareDialog(ScaleActivity.this, "请输入商品总价", "商品单价单位为元", Boolean.FALSE);
            tareDialog.show();
            tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, weight) -> {
                dialog.dismiss();
                mTvSubtotal.setText(NumFormatUtil.DF_PRICE.format(Double.parseDouble(weight)));
            });
            tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
        } else {
            showMessage("直接输入总价，请先选择【包装】商品！");
        }


    }

    //重置按钮点击事件
    public void sbtnReset(View view) {
        mMisc.beep();
        if (null != weightsCache && weightsCache.length() > 1) {

            //称重了，却没有发起交易
            RequestWatcherAlertVM vm = new RequestWatcherAlertVM();
            vm.setWeight_data(weightsCache.toString());
            weightsCache = null;
            if (seller.getTag() != null) {
                UserVipCardDetailDTO sellerDTO = (UserVipCardDetailDTO) seller.getTag();
                vm.setSeller_card_id(sellerDTO.getUser_vip_card_id());
                vm.setSeller_id(sellerDTO.getUser_vip_id());
                vm.setSeller_name(sellerDTO.getUser_name());
            } else {
                return;
            }

            if (buyer.getTag() != null) {
                UserVipCardDetailDTO buyerDTO = (UserVipCardDetailDTO) buyer.getTag();
                vm.setBuyer_card_id(buyerDTO.getUser_vip_card_id());
                vm.setBuyer_id(buyerDTO.getUser_vip_id());
                vm.setBuyer_name(buyerDTO.getUser_name());
            } else {
                return;
            }

            ResultData resultData = new ResultData();
            resultData = AESTools.encrypt(resultData, CacheHelper.device_aes_key, System.currentTimeMillis() + "," + CacheHelper.device_id);
            if (!resultData.isSuccess()) {
                showMessage(resultData.getCode() + "-" + resultData.getMsg());
                return;
            }
            String sign = resultData.getMsg() + "";
            LLog.i("哨兵模式请求：" + vm.toString());
            HttpServicesFactory.getHttpServiceApi().watcher_alert(CacheHelper.device_id.toString(), sign, vm).enqueue(new Callback<ApiDataRsp<String>>() {
                @Override
                public void onResponse(Call<ApiDataRsp<String>> call, Response<ApiDataRsp<String>> response) {
                    LLog.i("哨兵模式返回：" + response.body().getCode() + response.body().getError_msg());
                }

                @Override
                public void onFailure(Call<ApiDataRsp<String>> call, Throwable t) {
                    LLog.i("哨兵模式返回：" + t.getMessage());
                }
            });
        }
        reset(true);
    }

    //去皮按钮点击事件
    public void sbtnTare(View views) {
        mMisc.beep();
        CustomInputTareDialog tareDialog = new CustomInputTareDialog(ScaleActivity.this, "请输入去皮重量", "去皮重量单位为公斤（kg）", Boolean.FALSE);
        tareDialog.show();
        tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, weight) -> {
            dialog.dismiss();
            mTvTare.setText(NumFormatUtil.DF_WEIGHT.format(Double.parseDouble(weight)));
            //计算总价
            countPirce();
        });
        tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
    }

    //改价按钮点击事件
    public void sbtnPrice(View views) {
        mMisc.beep();
        if (null == proTradeNow) {
            showMessage("请选择商品后，再进行改价！");
            return;
        }

        CustomInputTareDialog tareDialog = new CustomInputTareDialog(ScaleActivity.this, "请输入商品单价", "商品单价单位为元", Boolean.FALSE);
        tareDialog.show();
        tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, weight) -> {
            dialog.dismiss();
            BigDecimal unitPrice = BigDecimal.valueOf(Double.parseDouble(weight));
            System.out.println("proTradeNow------->" + proTradeNow.toString());
            if (unitPrice.compareTo(BigDecimal.valueOf(proTradeNow.getMax_price())) > 0) {
                showMessage("价格高于商品的最高价！");
                return;
            }
            if (unitPrice.compareTo(BigDecimal.valueOf(proTradeNow.getMin_price())) < 0) {
                showMessage("价格低于商品的最低价！");
                return;
            }
            mTvUnitPrice.setText(NumFormatUtil.DF_PRICE.format(Double.parseDouble(weight)));
            //计算总价
            countPirce();
        });
        tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
    }

    //加入购物车按钮点击事件
    @SuppressLint("SetTextI18n")
    public void sbtnCart(View views) {
        mMisc.beep();
        UserVipCardDetailDTO sellerTag = (UserVipCardDetailDTO) seller.getTag();
        if (null == sellerTag) {
            showMessage("加入购物车失败，卖家没有刷卡！");
            return;
        }

        UserVipCardDetailDTO buyerTag = (UserVipCardDetailDTO) buyer.getTag();
        if (null == buyerTag) {
            showMessage("加入购物车失败，买家没有刷卡！");
            return;
        }

        if (null == proTradeNow) {
            showMessage("加入购物车失败，没有选择商品！");
            return;
        }
        if (null == productCartList) {
            productCartList = new ArrayList<>();
        }

        //账户余额
        BigDecimal buerBalnce = BigDecimal.valueOf(Double.parseDouble(buyerTag.getBalance()));

        BigDecimal tare = BigDecimal.valueOf(Double.parseDouble(mTvTare.getText().toString().trim()));//皮重
        BigDecimal weight = BigDecimal.valueOf(Double.parseDouble(mTvWeight.getText().toString().trim()));//总重
        BigDecimal realPrice = BigDecimal.valueOf(Double.parseDouble(mTvUnitPrice.getText().toString().trim()));//成交价
        BigDecimal price = BigDecimal.valueOf(proTradeNow.getPrice());//原始单价
        if (realPrice.compareTo(BigDecimal.valueOf(proTradeNow.getPrice())) != 0) {
            proTradeNow.setPrice(Double.parseDouble(realPrice + ""));
            CacheHelper.updateProductADB(proTradeNow);
            System.out.println("-------showPros()-------->");
            //刷新商品列表
            showPros();
        }
        BigDecimal mTvSubtotalStr = BigDecimal.valueOf(Double.parseDouble(mTvSubtotal.getText().toString().trim()));//商品总价
        if (mTvSubtotalStr.compareTo(new BigDecimal(1.00)) < 0) {
            showMessage("总价不能低于1元！");
            return;
        }

        int proID = proTradeNow.getProduct_id();//商品id
        //判断是否重复加入购物车
        boolean hasRepeat = Boolean.FALSE;
        for (ProductCart checkObj : productCartList) {
            if (checkObj.getProduct_id().equals(proID) && checkObj.getMoney_total().equals(mTvSubtotalStr)) {
                hasRepeat = Boolean.TRUE;
            }
        }

        if (vm == null) {
            Integer vip_id_seller = sellerTag.getUser_vip_id();
            Integer vip_id_buyer = buyerTag.getUser_vip_id();
            Integer vip_card_id_seller = sellerTag.getUser_vip_card_id();
            Integer vip_card_id_buyer = buyerTag.getUser_vip_card_id();
            vm = new RequestOrderVM();
            vm.setBuyer_card_id(vip_card_id_buyer);
            vm.setSeller_card_id(vip_card_id_seller);
            vm.setBuyer_id(vip_id_buyer);
            vm.setSeller_id(vip_id_seller);
        }
        String tvProNameStr = tvProName.getText().toString().trim();//商品名称
        ProductCart productCart = new ProductCart(proID, tvProNameStr, price, realPrice, tare, weight, mTvSubtotalStr);

        if (hasRepeat) {
            CustomTipDialog tipDialog = new CustomTipDialog(this);
            tipDialog.show();
            tipDialog.setTitle("提醒");
            tipDialog.setMessage("购物车中出现相同的商品和总价，是否需要加入购物车？");
            tipDialog.setOnPositiveClickListener("加入", dialog -> {

                productCartList.add(productCart);
                //当前商品列表总价
                BigDecimal allPrice = NumFormatUtil.countPrice(productCartList);
                if (allPrice.compareTo(buerBalnce) > 0) {
                    productCartList.remove(productCart);
                    showMessage("买家账户余额已经超出商品总价，余额不足！");
                    return;
                }
                pay.setText("支付\n(" + productCartList.size() + "笔)");
                tvAllPrice.setText(NumFormatUtil.countPrice(productCartList) + "");
                showMessage("加入购物车成功！");

            });
            tipDialog.setOnNegativeClickListenerPrivate("取消", Dialog::dismiss);
        } else {
            productCartList.add(productCart);
            //当前商品列表总价
            BigDecimal allPrice = NumFormatUtil.countPrice(productCartList);
            if (allPrice.compareTo(buerBalnce) > 0) {
                productCartList.remove(productCart);
                showMessage("买家账户余额已经超出商品总价，余额不足！");
                return;
            }
            pay.setText("支付\n(" + productCartList.size() + "笔)");
            tvAllPrice.setText(NumFormatUtil.countPrice(productCartList) + "");
            showMessage("加入购物车成功！");
            //保存当前单价和去皮重量


        }

    }

    //支付按钮点击事件
    public void sbtnPay(View views) {
        mMisc.beep();
        if (null == productCartList || productCartList.size() < 1) {
            showMessage("当前购物车没有商品！");
            return;
        }
        vm.setProductCartList(productCartList);

        if (null == buyer.getTag()) {
            showMessage("买家信息异常，请重新刷卡！");
            return;
        }
        UserVipCardDetailDTO buyerTag = (UserVipCardDetailDTO) buyer.getTag();


        Intent intent = new Intent(this, CartOrderActivity.class);
        intent.putExtra("RequestOrderVM", (Serializable) vm);
        intent.putExtra("UserVipCardDetailDTO", (Serializable) buyerTag);
        startActivity(intent);
    }


    //处理称重模块返回数据
    private static class HandlerScale extends Handler {
        WeakReference<ScaleActivity> mWeakReference;

        HandlerScale(WeakReference<ScaleActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScaleActivity activity = mWeakReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    //显示重量
                    activity.weightChangedCyclicity();
                    //点亮屏幕
                    activity.lightScreenCyclicity();
                    //展示时间
                    activity.mTimeView.setText(NumFormatUtil.getFormatDate());
                }
            }
        }
    }

    //点亮屏幕
    private void lightScreenCyclicity() {
        App.getApplication().openScreen();
    }


    //记录每笔交易的称重次数
    public static StringBuilder weightsCache = null;

    private static int averageTimes = 5;
    private static int weightCount = 0;
    private static BigDecimal[] weights = new BigDecimal[averageTimes];
    private static BigDecimal maxFW = new BigDecimal("0.200");
    private static BigDecimal currentWeight = new BigDecimal("0.000");

    //显示称重模块读取的数据
    private void weightChangedCyclicity() {
        //获取电子秤返回的重量数据
        String strNet = mScale.getStringNet().trim();
        if (!NumFormatUtil.isNumeric(strNet)) {
            return;
        }
        if (strNet.contains("OL")) {
            showMessage("超出量程范围！");
            return;
        }
        BigDecimal cacheWeight = new BigDecimal(strNet);     //默认公斤kg

        if (cacheWeight.compareTo(new BigDecimal("0.000")) > 0 && (null == buyer.getTag() || null == seller.getTag())) {
            weightCount = 0;
            currentWeight = new BigDecimal("0.000");
            showMessage("买卖双方尚未刷卡，称重不予以显示！");
            return;
        }

        //记录称重数值
        if (weightCount < averageTimes) {
            weights[weightCount] = cacheWeight;
            weightCount++;
            return;
        }
        //完成连续五次的称重记录
        weightCount = 0;


        //获取五次内的称重最大值
        BigDecimal cacheMax = new BigDecimal("-100.000");
        for (BigDecimal pi : weights) {
            if (pi.compareTo(cacheMax) >= 0)
                cacheMax = pi;
        }


        if (cacheMax.compareTo(new BigDecimal("0.000")) != 0) {
            //差值的绝对值，大于指定阙值则显示称重
            BigDecimal absWeight = currentWeight.subtract(cacheMax).abs();
            //如果变动的重量低于maxFW则不显示
            if (absWeight.compareTo(maxFW) < 0) {
                return;
            }
        }
        //当前称重数值
        currentWeight = cacheMax;
        //如果变动的重量高于maxFW则显示
        mTvWeight.setText(NumFormatUtil.DF_WEIGHT.format(cacheMax));

        //记录本次的称重数值
        if (weightsCache == null) {
            weightsCache = new StringBuilder();
        }
        if (currentWeight.compareTo(maxFW) > 0) {
            weightsCache.append(currentWeight).append(",");
        }
        //规格包装的商品不用计价，直接输入总价
        if (!(proTradeNow != null && proTradeNow.getProduct_id() == 46)) {
            //计算总价
            countPirce();
        }


    }

    //通知背面显示屏上显示单价、皮重、总价
    private class TWShowBackDisplay implements TextWatcher {
        int flag;

        TWShowBackDisplay(int flag) {
            this.flag = flag;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (flag) {
                case 1:
                    backDisplay.showTare(s.toString()); //商品的皮重
                    break;
                case 2:
                    backDisplay.showProductName(s.toString()); //商品的名称
                    break;
                case 3:
                    backDisplay.showPrice(s.toString());//商品单价
                    break;
                case 4:
                    backDisplay.showAmount(s.toString());//商品总价
                    break;
                case 5:
                    backDisplay.showWeight(s.toString());//商品的总重
                    break;
                case 6:
                    backDisplay.showSubtotalAmount(productCartList != null ? productCartList.size() + "" : "0");//购物车商品数量
                    break;
                case 7:
                    backDisplay.showSubtotal(s.toString());//购物车商品总价
                    break;
                case 8:
                    backDisplay.showCustomerName(s.toString());//买家信息
                    break;
            }

        }

    }

    //重置交易界面信息
    private void reset(boolean isSellerClear) {
        this.productCartList = null;
        this.vm = null;
        proTradeNow = null;

        mTvTare.setText(this.getResources().getString(R.string.base_weight));
        mTvTare.setTag(null);

        mTvWeight.setText(this.getResources().getString(R.string.base_weight));
        mTvWeight.setTag(null);

        mTvUnitPrice.setText(this.getResources().getString(R.string.base_price));
        mTvUnitPrice.setTag(null);

        tvAllPrice.setText(this.getResources().getString(R.string.base_price));
        tvAllPrice.setTag(null);

        mTvSubtotal.setText(this.getResources().getString(R.string.base_price));
        mTvSubtotal.setTag(null);

        tvProName.setText(this.getResources().getString(R.string.tv_pro_text));
        tvProName.setTag(null);

        buyer.setText(this.getResources().getString(R.string.tv_buyer_text));
        buyer.setTag(null);

        if (isSellerClear) {
            seller.setText(this.getResources().getString(R.string.tv_seller_text));
            seller.setTag(null);
        }

        pay.setText(this.getResources().getString(R.string.tv_pay_text));
    }


}
