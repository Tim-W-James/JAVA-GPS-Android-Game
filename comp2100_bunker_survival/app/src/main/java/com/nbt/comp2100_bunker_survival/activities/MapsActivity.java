package com.nbt.comp2100_bunker_survival.activities;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.nbt.comp2100_bunker_survival.R;
import com.nbt.comp2100_bunker_survival.model.Inventory;
import com.nbt.comp2100_bunker_survival.model.Player;
import com.nbt.comp2100_bunker_survival.model.Treasure;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private Marker playerMarker;
    private Circle circle;
    private float collectionRadius = 25;
    private LatLng currentLatLang;
    private Player player;

    private int treasureInterval = 5000; // delay for generating treasure
    private Handler treasureHandler;
    private int treasureCount = 0;
    private int treasureMaxCount = 6;
    private int treasureMinDist = 30; // min distance for generating treasure
    private int treasureMaxDist = 150; // min distance for generating treasure
    private Map<Marker, Treasure> treasureInstances;

    /**
     * Player Data is received from the logon activity. If no data is found, default to
     * the test player
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Get PlayerName from login screen
        Intent intent = getIntent();
        String playerName = intent.getStringExtra("PlayerData");

        // PLAYER KEY TEMPORARY CODE
        String playerID = readPlayerKey();
        if (playerID.equals("Default")) {
            System.out.println("NO PLAYER KEY CURRENTLY DETECTED, WRITING NEW KEY");
            writePlayerKey("Bob1234");
        } else {
            System.out.println("PLAYER KEY IS: " + playerID);
        }

        player = Player.getTestPlayer(); // TODO fetch player data from server

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
                .radius(collectionRadius)
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

    /**
     * Writes the player ID to preferences
     * @param PlayerKey
     */
    public void writePlayerKey(String PlayerKey) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ID", "testPlayer");
        editor.commit();
    }

    /**
     * Reads the player ID from preferences
     * @return
     */
    public String readPlayerKey() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String PlayerKey = sharedPref.getString("ID","Default");

        return PlayerKey;
    }

    /**
     * Initializes a handler which runs every second, and calls updateLocation();
     */
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

    /**
     * Update the players location on the map
     */
    public void updateLocation(){
        //Checking if the app has permission to use location information
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return;
        } else if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.INTERNET}, 10);
            return;
        }

        // TODO check for null location on initial load
        locationManager.requestLocationUpdates(
                locationManager.getBestProvider(new Criteria(), false), 0, 0, new LocationListener() {
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    @Override
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                    @Override
                    public void onLocationChanged(final Location location) {
                    }
                });
        Location currentLocation = locationManager.getLastKnownLocation(
                locationManager.getBestProvider(new Criteria(), false));
        if (currentLocation == null){
            Toast.makeText(getApplicationContext(),"No Location data", Toast.LENGTH_SHORT).show();
            return;
        }
        this.currentLatLang = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());

        mMap.animateCamera(CameraUpdateFactory.newLatLng(currentLatLang), 500, null);

        //TODO - move this in small increments so that it smoothly transitions over 500ms, the same as the camera above
        //Will require pulling apart currentLatLng, and playerMarket.getPosition(), and calculating how far it should move over a number of steps
        playerMarker.setPosition(currentLatLang);
        circle.setCenter(currentLatLang);
    }

    /**
     * Opens the players inventory
     * @param view
     */
    public void inventoryButtonPressed(View view) {
        Intent intent = new Intent(getApplicationContext(), InventoryActivity.class);
        intent.putExtra("header", "Your Inventory");
        intent.putExtra("inventory", player.getCurrentInventory());
        startActivity(intent);
    }

    /**
     * Collects treasure within a radius around the player
     * @param view
     */
    public void collectionButtonPressed(View view) {
        Set<Marker> keys = treasureInstances.keySet(); // get the set of keys
        List<Treasure> collectedTreasure = new LinkedList<Treasure>();

        // find which treasure is close enough for collection
        for (Marker m : keys) {
            Treasure t = treasureInstances.get(m);
            if (SphericalUtil.computeDistanceBetween(
                    new LatLng(t.getLatitude(), t.getLongitude()), currentLatLang)
                    <= (collectionRadius)) {
                m.remove();
                collectedTreasure.add(t);
            }
        }

        if (collectedTreasure.size() == 0) {
            // throw toast if no treasure is nearby
            Toast.makeText(getApplicationContext(),"No Treasure Nearby", Toast.LENGTH_SHORT).show();
        }
        else {
            Inventory totalInventory = new Inventory();
            for (Treasure t : collectedTreasure) {
                // add inventories
                totalInventory.addInventory(t.getTreasureInventory());
                player.findTreasure(t);

                // generate a new treasure to find
                addNewTreasure();
            }
            // TODO send updated inventory to server

            // pass found inventories to inventory activity
            Intent intent = new Intent(getApplicationContext(), InventoryActivity.class);
            intent.putExtra("header", "You Found Treasure!");
            intent.putExtra("inventory", totalInventory);
            startActivity(intent);
        }
    }

    // zooms & centers the view
    public void centerButtonPressed(View view) {
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18));
    }

    // adds a new treasure to the map
    public void addNewTreasure() {
        Random rand = new Random();
        int vertOffset = rand.nextInt((treasureMaxDist - treasureMinDist) + 1) + treasureMinDist;
        if (rand.nextBoolean()) vertOffset = -vertOffset;
        int horizOffset = rand.nextInt((treasureMaxDist - treasureMinDist) + 1) + treasureMinDist;
        if (rand.nextBoolean()) horizOffset = -horizOffset;
        LatLng loc = SphericalUtil.computeOffset(currentLatLang, vertOffset, horizOffset);

        // pair marker with treasure
        Treasure t = Treasure.generateTreasure(loc);

        System.out.println("Generated treasure: " + t);

        Marker m = mMap.addMarker(new MarkerOptions()
                .position(loc)
                .icon(generateBitmapDescriptorFromRes(this,t.getIcon()))
                .title(t.getName()));
        //TODO - set the treasure Icon
        //m.setIcon();
        treasureInstances.put(m, t);
    }

    // Turns icons into the correct format for the GMaps API
    // ADAPTED FROM: https://android.jlelse.eu/the-danger-of-using-vector-drawables-5485b2a035fe
    // Cause I decided to make the icons SVGs
    public static BitmapDescriptor generateBitmapDescriptorFromRes(
            Context context, int resId) {

        int height = 150;
        int width = 150;

        Drawable drawable = ContextCompat.getDrawable(context, resId);
        drawable.setBounds(
                0,
                0,
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

//        Bitmap b = BitmapFactory.decodeResource(context, resId);

        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);

        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);

//        return BitmapDescriptorFactory.fromBitmap(bitmap);

    }

    // distribute treasure
    public void checkForGeneration() {
        // TODO improve distribution
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