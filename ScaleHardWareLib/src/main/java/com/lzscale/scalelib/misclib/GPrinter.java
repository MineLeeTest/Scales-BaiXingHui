package com.lzscale.scalelib.misclib;

import android.util.Log;

public class GPrinter extends AbstractPrinter {

    private static final String DEBUG = GPrinter.class.getSimpleName();

    /**
     * 注意修改端口号
     * 表头和小称的端口号可能不同
     * 不同版本硬件端口号可能不同 未做验证
     */
    public GPrinter() {
        super("dev/ttymxc3");
    }

    @Override
    protected void finalize() {
        if (mPort != null) {
            try {
                mPort.finalize();
            } catch (Exception e) {
                Log.e(DEBUG, e.getMessage());
            }
        }
    }

    @Override
    public void print(byte[] bs) {
        try {
            mPort.send(bs);
        } catch (Exception e) {
            Log.e(DEBUG, e.getMessage());
        }
    }

    @Deprecated
    @Override
    public void print(String txt) {
        try {
            this.print(txt.getBytes());
        } catch (Exception e) {
            Log.e(DEBUG, e.getMessage());
        }
    }
}
