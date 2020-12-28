package com.seray.view.presentation;

import android.app.Presentation;
import android.content.Context;
import android.view.Display;

/**
 * Author：李程
 * CreateTime：2019/6/1 12:18
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public abstract class BasePresentation extends Presentation {

    public BasePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

//    public abstract void initBackDisplay();
//
//    public abstract void showIsStable(boolean isStable);
//
//    public abstract void showIsZero(boolean isZero);
//
//    public abstract void showPrice(String price);
//
//    public abstract void showTare(String tare);
//
//    public abstract void showAmount(String amount);
//
//    public abstract void showWeight(String weight);

}
