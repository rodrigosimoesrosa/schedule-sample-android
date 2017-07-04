package br.com.mirabilis.schedulesample.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import br.com.mirabilis.schedulesample.schedule.model.Period;
import br.com.mirabilis.schedulesample.util.SharedUtil;
import br.com.mirabilis.schedulesample.schedule.CouponScheduleManager;

/**
 * Created by rodrigosimoesrosa
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(br.com.mirabilis.schedulesample.R.layout.activity_main);

        if(!SharedUtil.isWasNoticationTriggered(this)){
            Log.i("Coupon","scheduleAlarms - starting ...");
            CouponScheduleManager.scheduleAlarms(getApplicationContext(),
                    new int[]{Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY },
                    Period.MORNING, Period.AFTERNOON, Period.NIGHT);
        }
    }
}
