package br.com.mirabilis.schedulesample.schedule.model;

import java.util.Calendar;

/**
 * Created by rodrigosimoesrosa
 */
public class PeriodOfTime{

    private int hour;
    private int minute;
    private int second;

    public PeriodOfTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }

    public static PeriodOfTime buildMorning() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 35);
        calendar.set(Calendar.SECOND, 30);
        return new PeriodOfTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public static PeriodOfTime buildAfternoon() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 37);
        calendar.set(Calendar.SECOND, 30);
        return new PeriodOfTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public static PeriodOfTime buildNight() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 39);
        calendar.set(Calendar.SECOND, 30);
        return new PeriodOfTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    public static PeriodOfTime getByPeriod(Period period) {
        switch (period){
            case MORNING:
                return PeriodOfTime.buildMorning();

            case AFTERNOON:
                return PeriodOfTime.buildAfternoon();

            case NIGHT:
                return PeriodOfTime.buildNight();
        }
        return PeriodOfTime.buildMorning();
    }

}