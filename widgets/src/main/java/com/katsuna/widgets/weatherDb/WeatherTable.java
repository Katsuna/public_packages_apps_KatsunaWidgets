package com.katsuna.widgets.weatherDb;

import android.database.sqlite.SQLiteDatabase;


public class WeatherTable {

    public static final String TABLE_WEATHER = "CurrentWeather";

    public static final String id = "id";
    public static final String temp = "temperature";
    public static final String wind = "wind";
    public static final String rain = "rain";
    public static final String snow = "snow";
    public static final String clouds = "clouds";
    public static final String timestamp = "timestamp";


    private static final String TABLE_CREATE = "create table "
            + TABLE_WEATHER
            + "("
            + id + " TEXT PRIMARY KEY,"
            + wind + " TEXT,"
            + rain + " TEXT,"
            + snow + " TEXT,"
            + clouds + " TEXT,"
            + timestamp + " TEXT" +")";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_WEATHER);
        onCreate(database);
    }

}
