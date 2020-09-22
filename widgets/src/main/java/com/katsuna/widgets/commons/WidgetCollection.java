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
package com.katsuna.widgets.commons;


import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.widgets.R;

import com.katsuna.widgets.weatherDb.WeatherContentProvider;
import com.katsuna.widgets.weatherDb.WeatherDbHandler;
import com.katsuna.widgets.weatherwidget.WeatherMonitorService;
import com.katsuna.widgets.weatherwidget.WeatherUpdateFunctions;

import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class WidgetCollection extends AppWidgetProvider {

    public static final String BACK_CLICKED = "backClicked";
    public static final String ADD_PERMISSION_CLICKED ="addPermissions";
    public static final String WEEK_CLICKED = "weekCLicked";
    public static final String DAY_CLICKED = "dayClicked";
    private PendingIntent service = null;
    public static final String WEATHER_CLICKED    = "automaticWidgetSyncButtonClick";
    public static final String VIEW_WEATHER_CLICKED    = "automaticWidgetForecastButtonClick";
    public static final String TIME_CLICKED    = "automaticWidgetSyncTimeClick";
    public static final String VIEW_CALENDAR_CLICKED    = "automaticWidgetCalendarButtonClick";


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    private static final String DEBUG_TAG = "onClicked";
    public static boolean extended = false;
    public static boolean calendar = false;
    public static boolean enabled = false;


    public static WeatherDbHandler wDBHandler;
    public static WeatherContentProvider weatherContentProvider;
    public static String ACTION_MENU_CLICKED = "MenuClicked";
    private String actionClicked ="backClicked";

    UserProfileContainer mUserProfileContainer;
    ColorProfile colorProfile;
    private int mTheme;
    private WeatherUpdateFunctions weatherUpdater;



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       // Log.d("Collection calendar_widget","I am n update...");

        for (int id : appWidgetIds) {

            setupTheme(context);

            if(weatherUpdater == null){
                weatherUpdater = new WeatherUpdateFunctions();
            }


            if (extended == false &&calendar == false) {

                super.onUpdate(context, appWidgetManager, appWidgetIds);



                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {


                    if(weatherUpdater == null){
                        weatherUpdater = new WeatherUpdateFunctions();
                    }
                    setupTheme(context);

                    RemoteViews remoteViews = weatherUpdater.createRemoteViews(1,context,context.getPackageName(),this,colorProfile, mUserProfileContainer);
                    remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(context, WidgetCollection.VIEW_CALENDAR_CLICKED));
                    ComponentName componentName = new ComponentName(context, WidgetCollection.class);
                    appWidgetManager.updateAppWidget(componentName, remoteViews);

                }
                else{
                    //System.out.println("Im in update and i don't have permission");
                    RemoteViews remoteViews = null;
                    if (mUserProfileContainer != null){
                        if( !mUserProfileContainer.isRightHanded()) {
                            remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permition_layout_left);
                        }
                        else {
                            remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
                        }
                    }
                    else{
                        remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
                    }

                    remoteViews.setOnClickPendingIntent(R.id.addPermissionBtn, getPendingSelfIntent(context, WidgetCollection.ADD_PERMISSION_CLICKED));

                    appWidgetManager.updateAppWidget( appWidgetIds, remoteViews);
                }
            }
        }
    }



    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        if(weatherUpdater == null){
            weatherUpdater = new WeatherUpdateFunctions();
        }
        setupTheme(context);
//        context.startService(new Intent(context, ClockMonitorService.class));
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            RemoteViews remoteViews = weatherUpdater.createRemoteViews(1,context,context.getPackageName(),this,colorProfile, mUserProfileContainer);
            remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(context, WidgetCollection.VIEW_CALENDAR_CLICKED));
            ComponentName componentName = new ComponentName(context, WidgetCollection.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName, remoteViews);

          //  context.startService(new Intent(context, WeatherMonitorService.class));
        }
        else{
            RemoteViews remoteViews = null;
            if( mUserProfileContainer != null) {
                if (!mUserProfileContainer.isRightHanded()) {
                    remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permition_layout_left);
                } else {
                    remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
                }
            }
            else{
                remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
            }
            remoteViews.setOnClickPendingIntent(R.id.addPermissionBtn, getPendingSelfIntent(context, WidgetCollection.ADD_PERMISSION_CLICKED));
            ComponentName componentName = new ComponentName(context, WidgetCollection.class);

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

            appWidgetManager.updateAppWidget( componentName, remoteViews);
        }



    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        super.onDeleted(context, widgetIds);
