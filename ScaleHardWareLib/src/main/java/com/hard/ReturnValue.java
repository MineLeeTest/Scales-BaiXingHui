package com.hard;

public class ReturnValue {

    private boolean isSuccess;

    private String code;

    private Object tag;

    public ReturnValue(boolean isSuccess, String code, Object tag) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.tag = tag;
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", this.isSuccess, this.code, this.tag);
    }
}
