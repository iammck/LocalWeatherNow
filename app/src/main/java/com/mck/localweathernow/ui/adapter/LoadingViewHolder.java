package com.mck.localweathernow.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

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
}
