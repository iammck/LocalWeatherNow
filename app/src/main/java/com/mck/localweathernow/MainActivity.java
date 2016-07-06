package com.mck.localweathernow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.model.LocationData;

public class MainActivity extends AppCompatActivity implements
        LocationFragment.LocationFragmentListener,
        WeatherFragment.WeatherFragmentListener,
        HourlyViewFragment.HourlyViewFragmentListener{

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mainFragmentContainer, MainViewFragment.newInstance())
                    .add(LocationFragment.newInstance(), LocationFragment.TAG)
                    .add(WeatherFragment.newInstance(), WeatherFragment.TAG)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocationFragment fragment = (LocationFragment)
                getSupportFragmentManager().findFragmentByTag(LocationFragment.TAG);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocationFragment fragment = (LocationFragment)
                getSupportFragmentManager().findFragmentByTag(LocationFragment.TAG);
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLocationDataUpdate(LocationData locationData) {
        Log.v(TAG, "onLocationDataUpdate()");
        WeatherFragment fragment = (WeatherFragment)
                getSupportFragmentManager().findFragmentByTag(WeatherFragment.TAG);
        fragment.onLocationDataUpdate(locationData);
    }

    @Override
    public void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        MainViewFragment fragment = (MainViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (fragment != null){
            fragment.onCurrentWeatherDataUpdate(currentWeatherData);
        } else {
            Log.v(TAG, "onCurrentWeatherDataUpdate(), but MainViewFragment can not be found by id.");
        }
    }

    @Override
    public void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        MainViewFragment fragment = (MainViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (fragment != null){
            fragment.onForecastWeatherDataUpdate(forecastWeatherData);
        } else {
            Log.v(TAG, "onForecastWeatherDataUpdate(), but MainViewFragment can not be found by id.");
        }
    }

    @Override
    public void onRefreshHourlyViewFragment() {
        WeatherFragment weatherFragment = (WeatherFragment)
                getSupportFragmentManager().findFragmentByTag(WeatherFragment.TAG);
        LocationFragment locationFragment = (LocationFragment)
                getSupportFragmentManager().findFragmentByTag(LocationFragment.TAG);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (weatherFragment != null){
            transaction.remove(weatherFragment);
        }
        if (locationFragment != null){
            transaction.remove(locationFragment);
        }
        transaction
            .add(LocationFragment.newInstance(), LocationFragment.TAG)
            .add(WeatherFragment.newInstance(), WeatherFragment.TAG)
            .commit();
    }
}
