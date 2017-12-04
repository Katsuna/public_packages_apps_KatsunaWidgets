package com.katsuna.widgets.weatherwidget;


import android.Manifest;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.katsuna.widgets.R;
import com.katsuna.widgets.weatherDb.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WeatherJobService extends JobService implements LocationListener {

    private static final int JOB_CURRENT_ID = 1;
    private static final int JOB_SHORT_ID = 2;
    private static final int JOB_LONG_ID = 3;

    LocationManager locationManager;

    private static final int ONE_MIN = 60 * 1000;

    private Context context;

    String longitude = "";
    String latitude = "";



    public void schedule(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {


            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
            else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }



        }
        getLastBestLocation(context);
//        if( jobId == JOB_CURRENT_ID) {
            ComponentName component = new ComponentName(context, WeatherJobService.class);
            JobInfo.Builder currentBuilder = new JobInfo.Builder(JOB_CURRENT_ID, component)
                    // schedule it to run any time between 1 - 5 minutes
                    .setMinimumLatency(ONE_MIN)
                    .setOverrideDeadline(60 * ONE_MIN);
            JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(currentBuilder.build());
//        }
//        else if( jobId == JOB_SHORT_ID){
//            ComponentName component = new ComponentName(context, WeatherJobService.class);
            JobInfo.Builder shortBuilder = new JobInfo.Builder(JOB_SHORT_ID, component)
                    // schedule it to run any time between 1 - 5 minutes
                    .setMinimumLatency(ONE_MIN)
                    .setOverrideDeadline(60 * ONE_MIN);
            JobScheduler shortJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        shortJobScheduler.schedule(shortBuilder.build());
//        }
//        else if( jobId == JOB_LONG_ID){
//            ComponentName component = new ComponentName(context, WeatherJobService.class);
            JobInfo.Builder longBuilder = new JobInfo.Builder(JOB_LONG_ID, component)
                    // schedule it to run any time between 1 - 5 minutes
                    .setMinimumLatency(ONE_MIN)
                    .setOverrideDeadline(60 * ONE_MIN);
            JobScheduler longJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        longJobScheduler.schedule(longBuilder.build());
//        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {


            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
            else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }



        }
        getLastBestLocation(context);




        if( params.getJobId() == JOB_CURRENT_ID){
            getCurrentWeather(context);
        }
        else if(params.getJobId() == JOB_SHORT_ID){
            getShortWeather(context);
        }
        else if(params.getJobId() == JOB_LONG_ID){
            getLongWeather(context);
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // whether or not you would like JobScheduler to automatically retry your failed job.
        return false;
    }

    public void getCurrentWeather(Context context) {
        if(locationManager == null){
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }
        getLastBestLocation(context);

        String result = "";
        try {
           // context =  this;
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String language = Locale.getDefault().getLanguage();
            if (language.equals("cs")) {
                language = "cz";
            }
            String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));
            //
            URL url = null;
            if (!latitude.equals("")) {
                //Log.d("api call","From api day forecast inside alarm with lat:"+latitude+"long:"+longitude);
                url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&appid=" + apiKey);


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                //Log.d("MainActivity", "Current Timestamp: " + format);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                System.out.println("IM in getWeather execute2");

                if (urlConnection.getResponseCode() == 200) {
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        result += line + "\n";
                    }
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("lastToday", result);
                    editor.apply();
                    WeatherMonitorService.saveLastUpdateTime(sp);

                    System.out.println("IM in getWeather execute");
                    Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
                    updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
                    context.startService(updateWeatherIntent);

                } else {
                    // Connection problem
                }
            } else {
                //Log.d("api call","No permissions for call or no location");
                //default city url
                //  url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(sp.getString("city", DEFAULT_CITY), "UTF-8") + "&lang=" + language + "&appid=" + apiKey);
            }
        } catch (IOException e) {
            // No connection
        }
    }

    public void getShortWeather(Context context){
        if(locationManager == null){
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }
        getLastBestLocation(context);

        String result = "";
        try {
          //  context =  this;

            //   ////System.out.println("short term weather call");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String language = Locale.getDefault().getLanguage();
            if (language.equals("cs")) {
                language = "cz";
            }
            String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));
            URL url = null;
            if (!latitude.equals("")) {
                url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&mode=json&&appid=" + apiKey);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                //Log.d("MainActivity", "Current Timestamp: " + format);


                if (urlConnection.getResponseCode() == 200) {
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        result += line + "\n";
                    }
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    //  //Log.i("JSON short",result);
                    editor.putString("lastShortterm", result);
                    editor.apply();
                } else {
                    //Log.d("Connection problem", "fail Response");
                }
            } else {

                //  url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=" + URLEncoder.encode(sp.getString("city", DEFAULT_CITY), "UTF-8") + "&lang=" + language + "&mode=json&appid=" + apiKey);

            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public void getLongWeather(Context context){
        if(locationManager == null){
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }
        getLastBestLocation(context);

        String result = "";
        try {
        //    context =  this;

            //System.out.println("long term weather call");
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String language = Locale.getDefault().getLanguage();
            if (language.equals("cs")) {
                language = "cz";
            }
            String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));
            URL url = null;

            if (!latitude.equals("")) {
                url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&mode=json&appid=" + apiKey);


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
//                Log.d("MainActivity", "Current Timestamp: " + format);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                if (urlConnection.getResponseCode() == 200) {
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        result += line + "\n";
                    }
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    //   //Log.i("JSON long",result);
                    editor.putString("lastLongterm", result);
                    editor.apply();
                } else {
                    // Connection problem
                }
            } else {
                //TODO weather from preferennces
                //   url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + URLEncoder.encode(sp.getString("city", DEFAULT_CITY), "UTF-8") + "&lang=" + language + "&mode=json&appid=" + apiKey);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            //Log.e("LocationManager", "Error while trying to stop listening for location updates. This is probably a permissions issue", e);
        }
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private void getLastBestLocation(Context context) {
        Location locationGPS = null;
        Location locationNet = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        }
        else if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else{
            return;
        }


        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }
        if( locationGPS != null || locationNet!= null) {
            if (0 < GPSLocationTime - NetLocationTime) {
                latitude = String.valueOf(locationGPS.getLatitude());
                longitude = String.valueOf(locationGPS.getLongitude());
            } else {
                latitude = String.valueOf(locationNet.getLatitude());
                longitude = String.valueOf(locationNet.getLongitude());
            }
        }
    }
}
