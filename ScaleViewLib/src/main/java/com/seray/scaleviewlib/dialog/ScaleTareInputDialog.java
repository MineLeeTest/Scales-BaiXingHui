package com.seray.scaleviewlib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scaleviewlib.R;
import com.seray.scaleviewlib.callback.IPositiveClickListener;

import java.util.regex.Pattern;

public class ScaleTareInputDialog extends Dialog {

    private Context mContext;
    private Misc mMisc;
    private ScaleTareInputDialog mDialog;
    private TextView mWeightTv;
    private boolean isOpenJin = false;
    private IPositiveClickListener<ScaleTareInputDialog, String> positiveClickListener;

    public ScaleTareInputDialog(@NonNull Context context, boolean isOpenJin) {
        super(context, R.style.Dialog);
        mContext = context;
        this.isOpenJin = isOpenJin;
        mMisc = Misc.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tare_input_layout);
        setCanceledOnTouchOutside(true);
        mDialog = this;
        initView();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    String txt = mWeightTv.getText().toString();
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
                        int i = txt.indexOf(".");
                        if (i < 0 || (i > -1 && i > txt.length() - 4))
                            txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
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
                            if (txt.equals(".") || !isNumeric(txt))
                                txt = "0.000";
                            float tare = Float.parseFloat(txt);
                            if (isOpenJin) {
                                if (tare > 0)
                                    tare = tare / 2;
                            }
                            txt = Float.toString(tare);
                            positiveClickListener.OnPositiveClickListener(mDialog, txt);
                        }
                        mDialog.dismiss();
                    } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        mDialog.dismiss();
                    }
                    mWeightTv.setText(txt);
                }
                return true;
            }
        });
    }

    private void initView() {
        findViewById(R.id.dialog_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                mDialog.dismiss();
            }
        });
        mWeightTv = findViewById(R.id.custom_input_tare_weight);
        TextView messageView = findViewById(R.id.custom_input_tare_message);
        String content = mContext.getString(R.string.dialog_tare_unit);
        if (isOpenJin)
            content += mContext.getString(R.string.unit_jin);
        else
            content += mContext.getString(R.string.unit_kg);
        messageView.setText(content);
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^(-?[0-9.]+)$");
        return !TextUtils.isEmpty(str) && pattern.matcher(str).matches();
    }

    public void setOnPositiveClickListener(@Nullable IPositiveClickListener<ScaleTareInputDialog, String> listener) {
        this.positiveClickListener = listener;
        findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                if (positiveClickListener != null) {
                    String weight = mWeightTv.getText().toString();
                    if (weight.equals(".") || !isNumeric(weight))
                        weight = "0.000";
                    float tare = Float.parseFloat(weight);
                    if (isOpenJin) {
                        if (tare > 0)
                            tare = tare / 2;
                    }
                    weight = Float.toString(tare);
                    positiveClickListener.OnPositiveClickListener(mDialog, weight);
                }
                mDialog.dismiss();
            }
        });
    }
}
