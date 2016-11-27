package com.katsuna.weatherParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.katsuna.R;
import com.katsuna.weatherwidget.WidgetActivity;
import com.katsuna.utils.UnitConvertor;
import com.katsuna.weatherDb.SecWeather;
import com.katsuna.weatherDb.Weather;

public class JSONWeatherParser {
    private final String JSON_COD = "cod";
    private final String JSON_MESSAGE = "message";
    protected final String JSON_CALCTIME = "calctime";
    private final String JSON_CALCTIME_TOTAL = "total";
    protected final String JSON_LIST = "list";
    private final String JSON_ID = "id";
    private final String JSON_COORD = "coord";
    private final String JSON_COUNTRY = "country";
    private final String JSON_NAME = "name";
    private final String JSON_DT_CALC = "dt_calc";
    private final String JSON_STATIONS_COUNT = "stations_count";
    private final String JSON_URL = "url";
    private final String JSON_CITY = "city";
    private final String JSON_UNITS = "units";
    private final String JSON_MODEL = "model";
    static final String JSON_SYS = "sys";

    private static String setWeatherIcon(int actualId, int hourOfDay, Context context) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = context.getString(R.string.weather_sunny);
            } else {
                icon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = context.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = context.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = context.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = context.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = context.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = context.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }

    public static Weather parseWidgetJson(String result, Context context) {
        try {
            //WidgetActivity.initMappings();

            JSONObject reader = new JSONObject(result);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            // Temperature
            float temperature = UnitConvertor.convertTemperature(Float.parseFloat(reader.optJSONObject("main").getString("temp").toString()), sp);
            if (sp.getBoolean("temperatureInteger", false)) {
                temperature = Math.round(temperature);
            }

            // Wind
            double wind;
            try {
                wind = Double.parseDouble(reader.optJSONObject("wind").getString("speed").toString());
            } catch (Exception e) {
                e.printStackTrace();
                wind = 0;
            }
            wind = UnitConvertor.convertWind(wind, sp);

            // Pressure
            double pressure = UnitConvertor.convertPressure((float) Double.parseDouble(reader.optJSONObject("main").getString("pressure").toString()), sp);

            long lastUpdateTimeInMillis = sp.getLong("lastUpdate", -1);
            String lastUpdate;
            if (lastUpdateTimeInMillis < 0) {
                // No time
                lastUpdate = "";
            } else {
                lastUpdate = context.getString(R.string.last_update_widget, WidgetActivity.formatTimeWithDayIfNotToday(context, lastUpdateTimeInMillis));
            }

            String description = reader.optJSONArray("weather").getJSONObject(0).getString("description");
            description = description.substring(0,1).toUpperCase() + description.substring(1).toLowerCase();

            Weather widgetWeather = new Weather();
            widgetWeather.setCity(reader.getString("name"));
            widgetWeather.setCountry(reader.optJSONObject("sys").getString("country"));
            widgetWeather.setTemperature(Math.round(temperature) + "°" + localize(sp, context, "unit", "C"));
            widgetWeather.setDescription(description);
            widgetWeather.setWind(context.getString(R.string.wind) + ": " + new DecimalFormat("#.0").format(wind) + " " + localize(sp, context, "speedUnit", "m/s")
                    + (widgetWeather.isWindDirectionAvailable() ? " " + WidgetActivity.getWindDirectionString(sp, context, widgetWeather) : ""));
            widgetWeather.setPressure(context.getString(R.string.pressure) + ": " + new DecimalFormat("#.0").format(pressure) + " " + localize(sp, context, "pressureUnit", "hPa"));
            widgetWeather.setHumidity(reader.optJSONObject("main").getString("humidity"));
            widgetWeather.setSunrise(reader.optJSONObject("sys").getString("sunrise"));
            widgetWeather.setSunset(reader.optJSONObject("sys").getString("sunset"));
            widgetWeather.setIcon(setWeatherIcon(Integer.parseInt(reader.optJSONArray("weather").getJSONObject(0).getString("id")), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
            widgetWeather.setLastUpdated(lastUpdate);

            return widgetWeather;
        } catch (JSONException e) {
            Log.e("JSONException Data", result);
            e.printStackTrace();
            return new Weather();
        }
    }

    protected static String localize(SharedPreferences sp, Context context, String preferenceKey,
                                     String defaultValueKey) {
        return WidgetActivity.localize(sp, context, preferenceKey, defaultValueKey);
    }

    public static SecWeather getWeather(JSONObject jObj) throws JSONException {
        SecWeather secWeather = new SecWeather();


        // We start extracting the info
        MyLocation loc = new MyLocation();

        JSONObject coordObj = getObject("coord", jObj);
        loc.setLatitude(getFloat("lat", coordObj));
        loc.setLongitude(getFloat("lon", coordObj));

        JSONObject sysObj = getObject("sys", jObj);
        loc.setCountry(getString("country", sysObj));
        loc.setSunrise(getInt("sunrise", sysObj));
        loc.setSunset(getInt("sunset", sysObj));
        loc.setCity(getString("name", jObj));
        secWeather.location = loc;

        JSONArray jArr = jObj.getJSONArray("secWeather");

        JSONObject JSONWeather = jArr.getJSONObject(0);
        secWeather.currentCondition.setWeatherId(getInt("id", JSONWeather));
        secWeather.currentCondition.setDescr(getString("description", JSONWeather));
        secWeather.currentCondition.setCondition(getString("main", JSONWeather));
        secWeather.currentCondition.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        secWeather.currentCondition.setHumidity(getInt("humidity", mainObj));
        secWeather.currentCondition.setPressure(getInt("pressure", mainObj));
        secWeather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        secWeather.temperature.setMinTemp(getFloat("temp_min", mainObj));
        secWeather.temperature.setTemp(getFloat("temp", mainObj));

        JSONObject wObj = getObject("wind", jObj);
        secWeather.wind.setSpeed(getFloat("speed", wObj));
        secWeather.wind.setDeg(getFloat("deg", wObj));

        JSONObject cObj = getObject("clouds", jObj);
        secWeather.clouds.setPerc(getInt("all", cObj));


        return secWeather;
    }


    public static List<SecWeather> getForecastWeather(JSONObject jObj) {


        List<SecWeather> forecasts;

        //   this.sys = jsonSys != null ? new Sys (jsonSys) : null;

        JSONArray jsonForecasts = jObj.optJSONArray("list");
        if (jsonForecasts != null) {
            forecasts = new ArrayList<SecWeather>(jsonForecasts.length());
            for (int i = 0; i < jsonForecasts.length(); i++) {
                JSONObject jsonForecast = jsonForecasts.optJSONObject(i);
                try {
                    forecasts.add(getWeather(jsonForecast));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            forecasts = Collections.emptyList();
        }

        return forecasts;
    }

    public Forecast getLongForecastWeather(JSONObject jObj) {
        Forecast forecast = new Forecast();

        return forecast;
    }


    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        JSONObject subObj = jObj.getJSONObject(tagName);
        return subObj;
    }

    private static String getString(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getString(tagName);
    }

    private static float getFloat(String tagName, JSONObject jObj) throws JSONException {
        return (float) jObj.getDouble(tagName);
    }

    private static int getInt(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getInt(tagName);
    }

}