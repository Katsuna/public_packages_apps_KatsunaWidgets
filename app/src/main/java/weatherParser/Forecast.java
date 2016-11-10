package weatherParser;

import java.util.ArrayList;

import weatherDb.SecWeather;


public class Forecast {
    private ArrayList<SecWeather> weekWeather;

    public ArrayList<SecWeather> getWeekWeather() {
        return weekWeather;
    }

    public void setWeekWeather(ArrayList<SecWeather> weekWeather) {
        this.weekWeather = weekWeather;
    }
}
