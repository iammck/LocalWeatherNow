package com.mck.localweathernow.ui.adapter;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.mck.localweathernow.R;
import com.mck.localweathernow.WeatherDataHelper;
import com.mck.localweathernow.asynctask.GetWeatherIconAsyncTask;
import com.mck.localweathernow.model.CurrentWeatherData;

import java.util.List;

/**
 * Holder of CurrentViewData for display in RecyclerView.Adapter
 * Created by Michael on 7/8/2016.
 */
class CurrentViewHolder extends HourlyViewHolder implements GetWeatherIconAsyncTask.Callback {
    private TextView tvCurrentTime;

    CurrentViewHolder(View view, HourlyViewRecyclerViewAdapter adapter) {
        super(view, adapter);
        tvCurrentTime = (TextView) mView.findViewById(R.id.tvCurrentTime);
    }

    @Override
    public void onWeatherIconResult(Bitmap icon, Integer requestId) {
        //ivIcon.setImageBitmap(icon);
        adapter.notifyItemChanged(getAdapterPosition(), icon);
    }

    void onBindViewHolder(CurrentWeatherData currentWeatherData, List<Object> payloads) {
        // is this
        if (payloads != null && !payloads.isEmpty() && payloads.get(0) instanceof Bitmap) {
            ivIcon.setImageBitmap((Bitmap) payloads.get(0));
            return;
        }

        // tvCurrentTime
        String currentTime = WeatherDataHelper.formatTime(System.currentTimeMillis()/1000);
        tvCurrentTime.setText(currentTime);
        // tvTime
        if (currentWeatherData.dt != null){
            String formattedTime = WeatherDataHelper.formatTimeAmPm(currentWeatherData.dt);
            tvTime.setText(formattedTime);
            tvTime.setVisibility(View.VISIBLE);
        }else{
            tvTime.setVisibility(View.GONE);
        }

        // tvTemperature,
        if (currentWeatherData.main.temp != null) {
            String formattedTemperature = WeatherDataHelper.formatTemperature(currentWeatherData.main.temp);
            tvTemperature.setText(formattedTemperature);
        }else{
            tvTemperature.setText(DNE);
        }

        // tvTemperatureHighLow,
        if (currentWeatherData.main.temp_max != null &&
                currentWeatherData.main.temp_min != null){
            String formattedTemperatureHigh = WeatherDataHelper.formatTemperature(currentWeatherData.main.temp_max);
            String formattedTemperatureLow = WeatherDataHelper.formatTemperature(currentWeatherData.main.temp_min);
            String result = formattedTemperatureHigh + "/" + formattedTemperatureLow;
            tvTemperatureHighLow.setText(result);
        } else {
            tvTemperatureHighLow.setText(DNE);
        }

        // tvDescription,
        if (currentWeatherData.weather.length > 0 &&
                currentWeatherData.weather[0].description != null){
            tvDescription.setText(
                currentWeatherData.weather[0].description.toUpperCase());
        } else {
            tvDescription.setText(DNE);
        }

        GetWeatherIconAsyncTask task = new GetWeatherIconAsyncTask(
                mView.getContext(), this,
                getAdapterPosition(),
                currentWeatherData.weather[0].icon);
        task.execute();
    }
}

//tvDate,
//tvLocation,
//tvHumidity,
//tvCloudCover,
//tvRainfall,
//tvSnowfall,
//tvWindSpeed,
//tvWindDirection;

// tvDate,
        /*if (currentWeatherData.dt != null){
            String formattedDate = WeatherDataHelper.formatDate(currentWeatherData.dt);
            holder.tvDate.setText(formattedDate);
            holder.tvDate.setVisibility(View.VISIBLE);
        }else{
            holder.tvDate.setVisibility(View.GONE);
        }*/
// tvLocation,
        /*if (currentWeatherData.name != null) {
            holder.tvLocation.setText(currentWeatherData.name);
            holder.tvLocation.setVisibility(View.VISIBLE);
        }else{
            holder.tvLocation.setVisibility(View.GONE);
        }*/


// tvHumidity,
        /*if (currentWeatherData.main.humidity != null){
            String formattedPercent = WeatherDataHelper.formatPercent(currentWeatherData.main.humidity);
            holder.tvHumidity.setText(formattedPercent);
        } else {
            holder.tvHumidity.setText(DNE);
        }*/
// tvCloudCover,
        /*if (currentWeatherData.clouds != null && currentWeatherData.clouds.all != null ){
            String formattedPercent = WeatherDataHelper.formatPercent(currentWeatherData.clouds.all);
            holder.tvCloudCover.setText(formattedPercent);
        } else {
            holder.tvCloudCover.setText(DNE);
        }*/

// tvRainfall,
        /*if (currentWeatherData.rain != null &&
                currentWeatherData.rain.threeHour != null){
            String formattedVolume = WeatherDataHelper.formatVolume(currentWeatherData.rain.threeHour);
            holder.tvRainfall.setText(formattedVolume);
        } else {
            holder.tvRainfall.setText(DNE);
        }*/
// tvSnowfall,
        /*if (currentWeatherData.snow != null &&
                currentWeatherData.snow.threeHour != null){
            String formattedVolume = WeatherDataHelper.formatVolume(currentWeatherData.snow.threeHour);
            holder.tvSnowfall.setText(formattedVolume);
        } else {
            holder.tvSnowfall.setText(DNE);
        }*/
// tvWindSpeed,
        /*if (currentWeatherData.wind != null &&
                currentWeatherData.wind.speed != null){
            String formattedSpeed = WeatherDataHelper.formatSpeed(currentWeatherData.wind.speed);
            holder.tvWindSpeed.setText(formattedSpeed);
        } else {
            holder.tvWindSpeed.setText(DNE);
        }*/
// tvWindDirection;
        /*if (currentWeatherData.wind != null &&
                currentWeatherData.wind.deg != null){
            String formattedDirection = WeatherDataHelper.formatDirection(currentWeatherData.wind.deg);
            holder.tvWindDirection.setText(formattedDirection);
        } else {
            holder.tvWindDirection.setText(DNE);
        }*/
