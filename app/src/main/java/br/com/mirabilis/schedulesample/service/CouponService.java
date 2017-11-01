package br.com.mirabilis.schedulesample.service;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.mirabilis.schedulesample.R;
import br.com.mirabilis.schedulesample.api.CouponUtil;
import br.com.mirabilis.schedulesample.api.model.ExistsPromotion;
import br.com.mirabilis.schedulesample.location.TicketLocation;
import br.com.mirabilis.schedulesample.receiver.CouponAlarmReceiver;
import br.com.mirabilis.schedulesample.view.CouponActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Eduardo-AIS on 19/05/2017.
 * modified by rodrigosimoesrosa
 */
public class CouponService extends IntentService{

    private static String title = "Você tem descontos por perto, %d cupon(os)";

    private static final String SERVICE_NAME = "CouponService";
    private SimpleDateFormat simpleDateFormat;

    private Intent intent;
    private TicketLocation ticketLocation;

    public CouponService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        ticketLocation = new TicketLocation.Builder(this).requireFine(true).build();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        ticketLocation.beginUpdates();
    }

    @Override
    public void onDestroy() {
        if(ticketLocation != null) ticketLocation.endUpdates();
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("Coupon","Service.onHandleIntent: "+ simpleDateFormat.format(new Date()));
        this.intent = intent;
        if (TicketLocation.isProviderOn(this, LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                /**
                 * Capture current location;
                 */
                double lat = ticketLocation.getLatitude();
                double lgt = ticketLocation.getLongitude();

                Log.i("Coupon","Service.checkAvailablePromotionRetrofit19: "+ simpleDateFormat.format(new Date()));
                CouponUtil.checkAvailablePromotionRetrofit19(lat,lgt, new OnCheckAvaiablePromotionRetrofit19());
            } else {
                Log.i("Coupon","Service.location not permission: "+ simpleDateFormat.format(new Date()));
                CouponAlarmReceiver.completeWakefulIntent(intent);
            }
        }else{
            Log.i("Coupon","Service.location is off: "+ simpleDateFormat.format(new Date()));
            CouponAlarmReceiver.completeWakefulIntent(intent);
        }
    }

    private class OnCheckAvaiablePromotionRetrofit19 implements Callback<ExistsPromotion> {
        @Override
        public void success(ExistsPromotion promotion, Response response) {
            if (promotion != null) {
                Log.i("Coupon","Service.recupera cupons: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                if(promotion.getPromotions() > 0){
                    Log.i("Coupon","Service.onSuccess: " + promotion.getPromotions());
                    String titleNotification = String.format(Locale.getDefault(), title, promotion.getPromotions());
                    buildNotification(promotion.getNotificationMessage(), titleNotification, promotion.getPromotions());

                    CouponAlarmReceiver.completeWakefulIntent(intent);
                }else{
                    Log.i("Coupon","Service.onSuccess but without coupon: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                }
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.i("Coupon","Service.withoutSuccess: "+ simpleDateFormat.format(new Date()));
            CouponAlarmReceiver.completeWakefulIntent(intent);
        }
    }

    /*private class OnCheckAvailablePromotion implements Callback<ExistsPromotion> {

        @Override
        public void onResponse(Call<ExistsPromotion> call, Response<ExistsPromotion> response) {
            if(response.isSuccessful()){
                ExistsPromotion promotion = response.body();
                Log.i("Coupon","Service.recupera cupons: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                if(promotion.getPromotions() > 0){
                    Log.i("Coupon","Service.onSuccess: " + promotion.getPromotions());
                    String titleNotification = String.format(Locale.getDefault(), title, response.body().getPromotions());
                    buildNotification(response.body().getNotificationMessage(), titleNotification, response.body().getPromotions());

                    CouponAlarmReceiver.completeWakefulIntent(intent);
                }else{
                    Log.i("Coupon","Service.onSuccess but without coupon: "+ promotion.getPromotions() + " message : " + promotion.getNotificationMessage());
                }
            }else{
                Log.i("Coupon","Service.withoutSuccess: "+ simpleDateFormat.format(new Date()));
                CouponAlarmReceiver.completeWakefulIntent(intent);
            }
        }

        @Override
        public void onFailure(Call<ExistsPromotion> call, Throwable t) {
            Log.i("Coupon","Service.onFailure: "+ simpleDateFormat.format(new Date()));
            CouponAlarmReceiver.completeWakefulIntent(intent);
        }
    }*/

    private void buildNotification(String message, String title, int quantity) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentText(message);
        builder.setContentTitle(title);

        builder.setSmallIcon(R.mipmap.ic_launcher);

        Intent notificationIntent = new Intent(this, CouponActivity.class);
        intent.putExtra(CouponActivity.COUPONS, quantity);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        notificationIntent.putExtra("NOTIFICATION", "FROM_NOTIFICATION");

        PendingIntent intent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        builder.setContentIntent(intent);
        builder.setAutoCancel(true);
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_SOUND;
        notificationManager.notify(9999, notification);
    }
}
