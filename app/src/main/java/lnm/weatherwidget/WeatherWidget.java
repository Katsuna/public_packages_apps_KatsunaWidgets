package lnm.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Random;

import weatherDb.WeatherContentProvider;
import weatherDb.WeatherDbHandler;

public class WeatherWidget extends AppWidgetProvider {

    public static WeatherDbHandler wDBHandler;
    public static WeatherContentProvider weatherContentProvider;

    public static int getNumberOfWidgets(final Context context) {
        ComponentName componentName = new ComponentName(context, WeatherWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] activeWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        if (activeWidgetIds != null) {
            return activeWidgetIds.length;
        } else {
            return 0;
        }
    }


    public boolean isStandaloneWidget(Context context){
        for (PackageInfo pack : context.getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    Log.d("Example", "provider: " + provider.authority);
                    if(provider.authority.equals("daemon_miner_app.sqlite.weatherDb.WeatherContentProvider")){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    @Override
    public void onEnabled(Context context) {
        System.out.println("I'm here1");
        WidgetVersion.instance().setIsStandalone(isStandaloneWidget(context));
        if(WidgetVersion.instance().isStandalone){
            wDBHandler = new WeatherDbHandler(context);

            
        }
        else{
            weatherContentProvider = new WeatherContentProvider();
        }

        super.onEnabled(context);
        context.startService(new Intent(context, WeatherWidgetUpdateService.class));
    }
    private static final String ACTION_CLICK = "ACTION_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        System.out.println("I'm here");
        if(WidgetVersion.instance().isStandalone){
            System.out.println("I'm in the stand");
            wDBHandler.getCurrentWeather();

        }
        else{
            weatherContentProvider.getCurrentWeatherFromContentProvider(context);
        }
        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                WeatherWidget.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {
            // create some random data
            int number = (new Random().nextInt(100));

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.weather_widget_view);
            Log.w("WidgetExample", String.valueOf(number));
            // Set the text
            remoteViews.setTextViewText(R.id.update, String.valueOf(number));

            // Register an onClickListener
            Intent intent = new Intent(context, WeatherWidget.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
