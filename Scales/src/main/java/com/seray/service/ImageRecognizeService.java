package com.seray.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.seray.cache.CacheHelper;
import com.seray.sjc.annotation.PriceType;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.ImageRecognizeReq;
import com.seray.sjc.api.result.ApiDataRsp;
import com.seray.sjc.api.result.RecognizeResult;
import com.seray.sjc.converters.MoneyConverter;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.entity.message.RecognizeMessage;
import com.seray.sjc.entity.product.SjcProduct;
import com.seray.util.CodeFormat;
import com.seray.util.LogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Response;

/**
 * Author：李程
 * CreateTime：2019/5/15 15:37
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class ImageRecognizeService extends Service {

    private ImageRecognizeBinder mImageRecognizeBinder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (mImageRecognizeBinder == null) {
            mImageRecognizeBinder = new ImageRecognizeBinder();
        }
        return mImageRecognizeBinder;
    }

    public class ImageRecognizeBinder extends Binder {

        private Future mLastQueryFuture;

        private ExecutorService mSingleThreadExecutor;

        ImageRecognizeBinder() {
            mSingleThreadExecutor = Executors.newSingleThreadExecutor();
        }

        public void productImageRecognizeStart(final String encodeOrderImg) {
            cancelLastRecognizeTask();
            // 提交新的识别请求
            mLastQueryFuture = mSingleThreadExecutor.submit(() -> {
                RecognizeMessage recognizeMessage = new RecognizeMessage();
                try {
                    ImageRecognizeReq request = new ImageRecognizeReq(encodeOrderImg);
                    Response<ApiDataRsp<List<RecognizeResult>>> response = HttpServicesFactory.getHttpServiceApi()
                            .productImageRecognize(request)
                            .execute();
                    if (response.isSuccessful()) {
                        ApiDataRsp<List<RecognizeResult>> body = response.body();
                        if (body != null && body.isSuccess()) {
                            List<RecognizeResult> info = body.info;
                            if (info != null && !info.isEmpty()) {
                                List<RecognizeResult> serverData = new ArrayList<>(info);
                                recognizeMessage = checkRecognizeResult(info);
                                recognizeMessage.serverData = serverData;
                            }
                        }
                    }
                } catch (IOException e) {
                    LogUtil.e("果蔬图片识别异常！" + e.getMessage());
                } finally {
                    EventBus.getDefault().post(recognizeMessage);
                }
            });
        }

        private void shutDownNow() {
            if (!mSingleThreadExecutor.isShutdown()) {
                mSingleThreadExecutor.shutdownNow();
            }
        }

        private void cancelLastRecognizeTask() {
            if (mLastQueryFuture != null) {
                mLastQueryFuture.cancel(true);
            }
        }

        private RecognizeMessage checkRecognizeResult(List<RecognizeResult> info) {
            RecognizeMessage recognizeMessage = new RecognizeMessage(true);
            // 去除非果蔬食材
            for (RecognizeResult bean : info) {
                String name = bean.name;
                if (name.equals("非果蔬食材")) {
                    info.remove(bean);
                    break;
                }
            }
            // 是否还有识别结果
            if (info.isEmpty()) {
                return recognizeMessage;
            }
            // 排序
            Collections.sort(info, (o1, o2) -> {
                BigDecimal score1 = new BigDecimal(o1.score).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal score2 = new BigDecimal(o2.score).setScale(2, BigDecimal.ROUND_HALF_UP);
                return score2.compareTo(score1);
            });

            List<RecognizeResult> satisfyData = new ArrayList<>();

            for (RecognizeResult recognizeResult : info) {
                double score = recognizeResult.score;
                boolean satisfy = isSatisfy(score);
                if (satisfy) {
                    satisfyData.add(recognizeResult);
                }
            }

            // 识别结果是否在品名库中
            AppDatabase database = AppDatabase.getInstance();

            if (satisfyData.size() <= 0) {
                satisfyData.addAll(info);
            } else {
                String queryStr = getQueryStr(satisfyData);
                Cursor cursor = database.query(queryStr, null);
                int count = cursor.getCount();
                cursor.close();
                if (count <= 0) {
                    satisfyData.clear();
                    satisfyData.addAll(info);
                }
            }

            String queryStr = getQueryStr(satisfyData);
            Cursor cursor = database.query(queryStr, null);
            List<SjcProduct> similarProducts = bindValue(cursor);
            cursor.close();

            int similarSize = similarProducts.size();
            if (similarSize <= 0) {
                covertServerDataToProducts(info, similarProducts);
            }
            recognizeMessage.similarProducts = similarProducts;
            return recognizeMessage;
        }

        private String getQueryStr(List<RecognizeResult> satisfyData) {
            StringBuilder basicQuery = new StringBuilder("SELECT * FROM " + SjcProduct.TABLE_NAME + " WHERE ");
            for (int i = 0; i < satisfyData.size(); i++) {
                String name = satisfyData.get(i).name;
                if (i == 0) {
                    basicQuery.append(SjcProduct.COLUMN_GOODS_NAME).append(" LIKE '%").append(name).append("%'");
                } else {
                    basicQuery.append(" OR ").append(SjcProduct.COLUMN_GOODS_NAME).append(" LIKE '%").append(name).append("%'");
                }
            }
            return basicQuery.toString();
        }

        private void covertServerDataToProducts(List<RecognizeResult> info, List<SjcProduct> similarProducts) {
            for (int i = 0; i < info.size(); i++) {
                String name = info.get(i).name;
                SjcProduct product = new SjcProduct();
                product.setGoodsName(name);
                product.setGoodsId(CodeFormat.createCode());
                product.setGoodsCode("0000000");
                product.setSalePrice(BigDecimal.ZERO);
                product.setSaleUnit("公斤");
                product.setReferencePrice(BigDecimal.ZERO);
                product.setPriceType(PriceType.BY_PRICE);
                similarProducts.add(product);
            }
        }

        private List<SjcProduct> bindValue(Cursor cursor) {
            try {
                final int cursorIndexOfGoodsId = cursor.getColumnIndexOrThrow("goodsId");
                final int cursorIndexOfGoodsCode = cursor.getColumnIndexOrThrow("goodsCode");
                final int cursorIndexOfCategoryId = cursor.getColumnIndexOrThrow("categoryId");
                final int cursorIndexOfGoodsName = cursor.getColumnIndexOrThrow("goodsName");
                final int cursorIndexOfImageUrl = cursor.getColumnIndexOrThrow("imageUrl");
                final int cursorIndexOfGoodsSort = cursor.getColumnIndexOrThrow("goodsSort");
                final int cursorIndexOfPriceType = cursor.getColumnIndexOrThrow("priceType");
                final int cursorIndexOfReferencePrice = cursor.getColumnIndexOrThrow("referencePrice");
                final int cursorIndexOfSalePrice = cursor.getColumnIndexOrThrow("salePrice");
                final int cursorIndexOfSaleUnit = cursor.getColumnIndexOrThrow("saleUnit");
                final List<SjcProduct> _result = new ArrayList<>(cursor.getCount());
                while (cursor.moveToNext()) {
                    final SjcProduct _item;
                    _item = new SjcProduct();
                    final String _tmpGoodsId;
                    _tmpGoodsId = cursor.getString(cursorIndexOfGoodsId);
                    _item.setGoodsId(_tmpGoodsId);
                    final String _tmpGoodsCode;
                    _tmpGoodsCode = cursor.getString(cursorIndexOfGoodsCode);
                    _item.setGoodsCode(_tmpGoodsCode);
                    final String _tmpCategoryId;
                    _tmpCategoryId = cursor.getString(cursorIndexOfCategoryId);
                    _item.setCategoryId(_tmpCategoryId);
                    final String _tmpGoodsName;
                    _tmpGoodsName = cursor.getString(cursorIndexOfGoodsName);
                    _item.setGoodsName(_tmpGoodsName);
                    final String _tmpImageUrl;
                    _tmpImageUrl = cursor.getString(cursorIndexOfImageUrl);
                    _item.setImageUrl(_tmpImageUrl);
                    final int _tmpGoodsSort;
                    _tmpGoodsSort = cursor.getInt(cursorIndexOfGoodsSort);
                    _item.setGoodsSort(_tmpGoodsSort);
                    final String _tmpPriceType;
                    _tmpPriceType = cursor.getString(cursorIndexOfPriceType);
                    _item.setPriceType(_tmpPriceType);
                    final BigDecimal _tmpReferencePrice;
                    final String _tmp;
                    _tmp = cursor.getString(cursorIndexOfReferencePrice);
                    _tmpReferencePrice = MoneyConverter.getMoneyBigDecimal(_tmp);
                    _item.setReferencePrice(_tmpReferencePrice);
                    final BigDecimal _tmpSalePrice;
                    final String _tmp_1;
                    _tmp_1 = cursor.getString(cursorIndexOfSalePrice);
                    _tmpSalePrice = MoneyConverter.getMoneyBigDecimal(_tmp_1);
                    _item.setSalePrice(_tmpSalePrice);
                    final String _tmpSaleUnit;
                    _tmpSaleUnit = cursor.getString(cursorIndexOfSaleUnit);
                    _item.setSaleUnit(_tmpSaleUnit);
                    _result.add(_item);
                }
                return _result;
            } finally {
                cursor.close();
            }
        }

        private boolean isSatisfy(double score) {
            BigDecimal bigScore = new BigDecimal(score).multiply(new BigDecimal(100))
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            // 若小于设置的果蔬识别最小阈值
            return bigScore.compareTo(new BigDecimal(CacheHelper.MinRecognizeValue)) >= 0;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mImageRecognizeBinder.cancelLastRecognizeTask();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mImageRecognizeBinder.shutDownNow();
        super.onDestroy();
    }
}
