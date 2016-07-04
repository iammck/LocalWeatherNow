package com.mck.localweathernow.asynctask;

import android.os.AsyncTask;

import com.mck.localweathernow.model.LocationData;
import com.mck.localweathernow.service.OpenWeatherMapService;

/**
 * Created by Michael on 7/2/2016.
 */
public class GetCurrentWeatherAsyncTask extends AsyncTask<Object,Integer,String> {
    private final LocationData locationData;
    private final Callback callback;

    public interface Callback {
        void onCurrentWeatherResult(String currentWeather);
    }

    public GetCurrentWeatherAsyncTask(Callback callback, LocationData locationData){
        this.callback = callback;
        this.locationData = locationData;
    }

    @Override
    protected String doInBackground(Object... objects) {
        return isCancelled()? null: OpenWeatherMapService.requestCurrent(locationData);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (!isCancelled()) callback.onCurrentWeatherResult(s);
    }
}
