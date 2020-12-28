package com.seray.sjc.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

class PopupController {

    View mPopupView;//弹窗布局View
    private int layoutResId;//布局id
    private Context context;
    private PopupWindow popupWindow;
    private View mView;
    private Window mWindow;

    PopupController(Context context, PopupWindow popupWindow) {
        this.context = context;
        this.popupWindow = popupWindow;
    }

    public void setView(int layoutResId) {
        mView = null;
        this.layoutResId = layoutResId;
        installContent();
    }

    private void installContent() {
        if (layoutResId != 0) {
            mPopupView = LayoutInflater.from(context).inflate(layoutResId, null);
        } else if (mView != null) {
            mPopupView = mView;
        }
        popupWindow.setContentView(mPopupView);
    }

    public void setView(View view) {
        mView = view;
        this.layoutResId = 0;
        installContent();
    }

    /**
     * 设置宽度
     *
     * @param width  宽
     * @param height 高
     */
    private void setWidthAndHeight(int width, int height) {
        if (width == 0 || height == 0) {
            //如果没设置宽高，默认是WRAP_CONTENT
            popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            popupWindow.setWidth(width);
            popupWindow.setHeight(height);
        }
    }


    /**
     * 设置背景灰色程度
     *
     * @param level 0.0f-1.0f
     */
    void setBackGroundLevel(float level) {
        mWindow = ((Activity) context).getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.alpha = level;
        mWindow.setAttributes(params);
    }


    /**
     * 设置动画
     */
    private void setAnimationStyle(int animationStyle) {
        popupWindow.setAnimationStyle(animationStyle);
    }

    /**
     * 设置Outside是否可点击
     *
     * @param touchable 是否可点击
     */
    private void setOutsideTouchable(boolean touchable) {
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(touchable);
        popupWindow.setFocusable(touchable);
    }

    /**
     * 设置是否拦截返回键
     *
     * @param isIntercept 是否拦截返回键
     */
    private void setInterceptBackEvent(boolean isIntercept) {
        if (isIntercept) {
            mPopupView.setFocusable(true);
            mPopupView.setFocusableInTouchMode(true);
            mPopupView.setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
        }
    }

    static class PopupParams {
        int layoutResId;//布局id
        Context mContext;
        int mWidth, mHeight;//弹窗的宽和高
        boolean isShowBg, isShowAnim;
        float bg_level;//屏幕背景灰色程度
        int animationStyle;//动画Id
        View mView;
        boolean isTouchable = true;
        boolean isInterceptBackEvent = false;

        PopupParams(Context mContext) {
            this.mContext = mContext;
        }

        void apply(PopupController controller) {
            if (mView != null) {
                controller.setView(mView);
            } else if (layoutResId != 0) {
                controller.setView(layoutResId);
            } else {
                throw new IllegalArgumentException("PopupView's contentView is null");
            }
            controller.setWidthAndHeight(mWidth, mHeight);
            controller.setOutsideTouchable(isTouchable);
            if (isShowBg) {
                controller.setBackGroundLevel(bg_level);
            }
            if (isShowAnim) {
                controller.setAnimationStyle(animationStyle);
            }
            controller.setInterceptBackEvent(isInterceptBackEvent);
        }
    }
}
