package com.seray.scales;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.sjc.api.SjcApi;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.RequestRegisterVM;
import com.seray.sjc.api.result.DeviceRegisterDTO;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.ProductDZCDTO;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.entity.device.ProductADB;
import com.seray.util.AESTools;
import com.seray.util.HardwareNetwork;
import com.seray.view.CustomTipDialog;

import java.util.List;

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
            return;
        }
        RequestRegisterVM requestRegisterVM = (RequestRegisterVM) resultData.getMsg();
        //发起注册请求
        showLoading("激活中,请稍后.......");
        SjcApi sjcApi = HttpServicesFactory.getHttpServiceApi();
        Call<ApiDataRsp<DeviceRegisterDTO>> request = sjcApi.register_device(requestRegisterVM);
        request.enqueue(new Callback<ApiDataRsp<DeviceRegisterDTO>>() {
            @Override
            public void onResponse(Call<ApiDataRsp<DeviceRegisterDTO>> call, Response<ApiDataRsp<DeviceRegisterDTO>> response) {
                dismissLoading();
                if (!response.isSuccessful()) {
                    showMessage("请求失败：" + response.toString());
                    return;
                }

                ApiDataRsp apiDataRsp = response.body();
                if (apiDataRsp == null) {
                    showMessage("请求返回的body为null");
                    return;
                }

                if (!apiDataRsp.getSuccess()) {
                    showMessage(apiDataRsp.getCode() + "-" + apiDataRsp.getError_msg());
                    return;
                }

                if (CacheHelper.deviceRegistered((DeviceRegisterDTO) apiDataRsp.getMsgs())) {
                    showMessage("将注册信息存储到本地失败！");
                    return;
                }
            }

            @Override
            public void onFailure(Call<ApiDataRsp<DeviceRegisterDTO>> call, Throwable t) {
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
        //没有注册过
        if (!CacheHelper.isDeviceRegistered()) {
            //提交注册信息
            registerNow();
        } else {
            //已经注册过则进入交易界面
            registed();
        }
    }


}

