package com.seray.sjc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seray.scales.R;
import com.seray.sjc.entity.order.SjcDetail;
import com.seray.sjc.util.SjcUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商品详情Adapter
 */
public class SjcSubDetailAdapter extends RecyclerView.Adapter {
    private List<SjcDetail> sjcDetails;
    private Context context;

    public SjcSubDetailAdapter(List<SjcDetail> sjcDetails, Context context) {
        this.sjcDetails = sjcDetails;
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
        if (holder instanceof DetailViewHolder){
           ((DetailViewHolder) holder).tvDealAmt.setText(sjcDetails.get(position).getDealAmt()+"");
           ((DetailViewHolder) holder).tvDealCnt.setText(sjcDetails.get(position).getDealCnt()+"");
           ((DetailViewHolder) holder).tvGoodename.setText(sjcDetails.get(position).getGoodsName());
           ((DetailViewHolder) holder).tvPrictype.setText(SjcUtil.getPriceTypeString(sjcDetails.get(position).getPriceType()));
           ((DetailViewHolder) holder).tvDealPrice.setText(sjcDetails.get(position).getDealPrice()+"");
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (sjcDetails.size() == 0) {
            return R.layout.item_subtotal_empty;
        } else {
            return R.layout.item_reportsubdetail;
        }
    }

    @Override
    public int getItemCount() {
        return (sjcDetails.size()==0)?1:sjcDetails.size();
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View view) {
            super(view);
        }
    }


    public class DetailViewHolder  extends RecyclerView.ViewHolder{
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
