package br.com.mirabilis.schedulesample.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import br.com.mirabilis.schedulesample.BuildConfig;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class ServiceGenerator {

    public static <S> S createService(Class<S> serviceClass, String baseUrl,
                                      final String token) {

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        OkHttpClient okHttpClient = new OkHttpClient();


        okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        RestAdapter.LogLevel logLevel = BuildConfig.DEBUG ? RestAdapter.LogLevel.NONE : RestAdapter.LogLevel.FULL;

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(baseUrl)
                .setConverter(new GsonConverter(gson))
                .setClient(new OkClient(okHttpClient))
                .setErrorHandler(new CustomErrorHandler())
                .setLogLevel(logLevel).setLog(new AndroidLog("RETROFIT"))
                .setRequestInterceptor(new RequestInterceptor() {

                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Authorization", "Bearer " + token);
                    }

                });

        RestAdapter adapter = builder.build();

        return adapter.create(serviceClass);

    }

}