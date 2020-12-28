package com.seray.information;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.seray.scales.R;
import com.seray.cache.CacheHelper;

public class DeviceInfoFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.device_fragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        ((TextView) view.findViewById(R.id.device_term_id)).setText(CacheHelper.TermId);
        ((TextView) view.findViewById(R.id.device_term_code)).setText(CacheHelper.TermCode);
        ((TextView) view.findViewById(R.id.device_term_name)).setText(CacheHelper.TermName);
        ((TextView) view.findViewById(R.id.device_factory_code)).setText(CacheHelper.DeviceCode);
    }
}
