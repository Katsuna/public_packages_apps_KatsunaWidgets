package com.katsuna.widgets.clockwidget;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.commons.utils.Shape;
import com.katsuna.widgets.R;
import com.katsuna.widgets.batterywidget.BatteryUpdateService;
import com.katsuna.widgets.commons.WidgetCollection;
import com.katsuna.widgets.weatherwidget.WeatherUpdateService;

import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.graphics.Color.YELLOW;


public class ClockUpdateService extends IntentService {

    public static final String ACTION_CLOCK_CHANGED = "clockwidget.action.CLOCK_CHANGED";

    public static final String ACTION_WIDGET_UPDATE = "clockwidget.action.WIDGET_UPDATE";

    public static final String EXTRA_WIDGET_IDS = "clockwidget.extra.WIDGET_IDS";

    public static final String ACTION_WIDGET_EXTENDED_CLOCK = "com.katsuna.weatherwidget.action.Clock";
    public static final String ACTION_WEATHER_CHOICE = "com.katsuna.batterywidget.action.weather_choice";
    public static final String ACTION_WIDGET_CLOCK_CHOICE = "com.katsuna.batterywidget.action.clock_choice";
    public static final String ACTION_WIDGET_EXTENDED_BACK = "com.katsuna.clockwidget.action.Back";
    public static final String ACTION_WIDGET_CALENDAR_VIEW = "com.katsuna.clockwidget.action.Calendar";

    private static final String PREF_MONTH = "month";
    private static final String PREF_YEAR = "year";
    public static final String ACTION_BATTERY_CHOICE = "com.katsuna.batterywidget.action.BATTERY_choice";

    UserProfileContainer mUserProfileContainer;
    ColorProfile colorProfile;


    public ClockUpdateService() {
        super("BatteryUpdateService");

    }

    CalendarView calendar;
    private  Handler handler;

