package com.seray.sjc.report;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scales.DateSelectActivity;
import com.seray.scales.R;
import com.seray.message.DatePickerMsg;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择时间界面
 */
public class SelectDateDialog extends DialogFragment {
    SelectDateListener selectDateListener;
    Date startDate;
    Date endDate;
    private static final int START_DATE = 0;

    private static final int END_DATE = 1;
    @BindView(R.id.custom_report_begin_date)
    TextView customReportBeginDate;
    @BindView(R.id.custom_report_end_date)
    TextView customReportEndDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Misc mMisc = Misc.newInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_report_select_date, container, false);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @OnClick({R.id.custom_report_begin_date, R.id.custom_report_end_date, R.id.custom_report_reset, R.id.custom_report_begin})
    public void onViewClicked(View view) {
        mMisc.beep();
        switch (view.getId()) {
            case R.id.custom_report_begin_date:
                goToDateSelectActivity(START_DATE);
                break;
            case R.id.custom_report_end_date:
                goToDateSelectActivity(END_DATE);
                break;
            case R.id.custom_report_reset:
                dismiss();
                break;
            case R.id.custom_report_begin:
                if (selectDateListener != null) {
                    if (startDate == null) {
                        Toast.makeText(getContext(), "请选择开始日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endDate == null) {
                        Toast.makeText(getContext(), "请选择截至日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (startDate.getTime() >= endDate.getTime()) {
                        Toast.makeText(getContext(), "开始日期不能大于截至日期", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectDateListener.onDateSelected(startDate, endDate);
                    dismiss();
                } else {
                    dismiss();
                }
                break;
        }
    }

    private void goToDateSelectActivity(int type) {
        Intent skipIntent = new Intent(getActivity(), DateSelectActivity.class);
        skipIntent.putExtra("DatePicker", type);
        startActivity(skipIntent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveDatePickerMsg(DatePickerMsg msg) {
        if (msg != null) {
            Object obj = msg.getObj();
            String strDate = msg.getDate();
            int flag = msg.getFlag();
            if (flag == START_DATE) {
                try {
                    startDate = sdf.parse(strDate);
                    customReportBeginDate.setText(strDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else if (flag == END_DATE) {
                try {
                    endDate = sdf.parse(strDate);
                    endDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000);
                    customReportEndDate.setText(strDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public SelectDateDialog setListener(SelectDateListener selectDateListener) {
        this.selectDateListener = selectDateListener;
        return this;
    }

    interface SelectDateListener {
        void onDateSelected(Date startDate, Date endDate);
    }
}
