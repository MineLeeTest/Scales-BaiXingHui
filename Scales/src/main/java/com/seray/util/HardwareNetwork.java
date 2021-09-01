package com.seray.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.seray.cache.CacheHelper;
import com.seray.instance.ResultData;
import com.seray.scales.BuildConfig;
import com.seray.sjc.api.request.RequestHeartBeatVM;
import com.seray.sjc.api.request.RequestRegisterVM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.hutool.core.util.NumberUtil;

public class HardwareNetwork {
    @SuppressLint("HardwareIds")
    public static ResultData getSimData(Context context) {

        ResultData resultData = new ResultData("5101", "获取硬件联网信息！", "");
        RequestRegisterVM requestRegisterVM = new RequestRegisterVM();
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
            imei = null == imei ? "ne" + System.currentTimeMillis() : imei;
            requestRegisterVM.setDzc_imei(imei);
            sb.append("@imei=").append(imei);
            int simState = tm.getSimState();
            sb.append("@simState=").append(simState);

            System.out.println(sb.toString());
            String imsi = tm.getSubscriberId();//手机卡ID
            if (null == imsi) {
                imsi = "ne" + System.currentTimeMillis();
            }
            requestRegisterVM.setSim_imsi(imsi);
            requestRegisterVM.setCompany_id(CacheHelper.company_id);
            requestRegisterVM.setApp_version(BuildConfig.VERSION_NAME);
            resultData.setTrueMsg(requestRegisterVM);

        } catch (Exception e) {
            resultData.setRetMsg("51044", "获取硬件信息失败！");
        }
        return resultData;
    }

    @SuppressLint("HardwareIds")
    public static ResultData getHeartBeatReq(Context context) {
        ResultData resultData = new ResultData("5101", "获取电子秤信息！", "");
        RequestHeartBeatVM heartReq = new RequestHeartBeatVM();
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                heartReq.setDzc_imei("5102-获取设备信息失败！");
            } else {
                heartReq.setDzc_imei(tm.getSimSerialNumber() + "-dzc_imei");//手机ID
            }

            String imsi = tm.getSubscriberId();//手机卡ID
            if (null == imsi) {
                heartReq.setSim_imsi("5103-手机卡不存在！");
            } else {
                heartReq.setSim_imsi(imsi + "-imsi");
            }
            heartReq.setDevice_dzc_id(CacheHelper.device_id);
            heartReq.setApp_version(BuildConfig.VERSION_NAME);
            heartReq.setInfo(CacheHelper.getConfigMapString() + "&&&&&CPU:" + getCPURateDesc() + "&&&&&RAM:" + getRam(context) + "&&&&&disk:" + getDiskInfo(context, 0));
            heartReq.setCompany_id(CacheHelper.company_id);
            heartReq.setApp_data(NumberUtil.isNumber(CacheHelper.data_version) ? CacheHelper.data_version : "0");
            resultData.setTrueMsg(heartReq);
        } catch (Exception e) {
            resultData.setRetMsg("51044", "获取硬件信息失败！");
        }
        return resultData;
    }


    /**
     * 获取手机存储 ROM 信息
     * <p>
     * type：用于区分内置存储于外置存储的方法
     * <p>
     * 内置SD卡 ：INTERNAL_STORAGE = 0;
     * <p>
     * 外置SD卡：EXTERNAL_STORAGE = 1;
     **/
    public static String getDiskInfo(Context context, int type) {
        String path = getStoragePath(context, type);
        File file = new File(path);
        StatFs statFs = new StatFs(file.getPath());
        String stotageInfo;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            long blockCount = statFs.getBlockCountLong();
            long bloackSize = statFs.getBlockSizeLong();
            long totalSpace = bloackSize * blockCount;
            long availableBlocks = statFs.getAvailableBlocksLong();
            long availableSpace = availableBlocks * bloackSize;
            stotageInfo = "可用/总共："
                    + Formatter.formatFileSize(context, availableSpace) + "/"
                    + Formatter.formatFileSize(context, totalSpace);
            return stotageInfo;
        } else {
            return "SDK too low";
        }


    }


    private static final int INTERNAL_STORAGE = 0;
    private static final int EXTERNAL_STORAGE = 1;

    /**
     * 使用反射方法 获取手机存储路径
     **/
    public static String getStoragePath(Context context, int type) {

        StorageManager sm = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        try {
            Method getPathsMethod = sm.getClass().getMethod("getVolumePaths",
                    null);
            String[] path = (String[]) getPathsMethod.invoke(sm, null);

            switch (type) {
                case INTERNAL_STORAGE:
                    return path[type];
                case EXTERNAL_STORAGE:
                    if (path.length > 1) {
                        return path[type];
                    } else {
                        return null;
                    }

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取 手机 RAM 信息
     */
    public static String getRam(Context context) {
        long totalSize = 0;
        long availableSize = 0;
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(context.ACTIVITY_SERVICE);

        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        totalSize = memoryInfo.totalMem;
        availableSize = memoryInfo.availMem;

        return "可用/总共：" + Formatter.formatFileSize(context, availableSize)
                + "/" + Formatter.formatFileSize(context, totalSize);
    }

    //获取CPU信息
    public static String getCPURateDesc() {
        try {
            String path = "/proc/stat";// 系统CPU信息文件
            long totalJiffies[] = new long[2];
            long totalIdle[] = new long[2];
            int firstCPUNum = 0;//设置这个参数，这要是防止两次读取文件获知的CPU数量不同，导致不能计算。这里统一以第一次的CPU数量为基准
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            Pattern pattern = Pattern.compile(" [0-9]+");
            for (int i = 0; i < 2; i++) {
                totalJiffies[i] = 0;
                totalIdle[i] = 0;
                try {
                    fileReader = new FileReader(path);
                    bufferedReader = new BufferedReader(fileReader, 8192);
                    int currentCPUNum = 0;
                    String str;
                    while ((str = bufferedReader.readLine()) != null && (i == 0 || currentCPUNum < firstCPUNum)) {
                        if (str.toLowerCase().startsWith("cpu")) {
                            currentCPUNum++;
                            int index = 0;
                            Matcher matcher = pattern.matcher(str);
                            while (matcher.find()) {
                                try {
                                    long tempJiffies = Long.parseLong(matcher.group(0).trim());
                                    totalJiffies[i] += tempJiffies;
                                    if (index == 3) {//空闲时间为该行第4条栏目
                                        totalIdle[i] += tempJiffies;
                                    }
                                    index++;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        if (i == 0) {
                            firstCPUNum = currentCPUNum;
                            try {//暂停50毫秒，等待系统更新信息。
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            double rate = -1;
            if (totalJiffies[0] > 0 && totalJiffies[1] > 0 && totalJiffies[0] != totalJiffies[1]) {
                rate = 1.0 * ((totalJiffies[1] - totalIdle[1]) - (totalJiffies[0] - totalIdle[0])) / (totalJiffies[1] - totalJiffies[0]);
            }
            return rate + "";

        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
