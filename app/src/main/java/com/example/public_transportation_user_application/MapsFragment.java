package com.example.public_transportation_user_application;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    MapView mapFragment;
    private GoogleMap mMap;
    LocationManager locationManager;
    LatLng currentLocation;
    LatLng destinationLocation;
    EditText destination;
    PlacesClient placesClient;
    public static final int LOCATION_REQUEST = 1;
    private LocationListener locationListener;
    static Location cLocation;
    boolean isLocationSet = false,lessWalking = false, fewerTransfer = false;
    Button planRoute;
    RadioButton lessWalkingRB, fewerTransfersRB;
    CardView routeDetailsCV;
    String fare, duration, routeDetails;
    GetDirectionData getDirectionData;
    RadioGroup planningReferences;

    public MapsFragment(){
        //Require empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_route_planning,container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mapFragment = view.findViewById(R.id.google_map);
        mapFragment.onCreate(savedInstanceState);
        mapFragment.getMapAsync(this);

        //Assign Variables
        destination = view.findViewById(R.id.etDestination);
        planRoute = view.findViewById(R.id.planRouteBtn);
        routeDetailsCV = view.findViewById(R.id.routeDetailsCV);
        planRoute.getBackground().setAlpha(45);

        planRoute.setOnClickListener(v -> {
            if (!destination.getText().toString().equals("")) {
                final CustomDialogClass customPlanRouteAlertDialog = new CustomDialogClass(getContext(), R.layout.plan_route_alert_dialog);
                customPlanRouteAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                customPlanRouteAlertDialog.show();

                Button confirm;
                ImageButton close;

                confirm = customPlanRouteAlertDialog.findViewById(R.id.confirmBtn);
                close = customPlanRouteAlertDialog.findViewById(R.id.closeBtn);
                planningReferences = customPlanRouteAlertDialog.findViewById(R.id.planningReferencesRG);

                lessWalkingRB = customPlanRouteAlertDialog.findViewById(R.id.lessWalkingRB);
                lessWalkingRB.setChecked(true);
                lessWalking = true;

                lessWalkingRB.setOnClickListener(v15 -> {
                    lessWalkingRB.setChecked(true);
                    lessWalking = true;
                    fewerTransfersRB.setChecked(false);
                    fewerTransfer = false;
                });

                fewerTransfersRB = customPlanRouteAlertDialog.findViewById(R.id.fewerTransferRB);

                fewerTransfersRB.setOnClickListener(v14 -> {
                    lessWalkingRB.setChecked(false);
                    lessWalking = false;
                    fewerTransfersRB.setChecked(true);
                    fewerTransfer = true;
                });

                close.setOnClickListener(v12 -> customPlanRouteAlertDialog.dismiss());

                confirm.setOnClickListener(v1 -> {
                    customPlanRouteAlertDialog.dismiss();
                    calculateDirections();
                    routeDetailsCV.setVisibility(View.VISIBLE);
                    Log.e("LATLNG", "Origin LatLng" + currentLocation + "\n Destination LatLng:" + destinationLocation);
                });

            }
        });

        routeDetailsCV.setOnClickListener(v -> {
            final CustomDialogClass customRouteDetailsDialog = new CustomDialogClass(getContext(), R.layout.route_details_alert_dialog);
            customRouteDetailsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            customRouteDetailsDialog.show();

            ImageButton close;
            TextView fareTV, durationTV, routeDetailsTV;

            close = customRouteDetailsDialog.findViewById(R.id.detailsCloseBtn);
            fareTV = customRouteDetailsDialog.findViewById(R.id.fareTV);
            durationTV = customRouteDetailsDialog.findViewById(R.id.durationTV);
            routeDetailsTV = customRouteDetailsDialog.findViewById(R.id.routeDetailsTV);

            fare = getDirectionData.getRouteFare();
            duration = getDirectionData.getRouteDuration();
            routeDetails = getDirectionData.getRouteDetails();

            fareTV.setText(fare);
            durationTV.setText(duration);
            routeDetailsTV.setText(routeDetails);
            Log.e("LATLNG", fare +"\n" +  duration + "\n" + routeDetails);
            close.setOnClickListener(v13 -> customRouteDetailsDialog.dismiss());
        });

        locationListener = location -> {
            Log.e("Location", "Location Changed");
            cLocation = location;
            currentLocation = new LatLng(cLocation.getLatitude(), cLocation.getLongitude());
            initMapWithMakers();
        };

        //Initialise Places
        if (!Places.isInitialized()) {
            Places.initialize(Objects.requireNonNull(getContext()), "AIzaSyAxBOsTH4aJqTag3XShPtscTs2Y4-690Fw");
        }


        // Create a new Places client instance.
        placesClient = Places.createClient(Objects.requireNonNull(getContext()));

        //Set Edit Text non focusable
        destination.setFocusable(false);

        destination.setOnClickListener(v -> {
            //Initialise place field list
            List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

            //Create Intent
            Intent i = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fieldList).setCountry("MY").build(getContext());

            //Start Activity for result
            startActivityForResult(i, 100);

        });

    }

    @Override
    public void onStart() {
        mapFragment.onStart();
        super.onStart();

    }

    @Override
    public void onStop() {
        mapFragment.onStop();
        super.onStop();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==100 && resultCode == Activity.RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(Objects.requireNonNull(data));

            destination.setText(place.getName());
            destinationLocation = place.getLatLng();
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(currentLocation));
            mMap.addMarker(new MarkerOptions().position(destinationLocation));

            Log.i("TESTING", "Place: " + place.getName() + ", " + place.getId() + ", " + place.getAddress());


        }
        else if(resultCode == AutocompleteActivity.RESULT_ERROR){
            //When Error
            //Initialise status
            Status status = Autocomplete.getStatusFromIntent(Objects.requireNonNull(data));
            //Display Msg
            Toast.makeText(getContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        int LOCATION_UPDATE_MIN_TIME = 1000;
        int LOCATION_UPDATE_MIN_DISTANCE = 10;


        //Get Current Location
        locationManager = (LocationManager) Objects.requireNonNull(getContext()).getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }
        else{
            mMap.setMyLocationEnabled(true);

            if(!isGPSEnabled){
                Toast.makeText(getContext(), "Please turn on the GPS on your device.",Toast.LENGTH_SHORT).show();
            }

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, locationListener);
        }

    }
    private void initMapWithMakers() {
        if(!isLocationSet){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(cLocation.getLatitude(), cLocation.getLongitude()), 12));
            mMap.addMarker(new MarkerOptions().position(currentLocation));
            isLocationSet = true;
        }
    }

    private void calculateDirections(){
        StringBuilder sb;

        Object[] dataTransfer = new Object[4];
        sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=" + currentLocation.latitude + "," + currentLocation.longitude);
        sb.append("&destination=" + destinationLocation.latitude + "," + destinationLocation.longitude);
        sb.append("&mode=transit");
        sb.append("&transit_mode=bus");
        if (lessWalking){
            sb.append("&transit_routing_preferences=less_walking");
        }
        else if (fewerTransfer){
            sb.append("&transit_routing_preferences=fewer_transfer");
        }

        sb.append("&key=" + "AIzaSyAxBOsTH4aJqTag3XShPtscTs2Y4-690Fw");

        getDirectionData = new GetDirectionData(getContext());
        dataTransfer[0] = mMap;
        dataTransfer[1] = sb.toString();
        dataTransfer[2] = currentLocation;
        dataTransfer[3] = destinationLocation;

        getDirectionData.execute(dataTransfer);

    }

}