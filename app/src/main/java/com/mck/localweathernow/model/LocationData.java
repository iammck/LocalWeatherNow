package com.mck.localweathernow.model;

/**
 * used to contain location data
 * Created by Michael on 7/2/2016.
 */
public class LocationData {
    public double latitude;
    public double longitude;
    public float accuracy;
    public long time;

    public LocationData(double latitude, double longitude,
                        float accuracy, long time) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.accuracy = accuracy;
        this.time = time;
    }
}
