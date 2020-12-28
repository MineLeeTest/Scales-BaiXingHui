package com.seray.scales;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;

import com.seray.cache.CacheHelper;
import com.seray.setting.ConfigActivity;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.DataResetCallback;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomTipDialog;

import java.io.File;

public class SettingActivity extends BaseActivity {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_layout);
        initView();
    }

    // 应用重置
    public void projectResetClick(View view) {
        super.onClick(view);
        CustomTipDialog tipDialog = new CustomTipDialog(this);
        tipDialog.show();
        tipDialog.setMessage("清空订单、配置和激活等信息\r\n并重新启动应用程序");
        tipDialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomTipDialog dialog) {
                showLoading("正在重置");
                AppExecutors.getInstance().insertIO().submit(new Runnable() {
                    @Override
                    public void run() {
                        // 订单计数器重置
                        CacheHelper.DATE_ID = 1;
                        // 清空本地缓存文件
                        boolean fileCacheDeleteResult = FileHelp.deleteCache();
                        if (!fileCacheDeleteResult) {
                            showMessage("本地缓存文件重置失败！");
                        }
                        // 清空本地缓存数据库
                        AppDatabase.getInstance().dataReset(new DataResetCallback() {
                            @Override
                            public void dataResetResult(boolean isSuccess) {
                                if (!isSuccess) {
                                    LogUtil.e("本地数据清空失败！执行强制删除！");
                                    File databaseFile = new File(FileHelp.DATABASE_DIR);
                                    boolean databaseDeleteResult = FileHelp.deleteDir(databaseFile);
                                    if (!databaseDeleteResult) {
                                        showMessage("本地数据重置失败！");
                                    }
                                }
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dismissLoading();
                                        App.getApplication().rebootApp();
                                    }
                                }, 1200);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_1:
                openManageKey(NumFormatUtil.PASSWORD_TO_OPERATION, null);
                break;
            case R.id.btn_2:
                startActivity(ConfigActivity.class);
                break;
            case R.id.btn_4:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                break;
            case R.id.btn_5:
                showExitDialog();
                break;
            case R.id.btn_6:
                finish();
                break;
            case R.id.btn_8:
                showDeleteCalibrationFileDialog();
                break;
            case R.id.btn_9:
                startActivity(getSkipIntent(TestActivity.class));
                break;
        }
    }

    public void pushNVBitmap(View view) {
        super.onClick(view);
        showLoading("正在下发");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ALPHA_8;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo, options);
        //296 197
        Bitmap compressBitmap = Bitmap.createScaledBitmap(bitmap, 296, 197, true);
        mCustomPrinter.saveBitmap(compressBitmap, new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        showMessage("关机重启后生效");
                    }
                });
            }
        });
    }

    public void printNVBitmap(View view) {
        super.onClick(view);
        mCustomPrinter.printBitmap();
    }

    private void showExitDialog() {
        CustomTipDialog dialog = new CustomTipDialog(this);
        dialog.show();
        dialog.setTitle(R.string.test_clear_title);
        dialog.setMessage(R.string.test_exit);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomTipDialog dialog) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                App.getApplication().exit();
            }
        });
    }

    private void showDeleteCalibrationFileDialog() {
        CustomTipDialog dialog = new CustomTipDialog(this);
        dialog.show();
        dialog.setTitle(R.string.test_clear_title);
        dialog.setMessage(R.string.test_shutdown_msg);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, new CustomTipDialog.OnPositiveClickListener() {
            @Override
            public void onPositiveClick(CustomTipDialog dialog) {
                if (FileHelp.deleteCalibrationFile()) {
                    shutDown();
                } else {
                    showMessage(R.string.test_show_msg_1);
                }
            }
        });
    }

    private void initView() {
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
    }
}
