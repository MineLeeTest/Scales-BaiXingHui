package com.seray.scales;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import com.seray.message.DatePickerMsg;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateSelectActivity extends BaseActivity {

    private CalendarView mCalendarView;

    private Button mBtnOk, mBtnBack;

    private int datePickerTag;

    private Date selectDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date_select_layout);
        initView();
        initListener();
        Intent intent = getIntent();
        datePickerTag = intent.getIntExtra("DatePicker", -1);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.date_select_ok:
                if (selectDate == null)
                    selectDate = getToday();
                String formatDate = getStringFormatDate(selectDate);
                EventBus.getDefault().post(new DatePickerMsg(formatDate, datePickerTag,
                        selectDate));
                finish();
                break;
            case R.id.date_select_back:
                finish();
                break;
        }
    }

    private String getStringFormatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return sdf.format(date);
    }

    private void initView() {
        mCalendarView = (CalendarView) findViewById(R.id.date_select_view);
        mBtnBack = (Button) findViewById(R.id.date_select_back);
        mBtnOk = (Button) findViewById(R.id.date_select_ok);
    }

    private void initListener() {
        mBtnOk.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int
                    dayOfMonth) {
                mMisc.beep();
                selectDate = getDate(year, month, dayOfMonth);
            }
        });
        getToday();
    }

    private Date getDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year); // 年
        c.set(Calendar.MONTH, month); // 月
        c.set(Calendar.DAY_OF_MONTH, day); // 日
        return c.getTime();
    }

    /**
     *获取今天日期
     */
    private Date getToday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }
}
