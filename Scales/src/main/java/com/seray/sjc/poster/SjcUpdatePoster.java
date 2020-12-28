package com.seray.sjc.poster;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.seray.scales.App;
import com.seray.sjc.SjcConfig;

/**
 * Author：李程
 * CreateTime：2019/4/20 22:28
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class SjcUpdatePoster {

    public static final String KEY_INTENT = "NOTIFY_TYPE";

    private static LocalBroadcastManager mLocalBroadcastManager;

    private SjcUpdatePoster(@NonNull Context context) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public static void notifyAllReload() {
        notifyProductReload();
        notifyPayTypeReload();
        notifyParamInfoReload();
        notifyDeviceInfoReload();
        notifyScreenResourceReload();
    }

    public static void notifyProductReload() {
        Intent intent = getDefaultIntent().putExtra(KEY_INTENT, SjcConfig.UPDATE_PRODUCT);
        getDefaultBroadcastManager().sendBroadcast(intent);
    }

    public static void notifyScreenResourceReload() {
        Intent intent = getDefaultIntent().putExtra(KEY_INTENT, SjcConfig.UPDATE_SCREEN_RESOURCE);
        getDefaultBroadcastManager().sendBroadcast(intent);
    }

    public static void notifyPayTypeReload() {
        Intent intent = getDefaultIntent().putExtra(KEY_INTENT, SjcConfig.UPDATE_PAY_TYPE);
        getDefaultBroadcastManager().sendBroadcast(intent);
    }

    public static void notifyParamInfoReload() {
        Intent intent = getDefaultIntent().putExtra(KEY_INTENT, SjcConfig.UPDATE_PARAM_INFO);
        getDefaultBroadcastManager().sendBroadcast(intent);
    }

    public static void notifyDeviceInfoReload() {
        Intent intent = getDefaultIntent().putExtra(KEY_INTENT, SjcConfig.UPDATE_DEVICE_INFO);
        getDefaultBroadcastManager().sendBroadcast(intent);
    }

    private static LocalBroadcastManager getDefaultBroadcastManager() {
        if (mLocalBroadcastManager == null) {
            return LocalBroadcastManager.getInstance(App.getApplication());
        }
        return mLocalBroadcastManager;
    }

    private static Intent getDefaultIntent() {
        Intent intent = new Intent();
        intent.setAction(SjcConfig.ACTION_UPDATE_BUSINESS_DATA);
        return intent;
    }
}
