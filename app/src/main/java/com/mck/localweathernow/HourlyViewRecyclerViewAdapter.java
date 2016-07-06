package com.mck.localweathernow;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.model.Period;

import java.util.ArrayList;

/**
 *
 * {@link RecyclerView.Adapter} that can display hourly weather forecast.
 */
class HourlyViewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CURRENT = 0;
    private static final int VIEW_TYPE_PERIOD = 1;
    private static final int VIEW_TYPE_LOADING = 3;
    private static final String TAG = "HourlyRVAdapter";
    private static final String DNE = "---";
    private Period[] periods;
    private ArrayList<PeriodViewHolder> periodViewHolders;
    private CurrentWeatherData currentWeatherData;

    HourlyViewRecyclerViewAdapter(){
        Log.v(TAG, "HourlyViewRecyclerViewAdapter instantiation in progress.");
        periodViewHolders = new ArrayList<>();

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder for viewType " + viewType);
        View view;
        switch (viewType){
            case VIEW_TYPE_CURRENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_current, parent, false);
                return new CurrentViewHolder(view);
            case VIEW_TYPE_PERIOD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_period, parent, false);
                return new PeriodViewHolder(view);
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_loading, parent, false);
                return new LoadingViewHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder() for position " + position);
        // is this the current weather view holder?
        if (holder instanceof CurrentViewHolder){
                bindCurrentViewHolder((CurrentViewHolder) holder);
        } else if (holder instanceof PeriodViewHolder){
            PeriodViewHolder periodViewHolder = (PeriodViewHolder) holder;
            periodViewHolders.add(position - 1, periodViewHolder);
            bindPeriodViewHolder(periodViewHolder, position);
        }// nothing to do if holder is a LoadingViewHolder instance.

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   }
        });*/
    }

    private void bindPeriodViewHolder(PeriodViewHolder periodViewHolder, int position) {
        // TODO Set up the periodViewHolder for position.
    }

    private void bindCurrentViewHolder(CurrentViewHolder holder) {
        // tvDate,
        if (currentWeatherData.dt != null){
            String formattedDate = WeatherDataHelper.formatDate(currentWeatherData.dt);
            holder.tvDate.setText(formattedDate);
            holder.tvDate.setVisibility(View.VISIBLE);
        }else{
            holder.tvDate.setVisibility(View.GONE);
        }
        // tvLocation,
        if (currentWeatherData.name != null) {
            holder.tvLocation.setText(currentWeatherData.name);
            holder.tvLocation.setVisibility(View.VISIBLE);
        }else{
            holder.tvLocation.setVisibility(View.GONE);
        }

        // tvTemperature,
        if (currentWeatherData.main.temp != null) {
            String formattedTemperature = WeatherDataHelper.formatTemperature(currentWeatherData.main.temp);
            holder.tvTemperature.setText(formattedTemperature);
        }else{
            holder.tvTemperature.setText(DNE);
        }
        // tvHumidity,
        if (currentWeatherData.main.humidity != null){
            String formattedPercent = WeatherDataHelper.formatPercent(currentWeatherData.main.humidity);
            holder.tvHumidity.setText(formattedPercent);
        } else {
            holder.tvHumidity.setText(DNE);
        }
        // tvCloudCover,
        if (currentWeatherData.clouds != null && currentWeatherData.clouds.all != null ){
            String formattedPercent = WeatherDataHelper.formatPercent(currentWeatherData.clouds.all);
            holder.tvCloudCover.setText(formattedPercent);
        } else {
            holder.tvCloudCover.setText(DNE);
        }
        // tvTemperatureHighLow,
        if (currentWeatherData.main.temp_max != null &&
                currentWeatherData.main.temp_min != null){
            String formattedTemperatureHigh = WeatherDataHelper.formatTemperature(currentWeatherData.main.temp_max);
            String formattedTemperatureLow = WeatherDataHelper.formatTemperature(currentWeatherData.main.temp_min);
            String result = formattedTemperatureHigh + "/" + formattedTemperatureLow;
            holder.tvTemperatureHighLow.setText(result);
        } else {
            holder.tvTemperatureHighLow.setText(DNE);
        }

        // tvDescription,
        if (currentWeatherData.weather.length > 0 &&
                currentWeatherData.weather[0].description != null){
            holder.tvDescription.setText(
                    currentWeatherData.weather[0].description.toUpperCase());
        } else {
            holder.tvDescription.setText(DNE);
        }
        // tvRainfall,
        if (currentWeatherData.rain != null &&
                currentWeatherData.rain.threeHour != null){
            String formattedVolume = WeatherDataHelper.formatVolume(currentWeatherData.rain.threeHour);
            holder.tvRainfall.setText(formattedVolume);
        } else {
            holder.tvRainfall.setText(DNE);
        }
        // tvSnowfall,
        if (currentWeatherData.snow != null &&
                currentWeatherData.snow.threeHour != null){
            String formattedVolume = WeatherDataHelper.formatVolume(currentWeatherData.snow.threeHour);
            holder.tvSnowfall.setText(formattedVolume);
        } else {
            holder.tvSnowfall.setText(DNE);
        }
        // tvWindSpeed,
        if (currentWeatherData.wind != null &&
                currentWeatherData.wind.speed != null){
            String formattedSpeed = WeatherDataHelper.formatSpeed(currentWeatherData.wind.speed);
            holder.tvWindSpeed.setText(formattedSpeed);
        } else {
            holder.tvWindSpeed.setText(DNE);
        }
        // tvWindDirection;
        if (currentWeatherData.wind != null &&
                currentWeatherData.wind.deg != null){
            String formattedDirection = WeatherDataHelper.formatDirection(currentWeatherData.wind.deg);
            holder.tvWindDirection.setText(formattedDirection);
        } else {
            holder.tvWindDirection.setText(DNE);
        }

        // ivIcon TODO;
    }

    @Override
    public int getItemCount() {
        Log.v(TAG, "getItemCount() returning " + (periodViewHolders.size() + 1));
        return periodViewHolders.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        Log.v(TAG, "getItemViewType() for position " + position);
        if (position == 0){
            if (currentWeatherData == null){
                return VIEW_TYPE_LOADING;
            }
            return VIEW_TYPE_CURRENT;
        } else {
            return VIEW_TYPE_PERIOD;
        }
    }

    void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        this.currentWeatherData = currentWeatherData;
        //notifyItemChanged(0);
    }

    void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        periods = forecastWeatherData.list;
        // if there there are no periodViewHolders
        if (periodViewHolders.isEmpty()){
            notifyItemRangeInserted(1, periods.length);
        } else if (periods.length == periodViewHolders.size()){
            // TODO Update the viewHolders
            notifyItemRangeChanged(1, periodViewHolders.size());
        } else {
            notifyItemRangeRemoved(1, periodViewHolders.size());
            notifyItemRangeInserted(1, periods.length);
        }
    }

    private class CurrentViewHolder extends RecyclerView.ViewHolder {
        View mView;

        TextView tvDate, tvLocation, tvTemperature,
                tvHumidity, tvCloudCover, tvTemperatureHighLow, tvDescription,
                tvRainfall, tvSnowfall, tvWindSpeed, tvWindDirection;

        ImageView ivIcon;

        CurrentViewHolder(View view) {
            super(view);
            mView = view;

            tvDate = (TextView) mView.findViewById(R.id.tvDate);
            tvLocation = (TextView) mView.findViewById(R.id.tvLocation);
            tvTemperature = (TextView) mView.findViewById(R.id.tvTemperature);

            tvHumidity = (TextView) mView.findViewById(R.id.tvHumidity);
            tvCloudCover = (TextView) mView.findViewById(R.id.tvCloudCover);
            tvTemperatureHighLow = (TextView) mView.findViewById(R.id.tvTemperatureHighLow);
            tvDescription = (TextView) mView.findViewById(R.id.tvDescription);

            tvRainfall = (TextView) mView.findViewById(R.id.tvRainfall);
            tvSnowfall = (TextView) mView.findViewById(R.id.tvSnowfall);
            tvWindSpeed = (TextView) mView.findViewById(R.id.tvWindSpeed);
            tvWindDirection = (TextView) mView.findViewById(R.id.tvWindDirection);

            ivIcon = (ImageView) mView.findViewById(R.id.ivIcon);

        }
    }

    private class PeriodViewHolder extends RecyclerView.ViewHolder {
        View mView;

        TextView tvDate, tvLocation, tvTime, tvTemperature,
        tvHumidity, tvCloudCover, tvTemperatureHighLow, tvDescription,
        tvRainfall, tvSnowfall, tvWindSpeed, tvWindDirection;

        ImageView ivIcon;

        PeriodViewHolder(View view) {
            super(view);
            mView = view;
            tvDate = (TextView) mView.findViewById(R.id.tvDate);
            tvLocation = (TextView) mView.findViewById(R.id.tvLocation);
            tvTime = (TextView) mView.findViewById(R.id.tvTime);
            tvTemperature = (TextView) mView.findViewById(R.id.tvTemperature);

            tvHumidity = (TextView) mView.findViewById(R.id.tvHumidity);
            tvCloudCover = (TextView) mView.findViewById(R.id.tvCloudCover);
            tvTemperatureHighLow = (TextView) mView.findViewById(R.id.tvTemperatureHighLow);
            tvDescription = (TextView) mView.findViewById(R.id.tvDescription);

            tvRainfall = (TextView) mView.findViewById(R.id.tvRainfall);
            tvSnowfall = (TextView) mView.findViewById(R.id.tvSnowfall);
            tvWindSpeed = (TextView) mView.findViewById(R.id.tvWindSpeed);
            tvWindDirection = (TextView) mView.findViewById(R.id.tvWindDirection);

            ivIcon = (ImageView) mView.findViewById(R.id.ivIcon);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public LoadingViewHolder(View view) {
            super(view);
            mView = view;
        }
    }
}
