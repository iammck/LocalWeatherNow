package com.mck.localweathernow;

/**
 * Constants used through out the app are stored here for easy updates
 * and consistency.
 * Created by Michael on 6/25/2016.
 */
public class Constants {
    public static final String API_ID = PrivateConstants.API_ID;
    public static final int NUM_PERIODS_TO_REQUEST = 8;
    public static final String UNITS = "metric";

    static long locationReqInterval = 4000;
    static long fastestLocationReqInterval = 1000;

    static final int REQUEST_PERMISSIONS_ACCESS_ID = 23;
    static final int REQUEST_SETTINGS_RESOLUTION_ID = 72;
    static final int GPS_CONNECTION_RESOLUTION_ID = 43;
    static final long MAX_LOC_TIME_DELTA = 10000;
    static final float MIN_ACCURACY = 1500;


}
