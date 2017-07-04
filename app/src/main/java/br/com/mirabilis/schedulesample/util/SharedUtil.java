package br.com.mirabilis.schedulesample.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rodrigosimoesrosa
 */
public class SharedUtil {

    private static final String PREFERENCIAS = "PREFERENCIAS";
    private static final String WAS_NOTIFICATION_TRIGGERED = "WAS_NOTIFICATION_TRIGGERED";

    public static boolean setWasNotificationTriggered(Context context, boolean wasNotificationTriggered) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(WAS_NOTIFICATION_TRIGGERED, wasNotificationTriggered);

        return editor.commit();
    }

    public static boolean isWasNoticationTriggered(Context context){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE);
        return prefs.getBoolean(WAS_NOTIFICATION_TRIGGERED, false);
    }
}
