package com.seray.scales;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seray.cache.AppConfig;
import com.seray.cache.CacheHelper;
import com.seray.instance.ProductCart;
import com.seray.instance.ResultData;
import com.seray.inter.BackDisplayBase;
import com.seray.inter.LandBackDisplay;
import com.seray.inter.TableBackDisplay;
import com.seray.message.BatteryMsg;
import com.seray.message.HeartBeatMsg;
import com.seray.service.BatteryService;
import com.seray.service.DisplayService;
import com.seray.service.HeartBeatService;
import com.seray.sjc.annotation.DisplayType;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.GetUserByCardNoVM;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.UserVipCardDetailDTO;
import com.seray.sjc.entity.device.ProductADB;
import com.seray.sjc.report.ReportSjcDetailActivity;
import com.seray.util.AESTools;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomInputTareDialog;
import com.skyworth.splicing.ICCardSerialPortUtil;
import com.tools.ByteUtil;
import com.tscale.scalelib.jniscale.JNIScale;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.hutool.json.JSONUtil;
import retrofit2.Response;

public class ScaleActivity extends BaseActivity implements ICCardSerialPortUtil.OnDataReceiveListener {

    private TextView mTimeView,//日期时间
            mTvTare,//商品的皮重
            mTvWeight, //商品的重量
            mTvUnitPrice, //商品单价
            mTvSubtotal,//商品总价
            tvProName,//商品名称
            buyer,//买家
            seller,//卖家
            mMaxUnitView;//标题显示
    private Button pay;//付款按钮

    private HandlerScale handlerScale = new HandlerScale(new WeakReference<>(this));
    private HandlerICCard handlerICCard = new HandlerICCard(new WeakReference<>(this));
    private LinearLayout flippart;
    private ImageView mBatteryIv;
    private List<ProductADB> mProductList = new ArrayList<>();
    private List<ProductCart> productCartList = null;
    private ArrayList<Button> btnList = new ArrayList<>();
    private ProductADB proTradeNow = null;
    private JNIScale mScale;
    private NumFormatUtil mNumUtil = NumFormatUtil.getInstance();
    private BackDisplayBase backDisplay = null;
    private int RECUR_FLAG = 1;
    ICCardSerialPortUtil icCardSerialPortUtil = new ICCardSerialPortUtil();

