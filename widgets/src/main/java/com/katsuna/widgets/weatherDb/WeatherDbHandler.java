package com.katsuna.widgets.weatherDb;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.katsuna.widgets.weatherParser.WeatherData;

public class WeatherDbHandler  extends SQLiteOpenHelper {

    // All Static variablesfddf
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "WeatherDB";

    // Contacts table name
    private static final String TABLE_CurrentWeather = "CurrentWeather";

    // Contacts Table Columns names
    private static final String id = "id";
    private static final String temp = "temperature";
    private static final String wind = "wind";
    private static final String rain = "rain";
    private static final String snow = "snow";
    private static final String clouds = "clouds";
    private static final String timestamp = "timestamp";
    private int trajId=0;
    public int getTrajId() {
        return trajId;
    }

    public void setTrajId(int trajId) {
        this.trajId = trajId;
    }

    public WeatherDbHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        WeatherTable.onCreate(db);
        ForecastTable.onCreate(db);
    }


    // Upgrading database


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Drop older table if existed
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CurrentWeather);
//
//        // Create tables again
//        onCreate(db);
        WeatherTable.onUpgrade(db, oldVersion, newVersion);
        ForecastTable.onUpgrade(db,oldVersion,newVersion);
    }


    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new trajectory Data
    public void addCurrentWeatherRecord(WeatherData weather, int weatherId) {
        Log.d("Sqli","kanw add kati stin basi");
        SQLiteDatabase db = this.getWritableDatabase();
        Date date = new Date();
        String time = new Timestamp(date.getTime()).toString();

        ContentValues values = new ContentValues();
        values.put(id, weatherId);
        values.put(temp, weather.getTemp());

        values.put(wind, weather.getWind().toString());
        values.put(rain, weather.getRain());
        values.put(snow, weather.getSnow());
        values.put(clouds,weather.getClouds().toString());
        values.put(timestamp, time);

        // Inserting Row
        db.insert(TABLE_CurrentWeather, null, values);
        db.close(); // Closing database connection
    }


    public void addCurrentWeatherRecord(SecWeather secWeather, int weatherId) {
        Log.d("Sqli","kanw add kati stin basi");
        SQLiteDatabase db = this.getWritableDatabase();
        Date date = new Date();
        String time = new Timestamp(date.getTime()).toString();

        ContentValues values = new ContentValues();
        values.put(id, weatherId);
        values.put(temp, secWeather.temperature.getTemp());
        values.put(wind, secWeather.wind.toString());
        values.put(rain, secWeather.rain.toString());
        values.put(snow, secWeather.snow.toString());
        values.put(clouds, secWeather.clouds.toString());
        values.put(timestamp, time);

        // Inserting Row
        db.insert(TABLE_CurrentWeather, null, values);
        db.close(); // Closing database connection
    }

    public void addForecastRecord(List<WeatherData> weatherList) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        for (WeatherData weather : weatherList) {
            Date date = new Date();
            String time = new Timestamp(date.getTime()).toString();

            ContentValues values = new ContentValues();
            values.put(ForecastTable.num, String.valueOf(i));
            values.put(ForecastTable.day, weather.getDateTime());
            values.put(ForecastTable.wind, weather.getWind().toString());
            values.put(ForecastTable.rain, weather.getRain());
            values.put(ForecastTable.snow, weather.getSnow());
            values.put(ForecastTable.clouds, weather.getClouds().toString());
            values.put(ForecastTable.temp, weather.getTemp());
            values.put(timestamp, time);

            // Inserting Row
            db.insert(TABLE_CurrentWeather, null, values);
        }
        db.close(); // Closing database connection
    }



    public void deleteExistingForecastRecords(List<WeatherData> weatherList){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = 0;
        for (WeatherData weather : weatherList) {
            i++;
            db.delete(ForecastTable.TABLE_FORECAST, ForecastTable.num + " = ?",
                    new String[] { String.valueOf(i) });
        }
        db.close();

    }




    // Getting records Count
    public int getRecordsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CurrentWeather;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        //	if ( cursor.getCount();
        if (cursor != null && cursor.getCount() == 0) {
            //   cursor.moveToFirst();                       // Always one row returned.
            //  if (cursor.getInt (0) == 0) {               // Zero count means empty table.
            return 0;
        }
        else
        {
            cursor.moveToLast();
            int newId = cursor.getInt(1); // + 1;
            cursor.close();
            return newId;
        }
        //	}
        //	return -1;
    }

    public WeatherData getCurrentWeather(){
        WeatherData wdata = new WeatherData();
        String countQuery = "SELECT  * FROM " + TABLE_CurrentWeather;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        //	if ( cursor.getCount();
        if (cursor != null && cursor.getCount() == 0) {
            //   cursor.moveToFirst();                       // Always one row returned.
            if (cursor.getInt (0) == 0)               // Zero count means empty table.
                return null;
        }
        else
        {

            cursor.moveToFirst();
            String newId = cursor.getString(1); // + 1;
            System.out.println("I read from db:"+cursor.getString(1)+","+cursor.getString(2)+","+cursor.getString(3)+","+cursor.getString(4)+","+cursor.getString(5)+","+cursor.getString(6));
            cursor.close();
            return wdata;
        }

        return wdata;
    }



}
