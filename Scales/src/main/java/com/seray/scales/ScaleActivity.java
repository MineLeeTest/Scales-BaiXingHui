package com.seray.scales;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
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
import com.seray.sjc.api.result.ProductDZCDTO;
import com.seray.sjc.api.result.UserVipCardDetailDTO;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ProductDao;
import com.seray.sjc.entity.device.ProductADB;
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

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import retrofit2.Response;


/**
 * The type Scale activity.
 */
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
    private LinearLayout flippart;
    private ImageView mBatteryIv;
    private List<ProductADB> mProductList = new ArrayList<>();
    private ArrayList<Button> btnList = new ArrayList<>();
    private ProductADB proTradeNow = null;
    private float currWeight = 0.0f, tareFloat = -1.0F, lastWeight = 0.0f, divisionValue = 0.02f;
    private boolean isPlu = false, isByWeight = true;

    private BigDecimal BASIC_TARE = new BigDecimal("0.000");
    private ScheduledExecutorService advertThread = Executors.newScheduledThreadPool(1);
    private ScaleHandler mScaleHandler = new ScaleHandler(new WeakReference<>(this));
    private ICCardHandler icCardHandler = new ICCardHandler(new WeakReference<>(this));
    private JNIScale mScale;
    private NumFormatUtil mNumUtil = NumFormatUtil.getInstance();
    private BackDisplayBase backDisplay = null;
    private String priceRealValue = "";
    private int RECUR_FLAG = 1;

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
                    mTvTare.setText(String.format("(当前扣重：%s)", NumFormatUtil.df3.format(tare)));
                    mTvTare.setVisibility(View.VISIBLE);
                    mTvTare.setTextColor(Color.RED);
                } else {
                    mTvTare.setVisibility(View.INVISIBLE);
                }
                mTvWeight.setText(strNet);
            } else {
                if (tareFloat > 0) {
                    fW -= tare;
                    mTvTare.setText(String.format("(当前扣重：%s)", NumFormatUtil.df3.format(tare)));
                    mTvTare.setVisibility(View.VISIBLE);
                    mTvTare.setTextColor(Color.RED);
                } else {
                    mTvTare.setVisibility(View.INVISIBLE);
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
        }
    }

    /**
     * 判断秤稳定
     */
    private boolean isStable() {
        return mScale.getStabFlag();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(getSkipIntent(BatteryService.class));
        stopService(getSkipIntent(HeartBeatService.class));
        stopService(getSkipIntent(DisplayService.class));

        advertThread.shutdownNow();
        timerThreads.shutdownNow();
        mScaleHandler.removeCallbacksAndMessages(null);
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

        showPros();
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

        //配置自动计算功能
        mTvTare.addTextChangedListener(new TWShowBackDisplay(1)); //商品的皮重
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

    /**
     * 清除品名信息
     */
    void clearProductInfo() {
        priceRealValue = "";
        cleanTareFloat();
    }


    /**
     * 重置tareFloat
     */
    void cleanTareFloat() {
        tareFloat = -1.0F;
    }


    //
    private void changeWeightType() {
        isByWeight = true;
    }


    private BigDecimal getTotalMoney(List<ProductADB> list) {
        BigDecimal sum = new BigDecimal("0.00");
        for (int i = 0; i < list.size(); i++) {
            BigDecimal amount = BigDecimal.valueOf(list.get(i).getPrice());
            sum = sum.add(amount);
        }
        return sum;
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
            ProductADB product = mProductList.get(i);
            btnList.get(i).setTag(product);
            btnList.get(i).setText(product.getPro_name());
        }
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
//                clearEvent();

                return true;


            case KeyEvent.KEYCODE_NUMPAD_ADD:// 价格修改操作
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER: // 打印
//                keyEnter();
//                clearTraders();
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
                clearProductInfo();
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

//    public void clearTraders() {
//        icCardHandler.post(() -> {
//            System.out.println("---------clearTraders---------->");
//            seller.setText("卖家\n请刷卡");
//            buyer.setText("买家\n请刷卡");
//        });
//    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.pay:
//                doPay();
                showMessage("执行订单上传！");
                break;
            case R.id.printout:
//                keyEnter();
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


    private void updateViews() {
        sqlQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                ProductDao productDao = AppDatabase.getInstance().getProductDao();
                mProductList = productDao.loadAll();
                List<ProductADB> result = mProductList;
                if (!result.isEmpty()) {
                    mProductList.clear();
                    mProductList.addAll(result);
                }
                mScaleHandler.sendEmptyMessage(3);
            }
        });
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
//            String productName = vegenames.getText().toString();
            // 当开启了无PLU不显示总价 选项 并且 品名为空时
