package com.seray.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Display;

import com.seray.inter.DisplayController;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.annotation.DisplayType;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.poster.DisplayPoster;
import com.seray.sjc.poster.Instruction;
import com.seray.util.LogUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author licheng
 * @since 2019/6/5 13:29
 */
public class DisplayService extends Service {

    private DisplayController mDisplayController;
    private ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
    private boolean isHaveResource;

    private boolean isSyncShowing = false;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 0) {
                LogUtil.d("多媒体资源自检结束！可用的多媒体资源个数：" + msg.arg1);
                isHaveResource = msg.arg1 > 0;
                startPrepareWork();
            }
        }
    };

    private BroadcastReceiver mDisplayControlReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && action.equals(SjcConfig.ACTION_DISPLAY_CONTROL)) {
                String instruction = intent.getStringExtra(DisplayPoster.KEY_INSTRUCTION);
                switch (instruction) {
                    case Instruction.DISPLAY_ON:
                        break;
                    case Instruction.DISPLAY_OFF:
                        stopSelf();
                        break;
                    case Instruction.DISPLAY_SYNC:
                        mDisplayController.syncDisplayShow();
                        isSyncShowing = true;
                        break;
                    case Instruction.DISPLAY_ASYNC:
                        Bundle bundle = intent.getBundleExtra(DisplayPoster.KEY_DATA);
                        int displayType = bundle.getInt(DisplayType.class.getSimpleName(), DisplayType.DISPLAY_AD);
                        mDisplayController.showPresentation(displayType);
                        isSyncShowing = false;
                        break;
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("广告后显示服务已启动！");
        registerReceiver();
        checkDisplay();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("广告后显示服务已停止！");
        mHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDisplayControlReceiver);
        LogUtil.d("广告后显示服务控制广播已解除注册！");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startPrepareWork() {
        if (!isHaveResource) {
            stopSelf();
            return;
        }
        timer.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                // 正在同步显示
                if (isSyncShowing) return;

                // 正在称重
                boolean weighting = mDisplayController.isWeighting();
                if (weighting) return;

                // 正在轮播广告
                boolean showing = mDisplayController.isShowing(DisplayType.DISPLAY_AD);
                if (showing) return;

                mHandler.post(() -> mDisplayController.showPresentation(DisplayType.DISPLAY_AD));

            }
        }, 10, 5 * 60, TimeUnit.SECONDS);
    }

    private void checkDisplay() {
        mDisplayController = DisplayController.getInstance();
        Display display = mDisplayController.getDisplay();
        if (display == null) {
            LogUtil.w("无可用的后显示器");
            stopSelf();
            return;
        }
        prepareScreenResource();
    }

    private void prepareScreenResource() {
        LogUtil.d("多媒体资源自检开始！");
        AppExecutors.getInstance().queryIO()
                .submit(new Runnable() {
                    @Override
                    public void run() {
//                        ScreenResourceDao dao = AppDatabase.getInstance().getScreenResourceDao();
//                        int countUsableResource = dao.countUsableResource();
//                        Message message = Message.obtain();
//                        message.what = 0;
//                        message.arg1 = countUsableResource;
//                        mHandler.sendMessage(message);
                    }
                });
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SjcConfig.ACTION_DISPLAY_CONTROL);
        LocalBroadcastManager.getInstance(this).registerReceiver(mDisplayControlReceiver, filter);
    }
}