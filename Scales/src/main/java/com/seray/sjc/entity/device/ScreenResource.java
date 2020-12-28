package com.seray.sjc.entity.device;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

/**
 * Author：李程
 * CreateTime：2019/4/13 13:07
 * E-mail：licheng@kedacom.com
 * Describe：世界村后显示多媒体数据
 */
@Entity(tableName = ScreenResource.TABLE_NAME)
public class ScreenResource {

    public static final String TABLE_NAME = "ScreenResource";

    // 资源ID
    @PrimaryKey
    @NonNull
    @SerializedName("resourceId")
    @Expose
    private String resourceId = UUID.randomUUID().toString();

    // 开始时间
    @SerializedName("startDate")
    @Expose
    private long startDate;

    // 结束时间
    @SerializedName("endDate")
    @Expose
    private long endDate;

    // 资源类型
    @SerializedName("resourceType")
    @Expose
    private String resourceType;

    // 资源地址
    @SerializedName("resourceUrl")
    @Expose
    private String resourceUrl;

    public ScreenResource() {
    }

    @NonNull
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(@NonNull String resourceId) {
        this.resourceId = resourceId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    @Override
    public String toString() {
        return "ScreenResource{" +
                "resourceId='" + resourceId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", resourceType='" + resourceType + '\'' +
                ", resourceUrl='" + resourceUrl + '\'' +
                '}';
    }
}
