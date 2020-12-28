package com.seray.sjc.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzscale.scalelib.misclib.Misc;
import com.seray.scales.R;
import com.seray.sjc.adapter.RecognizeProductAdapter;
import com.seray.sjc.entity.message.RecognizeMessage;
import com.seray.sjc.entity.product.SjcProduct;

/**
 * Author：李程
 * CreateTime：2019/5/11 11:41
 * E-mail：licheng@kedacom.com
 * Describe：世界村果蔬识别结果对话框
 */
public class RecognizeProductDialog extends BottomSheetDialogFragment {

    View mRootView;

    RecognizeMessage mRecognizeMessage = new RecognizeMessage(false);

    RecyclerView mRecyclerView;

    RecognizeProductAdapter mAdapter;

    Misc mMisc;

    OnRecognizeProductSelectListener mCallback;

    public interface OnRecognizeProductSelectListener {

        void recognizeProductClick(int position, SjcProduct product);

    }

    public static RecognizeProductDialog getInstance(RecognizeMessage recognizeMessage) {
        RecognizeProductDialog fragment = new RecognizeProductDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable(RecognizeMessage.class.getSimpleName(), recognizeMessage);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMisc = Misc.newInstance();
        mAdapter = new RecognizeProductAdapter(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRecognizeProductSelectListener) {
            mCallback = (OnRecognizeProductSelectListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.dialog_recognize_product_layout, container, false);
            initView();
            if (mRecognizeMessage != null) {
                mAdapter.setNewData(mRecognizeMessage.similarProducts);
            }
            mAdapter.setOnItemClickListener((adapter, view, position) -> {
                mMisc.beep();
                if (mCallback != null) {
                    SjcProduct localProduct = mAdapter.getData().get(position);
                    mCallback.recognizeProductClick(position, localProduct);
                }
            });
        }
        return mRootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
            bottomSheet.getLayoutParams().height = 300;
        }
        final View view = getView();
        view.post(new Runnable() {
            @Override
            public void run() {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
                bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
                parent.setBackgroundColor(Color.TRANSPARENT);
            }
        });
    }

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        if (args != null) {
            mRecognizeMessage = (RecognizeMessage) args.getSerializable(RecognizeMessage.class.getSimpleName());
        }
    }

    private void initView() {
        mRecyclerView = mRootView.findViewById(R.id.dialog_recognize_product_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mRecyclerView.setAdapter(mAdapter);
    }
}
