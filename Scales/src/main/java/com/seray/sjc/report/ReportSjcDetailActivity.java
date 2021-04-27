package com.seray.sjc.report;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.seray.instance.ProductCart;
import com.seray.scales.BaseActivity;
import com.seray.scales.R;
import com.seray.sjc.adapter.SjcSubDetailAdapter;
import com.seray.util.NumFormatUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商品销售详细界面
 */
public class ReportSjcDetailActivity extends BaseActivity {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_sum)
    TextView tvSum;
    @BindView(R.id.sjcdetail_recyclerView)
    RecyclerView sjcdetailRecyclerView;

    private SjcSubDetailAdapter sjcSubDetailAdapter;
    List<ProductCart> pcChoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportsjcdetail);
        ButterKnife.bind(this);
        List<ProductCart> productCartList = (List<ProductCart>) this.getIntent().getExtras().get("order_list");
        pcChoice = productCartList;
        sjcdetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tvSum.setText(String.format("( ¥ %s元 ) ", countPrice()));
        if (productCartList != null) {
            sjcSubDetailAdapter = new SjcSubDetailAdapter(productCartList, this);
            sjcdetailRecyclerView.setAdapter(sjcSubDetailAdapter);
        } else {
            sjcSubDetailAdapter = new SjcSubDetailAdapter(new ArrayList<>(), this);
            sjcdetailRecyclerView.setAdapter(sjcSubDetailAdapter);
        }
    }

    private String countPrice() {
        BigDecimal all = new BigDecimal(0.00);
        for (ProductCart productCart : pcChoice) {
            all.add(productCart.getMTvSubtotalStr());
        }
        return NumFormatUtil.DF_PRICE.format(all);
    }


    public void return_cilck(View view) {
        this.finish();
    }

    public void confirm_click(View view) {

    }
}
