package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Author：李程
 * CreateTime：2019/5/11 09:59
 * E-mail：licheng@kedacom.com
 * Describe：世界村果蔬识别请求参数
 */
public class ImageRecognizeReq {

    @Expose
    @SerializedName("image")
    public String image;

    public ImageRecognizeReq(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ImageRecognizeReq{" +
                "image='" + image + '\'' +
                '}';
    }
}
