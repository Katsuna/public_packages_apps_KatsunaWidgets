
package com.katsuna.batterywidget;

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
import android.view.View;
import android.widget.RemoteViews;

import com.katsuna.R;
import com.katsuna.commons.WidgetCollection;

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

                RemoteViews remoteViews = createRemoteViews(level, isCharging,1);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }
            else if (ACTION_BATTERY_CHOICE.equals(action)){
                System.out.println("IM in here");
                RemoteViews remoteViews = createRemoteViews(level, isCharging,3);
                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }

        }
        return START_NOT_STICKY;
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
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
     * Creates the RemoteViews object to be shown for the widget view.
     *
     * @param level      battery level
     * @param isCharging whether the battery has been charging
     * @return the RemoteViews
     */
    private RemoteViews createRemoteViews(final int level, final boolean isCharging, int backFlag) {
        RemoteViews remoteViews = null;
        if (backFlag == 0) {
            remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget);
            remoteViews.setOnClickPendingIntent(R.id.battery_root, getPendingSelfIntent(this, WidgetCollection.BATTERY_CLICKED));

        }
        else if (backFlag == 1)
            remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_weather);
        else if (backFlag == 2)
            remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_clock);
        else if (backFlag ==3){
            System.out.println("I<M in back 3");
            remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget_battery);
            remoteViews.setOnClickPendingIntent(R.id.energy_mode_btn, getPendingSelfIntent(this, WidgetCollection.ENERGY_MODE_CLICKED));
            remoteViews.setOnClickPendingIntent(R.id.battery_close_btn, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));

        }


        if (isCharging)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.battery_charge);

        }
        else if (level > 0 && level <=5)
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_red_5);
        else if (level > 5 && level <=10)
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_red_10);

        else if (level < 10 && level <=20)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_red_20);

        }
        else if (level < 20 && level <=30)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_black_30);

        }
        else if (level <=30 && level <=40)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_black_40);

        }
        else if (level <40 && level <=50)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_black_50);
        }
        else if (level <=50 && level <=60)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_black_60);
        }
        else if (level < 60 && level <=70)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_black_70);
        }
        else if (level <=70 && level <=80)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_black_80);

        }
        else if (level > 80 && level <=90)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.ic_battery_black_90);

        }
        else if (level > 90 && level <=100)
        {
            remoteViews.setImageViewResource(R.id.battery_view, R.drawable.battery_black);

        }

//        remoteViews.setViewVisibility(R.id.percent100, (level <= 100 && level > 90) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent90, (level <= 90 && level > 80) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent80, (level <= 80 && level > 70) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent70, (level <= 70 && level > 60) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent60, (level <= 60 && level > 50) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent50, (level <= 50 && level > 40) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent40, (level <= 40 && level > 30) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent30, (level <= 30 && level > 20) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent20, (level <= 20 && level > 10) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.percent10, (level <= 10 && level > 0) ?
//                View.VISIBLE : View.INVISIBLE);
//        remoteViews.setViewVisibility(R.id.charge_view, isCharging ?
//                View.VISIBLE : View.INVISIBLE);
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
