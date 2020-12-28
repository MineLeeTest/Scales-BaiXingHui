package com.seray.scales;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.decard.NDKMethod.BasicOper;
import com.google.gson.Gson;
import com.seray.cache.AppConfig;
import com.seray.cache.CacheHelper;
import com.seray.inter.BackDisplayBase;
import com.seray.inter.LandBackDisplay;
import com.seray.inter.TableBackDisplay;
import com.seray.message.BatteryMsg;
import com.seray.message.ClearOrderMsg;
import com.seray.message.LocalFileTag;
import com.seray.message.QuantifyMessage;
import com.seray.service.BatteryService;
import com.seray.service.DisplayService;
import com.seray.service.ImageRecognizeService;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.annotation.DisplayType;
import com.seray.sjc.annotation.PriceType;
import com.seray.sjc.annotation.TransType;
import com.seray.sjc.api.net.LocalServer;
import com.seray.sjc.api.result.RecognizeResult;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.SjcProductDao;
import com.seray.sjc.entity.message.ImageEncodeMessage;
import com.seray.sjc.entity.message.RecognizeMessage;
import com.seray.sjc.entity.order.OrderInfo;
import com.seray.sjc.entity.order.SjcDetail;
import com.seray.sjc.entity.order.SjcSubtotal;
import com.seray.sjc.entity.product.SjcProduct;
import com.seray.sjc.pay.SjcPayActivity;
import com.seray.sjc.poster.DisplayPoster;
import com.seray.sjc.poster.SjcUpdatePoster;
import com.seray.sjc.util.CameraHelper;
import com.seray.sjc.view.RecognizeProductDialog;
import com.seray.sjc.work.UploadOrderWork;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomInputTareDialog;
import com.tscale.scalelib.jniscale.JNIScale;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * The type Scale activity.
 */
public class ScaleActivity extends BaseActivity implements RecognizeProductDialog.OnRecognizeProductSelectListener {

    private TextView mTimeView, mTvWeight, mTvUnitPrice, mTvSubtotal, mTvWeightType,
            mTvWeightUnit, mTvTare, totalvegenum, allvegeprice, fnshow, vegenames;
    private TextView mPriceUnitView, mTareTextView, mMaxUnitView, mTareUnitView;
    private LinearLayout flippart, numberpart;
    private ImageView mBatteryIv;
    private LocalServer mLocalServer = null;
    private List<SjcProduct> mProductList = new ArrayList<>();
    private List<SjcDetail> Fn = CacheHelper.Detail_1;
    private ArrayList<Button> btnList = new ArrayList<>();
    private BigDecimal BASIC_TARE = new BigDecimal("0.000");

    private ScheduledExecutorService advertThread = Executors.newScheduledThreadPool(1);
    private ScaleHandler mScaleHandler = new ScaleHandler(new WeakReference<>(this));

    private JNIScale mScale;

    private SjcSubtotal mSubtotal;
    private SjcDetail mDetail;

    private NumFormatUtil mNumUtil = null;
    private BackDisplayBase backDisplay = null;

    private SjcProduct mSelectedProduct;

    private String priceRealValue = "";

    private int RECUR_FLAG = 1;
    private float currWeight = 0.0f;
    private float threshold = 0.1f;
    private float tareFloat = -1.0F;
    private float lastWeight = 0.0f;
    private float divisionValue = 0.02f;

    private boolean camerIsEnable = true;
    private boolean isDisplay = false;
    private boolean isPlu = false;
    private boolean isByWeight = true;

    private CameraHelper mCameraHelper;

    private void lightScreenCyclicity() {
        float w = mScale.getFloatNet();
        if (isOL() || Math.abs(w - currWeight) > divisionValue) {
            App.getApplication().openScreen();
        }
    }

    private Boolean isOL() {
        return mScale.getStringNet().contains("OL");
    }

