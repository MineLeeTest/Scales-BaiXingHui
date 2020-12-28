package com.seray.sjc.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;
import android.util.Log;

import com.seray.scales.App;
import com.seray.scales.BuildConfig;
import com.seray.sjc.db.dao.CardOrderDao;
import com.seray.sjc.db.dao.ConfigDao;
import com.seray.sjc.db.dao.PayTypeInfoDao;
import com.seray.sjc.db.dao.ScreenResourceDao;
import com.seray.sjc.db.dao.SjcCategoryDao;
import com.seray.sjc.db.dao.SjcDetailDao;
import com.seray.sjc.db.dao.SjcProductDao;
import com.seray.sjc.db.dao.SjcSubtotalDao;
import com.seray.sjc.entity.card.CardPayOrder;
import com.seray.sjc.entity.device.Config;
import com.seray.sjc.entity.device.PayTypeInfo;
import com.seray.sjc.entity.device.ScreenResource;
import com.seray.sjc.entity.order.SjcDetail;
import com.seray.sjc.entity.order.SjcSubtotal;
import com.seray.sjc.entity.product.SjcCategory;
import com.seray.sjc.entity.product.SjcProduct;
import com.seray.util.FileHelp;
import com.seray.util.LogUtil;

import java.io.File;
import java.io.IOException;

/**
 * Author：李程
 * CreateTime：2019/4/13 14:13
 * E-mail：licheng@kedacom.com
 * Describe：世界村数据库
 */
@Database(entities = {SjcCategory.class, SjcProduct.class,
        SjcSubtotal.class, SjcDetail.class,
        ScreenResource.class, PayTypeInfo.class,
        Config.class, CardPayOrder.class},
        version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final Object sLock = new Object();

    private static AppDatabase INSTANCE;

    public static AppDatabase getInstance() {
        synchronized (sLock) {
            if (INSTANCE == null) {
                File databasePath = getDatabasePath();
                if (databasePath == null) {
                    databasePath = new File(BuildConfig.DB_NAME);
                }
                INSTANCE = Room.databaseBuilder(App.getApplication(),
                        AppDatabase.class, databasePath.getAbsolutePath())
                        .addMigrations(MIGRATION_1_2)
                        .allowMainThreadQueries()
                        .build();
            }
            return INSTANCE;
        }
    }

    public abstract SjcCategoryDao getCategoryDao();

    public abstract SjcProductDao getProductDao();

    public abstract SjcSubtotalDao getSubtotalDao();

    public abstract SjcDetailDao getDetailDao();

    public abstract ScreenResourceDao getScreenResourceDao();

    public abstract PayTypeInfoDao getPayTypeDao();

    public abstract ConfigDao getConfigDao();

    public abstract CardOrderDao getCardOrderDao();

    /**
     * 自动清除无用数据
     */
    public void autoClean() {
        CardOrderDao cardOrderDao = getCardOrderDao();
        SjcSubtotalDao subtotalDao = getSubtotalDao();
        cardOrderDao.autoClean();
        subtotalDao.autoClean();
    }

    public void dataReset(DataResetCallback callback) {
        boolean isSuccess = true;
        try {
            // 开启事务
            beginTransaction();
            // 订单清空
            getSubtotalDao().delete();
            // 刷卡消费订单清空
            getCardOrderDao().delete();
            // 商品分类清空
            getCategoryDao().delete();
            // 多媒体资源清空
            getScreenResourceDao().delete();
            // 支付方式配置清空
            getPayTypeDao().delete();
            // 配置清空
            getConfigDao().delete();
            // 事务成功
            setTransactionSuccessful();
        } catch (Exception e) {
            isSuccess = false;
            LogUtil.e("数据重置失败！" + e.getMessage());
        } finally {
            // 事务结束
            endTransaction();
            // 回调
            if (callback != null) {
                callback.dataResetResult(isSuccess);
            }
        }
    }

    /**
     * 获得数据库路径，如果不存在，则创建对象对象
     */
    private static File getDatabasePath() {
        //判断是否存在sd卡
        boolean sdExist = android.os.Environment.MEDIA_MOUNTED.equals(android.os.Environment.getExternalStorageState());
        if (!sdExist) {//如果不存在,
            Log.e("SD卡管理：", "SD卡不存在，请加载SD卡");
            return null;
        } else {//如果存在
            String dbDir = FileHelp.DATABASE_DIR;//数据库所在目录
            String dbPath = FileHelp.DATABASE_DIR + BuildConfig.DB_NAME;//数据库路径
            File dirFile = new File(dbDir);
            if (!dirFile.exists())
                dirFile.mkdirs();
            //数据库文件是否创建成功
            boolean isFileCreateSuccess = false;
            File dbFile = new File(dbPath);
            if (!dbFile.exists()) {
                try {
                    isFileCreateSuccess = dbFile.createNewFile();//创建文件
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                isFileCreateSuccess = true;
            if (isFileCreateSuccess)
                return dbFile;
            else
                return null;
        }
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE " + SjcProduct.TABLE_NAME + " ADD COLUMN " + SjcProduct.COLUMN_GOODS_CODE + " TEXT(255)");
            database.execSQL("ALTER TABLE " + SjcDetail.TABLE_NAME + " ADD COLUMN " + SjcProduct.COLUMN_GOODS_CODE + " TEXT(255)");
        }
    };
}
