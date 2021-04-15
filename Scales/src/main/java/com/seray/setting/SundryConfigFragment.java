package com.seray.setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.seray.cache.CacheHelper;
import com.seray.scales.R;


public class SundryConfigFragment extends BaseConfigFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config_sundry_fragment, container, false);
        initListener(view);
        recoverChecked(view);
        return view;
    }

    private void initListener(View parent) {
        parent.findViewById(R.id.config_backDisplay).setOnClickListener(this);
        parent.findViewById(R.id.config_change_price).setOnClickListener(this);
        parent.findViewById(R.id.config_open_battery).setOnClickListener(this);
        parent.findViewById(R.id.config_open_collection).setOnClickListener(this);
    }

    @Override
    public void recoverChecked(View parent) {
        ToggleButton backBtn = (ToggleButton) parent.findViewById(R.id.config_backDisplay);
        ToggleButton priceBtn = (ToggleButton) parent.findViewById(R.id.config_change_price);
        ToggleButton batteryBtn = (ToggleButton) parent.findViewById(R.id.config_open_battery);
        ToggleButton collectionBtn = (ToggleButton) parent.findViewById(R.id.config_open_collection);
        backBtn.setChecked(CacheHelper.isOpenBackDisplay);
        priceBtn.setChecked(CacheHelper.isChangePrice);
//        batteryBtn.setChecked(CacheHelper.isOpenBattery);
//        collectionBtn.setChecked(CacheHelper.isOpenCollection);
    }
}
