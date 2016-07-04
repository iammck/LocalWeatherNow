package com.mck.localweathernow.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * used to contain location data
 * Created by Michael on 7/2/2016.
 */
public class LocationData implements Parcelable{
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

    private LocationData(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        accuracy = in.readFloat();
        time = in.readLong();
    }

    public static final Creator<LocationData> CREATOR
            = new Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel in) {
            return new LocationData(in);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeFloat(accuracy);
        parcel.writeLong(time);
    }
}
