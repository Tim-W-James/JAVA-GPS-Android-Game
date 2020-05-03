package com.nbt.comp2100_bunker_survival.model.items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// an Item that has an interesting lore description,
// along with location and generally has large value
public class Curiosity extends Item {
    // stores the location it was found
    private String locationFound;

    public Curiosity(String name, String description, int tradingValue, String locationFound) {
        super(name, description, tradingValue);
        this.locationFound = locationFound;
    }

    public String getLocationFound() {
        return locationFound;
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
