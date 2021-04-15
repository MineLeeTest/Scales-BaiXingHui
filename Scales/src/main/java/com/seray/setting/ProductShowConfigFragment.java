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

public class ProductShowConfigFragment extends BaseConfigFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.config_product_fragment, container, false);
        recoverChecked(view);
        initListener(view);
        return view;
    }

    private void initListener(View parent) {
        parent.findViewById(R.id.config_save_name).setOnClickListener(this);
        parent.findViewById(R.id.config_save_price).setOnClickListener(this);
        parent.findViewById(R.id.config_force_selected).setOnClickListener(this);
    }

    @Override
    public void recoverChecked(View parent) {
        ToggleButton nameBtn = (ToggleButton) parent.findViewById(R.id.config_save_name);
        ToggleButton priceBtn = (ToggleButton) parent.findViewById(R.id.config_save_price);
        ToggleButton selectedBtn = (ToggleButton) parent.findViewById(R.id.config_force_selected);
//        nameBtn.setChecked(CacheHelper.isHoldPlu);
//        priceBtn.setChecked(CacheHelper.isHoldPrice);
//        selectedBtn.setChecked(CacheHelper.isNoPluNoSum);
    }
}
