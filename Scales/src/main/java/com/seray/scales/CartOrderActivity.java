package com.seray.scales;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.message.ClearCartMsg;
import com.seray.sjc.adapter.SjcSubDetailAdapter;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.ProductCart;
import com.seray.sjc.api.request.RequestOrderVM;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.UserVipCardDetailDTO;
import com.seray.util.AESTools;
import com.seray.util.MD5Utils;
import com.seray.util.NumFormatUtil;
import com.seray.view.CustomInputTareDialog;

import org.greenrobot.eventbus.EventBus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
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
    private UserVipCardDetailDTO dto = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportsjcdetail);
        ButterKnife.bind(this);

        vm = (RequestOrderVM) this.getIntent().getExtras().get("RequestOrderVM");
        dto = (UserVipCardDetailDTO) this.getIntent().getExtras().get("UserVipCardDetailDTO");

        sjcdetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sjcSubDetailAdapter = new SjcSubDetailAdapter(CartOrderActivity.this, vm.getProductCartList() != null ? vm.getProductCartList() : new ArrayList<>(), this);
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
        if (allPrice.compareTo(new BigDecimal(dto.getBalance())) > 0) {
            showMessage("买家账户余额不足！如果买家刚才执行了充值，请返回交易界面请重新刷卡，买家的充值金额将会到账更新！");
            return;
        }

        CustomInputTareDialog tareDialog = new CustomInputTareDialog(this, "请输会员交易密码", "会员注册时输入的6位交易密码！", Boolean.TRUE);
        tareDialog.show();
        tareDialog.setOnPositiveClickListener(R.string.reprint_ok, (dialog, data) -> {
            updateOrder(data, allPrice);
            tareDialog.dismiss();
        });
        tareDialog.setOnNegativeClickListener(R.string.reprint_cancel, Dialog::dismiss);
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
                    EventBus.getDefault().post(new ClearCartMsg("9000", "true"));
                    CartOrderActivity.this.finish();
                } else {
                    mMisc.beep();
                    showMessage(resData.getCode() + "-" + resData.getError_msg());
                    if ("8000".equals(resData.getCode())) {
                        String retStr = resData.getMsgs();

                        String[] failedIds = retStr.split(",");
                        List<ProductCart> upFailedlist = new ArrayList<>();
                        for (ProductCart cart : vm.getProductCartList()) {
                            for (String id : failedIds) {
                                if (id.equals(cart.getProduct_id().toString())) {
                                    upFailedlist.add(cart);
                                }
                            }
                        }
                        //改变当前界面购车内容
                        sjcSubDetailAdapter.productADBList=upFailedlist;
                        sjcSubDetailAdapter.productADBListChoice=upFailedlist;
                        sjcSubDetailAdapter.notifyDataSetChanged();
                        //显示价格
                        change(upFailedlist);
                        //改变称重界面购物车内容
                        EventBus.getDefault().post(new ClearCartMsg("8000", upFailedlist));
                    }
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
        //显示价格
        shouPrice(productADBList);
    }
}
