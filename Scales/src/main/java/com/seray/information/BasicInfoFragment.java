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
import com.seray.util.HttpUtils;

public class BasicInfoFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.basic_fragment_layout, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
//        ((TextView) view.findViewById(R.id.basic_boothName)).setText(CacheHelper.BoothName);
//        ((TextView) view.findViewById(R.id.basic_boothId)).setText(CacheHelper.BoothId);
//        ((TextView) view.findViewById(R.id.basic_shopName)).setText(CacheHelper.ShopName);
        ((TextView) view.findViewById(R.id.basic_marketName)).setText(CacheHelper.company_name);
        ((TextView) view.findViewById(R.id.basic_localIp)).setText(HttpUtils.getLocalIpStr(getContext()));
    }
}
