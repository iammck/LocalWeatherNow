package com.mck.quicktemps.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mck.quicktemps.R;
import com.mck.quicktemps.model.CurrentWeatherData;
import com.mck.quicktemps.model.ForecastWeatherData;
import com.mck.quicktemps.model.Period;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display weather forecast.
 */
public class WeatherViewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "WeatherRVAdapter";

    private static final int VIEW_TYPE_CURRENT = 1;
    private static final int VIEW_TYPE_PERIOD = 2;
    private static final int VIEW_TYPE_HEADING = 3;
    private static final String UPDATE = "UPDATE";
    private ArrayList<Integer> itemViewTypes;

    private boolean hasCurrentViewHolder = false;
    private CurrentWeatherData currentWeatherData;
    private Period[] periods;
    private String locationName;

    public WeatherViewRecyclerViewAdapter(){
        Log.v(TAG, "WeatherViewRecyclerViewAdapter instantiation in progress.");
        itemViewTypes = new ArrayList<>();
        itemViewTypes.add(0, VIEW_TYPE_HEADING);
    }

    @Override
    public int getItemCount() {
        return itemViewTypes.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < itemViewTypes.size()){
            return itemViewTypes.get(position);
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.v(TAG, "onCreateViewHolder for viewType " + viewType);
        View view;
        switch (viewType){
            case VIEW_TYPE_PERIOD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_weather, parent, false);
                return new PeriodViewHolder(view, this);
            case VIEW_TYPE_CURRENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_weather, parent, false);
                return new CurrentViewHolder(view, this);
            case VIEW_TYPE_HEADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_heading, parent, false);
                return new HeadingViewHolder(view, locationName);
            }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder() for position " + position);
        onBindViewHolder(holder, position, null);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        Log.v(TAG, "onBindViewHolder() for position " + position + " with payload " + payloads);
        if (holder instanceof PeriodViewHolder){
            if (hasCurrentViewHolder) {
                ((PeriodViewHolder) holder).bindPeriodView(periods[position - 2], payloads);
            } else
                ((PeriodViewHolder)  holder).bindPeriodView(periods[position - 1] , payloads);
        } else if (holder instanceof CurrentViewHolder){
            hasCurrentViewHolder = true;
            ((CurrentViewHolder) holder).onBindViewHolder(currentWeatherData, payloads);
        }else {
            ((HeadingViewHolder) holder).onBindViewHolder(locationName, payloads);
        }
    }

    public void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        this.currentWeatherData = currentWeatherData;
        locationName = currentWeatherData.name;
        notifyItemChanged(0, HeadingViewHolder.WEATHER_UPDATE);
        // check the second view type
        // if it does not contain or is not view type current in position 1.
        if (itemViewTypes.size() < 2 || itemViewTypes.get(1) != VIEW_TYPE_CURRENT){
            // insert current view type here and in adapter.
            itemViewTypes.add(1, VIEW_TYPE_CURRENT);
            notifyItemInserted(1);
        } else {
            // else just update
            notifyItemChanged(1, UPDATE);
        }
    }

    public void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        periods = forecastWeatherData.list;
        locationName = forecastWeatherData.city.name;
        notifyItemChanged(0, HeadingViewHolder.WEATHER_UPDATE);

        // if it does not contain or is not view type current in position 1.
        if (itemViewTypes.size() < 2 || itemViewTypes.get(1) != VIEW_TYPE_CURRENT) {
            // if it only contains the heading view type
            if (itemViewTypes.size() == 1){
                // add the view types
                int index = 1;
                while (index < periods.length + 1) {
                    itemViewTypes.add(index++, VIEW_TYPE_PERIOD);
                }
                // insert each period
                notifyItemRangeInserted(1, periods.length);
            } else {
                // update the periods
                notifyItemRangeChanged(1, periods.length, UPDATE);
            }
        } else {
            // if it does not contain any periods
            if (itemViewTypes.size() < 3){
                int index = 2;
                // add the view types
                while (index < periods.length + 2) {
                    itemViewTypes.add(index++, VIEW_TYPE_PERIOD);
                }
                // insert each period
                notifyItemRangeInserted(2, periods.length);
            } else {
                // update the periods
                notifyItemRangeChanged(2, periods.length, UPDATE);
            }
        }
    }
}
