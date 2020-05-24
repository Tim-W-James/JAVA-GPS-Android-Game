package com.nbt.comp2100_bunker_survival.model.items;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

// an Item that is used in combat
public class Weapon extends Item {
    // rawPower indicates the base strength this item has in combat
    private int rawPower;

    public Weapon(String name, String description, int tradingValue, int rawPower) {
        super(name, description, tradingValue);
        this.rawPower = rawPower;
    }

    public int getRawPower() {
        return rawPower;
    }

    @Override
    public List<String> getDetails() {
        ArrayList<String> details = new ArrayList<String>();
        details.add("Description: "+getDescription());
        details.add("Trading Value: "+getTradingValue());
        details.add("Raw Power: "+getRawPower());
        return details;
    }

    // equal if properties are equal
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Weapon) {
            return (getName().equals(((Weapon) obj).getName()) &&
                    getDescription().equals(((Weapon) obj).getDescription()) &&
                    getTradingValue() == ((Weapon) obj).getTradingValue() &&
                    rawPower == ((Weapon) obj).rawPower);
        }
        else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString()+
                "\nRaw Power: "+rawPower;
    }
}
