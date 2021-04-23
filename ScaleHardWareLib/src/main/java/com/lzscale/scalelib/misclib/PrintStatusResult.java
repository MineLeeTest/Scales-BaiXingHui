package com.lzscale.scalelib.misclib;

public class PrintStatusResult {

    public int code;

    public String msg;

    public PrintStatusResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "PrintStatusResult{" +
                "code=" + code +
                ", msgs='" + msg + '\'' +
                '}';
    }
}
