package com.hard;

import com.lzscale.scalelib.misclib.Misc;
import com.skyworth.splicing.SerialPortUtil;

public class CardReadHelp {

    private static boolean _isLocked = false;
    private ReturnValueCallback returnValueCallback;
    private int timeout;
    private SerialPortUtil mSerialPortUtil;
    private Misc mMisc = Misc.newInstance();

    public CardReadHelp() {
        this.timeout = 48 * 1000;
    }

    public void readCardId(ReturnValueCallback callback) {
        this.setAutoReadBlockData(callback, new ReturnValueCallback() {

            @Override
            public void run(ReturnValue rv) {

                if (rv.getIsSuccess()) {

                    String remark = "读卡";

                    readData(remark, new ReadDataListener() {

                        @Override
                        public void readDataCallback(ReturnValue result) {

                            if (result.getIsSuccess()) {//读取成功

                                mMisc.beepEx(100, 50, 3);

                                Object tag = result.getTag();//卡号

                                String userCardNum = (String) tag;

                                ReturnValue rv = new ReturnValue(true, String.valueOf(1), userCardNum);
                                callReturnValueCallback(rv);

                            } else {
                                mMisc.beepWarn();
                                callReturnValueCallback(result);
                            }
                        }
                    });
                } else {
                    callReturnValueCallback(rv);
                }
            }
        });
    }

    public void cancelReadCard(ReturnValueCallback callback) {
        if (this.mSerialPortUtil == null) {
            if (callback != null)
                callback.run(new ReturnValue(false, "-1", "没有发起读卡请求，不需要取消"));
            return;
        }
        this.returnValueCallback = callback;
        this.callReturnValueCallback(new ReturnValue(false, "-1", "已取消读卡"));
    }

    // 初始化串口, 如果已经打开则先关闭
    private String initSerialPortUtil() {
        try {
            this.closeSerialPort();
            this.mSerialPortUtil = new SerialPortUtil("/dev/ttymxc2", 9600);
            return null;
        } catch (Exception e) {
            return "初始化串口失败:" + e.getMessage();
        }
    }

    private void setAutoReadBlockData(ReturnValueCallback finalCallback, final ReturnValueCallback callback) {

        hand(finalCallback, new Runnable() {

            @Override
            public void run() {

                final String remark = "设置自动读卡模式";

                if (!sendData(getSetAutoReadBlockData(), remark))
                    return;

                readData(remark, new ReadDataListener() {

                    @Override
                    public void readDataCallback(ReturnValue result) {
                        callback.run(result);
                    }
                });

            }
        });
    }

    private void closeSerialPort() {
        if (mSerialPortUtil != null) {
            mSerialPortUtil.closeSerialPort();
            mSerialPortUtil = null;
        }
    }

    private void hand(ReturnValueCallback callback, final Runnable run) {
        if (!this.isCanLaunchRequest(callback))
            return;
        run.run();
    }

    private boolean sendData(byte[] data, String explain) {
        return this.sendData(data, explain, true);
    }

    // 发送数据
    private boolean sendData(byte[] data, String explain, boolean autoCallReturn) {
        if (this.mSerialPortUtil.sendBuffer(data))
            return true;
        if (autoCallReturn) {
            ReturnValue rv = new ReturnValue(false, "-2", "发送" + explain + "数据失败");
            this.callReturnValueCallback(rv);
        }
        return false;
    }

    private void readData(String explain, ReadDataListener listener) {
        this.readData(this.timeout, explain, listener);
    }

