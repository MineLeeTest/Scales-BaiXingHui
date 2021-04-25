package com.tools;

import android.annotation.SuppressLint;

public class ByteUtil {




    public static String getHexStr(byte[] buffer, int offset, int len, String split) {
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

    public static byte[] reveseBytes(byte[] bytes) {

        if (bytes == null || bytes.length < 1) {
            return null;
        }
        byte[] val = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            val[bytes.length - 1 - i] = bytes[i];
        }
        return val;
    }

    @SuppressLint("DefaultLocale")
    public static String getCardNo(byte[] bytes) {
        try {
            byte[] cardid = new byte[4];
            System.arraycopy(bytes, 7, cardid, 0, 4);
            cardid = reveseBytes(cardid);
            String hexStr = getHexStr(cardid, 0, cardid.length, "");
            System.out.println("IC卡号hex：" + hexStr);
            long cardIDInt = Long.parseLong(hexStr, 16);
            System.out.println("IC卡号long：" + cardIDInt);
            return String.format("%010d", cardIDInt);
        } catch (Exception e) {
            return null;
        }
    }

//    public static void main(String[] args) {
//        byte[] bytes = new byte[4];
//        bytes[0] = (byte) 0x5C;
//        bytes[1] = (byte) 0x58;
//        bytes[2] = (byte) 0xC6;
//        bytes[3] = (byte) 0x8C;
//        bytes = reveseBytes(bytes);
//        String hex = getHexStr(bytes, 0, bytes.length, "");
////        getCardNo(hex);
//    }
}
