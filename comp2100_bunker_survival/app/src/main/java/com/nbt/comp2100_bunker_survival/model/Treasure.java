package com.nbt.comp2100_bunker_survival.model;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.nbt.comp2100_bunker_survival.model.items.Item;
import com.nbt.comp2100_bunker_survival.model.items.Weapon;

import java.util.Random;

// an instance of Treasure to be spawned on the map
public class Treasure {
    // basic properties
    private String name;
    private double latitude;
    private double longitude;
    private Inventory treasureInventory;
    private long seed;

    // constants
    private static final int FOOD_MAX = 50;
    private static final int FOOD_MIN = 0;
    private static final int SCRAPMETAL_MAX = 50;
    private static final int SCRAPMETAL_MIN = 0;
    private static final int TOILETPAPER_MAX = 50;
    private static final int TOILETPAPER_MIN = 0;

    // constructor for empty inventory
    public Treasure(String name, double latitude, double longitude, long seed) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.seed = seed;
        this.treasureInventory = new Inventory();
    }
    // also accepts Location input
    public Treasure(String name, Location location, long seed) {
        this(name, location.getLatitude(), location.getLongitude(), seed);
    }
    // also accepts LatLng input
    public Treasure(String name, LatLng location, long seed) {
        this(name, location.latitude, location.longitude, seed);
    }

    // constructor for input treasure inventory
    public Treasure(String name, double latitude, double longitude, long seed, Inventory treasureInventory) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.seed = seed;
        this.treasureInventory = treasureInventory;
    }
    // also accepts Location input
    public Treasure(String name, Location location, long seed, Inventory treasureInventory) {
        this(name, location.getLatitude(), location.getLongitude(), seed, treasureInventory);
    }

    public Inventory getTreasureInventory() {
        return treasureInventory;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    // factory method that returns a new inventory instance with default player inventory values.
    // generates a random seed
    @NonNull
    public static Treasure generateTreasure(double latitude, double longitude) {
        Random rand = new Random();
        long seed = rand.nextLong();
        return generateTreasure(latitude, longitude, seed);
    }
    // also accepts Location input
    @NonNull
    public static Treasure generateTreasure(Location location) {
        return generateTreasure(location.getLatitude(), location.getLongitude());
    }
    // also accepts LatLng input
    @NonNull
    public static Treasure generateTreasure(LatLng location) {
        return generateTreasure(location.latitude, location.longitude);
    }

    // factory method that returns a new inventory instance with default player inventory values.
    // uses inputted seed
    // TODO treasure types
    // TODO resource distribution
    // TODO unique item generation
    @NonNull
    public static Treasure generateTreasure(double latitude, double longitude, long seed) {
        Random rand = new Random(seed);
        Item item = new Weapon("TestWeapon","TestDesc",0,0);
        Inventory inventory = new Inventory(
                rand.nextInt(FOOD_MAX)+FOOD_MIN,
                rand.nextInt(SCRAPMETAL_MAX)+SCRAPMETAL_MIN,
                rand.nextInt(TOILETPAPER_MAX)+TOILETPAPER_MIN,
                item);
        return new Treasure("Test Treasure", latitude, longitude, seed, inventory);
    }
    // also accepts Location input
    @NonNull
    public static Treasure generateTreasure(Location location, long seed) {
        return generateTreasure(location.getLatitude(), location.getLongitude(), seed);
    }
    // also accepts LatLng input
    @NonNull
    public static Treasure generateTreasure(LatLng location, long seed) {
        return generateTreasure(location.latitude, location.longitude, seed);
    }

    // returns a String of a location input of latitude and longitude
    public static String locationToSimpleString(double latitude, double longitude) {
        return ("Latitude: "+latitude+", Longitude: "+longitude);
    }
    // also accepts Location input
    public static String locationToSimpleString(Location location) {
        return locationToSimpleString(location.getLatitude(), location.getLongitude());
    }
    // also accepts LatLng input
    public static String locationToSimpleString(LatLng location) {
        return locationToSimpleString(location.latitude, location.longitude);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Treasure) {
            return (name.equals(((Treasure) obj).name) &&
                    latitude == ((Treasure) obj).latitude &&
                    longitude == ((Treasure) obj).longitude &&
                    treasureInventory.equals(((Treasure) obj).treasureInventory) &&
                    seed == ((Treasure) obj).seed);
        }
        else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        return ("Name: "+name+
                "\nSeed: "+seed+
                "\nLocation: ["+locationToSimpleString(latitude, longitude)+
                "]\n\n===\nTreasure Inventory:\n===\n\n"+treasureInventory.toString());
    }
}
