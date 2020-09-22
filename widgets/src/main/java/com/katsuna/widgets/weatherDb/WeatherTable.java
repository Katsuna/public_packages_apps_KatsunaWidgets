/**
* Copyright (C) 2020 Manos Saratsis
*
* This file is part of Katsuna.
*
* Katsuna is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Katsuna is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with Katsuna.  If not, see <https://www.gnu.org/licenses/>.
*/
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
