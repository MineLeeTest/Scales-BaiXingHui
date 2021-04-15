//package com.seray.sjc.view;
//
//import android.app.DialogFragment;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.lzscale.scalelib.misclib.Misc;
//import com.seray.cache.CacheHelper;
//import com.seray.scales.R;
//import com.seray.sjc.util.SystemUtils;
///**
// * 激活设置Fragment
// */
//public class ActivateDialogFragment extends DialogFragment {
//    private Misc mMisc = Misc.newInstance();
//    public interface ActivateInterface {
//        void activateConfirm(String number, String snnumber, String version, String password);
//        void activateCancel();
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.dialog_active, container);
//
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//        TextView textView = view.findViewById(R.id.tv_version);
//        textView.setText(SystemUtils.getVersionName(getActivity()));
//
//        EditText et_number = view.findViewById(R.id.et_number);
//        et_number.setText(CacheHelper.DeviceCode);
//
//        EditText et_password = view.findViewById(R.id.et_password);
//        et_password.setText(CacheHelper.DeviceCode);
//
//        EditText snnumber = view.findViewById(R.id.tv_snnumber);
//        snnumber.setText(CacheHelper.DeviceCode);
//        if (!TextUtils.isEmpty(CacheHelper.TermCode)){
//            ((EditText) view.findViewById(R.id.et_number)).setText(CacheHelper.TermCode);
//        }
//        view.findViewById(R.id.dialog_negative).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMisc.beep();
//                ActivateInterface activateInterface = (ActivateInterface) getActivity();
//                activateInterface.activateCancel();
//            }
//        });
//        view.findViewById(R.id.dialog_positive).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMisc.beep();
//                ActivateInterface activateInterface = (ActivateInterface)  getActivity();
//                String number = ((EditText) view.findViewById(R.id.et_number)).getText().toString();
//                String snnumber = ((EditText) view.findViewById(R.id.tv_snnumber)).getText().toString();
//                String version = ((TextView) view.findViewById(R.id.tv_version)).getText().toString();
//                String password = ((EditText) view.findViewById(R.id.et_password)).getText().toString();
//                activateInterface.activateConfirm(number, snnumber, version, password);
//            }
//        });
//        return view;
//    }
//}
