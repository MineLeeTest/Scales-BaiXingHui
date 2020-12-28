package com.seray.cache;

import android.text.TextUtils;

import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.entity.device.Config;
import com.seray.util.NumFormatUtil;

/**
 * 秤系统基本配置类
 */
public class AppConfig {

    /**
     * 是否是生产环境
     * 发布前注意修改 LEVEL = NOTHING{@link com.seray.util.LogUtil }
     */
    public static final boolean isDeploy = true;

    /**
     * 秤系统的类型
     */
    public static ScaleType SCALE_TYPE = getScaleTypeFromDb();

    /**
     * 是否是地秤
     */
    public static boolean isT200() {
        return SCALE_TYPE == ScaleType.T200;
    }

    /**
     * 是否是桌秤
     */
    public static boolean isSR200() {
        return SCALE_TYPE == ScaleType.SR200;
    }

    /**
     * 从数据库中取出秤类型
     *
     * @return 默认类型：T200
     */
    private static ScaleType getScaleTypeFromDb() {
        ScaleType st = ScaleType.SR200;
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        String result = configDao.get("ScaleType");
        if (TextUtils.isEmpty(result)) {
            return st;
        } else if (NumFormatUtil.isNumeric(result)) {
            int t = Integer.parseInt(result);
            if (t == 1) {
                st = ScaleType.T200;
            }
        } else {
            return st;
        }
        return st;
    }

    /**
     * 将秤类型保存至数据库
     */
    public static void saveScaleTypeToDb() {
        String value;
        if (SCALE_TYPE == ScaleType.T200) {
            value = "1";
        } else if (SCALE_TYPE == ScaleType.SR200) {
            value = "2";
        } else {// 默认值SR200
            value = "2";
        }
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        Config config = new Config("ScaleType", value);
        configDao.save(config);
    }

    /**
     * T200地秤 SR200桌秤
     */
    public enum ScaleType {
        T200, SR200
    }
}
