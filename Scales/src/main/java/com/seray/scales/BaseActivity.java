package com.seray.scales;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.cache.CacheHelper;
import com.seray.inter.DisplayController;
import com.seray.message.LocalFileTag;
import com.seray.message.QuantifyMessage;
import com.seray.sjc.AppExecutors;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public abstract class BaseActivity extends AppCompatActivity implements OnClickListener {

    /**
     * 执行数据库查询固定数量线程池
     */
    public static ExecutorService sqlQueryThread = AppExecutors.getInstance().queryIO();

    /**
     * 执行定时任务固定数量线程池
     */
    public static ScheduledExecutorService timerThreads = Executors.newScheduledThreadPool(2);

    /**
     * 蜂鸣器控制器
     */
    public Misc mMisc;
    /**
     * 后显示控制器
     */
    public DisplayController mController;
    /**
     * 打印控制器
     */
//    public CustomPrinter mCustomPrinter = CustomPrinter.getInstance();

    protected Dialog mLoadingDialog;
    private AVLoadingIndicatorView mLoadingDialogImage;
    private TextView mLoadingDialogMessageView;

    public void showLoading(String message) {
        if (mLoadingDialog == null) {
            View v = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null);
            mLoadingDialogMessageView = v.findViewById(R.id.dialog_loading_tv);
            LinearLayout layout = v.findViewById(R.id.dialog_loading_view);
            mLoadingDialogImage = v.findViewById(R.id.dialog_loading_av);
            mLoadingDialog = new Dialog(this, R.style.MyDialogStyle);
            mLoadingDialog.setCancelable(false);
            mLoadingDialog.setCanceledOnTouchOutside(false);
            mLoadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
            Window window = mLoadingDialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setGravity(Gravity.CENTER);
            window.setAttributes(lp);
        }
        mLoadingDialogMessageView.setText(message);
        mLoadingDialogImage.smoothToShow();
        mLoadingDialog.onAttachedToWindow();
        mLoadingDialog.show();
    }

    public void dismissLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialogMessageView.post(() -> {
                mLoadingDialogImage.smoothToHide();
                mLoadingDialog.dismiss();
            });
        }
    }

    private Toast mToast;
    private String msg;
    private Handler mHandler = new Handler();

    Runnable showRun = new Runnable() {
        @SuppressLint("ShowToast")
        @Override
        public void run() {
            if (mToast == null) {
                mToast = Toast.makeText(App.getApplication(), msg, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(msg);
            }
            mToast.show();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_F3) {
            shutDown();
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMisc = Misc.newInstance();
        mController = DisplayController.getInstance();
        App.getApplication().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getApplication().removeActivity(this);
    }

    /**
     * 关机操作
     */
    public void shutDown() {
        Intent shutDownIntent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
        shutDownIntent.putExtra("android.intent.extra.KEY_CONFIRM", true);
        shutDownIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(shutDownIntent);
    }

    /**
     * 显示吐司
     */
    public void showMessage(int msg) {
        try {
            this.showMessage(getResources().getString(msg));
        } catch (NotFoundException e) {

        }
    }

    /**
     * 显示吐司
     */
    public void showMessage(final String msg) {
        this.msg = msg;
        mHandler.post(showRun);
    }

    /**
     * 设置是否允许关闭Dialog
     */
    public void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialogInterface, close);
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
        }
    }

    /**
     * 创建密码对话框并支持小键盘输入
     */
    public void openManageKey(final int flag, final Button btn) {
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.user_dialog, null);
        final EditText userPwdEdit = (EditText) view.findViewById(R.id.password_edit);

        userPwdEdit.setCursorVisible(false);
        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.operation_usercode)
                .setView(view)
                .setPositiveButton(R.string.reprint_ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        String userPwd = userPwdEdit.getText().toString();
//                        if (userPwd.equals(randomPwd)) {
                            canCloseDialog(dialog, true);
                            switch (flag) {
                                case NumFormatUtil.PASSWORD_TO_UNIT:
                                    if (CacheHelper.isOpenJin) {
                                        CacheHelper.isOpenJin = false;
                                        if (btn != null) {
                                            btn.setText(R.string.manager_change_weight_unit_2);
                                        }
                                    } else {
                                        CacheHelper.isOpenJin = true;
                                        if (btn != null) {
                                            btn.setText(R.string.manager_change_weight_unit_1);
                                        }
                                    }
                                    String curUnit = CacheHelper.isOpenJin ? getString(R.string
                                            .manager_scale_jin) : getString(R.string
                                            .manager_scale_kg);
                                    showMessage(getResources().getString(R.string.setting_unit_type)
                                            + curUnit);
                                    EventBus.getDefault().post(new QuantifyMessage(CacheHelper
                                            .isOpenJin));
                                    break;
                                case NumFormatUtil.PASSWORD_TO_OPERATION:
                                    startActivity(OperationActivity.class);
                                    break;
                                case NumFormatUtil.PASSWORD_TO_SETTING:
                                    startActivity(SettingActivity.class);
                                    break;
                                case NumFormatUtil.PASSWORD_TO_REPORT:
//                                    startActivity(NewReportActivity.class);
                                    break;
                            }
//                        } else {
//                            canCloseDialog(dialog, false);
//                            showMessage(getResources().getString(R.string
//                                    .operation_usercode_wrong));
//                            userPwdEdit.setText("");
//                        }
                    }
                }).setNegativeButton(R.string.reprint_cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        canCloseDialog(dialog, true);
                    }
                }).setOnKeyListener(new OnKeyListener() {

                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            mMisc.beep();
                            String txt = userPwdEdit.getText().toString();
                            if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                                    .KEYCODE_NUMPAD_9) {
                                txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                            } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                                    .KEYCODE_9) {
                                txt += keyCode - KeyEvent.KEYCODE_0;
                            } else if (keyCode == KeyEvent.KEYCODE_E) {
                                txt += ".";
                            } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                                if (!txt.isEmpty()) {
                                    txt = txt.substring(0, txt.length() - 1);
                                }
                            } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                                txt = "";
                            } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                                InputMethodManager imm = (InputMethodManager) getSystemService
                                        (INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(userPwdEdit.getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                                return true;
                            }
                            userPwdEdit.setText(txt);
                            userPwdEdit.setSelection(txt.length());
                        }
                        return true;
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 默认模式启动Activity（无对象传递）
     */
    public void startActivity(Class<?> targetActivity) {
        startActivity(getSkipIntent(targetActivity));
    }


    protected Intent getSkipIntent(Class<?> targetActivity) {
        return new Intent(getApplicationContext(), targetActivity);
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
    }

}
