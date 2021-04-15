package com.seray.sjc.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.seray.cache.CacheHelper;
import com.seray.scales.R;
import com.seray.sjc.entity.device.ScreenResource;
import com.youth.banner.loader.ImageLoader;

/**
 * Author：李程
 * CreateTime：2019/5/7 11:04
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class BannerImageLoader extends ImageLoader {


    public BannerImageLoader() {
    }

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (path instanceof ScreenResource) {
            ScreenResource resource = (ScreenResource) path;
            String resourceType = resource.getResourceType();
            if (resourceType.equals("1")) {
//                String imageServerURL = CacheHelper.ResourceBaseUrl + resource.getResourceUrl();
                String imageServerURL = "";
                Glide.with(context.getApplicationContext())
                        .load(imageServerURL)
                        .asBitmap()
                        .centerCrop()
                        .placeholder(R.drawable.icon_logo)
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .into(imageView);
            } else if (resourceType.equals("2")) {
            }
        }
    }
}
