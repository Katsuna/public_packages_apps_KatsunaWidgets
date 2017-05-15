
package com.katsuna.widgets.batterywidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.katsuna.commons.entities.ColorProfile;
import com.katsuna.commons.entities.ColorProfileKey;
import com.katsuna.commons.entities.UserProfileContainer;
import com.katsuna.commons.utils.ColorCalc;
import com.katsuna.commons.utils.ProfileReader;
import com.katsuna.widgets.R;
import com.katsuna.widgets.clockwidget.ClockUpdateService;
import com.katsuna.widgets.commons.WidgetCollection;
import com.katsuna.widgets.utils.BatterySaverUtil;
import com.katsuna.widgets.weatherwidget.WeatherUpdateService;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 */
public class BatteryUpdateService extends IntentService {

    public static final String ACTION_BATTERY_CHANGED = "com.em.batterywidget.action.BATTERY_CHANGED";
    public static final String ACTION_BATTERY_LOW = "com.em.batterywidget.action.BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "com.em.batterywidget.action.BATTERY_OKAY";
    public static final String ACTION_WIDGET_UPDATE = "com.em.batterywidget.action.WIDGET_UPDATE";

    public static final String EXTRA_WIDGET_IDS = "com.em.batterywidget.extra.WIDGET_IDS";
    public static final String ACTION_BATTERY_BACK = "com.em.batterywidget.action.BATTERY_CHANGED";
    public static final String ACTION_WEATHER_CHOICE = "com.katsuna.batterywidget.action.weather_choice";
    public static final String ACTION_CLOCK_CHOICE =  "com.katsuna.batterywidget.action.clock_choice";
    public static final String ACTION_BATTERY_CHOICE =  "com.katsuna.batterywidget.action.battery_choice";
    public static final String ACTION_ENERGY_MODE_CHOICE =  "com.katsuna.batterywidget.action.energy_mode";
    public static final String ACTION_ENERGY_MODE_OFF_CHOICE =  "com.katsuna.batterywidget.action.energy_mode_off";

    private boolean battery_saveMode =false;

    UserProfileContainer mUserProfileContainer;
    ColorProfile colorProfile;
    /**
     * Creates an BatteryUpdateService.
     */
    public BatteryUpdateService() {
        super("BatteryUpdateService");
    }

