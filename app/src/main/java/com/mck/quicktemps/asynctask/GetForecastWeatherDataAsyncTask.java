package com.mck.quicktemps.asynctask;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mck.quicktemps.model.ForecastWeatherData;
import com.mck.quicktemps.model.Rain;
import com.mck.quicktemps.model.Snow;

/**
 * gets forecast weather data from a forecast string.
 * Created by Michael on 7/3/2016.
 */

public class GetForecastWeatherDataAsyncTask extends AsyncTask<Object, Integer, ForecastWeatherData> {
    private final String forecastWeather;
    private final Callback callback;

    public interface Callback {
        void onForecastWeatherDataResult(ForecastWeatherData forecastWeatherData);
    }

    public GetForecastWeatherDataAsyncTask(Callback callback, String forecastWeather) {
        this.callback = callback;
        this.forecastWeather = forecastWeather;
    }

    @Override
    protected ForecastWeatherData doInBackground(Object... objects) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Rain.class, new Rain.RainDeserializer())
                .registerTypeAdapter(
                        Snow.class, new Snow.SnowDeserializer())
                .create();
        return isCancelled()? null :(gson.fromJson(forecastWeather, ForecastWeatherData.class));
    }

    @Override
    protected void onPostExecute(ForecastWeatherData forecastWeatherData) {
        super.onPostExecute(forecastWeatherData);
        if (!isCancelled() && forecastWeatherData != null) callback.onForecastWeatherDataResult(forecastWeatherData);

    }
}
