package com.nbt.comp2100_bunker_survival.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.nbt.comp2100_bunker_survival.model.items.AbstractItemAdapter;
import com.nbt.comp2100_bunker_survival.model.items.Item;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

// holds metadata for each individual user of the app
public class Player {
    // basic properties
    private String id;
    private String displayName;
    private Inventory currentInventory;

    // display name can be set to id initially to be updated later
    public Player () {
        this.id = generateUUID();
        this.displayName = id;
        this.currentInventory = Inventory.defaultPlayerInventory();
    }

    // set display name with constructor
    public Player (String displayName) {
        this ();
        this.displayName = displayName;
    }

    // create instance with preset inventory
    public Player (String displayName, Inventory currentInventory) {
        this (displayName);
        this.currentInventory = currentInventory;
    }

    // returns a sample player with filled inventory
    public static Player getTestPlayer() {
        return new Player("Bob", Inventory.getTestInventory());
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
     * class methods
     */

    // generates a new unique id
    public static String generateUUID() {
        String id = UUID.randomUUID().toString();

        return id;
    }

    /*
     * instance methods
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
    // constrained, returns items that do not fit
    public LinkedList<Item> addToCurrentInventory(Inventory inventoryToBeAdded) {
        return currentInventory.addInventory(inventoryToBeAdded, true);
    }

    // adds the inventory of treasure to the player's current inventory
    public Inventory findTreasure(Treasure treasureFound) {
        addToCurrentInventory(treasureFound.getTreasureInventory());
        return currentInventory;
    }

    // returns item if it does not fit
    public Item obtainItem(Item itemToObtain) {
        return currentInventory.addUniqueItem(itemToObtain);
    }

    public void useItem(Item itemToUse) {
        currentInventory.removeUniqueItem(itemToUse);
    }

    // return false if player has run out of food
    public boolean isAlive() {
        return currentInventory.getFood() > 0;
    }

    // consume inputted food.
    // return false if player has run out of food
    public boolean consumeFood(int foodConsumed) {
        currentInventory.setFood(currentInventory.getFood() - foodConsumed);
        return isAlive();
    }

    // return true if player can afford input value, item or inventory
    public boolean canAfford(int value) {
        return currentInventory.getScrapMetal() >= value;
    }
    public boolean canAfford(Item item) {
        return canAfford(item.getTradingValue());
    }
    public boolean canAfford(Inventory inventory) {
        return canAfford(inventory.getValue());
    }

    // buy an item or inventory if able to afford.
    // returns items that do not fit
    public Item buyItem(Item itemToBuy) {
        if (canAfford(itemToBuy)) {
            currentInventory.setScrapMetal(currentInventory.getScrapMetal() - itemToBuy.getTradingValue());
            return obtainItem(itemToBuy);
        }
        else
            return null;
    }
    public LinkedList<Item> buyInventory(Inventory inventoryToBuy) {
        if (canAfford(inventoryToBuy)) {
            currentInventory.setScrapMetal(currentInventory.getScrapMetal() - inventoryToBuy.getValue());
            return addToCurrentInventory(inventoryToBuy);
        }
        else
            return null;
    }

    /*
     * JSON (w/ gson)
     */

    public static Player loadFromJSONFile(File file) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Item.class, new AbstractItemAdapter()).create();
        JsonReader jsonReader = null;
        try {
            jsonReader = new JsonReader(new FileReader(file));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return gson.fromJson(jsonReader, Player.class);
    }

    public void saveToJSONFile(File file) {
        currentInventory.sortUniqueItems(); // sort first

        Gson gson = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Item.class, new AbstractItemAdapter()).create();

        try (FileWriter writer = new FileWriter(file)){
            gson.toJson(this, writer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * override
     */

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
