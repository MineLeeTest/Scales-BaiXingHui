package com.seray.scales;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;

import com.seray.cache.CacheHelper;
import com.seray.service.BatteryService;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.ActivateReq;
import com.seray.sjc.api.result.ActivateRsp;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.entity.device.Config;
import com.seray.sjc.entity.message.SyncDataWorkMessage;
import com.seray.sjc.view.ActivateDialogFragment;
import com.seray.sjc.view.SetWebDialogFragment;
import com.seray.sjc.work.CheckBrokenWork;
import com.seray.sjc.work.CheckSyncDataWork;
import com.seray.sjc.work.CheckUnUploadWork;
import com.seray.sjc.work.LocalDataCleanWork;
import com.seray.sjc.work.UploadHeartWork;
import com.seray.util.DateUtil;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomTipDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends BaseActivity implements View.OnClickListener, ActivateDialogFragment.activateInterface, SetWebDialogFragment.SetwebInrerface {

    private StartHandler mHandler = new StartHandler(new WeakReference<>(this));

    private ActivateDialogFragment dialogFragment;

    private SetWebDialogFragment setWebDialogFragment;

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(StartActivity.this, ScaleActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };

    /**
     * 激活设备
     * @param  number 称号
     * @param  snnumber 设备编号
     * @param  version 版本号
     * @param  password 密码
     */
    @Override
    public void activateConfirm(String number, String snnumber, String version, String password) {
        if (snnumber == null || snnumber.isEmpty()) {
            showMessage("请联系供应商，下发设备编号！");
            dialogFragment.dismiss();
            return;
        }
        ActivateReq activateReq = new ActivateReq();
        activateReq.deviceSn = snnumber;
        activateReq.termPwd = password;
        activateReq.deviceVersion = version;
        activateReq.termCode = number;
        doActivate(activateReq);
        dialogFragment.dismiss();
    }

    /**
     * 取消激活设设备
     */
    @Override
    public void activateCancel() {
        dialogFragment.dismiss();
    }

    /**
     * 设置网络设置
     * @param  ip 地址
     * @param  port 端口
     */
    @Override
    public void setWebConfirm(String ip, String port) {
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        Config ipConfig = new Config(SjcConfig.WEB_IP, ip);
        Config portConfig = new Config(SjcConfig.WEB_PORT, port);
        configDao.save(ipConfig);
        configDao.save(portConfig);
        HttpServicesFactory.clearHttpFactory();
        setWebDialogFragment.dismiss();
    }

    /**
     * 取消网络设置
     */
    @Override
    public void setWebCancel() {
        setWebDialogFragment.dismiss();
    }

    private static class StartHandler extends Handler {

        WeakReference<StartActivity> mWeakReference;

        StartHandler(WeakReference<StartActivity> mWeakReference) {
            this.mWeakReference = mWeakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            StartActivity ac = mWeakReference.get();
            if (ac != null) {
                switch (msg.what) {
                    case 0:
                        CacheHelper.prepareCacheData();
                        if (CacheHelper.isOpenBattery) {
                            ac.startService(ac.getSkipIntent(BatteryService.class));
                        }
                        ac.dismissLoading();
                        ac.initSysTimeConfig();
                        break;
                    case 1:
                        ac.checkIsActivated();
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.load_layout);
        initCache();
    }

    /**
     * 显示激活弹框
     */
    public void checkIsActivated(View view) {
        super.onClick(view);
        checkIsActivated();
    }

    /**
     * 重启
     */
    public void onReStartClick(View view) {
        super.onClick(view);
        CustomTipDialog dialog = new CustomTipDialog(this);
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

    /**
     * 显示设置web弹框
     */
    public void showSetWebDialog(View view) {
        super.onClick(view);
        setWebDialogFragment = new SetWebDialogFragment();
        setWebDialogFragment.show(getFragmentManager(), "SetWebDialogFragment");
    }

    /**
     * 运维管理
     */
    public void startSettingActivity(View view) {
        super.onClick(view);
        openManageKey(NumFormatUtil.PASSWORD_TO_SETTING, null);
    }

    /**
     * 启动拆机警报任务
     */
    private void startCheckBrokenWorker() {
        // 网络连接要求
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest brokenCheckWork = new OneTimeWorkRequest.Builder(CheckBrokenWork.class)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance().enqueueUniqueWork("CheckBrokenWork", ExistingWorkPolicy.KEEP, brokenCheckWork);
    }

    /**
     * 周期性任务发起
     */
    private void startPeriodWork() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showLoading("系统初始化中");
            }
        });

        // 周期性数据删除任务
        PeriodicWorkRequest autoCleanRequest = new PeriodicWorkRequest
                .Builder(LocalDataCleanWork.class, 1, TimeUnit.DAYS)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("LocalDataCleanWork",
                ExistingPeriodicWorkPolicy.KEEP,
                autoCleanRequest);

        // 周期性订单回传任务自检启动
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest
                .Builder(CheckUnUploadWork.class, 15, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("CheckUnUploadWork",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest);

        // 周期性心跳包
        PeriodicWorkRequest uploadheatRequest = new PeriodicWorkRequest
                .Builder(UploadHeartWork.class, CacheHelper.HeartBeatTime, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("UploadHeartWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                uploadheatRequest);

        // 周期性业务数据更新
        PeriodicWorkRequest syncDataWorkRequest = new PeriodicWorkRequest
                .Builder(CheckSyncDataWork.class, CacheHelper.SyncDownTime, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("CheckSyncDataWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                syncDataWorkRequest);

    }

    /**
     * 检查设备是否激活
     */
    private void checkIsActivated() {
        if (CacheHelper.TermId == null || CacheHelper.TermId.isEmpty()) {
            dialogFragment = new ActivateDialogFragment();
            dialogFragment.show(getFragmentManager(), "ActivateDialogFragment");
        } else {
//            showConfirmDialog();
            // 周期任务
            startPeriodWork();
            // 拆机自检任务
            startCheckBrokenWorker();
        }
    }

//    /**
//     * 设备激活，进入系统确认弹框
//     */
//    private void showConfirmDialog() {
//        CustomTipDialog dialog = new CustomTipDialog(this);
//        dialog.show();
//        dialog.setMessage("检测到设备已激活\r\n是否进入系统");
//        dialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
//            @Override
//            public void onPositiveClick(CustomTipDialog dialog) {
//                // 周期任务
//                startPeriodWork();
//                // 拆机自检任务
//                startCheckBrokenWorker();
//            }
//        });
//    }

    /**
     * 电子称激活
     */
    private void doActivate(ActivateReq activateReq) {
        showLoading("正在激活");
        HttpServicesFactory.getHttpServiceApi().activate(activateReq)
                .enqueue(new Callback<ApiDataRsp<ActivateRsp>>() {
                    @Override
                    public void onResponse(Call<ApiDataRsp<ActivateRsp>> call, Response<ApiDataRsp<ActivateRsp>> response) {
                        dismissLoading();
                        if (response.isSuccessful()) {
                            ApiDataRsp<ActivateRsp> body = response.body();
                            if (body != null) {
                                if (body.isSuccess()) {
                                    saveActivateInfo(body.info);
                                    // 更新静态类
                                    CacheHelper.prepareTermConfig();
                                    // 周期任务
                                    startPeriodWork();
                                    // 拆机自检任务
                                    startCheckBrokenWorker();
                                } else {
                                    onFailure(call, new Throwable(body.desc));
                                }
                            }
                            return;
                        }
                        onFailure(call, new Throwable());
                    }

                    @Override
                    public void onFailure(Call<ApiDataRsp<ActivateRsp>> call, Throwable t) {
                        dismissLoading();
                        showMessage(TextUtils.isEmpty(t.getMessage()) ? "激活失败" : t.getMessage());
                    }
                });
    }

    /**
     * 保存激活信息
     * @param info  激活信息
     */
    private void saveActivateInfo(ActivateRsp info) {
        if (info == null)
            return;
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        Map<String, String> map = info.toMap();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || key.isEmpty()) {
                continue;
            }
            Config query = new Config(key, value);
            configDao.save(query);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK
                || super.onKeyDown(keyCode, event);
    }

    private void initSysTimeConfig() {
        if (!BuildConfig.DEBUG) {
            DateUtil.set24Hour();
            DateUtil.setTimeZone();
            DateUtil.setAutoDateTime(0);
            DateUtil.setAutoTimeZone(0);
        }
        mHandler.postDelayed(r, 1200);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        r = null;
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 监听业务数据初始化结果
     * 启动程序
     */
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void receiveSyncDataWorkMessage(SyncDataWorkMessage message) {
        if (message != null) {
            if (!message.isSuccess()) {
                dismissLoading();
                showMessage("业务数据初始化失败");
            }
            mHandler.sendEmptyMessage(0);
        }
    }

    private void initCache() {
        sqlQueryThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    FileHelp.prepareConfigDir();
                    CacheHelper.prepareCacheData();
                } catch (Exception e) {
                    LogUtil.e(e.getMessage());
                } finally {
                    mHandler.sendEmptyMessage(1);
                }
            }
        });
    }
}

