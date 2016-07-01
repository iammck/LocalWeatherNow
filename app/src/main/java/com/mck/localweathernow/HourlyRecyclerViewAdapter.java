package com.mck.localweathernow;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;
import com.mck.localweathernow.model.Period;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display hourly weather forecast.
 */
class HourlyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CURRENT = 0;
    private static final int VIEW_TYPE_PERIOD = 1;
    private static final String TAG = "HourlyRVAdapter";
    private Period[] periods;
    private ArrayList<PeriodViewHolder> periodViewHolders;
    private CurrentWeatherData currentWeather;
    private CurrentViewHolder currentViewHolder;

    public HourlyRecyclerViewAdapter(){
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
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        Log.v(TAG, "onBindViewHolder() for position " + position);
        // is this the current weather view holder?
        if (position == 0){
            currentViewHolder = (CurrentViewHolder) holder;
            // TODO Set up the currentWeatherView holder
        } else {
            PeriodViewHolder periodViewHolder = (PeriodViewHolder) holder;
            periodViewHolders.add(position - 1, periodViewHolder);
            // TODO Set up the periodViewHolder for position
        }

        /*holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   }
        });*/
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
            return VIEW_TYPE_CURRENT;
        } else {
            return VIEW_TYPE_PERIOD;
        }
    }

    void updateForecastWeather(ForecastWeatherData forecastWeather) {
        Log.v(TAG, "updateForecastWeather");
        periods = forecastWeather.list;
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

    public void updateCurrentWeather(CurrentWeatherData currentWeather) {
        Log.v(TAG, "updateCurrentWeather");
        this.currentWeather = currentWeather;
        if (currentViewHolder != null){
            // TODO Update current weather view.
        }
        notifyItemChanged(0);
    }

    class CurrentViewHolder extends RecyclerView.ViewHolder {
        View mView;
        CurrentViewHolder(View view) {
            super(view);
            mView = view;
        }
    }

    class PeriodViewHolder extends RecyclerView.ViewHolder {
        View mView;
        PeriodViewHolder(View view) {
            super(view);
            mView = view;
        }
    }
}
