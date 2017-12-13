package com.katsuna.widgets.commons;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.widgets.R;

import com.katsuna.widgets.clockwidget.ClockMonitorService;
import com.katsuna.widgets.clockwidget.ClockUpdateService;
import com.katsuna.widgets.clockwidget.MainActivity;
import com.katsuna.widgets.weatherDb.WeatherContentProvider;
import com.katsuna.widgets.weatherDb.WeatherDbHandler;
import com.katsuna.widgets.weatherwidget.WeatherMonitorService;
import com.katsuna.widgets.weatherwidget.WeatherUpdateFunctions;
import com.katsuna.widgets.weatherwidget.WeatherUpdateService;

import java.text.DateFormatSymbols;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.katsuna.widgets.R.string.sunday;


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

//    public static int getNumberOfWidgets(final Context context) {
//        ComponentName componentName = new ComponentName(context, ClockWidget.class);
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
//        int[] activeWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
//        if (activeWidgetIds != null) {
//            return activeWidgetIds.length;
//        } else {
//            return 0;
//        }
//    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
       // Log.d("Collection calendar_widget","I am n update...");

        for (int id : appWidgetIds) {

            setupTheme(context);




            if (extended == false &&calendar == false) {

                super.onUpdate(context, appWidgetManager, appWidgetIds);
                // CLOCK WIDGET UPDATE
                // ensure service is running
             //   context.startService(new Intent(context, ClockMonitorService.class));
                // update the widgets
//                Intent updateIntent = new Intent(context, ClockUpdateService.class);
//                updateIntent.setAction(ClockUpdateService.ACTION_WIDGET_UPDATE);
//                updateIntent.putExtra(ClockUpdateService.EXTRA_WIDGET_IDS, appWidgetIds);
//                context.startService(updateIntent);
                RemoteViews remoteViews = weatherUpdater.createRemoteViews(1,context,context.getPackageName(),this,colorProfile);
                remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(context, WidgetCollection.VIEW_CALENDAR_CLICKED));
                ComponentName componentName = new ComponentName(context, WidgetCollection.class);

                appWidgetManager.updateAppWidget(componentName, remoteViews);

                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    if(weatherUpdater == null){
                        weatherUpdater = new WeatherUpdateFunctions();
                    }
                    setupTheme(context);
                    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        System.out.println("Im in enabled and i have permission");
                        weatherUpdater.createRemoteViews(1,context,context.getPackageName(),this,colorProfile);
                    }


//                    context.startService(new Intent(context, WeatherMonitorService.class));
//                    Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
//                    updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
//                    updateWeatherIntent.putExtra(WeatherUpdateService.WIDGET_IDS, appWidgetIds);
//                    context.startService(updateWeatherIntent);
                }
                else{
                    //System.out.println("Im in update and i don't have permission");

                     remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
                    remoteViews.setOnClickPendingIntent(R.id.addPermissionBtn, getPendingSelfIntent(context, WidgetCollection.ADD_PERMISSION_CLICKED));

                    appWidgetManager.updateAppWidget( appWidgetIds, remoteViews);
                }
            }
        }
    }



    @Override
    public void onEnabled(Context context) {
        System.out.println("On enabled called!");
        super.onEnabled(context);
        if(weatherUpdater == null){
            weatherUpdater = new WeatherUpdateFunctions();
        }
        setupTheme(context);
//        context.startService(new Intent(context, ClockMonitorService.class));
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Im in enabled and i have permission");
            RemoteViews remoteViews = weatherUpdater.createRemoteViews(1,context,context.getPackageName(),this,colorProfile);
            remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(context, WidgetCollection.VIEW_CALENDAR_CLICKED));
            ComponentName componentName = new ComponentName(context, WidgetCollection.class);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.updateAppWidget(componentName, remoteViews);

          //  context.startService(new Intent(context, WeatherMonitorService.class));
        }



    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        super.onDeleted(context, widgetIds);
//        System.out.println("Remove service");

// if (getNumberOfWidgets(context) == 0) {
// stop monitoring if there are no more widgets on screen
            context.stopService(new Intent(context, ClockMonitorService.class));
            context.stopService(new Intent(context, WeatherMonitorService.class));

     //   }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
//        System.out.println("on Receive widget:"+ intent.getAction());
        if (TIME_CLICKED.equals(intent.getAction())) {
            extended = true;


            Intent updateClockIntent = new Intent(context, ClockUpdateService.class);
            updateClockIntent.setAction(ClockUpdateService.ACTION_WIDGET_CLOCK_CHOICE);
            context.startService(updateClockIntent);
        }
        else if (WEATHER_CLICKED.equals(intent.getAction())) {
            extended = true;

//            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
//            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_WEATHER_CHOICE);
//            context.startService(updateWeatherIntent);

        }
        else if (VIEW_WEATHER_CLICKED.equals(intent.getAction())) {
            extended = true;
            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_EXTENDED_DAY);
            context.startService(updateWeatherIntent);
        }
        else if (BACK_CLICKED.equals(intent.getAction())){
            extended = false;
            calendar = false;
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


        ////System.out.println("im inside draw calendar");
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
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.calendar_widget);

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
