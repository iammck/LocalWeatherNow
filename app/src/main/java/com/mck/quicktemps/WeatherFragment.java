package com.mck.quicktemps;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.mck.quicktemps.asynctask.GetCurrentWeatherAsyncTask;
import com.mck.quicktemps.asynctask.GetCurrentWeatherDataAsyncTask;
import com.mck.quicktemps.asynctask.GetForecastWeatherAsyncTask;
import com.mck.quicktemps.asynctask.GetForecastWeatherDataAsyncTask;
import com.mck.quicktemps.model.CurrentWeatherData;
import com.mck.quicktemps.model.ForecastWeatherData;
import com.mck.quicktemps.model.LocationData;

@SuppressWarnings("WeakerAccess")
public class WeatherFragment extends Fragment implements
        GetCurrentWeatherAsyncTask.Callback,
        GetCurrentWeatherDataAsyncTask.Callback,
        GetForecastWeatherAsyncTask.Callback,
        GetForecastWeatherDataAsyncTask.Callback {

    public static final String TAG = "WeatherFragment";

    private static final String KEY_LOCATION_DATA = TAG + ".key_current_location";
    private static final String KEY_CURR_WEATHER = TAG + ".KEY_CURR_WEATHER" ;
    private static final String KEY_FORE_WEATHER = TAG + ".KEY_FORE_WEATHER" ;

    private WeatherFragmentListener mWeatherFragmentListener;

    private LocationData mLocationData;

    private String mCurrentWeather;
    private CurrentWeatherData mCurrentWeatherData;
    private GetCurrentWeatherAsyncTask mGetCurrentWeatherAsyncTask;
    private GetCurrentWeatherDataAsyncTask mGetCurrentWeatherDataAsyncTask;

    private String mForecastWeather;
    private ForecastWeatherData mForecastWeatherData;
    private GetForecastWeatherAsyncTask mGetForecastWeatherAsyncTask;
    private GetForecastWeatherDataAsyncTask mGetForecastWeatherDataAsyncTask;

    public WeatherFragment() {     }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate");
        if (savedInstanceState != null){
            mLocationData = savedInstanceState.getParcelable(KEY_LOCATION_DATA);
            mCurrentWeather = savedInstanceState.getString(KEY_CURR_WEATHER);
            mForecastWeather = savedInstanceState.getString(KEY_FORE_WEATHER);
        }
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
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
        // if null or stale data, can just return.
        if (mLocationData == null ||
                (System.currentTimeMillis() - mLocationData.time > Constants.MAX_LOC_TIME_DELTA)){
            Log.v(TAG, "onResume() mLocationData is null or has stale time.");
            return; // just return.
        }

        if (mCurrentWeatherData != null) {
            Log.v(TAG, "onResume() mCurrentWeatherData is not null.");
            onCurrentWeatherDataResult(mCurrentWeatherData);
        } else if (mCurrentWeather != null) {
            Log.v(TAG, "onResume() mCurrentWeather is not null");
            onCurrentWeatherResult(mCurrentWeather);
        } else {
            Log.v(TAG, "onResume() with no mCurrentWeather");
            updateCurrentWeather(mLocationData);
        }

        if (mForecastWeatherData != null) {
            Log.v(TAG, "onResume() mForecastWeatherData is not null.");
            onForecastWeatherDataResult(mForecastWeatherData);
        } else if (mForecastWeather != null) {
            Log.v(TAG, "onResume() mForecastWeather is not null");
            onCurrentWeatherResult(mForecastWeather);
        } else {
            Log.v(TAG, "onResume() with no mForecastWeather");
            updateForecastWeather(mLocationData);
        }
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
        if (mCurrentWeather != null){
            Log.v(TAG, "onSaveInstanceState() putting mCurrentWeather in outState. ");
            outState.putString(KEY_CURR_WEATHER, mCurrentWeather);
        }
        if (mLocationData != null){
            Log.v(TAG, "onSaveInstanceState() putting mLocationData in outState. ");
            outState.putParcelable(KEY_LOCATION_DATA, mLocationData);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mWeatherFragmentListener = null;
    }

    public void onLocationDataUpdate(LocationData locationData){
        Log.v(TAG, "onLocationDataUpdate()");
        if(mLocationData != null && mLocationData.time == locationData.time){
            return;
        }
        updateCurrentWeather(locationData);
        updateForecastWeather(locationData);
    }

    private void updateForecastWeather(LocationData locationData) {
        Log.v(TAG, "updateForecastWeather()");
        mLocationData = locationData;
        if (mGetForecastWeatherAsyncTask != null){
            mGetForecastWeatherAsyncTask.cancel(true);
            mGetForecastWeatherAsyncTask = null;
        }
        mGetForecastWeatherAsyncTask = new GetForecastWeatherAsyncTask(this, locationData);
        mGetForecastWeatherAsyncTask.execute();
    }

    private void updateCurrentWeather(LocationData locationData){
        Log.v(TAG, "updateCurrentWeather()");
        mLocationData = locationData;
        if (mGetCurrentWeatherAsyncTask != null){
            mGetCurrentWeatherAsyncTask.cancel(true);
            mGetCurrentWeatherAsyncTask = null;
        }
        mGetCurrentWeatherAsyncTask = new GetCurrentWeatherAsyncTask(this, locationData);
        mGetCurrentWeatherAsyncTask.execute();
    }

    @Override
    public void onCurrentWeatherResult(String currentWeather) {
        Log.v(TAG, "onCurrentWeatherResult() instantiating GetCurrentWeatherDataAsyncTask before executing.");
        mCurrentWeather = currentWeather;
        mGetCurrentWeatherAsyncTask = null;
        if (mGetCurrentWeatherDataAsyncTask != null){
            mGetCurrentWeatherDataAsyncTask.cancel(true);
            mGetCurrentWeatherDataAsyncTask = null;
        }
        mGetCurrentWeatherDataAsyncTask =
                new GetCurrentWeatherDataAsyncTask(this, currentWeather);
        mGetCurrentWeatherDataAsyncTask.execute();
    }

    @Override
    public void onForecastWeatherResult(String forecastWeather) {
        Log.v(TAG, "onForecastWeatherResult() instantiating GetForecastWeatherDataAsyncTask before executing.");
        mForecastWeather = forecastWeather;
        mGetForecastWeatherAsyncTask = null;
        if (mGetForecastWeatherDataAsyncTask != null){
            mGetForecastWeatherDataAsyncTask.cancel(true);
            mGetForecastWeatherDataAsyncTask = null;
        }
        mGetForecastWeatherDataAsyncTask =
                new GetForecastWeatherDataAsyncTask(this, forecastWeather);
        mGetForecastWeatherDataAsyncTask.execute();
    }


    @Override
    public void onCurrentWeatherDataResult(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataResult()");
        currentWeatherData.locDataTime = mLocationData.time;
        mCurrentWeatherData = currentWeatherData;
        mGetCurrentWeatherDataAsyncTask = null;
        if (mWeatherFragmentListener != null){
            Log.v(TAG, "onCurrentWeatherDataResult() updating listener.");
            mWeatherFragmentListener.onCurrentWeatherDataUpdate(currentWeatherData);
        }
    }

    @Override
    public void onForecastWeatherDataResult(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataResult()");
        forecastWeatherData.locDataTime = mLocationData.time;
        mForecastWeatherData = forecastWeatherData;
        mGetForecastWeatherAsyncTask = null;
        if (mWeatherFragmentListener != null){
            Log.v(TAG, "onForecastWeatherDataResult() updating listener.");
            mWeatherFragmentListener.onForecastWeatherDataUpdate(forecastWeatherData);
        }
    }

    public boolean hasOldData(){
        return (mLocationData == null ||
                (System.currentTimeMillis() - mLocationData.time > Constants.MAX_LOC_TIME_DELTA));
    }

    public void refresh() {
        if (mCurrentWeatherData != null) {
            Log.v(TAG, "refresh() mCurrentWeatherData is not null.");
            onCurrentWeatherDataResult(mCurrentWeatherData);
        } else if (mCurrentWeather != null) {
            Log.v(TAG, "refresh() mCurrentWeather is not null");
            onCurrentWeatherResult(mCurrentWeather);
        } else {
            Log.v(TAG, "refresh() with no mCurrentWeather");
            updateCurrentWeather(mLocationData);
        }

        if (mForecastWeatherData != null) {
            Log.v(TAG, "refresh() mForecastWeatherData is not null.");
            onForecastWeatherDataResult(mForecastWeatherData);
        } else if (mForecastWeather != null) {
            Log.v(TAG, "refresh() mForecastWeather is not null");
            onCurrentWeatherResult(mForecastWeather);
        } else {
            Log.v(TAG, "refresh() with no mForecastWeather");
            updateForecastWeather(mLocationData);
        }
    }

    interface WeatherFragmentListener {
        void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData);
        void onForecastWeatherDataUpdate(ForecastWeatherData mForecastWeatherData);
    }
}