package com.seray.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioGroup;

import com.seray.cache.CacheHelper;
import com.seray.scales.BaseActivity;
import com.seray.scales.R;
import com.seray.sjc.AppExecutors;

import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends BaseActivity {

    private static final int TO_DATA = 0;
    private static final int TO_PRINT = 1;
    private static final int TO_PRODUCT = 2;
    private static final int TO_SUNDRY = 3;

    private RadioGroup mGroup;
    private List<BaseConfigFragment> mFragmentList = null;
    private FragmentManager mFragmentManager = null;
    private BaseConfigFragment mLastSelectedFragment = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_data_fragment);
        initView();
        initData();
        mGroup.check(R.id.config_to_recognize);
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        mLastSelectedFragment = mFragmentList.get(TO_DATA);
        boolean added = mLastSelectedFragment.isAdded();
        if (added)
            transaction.show(mLastSelectedFragment);
        else
            transaction.add(R.id.config_frameLayout, mLastSelectedFragment);
        transaction.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                super.onClick(null);
                AppExecutors.getInstance().queryIO().submit(new Runnable() {
                    @Override
                    public void run() {
                        CacheHelper.prepareConfig();
                        finish();
                    }
                });
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (mLastSelectedFragment != null) {
            boolean hide = mLastSelectedFragment.isHidden();
            if (!hide) {
                FragmentTransaction transaction = mFragmentManager.beginTransaction();
                transaction.hide(mLastSelectedFragment);
                transaction.commit();
            }
        }
        int id = v.getId();
        switch (id) {
            case R.id.config_to_recognize:
                mLastSelectedFragment = mFragmentList.get(TO_DATA);
                break;
            case R.id.config_to_print:
                mLastSelectedFragment = mFragmentList.get(TO_PRINT);
                break;
            case R.id.config_to_product:
                mLastSelectedFragment = mFragmentList.get(TO_PRODUCT);
                break;
            case R.id.config_to_sundry:
                mLastSelectedFragment = mFragmentList.get(TO_SUNDRY);
                break;
        }
        if (mLastSelectedFragment == null) {
            if (mFragmentList != null) {
                mFragmentList.clear();
                mFragmentList = null;
            }
            initData();
        }
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        boolean added = mLastSelectedFragment.isAdded();
        if (added)
            transaction.show(mLastSelectedFragment);
        else
            transaction.add(R.id.config_frameLayout, mLastSelectedFragment);
        transaction.commit();
    }

    private void initData() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentList = new ArrayList<>(5);
        mFragmentList.add(TO_DATA, new SaveOrderConfigFragment());
        mFragmentList.add(TO_PRINT, new PrintOrderConfigFragment());
        mFragmentList.add(TO_PRODUCT, new ProductShowConfigFragment());
        mFragmentList.add(TO_SUNDRY, new SundryConfigFragment());
    }

    private void initView() {
        mGroup = (RadioGroup) findViewById(R.id.config_group);
        mGroup.findViewById(R.id.config_to_recognize).setOnClickListener(this);
        mGroup.findViewById(R.id.config_to_print).setOnClickListener(this);
        mGroup.findViewById(R.id.config_to_product).setOnClickListener(this);
        mGroup.findViewById(R.id.config_to_sundry).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFragmentList != null)
            mFragmentList.clear();
        mFragmentList = null;
    }
}
