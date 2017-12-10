package com.katsuna.widgets.commons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.katsuna.commons.utils.Log;
import com.katsuna.widgets.clockwidget.ClockUpdateService;
import com.katsuna.widgets.weatherwidget.WeatherJobService;
import com.katsuna.widgets.weatherwidget.WeatherMonitorService;
import com.katsuna.widgets.weatherwidget.WeatherUpdateService;


public class PermissionActivity extends Activity {

    private static final String TAG = "PermissionsActivity";

    private static final int PERMISSION_ALL = 1;
    public static String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
         //   finish();

            closeActivity(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean permissionsGranted = false;
        switch (requestCode) {
            case PERMISSION_ALL: {
                if(hasPermissions(this, PERMISSIONS)) {
                    permissionsGranted = true;
                    Intent clockIntent = new Intent(this, ClockUpdateService.class);
                    clockIntent.setAction(ClockUpdateService.ACTION_WIDGET_UPDATE);
                    this.startService(clockIntent);
//                    Intent updateIntent = new Intent(this, WeatherUpdateService.class);
//
//                    updateIntent.setAction(WeatherUpdateService.ACTION_WIDGET_UPDATE);
//                    this.startService(updateIntent);

                    WeatherJobService jobService = new WeatherJobService();
                    jobService.schedule(this);
                    jobService.getCurrentWeather(this);
                    jobService.getShortWeather(this);
                    jobService.getLongWeather(this);
//                    this.startService(new Intent(this, WeatherMonitorService.class));

                }

                break;
            }
        }
        closeActivity(permissionsGranted);
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void closeActivity(boolean permissionsGranted) {
        Intent intent = new Intent();
        intent.putExtra("permissionsGranted", permissionsGranted);
        setResult(RESULT_OK, intent);
        finish();
    }

}
