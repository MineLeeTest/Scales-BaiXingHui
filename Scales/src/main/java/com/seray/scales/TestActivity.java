package com.seray.scales;


import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.seray.sjc.view.CpuCardManagerDialog;
import com.tscale.scalelib.jniscale.JNIScale;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TestActivity extends BaseActivity {

    private ScaleHandler mScaleHandler = new ScaleHandler(new WeakReference<>(this));
    private ScheduledExecutorService timerThreads = Executors.newScheduledThreadPool(1);
    private JNIScale mScale;
    private TextView mKeyNameView, mWeightView;
    private boolean isStop = false;

    private SoundPool soundPool;
    private int paySuccessSound = -1; // 支付成功语音
    private int maxVolume = -1; // 系统最大语音
    private int currentVolume = -1; // 系统当前语音
    private AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.seray.scales.R.layout.activity_test);
        mKeyNameView = findViewById(R.id.test_key_name);
        mWeightView = findViewById(R.id.test_weight);
        mScale = JNIScale.getScale();
        timer();


        // 初始化音量及播放语音
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundPool.load(this, R.raw.pay_suceesee, 1);
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> paySuccessSound = sampleId);
    }

    private void playPaySuccessSound() {
        try {
            if (paySuccessSound != -1 && audioManager != null) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
                soundPool.play(paySuccessSound, 1, 1, 1, 0, 1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void timer() {
        Runnable timerRun = new Runnable() {
            @Override
            public void run() {
                if (!isStop)
                    mScaleHandler.sendEmptyMessage(1);
            }
        };
        timerThreads.scheduleAtFixedRate(timerRun, 1500, 50, TimeUnit.MILLISECONDS);
    }

    private static class ScaleHandler extends Handler {

        WeakReference<TestActivity> mWeakReference;

        ScaleHandler(WeakReference<TestActivity> weakReference) {
            this.mWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TestActivity activity = mWeakReference.get();
            if (activity != null && !activity.isFinishing()) {
                if (msg.what == 1) {
                    activity.mWeightView.setText(activity.mScale.getStringNet());
                }
            }
        }
    }

    private void showKeyName(String name) {
        mKeyNameView.setText(String.format("%s-%s", "按键", name));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mMisc.beep();
        switch (keyCode) {
            case KeyEvent.KEYCODE_A:
                showKeyName("客户一");
                return true;
            case KeyEvent.KEYCODE_D:
                showKeyName("客户二");
                return true;
            case KeyEvent.KEYCODE_B:
                showKeyName("客户三");
                return true;
            case KeyEvent.KEYCODE_C:
                showKeyName("客户四");
                return true;
            case KeyEvent.KEYCODE_MENU:// 桌秤
            case KeyEvent.KEYCODE_MOVE_HOME:// 地秤
                showKeyName("菜单");
                return true;
            case KeyEvent.KEYCODE_F1:// 去皮
                showKeyName("去皮");
                return true;
            case KeyEvent.KEYCODE_F2:// 置零
                showKeyName("置零");
                return true;
            case KeyEvent.KEYCODE_NUM_LOCK: // 退格
                showKeyName("退格");
                return true;
            case KeyEvent.KEYCODE_DEL:// 语音
                showKeyName("语音");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_0:
                showKeyName("数字【0】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_1:
                showKeyName("数字【1】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
                showKeyName("数字【2】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
                showKeyName("数字【3】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_4:
                showKeyName("数字【4】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_5:
                showKeyName("数字【5】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                showKeyName("数字【6】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
                showKeyName("数字【7】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                showKeyName("数字【8】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                showKeyName("数字【9】");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT: // 一键清除
                showKeyName("清除");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ADD:// 价格修改操作
                showKeyName("保存");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER: // 打印
                showKeyName("打印");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:// 商品
                showKeyName("商品");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_MULTIPLY: // 计件计重切换
                showKeyName("计件");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DIVIDE:// 取消
                showKeyName("取消");
                return true;
            case KeyEvent.KEYCODE_E: // 小数点
                showKeyName("小数点");
                return true;
            case KeyEvent.KEYCODE_F3: // 电源
                showKeyName("电源");
                return true;
            case KeyEvent.KEYCODE_BACK: // 返回
                showKeyName("返回");
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onPrintTestClick(View view) {
        showMessage("test print");
    }

    public void onMiscTestClick(View view) {
        mMisc.beepEx(100, 50, 3);
    }

    public void onBackClick(View view) {
        mMisc.beep();
        finish();
    }

    public void onReadCardTestClick(View view) {
        mMisc.beep();
        BigDecimal transAmt = new BigDecimal("1.00");
        String transOrderCode = System.currentTimeMillis() + "";
        CpuCardManagerDialog dialog = CpuCardManagerDialog.getInstance(transOrderCode, transAmt);
        dialog.show(getSupportFragmentManager(), "CpuCardDialog");
    }

    public void onVoiceTestClick(View view) {
        mMisc.beep();
        playPaySuccessSound();
    }

    public void onOpenBoxTestClick(View view) {
        mMisc.beep();
        try {
            mMisc.openCashBoxAutoClose();
        } catch (Exception e) {
            Log.e("Misc", e.getMessage());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (audioManager != null && currentVolume != -1) // 重置音量
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
        if (soundPool != null)
            soundPool.release();
        isStop = true;
        timerThreads.shutdown();
        mScaleHandler.removeCallbacksAndMessages(null);
    }
}