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
package com.katsuna.widgets.weatherwidget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import com.katsuna.widgets.R;

public abstract class GenericRequestTask extends AsyncTask<String, String, TaskOutput> {

    ProgressDialog progressDialog;
    Context context;
    WeatherMonitorService service;
    public int loading = 0;

    public GenericRequestTask(Context context, WeatherMonitorService service, ProgressDialog progressDialog) {
        this.context = context;
        this.service = service;
        this.progressDialog = progressDialog;
    }

    @Override
    protected void onPreExecute() {
        incLoadingCounter();
        if(!progressDialog.isShowing()) {
            progressDialog.setMessage(context.getString(R.string.downloading_data));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    @Override
    protected TaskOutput doInBackground(String... params) {
        TaskOutput output = new TaskOutput();

        String response = "";
        String[] coords = new String[]{};

        if (params != null && params.length > 0) {
            final String zeroParam = params[0];
            if ("cachedResponse".equals(zeroParam)) {
                response = params[1];
                // Actually we did nothing in this case :)
                output.taskResult = TaskResult.SUCCESS;
            } else if ("coords".equals(zeroParam)) {
                String lat = params[1];
                String lon = params[2];
                coords = new String[]{lat, lon};
            }
        }

        if (response.isEmpty()) {
            try {
                URL url = provideURL(coords);
                //Log.i("URL", url.toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
                    BufferedReader r = new BufferedReader(inputStreamReader);

                    int responseCode = urlConnection.getResponseCode();
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        response += line + "\n";
                    }
                    close(r);
                    urlConnection.disconnect();
                    // Background work finished successfully
                    //Log.i("Task", "done successfully");
                    output.taskResult = TaskResult.SUCCESS;
                    // Save date/time for latest successful result
                    service.saveLastUpdateTime(PreferenceManager.getDefaultSharedPreferences(context));
                }
                else if (urlConnection.getResponseCode() == 429) {
                    // Too many requests
                    //Log.i("Task", "too many requests");
                    output.taskResult = TaskResult.TOO_MANY_REQUESTS;
                }
                else {
                    // Bad response from server
                    //Log.i("Task", "bad response " + urlConnection.getResponseCode());
                    output.taskResult = TaskResult.BAD_RESPONSE;
                }
            } catch (IOException e) {
                //Log.e("IOException Data", response);
                e.printStackTrace();
                // Exception while reading data from url connection
                output.taskResult = TaskResult.IO_EXCEPTION;
            }
        }

        if (TaskResult.SUCCESS.equals(output.taskResult)) {
            // Parse JSON data
            ParseResult parseResult = parseResponse(response);
            if (ParseResult.CITY_NOT_FOUND.equals(parseResult)) {
                // Retain previously specified city if current one was not recognized
                restorePreviousCity();
            }
            output.parseResult = parseResult;
        }

        return output;
    }

    @Override
    protected void onPostExecute(TaskOutput output) {
        if(loading == 1) {
            progressDialog.dismiss();
        }
        decLoadingCounter();

        updateMainUI();

        handleTaskOutput(output);
    }

    protected final void handleTaskOutput(TaskOutput output) {
//        switch (output.taskResult) {
//            case SUCCESS: {
//                ParseResult parseResult = output.parseResult;
//                if (ParseResult.CITY_NOT_FOUND.equals(parseResult)) {
//                    Snackbar.make(service.findViewById(android.R.id.content), context.getString(R.string.msg_city_not_found), Snackbar.LENGTH_LONG).show();
//                } else if (ParseResult.JSON_EXCEPTION.equals(parseResult)) {
//                    Snackbar.make(service.findViewById(android.R.id.content), context.getString(R.string.msg_err_parsing_json), Snackbar.LENGTH_LONG).show();
//                }
//                break;
//            }
//            case TOO_MANY_REQUESTS: {
//                Snackbar.make(service.findViewById(android.R.id.content), context.getString(R.string.msg_too_many_requests), Snackbar.LENGTH_LONG).show();
//                break;
//            }
//            case BAD_RESPONSE: {
//                Snackbar.make(service.findViewById(android.R.id.content), context.getString(R.string.msg_connection_problem), Snackbar.LENGTH_LONG).show();
//                break;
//            }
//            case IO_EXCEPTION: {
//                Snackbar.make(service.findViewById(android.R.id.content), context.getString(R.string.msg_connection_not_available), Snackbar.LENGTH_LONG).show();
//                break;
//            }
//        }
    }

    private String getLanguage() {
        String language = Locale.getDefault().getLanguage();
        if (language.equals("cs")) {
            language = "cz";
        }
        return language;
    }

    private URL provideURL(String[] coords) throws UnsupportedEncodingException, MalformedURLException {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String apiKey = sp.getString("apiKey", service.getResources().getString(R.string.open_weather_maps_app_id));

        StringBuilder urlBuilder = new StringBuilder("http://api.openweathermap.org/data/2.5/");
        urlBuilder.append(getAPIName()).append("?");
        if (coords.length == 2) {
            urlBuilder.append("lat=").append(coords[0]).append("&lon=").append(coords[1]);
        } else {
            final String city = sp.getString("city","Athens");
            urlBuilder.append("q=").append(URLEncoder.encode(city, "UTF-8"));
        }
        urlBuilder.append("&lang=").append(getLanguage());
        urlBuilder.append("&mode=json");
        urlBuilder.append("&appid=").append(apiKey);

        return new URL(urlBuilder.toString());
    }

    private void restorePreviousCity() {
        if (!TextUtils.isEmpty(service.recentCity)) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString("city", service.recentCity);
            editor.commit();
            service.recentCity = "";
        }
    }

    private static void close(Closeable x) {
        try {
            if (x != null) {
                x.close();
            }
        } catch (IOException e) {
            //Log.e("IOException Data", "Error occurred while closing stream");
        }
    }

    private void incLoadingCounter() {
        loading++;
    }

    private void decLoadingCounter() {
        loading--;
    }

    protected void updateMainUI() { }

    protected abstract ParseResult parseResponse(String response);
    protected abstract String getAPIName();
}