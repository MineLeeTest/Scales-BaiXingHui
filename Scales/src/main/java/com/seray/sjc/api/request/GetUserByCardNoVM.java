package com.seray.sjc.api.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;
import java.io.Serializable;

/**
 * Author：李飞龙
 * CreateTime：2021/02/20 12:40
 * E-mail：lifl159357@163.com
 * Describe：世界村接口标准格式
 */
@Data
public class GetUserByCardNoVM implements Serializable {
    @SerializedName("CardNo")
    @Expose
    public String CardNo;

}
