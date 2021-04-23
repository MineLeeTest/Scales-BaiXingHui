//package com.seray.sjc.report;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.seray.scales.BaseActivity;
//import com.seray.scales.R;
//import com.seray.sjc.AppExecutors;
//import com.seray.sjc.adapter.SjcSubtotalAdapter;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.sjc.entity.report.SjcSubtotalWithSjcDetail;
//import com.seray.sjc.view.ReportScanDialog;
//
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//import butterknife.OnClick;
//
///**
// * 销售小计界面
// */
//public class ReportSjcSubtotalActivity extends BaseActivity implements SjcSubtotalAdapter.clickItemInterface {
//
//    @BindView(R.id.et_search)
//    EditText etSearch;
//    @BindView(R.id.tv_scan)
//    TextView tvScan;
//    @BindView(R.id.tv_cancel)
//    TextView tvCancel;
//    @BindView(R.id.rl_search)
//    RelativeLayout rlSearch;
//    @BindView(R.id.rl_title)
//    LinearLayout rlTitle;
//    @BindView(R.id.subtotal_recyclerView)
//    RecyclerView subtotalRecyclerView;
//
//    private String startTime;
//    private String endTime;
//    private SjcSubtotalAdapter sjcSubtotalAdapter;
//
//    private Handler mHandler = new Handler(Looper.getMainLooper());
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_reportsubtotal);
//        ButterKnife.bind(this);
//        subtotalRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        if (getIntent()!= null) {
//            startTime = getIntent().getStringExtra("startTime");
//            endTime = getIntent().getStringExtra("endTime");
//        }
//        sjcSubtotalAdapter = new SjcSubtotalAdapter(this);
//        sjcSubtotalAdapter.setInterface(this);
//        initData();
//        setEtSearch();
//    }
//
//    private void setEtSearch(){
//        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if ((actionId == 0 || actionId == 3) && event != null) {
//                    //点击搜索要做的操作
//                    if (!TextUtils.isEmpty(etSearch.getText().toString().trim())) {
//                        searchDate(etSearch.getText().toString().trim());
//                    }else {
//                        showMessage("请输入流水号或扫码");
//                    }
//                }
//                return false;
//            }
//        });
//    }
//
//
//    @OnClick({R.id.tv_scan, R.id.tv_cancel})
//    public void onViewClicked(View view) {
//        super.onClick(view);
//        switch (view.getId()) {
//            case R.id.tv_scan:
//                displayScanView();
//                break;
//            case R.id.tv_cancel:
//                etSearch.setText("");
//                break;
//        }
//    }
//
//    private void initData(){
//        AppExecutors.getInstance().queryIO().submit(new Runnable() {
//            @Override
//            public void run() {
//                List<SjcSubtotalWithSjcDetail> subtotalList;
//                SjcSubtotalDao subtotalDao = AppDatabase.getInstance().getSubtotalDao();
//                subtotalList = subtotalDao.loadSubtotalByTime(startTime,endTime);
//                sjcSubtotalAdapter.setData(subtotalList);
//                subtotalRecyclerView.setAdapter(sjcSubtotalAdapter);
//            }
//        });
//    }
//
//    private void searchDate(String id){
//        AppExecutors.getInstance().queryIO().submit(new Runnable() {
//            @Override
//            public void run() {
//                SjcSubtotalDao subtotalDao = AppDatabase.getInstance().getSubtotalDao();
//                SjcSubtotalWithSjcDetail sjcSubtotalWithSjcDetail;
//                sjcSubtotalWithSjcDetail = subtotalDao.loadSjdSubtotalWithDetail(id);
//
//                if (sjcSubtotalWithSjcDetail == null) {
//                    showMessage("未找到相关订单");
//                } else {
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Intent intent = getSkipIntent(ReportSjcDetailActivity.class);
//                            intent.putExtra("sjcsubtotal", sjcSubtotalWithSjcDetail);
//                            startActivity(intent);
//                        }
//                    });
//                }
//            }
//        });
//    }
//
//    @Override
//    public void clickItem(SjcSubtotalWithSjcDetail sjcSubtotal) {
//        super.onClick(null);
//        Intent intent = new Intent(ReportSjcSubtotalActivity.this,ReportSjcDetailActivity.class);
//        intent.putExtra("sjcsubtotal",sjcSubtotal);
//        startActivity(intent);
//    }
//
//    /**
//     * 扫码对话框及结果处理
//     */
//    private void displayScanView() {
//        ReportScanDialog dialog = new ReportScanDialog(ReportSjcSubtotalActivity.this);
//        dialog.show();
//        dialog.setOnLoadDataListener(new ReportScanDialog.OnLoadDataListener() {
//
//            @Override
//            public void onLoadSuccess(String result) {
//                if (result == null)
//                    return;
//               searchDate(result);
//            }
//            @Override
//            public void onLoadFailed(String error) {
//                showMessage(error);
//            }
//        });
//    }
//}
//
