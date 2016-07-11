package com.mck.quicktemps.asynctask;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mck.quicktemps.model.CurrentWeatherData;
import com.mck.quicktemps.model.Rain;
import com.mck.quicktemps.model.Snow;

/**
 * gets current weather from OpenWeatherMapService
 * Created by Michael on 7/2/2016.
 */
public class GetCurrentWeatherDataAsyncTask extends AsyncTask<Object, Integer, CurrentWeatherData> {
    private final String currentWeather;
    private final Callback callback;

    public interface Callback {
        void onCurrentWeatherDataResult(CurrentWeatherData currentWeatherData);
    }

    public GetCurrentWeatherDataAsyncTask(Callback callback, String currentWeather) {
        this.callback = callback;
        this.currentWeather = currentWeather;
    }

    @Override
    protected CurrentWeatherData doInBackground(Object... objects) {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(
                        Rain.class, new Rain.RainDeserializer())
                .registerTypeAdapter(
                        Snow.class, new Snow.SnowDeserializer())
                .create();
        return isCancelled()? null :(gson.fromJson(currentWeather, CurrentWeatherData.class));
    }

    @Override
    protected void onPostExecute(CurrentWeatherData currentWeatherData) {
        super.onPostExecute(currentWeatherData);
        if (!isCancelled() && currentWeatherData != null) callback.onCurrentWeatherDataResult(currentWeatherData);

    }
}
