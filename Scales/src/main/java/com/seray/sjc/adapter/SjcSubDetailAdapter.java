package com.seray.sjc.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seray.instance.ProductCart;
import com.seray.scales.R;
import com.seray.sjc.entity.device.ProductADB;
import com.seray.sjc.util.SjcUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商品详情Adapter
 */
public class SjcSubDetailAdapter extends RecyclerView.Adapter {

    private List<ProductCart> productADBList;
    private Context context;

    public SjcSubDetailAdapter(List<ProductCart> productADBList, Context context) {
        this.productADBList = productADBList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.item_reportsubdetail) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_reportsubdetail, parent, false);
            return new DetailViewHolder(view);
        } else {
            View emptyview = LayoutInflater.from(context).inflate(R.layout.item_subtotal_empty, parent, false);
            return new EmptyViewHolder(emptyview);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DetailViewHolder) {
            ((DetailViewHolder) holder).tvDealAmt.setText(productADBList.get(position).getTvProNameStr() + "");
            ((DetailViewHolder) holder).tvDealCnt.setText(productADBList.get(position).getPrice() + "");
            ((DetailViewHolder) holder).tvGoodename.setText(productADBList.get(position).getTvProNameStr());
            ((DetailViewHolder) holder).tvPrictype.setText(SjcUtil.getPriceTypeString(productADBList.get(position).getPrice()));
            ((DetailViewHolder) holder).tvDealPrice.setText(productADBList.get(position).getReal_price() + "");
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (productADBList.size() == 0) {
            return R.layout.item_subtotal_empty;
        } else {
            return R.layout.item_reportsubdetail;
        }
    }

    @Override
    public int getItemCount() {
        return (productADBList.size() == 0) ? 1 : productADBList.size();
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View view) {
            super(view);
        }
    }


    public class DetailViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_goodename)
        TextView tvGoodename;

        @BindView(R.id.tv_prictype)
        TextView tvPrictype;

        @BindView(R.id.tv_dealCnt)
        TextView tvDealCnt;

        @BindView(R.id.tv_dealPrice)
        TextView tvDealPrice;

        @BindView(R.id.tv_dealAmt)
        TextView tvDealAmt;

        @BindView(R.id.rl_tabtitle)
        LinearLayout rlTabtitle;

        public DetailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
