package com.katsuna.widgets.commons.utils;

import android.content.ContentValues;
import android.content.Context;

import org.joda.time.DateTime;

import com.katsuna.widgets.commons.entities.KatsounaError;
import com.katsuna.widgets.commons.providers.ErrorProvider;

public class ExceptionLogger {

    public static void save(Context context, String application, String message) {
        ContentValues values = new ContentValues();
        values.put(KatsounaError.COL_TIME, new DateTime().toString());
        values.put(KatsounaError.COL_APPLICATION, application);
        values.put(KatsounaError.COL_MESSAGE, message);

        try {
            context.getContentResolver().insert(ErrorProvider.URI_ERRORS, values);
        } catch (Exception ex) {
            Log.w(ExceptionLogger.class, ex.getMessage());
        }
    }
}
