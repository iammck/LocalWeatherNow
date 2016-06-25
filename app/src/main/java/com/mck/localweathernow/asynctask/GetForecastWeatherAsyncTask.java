package com.mck.localweathernow.asynctask;

import android.os.AsyncTask;

import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.service.OpenWeatherMapService;


/**
 * Gets the forecast for the lat and lon over a period and returns the result to a Callback.
 *
 * Created by Michael on 5/17/2016.
 */
public class GetForecastWeatherAsyncTask extends AsyncTask<Object,Integer,ForecastWeatherData> {
    Callback callback;
    double lat;
    double lon;
    int periods;

    public interface Callback {
        void onForecastWeatherResult(ForecastWeatherData data);
    }

    public GetForecastWeatherAsyncTask(Callback callback, double lat, double lon, int periods){
        this.callback = callback;
        this.lat = lat;
        this.lon = lon;
        this.periods = periods;
    }

    @Override
    protected ForecastWeatherData doInBackground(Object... params) {
        if (isCancelled()) return null;
        return OpenWeatherMapService.instance().requestForecastWeather(lat, lon, periods);
    }

    @Override
    protected void onPostExecute(ForecastWeatherData data) {
        super.onPostExecute(data);
        if (!isCancelled()){
            callback.onForecastWeatherResult(data);
        }
    }
}
