package br.com.mirabilis.schedulesample.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.text.SimpleDateFormat;

import br.com.mirabilis.schedulesample.service.CouponService;
import br.com.mirabilis.schedulesample.schedule.model.Period;

/**
 * Created by rodrigosimoesrosa
 */
public class CouponAlarmReceiver extends WakefulBroadcastReceiver {

    private static final String ID = "ID";
    private static final String ALARM = "ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Coupon","buildAlarm : "+ intent.getStringExtra(ALARM) + " intent : " + intent.getIntExtra(ID, 0));

        Intent service = new Intent(context, CouponService.class);
        startWakefulService(context, service);
    }

    /**
     * Build Alarm
     * @param context
     * @param period
     * @param intent
     * @param timeInMillis
     */
    public static void buildAlarm(Context context, int id, Period period, Intent intent, long timeInMillis){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Log.i("Coupon","buildAlarm : "+ simpleDateFormat.format(timeInMillis));
        int requestCode = id + period.getId();
        intent.putExtra(ID, requestCode);
        intent.putExtra(ALARM, simpleDateFormat.format(timeInMillis));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode , intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, timeInMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }
}
