package com.example.public_transportation_user_application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomNearbyBusStopAdapter extends RecyclerView.Adapter<CustomNearbyBusStopAdapter.CustomViewHolder> {
    List<Stops> stopsList;

    CustomNearbyBusStopAdapter(List<Stops> stops){
        stopsList= stops;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.nearby_bus_stop_item,viewGroup,false);
        return new CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomNearbyBusStopAdapter.CustomViewHolder holder, int position) {
        String name, duration;
        Stops tempStops = stopsList.get(position);
        name = tempStops.getStopsName();
        duration = tempStops.getDurationStr();

        holder.stopsNameTV.setText(name);
        holder.etaTV.setText(duration);
    }


    @Override
    public int getItemCount() {
        return stopsList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{

        TextView stopsNameTV, etaTV;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            stopsNameTV = itemView.findViewById(R.id.stopsNameTV);
            etaTV = itemView.findViewById(R.id.etaTV);

        }
    }

    public void setData(List<Stops> stopsList){
        this.stopsList = stopsList;
        notifyDataSetChanged();
    }
}
