package com.nbt.comp2100_bunker_survival.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.nbt.comp2100_bunker_survival.R;
import com.nbt.comp2100_bunker_survival.model.items.Curiosity;
import com.nbt.comp2100_bunker_survival.model.items.Item;
import com.nbt.comp2100_bunker_survival.model.items.Weapon;

import java.text.DecimalFormat;
import java.util.Random;

// an instance of Treasure to be spawned on the map
public class Treasure {
    // basic properties
    private String name;
    private TreasureType type;
    private double latitude;
    private double longitude;
    private Inventory treasureInventory;
    private long seed;

    // constants
    private static final int FOOD_MAX = 50;
    private static final int FOOD_MIN = 10;
    private static final int SCRAPMETAL_MAX = 50;
    private static final int SCRAPMETAL_MIN = 10;
    private static final int TOILETPAPER_MAX = 50;
    private static final int TOILETPAPER_MIN = 10;

    // constructor for empty inventory
    public Treasure(String name, double latitude, double longitude, long seed) {
        this.name = name;
        this.type = TreasureType.OTHER;
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
        this (name, latitude, longitude, seed);
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

    public String getName() {
        return name;
    }

    public long getSeed() {
        return seed;
    }

    // Returns resId for an icon
    public int getIcon() {
        switch (type) {
            case FOOD:
                return R.drawable.ic_food_foreground;
            case SCRAP_METAL:
                return R.drawable.ic_scrapmetal;
            case TOILET_PAPER:
                return R.drawable.ic_toiletpaper;
            default:
                return R.drawable.ic_treasurechest_foreground;
        }
    }

    public void setType(TreasureType type) {
        this.type = type;
    }

    // factory method that returns a treasure object with a randomly generated inventory
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

    // factory method that returns a treasure object with an generated inventory
    // uses inputted seed
    // TODO improve resource distribution
    @NonNull
    public static Treasure generateTreasure(double latitude, double longitude, long seed) {
        Random rand = new Random(seed);
        Treasure treasure;

        int type = rand.nextInt(5);
        switch (type) {
            // item cache
            case 0:
                Inventory inv0 = new Inventory(
                        0,
                        0,
                        0);
                inv0.addUniqueItem(generateItem(latitude, longitude, seed));
                return new Treasure("Item Cache", latitude, longitude, seed, inv0);

            // food cache
            case 1:
                Inventory inv1 = new Inventory(
                        rand.nextInt(FOOD_MAX)+FOOD_MIN,
                        0,
                        0);
                inv1.addUniqueItem(generateItem(latitude, longitude, seed));
                Treasure tFood = new Treasure("Food Cache", latitude, longitude, seed, inv1);
                tFood.setType(TreasureType.FOOD);
                return tFood;

            // scrap metal cache
            case 2:
                Inventory inv2 = new Inventory(
                        0,
                        rand.nextInt(SCRAPMETAL_MAX)+SCRAPMETAL_MIN,
                        0);
                inv2.addUniqueItem(generateItem(latitude, longitude, seed));
                Treasure tScrapMetal = new Treasure("Scrap Metal Cache", latitude, longitude, seed, inv2);
                tScrapMetal.setType(TreasureType.SCRAP_METAL);
                return tScrapMetal;

            // toilet paper cache
            case 3:
                Inventory inv3 = new Inventory(
                        0,
                        0,
                        rand.nextInt(TOILETPAPER_MAX)+TOILETPAPER_MIN);
                inv3.addUniqueItem(generateItem(latitude, longitude, seed));
                Treasure tToiletPaper = new Treasure("Toilet Paper Cache", latitude, longitude, seed, inv3);
                tToiletPaper.setType(TreasureType.TOILET_PAPER);
                return tToiletPaper;

            // large resource cache
            default:
                Inventory inv4 = new Inventory(
                        rand.nextInt(FOOD_MAX)+FOOD_MIN,
                        rand.nextInt(SCRAPMETAL_MAX)+SCRAPMETAL_MIN,
                        rand.nextInt(TOILETPAPER_MAX)+TOILETPAPER_MIN);
                return new Treasure("Large Resource Cache", latitude, longitude, seed, inv4);
        }
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

    // picks a random item based on the seed
    public static Item generateItem(double latitude, double longitude, long seed) {
        Random rand = new Random(seed);
        int i = rand.nextInt(8);

        switch (i) {
            case 0:
                return new Weapon("Sting","Glows when goblins are near",101, 150);
            case 1:
                return new Weapon("Excalibur","Very shiny",355, 600);
            case 2:
                return new Weapon("Wooden Club","Primitive",5, 30);
            case 3:
                return new Curiosity("NASA Mug","An old mug with a NASA logo",200, locationToSimpleString(latitude, longitude));
            case 4:
                return new Curiosity("Fidget Spinner","A relic of the past",200, locationToSimpleString(latitude, longitude));
            case 5:
                return new Curiosity("Bottle Cap","Might hold some value",999, locationToSimpleString(latitude, longitude));
            case 6:
                return new Weapon("Mysterious Axe","???",rand.nextInt(980)+10, rand.nextInt(980)+10);
            case 7:
                return new Curiosity("Abstract Object","How does this exist?",rand.nextInt(980)+10, locationToSimpleString(latitude, longitude));
            default:
                return new Weapon("Rock","Versatile",2, 10);
        }
    }

    // returns a String of a location input of latitude and longitude
    public static String locationToSimpleString(double latitude, double longitude) {
        DecimalFormat twoDP = new DecimalFormat("#.##");
        return ("["+twoDP.format(latitude)+", "+twoDP.format(longitude)+"]");
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
