package com.seray.scales;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seray.cache.CacheHelper;
import com.seray.message.ClearOrderMsg;
import com.seray.message.LocalFileTag;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.adapter.SjcDetailAdapter;
import com.seray.sjc.annotation.TransType;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.entity.order.OrderInfo;
import com.seray.sjc.entity.order.SjcDetail;
import com.seray.sjc.entity.order.SjcSubtotal;
import com.seray.sjc.pay.SjcPayActivity;
import com.seray.sjc.work.UploadOrderWork;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomTipDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

/**
 * 累计页面
 */
public class TotalActivity extends BaseActivity implements SjcDetailAdapter.OnTotalDetailDeleteListener {

    private RecyclerView mListView;
    private TextView totalpage_Num, totalpage_totalMoney, mTotalCustomerTv;
    private Button fntotalpay, totalpageButton_f1, totalpageButton_f2, totalpageButton_f3,
            totalpageButton_f4;
    private List<SjcDetail> fnList = CacheHelper.Detail_1;
    private List<Button> btnList = new ArrayList<>();
    private int FN_FLAG = 1;
    private SjcDetailAdapter mAdapter;
    private Handler mTotalHandler = new Handler();

    /**
     * 还原客户按钮的显示
     */
    protected void clearCustomer() {
        int strIndex = CacheHelper.basicBtnText[FN_FLAG - 1];
        Button btn = btnList.get(FN_FLAG - 1);
        btn.setTag(false);
        btn.setText(strIndex);
    }

    /**
     * 清空订单集合刷新适配器显示
     */
    protected void clearCurrFn() {
        fnList.clear();
        mAdapter.notifyDataSetChanged();
        totalpage_Num.setText(R.string.base_weight_by_piece);
        totalpage_totalMoney.setText(R.string.base_price);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                keyEnter();
                return true;
            case KeyEvent.KEYCODE_BACK:
                backToMain();
                return true;
            case KeyEvent.KEYCODE_A:
                fnList = CacheHelper.Detail_1;
                showSelectedDetails(1);
                return true;
            case KeyEvent.KEYCODE_D:
                fnList = CacheHelper.Detail_2;
                showSelectedDetails(2);
                return true;
            case KeyEvent.KEYCODE_B:
                fnList = CacheHelper.Detail_3;
                showSelectedDetails(3);
                return true;
            case KeyEvent.KEYCODE_C:
                fnList = CacheHelper.Detail_4;
                showSelectedDetails(4);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT:
                clearAtOnce();
                break;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:
                clearCustomer();
                break;
            case KeyEvent.KEYCODE_DEL:// 语音

                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fntotalpage);
        EventBus.getDefault().register(this);
        initViews();
        initAdapter();
        initData();
        recurFnContent();
        initListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveClearInfoFromPay(ClearOrderMsg msg) {
        String msgMsg = msg.getMsg();
        if (msgMsg.equals(TotalActivity.class.getSimpleName())) {
            clearCustomer();
            clearCurrFn();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (btnList != null) {
            btnList.clear();
            btnList = null;
        }
    }

    public void onOnceClearClick(View view) {
        super.onClick(view);
        clearAtOnce();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.fntotalpay:
                goToPay();
                break;
            case R.id.close:
                backToMain();
                break;
            case R.id.totalpageButton_f1:
                fnList = CacheHelper.Detail_1;
                showSelectedDetails(1);
                break;
            case R.id.totalpageButton_f2:
                fnList = CacheHelper.Detail_2;
                showSelectedDetails(2);
                break;
            case R.id.totalpageButton_f3:
                fnList = CacheHelper.Detail_3;
                showSelectedDetails(3);
                break;
            case R.id.totalpageButton_f4:
                fnList = CacheHelper.Detail_4;
                showSelectedDetails(4);
                break;
        }
    }

    /**
     * 前往支付页面
     */
    private void goToPay() {
        if (fnList != null && !fnList.isEmpty()) {
            OrderInfo info = toBean();
            info.getSjcSubtotal().setPayStatus(0);
            Intent intent = getSkipIntent(SjcPayActivity.class);
            intent.putExtra(OrderInfo.class.getSimpleName(), info);
            intent.putExtra(SjcConfig.KEY_CLEAR_ORDER_ACTIVITY, this.getClass().getSimpleName());
            startActivity(intent);
        } else {
            showMessage(R.string.total_tips);
        }
    }

