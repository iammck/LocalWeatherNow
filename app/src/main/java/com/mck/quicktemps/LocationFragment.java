package com.mck.quicktemps;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
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
import com.mck.quicktemps.dialog.LocationSettingsFailureDialogFragment;
import com.mck.quicktemps.dialog.RequiresGooglePlayServicesDialogFragment;
import com.mck.quicktemps.dialog.RequiresPermissionsRationaleDialogFragment;
import com.mck.quicktemps.dialog.RequiresSettingsRationaleDialogFragment;
import com.mck.quicktemps.model.LocationData;

@SuppressWarnings("WeakerAccess")
public class LocationFragment extends Fragment implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        RequiresSettingsRationaleDialogFragment.RequiresSettingsRationaleCallback,
        RequiresPermissionsRationaleDialogFragment.RequiresPermissionsRationaleCallback {
    public static String TAG = "LocationFragment";

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationModeReceiver mLocationModeReceiver;

    private static final String KEY_LOCATION_DATA = TAG + ".key_location_data";

    private LocationFragmentListener mListener;

    private boolean requiresSettingsRationale = false;
    private boolean requiresPermissionsRationale = false;

    private LocationData locationData;

    public LocationFragment() {
    }

    static LocationFragment newInstance() {
        return new LocationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
                locationData = savedInstanceState.getParcelable(KEY_LOCATION_DATA);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LocationFragmentListener) {
            mListener = (LocationFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Location2FragmentListener");
        }
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
        if (requiresSettingsRationale) {
            Log.v(TAG, "onResume() requiresSettingsRationale so building and showing dialog now.");
            requiresSettingsRationale = false;
            RequiresSettingsRationaleDialogFragment frag =
                    RequiresSettingsRationaleDialogFragment.newInstance(this);
            frag.show(getChildFragmentManager(), RequiresSettingsRationaleDialogFragment.TAG);
        } else if (requiresPermissionsRationale) {
            Log.v(TAG, "onResume() requiresPermissionsRationale so building and showing dialog now.");
            requiresPermissionsRationale = false;
            RequiresPermissionsRationaleDialogFragment frag =
                    RequiresPermissionsRationaleDialogFragment.newInstance(this);
            frag.show(getChildFragmentManager(), RequiresPermissionsRationaleDialogFragment.TAG);
        } else if (getChildFragmentManager().findFragmentByTag(RequiresSettingsRationaleDialogFragment.TAG) != null ||
                getChildFragmentManager().findFragmentByTag(RequiresPermissionsRationaleDialogFragment.TAG) != null) {
            // if showing either of the dialogs, can return without connecting
            Log.v(TAG, "onResume() a dialog is showing so just returning.");
        } else if (hasIdealLocationData()) {
            Log.v(TAG, "onResume() has ideal location data, forwarding to listener.onLocationDataUpdate().");
            mListener.onLocationDataUpdate(locationData);
        } else {
            Log.v(TAG, "onResume() does not have ideal location data, calling connectGoogleApiClient()");
            disconnectGoogleApiClient();
            connectGoogleApiClient();
            IntentFilter filter = new IntentFilter("android.location.MODE_CHANGED");
            mLocationModeReceiver = new LocationModeReceiver();
            getActivity().registerReceiver(mLocationModeReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
        if (mLocationModeReceiver != null) {
            getActivity().unregisterReceiver(mLocationModeReceiver);
            mLocationModeReceiver = null;
        }
        disconnectGoogleApiClient();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (locationData != null) {
            outState.putParcelable(KEY_LOCATION_DATA, locationData);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void connectGoogleApiClient() {
        Log.v(TAG, "connectGoogleApiClient()");
        // create the googleApiClient for access to google services.
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void disconnectGoogleApiClient() {
        Log.v(TAG, "disconnectGoogleApiClient()");
        // if we are listening for location updates from LocationManager,
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
        if (mGoogleApiClient != null &&
                (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected())) {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient = null;
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
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        Log.v(TAG, "requestLocationUpdates()...");
        // Make sure location services are enabled.
        // get a location settings builder and check location settings via a PendingResult
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, settingsBuilder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Log.v(TAG, "onResult()");
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
                                            Manifest.permission.ACCESS_COARSE_LOCATION},
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
                        break;
                    default:
                        Log.v(TAG, "default case:");
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
        Log.v(TAG, "onLocationSettingsFailure()");
        LocationSettingsFailureDialogFragment frag = LocationSettingsFailureDialogFragment.newInstance();
            frag.show(getChildFragmentManager(), LocationSettingsFailureDialogFragment.TAG);
    }

    // google api client has been suspended.
    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "onConnectionSuspended()");
    }

    // google api client has failed.
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        try {
            Log.v(TAG, "onConnectionFailed() with result " + connectionResult);
            if (!connectionResult.hasResolution()){
                RequiresGooglePlayServicesDialogFragment frag =
                        RequiresGooglePlayServicesDialogFragment.newInstance();
                frag.show(getChildFragmentManager(), RequiresGooglePlayServicesDialogFragment.TAG);
            } else {
                connectionResult.startResolutionForResult(getActivity(), Constants.GPS_CONNECTION_RESOLUTION_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
            RequiresGooglePlayServicesDialogFragment frag =
                    RequiresGooglePlayServicesDialogFragment.newInstance();
            frag.show(getChildFragmentManager(), RequiresGooglePlayServicesDialogFragment.TAG);
        }
    }

    // location has been updated.
    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "onLocationChanged()");
        double lat = location.getLatitude();
        double lon = location.getLongitude();
        float acc = location.getAccuracy();
        long time = System.currentTimeMillis();
        locationData = new LocationData(lat, lon, acc, time);

        // if the accuracy is great, remove location helper fragment.
        if (hasIdealLocationData()){
            Log.v(TAG, "onLocationChanged() with hasIdealLocationData() returning true. Disconnecting the mGoogleApiClient");
            disconnectGoogleApiClient();
        }
        mListener.onLocationDataUpdate(locationData);
    }

    @Override
    public void requiresSettingsRationaleTryAgain() {
        disconnectGoogleApiClient();
        connectGoogleApiClient();
    }

    @Override
    public void requiresPermissionsRationaleTryAgain() {
        disconnectGoogleApiClient();
        connectGoogleApiClient();
    }

    public boolean hasIdealLocationData(){
        // if no locationData or accuracy is greater than optimal or the time is stale
        return !(locationData == null ||
                locationData.accuracy > Constants.MIN_ACCURACY ||
                System.currentTimeMillis() - locationData.time > Constants.MAX_LOC_TIME_DELTA);
    }

    public boolean hasOldData() {
        // if no locationData or the time is stale
        boolean result =  (locationData == null ||
                System.currentTimeMillis() - locationData.time > Constants.MAX_LOC_TIME_DELTA);
        Log.v(TAG, "hasOldData() with result " + result);
        return result;
    }

    public class LocationModeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "LocationModeReceiver.onReceiver()");
            try {
                ContentResolver contentResolver = context.getContentResolver();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    int mode = Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE);
                    if (mode == Settings.Secure.LOCATION_MODE_OFF){
                        Log.v(TAG, "LocationModeReceiver.onReceiver() with mode Settings.Secure.LOCATION_MODE_OFF.");
                        disconnectGoogleApiClient();
                        connectGoogleApiClient();
                    }
                } else {
                    @SuppressWarnings("deprecation")
                    String providers = Settings.Secure.getString(contentResolver, Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
                    Log.v(TAG, "LocationModeReceiver.onReceiver() with providers " + providers);
                    if (providers.isEmpty()){
                        disconnectGoogleApiClient();
                        connectGoogleApiClient();
                    }
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    interface LocationFragmentListener {
        void onLocationDataUpdate(LocationData location);
    }
}