    private void weightChangedCyclicity() {
        if (isByWeight) {
            String strNet = mScale.getStringNet().trim();
            float tare = CacheHelper.isOpenJin ? tareFloat * 2 : tareFloat;
            float fW = NumFormatUtil.isNumeric(strNet) ? Float.parseFloat(strNet) : 0;
            fW = CacheHelper.isOpenJin ? fW * 2 : fW;
            if (isOL()) {
                if (tareFloat > 0) {
                    mTareTextView.setText(String.format("(当前扣重：%s)", NumFormatUtil.df3.format(tare)));
                    mTareTextView.setVisibility(View.VISIBLE);
                    mTareTextView.setTextColor(Color.RED);
                } else {
                    mTareTextView.setVisibility(View.INVISIBLE);
                }
                mTvWeight.setText(strNet);
            } else {
                if (tareFloat > 0) {
                    fW -= tare;
                    mTareTextView.setText(String.format("(当前扣重：%s)", NumFormatUtil.df3.format(tare)));
                    mTareTextView.setVisibility(View.VISIBLE);
                    mTareTextView.setTextColor(Color.RED);
                } else {
                    mTareTextView.setVisibility(View.INVISIBLE);
                }
                mTvWeight.setText(NumFormatUtil.df3.format(fW));
            }
        }
        if (isStable()) {
            backDisplay.showIsStable(true);
            findViewById(R.id.stableflag).setVisibility(View.VISIBLE);
        } else {
            backDisplay.showIsStable(false);
            findViewById(R.id.stableflag).setVisibility(View.INVISIBLE);
        }
        if (mScale.getZeroFlag()) {
            backDisplay.showIsZero(true);
            findViewById(R.id.zeroflag).setVisibility(View.VISIBLE);
        } else {
            backDisplay.showIsZero(false);
            findViewById(R.id.zeroflag).setVisibility(View.INVISIBLE);
        }
        if (!isOL()) {
            String wString = mTvWeight.getText().toString().trim();
            String pString = mTvUnitPrice.getText().toString().trim();
            if (TextUtils.isEmpty(wString)) {
                mTvWeight.setText(R.string.base_weight);
                wString = "0";
            }
            if (TextUtils.isEmpty(pString)) {
                mTvUnitPrice.setText(R.string.base_price);
                pString = "0";
            }
            BigDecimal w = BASIC_TARE, p = BASIC_TARE;
            if (NumFormatUtil.isNumeric(wString)) {
                w = new BigDecimal(wString);
            }

            if (NumFormatUtil.isNumeric(pString)) {
                p = new BigDecimal(pString);
            }
            BigDecimal sum = mNumUtil.getDecimalSum(p, w);
            mTvSubtotal.setText(String.valueOf(sum));
            if (!IsDisplaySum()) {
                mTvSubtotal.setText(R.string.base_price);
            } else {
                mTvSubtotal.setText(String.valueOf(sum));
            }
        }
    }

    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    /**
     * 是否显示总价
     */
    private Boolean IsDisplaySum() {
        return !CacheHelper.isNoPluNoSum || !vegenames.getText().toString().isEmpty();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveClearInfoFromPay(ClearOrderMsg msg) {
        if (msg.getMsg().equals(ScaleActivity.class.getSimpleName())) {
            if (!Fn.isEmpty()) {
                Fn.clear();
                recurFnContent(RECUR_FLAG);
            }
            clearProductInfo();
        }
    }

    void recurFnContent(int flag) {
        RECUR_FLAG = flag;
        int contentId = CacheHelper.basicBtnText[RECUR_FLAG - 1];
        String content = getResources().getString(contentId);
        fnshow.setText(content);
        if (backDisplay != null) {
            backDisplay.showCustomerName(content);
        }
        totalAssign();
    }

    /**
     * 清除品名信息
     */
    void clearProductInfo() {
        priceRealValue = "";
        cleanTareFloat();
        mScaleHandler.post(new Runnable() {

            @Override
            public void run() {
                if (!CacheHelper.isHoldPlu) {
                    vegenames.setText("");
                    mSelectedProduct = null;
                }
                if (CacheHelper.isChangePrice && !CacheHelper.isHoldPrice) {
                    mTvUnitPrice.setText(R.string.base_price);
                }
                if (!isByWeight) {
                    isByWeight = true;
                    mTvWeightType.setText(R.string.base_weight_unit_by_scale);
                    mTvWeightUnit.setTextColor(Color.WHITE);
                    mTvWeightType.setTextColor(Color.WHITE);
                    setDefaultUnitView();
                    displayUnitShow();
                }
            }
        });
    }

    /**
     * 控制Fn总价和笔数的方法
     */
    private void totalAssign() {
        List<SjcDetail> single_fn = new ArrayList<>();
        String totalPrice_fn;
        switch (RECUR_FLAG) {
            case 1:
                single_fn = CacheHelper.Detail_1;
                break;
            case 2:
                single_fn = CacheHelper.Detail_2;
                break;
            case 3:
                single_fn = CacheHelper.Detail_3;
                break;
            case 4:
                single_fn = CacheHelper.Detail_4;
                break;
        }
        BigDecimal totalMoney = getTotalMoney(single_fn);
        totalPrice_fn = totalMoney.toString();
        totalvegenum.setText(String.valueOf(single_fn.size()));
        allvegeprice.setText(totalPrice_fn);
        if (backDisplay != null) {
            backDisplay.showSubtotal(totalPrice_fn);
            backDisplay.showSubtotalAmount(String.valueOf(single_fn.size()));
        }
    }

    /**
     * 重置tareFloat
     */
    void cleanTareFloat() {
        tareFloat = -1.0F;
    }

    private void changeWeightType() {
        if (isByWeight) {
            cleanTareFloat();
            isByWeight = false;
            mTvWeightUnit.setText(R.string.base_piece);
            mTvWeightType.setText(R.string.base_weight_unit_by_piece);
            mTvWeightUnit.setTextColor(Color.RED);
            mTvWeightType.setTextColor(Color.RED);
            mPriceUnitView.setText(R.string.base_unit_with_piece_1);
            mTvWeight.setText(R.string.base_weight_by_piece);
            mTareTextView.setVisibility(View.INVISIBLE);
        } else {
            isByWeight = true;
            setDefaultUnitView();
            mTvWeightType.setText(R.string.base_weight_unit_by_scale);
            mTvWeightUnit.setTextColor(Color.WHITE);
            mTvWeightType.setTextColor(Color.WHITE);
        }
        displayUnitShow();
    }

    protected void toggleIsWeight() {
        if (isByWeight) {
            setDefaultUnitView();
            mTvWeightType.setText(R.string.base_weight_unit_by_scale);
            mTvWeightUnit.setTextColor(Color.WHITE);
            mTvWeightType.setTextColor(Color.WHITE);
        } else {
            cleanTareFloat();
            mTvWeightUnit.setText(R.string.base_piece);
            mTvWeightType.setText(R.string.base_weight_unit_by_piece);
            mTvWeightUnit.setTextColor(Color.RED);
            mTvWeightType.setTextColor(Color.RED);
            mPriceUnitView.setText(R.string.base_unit_with_piece_1);
            mTvWeight.setText(R.string.base_weight_by_piece);
            mTareTextView.setVisibility(View.INVISIBLE);
        }
        displayUnitShow();
    }

    private BigDecimal getTotalMoney(List<SjcDetail> list) {
        BigDecimal sum = new BigDecimal("0.00");
        for (int i = 0; i < list.size(); i++) {
            BigDecimal amount = list.get(i).getDealAmt();
            sum = sum.add(amount);
        }
        return sum;
    }

    void setDefaultUnitView() {
        if (CacheHelper.isOpenJin) {
            mTareUnitView.setText(R.string.base_unit_type_2);
            mTvWeightUnit.setText(R.string.base_unit_type_2);
            mPriceUnitView.setText(R.string.base_unit_with_piece_3);
        } else {
            mTareUnitView.setText(R.string.base_unit_type_1);
            mTvWeightUnit.setText(R.string.base_unit_type_1);
            mPriceUnitView.setText(R.string.base_unit_with_piece_2);
        }
    }

    private void displayUnitShow() {
        if (AppConfig.isT200())
            return;
        if (CacheHelper.isOpenJin) {
            backDisplay.showWeightType(isByWeight ? 2 : 1);
        } else {
            backDisplay.showWeightType(isByWeight ? 0 : 1);
        }
    }

    //接收电量消息 每半小时一次
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveBattery(BatteryMsg msg) {

        if (msg != null) {

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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveSelectPLU(List<SjcProduct> selected) {
        if (!selected.isEmpty()) {
            mProductList.clear();
            mProductList.addAll(selected);
            assign();
        }
    }

    private void assign() {
        int index = mProductList.size();
        if (index > 11)
            index = 11;
        clearPlu();
        PLUValue(index);
    }

    private void clearPlu() {
        for (int i = 0; i < btnList.size(); i++) {
            btnList.get(i).setText("");
            btnList.get(i).setTag(null);
        }
    }

    private void PLUValue(int value) {
        if (value == 0) {
            clearPlu();
            return;
        }
        for (int i = 0; i < value; i++) {
            SjcProduct product = mProductList.get(i);
            btnList.get(i).setTag(product);
            btnList.get(i).setText(product.getGoodsName());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveProductFromSearch(@NonNull SjcProduct product) {
        mSelectedProduct = product;
        isByWeight = mSelectedProduct.getPriceType().equals(PriceType.BY_PRICE);
        showProductName();
    }

    /**
     * 处理显示单价
     */
    private void displayPrice() {
        String temp = priceRealValue;
        temp = formatPrice(temp);
        mTvUnitPrice.setText(temp);
    }

    private String formatPrice(String temp) {
        int len = temp.length();
        int dotIndex = temp.indexOf('.');
        if (len == 0)
            temp = "0";
        if (dotIndex > -1) {
            if (dotIndex == len - 1)
                temp += "00";
            else if (dotIndex >= len - 2)
                temp += "0";
            else if (dotIndex == len - 3)
                return temp;
            else if (dotIndex < len - 3)
                temp = temp.substring(0, dotIndex + 3);
            else if (temp.endsWith("0"))
                temp = temp.substring(0, len - 1);
        } else {
            temp += ".00";
        }
        return temp;
    }

    /**
     * 显示单价 小计 默认保留0.00
     */
    private void showProductName() {
        toggleIsWeight();
        isPlu = true;
        vegenames.setText(mSelectedProduct.getGoodsName());
        float price = mSelectedProduct.getSalePrice().floatValue();
        if (CacheHelper.isOpenJin) {
            price /= 2;
        }
        priceRealValue = String.valueOf(price);
        displayPrice();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_A:
                ACCUx(1);
                return true;
            case KeyEvent.KEYCODE_D:
                ACCUx(2);
                return true;
            case KeyEvent.KEYCODE_B:
                ACCUx(3);
                return true;
            case KeyEvent.KEYCODE_C:
                ACCUx(4);
                return true;
            case KeyEvent.KEYCODE_BACK:// 单价保存

                return true;
            case KeyEvent.KEYCODE_MENU:// 桌秤
            case KeyEvent.KEYCODE_MOVE_HOME:// 地秤
                startActivity(ManageActivity.class);
                return true;
            case KeyEvent.KEYCODE_F1:// 去皮
                cleanTareFloat();
                if (mScale.tare()) {
                    float curTare = mScale.getFloatTare();
                    if (CacheHelper.isOpenJin)
                        curTare *= 2;
                    mTvTare.setText(NumFormatUtil.df3.format(curTare));
                } else {
                    showMessage("去皮失败");
                }
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                if (mScale.zero()) {
                    cleanTareFloat();
                    mTvTare.setText(R.string.base_weight);
                } else {
                    showMessage("置零失败");
                }
                return true;
            case KeyEvent.KEYCODE_NUM_LOCK: // 退格
                undoLast();
                return true;
            case KeyEvent.KEYCODE_DEL:// 语音

                return true;
            case KeyEvent.KEYCODE_NUMPAD_0:
                unitPriceValu("0");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1:
                unitPriceValu("1");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
                unitPriceValu("2");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
                unitPriceValu("3");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4:
                unitPriceValu("4");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5:
                unitPriceValu("5");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                unitPriceValu("6");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
                unitPriceValu("7");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                unitPriceValu("8");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                unitPriceValu("9");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT: // 一键清除
                clearEvent();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ADD:// 价格修改操作
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER: // 打印
                keyEnter();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:// 手动输入去皮重量
                if (!isByWeight)
                    return true;
                createTareDialog();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY: // 计件计重切换
                changeWeightType();
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                clearProductInfo(true);
                return true;
            case KeyEvent.KEYCODE_E: // 小数点
                unitPriceValu(".");
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("RestrictedApi")
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

    /**
     * 时间刷新监听器 每分钟自动监听 注意：API要求必须动态注册
     */
    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(Intent.ACTION_TIME_TICK)) {
                mTimeView.setText(NumFormatUtil.getFormatDate());
            }
        }
    };

    /**
     * 业务数据周期性自检更新回调通知
     */
    private BroadcastReceiver mBusinessDataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(SjcConfig.ACTION_UPDATE_BUSINESS_DATA)) {
                String notifyType = intent.getStringExtra(SjcUpdatePoster.KEY_INTENT);
                LogUtil.d("BusinessDataUpdateReceiver", ScaleActivity.class.getSimpleName()
                        + ": RECEIVE RELOAD MESSAGE : " + notifyType);
                switch (notifyType) {
                    case SjcConfig.UPDATE_PRODUCT:// 品名库更新
                        clearProductInfo(true);
                        updateViews();
                        break;
                    case SjcConfig.UPDATE_PAY_TYPE:// 支付方式更新
                        CacheHelper.preparePayTypeInfoList();
                        break;
                    case SjcConfig.UPDATE_DEVICE_INFO:// 设备信息更新
                        CacheHelper.prepareTermConfig();
                        break;
                    case SjcConfig.UPDATE_PARAM_INFO:// 下发参数配置更新
                        CacheHelper.prepareParamInfo();
                        break;
                }
            }
        }
    };

