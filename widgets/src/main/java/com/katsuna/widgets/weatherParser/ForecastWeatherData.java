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
package com.katsuna.widgets.weatherParser;

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
