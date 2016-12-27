package com.katsuna.weatherwidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.katsuna.R;
import com.katsuna.batterywidget.BatteryInfo;
import com.katsuna.batterywidget.BatteryMonitorService;
import com.katsuna.batterywidget.BatteryUpdateService;
import com.katsuna.clockwidget.ClockMonitorService;
import com.katsuna.clockwidget.ClockUpdateService;
import com.katsuna.commons.WidgetCollection;
import com.katsuna.weatherDb.Weather;
import com.katsuna.weatherParser.JSONWeatherParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class WeatherUpdateService extends IntentService {


    public static final String ACTION_FETCH_WEATHER_DATA = "lnm.weatherwiget.action.FETCH_WEATHER_DATA";
    public static final String ACTION_RECORD_WEATHER_DATA = "lnm.weatherwiget.action.RECORD_WEATHER_DATA";
    public static final String ACTION_FETCH_FORECAST_WEATHER_DATA = "lnm.weatherwiget.action.FETCH_FORECAST_WEATHER_DATA";
    public static final String ACTION_FETCH_LONG_FORECAST_WEATHER_DATA = "lnm.weatherwiget.action.FETCH_LONG_FORECAST_WEATHER_DATA";
    public static final String WIDGET_IDS = "com.katsuna.weatherwidget.extra.WIDGET_IDS";
    public static final String ACTION_WIDGET_UPDATE = "com.katsuna.weatherwidget.action.WIDGET_UPDATE";
    public static final String ACTION_WIDGET_EXTENDED_NOW = "com.katsuna.weatherwidget.action.WeatherNow";
    public static final String ACTION_WIDGET_EXTENDED_DAY = "com.katsuna.weatherwidget.action.WeatherDay";
    public static final String ACTION_WIDGET_EXTENDED_WEEK = "com.katsuna.weatherwidget.action.WeatherWeek";
    public static final String ACTION_WIDGET_EXTENDED_BACK = "com.katsuna.weatherwidget.action.Back";
    public static final String ACTION_WIDGET_WEATHER_CHOICE = "com.katsuna.weatherwidget.action.WeatherChoice";;


    public WeatherUpdateService() {
        super("WeatherUpdateService");
    }



    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            System.out.println("="+action);
            if( ACTION_WIDGET_WEATHER_CHOICE.equals(action)){
                Intent updateIntent = new Intent(this, ClockUpdateService.class);
                updateIntent.setAction(ClockUpdateService.ACTION_WEATHER_CHOICE);
                this.startService(updateIntent);

                Intent updateBatteryIntent = new Intent(this, BatteryUpdateService.class);
                updateBatteryIntent.setAction(BatteryUpdateService.ACTION_WEATHER_CHOICE);
                this.startService(updateBatteryIntent);

                RemoteViews remoteViews =createRemoteViews(5);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_WIDGET_UPDATE.equals(action)) {

                Intent updateIntent = new Intent(this, ClockUpdateService.class);
                updateIntent.setAction(ClockUpdateService.ACTION_WIDGET_UPDATE);
                this.startService(updateIntent);

                Intent updateBatteryIntent = new Intent(this, BatteryUpdateService.class);
                updateBatteryIntent.setAction(BatteryUpdateService.ACTION_BATTERY_BACK);
                this.startService(updateBatteryIntent);

                RemoteViews remoteViews =createRemoteViews(1);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            } else if (ACTION_WIDGET_EXTENDED_NOW.equals(action)) {
                RemoteViews remoteViews =createRemoteViews(2);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_WIDGET_EXTENDED_WEEK.equals(action)){
                RemoteViews remoteViews =createRemoteViews(3);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_WIDGET_EXTENDED_DAY.equals(action)){
                RemoteViews remoteViews =createRemoteViews(4);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_WIDGET_EXTENDED_BACK.equals(action)){
                this.startService(new Intent(this, ClockMonitorService.class));
                // update the widgets
                Intent updateIntent = new Intent(this, ClockUpdateService.class);
                updateIntent.setAction(ClockUpdateService.ACTION_WIDGET_UPDATE);
                this.startService(updateIntent);

                Intent updateBatteryIntent = new Intent(this, BatteryUpdateService.class);
                updateBatteryIntent.setAction(BatteryUpdateService.ACTION_BATTERY_BACK);
                this.startService(updateBatteryIntent);

                RemoteViews remoteViews =createRemoteViews(1);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
        }


    }

    private RemoteViews createRemoteViews(int layout) {
        Intent intent = new Intent(this, AlarmReceiver.class);
        //intent.setAction(ACTION_MENU_CLICKED);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        Weather widgetWeather = new Weather();
        if (!sp.getString("lastToday", "").equals("")) {
            widgetWeather = JSONWeatherParser.parseWidgetJson(sp.getString("lastToday", ""), this);
        } else {
            try {
                pendingIntent2.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        }

        if( layout == 1) {
            if(widgetWeather.getIcon()!= null) {

                remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget);
                remoteViews.setOnClickPendingIntent(R.id.widgetRoot, getPendingSelfIntent(this, WidgetCollection.WEATHER_CLICKED));

                remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
                remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription());
                remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());

                remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
            }
        }
        else if( layout == 2) {
            if(widgetWeather.getIcon()!= null) {
                remoteViews = new RemoteViews(getPackageName(), R.layout.extended_widget_view);
                remoteViews.setOnClickPendingIntent(R.id.back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.state_week, getPendingSelfIntent(this, WidgetCollection.WEEK_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.state_day, getPendingSelfIntent(this, WidgetCollection.DAY_CLICKED));

                remoteViews.setTextViewText(R.id.widgetExTemperature, widgetWeather.getTemperature());
                remoteViews.setTextViewText(R.id.widgetExDescription, widgetWeather.getDescription());
                remoteViews.setTextViewText(R.id.widgetHumidity, "Humidity:"+ widgetWeather.getHumidity() + "%");
                remoteViews.setTextViewText(R.id.precipitation, "Chance of rain/snow: "+ widgetWeather.getPrecipitation());
                if(widgetWeather.getWindDirectionDegree() != null)
                    remoteViews.setTextViewText(R.id.widgetExWind, widgetWeather.getWindDirection().getLocalizedString(this)+", "+widgetWeather.getWind());

                remoteViews.setImageViewResource(R.id.widgetExIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
            }
        }
        else if(layout == 3){

            List<Weather> forecast = new ArrayList<>();
            if (!sp.getString("lastLongterm", "").equals("")) {
                System.out.println("im called");
                forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), this);
            } else {
                System.out.println("im called 2");
                try {
                    pendingIntent2.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }

            remoteViews = new RemoteViews(getPackageName(), R.layout.week_widget_view);
            remoteViews.setOnClickPendingIntent(R.id.back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_now, getPendingSelfIntent(this, WidgetCollection.VIEW_WEATHER_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_week_day, getPendingSelfIntent(this, WidgetCollection.DAY_CLICKED));

            remoteViews.setTextViewText(R.id.widgetWeekTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetWeekDescription, widgetWeather.getDescription());
            if(widgetWeather.getWindDirectionDegree() != null)
            remoteViews.setTextViewText(R.id.widgetWeekWind, widgetWeather.getWindDirection().getLocalizedString(this)+", "+widgetWeather.getWind());

            remoteViews.setImageViewResource(R.id.widgetWeekIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
            int[] daysIDS = new int[] {R.id.day1, R.id.day2, R.id.day3,R.id.day4, R.id.day5,R.id.day6, R.id.day7};
            int[] iconsIDs = new int[] {R.id.icon1, R.id.icon2, R.id.icon3,R.id.icon4, R.id.icon5, R.id.icon6, R.id.icon7 };
            int[] tempIDs = new int[] {R.id.temp1, R.id.temp2, R.id.temp3,R.id.temp4, R.id.temp5, R.id.temp6, R.id.temp7 };

            int j = 0;

//            if (!isSameDate(forecast.get(0).getDate()))
//                j++;

            for(int i = 0; i < 7; i++){


                remoteViews.setTextViewText(daysIDS[i],getDay(forecast.get(i).getDate()));
                remoteViews.setImageViewResource(iconsIDs[i], getWeatherIconId(forecast.get(i).getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
                remoteViews.setTextViewText(tempIDs[i],forecast.get(i).getTemperature());
                j++;
            }
        }
        else if(layout == 4){

            List<Weather> forecast = new ArrayList<>();
            if (!sp.getString("lastShortterm", "").equals("")) {
                forecast = JSONWeatherParser.parseShortTermWidgetJson(sp.getString("lastShortterm", ""), this);
            } else {
                System.out.println("im called 2");
                try {
                    pendingIntent2.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }

            remoteViews = new RemoteViews(getPackageName(), R.layout.day_widget_view);
            remoteViews.setOnClickPendingIntent(R.id.day_back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_day_now, getPendingSelfIntent(this, WidgetCollection.VIEW_WEATHER_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_day_week, getPendingSelfIntent(this, WidgetCollection.WEEK_CLICKED));


            remoteViews.setTextViewText(R.id.widgetWeekTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetWeekDescription, widgetWeather.getDescription());
            remoteViews.setTextViewText(R.id.widgetWeekWind, widgetWeather.getWindDirection().getLocalizedString(this)+", "+widgetWeather.getWind());

            remoteViews.setImageViewResource(R.id.widgetWeekIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
            int[] timeIDS = new int[] {R.id.time1, R.id.time2, R.id.time3,R.id.time4, R.id.time5,R.id.time6, R.id.time7};
            int[] iconsIDs = new int[] {R.id.day_icon1, R.id.day_icon2, R.id.day_icon3,R.id.day_icon4, R.id.day_icon5, R.id.day_icon6, R.id.day_icon7 };
            int[] tempIDs = new int[] {R.id.day_temp1, R.id.day_temp2, R.id.day_temp3,R.id.day_temp4, R.id.day_temp5, R.id.day_temp6, R.id.day_temp7 };


            DateFormat format = new SimpleDateFormat("HH:mm");
            for(int i = 0; i < 7; i++){

                remoteViews.setTextViewText(timeIDS[i],format.format(forecast.get(i).getDate()));
                remoteViews.setImageViewResource(iconsIDs[i], getWeatherIconId(forecast.get(i).getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
                remoteViews.setTextViewText(tempIDs[i],forecast.get(i).getTemperature());
            }
        }
        else if (layout == 5){
            if(widgetWeather.getIcon()!= null) {
                remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_weather);
                remoteViews.setOnClickPendingIntent(R.id.forecast_btn, getPendingSelfIntent(this, WidgetCollection.VIEW_WEATHER_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.forecast_close_btn, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
                remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
                remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription());
                remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());

                remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
            }
        }


        return remoteViews;
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, WidgetCollection.class);
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

    public String getDay(Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String day ="";
        switch (dayOfWeek){
            case 1:
                day = getString(R.string.sunday);
                break;
            case 2:
                day = getString(R.string.monday);
                break;
            case 3:
                day = getString(R.string.tuesday);
                break;
            case 4:
                day = getString(R.string.wednesday);
                break;
            case 5:
                day = getString(R.string.thursday);
                break;
            case 6:
                day = getString(R.string.friday);
                break;
            case 7:
                day = getString(R.string.saturday);
                break;
        }
        return day;
    }

    boolean isSameDate(Date date){
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        Date now = new Date();
        cal1.setTime(now);
        cal2.setTime(date);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }


}
