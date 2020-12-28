package com.seray.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seray.scales.R;

public class CustomView extends LinearLayout {

    public CustomView(Context context) {
        super(context, null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 导入布局
        View view = LayoutInflater.from(context).inflate(R.layout.manage_item_layout, this);
        ImageView imageView = (ImageView) view.findViewById(R.id.customerView_iv);
        TextView textView = (TextView) view.findViewById(R.id.customerView_tv);

        // 获取属性值
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.CustomView);
        int tv_back_color;
        String tv_title;
        int iv_back_color;
        int iv_icon;
        try {
            iv_back_color = array.getColor(R.styleable.CustomView_image_back_color, Color.BLACK);
            iv_icon = array.getResourceId(R.styleable.CustomView_image_src, R.drawable.ic_launcher);
            tv_title = array.getString(R.styleable.CustomView_text_title);
            tv_back_color = array.getColor(R.styleable.CustomView_text_back_color, Color.BLACK);
        } finally {
            array.recycle();
        }

        // 设置属性值
        imageView.setImageResource(iv_icon);
        imageView.setBackgroundColor(iv_back_color);
        textView.setText(tv_title);
        textView.setBackgroundColor(tv_back_color);
    }

    // 拦截子View的触摸监听
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
