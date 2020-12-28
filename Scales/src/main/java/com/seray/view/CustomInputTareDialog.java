package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scales.R;
import com.seray.util.NumFormatUtil;

/**
 * Created by SR_Android on 2017/11/23.
 * 手动输入扣重重量
 */

public class CustomInputTareDialog extends Dialog implements View.OnClickListener {

    private OnPositiveClickListener positiveClickListener;
    private OnNegativeClickListener negativeClickListener;
    private Context mContext;
    private Misc mMisc;
    private CustomInputTareDialog mDialog;
    private TextView mWeightTv;
    private boolean isOpenJin = false;

    public CustomInputTareDialog(@NonNull Context context, boolean isOpenJin) {
        super(context, R.style.Dialog);
        mContext = context;
        this.isOpenJin = isOpenJin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_input_tare_dialog_layout);
        setCanceledOnTouchOutside(true);
        setMessage();
        mWeightTv = (TextView) findViewById(R.id.custom_input_tare_weight);
        mDialog = this;
        mMisc = Misc.newInstance();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    String txt = mWeightTv.getText().toString();
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent
                            .KEYCODE_NUMPAD_9) {
                        int i = txt.indexOf(".");
                        if (i < 0 || (i > -1 && i > txt.length() - 4))
                            txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent
                            .KEYCODE_9) {
                        int i = txt.indexOf(".");
                        if (i < 0 || (i > -1 && i > txt.length() - 4))
                            txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_E) {
                        if (!txt.contains("."))
                            txt += ".";
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        if (!txt.isEmpty())
                            txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        txt = "";
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (positiveClickListener != null) {
                            if (txt.equals(".") || !NumFormatUtil.isNumeric(txt))
                                txt = "0.000";
                            float tare = Float.parseFloat(txt);
                            if (isOpenJin) {
                                if (tare > 0)
                                    tare = tare / 2;
                            }
                            txt = Float.toString(tare);
                            positiveClickListener.onPositiveClick(mDialog, txt);
                        }
                    } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        if (negativeClickListener != null)
                            negativeClickListener.onNegativeClick(mDialog);
                    }
                    mWeightTv.setText(txt);
                }
                return true;
            }
        });
    }

    private void setMessage() {
        TextView messageView = (TextView) findViewById(R.id.custom_input_tare_message);
        String content = mContext.getString(R.string.setting_unit_type);
        if (isOpenJin)
            content += mContext.getString(R.string.manager_scale_jin);
        else
            content += mContext.getString(R.string.manager_scale_kg);
        messageView.setText(content);
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
                String weight = mWeightTv.getText().toString();
                if (weight.equals(".") || !NumFormatUtil.isNumeric(weight))
                    weight = "0.000";
                float tare = Float.parseFloat(weight);
                if (isOpenJin) {
                    if (tare > 0)
                        tare = tare / 2;
                }
                weight = Float.toString(tare);
                positiveClickListener.onPositiveClick(mDialog, weight);
                break;
            case R.id.custom_input_tare_negative:
                negativeClickListener.onNegativeClick(mDialog);
                break;
        }
    }

    public interface OnPositiveClickListener {
        void onPositiveClick(CustomInputTareDialog dialog, String weight);
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(CustomInputTareDialog dialog);
    }
}
