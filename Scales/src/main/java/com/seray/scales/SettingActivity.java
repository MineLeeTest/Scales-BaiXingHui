package com.seray.scales;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.seray.log.FileIOUtils;
import com.seray.log.LLog;
import com.seray.scaleviewlib.utils.Utils;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.util.FileHelp;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomTipDialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SettingActivity extends BaseActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operation_layout);
        context = SettingActivity.this;
    }

    //设置称重模块标定功能
    public void setWeight(View view) {
        openManageKey(NumFormatUtil.PASSWORD_TO_OPERATION);
    }

    //设置网络
    public void setNet(View view) {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    // 校准屏幕
    public void setScreen(View view) {
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


    //更新APP
    public void setUpdateApp(View view) {
        HttpServicesFactory.getHttpServiceApi().getAppVersion(HttpServicesFactory.URL_YHYC + HttpServicesFactory.APP_VERSION).enqueue(new Callback<ResponseBody>() {
            @SneakyThrows
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (200 == response.code()) {
                        CustomTipDialog tipDialog = new CustomTipDialog(context);
                        tipDialog.show();
                        tipDialog.setTitle("提醒");
                        tipDialog.setMessage("当前APP版本号为:" + App.VersionName + "\r\n云端APP版本号为:" + response.body().string());
                        tipDialog.setOnPositiveClickListener("更新", dialog -> {
                            tipDialog.dismiss();
                            downloadApp();
                        });
                        tipDialog.setOnNegativeClickListenerPrivate("取消", Dialog::dismiss);

                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LLog.e(t.toString());
                showMessage("访问云端失败：" + t.getMessage());
            }
        });

    }

    public static final String APP_DIR = FileIOUtils.getSDCardPathByEnvironment() + File.separator + Utils.getApp().getPackageName() + ".apk";

    //下载APP
    private void downloadApp() {
        HttpServicesFactory.getHttpServiceApi().getAppVersion(HttpServicesFactory.URL_YHYC + HttpServicesFactory.APP_NAME).enqueue(new Callback<ResponseBody>() {
            @SneakyThrows
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response != null) {
                    if (200 == response.code()) {


                        //下载APK文件到本地

//                        File file = new File(APP_DIR, HttpServicesFactory.APP_NAME);
//                        if (file.exists()) {
//                            file.delete();
//                            file = new File(APP_DIR, HttpServicesFactory.APP_NAME);
//                        } else {
//                            file.createNewFile();
//                        }

                        File f = new File(APP_DIR);
                        if (!f.exists()) {
                            f.mkdirs();
                        }
                        f = new File(APP_DIR + File.separator + HttpServicesFactory.APP_NAME);
                        f.delete();
                        f.createNewFile();

                        InputStream is = response.body().byteStream();
                        FileOutputStream fos = new FileOutputStream(f);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        bis.close();
                        is.close();


                        //安装APK文件
                        File apk = new File(APP_DIR + File.separator + HttpServicesFactory.APP_NAME);
                        Uri uri = Uri.fromFile(apk);
                        Intent intent = new Intent();
                        intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity");
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LLog.e(t.toString());
                showMessage("访问云端失败：" + t.getMessage());
            }
        });
    }

    //退出APP
    public void setExit(View view) {
        CustomTipDialog dialog = new CustomTipDialog(this);
        dialog.show();
        dialog.setTitle(R.string.test_clear_title);
        dialog.setMessage(R.string.test_exit);
        dialog.setOnPositiveClickListener(R.string.reprint_ok, dialog1 -> {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
            App.getApplication().exit();
        });
    }

    public void setClose(View view) {
        this.finish();
    }
}
