package com.seray.print;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lzscale.scalelib.misclib.Printer;
import com.seray.cache.CacheHelper;
import com.seray.sjc.annotation.PriceType;
import com.seray.sjc.api.result.RecognizeResult;
import com.seray.sjc.entity.card.CardPayOrder;
import com.seray.sjc.entity.order.OrderInfo;
import com.seray.sjc.entity.order.SjcDetail;
import com.seray.sjc.entity.order.SjcSubtotal;
import com.seray.sjc.entity.product.SjcProduct;
import com.seray.sjc.util.SjcUtil;
import com.seray.util.LogUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 小票打印类
 */
public class CustomPrinter extends ScalePrinter {

    private static CustomPrinter mCustomPrinter = null;
    private static Printer mPrinter = null;
    private static ExecutorService printThread;

    private CustomPrinter() {
        mPrinter = new Printer();
        printThread = Executors.newSingleThreadExecutor();
    }

    public static CustomPrinter getInstance() {
        if (mCustomPrinter == null) {
            synchronized (CustomPrinter.class) {
                if (mCustomPrinter == null) {
                    mCustomPrinter = new CustomPrinter();
                }
            }
        }
        return mCustomPrinter;
    }

    @Override
    public void printOrder(@NonNull OrderInfo info) {
        this.print(info, null);
    }

    @Override
    public void printOrder(@NonNull OrderInfo info, Runnable callback) {
        this.print(info, callback);
    }

