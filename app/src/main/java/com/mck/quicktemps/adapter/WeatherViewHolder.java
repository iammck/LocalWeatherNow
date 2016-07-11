package com.mck.quicktemps.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mck.quicktemps.R;

import java.util.List;

/**
 * Base class for current and period view holders.
 *
 * Created by Michael on 7/8/2016.
 */

class WeatherViewHolder extends ViewHolder {
    static final String ON_CLICK = "ON_CLICK";
    private static final String TAG = "WeatherViewHolder";
    View mView;

    public final WeatherViewRecyclerViewAdapter adapter;
    TextView tvTime, tvLabelGeneratedTime, tvGeneratedTime, tvTemperature,
            tvTemperatureHighLow, tvDescription, tvHumidity, tvCloudiness,
            tvRainfall, tvSnowfall, tvWindSpeed, tvWindDirection,
            tvLabelHumidity, tvLabelCloudiness;

    // tvDate, tvLocation;
    @SuppressWarnings("WeakerAccess")
    public LinearLayout layoutDetails, layoutWind,
            layoutRainfall, layoutSnowfall;

    @SuppressWarnings("WeakerAccess")
    public RelativeLayout layoutMainInfo;

    ImageView ivIcon;

    boolean detailsAreVisible;

    WeatherViewHolder(View view, WeatherViewRecyclerViewAdapter adapter) {
        super(view);
        mView = view;
        tvTime = (TextView) mView.findViewById(R.id.tvTime);
        tvLabelGeneratedTime = (TextView) mView.findViewById(R.id.tvLabelGeneratedTime);
        tvGeneratedTime = (TextView)mView.findViewById(R.id.tvGeneratedTime);
        tvTemperature = (TextView) mView.findViewById(R.id.tvTemperature);
        tvTemperatureHighLow = (TextView) mView.findViewById(R.id.tvTemperatureHighLow);
        tvDescription = (TextView) mView.findViewById(R.id.tvDescription);
        ivIcon = (ImageView) mView.findViewById(R.id.ivIcon);
        tvHumidity = (TextView) mView.findViewById(R.id.tvHumidity);
        tvLabelHumidity = (TextView) mView.findViewById(R.id.tvLabelHumidy);
        tvCloudiness = (TextView) mView.findViewById(R.id.tvCloudiness);
        tvLabelCloudiness = (TextView) mView.findViewById(R.id.tvLabelCloudiness);
        tvRainfall = (TextView) mView.findViewById(R.id.tvRainfall);
        tvSnowfall = (TextView) mView.findViewById(R.id.tvSnowfall);
        tvWindSpeed = (TextView) mView.findViewById(R.id.tvWindSpeed);
        tvWindDirection = (TextView) mView.findViewById(R.id.tvWindDirection);
        layoutWind = (LinearLayout) mView.findViewById(R.id.layoutWind);
        layoutRainfall = (LinearLayout) mView.findViewById(R.id.layoutRainfall);
        layoutSnowfall = (LinearLayout) mView.findViewById(R.id.layoutSnowfall);
        layoutDetails = (LinearLayout) mView.findViewById(R.id.layoutDetails);
        layoutMainInfo = (RelativeLayout) mView.findViewById(R.id.layoutMainInfo);
        this.adapter = adapter;
    }

    void bindViewHolder(List<Object> payloads) {
        if (payloads != null && payloads.contains(ON_CLICK)) {
            return;
        } else {
            if (detailsAreVisible) {
                detailsAreVisible = true;
                layoutDetails.setVisibility(View.VISIBLE);
            } else {
                detailsAreVisible = false;
                layoutDetails.setVisibility(View.GONE);

            }
        }
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "onclick() has begun");
                adapter.notifyItemChanged(getAdapterPosition(),ON_CLICK);
            }
        });
    }
}
