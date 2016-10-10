package lnm.weatherwidget;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by nikos on 26-Sep-16.
 */
public class WeatherWidgetService extends Service {



    final private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("Adsdasd");
//            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
//                BatteryInfo batteryInfo = new BatteryInfo(intent);
//                Intent updateIntent = new Intent(context, UpdateService.class);
//                updateIntent.setAction(UpdateService.ACTION_BATTERY_CHANGED);
//                batteryInfo.saveToIntent(updateIntent);
//                context.startService(updateIntent);
//            } else if (Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
//                Intent updateIntent = new Intent(context, UpdateService.class);
//                updateIntent.setAction(UpdateService.ACTION_BATTERY_LOW);
//                context.startService(updateIntent);
//            } else if (Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())) {
//                Intent updateIntent = new Intent(context, UpdateService.class);
//                updateIntent.setAction(UpdateService.ACTION_BATTERY_OKAY);
//                context.startService(updateIntent);
//            }
        }
    };

    /**
     * Creates the MonitorService.
     */
    public WeatherWidgetService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Adsdasd2");

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
        intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return Service.START_STICKY;
    }


}
