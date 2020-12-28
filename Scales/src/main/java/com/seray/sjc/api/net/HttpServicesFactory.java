package com.seray.sjc.api.net;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seray.scales.BuildConfig;
import com.seray.sjc.SjcConfig;
import com.seray.sjc.api.SjcApi;
import com.seray.sjc.db.AppDatabase;
import com.seray.sjc.db.dao.ConfigDao;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HttpServicesFactory {

    private static Retrofit httpRetrofitFactory;

    static {

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                .create();

        //获取设置的baseurl
        ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
        String webIp = configDao.get(SjcConfig.WEB_IP);
        String webPort = configDao.get(SjcConfig.WEB_PORT);
        String curBaseUrl = BuildConfig.HTTP_BASE_URL;
        if (!TextUtils.isEmpty(webIp)) {
            if (TextUtils.isEmpty(webPort)) {
                curBaseUrl = "http://" + webIp + "/";
            } else {
                curBaseUrl = "http://" + webIp + ":" + webPort + "/";
            }
        }
        httpRetrofitFactory = new Retrofit.Builder()
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(curBaseUrl)
                .build();
    }

    private static <T> T createService(@NonNull final Class<T> clazz) {
        if (httpRetrofitFactory == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                    .create();

            //获取设置的baseurl
            ConfigDao configDao = AppDatabase.getInstance().getConfigDao();
            String webIp = configDao.get(SjcConfig.WEB_IP);
            String webPort = configDao.get(SjcConfig.WEB_PORT);
            String curBaseUrl = BuildConfig.HTTP_BASE_URL;
            if (!TextUtils.isEmpty(webIp)) {
                if (TextUtils.isEmpty(webPort)) {
                    curBaseUrl = "http://" + webIp + "/";
                } else {
                    curBaseUrl = "http://" + webIp + ":" + webPort + "/";
                }
            }
            httpRetrofitFactory = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(curBaseUrl)
                    .build();
        }
        return httpRetrofitFactory.create(clazz);
    }


    public static void clearHttpFactory() {
        httpRetrofitFactory = null;
    }

    public static synchronized SjcApi getHttpServiceApi() {

        return createService(SjcApi.class);
    }


}
