package com.mck.localweathernow.asynctask;

import android.os.AsyncTask;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.service.OpenWeatherMapService;

/**
 * AsyncTask to get the current weather. requires the call, lat and long
 * to start.
 * Created by Michael on 5/16/2016.
 */
public class GetCurrentWeatherAsyncTask extends AsyncTask<Object,Integer,CurrentWeatherData> {
    Callback callback;
    private double lat;
    private double lon;

    public interface Callback {
        void onCurrentWeatherResult(CurrentWeatherData data);
    }

    public GetCurrentWeatherAsyncTask(Callback callback, double lat, double lon){
        this.callback = callback;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    protected CurrentWeatherData doInBackground(Object... params) {
        if (isCancelled()) return null;
        return OpenWeatherMapService.instance().requestCurrentWeather(lat, lon);
    }

    @Override
    protected void onPostExecute(CurrentWeatherData data) {
        super.onPostExecute(data);
        if (!isCancelled()){
            callback.onCurrentWeatherResult(data);
        }
    }
}
