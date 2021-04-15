package com.seray.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.seray.scales.R;
import com.seray.cache.CacheHelper;

public class PrintOrderConfigFragment extends BaseConfigFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config_print_fragment, container, false);
        recoverChecked(view);
        initListener(view);
        return view;
    }

    private void initListener(View parent) {
        parent.findViewById(R.id.config_print_qr).setOnClickListener(this);
        parent.findViewById(R.id.config_print_price).setOnClickListener(this);
        parent.findViewById(R.id.config_print_recognize).setOnClickListener(this);
    }

    @Override
    public void recoverChecked(View parent) {
        ToggleButton qrBtn = (ToggleButton) parent.findViewById(R.id.config_print_qr);
        ToggleButton priceBtn = (ToggleButton) parent.findViewById(R.id.config_print_price);
        ToggleButton logBtn = (ToggleButton) parent.findViewById(R.id.config_print_recognize);
//        qrBtn.setChecked(CacheHelper.isOpenPrintQR);
//        priceBtn.setChecked(CacheHelper.isPrintPrice);
//        logBtn.setChecked(CacheHelper.isPrintRecognizeLog);
    }
}
