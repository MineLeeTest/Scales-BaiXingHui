package com.seray.sjc.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.widget.TextView;

import com.seray.scales.R;
import com.seray.util.ScanGunKeyEventHelper;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * 扫码dialog
 */
public class ReportScanDialog extends Dialog implements ScanGunKeyEventHelper.OnScanSuccessListener {

    private ScanGunKeyEventHelper mGunHelper;
    private AVLoadingIndicatorView loadView;
    private OnLoadDataListener loadDataListener;

    public ReportScanDialog(@NonNull Context context) {
        super(context, R.style.MyDialogStyle);
        mGunHelper = new ScanGunKeyEventHelper(this);
    }

    public void setOnLoadDataListener(@NonNull OnLoadDataListener listener) {
        this.loadDataListener = listener;
    }

    @Override
    public void show() {
        super.show();
        if (loadView != null)
            loadView.show();
    }

    @Override
    public void dismiss() {
        loadView.hide();
        mGunHelper.onDestroy();
        super.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_progress_dialog);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        loadView = (AVLoadingIndicatorView) findViewById(R.id.dialog_loading_av);
        TextView msgView = (TextView) findViewById(R.id.dialog_loading_tv);
        msgView.setText("开始扫码");
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                delayDismiss("取消扫码");
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(@NonNull KeyEvent event) {
        int code = event.getKeyCode();
        if (code == KeyEvent.KEYCODE_BACK || code == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
            return super.dispatchKeyEvent(event);
        } else {
            mGunHelper.analysisKeyEvent(event);
        }
        return true;
    }

    @Override
    public void onScanSuccess(final String barcode) {
        if (barcode.isEmpty()) {
            if (loadDataListener != null) {
                delayDismiss("扫码失败");
                loadDataListener.onLoadFailed("空条码");
            }
            return;
        }
        delayDismiss("扫码成功");
        loadDataListener.onLoadSuccess(barcode);
    }

    private void delayDismiss(@NonNull String tip) {
        TextView msgView = (TextView) findViewById(R.id.dialog_loading_tv);
        msgView.setText(tip);
        loadView.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismiss();
            }
        }, 500);
    }

    public interface OnLoadDataListener {

        void onLoadSuccess(String result);
        void onLoadFailed(String error);
    }
}
