package com.katsuna.widgets.weatherwidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.widgets.R;
import com.katsuna.widgets.commons.PermissionActivity;
import com.katsuna.widgets.commons.WidgetCollection;
import com.katsuna.widgets.weatherDb.Weather;
import com.katsuna.widgets.weatherParser.JSONWeatherParser;

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
    public static final String ACTION_WIDGET_WEATHER_CHOICE = "com.katsuna.weatherwidget.action.WeatherChoice";
    public static final String ACTION_WIDGET_CLOCK_CHOICE = "com.katsuna.weatherwidget.action.ClockChoice";
    public static final String ACTION_WIDGET_BATTERY_CHOICE = "com.katsuna.weatherwidget.action.BatteryChoice";
    ColorProfile colorProfile;
    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION};


    public WeatherUpdateService() {
        super("WeatherMonitorUpdateService");
    }

//    public static final int ACTION_WIDGET_UPDATE = 1;
//    public static final int ACTION_WIDGET_EXTENDED_NOW =2;
//    public static final int ACTION_WIDGET_EXTENDED_DAY = 3;
//    public static final int ACTION_WIDGET_EXTENDED_WEEK = 4;
//    public static final int ACTION_WIDGET_EXTENDED_BACK = 5;
//    public static final int ACTION_WIDGET_WEATHER_CHOICE = 6;

//   public WeatherUpdateService() {
//        super("WeatherUpdateService");
//    }



    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            //System.out.println("Im on weatherUpdate service with action="+action);
            setupTheme(this);

            if( ACTION_WIDGET_WEATHER_CHOICE.equals(action)){

                RemoteViews remoteViews =createRemoteViews(5);
                int color1 = ColorCalc.getColor(getApplicationContext(),
                        ColorProfileKey.ACCENT1_COLOR, colorProfile);
                remoteViews.setInt(R.id.forecast_btn, "setBackgroundColor", color1);
                int color2 = ColorCalc.getColor(getApplicationContext(), ColorProfileKey.ACCENT2_COLOR,
                        colorProfile);
                remoteViews.setInt(R.id.forecast_close_btn, "setBackgroundColor", color2);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_WIDGET_UPDATE.equals(action)) {

                RemoteViews remoteViews =createRemoteViews(1);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            } else if (ACTION_WIDGET_EXTENDED_NOW.equals(action)) {
                RemoteViews remoteViews =createRemoteViews(2);

                int color2 = ColorCalc.getColor(getApplicationContext(), ColorProfileKey.ACCENT2_COLOR,
                        colorProfile);
               // remoteViews.setInt(R.id.back, "setBackgroundColor", color2);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_WIDGET_EXTENDED_WEEK.equals(action)){
                RemoteViews remoteViews =createRemoteViews(3);
                int color2 = ColorCalc.getColor(getApplicationContext(), ColorProfileKey.ACCENT2_COLOR,
                        colorProfile);
             //   remoteViews.setInt(R.id.back, "setBackgroundColor", color2);


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
                //this.startService(new Intent(this, ClockMonitorService.class));
                // update the widgets


                if(PermissionActivity.hasPermissions(getApplicationContext(), PERMISSIONS)) {
                    RemoteViews remoteViews = createRemoteViews(1);

                    remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(this, WidgetCollection.VIEW_CALENDAR_CLICKED));


                    ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                    appWidgetManager.updateAppWidget(componentName, remoteViews);
                }
            }

        }


    }

    private void setupTheme(Context context) {
        UserProfileContainer userProfileContainer = ProfileReader.getKatsunaUserProfile(context);
        colorProfile = userProfileContainer.getColorProfile();
    }

    private RemoteViews createRemoteViews(int layout) {
        int color2 = ColorCalc.getColor(getApplicationContext(),
                ColorProfileKey.ACCENT2_COLOR, colorProfile);
//        Drawable mDrawable = getApplicationContext().getResources().getDrawable(R.drawable.ic_1495041635_minus);
//        DrawableCompat.setTint(mDrawable, ContextCompat.getColor(getApplicationContext(), color2));
        int textColor;
        if(color2 == com.katsuna.commons.R.color.common_indigoA700){
            textColor =R.color.common_white;
        }

//        Intent intent = new Intent(this, AlarmReceiver.class);
//        //intent.setAction(ACTION_MENU_CLICKED);
//        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews remoteViews = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        Weather widgetWeather = new Weather();
        if (!sp.getString("lastToday", "").equals("")) {
            //Log.d("api call","get day forecast inside update");

            widgetWeather = JSONWeatherParser.parseWidgetJson(sp.getString("lastToday", ""), this);
        } else {
            WeatherJobService jobService = new WeatherJobService();
            jobService.getCurrentWeather(this);

//            intent.setAction(CURRENT);
//            pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            //System.out.println("current forecast without saved");
//
//            //Log.d("api call","Else last day forecast inside update");
//
//            try {
//                pendingIntent2.send();
//            } catch (PendingIntent.CanceledException e) {
//                e.printStackTrace();
//            }
            if (!sp.getString("lastToday", "").equals("")) {
                //Log.d("api call","get day forecast inside update");

                widgetWeather = JSONWeatherParser.parseWidgetJson(sp.getString("lastToday", ""), this);
            }
            else{
                remoteViews = new RemoteViews(getPackageName(), R.layout.no_weather_data);

            }
        }

        if( layout == 1 || layout == 6 || layout ==7) {
            if(widgetWeather.getIcon()!= null) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    RemoteViews rv = new RemoteViews(getPackageName(), R.layout.current_weather);
                    remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_v4);
                    remoteViews.setOnClickPendingIntent(R.id.weatherRoot, getPendingSelfIntent(this, WidgetCollection.VIEW_WEATHER_CLICKED));

                    rv.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());

                    rv.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
                    //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
                    rv.setTextViewText(R.id.city, widgetWeather.getCity());

                    rv.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
                    remoteViews.removeAllViews(R.id.weatherRootNoData);
                    remoteViews.removeAllViews(R.id.weatherRootContainer);

                    remoteViews.addView(R.id.weatherRootContainer, rv);

                }
                else{
                    remoteViews = new RemoteViews(getPackageName(), R.layout.no_permission_layout);
                    remoteViews.setOnClickPendingIntent(R.id.addPermissionBtn, getPendingSelfIntent(this, WidgetCollection.ADD_PERMISSION_CLICKED));


                }



            }

        }
        else if( layout == 2) {
            if(widgetWeather.getIcon()!= null) {
                remoteViews = new RemoteViews(getPackageName(), R.layout.extended_widget_view);

                remoteViews.setOnClickPendingIntent(R.id.back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.state_week, getPendingSelfIntent(this, WidgetCollection.WEEK_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.state_day, getPendingSelfIntent(this, WidgetCollection.DAY_CLICKED));

                remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());

                remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
                remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
                //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
                remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());


                remoteViews.setTextViewText(R.id.widgetExDescription, widgetWeather.getDescription());
                remoteViews.setTextViewText(R.id.widgetHumidity, "Humidity:"+ widgetWeather.getHumidity() + "%");
                remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));

                //       remoteViews.setTextViewText(R.id.precipitation, "Chance of rain/snow: "+ widgetWeather.getPrecipitation());
