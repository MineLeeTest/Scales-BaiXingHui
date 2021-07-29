package com.seray.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.seray.instance.ResultData;
import com.seray.message.ShutdownMsg;
import com.seray.util.NumFormatUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ShutdownService extends Service {

    private ScheduledExecutorService shutdown = null;

    private ShutdownRunnable mRunnable = null;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        shutdown = Executors.newScheduledThreadPool(1);
        context = this;
        mRunnable = new ShutdownRunnable();
    }

    private final long TIMES = 1;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shutdown.scheduleAtFixedRate(mRunnable, 1, TIMES, TimeUnit.MINUTES);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        shutdown.shutdownNow();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static final int TimeToShutdown = 90840;
    private static final int TimeToShutdownWaring1 = TimeToShutdown - 10;
    private static final int TimeToShutdownWaring2 = TimeToShutdown - 5;
    private static final int TimeToShutdownWaring3 = TimeToShutdown - 3;
    private static final int TimeToShutdownWaring4 = TimeToShutdown - 2;
    private static final int TimeToShutdownWaring5 = TimeToShutdown - 1;

    private class ShutdownRunnable implements Runnable {
        @Override
        public void run() {
            ResultData resultData = new ResultData();
            int HHmm = Integer.parseInt(NumFormatUtil.getFormatTime());
            System.out.println("---HHmm------->" + HHmm);
            if (TimeToShutdownWaring1 == HHmm) {
                resultData.setRetMsg("11122", "距离自动关机还有10分钟！");
            }
            if (TimeToShutdownWaring2 == HHmm) {
                resultData.setRetMsg("11122", "距离自动关机还有5分钟！");
            }
            if (TimeToShutdownWaring3 == HHmm) {
                resultData.setRetMsg("11122", "距离自动关机还有3分钟！");
            }
            if (TimeToShutdownWaring4 == HHmm) {
                resultData.setRetMsg("11122", "距离自动关机还有2分钟！");
            }
            if (TimeToShutdownWaring5 == HHmm) {
                resultData.setRetMsg("11122", "距离自动关机还有1分钟！");
            }
            if (TimeToShutdown == HHmm) {
                resultData.setRetMsg("222111", "开始关机");

            }
            EventBus.getDefault().post(new ShutdownMsg(resultData));
        }
    }

}
