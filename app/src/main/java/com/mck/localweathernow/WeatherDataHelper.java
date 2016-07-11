package com.mck.localweathernow;

import java.text.DecimalFormat;
import java.util.Calendar;

/**
 * Useful methods for working with weather data
 * Created by Michael on 7/5/2016.
 */

public class WeatherDataHelper {


    public static String formatTime(Long time){
        StringBuilder resultTime = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( time * 1000 );
        int hour = calendar.get(Calendar.HOUR);
        int min = calendar.get(Calendar.MINUTE);
        hour = (hour == 0)? 12: hour;
        String minute;
        if (min == 0) {
            minute = "00";
        } else if (min < 10){
            minute = "0" + min;
        } else {
            minute = String.valueOf(min);
        }
        resultTime.append(hour).append(":").append(minute);
        return resultTime.toString();
    }


    public static String formatTimeAmPm(Long time){
        StringBuilder resultTime = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( time * 1000 );
        int hour = calendar.get(Calendar.HOUR);
        hour = (hour == 0)? 12: hour;
        resultTime.append(hour);
        int min = calendar.get(Calendar.MINUTE);
        String minute;
        if (min == 0) {
            minute = "";
        } else if (min < 10){
            minute = ":0" + min;
        } else {
            minute = ":" + min;
        }
        resultTime.append(minute);
        int amPM = calendar.get(Calendar.AM_PM);
        if (amPM == Calendar.AM) {
            resultTime.append(" AM");
        } else {
            resultTime.append(" PM");
        }
        return resultTime.toString();
    }

    public static String formatDate(Long time) {
        StringBuilder result = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( time * 1000 );
        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        result.append(" ");
        result.append(month);
        result.append("/");
        result.append(dayOfMonth);
        return result.toString();
    }

    public static String formatDirection(Double deg) {
        if (deg > 348.75 || deg < 11.24) {
            return "N";
        } else if (deg < 33.75){
            return "NNE";
        }else if (deg < 65.25){
            return "NE";
        }else if (deg < 33.75){
            return "NNE";
        }else if (deg < 78.75){
            return "ENE";
        }else if (deg < 101.25){
            return "E";
        }else if (deg < 123.75){
            return "ESE";
        }else if (deg < 146.25){
            return "SE";
        }else if (deg < 168.75){
            return "SSE";
        }else if (deg < 191.25){
            return "S";
        }else if (deg < 213.75){
            return "SSW";
        }else if (deg < 236.25){
            return "SW";
        }else if (deg < 258.75){
            return "WSW";
        }else if (deg < 281.25){
            return "W";
        }else if (deg < 303.75){
            return "WNW";
        }else if (deg < 326.25){
            return "NW";
        }else{
            return "NNW";
        }
    }

    public static String formatSpeed(Double speed) {
        if (Constants.isMetric)
            return  (new DecimalFormat("####.#")).format(speed) +  " m/s";
        else
            return (new DecimalFormat("####.#")).format(speed*2.2369) + " M/h";
    }

    public static String formatVolume(Double threeHour) {
        if (Constants.isMetric)
            return threeHour + " mm";
        else
            return (new DecimalFormat("####.###")).format(threeHour*0.0393701) + " in";
    }

    public static String formatTemperature(Double temp) {
        if (Constants.isMetric)
            return String.valueOf(Double.valueOf(temp - 273.15).intValue()) + "°";
        else {
            return String.valueOf(Double.valueOf(temp * 9/5 - 459.67).intValue()) + "°";
        }
    }

    public static String formatPercent(Double all) {
        return all + "%";
    }
}
