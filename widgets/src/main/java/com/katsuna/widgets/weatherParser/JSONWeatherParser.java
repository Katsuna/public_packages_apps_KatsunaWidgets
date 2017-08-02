package com.katsuna.widgets.weatherParser;

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

import com.katsuna.widgets.R;
import com.katsuna.widgets.utils.UnitConvertor;
import com.katsuna.widgets.weatherDb.SecWeather;
import com.katsuna.widgets.weatherDb.Weather;
import com.katsuna.widgets.weatherwidget.WeatherMonitorService;

import static com.katsuna.widgets.weatherwidget.WeatherMonitorService.getRainString;

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
                icon = context.getString(R.string.weather_sunny);
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
            //WeatherMonitorService.initMappings();

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
                lastUpdate = R.string.last_update + WeatherMonitorService.formatTimeWithDayIfNotToday(context, lastUpdateTimeInMillis);
            }



            String description = reader.optJSONArray("weather").getJSONObject(0).getString("description");
            description = description.substring(0,1).toUpperCase() + description.substring(1).toLowerCase();

            Weather widgetWeather = new Weather();
            widgetWeather.setCity(reader.getString("name"));
            widgetWeather.setCountry(reader.optJSONObject("sys").getString("country"));
            widgetWeather.setTemperature(Math.round(temperature) + "°" + localize(sp, context, "unit", "C"));
            widgetWeather.setDescription(description);
            widgetWeather.setWind(context.getString(R.string.wind) + ": " + new DecimalFormat("#.0").format(wind) + " " + localize(sp, context, "speedUnit", "m/s")
                    + (widgetWeather.isWindDirectionAvailable() ? " " + WeatherMonitorService.getWindDirectionString(sp, context, widgetWeather) : ""));
            widgetWeather.setPressure(context.getString(R.string.pressure) + ": " + new DecimalFormat("#.0").format(pressure) + " " + localize(sp, context, "pressureUnit", "hPa"));
            widgetWeather.setHumidity(reader.optJSONObject("main").getString("humidity"));
