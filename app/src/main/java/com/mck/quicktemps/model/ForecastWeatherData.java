package com.mck.quicktemps.model;

/**
 * Main container for current weather data.
 * Created by Michael on 5/15/2016.
 */
public class ForecastWeatherData {
    public City city;
    public String cod;
    public String message;
    public Integer cnt;
    public Period[] list;
    public long locDataTime;

    public ForecastWeatherData(){

    }
}