//                if(widgetWeather.getWindDirectionDegree() != null)
//                    remoteViews.setTextViewText(R.id.widgetExWind, widgetWeather.getWindDirection().getLocalizedString(this)+", "+widgetWeather.getWind());
//
//                remoteViews.setImageViewResource(R.id.widgetExIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
            }

            int color1 = ColorCalc.getColor(getApplicationContext(),
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.state_now, "setBackgroundColor", color1);

            remoteViews.setTextColor(R.id.state_now, color2);

        }
        else if(layout == 3){

            List<Weather> forecast = new ArrayList<>();
            if (!sp.getString("lastLongterm", "").equals("")) {
             //   //System.out.println("im called");
                ////Log.d("api call","Api call for longTerm forecast inside update");

                forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), this);
            } else {

                WeatherJobService jobService = new WeatherJobService();

                jobService.getLongWeather(this);
//                intent.setAction(LONG_FORECAST);
//                pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                //System.out.println("long forecast without saved");
//                try {
//                    pendingIntent2.send();
//                } catch (PendingIntent.CanceledException e) {
//                    e.printStackTrace();
//                }
                if (!sp.getString("lastLongterm", "").equals("")) {
                    //   //System.out.println("im called");
                    ////Log.d("api call","Api call for longTerm forecast inside update");

                    forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), this);
                }
                else{
                    System.out.println("I HAVEN'T ANY DATA");
                }
            }

            remoteViews = new RemoteViews(getPackageName(), R.layout.week_widget_view);
            remoteViews.setOnClickPendingIntent(R.id.back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_now, getPendingSelfIntent(this, WidgetCollection.VIEW_WEATHER_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_week_day, getPendingSelfIntent(this, WidgetCollection.DAY_CLICKED));

            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
            //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
            remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());

            int color1 = ColorCalc.getColor(getApplicationContext(),
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.state_week, "setBackgroundColor", color1);
//            int color2 = ColorCalc.getColor(getApplicationContext(),
//                    ColorProfileKey.ACCENT2_COLOR, colorProfile);
            remoteViews.setTextColor(R.id.state_week, color2);
            remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
            remoteViews.setInt(R.id.back, "setBackgroundColor", color1);


            int[] daysIDS = new int[] {R.id.day1, R.id.day2, R.id.day3,R.id.day4, R.id.day5,R.id.day6, R.id.day7};
            int[] iconsIDs = new int[] {R.id.icon1, R.id.icon2, R.id.icon3,R.id.icon4, R.id.icon5, R.id.icon6, R.id.icon7 };
            int[] tempIDs = new int[] {R.id.temp1, R.id.temp2, R.id.temp3,R.id.temp4, R.id.temp5, R.id.temp6, R.id.temp7 };

            int j = 0;

