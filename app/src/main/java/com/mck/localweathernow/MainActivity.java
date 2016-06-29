package com.mck.localweathernow;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.mck.localweathernow.dialog.RequiresPermissionsRationaleDialogFragment;
import com.mck.localweathernow.dialog.RequiresSettingsRationaleDialogFragment;

public class MainActivity extends AppCompatActivity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RequiresSettingsRationaleDialogFragment.RequiresSettingsRationaleCallback,
        RequiresPermissionsRationaleDialogFragment.RequiresPermissionsRationaleCallback {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final int REQUEST_PERMISSIONS_ACCESS_ID = 23;
    private static final int REQUEST_SETTINGS_RESOLUTION_ID = 72;
    private static final int GPS_CONNECTION_RESOLUTION_ID = 43;
    private boolean requiresSettingsRationale;
    private boolean requiresPermissionsRationale;
    private boolean requiresGooglePlayServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        // Set up the tab layout with the view pager.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            switch (position) {
                case 0:
                    return HourlyFragment.newInstance();
                case 1:
                    return DailyFragment.newInstance();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requiresGooglePlayServices){
            // TODO
        }
        // create the googleApiClient for access to google services.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() ){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        if (mGoogleApiClient != null &&
                (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())) {
            mGoogleApiClient.disconnect();
        }
    }

    // google api client is connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // create location request instance
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.locationReqInterval);
        mLocationRequest.setFastestInterval(Constants.fastestLocationReqInterval);
        //mLocationRequest.setSmallestDisplacement(100);// in meters.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        // if currently showing RequiresSettingsRationaleDialogFragment, just return;
        if (requiresSettingsRationale){
            RequiresSettingsRationaleDialogFragment frag = (RequiresSettingsRationaleDialogFragment)
                    getSupportFragmentManager().findFragmentByTag(RequiresSettingsRationaleDialogFragment.TAG);
            if (frag == null){
                frag = RequiresSettingsRationaleDialogFragment.newInstance(this);
                frag.show(getSupportFragmentManager(), RequiresSettingsRationaleDialogFragment.TAG);
            }
            return;
        }
        if (requiresPermissionsRationale){
            RequiresPermissionsRationaleDialogFragment frag = (RequiresPermissionsRationaleDialogFragment)
                    getSupportFragmentManager().findFragmentByTag(RequiresPermissionsRationaleDialogFragment.TAG);
            if (frag == null){
                frag = RequiresPermissionsRationaleDialogFragment.newInstance(this);
                frag.show(getSupportFragmentManager(), RequiresPermissionsRationaleDialogFragment.TAG);
            }
            return;
        }

        // Make sure location services are enabled.
        // get a location settings builder and check location settings via a PendingResult
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, settingsBuilder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied(ie: not in airplane mode). The client can
                        // If the application does not have necessary permissions
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            // Request the permissions.
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION },
                                    REQUEST_PERMISSIONS_ACCESS_ID);
                            return;
                        }
                        // have permissions, go ahead and request updates.
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, MainActivity.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    MainActivity.this,
                                    REQUEST_SETTINGS_RESOLUTION_ID);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        onLocationsSettingsFailure();
                }
            }
        });
    }

    // google api client has been suspended.
    @Override
    public void onConnectionSuspended(int i) {

    }

    // google api client has failed.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(this, GPS_CONNECTION_RESOLUTION_ID);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    // location has been updated.
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS_RESOLUTION_ID) {
            if (resultCode != Activity.RESULT_OK) {
                requiresSettingsRationale = true;
            }
        }
        if (requestCode == GPS_CONNECTION_RESOLUTION_ID) {
            if (resultCode != Activity.RESULT_OK) {
                requiresGooglePlayServices = true;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Make sure the app has location permissions. If it does not,
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requiresPermissionsRationale = true;
        }

    }

    @Override
    public void requiresSettingsRationaleTryAgain() {
        requiresSettingsRationale = false;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    @Override
    public void requiresPermissionsRationaleTryAgain() {
        requiresPermissionsRationale = false;
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            requestLocationUpdates();
        }
    }

    private void onLocationsSettingsFailure() {

    }

}
