package weatherParser;

import org.json.JSONObject;


public class ForecastWeatherData  extends LocalizedWeatherData {
    static private final String DATETIME_KEY_NAME = "dt";

    private long calcDateTime = Long.MIN_VALUE;

    /**
     * @param json json container with the forecast data */
    public ForecastWeatherData (JSONObject json) {
        super (json);
        this.calcDateTime = json.optLong (ForecastWeatherData.DATETIME_KEY_NAME, Long.MIN_VALUE);
    }

    public long getCalcDateTime () {
        return this.calcDateTime;
    }
}
