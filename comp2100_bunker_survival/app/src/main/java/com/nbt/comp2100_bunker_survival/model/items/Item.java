package com.nbt.comp2100_bunker_survival.model.items;

import androidx.annotation.NonNull;

// unique resources that are stored in a list in Inventories.
// subclass defines the type of Item and it's function
public abstract class Item implements Comparable{
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
