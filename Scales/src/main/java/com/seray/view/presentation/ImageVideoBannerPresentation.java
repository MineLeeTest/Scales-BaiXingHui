package com.seray.view.presentation;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;

import com.seray.scales.R;
import com.seray.view.banner.ImageVideoBanner;
import com.seray.view.banner.bean.BannerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/6/9 15:04
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class ImageVideoBannerPresentation extends BasePresentation {

    private List<BannerBean> list = new ArrayList<>();

    public ImageVideoBannerPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_image_video);
        ImageVideoBanner banner = findViewById(R.id.banner);
        setDatas();
        banner.replaceData(list);
        banner.startBanner();
    }

    private void setDatas() {
        for (int i = 0; i < 4; i++) {
            BannerBean listBean = new BannerBean();
            if (i == 1) {
                listBean.setUrl("http://www.w3school.com.cn/example/html5/mov_bbb.mp4");
                listBean.setType(1);//图片类型 视频
                list.add(listBean);
            } else if (i == 2) {
                listBean.setUrl("http://pic11.nipic.com/20101201/4452735_182232064453_2.jpg");
                listBean.setType(0);//图片类型 视频
                list.add(listBean);
            } else if (i == 3) {
                listBean.setUrl("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                listBean.setType(1);//图片类型 视频
                list.add(listBean);
            } else {
                listBean.setUrl("http://img.zcool.cn/community/01635d571ed29832f875a3994c7836.png@900w_1l_2o_100sh.jpg");
                listBean.setType(0);//图片类型 图片
                list.add(listBean);
            }
        }
    }
}
