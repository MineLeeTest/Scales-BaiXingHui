package com.seray.view.presentation;

import android.view.Display;

import com.seray.scales.App;
import com.seray.sjc.annotation.DisplayType;

/**
 * Author：李程
 * CreateTime：2019/6/1 12:20
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class CustomPresentationFactory {

    public static BasePresentation getPresentation(@DisplayType int type, Display display) {
        BasePresentation presentation;
        if (type == DisplayType.DISPLAY_WEIGHT) {
            presentation = new WeightPresentation(App.getApplication(), display);
        } else if (type == DisplayType.DISPLAY_AD) {
            presentation = new PicturePresentation(App.getApplication(), display);
        }
//        else if (type == DisplayType.DISPLAY_PAY) {
//            presentation = new PayInfoPresentation(App.getApplication(), display);
//        }
        else {
            presentation = new WeightPresentation(App.getApplication(), display);
        }
        return presentation;
    }
}
