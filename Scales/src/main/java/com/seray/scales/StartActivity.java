package com.seray.scales;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.sjc.api.SjcApi;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.ActivateReq;
import com.seray.sjc.api.result.ActivateRsp;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.work.UploadHeartWork;
import com.seray.util.HardwareNetwork;
import com.seray.view.CustomTipDialog;

import java.util.concurrent.TimeUnit;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends BaseActivity implements View.OnClickListener {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_layout);
        context = this.getBaseContext();
        //读取配置文件数据
        CacheHelper.prepareCacheData();
        System.out.println(CacheHelper.device_id + "--------CacheHelper.company_name----" + CacheHelper.company_name);
        //没有注册过
        if (!CacheHelper.isDeviceRegistered()) {
            //提交注册信息
            registerNow();
        } else {
            //已经注册过则进入交易界面
            registed();
        }
    }

    //已经注册过则进入交易界面
    private void registed() {
        startPeriodWork();
        startActivity(ScaleActivity.class);
        StartActivity.this.finish();
    }

    /**
     * 电子秤激活
     */
    private void registerNow() {
        //获取电子秤硬件信息
        ResultData resultData = HardwareNetwork.getSimData(context);
        if (!resultData.isSuccess()) {
            showLoading(resultData.getCode() + "-" + resultData.getMsg());
        }
        ActivateReq activateReq = (ActivateReq) resultData.getMsg();

        System.out.println("resultData----------->" + resultData.toString());
        //发起注册请求
        showLoading("激活中,请稍后.......");
        SjcApi sjcApi = HttpServicesFactory.getHttpServiceApi();
        Call<ApiDataRsp> request = sjcApi.register_device(activateReq);
        System.out.println("request().url()---------->" + request.request().url());
        request.enqueue(new Callback<ApiDataRsp>() {
            @Override
            public void onResponse(Call<ApiDataRsp> call, Response<ApiDataRsp> response) {
                dismissLoading();
                System.out.println("----isSuccessful----->" + response.isSuccessful());
                if (response.isSuccessful()) {
                    ApiDataRsp apiDataRsp = response.body();
                    if (apiDataRsp != null) {
                        System.out.println("----ActivateRsp----->" + apiDataRsp.toString());
                        if (apiDataRsp.success) {
                            if (CacheHelper.deviceRegistered((ActivateRsp) apiDataRsp.msg)) {
                                registed();
                            } else {
                                showMessage("将注册信息存储到本地失败！");
                            }
                        } else {
                            showMessage(apiDataRsp.code + "-" + apiDataRsp.errorMsg);
                        }
                    } else {
                        showMessage("请求返回的body为null");
                    }
                } else {
                    showMessage("请求失败：" + response.toString());
                }
            }

            @Override
            public void onFailure(Call<ApiDataRsp> call, Throwable t) {
                dismissLoading();

                showMessage(TextUtils.isEmpty(t.getMessage()) ? "激活失败" : "激活失败::" + t.getMessage());
            }
        });
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
     * 手动激活
     */
    public void checkIsActivated(View view) {
        super.onClick(view);
        registerNow();
    }

    /**
     * 周期性任务发起
     */
    private void startPeriodWork() {
        // 周期性心跳包
        PeriodicWorkRequest uploadheatRequest = new PeriodicWorkRequest
                .Builder(UploadHeartWork.class, CacheHelper.HeartBeatTime, TimeUnit.SECONDS)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork("UploadHeartWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                uploadheatRequest);
    }

//        //获取数据库
//        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
//        List<Config> list = configDao.loadAll();
//        for (Config config : list) {
//            System.out.println(config.getConfigKey() + "---DB-->" + config.getConfigValue());
//        }


//        String aesKey = "C2ckCioYP0ygY9yPw55jVA==";
//        ResultData resultData = new ResultData();
//        resultData = AESTools.encrypt(resultData, aesKey, System.currentTimeMillis() + "");
//        System.out.println("--------aes----->" + resultData.getMsg());

}

