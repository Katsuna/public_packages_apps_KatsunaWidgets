package weatherParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import weatherDb.Weather;

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


    public static Weather getWeather(JSONObject jObj) throws JSONException {
        Weather weather = new Weather();


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
        weather.location = loc;

        JSONArray jArr = jObj.getJSONArray("weather");

        JSONObject JSONWeather = jArr.getJSONObject(0);
        weather.currentCondition.setWeatherId(getInt("id", JSONWeather));
        weather.currentCondition.setDescr(getString("description", JSONWeather));
        weather.currentCondition.setCondition(getString("main", JSONWeather));
        weather.currentCondition.setIcon(getString("icon", JSONWeather));

        JSONObject mainObj = getObject("main", jObj);
        weather.currentCondition.setHumidity(getInt("humidity", mainObj));
        weather.currentCondition.setPressure(getInt("pressure", mainObj));
        weather.temperature.setMaxTemp(getFloat("temp_max", mainObj));
        weather.temperature.setMinTemp(getFloat("temp_min", mainObj));
        weather.temperature.setTemp(getFloat("temp", mainObj));

        JSONObject wObj = getObject("wind", jObj);
        weather.wind.setSpeed(getFloat("speed", wObj));
        weather.wind.setDeg(getFloat("deg", wObj));

        JSONObject cObj = getObject("clouds", jObj);
        weather.clouds.setPerc(getInt("all", cObj));


        return weather;
    }


    public static List<Weather> getForecastWeather(JSONObject jObj) {


        List<Weather> forecasts;

        //   this.sys = jsonSys != null ? new Sys (jsonSys) : null;

        JSONArray jsonForecasts = jObj.optJSONArray("list");
        if (jsonForecasts != null) {
            forecasts = new ArrayList<Weather>(jsonForecasts.length());
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