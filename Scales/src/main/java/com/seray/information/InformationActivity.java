package com.seray.information;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.seray.scales.BaseActivity;
import com.seray.scales.R;

import java.util.ArrayList;
import java.util.List;

public class InformationActivity extends BaseActivity {

    private TabLayout mTabLayout;

    private List<Fragment> mFragmentList = null;

    private TabSelectedListener mTabSelectedListener = null;

    private FragmentManager mFragmentManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infor_activity_layout);
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mTabLayout = (TabLayout) findViewById(R.id.info_tabLayout);
    }

    private void initData() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentList = new ArrayList<>();
        mFragmentList.add(new BasicInfoFragment());
        mFragmentList.add(new DeviceInfoFragment());

        TabLayout.Tab tabAt = mTabLayout.getTabAt(0);
        if (tabAt != null)
            tabAt.select();
    }

    private void initListener() {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.add(R.id.info_frameLayout, mFragmentList.get(0));
        transaction.commit();
        mTabSelectedListener = new TabSelectedListener();
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(mTabSelectedListener);
        mFragmentList.clear();
    }

    private class TabSelectedListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            mMisc.beep();
            int position = tab.getPosition();
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            Fragment fragment = mFragmentList.get(position);
            boolean added = fragment.isAdded();
            if (added)
                transaction.show(fragment);
            else
                transaction.add(R.id.info_frameLayout, fragment);
            transaction.commit();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            int position = tab.getPosition();
            Fragment fragment = mFragmentList.get(position);
            boolean hidden = fragment.isHidden();
            if (!hidden) {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.hide(mFragmentList.get(position));
                transaction.commit();
            }
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }
}