//            if (CacheHelper.isNoPluNoSum && productName.isEmpty())
//                return;

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

//            if (isCanAddUp(transAmt, number)) {
//                if (mSelectedProduct != null) {
//                    mDetail = SjcDetail.getSjcDetail("", mSelectedProduct);
//                } else {
//                    mDetail = SjcDetail.getSjcDetail("");
//                    mDetail.setPriceType(isByWeight ? PriceType.BY_PRICE : PriceType.BY_PIECE);
//                    mDetail.setSaleUnit(isByWeight ? "公斤" : "件");
//                    mDetail.setSalePrice(bigPrice);
//                }
//                mDetail.setDealPrice(bigPrice);
//                mDetail.setDealCnt(number);
//                mDetail.setDealAmt(transAmt);
//                mDetail.setDiscountAmt(BigDecimal.ZERO);
//                mDetail.setNetWeight(bigTare.compareTo(BigDecimal.ZERO) < 0 ? BASIC_TARE : bigTare);
//                clearProductInfo();
//            }
        }
    }


    /**
     * 累加操作
     */
    private void ACCUx(int fn) {
        ACCCUx_add();
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onICCardDataReceive(ICCardSerialPortUtil.DataReceType type, byte[] buffer, int size) {
        Message message = new Message();
        message.what = -1;
        if (ICCardSerialPortUtil.DataReceType.success == type) {
            if (buffer.length != 12) {
                message.obj = "卡号长度有误：" + buffer.length;
            }
            String cardID = ByteUtil.getCardNo(buffer);
            if (null == cardID) {
                message.obj = "解析卡号失败！";
            }
            ResultData resultData = getUserByICCard(cardID);

        } else {
            message.obj = "读取卡号失败！";
        }
        icCardHandler.sendMessage(message);
    }

    private ResultData getUserByICCard(String cardID) {
        ResultData resultData = new ResultData();
        try {
            GetUserByCardNoVM getUserByCardNoVM = new GetUserByCardNoVM();
            resultData = AESTools.encrypt(resultData, CacheHelper.device_aes_key, System.currentTimeMillis() + "," + CacheHelper.device_id);
            if (!resultData.isSuccess()) {
                return resultData;
            }
            String sign = resultData.getMsg() + "";
            getUserByCardNoVM.setCard_id(cardID);
            Response<ApiDataRsp<List<UserVipCardDetailDTO>>> response = HttpServicesFactory.getHttpServiceApi().getuserbycardno(CacheHelper.device_id + "", sign, getUserByCardNoVM).execute();
            if (response.isSuccessful()) {

            }
            return resultData;
        } catch (Exception e) {
//            return resultData.setRetMsg(resultData)
            return resultData;
        }
    }


    private static class ICCardHandler extends Handler {
        WeakReference<ScaleActivity> mWeakReference;

        ICCardHandler(WeakReference<ScaleActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ScaleActivity activity = mWeakReference.get();
            if (msg.what == -1) {
                activity.showMessage(msg.obj.toString());
            }
        }
    }


    //显示重量
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

    //在背面显示屏上显示单价、皮重、总价
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
                case 3:
                    backDisplay.showPrice(s.toString());//商品单价
                    break;
                case 4:
                    backDisplay.showAmount(s.toString());//商品总价
                    break;
            }
        }
    }


}
