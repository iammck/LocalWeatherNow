package com.mck.quicktemps;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.mck.quicktemps.dialog.AboutDialogFragment;
import com.mck.quicktemps.model.CurrentWeatherData;
import com.mck.quicktemps.model.ForecastWeatherData;
import com.mck.quicktemps.model.LocationData;

public class MainActivity extends AppCompatActivity implements
        LocationFragment.LocationFragmentListener,
        WeatherFragment.WeatherFragmentListener,
        WeatherViewFragment.MainViewFragmentListener{

    private static final String TAG = "MainActivity";
    private static final String PREFS = "SharedPrefs";
    private static final String KEY_IS_METRIC = "KeyIsMetric";
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        Constants.isMetric = prefs.getBoolean(KEY_IS_METRIC, false);

        if (savedInstanceState == null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.mainFragmentContainer, WeatherViewFragment.newInstance())
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
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_IS_METRIC, Constants.isMetric).apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        menu.findItem(R.id.action_metric).setChecked(Constants.isMetric);
        menu.findItem(R.id.action_imperial).setChecked(!Constants.isMetric);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                new AboutDialogFragment().show(getSupportFragmentManager(), "information");
                return true;
            case R.id.action_metric:
                if (!item.isChecked()) {
                    Constants.isMetric = true;
                    item.setChecked(true);
                    mMenu.findItem(R.id.action_imperial).setChecked(false);
                    updateUnits();
                }
                return true;
            case R.id.action_imperial:
                if (!item.isChecked()) {
                    Constants.isMetric = false;
                    item.setChecked(true);
                    mMenu.findItem(R.id.action_metric).setChecked(false);
                    updateUnits();
                }
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
        WeatherViewFragment fragment = (WeatherViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (fragment != null){
            fragment.onCurrentWeatherDataUpdate(currentWeatherData);
        } else {
            Log.v(TAG, "onCurrentWeatherDataUpdate(), but WeatherViewFragment can not be found by id.");
        }
    }

    @Override
    public void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        WeatherViewFragment fragment = (WeatherViewFragment)
                getSupportFragmentManager().findFragmentById(R.id.mainFragmentContainer);
        if (fragment != null){
            fragment.onForecastWeatherDataUpdate(forecastWeatherData);
        } else {
            Log.v(TAG, "onForecastWeatherDataUpdate(), but WeatherViewFragment can not be found by id.");
        }
    }

    @Override
    public void onRefresh() {
        WeatherFragment weatherFragment = (WeatherFragment)
                getSupportFragmentManager().findFragmentByTag(WeatherFragment.TAG);
        LocationFragment locationFragment = (LocationFragment)
                getSupportFragmentManager().findFragmentByTag(LocationFragment.TAG);

        // if locationFragment or weatherFragment has old data
        if (locationFragment.hasOldData() || weatherFragment.hasOldData()){
            getSupportFragmentManager().beginTransaction()
                    .remove(locationFragment)
                    .add(LocationFragment.newInstance(), LocationFragment.TAG)
                    .commit();
        }
        weatherFragment.refresh();

    }

    private void updateUnits() {
        Log.v(TAG, "updateUnits()");
        onRefresh();
    }


}
