package com.mck.localweathernow;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mck.localweathernow.model.LocationData;

@SuppressWarnings("WeakerAccess")
public class WeatherFragment extends Fragment {
    public static final String TAG = "WeatherFragment";

    private WeatherFragmentListener mWeatherFragmentListener;

    public WeatherFragment() {     }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WeatherFragmentListener) {
            mWeatherFragmentListener = (WeatherFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement WeatherFragmentListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mWeatherFragmentListener = null;
    }

    public synchronized void updateLocation(LocationData locationData){
        /*if (mGetCurrentWeatherAsyncTask != null){
            mGetCurrentWeatherAsyncTask.cancel(true);
            mGetForecastWeatherAsyncTask = null;
        }*/
    }


    interface WeatherFragmentListener {
    }
}
