package com.example.public_transportation_user_application;

import android.os.Parcel;
import android.os.Parcelable;

public class Stops implements Parcelable {
    private double latitude, longitude;
    private String stopsName,durationStr="NA";

    public Stops(){
        //default constructor
    }

    public Stops(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        stopsName = in.readString();
        durationStr = in.readString();
    }

    public static final Creator<Stops> CREATOR = new Creator<Stops>() {
        @Override
        public Stops createFromParcel(Parcel in) {
            return new Stops(in);
        }

        @Override
        public Stops[] newArray(int size) {
            return new Stops[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(stopsName);
        dest.writeString(durationStr);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStopsName() {
        return stopsName;
    }

    public void setStopsName(String stopsName) {
        this.stopsName = stopsName;
    }

    public String getDurationStr() {
        return durationStr;
    }

    public void setDurationStr(String durationStr) {
        this.durationStr = durationStr;
    }

}
