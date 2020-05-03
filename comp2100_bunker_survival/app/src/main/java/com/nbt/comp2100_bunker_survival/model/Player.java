package com.nbt.comp2100_bunker_survival.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// holds metadata for each individual user of the app
public class Player {
    // basic properties
    private String id;
    private String displayName;
    private Inventory currentInventory;

    // display name can be set to id initially to be updated later
    public Player (String id) {
        this.id = id;
        this.displayName = id;
        this.currentInventory = Inventory.defaultPlayerInventory();
    }

    // set display name with constructor
    public Player (String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.currentInventory = Inventory.defaultPlayerInventory();
    }

    // create instance with preset inventory
    public Player (String id, String displayName, Inventory currentInventory) {
        this.id = id;
        this.displayName = displayName;
        this.currentInventory = currentInventory;
    }

    /*
     * get methods
     */

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Inventory getCurrentInventory() {
        return currentInventory;
    }

    /*
     * set methods
     */

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /*
     * currentInventory management
     */

    // set the contents of currentInventory to default
    public Inventory defaultCurrentInventory() {
        currentInventory = Inventory.defaultPlayerInventory();
        return currentInventory;
    }

    // clear the contents of currentInventory to 0
    public Inventory clearCurrentInventory() {
        currentInventory = new Inventory();
        return currentInventory;
    }

    // adds an input inventory to the player's current inventory.
    // constrained
    public Inventory addToCurrentInventory(Inventory inventoryToBeAdded) {
        currentInventory.addInventory(inventoryToBeAdded, true);
        return currentInventory;
    }

    // adds the inventory of treasure to the player's current inventory
    public Inventory findTreasure(Treasure treasureFound) {
        addToCurrentInventory(treasureFound.getTreasureInventory());
        return currentInventory;
    }

    // two players are equal if they share the same basic properties,
    // and inventory contents are the same
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Player) {
            return (id.equals(((Player) obj).id) &&
                    displayName.equals(((Player) obj).displayName) &&
                    currentInventory.equals(((Player) obj).currentInventory));
        }
        else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "ID: "+id+
                "\nDisplay Name: "+displayName+
                "\n\n===\nCurrent Inventory:\n===\n\n"+currentInventory.toString();
    }
}
