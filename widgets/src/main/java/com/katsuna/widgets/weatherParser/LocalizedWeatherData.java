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

/**
 * Created by nikos on 12-Sep-16.
 */
public class LocalizedWeatherData extends WeatherData {
    private static final String JSON_URL      = "url";
    private static final String JSON_COORD    = "coord";
    private static final String JSON_DISTANCE = "distance";

    public static class GeoCoord {
        private static final String JSON_LAT = "lat";
        private static final String JSON_LON = "lon";

        private float latitude;
        private float longitude;

        GeoCoord (JSONObject json) {
            this.latitude = (float) json.optDouble (GeoCoord.JSON_LAT);
            this.longitude = (float) json.optDouble (GeoCoord.JSON_LON);
        }

        public boolean hasLatitude () {
            return this.latitude != Float.NaN;
        }
        public float getLatitude () {
            return latitude;
        }

        public boolean hasLongitude () {
            return this.longitude != Float.NaN;
        }
        public float getLongitude () {
            return longitude;
        }
    }

    private final String url;
    private final GeoCoord coord ;
    private final float distance;

    public LocalizedWeatherData (JSONObject json) {
        super(json);

        this.url = json.optString (LocalizedWeatherData.JSON_URL);
        this.distance = (float) json.optDouble (LocalizedWeatherData.JSON_DISTANCE, Double.NaN);

        JSONObject jsonCoord = json.optJSONObject (LocalizedWeatherData.JSON_COORD);
        this.coord = jsonCoord != null ? new GeoCoord (jsonCoord) : null;
    }

    public boolean hasUrl () {
        return this.url != null && this.url.length () > 0;
    }
    public String getUrl () {
        return this.url;
    }

    public boolean hasCoord () {
        return this.coord != null;
    }
    public GeoCoord getCoord () {
        return this.coord;
    }

    public boolean hasDistance () {
        return !Float.isNaN (this.distance);
    }
    public float getDistance () {
        return this.distance;
    }
}