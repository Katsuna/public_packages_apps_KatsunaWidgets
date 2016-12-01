package com.katsuna.weatherwidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.katsuna.R;
import com.katsuna.weatherDb.ForecastTable;
import com.katsuna.weatherDb.SecWeather;
import com.katsuna.weatherDb.WeatherContentProvider;
import com.katsuna.weatherDb.WeatherTable;
import com.katsuna.weatherParser.ForecastWeatherData;
import com.katsuna.weatherParser.JSONWeatherParser;
import com.katsuna.weatherParser.WeatherData;
import com.katsuna.weatherParser.WeatherForecastParser;
import com.katsuna.weatherParser.WeatherHttpClient;


public class WeatherWidgetUpdateService extends IntentService {

    public static final String ACTION_FETCH_WEATHER_DATA = "lnm.weatherwiget.action.FETCH_WEATHER_DATA";
    public static final String ACTION_RECORD_WEATHER_DATA = "lnm.weatherwiget.action.RECORD_WEATHER_DATA";
    public static final String ACTION_FETCH_FORECAST_WEATHER_DATA = "lnm.weatherwiget.action.FETCH_FORECAST_WEATHER_DATA";
    public static final String ACTION_FETCH_LONG_FORECAST_WEATHER_DATA = "lnm.weatherwiget.action.FETCH_LONG_FORECAST_WEATHER_DATA";

    private SecWeather secWeather;


    public boolean isStandaloneWidget(){
        for (PackageInfo pack : getPackageManager().getInstalledPackages(PackageManager.GET_PROVIDERS)) {
            ProviderInfo[] providers = pack.providers;
            if (providers != null) {
                for (ProviderInfo provider : providers) {
                    Log.d("Example", "provider: " + provider.authority);
                    if(provider.authority.equals("daemon_miner_app.sqlite.com.katsuna.weatherDb.WeatherContentProvider")){
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /**
     * Creates an BatteryUpdateService.
     */
    public WeatherWidgetUpdateService() {
        super("BatteryUpdateService");
      //  isStandaloneWidget();
        System.out.println("Start!!!5.1");
    }

    /**
     * @param intent
     * @see IntentService
     */
    @Override
    protected void onHandleIntent(Intent intent) {
       //System.out.println("Start!!!5");


        if (intent != null) {
            final String action = intent.getAction();
            System.out.println("="+action);
            if (ACTION_FETCH_WEATHER_DATA.equals(action)) {
                fetchWeatherData(ACTION_FETCH_WEATHER_DATA);
            } else if (ACTION_FETCH_FORECAST_WEATHER_DATA.equals(action)) {
                fetchWeatherData(ACTION_FETCH_FORECAST_WEATHER_DATA);
            }
            else if (ACTION_FETCH_LONG_FORECAST_WEATHER_DATA.equals(action)){
                fetchWeatherData(ACTION_FETCH_LONG_FORECAST_WEATHER_DATA);
            }
        }

    }

    private void fetchWeatherData(String action) {
        String address = getAddress();
        WeatherHttpClient client =  new WeatherHttpClient();
        switch (action) {
            case ACTION_FETCH_WEATHER_DATA:
                JSONObject weatherObj = client.getCurrentWeatherJSON(getApplicationContext(), address);
                System.out.println("SecWeather:" + weatherObj);
                try {
                    secWeather = JSONWeatherParser.getSecWeather(weatherObj);
                    addWeatherRecord(secWeather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ACTION_FETCH_FORECAST_WEATHER_DATA:
                JSONObject forecastObj = client.getForecastWeatherJSON(getApplicationContext(), address);
                System.out.println("Forecast:" + forecastObj);
                WeatherForecastParser forecast = new WeatherForecastParser(forecastObj);
                addForecastRecord(forecast.getForecasts());
                break;
            case ACTION_FETCH_LONG_FORECAST_WEATHER_DATA:
                JSONObject longForecastObj = client.getLongForecastWeatherJSON(getApplicationContext(), address);
                System.out.println("Forecast2:" + longForecastObj);

                WeatherForecastParser longForecast = new WeatherForecastParser(longForecastObj);
                addForecastRecord(longForecast.getForecasts());
                break;
        }
    }

    public void addForecastRecord(List<ForecastWeatherData> weatherList) {
        Log.d("Sqli","kanw add kati stin basi");

        int i = 0;
        for (ForecastWeatherData weather : weatherList) {
            Date date = new Date();
            String time = new Timestamp(date.getTime()).toString();

            ContentValues values = new ContentValues();
            values.put(ForecastTable.num, String.valueOf(i));
            values.put(ForecastTable.day, weather.getDateTime());
            values.put(ForecastTable.wind, weather.getWind().toString());
            values.put(ForecastTable.rain, weather.getRain());
            values.put(ForecastTable.snow, weather.getSnow());
            values.put(ForecastTable.clouds, weather.getClouds().toString());
            values.put(ForecastTable.temp, weather.getTemp());
            values.put(ForecastTable.timestamp, time);

            // Inserting Row
            Uri uri = getContentResolver().insert(WeatherContentProvider.FWEATHER_URI, values);
        }
    }

    public void addWeatherRecord(SecWeather secWeather) {
        Log.d("Sqli","kanw add kati stin basi");

        Date date = new Date();
        String time = new Timestamp(date.getTime()).toString();

        ContentValues values = new ContentValues();

        values.put(WeatherTable.id, secWeather.currentCondition.getWeatherId());

        values.put(WeatherTable.wind, secWeather.wind.toString());
        values.put(WeatherTable.rain, secWeather.rain.toString());
        values.put(WeatherTable.snow, secWeather.snow.toString());
        values.put(WeatherTable.clouds, secWeather.clouds.toString());
        values.put(WeatherTable.timestamp, time);
    //    WeatherWidget.wDBHandler.addCurrentWeatherRecord(secWeather, secWeather.currentCondition.getWeatherId());
        // Inserting Row
//        Uri uri = getContentResolver().insert(WeatherContentProvider.CWEATHER_URI, values);

    }

    private RemoteViews createRemoteViews(WeatherData weatherData) {
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget);
//        remoteViews.setImageViewResource(R.id.battery_view, R.drawable.battery);
//        remoteViews.setViewVisibility(R.id.percent100, (level <= 100 && level > 90) ?
//                View.VISIBLE : View.INVISIBLE);
//
//        remoteViews.setTextViewText(R.id.batterytext, String.valueOf(level) + "%");

        //remoteViews.setOnClickPendingIntent(R.id.weather_widget_view, pendingIntent);
        return remoteViews;
    }

    public  String getAddress(){
        //MyLocation location = new MyLocation();
        Location location = new Location("location");
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            //38.0398905,23.7692327
            List<Address> address = geoCoder.getFromLocation(38.0398905, 23.7692327, 1);

            System.out.println(">>>>*"+address.get(0).getLocality());
            int maxLines = address.get(0).getMaxAddressLineIndex();
            for (int i=0; i<maxLines; i++) {
                String addressStr = address.get(0).getAddressLine(i);
                builder.append(addressStr);
                builder.append(" ");
            }

            String finalAddress = builder.toString(); //This is the complete address.
            String localityCountry = "";
            if (address.size() > 0)
                localityCountry = address.get(0).getLocality()+","+address.get(0).getCountryCode();
            return localityCountry;
        } catch (IOException e) {}
        catch (NullPointerException e) {

        }
        return null;
    }
}
