package com.mck.localweathernow.ui.adapter;

import android.graphics.Bitmap;
import android.view.View;

import com.mck.localweathernow.WeatherDataHelper;
import com.mck.localweathernow.asynctask.GetWeatherIconAsyncTask;
import com.mck.localweathernow.model.Period;

import java.util.List;

/**
 * Holder of CurrentViewData for display in RecyclerView.Adapter
 * Created by Michael on 7/8/2016.
 */
class PeriodViewHolder extends HourlyViewHolder implements GetWeatherIconAsyncTask.Callback {

    PeriodViewHolder(View view, HourlyViewRecyclerViewAdapter adapter) {
        super(view, adapter);
    }

    @Override
    public void onWeatherIconResult(Bitmap icon, Integer requestId) {
        ivIcon.setImageBitmap(icon);
    }


    void bindPeriodView(Period period, List<Object> payloads) {

        if (payloads != null && !payloads.isEmpty() && payloads.get(0) instanceof Bitmap) {
            ivIcon.setImageBitmap((Bitmap) payloads.get(0));
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

        GetWeatherIconAsyncTask task = new GetWeatherIconAsyncTask(
                mView.getContext(), this,
                getAdapterPosition(),
                period.weather[0].icon);
        task.execute();
    }
}
