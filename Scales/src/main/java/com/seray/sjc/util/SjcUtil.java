package com.seray.sjc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.TypedValue;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.seray.cache.CacheHelper;
import com.seray.sjc.annotation.PriceType;

import java.util.Hashtable;

public class SjcUtil {

    public static String getCurPayTypeName(@NonNull String payType) {
        String payTypeName = "现金支付";
        for (int i = 0; i < CacheHelper.PayTypeInfoList.size(); i++) {
            if (payType.equals(CacheHelper.PayTypeInfoList.get(i).getPayType())) {
                payTypeName = CacheHelper.PayTypeInfoList.get(i).getPayName();
                break;
            }
        }
        return payTypeName;
    }

    public static String getPriceTypeString(@PriceType String priceType) {
        if (priceType == null) {
            return "计价";
        }
        if (PriceType.BY_PRICE.equals(priceType)) {
            return "计价";
        } else if (PriceType.BY_PIECE.equals(priceType)) {
            return "计件";
        }
        return priceType;
    }

    /**
     * 生成二维码
     *
     * @param str            字符串
     * @param widthAndHeight 二维码宽高 px
     */
    public static Bitmap createQRCode(String str, int widthAndHeight) throws WriterException {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 0); /* default = 4 */

        BitMatrix matrix = new MultiFormatWriter().encode(str,
                BarcodeFormat.QR_CODE, widthAndHeight, widthAndHeight, hints);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = Color.BLACK;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * dp转px
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics()));
    }
}
