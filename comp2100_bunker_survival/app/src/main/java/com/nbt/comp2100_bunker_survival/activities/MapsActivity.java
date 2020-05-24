package com.nbt.comp2100_bunker_survival.activities;

import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nbt.comp2100_bunker_survival.R;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker playerMarker;
    private Circle circle;
    private float x = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Disabling the ability to pan the map around
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        //TODO - You can add a tag to the circle, which means that you can check when you pick something up if it is in the circle
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(0, 0))
                .radius(25)
                .strokeColor(Color.argb(100, 0, 0, 255))
                .fillColor(Color.argb(40, 0, 0, 255)));

        //Creating the marker which shows the players current location
        MarkerOptions playerMark = new MarkerOptions().position(new LatLng(0, 0));
        playerMarker = mMap.addMarker(playerMark);

        //TODO - set the player Icon
        //playerMarker.setIcon();


        //Zooming the camera to a more appropriate level
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
        locationHandler();
    }

    //Initializes a handler which runs every second, and calls updateLocation();
    public void locationHandler(){
        final Handler handler = new Handler();
        final Runnable locationUpdater = new Runnable() {
            public void run()
            {
                updateLocation();
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(locationUpdater, 1000);
    }

    public void updateLocation(){
        //Checking if the app has permission to use location information
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}, 10);
        }

        Location currentLocation = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(new Criteria(), false));
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude() + x,currentLocation.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng), 500, null);

        //TODO - move this in small increments so that it smoothly transitions over 500ms, the same as the camera above
        //Will require pulling apart currentLatLng, and playerMarket.getPosition(), and calculating how far it should move over a number of steps
        playerMarker.setPosition(currentLatLng);
        circle.setCenter(currentLatLng);
    }

    public void inventoryButtonPressed(View view) {
        Intent intent = new Intent(getApplicationContext(), InventoryActivity.class);
        startActivity(intent);
    }

    public void collectionButtonPressed(View view) {

    }

    public void centerButtonPressed(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }
}