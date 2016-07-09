package com.mck.localweathernow.ui;

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

import com.mck.localweathernow.R;
import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.ui.adapter.HourlyViewItemAnimator;
import com.mck.localweathernow.ui.adapter.HourlyViewRecyclerViewAdapter;

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
    private HourlyViewFragmentListener mHourlyViewFragmentListener;

    public HourlyViewFragment() {    }

    public static HourlyViewFragment newInstance() {
        return new HourlyViewFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HourlyViewFragmentListener) {
            mHourlyViewFragmentListener = (HourlyViewFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WeatherFragmentListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView()");
        View result = inflater.inflate(R.layout.fragment_hourly, container, false);

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
                recyclerView.setAdapter(new HourlyViewRecyclerViewAdapter());
                recyclerView.setItemAnimator(new HourlyViewItemAnimator());
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
        mHourlyViewFragmentListener = null;
    }

    void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        lastCurrentWeatherUpdateTime = currentWeatherData.locDataTime;
        View view = getView();
        if (view instanceof SwipeRefreshLayout){
            ((SwipeRefreshLayout) view).setRefreshing(false);
            RecyclerView recyclerView = ((RecyclerView) view.findViewById(R.id.recyclerView));
            if (recyclerView != null) {
                ((HourlyViewRecyclerViewAdapter) recyclerView.getAdapter())
                        .onCurrentWeatherDataUpdate(currentWeatherData);
            }
        }

    }


    void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        lastForecastWeatherUpdateTime = forecastWeatherData.locDataTime;
        if (getView() != null) {
            RecyclerView recyclerView = ((RecyclerView) getView().findViewById(R.id.recyclerView));
            if (recyclerView != null) {
                ((HourlyViewRecyclerViewAdapter) recyclerView.getAdapter())
                        .onForecastWeatherDataUpdate(forecastWeatherData);
            }
        }
    }

    private void onRefreshData() {
        // if false, no need to refresh.
        if (!mHourlyViewFragmentListener.onRefresh()){
            Log.v(TAG, "onRefresh(), but data is still good.");
            View view = getView();
            if (view instanceof SwipeRefreshLayout){
                ((SwipeRefreshLayout) view).setRefreshing(false);
            }
        } else {
            Log.v(TAG, "onRefresh(), refreshing data.");
        }
    }

    public interface HourlyViewFragmentListener {
        boolean onRefresh();
    }
}
