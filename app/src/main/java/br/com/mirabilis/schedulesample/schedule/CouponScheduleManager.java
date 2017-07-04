package br.com.mirabilis.schedulesample.schedule;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

import br.com.mirabilis.schedulesample.receiver.CouponAlarmReceiver;
import br.com.mirabilis.schedulesample.schedule.model.Period;
import br.com.mirabilis.schedulesample.schedule.model.PeriodOfTime;
import br.com.mirabilis.schedulesample.util.SharedUtil;

/**
 * Created by rodrigosimoesrosa
 */
public class CouponScheduleManager {

    private static void buildTimeToSchedule(Context context, int day, Period period) {
        Intent intent = new Intent(context, CouponAlarmReceiver.class);

        Calendar schedule = Calendar.getInstance();
        schedule.set(Calendar.DAY_OF_WEEK, day);
        PeriodOfTime periodOfTime = PeriodOfTime.getByPeriod(period);

        schedule.set(Calendar.HOUR_OF_DAY, periodOfTime.getHour());
        schedule.set(Calendar.MINUTE, periodOfTime.getMinute());
        schedule.set(Calendar.SECOND, periodOfTime.getSecond());

        Calendar now = Calendar.getInstance();

        if(now.compareTo(schedule) == 1){
            int currentWeek = schedule.get(Calendar.WEEK_OF_MONTH);
            currentWeek ++;
            schedule.set(Calendar.WEEK_OF_MONTH, currentWeek);
        }

        CouponAlarmReceiver.buildAlarm(context, day, period, intent, schedule.getTimeInMillis());
    }

    private static void scheduleDayOfWeekAlarm(Context context, int day, Period[] periods){
        for(Period period : periods){
            buildTimeToSchedule(context, day, period);
        }
    }

    public static void scheduleAlarms(Context context, int [] daysOfWeek, Period ... periods) {
        for(int day : daysOfWeek){
            scheduleDayOfWeekAlarm(context, day, periods);
        }
        Log.i("Coupon","scheduleAlarms - finishing ...");
        SharedUtil.setWasNotificationTriggered(context, true);
    }
}
