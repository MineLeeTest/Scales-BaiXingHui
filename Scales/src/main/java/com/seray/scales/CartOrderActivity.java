package com.seray.scales;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.sjc.adapter.SjcSubDetailAdapter;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.ProductCart;
import com.seray.sjc.api.request.RequestOrderVM;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.util.AESTools;
import com.seray.util.MD5Utils;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomInputTareDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 商品销售详细界面
 */
public class CartOrderActivity extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_sum)
    TextView tvSum;

    @BindView(R.id.sjcdetail_recyclerView)
    RecyclerView sjcdetailRecyclerView;

    private SjcSubDetailAdapter sjcSubDetailAdapter;

    private RequestOrderVM vm = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportsjcdetail);
        ButterKnife.bind(this);
        vm = (RequestOrderVM) this.getIntent().getExtras().get("RequestOrderVM");

        sjcdetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sjcSubDetailAdapter = new SjcSubDetailAdapter(CartOrderActivity.this, vm.getProductCartList() != null ? vm.getProductCartList() : new ArrayList<ProductCart>(), this);
        sjcdetailRecyclerView.setAdapter(sjcSubDetailAdapter);

        shouPrice(sjcSubDetailAdapter.productADBListChoice);
    }

    public void shouPrice(List<ProductCart> list) {
        tvSum.setText(String.format("( ¥ %s元 ) ", NumFormatUtil.DF_PRICE.format(NumFormatUtil.countPrice(list))));
    }

    public void return_cilck(View view) {
        mMisc.beep();
        this.finish();
    }

    public void confirm_click(View view) {
        mMisc.beep();
        BigDecimal allPrice = NumFormatUtil.countPrice(sjcSubDetailAdapter.productADBListChoice);
        if (allPrice.compareTo(BigDecimal.valueOf(Double.valueOf("500.00"))) > 0) {
            CustomInputTareDialog tareDialog = new CustomInputTareDialog(this, "请输会员交易密码", "会员注册时输入的6位交易密码！", Boolean.TRUE);
            tareDialog.show();
            tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, data) -> {
                updateOrder(data, allPrice);
                tareDialog.dismiss();
            });
            tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
        } else {
            updateOrder("UNUSE000000", allPrice);
        }

    }


    private void updateOrder(String pwd, BigDecimal allPrice) {
        vm.setAll_price(allPrice);
        vm.setBuyer_pwd(MD5Utils.digest(pwd));
        vm.setDevice_id(CacheHelper.device_id);
        ResultData resultData = new ResultData();
        resultData = AESTools.encrypt(resultData, CacheHelper.device_aes_key, System.currentTimeMillis() + "," + CacheHelper.device_id);
        if (!resultData.isSuccess()) {
            showMessage(resultData.getCode() + "-" + resultData.getMsg());
            return;
        }
        String sign = resultData.getMsg() + "";
        vm.setProductCartList(sjcSubDetailAdapter.productADBListChoice);
        //发起注册请求
        showLoading("订单上传中,请稍后.......");
        HttpServicesFactory.getHttpServiceApi().order_create(CacheHelper.device_id + "", sign, vm).enqueue(new Callback<ApiDataRsp<String>>() {
            @SneakyThrows
            @Override
            public void onResponse(Call<ApiDataRsp<String>> call, Response<ApiDataRsp<String>> response) {
                dismissLoading();
                ApiDataRsp<String> resData = response.body();
                if (resData.getSuccess()) {
                    mMisc.beep();
                    Thread.sleep(200);
                    mMisc.beep();
                    showMessage(resData.getCode() + "-" + resData.getMsgs());
                } else {
                    mMisc.beep();
                    showMessage(resData.getCode() + "-" + resData.getError_msg());
                }

            }

            @Override
            public void onFailure(Call<ApiDataRsp<String>> call, Throwable t) {
                dismissLoading();
                mMisc.beep();
                showMessage("交易失败！");
            }
        });
    }

    public void change(List<ProductCart> productADBList) {
        mMisc.beep();
        shouPrice(productADBList);
    }
}
