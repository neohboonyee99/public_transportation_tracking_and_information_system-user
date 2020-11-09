package com.example.public_transportation_user_application.Retrofit;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ServerService {
    String BASE_URL = "http://192.168.0.101/fyp/public_transportation_tracking_and_information/";

    @POST("getNearbyBusStop.php")
    @FormUrlEncoded
    Call<JsonObject> getNearbyBusStop(
            @Field("latitude") double latitude,
            @Field("longitude") double longitude);

    @POST("getBus.php")
    @FormUrlEncoded
    Call<JsonObject> getBus(
            @Field("stop_name") String stopName
    );

    @POST("getBusStopsDetails.php")
    @FormUrlEncoded
    Call<JsonObject> getBusStopLatLng(
            @Field("stops_name") String stopName
    );

    @POST("getRoute.php")
    Call<JsonObject> getRoute();

}
