package com.seray.view.presentation;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.seray.scales.R;

/**
 * Author：李程
 * CreateTime：2019/6/1 12:19
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class PayInfoPresentation extends BasePresentation {

    public PayInfoPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_pay_detail);
    }
}
