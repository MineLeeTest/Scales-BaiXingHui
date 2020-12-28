package com.seray.sjc.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scales.R;
import com.seray.sjc.AppExecutors;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;

/**
 * 网络ip和端口动态配置Fragment
 */
public class SetWebDialogFragment extends DialogFragment {

    private Misc mMisc = Misc.newInstance();
    private EditText etIP, etPort;

    private View mRootView;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public interface SetwebInrerface {
        void setWebConfirm(String ip, String port);

        void setWebCancel();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_setweb, container, false);
        }
        initView();
        return mRootView;
    }

    private void initView() {
        etIP = mRootView.findViewById(R.id.et_ip);
        etPort = mRootView.findViewById(R.id.et_port);
        mRootView.findViewById(R.id.dialog_negative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                SetwebInrerface setwebInrerface = (SetwebInrerface) getActivity();
                setwebInrerface.setWebCancel();
            }
        });
        mRootView.findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMisc.beep();
                SetwebInrerface setwebInrerface = (SetwebInrerface) getActivity();
                String ip = etIP.getText().toString();
                String port = etPort.getText().toString();
                setwebInrerface.setWebConfirm(ip, port);
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AppExecutors.getInstance().queryIO().submit(new Runnable() {
            @Override
            public void run() {
                ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
                final String ip = configDao.get(SjcConfig.WEB_IP);
                final String port = configDao.get(SjcConfig.WEB_PORT);

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!TextUtils.isEmpty(ip)) {
                            etIP.setText(ip);
                            etIP.setSelection(ip.length());
                        }

                        if (!TextUtils.isEmpty(port)) {
                            etPort.setText(port);
                            etPort.setSelection(port.length());
                        }
                    }
                });
            }
        });
    }
}
