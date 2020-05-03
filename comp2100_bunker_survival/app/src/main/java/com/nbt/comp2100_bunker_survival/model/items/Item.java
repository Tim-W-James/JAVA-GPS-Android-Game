package com.nbt.comp2100_bunker_survival.model.items;

import androidx.annotation.NonNull;

// unique resources that are stored in a list in Inventories.
// subclass defines the type of Item and it's function
public abstract class Item {
    // basic properties
    private String name;
    private String description;
    private int tradingValue;

    // type of Item - subclass
    private String type;

    Item (String name, String description, int tradingValue) {
        this.name = name;
        this.description = description;
        this.tradingValue = tradingValue;
        type = this.getClass().getSimpleName();
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

    public String getType() {
        return type;
    }

    @NonNull
    @Override
    public String toString() {
        return "Name: "+name+
                "\nType: "+type+
                "\nDescription: "+description+
                "\nTrading Value: "+tradingValue;
    }
}