//        System.out.println("Remove service");

// if (getNumberOfWidgets(context) == 0) {
// stop monitoring if there are no more widgets on screen
            context.stopService(new Intent(context, WeatherMonitorService.class));

     //   }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(weatherUpdater == null){
            weatherUpdater = new WeatherUpdateFunctions();
        }
        setupTheme(context);

        // TODO Auto-generated method stub
        super.onReceive(context, intent);
//        System.out.println("on Receive widget:"+ intent.getAction());
      if (WEATHER_CLICKED.equals(intent.getAction())) {
            extended = true;

          RemoteViews remoteViews = weatherUpdater.createRemoteViews(5,context,context.getPackageName(),this,colorProfile, mUserProfileContainer);
          ComponentName componentName = new ComponentName(context, WidgetCollection.class);
          AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
          appWidgetManager.updateAppWidget(componentName, remoteViews);

        }
        else if (VIEW_WEATHER_CLICKED.equals(intent.getAction())) {
            extended = true;
          RemoteViews remoteViews = weatherUpdater.createRemoteViews(4,context,context.getPackageName(),this,colorProfile, mUserProfileContainer);
          ComponentName componentName = new ComponentName(context, WidgetCollection.class);
          AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
          appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
        else if (BACK_CLICKED.equals(intent.getAction())){
            extended = false;
            calendar = false;

            RemoteViews remoteViews = weatherUpdater.createRemoteViews(1,context,context.getPackageName(),this,colorProfile, mUserProfileContainer);
            remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(context, WidgetCollection.VIEW_CALENDAR_CLICKED));
            ComponentName componentName = new ComponentName(context, WidgetCollection.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName, remoteViews);

        }
        else if (WEEK_CLICKED.equals(intent.getAction())){
            extended = true;
            RemoteViews remoteViews = weatherUpdater.createRemoteViews(3,context,context.getPackageName(),this,colorProfile, mUserProfileContainer);
            ComponentName componentName = new ComponentName(context, WidgetCollection.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
        else if (DAY_CLICKED.equals(intent.getAction())){
            extended = true;
            RemoteViews remoteViews = weatherUpdater.createRemoteViews(4,context,context.getPackageName(),this,colorProfile, mUserProfileContainer);
            ComponentName componentName = new ComponentName(context, WidgetCollection.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName, remoteViews);
        }
        else if(VIEW_CALENDAR_CLICKED.equals(intent.getAction())){
            calendar = true;
            drawWidget(context);
        }
        else if(ADD_PERMISSION_CLICKED.equals(intent.getAction())){
            Intent activityIntent = new Intent(context, PermissionActivity.class);
            context.startActivity(activityIntent);
        }


    }

    public PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }



    private void setupTheme(Context context) {
        UserProfileContainer userProfileContainer = ProfileReader.getKatsunaUserProfile(context);
        colorProfile = userProfileContainer.getColorProfile();
//        colorProfile =  ColorProfile.CONTRAST;
    }

    private int getTheme(ColorProfile profile) {
        int theme = R.style.CommonAppTheme;
        if (profile == ColorProfile.CONTRAST ||
                profile == ColorProfile.COLOR_IMPAIRMENT_AND_CONTRAST) {
            theme = R.style.CommonAppThemeContrast;
        }
        return theme;
    }

    private void drawWidget(Context context) {
        setupTheme(context);

        //  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Resources res = context.getResources();
        //  Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        boolean shortMonthName = false;
        boolean mini = false;
        int numWeeks = 5;

        int color1 = ColorCalc.getColor(context,
                ColorProfileKey.ACCENT1_COLOR, colorProfile);
        int color2 = ColorCalc.getColor(context, ColorProfileKey.ACCENT2_COLOR,
                colorProfile);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        RemoteViews rv = null;
        if(mUserProfileContainer != null) {
            if (!mUserProfileContainer.isRightHanded()) {
                rv = new RemoteViews(context.getPackageName(), R.layout.calendar_widget_left);
            } else {
                rv = new RemoteViews(context.getPackageName(), R.layout.calendar_widget);
            }
        }
        else{
            rv = new RemoteViews(context.getPackageName(), R.layout.calendar_widget);
        }

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        int todayYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);
        int todayWeek = cal.get(Calendar.DAY_OF_WEEK);


