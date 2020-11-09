package com.example.public_transportation_user_application;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.public_transportation_user_application.Retrofit.RetrofitClient;
import com.example.public_transportation_user_application.Retrofit.ServerService;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LOCATION_SERVICE;

public class NearbyBusStopETAFragment extends Fragment {

    LocationManager mLocationManager;
    double latitude ,longitude, busLatitude,busLongitude;
    public static final int LOCATION_REQUEST = 1;
    private LocationListener mLocationListener;
    static Location cLocation;
    RecyclerView nearbyBusStopRV;
    ImageView noItemFoundIV;
    TextView noItemFoundTV;
    CustomNearbyBusStopAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Stops> stopList;
    List<Stops> adapterList;
    String responseMsg;
    int shortestDuration =-1;
    String durationStr="";

    RecyclerView.LayoutManager mLayoutManager;

    public NearbyBusStopETAFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby_bus_stop_eta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nearbyBusStopRV = view.findViewById(R.id.busStopRV);
        noItemFoundIV = view.findViewById(R.id.noItemFoundIV);
        noItemFoundTV = view.findViewById(R.id.noItemFoundTV);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutNearby);
        stopList = new ArrayList<>();


        mLocationListener = location -> {
            Log.e("Location", "Location Changed");
            cLocation = location;
            latitude = cLocation.getLatitude();
            longitude = cLocation.getLongitude();
            getData();

        };

        getLocation();
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getData();
        });
    }

    private void getData(){
        ServerService serverService = RetrofitClient.getClient(ServerService.BASE_URL).create(ServerService.class);
        stopList = new ArrayList<>();

        Call<JsonObject> getNearByBusStop = serverService.getNearbyBusStop(latitude,longitude);
        getNearByBusStop.enqueue(new Callback<JsonObject>(){

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){
                    JsonObject jsonObject = response.body();
                    responseMsg = jsonObject.get("msg").getAsString();
                    Log.e("Nearbyu", String.valueOf(jsonObject));
                    boolean error= jsonObject.get("error").getAsBoolean();
                    if (!error){
                        JsonArray stops = jsonObject.get("bus_stops").getAsJsonArray();
                        for(JsonElement JO : stops){
                            JsonObject j = JO.getAsJsonObject();
                            Stops busStop = new Stops();
                            busStop.setLatitude(j.get("latitude").getAsDouble());
                            busStop.setLongitude(j.get("longitude").getAsDouble());
                            String stopName = j.get("stops_name").getAsString();
                            Log.e("stopnae", stopName);
                            busStop.setStopsName(stopName);
                            stopList.add(busStop);
                            loadData();
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }

                }
                else{
                    loadData();
                    Toast.makeText(getContext(),responseMsg,Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadData(){
        ServerService serverService = RetrofitClient.getClient(ServerService.BASE_URL).create(ServerService.class);
        if (stopList.size()!=0) {
            for (int i = 0; i < stopList.size(); i++) {
                Gson s = new Gson();
                String test = s.toJson(stopList);
                Log.e("stoplist", test);
                Call<JsonObject> getBus = serverService.getBus(stopList.get(i).getStopsName());
                int finalI = i;
                getBus.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {

                            JsonObject jsonObject = response.body();
                            Log.e("getbus", String.valueOf(jsonObject));
                            responseMsg = jsonObject.get("msg").getAsString();
                            boolean error = jsonObject.get("error").getAsBoolean();
                            if (!error) {
                                JsonArray bus = jsonObject.get("bus").getAsJsonArray();

                                for (JsonElement JO : bus) {
                                    JsonObject j = JO.getAsJsonObject();
                                    busLatitude = j.get("latitude").getAsDouble();
                                    busLongitude = j.get("longitude").getAsDouble();
                                    calculateDuration(stopList.get(finalI).getLatitude(), stopList.get(finalI).getLatitude(), finalI);

                                }
                            }
                        } else {
                            Toast.makeText(getContext(), responseMsg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            nearbyBusStopRV.setVisibility(View.VISIBLE);
            noItemFoundTV.setVisibility(View.GONE);
            noItemFoundIV.setVisibility(View.GONE);
        }

        else {
            nearbyBusStopRV.setVisibility(View.GONE);
            noItemFoundTV.setVisibility(View.VISIBLE);
            noItemFoundIV.setVisibility(View.VISIBLE);

        }

        if(adapter ==null){
            adapter = new CustomNearbyBusStopAdapter(stopList);
            mLayoutManager = new LinearLayoutManager(getContext());
            nearbyBusStopRV.setLayoutManager(mLayoutManager);
            nearbyBusStopRV.setItemAnimator(new DefaultItemAnimator());
            nearbyBusStopRV.setAdapter(adapter);

        }
        else{
            adapter.setData(stopList);
        }
    }

    public void getLocation(){
        int LOCATION_UPDATE_MIN_TIME = 1000;
        int LOCATION_UPDATE_MIN_DISTANCE = 10;

        //Get Current Location
        mLocationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }
        else{

            if(!isGPSEnabled){
                Toast.makeText(getContext(), "Please turn on the GPS on your device.",Toast.LENGTH_SHORT).show();
            }


            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, mLocationListener);
            Log.e("GG","LOCATIONLISTENER");

        }

    }

    private void calculateDuration(double stopLat, double stopLong,int i){
        GetDurationData getDurationData = new GetDurationData(getContext(), (duration, durationString) -> {
            if (shortestDuration == -1 || duration < shortestDuration) {
                shortestDuration = duration;
                durationStr = durationString;

                stopList.get(i).setDurationStr(durationStr);
                adapter.setData(stopList);
                Log.e("stoplist", String.valueOf(stopList));

            }
        });

        StringBuilder sb;
        LatLng busLocation,stopLocation;
        stopLocation = new LatLng(stopLat, stopLong);
        busLocation = new LatLng(busLatitude, busLongitude);

        Object[] dataTransfer = new Object[3];
        sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/distancematrix/json?");
        sb.append("origins=" + busLatitude + "," + busLongitude);
        sb.append("&destinations=" + stopLocation.latitude + "," + stopLocation.longitude);
        sb.append("&mode=transit");
        sb.append("&transit_mode=bus");
        sb.append("&key=" + "AIzaSyAxBOsTH4aJqTag3XShPtscTs2Y4-690Fw");

        dataTransfer[0] = sb.toString();
        dataTransfer[1] = busLocation;
        dataTransfer[2] = stopLocation;

        getDurationData.execute(dataTransfer);

    }

}