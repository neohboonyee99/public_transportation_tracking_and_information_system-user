package com.example.public_transportation_user_application;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteNumberETAFragment extends Fragment {

    RecyclerView routeNumberRV;
    ImageView noRouteFoundIV;
    TextView noRouteFoundTV;
    int shortestDuration;
    String durationStr;
    CustomRouteNumberAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<String> busStop;
    List<Stops> stopList;
    List<RouteNumber> routeNumberList;
    String responseMsg,responseMsg1;
    RecyclerView.LayoutManager mLayoutManager;
    double busLatitude, busLongitude;

    public RouteNumberETAFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_number_eta, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        routeNumberRV = view.findViewById(R.id.routeNumberRV);
        noRouteFoundIV = view.findViewById(R.id.noRouteFoundIV);
        noRouteFoundTV = view.findViewById(R.id.noRouteFoundTV);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayoutRouteNumber);
        routeNumberList=  new ArrayList<>();
        busStop = new ArrayList<>();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            routeNumberList=  new ArrayList<>();
            busStop = new ArrayList<>();
            getData();

        });

        getData();

    }

    private void getData(){

        ServerService serverService = RetrofitClient.getClient(ServerService.BASE_URL).create(ServerService.class);

        Call<JsonObject> getRoute = serverService.getRoute();
        getRoute.enqueue(new Callback<JsonObject>(){

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if(response.isSuccessful()){

                    JsonObject jsonObject = response.body();
                    responseMsg = jsonObject.get("msg").getAsString();
                    JsonArray stops = jsonObject.get("bus_route").getAsJsonArray();
                    int position = 0;
                    for(JsonElement JO : stops){

                        JsonObject route = JO.getAsJsonObject();
                        RouteNumber routeObj = new RouteNumber();
                        routeObj.setRouteNumber(route.get("route_number").getAsString());
                        busStop.add(route.get("1st_stop_go").toString());
                        busStop.add(route.get("2nd_stop_go").toString());
                        busStop.add(route.get("3rd_stop_go").toString());
                        busStop.add(route.get("4th_stop_go").toString());
                        busStop.add(route.get("5th_stop_go").toString());
                        busStop.add(route.get("6th_stop_go").toString());
                        busStop.add(route.get("7th_stop_go").toString());
                        busStop.add(route.get("8th_stop_go").toString());
                        busStop.add(route.get("9th_stop_go").toString());
                        busStop.add(route.get("10th_stop_go").toString());
                        busStop.add(route.get("11th_stop_go").toString());
                        busStop.add(route.get("12th_stop_go").toString());
                        busStop.add(route.get("13th_stop_go").toString());
                        busStop.add(route.get("14th_stop_go").toString());
                        busStop.add(route.get("15th_stop_go").toString());
                        busStop.add(route.get("16th_stop_go").toString());
                        busStop.add(route.get("17th_stop_go").toString());
                        busStop.add(route.get("18th_stop_go").toString());
                        busStop.add(route.get("19th_stop_go").toString());
                        busStop.add(route.get("20th_stop_go").toString());
                        busStop.add(route.get("1st_stop_back").toString());
                        busStop.add(route.get("2nd_stop_back").toString());
                        busStop.add(route.get("3rd_stop_back").toString());
                        busStop.add(route.get("4th_stop_back").toString());
                        busStop.add(route.get("5th_stop_back").toString());
                        busStop.add(route.get("6th_stop_back").toString());
                        busStop.add(route.get("7th_stop_back").toString());
                        busStop.add(route.get("8th_stop_back").toString());
                        busStop.add(route.get("9th_stop_back").toString());
                        busStop.add(route.get("10th_stop_back").toString());
                        busStop.add(route.get("11th_stop_back").toString());
                        busStop.add(route.get("12th_stop_back").toString());
                        busStop.add(route.get("13th_stop_back").toString());
                        busStop.add(route.get("14th_stop_back").toString());
                        busStop.add(route.get("15th_stop_back").toString());
                        busStop.add(route.get("16th_stop_back").toString());
                        busStop.add(route.get("17th_stop_back").toString());
                        busStop.add(route.get("18th_stop_back").toString());
                        busStop.add(route.get("19th_stop_back").toString());
                        busStop.add(route.get("20th_stop_back").toString());
                        List<Stops> temp = new ArrayList<>();
                        for (int j=0;j<busStop.size(); j++) {
                            if (!busStop.get(j).equals("null")) {
                                ServerService serverService = RetrofitClient.getClient(ServerService.BASE_URL).create(ServerService.class);
                                String stopName = busStop.get(j).substring(1,busStop.get(j).length()-1);
                                Log.e("Bus Stop Name", busStop.get(j));
                                Call<JsonObject> getBusStopLatLng = serverService.getBusStopLatLng(stopName);
                                int finalJ = j;
                                int finalPosition = position;
                                getBusStopLatLng.enqueue(new Callback<JsonObject>() {
                                    @Override
                                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                        if (response.isSuccessful()) {
                                            double stopLatitude, stopLongitude;
                                            JsonObject jsonObject = response.body();
                                            responseMsg1 = jsonObject.get("msg").getAsString();
                                            JsonObject stopDetails = jsonObject.get("stop_details").getAsJsonObject();
                                            stopLatitude = stopDetails.get("latitude").getAsDouble();
                                            stopLongitude = stopDetails.get("longitude").getAsDouble();
                                            Stops stop = new Stops();
                                            stop.setLatitude(stopLatitude);
                                            stop.setLongitude(stopLongitude);
                                            stop.setStopsName(busStop.get(finalJ).substring(1,busStop.get(finalJ).length()-1));
                                            temp.add(stop);
                                            routeNumberList.get(finalPosition).setStopsList(temp);
                                            loadData();

                                        }
                                    }


                                    @Override
                                    public void onFailure(Call<JsonObject> call, Throwable t) {
                                        Log.e("Bus Stop", t.getMessage());
                                        Toast.makeText(getContext(), t.getMessage() + "busstop", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            routeObj.setStopsList(temp);
                        }
                        routeNumberList.add(routeObj);
                        swipeRefreshLayout.setRefreshing(false);
                        position ++;
                    }
                }
                else{
                    loadData();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(),t.getMessage()+"busroute",Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadData(){
        ServerService serverService = RetrofitClient.getClient(ServerService.BASE_URL).create(ServerService.class);
        if (routeNumberList.size()!=0) {
            for (int i = 0; i < routeNumberList.size(); i++) {
                if (routeNumberList.get(i).getStopsList().size() != 0) {
                    for (int j = 0; j < routeNumberList.get(i).getStopsList().size(); j++) {

                        Call<JsonObject> getBus = serverService.getBusByRouteNum(routeNumberList.get(i).getRouteNumber(), routeNumberList.get(i).getStopsList().get(j).getStopsName());
                        int finalJ = j;
                        int finalI = i;
                        getBus.enqueue(new Callback<JsonObject>() {

                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {

                                    shortestDuration = -1;
                                    durationStr = "";

                                    JsonObject jsonObject = response.body();
                                    responseMsg = jsonObject.get("msg").getAsString();
                                    boolean error = jsonObject.get("error").getAsBoolean();
                                    if (!error) {
                                        JsonArray bus = jsonObject.get("bus").getAsJsonArray();

                                        for (JsonElement JO : bus) {
                                            JsonObject j = JO.getAsJsonObject();
                                            busLatitude = j.get("latitude").getAsDouble();
                                            busLongitude = j.get("longitude").getAsDouble();
                                            calculateDuration(routeNumberList.get(finalI).getStopsList().get(finalJ).getLatitude(), routeNumberList.get(finalI).getStopsList().get(finalJ).getLongitude(), finalI, finalJ);
                                        }
                                    }
                                } else {
                                    Toast.makeText(getContext(), responseMsg1, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Toast.makeText(getContext(), t.getMessage() + "busstopdetails", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
            routeNumberRV.setVisibility(View.VISIBLE);
            noRouteFoundTV.setVisibility(View.GONE);
            noRouteFoundIV.setVisibility(View.GONE);
        }


        else {
            routeNumberRV.setVisibility(View.GONE);
            noRouteFoundTV.setVisibility(View.VISIBLE);
            noRouteFoundIV.setVisibility(View.VISIBLE);
        }

        if(adapter ==null){
            Log.e("size", String.valueOf(routeNumberList.get(0).getStopsList().size()));
            adapter = new CustomRouteNumberAdapter(getContext(),routeNumberList);
            mLayoutManager = new LinearLayoutManager(getContext());
            routeNumberRV.setLayoutManager(mLayoutManager);
            routeNumberRV.setItemAnimator(new DefaultItemAnimator());
            routeNumberRV.setAdapter(adapter);

        }
        else{
            adapter.setData(routeNumberList);
        }
    }

    private void calculateDuration(double stopLat, double stopLong,int i ,int j){
        GetDurationData getDurationData = new GetDurationData(getContext(), (duration, durationString) -> {
            if (shortestDuration == -1 || duration < shortestDuration) {
                shortestDuration = duration;
                durationStr = durationString;

                Log.e("duration", durationStr);
                routeNumberList.get(i).getStopsList().get(j).setDurationStr(durationStr);
                Gson gson = new Gson();
                String json = gson.toJson(routeNumberList);
                Log.e("ROUTENUMITEM", json);
                Toast.makeText(getContext(),json,Toast.LENGTH_SHORT).show();

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
        sb.append("&key=" + "AIzaSyAxBOsTH4aJqTag3XShPtscTs2Y4-690Fw");

        dataTransfer[0] = sb.toString();
        dataTransfer[1] = busLocation;
        dataTransfer[2] = stopLocation;

        getDurationData.execute(dataTransfer);
    }


}