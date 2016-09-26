package lnm.weatherwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nikos on 24-Sep-16.
 */
public class BootReceiver extends BroadcastReceiver {

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Start!!!2");

        if (WeatherWidget.getNumberOfWidgets(context) > 0) {
            // ensure service is running
            context.startService(new Intent(context, WeatherWidgetUpdateService.class));
        }
    }
}