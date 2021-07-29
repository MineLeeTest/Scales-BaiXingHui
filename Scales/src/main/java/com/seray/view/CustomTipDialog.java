package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scales.R;

public class CustomTipDialog extends Dialog {

    private OnPositiveClickListener positiveClickListener;
    private OnNegativeClickListener negativeClickListener;
    private Misc mMisc;
    private CustomTipDialog mDialog;
    private Context mContext;
    public CustomTipDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        mContext = context;
    }

    public void setMessage(@StringRes int str) {
        TextView view = (TextView) findViewById(R.id.custom_tip_msg);
        view.setText(str);
    }

    public void setMessage(@NonNull String str) {
        TextView view = (TextView) findViewById(R.id.custom_tip_msg);
        view.setText(str);
    }

    public String getMessage() {
        TextView view = (TextView) findViewById(R.id.custom_tip_msg);
        return view.getText().toString();
    }

    public void setTitle(@StringRes int str) {
        TextView view = (TextView) findViewById(R.id.custom_tip_title);
        view.setText(str);
    }

    public void setTitle(@NonNull String str) {
        TextView view = findViewById(R.id.custom_tip_title);
        view.setText(str);
    }

    public void setOnPositiveClickListener(@NonNull String str, @Nullable OnPositiveClickListener listener) {
        this.positiveClickListener = listener;
        Button button = (Button) findViewById(R.id.dialog_positive);
        button.setText(str);
        if (this.positiveClickListener != null) {
            button.setOnClickListener(v -> {
                mMisc.beep();
                positiveClickListener.onPositiveClick(mDialog);
                mDialog.dismiss();
            });
        }
    }

    public void setOnPositiveClickListener(@StringRes int str, @Nullable OnPositiveClickListener listener) {
        String string = mContext.getString(str);
        this.setOnPositiveClickListener(string, listener);
    }

    public void setOnNegativeClickListenerPrivate(String name, @Nullable OnNegativeClickListener listener) {
        this.negativeClickListener = listener;
        Button button = findViewById(R.id.dialog_negative);
        button.setText(name);
        if (this.negativeClickListener != null) {
            button.setOnClickListener(v -> {
                mMisc.beep();
                negativeClickListener.onNegativeClick(mDialog);
                mDialog.dismiss();
            });
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_tip_dialog_layout);
        setCanceledOnTouchOutside(false);
        mMisc = Misc.newInstance();
        mDialog = this;
        setOnKeyListener((dialog, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    mMisc.beep();
                    if (positiveClickListener != null) {
                        positiveClickListener.onPositiveClick(mDialog);
                        mDialog.dismiss();
                        return true;
                    }
                }
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                    mMisc.beep();
                    mDialog.dismiss();
                    return true;
                }
            }
            return false;
        });
    }

    public interface OnPositiveClickListener {
        void onPositiveClick(CustomTipDialog dialog);
    }

    public interface OnNegativeClickListener {
        void onNegativeClick(CustomTipDialog dialog);
    }
}
