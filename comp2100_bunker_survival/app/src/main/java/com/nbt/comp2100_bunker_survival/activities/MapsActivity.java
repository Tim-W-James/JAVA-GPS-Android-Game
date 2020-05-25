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
import com.google.maps.android.SphericalUtil;
import com.nbt.comp2100_bunker_survival.R;
import com.nbt.comp2100_bunker_survival.model.Treasure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker playerMarker;
    private Circle circle;
    private float x = 0;
    private LatLng currentLatLang;

    private int treasureInterval = 5000; // delay for generating treasure
    private Handler treasureHandler;
    private int treasureCount = 0;
    private int treasureMaxCount = 6;
    private int treasureMinDist = 30; // min distance for generating treasure
    private int treasureMaxDist = 150; // min distance for generating treasure
    private Map<Marker, Treasure> treasureInstances;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        treasureHandler = new Handler();
        treasureInstances = new HashMap<Marker, Treasure>();
        startGeneratingTreasure();
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
        LatLng currentLatLng;
        this.currentLatLang = currentLatLng = new LatLng(currentLocation.getLatitude() + x,currentLocation.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLng), 500, null);

        //TODO - move this in small increments so that it smoothly transitions over 500ms, the same as the camera above
        //Will require pulling apart currentLatLng, and playerMarket.getPosition(), and calculating how far it should move over a number of steps
        playerMarker.setPosition(currentLatLng);
        circle.setCenter(currentLatLng);

        System.out.println(currentLatLng.latitude+ " " + currentLatLng.longitude); // TODO remove debug
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

    public void addNewTreasure() {
        Random rand = new Random();
        int vertOffset = rand.nextInt((treasureMaxDist - treasureMinDist) + 1) + treasureMinDist;
        if (rand.nextBoolean()) vertOffset = -vertOffset;
        int horizOffset = rand.nextInt((treasureMaxDist - treasureMinDist) + 1) + treasureMinDist;
        if (rand.nextBoolean()) horizOffset = -horizOffset;
        LatLng loc = SphericalUtil.computeOffset(currentLatLang, vertOffset, horizOffset);

        Treasure t = Treasure.generateTreasure(loc);
        Marker m;
        m = mMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("Hello world"));
        treasureInstances.put(m, t);
    }

    public void checkForGeneration() {
        // TODO improve distribution
        // TODO remove treasure that is too far away
        if (currentLatLang != null) {
            if (treasureCount < treasureMaxCount) { // generate new treasure
                addNewTreasure();
                treasureCount++;
            }
            else { // check if there is a treasure which is too far from the player and should be removed
                Set<Marker> keys = treasureInstances.keySet(); // get the set of keys
                for (Marker m : keys) {
                    Treasure t = treasureInstances.get(m);

                    if (SphericalUtil.computeDistanceBetween(
                            new LatLng(t.getLatitude(), t.getLongitude()), currentLatLang)
                            > (treasureMaxDist+(treasureMaxDist*0.5))) {
                        treasureInstances.remove(m); // remove treasure from list
                        m.remove(); // remove marker from map

                        addNewTreasure(); // create replacement treasure
                        break;
                    }
                }
            }
        }
    }

    Runnable treasureStatus = new Runnable() {
        @Override
        public void run() {
            try {
                checkForGeneration();
            } finally {
                treasureHandler.postDelayed(treasureStatus, treasureInterval);
            }
        }
    };

    void startGeneratingTreasure() {
        treasureStatus.run();
    }

    void stopGeneratingTreasure() {
        treasureHandler.removeCallbacks(treasureStatus);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGeneratingTreasure();
    }
}