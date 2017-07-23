package com.katsuna.widgets.commons;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.katsuna.commons.utils.Log;


public class PermissionActivity extends Activity {

    private static final String TAG = "PermissionsActivity";

    private static final int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION};


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
            Log.d(TAG, "missing permissions " );
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            Log.d(TAG, "permissions granted!" );

            closeActivity(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        boolean permissionsGranted = false;
        switch (requestCode) {
            case PERMISSION_ALL: {
                Log.d(TAG, "onRequestPermissionsResult permissions accepted: " + grantResults.length);
                if(hasPermissions(this, PERMISSIONS)) {
                    permissionsGranted = true;

                }

                break;
            }
        }
        Log.e(TAG, "onRequestPermissionsResult finishing");
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
        Log.d(TAG, "closing Activity: " + permissionsGranted);
        Intent intent = new Intent();
        intent.putExtra("permissionsGranted", permissionsGranted);
        setResult(RESULT_OK, intent);
        finish();
    }

}
