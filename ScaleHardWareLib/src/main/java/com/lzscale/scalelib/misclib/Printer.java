package com.lzscale.scalelib.misclib;

import android.graphics.Bitmap;
import android.util.Log;


public class Printer {

    private static SerialPort mPort = new SerialPort("/dev/ttymxc1");

    static {
        System.loadLibrary("Misc");
    }

    public Printer() {
        mPort.setParameter(9600, 8, 1, 'n');
    }

    public void printASCIIString(final String str) {
        mPort.send(str);
    }

    public void PrintGBKString(final String str) {
        try {
            byte[] bs = str.getBytes("GBK");
            mPort.send(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void expand(int ex, int ey) // 字体放大，ex横向放大倍数， ey 纵向放大倍数，ex,ey
    // 范围是1-2，目前只支持放大到2倍
    {
        byte[] b = {0x1d, 0x21, 0x00};
        if (ex > 1 && ex <= 8)
            b[2] |= ((ex - 1) << 4);
        if (ey > 1 && ey <= 8)
            b[2] |= (ex - 1);
        mPort.send(b);
    }

    public void inverse(boolean b) // 反白打印
    {
        byte[] ba = {0x1d, 0x42, 0x00};
        if (b)
            ba[2] = 1;
        mPort.send(ba);
    }

    private int RGB2Gray(int r, int g, int b) {
        return (int) (0.29900 * r + 0.58700 * g + 0.11400 * b);
    }

    private byte px2Byte(int pixel) {
        byte b = 0;
        int red = (pixel & 0x00ff0000) >> 16; // 取高两位
        int green = (pixel & 0x0000ff00) >> 8; // 取中两位
        int blue = pixel & 0x000000ff; // 取低两位
        int gray = RGB2Gray(red, green, blue);
        if (gray < 128) {
            b = 1;
        }
        return b;
    }

    public void saveNVBitmap(final Bitmap bitmap) {
        int width = bitmap.getWidth();
        int xByte = (width + 7) / 8;
        int height = bitmap.getHeight();
        int yByte = (height + 7) / 8;

        int[] pixel = new int[width * height];
        byte[] pixel_0_1 = new byte[width * height];
        bitmap.getPixels(pixel, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            pixel_0_1[i] = px2Byte(pixel[i]);
        }
        byte[] bits = new byte[width * yByte];
        for (int x = 0; x < width; x++) {
            int offset1;
            int offset2;
            for (int yB = 0; yB < yByte - 1; yB++) {
                offset1 = x * yByte + yB;
                for (int i = 0; i < 8; i++) {
                    offset2 = (yB * 8 + i) * width + x;
                    bits[offset1] |= pixel_0_1[offset2];
                    bits[offset1] <<= 1;
                }
            }
            offset1 = x * yByte + yByte - 1;
            for (int y = (yByte - 1) * 8; y < height; y++) {
                bits[offset1] |= pixel_0_1[y * width + x];
                bits[offset1] <<= 1;
            }
        }
        byte[] cmd = {0x1c, 0x71, 0x01, (byte) (xByte & 0xff), (byte) ((xByte >> 8) & 0xff), (byte) (yByte & 0xff), (byte) ((yByte >> 8) & 0xff)};
        mPort.send(cmd);
        mPort.send(bits);
    }

    public void printNVBitmap(boolean ex, boolean ey)//ex =true,为横向放大1倍，ey 为纵向放大1倍
    {
        byte m = 0;

        if (ex && ey) m = 0x33;
        else if (ex) m = 0x31;
        else if (ey) m = 0x32;
        byte[] cmdCenter = {0x1b, 0x61, 0x01};
        mPort.send(cmdCenter);
        byte[] cmd = {0x1c, 0x70, 0x01, m};
        mPort.send(cmd);
        byte[] cmdLeft = {0x1b, 0x61, 0x00};
        mPort.send(cmdLeft);
    }

    public void printBitmap(final Bitmap bitmap, boolean ex, boolean ey)//ex =true,为横向放大1倍，ey 为纵向放大1倍
    {
        byte m = 0;

        if (ex && ey) m = 0x33;
        else if (ex) m = 0x31;
        else if (ey) m = 0x32;

        int width = bitmap.getWidth();
        int xByte = (width + 7) / 8;
        int height = bitmap.getHeight();

        int[] pixel = new int[width * height];
        byte[] pixel_0_1 = new byte[width * height];
        bitmap.getPixels(pixel, 0, width, 0, 0, width, height);
        for (int i = 0; i < width * height; i++) {
            pixel_0_1[i] = px2Byte(pixel[i]);
        }
        byte[] bits = new byte[xByte * height];

        for (int y = 0; y < height; y++) {
            int offset1 = 0;
            int offset2 = 0;
            for (int x = 0; x < xByte - 1; x++) {
                offset1 = y * xByte + x;
                offset2 = y * width + x * 8;
                for (int i = 0; i < 8; i++) {
                    bits[offset1] |= pixel_0_1[offset2 + i];
                    bits[offset1] <<= 1;
                }
            }
            offset1 = y * xByte + xByte - 1;
            for (offset2 = y * xByte + (xByte - 1) * 8; offset2 < width; offset2++) {
                bits[offset1] |= pixel_0_1[offset2];
                bits[offset1] <<= 1;
            }
        }
        byte[] cmd = {0x1d, 0x76, 0x30, m, (byte) (xByte & 0xff), (byte) ((xByte >> 8) & 0xff), (byte) (height & 0xff), (byte) ((height >> 8) & 0xff)};
        mPort.send(cmd);
        mPort.send(bits);

    }

    public void printQRCode(final String content, int size, CorrectionLevel level) // content
    // 为条码内容，size为每个小方块所占的点数(1-8)，level为纠错等级。
    {
        printQRCode(content.getBytes(), size, level);
    }

    public void printQRCode(byte[] content, int size, CorrectionLevel level) // content
    // 为条码内容，size为每个小方块所占的点数(1-8)，level为纠错等级。
    {

        byte[] ba = {0x1d, 0x28, 0x6b, 0x03, 0x00, 0x31, 0x43, 0x00};
        // ba[ba.length-1] = (byte)(0x30+size); -> ba[ba.length-1] = (byte)size;
        ba[ba.length - 1] = (byte) (size);
        mPort.send(ba);

        ba[ba.length - 2] = 0x45;
        switch (level) {
            case CORRECTION_L:
                ba[ba.length - 1] = '0';
                break;
            case CORRECTION_M:
                ba[ba.length - 1] = '1';
                break;
            case CORRECTION_Q:
                ba[ba.length - 1] = '2';
                break;
            case CORRECTION_H:
                ba[ba.length - 1] = '3';
                break;
        }
        mPort.send(ba);
        ba[3] = (byte) ((content.length + 3) & 0xff);
        ba[4] = (byte) ((content.length + 3) >> 8);
        ba[ba.length - 2] = 0x50;
        ba[ba.length - 1] = '0';
        mPort.send(ba);
        mPort.send(content);
        ba[3] = 0x03;
        ba[4] = 0x00;
        ba[ba.length - 2] = 0x51;
        mPort.send(ba);
    }

    @Override
    protected void finalize() {
        if (mPort != null) {
            try {
                mPort.finalize();
            } catch (Exception e) {
                Log.e("Printer", e.getMessage());
            }
        }
    }

    public enum CorrectionLevel// 纠错等级
    {
        CORRECTION_L, CORRECTION_M, CORRECTION_Q, CORRECTION_H
    }
}
