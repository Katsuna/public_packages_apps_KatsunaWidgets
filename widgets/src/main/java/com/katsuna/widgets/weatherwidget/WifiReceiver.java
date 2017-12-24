package com.katsuna.widgets.weatherwidget;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WifiReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info =
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info.isConnected()) {
                // Wifi is connected
    //            Log.d("Inetify", "Wifi is connected: " + String.valueOf(info));
                WeatherJobService weatherJobService = new WeatherJobService();
                weatherJobService.getCurrentWeather(context);
                weatherJobService.getShortWeather(context);
                weatherJobService.getLongWeather(context);

                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ssid = wifiInfo.getSSID();
            }
        } else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                    ! networkInfo.isConnected()) {
                // Wifi is disconnected
         //       Log.d("Inetify", "Wifi is disconnected: " + String.valueOf(networkInfo));
            }
        }

    }
}
