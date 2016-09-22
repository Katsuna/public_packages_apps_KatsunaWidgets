package lnm.weatherwidget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;


public class WeatherWidgetUpdateService extends Service {


//    public int onStartCommand(Intent intent, int startId) {
//        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
//                .getApplicationContext());
//
//        int[] allWidgetIds = intent
//                .getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
//
////                ComponentName thisWidget = new ComponentName(getApplicationContext(),
////                                MyWidgetProvider.class);
////                int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
//
//        for (int widgetId : allWidgetIds) {
//            // create some random data
//            int number = (new Random().nextInt(100));
//
//            RemoteViews remoteViews = new RemoteViews(this
//                    .getApplicationContext().getPackageName(),
//                    R.layout.activity_main);
//            Log.w("WidgetExample", String.valueOf(number));
//            // Set the text
//            remoteViews.setTextViewText(R.id.update,
//                    "Random: " + String.valueOf(number));
//
//            // Register an onClickListener
//            Intent clickIntent = new Intent(this.getApplicationContext(),
//                    MyWidgetProvider.class);
//
//            clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
//            clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
//                    allWidgetIds);
//
//            PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                    getApplicationContext(), 0, clickIntent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
//            appWidgetManager.updateAppWidget(widgetId, remoteViews);
//        }
//        stopSelf();
//
//        return super.onStartCommand(intent, startId);
//    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}