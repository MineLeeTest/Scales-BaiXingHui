package com.seray.sjc.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SystemUtils {

    /**
     * 获取versionCode
     */
    public static int getVersionCode(Context context) {
        PackageManager packageManager = context.getPackageManager();
        int versionCode = -1;
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionCode;
    }

    /**
     * 获取versionName
     */
    public static String getVersionName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        String versionName = null;
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionName;
    }


}