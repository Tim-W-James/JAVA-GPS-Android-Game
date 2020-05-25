package com.nbt.comp2100_bunker_survival.model.items;

import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

// unique resources that are stored in a list in Inventories.
// subclass defines the type of Item and it's function
public abstract class Item implements Parcelable, Comparable {
    // basic properties
    private String name;
    private String description;
    private int tradingValue;

    Item (String name, String description, int tradingValue) {
        this.name = name;
        this.description = description;
        this.tradingValue = tradingValue;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getTradingValue() {
        return tradingValue;
    }

    // returns a list of strings containing the properties of that item
    public abstract List<String> getDetails();

    @Override
    public int compareTo(Object obj) {
        if (obj.getClass().equals(this.getClass()))
            return this.getName().compareTo(((Item) obj).getName());
        else
            return this.getClass().getSimpleName().compareTo(obj.getClass().getSimpleName());
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: "+name+
                "\nType: "+this.getClass().getSimpleName()+
                "\nDescription: "+description+
                "\nTrading Value: "+tradingValue;
    }
}
