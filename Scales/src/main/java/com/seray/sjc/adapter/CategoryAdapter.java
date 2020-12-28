package com.seray.sjc.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.seray.scales.R;
import com.seray.sjc.entity.product.SjcCategory;

import java.util.List;

/**
 * Author：李程
 * CreateTime：2019/4/20 14:04
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public class CategoryAdapter extends BaseQuickAdapter<SjcCategory, BaseViewHolder> {


    public CategoryAdapter(@Nullable List<SjcCategory> data) {
        super(R.layout.item_category, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SjcCategory item) {
        helper.setText(R.id.category_name, item.getCategoryName());
    }
}
