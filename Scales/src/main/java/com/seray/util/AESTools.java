package com.seray.util;

import android.text.TextUtils;

import com.seray.instance.ResultData;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;


import java.math.BigDecimal;

public class AESTools {
    public static void main(String[] args) {
//
    }

    public static String getKey() {
        //随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();
        //Base64 Encoded
        return Base64.encode(key);
    }

    public static double changeF2Y(String price) {
        return BigDecimal.valueOf(Long.parseLong(price)).divide(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static ResultData encrypt(ResultData resultData, String keyStr, String content) {
        try {
            if (TextUtils.isEmpty(keyStr)) {
                return resultData.setRetMsg(resultData, "2001", "密钥不能为空！");
            }
            if (TextUtils.isEmpty(content)) {
                return resultData.setRetMsg(resultData, "2002", "加密内容不能为空！");
            }
            //Base64 Decoded
            byte[] key = Base64.decode(keyStr);
            //构建
            AES aes = SecureUtil.aes(key);
            //加密为16进制表示
            String encryptHex = aes.encryptHex(content);
            return resultData.setRetMsg(resultData, "9000", encryptHex);
        } catch (Exception e) {
            return resultData.setRetMsg(resultData, "2044", "加密失败");
        }
    }

    public static ResultData decrypt(ResultData resultData, String keyStr, String sign) {
        try {
            //Base64 Decoded
            byte[] key = Base64.decode(keyStr);
            //构建
            AES aes = SecureUtil.aes(key);
            String ret = aes.decryptStr(sign);
            System.out.println("-----ret----------->" + ret);
            //解密为原字符串
            return resultData.setRetMsg(resultData, "9000", ret);
        } catch (Exception e) {
            return resultData.setRetMsg(resultData, "2045", "解签失败");
        }
    }

//    public static ResultData getSign(ResultData resultData, String keyStr) {
//       return encrypt(resultData, keyStr, );
//
//    }
//
//    public static ResultData IsIllegal(ResultData resultData, String key, String sign) {
//        try {
//            resultData = decrypt(resultData, key, sign);
//            if (!"9000".equals(resultData.getCode())) {
//                return resultData;
//            }
//            String[] ret = resultData.getMsgs().toString().split(",");
//            if (ret.length != 5) {
//                return resultData.setRetMsg(resultData, "208", "sign签名内容部分数据缺失！");
//            }
//            String time = ret[0];
//            String userid = ret[1];
//            String money = ret[2];
//            String type = ret[3];
//            String pay_type_id = ret[4];
//            LocalDateTime requestTime = LocalDateTime.parse(time, Constant.DateTimeFormatter_1);
//            LocalDateTime nowTime = LocalDateTime.now();
//            // 【当前时间-1分钟】  <  【有效时间】   <   【当前时间+1分钟】
//            if (!requestTime.isAfter(nowTime.plusMinutes(-1))) {
//                System.out.println("requestTime=" + requestTime.toString());
//                System.out.println("nowTime=" + nowTime.plusMinutes(-1).toString());
//                return resultData.setRetMsg(resultData, "209", "签名过期,请检查您的计算机时间是否正确！");
//            }
//            if (!requestTime.isBefore(nowTime.plusMinutes(1))) {
//                return resultData.setRetMsg(resultData, "210", "签名超前,请检查您的计算机时间是否正确！");
//            }
//            //解密为原字符串
//            return resultData.setRetMsg(resultData, "9000", ret);
//        } catch (Exception e) {
//            log.error("解签失败", e);
//            return resultData.setRetMsg(resultData, "2044", "解签失败");
//        }
//    }

//    public static void main(String[] args) {
//        byte[] bytes = new byte[12];
//        bytes[0] = (byte) 0x00;
//        bytes[1] = (byte) 0x08;
//        bytes[2] = (byte) 0xF8;
//        bytes[3] = (byte) 0xFC;
//        bytes[4] = (byte) 0xF9;
//        bytes[5] = (byte) 0xF0;
//        bytes[6] = (byte) 0xF8;
//        bytes[7] = (byte) 0xF0;
//        bytes[8] = (byte) 0x80;
//        bytes[9] = (byte) 0x78;
//        bytes[10] = (byte) 0xFF;
//        bytes[11] = (byte) 0xD8;
//        String t = new String(bytes);
//        System.out.println(t);
//        System.out.println(changeF2Y("1"));
//        System.out.println("" + changeF2Y("1000000"));
//        System.out.println(URLDecoder.decode("%E7%9F%A5%E4%BA%86%E9%9D%9E%E7%9F%A5%E4%BA%86"));
//        getKey()
//        System.out.println();
//        String aesKey = "C2ckCioYP0ygY9yPw55jVA==";
//        ResultData resultData = new ResultData();
//        resultData = encrypt(resultData, aesKey, "123");
//        System.out.println(resultData.getMsgs());
//        resultData = decrypt(resultData, aesKey, resultData.getMsgs().toString());
//        System.out.println(resultData.getMsgs());
//    }
}
