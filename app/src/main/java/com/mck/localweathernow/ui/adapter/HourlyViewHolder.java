package com.mck.localweathernow.ui.adapter;

import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mck.localweathernow.R;

/**
 * Created by Michael on 7/8/2016.
 */

class HourlyViewHolder extends ViewHolder {
    static final String DNE = "---";
    View mView;
    final HourlyViewRecyclerViewAdapter adapter;
    TextView tvTime, tvTemperature, tvTemperatureHighLow, tvDescription;
    ImageView ivIcon;


    HourlyViewHolder(View view, HourlyViewRecyclerViewAdapter adapter) {
        super(view);
        mView = view;
        tvTime = (TextView) mView.findViewById(R.id.tvTime);
        tvTemperature = (TextView) mView.findViewById(R.id.tvTemperature);
        tvTemperatureHighLow = (TextView) mView.findViewById(R.id.tvTemperatureHighLow);
        tvDescription = (TextView) mView.findViewById(R.id.tvDescription);
        ivIcon = (ImageView) mView.findViewById(R.id.ivIcon);
        this.adapter = adapter;
    }
}
