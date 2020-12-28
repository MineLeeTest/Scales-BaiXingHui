package com.seray.sjc.annotation;

import android.support.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author：李程
 * CreateTime：2019/5/8 13:57
 * E-mail：licheng@kedacom.com
 * Describe：订单上传状态
 */
@Documented
@IntDef({
        UploadStatus.NO,
        UploadStatus.SUCCESS
})
@Retention(RetentionPolicy.SOURCE)
public @interface UploadStatus {

    /**
     * 未上传（失败）
     */
    int NO = 0;

    /**
     * 已上传（成功）
     */
    int SUCCESS = 1;

}
