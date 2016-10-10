package weatherParser;

import java.util.ArrayList;

import weatherDb.Weather;


public class Forecast {
    private ArrayList<Weather> weekWeather;

    public ArrayList<Weather> getWeekWeather() {
        return weekWeather;
    }

    public void setWeekWeather(ArrayList<Weather> weekWeather) {
        this.weekWeather = weekWeather;
    }
}
