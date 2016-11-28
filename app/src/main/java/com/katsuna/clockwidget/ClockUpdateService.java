package com.katsuna.clockwidget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import com.katsuna.R;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ClockUpdateService extends IntentService {

    public static final String ACTION_CLOCK_CHANGED = "clockwidget.action.CLOCK_CHANGED";

    public static final String ACTION_WIDGET_UPDATE = "clockwidget.action.WIDGET_UPDATE";

    public static final String EXTRA_WIDGET_IDS = "clockwidget.extra.WIDGET_IDS";


    public ClockUpdateService() {
        super("BatteryUpdateService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CLOCK_CHANGED.equals(action) || ACTION_WIDGET_UPDATE.equals(action)) {



                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget);
                String []clock = setTime();
                remoteViews.setTextViewText(R.id.appwidget_text, clock[0]);
                remoteViews.setTextViewText(R.id.date, clock[1]);


                ComponentName componentName = new ComponentName(this, ClockWidget.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);


            }

        }


    }

    public String [] setTime() {
        String []clock;
        clock = new String[2];
        Date now = new Date();
        int hours = now.getHours();
        String time;
        if (hours >= 12)
            time = "PM";
        else
            time = "AM";
        hours = (hours > 12) ? hours - 12 : hours;
        hours = (hours == 0) ? hours + 12 : hours;
        int minutes = now.getMinutes();
        int h1 = hours / 10;
        int h2 = hours - h1 * 10;
        int m1 = minutes / 10;
        int m2 = minutes - m1 * 10;
        Format formatter = new SimpleDateFormat("EEEE dd MMMM ");
        String today = formatter.format(new Date());
        String clockUI = String.format("%d:%d%d %s",hours,m1,m2,time);
        clock[0] = clockUI;
        clock[1] = today;
        Log.d("Clock Alarm: ", String.format("%d:%d -> %d %d: %d %d %s date: %s", hours, minutes, h1, h2, m1, m2,time,today));
        return clock;
    }
}
