package com.mck.localweathernow;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

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
import com.mck.localweathernow.dialog.LocationSettingsFailureDialogFragment;
import com.mck.localweathernow.dialog.RequiresGooglePlayServicesDialogFragment;
import com.mck.localweathernow.dialog.RequiresPermissionsRationaleDialogFragment;
import com.mck.localweathernow.dialog.RequiresSettingsRationaleDialogFragment;

public class LocationFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RequiresSettingsRationaleDialogFragment.RequiresSettingsRationaleCallback,
        RequiresPermissionsRationaleDialogFragment.RequiresPermissionsRationaleCallback {
    public static String TAG = "LocationFragment";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;


    private static final long ACCEPTABLE_TIME_DELTA = 10000;
    private static final float OPTIMAL_ACCURACY = 1500;
    private static final String KEY_CUR_LAT = "KEY_CUR_LAT";
    private static final String KEY_CUR_LON = "KEY_CUR_LON";
    private static final String KEY_CUR_LOC_RETRVL_TIME = "KEY_CUR_LOC_RETRVL_TIME";
    private static final String KEY_CUR_LOC_ACCURACY = "KEY_CUR_LOC_ACCURACY";

    private LocationFragmentListener mListener;


    class LocationData {
        double latitude;
        double longitude;
        float accuracy;
        long time;

        LocationData(double latitude, double longitude,
                     float accuracy, long time){

            this.latitude = latitude;
            this.longitude = longitude;
            this.accuracy = accuracy;
            this.time = time;
        }
    }

    private boolean requiresSettingsRationale = false;
    private boolean requiresPermissionsRationale = false;

    private LocationData locationData;

    public LocationFragment() {
    }

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            double lat = savedInstanceState.getDouble(KEY_CUR_LAT);
            double lon = savedInstanceState.getDouble(KEY_CUR_LON);
            float acc = savedInstanceState.getFloat(KEY_CUR_LOC_ACCURACY);
            long time = savedInstanceState.getLong(KEY_CUR_LOC_RETRVL_TIME);
            locationData = new LocationData(lat, lon, acc, time);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationFragmentListener) {
            mListener = (LocationFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LocationFragmentListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart()");
        // if we have some usable location data and no weather data,
        // get current and forecast data
        if (locationData != null){
            mListener.onLocationUpdate(locationData);
            /*// if current and forecast data is present
            if (currentWeather != null && forecastWeather != null){
                updateCurrentWeatherListener();
                updateForecastWeatherListener();
            } else { // otherwise get weather data.
                getWeatherData();
            }*/
        }
        // if !isLocationDataUsable (no data) or accuracy is greater than optimal
        if (locationData == null ||
                locationData.accuracy > OPTIMAL_ACCURACY ){
            // create the googleApiClient for access to google services.
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
        if (requiresSettingsRationale){
            Log.v(TAG, "onResume() requiresSettingsRationale so building and showing dialog now.");
            requiresSettingsRationale = false;
            RequiresSettingsRationaleDialogFragment frag =
                    RequiresSettingsRationaleDialogFragment.newInstance(this);
            frag.show(getChildFragmentManager(), RequiresSettingsRationaleDialogFragment.TAG);
        } else if (requiresPermissionsRationale){
            Log.v(TAG, "onResume() requiresPermissionsRationale so building and showing dialog now.");
            requiresPermissionsRationale = false;
            RequiresPermissionsRationaleDialogFragment frag =
                    RequiresPermissionsRationaleDialogFragment.newInstance(this);
            frag.show(getChildFragmentManager(), RequiresPermissionsRationaleDialogFragment.TAG);
        }

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
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() ){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        if (mGoogleApiClient != null &&
                (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(KEY_CUR_LAT, locationData.latitude);
        outState.putDouble(KEY_CUR_LON, locationData.longitude);
        outState.putLong(KEY_CUR_LOC_RETRVL_TIME, locationData.time);
        outState.putFloat(KEY_CUR_LOC_ACCURACY, locationData.accuracy);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    // google api client is connected
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "onConnected()");
        // create location request instance
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.locationReqInterval);
        mLocationRequest.setFastestInterval(Constants.fastestLocationReqInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        //  only want to requestLocationUpdates() if there is not requiresSettingsRationale dialog already showing.
        if (getChildFragmentManager().findFragmentByTag(RequiresSettingsRationaleDialogFragment.TAG) == null &&
                getChildFragmentManager().findFragmentByTag(RequiresPermissionsRationaleDialogFragment.TAG) == null) {
            Log.v(TAG, "onConnect() .. no requires settings and permissions dialogs, requesting location updates.");
            requestLocationUpdates();
        }
    }

    private void requestLocationUpdates() {
        Log.v(TAG, "requestLocationUpdates()");
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
                        Log.v(TAG, "onResult() LocationSettingsStatusCodes.SUCCESS");
                        // All location settings are satisfied(ie: not in airplane mode). The client can
                        // If the application does not have necessary permissions
                        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                                PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                        PackageManager.PERMISSION_GRANTED) {
                            Log.v(TAG, "onResult() LocationSettingsStatusCodes.SUCCESS, but needs to request permissions");
                            // Request the permissions.
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION },
                                    Constants.REQUEST_PERMISSIONS_ACCESS_ID);
                            return;
                        }
                        Log.v(TAG, "onResult() LocationSettingsStatusCodes.SUCCESS and has permissions, requesting updates");
                        // have permissions, go ahead and request updates.
                        LocationServices.FusedLocationApi.requestLocationUpdates(
                                mGoogleApiClient, mLocationRequest, LocationFragment.this);
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.v(TAG, "onResult() LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(),
                                    Constants.REQUEST_SETTINGS_RESOLUTION_ID);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.v(TAG, "onResult() LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        onLocationsSettingsFailure();
                }
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_SETTINGS_RESOLUTION_ID) {
            Log.v(TAG, "onActivityResult() requestCode is REQUEST_SETTINGS_RESOLUTION_ID");
            if (resultCode != Activity.RESULT_OK) {
                requiresSettingsRationale = true;
            }
        }
        if (requestCode == Constants.GPS_CONNECTION_RESOLUTION_ID) {
            Log.v(TAG, "onActivityResult() requestCode is GPS_CONNECTION_RESOLUTION_ID");
            if (resultCode != Activity.RESULT_OK) {
                RequiresGooglePlayServicesDialogFragment frag =
                        RequiresGooglePlayServicesDialogFragment.newInstance();
                frag.show(getChildFragmentManager(), RequiresGooglePlayServicesDialogFragment.TAG);
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v(TAG, "onRequestPermissionsResult()");
        // Make sure the app has location permissions. If it does not,
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "onRequestPermissionsResult() .. permission not granted.");
            requiresPermissionsRationale = true;
        } else {
            Log.v(TAG, "onRequestPermissionsResult() .. permission is granted.");

        }

    }

    private void onLocationsSettingsFailure() {
        LocationSettingsFailureDialogFragment frag = LocationSettingsFailureDialogFragment.newInstance();
            frag.show(getChildFragmentManager(), LocationSettingsFailureDialogFragment.TAG);
    }

    // google api client has been suspended.
    @Override
    public void onConnectionSuspended(int i) {

    }

    // google api client has failed.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            connectionResult.startResolutionForResult(getActivity(), Constants.GPS_CONNECTION_RESOLUTION_ID);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            RequiresGooglePlayServicesDialogFragment frag =
                    RequiresGooglePlayServicesDialogFragment.newInstance();
            frag.show(getChildFragmentManager(), RequiresGooglePlayServicesDialogFragment.TAG);
        }
    }

    // location has been updated.
    @Override
    public void onLocationChanged(Location location) {
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        float acc = location.getAccuracy();
        long time = System.currentTimeMillis();
        locationData = new LocationData(lat, lon, acc, time);

        // if the accuracy is great, remove location helper fragment.
        if (locationData.accuracy < OPTIMAL_ACCURACY){
            if (mGoogleApiClient.isConnected()){
                mGoogleApiClient.unregisterConnectionCallbacks(this);
                mGoogleApiClient.unregisterConnectionFailedListener(this);
                mGoogleApiClient.disconnect();
            }
        }
        mListener.onLocationUpdate(locationData);
    }

    @Override
    public void requiresSettingsRationaleTryAgain() {
        requestLocationUpdates();
    }

    @Override
    public void requiresPermissionsRationaleTryAgain() {
        requestLocationUpdates();
    }

    public interface LocationFragmentListener {
        void onLocationUpdate(LocationData location);
    }
}
