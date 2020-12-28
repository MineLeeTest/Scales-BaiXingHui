package com.seray.sjc.api.result;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.seray.sjc.entity.device.PayTypeInfo;
import com.seray.sjc.entity.device.ScreenResource;
import com.seray.sjc.entity.product.SjcCategory;
import com.seray.sjc.entity.device.SjcParamInfo;
import com.seray.sjc.entity.product.SjcProduct;
import com.seray.sjc.entity.device.TermInfo;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:12
 * E-mail：licheng@kedacom.com
 * Describe：世界村获取业务数据结果
 */
public class BusinessRsp  {

    // 终端 id
    @SerializedName("termId")
    @Expose
    public String termId;

    // 商品分类状态 1：未通讯；3：已完成
    @SerializedName("goodsCategory")
    @Expose
    public String goodsCategory;

    // 商品分类
    @SerializedName("goodsCategoryList")
    @Expose
    public List<SjcCategory> goodsCategoryList;

    // 商品信息状态 1：未通讯；3：已完成
    @SerializedName("goodsInfo")
    @Expose
    public String goodsInfo;

    // 商品信息
    @SerializedName("goodsInfoList")
    @Expose
    public List<SjcProduct> goodsInfoList;

    // 参数信息状态 1：未通讯；3：已完成
    @SerializedName("paramInfo")
    @Expose
    public String paramInfo;

    // 参数信息
    @SerializedName("paramInfoList")
    @Expose
    public List<SjcParamInfo> paramInfoList;

    // 客显屏图片状态 1：未通讯；3：已完成
    @SerializedName("screenResource")
    @Expose
    public String screenResource;

    // 客显屏图片
    @SerializedName("screenResourceList")
    @Expose
    public List<ScreenResource> screenResourceList;

    // 终端信息状态 1：未通讯；3：已完成
    @SerializedName("termInfo")
    @Expose
    public String termInfo;

    // 终端信息内容
    @SerializedName("termInfoObj")
    @Expose
    public TermInfo termInfoObj;


    //支付方式通讯状态 1：未通讯；3：已完成
    @SerializedName("payTypeInfo")
    @Expose
    public String payTypeInfo;

    //支付方式
    @SerializedName("payTypeList")
    @Expose
    public List<PayTypeInfo> payTypeList;

    @Override
    public String toString() {
        return "BusinessRsp{" +
                "termId='" + termId + '\'' +
                ", goodsCategory='" + goodsCategory + '\'' +
                ", goodsCategoryList=" + goodsCategoryList +
                ", goodsInfo='" + goodsInfo + '\'' +
                ", goodsInfoList=" + goodsInfoList +
                ", paramInfo='" + paramInfo + '\'' +
                ", paramInfoList=" + paramInfoList +
                ", screenResource='" + screenResource + '\'' +
                ", screenResourceList=" + screenResourceList +
                ", termInfo='" + termInfo + '\'' +
                ", termInfoObj=" + termInfoObj +
                ", payTypeInfo='" + payTypeInfo + '\'' +
                ", payTypeList=" + payTypeList +
                '}';
    }
}
