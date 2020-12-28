package com.seray.sjc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seray.scales.R;
import com.seray.sjc.entity.product.SjcProduct;

import java.math.BigDecimal;
import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/5/11 11:46
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class RecognizeProductAdapter extends BaseQuickAdapter<SjcProduct, BaseViewHolder> {

    public RecognizeProductAdapter(@Nullable List<SjcProduct> data) {
        super(R.layout.item_recognize_product, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SjcProduct item) {
        helper.setText(R.id.item_recognize_product_name, item.getGoodsName());
        BigDecimal salePrice = item.getSalePrice();
        if (salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0) {
            helper.setText(R.id.item_recognize_product_score, salePrice.toString());
        }
    }
}
