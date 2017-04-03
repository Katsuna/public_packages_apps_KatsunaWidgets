package com.katsuna.widgets.commons.providers;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.katsuna.widgets.commons.entities.KeyboardEvent;

public class KeyboardProvider {

    public static final String TAG = "KeyboardProvider";
    public static final String AUTHORITY = "com.katsuna.services.datastore.providers.KeyboardProvider";
    public static final String SCHEME = "content://";

    public static final String KEYBOARD = SCHEME + AUTHORITY + "/keyboard";
    public static final String KEYBOARD_BASE = KEYBOARD + "/";
    public static final Uri URI_KEYBOARD = Uri.parse(KEYBOARD);

    public static void save(Context context, KeyboardEvent keyboardEvent) {
        ContentValues values = new ContentValues();
        values.put(KeyboardEvent.COL_CODE, keyboardEvent.getCode());
        // Character logging disabled.
        values.put(KeyboardEvent.COL_CHARACTER, "");
        values.put(KeyboardEvent.COL_TIME, System.currentTimeMillis());

        try {
            context.getContentResolver().insert(URI_KEYBOARD, values);
        } catch (IllegalArgumentException ex) {
            // Keyboard provider is missing.
            // Do nothing because katsuna services is not installed and this happens for
            // standalone apps installation from google play store.
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }
}
