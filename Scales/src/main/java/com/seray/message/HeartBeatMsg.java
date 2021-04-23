package com.seray.message;

import com.seray.instance.ResultData;

import lombok.Data;

/**
 * 心跳数据
 */
@Data
public class HeartBeatMsg {
    private String code;
    private Object msg;
    private String uuid = "0";

    public HeartBeatMsg() {

    }

    public HeartBeatMsg(ResultData resultData) {
        this.code = resultData.getCode();
        this.msg = resultData.getMsg();
        this.uuid = resultData.getUuid();
    }
}
