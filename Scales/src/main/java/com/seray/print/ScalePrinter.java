//package com.seray.print;
//
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//
//abstract class ScalePrinter {
//
//    static final String LINE_BREAK = "\r\n";
//    static final String BOOTH_CODE = "摊位号:";
//    static final String TERM_CODE = "称号:";
//    static final String DEAL_TIME = "销售时间:";
//    static final String DEAL_NUMBER = "票号:";
//    static final String TOTAL_COUNT = "总数:";
//    static final String TOTAL_AMOUNT = "总金额:";
//    static final String PAYMENT_TYPE = "支付方式:";
//    static final String CARD_NUMBER = "卡号:";
//    static final String CARD_BALANCE = "余额:";
//    static final String SMALL_CATEGORY_B = "数量(kg/件)  单价(元)   金额(元)";
//    static final String SMALL_SEPARATOR = "--------------------------------";
//
//    public abstract void printOrder(@NonNull OrderInfo info);
//
//    public abstract void printOrder(@NonNull OrderInfo info, @Nullable Runnable callBack);
//
//    String getCenterStr(String txt, boolean isBig) {
//        if (isBig)
//            return getCenterStr(txt, 16);
//        return getCenterStr(txt, 32);
//    }
//
//    private String getCenterStr(String txt, int maxByteLen) {
//        StringBuilder res = new StringBuilder();
//        String spaceChar = " ";
//        do {
//            int len = getSingleByteCount(txt);
//            if (len <= maxByteLen) {
//                for (int i = 0; i < (maxByteLen - len) / 2; i++) {
//                    res.insert(0, spaceChar);
//                }
//                res.append(txt);
//                break;
//            }
//            String str = getStrByByteCount(txt, maxByteLen);
//            res.append(str).append("\r\n");
//            txt = txt.substring(str.length());
//        } while (true);
//        return res.toString();
//    }
//
//    private int getSingleByteCount(String txt) {
//        char[] arr = txt.toCharArray();
//        int i = 0, count = 0;
//        for (; i < arr.length; i++) {
//            if (arr[i] > 256)
//                count += 2;
//            else
//                count++;
//        }
//        return count;
//    }
//
//    private String getStrByByteCount(String txt, int byteCount) {
//        char[] arr = txt.toCharArray();
//        int i = 0, count = 0;
//        for (; i < arr.length; i++) {
//            if (arr[i] > 256)
//                count += 2;
//            else
//                count++;
//            if (count == byteCount)
//                return txt.substring(0, i + 1);
//            else if (count > byteCount)
//                return txt.substring(0, i);
//        }
//        return txt;
//    }
//}