//        rv.setTextViewText(R.id.month_label, DateFormat.format(
//                shortMonthName ? "MMM yy" : "MMMM yyyy", cal));

        if (!mini) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int monthStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - monthStartDayOfWeek);
        } else {
            int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - todayDayOfWeek);
        }

        rv.removeAllViews(R.id.calendar);

        RemoteViews headerRowRv = new RemoteViews(context.getPackageName(),
                R.layout.row_header);
        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();
        for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
            RemoteViews dayRv = new RemoteViews(context.getPackageName(), R.layout.cell_header);
            dayRv.setTextViewText(android.R.id.text1, weekdays[day]);
            if(todayWeek == day){
                dayRv.setTextColor(android.R.id.text1,color2);
            }
            headerRowRv.addView(R.id.row_container, dayRv);

        }
        rv.addView(R.id.calendar, headerRowRv);

        for (int week = 0; week < numWeeks; week++) {
            RemoteViews rowRv = new RemoteViews(context.getPackageName(), R.layout.row_week);
            for (int day = 0; day < 7; day++) {
                boolean inMonth = cal.get(Calendar.MONTH) == thisMonth;
                boolean inYear  = cal.get(Calendar.YEAR) == todayYear;
                boolean isToday = inYear && inMonth && (cal.get(Calendar.DAY_OF_YEAR) == today);

                boolean isFirstOfMonth = cal.get(Calendar.DAY_OF_MONTH) == Calendar.SUNDAY;
                int cellLayoutResId = R.layout.cell_day;
                if (isToday) {
                    cellLayoutResId = R.layout.cell_today;

                } else if (inMonth) {
                    cellLayoutResId = R.layout.cell_day_this_month;
                }
                RemoteViews cellRv = new RemoteViews(context.getPackageName(), cellLayoutResId);

                cellRv.setTextViewText(android.R.id.text1,
                        Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
                if (isFirstOfMonth) {
                    // cellRv.setTextViewText(R.id.month_label, DateFormat.format("MMM", cal));
                }
                rowRv.addView(R.id.row_container, cellRv);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            rv.addView(R.id.calendar, rowRv);
        }

        //  rv.setInt(R.id.cell_today, "setBackgroundResource", color1);
        rv.setViewVisibility(R.id.month_bar, numWeeks <= 1 ? View.GONE : View.VISIBLE);
        rv.setOnClickPendingIntent(R.id.back, getPendingSelfIntent(context, WidgetCollection.BACK_CLICKED));
        String []clock = setTime();
        ////System.out.println("in clock choice with time:"+clock[0]);
        rv.setTextViewText(R.id.appwidget_text, clock[0]);
        rv.setTextViewText(R.id.date, clock[1]);
        rv.setInt(R.id.back, "setBackgroundColor", color1);
        rv.setTextColor(R.id.back, color2);
        if(color2 == com.katsuna.commons.R.color.common_indigoA700 || color2 == com.katsuna.commons.R.color.common_black
                || color2 == com.katsuna.commons.R.color.common_grey900 || color2 == com.katsuna.commons.R.color.common_grey600
                || color2 == com.katsuna.commons.R.color.common_grey300 || colorProfile == ColorProfile.CONTRAST){

            rv.setTextViewCompoundDrawables(R.id.back,R.drawable.ic_x_icon_white,0,0,0);
//            rv.setImageViewResource(R.id.today_container,R.drawable.oval_black);
            rv.setInt(R.id.today_container,"setBackgroundResource",R.drawable.oval_black);
        }

        // rv.setInt(R.id.back, "setBackgroundColor", color2);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName componentName = new ComponentName(context, WidgetCollection.class);
        appWidgetManager.updateAppWidget(componentName, rv);
    }

    public static String [] setTime() {
        String []clock;
        clock = new String[2];
        Date now = new Date();
        int hours = now.getHours();
        String time;
        if (hours >= 12)
            time = "PM";
        else
            time = "AM";
        hours = (hours > 12) ? hours - 12 : hours;
        hours = (hours == 0) ? hours + 12 : hours;
        int minutes = now.getMinutes();
        int h1 = hours / 10;
        int h2 = hours - h1 * 10;
        int m1 = minutes / 10;
        int m2 = minutes - m1 * 10;
        Format formatter = new SimpleDateFormat("EEEE dd MMMM ");
        String today = formatter.format(new Date());
        String clockUI = String.format("%d:%d%d %s",hours,m1,m2,time);
        clock[0] = clockUI;
        clock[1] = today;
        //Log.d("Clock Alarm: ", String.format("%d:%d -> %d %d: %d %d %s date: %s", hours, minutes, h1, h2, m1, m2,time,today));
        return clock;
    }


}
