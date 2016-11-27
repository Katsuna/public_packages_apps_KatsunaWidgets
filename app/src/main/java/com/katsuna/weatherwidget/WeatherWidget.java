package com.katsuna.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;

import lnm.weatherwidget.R;
import com.katsuna.weatherDb.Weather;
import com.katsuna.weatherDb.WeatherContentProvider;
import com.katsuna.weatherDb.WeatherDbHandler;
import com.katsuna.weatherParser.JSONWeatherParser;

public class WeatherWidget extends AppWidgetProvider {
    private static final String SYNC_CLICKED    = "automaticWidgetSyncButtonClick";
    private static final String DEBUG_TAG = "onClicked";
    private boolean extended = false;
    public static WeatherDbHandler wDBHandler;
    public static WeatherContentProvider weatherContentProvider;
    public static String ACTION_MENU_CLICKED = "MenuClicked";



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

//        if(WidgetVersion.instance().isStandalone()){
//            System.out.println("I'm in the stand");
//            wDBHandler.getCurrentWeather();
//
//        }
//        else{
//            // if AI_Miner is running (standalone)
////            weatherContentProvider.getCurrentWeatherFromContentProvider(context);
//        }
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


//    public static int getNumberOfWidgets(final Context context) {
//        ComponentName componentName = new ComponentName(context, WeatherWidget.class);
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        int[] activeWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
//        if (activeWidgetIds != null) {
//            return activeWidgetIds.length;
//        } else {
//            return 0;
//        }
//    }
//
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

//
//    public static void updateWidgets(Context context, Class widgetClass) {
//        System.out.println("im pressed");
//        Intent intent = new Intent(context.getApplicationContext(), widgetClass)
//                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext())
//                .getAppWidgetIds(new ComponentName(context.getApplicationContext(), widgetClass));
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
//        context.getApplicationContext().sendBroadcast(intent);
//    }
//
    public boolean isStandaloneWidget(Context context){
        for (PackageInfo pack : context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    Log.d("Example", "provider: " + provider.authority);
                    if(provider.authority.equals("daemon_miner_app.sqlite.com.katsuna.weatherDb.WeatherContentProvider")){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    @Override
    public void onEnabled(Context context) {
        System.out.println("I'm here1");
        WidgetVersion.instance().setIsStandalone(isStandaloneWidget(context));
        if(WidgetVersion.instance().isStandalone()){
            wDBHandler = new WeatherDbHandler(context);


        }
        else{
            weatherContentProvider = new WeatherContentProvider();
        }

        super.onEnabled(context);
        context.startService(new Intent(context, WeatherWidgetUpdateService.class));
    }

}
