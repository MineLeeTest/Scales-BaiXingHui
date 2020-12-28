package com.seray.sjc.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seray.scales.R;
import com.seray.cache.CacheHelper;
import com.seray.sjc.annotation.PriceType;
import com.seray.sjc.entity.order.SjcDetail;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/5/22 00:55
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class SjcDetailAdapter extends BaseQuickAdapter<SjcDetail, BaseViewHolder> {

    private OnTotalDetailDeleteListener mItemDeleteCallback;

    public SjcDetailAdapter(@Nullable List<SjcDetail> data, OnTotalDetailDeleteListener callback) {
        super(R.layout.item_total_detail, data);
        mItemDeleteCallback = callback;
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, SjcDetail sjcDetail) {

        BigDecimal bigWeight = sjcDetail.getDealCnt();
        BigDecimal bigPrice = sjcDetail.getDealPrice();
        BigDecimal amount = sjcDetail.getDealAmt();
        final String name = sjcDetail.getGoodsName();
        final String dealMode = sjcDetail.getPriceType();

        if (CacheHelper.isOpenJin) {
            BigDecimal multiple = new BigDecimal(2.0);
            if (dealMode.equals(PriceType.BY_PRICE)) {
                bigWeight = bigWeight.multiply(multiple);
            }
            bigPrice = bigPrice.divide(multiple, 2);
        }

        baseViewHolder.setText(R.id.total_list_item_productName, name)
                .setText(R.id.total_list_item_productPrice, bigPrice.toString())
                .setText(R.id.total_list_item_productWeight, bigWeight.toString())
                .setText(R.id.total_list_item_productMoney, amount.toString());

        if (mItemDeleteCallback != null) {
            BigDecimal finalBigWeight = bigWeight;
            baseViewHolder.getView(R.id.total_list_item_delete)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemDeleteCallback.onItemDeleteClick(baseViewHolder.getAdapterPosition(),
                                    name, finalBigWeight.toString());
                        }
                    });
        }
    }

    public interface OnTotalDetailDeleteListener {
        void onItemDeleteClick(int position, String name, String number);
    }
}
