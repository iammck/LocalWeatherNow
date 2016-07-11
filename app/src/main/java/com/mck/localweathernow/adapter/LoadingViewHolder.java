package com.mck.localweathernow.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mck.localweathernow.Constants;

import java.util.List;

/**
 * view to show when there is no data.
 * Created by Michael on 7/8/2016.
 */
class LoadingViewHolder extends RecyclerView.ViewHolder {
    View mView;

    LoadingViewHolder(View view) {
        super(view);
        mView = view;
    }

    public void onBindViewHolder(List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty() && payloads.get(0).equals(Constants.REMOVE_LOADING_VIEW)){

        }
    }
}