    /**
     * 携带当前选中客户返回主界面
     */
    protected void backToMain() {
        Intent intent = new Intent(TotalActivity.this, ScaleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("FNS", FN_FLAG);
        startActivity(intent);
    }

    /**
     * 初始化订单信息
     */
    private OrderInfo toBean() {
        SjcSubtotal subtotal = getSubtotal(fnList);
        for (SjcDetail detail : fnList) {
            detail.setTransOrderCode(subtotal.getTransOrderCode());
        }
        return new OrderInfo(subtotal, fnList);
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

    private void initViews() {
        mListView = findViewById(R.id.list_fntotal);
        mListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        totalpage_totalMoney = (TextView) findViewById(R.id.totalpage_totalMoney);
        fntotalpay = (Button) findViewById(R.id.fntotalpay);
        totalpage_Num = (TextView) findViewById(R.id.totalpage_Num);
        mTotalCustomerTv = (TextView) findViewById(R.id.total_customer_name);
        totalpageButton_f1 = (Button) findViewById(R.id.totalpageButton_f1);
        totalpageButton_f2 = (Button) findViewById(R.id.totalpageButton_f2);
        totalpageButton_f3 = (Button) findViewById(R.id.totalpageButton_f3);
        totalpageButton_f4 = (Button) findViewById(R.id.totalpageButton_f4);
        fntotalpay.setText(R.string.zhifu);
    }

    /**
     * 初始化ListView适配器
     */
    private void initAdapter() {
        mAdapter = new SjcDetailAdapter(fnList, this);
        mListView.setAdapter(mAdapter);
    }

    private void initData() {
        btnList.add(totalpageButton_f1);
        btnList.add(totalpageButton_f2);
        btnList.add(totalpageButton_f3);
        btnList.add(totalpageButton_f4);
    }

    /**
     * 恢复客户姓名
     */
    protected void recurFnContent() {
        Intent intent = getIntent();
        int SUM_ID = intent.getIntExtra("Customer", 1);
        showSelectedDetails(SUM_ID);
        for (int i = 0; i < CacheHelper.basicBtnText.length; i++) {
            int contentId = CacheHelper.basicBtnText[i];
            Button btn = btnList.get(i);
            btn.setText(getResources().getString(contentId));
        }
    }

    /**
     * 主页面以及window页面大按钮的点击事件
     */
    private void initListener() {
        fntotalpay.setOnClickListener(this);
        findViewById(R.id.close).setOnClickListener(this);
        totalpageButton_f1.setOnClickListener(this);
        totalpageButton_f2.setOnClickListener(this);
        totalpageButton_f3.setOnClickListener(this);
        totalpageButton_f4.setOnClickListener(this);
    }

    /**
     * 累计1累计2累计3累计4点击刷新
     */
    protected void showSelectedDetails(int flag) {
        totalAssign(flag);
        totalpageButton_change(flag);
        FN_FLAG = flag;
    }

    /**
     * 控制客户总价和笔数的方法
     */
    private void totalAssign(int fn) {
        switch (fn) {
            case 1:
                fnList = CacheHelper.Detail_1;
                break;
            case 2:
                fnList = CacheHelper.Detail_2;
                break;
            case 3:
                fnList = CacheHelper.Detail_3;
                break;
            case 4:
                fnList = CacheHelper.Detail_4;
                break;
        }
        mAdapter.setNewData(fnList);
        setTotalAmountShow();
    }

    /**
     * 切换客户背景色变换
     */
    private void totalpageButton_change(int fn) {
        switch (fn) {
            case 1:
                totalpageButton_f1.setBackgroundResource(R.drawable.fntopagebutton_change);
                totalpageButton_f2.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f3.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f4.setBackgroundResource(R.drawable.fntopagebutton);
                break;
            case 2:
                totalpageButton_f1.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f2.setBackgroundResource(R.drawable.fntopagebutton_change);
                totalpageButton_f3.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f4.setBackgroundResource(R.drawable.fntopagebutton);
                break;
            case 3:
                totalpageButton_f1.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f2.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f3.setBackgroundResource(R.drawable.fntopagebutton_change);
                totalpageButton_f4.setBackgroundResource(R.drawable.fntopagebutton);
                break;
            case 4:
                totalpageButton_f1.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f2.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f3.setBackgroundResource(R.drawable.fntopagebutton);
                totalpageButton_f4.setBackgroundResource(R.drawable.fntopagebutton_change);
                break;
        }
        String content = btnList.get(fn - 1).getText().toString();
        mTotalCustomerTv.setText(content);
    }

    private void setTotalAmountShow() {
        String total_Price = getTotalMoney(fnList).toString();
        int total_Num = fnList.size();
        totalpage_Num.setText(String.valueOf(total_Num));
        totalpage_totalMoney.setText(total_Price);
    }

    /**
     * 获取某客户订单总价主方法
     */
    private BigDecimal getTotalMoney(List<SjcDetail> list) {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < list.size(); i++) {
            BigDecimal amount = list.get(i).getDealAmt();
            sum = sum.add(amount);
        }
        return sum.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 准备打印
     */
    private void keyEnter() {
        if (fnList != null && !fnList.isEmpty()) {
            OrderInfo info = toBean();
            info.getSjcSubtotal().setPayStatus(2);
            record(info);
            print(info);
            clearCustomer();
            clearCurrFn();
            backToMain();
        } else if (fnList != null && !CacheHelper.orderCache.isEmpty()) {
            createReprintShow();
        }
    }

    private void clearAtOnce() {
        if (!fnList.isEmpty()) {
            CustomTipDialog dialog = new CustomTipDialog(this);
            dialog.show();
            dialog.setTitle(R.string.test_clear_title);
            dialog.setMessage(R.string.total_clear_detail_msg);
            dialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
                @Override
                public void onPositiveClick(CustomTipDialog dialog) {
                    clearCurrFn();
                    clearCustomer();
                }
            });
        } else {
            showMessage(R.string.total_clear_detail_error);
        }
    }

