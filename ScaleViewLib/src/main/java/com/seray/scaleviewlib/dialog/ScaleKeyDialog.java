package com.seray.scaleviewlib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scaleviewlib.R;
import com.seray.scaleviewlib.callback.IPositiveClickListener;

public class ScaleKeyDialog extends Dialog {

    private Misc mMisc;
    private IPositiveClickListener<ScaleKeyDialog, String> positiveClickListener;
    //    private TextView mKeyView;
    private EditText mKeyView;
    private ScaleKeyDialog mDialog;

    public ScaleKeyDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        mMisc = Misc.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_key_layout);
        setCanceledOnTouchOutside(true);
        mDialog = this;
        mKeyView = findViewById(R.id.custom_key_content);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    String txt = mKeyView.getText().toString();
                    // 修复密码输入0无效的BUG
                    if (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                        txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        if (!txt.isEmpty())
                            txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        txt = "";
                    }
                    mKeyView.setText(txt);
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (positiveClickListener != null)
                            positiveClickListener.OnPositiveClickListener(mDialog, txt);
                        else
                            mDialog.dismiss();
                    } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        mDialog.dismiss();
                    }
                    return true;
                }
                return true;
            }
        });
    }

    public void setOnPositiveClickListener(@NonNull IPositiveClickListener<ScaleKeyDialog, String> listener) {
        this.positiveClickListener = listener;
        findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                if (positiveClickListener != null) {
                    String key = mKeyView.getText().toString();
                    positiveClickListener.OnPositiveClickListener(mDialog, key);
                } else {
                    mDialog.dismiss();
                }
            }
        });
    }
}
