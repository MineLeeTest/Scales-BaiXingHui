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
import android.widget.Toast;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scaleviewlib.R;
import com.seray.scaleviewlib.callback.INegativeClickListener;
import com.seray.scaleviewlib.callback.IPositiveClickListener;

import java.util.regex.Pattern;

public class ScalePrintDialog extends Dialog {

    private Misc mMisc;
    private IPositiveClickListener<ScalePrintDialog, Integer> positiveClickListener;
    private INegativeClickListener<ScalePrintDialog> negativeClickListener;
    private TextView mCountView;
    private ScalePrintDialog mDialog;

    public ScalePrintDialog(@NonNull Context context) {
        super(context, R.style.Dialog);
        mMisc = Misc.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_print_layout);
        setCanceledOnTouchOutside(false);
        mCountView = findViewById(R.id.custom_print_count);
        mDialog = this;
        setPrintMessage();
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mMisc.beep();
                    String txt = mCountView.getText().toString();
                    if (keyCode > KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) {
                        txt += keyCode - KeyEvent.KEYCODE_NUMPAD_0;
                    } else if (keyCode > KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {
                        txt += keyCode - KeyEvent.KEYCODE_0;
                    } else if (keyCode == KeyEvent.KEYCODE_NUM_LOCK) {
                        if (!txt.isEmpty())
                            txt = txt.substring(0, txt.length() - 1);
                    } else if (keyCode == KeyEvent.KEYCODE_NUMPAD_DOT) {
                        txt = "";
                    }
                    mCountView.setText(txt);
                    if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        check(txt);
                    } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_NUMPAD_DIVIDE) {
                        if (negativeClickListener == null)
                            check("1");
                        else
                            mDialog.dismiss();
                    }
                    return true;
                }
                return true;
            }
        });
    }

    private void setPrintMessage() {
        TextView msgView = findViewById(R.id.custom_print_message);
        String msg = "限制一次最多打印32份";
        msgView.setText(msg);
    }

    private void check(String con) {
        int count;
        if (con.isEmpty()) {
            count = 1;
            positiveClickListener.OnPositiveClickListener(mDialog, count);
            mDialog.dismiss();
        } else {
            boolean numeric = isNumeric(con);
            if (numeric) {
                count = Integer.parseInt(con);
                if (count > 32) {
                    showToast();
                    mCountView.setText("");
                } else {
                    if (count < 1)
                        count = 1;
                    positiveClickListener.OnPositiveClickListener(mDialog, count);
                    mDialog.dismiss();
                }
            } else {
                showToast();
                mCountView.setText("");
            }
        }
    }

    private boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^(-?[0-9.]+)$");
        return !TextUtils.isEmpty(str) && pattern.matcher(str).matches();
    }

    private void showToast() {
        Toast.makeText(getContext(), "请重新输入打印份数", Toast.LENGTH_SHORT).show();
    }

    public void setOnPositiveClickListener(@NonNull IPositiveClickListener<ScalePrintDialog, Integer> listener) {
        this.positiveClickListener = listener;
        findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                String weight = mCountView.getText().toString();
                check(weight);
            }
        });
    }

    public void setOnNegativeClickListener(@Nullable INegativeClickListener<ScalePrintDialog> listener) {
        this.negativeClickListener = listener;
    }
}
