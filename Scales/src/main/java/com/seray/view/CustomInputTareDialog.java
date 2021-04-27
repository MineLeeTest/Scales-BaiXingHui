package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scales.R;
import com.seray.util.NumFormatUtil;

import java.math.BigDecimal;

/**
 * Created by SR_Android on 2017/11/23.
 * 手动输入皮重、单价
 */

public class CustomInputTareDialog extends Dialog implements View.OnClickListener {

    private OnPositiveClickListener positiveClickListener;
    private OnNegativeClickListener negativeClickListener;
    private Context mContext;
    private Misc mMisc;
    private CustomInputTareDialog mDialog;
    private TextView tv;
    private boolean isOpenJin = false;
    private String titleMsg;
    private String alertContent;

    public CustomInputTareDialog(@NonNull Context context, String titleMsg, String alertContent) {
        super(context, R.style.Dialog);
        mContext = context;
        this.titleMsg = titleMsg;
        this.alertContent = alertContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_input_tare_dialog_layout);
        setCanceledOnTouchOutside(true);
        mDialog = this;
        tv = mDialog.findViewById(R.id.custom_input_tare_weight);
        setMessage();
        mMisc = Misc.newInstance();
        this.setOnKeyListener((dialog, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                mMisc.beep();
                String txt = tv.getText().toString();
                if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                        .KEYCODE_NUMPAD_9) {
                    int i = txt.indexOf(".");
                    if (i < 0 || i > txt.length() - 4)
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                        .KEYCODE_9) {
                    int i = txt.indexOf(".");
                    if (i < 0 || i > txt.length() - 4)
                        txt += keyCode - KeyEvent.KEYCODE_0;
                } else if (keyCode == KeyEvent.KEYCODE_E) {
                    if (!txt.contains("."))
                        txt += ".";
                } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                    if (!txt.isEmpty())
                        txt = txt.substring(0, txt.length() - 1);
                } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                    txt = "";
                }
                tv.setText(txt);
            }
            return true;
        });
    }

    private void setMessage() {
        TextView messageView = findViewById(R.id.custom_input_alert_msg);
        TextView tittleView = findViewById(R.id.custom_input_title_msg);
        messageView.setText(this.alertContent);
        tittleView.setText(this.titleMsg);

    }

    public void setOnPositiveClickListener(@StringRes int str, @Nullable OnPositiveClickListener listener) {
        this.positiveClickListener = listener;
        Button button = (Button) findViewById(R.id.custom_input_tare_positive);
        button.setText(str);
        if (this.positiveClickListener != null) {
            button.setOnClickListener(this);
        }
    }

    public void setOnNegativeClickListener(@StringRes int str, @Nullable OnNegativeClickListener listener) {
        this.negativeClickListener = listener;
        Button button = (Button) findViewById(R.id.custom_input_tare_negative);
        button.setText(str);
        if (this.negativeClickListener != null) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        mMisc.beep();
        switch (v.getId()) {
            case R.id.custom_input_tare_positive:
                String tvData = tv.getText().toString();
                if (TextUtils.isEmpty(tvData) || !NumFormatUtil.isNumeric(tvData)) {
                    tvData = "0";
                }
                positiveClickListener.onPositiveClick(mDialog, BigDecimal.valueOf(Double.parseDouble(tvData)));
                break;
            case R.id.custom_input_tare_negative:
                negativeClickListener.onNegativeClick(mDialog);
                break;
        }
    }

    public interface OnPositiveClickListener {
        void onPositiveClick(CustomInputTareDialog dialog, BigDecimal weight);
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(CustomInputTareDialog dialog);
    }
}
