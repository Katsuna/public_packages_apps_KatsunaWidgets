package com.katsuna.commons;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import com.katsuna.R;
import com.katsuna.batterywidget.BatteryMonitorService;
import com.katsuna.batterywidget.BatteryUpdateService;
import com.katsuna.clockwidget.ClockWidget;
import com.katsuna.clockwidget.ClockMonitorService;
import com.katsuna.clockwidget.ClockUpdateService;
import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.Shape;
import com.katsuna.weatherDb.WeatherContentProvider;
import com.katsuna.weatherDb.WeatherDbHandler;
import com.katsuna.weatherwidget.AlarmReceiver;
import com.katsuna.weatherwidget.WeatherMonitorService;
import com.katsuna.weatherwidget.WeatherUpdateService;

import java.util.Calendar;

public class WidgetCollection extends AppWidgetProvider {

    public static final String BACK_CLICKED = "backClicked";
    public static final String ENERGY_MODE_CLICKED ="energy_mode";
    public static final String WEEK_CLICKED = "weekCLicked";
    public static final String DAY_CLICKED = "dayClicked";
    private PendingIntent service = null;
    public static final String WEATHER_CLICKED    = "automaticWidgetSyncButtonClick";
    public static final String VIEW_WEATHER_CLICKED    = "automaticWidgetForecastButtonClick";
    public static final String TIME_CLICKED    = "automaticWidgetSyncTimeClick";
    public static final String VIEW_CALENDAR_CLICKED    = "automaticWidgetCalendarButtonClick";
    public static final String BATTERY_CLICKED    = "automaticWidgetSyncBatteryClick";


    private static final String DEBUG_TAG = "onClicked";
    public static boolean extended = false;
    public static WeatherDbHandler wDBHandler;
    public static WeatherContentProvider weatherContentProvider;
    public static String ACTION_MENU_CLICKED = "MenuClicked";
    private String actionClicked ="backClicked";



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
        Log.d("Collection widget","I am n update...");

        for (int id : appWidgetIds) {


            if (extended == false) {
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
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        intent.setAction(ACTION_MENU_CLICKED);
//        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, appWidgetIds[0], intent, PendingIntent.FLAG_UPDATE_CURRENT);


                context.startService(new Intent(context, WeatherMonitorService.class));
                Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
                updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
                updateWeatherIntent.putExtra(WeatherUpdateService.WIDGET_IDS, appWidgetIds);
                context.startService(updateWeatherIntent);
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        System.out.println("On enabled called!");
        super.onEnabled(context);



        context.startService(new Intent(context, ClockMonitorService.class));
        context.startService(new Intent(context, BatteryMonitorService.class));
        context.startService(new Intent(context, WeatherMonitorService.class));


    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        super.onDeleted(context, widgetIds);
        System.out.println("On remove called");

        if (getNumberOfWidgets(context) == 0) {
            // stop monitoring if there are no more widgets on screen
            context.stopService(new Intent(context, ClockMonitorService.class));
            context.stopService(new Intent(context, BatteryMonitorService.class));
            context.stopService(new Intent(context, WeatherMonitorService.class));

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        System.out.println("on Receive widget:"+ intent.getAction());
        if (TIME_CLICKED.equals(intent.getAction())) {
//            RemoteViews rv = new RemoteViews(context.getPackageName(),
//                    R.layout.collection_widget);
//            rv.showNext(R.id.clock_flipper);
//            AppWidgetManager.getInstance(context).partiallyUpdateAppWidget(
//                    intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                    AppWidgetManager.INVALID_APPWIDGET_ID), rv);
//            extended = true;
            Intent updateClockIntent = new Intent(context, ClockUpdateService.class);
            updateClockIntent.setAction(ClockUpdateService.ACTION_WIDGET_CLOCK_CHOICE);
            context.startService(updateClockIntent);
        }
        else if(BATTERY_CLICKED.equals(intent.getAction())){
            Intent updateBatteryIntent = new Intent(context, BatteryUpdateService.class);
            updateBatteryIntent.setAction(BatteryUpdateService.ACTION_BATTERY_CHOICE);
            context.startService(updateBatteryIntent);
        }
        else if (WEATHER_CLICKED.equals(intent.getAction())) {
            extended = true;
            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_WEATHER_CHOICE);
            context.startService(updateWeatherIntent);

        }
        else if (VIEW_WEATHER_CLICKED.equals(intent.getAction())) {
            extended = true;
            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_EXTENDED_NOW);
            context.startService(updateWeatherIntent);
        }
        else if (BACK_CLICKED.equals(intent.getAction())){
            extended = false;
            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_EXTENDED_BACK);
            context.startService(updateWeatherIntent);
        }
        else if (WEEK_CLICKED.equals(intent.getAction())){
            extended = true;
            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_EXTENDED_WEEK);
            context.startService(updateWeatherIntent);
        }
        else if (DAY_CLICKED.equals(intent.getAction())){
            extended = true;
            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_EXTENDED_DAY);
            context.startService(updateWeatherIntent);
        }

    }


//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//
//            RemoteViews remoteViews;
//            ComponentName watchWidget;
//
//            remoteViews = new RemoteViews(context.getPackageName(), R.layout.extended_widget_view);
////            remoteViews.setViewVisibility(R.id.widgetRoot,View.INVISIBLE);
////            remoteViews.setViewVisibility(R.id.extendedRoot,View.VISIBLE);
//            watchWidget = new ComponentName(context, WidgetCollection.class);
//
//            appWidgetManager.updateAppWidget(watchWidget, remoteViews);

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


    private int getTheme(ColorProfile profile) {
        int theme = R.style.CommonAppTheme;
        if (profile == ColorProfile.CONTRAST ||
                profile == ColorProfile.COLOR_IMPAIRMENT_AND_CONTRAST) {
            theme = R.style.CommonAppThemeContrast;
        }
        return theme;
    }




}
