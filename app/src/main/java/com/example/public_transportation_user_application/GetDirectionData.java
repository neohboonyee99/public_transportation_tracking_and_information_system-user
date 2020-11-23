package com.example.public_transportation_user_application;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetDirectionData extends AsyncTask<Object, String , String> {
    String fare,duration,routeDetails;
    GoogleMap mMap;
    String url;
    LatLng currentLatLng, destinationLatLng;
    HttpURLConnection httpURLConnection = null;
    String data="";
    InputStream inputStream = null;
    Context c;

    GetDirectionData(Context c){
        this.c = c;
    }



    @Override
    public String doInBackground(Object... params) {
        mMap = (GoogleMap) params[0];
        url = (String) params[1];
        currentLatLng = (LatLng) params[2];
        destinationLatLng = (LatLng) params[3];

        try{
            URL myUrl = new URL(url);
            httpURLConnection = (HttpURLConnection)myUrl.openConnection();
            httpURLConnection.connect();

            inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            String line="";
            while((line=bufferedReader.readLine())!=null){
                sb.append(line);
            }
            data = sb.toString();
            bufferedReader.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public void onPostExecute(String s) {
        StringBuilder sb;
        JSONObject transitDetails,arrival_stops, departure_stops;
        String travel_mode;
        sb = new StringBuilder();
        try{
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            fare = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONObject("fare").getString("text");
            duration = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

            int count = jsonArray.length();
            String[] polyline_array = new String[count];

            for (int i =0; i<count; i++){
                JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                String polygone = jsonObject2.getJSONObject("polyline").getString("points");
                polyline_array[i] = polygone;
                travel_mode = jsonObject2.getString("travel_mode");

                if(travel_mode.equals("WALKING")){
                    sb.append(jsonArray.getJSONObject(i).getString("html_instructions") + "\n\n");

                }
                else if(travel_mode.equals("TRANSIT")){
                    transitDetails = jsonArray.getJSONObject(i).getJSONObject("transit_details");
                    arrival_stops = transitDetails.getJSONObject("arrival_stop");
                    departure_stops = transitDetails.getJSONObject("departure_stop");
                    sb.append(jsonArray.getJSONObject(i).getString("html_instructions") + "\n\n");
                    sb.append("Total Stops: " + transitDetails.getString("num_stops")+ "\n\n");
                    sb.append("Departure Bus Stop:\n" + departure_stops.getString("name") + "\n\n");
                    sb.append("Arrival Bus Stop:\n" + arrival_stops.getString("name") + "\n\n");
                    sb.append("Get off from the bus.\n\n");
                }
            }
            routeDetails = sb.toString();
            int count2 = polyline_array.length;

            for(int i=0; i< count2; i++){
                PolylineOptions options2 = new PolylineOptions();
                options2.color(Color.BLUE);
                options2.width(10);
                options2.addAll(PolyUtil.decode(polyline_array[i]));
                mMap.addPolyline(options2);
            }
        } catch (JSONException e) {
            Log.e("TESTING", e.getMessage());
        }


    }
    public String getRouteFare() {
        return fare;
    }

    public String getRouteDuration(){
        return duration;
    }

    public String getRouteDetails(){
        return routeDetails;
    }

}
