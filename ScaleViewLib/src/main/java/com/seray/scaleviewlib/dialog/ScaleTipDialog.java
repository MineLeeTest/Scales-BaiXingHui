package com.seray.scaleviewlib.dialog;

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
import com.seray.scaleviewlib.R;
import com.seray.scaleviewlib.callback.INegativeClickListener;
import com.seray.scaleviewlib.callback.IPositiveClickListener;

public class ScaleTipDialog extends Dialog {

    private IPositiveClickListener<ScaleTipDialog, ?> positiveClickListener;
    private INegativeClickListener<ScaleTipDialog> negativeClickListener;
    private Misc mMisc;
    private ScaleTipDialog mDialog;
    private Context mContext;

    public ScaleTipDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        mContext = context;
        mMisc = Misc.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tip_layout);
        setCanceledOnTouchOutside(false);
        mDialog = this;
        setOnNegativeClickListener(R.string.dialog_negative, null);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        mMisc.beep();
                        mDialog.dismiss();
                        if (positiveClickListener != null)
                            positiveClickListener.OnPositiveClickListener(mDialog, null);
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        mMisc.beep();
                        mDialog.dismiss();
                        if (negativeClickListener != null)
                            negativeClickListener.OnNegativeClickListener(mDialog);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void setTitle(@Nullable CharSequence title) {
        TextView view = findViewById(R.id.dialog_tip_title);
        view.setText(title);
    }

    @Override
    public void setTitle(@StringRes int titleId) {
        TextView view = findViewById(R.id.dialog_tip_title);
        view.setText(titleId);
    }

    public void setOnNegativeClickListener(@StringRes int str, @Nullable INegativeClickListener<ScaleTipDialog> listener) {
        String string = mContext.getString(str);
        this.setOnNegativeClickListener(string, listener);
    }

    public void setOnNegativeClickListener(@NonNull String str, @Nullable INegativeClickListener<ScaleTipDialog> listener) {
        this.negativeClickListener = listener;
        Button button = findViewById(R.id.dialog_negative);
        button.setText(str);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                mDialog.dismiss();
                if (negativeClickListener != null)
                    negativeClickListener.OnNegativeClickListener(mDialog);
            }
        });
    }

    public void setMessage(@StringRes int str) {
        TextView view = findViewById(R.id.dialog_tip_msg);
        view.setText(str);
    }

    public void setMessage(@NonNull String str) {
        TextView view = findViewById(R.id.dialog_tip_msg);
        view.setText(str);
    }

    public void setOnPositiveClickListener(@StringRes int str, @Nullable IPositiveClickListener<ScaleTipDialog, ?> listener) {
        String string = mContext.getString(str);
        this.setOnPositiveClickListener(string, listener);
    }

    public void setOnPositiveClickListener(@NonNull String str, @Nullable IPositiveClickListener<ScaleTipDialog, ?> listener) {
        this.positiveClickListener = listener;
        Button button = findViewById(R.id.dialog_positive);
        button.setText(str);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                mDialog.dismiss();
                if (positiveClickListener != null)
                    positiveClickListener.OnPositiveClickListener(mDialog, null);
            }
        });
    }
}
