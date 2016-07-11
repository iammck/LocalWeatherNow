package com.mck.quicktemps.asynctask;

import android.os.AsyncTask;

import com.mck.quicktemps.model.LocationData;
import com.mck.quicktemps.service.OpenWeatherMapService;

/**
 * gets forecast weather from OpenWeatherMapService
 * Created by Michael on 7/3/2016.
 */

public class GetForecastWeatherAsyncTask extends AsyncTask<Object,Integer,String> {
    private final LocationData locationData;
    private final Callback callback;

    public interface Callback {
        void onForecastWeatherResult(String forecastWeather);
    }

    public GetForecastWeatherAsyncTask(Callback callback, LocationData locationData){
        this.callback = callback;
        this.locationData = locationData;
    }

    @Override
    protected String doInBackground(Object... objects) {
        return isCancelled()? null: OpenWeatherMapService.requestForecast(locationData);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!isCancelled()) callback.onForecastWeatherResult(s);
    }
}
