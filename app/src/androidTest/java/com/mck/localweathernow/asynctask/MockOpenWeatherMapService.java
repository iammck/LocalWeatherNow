package com.mck.localweathernow.asynctask;

import android.content.Context;
import android.graphics.Bitmap;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.service.OpenWeatherMapService;

/**
 * Mock OpenWeatherMapService class for running tests.
 * Created by Michael on 5/16/2016.
 */
public class MockOpenWeatherMapService extends OpenWeatherMapService {
    private int sleepTime;
    private CurrentWeatherData currentResult;
    private ForecastWeatherData forecastResult;
    private Bitmap iconResult;

    public static void setup(MockOpenWeatherMapService service) {
        instance = service;
    }

    public static void tearDown(){
        instance = null;

    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    public void setCurrentResult(CurrentWeatherData data) {
        currentResult = data;
    }
    public void setForecastResult(ForecastWeatherData data) {
        forecastResult = data;
    }
    public void setIconResult(Bitmap icon) {
        iconResult = icon;
    }

    @Override
    public CurrentWeatherData requestCurrentWeather(Double lat, Double lon) {
        sleep();
        return currentResult;
    }

    private void sleep() {
        if (this.sleepTime > 0){
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ForecastWeatherData requestForecastWeather(Double lat, Double lon, Integer periods) {
        sleep();
        return forecastResult;
    }

    @Override
    public Bitmap requestWeatherIcon(Context context, String iconId) {
        return iconResult;
    }

}