    /**
     * 将订单信息插入数据库
     */
    private void record(final OrderInfo info) {
        AppExecutors.getInstance().insertIO().submit(new Runnable() {
            @Override
            public void run() {
                SjcSubtotal subtotal = info.getSjcSubtotal();
                List<SjcDetail> sjcDetails = info.getSjcDetails();
                AppDatabase database = AppDatabase.getInstance();
                database.beginTransaction();
                try {
                    database.getSubtotalDao().save(subtotal);
                    database.getDetailDao().save(sjcDetails);
                    database.setTransactionSuccessful();
                    LogUtil.d("订单插入数据库成功！" + subtotal.getTransOrderCode());
                } catch (Exception e) {
                    LogUtil.e("订单插入数据库失败！" + e.getMessage());
                } finally {
                    database.endTransaction();
//                    // 创建订单上传任务
//                    Gson gson = new Gson();
//                    String jsonDataStr = gson.toJson(info);
//                    uploadOrder(jsonDataStr);
                }
            }
        });
    }

    /**
     * 创建并提交订单上传任务
     *
     * @param jsonDataStr 订单JSON字串
     */
    private void uploadOrder(final String jsonDataStr) {
        mTotalHandler.post(new Runnable() {
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
     * 打印订单信息
     */
    private void print(final OrderInfo info) {
        mCustomPrinter.printOrder(info, () -> {
            LocalFileTag tag = CacheHelper.addOrderToCache(info);
            if (!tag.isSuccess()) {
                showMessage(tag.getContent());
            }
        });
    }

    @Override
    public void onItemDeleteClick(final int position, String name, String number) {
        super.onClick(null);
        String msg = "商品：" + name + "，数量：" + number
                + "\n" + getString(R.string.total_clear_item_msg);

        CustomTipDialog dialog = new CustomTipDialog(TotalActivity.this);
        dialog.show();
        dialog.setTitle(R.string.test_clear_title);
        dialog.setMessage(msg);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomTipDialog dialog) {
                fnList.remove(position);
                mAdapter.notifyDataSetChanged();
                setTotalAmountShow();
            }
        });
    }
}
