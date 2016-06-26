package com.mck.localweathernow.hourly;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mck.localweathernow.R;

/**
 * A fragment representing a list of hourly forecasts.
 */
public class HourlyFragment extends Fragment {
    public HourlyFragment() {    }

    public static HourlyFragment newInstance() {
        return new HourlyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hourly, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new HourlyRecyclerViewAdapter());
        }
        return view;
    }
}
