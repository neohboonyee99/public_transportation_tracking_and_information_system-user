package com.example.public_transportation_user_application;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class RouteNumber implements Parcelable {
    private String routeNumber;
    private List<Stops> stopsList;

    public RouteNumber(){
        //default constructor
    }

    public RouteNumber(Parcel in){
        routeNumber = in.readString();
        if (in.readByte() == 0x01) {
            stopsList = new ArrayList<Stops>();
            in.readList(stopsList, Stops.class.getClassLoader());
        }
        else {
            stopsList = null;
        }
    }


    public static final Creator<RouteNumber> CREATOR = new Creator<RouteNumber>() {
        @Override
        public RouteNumber createFromParcel(Parcel in) {
            return new RouteNumber(in);
        }

        @Override
        public RouteNumber[] newArray(int size) {
            return new RouteNumber[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(routeNumber);
        dest.writeTypedList(stopsList);
    }

    public String getRouteNumber() {
        return routeNumber;
    }

    public void setRouteNumber(String routeNumber) {
        this.routeNumber = routeNumber;
    }

    public List<Stops> getStopsList() {
        return stopsList;
    }

    public void setStopsList(List<Stops> stopsList) {
        this.stopsList = stopsList;
    }
}
