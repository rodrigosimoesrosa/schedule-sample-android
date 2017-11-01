package br.com.mirabilis.schedulesample.api;

import java.util.HashMap;
import java.util.Map;

import br.com.mirabilis.schedulesample.BuildConfig;
import br.com.mirabilis.schedulesample.api.model.ExistsPromotion;
import br.com.mirabilis.schedulesample.util.HashUtil;
import retrofit.Callback;

/**
 * Created by ticketservices on 7/6/17.
 */

public class CouponUtil {

    private static final String EMAIL = "rodrigosimoesrosa@gmail.com";
    private static final String TOKEN = "Bearer " +  "02bf5ed5f3b37a27ec9edd3e78f1264";

    /*public static void checkAvailablePromotionRetrofit19(double lat, double lgt, Callback<ExistsPromotion> callback) {
        Map<String, Double> queryMap = new HashMap<>();

        queryMap.put("latitude", lat);
        queryMap.put("longitude", lgt);

        RetrofitUtil<CouponAdapter> service = RetrofitUtil.buildWithAutorization(CouponAdapter.class, BuildConfig.REST_CUPOM_URL, TIME_OUT, TIME_OUT, TOKEN);
        CouponAdapter adapter = service.getService();
        Call<ExistsPromotion> call = adapter.getExistsPromotion(queryMap, "user-app", HashUtil.doHash(EMAIL));
        call.enqueue(callback);
    }*/

    public static void checkAvailablePromotionRetrofit19(double lat, double lgt, Callback<ExistsPromotion> callback) {
        Map<String, Double> queryMap = new HashMap<>();

        queryMap.put("latitude", lat);
        queryMap.put("longitude", lgt);

        CouponAdapter api = ServiceGenerator.createService(CouponAdapter.class, BuildConfig.REST_CUPOM_URL, TOKEN);
        api.getExistsPromotion(queryMap, "user-app", HashUtil.doHash(EMAIL), callback);
    }
}
