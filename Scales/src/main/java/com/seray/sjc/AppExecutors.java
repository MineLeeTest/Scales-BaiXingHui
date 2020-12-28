package com.seray.sjc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author：李程
 * CreateTime：2019/4/13 14:27
 * E-mail：licheng@kedacom.com
 * Describe：
 */
public final class AppExecutors {

    private final ExecutorService mInsertIO;

    private final ExecutorService mQueryIO;

    private static AppExecutors mInstance = null;

    public static AppExecutors getInstance() {
        if (mInstance == null) {
            synchronized (AppExecutors.class) {
                if (mInstance == null) {
                    mInstance = new AppExecutors();
                }
            }
        }
        return mInstance;
    }

    private AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(4));
    }

    private AppExecutors(ExecutorService insertIO, ExecutorService queryIO) {
        this.mInsertIO = insertIO;
        this.mQueryIO = queryIO;
    }

    public ExecutorService insertIO() {
        return mInsertIO;
    }

    public ExecutorService queryIO() {
        return mQueryIO;
    }
}
