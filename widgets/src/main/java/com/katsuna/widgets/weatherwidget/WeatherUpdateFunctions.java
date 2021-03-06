/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.widgets.weatherwidget;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
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


    public RemoteViews createRemoteViews(int layout, Context context, String packageName, WidgetCollection provider, ColorProfile colorProfile,UserProfileContainer mUserProfileContainer) {
        int color2 = ColorCalc.getColor(context,
                ColorProfileKey.ACCENT2_COLOR, colorProfile);

        int textColor = 0;
        if(color2 == com.katsuna.commons.R.color.common_indigoA700 || color2 == com.katsuna.commons.R.color.common_black
                || color2 == com.katsuna.commons.R.color.common_grey900 || color2 == com.katsuna.commons.R.color.common_grey600
                || color2 == com.katsuna.commons.R.color.common_grey300 || colorProfile == ColorProfile.CONTRAST){
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
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    RemoteViews rv = null;
                    if(mUserProfileContainer !=null) {
                        if (!mUserProfileContainer.isRightHanded()) {
                            rv = new RemoteViews(context.getPackageName(), R.layout.current_weather_left);
                            remoteViews = new RemoteViews(packageName, R.layout.collection_widget_left);
                        } else {
                            rv = new RemoteViews(context.getPackageName(), R.layout.current_weather);
                            remoteViews = new RemoteViews(packageName, R.layout.collection_widget_v4);
                        }
                    }
                    else{
                        rv = new RemoteViews(context.getPackageName(), R.layout.current_weather);
                        remoteViews = new RemoteViews(packageName, R.layout.collection_widget_v4);

                    }

                    remoteViews.setOnClickPendingIntent(R.id.weatherRoot, provider.getPendingSelfIntent(context, WidgetCollection.VIEW_WEATHER_CLICKED));

                    rv.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());

                    rv.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
                    //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
                    rv.setTextViewText(R.id.city, widgetWeather.getCity());

                    rv.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
                    remoteViews.removeAllViews(R.id.weatherRootNoData);
                    remoteViews.removeAllViews(R.id.weatherRootContainer);

                    remoteViews.addView(R.id.weatherRootContainer, rv);
                    if(textColor != 0){
                        remoteViews.setTextColor(R.id.back, textColor);

                    }
                }
                else{
                    if (mUserProfileContainer != null) {
                        if (!mUserProfileContainer.isRightHanded()) {
                            remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permition_layout_left);
                        } else {
                            remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
                        }
                    }
                    else{
                        remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
                    }
                    remoteViews.setOnClickPendingIntent(R.id.addPermissionBtn, provider.getPendingSelfIntent(context, WidgetCollection.ADD_PERMISSION_CLICKED));

                }
            }

        }
        else if(layout == 3){

            List<Weather> forecast = new ArrayList<>();
            if (!sp.getString("lastLongterm", "").equals("")) {

                forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), context);
                if(forecast == null){
                    WeatherJobService jobService = new WeatherJobService();
                    jobService.getLongWeather(context);
                    if (!sp.getString("lastLongterm", "").equals("")) {
                        forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), context);
                    }
                }
            } else {

                WeatherJobService jobService = new WeatherJobService();

                jobService.getLongWeather(context);

                if (!sp.getString("lastLongterm", "").equals("")) {

                    forecast = JSONWeatherParser.parseLongTermWidgetJson(sp.getString("lastLongterm", ""), context);
                }
                else{
//                    System.out.println("I HAVEN'T ANY DATA");
                }
            }
            if(mUserProfileContainer != null) {
                if (!mUserProfileContainer.isRightHanded()) {
                    remoteViews = new RemoteViews(packageName, R.layout.week_widget_left);
                } else {
                    remoteViews = new RemoteViews(packageName, R.layout.week_widget_view);
                }
            }
            else{
                remoteViews = new RemoteViews(packageName, R.layout.week_widget_view);
            }
            remoteViews.setOnClickPendingIntent(R.id.back, provider.getPendingSelfIntent(context, WidgetCollection.BACK_CLICKED));
//            remoteViews.setOnClickPendingIntent(R.id.state_now, provider.getPendingSelfIntent(context, WidgetCollection.VIEW_WEATHER_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.state_week_day, provider.getPendingSelfIntent(context, WidgetCollection.DAY_CLICKED));

            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription()+", "+widgetWeather.getWind());
            //remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
            remoteViews.setTextViewText(R.id.city, widgetWeather.getCity());

            int color1 = ColorCalc.getColor(context,
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.state_week, "setBackgroundColor", color1);


            remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
            remoteViews.setInt(R.id.back, "setBackgroundColor", color1);
            remoteViews.setTextColor(R.id.back, color2);
            remoteViews.setTextColor(R.id.state_week, color2);
            if( textColor ==  R.color.common_white){
                remoteViews.setTextViewCompoundDrawables(R.id.back,R.drawable.ic_x_icon_white,0,0,0);
                remoteViews.setTextViewCompoundDrawables(R.id.state_week,0,0,0,R.drawable.ic_minus_white);
            }


            int[] daysIDS = {R.id.day1, R.id.day2, R.id.day3,R.id.day4, R.id.day5,R.id.day6, R.id.day7};
            int[] iconsIDs =  {R.id.icon1, R.id.icon2, R.id.icon3,R.id.icon4, R.id.icon5, R.id.icon6, R.id.icon7 };
            int[] tempIDs = {R.id.temp1, R.id.temp2, R.id.temp3,R.id.temp4, R.id.temp5, R.id.temp6, R.id.temp7 };

            int j = 0;

//            if (!isSameDate(forecast.get(0).getDate()))
//                j++;
            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
            Date date = new Date();

            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_WEEK);
            if(forecast.size()>0) {
                for (int i = 0; i < 7; i++) {

                if(  fmt.format(date).equals(fmt.format(forecast.get(i).getDate()) )){
                    remoteViews.setTextColor(daysIDS[i], color2);
                }

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
            if( mUserProfileContainer != null) {
                if (!mUserProfileContainer.isRightHanded()) {
                    remoteViews = new RemoteViews(packageName, R.layout.day_widget_left);
                } else {
                    remoteViews = new RemoteViews(packageName, R.layout.day_widget_view);
                }
            }
            else{
                remoteViews = new RemoteViews(packageName, R.layout.day_widget_view);
            }
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

            remoteViews.setTextColor(R.id.state_day_day, color2);
            remoteViews.setTextColor(R.id.back, color2);

            remoteViews.setInt(R.id.back, "setBackgroundColor", color1);

            if( textColor ==  R.color.common_white){

                remoteViews.setTextViewCompoundDrawables(R.id.back,R.drawable.ic_x_icon_white,0,0,0);
                remoteViews.setTextViewCompoundDrawables(R.id.state_day_day,0,0,0,R.drawable.ic_minus_white);
            }
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