//            widgetWeather.setPrecipitation(reader.optJSONObject("main").getString("precipProbability"));
            widgetWeather.setSunrise(reader.optJSONObject("sys").getString("sunrise"));
            widgetWeather.setSunset(reader.optJSONObject("sys").getString("sunset"));
            widgetWeather.setIcon(setWeatherIcon(Integer.parseInt(reader.optJSONArray("weather").getJSONObject(0).getString("id")), Calendar.getInstance().get(Calendar.HOUR_OF_DAY), context));
            widgetWeather.setLastUpdated(lastUpdate);
            if(reader.optJSONObject("wind").has("deg"))
                widgetWeather.setWindDirectionDegree(Double.valueOf(reader.optJSONObject("wind").getString("deg")));
            else
                widgetWeather.setWindDirectionDegree(null);
            //Log.e("JSONException Data", result);


            return widgetWeather;
        } catch (JSONException e) {
           // Log.e("JSONException Data", result);
            e.printStackTrace();
            return new Weather();
        }
    }

    public static List<Weather> parseShortTermWidgetJson(String result, Context context) {
        ArrayList<Weather> forecast =  new ArrayList<>();
        int i;
        try {
            JSONObject reader = new JSONObject(result);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            JSONArray list = reader.getJSONArray("list");
            for (i = 0; i < list.length(); i++) {
                //   JSONObject listItem = list.getJSONObject(i);
                Weather weather = new Weather();//getWeather(listItem);

                JSONObject listItem = list.getJSONObject(i);
                JSONObject main = listItem.getJSONObject("main");
                float temperature = UnitConvertor.convertTemperature(Float.parseFloat(main.getString("temp").toString()), sp);
                weather.setDate(listItem.getString("dt"));
                weather.setTemperature(Math.round(temperature) + "°" + localize(sp, context, "unit", "C"));
                weather.setDescription(listItem.optJSONArray("weather").getJSONObject(0).getString("description"));
                JSONObject windObj = listItem.optJSONObject("wind");
                if (windObj != null) {
                    weather.setWind(windObj.getString("speed"));
                    weather.setWindDirectionDegree(windObj.getDouble("deg"));
                }
                weather.setPressure(main.getString("pressure"));
                weather.setHumidity(main.getString("humidity"));

                JSONObject rainObj = listItem.optJSONObject("rain");
                String rain = "";
                if (rainObj != null) {
                    rain = getRainString(rainObj);
                } else {
                    JSONObject snowObj = listItem.optJSONObject("snow");
                    if (snowObj != null) {
                        rain = getRainString(snowObj);
                    } else {
                        rain = "0";
                    }
                }
                weather.setRain(rain);

                final String idString = listItem.optJSONArray("weather").getJSONObject(0).getString("id");
                weather.setId(idString);

                final String dateMsString = listItem.getString("dt") + "000";
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(dateMsString));
                weather.setIcon(setWeatherIcon(Integer.parseInt(idString), cal.get(Calendar.HOUR_OF_DAY),context));

                forecast.add(weather);


            }


            return forecast;
        } catch (JSONException e) {
            //Log.e("JSONException Data long", result);
            e.printStackTrace();
            return forecast;
        }
    }



    public static List<Weather> parseLongTermWidgetJson(String result, Context context) {
        ArrayList<Weather> forecast =  new ArrayList<>();
        int i;
        try {
            JSONObject reader = new JSONObject(result);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

            JSONArray list = reader.getJSONArray("list");
            for (i = 0; i < list.length(); i++) {
             //   JSONObject listItem = list.getJSONObject(i);
                Weather weather = new Weather();//getWeather(listItem);

                JSONObject listItem = list.getJSONObject(i);
                JSONObject main = listItem;//listItem.getJSONObject("weather");

                float temperature = UnitConvertor.convertTemperature(Float.parseFloat(main.getJSONObject("temp").getString("day").toString()), sp);
                if (sp.getBoolean("temperatureInteger", false)) {
                    temperature = Math.round(temperature);
                }

                weather.setDate(listItem.getString("dt"));
                weather.setTemperature(Math.round(temperature) + "°" + localize(sp, context, "unit", "C"));
                weather.setDescription(listItem.optJSONArray("weather").getJSONObject(0).getString("description"));
                JSONObject windObj = listItem.optJSONObject("wind");
                if (windObj != null) {
                    weather.setWind(windObj.getString("speed"));
                    weather.setWindDirectionDegree(windObj.getDouble("deg"));
                }
                weather.setPressure(main.getString("pressure"));
                weather.setHumidity(main.getString("humidity"));

                JSONObject rainObj = listItem.optJSONObject("rain");
                String rain = "";
                if (rainObj != null) {
                    rain = getRainString(rainObj);
                } else {
                    JSONObject snowObj = listItem.optJSONObject("snow");
                    if (snowObj != null) {
                        rain = getRainString(snowObj);
                    } else {
                        rain = "0";
                    }
                }
                weather.setRain(rain);

                final String idString = listItem.optJSONArray("weather").getJSONObject(0).getString("id");
                weather.setId(idString);

                final String dateMsString = listItem.getString("dt") + "000";
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(Long.parseLong(dateMsString));
                weather.setIcon(setWeatherIcon(Integer.parseInt(idString), cal.get(Calendar.HOUR_OF_DAY),context));

                forecast.add(weather);


            }


            return forecast;
        } catch (JSONException e) {
            //Log.e("JSONException Data long", result);
            e.printStackTrace();
            return forecast;
        }
    }

    protected static String localize(SharedPreferences sp, Context context, String preferenceKey,
                                     String defaultValueKey) {
        return WeatherMonitorService.localize(sp, context, preferenceKey, defaultValueKey);
    }



    public static Weather getWeather(JSONObject jObj) throws JSONException {
        Weather weather = new Weather();


        // We start extracting the info
        MyLocation loc = new MyLocation();


        JSONArray jArr = jObj.getJSONArray("weather");

        JSONObject JSONWeather = jArr.getJSONObject(0);
        weather.setId(getString("id", JSONWeather));
        weather.setDescription(getString("description", JSONWeather));
       // weather.set(getString("main", JSONWeather));
        weather.setIcon(getString("icon", JSONWeather));

      // JSONObject mainObj = getObject("main", jObj);
        weather.setHumidity(getString("humidity", jObj));
        weather.setPressure(getString("pressure", jObj));
        weather.setTemperature(getString("temp", jObj));

        JSONObject wObj = getObject("wind", jObj);
        weather.setWind(getString("speed", wObj));
        weather.setWindDirectionDegree(Double.valueOf(getFloat("deg", wObj)));

        JSONObject cObj = getObject("clouds", jObj);


        return weather;
    }



    public static SecWeather getSecWeather(JSONObject jObj) throws JSONException {
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
                    forecasts.add(getSecWeather(jsonForecast));
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