//            if (!isSameDate(forecast.get(0).getDate()))
//                j++;
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            System.out.println("the day is:"+ day+"forecaste"+forecast.size());
            if(forecast.size()>0) {
                for (int i = 0; i < 7; i++) {
                    if (i + 1 == day) {
                        remoteViews.setTextColor(daysIDS[i], color2);
                    }
                    ////System.out.println("the day:"+i+" is the day:"+forecast.get(i).getDate().toString());
                    remoteViews.setTextViewText(daysIDS[i], getDay(forecast.get(i).getDate()));
                    remoteViews.setImageViewResource(iconsIDs[i], getWeatherIconId(forecast.get(i).getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));
                    remoteViews.setTextViewText(tempIDs[i], forecast.get(i).getTemperature());
                    j++;
                }
            }
        }
        else if(layout == 4){

            List<Weather> forecast = new ArrayList<>();
            if (!sp.getString("lastShortterm", "").equals("")) {
                ////Log.d("api call","Api call for shortTerm forecast inside update");
                forecast = JSONWeatherParser.parseShortTermWidgetJson(sp.getString("lastShortterm", ""), this);
            } else {
                WeatherJobService jobService = new WeatherJobService();

                jobService.getShortWeather(this);
//                intent.setAction(SHORT_FORECAST);
//                pendingIntent2 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//                //System.out.println("short forecast without saved");
//                try {
//                    pendingIntent2.send();
//                } catch (PendingIntent.CanceledException e) {
//                    e.printStackTrace();
//                }
                if (!sp.getString("lastShortterm", "").equals("")) {
                    ////Log.d("api call","Api call for shortTerm forecast inside update");
                    forecast = JSONWeatherParser.parseShortTermWidgetJson(sp.getString("lastShortterm", ""), this);
                }
                else{
                    System.out.println("I HAVEN'T ANY 2");
                }
            }

            remoteViews = new RemoteViews(getPackageName(), R.layout.day_widget_view);
            remoteViews.setOnClickPendingIntent(R.id.back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_day_week, getPendingSelfIntent(this, WidgetCollection.WEEK_CLICKED));

            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
           // remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
            remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());
            remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), this));


            int color1 = ColorCalc.getColor(getApplicationContext(),
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.state_day_day, "setBackgroundColor", color1);
//            int color2 = ColorCalc.getColor(getApplicationContext(),
//                    ColorProfileKey.ACCENT2_COLOR, colorProfile);
            remoteViews.setTextColor(R.id.state_day_day, color2);
            remoteViews.setInt(R.id.back, "setBackgroundColor", color1);

            int[] timeIDS = new int[] {R.id.time1, R.id.time2, R.id.time3,R.id.time4, R.id.time5,R.id.time6, R.id.time7};
            int[] iconsIDs = new int[] {R.id.day_icon1, R.id.day_icon2, R.id.day_icon3,R.id.day_icon4, R.id.day_icon5, R.id.day_icon6, R.id.day_icon7 };
            int[] tempIDs = new int[] {R.id.day_temp1, R.id.day_temp2, R.id.day_temp3,R.id.day_temp4, R.id.day_temp5, R.id.day_temp6, R.id.day_temp7 };


            DateFormat format = new SimpleDateFormat("HH:mm");
            DateFormat formatDay = new SimpleDateFormat("HH");
            System.out.println("forecast:"+forecast.size());
            if( forecast.size() > 0) {
                for (int i = 0; i < 7; i++) {
                    System.out.println("forecast:" + forecast.size() + "time is:" + forecast.get(i).getDate());

                    remoteViews.setTextViewText(timeIDS[i], format.format(forecast.get(i).getDate()));
                    remoteViews.setImageViewResource(iconsIDs[i], getWeatherIconId(forecast.get(i).getIcon(), Integer.parseInt(formatDay.format(forecast.get(i).getDate())), this));
                    remoteViews.setTextViewText(tempIDs[i], forecast.get(i).getTemperature());
                }
            }
        }
        else if (layout == 5){
            if(widgetWeather.getIcon()!= null) {
                remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_weather);
                remoteViews.setOnClickPendingIntent(R.id.forecast_btn, getPendingSelfIntent(this, WidgetCollection.VIEW_WEATHER_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.forecast_close_btn, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
                remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
                remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
               // remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
                remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());
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
                icon = R.drawable.ic_weathericonssun;
            } else {
                icon = R.drawable.ic_weathericonsnight;
            }



        } else if (actualId.equals(context.getString(R.string.weather_thunder))) {
            icon = R.drawable.ic_weathericonsthunderstorm;

        }
        else if (actualId.equals(context.getString(R.string.weather_drizzle))) {
            icon = R.drawable.ic_weathericonslightrain;

        }
        else if(actualId.equals(context.getString(R.string.weather_foggy))){
            icon = R.drawable.ic_weathericonsfog;

        }
        else if(actualId.equals(context.getString(R.string.weather_cloudy))){
            icon = R.drawable.ic_weathericonsclouds;

        }
        else if(actualId.equals(context.getString(R.string.weather_snowy))){
            icon = R.drawable.ic_weathericonsheavysnow;

        }
        else if(actualId.equals(context.getString(R.string.weather_rainy))){
            icon = R.drawable.ic_weathericonsheavyrain;

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
