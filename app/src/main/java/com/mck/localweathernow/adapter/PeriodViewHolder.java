package com.mck.localweathernow.adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.mck.localweathernow.WeatherDataHelper;
import com.mck.localweathernow.asynctask.GetWeatherIconAsyncTask;
import com.mck.localweathernow.model.Period;

import java.util.List;

/**
 * Holder of CurrentViewData for display in RecyclerView.Adapter
 * Created by Michael on 7/8/2016.
 */
class PeriodViewHolder extends WeatherViewHolder implements GetWeatherIconAsyncTask.Callback {
    private static final String TAG = "PeriodViewHolder";

    PeriodViewHolder(View view, WeatherViewRecyclerViewAdapter adapter) {
        super(view, adapter);
        detailsAreVisible = false;
    }

    @Override
    public void onWeatherIconResult(Bitmap icon, Integer requestId) {
        ivIcon.setImageBitmap(icon);
    }


    void bindPeriodView(Period period, List<Object> payloads) {
        super.bindViewHolder(payloads);
        Log.v(TAG,"onBindViewHolder() for position " + getAdapterPosition() + " with payload " + payloads );

        if (payloads != null && payloads.contains(ON_CLICK)){
            return;
        }

        // tvTime
        if (period.dt != null){
            String formattedTime = WeatherDataHelper.formatTimeAmPm(period.dt);
            tvTime.setText(formattedTime);
            tvTime.setVisibility(View.VISIBLE);
        }
        // remove these two TextView
        tvLabelGeneratedTime.setVisibility(View.GONE);
        tvGeneratedTime.setVisibility(View.GONE);

        // tvTemperature,
        if (period.main.temp != null) {
            String formattedTemperature = WeatherDataHelper.formatTemperature(period.main.temp);
            tvTemperature.setText(formattedTemperature);
        }

        // tvTemperatureHighLow,
        if (period.main.temp_max != null &&
                period.main.temp_min != null){
            String formattedTemperatureHigh = WeatherDataHelper.formatTemperature(period.main.temp_max);
            String formattedTemperatureLow = WeatherDataHelper.formatTemperature(period.main.temp_min);
            String result = formattedTemperatureHigh + "/" + formattedTemperatureLow;
            tvTemperatureHighLow.setText(result);
        }

        // tvDescription,
        if (period.weather.length > 0 &&
                period.weather[0].description != null){
            tvDescription.setText(
                    period.weather[0].description.toUpperCase());
        }

        //////////////////
        // Extra data

        // tvWindSpeed,
        if (period.wind != null &&
                period.wind.speed != null){
            String formattedSpeed = WeatherDataHelper.formatSpeed(period.wind.speed);
            tvWindSpeed.setText(formattedSpeed);
            layoutWind.setVisibility(View.VISIBLE);
        } else {
            layoutWind.setVisibility(View.GONE);
        }
        // tvWindDirection;
        if (period.wind != null &&
                period.wind.deg != null){
            String formattedDirection = WeatherDataHelper.formatDirection(period.wind.deg);
            tvWindDirection.setText(formattedDirection);
            tvWindDirection.setVisibility(View.VISIBLE);
        } else {
            tvWindDirection.setVisibility(View.GONE);
        }

        // tvHumidity,
        if (period.main.humidity != null){
            String formattedPercent = WeatherDataHelper.formatPercent(period.main.humidity);
            tvHumidity.setText(formattedPercent);
            tvHumidity.setVisibility(View.VISIBLE);
            tvLabelHumidity.setVisibility(View.VISIBLE);
        } else {
            tvHumidity.setVisibility(View.GONE);
            tvLabelHumidity.setVisibility(View.GONE);
        }
        // tvCloudiness,
        if (period.clouds != null && period.clouds.all != null ){
            String formattedPercent = WeatherDataHelper.formatPercent(period.clouds.all);
            tvCloudiness.setText(formattedPercent);
            tvCloudiness.setVisibility(View.VISIBLE);
            tvLabelCloudiness.setVisibility(View.VISIBLE);
        } else {
            tvCloudiness.setVisibility(View.GONE);
            tvLabelCloudiness.setVisibility(View.GONE);
        }

        // tvRainfall,
        if (period.rain != null &&
                period.rain.threeHour != null){
            String formattedVolume = WeatherDataHelper.formatVolume(period.rain.threeHour);
            tvRainfall.setText(formattedVolume);
            layoutRainfall.setVisibility(View.VISIBLE);
        } else {
            layoutRainfall.setVisibility(View.GONE);
        }
        // tvSnowfall,
        if (period.snow != null &&
                period.snow.threeHour != null){
            String formattedVolume = WeatherDataHelper.formatVolume(period.snow.threeHour);
            tvSnowfall.setText(formattedVolume);
            layoutSnowfall.setVisibility(View.VISIBLE);
        } else {
            layoutSnowfall.setVisibility(View.GONE);
        }

/*
        if(detailsAreVisible)
            layoutDetails.setVisibility(View.VISIBLE);
        else
            layoutDetails.setVisibility(View.GONE);
*/

        GetWeatherIconAsyncTask task = new GetWeatherIconAsyncTask(
                mView.getContext(), this,
                getAdapterPosition(),
                period.weather[0].icon);
        task.execute();
    }
}
