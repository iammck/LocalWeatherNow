package com.mck.localweathernow;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * {@link RecyclerView.Adapter} that can display hourly weather forecast.
 */
class HourlyRecyclerViewAdapter extends RecyclerView.Adapter<HourlyRecyclerViewAdapter.ViewHolder> {

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_hourly, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View mView;

        ViewHolder(View view) {
            super(view);
            mView = view;
        }
    }
}