    public void printRecognizeListResult(final List<RecognizeResult> data) {
        printThread.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.PrintGBKString(LINE_BREAK);
                mPrinter.PrintGBKString("后台服务返回结果" + LINE_BREAK);

                for (RecognizeResult bean : data) {
                    if (bean != null) {
                        mPrinter.PrintGBKString(bean.name + "\t" + bean.score + LINE_BREAK);
                    }
                }
                mPrinter.PrintGBKString(LINE_BREAK);
            }
        });
    }

    public void printRecognizeProducts(List<SjcProduct> data) {
        printThread.execute(new Runnable() {
            @Override
            public void run() {
                mPrinter.PrintGBKString(LINE_BREAK);
                mPrinter.PrintGBKString("本地品名库比对结果" + LINE_BREAK);
                for (SjcProduct bean : data) {
                    if (bean != null) {
                        mPrinter.PrintGBKString(bean.getGoodsName() + LINE_BREAK);
                    }
                }
                mPrinter.PrintGBKString(LINE_BREAK);
            }
        });
    }

    public void printBitmap() {
        printThread.submit(new Runnable() {
            @Override
            public void run() {
                mPrinter.PrintGBKString(LINE_BREAK);
                mPrinter.PrintGBKString(LINE_BREAK);
                mPrinter.PrintGBKString(LINE_BREAK);

                mPrinter.printNVBitmap(false, false);

                mPrinter.PrintGBKString(LINE_BREAK);
                mPrinter.PrintGBKString(LINE_BREAK);
                mPrinter.PrintGBKString(LINE_BREAK);
            }
        });
    }


    public void printTest() {
        printThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mPrinter.PrintGBKString(LINE_BREAK);
                    mPrinter.PrintGBKString(LINE_BREAK);
                    mPrinter.PrintGBKString("测试打印" + LINE_BREAK);
                    mPrinter.expand(2, 2);
                    mPrinter.PrintGBKString("测试打印" + LINE_BREAK);
                    mPrinter.expand(1, 1);
                    String sb = "测试打印";
                    String str = getCenterStr("测试打印", false);
                    mPrinter.PrintGBKString(str + LINE_BREAK);
                    mPrinter.expand(2, 2);
                    mPrinter.PrintGBKString(getCenterStr("测试打印", true) + LINE_BREAK);
                    mPrinter.expand(1, 1);
                    mPrinter.printQRCode(sb, 6, Printer.CorrectionLevel.CORRECTION_L);
                    mPrinter.PrintGBKString(LINE_BREAK);
                    mPrinter.PrintGBKString(LINE_BREAK);
                    mPrinter.PrintGBKString(LINE_BREAK);
                    mPrinter.PrintGBKString(LINE_BREAK);
                } catch (Exception e) {
                    Log.e("Print", e.getMessage());
                }
            }
        });
    }

    public void saveBitmap(Bitmap bitmap, final Runnable callback) {
        printThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mPrinter.saveNVBitmap(bitmap);
                } catch (Exception e) {
                    LogUtil.e("下发NV位图失败！" + e.getMessage());
                } finally {
                    if (callback != null) {
                        callback.run();
                    }
                }
            }
        });
    }

    private void print(final OrderInfo order, final Runnable callback) {
        printThread.submit(new Runnable() {
            @Override
            public void run() {
                SjcSubtotal subtotal = order.getSjcSubtotal();
                List<SjcDetail> sjcDetails = order.getSjcDetails();
                int totalCount = sjcDetails.size();
                try {
                    String transOrderCode = subtotal.getTransOrderCode();
                    String dealTime = subtotal.getTransDate();
                    printUniversalInfo(transOrderCode, dealTime);
                    printSaleDetail(sjcDetails);
                    String payType = subtotal.getPayType();
                    BigDecimal amount = subtotal.getTransAmt();
                    printTotalAmount(totalCount, amount, payType);
                    printOrderTag(order.getTag());
                    printTracingImage(transOrderCode);
                } catch (Exception e) {
                    LogUtil.e("打印错误:" + e.getMessage());
                } finally {
                    if (callback != null) {
                        callback.run();
                    }
                }
            }
        });
    }

    private void printOrderTag(Object tag) {
        if (tag != null) {
            if (tag instanceof CardPayOrder) {
                CardPayOrder cardInfo = (CardPayOrder) tag;
                String userCardNumber = dealUserCardNumber(cardInfo.getAliasCardId());
                mPrinter.PrintGBKString(CARD_NUMBER + userCardNumber + LINE_BREAK);
                mPrinter.PrintGBKString(CARD_BALANCE + cardInfo.getAfterCardBalance().toString() + LINE_BREAK);
            }
        }
        mPrinter.PrintGBKString(SMALL_SEPARATOR + LINE_BREAK);
    }

    private String dealUserCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "NULL";
        }
        byte[] bytes = cardNumber.getBytes();
        if (bytes.length > 8) {
            int start = 4;
            int end = bytes.length - 4;
            for (int i = start; i < end; i++) {
                bytes[i] = '*';
            }
        }
        return new String(bytes);
    }

    private void printTracingImage(String transOrderCode) {
        mPrinter.PrintGBKString("\t");
        String sb = CacheHelper.TracingBaseUr + transOrderCode;
        mPrinter.printQRCode(sb, 6, Printer.CorrectionLevel.CORRECTION_L);
        mPrinter.PrintGBKString(LINE_BREAK);
        mPrinter.PrintGBKString(LINE_BREAK);
        mPrinter.PrintGBKString(LINE_BREAK);
    }

    private void printSaleDetail(List<SjcDetail> list) {
        for (int j = 0; j < list.size(); j++) {
            SjcDetail detail = list.get(j);
            String priceType = detail.getPriceType();
            boolean isByWeight = priceType.equals(PriceType.BY_PRICE);
            String proName = detail.getGoodsName();
            String proTotalMoney = detail.getDealAmt().toString();
            mPrinter.PrintGBKString(proName + LINE_BREAK);
            BigDecimal price = detail.getDealPrice();
            BigDecimal weight = detail.getDealCnt();

            if (isByWeight) {
                BigDecimal mut = new BigDecimal(2.0);
                price = CacheHelper.isOpenJin ? price.divide(mut, BigDecimal.ROUND_UNNECESSARY) : price;
                weight = CacheHelper.isOpenJin ? weight.multiply(mut) : weight;
            }

            String strPrice = String.valueOf(price);

            mPrinter.PrintGBKString("\t" + weight.toString() + "\t");

            mPrinter.PrintGBKString(CacheHelper.isPrintPrice ? (strPrice + "\t") : ("-" + "\t"));// 是否开启了打印单价

            mPrinter.PrintGBKString(proTotalMoney + LINE_BREAK);

            mPrinter.PrintGBKString("商品编码:" + detail.getGoodsCode() + LINE_BREAK);
        }

        mPrinter.PrintGBKString(SMALL_SEPARATOR + LINE_BREAK);

    }

    private void printUniversalInfo(String serialNum, String dealTime) {
        mPrinter.printNVBitmap(false, false);
        mPrinter.PrintGBKString(SMALL_SEPARATOR + LINE_BREAK);
        mPrinter.PrintGBKString(BOOTH_CODE + CacheHelper.BoothId + LINE_BREAK);
        mPrinter.PrintGBKString(DEAL_TIME + dealTime + LINE_BREAK);
        mPrinter.PrintGBKString(TERM_CODE + CacheHelper.TermCode + LINE_BREAK);
        mPrinter.PrintGBKString(DEAL_NUMBER + serialNum + LINE_BREAK);
        mPrinter.PrintGBKString(SMALL_SEPARATOR + LINE_BREAK);
        mPrinter.PrintGBKString(SMALL_CATEGORY_B + LINE_BREAK);
        mPrinter.PrintGBKString(SMALL_SEPARATOR + LINE_BREAK);
    }

    private void printTotalAmount(int totalCount, BigDecimal payAmount, String type) {
        String amount = payAmount.toString();
        mPrinter.PrintGBKString(TOTAL_COUNT + totalCount + "\t" + TOTAL_AMOUNT + amount + LINE_BREAK);
        mPrinter.PrintGBKString(SMALL_SEPARATOR + LINE_BREAK);
        String payTypeString = SjcUtil.getCurPayTypeName(type);
        mPrinter.PrintGBKString(PAYMENT_TYPE + payTypeString + LINE_BREAK);
    }
}
