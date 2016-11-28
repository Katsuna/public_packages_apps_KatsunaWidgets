package com.katsuna.clockwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Implementation of App Widget functionality.
 */
public class ClockWidget extends AppWidgetProvider {

    private PendingIntent service = null;


    public static int getNumberOfWidgets(final Context context) {
        ComponentName componentName = new ComponentName(context, ClockWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] activeWidgetIds = appWidgetManager.getAppWidgetIds(componentName);
        if (activeWidgetIds != null) {
            return activeWidgetIds.length;
        } else {
            return 0;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("Clock widget","I am n update...");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        // ensure service is running
        context.startService(new Intent(context, ClockMonitorService.class));
        // update the widgets
        Intent updateIntent = new Intent(context, ClockUpdateService.class);
        updateIntent.setAction(ClockUpdateService.ACTION_WIDGET_UPDATE);
        updateIntent.putExtra(ClockUpdateService.EXTRA_WIDGET_IDS, appWidgetIds);
        context.startService(updateIntent);

    }

    @Override
    public void onEnabled(Context context) {

        super.onEnabled(context);
        context.startService(new Intent(context, ClockMonitorService.class));

    }

    @Override
    public void onDeleted(Context context, int[] widgetIds) {
        super.onDeleted(context, widgetIds);
        if (getNumberOfWidgets(context) == 0) {
            // stop monitoring if there are no more widgets on screen
            context.stopService(new Intent(context, ClockMonitorService.class));
        }
    }

}

