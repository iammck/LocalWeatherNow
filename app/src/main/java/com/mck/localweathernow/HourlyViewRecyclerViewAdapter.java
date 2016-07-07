package com.mck.localweathernow;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mck.localweathernow.asynctask.GetWeatherIconAsyncTask;
import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.model.Period;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * {@link RecyclerView.Adapter} that can display hourly weather forecast.
 */
class HourlyViewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CURRENT = 1;
    private static final int VIEW_TYPE_PERIOD = 2;
    private static final int VIEW_TYPE_LOADING = 3;
    private static final String TAG = "HourlyRVAdapter";
    private static final String DNE = "---";
    private Period[] periods;
    private CurrentWeatherData currentWeatherData;
    private ArrayList<Integer> itemViewTypes;

    HourlyViewRecyclerViewAdapter(){
        Log.v(TAG, "HourlyViewRecyclerViewAdapter instantiation in progress.");
        itemViewTypes = new ArrayList<>();
        itemViewTypes.add(0,VIEW_TYPE_LOADING);
    }

    @Override
    public int getItemCount() {
        Log.v(TAG, "getItemCount() returning " + itemViewTypes.size());
        return itemViewTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        Log.v(TAG, "getItemViewType() for position " + position);
        if (position < itemViewTypes.size()){
            return itemViewTypes.get(position);
        } else {
            Log.v(TAG, "getItemViewType() for position " + position + ", but itemViewTypes does not contain position.");
            // produce an error.
            return 0;
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder for viewType " + viewType);
        View view;
        switch (viewType){
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_loading, parent, false);
                return new LoadingViewHolder(view);
            case VIEW_TYPE_CURRENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_current, parent, false);
                return new CurrentViewHolder(view);
            case VIEW_TYPE_PERIOD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_period, parent, false);
                return new PeriodViewHolder(view);
            }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        Log.v(TAG, "onBindViewHolder() for position " + position + " with payload " + payloads);
        if (payloads.isEmpty()){
            onBindViewHolder(holder, position);
        } else if (payloads.get(0) instanceof Bitmap){
            Log.v(TAG, "onBindViewHolder() for position " + position + " with icon payload");
            if (holder instanceof CurrentViewHolder){
                ((CurrentViewHolder) holder).ivIcon.setImageBitmap((Bitmap) payloads.get(0));
            } else if (holder instanceof PeriodViewHolder){
                ((PeriodViewHolder) holder).ivIcon.setImageBitmap((Bitmap) payloads.get(0));
            }
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder() for position " + position);
        // is this the current weather view holder?
        if (holder instanceof CurrentViewHolder){
            bindCurrentView((CurrentViewHolder) holder);
        } else if (holder instanceof PeriodViewHolder) {
            bindPeriodView((PeriodViewHolder) holder, position);
        }
    }

    private void bindCurrentView(CurrentViewHolder holder) {

        // tvCurrentTime
        String currentTime = WeatherDataHelper.formatTime(System.currentTimeMillis()/1000);
        holder.tvCurrentTime.setText(currentTime);
        // tvTime
        if (currentWeatherData.dt != null){
            String formattedTime = WeatherDataHelper.formatTimeAmPm(currentWeatherData.dt);
            holder.tvTime.setText(formattedTime);
            holder.tvTime.setVisibility(View.VISIBLE);
        }else{
            holder.tvTime.setVisibility(View.GONE);
        }

        // tvTemperature,
        if (currentWeatherData.main.temp != null) {
            String formattedTemperature = WeatherDataHelper.formatTemperature(currentWeatherData.main.temp);
            holder.tvTemperature.setText(formattedTemperature);
        }else{
            holder.tvTemperature.setText(DNE);
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
        // todo
        // is there an icon already? use it.

        GetWeatherIconAsyncTask task = new GetWeatherIconAsyncTask(
                holder.mView.getContext(), holder,
                holder.getAdapterPosition(),
                currentWeatherData.weather[0].icon);
        task.execute();
    }

    private void bindPeriodView(PeriodViewHolder holder, int position) {
        Period period;
        if (itemViewTypes.get(0) == VIEW_TYPE_PERIOD){
            period = periods[position];
        } else {
            period = periods[position - 1];
        }
        // tvTime
        if (period.dt != null){
            String formattedTime = WeatherDataHelper.formatTimeAmPm(period.dt);
            holder.tvTime.setText(formattedTime);
            holder.tvTime.setVisibility(View.VISIBLE);
        }else{
            holder.tvTime.setVisibility(View.GONE);
        }

        // tvTemperature,
        if (period.main.temp != null) {
            String formattedTemperature = WeatherDataHelper.formatTemperature(period.main.temp);
            holder.tvTemperature.setText(formattedTemperature);
        }else{
            holder.tvTemperature.setText(DNE);
        }

        // tvTemperatureHighLow,
        if (period.main.temp_max != null &&
                period.main.temp_min != null){
            String formattedTemperatureHigh = WeatherDataHelper.formatTemperature(period.main.temp_max);
            String formattedTemperatureLow = WeatherDataHelper.formatTemperature(period.main.temp_min);
            String result = formattedTemperatureHigh + "/" + formattedTemperatureLow;
            holder.tvTemperatureHighLow.setText(result);
        } else {
            holder.tvTemperatureHighLow.setText(DNE);
        }

        // tvDescription,
        if (period.weather.length > 0 &&
                period.weather[0].description != null){
            holder.tvDescription.setText(
                    period.weather[0].description.toUpperCase());
        } else {
            holder.tvDescription.setText(DNE);
        }

        GetWeatherIconAsyncTask task = new GetWeatherIconAsyncTask(
                holder.mView.getContext(), holder,
                holder.getAdapterPosition(),
                period.weather[0].icon);
        task.execute();
    }



    void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        this.currentWeatherData = currentWeatherData;
        // if there are many items and have already bound
        if (itemViewTypes.get(0) != VIEW_TYPE_PERIOD){
            itemViewTypes.set(0, VIEW_TYPE_CURRENT);
            notifyItemChanged(0);
        } else {
            itemViewTypes.add(0, VIEW_TYPE_CURRENT);
            notifyItemInserted(0);
        }
    }

    void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        periods = forecastWeatherData.list;
        if (itemViewTypes.get(0) == VIEW_TYPE_LOADING){
            int index = 0;
            itemViewTypes.remove(index);
            while (index < periods.length) {
                itemViewTypes.add(index++, VIEW_TYPE_PERIOD);
            }
            notifyItemRemoved(0);
            notifyItemRangeInserted(0, periods.length - 1);

        } else if (itemViewTypes.get(0) == VIEW_TYPE_PERIOD){
            notifyItemRangeChanged(0, periods.length - 1);
        } else {
            if (itemViewTypes.size() > 1){
                notifyItemRangeChanged(1, periods.length - 1);
            } else {
                int index = 1;
                while (index <= periods.length){
                    itemViewTypes.add(index++, VIEW_TYPE_PERIOD);
                }
                notifyItemRangeInserted(1, periods.length - 1);
            }
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        View mView;
        LoadingViewHolder(View view) {
            super(view);
            mView = view;
        }
    }

    private class CurrentViewHolder extends RecyclerView.ViewHolder implements GetWeatherIconAsyncTask.Callback {
        View mView;
        TextView tvCurrentTime, tvTime, tvTemperature, tvTemperatureHighLow, tvDescription;
        ImageView ivIcon;

        CurrentViewHolder(View view) {
            super(view);
            mView = view;

            tvCurrentTime = (TextView) mView.findViewById(R.id.tvCurrentTime);
            tvTime = (TextView) mView.findViewById(R.id.tvTime);
            tvTemperature = (TextView) mView.findViewById(R.id.tvTemperature);
            tvTemperatureHighLow = (TextView) mView.findViewById(R.id.tvTemperatureHighLow);
            tvDescription = (TextView) mView.findViewById(R.id.tvDescription);

            ivIcon = (ImageView) mView.findViewById(R.id.ivIcon);

        }

        @Override
        public void onWeatherIconResult(Bitmap icon, Integer requestId) {
            //ivIcon.setImageBitmap(icon);
            notifyItemChanged(getAdapterPosition(), icon);
        }
    }

    private class PeriodViewHolder extends RecyclerView.ViewHolder implements GetWeatherIconAsyncTask.Callback {
        View mView;
        TextView tvTime, tvTemperature, tvTemperatureHighLow, tvDescription;
        ImageView ivIcon;

        PeriodViewHolder(View view) {
            super(view);
            mView = view;
            tvTime = (TextView) mView.findViewById(R.id.tvTime);
            tvTemperature = (TextView) mView.findViewById(R.id.tvTemperature);
            tvTemperatureHighLow = (TextView) mView.findViewById(R.id.tvTemperatureHighLow);
            tvDescription = (TextView) mView.findViewById(R.id.tvDescription);

            ivIcon = (ImageView) mView.findViewById(R.id.ivIcon);
        }

        @Override
        public void onWeatherIconResult(Bitmap icon, Integer requestId) {
            notifyItemChanged(getAdapterPosition(), icon);
            // TODO
        }
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
