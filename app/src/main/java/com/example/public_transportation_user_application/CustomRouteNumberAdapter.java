package com.example.public_transportation_user_application;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



public class CustomRouteNumberAdapter extends RecyclerView.Adapter<CustomRouteNumberAdapter.CustomViewHolder> {
    List<RouteNumber> routeNumberList;

    Context context;
   

    CustomRouteNumberAdapter(Context context, List<RouteNumber> item){
        this.context = context;
        routeNumberList= item;
    }

    @NonNull
    @Override
    public CustomRouteNumberAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.route_number_item,viewGroup,false);
        return new CustomRouteNumberAdapter.CustomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRouteNumberAdapter.CustomViewHolder holder, int position) {
        int totalStops; String routeNumber, totalStopsStr="";


        RouteNumber tempRoute = routeNumberList.get(position);
        routeNumber = tempRoute.getRouteNumber();
        totalStops = tempRoute.getStopsList().size();
        totalStopsStr = totalStops + " Stops";
        holder.routeNumberTV.setText(routeNumber);
        holder.totalStopsTV.setText(totalStopsStr);
    }


    @Override
    public int getItemCount() {
        return routeNumberList.size();
    }

    class CustomViewHolder extends RecyclerView.ViewHolder{
        TextView totalStopsTV, routeNumberTV;
        CardView routeNumberCV;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            totalStopsTV = itemView.findViewById(R.id.totalStopsTV);
            routeNumberTV = itemView.findViewById(R.id.routeNumberTV);
            routeNumberCV = itemView.findViewById(R.id.routeNumberCV);


            routeNumberCV.setOnClickListener(v -> {
                final CustomDialogClass customRouteNumberDetailsDialog = new CustomDialogClass(context, R.layout.eta_route_number_alert);
                customRouteNumberDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customRouteNumberDetailsDialog.show();
                String routeNumber;
                routeNumber = routeNumberList.get(getAdapterPosition()).getRouteNumber();
                RecyclerView.LayoutManager mLayoutManager;
                CustomNearbyBusStopAdapter adapter = null;
                ImageButton closeBtn;
                ImageView noDataFoundIV;
                RecyclerView stopsRV;
                TextView titleRouteNum,noDataFoundTV;
                RouteNumber r = routeNumberList.get(getAdapterPosition());
                titleRouteNum = customRouteNumberDetailsDialog.findViewById(R.id.routeNumberAlertTV);
                noDataFoundIV = customRouteNumberDetailsDialog.findViewById(R.id.alertNoItemIV);
                noDataFoundTV = customRouteNumberDetailsDialog.findViewById(R.id.alertNoItemFoundTV);
                stopsRV = customRouteNumberDetailsDialog.findViewById(R.id.eachBusStopRV);
                closeBtn = customRouteNumberDetailsDialog.findViewById(R.id.closeETARouteBtn);
                titleRouteNum.setText(routeNumber);

                if (r.getStopsList().size()==0){
                    stopsRV.setVisibility(View.GONE);
                    noDataFoundIV.setVisibility(View.VISIBLE);
                    noDataFoundTV.setVisibility(View.VISIBLE);
                }
                else{
                    stopsRV.setVisibility(View.VISIBLE);
                    noDataFoundIV.setVisibility(View.GONE);
                    noDataFoundTV.setVisibility(View.GONE);
                }

                if(adapter ==null){
                    adapter = new CustomNearbyBusStopAdapter(r.getStopsList());
                    mLayoutManager = new LinearLayoutManager(context);
                    stopsRV.setLayoutManager(mLayoutManager);
                    stopsRV.setItemAnimator(new DefaultItemAnimator());
                    stopsRV.setAdapter(adapter);

                }
                else{
                    adapter.setData(r.getStopsList());
                }

                closeBtn.setOnClickListener(v1 -> customRouteNumberDetailsDialog.dismiss());
            });
        }
    }
    public void setData(List<RouteNumber> itemList){
        this.routeNumberList = itemList;
        notifyDataSetChanged();
    }
}


