package com.mck.localweathernow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.model.LocationData;

public class MainActivity extends AppCompatActivity
        implements LocationFragment.LocationFragmentListener,
        WeatherFragment.WeatherFragmentListener {

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
    public void onLocationUpdate(LocationData location) {
        Log.v(TAG, "onLocationUpdate()");
    }

    @Override
    public void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {

    }

    @Override
    public void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {

    }
}
