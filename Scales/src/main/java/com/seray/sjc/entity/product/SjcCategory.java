package com.seray.sjc.entity.product;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Author：李程
 * CreateTime：2019/4/13 12:52
 * E-mail：licheng@kedacom.com
 * Describe：世界村商品分类
 */
@Entity(tableName = SjcCategory.TABLE_NAME)
public class SjcCategory {

    public static final String TABLE_NAME = "Category";

    public static final String CATEGORY_ID = "categoryId";

    // 分类ID
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = SjcCategory.CATEGORY_ID)
    @SerializedName("categoryId")
    @Expose
    private String categoryId = UUID.randomUUID().toString();

    // 分类名称
    @SerializedName("categoryName")
    @Expose
    private String categoryName;

    // 分类级别
    @SerializedName("categoryLevel")
    @Expose
    private String categoryLevel;

    // 分类排序
    @SerializedName("categorySort")
    @Expose
    private int categorySort;

    // 图片地址
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;

    // 父级别ID
    @SerializedName("parentId")
    @Expose
    private String parentId;

    public SjcCategory() {
    }

    @NonNull
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryLevel() {
        return categoryLevel;
    }

    public void setCategoryLevel(String categoryLevel) {
        this.categoryLevel = categoryLevel;
    }

    public int getCategorySort() {
        return categorySort;
    }

    public void setCategorySort(int categorySort) {
        this.categorySort = categorySort;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    @Override
    public String toString() {
        return "SjcCategory{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", categoryLevel='" + categoryLevel + '\'' +
                ", categorySort=" + categorySort +
                ", imageUrl='" + imageUrl + '\'' +
                ", parentId='" + parentId + '\'' +
                '}';
    }
}
