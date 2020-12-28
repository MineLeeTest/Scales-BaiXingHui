package com.seray.sjc.poster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.seray.cache.AppConfig;
import com.seray.scales.App;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.annotation.DisplayType;

/**
 * @author licheng
 * @since 2019/6/5 13:41
 */
public class DisplayPoster {

    public static final String KEY_INSTRUCTION = "KEY-INSTRUCTION";
    public static final String KEY_DATA = "KEY-DATA";

    private static LocalBroadcastManager mLocalBroadcastManager;

    private static boolean isSR200 = true;

    public static void init() {
        isSR200 = AppConfig.isSR200();
        getDefaultBroadcastManager();
    }

    private DisplayPoster(@NonNull Context context) {
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public static void notifyDisplayStateChange(boolean isOpen) {
        if (isOpen) {
            postInstruction(Instruction.DISPLAY_ON);
        } else {
            postInstruction(Instruction.DISPLAY_OFF);
        }
    }

    public static void notifyDisplaySyncShow() {
        if (!isSR200)
            return;
        postInstruction(Instruction.DISPLAY_SYNC);
    }

    public static void notifyDisplayAsyncShow(@DisplayType int displayType) {
        if (!isSR200)
            return;
        Bundle bundle = new Bundle();
        bundle.putInt(DisplayType.class.getSimpleName(), displayType);
        postInstruction(Instruction.DISPLAY_ASYNC, bundle);
    }

    private static void postInstruction(@Instruction String instruction) {
        postInstruction(instruction, null);
    }

    private static void postInstruction(@Instruction String instruction, Bundle bundle) {
        Intent intent = getDefaultIntent().putExtra(KEY_INSTRUCTION, instruction);
        if (bundle != null) {
            intent.putExtra(KEY_DATA, bundle);
        }
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
        intent.setAction(SjcConfig.ACTION_DISPLAY_CONTROL);
        return intent;
    }
}
