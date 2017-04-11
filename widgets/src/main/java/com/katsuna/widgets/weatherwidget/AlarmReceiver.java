package com.katsuna.widgets.weatherwidget;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import com.katsuna.widgets.R;

import static android.content.Context.LOCATION_SERVICE;
import static com.katsuna.widgets.weatherwidget.WeatherMonitorService.MY_PERMISSIONS_ACCESS_FINE_LOCATION;


public class AlarmReceiver extends BroadcastReceiver implements LocationListener {
    public static final int REQUEST_CODE_CURRENT = 12345;
    public static final int REQUEST_CODE_FORECAST = 23456;
    public static final int REQUEST_CODE_LONG_FORECAST = 34567;
    private static final String DEFAULT_CITY = "Athens";
    private static final long LOCATION_REFRESH_TIME = 3600 * 1000 * 3;
    private static final float LOCATION_REFRESH_DISTANCE = 1000;
    public String recentCity = "";
    String longitude = "";
    String latitude = "";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    LocationManager locationManager;

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        System.out.println("I m here");

        String action = intent.getStringExtra("action");

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        } else {
          //  showLocationSettingsDialog();
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, this);
        getLastBestLocation();

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            System.out.println("I m boot");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String interval = sp.getString("refreshInterval", "1");
            if (!interval.equals("0")) {
                getWeather();
            }


        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // Get weather if last attempt failed or if 'update location in background' is activated
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String interval = sp.getString("refreshInterval", "1");
            if (!interval.equals("0") &&
                    (sp.getBoolean("backgroundRefreshFailed", false) || isUpdateLocation())) {
                getWeather();
            }
        } else {
            System.out.println("I m else alarm" + action);
            if (action != null) {
                switch (action) {
                    case "current":
                        System.out.println(">:" + action);
                        getCurrentWeather();
                        break;
                    case "forecast":
                        System.out.println(">:2" + action);

                        getShortWeather();
                        break;
                    case "long_forecast":
                        getLongWeather();
                        break;
                }
            } else {
                System.out.println("Im in get weather current else");
                getWeather();
            }

        }


        Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
        updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
        context.startService(updateWeatherIntent);
    }



    private void getCurrentWeather() {
        Log.d("Alarm", "Recurring alarm; requesting download service.");
        boolean failed;
        if (isNetworkAvailable()) {
            failed = false;
//            if (isUpdateLocation()) {
//                new GetLocationAndWeatherTask().execute(); // This method calls the two methods below once it has determined a location
//            } else {
//                new GetWeatherTask().execute();
//            }
            new GetWeatherTask().execute();
        } else {
            failed = true;
        }
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("backgroundRefreshFailed", failed);
        editor.apply();
    }

    private void getShortWeather() {
        Log.d("Alarm", "Recurring alarm; requesting download service.Short weather");
        boolean failed;
        if (isNetworkAvailable()) {
            failed = false;
//            if (isUpdateLocation()) {
//                new GetLocationAndWeatherTask().execute(); // This method calls the two methods below once it has determined a location
//
//            }
            new GetShortTermWeatherTask().execute();
        } else {
            failed = true;
        }
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("backgroundRefreshFailed", failed);
        editor.apply();
    }

    private void getLongWeather() {
        Log.d("Alarm", "Recurring alarm; requesting download service.Long weather");
        boolean failed;
        if (isNetworkAvailable()) {
            failed = false;
//            if (isUpdateLocation()) {
//                new GetLocationAndWeatherTask().execute(); // This method calls the two methods below once it has determined a location
//            }
            new GetLongTermWeatherTask().execute();
        } else {
            failed = true;
        }
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("backgroundRefreshFailed", failed);
        editor.apply();
    }

    private void getWeather() {
        Log.d("Alarm", "Recurring alarm; requesting download service.");
        System.out.println("Recurring alarm; requesting download service.");
        boolean failed;
        if (isNetworkAvailable()) {
            failed = false;
//            if (isUpdateLocation()) {
//                new GetLocationAndWeatherTask().execute(); // This method calls the two methods below once it has determined a location
//            } else {
                new GetWeatherTask().execute();
                new GetLongTermWeatherTask().execute();
                new GetShortTermWeatherTask().execute();
//            }
        } else {
            failed = true;
        }
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("backgroundRefreshFailed", failed);
        editor.apply();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean isUpdateLocation() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean("updateLocationAutomatically", false);
    }

    private void saveLocation(String result) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        recentCity = preferences.getString("city", DEFAULT_CITY);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("city", result);
        editor.commit();

        if (!recentCity.equals(result)) {
            // New location, update weather
            getWeather();
        }
    }

    private void showLocationSettingsDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.location_settings);
        alertDialog.setMessage(R.string.location_settings_message);
        alertDialog.setPositiveButton(R.string.location_settings_button, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    void getCityByLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        } else {
         //   showLocationSettingsDialog();
        }
    }

    // @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCityByLocation();
                }
                return;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            locationManager.removeUpdates(this);
        } catch (SecurityException e) {
            Log.e("LocationManager", "Error while trying to stop listening for location updates. This is probably a permissions issue", e);
        }
        Log.i("LOCATION (" + location.getProvider().toUpperCase() + ")", location.getLatitude() + ", " + location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        System.out.println("location changed lat:"+location.getLatitude() +" long:"+ location.getLongitude());
        Log.i("onLocationChanged","location changed lat:"+location.getLatitude() +" long:"+ location.getLongitude());

//        new ProvideCityNameTask(this, this, progressDialog).execute("coords", Double.toString(latitude), Double.toString(longitude));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public class GetWeatherTask extends AsyncTask<String, String, Void> {

        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {
            String result = "";
            try {
               // System.out.println("day weather call");

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String language = Locale.getDefault().getLanguage();
                if (language.equals("cs")) {
                    language = "cz";
                }
                String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));
                //
                URL url = null;
                if (!latitude.equals("")) {
                    Log.d("api call","From api day forecast inside alarm with lat:"+latitude+"long:"+longitude);

                    url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&appid=" + apiKey);
                } else {
                    Log.d("api call","From api day forecast inside alarm default citty call");

                    url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + URLEncoder.encode(sp.getString("city", DEFAULT_CITY), "UTF-8") + "&lang=" + language + "&appid=" + apiKey);
                }

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                if (urlConnection.getResponseCode() == 200) {
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        result += line + "\n";
                    }
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("lastToday", result);
                    editor.apply();
                    WeatherMonitorService.saveLastUpdateTime(sp);
                } else {
                    // Connection problem
                }
            } catch (IOException e) {
                // No connection
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            // Update widgets
//            AbstractWidgetProvider.updateWidgets(context);
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

             //   System.out.println("short term weather call");

                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String language = Locale.getDefault().getLanguage();
                if (language.equals("cs")) {
                    language = "cz";
                }
                String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));
                URL url = null;
                if (!latitude.equals("")) {
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&mode=json&&appid=" + apiKey);
                } else
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=" + URLEncoder.encode(sp.getString("city", DEFAULT_CITY), "UTF-8") + "&lang=" + language + "&mode=json&appid=" + apiKey);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                if (urlConnection.getResponseCode() == 200) {
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        result += line + "\n";
                    }
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    //  Log.i("JSON short",result);
                    editor.putString("lastShortterm", result);
                    editor.apply();
                } else {
                    // Connection problem
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

           //     System.out.println("long term weather call");
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                String language = Locale.getDefault().getLanguage();
                if (language.equals("cs")) {
                    language = "cz";
                }
                String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));
                URL url = null;

                if (!latitude.equals("")) {
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&mode=json&appid=" + apiKey);
                } else
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=" + URLEncoder.encode(sp.getString("city", DEFAULT_CITY), "UTF-8") + "&lang=" + language + "&mode=json&appid=" + apiKey);


                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                if (urlConnection.getResponseCode() == 200) {
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        result += line + "\n";
                    }
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
                    //   Log.i("JSON long",result);
                    editor.putString("lastLongterm", result);
                    editor.apply();
                } else {
                    // Connection problem
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }


    private void getLastBestLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

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
    public class GetCityNameTask extends AsyncTask<String, String, Void> {
        private static final String TAG = "GetCityNameTask";

        @Override
        protected Void doInBackground(String... params) {
            String lat = params[0];
            String lon = params[1];

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String language = Locale.getDefault().getLanguage();
            if(language.equals("cs")) {
                language = "cz";
            }
            String apiKey = sp.getString("apiKey", context.getResources().getString(R.string.open_weather_maps_app_id));

            try {


                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=&lat=" + lat + "&lon=" + lon + "&lang="+ language +"&appid=" + apiKey);
                Log.d(TAG, "Request: " + url.toString());

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                if (urlConnection.getResponseCode() == 200) {
                    BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String result = "";
                    String line;
                    while ((line = r.readLine()) != null) {
                        result += line + "\n";
                    }
                    Log.d(TAG, "JSON Result: " + result);
                    try {
                        JSONObject reader = new JSONObject(result);
                        String city = reader.getString("name");
                        String country = "";
                        JSONObject countryObj = reader.optJSONObject("sys");
                        if (countryObj != null) {
                            country = ", " + countryObj.getString("country");
                        }
                        Log.d(TAG, "City: " + city + country);
                        String lastCity = PreferenceManager.getDefaultSharedPreferences(context).getString("city", "");
                        String currentCity = city + country;
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("city", currentCity);
                        editor.putBoolean("cityChanged", !currentCity.equals(lastCity));
                        editor.commit();

                    } catch (JSONException e){
                        Log.e(TAG, "An error occurred while reading the JSON object", e);
                    }
                } else {
                    Log.e(TAG, "Error: Response code " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(TAG, "Connection error", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new GetWeatherTask().execute();
            new GetLongTermWeatherTask().execute();
        }
    }

    private static long intervalMillisForRecurringAlarm(String intervalPref) {
        int interval = Integer.parseInt(intervalPref);
        switch (interval) {
            case 0:
                return 0; // special case for cancel
            case 15:
                return AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            case 30:
                return AlarmManager.INTERVAL_HALF_HOUR;
            case 1:
                return AlarmManager.INTERVAL_HOUR;
            case 12:
                return AlarmManager.INTERVAL_HALF_DAY;
            case 24:
                return AlarmManager.INTERVAL_DAY;
            default: // cases 2 and 6 (or any number of hours)
                return interval * 3600000;
        }
    }

    public static void setRecurringAlarm(Context context) {
        System.out.println("I ve been called recurring");
        String intervalPref = PreferenceManager.getDefaultSharedPreferences(context)
                .getString("refreshInterval", "1");
        Intent refresh = new Intent(context, AlarmReceiver.class);
        PendingIntent recurringRefresh = PendingIntent.getBroadcast(context,
                0, refresh, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarms = (AlarmManager) context.getSystemService(
                Context.ALARM_SERVICE);
        long intervalMillis = intervalMillisForRecurringAlarm(intervalPref);
        if (intervalMillis == 0) {
            // Cancel previous alarm
            alarms.cancel(recurringRefresh);
        } else {
            alarms.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + intervalMillis,
                    intervalMillis,
                    recurringRefresh);
        }
    }
}