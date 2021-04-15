package com.skyworth.splicing;


import java.io.File;
import java.io.InputStream;

public class ICCardSerialPortUtil {
    private SerialPort mSerialPort;
    private InputStream mInputStream;

    private ReadThread readThread = null;
    private boolean isRun = false;

    public ICCardSerialPortUtil() {
        try {
            mSerialPort = new SerialPort(new File("/dev/ttymxc2"), 9600);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startRead(ICCardSerialPortUtil.OnDataReceiveListener onDataReceiveListener) {
        mInputStream = mSerialPort.getInputStream();
        readThread = new ReadThread(onDataReceiveListener);
        isRun = true;
        readThread.start();

    }

    public void endRead() {
        mInputStream = null;
        isRun = false;
        readThread = null;

    }


    // 数据接收枚举
    public enum DataReceType {
        success, // 成功,可以正常读取数据
        faild, // 失败，不能读取数据
        timeout// 超时，不能读取数据
    }

    public interface OnDataReceiveListener {
        void onICCardDataReceive(DataReceType type, byte[] buffer, int size);
    }


    private class ReadThread extends Thread {
        ICCardSerialPortUtil.OnDataReceiveListener onDataReceiveListener;

        private ReadThread(ICCardSerialPortUtil.OnDataReceiveListener onDataReceiveListener) {
            this.onDataReceiveListener = onDataReceiveListener;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("----readBuffer()---now------------>");
            while (isRun) {
                ICCardSerialPortUtil.this.readBuffer(onDataReceiveListener);
            }
        }
    }

    private void readBuffer(OnDataReceiveListener onDataReceiveListener) {
        int size = 0, count;
        byte[] buffer = new byte[1024];
        try {
            do {
                Thread.sleep(30);
                if (mInputStream == null)
                    return;
                count = mInputStream.read(buffer, size, buffer.length - size);
                size += count;
                if (size == buffer.length) {
                    byte[] temp = new byte[size * 2];
                    System.arraycopy(buffer, 0, temp, 0, size);
                    buffer = temp;
                }
            } while (count > 0);
            if (size > 0) {
                byte[] a = new byte[size];
                System.arraycopy(buffer, 0, a, 0, size);
                onDataReceiveListener.onICCardDataReceive(DataReceType.success, a, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onDataReceiveListener.onICCardDataReceive(DataReceType.faild, buffer, size);
        }
    }
}
