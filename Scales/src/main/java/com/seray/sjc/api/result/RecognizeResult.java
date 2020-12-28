package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Author：李程
 * CreateTime：2019/5/11 10:00
 * E-mail：licheng@kedacom.com
 * Describe：世界村果蔬识别结果
 */
public class RecognizeResult implements Serializable {

    // 图像中的食材名称
    @Expose
    @SerializedName("name")
    public String name;

    // 得分 0-1
    @Expose
    @SerializedName("score")
    public double score;

    public RecognizeResult() {
    }

    public RecognizeResult(String name, double score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public String toString() {
        return "RecognizeResult{" +
                "name='" + name + '\'' +
                ", score=" + score +
                '}';
    }
}
