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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.katsuna.commons.utils.DeviceUtils;
import com.katsuna.widgets.R;
import com.katsuna.widgets.commons.PermissionActivity;
import com.katsuna.widgets.weatherDb.Weather;
import com.katsuna.widgets.weatherParser.JSONWeatherParser;

import static android.content.Context.LOCATION_SERVICE;
import static com.katsuna.widgets.weatherwidget.WeatherMonitorService.MY_PERMISSIONS_ACCESS_FINE_LOCATION;


public class AlarmReceiver extends BroadcastReceiver implements LocationListener {
    public static final int REQUEST_CODE_CURRENT = 12345;
    public static final int REQUEST_CODE_FORECAST = 23456;
    public static final int REQUEST_CODE_LONG_FORECAST = 34567;
    public static final String LONG_FORECAST = "long_forecast";
    public static final String SHORT_FORECAST = "short_forecast";
    public static final String CURRENT = "current";



    private static final String DEFAULT_CITY = "Athens";
    private static final long LOCATION_REFRESH_TIME = 3600 * 1000 * 3;
    private static final float LOCATION_REFRESH_DISTANCE = 1000;
    public String recentCity = "";
    String longitude = "";
    String latitude = "";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private int apiCounter=0;
    private int currentCounter=0;
    private int shortCounter=0;
    private int longCounter=0;


    LocationManager locationManager;

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        ////System.out.println("I m here");
        String action = intent.getAction();

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

//        if (DeviceUtils.isUserSetupcd App Complete(context)) {
//            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
//                Intent activityIntent = new Intent(context, PermissionActivity.class);
//                context.startActivity(activityIntent);
//            }
//        }


        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        } else {


            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
            else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }



        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
//                LOCATION_REFRESH_DISTANCE, this);
        getLastBestLocation();

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            ////System.out.println("I m boot");

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String interval = sp.getString("refreshInterval", "1");
            //System.out.println("REFRESH INTERVAL IS: "+ interval);
            if (!interval.equals("0")) {
                getWeather();
            }


        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            // Get weather if last attempt failed or if 'update location in background' is activated
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            String interval = sp.getString("refreshInterval", "1");
            if (!interval.equals("0") &&
                    (sp.getBoolean("backgroundRefreshFailed", false) )) {
                getWeather();
            }
        } else {
            ////System.out.println("I m else alarm" + action);
            if (action != null) {
                switch (action) {
                    case CURRENT:
                        getCurrentWeather();
                        break;
                    case SHORT_FORECAST:
                        getShortWeather();
                        break;
                    case LONG_FORECAST:
                        getLongWeather();
                        break;
                }
            } else {
                getWeather();
            }

        }


        Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
        updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
        context.startService(updateWeatherIntent);
    }



    private void getCurrentWeather() {
        //Log.d("Alarm", "Recurring alarm; requesting download service.");
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
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            if(!sp.getString("lastShortterm", "").equals("")){

                List<Weather> forecast = new ArrayList<>();
                forecast = JSONWeatherParser.parseShortTermWidgetJson(sp.getString("lastShortterm", ""), context);
                ////System.out.println("CurrentWeather from prefs"+sp.getString("lastShortterm", ""));
            }
            else {
               // failed = true;
            }
            failed = true;
        }
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("backgroundRefreshFailed", failed);
        editor.apply();
    }

    private void getShortWeather() {
        //Log.d("Alarm", "Recurring alarm; requesting download service.Short weather");
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
        //Log.d("Alarm", "Recurring alarm; requesting download service.Long weather");
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
        //Log.d("Alarm", "Recurring alarm; requesting download service.");
        ////System.out.println("Recurring alarm; requesting download service.");
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

//    private boolean isUpdateLocation() {
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
//        return preferences.getBoolean("updateLocationAutomatically", false);
//    }

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
            //Log.e("LocationManager", "Error while trying to stop listening for location updates. This is probably a permissions issue", e);
        }
        //Log.i("LOCATION (" + location.getProvider().toUpperCase() + ")", location.getLatitude() + ", " + location.getLongitude());
        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        ////System.out.println("location changed lat:"+location.getLatitude() +" long:"+ location.getLongitude());
        //Log.i("onLocationChanged","location changed lat:"+location.getLatitude() +" long:"+ location.getLongitude());

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
               // ////System.out.println("day weather call");

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

                currentCounter++;
                apiCounter++;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                //Log.d("MainActivity", "Current Timestamp: " + format);

                writeToFile("current Weather calls:"+currentCounter+" - total:"+apiCounter +"-Time:"+format,context );

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

                shortCounter++;
                apiCounter++;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
                //Log.d("MainActivity", "Current Timestamp: " + format);

                writeToFile("Short Weather calls:"+currentCounter+" - total:"+apiCounter +"-Time:"+format,context );

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

                if (!latitude.equals("")) {
                    url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + latitude + "&lon=" + longitude + "&lang=" + language + "&mode=json&appid=" + apiKey);

                longCounter++;
                apiCounter++;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
                String format = simpleDateFormat.format(new Date());
//                Log.d("MainActivity", "Current Timestamp: " + format);

                writeToFile("Long Weather calls:"+currentCounter+" - total:"+apiCounter +"-Time:"+format,context );
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
            return null;
        }

        protected void onPostExecute(Void v) {

        }
    }


    private void getLastBestLocation() {
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


    private void writeToFile(String data,Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            //Log.e("Exception", "File write failed: " + e.toString());
        }
    }

}


