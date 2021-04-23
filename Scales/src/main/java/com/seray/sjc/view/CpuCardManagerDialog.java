//package com.seray.sjc.view;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.design.widget.BottomSheetBehavior;
//import android.support.design.widget.BottomSheetDialogFragment;
//import android.support.design.widget.CoordinatorLayout;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.decard.NDKMethod.BasicOper;
//import com.google.gson.Gson;
//import com.hard.CardReadHelp;
//import com.hard.ReturnValue;
//import com.hard.ReturnValueCallback;
//import com.lzscale.scalelib.misclib.Misc;
//import com.seray.cache.CacheHelper;
//import com.seray.scales.App;
//import com.seray.scales.R;
//import com.seray.sjc.AppExecutors;
//import com.seray.sjc.db.AppDatabase;
//import com.seray.sjc.entity.card.CardPayOrder;
//import com.seray.sjc.entity.card.CpuCardResult;
//import com.seray.sjc.entity.card.PsamCardInfo;
//import com.seray.sjc.entity.card.UserCardInfo;
//import com.seray.sjc.util.CpuCardHelper;
//import com.seray.sjc.work.UploadCardPayOrderWork;
//import com.seray.util.LogUtil;
//import com.seray.util.NumFormatUtil;
//import com.wang.avi.AVLoadingIndicatorView;
//
//import org.greenrobot.eventbus.EventBus;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import androidx.work.Data;
//import androidx.work.OneTimeWorkRequest;
//import androidx.work.WorkManager;
//
///**
// * Author：李程
// * CreateTime：2019/5/5 22:35
// * E-mail：licheng@kedacom.com
// * Describe：
// */
//public class CpuCardManagerDialog extends BottomSheetDialogFragment    {
//
//    Context mContext;
//
//    View mRootView;
//
//    RelativeLayout mCardInfoLayout;
//
//    LinearLayout mCardWaitLayout;
//
//    AVLoadingIndicatorView mLoadView;
//
//    TextView mLoadingTipView;
//
//    Button mBtnQuery, mBtnPay, mBtnCancel;
//
//    String mTransOrderCode;
//
//    BigDecimal mTransAmout;
//
//    private TextView mCardIdView, mCardTypeView, mCardCityCodeView, mCardCompanyView, mCardBalanceView;
//
//    private Toast mToast;
//    private String msgs;
//    private Handler mHandler = new Handler();
//
//    Runnable showRun = new Runnable() {
//
//        @Override
//        public void run() {
//            if (mToast == null) {
//                mToast = Toast.makeText(App.getApplication(), msgs, Toast.LENGTH_SHORT);
//            } else {
//                mToast.setText(msgs);
//            }
//            mToast.show();
//        }
//    };
//
//    private boolean isPay = false;
//    private boolean isWorking = false;
//
//    private Misc mMisc = Misc.newInstance();
//    private BottomSheetBehavior<View> mBottomSheetBehavior;
//    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback
//            = new BottomSheetBehavior.BottomSheetCallback() {
//
//        @Override
//        public void onStateChanged(@NonNull View bottomSheet, int newState) {
//            //禁止拖拽，
//            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
//                //设置为收缩状态
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            }
//        }
//
//        @Override
//        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//        }
//    };
//    ReturnValueCallback returnValueCallback = new ReturnValueCallback() {
//        @Override
//        public void run(ReturnValue result) {
////            mLoadingTipView.setText();
//            Toast.makeText(getContext(), result.getIsSuccess() + "--" + result.getCode(), 20);
//        }
//    };
//    CardReadHelp cardReadHelp = new CardReadHelp();
//    private View.OnClickListener mControlClickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            mMisc.beep();
//            if (isWorking) {
//                showMessage("设备操作中，请稍后尝试！");
//                return;
//            }
//            switch (v.getId()) {
//                case R.id.dialog_card_balance_cancel:
//                    dismiss();
//                    break;
//                case R.id.dialog_card_balance_query:
//                    if (isDeviceReady()) {
//                        onDoQueryClick();
//                    }
////                    cardReadHelp.readCardId(returnValueCallback,"/dev/ttymxc2");
//                    break;
//                case R.id.dialog_card_balance_pay:
//                    if (isDeviceReady()) {
//                        onDoPayClick();
//                    }
//                    break;
//            }
//        }
//    };
//
//    public static CpuCardManagerDialog getInstance(String transOrderCode, BigDecimal transAmt) {
//        CpuCardManagerDialog fragment = new CpuCardManagerDialog();
//        Bundle bundle = new Bundle();
//        bundle.putString("transOrderCode", transOrderCode);
//        bundle.putDouble("transAmt", transAmt.doubleValue());
//        fragment.setArguments(bundle);
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (mRootView == null) {
//            mRootView = inflater.inflate(R.layout.dialog_card_manager_layout, container, false);
//            initView();
//        }
//        return mRootView;
//    }
//
//    @Override
//    public void setArguments(@Nullable Bundle args) {
//        super.setArguments(args);
//        if (args != null) {
//            mTransOrderCode = args.getString("transOrderCode");
//            double transAmt = args.getDouble("transAmt");
//            mTransAmout = new BigDecimal(transAmt);
//            LogUtil.i(mTransAmout.toString());
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.mContext = context;
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Dialog dialog = getDialog();
//        if (dialog != null) {
//            View bottomSheet = dialog.findViewById(R.id.design_bottom_sheet);
//            bottomSheet.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
//        }
//        final View view = getView();
//        view.post(new Runnable() {
//            @Override
//            public void run() {
//                View parent = (View) view.getParent();
//                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
//                CoordinatorLayout.Behavior behavior = params.getBehavior();
//                BottomSheetBehavior bottomSheetBehavior = (BottomSheetBehavior) behavior;
//                mBottomSheetBehavior = (BottomSheetBehavior) behavior;
//                mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
//                bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
//                parent.setBackgroundColor(Color.TRANSPARENT);
//            }
//        });
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        this.mContext = null;
//    }
//
//    @Override
//    public void dismiss() {
//        super.dismiss();
//        mLoadView.hide();
//        mHandler.removeCallbacksAndMessages(null);
//        CpuCardHelper.reset();
//        CpuCardHelper.stopCpuDevice();
//    }
//
//    private boolean isDeviceReady() {
//        int port = openCpuCardUsbReadPort();
//        if (port <= 0) {
//            showMessage("读卡设备端口打开失败！");
//            return false;
//        }
//        boolean startCpuDevice = CpuCardHelper.startCpuDevice();
//        if (!startCpuDevice) {
//            showMessage("读卡设备初始化失败！");
//            return false;
//        }
//        return true;
//    }
//
//    // 预付卡余额查询
//    private void onDoQueryClick() {
//        isWorking = true;
//        mLoadingTipView.setText("余额查询中");
//        if (mCardWaitLayout.getVisibility() == View.GONE || mCardWaitLayout.getVisibility() == View.INVISIBLE) {
//            mCardWaitLayout.setVisibility(View.VISIBLE);
//        }
//        doCpuCardBalance();
//    }
//
//    // 预付卡支付
//    private void onDoPayClick() {
//        if (isPay) {
//            showMessage("已支付！");
//            isWorking = false;
//            return;
//        }
//        isWorking = true;
//        mLoadingTipView.setText("支付确认中");
//        if (mCardWaitLayout.getVisibility() == View.GONE || mCardWaitLayout.getVisibility() == View.INVISIBLE) {
//            mCardWaitLayout.setVisibility(View.VISIBLE);
//        }
//        doCpuCardPay();
//    }
//
//    // 预付卡支付
//    private void doCpuCardPay() {
//        String allowCardClass = null;
//        List<PayTypeInfo> payTypeInfoList = CacheHelper.PayTypeInfoList;
//        for (int i = 0; i < payTypeInfoList.size(); i++) {
//            String cardClass = payTypeInfoList.get(i).getCardClass();
//            if (cardClass != null && !cardClass.isEmpty()) {
//                allowCardClass = cardClass;
//                break;
//            }
//        }
//        if (allowCardClass == null) {
//            closeWaitLoadingView("支付失败");
//            LogUtil.e("预付卡支付失败！无允许支付的卡种！请下发！");
//            isWorking = false;
//            CpuCardHelper.reset();
//            CpuCardHelper.stopCpuDevice();
//            return;
//        }
//
//        try {
//            boolean doPay = CpuCardHelper.doPay(allowCardClass);
//            String tip = doPay ? "支付成功" : "支付失败";
//            closeWaitLoadingView(tip);
//            if (doPay) {
//                isPay = true;
//                CpuCardHelper.updateBalance();
//                CpuCardResult<UserCardInfo> userCardInfoResponse = CpuCardHelper.getUserCardInfoResponse();
//                CpuCardResult<PsamCardInfo> pasmCardInfoResponse = CpuCardHelper.getPasmCardInfoResponse();
//                CardPayOrder cardPayOrder = createCardPayOrder(userCardInfoResponse.data, pasmCardInfoResponse.data);
//                saveCardPayOrder(cardPayOrder);
//
//                LogUtil.i("预付卡支付成功！" + userCardInfoResponse.data.toString());
//                LogUtil.i("预付卡支付成功！" + pasmCardInfoResponse.data.toString());
//            } else {
//                CpuCardResult errorResponse = CpuCardHelper.getErrorResponse();
//                LogUtil.e("预付卡支付失败！" + errorResponse.errorCode + "|" + errorResponse.errorMessage);
//            }
//        } catch (Exception e) {
//            LogUtil.e("预付卡支付异常！" + e.getMessage());
//        } finally {
//            isWorking = false;
//            CpuCardHelper.reset();
//            CpuCardHelper.stopCpuDevice();
//        }
//    }
//
//    // 预付卡余额查询
//    private void doCpuCardBalance() {
//        try {
//            boolean doBalance = CpuCardHelper.doBalance();
//            String tip = doBalance ? "查询成功" : "查询失败";
//            closeWaitLoadingView(tip);
//            if (doBalance) {
//                CpuCardResult<UserCardInfo> userCardInfoResponse = CpuCardHelper.getUserCardInfoResponse();
//                updateBalanceView(userCardInfoResponse);
//                LogUtil.i("预付卡余额查询成功！" + userCardInfoResponse.data.BALANCE.toString());
//            } else {
//                CpuCardResult errorResponse = CpuCardHelper.getErrorResponse();
//                LogUtil.e("预付卡余额查询失败！" + errorResponse.errorCode + "|" + errorResponse.errorMessage);
//            }
//        } catch (Exception e) {
//            LogUtil.e("预付卡余额查询异常！" + e.getMessage());
//        } finally {
//            isWorking = false;
//            CpuCardHelper.stopCpuDevice();
//        }
//    }
//
//    private CardPayOrder createCardPayOrder(UserCardInfo userCardInfo, PsamCardInfo psamCardInfo) {
//        CardPayOrder order = new CardPayOrder();
//        order.setTermId(CacheHelper.device_id+"");
//        order.setTransOrderCode(mTransOrderCode);
//        order.setTransAmt(mTransAmout);
//        order.setTransDate(NumFormatUtil.getDateDetail());
//
//        order.setAliasCardId(userCardInfo.CARD_ID);
//        order.setCardClass(userCardInfo.CARD_CLASS);
//        order.setCardType(userCardInfo.CARD_TYPE);
//        order.setCityCode(userCardInfo.CITY_CODE);
//        order.setIssuingCompany(userCardInfo.ISSUING_COMPANY);
//
//        order.setPsamCardId(psamCardInfo.PSAM_ID);
//        order.setPsamTermId(psamCardInfo.PSAM_TERM_NO);
//        order.setPsamTransSeqNo(psamCardInfo.PSAM_TTN);
//        order.setTransCertifyCode(psamCardInfo.TAC);
//        order.setTransSeqNo(psamCardInfo.TSN);
//
//        order.setBeforeCardBalance(psamCardInfo.ED_BALANCE);
//        order.setAfterCardBalance(userCardInfo.BALANCE);
//        order.setRecordType("TD");
//
//        return order;
//    }
//
//    // 预付卡订单保存
//    private void saveCardPayOrder(CardPayOrder order) {
//        AppExecutors.getInstance().insertIO().submit(new Runnable() {
//            @Override
//            public void run() {
//                CardOrderDao cardOrderDao = AppDatabase.getInstance().getCardOrderDao();
//                cardOrderDao.save(order);
//                EventBus.getDefault().post(order);
//            }
//        });
//
//        Data inputData = new Data.Builder()
//                .putString(UploadCardPayOrderWork.DATA_JSON_KEY, new Gson().toJson(order))
//                .build();
//        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(UploadCardPayOrderWork.class)
//                .setInputData(inputData)
//                .build();
//        WorkManager.getInstance().enqueue(request);
//    }
//
//    private void closeWaitLoadingView(String tip) {
//        if (tip != null) {
//            mLoadingTipView.setText(tip);
//        }
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mCardWaitLayout.setVisibility(View.GONE);
//            }
//        }, 800);
//    }
//
//    private void updateBalanceView(CpuCardResult<UserCardInfo> cardResult) {
//        if (mCardInfoLayout.getVisibility() == View.GONE || mCardInfoLayout.getVisibility() == View.INVISIBLE) {
//            mCardInfoLayout.setVisibility(View.VISIBLE);
//        }
//        UserCardInfo cardInfo = cardResult.data;
//        setTextViewContent(mCardIdView, cardInfo.CARD_ID);
//        setTextViewContent(mCardTypeView, cardInfo.CARD_TYPE);
//        setTextViewContent(mCardCityCodeView, cardInfo.CITY_CODE);
//        setTextViewContent(mCardCompanyView, cardInfo.ISSUING_COMPANY);
//        setTextViewContent(mCardBalanceView, cardInfo.BALANCE.toString());
//    }
//
//    private void setTextViewContent(TextView tv, String content) {
//        String hadText = tv.getText().toString();
//        String[] split = hadText.split("：");
//        tv.setText(String.format("%s：%s", split[0], content));
//    }
//
//
//    private int openCpuCardUsbReadPort() {
//        BasicOper.dc_AUSB_ReqPermission(mContext);
//
//
//        int devHandle = BasicOper.dc_open("AUSB", mContext, "", 0);
//        if (devHandle > 0) {
//            LogUtil.d("读卡设备端口打开成功！设备句柄号 = " + devHandle);
//        } else {
//            LogUtil.e("读卡设备端口打开失败！");
//        }
//        return devHandle;
//    }
//
//    private void showMessage(final String msgs) {
//        this.msgs = msgs;
//        mHandler.post(showRun);
//    }
//
//    private void initView() {
//        mCardInfoLayout = mRootView.findViewById(R.id.dialog_card_balance_layout);
//        mCardWaitLayout = mRootView.findViewById(R.id.dialog_card_wait_layout);
//        mLoadView = mRootView.findViewById(R.id.dialog_card_loading);
//        mLoadingTipView = mRootView.findViewById(R.id.dialog_card_tip);
//        mBtnQuery = mRootView.findViewById(R.id.dialog_card_balance_query);
//        mBtnPay = mRootView.findViewById(R.id.dialog_card_balance_pay);
//        mBtnCancel = mRootView.findViewById(R.id.dialog_card_balance_cancel);
//        mBtnQuery.setOnClickListener(mControlClickListener);
//        mBtnPay.setOnClickListener(mControlClickListener);
//        mBtnCancel.setOnClickListener(mControlClickListener);
//
//        mCardIdView = mRootView.findViewById(R.id.dialog_card_balance_card_id);
//        mCardTypeView = mRootView.findViewById(R.id.dialog_card_balance_card_type);
//        mCardCityCodeView = mRootView.findViewById(R.id.dialog_card_balance_city_code);
//        mCardCompanyView = mRootView.findViewById(R.id.dialog_card_balance_company);
//        mCardBalanceView = mRootView.findViewById(R.id.dialog_card_balance_content);
//    }
//}
