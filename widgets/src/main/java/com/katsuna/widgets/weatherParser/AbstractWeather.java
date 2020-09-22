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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractWeather {
    static private final String JSON_COD = "cod";
    static private final String JSON_MESSAGE = "message";
    static protected final String JSON_CALCTIME = "calctime";
    static private final String JSON_CALCTIME_TOTAL = "total";
    static protected final String JSON_LIST = "list";


    private final int code;
    private final String message;
    private final float calctime;

    public AbstractWeather (JSONObject json) {
        this.code = json.optInt (AbstractWeather.JSON_COD, Integer.MIN_VALUE);
        this.message = json.optString (AbstractWeather.JSON_MESSAGE);
        String calcTimeStr = json.optString (AbstractWeather.JSON_CALCTIME);
        float calcTimeTotal = Float.NaN;
        if (calcTimeStr.length () > 0) {
            try {
                calcTimeTotal = Float.valueOf (calcTimeStr);
            } catch (NumberFormatException nfe) { // So.. it's not a number.. let's see if we can still find it's value.
                String totalCalcTimeStr = AbstractWeather.getValueStrFromCalcTimePart (calcTimeStr, AbstractWeather.JSON_CALCTIME_TOTAL);
                if (totalCalcTimeStr != null) {
                    try {
                        calcTimeTotal = Float.valueOf (totalCalcTimeStr);
                    } catch (NumberFormatException nfe2) {
                        calcTimeTotal = Float.NaN;
                    }
                }
            }
        }
        this.calctime = calcTimeTotal;
    }

    public boolean hasCode () {
        return this.code != Integer.MIN_VALUE;
    }
    public int getCode () {
        return this.code;
    }

    public boolean hasMessage () {
        return this.message != null && this.message.length () > 0;
    }
    public String getMessage () {
        return this.message;
    }

    public boolean hasCalcTime () {
        return !Double.isNaN (this.calctime);
    }
    public double getCalcTime () {
        return this.calctime;
    }

    static String getValueStrFromCalcTimePart (final String calcTimeStr, final String part) {
        Pattern keyValuePattern = Pattern.compile (part + "\\s*=\\s*([\\d\\.]*)");
        Matcher matcher = keyValuePattern.matcher (calcTimeStr);
        if (matcher.find () && matcher.groupCount () == 1) {
            return matcher.group (1);
        }
        return null;
    }

    static float getValueFromCalcTimeStr (final String calcTimeStr, final String part) {
        if (calcTimeStr == null || calcTimeStr.length () == 0)
            return Float.NaN;
        float value = Float.NaN;
        String valueStr = AbstractWeather.getValueStrFromCalcTimePart (calcTimeStr, part);
        if (valueStr != null) {
            try {
                value = Float.valueOf (valueStr);
            } catch (NumberFormatException nfe) {
                value = Float.NaN;
            }
        }
        return value;
    }


}
