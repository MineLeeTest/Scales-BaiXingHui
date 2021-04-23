package com.seray.message;

public class QuantifyMessage {

    private boolean isOpenJin;

    private String msg;

    public QuantifyMessage(boolean isOpenJin) {
        this.isOpenJin = isOpenJin;
    }

    public boolean isOpenJin() {
        return isOpenJin;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "QuantifyMessage{" +
                "isOpenJin=" + isOpenJin +
                ", msgs='" + msg + '\'' +
                '}';
    }
}
