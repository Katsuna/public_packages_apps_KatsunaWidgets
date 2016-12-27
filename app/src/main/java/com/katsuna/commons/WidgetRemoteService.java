package com.katsuna.commons;


import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.katsuna.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WidgetRemoteService extends RemoteViewsService {

    private static final String TAG = WidgetRemoteService.class
            .getSimpleName();

    final static int[] mLayoutIds = { R.layout.collection_widget, R.layout.collection_widget_clock, R.layout.collection_widget_battery, R.layout.collection_widget_weather };

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ViewFactory(intent);
    }

    private class ViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private int mInstanceId = AppWidgetManager.INVALID_APPWIDGET_ID;
        private Date mUpdateDate = new Date();

        public ViewFactory(Intent intent) {
            mInstanceId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID );
        }

        @Override
        public void onCreate() {
            Log.i(TAG, "onCreate()");

        }

        @Override
        public void onDataSetChanged() {
            Log.i(TAG, "onDataSetChanged()");

            mUpdateDate = new Date();
        }

        @Override
        public void onDestroy() {
            Log.i(TAG, "onDestroy()");
        }

        @Override
        public int getCount() {
            Log.i(TAG, "getCount() " + mLayoutIds.length);

            return mLayoutIds.length;
        }

        @Override
        public RemoteViews getViewAt(int position) {
            Log.i(TAG, "getViewAt()" + position);

            RemoteViews page = new RemoteViews(getPackageName(), mLayoutIds[position]);


            return page;
        }

        @Override
        public RemoteViews getLoadingView() {
            Log.i(TAG, "getLoadingView()");

            return new RemoteViews(getPackageName(), R.layout.collection_widget_clock);
        }

        @Override
        public int getViewTypeCount() {
            Log.i(TAG, "getViewTypeCount()");

            return mLayoutIds.length;
        }

        @Override
        public long getItemId(int position) {
            Log.i(TAG, "getItemId()");

            return position;
        }

        @Override
        public boolean hasStableIds() {
            Log.i(TAG, "hasStableIds()");

            return true;
        }

    }
}

