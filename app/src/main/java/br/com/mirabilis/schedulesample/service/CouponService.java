package br.com.mirabilis.schedulesample.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.com.mirabilis.schedulesample.receiver.CouponAlarmReceiver;
import br.com.mirabilis.schedulesample.view.MainActivity;

/**
 * Created by rodrigosimoesrosa
 */
public class CouponService extends IntentService{

    private static String title = "Você tem descontos por perto, %d cupon(os)";
    private static final String SERVICE_NAME = "CouponService";
    private Intent intent;

    public CouponService() {
        super(SERVICE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Log.i("Coupon", "Service.realizaBuscaDeCupons: " + simpleDateFormat.format(Calendar.getInstance().getTime()));
        buildNotification("Existem cupons por perto " + simpleDateFormat.format(Calendar.getInstance().getTime()), "Cupons ");
        CouponAlarmReceiver.completeWakefulIntent(intent);
    }

    private void buildNotification(String message, String title) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        builder.setContentText(message);
        builder.setContentTitle(title);

        builder.setSmallIcon(br.com.mirabilis.schedulesample.R.mipmap.ic_launcher);

        Intent notificationIntent = new Intent(this, MainActivity.class);

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
