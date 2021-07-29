package com.seray.sjc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seray.scales.CartOrderActivity;
import com.seray.scales.R;
import com.seray.sjc.api.request.ProductCart;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 商品详情Adapter
 */
public class SjcSubDetailAdapter extends RecyclerView.Adapter {

    public List<ProductCart> productADBListChoice;
    public List<ProductCart> productADBList;
    private Context context;
    private CartOrderActivity cartOrderActivity;

    public SjcSubDetailAdapter(CartOrderActivity cartOrderActivity, List<ProductCart> productADBList, Context context) {
        this.productADBList = new ArrayList<>(productADBList);
        this.productADBListChoice = new ArrayList<>(productADBList);
        this.context = context;
        this.cartOrderActivity = cartOrderActivity;
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
            ((DetailViewHolder) holder).tvNo.setText((position + 1) + "");
            ((DetailViewHolder) holder).tvMoney.setText(productADBList.get(position).getMoney_total() + "元");
            ((DetailViewHolder) holder).tvTare.setText(productADBList.get(position).getTare() + "kg");
            ((DetailViewHolder) holder).tvGoodename.setText(productADBList.get(position).getProduct_name());
            ((DetailViewHolder) holder).tvPrice.setText(productADBList.get(position).getPrice_real() + "元");
            ((DetailViewHolder) holder).tvWeight.setText(productADBList.get(position).getWeight() + "kg");
            ((DetailViewHolder) holder).btnChoice.setTag(1);
            ((DetailViewHolder) holder).btnChoice.setText(position + "");
            ((DetailViewHolder) holder).btnChoice.setOnClickListener(btnChoiceClick);
        }
    }


    Button.OnClickListener btnChoiceClick = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button btn = (Button) view;

            int status = (Integer) btn.getTag();
            int postion = Integer.parseInt(btn.getText().toString());
            if (status == 1) {
                btn.setTag(2);
                btn.setBackgroundResource(R.drawable.uncheck);
                productADBListChoice.remove(productADBList.get(postion));
            } else {
                btn.setTag(1);
                btn.setBackgroundResource(R.drawable.checked);
                productADBListChoice.add(productADBList.get(postion));
            }
            cartOrderActivity.change(productADBListChoice);
        }
    };

//    public interface changeProductList(
//    List<ProductCart> productADBList );

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

        @BindView(R.id.tv_no)
        TextView tvNo;

        @BindView(R.id.tv_price)
        TextView tvPrice;

        @BindView(R.id.tv_tare)
        TextView tvTare;

        @BindView(R.id.tv_weight)
        TextView tvWeight;

        @BindView(R.id.tv_money)
        TextView tvMoney;

        @BindView(R.id.btn_choice)
        Button btnChoice;

        @BindView(R.id.rl_tabtitle)
        LinearLayout rlTabtitle;

        public DetailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
