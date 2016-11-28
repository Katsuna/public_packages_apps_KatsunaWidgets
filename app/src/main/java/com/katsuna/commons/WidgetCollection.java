package com.katsuna.commons;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.katsuna.R;
import com.katsuna.batterywidget.BatteryMonitorService;
import com.katsuna.batterywidget.BatteryUpdateService;
import com.katsuna.clockwidget.ClockWidget;
import com.katsuna.clockwidget.ClockMonitorService;
import com.katsuna.clockwidget.ClockUpdateService;
import com.katsuna.weatherDb.Weather;
import com.katsuna.weatherDb.WeatherContentProvider;
import com.katsuna.weatherDb.WeatherDbHandler;
import com.katsuna.weatherParser.JSONWeatherParser;
import com.katsuna.weatherwidget.AlarmReceiver;
import com.katsuna.weatherwidget.WeatherWidget;

import java.util.Calendar;

public class WidgetCollection extends AppWidgetProvider {

    private PendingIntent service = null;
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";
    private static final String DEBUG_TAG = "onClicked";
    private boolean extended = false;
    public static WeatherDbHandler wDBHandler;
    public static WeatherContentProvider weatherContentProvider;
    public static String ACTION_MENU_CLICKED = "MenuClicked";


    public static int getNumberOfWidgets(final Context context) {
        ComponentName componentName = new ComponentName(context, ClockWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] activeWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        if (activeWidgetIds != null) {
            return activeWidgetIds.length;
        } else {
            return 0;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("Clock widget","I am n update...");
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        // CLOCK WIDGET UPDATE
        // ensure service is running
        context.startService(new Intent(context, ClockMonitorService.class));
        // update the widgets
        Intent updateIntent = new Intent(context, ClockUpdateService.class);
        updateIntent.setAction(ClockUpdateService.ACTION_WIDGET_UPDATE);
        updateIntent.putExtra(ClockUpdateService.EXTRA_WIDGET_IDS, appWidgetIds);
        context.startService(updateIntent);


        // BATTERY WIDGET UPDATE
        context.startService(new Intent(context, BatteryMonitorService.class));
        // update the widgets
        Intent updateBatteryIntent = new Intent(context, BatteryUpdateService.class);
        updateBatteryIntent.setAction(BatteryUpdateService.ACTION_WIDGET_UPDATE);
        updateBatteryIntent.putExtra(BatteryUpdateService.EXTRA_WIDGET_IDS, appWidgetIds);
        context.startService(updateBatteryIntent);

        // WEATHER WIDGET UPDATE
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews;
            ComponentName watchWidget;

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.setAction(ACTION_MENU_CLICKED);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, widgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget_view);
            watchWidget = new ComponentName(context, WeatherWidget.class);

            remoteViews.setOnClickPendingIntent(R.id.widgetRoot, getPendingSelfIntent(context, SYNC_CLICKED));

//
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            Weather widgetWeather = new Weather();
            if (!sp.getString("lastToday", "").equals("")) {
                System.out.println("im called");
                widgetWeather = JSONWeatherParser.parseWidgetJson(sp.getString("lastToday", ""), context);
            } else {
                System.out.println("im called 2");
                try {
                    pendingIntent2.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                return;
            }
////            remoteViews.setTextViewText(R.id.widgetCity, widgetWeather.getCity() + ", " + widgetWeather.getCountry());
            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription());
            remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
//            remoteViews.setTextViewText(R.id.widgetPressure, widgetWeather.getPressure());
//            remoteViews.setTextViewText(R.id.widgetHumidity, context.getString(R.string.humidity) + ": " + widgetWeather.getHumidity() + " %");
            //remoteViews.setTextViewText(R.id.widgetLastUpdate, widgetWeather.getLastUpdated());
            remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
        }

    }

    @Override
    public void onEnabled(Context context) {

        super.onEnabled(context);
        context.startService(new Intent(context, ClockMonitorService.class));
        context.startService(new Intent(context, BatteryMonitorService.class));

    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        super.onDeleted(context, widgetIds);
        if (getNumberOfWidgets(context) == 0) {
            // stop monitoring if there are no more widgets on screen
            context.stopService(new Intent(context, ClockMonitorService.class));
            context.stopService(new Intent(context, BatteryMonitorService.class));

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        System.out.println("yeahhhhhhhhhh"+ intent.getAction());
        if (SYNC_CLICKED.equals(intent.getAction())) {


            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            RemoteViews remoteViews;
            ComponentName watchWidget;

            remoteViews = new RemoteViews(context.getPackageName(), R.layout.extended_widget_view);
//            remoteViews.setViewVisibility(R.id.widgetRoot,View.INVISIBLE);
//            remoteViews.setViewVisibility(R.id.extendedRoot,View.VISIBLE);
            watchWidget = new ComponentName(context, WeatherWidget.class);

            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private int getWeatherIconId(String actualId, int hourOfDay, Context context) {

        int icon = 0;
        if (actualId.equals(context.getString(R.string.weather_sunny))) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = R.drawable.k48_sun;
            } else {
                icon = R.drawable.k48_sun;
            }
        } else if (actualId.equals(context.getString(R.string.weather_thunder))) {
            icon = R.drawable.k48_heavyrain;

        }
        else if (actualId.equals(context.getString(R.string.weather_drizzle))) {
            icon = R.drawable.k48_light_rain;

        }
        else if(actualId.equals(context.getString(R.string.weather_foggy))){
            icon = R.drawable.k48_fog;

        }
        else if(actualId.equals(context.getString(R.string.weather_cloudy))){
            icon = R.drawable.k48_clouds;

        }
        else if(actualId.equals(context.getString(R.string.weather_snowy))){
            icon = R.drawable.k48_heavysnow;

        }
        else if(actualId.equals(context.getString(R.string.weather_rainy))){
            icon = R.drawable.k48_heavyrain;

        }

        return icon;
    }
}
