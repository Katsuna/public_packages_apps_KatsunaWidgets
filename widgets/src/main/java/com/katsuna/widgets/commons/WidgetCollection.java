package com.katsuna.widgets.commons;


import android.app.AlertDialog;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfile;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.widgets.R;
import com.katsuna.widgets.batterywidget.BatteryMonitorService;
import com.katsuna.widgets.batterywidget.BatteryUpdateService;
import com.katsuna.widgets.clockwidget.ClockWidget;
import com.katsuna.widgets.clockwidget.ClockMonitorService;
import com.katsuna.widgets.clockwidget.ClockUpdateService;
import com.katsuna.widgets.clockwidget.MainActivity;
import com.katsuna.widgets.weatherDb.WeatherContentProvider;
import com.katsuna.widgets.weatherDb.WeatherDbHandler;
import com.katsuna.widgets.weatherwidget.WeatherMonitorService;
import com.katsuna.widgets.weatherwidget.WeatherUpdateService;



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
       // Log.d("Collection calendar_widget","I am n update...");

        for (int id : appWidgetIds) {

            setupTheme(context);
//            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.collection_widget_v4);
//
//
//
//            //Coloring button
//            int color1 = ColorCalc.getColor(context,
//                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
//            remoteViews.setInt(R.id.calendar_btn, "setBackgroundColor", color1);
//            remoteViews.setInt(R.id.forecast_btn, "setBackgroundColor", color1);
//            remoteViews.setInt(R.id.energy_mode_btn, "setBackgroundColor", color1);
//
//            appWidgetManager.updateAppWidget( appWidgetIds, remoteViews);



            if (extended == false &&calendar == false) {
                super.onUpdate(context, appWidgetManager, appWidgetIds);
                // CLOCK WIDGET UPDATE
                // ensure service is running
                context.startService(new Intent(context, ClockMonitorService.class));
                // update the widgets
                Intent updateIntent = new Intent(context, ClockUpdateService.class);
                updateIntent.setAction(ClockUpdateService.ACTION_WIDGET_UPDATE);
                updateIntent.putExtra(ClockUpdateService.EXTRA_WIDGET_IDS, appWidgetIds);
                context.startService(updateIntent);

                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    context.startService(new Intent(context, WeatherMonitorService.class));
                    Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
                    updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
                    updateWeatherIntent.putExtra(WeatherUpdateService.WIDGET_IDS, appWidgetIds);
                    context.startService(updateWeatherIntent);
                }
                else{
                    System.out.println("Im in update and i don't have permission");

                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.no_permission_layout);
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


        context.startService(new Intent(context, ClockMonitorService.class));
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            System.out.println("Im in enabled and i have permission");

            context.startService(new Intent(context, WeatherMonitorService.class));
        }
        else{
            System.out.println("Im in enabled and i don't have permission");
        }


    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        super.onDeleted(context, widgetIds);
        System.out.println("On remove called");

        if (getNumberOfWidgets(context) == 0) {
            // stop monitoring if there are no more widgets on screen
            context.stopService(new Intent(context, ClockMonitorService.class));
            context.stopService(new Intent(context, WeatherMonitorService.class));

        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onReceive(context, intent);
        System.out.println("on Receive calendar_widget:"+ intent.getAction());
        if (TIME_CLICKED.equals(intent.getAction())) {
            extended = true;


            Intent updateClockIntent = new Intent(context, ClockUpdateService.class);
            updateClockIntent.setAction(ClockUpdateService.ACTION_WIDGET_CLOCK_CHOICE);
            context.startService(updateClockIntent);
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
            Intent updateClockIntent = new Intent(context, ClockUpdateService.class);
            updateClockIntent.setAction(ClockUpdateService.ACTION_WIDGET_CALENDAR_VIEW);
            context.startService(updateClockIntent);
        }
        else if(ADD_PERMISSION_CLICKED.equals(intent.getAction())){
            Intent activityIntent = new Intent(context, PermissionActivity.class);
            context.startActivity(activityIntent);
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
    


    private void setupTheme(Context context) {


       // UserProfile userProfile =   ProfileReader.getUserProfileFromKatsunaServices(context);
        mUserProfileContainer = ProfileReader.getKatsunaUserProfile(context);
     //   System.out.println("the user profile is:"+context.getPackageName().toString());
//        if(userProfile == null) {
//            userProfile = ProfileReader.getUserProfileFromAppSettings(context);
//        }
        colorProfile = mUserProfileContainer.getColorProfile();
        System.out.println("im in finding colorProfile"+colorProfile);
        mTheme = getTheme(colorProfile);
       // setTheme(mTheme);
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
