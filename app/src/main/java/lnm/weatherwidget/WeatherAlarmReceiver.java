package lnm.weatherwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class WeatherAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE_CURRENT = 12345;
    public static final int REQUEST_CODE_FORECAST = 23456;
    public static final int REQUEST_CODE_LONG_FORECAST = 34567;

    public static final String ACTION = "com.codepath.example.servicesdemo.alarm";
//    private static final String ACTION_FETCH_CURRENT_WEATHER_DATA = "lnm.androidminer.action.FETCH_WEATHER_DATA";
//    private static final String ACTION_FETCH_FORECAST_WEATHER_DATA = "lnm.androidminer.action.FETCH_FORECAST_WEATHER_DATA";
//    private static final String ACTION_FETCH_LONG_FORECAST_WEATHER_DATA = "lnm.androidminer.action.FETCH_LONG_FORECAST_WEATHER_DATA";


    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
        switch (action){
            case "current":
                System.out.println(">:"+action);

                Intent i = new Intent(context, WeatherWidgetUpdateService.class);
                //   i.putExtra("foo", "bar");
                i.setAction(WeatherWidgetUpdateService.ACTION_FETCH_WEATHER_DATA);
                context.startService(i);
                break;
            case "forecast":
                System.out.println(">:2"+action);

                Intent i2 = new Intent(context, WeatherWidgetUpdateService.class);
                //   i.putExtra("foo", "bar");
                i2.setAction(WeatherWidgetUpdateService.ACTION_FETCH_FORECAST_WEATHER_DATA);
                context.startService(i2);
                break;
            case "long_forecast":
                System.out.println(">:3"+action);

                Intent i3 = new Intent(context, WeatherWidgetUpdateService.class);
                //   i.putExtra("foo", "bar");
                i3.setAction(WeatherWidgetUpdateService.ACTION_FETCH_LONG_FORECAST_WEATHER_DATA);
                context.startService(i3);
        }

    }
}
