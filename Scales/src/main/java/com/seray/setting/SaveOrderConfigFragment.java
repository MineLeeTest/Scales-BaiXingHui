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

public class SaveOrderConfigFragment extends BaseConfigFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config_save_fragment, container, false);
        initListener(view);
        recoverChecked(view);
        return view;
    }

    private void initListener(View parent) {
        parent.findViewById(R.id.config_force_insert).setOnClickListener(this);
        parent.findViewById(R.id.config_force_picture).setOnClickListener(this);
    }

    @Override
    public void recoverChecked(View parent) {
        ToggleButton forceRecordBtn = (ToggleButton) parent.findViewById(R.id.config_force_insert);
        forceRecordBtn.setChecked(CacheHelper.isOpenForceRecord);
        ToggleButton pictureBtn = (ToggleButton) parent.findViewById(R.id.config_force_picture);
//        pictureBtn.setChecked(CacheHelper.isOpenCamera);
    }
}
