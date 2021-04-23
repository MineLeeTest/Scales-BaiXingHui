//package com.seray.sjc.report;
//
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.widget.TextView;
//
//import com.seray.scales.BaseActivity;
//import com.seray.scales.R;
//import com.seray.sjc.adapter.SjcSubDetailAdapter;
//import com.seray.sjc.entity.report.SjcSubtotalWithSjcDetail;
//import com.seray.sjc.util.SjcUtil;
//
//import java.util.ArrayList;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
///**
// * 商品销售详细界面
// */
//public class ReportSjcDetailActivity extends BaseActivity {
//    @BindView(R.id.tv_title)
//    TextView tvTitle;
//    @BindView(R.id.tv_sum)
//    TextView tvSum;
//    @BindView(R.id.tv_paytpe)
//    TextView tvPaytpe;
//    @BindView(R.id.sjcdetail_recyclerView)
//    RecyclerView sjcdetailRecyclerView;
//
//    private SjcSubtotalWithSjcDetail sjcSubtotal;
//    private SjcSubDetailAdapter sjcSubDetailAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reportsjcdetail);
//        ButterKnife.bind(this);
//        sjcSubtotal = (SjcSubtotalWithSjcDetail) getIntent().getSerializableExtra("sjcsubtotal");
//        sjcdetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        tvSum.setText(String.format("( ¥ %s元 ) ", sjcSubtotal.getSjcSubtotal().getTransAmt()));
//        tvPaytpe.setText(SjcUtil.getCurPayTypeName(sjcSubtotal.getSjcSubtotal().getPayType()));
//        if (sjcSubtotal.getSjcDetails() != null) {
//            sjcSubDetailAdapter = new SjcSubDetailAdapter(sjcSubtotal.getSjcDetails(), this);
//            sjcdetailRecyclerView.setAdapter(sjcSubDetailAdapter);
//        } else {
//            sjcSubDetailAdapter = new SjcSubDetailAdapter(new ArrayList<>(), this);
//            sjcdetailRecyclerView.setAdapter(sjcSubDetailAdapter);
//        }
//    }
//}
