package com.katsuna.widgets.commons.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.katsuna.widgets.commons.entities.LauncherAccess;
import com.katsuna.widgets.commons.providers.LauncherProvider;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LauncherAccessReader {

    public static List<LauncherAccess> getLauncherStats(Context context) {
        List<LauncherAccess> lAccesses = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(LauncherProvider.URI_LAUNCHER_ACCESS,
                null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    LauncherAccess lAccess = new LauncherAccess();
                    lAccess.setId(cursor.getLong(cursor.getColumnIndex(LauncherAccess.COL_ID)));
                    lAccess.setComponent(cursor.getString(cursor.getColumnIndex(LauncherAccess.COL_COMPONENT)));
                    lAccess.setUser(cursor.getString(cursor.getColumnIndex(LauncherAccess.COL_USER)));
                    lAccess.setTime(cursor.getString(cursor.getColumnIndex(LauncherAccess.COL_TIME)));
                    lAccesses.add(lAccess);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return lAccesses;
    }

    public static HashMap<String, Integer> getLauncherStatsPerApp(Context context) {
        HashMap<String, Integer> output = new HashMap<>();
        String[] projection = new String[]{"count(" + LauncherAccess.COL_USER + ") as launch_count", LauncherAccess.COL_USER};
        String selection = LauncherAccess.COL_TIME + ">? GROUP BY (" + LauncherAccess.COL_USER + ")";
        DateTime oneMonthAgo = new DateTime().minusMonths(1);
        String[] selectionArgs = new String[]{ oneMonthAgo.toString() };
        Cursor cursor = context.getContentResolver().query(LauncherProvider.URI_LAUNCHER_ACCESS,
                projection, selection, selectionArgs, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int count = cursor.getInt(0);
                    String intentUri = cursor.getString(1);
                    output.put(intentUri, count);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return output;
    }

    public static void save(Context context, LauncherAccess lAccess) {
        ContentValues values = new ContentValues();
        values.put(LauncherAccess.COL_COMPONENT, lAccess.getComponent());
        values.put(LauncherAccess.COL_USER, lAccess.getUser());
        values.put(LauncherAccess.COL_TIME, new DateTime().toString());

        try {
            context.getContentResolver().insert(LauncherProvider.URI_LAUNCHER_ACCESS, values);
        } catch (Exception ex) {
            Log.w(LauncherAccessReader.class, ex.getMessage());
        }
    }

}
