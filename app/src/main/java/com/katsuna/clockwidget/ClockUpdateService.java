package com.katsuna.clockwidget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.katsuna.R;
import com.katsuna.commons.WidgetCollection;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ClockUpdateService extends IntentService {

    public static final String ACTION_CLOCK_CHANGED = "clockwidget.action.CLOCK_CHANGED";

    public static final String ACTION_WIDGET_UPDATE = "clockwidget.action.WIDGET_UPDATE";

    public static final String EXTRA_WIDGET_IDS = "clockwidget.extra.WIDGET_IDS";

    public static final String ACTION_WIDGET_EXTENDED_CLOCK = "com.katsuna.weatherwidget.action.Clock";

    public ClockUpdateService() {
        super("BatteryUpdateService");

    }

    CalendarView calendar;
    private  Handler handler;

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            final String action = intent.getAction();
            System.out.println("CLOCK CALLED:"+action);
            if ((ACTION_CLOCK_CHANGED.equals(action) || ACTION_WIDGET_UPDATE.equals(action))&& WidgetCollection.extended == false) {
                System.out.println("CLOCK CALLED in");



                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.collection_widget);
                String []clock = setTime();
                remoteViews.setOnClickPendingIntent(R.id.time_root, getPendingSelfIntent(this, WidgetCollection.TIME_CLICKED));

                remoteViews.setTextViewText(R.id.appwidget_text, clock[0]);
                remoteViews.setTextViewText(R.id.date, clock[1]);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);


            }
            else if (ACTION_WIDGET_EXTENDED_CLOCK.equals(action)){
                System.out.println("CLOCK CALLEndar!!!!!!!!");

                initializeCalendar();
                RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.calendar_widget_view);
                String []clock = setTime();
                remoteViews.setOnClickPendingIntent(R.id.calendar_back, getPendingSelfIntent(this, WidgetCollection.BACK_CLICKED));


                remoteViews.setTextViewText(R.id.calendar_appwidget_text, clock[0]);
                remoteViews.setTextViewText(R.id.calendar_date, clock[1]);


                ComponentName componentName = new ComponentName(this, WidgetCollection.class);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                appWidgetManager.updateAppWidget(componentName, remoteViews);
            }

        }


    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {

        Intent intent = new Intent(context, WidgetCollection.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
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

    public void initializeCalendar() {

        Context applicationContext = getApplicationContext();
//        handler = new Handler(applicationContext.getMainLooper());
//
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {

//                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
//                View layout = inflater.inflate(R.layout.calendar_widget_view, null);
//                calendar = (CalendarView) findViewById(R.id.calendar);

                //sets the listener to be notified upon selected date change.
//                calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//                    //show the selected date as a toast
//                    @Override
//                    public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
//                        Toast.makeText(getApplicationContext(), day + "/" + month + "/" + year, Toast.LENGTH_LONG).show();
//                    }
//                });

//            }
//        });




    }


}
