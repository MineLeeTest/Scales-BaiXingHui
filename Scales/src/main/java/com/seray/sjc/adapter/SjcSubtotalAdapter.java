//package com.seray.sjc.adapter;
//
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.seray.scales.R;
//import com.seray.sjc.entity.report.SjcSubtotalWithSjcDetail;
//import com.seray.sjc.util.SjcUtil;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
///**
// * 商品小计Adapter
// */
//public class SjcSubtotalAdapter extends RecyclerView.Adapter {
//    private List<SjcSubtotalWithSjcDetail> subtotals = new ArrayList<>();
//    private Context context;
//    private clickItemInterface clickItemInterface;
//
//
//    public SjcSubtotalAdapter(Context context) {
//        this.context = context;
//    }
//
//    public void setData(List<SjcSubtotalWithSjcDetail> subtotals) {
//        this.subtotals = subtotals;
//        notifyDataSetChanged();
//    }
//
//    public void addData(List<SjcSubtotalWithSjcDetail> subtotalList) {
//        this.subtotals.addAll(subtotalList);
//        notifyDataSetChanged();
//    }
//
//    public interface clickItemInterface{
//        void clickItem(SjcSubtotalWithSjcDetail sjcSubtotal);
//    }
//
//    public void setInterface(clickItemInterface clickItemInterface){
//        this.clickItemInterface = clickItemInterface;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == R.layout.item_reportsubtotal) {
//            View view = LayoutInflater.from(context).inflate(R.layout.item_reportsubtotal, parent, false);
//            return new ViewHolder(view);
//        } else {
//            View emptyview = LayoutInflater.from(context).inflate(R.layout.item_subtotal_empty, parent, false);
//            return new EmptyViewHolder(emptyview);
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (holder instanceof ViewHolder) {
//            ((ViewHolder) holder).tvDate.setText(subtotals.get(position).getSjcSubtotal().getTransDate());
//           if (subtotals.get(position).getSjcDetails() == null) {
//               ((ViewHolder) holder).tvDetailcount.setText(0 + "");
//           }else {
//               ((ViewHolder) holder).tvDetailcount.setText(subtotals.get(position).getSjcDetails().size() + "");
//           }
//            ((ViewHolder) holder).tvPaytype.setText(SjcUtil.getCurPayTypeName(subtotals.get(position).getSjcSubtotal().getPayType()));
//            ((ViewHolder) holder).tvType.setText((subtotals.get(position).getSjcSubtotal().getTransType()).equals("1")?"正常交易":"异常交易");
//            ((ViewHolder) holder).tvPaycount.setText(subtotals.get(position).getSjcSubtotal().getTransAmt() + "");
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (clickItemInterface!=null){
//                        clickItemInterface.clickItem(subtotals.get(position));
//                    }
//                }
//            });
//
//            ((ViewHolder) holder).tvDetail.setVisibility(View.VISIBLE);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return (subtotals.size() == 0) ? 1 : subtotals.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (subtotals.size() == 0) {
//            return R.layout.item_subtotal_empty;
//        } else {
//            return R.layout.item_reportsubtotal;
//        }
//    }
//
//    public class EmptyViewHolder extends RecyclerView.ViewHolder {
//
//        public EmptyViewHolder(View view) {
//            super(view);
//        }
//    }
//
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        @BindView(R.id.tv_date)
//        TextView tvDate;
//        @BindView(R.id.tv_type)
//        TextView tvType;
//        @BindView(R.id.tv_paytype)
//        TextView tvPaytype;
//        @BindView(R.id.tv_paycount)
//        TextView tvPaycount;
//        @BindView(R.id.tv_detailcount)
//        TextView tvDetailcount;
//
//        @BindView(R.id.tv_detail)
//        TextView tvDetail;
//
//        public ViewHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }
//}
