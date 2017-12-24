package com.katsuna.widgets.weatherwidget;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.widgets.R;
import com.katsuna.widgets.commons.WidgetCollection;
import com.katsuna.widgets.weatherDb.Weather;
import com.katsuna.widgets.weatherParser.JSONWeatherParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class WeatherUpdateFunctions {

    public RemoteViews createRemoteViews(int layout, Context context, String packageName, WidgetCollection provider, ColorProfile colorProfile) {
        int color2 = ColorCalc.getColor(context,
                ColorProfileKey.ACCENT2_COLOR, colorProfile);

        int textColor;
        if(color2 == com.katsuna.commons.R.color.common_indigoA700){
            textColor = R.color.common_white;
        }

        RemoteViews remoteViews = null;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        Weather widgetWeather = new Weather();
        if (!sp.getString("lastToday", "").equals("")) {
            //Log.d("api call","get day forecast inside update");

            widgetWeather = JSONWeatherParser.parseWidgetJson(sp.getString("lastToday", ""), context);
        } else {
            WeatherJobService jobService = new WeatherJobService();
//            System.out.println("im before get current weather");
            jobService.getCurrentWeather(context);

            if (!sp.getString("lastToday", "").equals("")) {
                //Log.d("api call","get day forecast inside update");

                widgetWeather = JSONWeatherParser.parseWidgetJson(sp.getString("lastToday", ""), context);
            }
            else{
                remoteViews = new RemoteViews(packageName, R.layout.no_weather_data);

            }
        }

        if( layout == 1 || layout == 6 || layout ==7) {
            if(widgetWeather.getIcon()!= null) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    RemoteViews rv = new RemoteViews(packageName, R.layout.current_weather);
                    remoteViews = new RemoteViews(packageName, R.layout.collection_widget_v4);
                    remoteViews.setOnClickPendingIntent(R.id.weatherRoot, provider.getPendingSelfIntent(context, WidgetCollection.VIEW_WEATHER_CLICKED));

                    rv.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());

                    rv.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
                    //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
                    rv.setTextViewText(R.id.city, widgetWeather.getCity());

                    rv.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
                    remoteViews.removeAllViews(R.id.weatherRootNoData);
                    remoteViews.removeAllViews(R.id.weatherRootContainer);

                    remoteViews.addView(R.id.weatherRootContainer, rv);

                }
                else{
                    remoteViews = new RemoteViews(packageName, R.layout.no_permission_layout);
                    remoteViews.setOnClickPendingIntent(R.id.addPermissionBtn, provider.getPendingSelfIntent(context, WidgetCollection.ADD_PERMISSION_CLICKED));

                }
            }

        }
        else if( layout == 2) {
            if(widgetWeather.getIcon()!= null) {
                remoteViews = new RemoteViews(packageName, R.layout.extended_widget_view);

                remoteViews.setOnClickPendingIntent(R.id.back, provider.getPendingSelfIntent(context, WidgetCollection.BACK_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.state_week, provider.getPendingSelfIntent(context, WidgetCollection.WEEK_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.state_day, provider.getPendingSelfIntent(context, WidgetCollection.DAY_CLICKED));

                remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());

                remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
                remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
                //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
                remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());


                remoteViews.setTextViewText(R.id.widgetExDescription, widgetWeather.getDescription());
                remoteViews.setTextViewText(R.id.widgetHumidity, "Humidity:"+ widgetWeather.getHumidity() + "%");
                remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));


            }

            int color1 = ColorCalc.getColor(context,
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.state_now, "setBackgroundColor", color1);

            remoteViews.setTextColor(R.id.state_now, color2);

        }
        else if(layout == 3){

            List<Weather> forecast = new ArrayList<>();
            if (!sp.getString("lastLongterm", "").equals("")) {
                //   //System.out.println("im called");
                ////Log.d("api call","Api call for longTerm forecast inside update");

                forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), context);
            } else {

                WeatherJobService jobService = new WeatherJobService();

                jobService.getLongWeather(context);

                if (!sp.getString("lastLongterm", "").equals("")) {
                    //   //System.out.println("im called");
                    ////Log.d("api call","Api call for longTerm forecast inside update");

                    forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), context);
                }
                else{
//                    System.out.println("I HAVEN'T ANY DATA");
                }
            }

            remoteViews = new RemoteViews(packageName, R.layout.week_widget_view);
            remoteViews.setOnClickPendingIntent(R.id.back, provider.getPendingSelfIntent(context, WidgetCollection.BACK_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_now, provider.getPendingSelfIntent(context, WidgetCollection.VIEW_WEATHER_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_week_day, provider.getPendingSelfIntent(context, WidgetCollection.DAY_CLICKED));

            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
            //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
            remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());

            int color1 = ColorCalc.getColor(context,
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.state_week, "setBackgroundColor", color1);

            remoteViews.setTextColor(R.id.state_week, color2);
            remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
            remoteViews.setInt(R.id.back, "setBackgroundColor", color1);

            int[] daysIDS = new int[] {R.id.day1, R.id.day2, R.id.day3,R.id.day4, R.id.day5,R.id.day6, R.id.day7};
            int[] iconsIDs = new int[] {R.id.icon1, R.id.icon2, R.id.icon3,R.id.icon4, R.id.icon5, R.id.icon6, R.id.icon7 };
            int[] tempIDs = new int[] {R.id.temp1, R.id.temp2, R.id.temp3,R.id.temp4, R.id.temp5, R.id.temp6, R.id.temp7 };

            int j = 0;

//            if (!isSameDate(forecast.get(0).getDate()))
//                j++;
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if(forecast.size()>0) {
                for (int i = 0; i < 7; i++) {
                    if (i + 1 == day) {
                        remoteViews.setTextColor(daysIDS[i], color2);
                    }
//                   System.out.println("the day:"+i+" is the day:"+forecast.get(i).getDate().toString());
                    remoteViews.setTextViewText(daysIDS[i], getDay(forecast.get(i).getDate(),context));
                    remoteViews.setImageViewResource(iconsIDs[i], getWeatherIconId(forecast.get(i).getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
                    remoteViews.setTextViewText(tempIDs[i], forecast.get(i).getTemperature());
                    j++;
                }
            }
        }
        else if(layout == 4){

            List<Weather> forecast = new ArrayList<>();
            if (!sp.getString("lastShortterm", "").equals("")) {
                ////Log.d("api call","Api call for shortTerm forecast inside update");
                forecast = JSONWeatherParser.parseShortTermWidgetJson(sp.getString("lastShortterm", ""), context);
            } else {
                WeatherJobService jobService = new WeatherJobService();

                jobService.getShortWeather(context);

                if (!sp.getString("lastShortterm", "").equals("")) {
                    ////Log.d("api call","Api call for shortTerm forecast inside update");
                    forecast = JSONWeatherParser.parseShortTermWidgetJson(sp.getString("lastShortterm", ""), context);
                }
                else{
                }
            }

            remoteViews = new RemoteViews(packageName, R.layout.day_widget_view);
            remoteViews.setOnClickPendingIntent(R.id.back, provider.getPendingSelfIntent(context, WidgetCollection.BACK_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_day_week, provider.getPendingSelfIntent(context, WidgetCollection.WEEK_CLICKED));

            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
            // remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
            remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());
            remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));


            int color1 = ColorCalc.getColor(context,
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.state_day_day, "setBackgroundColor", color1);
//            int color2 = ColorCalc.getColor(context,
//                    ColorProfileKey.ACCENT2_COLOR, colorProfile);
            remoteViews.setTextColor(R.id.state_day_day, color2);
            remoteViews.setInt(R.id.back, "setBackgroundColor", color1);

            int[] timeIDS = new int[] {R.id.time1, R.id.time2, R.id.time3,R.id.time4, R.id.time5,R.id.time6, R.id.time7};
            int[] iconsIDs = new int[] {R.id.day_icon1, R.id.day_icon2, R.id.day_icon3,R.id.day_icon4, R.id.day_icon5, R.id.day_icon6, R.id.day_icon7 };
            int[] tempIDs = new int[] {R.id.day_temp1, R.id.day_temp2, R.id.day_temp3,R.id.day_temp4, R.id.day_temp5, R.id.day_temp6, R.id.day_temp7 };


            DateFormat format = new SimpleDateFormat("HH:mm");
            DateFormat formatDay = new SimpleDateFormat("HH");
            if( forecast.size() > 0) {
                for (int i = 0; i < 7; i++) {

                    remoteViews.setTextViewText(timeIDS[i], format.format(forecast.get(i).getDate()));
                    remoteViews.setImageViewResource(iconsIDs[i], getWeatherIconId(forecast.get(i).getIcon(), Integer.parseInt(formatDay.format(forecast.get(i).getDate())), context));
                    remoteViews.setTextViewText(tempIDs[i], forecast.get(i).getTemperature());
                }
            }
        }
        else if (layout == 5){
            if(widgetWeather.getIcon()!= null) {
                remoteViews = new RemoteViews(packageName, R.layout.collection_widget_weather);
                remoteViews.setOnClickPendingIntent(R.id.forecast_btn, provider.getPendingSelfIntent(context, WidgetCollection.VIEW_WEATHER_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.forecast_close_btn, provider.getPendingSelfIntent(context, WidgetCollection.BACK_CLICKED));
                remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
                remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
                // remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
                remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());
                remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
            }


        }


        return remoteViews;
    }

    public String getDay(Date date, Context context){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        String day ="";
        switch (dayOfWeek){
            case 1:
                day = context.getString(R.string.sunday);
                break;
            case 2:
                day = context.getString(R.string.monday);
                break;
            case 3:
                day = context.getString(R.string.tuesday);
                break;
            case 4:
                day = context.getString(R.string.wednesday);
                break;
            case 5:
                day = context.getString(R.string.thursday);
                break;
            case 6:
                day = context.getString(R.string.friday);
                break;
            case 7:
                day = context.getString(R.string.saturday);
                break;
        }
        return day;
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

}
