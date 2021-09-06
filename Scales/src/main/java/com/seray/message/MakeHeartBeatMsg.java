package com.seray.message;

import com.seray.instance.ResultData;

import lombok.Data;

/**
 * 心跳数据
 */
@Data
public class MakeHeartBeatMsg {
    private String code;
    private Object msg;
    private String uuid = "0";

    public MakeHeartBeatMsg() {
        super();
    }

    public MakeHeartBeatMsg(ResultData resultData) {
        this.code = resultData.getCode();
        this.msg = resultData.getMsg();
        this.uuid = resultData.getUuid();
    }
}
