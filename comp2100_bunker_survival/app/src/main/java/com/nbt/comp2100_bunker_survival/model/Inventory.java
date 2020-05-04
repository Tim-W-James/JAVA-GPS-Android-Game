package com.nbt.comp2100_bunker_survival.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nbt.comp2100_bunker_survival.model.items.Item;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.LinkedList;

// holds information on basic resources and unique items.
// can either be the Inventory of treasure or a player
public class Inventory {
    // basic resources
    private int food;
    private int scrapMetal;
    private int toiletPaper;
    // items
    private LinkedList <Item> uniqueItems;

    // constants
    public static final int RESOURCE_MAX = 9999;
    public static final int RESOURCE_MIN = 0;
    public static final int ITEMS_MAX = 25;

    // empty inventory
    public Inventory() {
        this(0, 0, 0);
    }

    // custom complex inventory
    public Inventory(int food, int scrapMetal, int toiletPaper, LinkedList<Item> uniqueItems) {
        this.food = food;
        this.scrapMetal = scrapMetal;
        this.toiletPaper = toiletPaper;
        this.uniqueItems = uniqueItems;
    }

    // custom partial inventory of basic resources
    public Inventory(int food, int scrapMetal, int toiletPaper) {
        this(food, scrapMetal, toiletPaper, new LinkedList<Item>());
    }

    // custom partial inventory of unique items
    public Inventory(LinkedList<Item> uniqueItems) {
        this(0, 0, 0, uniqueItems);
    }

    // custom partial inventory of basic resources and an Item
    public Inventory(int food, int scrapMetal, int toiletPaper, Item item) {
        this(food, scrapMetal, toiletPaper, new LinkedList<Item>());
        uniqueItems.add(item);
    }

    // custom complex inventory constrained
    public Inventory(int food, int scrapMetal, int toiletPaper, LinkedList <Item> uniqueItems, boolean isConstrained) {
        this(food, scrapMetal, toiletPaper, uniqueItems);
        if (isConstrained)
            constrainInventory();
    }

    // factory method that returns a new inventory instance with default player inventory values
    @Contract(" -> new")
    @NonNull
    public static Inventory defaultPlayerInventory() {
        return new Inventory(50, 25, 0);
    }

    /*
     * get methods
     */

    public int getFood() {
        return food;
    }

    public int getScrapMetal() {
        return scrapMetal;
    }

    public int getToiletPaper() {
        return toiletPaper;
    }

    public LinkedList<Item> getUniqueItems() {
        return uniqueItems;
    }

    /*
     * set methods
     */

    // constrained
    public void setFood(int food) {
        this.food = constrainResource(food);
    }

    // constrained
    public void setScrapMetal(int scrapMetal) {
        this.scrapMetal = constrainResource(scrapMetal);
    }

    // constrained
    public void setToiletPaper(int toiletPaper) {
        this.toiletPaper = constrainResource(toiletPaper);
    }

    // constrained.
    // returns the Item if uniqueItems size exceeds ITEMS_MAX, otherwise returns null
    public Item addUniqueItem(Item itemToAdd) {
        uniqueItems.add(itemToAdd);

        LinkedList<Item> rtn = constrainUniqueItems(uniqueItems);
        if (rtn.isEmpty())
            return null;
        else
            return rtn.getLast();

    }

    public void removeUniqueItem(Item itemToRemove) {
        uniqueItems.remove(itemToRemove);
    }

    /*
     * class methods
     */

    // maintain min and max limits on an inputted resource
    public static int constrainResource(int count) {
        return Math.min(Math.max(count, RESOURCE_MIN), RESOURCE_MAX);
    }

    /*
     * instance methods
     */

    // maintain min and max limits on the size of an item list.
    // returns a list of items that were removed
    public LinkedList<Item> constrainUniqueItems(LinkedList<Item> items) {
        LinkedList<Item> rtn = new LinkedList<>();
        while (items.size() > ITEMS_MAX) {
            rtn.add(items.getLast());
            items.removeLast();
        }
        uniqueItems = items;
        return rtn;
    }

    // maintain min and max limits on all resources.
    // returns a list of items that were removed from uniqueItems
    public LinkedList<Item> constrainInventory() {
        food = constrainResource(food);
        scrapMetal = constrainResource(scrapMetal);
        toiletPaper = constrainResource(toiletPaper);
        return constrainUniqueItems(uniqueItems);
    }

    // adds the basic resources and unique items of another inventory
    public Inventory addInventory(@NotNull Inventory inventoryToBeAdded) {
        food += inventoryToBeAdded.getFood();
        scrapMetal += inventoryToBeAdded.getScrapMetal();
        toiletPaper += inventoryToBeAdded.getToiletPaper();
        uniqueItems.addAll(inventoryToBeAdded.getUniqueItems());
        return this;
    }

    // adds the basic resources and unique items of another inventory,
    // and if isConstrained will maintain min and max limits on all resources.
    // returns a list of items that were removed from uniqueItems
    public LinkedList<Item> addInventory(@NotNull Inventory inventoryToBeAdded, boolean isConstrained) {
        addInventory(inventoryToBeAdded);

        if (isConstrained)
            return constrainInventory();
        else
            return null;
    }

    // verify that the inventory is valid
    public boolean verifyInventory() {
        return (food > 999);
    }

    // two inventories are equal if they share the same number of basic resources,
    // and if unique items are the same regardless of order
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Inventory) {
            return (food == ((Inventory) obj).food &&
                    scrapMetal == ((Inventory) obj).scrapMetal &&
                    toiletPaper == ((Inventory) obj).toiletPaper &&
                    new HashSet<>(uniqueItems).equals(new HashSet<>(((Inventory) obj).uniqueItems)));
        }
        else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder uniqueItemsString = new StringBuilder();
        int count = 0;
        for (Item item : uniqueItems) {
            uniqueItemsString.append("[").append(item.toString()).append("]");
            if (count < uniqueItems.size() - 1)
                uniqueItemsString.append(",\n\n");
            count++;
        }
        return "Food: "+food+
                "\nScrap Metal: "+scrapMetal+
                "\nToilet Paper: "+toiletPaper+
                "\n\n==\nUnique Items:\n==\n\n"+uniqueItemsString;
    }
}
