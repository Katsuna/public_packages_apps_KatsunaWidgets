package lnm.weatherwidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

import weatherParser.WeatherData;


public class WeatherWidgetUpdateService extends IntentService {



    public static final String EXTRA_WIDGET_IDS = "com.em.batterywidget.extra.WIDGET_IDS";

    /**
     * Creates an UpdateService.
     */
    public WeatherWidgetUpdateService() {
        super("UpdateService");
    }

    /**
     * @param intent
     * @see IntentService
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Start!!!5");

        if (intent != null) {
            final String action = intent.getAction();


//            RemoteViews remoteViews = createRemoteViews(weatherData);
//            final int[] widgetIds = intent.getIntArrayExtra(EXTRA_WIDGET_IDS);
//            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
//            appWidgetManager.updateAppWidget(widgetIds, remoteViews);
        }

    }

    private RemoteViews createRemoteViews(WeatherData weatherData) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.weather_widget_view);
//        remoteViews.setImageViewResource(R.id.battery_view, R.drawable.battery);
//        remoteViews.setViewVisibility(R.id.percent100, (level <= 100 && level > 90) ?
//                View.VISIBLE : View.INVISIBLE);
//
//        remoteViews.setTextViewText(R.id.batterytext, String.valueOf(level) + "%");
      Intent activityIntent = new Intent(this, WidgetActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.weather_widget_view, pendingIntent);
        return remoteViews;
    }

}
