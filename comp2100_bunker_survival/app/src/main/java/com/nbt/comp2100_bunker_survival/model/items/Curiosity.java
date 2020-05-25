package com.nbt.comp2100_bunker_survival.model.items;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.nbt.comp2100_bunker_survival.model.Treasure;

import java.util.ArrayList;
import java.util.List;

// an Item that has an interesting lore description,
// along with location and generally has large value
public class Curiosity extends Item {
    // stores the location it was found
    private String locationFound;

    public Curiosity(String name, String description, int tradingValue, String locationFound) {
        super(name, description, tradingValue);
        this.locationFound = locationFound;
    }

    // constructors for directly inputting location
    public Curiosity(String name, String description, int tradingValue, double latitude, double longitude) {
        super(name, description, tradingValue);
        this.locationFound = Treasure.locationToSimpleString(latitude, longitude);
    }
    public Curiosity(String name, String description, int tradingValue, Location locationFound) {
        super(name, description, tradingValue);
        this.locationFound = Treasure.locationToSimpleString(locationFound);
    }
    public Curiosity(String name, String description, int tradingValue, LatLng locationFound) {
        super(name, description, tradingValue);
        this.locationFound = Treasure.locationToSimpleString(locationFound);
    }

    public String getLocationFound() {
        return locationFound;
    }

    @Override
    public List<String> getDetails() {
        ArrayList<String> details = new ArrayList<String>();
        details.add("Description: "+getDescription());
        details.add("Trading Value: "+getTradingValue());
        details.add("Location Found: "+getLocationFound());
        return details;
    }

    // equal if properties are equal
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Curiosity) {
            return (getName().equals(((Curiosity) obj).getName()) &&
                    getDescription().equals(((Curiosity) obj).getDescription()) &&
                    getTradingValue() == ((Curiosity) obj).getTradingValue() &&
                    locationFound.equals(((Curiosity) obj).locationFound));
        }
        else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()+
                "\nLocation Found: "+locationFound;
    }
}
