package com.seray.message;


public class DatePickerMsg {

    private String date;

    private int flag;

    private Object obj;

    public DatePickerMsg(String date, int flag, Object obj) {
        this.date = date;
        this.flag = flag;
        this.obj = obj;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    @Override
    public String toString() {
        return "DatePickerMsg{" +
                "date='" + date + '\'' +
                ", flag=" + flag +
                ", obj=" + obj +
                '}';
    }
}
