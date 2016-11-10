package lnm.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;

import weatherDb.SecWeather;
import weatherDb.Weather;
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



    protected Bitmap getWeatherIcon(String text, Context context) {
        Bitmap myBitmap = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);
        paint.setTextAlign(Paint.Align.CENTER);
        return myBitmap;
    }


    public static void updateWidgets(Context context, Class widgetClass) {
        Intent intent = new Intent(context.getApplicationContext(), widgetClass)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext())
                .getAppWidgetIds(new ComponentName(context.getApplicationContext(), widgetClass));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.getApplicationContext().sendBroadcast(intent);
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
        if(WidgetVersion.instance().isStandalone()){
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
        if(WidgetVersion.instance().isStandalone()){
            System.out.println("I'm in the stand");
            wDBHandler.getCurrentWeather();

        }
        else{
            // if AI_Miner is running (standalone)
//            weatherContentProvider.getCurrentWeatherFromContentProvider(context);
        }
        // Get all ids
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.weather_widget_view);

            Intent intent = new Intent(context, AlarmReceiver.class);

            //REFRESH BUTTON IF NEEDED

//            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
//                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            remoteViews.setOnClickPendingIntent(R.id.widgetButtonRefresh, pendingIntent);

            Intent intent2 = new Intent(context, WidgetActivity.class);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
            remoteViews.setOnClickPendingIntent(R.id.widgetRoot, pendingIntent2);

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            Weather widgetWeather = new Weather();
            if(!sp.getString("lastToday", "").equals("")) {
                System.out.println("im called");
              //  widgetWeather = JSONWeatherParser.getWeather(sp.getString("lastToday", ""), context);
            }
            else {
                try {
                    pendingIntent2.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                return;
            }

            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);

//            remoteViews.setTextViewText(R.id.widgetCity, widgetWeather.getCity() + ", " + widgetWeather.getCountry());
            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription());
            remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
//            remoteViews.setTextViewText(R.id.widgetPressure, widgetWeather.getPressure());
//            remoteViews.setTextViewText(R.id.widgetHumidity, context.getString(R.string.humidity) + ": " + widgetWeather.getHumidity() + " %");
            //remoteViews.setTextViewText(R.id.widgetLastUpdate, widgetWeather.getLastUpdated());
//            remoteViews.setImageViewBitmap(R.id.widgetIcon, getWeatherIcon(widgetWeather.getIcon(), context));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
