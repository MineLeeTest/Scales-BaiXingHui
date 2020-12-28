package com.seray.sjc.report;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seray.scales.BaseActivity;
import com.seray.scales.R;
import com.seray.scaleviewlib.utils.ToastUtils;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.entity.report.ReportDetail;
import com.seray.sjc.entity.report.ReportSubtotal;
import com.seray.sjc.util.SjcUtil;
import com.seray.util.LogUtil;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 今日销售统计、商品销售统计
 */
public class NewReportActivity extends BaseActivity implements DatePickerDialog.OnDateSetListener, DialogInterface.OnCancelListener{
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.report_title)
    TextView reportTitle;
    @BindView(R.id.order_count)
    TextView orderCount;
    @BindView(R.id.deal_amount)
    TextView dealAmount;
    @BindView(R.id.pay_type_amount_container)
    LinearLayout payTypeAmountContainer;
//    @BindView(R.id.search_more)
//    TextView searchMore;
    @BindView(R.id.empty_view)
    TextView emptyView;

    @BindView(R.id.tv_endDay)
    TextView tvEndDay;
    @BindView(R.id.tv_startDay)
    TextView tvStartDay;

    private int mSelectDateIndex;

    private ReportAdapter adapter;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //更新UI
            updateUI(msg.what == SEARCH_SUCCESS);
        }
    };
    private List<ReportSubtotal> reportSubtotalList;
    private List<ReportDetail> reportDetailList;
    private int SEARCH_SUCCESS = 1;
    private int SEARCH_FAIL = 0;
    //是否是今日销售统计
    private boolean isToday = true;
    private Date startDate;
    private Date endDate;

    private DatePickerDialog dpd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);
        adapter = new ReportAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        startDate = calendar.getTime();
//        calendar.add(Calendar.DATE, 1);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.HOUR_OF_DAY, 23);
        calendar2.set(Calendar.MINUTE, 59);
        calendar2.set(Calendar.SECOND, 59);
        endDate = calendar2.getTime();
        getReportData();


        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        tvStartDay.setText(format.format(startDate));
        tvEndDay.setText(format.format(endDate));


    }

    @OnClick(R.id.order_layout)
    void OnClick() {
        super.onClick(null);
        Intent intent = new Intent(NewReportActivity.this, ReportSjcSubtotalActivity.class);
        intent.putExtra("startTime", sdf.format(startDate));
        intent.putExtra("endTime", sdf.format(endDate));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        if (searchRunnable != null)
            handler.removeCallbacks(searchRunnable);
        super.onDestroy();
    }


    @OnClick(R.id.tv_startDay)
     void changeStartDay(){
        //弹出时间选择
        mMisc.beep();
            mSelectDateIndex = 1;
        showDateSelectedDialog();

    }

    @OnClick(R.id.tv_endDay)
    void changeEndDay(){
        mMisc.beep();
        mSelectDateIndex = 2;
        showDateSelectedDialog();
    }

    protected void showDateSelectedDialog() {
        Calendar now = Calendar.getInstance();
        if (this.dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.setVersion(DatePickerDialog.Version.VERSION_2);
            dpd.setOnCancelListener(this);
            dpd.setMaxDate(getEndDate());
            dpd.setMinDate(getBaseMinDate());
        }
        dpd.show(getFragmentManager(), "DatePickerDialog");
    }




    private Calendar getBaseMinDate() {
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR)-1, now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) );
        return now;
    }


    private Calendar getEndDate() {
        Calendar now = Calendar.getInstance();
        now.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH) );
        return now;
    }





    /**
     * 查询报表数据
     */
    void getReportData() {
        if (this.startDate == null || this.endDate == null)
            return;
        AppExecutors.getInstance().queryIO().submit(searchRunnable);
    }

    private void updateUI(boolean success) {
        if (reportTitle == null)
            return;
        if (success) {
            adapter.setData(reportDetailList);

            reportTitle.setText(isToday ? R.string.today_sales_statistics : R.string.sales_statistics);
            long count = 0;
            BigDecimal amount = new BigDecimal(0);

            payTypeAmountContainer.removeAllViews();
            for (ReportSubtotal reportSubtotal : reportSubtotalList) {
                BigDecimal tempAmount = new BigDecimal(reportSubtotal.transAmtSum)
                        .setScale(2, BigDecimal.ROUND_HALF_UP);

                TextView textView = new TextView(this);
                textView.setLayoutParams(new LinearLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                textView.setText(String.format("%s：  ¥ %s", SjcUtil.getCurPayTypeName(reportSubtotal.payType), String.valueOf(tempAmount)));
                payTypeAmountContainer.addView(textView);
                count += reportSubtotal.count;
                amount = amount.add(tempAmount);
            }
            orderCount.setText(String.valueOf(count));

            dealAmount.setText(String.format("¥ %s", amount));

            isToday = false;
        } else {
            Toast.makeText(this, "获取报表失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {
        Toast.makeText(NewReportActivity.this,"已取消选择",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        Date time = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        String dateStr = format.format(time);
        if (mSelectDateIndex == 1) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            this.startDate = calendar.getTime();
            tvStartDay.setText(dateStr);
        } else if (mSelectDateIndex == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            this.endDate = calendar.getTime();
            tvEndDay.setText(dateStr);
        }

        if (endDate.after(startDate)) {
            getReportData();
        }else {
            Toast.makeText(NewReportActivity.this,"结束时间需要大于开始时间！",Toast.LENGTH_SHORT).show();
        }
    }

    class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

        private List<ReportDetail> reportDetails = new ArrayList<>();

        @NonNull
        @Override
        public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ReportViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_report_detail, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
            holder.bindData(reportDetails.get(position));
        }

        @Override
        public int getItemCount() {
            if (reportDetails == null)
                return 0;
            return reportDetails.size();
        }

        public void setData(List<ReportDetail> reportDetailList) {
            if (reportDetailList == null)
                return;
            this.reportDetails = reportDetailList;
            notifyDataSetChanged();

            emptyView.setVisibility(reportDetailList.isEmpty() ? View.VISIBLE : View.GONE);
        }

        class ReportViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.goodsName)
            TextView goodsName;
            @BindView(R.id.deal_count)
            TextView dealCount;
            @BindView(R.id.deal_amount)
            TextView dealAmount;
            @BindView(R.id.price_type)
            TextView priceType;

            public ReportViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindData(ReportDetail reportDetail) {
                goodsName.setText(reportDetail.goodsName);

                BigDecimal dealCntSumBigDecimal =
                        new BigDecimal(reportDetail.dealCntSum)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);

                dealCount.setText(String.valueOf(dealCntSumBigDecimal));

                BigDecimal dealAmountBigDecimal =
                        new BigDecimal(reportDetail.dealAmtSum)
                                .setScale(2, BigDecimal.ROUND_HALF_UP);

                dealAmount.setText(String.valueOf(dealAmountBigDecimal));

                priceType.setText(SjcUtil.getPriceTypeString(reportDetail.priceType));
            }
        }
    }

    private Runnable searchRunnable = () -> {
        try {
            //获取订单报表统计数据
            reportSubtotalList = AppDatabase.getInstance()
                    .getSubtotalDao()
                    .getReportSubtotal(sdf.format(startDate), sdf.format(endDate));

            //获取商品明细报表信息
            reportDetailList = AppDatabase.getInstance()
                    .getDetailDao()
                    .getReportDetail(sdf.format(startDate), sdf.format(endDate));
            handler.sendEmptyMessage(SEARCH_SUCCESS);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            //查询失败
            handler.sendEmptyMessage(SEARCH_FAIL);
        }
    };
}
