package com.katsuna.weatherwidget;

import android.content.Context;
import android.content.SharedPreferences;


public class Preferences {
    private SharedPreferences sharedPreferences;

    public Preferences(String preference, Context context) {
        sharedPreferences = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
    }

    public void setValue(String key, int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setValue(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setValue(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public int getValue(String key, int value) {
        return sharedPreferences.getInt(key, value);
    }

    public boolean getValue(String key, boolean value) {
        return sharedPreferences.getBoolean(key, value);
    }

    public String getValue(String key, String value) {
        return sharedPreferences.getString(key, value);
    }
}