    private RecognizeProductDialog mLastRecognizeDialog;
    private boolean isBindImageRecognizeService = false;
    private ImageRecognizeService.ImageRecognizeBinder mImageRecognizeBinder;

    /**
     * 图片识别服务连接
     */
    private ServiceConnection mImageRecognizeConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBindImageRecognizeService = true;
            mImageRecognizeBinder = (ImageRecognizeService.ImageRecognizeBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBindImageRecognizeService = false;
            mImageRecognizeBinder = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scalepage);
        EventBus.getDefault().register(this);
        initViews();
        requestUSBPermission();
        register();
        initData();
        initJNI();
        initListeners();
        timer();
    }

    /**
     * 接收识别结果
     *
     * @param recognizeMessage 识别结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveImageRecognizeResult(@NonNull RecognizeMessage recognizeMessage) {
        // TODO: 2019/9/2 测试
        if (CacheHelper.isPrintRecognizeLog) {
            List<RecognizeResult> serverData = recognizeMessage.serverData;
            if (serverData != null) {
                mCustomPrinter.printRecognizeListResult(serverData);
            }
        }

        // 关闭等待对话框
        dismissLoading();
        boolean isSuccess = recognizeMessage.isSuccess;
        if (isSuccess) {
            List<SjcProduct> similarProducts = recognizeMessage.similarProducts;
            if (similarProducts != null) {

                if (CacheHelper.isPrintRecognizeLog) {
                    // TODO: 2019/9/2 测试
                    mCustomPrinter.printRecognizeProducts(similarProducts);
                }

                int size = similarProducts.size();
                if (size <= 0) {
                    // 无匹配项目
                    showMessage("无匹配食材");
                } else if (size == 1) {
                    // 唯一匹配项目
                    mSelectedProduct = similarProducts.get(0);
                    isByWeight = mSelectedProduct.getPriceType().equals(PriceType.BY_PRICE);
                    showProductName();
                } else {
                    // 多匹配项目
                    RecognizeProductDialog dialog = RecognizeProductDialog.getInstance(recognizeMessage);
                    dialog.show(getSupportFragmentManager(), "RecognizeProductDialog");
                    mLastRecognizeDialog = dialog;
                }
            } else {
                showMessage("无匹配食材");
            }
        } else {
            showMessage("果蔬识别失败");
        }
    }

    private void startImageRecognizeService() {
        Intent intent = new Intent(this, ImageRecognizeService.class);
        bindService(intent, mImageRecognizeConnection, BIND_AUTO_CREATE);
    }

    private void requestUSBPermission() {
        try {
            BasicOper.dc_AUSB_ReqPermission(this);
        } catch (UnsatisfiedLinkError e) {
            LogUtil.e(e.getMessage());
        }
    }

    public void register() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(timeReceiver, timeFilter);
        IntentFilter filter = new IntentFilter();
        filter.addAction(SjcConfig.ACTION_UPDATE_BUSINESS_DATA);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBusinessDataUpdateReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBusinessDataUpdateReceiver);
        stopService(getSkipIntent(BatteryService.class));
        stopService(getSkipIntent(DisplayService.class));
        if (isBindImageRecognizeService) {
            unbindService(mImageRecognizeConnection);
            stopService(new Intent(this, ImageRecognizeService.class));
        }
        unregisterReceiver(timeReceiver);
        if (mLocalServer != null && mLocalServer.wasStarted())
            mLocalServer.stop();
        mLocalServer = null;
        advertThread.shutdownNow();
        timerThreads.shutdownNow();
        mScaleHandler.removeCallbacksAndMessages(null);
        CacheHelper.cleanLocalCache();
        mController.cleanPresentations();
        if (mCameraHelper != null) {
            mCameraHelper.releaseCamera();
        }
        mProductList.clear();
        mProductList = null;
        btnList.clear();
        btnList = null;
        Fn.clear();
        Fn = null;
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private void doPay() {
        OrderInfo info = null;
        if (!Fn.isEmpty()) {
            info = toBean();
        } else if (initSubAndDetail()) {
            markRecord();
            mSubtotal.setPayStatus(0);
            info = new OrderInfo(mSubtotal, mDetail);
        }
        if (info != null) {
            Intent intent = getSkipIntent(SjcPayActivity.class);
            intent.putExtra(OrderInfo.class.getSimpleName(), info);
            intent.putExtra(SjcConfig.KEY_CLEAR_ORDER_ACTIVITY, this.getClass().getSimpleName());
            startActivity(intent);
        } else {
            showMessage(R.string.total_tips);
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.pay:
                doPay();
                break;
            case R.id.printout:
                keyEnter();
                break;
            case R.id.scalelayout_fn:
                Intent intent = getSkipIntent(TotalActivity.class);
                switch (RECUR_FLAG) {
                    case 1:
                        intent.putExtra("Customer", 1);
                        break;
                    case 2:
                        intent.putExtra("Customer", 2);
                        break;
                    case 3:
                        intent.putExtra("Customer", 3);
                        break;
                    case 4:
                        intent.putExtra("Customer", 4);
                        break;
                }
                startActivity(intent);
                break;
        }
    }

    /**
     * 准备打印
     */
    private void keyEnter() {
        if (!Fn.isEmpty()) {
            printTotalListOrder();
        } else if (isByWeight) {
            if (isStable() && initSubAndDetail()) {
                print();
            } else if (!CacheHelper.orderCache.isEmpty()) {
                createReprintShow();
            }
        } else if (initSubAndDetail()) {
            print();
        } else if (!CacheHelper.orderCache.isEmpty()) {
            createReprintShow();
        }
    }

    private void printTotalListOrder() {
        OrderInfo info = toBean();
        mCustomPrinter.printOrder(info, () -> {
            // 打印缓存
            preReprint(info);
        });
        record(false, info);
        if (!Fn.isEmpty()) {
            Fn.clear();
            recurFnContent(RECUR_FLAG);
        }
    }

    /**
     * 生成累计销售小计
     */
    private SjcSubtotal getSubtotal(List<SjcDetail> list) {
        String transOrderCode = NumFormatUtil.createSerialNum();
        String transDate = NumFormatUtil.getDateDetail();
        BigDecimal transAmt = getTotalMoney(list);
        return new SjcSubtotal(
                transOrderCode,
                transDate,
                TransType.NORMAL,
                transAmt
        );
    }

    /**
     * 初始化订单信息
     */
    private OrderInfo toBean() {
        SjcSubtotal subtotal = getSubtotal(Fn);
        subtotal.setPayStatus(2);
        for (SjcDetail detail : Fn) {
            detail.setTransOrderCode(subtotal.getTransOrderCode());
        }
        return new OrderInfo(subtotal, Fn);
    }

    /**
     * 打印
     */
    private void print() {
        // 支付成功
        mSubtotal.setPayStatus(2);
        OrderInfo info = new OrderInfo(mSubtotal, mDetail);
        mCustomPrinter.printOrder(info, () -> {
            // 打印缓存
            preReprint(info);

        });
        markRecord();
        record(false, info);
    }

    /**
     * 储存重印准备
     */
    void preReprint(OrderInfo info) {
        LocalFileTag tag = CacheHelper.addOrderToCache(info);
        if (!tag.isSuccess()) {
            showMessage(tag.getContent());
        }
    }

    private void initViews() {
        mTimeView = (TextView) findViewById(R.id.timer);
        mTimeView.setText(NumFormatUtil.getFormatDate());
        mTareTextView = (TextView) findViewById(R.id.tarestring);
        mTvSubtotal = (TextView) findViewById(R.id.subtotal);
        mTvUnitPrice = (TextView) findViewById(R.id.unitprice);
        mPriceUnitView = (TextView) findViewById(R.id.price_unit);
        vegenames = (TextView) findViewById(R.id.vegenames);
        mTvWeight = (TextView) findViewById(R.id.weight);
        mTvTare = (TextView) findViewById(R.id.tare);
        mTvWeightUnit = (TextView) findViewById(R.id.weight_unit);
        mTvWeightType = (TextView) findViewById(R.id.bypiece);
        flippart = (LinearLayout) findViewById(R.id.flippart);
        numberpart = (LinearLayout) findViewById(R.id.number);
        totalvegenum = (TextView) findViewById(R.id.totalvegenum);
        allvegeprice = (TextView) findViewById(R.id.allvegeprice);
        fnshow = (TextView) findViewById(R.id.fnshow);
        mMaxUnitView = (TextView) findViewById(R.id.maxUnit);
        TextView versionView = (TextView) findViewById(R.id.version);
        mTareUnitView = (TextView) findViewById(R.id.tare_unit);
        mBatteryIv = (ImageView) findViewById(R.id.battery);
        setPieceNum(mTvWeight);
        setTextChanged(mTvUnitPrice, 3);
        setTextChanged(mTvTare, 1);
        setTextChanged(mTvSubtotal, 4);
        setTextChanged(vegenames, 2);
        showVersionCode(versionView);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mNumUtil = NumFormatUtil.getInstance();
        SurfaceView surfaceView = findViewById(R.id.image_preview);
        if (CacheHelper.isOpenCamera) {
            startImageRecognizeService();
            surfaceView.setVisibility(View.VISIBLE);
            mCameraHelper = new CameraHelper(this, surfaceView);
        } else {
            surfaceView.setVisibility(View.GONE);
        }
        updateViews();
        try {
            mLocalServer = new LocalServer();
            mLocalServer.start();
        } catch (IOException e) {
            LogUtil.e(e.getMessage());
        }
    }

    private void initJNI() {
        try {
            mScale = JNIScale.getScale();
            divisionValue = mScale.getDivisionValue();
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

    private void initListeners() {
        Button num0 = (Button) findViewById(R.id.num0);
        Button num1 = (Button) findViewById(R.id.num1);
        Button num2 = (Button) findViewById(R.id.num2);
        Button num3 = (Button) findViewById(R.id.num3);
        Button num4 = (Button) findViewById(R.id.num4);
        Button num5 = (Button) findViewById(R.id.num5);
        Button num6 = (Button) findViewById(R.id.num6);
        Button num7 = (Button) findViewById(R.id.num7);
        Button num8 = (Button) findViewById(R.id.num8);
        Button num9 = (Button) findViewById(R.id.num9);
        Button PLU1 = (Button) findViewById(R.id.plu1);
        Button PLU2 = (Button) findViewById(R.id.plu2);
        Button PLU3 = (Button) findViewById(R.id.plu3);
        Button PLU4 = (Button) findViewById(R.id.plu4);
        Button PLU5 = (Button) findViewById(R.id.plu5);
        Button PLU6 = (Button) findViewById(R.id.plu6);
        Button PLU7 = (Button) findViewById(R.id.plu7);
        Button PLU8 = (Button) findViewById(R.id.plu8);
        Button PLU9 = (Button) findViewById(R.id.plu9);
        Button PLU10 = (Button) findViewById(R.id.plu10);
        Button PLU11 = (Button) findViewById(R.id.plu11);
        MyPluListener myPluListener = new MyPluListener();
        num0.setOnClickListener(myPluListener);
        num1.setOnClickListener(myPluListener);
        num2.setOnClickListener(myPluListener);
        num3.setOnClickListener(myPluListener);
        num4.setOnClickListener(myPluListener);
        num5.setOnClickListener(myPluListener);
        num6.setOnClickListener(myPluListener);
        num7.setOnClickListener(myPluListener);
        num8.setOnClickListener(myPluListener);
        num9.setOnClickListener(myPluListener);
        PLU1.setOnClickListener(myPluListener);
        PLU2.setOnClickListener(myPluListener);
        PLU3.setOnClickListener(myPluListener);
        PLU4.setOnClickListener(myPluListener);
        PLU5.setOnClickListener(myPluListener);
        PLU6.setOnClickListener(myPluListener);
        PLU7.setOnClickListener(myPluListener);
        PLU8.setOnClickListener(myPluListener);
        PLU9.setOnClickListener(myPluListener);
        PLU10.setOnClickListener(myPluListener);
        PLU11.setOnClickListener(myPluListener);
        btnList.add(PLU1);
        btnList.add(PLU2);
        btnList.add(PLU3);
        btnList.add(PLU4);
        btnList.add(PLU5);
        btnList.add(PLU6);
        btnList.add(PLU7);
        btnList.add(PLU8);
        btnList.add(PLU9);
        btnList.add(PLU10);
        btnList.add(PLU11);
        findViewById(R.id.printout).setOnClickListener(this);
        findViewById(R.id.pay).setOnClickListener(this);
        findViewById(R.id.scalelayout_fn).setOnClickListener(this);
        findViewById(R.id.pluorunm).setOnClickListener(myPluListener);
        findViewById(R.id.accuxbutton).setOnClickListener(myPluListener);
        findViewById(R.id.dotnum).setOnClickListener(myPluListener);
        findViewById(R.id.numdele).setOnClickListener(myPluListener);
        findViewById(R.id.morebutton).setOnClickListener(myPluListener);
    }

    // 定时器
    private void timer() {

        timerThreads.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                mScaleHandler.sendEmptyMessage(1);

                try {
                    if (isStable()) {

                        currWeight = mScale.getFloatNet();

                        String sumStr = mTvSubtotal.getText().toString().trim();
                        boolean isWeighting = (currWeight > 5 * divisionValue) || !sumStr.equals("0.00");
                        mController.setWeighting(isWeighting);

                        boolean isWeightShowing = mController.isShowing(DisplayType.DISPLAY_WEIGHT);
                        if (isDisplay && isWeighting && !isWeightShowing) {
                            DisplayPoster.notifyDisplayAsyncShow(DisplayType.DISPLAY_WEIGHT);
                        }

                        if (CacheHelper.isOpenForceRecord && lastWeight > 0 && currWeight <= threshold * lastWeight && isByWeight) {

                            if (tareFloat > 0) {
                                currWeight -= tareFloat;
                            }

                            boolean isInitRecord = initSubAndDetail();

                            Log.i("forceRecord", "isInitRecord = " + isInitRecord);

                            boolean latestDetail = isLatestDetail();

                            Log.i("forceRecord", "是否和上一笔详情相同 = " + latestDetail);

                            if (CacheHelper.isSaved && latestDetail) {
                                CacheHelper.isSaved = false;
                                lastWeight = currWeight;
                                return;
                            }

                            // 开启强制记录时进入
                            if (isInitRecord) {
                                Log.i("forceRecord", "进入强制记录流程！");
                                forceRecord();
                                Log.i("forceRecord", "强制记录流程结束！");
                            }
                        }
                        lastWeight = currWeight;
                    }

                } catch (Exception ex) {

                }

            }
        }, 1500, 50, TimeUnit.MILLISECONDS);

        if (CacheHelper.isOpenCamera) {
            timerThreads.scheduleAtFixedRate(mImageRunnable, 2000, 50, TimeUnit.MILLISECONDS);
        }
    }

    private Runnable mImageRunnable = new Runnable() {
        @Override
        public void run() {
            if (currWeight > divisionValue * 2) {
                if (camerIsEnable) {
                    camerIsEnable = false;
                    mScaleHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mCameraHelper.getPreviewImage();
                        }
                    });
                }
            } else {
                camerIsEnable = true;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEncodeImage(ImageEncodeMessage message) {
        if (message != null) {
//            // TODO: 2019/9/2 测试
//            ImageView imageView = findViewById(R.id.image_result);
//            imageView.setImageBitmap(message.bitmap);

            String data = message.data;
            imageRecognize(data);
            Bitmap bitmap = message.bitmap;
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }
    }

    private void imageRecognize(String encodeImage) {
        if (isBindImageRecognizeService && mImageRecognizeBinder != null) {
            showLoading("正在识别");
            // 5s超时 强制关闭loading
            mScaleHandler.postDelayed(this::dismissLoading, 5000);
            mImageRecognizeBinder.productImageRecognizeStart(encodeImage);
            if (mLastRecognizeDialog != null && mLastRecognizeDialog.isVisible()) {
                mLastRecognizeDialog.dismiss();
            }
        }
    }

    /**
     * 将当前订单标记为已记录
     */
    private void markRecord() {
        CacheHelper.isSaved = true;
        CacheHelper.latestDetail = mDetail;
        Log.i("forceRecord", "markRecord");
        Log.i("forceRecord", CacheHelper.latestDetail.getGoodsName() + "|" + CacheHelper.latestDetail.getGoodsCode() + "|" + CacheHelper.latestDetail.getDealAmt().toString());
    }

    public void setPieceNum(final TextView textView) {
        textView.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!isByWeight) {
                    if (s.toString().contains(".")) {
                        s = s.toString().subSequence(0, s.toString().length() - 1);
                        textView.setText(s);
                    }
                }
                backDisplay.showWeight(s.toString());
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // 单价变化监听器
    public void setTextChanged(TextView tv, int flag) {
        MyTextWatcher watcher = new MyTextWatcher(flag);
        tv.addTextChangedListener(watcher);
    }

    private void showVersionCode(TextView tv) {
        String content = "V" + App.VersionName;
        if (tv != null) {
            if (!AppConfig.isDeploy) {
                content += "测试版";
            }
            tv.setText(content);
        }
    }

    private void updateViews() {
        sqlQueryThread.submit(new Runnable() {

            @Override
            public void run() {
                SjcProductDao productDao = AppDatabase.getInstance().getProductDao();
                mProductList = productDao.loadAll();
                List<SjcProduct> result = checkSelectedData(mProductList);
                if (!result.isEmpty()) {
                    mProductList.clear();
                    mProductList.addAll(result);
                }
                mScaleHandler.sendEmptyMessage(3);
            }
        });
    }

    /**
     * 初始化订单信息
     */
    boolean initSubAndDetail() {
        String priceString = mTvUnitPrice.getText().toString().trim();

        if (!NumFormatUtil.isNumeric(priceString))
            priceString = getString(R.string.base_price);

        String weightString = mTvWeight.getText().toString().trim();
        if (!NumFormatUtil.isNumeric(weightString))
            weightString = getString(R.string.base_weight);

        BigDecimal bigPrice = mNumUtil.getDecimalPrice(priceString);
        BigDecimal bigLastW = mNumUtil.getDecimalNet(String.valueOf(lastWeight));
        if (CacheHelper.isOpenJin && isByWeight) {
            BigDecimal rate = new BigDecimal(2.0);
            bigPrice = bigPrice.multiply(rate);
        }

        BigDecimal num = isByWeight ? bigLastW : mNumUtil.getDecimalPiece(weightString);
        BigDecimal sum = mNumUtil.getDecimalSum(bigPrice, num);
        Log.i("forceRecord", "weight = " + num.toString() + "| sum = " + sum.toString());
        if (sum.compareTo(BigDecimal.ZERO) > 0 && IsDisplaySum()) {
            toBean(sum, bigPrice, num);
            return true;
        }
        return false;
    }

    private void forceRecord() {
        if (mSubtotal == null) {
            Log.i("forceRecord", "强制记录数据失败！");
            return;
        }
        mSubtotal.setTransType(TransType.FORCE_RECORD);
        OrderInfo info = new OrderInfo(mSubtotal, mDetail);
        Log.i("forceRecord", "强制记录数据订单生成成功！");
        recordSync(info);
        Log.i("forceRecord", "强制记录数据订单插入成功！");
        preReprint(info);
        Log.i("forceRecord", "强制记录数据订单打印缓存成功！");
        showMessage("强制记录数据!");
    }

    /**
     * 当前交易明细是否是上笔交易明细
     */
    protected boolean isLatestDetail() {
        if (CacheHelper.latestDetail != null) {
            Log.i("forceRecord", CacheHelper.latestDetail.getGoodsName() + "|" + CacheHelper.latestDetail.getGoodsCode() + "|" + CacheHelper.latestDetail.getDealAmt().toString());
            Log.i("forceRecord", mDetail.getGoodsName() + "|" + mDetail.getGoodsCode() + "|" + mDetail.getDealAmt().toString());
        }
        return CacheHelper.latestDetail != null && (
//                CacheHelper.latestDetail.getPriceType().equals(PriceType.BY_PIECE) ||
                CacheHelper.latestDetail.getGoodsCode().equals(mDetail.getGoodsCode()) &&
                        CacheHelper.latestDetail.getDealAmt().compareTo(mDetail.getDealAmt()) == 0);
    }

    private void record(boolean isSync, final OrderInfo oi) {
        if (isSync) {
            recordSync(oi);
        } else {
            AppExecutors.getInstance().insertIO().submit(new Runnable() {
                @Override
                public void run() {
                    recordSync(oi);
                }
            });
        }
    }

    private void recordSync(final OrderInfo oi) {
        SjcSubtotal subtotal = oi.getSjcSubtotal();
        List<SjcDetail> sjcDetails = oi.getSjcDetails();
        AppDatabase database = AppDatabase.getInstance();
        database.beginTransaction();
        try {
            database.getSubtotalDao().save(subtotal);
            database.getDetailDao().save(sjcDetails);
            database.setTransactionSuccessful();
            String transOrderCode = subtotal.getTransOrderCode();
            LogUtil.d("订单插入数据库成功！" + transOrderCode);
        } catch (Exception e) {
            LogUtil.e("订单插入数据库失败！" + e.getMessage());
        } finally {
            database.endTransaction();
            clearProductInfo();
            // 创建订单上传任务
            Gson gson = new Gson();
            String jsonDataStr = gson.toJson(oi);
            uploadOrder(jsonDataStr);
        }
    }

    /**
     * 创建并提交订单上传任务
     *
     * @param jsonDataStr 订单JSON字串
     */
    private void uploadOrder(final String jsonDataStr) {
        mScaleHandler.post(new Runnable() {
            @Override
            public void run() {
                @SuppressLint("RestrictedApi")
                Data inputData = new Data.Builder()
                        .put(UploadOrderWork.JSON_DATA_KEY, jsonDataStr)
                        .build();
                OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UploadOrderWork.class)
                        .setInputData(inputData)
                        .build();
                WorkManager.getInstance().enqueue(request);
            }
        });
    }

    /**
     * 订单信息的初始化
     */
    protected void toBean(BigDecimal transAmt, BigDecimal price, BigDecimal number) {

        String transOrderCode = NumFormatUtil.createSerialNum();

        mSubtotal = new SjcSubtotal(transOrderCode,
                NumFormatUtil.getDateDetail(),
                TransType.NORMAL,
                transAmt);

        BigDecimal bigTare = mNumUtil.getDecimalTare(String.valueOf(tareFloat));

        if (mSelectedProduct != null) {
            mDetail = SjcDetail.getSjcDetail(transOrderCode, mSelectedProduct);
        } else {
            mDetail = SjcDetail.getSjcDetail(transOrderCode);
            mDetail.setSalePrice(price);
            mDetail.setPriceType(isByWeight ? PriceType.BY_PRICE : PriceType.BY_PIECE);
            mDetail.setSaleUnit(isByWeight ? "/公斤" : "/件");
        }
        mDetail.setDealPrice(price);
        mDetail.setDealCnt(number);
        mDetail.setDealAmt(transAmt);
        mDetail.setDiscountAmt(BigDecimal.ZERO);
        mDetail.setNetWeight(bigTare.compareTo(BigDecimal.ZERO) < 0 ? BASIC_TARE : bigTare);
        Log.i("forceRecord", "toBean --- >   " + mDetail.getGoodsName() + "|" + mDetail.getGoodsCode() + "|" + mDetail.getDealAmt().toString());
    }

    void clearProductInfo(boolean flag) {
        if (flag) {
            cleanTareFloat();
            priceRealValue = "";
            mScaleHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSelectedProduct = null;
                    mTvUnitPrice.setText(R.string.base_price);
                    vegenames.setText("");
                    isByWeight = true;
                    toggleIsWeight();
                }
            });
        } else {
            clearProductInfo();
        }
    }

    /**
     * 创建并展示手动输入皮重对话框
     */
    void createTareDialog() {
        CustomInputTareDialog tareDialog = new CustomInputTareDialog(ScaleActivity.this, CacheHelper.isOpenJin);
        tareDialog.show();
        tareDialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomInputTareDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomInputTareDialog dialog, String weight) {
                setTareFloat(weight);
                dialog.dismiss();
            }
        });
        tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, new CustomInputTareDialog.OnNegativeClickListener() {
            @Override
            public void onNegativeClick(CustomInputTareDialog dialog) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 设置扣重
     */
    private void setTareFloat(String weight) {
        if (weight.equals(".")) {
            tareFloat = -1.0F;
        } else if (NumFormatUtil.isNumeric(weight)) {
            tareFloat = Float.parseFloat(weight);
        }
    }

    /**
     * 清除单价与重量
     */
    protected void clearEvent() {
        if (isByWeight) {// 计重
            if (CacheHelper.isChangePrice) {
                priceRealValue = "";
                mTvUnitPrice.setText(R.string.base_price);
            }
        } else {// 计件
            mTvWeight.setText(R.string.base_weight_by_piece);
        }
    }

    /**
     * 撤销最后一个字符
     */
    private void undoLast() {
        if (isByWeight) { // 计重
            if (!priceRealValue.isEmpty()) {
                priceRealValue = priceRealValue.substring(0, priceRealValue.length() - 1);
                if (!priceRealValue.isEmpty() && priceRealValue.charAt(priceRealValue.length() - 1) == '.')
                    priceRealValue = priceRealValue.substring(0, priceRealValue.length() - 1);
            }
            displayPrice();
        } else { // 计件
            String tv = mTvWeight.getText().toString();
            if (!tv.isEmpty())
                mTvWeight.setText(tv.substring(0, tv.length() - 1));
        }
    }

    /**
     * 键盘数字显示 处理保留两位小数问题
     */
    private void unitPriceValu(String num) {
        if (isByWeight) {
            if (CacheHelper.isChangePrice) {
                if (isPlu) {
                    isPlu = false;
                    priceRealValue = "";
                }
                int dotIndex = priceRealValue.indexOf('.');
                if (dotIndex == -1 && !num.equals(".")) {
                    String ps = priceRealValue;
                    if (ps.length() >= 4)
                        return;
                }
                if (dotIndex > -1) {// 有小数点
                    if (num.equals("."))
                        return;
                    if (dotIndex >= priceRealValue.length() - 2)
                        priceRealValue += num;
                    else {
                        if (priceRealValue.charAt(priceRealValue.length() - 1) == '0')
                            priceRealValue += num;
                    }
                } else { // 没有小数点
                    if (num.equals(".")) {
                        if (priceRealValue.isEmpty())
                            priceRealValue = "0";
                    } else {
                        if (priceRealValue.equals("0"))
                            priceRealValue = "";
                    }
                    priceRealValue += num;
                }
                displayPrice();
            }
        } else {// 计件
            String iszero = mTvWeight.getText().toString().trim();
            boolean wzero = iszero.equals("0");
            if (wzero) {
                mTvWeight.setText(num);
            } else {
                if (iszero.length() >= 4)
                    return;
                mTvWeight.setText(iszero + num);
            }
        }
    }

    /**
     * 累加操作主方法
     */
    private void ACCCUx_add() {
        // 当秤稳定时 或是 计件模式时
        if (isStable() || !isByWeight) {
            String productName = vegenames.getText().toString();
            // 当开启了无PLU不显示总价 选项 并且 品名为空时
            if (CacheHelper.isNoPluNoSum && productName.isEmpty())
                return;

            String weight = mTvWeight.getText().toString().trim();

            if (TextUtils.isEmpty(weight) || !NumFormatUtil.isNumeric(weight))
                return;

            String price = mTvUnitPrice.getText().toString().trim();

            BigDecimal bigTare = mNumUtil.getDecimalTare(String.valueOf(tareFloat));
            BigDecimal bigPrice = mNumUtil.getDecimalPrice(price);
            BigDecimal number;

            if (isByWeight) {
                number = mNumUtil.getDecimalNet(weight);
                if (CacheHelper.isOpenJin) {
                    BigDecimal rate = new BigDecimal(2.0);
                    number = number.divide(rate);
                    bigPrice = bigPrice.multiply(rate);
                }
            } else {
                number = new BigDecimal(weight);
            }

            BigDecimal transAmt = mNumUtil.getDecimalSum(bigPrice, number);

            if (isCanAddUp(transAmt, number)) {
                if (mSelectedProduct != null) {
                    mDetail = SjcDetail.getSjcDetail("", mSelectedProduct);
                } else {
                    mDetail = SjcDetail.getSjcDetail("");
                    mDetail.setPriceType(isByWeight ? PriceType.BY_PRICE : PriceType.BY_PIECE);
                    mDetail.setSaleUnit(isByWeight ? "公斤" : "件");
                    mDetail.setSalePrice(bigPrice);
                }
                mDetail.setDealPrice(bigPrice);
                mDetail.setDealCnt(number);
                mDetail.setDealAmt(transAmt);
                mDetail.setDiscountAmt(BigDecimal.ZERO);
                mDetail.setNetWeight(bigTare.compareTo(BigDecimal.ZERO) < 0 ? BASIC_TARE : bigTare);
                Fn.add(mDetail);
                // 标记当前明细进入累计页面
                markRecord();
                clearProductInfo();
            }
        }
    }

    private boolean isCanAddUp(@NonNull BigDecimal amount, @NonNull BigDecimal weight) {
        return weight.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * 累加操作
     */
    private void ACCUx(int fn) {
        changeFnList(fn);
        ACCCUx_add();
        recurFnContent(fn);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        this.RECUR_FLAG = getIntent().getIntExtra("FNS", RECUR_FLAG);
        changeFnList(RECUR_FLAG);
    }

    private void changeFnList(int tag) {
        switch (tag) {
            case 1:
                Fn = CacheHelper.Detail_1;
                break;
            case 2:
                Fn = CacheHelper.Detail_2;
                break;
            case 3:
                Fn = CacheHelper.Detail_3;
                break;
            case 4:
                Fn = CacheHelper.Detail_4;
                break;
        }
    }

    @Override
    protected void onResume() {
        isDisplay = true;
        recurFnContent(RECUR_FLAG);
        setDefaultUnitView();
        mMaxUnitView.setText(String.format("%skg", mScale.getMainUnitFull()));
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        isDisplay = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDisplayUnitChange(QuantifyMessage message) {
        if (message == null)
            return;
        displayUnitShow();
        float curTare = mScale.getFloatTare();
        if (curTare != 0) {
            if (message.isOpenJin())
                curTare *= 2;
            mTvTare.setText(NumFormatUtil.df3.format(curTare));
        }
    }

    @Override
    public void recognizeProductClick(int position, SjcProduct product) {
        if (mLastRecognizeDialog != null) {
            mLastRecognizeDialog.dismiss();
        }
        mSelectedProduct = product;
        isByWeight = mSelectedProduct.getPriceType().equals(PriceType.BY_PRICE);
        showProductName();
    }

    private static class ScaleHandler extends Handler {

        WeakReference<ScaleActivity> mWeakReference;

        ScaleHandler(WeakReference<ScaleActivity> weakReference) {
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
                    case 3:
                        activity.assign();
                        break;
                }
            }
        }
    }

    private class MyTextWatcher implements TextWatcher {

        int flag;

        MyTextWatcher(int flag) {
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
                    backDisplay.showTare(s.toString());
                    break;
                case 2:
                    backDisplay.showProductName(s.toString());
                    break;
                case 3:
                    backDisplay.showPrice(s.toString());
                    break;
                case 4:
                    backDisplay.showAmount(s.toString());
                    break;
            }
        }
    }

    private class MyPluListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            mMisc.beep();
            switch (v.getId()) {
                case R.id.plu1:
                case R.id.plu2:
                case R.id.plu3:
                case R.id.plu4:
                case R.id.plu5:
                case R.id.plu6:
                case R.id.plu7:
                case R.id.plu8:
                case R.id.plu9:
                case R.id.plu10:
                case R.id.plu11:
                    Object obj = v.getTag();
                    if (obj instanceof SjcProduct) {
                        mSelectedProduct = (SjcProduct) obj;
                        isByWeight = mSelectedProduct.getPriceType().equals(PriceType.BY_PRICE);
                        showProductName();
                    }
                    break;
                case R.id.accuxbutton:
                    ACCUx(RECUR_FLAG);
                    break;
                case R.id.pluorunm:
                    if (flippart.getVisibility() == View.VISIBLE) {
                        flippart.setVisibility(View.GONE);
                        numberpart.setVisibility(View.VISIBLE);
                    } else {
                        flippart.setVisibility(View.VISIBLE);
                        numberpart.setVisibility(View.GONE);
                    }
                    break;
                case R.id.morebutton:
                    startActivity(SearchActivity.class);
                    break;
                case R.id.num0:
                    unitPriceValu("0");
                    break;
                case R.id.num1:
                    unitPriceValu("1");
                    break;
                case R.id.num2:
                    unitPriceValu("2");
                    break;
                case R.id.num3:
                    unitPriceValu("3");
                    break;
                case R.id.num4:
                    unitPriceValu("4");
                    break;
                case R.id.num5:
                    unitPriceValu("5");
                    break;
                case R.id.num6:
                    unitPriceValu("6");
                    break;
                case R.id.num7:
                    unitPriceValu("7");
                    break;
                case R.id.num8:
                    unitPriceValu("8");
                    break;
                case R.id.num9:
                    unitPriceValu("9");
                    break;
                case R.id.dotnum:
                    unitPriceValu(".");
                    break;
                case R.id.numdele:
                    clearEvent();
                    break;
            }
        }
    }
}
