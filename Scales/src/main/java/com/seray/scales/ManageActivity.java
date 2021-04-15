package com.seray.scales;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.seray.cache.CacheHelper;
import com.seray.information.InformationActivity;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.entity.device.Config;
import com.seray.sjc.entity.device.SjcParamInfo;
import com.seray.sjc.report.NewReportActivity;
//import com.seray.sjc.work.CheckSyncDataWork;
import com.seray.util.HttpUtils;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomTipDialog;
import com.seray.view.InputImageRecognizeDialog;

import java.util.UUID;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class ManageActivity extends BaseActivity {

    private Button mChangeWeightBtn;
    private Button mPopDismissBtn;
    private ImageView mAddTimeIv, mCutTimeIv;
    private int screenOffTime = 5 * 60;
    private int[] offTimeArr = {15, 30, 60, 2 * 60, 5 * 60, 10 * 60, 30 * 60, 60 * 60, 48 * 60 * 60};
    private int arrIndex = 4;//默认值5分钟
    private PopupWindow window;
    private TextView mScreenTimeView;
    private View popupView;
    private SeekBar mLightSeekBar;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.managepage);
        initViews();
        initListener();
        rebuildData();
    }

    // 商品分类
    public void onCategoryManagerClick(View view) {
        super.onClick(view);
        startActivity(SelectActivity.class);
    }

