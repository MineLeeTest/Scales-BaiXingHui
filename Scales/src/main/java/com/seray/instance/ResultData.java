package com.seray.instance;

import java.io.Serializable;

public class ResultData implements Serializable {
    private String code;
    private Object msg;
    private String uuid = "0";

    @Override
    public String toString() {
        return "ResultObj{" +
                "code='" + code + '\'' +
                ", msgs='" + msg + '\'' +
                ", uuid='" + uuid + '\'' +
                '}';
    }

    public ResultData() {

    }

    public ResultData(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResultData(String code, String msg, String uuid) {
        this.code = code;
        this.msg = msg;
        this.uuid = uuid;
    }

    public ResultData(String code, Object msg, String uuid) {
        this.code = code;
        this.msg = msg;
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setRetMsg(String code, Object msg) {
        this.code = code;
        this.msg = msg;
    }

    public void setTrueMsg(Object msg) {
        this.code = "9000";
        this.msg = msg;
    }

    public boolean isSuccess() {
        if ("9000".equals(this.code)) {
            return true;
        }
        return false;
    }

    public ResultData setRetMsg(ResultData resultObj, String code, Object msg) {
        resultObj.setCode(code);
        resultObj.setMsg(msg);
        return resultObj;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
