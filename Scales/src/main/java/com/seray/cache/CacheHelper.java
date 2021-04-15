package com.seray.cache;

import android.text.TextUtils;

import com.seray.sjc.api.result.ActivateRsp;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.entity.device.Config;
import com.seray.sjc.entity.device.PayTypeInfo;
import com.seray.sjc.entity.device.TermInfo;
import com.seray.util.NumFormatUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheHelper {

    /**
     * 支持的支付类型
     */
    public static List<PayTypeInfo> PayTypeInfoList = new ArrayList<>();

    /**
     * 流水号排序号
     */
    public static int DATE_ID;

    public static boolean isOpenJin = false;//是否启用斤
    public static boolean isOpenBackDisplay = true;//是否第二屏幕
    public static boolean isChangePrice = true;//是否允许修改单价//默认开启
    public static boolean isOpenForceRecord = false; // 是否开启强制记录功能//默认关闭

    /*设备信息 用于激活验证*/
    public static String CFK_DEVICE_ID = "CFK_DEVICE_ID";// 终端ID_KEY
    public static Integer device_id = 0;// 终端ID

    public static Integer company_id = 1;// 公司ID

    public static String CFK_COMPANY_NAME = "CFK_COMPANY_NAME";// 公司名称KEY
    public static String company_name = "empty";// 公司名称

    public static long HeartBeatTime = 1800;// 心跳包发送时间间隔

    public static void prepareCacheData() {
        //获取缓存中所有的键值对
        Map<String, String> map = getConfigMap();

        //读取配置文件中的数据赋值到内存中
        //加载设备ID
        String deviceID = map.get(CFK_DEVICE_ID);
        if (!TextUtils.isEmpty(deviceID)) {
            if (NumFormatUtil.isNumeric(deviceID)) {
                device_id = Integer.parseInt(deviceID);
            }
        }
        //加载公司名称
        String companyName = map.get(TermInfo.COMPANY_NAME);
        if (!TextUtils.isEmpty(companyName)) {
            company_name = companyName;
        }

    }

    public static boolean isDeviceRegistered() {
        return !device_id.equals(0);
    }

    public static boolean deviceRegistered(ActivateRsp activateRsp) {
        try {
            device_id = activateRsp.device_id;
//            company_name = activateRsp.company_name;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getConfigMapString() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
            List<Config> configs = configDao.loadAll();
            for (Config bean : configs) {
                String configKey = bean.getConfigKey();
                String configValue = bean.getConfigValue();
                stringBuilder.append("[").append(configKey).append("]=[").append(configValue).append("],");
            }
        } catch (Exception e) {
            stringBuilder.append("Exception-->").append(e.getMessage());
        }
        return stringBuilder.toString();
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> data = new HashMap<>();
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        List<Config> configs = configDao.loadAll();
        for (Config bean : configs) {
            String configKey = bean.getConfigKey();
            String configValue = bean.getConfigValue();
            data.put(configKey, configValue);
        }
        return data;
    }
}
