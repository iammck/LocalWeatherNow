package com.mck.localweathernow;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.adapter.WeatherViewItemAnimator;
import com.mck.localweathernow.adapter.WeatherViewRecyclerViewAdapter;


public class WeatherViewFragment extends Fragment {
    public static final String TAG = "WeatherViewFragment";
    private static final String KEY_LAST_CURR_UPDATE_TIME =
            TAG + ".KEY_LAST_CURR_UPDATE_TIME";
    private static final String KEY_LAST_FORE_UPDATE_TIME =
            TAG + ".KEY_LAST_FORE_UPDATE_TIME";

    private Long lastCurrentWeatherUpdateTime;
    private Long lastForecastWeatherUpdateTime;
    private MainViewFragmentListener mMainViewFragmentListener;

    public WeatherViewFragment() {    }

    public static WeatherViewFragment newInstance() {
        return new WeatherViewFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainViewFragmentListener) {
            mMainViewFragmentListener = (MainViewFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WeatherFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");
        View result = inflater.inflate(R.layout.fragment_weather, container, false);

        if (result instanceof SwipeRefreshLayout){
            final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) result;
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
                @Override
                public void onRefresh() {
                    onRefreshData();
                }
            });
            View view = result.findViewById(R.id.recyclerView);
            // Set the adapter
            if (view instanceof RecyclerView) {
                Context context = view.getContext();
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new WeatherViewRecyclerViewAdapter());
                recyclerView.setItemAnimator(new WeatherViewItemAnimator());
            }

        }

        if (savedInstanceState != null){
            lastCurrentWeatherUpdateTime = savedInstanceState.getLong(KEY_LAST_CURR_UPDATE_TIME);
            lastForecastWeatherUpdateTime = savedInstanceState.getLong(KEY_LAST_FORE_UPDATE_TIME);
        }
        return result;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mMainViewFragmentListener = null;
    }

    public void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        lastCurrentWeatherUpdateTime = currentWeatherData.locDataTime;
        View view = getView();
        if (view instanceof SwipeRefreshLayout){
            ((SwipeRefreshLayout) view).setRefreshing(false);
            RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.recyclerView));
            if (recyclerView != null) {
                ((WeatherViewRecyclerViewAdapter) recyclerView.getAdapter())
                        .onCurrentWeatherDataUpdate(currentWeatherData);
            }
        }

    }

    public void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        lastForecastWeatherUpdateTime = forecastWeatherData.locDataTime;
        if (getView() != null) {
            RecyclerView recyclerView = ((RecyclerView) getView().findViewById(R.id.recyclerView));
            if (recyclerView != null) {
                ((WeatherViewRecyclerViewAdapter) recyclerView.getAdapter())
                        .onForecastWeatherDataUpdate(forecastWeatherData);
            }
        }
    }

    private void onRefreshData() {
        Log.v(TAG, "onRefresh(), refreshing data.");
        mMainViewFragmentListener.onRefresh();
    }

    public interface MainViewFragmentListener {
        void onRefresh();
    }
}
