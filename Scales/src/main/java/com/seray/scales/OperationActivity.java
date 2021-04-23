package com.seray.scales;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.seray.cache.AppConfig;
import com.seray.cache.AppConfig.ScaleType;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.entity.device.ConfigADB;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomTipDialog;
import com.tscale.scalelib.jniscale.JNIScale;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OperationActivity extends BaseActivity {

    private JNIScale mScale;
    private EditText et_calload, et_maxUnit;
    private TextView internelCount, weight;
    private Spinner mSpFdn, mSpBigFdn, mSpZeroRange, mSpRangeMode, mSpZeroTrack, mSpType;
    private Button demarcate, finishAll, maxUnitBtn, mZeroCodeBtn;
    private String[] arrDiv = {"1", "2", "5", "10", "20", "50", "100", "200", "500"};
    private String[] arrTrack = {"零点追踪范围0.0倍当前分度值", "零点追踪范围0.5倍当前分度值", "零点追踪范围1.0倍当前分度值",
            "零点追踪范围2.0倍当前分度值",
            "零点追踪范围4.0倍当前分度值"};
    private String[] types = {"T-200", "SR-200"};
    private String[] rangeModes = {"量程模式：单精度", "量程模式：双精度", "量程模式：双量程"};
    private String[] zeroRangeTabs = {"置零范围：0%", "置零范围：2%", "置零范围：3%", "置零范围：4%", "置零范围：10%",
            "置零范围：20%",
            "置零范围：50%", "置零范围：100%"};

    private OperationHandler mOperationHandler = new OperationHandler(new WeakReference
            <>(this));
    private boolean isDefaultFdn = true;
    private boolean isDefaultBigFdn = true;
    private boolean isDefaultTrack = true;
    private boolean isDefaultType = true;
    private boolean isDefaultRangeMode = true;
    private boolean isDefaultZeroRange = true;
    private int step = 0;
    private int zeroStep = 0;
    private int zeroISN = 0;
    private float calload = 0.00f;
    private Runnable timerRun = new Runnable() {
        @Override
        public void run() {
            mOperationHandler.sendEmptyMessage(1);
        }
    };
    /**
     * 执行定时任务固定数量线程池
     */
    private ScheduledExecutorService timerThreads = Executors.newScheduledThreadPool(1);

    private void _handleWeightChanged() {
        int count = mScale.getInternelCount();
        internelCount.setText(String.valueOf(count));
        weight.setText(mScale.getStringNet().trim());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operationaltest);
        //
        initJNI();
        initView();
        initData();
        initListener();
        timer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timerThreads.shutdownNow();
        mOperationHandler.removeCallbacksAndMessages(null);
    }

    private void initJNI() {
        mScale = JNIScale.getScale();
        mScale.setUnit(0);
        mScale.setMainUnitDeci(3);
        mScale.setStabDelay(40);//秤的稳定时间150  --> 0.75s
    }

    private void initView() {
        internelCount = (TextView) findViewById(R.id.internelCount);
        weight = (TextView) findViewById(R.id.weight);
        demarcate = (Button) findViewById(R.id.calibration);
        et_calload = (EditText) findViewById(R.id.et_calload);
        finishAll = (Button) findViewById(R.id.exitApp);
        mSpFdn = (Spinner) findViewById(R.id.setting_sp_fdn);
        mSpBigFdn = (Spinner) findViewById(R.id.setting_sp_bigFdn);
        mSpZeroTrack = (Spinner) findViewById(R.id.setting_sp_zerotrack);
        mSpRangeMode = (Spinner) findViewById(R.id.setting_sp_rangeMode);
        mSpType = (Spinner) findViewById(R.id.setting_sp_type);
        mSpZeroRange = (Spinner) findViewById(R.id.setting_sp_zeroRange);
        maxUnitBtn = (Button) findViewById(R.id.maxuint);
        et_maxUnit = (EditText) findViewById(R.id.et_maxuint);
        mZeroCodeBtn = (Button) findViewById(R.id.et_zero_btn);
    }

    private void initData() {
        mSpFdn.setAdapter(new ArrayAdapter<>(this, android.R.layout
                .simple_dropdown_item_1line, arrDiv));
        mSpBigFdn.setAdapter(new ArrayAdapter<>(this, android.R.layout
                .simple_dropdown_item_1line, arrDiv));
        mSpZeroTrack.setAdapter(new ArrayAdapter<>(this, android.R.layout
                .select_dialog_item, arrTrack));
        mSpRangeMode.setAdapter(new ArrayAdapter<>(this, android.R.layout
                .select_dialog_item, rangeModes));
        mSpZeroRange.setAdapter(new ArrayAdapter<>(this, android.R.layout
                .select_dialog_item, zeroRangeTabs));
        mSpType.setAdapter(new ArrayAdapter<>(this, android.R.layout.select_dialog_item,
                types));

        // 获取当前秤的最大量程 默认是0.0f
        float curMainUnit = mScale.getMainUnitFull();
        if (curMainUnit == 0.0f) {
            curMainUnit = AppConfig.isT200() ? 300.0f : 30.0f;
        }
        String unitStr = String.valueOf(curMainUnit);
        et_maxUnit.setText(unitStr);

        // 获取当前分度值[1] 默认是0
        mSpFdn.setSelection(mScale.getFdnPtr());

        // 获取当前分度值[2] 默认是0
        mSpBigFdn.setSelection(mScale.getBigFdnPtr());

        // 获取当前量程模式 默认是单精度
        mSpRangeMode.setSelection(mScale.getRangeMode());

        // 获取当前开机自动置零和手动置零的范围 默认都是0.0%
        int curAutoPtr = mScale.getAutoZeroRangePtr();
        int curManualPtr = mScale.getManualZeroRangePtr();
        if (curAutoPtr == 0 || curAutoPtr != curManualPtr) {
            boolean isSuccess = mScale.setAutoZeroRangePtr(curManualPtr);
            if (!isSuccess) {
                showMessage("设置开机自动置零范围失败");
            }
        }
        mSpZeroRange.setSelection(curManualPtr);

        // 获取当前零点追踪范围索引 默认是0倍当前分度值
        mSpZeroTrack.setSelection(mScale.getZeroTrackPtr());

        // 设置默认选中的秤模式
        if (AppConfig.isT200()) {
            mSpType.setSelection(0);
        } else if (AppConfig.isSR200()) {
            mSpType.setSelection(1);
        }
    }

    private void initListener() {
        et_calload.setOnKeyListener(new myOnKeyListener());
        et_maxUnit.setOnKeyListener(new myOnKeyListener());
        MyButtonListener btnListener = new MyButtonListener();
        weight.setOnClickListener(btnListener);
        demarcate.setOnClickListener(btnListener);
        finishAll.setOnClickListener(btnListener);
        maxUnitBtn.setOnClickListener(btnListener);
        mZeroCodeBtn.setOnClickListener(btnListener);
        mSpFdn.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDefaultFdn) {
                    isDefaultFdn = false;
                    return;
                }
                boolean isOk = mScale.setFdnPtr(position);
                if (!isOk) {
                    showMessage("分度值[1]设置失败");
                } else {
                    showMessage("分度值[1]设置成功");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpBigFdn.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDefaultBigFdn) {
                    isDefaultBigFdn = false;
                    return;
                }
                boolean isOk = mScale.setBigFdnPtr(position);
                if (!isOk) {
                    showMessage("分度值[2]设置失败");
                } else {
                    showMessage("分度值[2]设置成功");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpRangeMode.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDefaultRangeMode) {
                    isDefaultRangeMode = false;
                    return;
                }
                boolean isOk = mScale.setRangeMode(position);
                if (!isOk) {
                    showMessage("量程模式设置失败");
                } else {
                    showMessage("量程模式设置设置成功");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpZeroRange.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDefaultZeroRange) {
                    isDefaultZeroRange = false;
                    return;
                }
                boolean isManualOk = mScale.setManualZeroRangePtr(position);
                // 增加设置自动归零范围
                boolean isAutoOk = mScale.setAutoZeroRangePtr(position);
                if (isManualOk && isAutoOk) {
                    showMessage("置零范围设置成功");
                } else if (!isManualOk) {
                    showMessage("手动置零范围设置失败");
                } else {
                    showMessage("自动置零范围设置失败");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpZeroTrack.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDefaultTrack) {
                    isDefaultTrack = false;
                    return;
                }
                boolean isOk = mScale.setZeroTrackPtr(position);
                if (!isOk) {
                    showMessage("追踪范围设置失败");
                } else {
                    showMessage("追踪范围设置成功");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        mSpType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isDefaultType) {
                    isDefaultType = false;
                    return;
                }
                if (position == 0) {
                    AppConfig.SCALE_TYPE = ScaleType.T200;
                    mSpFdn.setSelection(4);
                    mSpRangeMode.setSelection(0);
                    et_maxUnit.setText(R.string.operation_max_weight_1);
                } else if (position == 1) {
                    AppConfig.SCALE_TYPE = ScaleType.SR200;
                    mSpFdn.setSelection(1);
                    mSpBigFdn.setSelection(2);
                    mSpRangeMode.setSelection(1);
                    et_maxUnit.setText(R.string.operation_max_weight_2);
                }
                AppConfig.saveScaleTypeToDb();
                showChangeScaleTypeDialog();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showChangeScaleTypeDialog() {
        CustomTipDialog tipDialog = new CustomTipDialog(this);
        tipDialog.show();
        tipDialog.setTitle(R.string.test_clear_title);
        tipDialog.setMessage(R.string.operation_demarcate_msg);
        tipDialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomTipDialog dialog) {
                dialog.dismiss();
            }
        });
    }

    private void timer() {
        timerThreads.scheduleAtFixedRate(timerRun, 1500, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 保存手动设置的最大量程
     */
    protected void setManualUnit() {
        String maxUnit = et_maxUnit.getText().toString();
        float inputMaxUnit;
        if (TextUtils.isEmpty(maxUnit)) {
            showMessage("请输入最大量程");
        } else if (!NumFormatUtil.isNumeric(maxUnit)) {
            showMessage("请输入正确的数字");
            et_maxUnit.setText("");
        } else {
            inputMaxUnit = Float.parseFloat(maxUnit);// 输入的最大量程
            if (inputMaxUnit > 300) {
                showMessage("最大量程大于300，请重新输入");
            } else if (inputMaxUnit > 0) {
                mScale.setMainUnitFull(inputMaxUnit);// 设置主单位称重量程
                float midUnit = inputMaxUnit / 2;
                mScale.setMainUnitMidFull(midUnit);// 设置主单位半量程
                float curFullUnit = mScale.getMainUnitFull();
                if (curFullUnit == inputMaxUnit) {// 获取设置最大量程操作后的最大量程，可行
                    showMessage("最大量程设置成功");
                } else {
                    showMessage("最大量程设置失败");
                }
            }
        }
    }

    private static class OperationHandler extends Handler {

        WeakReference<OperationActivity> mWeakReference;

        OperationHandler(WeakReference<OperationActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OperationActivity activity = mWeakReference.get();
            if (activity != null) {
                if (msg.what == 1) {
                    activity._handleWeightChanged();
                }
            }
        }
    }

    private class myOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            EditText view;

            if (keyCode == 66) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                return true;
            }

            if (v instanceof EditText) {
                view = (EditText) v;
            } else {
                return false;
            }

            if (event.getAction() == KeyEvent.ACTION_UP) {

                mMisc.beep();

                String txt = view.getText().toString();

                if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {

                    txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;

                } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {

                    txt += keyCode - KeyEvent.KEYCODE_0;

                } else if (keyCode == KeyEvent.KEYCODE_E) {
                    // 点
                    txt += ".";
                } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                    // 退格
                    if (!txt.isEmpty()) {
                        txt = txt.substring(0, txt.length() - 1);
                    }
                } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                    // 一键清除
                    txt = "";
                } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
                    return true;
                }
                view.setText(txt);
                view.setSelection(txt.length());
            }
            return true;
        }
    }

    private class MyButtonListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            mMisc.beep();
            switch (v.getId()) {
                case R.id.calibration:
                    if (step == 0) {
                        ((Button) (v)).setText("标定零点");
                        step++;
                    } else if (step == 1) {
                        if (et_calload.getText().toString().isEmpty()) {
                            step = 0;
                            ((Button) (v)).setText("标定");
                            break;
                        }
                        calload = Float.valueOf(et_calload.getText().toString().trim());
                        zeroISN = mScale.getInternelCount();
                        ((Button) (v)).setText("标定重量" + calload + "kg");
                        step++;
                    } else if (step == 2) {
                        int count = mScale.getInternelCount();
                        boolean cali = mScale.saveNormalCalibration(zeroISN, count, calload);
                        if (cali) {
                            showMessage("标定成功");
                        } else {
                            showMessage("标定失败");
                        }
                        ((Button) (v)).setText("标定");
                        step = 0;
                    }
                    break;
                case R.id.exitApp:
                    finish();
                    break;
                case R.id.maxuint:
                    setManualUnit();
                    break;
                case R.id.et_zero_btn:
                    TextView zeroTv = (TextView) findViewById(R.id.et_zero);
                    if (zeroStep == 0) {
                        mZeroCodeBtn.setText("保存零点值");
                        int zeroCode = mScale.getInternelCount();
                        zeroTv.setText(String.valueOf(zeroCode));
                        zeroStep++;
                    } else if (zeroStep == 1) {
                        String text = zeroTv.getText().toString();
                        saveZeroCodeValue(text);
                        showMessage("零点内码保存成功！");
                        mZeroCodeBtn.setText("重新设置");
                        zeroStep = 0;
                    }
                    break;
            }
        }
    }

    private void saveZeroCodeValue(String text) {
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        ConfigADB configADB = new ConfigADB("ZeroCodeValue", text);
        configDao.save(configADB);
    }
}
