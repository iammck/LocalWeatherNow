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
 *
 * {@link RecyclerView.Adapter} that can display weather forecast.
 */
public class WeatherViewRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "WeatherRVAdapter";

    private static final int VIEW_TYPE_CURRENT = 1;
    private static final int VIEW_TYPE_PERIOD = 2;
    private static final int VIEW_TYPE_LOADING = 3;
    public static final String UPDATE = "UPDATE";
    private ArrayList<Integer> itemViewTypes;

    private boolean hasCurrentViewHolder = false;
    private CurrentWeatherData currentWeatherData;
    private Period[] periods;

    public WeatherViewRecyclerViewAdapter(){
        Log.v(TAG, "WeatherViewRecyclerViewAdapter instantiation in progress.");
        itemViewTypes = new ArrayList<>();
        itemViewTypes.add(0,VIEW_TYPE_LOADING);
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
            case VIEW_TYPE_LOADING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_loading, parent, false);
                return new LoadingViewHolder(view);
            case VIEW_TYPE_CURRENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_weather, parent, false);
                return new CurrentViewHolder(view, this);
            case VIEW_TYPE_PERIOD:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_weather, parent, false);
                return new PeriodViewHolder(view, this);
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
        if (holder instanceof CurrentViewHolder){
            hasCurrentViewHolder = true;
            ((CurrentViewHolder) holder).onBindViewHolder(currentWeatherData, payloads);
        } else if (holder instanceof PeriodViewHolder){
            if (hasCurrentViewHolder)
                ((PeriodViewHolder)  holder).bindPeriodView(periods[position - 1] , payloads);
            else
                ((PeriodViewHolder)  holder).bindPeriodView(periods[position] , payloads);
        } else {
            ((LoadingViewHolder) holder).onBindViewHolder(payloads);
        }
    }

    public void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        this.currentWeatherData = currentWeatherData;
        // if there are many items and have already bound
        if (itemViewTypes.get(0) == VIEW_TYPE_LOADING){
            itemViewTypes.set(0, VIEW_TYPE_CURRENT);
            notifyItemRemoved(0);
            notifyItemInserted(1);
        } else if (itemViewTypes.get(0) == VIEW_TYPE_PERIOD){
            itemViewTypes.add(0, VIEW_TYPE_CURRENT);
            notifyItemInserted(0);
        } else {
            notifyItemChanged(0, UPDATE);
        }

    }

    public void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
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
            notifyItemRangeChanged(0, periods.length - 1, UPDATE);
        } else {
            if (itemViewTypes.size() > 1){
                notifyItemRangeChanged(1, periods.length, UPDATE);
            } else {
                int index = 1;
                while (index <= periods.length){
                    itemViewTypes.add(index++, VIEW_TYPE_PERIOD);
                }
                notifyItemRangeInserted(1, periods.length - 1);
            }
        }
    }
}
