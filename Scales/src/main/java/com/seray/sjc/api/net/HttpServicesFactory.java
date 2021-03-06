package com.seray.sjc.api.net;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.seray.sjc.api.SjcApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HttpServicesFactory {

    private static Retrofit httpRetrofitFactory;

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
            String curBaseUrl = "http://8.136.212.105:8108";
//            String curBaseUrl = "http://192.168.5.8:8108";
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

    public static synchronized SjcApi getHttpServiceApiSgin() {
        return createService(SjcApi.class);

    }
}
