package com.seray.cache;

import android.text.TextUtils;

import com.seray.sjc.api.result.DeviceRegisterDTO;
import com.seray.sjc.api.result.HeartBeatDeviceDzcDTO;
import com.seray.sjc.api.result.ProductDZCDTO;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.db.dao.ProductDao;
import com.seray.sjc.entity.device.ConfigADB;
import com.seray.sjc.entity.device.ProductADB;
import com.seray.util.LogUtil;
import com.seray.util.NumFormatUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.json.JSONUtil;
import lombok.NonNull;

public class CacheHelper {

    /**
     * 流水号排序号
     */
    public static int DATE_ID;

    public static boolean isOpenJin = false;//是否启用斤
    public static boolean isOpenBackDisplay = true;//是否第二屏幕
    public static boolean isChangePrice = true;//是否允许修改单价//默认开启
    public static boolean isOpenForceRecord = false; // 是否开启强制记录功能//默认关闭
    public static Integer company_id = 1;// 公司ID


    /*设备信息 用于激活验证*/
    public static String CFK_DEVICE_ID = "CFK_DEVICE_ID";// 终端ID_KEY
    public static Integer device_id = 0;// 终端ID

    public static String CFK_DEVICE_AES_KEY = "CFK_DEVICE_AES_KEY";// 终端ID_KEY
    public static String device_aes_key = "empty";// 终端密钥

    public static String CFK_COMPANY_NAME = "CFK_COMPANY_NAME";// 公司名称KEY
    public static String company_name = "empty";// 公司名称

    public static String CFK_DATA_VERSION = "CFK_DATA_VERSION";// APP版本
    public static String data_version = "empty";// APP版本


    public static long HeartBeatTime = 30;// 秒钟心跳包发送时间间隔

    public static void prepareCacheData() {
        //获取缓存中所有的键值对
        //读取配置文件中的数据赋值到内存中
        Map<String, String> map = getConfigMap();

        //加载设备ID
        String deviceID = map.get(CFK_DEVICE_ID);
        System.out.println("--deviceID------------>" + deviceID);
        if (!TextUtils.isEmpty(deviceID)) {
            if (NumFormatUtil.isNumeric(deviceID)) {
                device_id = Integer.parseInt(deviceID);
            }
        }
        //加载公司名称
        String companyName = map.get(CFK_COMPANY_NAME);
        if (!TextUtils.isEmpty(companyName)) {
            company_name = companyName;
        }

        //加载数据版本
        String dataVersion = map.get(CFK_DATA_VERSION);
        if (!TextUtils.isEmpty(dataVersion)) {
            data_version = dataVersion;
        }
    }


    public static boolean isDeviceRegistered() {
        System.out.println("---isDeviceRegistered---device_id----------->" + device_id);
        return !device_id.equals(0);
    }

    //更新心跳数据
    public static boolean updateHeartBeat(HeartBeatDeviceDzcDTO heartBeatDeviceDzcDTO) {
        if (!saveData(CFK_DATA_VERSION, heartBeatDeviceDzcDTO.getData_version() + "")) {
            return false;
        }
        data_version = heartBeatDeviceDzcDTO.getData_version();
        return true;
    }
    //更新商品列表
    public static boolean updatePros(@NonNull List<ProductDZCDTO> productDZCDTOS) {
        try {
            LogUtil.i("-----执行商品更新-----");
            LogUtil.i("-----获取服务端商品列表个数【" + productDZCDTOS.size() + "】-----");
            ProductDao productDao = AppDatabase.getInstance().getProductDao();
            List<ProductADB> list = productDao.loadAll();
            LogUtil.i("-----本地原有商品列表个数【" + list.size() + "】-----");
            //清空原有商品列表
            productDao.delete();
            LogUtil.i("-----清空本地商品数据-----");

            if (productDZCDTOS.size() > 0) {
                for (ProductDZCDTO productDZCDTO : productDZCDTOS) {
                    productDao.save(new ProductADB(productDZCDTO));
                }
            }
            return true;
        } catch (Exception e) {
            LogUtil.e("-updatePros()--Exception->", e.getMessage());
            return false;
        }
    }

    public static List<ProductADB> getPros() {
        ProductDao productDao = AppDatabase.getInstance().getProductDao();
        return productDao.loadAll();

    }



    public static boolean deviceRegistered(DeviceRegisterDTO deviceRegisterDTO) {
        try {
            if (!saveData(CFK_DEVICE_ID, deviceRegisterDTO.getDevice_dzc_id() + "")) {
                return false;
            }
            device_id = deviceRegisterDTO.getDevice_dzc_id();


            if (!saveData(CFK_DEVICE_AES_KEY, deviceRegisterDTO.getUse_aes_key())) {
                return false;
            }
            device_aes_key = deviceRegisterDTO.getUse_aes_key();

            if (!saveData(CFK_COMPANY_NAME, deviceRegisterDTO.getCompany_name())) {
                return false;
            }
            company_name = deviceRegisterDTO.getCompany_name();
            return true;
        } catch (Exception e) {
            System.out.println("-----deviceRegistered----Exception---->" + e.getMessage());
            return false;
        }
    }

    private static boolean saveData(String key, String value) {
        try {
            ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
            ConfigADB configADB = new ConfigADB();
            configADB.setConfigKey(key);
            configADB.setConfigValue(value);
            configDao.save(configADB);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getConfigMapString() {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
            List<ConfigADB> configADBS = configDao.loadAll();
            for (ConfigADB bean : configADBS) {
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
        List<ConfigADB> configADBS = configDao.loadAll();
        for (ConfigADB bean : configADBS) {
            String configKey = bean.getConfigKey();
            String configValue = bean.getConfigValue();
            data.put(configKey, configValue);
        }
        return data;
    }
}
