package com.mck.localweathernow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;

/**
 * A fragment representing a list of hourly forecasts.
 */
public class HourlyViewFragment extends Fragment {
    private static final String TAG = "HourlyViewFragment";
    private static final String KEY_LAST_CURR_UPDATE_TIME =
            TAG + ".KEY_LAST_CURR_UPDATE_TIME";
    private static final String KEY_LAST_FORE_UPDATE_TIME =
            TAG + ".KEY_LAST_FORE_UPDATE_TIME";

    private Long lastCurrentWeatherUpdateTime;
    private Long lastForecastWeatherUpdateTime;
    public HourlyViewFragment() {    }

    public static HourlyViewFragment newInstance() {
        return new HourlyViewFragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_hourly, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new HourlyViewRecyclerViewAdapter());
        }

        if (savedInstanceState != null){
            lastCurrentWeatherUpdateTime = savedInstanceState.getLong(KEY_LAST_CURR_UPDATE_TIME);
            lastForecastWeatherUpdateTime = savedInstanceState.getLong(KEY_LAST_FORE_UPDATE_TIME);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (lastCurrentWeatherUpdateTime != null) {
            outState.putLong(KEY_LAST_CURR_UPDATE_TIME, lastCurrentWeatherUpdateTime);
        }
        if ( lastForecastWeatherUpdateTime != null){
            outState.putLong(KEY_LAST_FORE_UPDATE_TIME, lastForecastWeatherUpdateTime);
        }
    }

    void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        if (lastCurrentWeatherUpdateTime != null &&
                lastCurrentWeatherUpdateTime.equals(currentWeatherData.locDataTime)){
            return;
        }
        lastCurrentWeatherUpdateTime = currentWeatherData.locDataTime;
        RecyclerView recyclerView = ((RecyclerView) getView());
        if (recyclerView != null) {
            ((HourlyViewRecyclerViewAdapter) recyclerView.getAdapter())
                    .onCurrentWeatherDataUpdate(currentWeatherData);
        }
    }

    void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        if (lastForecastWeatherUpdateTime != null &&
                lastForecastWeatherUpdateTime.equals(forecastWeatherData.locDataTime)){
            return;
        }
        lastForecastWeatherUpdateTime = forecastWeatherData.locDataTime;
        RecyclerView recyclerView = ((RecyclerView) getView());
        if (recyclerView != null) {
            ((HourlyViewRecyclerViewAdapter) recyclerView.getAdapter())
                    .onForecastWeatherDataUpdate(forecastWeatherData);
        }
    }

    private boolean isNotUpdate(CurrentWeatherData currentWeatherData) {
        return lastCurrentWeatherUpdateTime != null && lastCurrentWeatherUpdateTime.equals(currentWeatherData.dt);
    }
}
