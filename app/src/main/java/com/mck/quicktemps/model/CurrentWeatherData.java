package com.mck.quicktemps.model;

/**
 * Main container for current weather data.
 * Created by Michael on 5/14/2016.
 */
public class CurrentWeatherData {


    public Coord coord;
    public Weather[] weather;
    public Main main;
    public Wind wind;
    public Clouds clouds;
    public Rain rain;
    public Snow snow;
    public Long dt; // date in unix
    public Sys sys;
    public Integer id; // City id
    public String name; // City name
    public Integer cod; // should be
    public String message; // error message
    public Long locDataTime;

    public CurrentWeatherData(){}



}
