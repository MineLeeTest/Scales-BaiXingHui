package com.seray.sjc.entity.message;

/**
 * Author：李程
 * CreateTime：2019/5/11 21:58
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class SyncDataWorkMessage {

    private boolean isSuccess;

    public SyncDataWorkMessage(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
}
