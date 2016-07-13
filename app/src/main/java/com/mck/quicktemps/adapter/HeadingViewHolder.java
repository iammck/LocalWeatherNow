package com.mck.quicktemps.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mck.quicktemps.R;

import java.util.List;

/**
 * view to show when there is no data.
 * Created by Michael on 7/8/2016.
 */
class HeadingViewHolder extends RecyclerView.ViewHolder {
    static final String WEATHER_UPDATE = "WEATHER_UPDATE";
    View mView;
    TextView tvLoadingMessage;
    ProgressBar pbLoading;
    TextView tvLocation;

    boolean hasLocationName;

    HeadingViewHolder(View view, String locationName) {
        super(view);
        tvLoadingMessage = (TextView) view.findViewById(R.id.tvLoadingMessage);
        pbLoading = (ProgressBar) view.findViewById(R.id.pbLoading);
        tvLocation = (TextView) view.findViewById(R.id.tvLocation);
        if (locationName != null){
            this.hasLocationName = true;
            tvLoadingMessage.setVisibility(View.GONE);
            pbLoading.setVisibility(View.GONE);
            tvLocation.setVisibility(View.VISIBLE);
        } else
            hasLocationName = false;
    }

    void onBindViewHolder(String loactionName, List<Object> payloads) {
        if (loactionName != null && loactionName != null) {
            hasLocationName = true;
            tvLocation.setText(loactionName);
        } else {
            hasLocationName = false;
            tvLocation.setText("");
        }
    }
}
