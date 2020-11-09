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

public class GetDurationData extends AsyncTask<Object, String, String> {
    private OnTaskCompleted listener;
    int duration=-1;
    String durationStr="";
    String url;
    LatLng busLatLng, stopLatLng;
    HttpURLConnection httpURLConnection = null;
    String data="";
    InputStream inputStream = null;
    Context c;

    GetDurationData(Context c){
        this.c = c;
    }


    GetDurationData(Context c,OnTaskCompleted listener){
        this.c = c;
        this.listener = listener;
    }


    @Override
    public String doInBackground(Object... params) {
        url = (String) params[0];
        busLatLng = (LatLng) params[1];
        stopLatLng = (LatLng) params[2];

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
        try{
            JSONObject jsonObject = new JSONObject(s);
            duration = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getInt("value");
            durationStr = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");
            Log.e("durationTxt", durationStr);
            if (listener!=null) {
                listener.onTaskCompleted(duration, durationStr);
            }

        } catch (JSONException e) {
            Log.e("TESTING", e.getMessage());
        }


    }

    public int getDuration(){
        return duration;
    }

    public String getDurationStr(){
        return durationStr;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setDurationStr(String durationStr) {
        this.durationStr = durationStr;
    }

    public void setListener(OnTaskCompleted listener) {
        this.listener = listener;
    }
}
