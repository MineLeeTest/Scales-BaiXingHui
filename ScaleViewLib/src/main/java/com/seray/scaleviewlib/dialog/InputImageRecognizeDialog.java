package com.seray.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scaleviewlib.R;

import java.util.regex.Pattern;

/**
 * Author：李程
 * CreateTime：2019/5/13 21:23
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class InputImageRecognizeDialog extends Dialog {

    private OnPositiveClickListener positiveClickListener;
    private Misc mMisc;
    private InputImageRecognizeDialog mDialog;
    private TextView mWeightTv;
    private Pattern isIntPattern = Pattern.compile("^(-?[0-9]+)$");

    public interface OnPositiveClickListener {
        void onPositiveClick(InputImageRecognizeDialog dialog, int value);
    }

    public InputImageRecognizeDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        mDialog = this;
        mMisc = Misc.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_recognize);
        mWeightTv = findViewById(R.id.image_recognize_min);
        setCanceledOnTouchOutside(true);
        setOnNegativeClickListener();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    String txt = mWeightTv.getText().toString();
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                        txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        if (!txt.isEmpty())
                            txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        txt = "";
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (positiveClickListener != null) {
                            if (!isInt(txt)) {
                                positiveClickListener.onPositiveClick(mDialog, -1);
                            } else {
                                positiveClickListener.onPositiveClick(mDialog, Integer.parseInt(txt));
                            }
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

    private boolean isInt(String str) {
        return !TextUtils.isEmpty(str) && isIntPattern.matcher(str).matches();
    }

    public void setOnPositiveClickListener(@Nullable OnPositiveClickListener listener) {
        this.positiveClickListener = listener;
        Button button = (Button) findViewById(R.id.dialog_positive);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                if (positiveClickListener != null) {
                    String toString = mWeightTv.getText().toString();
                    if (!isInt(toString)) {
                        positiveClickListener.onPositiveClick(mDialog, -1);
                    } else {
                        positiveClickListener.onPositiveClick(mDialog, Integer.parseInt(toString));
                    }
                }
                mDialog.dismiss();
            }
        });
    }

    private void setOnNegativeClickListener() {
        Button button = (Button) findViewById(R.id.dialog_negative);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                mDialog.dismiss();
            }
        });
    }
}
