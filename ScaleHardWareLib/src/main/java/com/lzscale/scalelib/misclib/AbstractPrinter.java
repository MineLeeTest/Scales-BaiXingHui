package com.lzscale.scalelib.misclib;

public abstract class AbstractPrinter {

    protected static SerialPort mPort;

    static {
        System.loadLibrary("Misc");
    }

    protected AbstractPrinter(String path) {
        mPort = new SerialPort(path);
        mPort.setParameter(9600, 8, 1, 'n');
    }

    public abstract void print(byte[] bs);

    public abstract void print(String txt);

}
