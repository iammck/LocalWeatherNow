package com.mck.localweathernow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mck.localweathernow.dialog.AboutDialogFragment;
import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.model.LocationData;
import com.mck.localweathernow.ui.HourlyViewFragment;
import com.mck.localweathernow.ui.MainViewFragment;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
                new AboutDialogFragment().show(getSupportFragmentManager(), "information");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    public boolean onRefresh() {
        WeatherFragment weatherFragment = (WeatherFragment)
                getSupportFragmentManager().findFragmentByTag(WeatherFragment.TAG);
        LocationFragment locationFragment = (LocationFragment)
                getSupportFragmentManager().findFragmentByTag(LocationFragment.TAG);
        // if the weather data does not have old data and locationFragment has ideal data
        if(!weatherFragment.hasOldData() && locationFragment.hasIdealLocationData()){
            return false;
        }
        getSupportFragmentManager().beginTransaction()
            .remove(weatherFragment)
            .remove(locationFragment)
            .add(LocationFragment.newInstance(), LocationFragment.TAG)
            .add(WeatherFragment.newInstance(), WeatherFragment.TAG)
            .commit();
        return true;
    }
}