    private void readData(int tTimeout, final String explain, final ReadDataListener listener) {

        this.mSerialPortUtil.readBuffer(tTimeout, new SerialPortUtil.OnDataReceiveListener() {

            @Override
            public void onDataReceive(SerialPortUtil.DataReceType type, byte[] buffer, int size) {

                ReturnValue result;
                String faildStr = "";
                boolean flag = false;
                if (SerialPortUtil.DataReceType.timeout == type) {
                    faildStr = "超时";
                } else if (SerialPortUtil.DataReceType.success != type) {
                    faildStr = "失败";
                } else {
                    flag = true;
                }
                if (!flag) {
                    //105 原 等于 -2
                    result = new ReturnValue(false, "105", "读取" + explain + "响应数据" + faildStr);
                    listener.readDataCallback(result);
                    return;
                }

                String hex = getHexStr(buffer, size);

                if (isOk(buffer, size)) {

                    byte[] cont = getContent(buffer, size);
                    String strRes = getHexStr(cont, 0, cont.length).replace(" ", "");

                    result = new ReturnValue(true, "0", strRes);

                } else {
                    result = new ReturnValue(false, "-1", "校验" + explain + "应答数据有误:" + hex);
                }

                listener.readDataCallback(result);
            }
        });
    }

    private boolean isCanLaunchRequest(ReturnValueCallback callback) {
        if (this.getIsLock()) {
            if (callback != null) {
                callback.run(new ReturnValue(false, "99", "请等待上次通讯请求完成"));
            }
            return false;
        }
        this.setLock();
        this.returnValueCallback = callback;
        String msg = this.initSerialPortUtil();
        if (msg == null)
            return true;
        this.callReturnValueCallback(new ReturnValue(false, "-3", msg));
        return true;
    }

    private boolean getIsLock() {
        return _isLocked;
    }

    private void setLock() {
        _isLocked = true;
    }

    private void callReturnValueCallback(ReturnValue result) {
        _isLocked = false;
        if (returnValueCallback != null) {
            returnValueCallback.run(result);
        }
    }

    private String getHexStr(byte[] buffer, int size) {
        return this.getHexStr(buffer, 0, size, " ");
    }

    private String getHexStr(byte[] buffer, int offset, int len, String split) {
        StringBuilder str = new StringBuilder();
        int end = offset + len;
        for (int i = offset; i < end; i++) {
            String hex = Integer.toHexString(buffer[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            str.append(hex).append(split);
        }
        return str.toString().toUpperCase();
    }

    private String getHexStr(byte[] buffer, int offset, int len) {
        return this.getHexStr(buffer, offset, len, "");
    }

    private byte[] getContent(byte[] data, int size) {
        byte[] cont = new byte[size - 6];
        if (cont.length > 0) {
            System.arraycopy(data, 4, cont, 0, cont.length);
        }
        return cont;
    }

    private byte getXORBack(byte[] data, int index, int end) {
        byte res = this.getXOR(data, index, end);
        return (byte) (~((int) res));
    }

    private byte getXOR(byte[] data, int index, int end) {
        byte t = 0;
        for (int i = index; i < end; i++)
            t ^= data[i];
        return t;
    }

    private byte[] getSetAutoReadBlockData() {
        int offset = 0;
        byte[] data = new byte[255];
        data[offset++] = 0x00;
        data[offset++] = 0x26;
        data[offset++] = 0x04;
        data[offset++] = 0x00;
        data[offset++] = 0x00;
        data[offset++] = 0x19;
        data[offset++] = 0x01;
        return this.getSendData(data, offset);
    }

    private byte[] getSendData(byte[] data, int offset) {
        int index = 0;
        byte[] sendData = new byte[offset + 3];
        sendData[index++] = 0x20;
        System.arraycopy(data, 0, sendData, index, offset);
        index += offset;
        sendData[index++] = this.getXORBack(sendData, 1, index);
        sendData[index] = 0x03;
        return sendData;
    }

    private boolean isOk(byte[] oData, int size) {
        if (size < 6)
            return false;
        byte[] data;
        if (oData[0] == 0x06) {
            data = new byte[size - 1];
            System.arraycopy(oData, 1, data, 0, data.length);
        } else {
            data = oData;
        }
        return data[0] == 0x20 && data[size - 1] == 0x03 && data[size - 2] == this.getXORBack(data, 1, size - 2);
    }

    @Override
    protected void finalize() throws Throwable {
        _isLocked = false;
        closeSerialPort();
        super.finalize();
    }

    interface ReadDataListener {
        void readDataCallback(ReturnValue result);
    }
}
