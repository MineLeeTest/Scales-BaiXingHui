package com.seray.inter;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.seray.scales.App;
import com.seray.sjc.annotation.DisplayType;
import com.seray.view.presentation.BasePresentation;
import com.seray.view.presentation.CustomPresentationFactory;

public class DisplayController {

    private static DisplayController controller = null;

    private BasePresentation mLastPresentation = null;

    private static SparseArray<BasePresentation> mPresentations = new SparseArray<>();

    private Display mDisplay;

    private boolean isWeighting = true;

    public boolean isWeighting() {
        return isWeighting;
    }

    public void setWeighting(boolean weighting) {
        isWeighting = weighting;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private DisplayController() {
        DisplayManager manager = (DisplayManager) App.getApplication().getSystemService(Context.DISPLAY_SERVICE);
        String cat = DisplayManager.DISPLAY_CATEGORY_PRESENTATION;
        Display[] displays = manager.getDisplays(cat);
        this.mDisplay = displays.length > 0 ? displays[0] : null;
    }

    public static DisplayController getInstance() {
        if (controller == null) {
            synchronized (DisplayController.class) {
                if (controller == null) {
                    controller = new DisplayController();
                }
            }
        }
        return controller;
    }

    public Display getDisplay() {
        return mDisplay;
    }

    public void showPresentation(int type) {
        if (mDisplay == null) {
            mLastPresentation = null;
            return;
        }
        BasePresentation presentation = createPresentation(type);
        presentation.show();
        if (mLastPresentation != null && mLastPresentation.equals(presentation)) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mLastPresentation != null) {
                    mLastPresentation.dismiss();
                }
                mLastPresentation = presentation;
            }
        }, 200);
    }

    public void syncDisplayShow() {
        if (mLastPresentation == null)
            return;
        if (mLastPresentation.isShowing()) {
            mLastPresentation.dismiss();
        }
    }

    public BasePresentation getDisplayPresentation(@DisplayType int type) {
        return mPresentations.get(type);
    }

    public boolean isShowing(int type) {
        BasePresentation presentation = getDisplayPresentation(type);
        return presentation != null && presentation.isShowing();
    }

    public void cleanPresentations() {
        for (int index = 0; index < mPresentations.size(); index++) {
            BasePresentation presentation = mPresentations.get(index);
            if (presentation != null && presentation.isShowing()) {
                presentation.dismiss();
            }
        }
        mPresentations.clear();
    }

    private BasePresentation createPresentation(@DisplayType int type) {
        BasePresentation presentation = mPresentations.get(type);
        if (presentation != null) {
            return presentation;
        }
        presentation = CustomPresentationFactory.getPresentation(type, mDisplay);
        Window window = presentation.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        mPresentations.put(type, presentation);
        return presentation;
    }
}
