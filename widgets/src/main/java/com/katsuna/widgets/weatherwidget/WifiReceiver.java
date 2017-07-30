package com.katsuna.widgets.weatherwidget;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        if(info != null && info.isConnected()) {
            context.startService(new Intent(context, WeatherMonitorService.class));
            Intent updateWeatherIntent = new Intent(context, WeatherUpdateService.class);
            updateWeatherIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
            context.startService(updateWeatherIntent);
            WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
        }
    }
}
