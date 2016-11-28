package com.katsuna.clockwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ClockBootReceiver extends BroadcastReceiver {

    public ClockBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ClockWidget.getNumberOfWidgets(context) > 0) {
            // ensure service is running
            context.startService(new Intent(context, ClockMonitorService.class));
        }
    }
}
