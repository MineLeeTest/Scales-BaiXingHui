package com.seray.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.scales.BuildConfig;
import com.seray.sjc.api.request.ActivateReq;
import com.seray.sjc.api.request.TermHeartReq;

import java.util.UUID;

public class HardwareNetwork {
    @SuppressLint("HardwareIds")
    public static ResultData getSimData(Context context) {

        ResultData resultData = new ResultData("5101", "获取硬件联网信息！", "");
        ActivateReq activateReq = new ActivateReq();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return resultData.setRetMsg(resultData, "5102", "获取设备信息失败！");
            }
            StringBuffer sb = new StringBuffer();
            switch (tm.getSimState()) { //getSimState()取得sim的状态  有下面6中状态
                case TelephonyManager.SIM_STATE_ABSENT:
                    sb.append("无卡");
                    break;
                case TelephonyManager.SIM_STATE_UNKNOWN:
                    sb.append("未知状态");
                    break;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    sb.append("需要NetworkPIN解锁");
                    break;
                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    sb.append("需要PIN解锁");
                    break;
                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    sb.append("需要PUK解锁");
                    break;
                case TelephonyManager.SIM_STATE_READY:
                    sb.append("良好");
                    break;
            }
            if (tm.getSimOperator().equals("")) {
                sb.append("@无法取得供货商代码");
            } else {
                sb.append("@").append(tm.getSimOperator().toString());
            }

            if (tm.getSimOperatorName().equals("")) {
                sb.append("@无法取得供货商");
            } else {
                sb.append("@").append(tm.getSimOperatorName().toString());
            }

            if (tm.getSimCountryIso().equals("")) {
                sb.append("@无法取得国籍");
            } else {
                sb.append("@").append(tm.getSimCountryIso().toString());
            }

            if (tm.getNetworkOperator().equals("")) {
                sb.append("@无法取得网络运营商");
            } else {
                sb.append("@").append(tm.getNetworkOperator());
            }
            if (tm.getNetworkOperatorName().equals("")) {
                sb.append("@无法取得网络运营商名称");
            } else {
                sb.append("@").append(tm.getNetworkOperatorName());
            }
            if (tm.getNetworkType() == 0) {
                sb.append("@无法取得网络类型");
            } else {
                sb.append("@").append(tm.getNetworkType());
            }
            if (tm.getSimSerialNumber() != null) {
                sb.append("@").append(tm.getSimSerialNumber().toString());
            } else {
                sb.append("@无法取得SIM卡号");
            }
            String deviceid = tm.getDeviceId();
            sb.append("@deviceid=").append(deviceid);
            String tel = tm.getLine1Number();
            sb.append("@tel=").append(tel);
            String imei = tm.getSimSerialNumber();//手机ID
            activateReq.dzc_imei = imei;
            sb.append("@imei=").append(imei);
            int simState = tm.getSimState();
            sb.append("@simState=").append(simState);

            System.out.println(sb.toString());
            String imsi = tm.getSubscriberId();//手机卡ID
            if (null == imsi) {
                return resultData.setRetMsg(resultData, "5103", "手机卡不存在！");
            }
            activateReq.sim_imsi = imsi;
            activateReq.company_id = CacheHelper.company_id;
            activateReq.app_version = BuildConfig.VERSION_NAME;
            resultData.setTrueMsg(activateReq);

        } catch (Exception e) {
            resultData.setRetMsg("51044", "获取硬件信息失败！");
        }
        return resultData;
    }

    @SuppressLint("HardwareIds")
    public static ResultData getHeartBeatReq(Context context) {
        ResultData resultData = new ResultData("5101", "获取电子秤信息！", "");
        TermHeartReq heartReq = new TermHeartReq();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return resultData.setRetMsg(resultData, "5102", "获取设备信息失败！");
            }

            String imsi = tm.getSubscriberId();//手机卡ID
            if (null == imsi) {
                return resultData.setRetMsg(resultData, "5103", "手机卡不存在！");
            }
            heartReq.sim_imsi = imsi;
            heartReq.dzc_imei = tm.getSimSerialNumber();//手机ID
            heartReq.device_dzc_id = CacheHelper.device_id;
            heartReq.app_version = BuildConfig.VERSION_NAME;
            heartReq.info = CacheHelper.getConfigMapString();
            heartReq.company_id = CacheHelper.company_id;
            resultData.setTrueMsg(heartReq);
        } catch (Exception e) {
            resultData.setRetMsg("51044", "获取硬件信息失败！");
        }
        return resultData;
    }
}
