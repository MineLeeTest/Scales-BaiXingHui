//package com.seray.sjc.util;
//
//import com.decard.NDKMethod.BasicOper;
//import com.seray.sjc.entity.card.CpuCardResult;
//import com.seray.sjc.entity.card.PsamCardInfo;
//import com.seray.sjc.entity.card.UserCardInfo;
//import com.seray.util.LogUtil;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * Author：李程
// * CreateTime：2019/5/4 22:40
// * E-mail：licheng@kedacom.com
// * Describe：预付卡余额查询与支付操作帮助类
// */
//public class CpuCardHelper {
//
//    private static final String TAG = CpuCardHelper.class.getSimpleName();
//
//    private static String errorCode;
//    private static String error_msg;
//
//    /*用户卡信息*/
//    private static String CARD_ID;// 卡号
//    private static String ISSUING_COMPANY_2;// 发卡机构代码+FFFFFFFF
//    private static String ISSUING_COMPANY;// 发卡机构代码
//    private static String CITY_CODE;// 发卡城市代码
//    private static String CARD_TYPE; // 卡类型
//    private static String CARD_CLASS; // 卡种
//    private static BigDecimal BALANCE;// 卡余额
//
//    /*PSAM卡信息*/
//    private static String PSAM_ID;// 卡号
//    private static String PSAM_TERM_NO;// 终端号
//    private static BigDecimal ED_BALANCE;// 消费前卡余额
//    private static String TSN;// 卡片交易序列号
//    private static String PSAM_TTN;// PSAM卡交易序列号
//    private static String TAC;// 交易认证码
//
//    public static void reset() {
//        CARD_ID = "";
//        CARD_CLASS = "";
//        CITY_CODE = "";
//        CARD_TYPE = "";
//        PSAM_ID = "";
//        PSAM_TERM_NO = "";
//        TSN = "";
//        PSAM_TTN = "";
//        TAC = "";
//    }
//
//    /**
//     * 余额更新
//     */
//    public static void updateBalance() {
//        String balanceStr = processResult(doUserCard("805C000204"));
//        if (balanceStr != null && balanceStr.endsWith("9000")) {
//            balanceStr = balanceStr.substring(0, balanceStr.length() - 4);
//            BALANCE = new BigDecimal(Long.parseLong(balanceStr, 16));
//            BALANCE = BALANCE.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
//        }
//    }
//
//    /**
//     * 余额查询
//     */
//    public static boolean doBalance() {
//        // 读用户卡电子钱包文件
//        doUserCard("00A4040009A00000000386980701");
//
//        // 读用户卡15文件
//        String cardInfo15 = processResult(doUserCard("00B0950000"));
//        if (cardInfo15 == null) {
//            return false;
//        }
//        CARD_ID = cardInfo15.substring(24, 40);
//        LogUtil.i(TAG, "卡号 " + CARD_ID);
//        ISSUING_COMPANY = cardInfo15.substring(0, 8);
//        LogUtil.i(TAG, "发卡机构代码 " + ISSUING_COMPANY);
//        ISSUING_COMPANY_2 = cardInfo15.substring(0, 16);
//        LogUtil.i(TAG, "发卡机构代码 " + ISSUING_COMPANY_2);
//        CITY_CODE = cardInfo15.substring(20, 24);
//        LogUtil.i(TAG, "发卡城市代码 " + CITY_CODE);
//
//        // 读用户卡19文件
//        String cardInfo19 = processResult(doUserCard("00B0990000"));
//        if (cardInfo19 == null) {
//            return false;
//        }
//        CARD_TYPE = cardInfo19.substring(78, 80);
//        LogUtil.i(TAG, "卡类型 " + CARD_TYPE);
//        CARD_CLASS = cardInfo19.substring(78, 81);
//        LogUtil.i(TAG, "卡种 " + CARD_CLASS);
//
//        // 获取卡余额
//        String balanceStr = processResult(doUserCard("805C000204"));
//        if (balanceStr == null || !balanceStr.endsWith("9000")) {
//            return false;
//        }
//        balanceStr = balanceStr.substring(0, balanceStr.length() - 4);
//        BALANCE = new BigDecimal(Long.parseLong(balanceStr, 16));
//        BALANCE = BALANCE.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
//        LogUtil.i(TAG, "卡内余额 " + BALANCE.toString());
//        return true;
//    }
//
//    /**
//     * 支付
//     *
//     * @param allowCardClass 下发允许支付的卡种
//     */
//    public static boolean doPay(String allowCardClass) {
//        boolean doBalance = doBalance();
//        if (!doBalance) {
//            return false;
//        }
//
//        // 用户卡种不能为空
//        if (CARD_CLASS == null || CARD_CLASS.isEmpty()) {
//            error_msg = "获取卡片信息失败，请检查卡片";
//            return false;
//        }
//
//        // 如果用户卡卡种不在允许的交易卡种列表中，则不允许交易
//        if (!allowCardClass.contains(CARD_CLASS)) {
//            error_msg = "空白卡，无法交易";
//            return false;
//        }
//
//        // 获取PSAM卡主安全域
//        doPsamCard("00A40000023F00");
//
//        // 获取PSAM卡卡号
//        String psamCardInfo = processResult(doPsamCard("00B0950208"));
//        if (psamCardInfo == null) {
//            error_msg = "获取PSAM卡卡号失败";
//            return false;
//        }
//        PSAM_ID = psamCardInfo.substring(0, psamCardInfo.length() - 3);
//        LogUtil.i(TAG, "PSAM卡号 " + PSAM_ID);
//        PSAM_ID = psamCardInfo.substring(5);
//        LogUtil.i(TAG, "PSAM ID " + PSAM_ID);
//
//        // 获取PSAM终端号
//        String psamTermInfo = processResult(doPsamCard("00B0960006"));
//        if (psamTermInfo == null || !psamTermInfo.endsWith("9000")) {
//            error_msg = "获取PSAM终端号失败";
//            return false;
//        }
//        PSAM_TERM_NO = psamTermInfo.substring(0, psamTermInfo.length() - 4);
//        LogUtil.i(TAG, "PSAM终端号 " + PSAM_TERM_NO);
//
//        // 获取当前时间
//        Date operationDate = new Date();
//        String date_time = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.CHINA)
//                .format(operationDate);
//        String[] dates = date_time.split(" ");
//        String dateStr = dates[0];
//        String timeStr = dates[1];
//
//        // PSAM卡选择应用
//        String chooseAppResp = processResult(doPsamCard("00A4040008A000000353414D31"));
//        if (chooseAppResp == null) {
//            error_msg = "PSAM卡选择应用失败";
//            return false;
//        }
//
//        // 读用户卡电子钱包文件
//        doUserCard("00A4040009A00000000386980701");
//
//        // 用户卡消费初始化
//        BigDecimal payAmount = new BigDecimal("0.01");
//        String transMoney = toHexString(payAmount);
//        LogUtil.i(TAG, "交易金额 " + transMoney);
//
//        //拼装指令
//        String command = "805001020B" + "01" + transMoney + PSAM_TERM_NO;
//        String prePayRsp = processResult(doUserCard(command));
//        if (prePayRsp == null) {
//            error_msg = "消费初始化失败";
//            return false;
//        } else if (prePayRsp.equals("9401")) {
//            error_msg = "余额不足";
//            return false;
//        }
//
//        String balanceStr = prePayRsp.substring(0, 8);
//        ED_BALANCE = new BigDecimal(Long.parseLong(balanceStr, 16));
//        ED_BALANCE = ED_BALANCE.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
//        LogUtil.i(TAG, "消费前卡余额 " + ED_BALANCE.toString());
//
//        TSN = prePayRsp.substring(8, 12);
//        LogUtil.i(TAG, "卡片交易序列号 " + TSN);
//        String randRev = prePayRsp.substring(22, 30);
//
//        command = "8070000024" + randRev + TSN + transMoney + "06" + dateStr + timeStr
//                + "01" + "00" + CARD_ID + ISSUING_COMPANY_2;
//
//        String computeMacResp = processResult(doPsamCard(command));
//        if (computeMacResp == null) {
//            error_msg = "计算MAC1失败";
//            return false;
//        }
//
//        String rsp = processResult(doPsamCard("00C0000008"));
//        if (null == rsp) {
//            error_msg = "计算MAC1失败";
//            return false;
//        }
//
//        PSAM_TTN = rsp.substring(0, 8);
//        LogUtil.i(TAG, "PSAM卡交易序列号 " + PSAM_TTN);
//        String MAC1 = rsp.substring(8, 16);
//        LogUtil.i(TAG, "MAC1 " + MAC1);
//
//        // 操作用户卡，消费确认
//        command = "805401000F" + PSAM_TTN + dateStr + timeStr + MAC1;
//        String result = processResult(doUserCard(command));
//        if (result == null) {
//            error_msg = "消费确认失败";
//            return false;
//        }
//
//        // 用户卡金额已经成功扣款
//        TAC = result.substring(0, 8);
//        LogUtil.i(TAG, "交易认证码 " + TAC);
//        String MAC2 = result.substring(8, 16);
//        LogUtil.i(TAG, "MAC2 " + MAC2);
//
//        // 操作PSAM，校验MAC2 MAC2验证失败不影响交易结果
//        processResult(doPsamCard("8072000004" + MAC2));
//
//        // 蜂鸣提示
//        BasicOper.dc_beep(2);
//        return true;
//    }
//
//    /**
//     * 获取用户卡信息
//     * 余额查询成功后调用
//     */
//    public static CpuCardResult<UserCardInfo> getUserCardInfoResponse() {
//        CpuCardResult<UserCardInfo> userCardInfoCpuCardRsp = new CpuCardResult<>();
//        UserCardInfo cardInfo = new UserCardInfo();
//        cardInfo.CARD_ID = CARD_ID;
//        cardInfo.CARD_TYPE = CARD_TYPE;
//        cardInfo.CARD_CLASS = CARD_CLASS;
//        cardInfo.ISSUING_COMPANY = ISSUING_COMPANY;
//        cardInfo.CITY_CODE = CITY_CODE;
//        cardInfo.BALANCE = BALANCE;
//        userCardInfoCpuCardRsp.data = cardInfo;
//        return userCardInfoCpuCardRsp;
//    }
//
//    /**
//     * PSAM卡信息
//     * 支付成功后调用
//     */
//    public static CpuCardResult<PsamCardInfo> getPasmCardInfoResponse() {
//        CpuCardResult<PsamCardInfo> psamCardInfoCpuCardRsp = new CpuCardResult<>();
//        PsamCardInfo cardInfo = new PsamCardInfo();
//        cardInfo.PSAM_ID = PSAM_ID;
//        cardInfo.PSAM_TERM_NO = PSAM_TERM_NO;
//        cardInfo.ED_BALANCE = ED_BALANCE;
//        cardInfo.TSN = TSN;
//        cardInfo.PSAM_TTN = PSAM_TTN;
//        cardInfo.TAC = TAC;
//        psamCardInfoCpuCardRsp.data = cardInfo;
//        return psamCardInfoCpuCardRsp;
//    }
//
//    /**
//     * 获取操作错误信息
//     * 余额查询和支付操作
//     */
//    public static CpuCardResult getErrorResponse() {
//        CpuCardResult errorRsp = new CpuCardResult();
//        errorRsp.errorCode = errorCode;
//        errorRsp.errorMessage = error_msg;
//        return errorRsp;
//    }
//
//    /**
//     * 设备初始化准备
//     */
//    public static boolean startCpuDevice() {
//        return dcReset() && dcConfigCard() && dcCardHex() && dcProResetHex() && dcSetCpu() && dcCpuResetHex();
//    }
//
//    /**
//     * 关闭设备
//     */
//    public static void stopCpuDevice() {
//        try {
//            BasicOper.dc_exit();
//            LogUtil.d(TAG, "读卡设备已关闭！");
//        } catch (Exception e) {
//            LogUtil.e(TAG, "读卡设备关闭异常！" + e.getMessage());
//        }
//    }
//
//    /**
//     * 非接触式CPU射频复位
//     */
//    private static boolean dcReset() {
//        String result = BasicOper.dc_reset();
//        String[] resultArr = result.split("\\|", -1);
//        if (resultArr[0].equals("0000")) {
//            LogUtil.d(TAG, "非接触式CPU射频复位成功！");
//            return true;
//        } else {
//            LogUtil.e(TAG, "非接触式CPU射频复位失败！" + resultArr[0] + "|" + resultArr[1]);
//            return false;
//        }
//    }
//
//    /**
//     * 配置非接触卡类型
//     */
//    private static boolean dcConfigCard() {
//        String result = BasicOper.dc_config_card(0x00);
//        String[] resultArr = result.split("\\|", -1);
//        if (resultArr[0].equals("0000")) {
//            LogUtil.d(TAG, "配置非接触CPU卡成功！");
//            return true;
//        } else {
//            LogUtil.e(TAG, "配置非接触CPU卡失败！" + resultArr[0] + "|" + resultArr[1]);
//            return false;
//        }
//    }
//
//    /**
//     * 寻卡
//     */
//    private static boolean dcCardHex() {
//        String result = BasicOper.dc_card_hex(0x01);
//        String[] resultArr = result.split("\\|", -1);
//        processResult(result);
//        if (resultArr[0].equals("0000")) {
//            LogUtil.d(TAG, "寻卡成功！");
//            return true;
//        } else {
//            LogUtil.e(TAG, "寻卡失败！" + resultArr[0] + "|" + resultArr[1]);
//            return false;
//        }
//    }
//
//    /**
//     * 非接触式CPU卡复位
//     */
//    private static boolean dcProResetHex() {
//        String result = BasicOper.dc_pro_resethex();
//        String[] resultArr = result.split("\\|", -1);
//        if (resultArr[0].equals("0000")) {
//            LogUtil.d(TAG, "非接触式CPU卡复位成功");
//            return true;
//        } else {
//            LogUtil.e(TAG, "非接触式CPU卡复位失败！" + resultArr[0] + "|" + resultArr[1]);
//            return false;
//        }
//    }
//
//    /**
//     * 设置接触式CPU卡卡座
//     */
//    private static boolean dcSetCpu() {
//        String result = BasicOper.dc_setcpu(2);
//        String[] resultArr = result.split("\\|", -1);
//        if (resultArr[0].equals("0000")) {
//            LogUtil.d(TAG, "设置接触式CPU卡卡座成功");
//            return true;
//        } else {
//            LogUtil.e(TAG, "设置接触式CPU卡卡座失败！" + resultArr[0] + "|" + resultArr[1]);
//            return false;
//        }
//    }
//
//    /**
//     * 接触式CPU卡复位
//     */
//    private static boolean dcCpuResetHex() {
//        String result = BasicOper.dc_cpureset_hex();
//        String[] resultArr = result.split("\\|", -1);
//        if (resultArr[0].equals("0000")) {
//            LogUtil.d(TAG, "接触式CPU卡复位成功，ATR/ATS = " + resultArr[1]);
//            return true;
//        } else {
//            LogUtil.e(TAG, "接触式CPU卡复位失败！" + resultArr[0] + "|" + resultArr[1]);
//            BasicOper.dc_exit();//关闭设备
//            return false;
//        }
//    }
//
//    private static String doUserCard(String command) {
//        String result = BasicOper.dc_pro_commandhex(command, 7);
//        LogUtil.d(TAG, "opt user card command : " + command + " , result is " + result);
//        return result;
//    }
//
//    private static String doPsamCard(String command) {
//        String result = BasicOper.dc_cpuapdu_hex(command);
//        LogUtil.d(TAG, "opt psam card command : " + command + " , result is " + result);
//        return result;
//    }
//
//    private static String processResult(String result) {
//        String[] resultArr = result.split("\\|", -1);
//        if (resultArr[0].equals("0000")) {
//            return resultArr[1];
//        } else {
//            errorCode = resultArr[0];
//            error_msg = resultArr[1];
//            return null;
//        }
//    }
//
//    // 金额转化为16进制字符串
//    private static String toHexString(BigDecimal decimal) {
//        long l1 = decimal.multiply(new BigDecimal("100")).longValue();
//        String hex_str = Long.toHexString(l1);
//        for (; ; ) {
//            if (hex_str.length() != 8) {
//                hex_str = "0" + hex_str;
//            } else {
//                break;
//            }
//        }
//        return hex_str.toUpperCase();
//    }
//}
