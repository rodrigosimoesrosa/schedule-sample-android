package br.com.mirabilis.schedulesample.api;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.mirabilis.schedulesample.BuildConfig;
/*import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;*/

/**
 * Created by Edenred Mobile Team
 * @author rodrigosimoesrosa
 * rodrigo.rosa@consulting-for.edenred.com
 */
/*public class RetrofitUtil<T> {

    protected Retrofit retrofit;
    protected T service;

    private RetrofitUtil(T service, Retrofit retrofit){
        this.service = service;
        this.retrofit = retrofit;
    }

    public T getService() {
        return service;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public static Retrofit.Builder getRetrofitBuilder(String baseURL){
        return new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create());
    }

    public static OkHttpClient.Builder getOkHttpBuilder(int writeTimeout, int readTimeout){
        return new OkHttpClient.Builder()
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS);
    }

    public static <T> RetrofitUtil<T> build(Class<T> serviceClass, String baseURL, int writeTimeout, int readTimetout) {

        OkHttpClient.Builder clientBuilder = getOkHttpBuilder(writeTimeout,readTimetout);

        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(loggingInterceptor);
        }

        clientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = getRetrofitBuilder(baseURL).client(client).build();
        T service = retrofit.create(serviceClass);
        RetrofitUtil<T> util = new RetrofitUtil(service,retrofit);
        return util;
    }


    public static <T> RetrofitUtil<T> buildWithAutorization(Class<T> serviceClass, String baseURL, int writeTimeout, int readTimetout, final String authorization) {

        OkHttpClient.Builder clientBuilder = getOkHttpBuilder(writeTimeout,readTimetout);

        if(BuildConfig.DEBUG){
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
            clientBuilder.addInterceptor(loggingInterceptor);
        }

        clientBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                Request.Builder requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-Type", "application/json")
                        .header("Authorization", authorization)
                        .method(original.method(), original.body());

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = getRetrofitBuilder(baseURL).client(client).build();
        T service = retrofit.create(serviceClass);
        RetrofitUtil<T> util = new RetrofitUtil(service,retrofit);
        return util;
    }
}
*/
