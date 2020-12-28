package com.seray.util;

import android.app.AlarmManager;
import android.content.Context;
import android.os.SystemClock;
import android.text.TextUtils;

import com.seray.scales.App;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 系统日期工具类
 */
public class DateUtil {

    /**
     * 设置系统时间
     *
     * @param str 年月日不可颠倒 "2017-01-22 18:04:22"
     */
    public static void setSystemDate(String str) {

        if (TextUtils.isEmpty(str)) {
            return;
        }

        str = str.replace('/', '-');

        Calendar c = Calendar.getInstance();

        String[] first_date = str.split(" ");
        String[] ymdArr = null;
        String[] hmmArr = null;

        if (first_date.length > 1) {
            ymdArr = first_date[0].split("-");
            hmmArr = first_date[1].split(":");
        }

        if (ymdArr == null || ymdArr.length <= 2) {
            return;
        }

        int year = Integer.parseInt(ymdArr[0]);
        int month = Integer.parseInt(ymdArr[1]) - 1;
        int day = Integer.parseInt(ymdArr[2]);

        if (hmmArr.length <= 2) {
            return;
        }

        int hourOfDay = Integer.parseInt(hmmArr[0]);
        int minute = Integer.parseInt(hmmArr[1]);
        int second = Integer.parseInt(hmmArr[2]);

        c.set(Calendar.YEAR, year); // 年
        c.set(Calendar.MONTH, month); // 月
        c.set(Calendar.DAY_OF_MONTH, day); // 日
        c.set(Calendar.HOUR_OF_DAY, hourOfDay); // 时
        c.set(Calendar.MINUTE, minute); // 分
        c.set(Calendar.SECOND, second); // 秒

        long when = c.getTimeInMillis();

        SystemClock.setCurrentTimeMillis(when);
    }

    /**
     * 设置当前系统时区
     */
    public static void setTimeZone() {
        AlarmManager mAlarmManager = (AlarmManager) App.getApplication().getSystemService(Context
                .ALARM_SERVICE);
        mAlarmManager.setTimeZone("GMT+08:00");
    }

    /**
     * 设置当前系统时间24小时制
     */
    public static void set24Hour() {
        android.provider.Settings.System.putString(App.getApplication().getContentResolver(),
                android.provider.Settings.System.TIME_12_24, "24");
    }

    /**
     * 设置当前系统时间是否自动获取
     */
    public static void setAutoDateTime(int checked) {
        android.provider.Settings.Global.putInt(App.getApplication().getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME, checked);
    }

    /**
     * 设置当前系统时区是否自动获取
     */
    public static void setAutoTimeZone(int checked) {
        android.provider.Settings.Global.putInt(App.getApplication().getContentResolver(),
                android.provider.Settings.Global.AUTO_TIME_ZONE, checked);
    }


    /**
     * 获取固定格式的时间
     */
    public static String getDateStr(Date date, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(date);
    }
}