    @Override
    protected void onHandleIntent(Intent intent) {
        setupTheme(this);

        if (intent != null) {
            final String action = intent.getAction();
            System.out.println("CLOCK CALLED:"+action);
            if ((ACTION_CLOCK_CHANGED.equals(action) || ACTION_WIDGET_UPDATE.equals(action))&& WidgetCollection.extended == false) {
                System.out.println("CLOCK CALLED in");



                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget);
                String []clock = setTime();
                remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(this, WidgetCollection.TIME_CLICKED));

                remoteViews.setTextViewText(R.id.appwidget_text, clock[0]);
                remoteViews.setTextViewText(R.id.date, clock[1]);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);


            }
            else if( ACTION_WEATHER_CHOICE.equals(action)){
                System.out.println("CLOCK Weather Opened!!!!!!!!");

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_weather);
                String []clock = setTime();
                remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(this, WidgetCollection.TIME_CLICKED));

                remoteViews.setTextViewText(R.id.appwidget_text, clock[0]);
                remoteViews.setTextViewText(R.id.date, clock[1]);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if( ACTION_BATTERY_CHOICE.equals(action)){
                System.out.println("CLOCK battery Opened!!!!!!!!");

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_battery);
                String []clock = setTime();
                remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(this, WidgetCollection.TIME_CLICKED));

                remoteViews.setTextViewText(R.id.appwidget_text, clock[0]);
                remoteViews.setTextViewText(R.id.date, clock[1]);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if( ACTION_WIDGET_CLOCK_CHOICE.equals(action)){
                System.out.println("CLOCK clock Opened!!!!!!!!");

                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_clock);
                String []clock = setTime();
                remoteViews.setOnClickPendingIntent(R.id.calendar_btn, getPendingSelfIntent(this, WidgetCollection.VIEW_CALENDAR_CLICKED));
                remoteViews.setOnClickPendingIntent(R.id.clock_close_btn, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
                System.out.println("in clock choice with time:"+clock[0]);
                remoteViews.setTextViewText(R.id.appwidget_text_clock, clock[0]);
                remoteViews.setTextViewText(R.id.date_clock, clock[1]);
//                adjustColorProfile(this);
                int color1 = ColorCalc.getColor(getApplicationContext(),
                        ColorProfileKey.ACCENT1_COLOR, colorProfile);
                remoteViews.setInt(R.id.calendar_btn, "setBackgroundColor", color1);
                int color2 = ColorCalc.getColor(getApplicationContext(), ColorProfileKey.ACCENT2_COLOR,
                        colorProfile);
                remoteViews.setInt(R.id.clock_close_btn, "setBackgroundColor", color2);
                // update the widgets
                Intent updateBatteryIntent = new Intent(this, BatteryUpdateService.class);
                updateBatteryIntent.setAction(BatteryUpdateService.ACTION_CLOCK_CHOICE);
                this.startService(updateBatteryIntent);
//!CHECK this
                //this.startService(new Intent(this, WeatherMonitorService.class));
                Intent updateWeatherIntent = new Intent(this, WeatherUpdateService.class);
                updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_CLOCK_CHOICE);
                this.startService(updateWeatherIntent);

                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if(ACTION_WIDGET_CALENDAR_VIEW.equals(action)){
                System.out.println("Calendar Opened!!!!!!!!");

                drawWidget();
            }

        }


    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {

        Intent intent = new Intent(context, WidgetCollection.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    public String [] setTime() {
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
        Log.d("Clock Alarm: ", String.format("%d:%d -> %d %d: %d %d %s date: %s", hours, minutes, h1, h2, m1, m2,time,today));
        return clock;
    }


    private void setupTheme(Context context) {
        UserProfileContainer  userProfileContainer = ProfileReader.getKatsunaUserProfile(context);
        colorProfile = userProfileContainer.getColorProfile();
        System.out.println("im out"+colorProfile);
    }


    private void adjustColorProfile() {

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_clock);

        int color1 = ColorCalc.getColor(getApplicationContext(),
                ColorProfileKey.ACCENT1_COLOR, colorProfile);
        remoteViews.setInt(R.id.calendar_btn, "setBackgroundColor", color1);
        int color2 = ColorCalc.getColor(getApplicationContext(), ColorProfileKey.ACCENT2_COLOR,
                colorProfile);
        remoteViews.setInt(R.id.clock_close_btn, "setBackgroundColor", color2);


    }

    private void drawWidget() {
        System.out.println("im inside draw calendar");
      //  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        Resources res = getApplicationContext().getResources();
      //  Bundle widgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        boolean shortMonthName = false;
        boolean mini = false;
        int numWeeks = 6;


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        RemoteViews rv = new RemoteViews(getPackageName(), R.layout.calendar_widget);

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_YEAR);
        int todayYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);


        rv.setTextViewText(R.id.month_label, DateFormat.format(
                shortMonthName ? "MMM yy" : "MMMM yyyy", cal));

        if (!mini) {
            cal.set(Calendar.DAY_OF_MONTH, 1);
            int monthStartDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - monthStartDayOfWeek);
        } else {
            int todayDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            cal.add(Calendar.DAY_OF_MONTH, 1 - todayDayOfWeek);
        }

        rv.removeAllViews(R.id.calendar);

        RemoteViews headerRowRv = new RemoteViews(getPackageName(),
                R.layout.row_header);
        DateFormatSymbols dfs = DateFormatSymbols.getInstance();
        String[] weekdays = dfs.getShortWeekdays();
        for (int day = Calendar.SUNDAY; day <= Calendar.SATURDAY; day++) {
            RemoteViews dayRv = new RemoteViews(getPackageName(), R.layout.cell_header);
            dayRv.setTextViewText(android.R.id.text1, weekdays[day]);
            headerRowRv.addView(R.id.row_container, dayRv);
        }
        rv.addView(R.id.calendar, headerRowRv);

        for (int week = 0; week < numWeeks; week++) {
            RemoteViews rowRv = new RemoteViews(getPackageName(), R.layout.row_week);
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
                RemoteViews cellRv = new RemoteViews(getPackageName(), cellLayoutResId);
                cellRv.setTextViewText(android.R.id.text1,
                        Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
                if (isFirstOfMonth) {
                    cellRv.setTextViewText(R.id.month_label, DateFormat.format("MMM", cal));
                }
                rowRv.addView(R.id.row_container, cellRv);
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            rv.addView(R.id.calendar, rowRv);
        }


        rv.setViewVisibility(R.id.month_bar, numWeeks <= 1 ? View.GONE : View.VISIBLE);
        rv.setOnClickPendingIntent(R.id.back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));
        String []clock = setTime();
        System.out.println("in clock choice with time:"+clock[0]);
        rv.setTextViewText(R.id.appwidget_text_calendar, clock[0]);
        rv.setTextViewText(R.id.date_calendar, clock[1]);
        int color2 = ColorCalc.getColor(getApplicationContext(), ColorProfileKey.ACCENT2_COLOR,
                colorProfile);
        rv.setInt(R.id.back, "setBackgroundColor", color2);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ComponentName componentName = new ComponentName(this, WidgetCollection.class);
        appWidgetManager.updateAppWidget(componentName, rv);
    }


}
