package com.seray.message;

public class DisplayMessage {

    private boolean isOpen;

    private String message;

    public DisplayMessage(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DisplayMessage{" +
                "isOpen=" + isOpen +
                ", message='" + message + '\'' +
                '}';
    }
}
