package com.lzscale.scalelib.misclib;

import android.util.Log;

public class SerialPort {

    private static final String TAG = SerialPort.class.getSimpleName();

    static {
        System.loadLibrary("Misc");
    }

    private int mFd = -1;

    public SerialPort(final String str) {
        try {
            mFd = openPort(str);
        } catch (Exception e) {
            Log.e(TAG, "SerialPort---openPort()异常..." + e.getMessage());
        }
        if (mFd < 0) {
            Log.e(TAG, "SerialPort: open faile");
        }
    }

    private native int openPort(final String str);

    @Override
    protected void finalize() {
        try {
            close(mFd);
        } catch (Exception e) {// 捕获不到异常消息
            Log.e(TAG, "SerialPort---close()异常..." + e.getMessage());
        }
    }

    private native void close(int fd);

    public void setParameter(int baud, int dataBit, int stopBit, int check) {
        if (mFd > 0)
            try {
                setParameter(mFd, baud, dataBit, stopBit, check);
            } catch (Exception e) {
                Log.e(TAG, "SerialPort---setParameter()异常..." + e.getMessage());
            }
    }

    private native void setParameter(int fd, int baud, int dataBit, int stopBit, int check);

    public int send(final String str) {
        try {
            if (mFd > 0)
                return send(mFd, str);
            else
                return -1;
        } catch (Exception e) {
            Log.e(TAG, "SerialPort---send()异常..." + e.getMessage());
        }
        return -1;
    }

    private native int send(int fd, final String str);

    public int send(final byte[] s) {
        try {
            if (mFd > 0)
                return sendBytes(mFd, s);
            else
                return -1;
        } catch (Exception e) {
            Log.e(TAG, "SerialPort---sendBytes()异常..." + e.getMessage());
        }
        return -1;
    }

    private native int sendBytes(int fd, final byte[] s);

    public byte[] read(int n, int ms) {
        try {
            if (mFd > 0)
                return read(mFd, n, ms);
            else
                return null;
        } catch (Exception e) {
            Log.e(TAG, "SerialPort---read()异常..." + e.getMessage());
        }
        return null;
    }

    private native byte[] read(int fd, int len, int ms);

    public byte[] readAll() {
        try {
            if (mFd > 0)
                return readAll(mFd);
            else
                return null;
        } catch (Exception e) {
            Log.e(TAG, "SerialPort---readAll()异常..." + e.getMessage());
        }
        return null;
    }

    private native byte[] readAll(int fd);

}
