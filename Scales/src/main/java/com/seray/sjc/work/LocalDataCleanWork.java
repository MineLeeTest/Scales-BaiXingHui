package com.seray.sjc.work;

import android.content.Context;
import android.support.annotation.NonNull;

import com.seray.sjc.db.AppDatabase;
import com.seray.util.LogUtil;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * @author licheng
 * @since 2019/5/30 13:58
 * 自动删除数据任务
 */
public class LocalDataCleanWork extends Worker {

    public LocalDataCleanWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase instance = AppDatabase.getInstance();
        try {
            instance.beginTransaction();
            instance.autoClean();
            instance.setTransactionSuccessful();
        } catch (Exception e) {
            LogUtil.e("自动清除数据失败！");
        } finally {
            instance.endTransaction();
        }
        return Result.success();
    }
}
