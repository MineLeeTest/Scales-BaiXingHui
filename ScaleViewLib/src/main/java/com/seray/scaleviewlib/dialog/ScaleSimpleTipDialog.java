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
import com.seray.scaleviewlib.callback.IPositiveClickListener;

public class ScaleSimpleTipDialog extends Dialog {

    private IPositiveClickListener<ScaleSimpleTipDialog, String> positiveClickListener;
    private Misc mMisc;
    private ScaleSimpleTipDialog mDialog;
    private Context mContext;

    public ScaleSimpleTipDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        mContext = context;
        mMisc = Misc.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_simple_tip_layout);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        mDialog = this;
        setOnPositiveClickListener(R.string.dialog_positive, null);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        mMisc.beep();
                        mDialog.dismiss();
                        if (positiveClickListener != null) {
                            positiveClickListener.OnPositiveClickListener(mDialog, null);
                            return true;
                        }
                    }
                    if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        mMisc.beep();
//                        mDialog.dismiss();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    public void setTitle(@Nullable CharSequence title) {
        TextView view = findViewById(R.id.dialog_simple_tip_title);
        view.setText(title);
    }

    public void setTitle(@StringRes int titleId) {
        TextView view = findViewById(R.id.dialog_simple_tip_title);
        view.setText(titleId);
    }

    public void setOnPositiveClickListener(@StringRes int str, @Nullable IPositiveClickListener<ScaleSimpleTipDialog, String> listener) {
        String string = mContext.getString(str);
        this.setOnPositiveClickListener(string, listener);
    }

    public void setOnPositiveClickListener(@NonNull String str, @Nullable IPositiveClickListener<ScaleSimpleTipDialog, String> listener) {
        this.positiveClickListener = listener;
        Button button = findViewById(R.id.dialog_positive);
        button.setText(str);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                if (positiveClickListener != null)
                    positiveClickListener.OnPositiveClickListener(mDialog, null);
                mDialog.dismiss();
            }
        });
    }

    public void setMessage(@NonNull String message) {
        TextView view = findViewById(R.id.dialog_simple_tip_msg);
        view.setText(message);
    }

    public void setMessage(@StringRes int messageId) {
        TextView view = findViewById(R.id.dialog_simple_tip_msg);
        view.setText(messageId);
    }
}