    /**
     * @param intent
     * @see IntentService
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            System.out.println("Im here also somehow"+action);
            if ((ACTION_BATTERY_CHANGED.equals(action ) && WidgetCollection.extended == false) ) {
                BatteryInfo newBatteryInfo = new BatteryInfo(intent);

                final int level = newBatteryInfo.getLevel();
                final boolean isCharging = newBatteryInfo.isCharging();
                RemoteViews remoteViews = createRemoteViews(level, isCharging,0);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);

                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                BatteryInfo oldBatteryInfo = new BatteryInfo(sharedPreferences);
                if (oldBatteryInfo.getLevel() != newBatteryInfo.getLevel()) {
                    Database database = new Database(this);
                    database.openWrite().insert(new DatabaseEntry(newBatteryInfo.getLevel()));
                    database.close();
                }

                newBatteryInfo.saveToSharedPreferences(sharedPreferences);
            } else if (ACTION_BATTERY_LOW.equals(action)) {
                // TODO
            } else if (ACTION_BATTERY_OKAY.equals(action)) {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1500);
            } else if (ACTION_WIDGET_UPDATE.equals(action)) {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(this);
                BatteryInfo batteryInfo = new BatteryInfo(sharedPreferences);
                final int level = batteryInfo.getLevel();
                final boolean isCharging = batteryInfo.isCharging();
                RemoteViews remoteViews = createRemoteViews(level, isCharging,0);
                final int[] widgetIds = intent.getIntArrayExtra(EXTRA_WIDGET_IDS);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(widgetIds, remoteViews);
            }
            else  if (ACTION_BATTERY_BACK.equals(action)) {
                System.out.println("Im called after back clicked");
//                BatteryInfo newBatteryInfo = new BatteryInfo(intent);
//
//                final int level = newBatteryInfo.getLevel();
//                final boolean isCharging = newBatteryInfo.isCharging();
//
//
//                SharedPreferences sharedPreferences = PreferenceManager
//                        .getDefaultSharedPreferences(this);
//                BatteryInfo oldBatteryInfo = new BatteryInfo(sharedPreferences);
//                if (oldBatteryInfo.getLevel() != newBatteryInfo.getLevel()) {
//                    Database database = new Database(this);
//                    database.openWrite().insert(new DatabaseEntry(newBatteryInfo.getLevel()));
//                    database.close();
//                }
//
//                newBatteryInfo.saveToSharedPreferences(sharedPreferences);
//                RemoteViews remoteViews = createRemoteViews(level, isCharging, 1);
//                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
//                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setupTheme(this);

        if (intent != null) {
            System.out.println("IM in here");

            final String action = intent.getAction();
            System.out.println("Started  battery service with action:"+action);

            final int level = Math.round(getBatteryLevel());
            final boolean isCharging = isConnected(this);

            if (ACTION_BATTERY_BACK.equals(action)) {

                RemoteViews remoteViews = createRemoteViews(level, isCharging,0);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_WEATHER_CHOICE.equals(action)){


                RemoteViews remoteViews = createRemoteViews(level, isCharging,1);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_CLOCK_CHOICE.equals(action)){

                RemoteViews remoteViews = createRemoteViews(level, isCharging,2);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_BATTERY_CHOICE.equals(action)){
                System.out.println("IM in here");
                Intent updateIntent = new Intent(this, ClockUpdateService.class);
                updateIntent.setAction(ClockUpdateService.ACTION_BATTERY_CHOICE);
                this.startService(updateIntent);

                Intent updateWeatherIntent = new Intent(this, WeatherUpdateService.class);
                updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_BATTERY_CHOICE);
                this.startService(updateWeatherIntent);


                RemoteViews remoteViews = null; createRemoteViews(level, isCharging,3);
                if(battery_saveMode) {
                    remoteViews = createRemoteViews(level, isCharging, 4);
                }
                else{
                    remoteViews = createRemoteViews(level, isCharging,5);

                }
                int color1 = ColorCalc.getColor(getApplicationContext(),
                        ColorProfileKey.ACCENT1_COLOR, colorProfile);
                remoteViews.setInt(R.id.calendar_btn, "setBackgroundColor", color1);
                remoteViews.setInt(R.id.forecast_btn, "setBackgroundColor", color1);
                remoteViews.setInt(R.id.energy_mode_btn, "setBackgroundColor", color1);

                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if(ACTION_ENERGY_MODE_CHOICE.equals(action) || ACTION_ENERGY_MODE_OFF_CHOICE.equals(action)){
                Intent updateIntent = new Intent(this, ClockUpdateService.class);
                updateIntent.setAction(ClockUpdateService.ACTION_BATTERY_CHOICE);
                this.startService(updateIntent);

                Intent updateWeatherIntent = new Intent(this, WeatherUpdateService.class);
                updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_BATTERY_CHOICE);
                this.startService(updateWeatherIntent);

                RemoteViews remoteViews = null;
                if(!battery_saveMode) {
                    remoteViews = createRemoteViews(level, isCharging, 4);
                    BatterySaverUtil.enable();
                    battery_saveMode = true;
                }
                else{
                    remoteViews = createRemoteViews(level, isCharging,5);
                    BatterySaverUtil.disable();
                    battery_saveMode = false;

                }

                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
//            else if(){
//                Intent updateIntent = new Intent(this, ClockUpdateService.class);
//                updateIntent.setAction(ClockUpdateService.ACTION_BATTERY_CHOICE);
//                this.startService(updateIntent);
//
//                Intent updateWeatherIntent = new Intent(this, WeatherUpdateService.class);
//                updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_BATTERY_CHOICE);
//                this.startService(updateWeatherIntent);
//
//                RemoteViews remoteViews = createRemoteViews(level, isCharging,5);
//                BatterySaverUtil.disable();
//
//
//                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
//                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//                appWidgetManager.updateAppWidget(componentName, remoteViews);
//            }

        }
        return START_NOT_STICKY;
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }

    private void setupTheme(Context context) {
        UserProfileContainer userProfileContainer = ProfileReader.getKatsunaUserProfile(context);
        colorProfile = userProfileContainer.getColorProfile();
        System.out.println("im out"+colorProfile);
    }
    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    /**
     * Creates the RemoteViews object to be shown for the calendar_widget view.
     *
     * @param level      battery level
     * @param isCharging whether the battery has been charging
     * @return the RemoteViews
     */
    private RemoteViews createRemoteViews(final int level, final boolean isCharging, int backFlag) {
        RemoteViews remoteViews = null;

            remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_v3);
            int color1 = ColorCalc.getColor(getApplicationContext(),
                    ColorProfileKey.ACCENT1_COLOR, colorProfile);
            remoteViews.setInt(R.id.calendar_btn, "setBackgroundColor", color1);
            remoteViews.setInt(R.id.forecast_btn, "setBackgroundColor", color1);
            remoteViews.setInt(R.id.energy_mode_btn, "setBackgroundColor", color1);
            remoteViews.setOnClickPendingIntent(R.id.energy_mode_btn, getPendingSelfIntent(this, WidgetCollection.ENERGY_MODE_CLICKED));


        if (backFlag ==4){
            remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_v3);
            remoteViews.setTextViewTextSize(R.id.energy_mode_txt, TypedValue.COMPLEX_UNIT_SP,0);

            remoteViews.setOnClickPendingIntent(R.id.energy_mode_btn, getPendingSelfIntent(this, WidgetCollection.ENERGY_MODE_OFF_CLICKED));

        }
        else if (backFlag ==5){
            remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_v3);
            remoteViews.setTextViewTextSize(R.id.energy_mode_txt, TypedValue.COMPLEX_UNIT_SP,15);

            remoteViews.setOnClickPendingIntent(R.id.energy_mode_btn, getPendingSelfIntent(this, WidgetCollection.ENERGY_MODE_CLICKED));

        }


        if (isCharging)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.battery_charge);

        }
        else if (level > 0 && level <=5)
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_red_5);
        else if (level > 5 && level <=10)
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_red_10);

        else if (level < 10 && level <=20)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_red_20);

        }
        else if (level < 20 && level <=30)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_black_30);

        }
        else if (level <=30 && level <=40)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_black_40);

        }
        else if (level <40 && level <=50)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_black_50);
        }
        else if (level <=50 && level <=60)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_black_60);
        }
        else if (level < 60 && level <=70)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_black_70);
        }
        else if (level <=70 && level <=80)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_black_80);

        }
        else if (level > 80 && level <=90)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.ic_battery_black_90);

        }
        else if (level > 90 && level <=100)
        {
            remoteViews.setImageViewResource(R.id.energy_mode_btn, R.drawable.battery_black);

        }


        remoteViews.setTextViewText(R.id.batterytext, String.valueOf(level) + "%");
//        Intent activityIntent = new Intent(this, WidgetActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
//        remoteViews.setOnClickPendingIntent(R.id.widget_view, pendingIntent);
        return remoteViews;
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, WidgetCollection.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}

