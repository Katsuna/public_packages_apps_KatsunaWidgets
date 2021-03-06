/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.katsuna.widgets.weatherwidget;


import android.Manifest;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.katsuna.commons.utils.Log;
import com.katsuna.widgets.R;
import com.katsuna.widgets.commons.PermissionActivity;
import com.katsuna.widgets.commons.WidgetCollection;
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


        ComponentName component = new ComponentName(context, WeatherJobService.class);
        JobInfo jobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo = new JobInfo.Builder(JOB_CURRENT_ID, component)
                    .setPeriodic(30 * ONE_MIN)
                    .setPersisted(true)
                    .build();
        } else {
            jobInfo = new JobInfo.Builder(JOB_CURRENT_ID, component)
                    .setPeriodic(30 * ONE_MIN)
                    .setPersisted(true)
                    .build();

        }

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);

        JobInfo shortJobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            shortJobInfo = new JobInfo.Builder(JOB_SHORT_ID, component)
                    .setPeriodic(3 * 60 * ONE_MIN)
                    .setPersisted(true)
                    .build();
        } else {
            shortJobInfo = new JobInfo.Builder(JOB_SHORT_ID, component)
                    .setPeriodic(3 * 60 * ONE_MIN)
                    .setPersisted(true)
                    .build();
        }

        JobScheduler shortJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        shortJobScheduler.schedule(shortJobInfo);

        JobInfo longJobInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            longJobInfo = new JobInfo.Builder(JOB_LONG_ID, component)
                    .setPeriodic(20 * 60 * ONE_MIN)
                    .setPersisted(true)
                    .build();
        } else {
            longJobInfo = new JobInfo.Builder(JOB_LONG_ID, component)
                    .setPeriodic(20 * 60 * ONE_MIN)
                    .setPersisted(true)
                    .build();
        }
        JobScheduler longJobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        longJobScheduler.schedule(longJobInfo);

//        }
    }

    @Override
    public boolean onStartJob(JobParameters params) {
//      System.out.println("onStartjob" + params.getJobId());
//        Log.d("job","startjon"+params.getJobId());
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            Intent activityIntent = new Intent(getApplicationContext(), PermissionActivity.class);
            //activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            getApplicationContext().startActivity(activityIntent);
        } else {

            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);


            getLastBestLocation(getApplicationContext());


            if (params.getJobId() == JOB_CURRENT_ID) {
                getCurrentWeather(getApplicationContext(), params);
            } else if (params.getJobId() == JOB_SHORT_ID) {
                getShortWeather(getApplicationContext(), params);
            } else if (params.getJobId() == JOB_LONG_ID) {
                getLongWeather(getApplicationContext(), params);
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        // whether or not you would like JobScheduler to automatically retry your failed job.
        return false;
    }


    public void getCurrentWeather(Context context, JobParameters params) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.context = context;
        getLastBestLocation(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetWeatherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetWeatherTask().execute();

        }
        jobFinished(params, true);
    }
    public void getCurrentWeather(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.context = context;
        getLastBestLocation(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetWeatherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetWeatherTask().execute();

        }
    }

    public void getShortWeather(Context context, JobParameters params) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.context = context;

        getLastBestLocation(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetShortTermWeatherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetShortTermWeatherTask().execute();

        }
        jobFinished(params, true);

    }

    public void getShortWeather(Context context) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.context = context;

        getLastBestLocation(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            new GetShortTermWeatherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetShortTermWeatherTask().execute();

        }

    }
    public void getLongWeather(Context context,JobParameters params) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.context = context;
        getLastBestLocation(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            new GetLongTermWeatherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetLongTermWeatherTask().execute();

        }
        jobFinished(params, true);

    }
    public void getLongWeather(Context context ) {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.context = context;
        getLastBestLocation(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            new GetLongTermWeatherTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new GetLongTermWeatherTask().execute();

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


        if(locationManager!= null){
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        }

        Location locationGPS = null;
        Location locationNet = null;
//
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);


            locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


        }else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

            locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            return;
        }
        Location bestLocation = null;
        if (locationGPS == null && locationNet == null) {
            List<String> providers = locationManager.getProviders(true);

            for (String provider : providers) {
                locationGPS = locationManager.getLastKnownLocation(provider);
                if (locationGPS == null) {
                    continue;
                }
                if (bestLocation == null || locationGPS.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = locationGPS;
                }
            }
        }

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }
        if (locationGPS != null || locationNet != null) {
            if (0 < GPSLocationTime - NetLocationTime) {
                latitude = String.valueOf(locationGPS.getLatitude());
                longitude = String.valueOf(locationGPS.getLongitude());
            } else {
                latitude = String.valueOf(locationNet.getLatitude());
                longitude = String.valueOf(locationNet.getLongitude());
            }
        }
    }

    public class GetWeatherTask extends AsyncTask<String, String, Void> {

        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            String result = "";
            try {

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

                    if (urlConnection.getResponseCode() == 200) {
                        String line = null;
                        while ((line = r.readLine()) != null) {
                            result += line + "\n";
                        }
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("lastToday", result);
                        editor.commit();
                        editor.apply();
                        WeatherMonitorService.saveLastUpdateTime(sp);


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
            return null;
        }

        protected void onPostExecute(Void v) {
            // Update widgets
            Intent intent = new Intent(context, WidgetCollection.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
// Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
// since it seems the onUpdate() is only fired on that:
            int[] ids = AppWidgetManager.getInstance(context.getApplicationContext())
                    .getAppWidgetIds(new ComponentName(context.getApplicationContext(), WidgetCollection.class));
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);
//            DashClockWeatherExtension.updateDashClock(context);
        }
    }

    class GetShortTermWeatherTask extends AsyncTask<String, String, Void> {

        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            String result = "";
            try {

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
                        editor.commit();
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
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }


    class GetLongTermWeatherTask extends AsyncTask<String, String, Void> {

        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            String result = "";
            try {

                //System.out.println("long term weather call");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String language = Locale.getDefault().getLanguage();
                if (language.equals("cs")) {
                    language = "cz";
                }
                String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));
                URL url = null;
//                System.out.println("before call and lati is:"+latitude);

                if (!latitude.equals("")) {
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&mode=json&appid=" + apiKey);

//                    System.out.println("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&mode=json&appid=" + apiKey);
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
                        editor.commit();
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
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }
}
