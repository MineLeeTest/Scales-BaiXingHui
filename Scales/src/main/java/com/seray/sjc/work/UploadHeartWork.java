package com.seray.sjc.work;

import android.content.Context;
import android.support.annotation.NonNull;

import com.seray.cache.CacheHelper;
import com.seray.sjc.api.net.HttpServicesFactory;
import com.seray.sjc.api.request.TermHeartReq;
import com.seray.sjc.util.SystemUtils;
import com.seray.util.DateUtil;
import com.seray.util.LogUtil;

import java.util.Date;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * 心跳包任务
 */
public class UploadHeartWork extends Worker {

    private Context context;

    public UploadHeartWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            TermHeartReq termHeartReq = new TermHeartReq();

            HttpServicesFactory.getHttpServiceApi()
                    .heart_beat(termHeartReq)
                    .execute();

        } catch (Exception e) {
            LogUtil.e("心跳包失败" + e.getMessage());
            return Result.failure();
        }
        return Result.success();
    }
}
