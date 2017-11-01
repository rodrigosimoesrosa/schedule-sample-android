package br.com.mirabilis.schedulesample.api;

import java.util.Map;

import br.com.mirabilis.schedulesample.api.model.ExistsPromotion;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.QueryMap;
/*import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;*/

/**
 * Created by ticketservices on 7/6/17.
 */
public interface CouponAdapter {

    //Retrofit > 2.2@GET("promotions/exists/")
    @GET("/promotions/exists/")
    void getExistsPromotion(@QueryMap Map<String, Double> queryMap, @Header("x-user-client") String xUserClient, @Header("x-user") String xUser, Callback<ExistsPromotion> callback);
    //Call<ExistsPromotion> getExistsPromotion(@QueryMap Map<String, Double> queryMap, @Header("x-user-client") String xUserClient, @Header("x-user") String xUser);
}
