package com.katsuna.weatherwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.List;

import lnm.weatherwidget.R;
import weatherDb.Weather;


public class ExtendedWeatherWidget extends AppWidgetProvider {

    private List<Weather> longTermWeather = new ArrayList<>();
    private List<Weather> longTermTodayWeather = new ArrayList<>();
    private List<Weather> longTermTomorrowWeather = new ArrayList<>();


//    public WeatherRecyclerAdapter getAdapter(int id) {
//        WeatherRecyclerAdapter weatherRecyclerAdapter;
//        if (id == 0) {
//            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermTodayWeather);
//        } else if (id == 1) {
//            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermTomorrowWeather);
//        } else {
//            weatherRecyclerAdapter = new WeatherRecyclerAdapter(this, longTermWeather);
//        }
//        return weatherRecyclerAdapter;
//    }
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

//        System.out.println("I'm here");
//        if(WidgetVersion.instance().isStandalone()){
//            System.out.println("I'm in the stand");
//            wDBHandler.getCurrentWeather();
//
//        }
//        else{
//            // if AI_Miner is running (standalone)
////            weatherContentProvider.getCurrentWeatherFromContentProvider(context);
//        }
//        // Get all ids
        for (int widgetId : appWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.extended_widget_view);
//
//            Intent intent = new Intent(context, AlarmReceiver.class);
//
//            //REFRESH BUTTON IF NEEDED
//
////            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
////                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
////            remoteViews.setOnClickPendingIntent(R.id.widgetButtonRefresh, pendingIntent);
//
            Intent intent2 = new Intent(context, WidgetActivity.class);
            PendingIntent pendingIntent2 = PendingIntent.getActivity(context, 0, intent2, 0);
            remoteViews.setOnClickPendingIntent(R.id.extendedRoot, pendingIntent2);

//            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//            Weather widgetWeather = new Weather();
//            if(!sp.getString("lastToday", "").equals("")) {
//                System.out.println("im called");
//                widgetWeather = JSONWeatherParser.parseWidgetJson(sp.getString("lastToday", ""), context);
//            }
//            else {
//                System.out.println("im called 2");
//
//                try {
//                    pendingIntent2.send();
//                } catch (PendingIntent.CanceledException e) {
//                    e.printStackTrace();
//                }
//                return;
//            }
//
//            DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(context);
//
////            remoteViews.setTextViewText(R.id.widgetCity, widgetWeather.getCity() + ", " + widgetWeather.getCountry());
//            remoteViews.setTextViewText(R.id.widgetTemperature, widgetWeather.getTemperature());
//            remoteViews.setTextViewText(R.id.widgetDescription, widgetWeather.getDescription());
//            System.out.println("WInd: "+ widgetWeather.getWind());
//            remoteViews.setTextViewText(R.id.widgetWind, widgetWeather.getWind());
////            remoteViews.setTextViewText(R.id.widgetPressure, widgetWeather.getPressure());
////            remoteViews.setTextViewText(R.id.widgetHumidity, context.getString(R.string.humidity) + ": " + widgetWeather.getHumidity() + " %");
//            //remoteViews.setTextViewText(R.id.widgetLastUpdate, widgetWeather.getLastUpdated());
//            remoteViews.setImageViewResource(R.id.widgetIcon, getWeatherIconId(widgetWeather.getIcon(), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
//
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

}
