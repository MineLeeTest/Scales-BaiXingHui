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
    private static final String SERVER_IP = "http://yhyc.exd365.net";
    public static final String URL_YHYC = SERVER_IP + "/yhyc/yhyc_dzc_app/";
    public static final String APP_VERSION = "version.html";
    public static final String APP_NAME = "scales.apk";
    private static final String curBaseUrl = "http://8.136.212.105:8108";

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
            httpRetrofitFactory = new Retrofit.Builder()
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(curBaseUrl)
                    .build();
        }
        return httpRetrofitFactory.create(clazz);
    }

    public static synchronized SjcApi getHttpServiceApi() {
        return createService(SjcApi.class);
    }

}
