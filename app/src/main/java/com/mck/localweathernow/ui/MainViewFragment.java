package com.mck.localweathernow.ui;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mck.localweathernow.R;
import com.mck.localweathernow.model.CurrentWeatherData;
import com.mck.localweathernow.model.ForecastWeatherData;


public class MainViewFragment extends Fragment {
    public static final String TAG = "MainViewFragment";

    private String[] fragmentTags;

    public MainViewFragment() {
        fragmentTags = new String[2];
    }

    public static MainViewFragment newInstance() {
        return new MainViewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        Log.v(TAG, "onCreateView()");
        SectionsPagerAdapter sectionsPagerAdapter =
                new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPagerContainer);
        viewPager.setAdapter(sectionsPagerAdapter);
        // Set up the tab layout with the view pager.
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }


    public void onCurrentWeatherDataUpdate(CurrentWeatherData currentWeatherData) {
        Log.v(TAG, "onCurrentWeatherDataUpdate()");
        HourlyViewFragment fragment = (HourlyViewFragment)
                getChildFragmentManager().findFragmentByTag(fragmentTags[0]);
        fragment.onCurrentWeatherDataUpdate(currentWeatherData);
    }

    public void onForecastWeatherDataUpdate(ForecastWeatherData forecastWeatherData) {
        Log.v(TAG, "onForecastWeatherDataUpdate()");
        HourlyViewFragment fragment = (HourlyViewFragment)
                getChildFragmentManager().findFragmentByTag(fragmentTags[0]);
        fragment.onForecastWeatherDataUpdate(forecastWeatherData);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            Log.v(TAG, "getItem() for position " + position);
            switch (position) {
                case 0:
                    return HourlyViewFragment.newInstance();
                case 1:
                    return DailyViewFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.hourly);
                case 1:
                    return getString(R.string.daily);
            }
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            fragmentTags[position] = fragment.getTag();
            return fragment;
        }

    }



}