    //展示商品列表
    private void showPros() {
        System.out.println("------CacheHelper.getConfigMapString()------->" + CacheHelper.getConfigMapString());
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
                        btnList.add((Button) btn);
                    }

                }
            }
        }
        System.out.println("--btnList.size()-->" + btnList.size());
        System.out.println("--productADBList.size()-->" + productADBList.size());
        for (int j = 0; j < productADBList.size(); j++) {
            btnList.get(j).setText(productADBList.get(j).getPro_name());
            btnList.get(j).setTag(productADBList.get(j));
            btnList.get(j).setOnClickListener(new OnClickListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onClick(View v) {
                    mMisc.beep();
                    ProductADB productADB = (ProductADB) v.getTag();
                    mTvUnitPrice.setText(productADB.getPrice() + "");
                    tvProName.setText(productADB.getPro_name());
                    //标记该商品已经被选中
                    proTradeNow = productADB;

                }
            });
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

        if (null != proTradeNow) {
            if (unitPrice.compareTo(BigDecimal.valueOf(proTradeNow.getMax_price())) > 0) {
                showMessage("价格高于商品的最高价！");
                return;
            }
            if (unitPrice.compareTo(BigDecimal.valueOf(proTradeNow.getMin_price())) < 0) {
                showMessage("价格低于商品的最低价！");
                return;
            }
        }
        //计算总价
        BigDecimal sum = mNumUtil.getDecimalSum((weight.subtract(tare)), unitPrice);
        mTvSubtotal.setText(String.valueOf(sum));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(getSkipIntent(BatteryService.class));
        stopService(getSkipIntent(HeartBeatService.class));
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
        //后台发送服务-电池电量订阅服务
        startService(getSkipIntent(BatteryService.class));
        //后台发送服务-心跳数据订阅服务
        startService(getSkipIntent(HeartBeatService.class));
        //初始化称重模块
        initJNI();
        //初始化控件
        initViews();
        //展示数据库中的商品
        showPros();
        //开启定时刷新线程
        timerThreads.scheduleAtFixedRate(runnableTimer, 1500, 50, TimeUnit.MILLISECONDS);
        //开启IC卡读取功能
        icCardSerialPortUtil.startRead(this);
    }

    //初始化控件
    private void initViews() {
        mTimeView = findViewById(R.id.timer);//日期时间
        mTimeView.setText(NumFormatUtil.getFormatDate());
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
        //配置自动计算功能
        mTvTare.addTextChangedListener(new TWShowBackDisplay(1)); //商品的皮重
        mTvWeight.addTextChangedListener(new TWShowBackDisplay(5));//商品的重量
        tvProName.addTextChangedListener(new TWShowBackDisplay(2)); //商品的名称
        mTvUnitPrice.addTextChangedListener(new TWShowBackDisplay(3));//商品单价
        mTvSubtotal.addTextChangedListener(new TWShowBackDisplay(4));//商品总价


        TextView versionView = findViewById(R.id.version);
        String content = "V" + App.VersionName;
        versionView.setText(content);
    }

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
        mTimeView.setText(NumFormatUtil.getFormatDate());
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

    //接收心跳数据信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveHeartBeatMsg(HeartBeatMsg heartBeatMsg) {
        LogUtil.i("--------HeartBeatMsg---->" + heartBeatMsg.toString());
    }

    //监听右侧键盘点击事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                return true;
            case KeyEvent.KEYCODE_BACK:// 返回按钮
                return true;
            case KeyEvent.KEYCODE_MENU:// 桌秤
                return true;
            case KeyEvent.KEYCODE_MOVE_HOME:// 地秤
                startActivity(ManageActivity.class);
                return true;
            case KeyEvent.KEYCODE_F1:// 去皮
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                return true;
            case KeyEvent.KEYCODE_NUM_LOCK: // 退格
                return true;
            case KeyEvent.KEYCODE_DEL:// 语音
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ADD:// 价格修改操作
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER: // 打印
                return true;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:// 手动输入去皮重量
                return true;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY: // 计件计重切换
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
        if (ICCardSerialPortUtil.DataReceType.success == type) {
            if (buffer.length != 12) {
                message.obj = "卡号长度有误：" + buffer.length;
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
            Response<ApiDataRsp<UserVipCardDetailDTO>> response = HttpServicesFactory.getHttpServiceApi().getuserbycardno(CacheHelper.device_id + "", sign, getUserByCardNoVM).execute();
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
                            activity.buyer.setText("买家:" + userVipCardDetailDTO.getUser_name() + "-" + userVipCardDetailDTO.getBalance());
                            activity.buyer.setTag(userVipCardDetailDTO);
                        } else if ("卖家".equals(userVipCardDetailDTO.getUser_type())) {
                            activity.seller.setText("卖家:" + userVipCardDetailDTO.getUser_name());
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

    //去皮按钮点击事件
    public void sbtnTare(View view) {
        mMisc.beep();
        CustomInputTareDialog tareDialog = new CustomInputTareDialog(ScaleActivity.this, "请输入去皮重量", "去皮重量单位为公斤（kg）");
        tareDialog.show();
        tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, weight) -> {
            dialog.dismiss();
            mTvTare.setText(NumFormatUtil.DF_WEIGHT.format(weight));
            //计算总价
            countPirce();
        });
        tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
    }

    //价格按钮点击事件
    public void sbtnPrice(View view) {
        mMisc.beep();
        CustomInputTareDialog tareDialog = new CustomInputTareDialog(ScaleActivity.this, "请输入商品单价", "商品单价单位为元");
        tareDialog.show();
        tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, weight) -> {
            dialog.dismiss();
            mTvUnitPrice.setText(NumFormatUtil.DF_PRICE.format(weight));
            //计算总价
            countPirce();
        });
        tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
    }

    //累加按钮点击事件
    @SuppressLint("SetTextI18n")
    public void sbtnAccux(View view) {
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

        // 当秤稳定时 或是 计件模式时
        if (mScale.getStabFlag()) {
            String weight = mTvWeight.getText().toString().trim();//总重
            String price = mTvUnitPrice.getText().toString().trim();//单价
            String tare = mTvTare.getText().toString().trim();//皮重
            String tvProNameStr = tvProName.getText().toString().trim();//商品名称
            BigDecimal mTvSubtotalStr = BigDecimal.valueOf(Double.parseDouble(mTvSubtotal.getText().toString().trim()));//商品总价
            int proID = proTradeNow.getProduct_id();//商品id
            Integer vip_id_seller = sellerTag.getUser_vip_id();
            Integer vip_id_buyer = buyerTag.getUser_vip_id();
            Integer vip_card_id_seller = sellerTag.getUser_vip_card_id();
            Integer vip_card_id_buyer = buyerTag.getUser_vip_card_id();
            String realPrice = proTradeNow.getPrice() + "";
            ProductCart productCart = new ProductCart(proID, tvProNameStr, realPrice, price, tare, weight, mTvSubtotalStr, vip_id_seller, vip_card_id_seller,
                    vip_id_buyer, vip_card_id_buyer, CacheHelper.device_id);
            if (null == productCartList) {
                productCartList = new ArrayList<ProductCart>();
            }
            productCartList.add(productCart);
            pay.setText("支付\n(" + productCartList.size() + "笔)");
            showMessage("加入购物车成功！");
        } else {
            showMessage("重量仍在变动！");
        }
    }


    //支付按钮点击事件
    public void sbtnPay(View view) {
        mMisc.beep();
        if (null == productCartList || productCartList.size() < 1) {
            showMessage("当前购物车没有商品！");
            return;
        }
        Intent intent = new Intent(this, ReportSjcDetailActivity.class);
        intent.putExtra("order_list", (Serializable) productCartList);
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
                switch (msg.what) {
                    case 1:
                        activity.weightChangedCyclicity();
                        activity.lightScreenCyclicity();
                        break;
                }
            }
        }
    }

    //点亮屏幕
    private void lightScreenCyclicity() {
        App.getApplication().openScreen();
    }

    //显示称重模块读取的数据
    private void weightChangedCyclicity() {
        //获取电子秤返回的重量数据
        String strNet = mScale.getStringNet().trim();
        float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;     //默认公斤kg
        //展示重量
        mTvWeight.setText(NumFormatUtil.DF_WEIGHT.format(fW));

        if (mScale.getStringNet().contains("OL")) {
            showMessage("超出量程范围！");
            return;
        }
        //展示电子秤稳定状态
        if (mScale.getStabFlag()) {
            backDisplay.showIsStable(true);
            findViewById(R.id.stableflag).setVisibility(View.VISIBLE);
        } else {
            backDisplay.showIsStable(false);
            findViewById(R.id.stableflag).setVisibility(View.INVISIBLE);
        }
        //是否为0值
        if (mScale.getZeroFlag()) {
            backDisplay.showIsZero(true);
            findViewById(R.id.zeroflag).setVisibility(View.VISIBLE);
        } else {
            backDisplay.showIsZero(false);
            findViewById(R.id.zeroflag).setVisibility(View.INVISIBLE);
        }
        //计算总价
        countPirce();
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
            }

        }
    }


}