//    // 业务数据同步
//    public void onSyncBusinessDataClick(View view) {
//        super.onClick(view);
//        showLoading("正在同步");
//        // 强制更新 不使用已缓存的请求参数
//        Data inputData = new Data.Builder()
//                .putBoolean(CheckSyncDataWork.KEY_FORCE_UPDATE, true)
//                .build();
//
//        OneTimeWorkRequest syncDataWorkRequest = new OneTimeWorkRequest
//                .Builder(CheckSyncDataWork.class)
//                .setInputData(inputData)
//                .build();
//
//        final UUID requestId = syncDataWorkRequest.getId();
//        WorkManager.getInstance().enqueue(syncDataWorkRequest);
//        WorkManager.getInstance().getWorkInfoByIdLiveData(requestId)
//                .observe(this, workInfo -> {
//                    if (workInfo != null) {
//                        WorkInfo.State state = workInfo.getState();
//                        if (state.isFinished()) {
//                            dismissLoading();
//                            if (state == WorkInfo.State.SUCCEEDED) {
//                                showMessage("同步成功");
//                            } else if (state == WorkInfo.State.FAILED) {
//                                showMessage("同步失败");
//                            } else {
//                                showMessage("同步取消");
//                            }
//                        }
//                    }
//                });
//        // 以防超时 对话框无法关闭
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                WorkManager.getInstance().cancelWorkById(requestId);
//                // 确认对话框关闭
//                dismissLoading();
//            }
//        }, 7000);
//    }

    // 返回
    public void backToMain(View view) {
        super.onClick(view);
        finish();
    }

    // 重启
    public void onReStartClick(View view) {
        super.onClick(view);
        openRebootDialog();
    }

    // 去摊位信息
    public void startInformationActivity(View view) {
        super.onClick(view);
        startActivity(InformationActivity.class);
    }

    // 去报表
    public void startReportActivity(View view) {
        super.onClick(view);
        startActivity(NewReportActivity.class);
    }

    // 去运维管理中心
    public void startSettingActivity(View view) {
        super.onClick(view);
        openManageKey(NumFormatUtil.PASSWORD_TO_SETTING, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (window != null) {
            if (window.isShowing()) {
                window.dismiss();
            }
        }
        window = null;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.item3:
                Intent intent = new Intent();
                ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.ChooseLockGeneric");
                intent.setComponent(cm);
                startActivityForResult(intent, 0);
                break;
            case R.id.item4:
//                InputImageRecognizeDialog inputImageRecognizeDialog = new InputImageRecognizeDialog(this);
//                inputImageRecognizeDialog.show();
//                inputImageRecognizeDialog.setOnPositiveClickListener((dialog, value) -> {
//                    if (value <= 0 || value == CacheHelper.MinRecognizeValue) {
//                        return;
//                    }
//                    AppExecutors.getInstance().insertIO().submit(() -> {
//                        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
//                        Config config = new Config(SjcParamInfo.MinRecognizeValue, String.valueOf(value));
//                        configDao.save(config);
//                        // 更新数据
////                        CacheHelper.prepareParamInfo();
//                        runOnUiThread(this::changePopRecognizeValue);
//                    });
//                });
                break;
            case R.id.setting_pop_back_btn:
                if (window != null) {
                    window.dismiss();
                }
                break;
            case R.id.setting_pop_dismiss_btn:
                closePopupWindow();
                break;
            case R.id.setting:
                showPopupWindow();
                break;
            case R.id.setting_screenOffTime_add:
                if (arrIndex < offTimeArr.length - 1) {
                    arrIndex++;
                    screenOffTime = offTimeArr[arrIndex];
                    changeTimeShow();
                }
                break;
            case R.id.setting_screenOffTime_cut:
                if (arrIndex > 0) {
                    arrIndex--;
                    screenOffTime = offTimeArr[arrIndex];
                    changeTimeShow();
                }
                break;
            case R.id.mana_weight_type:
                String latestUnit = CacheHelper.isOpenJin ? getString(R.string.manager_scale_jin)
                        : getString(R.string.manager_scale_kg);
                String msg = getString(R.string.setting_unit_type) +
                        latestUnit + "\n" + getString(R.string.setting_unit_type_msg);
                CustomTipDialog tipDialog = new CustomTipDialog(this);
                tipDialog.show();
                tipDialog.setTitle(R.string.test_clear_title);
                tipDialog.setMessage(msg);
                tipDialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
                    @Override
                    public void onPositiveClick(CustomTipDialog dialog) {
                        dialog.dismiss();
                        openManageKey(NumFormatUtil.PASSWORD_TO_UNIT, mChangeWeightBtn);
                    }
                });
                break;
        }
    }

    private void changePopRecognizeValue() {
        TextView recognizeView = popupView.findViewById(R.id.manage_image_recognize_allow);
//        recognizeView.setText(String.format("%s%s", CacheHelper.MinRecognizeValue, "%"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                showMessage(R.string.manager_lock_screen_success);
            }
        }
    }

    private void showPopupWindow() {
        if (window == null) {
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            int width = metric.widthPixels;
            int height = metric.heightPixels;
            window = new PopupWindow(popupView, width, height);
            window.setAnimationStyle(R.style.popup_window_anim);
            window.setFocusable(true);
            window.setOutsideTouchable(false);
            window.update();
        }
        changePopRecognizeValue();
        LinearLayout ly = new LinearLayout(ManageActivity.this);
        window.showAtLocation(ly, Gravity.CENTER, 0, 0);
    }

    private void openRebootDialog() {
        CustomTipDialog dialog = new CustomTipDialog(ManageActivity.this);
        dialog.show();
        dialog.setTitle(R.string.test_clear_title);
        dialog.setMessage(R.string.operation_reboot);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomTipDialog dialog) {
                App.getApplication().rebootApp();
            }
        });
    }

    private void initViews() {
        popupView = getLayoutInflater().inflate(R.layout.manage_pop, null);
        popupView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        closePopupWindow();
                    }
                }
                return true;
            }
        });
        mScreenTimeView = (TextView) popupView.findViewById(R.id.setting_showScreenOffTime_tv);
        mLightSeekBar = (SeekBar) popupView.findViewById(R.id.setting_light_seekBar);
        mPopDismissBtn = (Button) popupView.findViewById(R.id.setting_pop_dismiss_btn);
        mAddTimeIv = (ImageView) popupView.findViewById(R.id.setting_screenOffTime_add);
        mCutTimeIv = (ImageView) popupView.findViewById(R.id.setting_screenOffTime_cut);
        LinearLayout lockView = (LinearLayout) popupView.findViewById(R.id.item3);
        lockView.setOnClickListener(this);

        popupView.findViewById(R.id.item4).setOnClickListener(this);
        changePopRecognizeValue();

        Button mPopBackBtn = (Button) popupView.findViewById(R.id.setting_pop_back_btn);
        mPopBackBtn.setOnClickListener(this);
        mChangeWeightBtn = (Button) findViewById(R.id.mana_weight_type);
        setDefaultContent();
    }

    private void initListener() {
        findViewById(R.id.setting).setOnClickListener(this);
        mChangeWeightBtn.setOnClickListener(this);
        mPopDismissBtn.setOnClickListener(this);
        mAddTimeIv.setOnClickListener(this);
        mCutTimeIv.setOnClickListener(this);
        mLightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int current = (int) (progress * 2.55);
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS,
                        current);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void rebuildData() {
        try {
            //获取当前的屏幕亮度
            int CurrentLight = Settings.System.getInt(getContentResolver(), Settings.System
                    .SCREEN_BRIGHTNESS);
            CurrentLight = CurrentLight * 100 / 254;
            mLightSeekBar.setProgress(CurrentLight);

            //获取当前的休眠时间 单位 秒
            screenOffTime = App.getApplication().getScreenOffTime() / 1000;
            for (int i = 0; i < offTimeArr.length; i++) {
                if (offTimeArr[i] == screenOffTime) {
                    arrIndex = i;
                    break;
                }
            }
            changeTimeShow();
        } catch (Settings.SettingNotFoundException e) {
            LogUtil.e(e.getMessage());
        }
    }

    private void closePopupWindow() {
        if (window != null) {
            if (window.isShowing())
                window.dismiss();
        }
        //保存当前选择的休眠时间
        int screenOffTime = offTimeArr[arrIndex] * 1000;
        boolean isSetScreenOffTimeSuccess = App.getApplication().setScreenOffTime(screenOffTime);
        if (isSetScreenOffTimeSuccess) {
            showMessage(R.string.setting_screenOffTime_success);
        }
    }

    private void setDefaultContent() {
        mChangeWeightBtn.setText(
                CacheHelper.isOpenJin ? R.string.manager_change_weight_unit_1 : R.string
                        .manager_change_weight_unit_2);
        TextView mIpTextView = (TextView) findViewById(R.id.mana_ip);
        String ipAddress = HttpUtils.getLocalIpStr(getApplication());
        mIpTextView.setText(ipAddress);
        TextView mScaleCodeView = (TextView) findViewById(R.id.mana_device_code);
        TextView mBoothIdView = (TextView) findViewById(R.id.mana_boothId);
//        if (!TextUtils.isEmpty(CacheHelper.TermCode)) {
//            String deviceId = getString(R.string.manager_device_id);
//            mScaleCodeView.setText(String.format("%s%s", deviceId, CacheHelper.TermCode));
//        }
//        if (!TextUtils.isEmpty(CacheHelper.BoothName)) {
//            String boothName = getString(R.string.manager_booth_name);
//            mBoothIdView.setText(String.format("%s%s", boothName, CacheHelper.BoothName));
//        }
    }

    private void changeTimeShow() {
        if (screenOffTime > 60 && screenOffTime <= 60 * 60) {
            mScreenTimeView.setText(screenOffTime / 60 + getString(R.string
                    .manager_screen_time_unit_2));
        } else if (screenOffTime > 60 * 60) {
            mScreenTimeView.setText(screenOffTime / 60 / 60 + getString(R.string
                    .manager_screen_time_unit_3));
        } else {
            mScreenTimeView.setText(screenOffTime + getString(R.string.manager_screen_time_unit_1));
        }
    }
}
