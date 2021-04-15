package com.seray.view.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seray.cache.CacheHelper;
import com.seray.scales.R;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Author：李程
 * CreateTime：2019/6/1 12:18
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class WeightPresentation extends BasePresentation {

    private TextView mProNameView, mWeightView, mPriceView, mAmountView, mSubtotalView,
            mSubAmountView, mCusNameView, mStockPlaceView,
            mPriceUnitView, mWeightUnitView;
    private ImageView mStableView, mQRView;

    public WeightPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configContentView();
        initView();
    }

    private void configContentView() {
        setContentView(R.layout.sale_back_pre3);
    }

    public void setQRContent() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(FileHelp.PAY_CODE_PIC_DIR + "payCode.jpg"));
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            mQRView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                LogUtil.e(e.getMessage());
            }
        }
    }

    public void setPlaceContent(String place) {
        if (mStockPlaceView != null) {
            place = TextUtils.isEmpty(place) ? "无" : place;
            mStockPlaceView.setText(place);
        }
    }

    public void setWeightContent(String w) {
        if (TextUtils.isEmpty(w)) {
            return;
        }
        if (mWeightView != null) {
            mWeightView.setText(w);
        }
    }

    public void setPriceContent(String p) {
        if (TextUtils.isEmpty(p)) {
            return;
        }
        if (mPriceView != null) {
            mPriceView.setText(p);
        }
    }

    public void setAmountContent(String a) {
        if (TextUtils.isEmpty(a)) {
            return;
        }
        if (mAmountView != null) {
            mAmountView.setText(a);
        }
    }

    public void setSubtotalContent(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        mSubtotalView.setText(s);
    }

    public void setProductName(String p) {
        if (TextUtils.isEmpty(p)) {
            mProNameView.setText(R.string.scale_product_title);
        } else {
            mProNameView.setText(p);
        }
    }

    public void setSubtotalAmount(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        mSubAmountView.setText(s);
    }

    public void setCustomerName(String c) {
        if (TextUtils.isEmpty(c)) {
            return;
        }
        mCusNameView.setText(c);
    }

    public void setWeightType(int type) {
        if (type == 0) {
            mWeightUnitView.setText(R.string.display_weight_unit_kg);
            mPriceUnitView.setText(R.string.display_price_unit_kg);
        } else if (type == 1) {
            mWeightUnitView.setText(R.string.display_weight_unit_piece);
            mPriceUnitView.setText(R.string.display_price_unit_piece);
        } else if (type == 2) {
            mWeightUnitView.setText(R.string.display_weight_unit_jin);
            mPriceUnitView.setText(R.string.display_price_unit_jin);
        } else {
            mWeightUnitView.setText(R.string.display_weight_unit_kg);
            mPriceUnitView.setText(R.string.display_price_unit_kg);
        }
    }

    public void setStableView(boolean isStable) {
        if (isStable) {
            mStableView.setVisibility(View.VISIBLE);
        } else {
            mStableView.setVisibility(View.INVISIBLE);
        }
    }

    private void initView() {
        mProNameView = (TextView) findViewById(R.id.tv_productName);
        mWeightView = (TextView) findViewById(R.id.tv_weight);
        mWeightUnitView = (TextView) findViewById(R.id.back_weight_unit);
        mPriceView = (TextView) findViewById(R.id.tv_price);
        mAmountView = (TextView) findViewById(R.id.tv_amount);
        mCusNameView = (TextView) findViewById(R.id.tv_customerName);
        mSubtotalView = (TextView) findViewById(R.id.tv_subtotal);
        mSubAmountView = (TextView) findViewById(R.id.tv_subtotalAmount);
        mStableView = (ImageView) findViewById(R.id.iv_stable);
        mPriceUnitView = (TextView) findViewById(R.id.back_price_unit);
        mStockPlaceView = (TextView) findViewById(R.id.tv_place);
        mQRView = (ImageView) findViewById(R.id.iv_qr);
    }
}
