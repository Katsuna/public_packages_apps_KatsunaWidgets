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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.UserDictionary;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import com.katsuna.widgets.weatherParser.WeatherData;


public class WeatherContentProvider extends ContentProvider {
    private WeatherDbHandler weatherDb;

    // used for the UriMacher
    private static final int CURRENT = 30;
    private static final int FORECAST = 40;

    private static final String AUTHORITY = "daemon_miner_app.sqlite.com.katsuna.weatherDb.WeatherContentProvider";

    private static final String WEATHER_PATH = "CurrentWeather";
    private static final String FORECAST_PATH = "ForecastWeather";



    public static final Uri CWEATHER_URI = Uri.parse("content://" + AUTHORITY
            + "/" + WEATHER_PATH);

    public static final Uri FWEATHER_URI = Uri.parse("content://" + AUTHORITY
            + "/" + FORECAST_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/CURRENT";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/todo";

    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, WEATHER_PATH, CURRENT);
        sURIMatcher.addURI(AUTHORITY, FORECAST_PATH, FORECAST);

    }

    ////*********Retrieve Data***********/////////
    // A "projection" defines the columns that will be returned for each row
    String[] mProjection =
            {
                    UserDictionary.Words._ID,    // Contract class constant for the _ID column name
                    UserDictionary.Words.WORD,   // Contract class constant for the word column name
                    UserDictionary.Words.LOCALE  // Contract class constant for the locale column name
            };

    // Defines a string to contain the selection clause
    String mSelectionClause = null;

    // Initializes an array to contain selection arguments
    String[] mSelectionArgs = {""};
    //////*************//////////

    @Override
    public boolean onCreate() {
        weatherDb = new WeatherDbHandler(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table


        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CURRENT:
                queryBuilder.setTables(WeatherTable.TABLE_WEATHER);
                queryBuilder.appendWhere(WeatherTable.id + "="
                        + uri.getLastPathSegment());
                break;
            case FORECAST:
                queryBuilder.setTables(ForecastTable.TABLE_FORECAST);
                // adding the ID to the original query
                queryBuilder.appendWhere(ForecastTable.num + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = weatherDb.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }



    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = weatherDb.getWritableDatabase();
        long id = 0;
     //   System.out.println("-----"+uriType+"-"+uri.toString());
        switch (uriType) {
            case CURRENT:
                id = sqlDB.insert(WeatherTable.TABLE_WEATHER, null, values);
                break;
            case FORECAST:
                id = sqlDB.insert(ForecastTable.TABLE_FORECAST, null, values);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(WEATHER_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = weatherDb.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CURRENT:
                rowsDeleted = sqlDB.delete(WeatherTable.TABLE_WEATHER, selection,
                        selectionArgs);
                break;
            case FORECAST:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            WeatherTable.TABLE_WEATHER,
                            WeatherTable.id + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(
                            WeatherTable.TABLE_WEATHER,
                            WeatherTable.id + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = weatherDb.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CURRENT:
                rowsUpdated = sqlDB.update(WeatherTable.TABLE_WEATHER,
                        values,
                        selection,
                        selectionArgs);
                break;
            case FORECAST:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(WeatherTable.TABLE_WEATHER,
                            values,
                            WeatherTable.id + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(WeatherTable.TABLE_WEATHER,
                            values,
                            WeatherTable.id + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = { WeatherTable.rain,
                WeatherTable.clouds, WeatherTable.snow, WeatherTable.temp, WeatherTable.timestamp,
                WeatherTable.id };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }

    public WeatherData getCurrentWeatherFromContentProvider(Context context){
        /*
        * This defines a one-element String array to contain the selection argument.
        */
        String[] mSelectionArgs = {""};


        // Remember to insert code here to check for invalid or malicious input.

        // If the word is the empty string, gets everything
        //        if (TextUtils.isEmpty(mSearchString)) {
        //            // Setting the selection clause to null will return all words
        //            mSelectionClause = null;
        //            mSelectionArgs[0] = "";
        //
        //        } else {
        //            // Constructs a selection clause that matches the word that the user entered.
        //            mSelectionClause = UserDictionary.Words.WORD + " = ?";
        //
        //            // Moves the user's input string to the selection arguments.
        //            mSelectionArgs[0] = mSearchString;
        //
        //        }

        // Does a query against the table and returns a Cursor object
        Cursor mCursor = context.getContentResolver().query(
                CWEATHER_URI,  // The content URI of the words table
                null,                       // The columns to return for each row
                null,                   // Either null, or the word the user entered
                null,                    // Either empty, or the string the user entered
                null);                       // The sort order for the returned rows

// Some providers return null if an error occurs, others throw an exception
        if (null == mCursor) {
    /*
     * Insert code here to handle the error. Be sure not to use the cursor! You may want to
     * call android.util.Log.e() to log this error.
     *
     */
// If the Cursor is empty, the provider found no matches
        } else if (mCursor.getCount() < 1) {

    /*
     * Insert code here to notify the user that the search was unsuccessful. This isn't necessarily
     * an error. You may want to offer the user the option to insert a new row, or re-type the
     * search term.
     */

        } else {
            // Insert code here to do something with the results
        //    System.out.println("----------"+ mCursor.getString(1)+","+mCursor.getString(2));
        }

        return null;
    }

}

