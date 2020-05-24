package com.nbt.comp2100_bunker_survival.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nbt.comp2100_bunker_survival.model.items.Curiosity;
import com.nbt.comp2100_bunker_survival.model.items.Item;
import com.nbt.comp2100_bunker_survival.model.items.Weapon;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// holds information on basic resources and unique items.
// can either be the Inventory of treasure or a player
public class Inventory {
    // basic resources.
    // note that negative values can be used for subtracting from another inventory,
    // for such inventories do not constrain
    private int food; // keeps the player alive
    private int scrapMetal; // currency
    private int toiletPaper;
    // items
    private LinkedList <Item> uniqueItems;
    // value is the total of all resources multiplied by respective values and trading value of items
    private int value;

    // constants
    public static final int RESOURCE_MAX = 9999;
    public static final int RESOURCE_MIN = 0;
    public static final int ITEMS_MAX = 25;
    public static final int FOOD_VALUE = 1;
    public static final int TOILETPAPER_VALUE = 1;

    // empty inventory
    public Inventory() {
        this(RESOURCE_MIN, RESOURCE_MIN, RESOURCE_MIN);
    }

    // custom complex inventory
    public Inventory(int food, int scrapMetal, int toiletPaper, LinkedList<Item> uniqueItems) {
        this.food = food;
        this.scrapMetal = scrapMetal;
        this.toiletPaper = toiletPaper;
        this.uniqueItems = uniqueItems;
        setValue();
    }

    // custom partial inventory of basic resources
    public Inventory(int food, int scrapMetal, int toiletPaper) {
        this(food, scrapMetal, toiletPaper, new LinkedList<Item>());
    }

    // custom partial inventory of unique items
    public Inventory(LinkedList<Item> uniqueItems) {
        this(RESOURCE_MIN, RESOURCE_MIN, RESOURCE_MIN, uniqueItems);
    }

    // custom partial inventory of basic resources and an Item
    public Inventory(int food, int scrapMetal, int toiletPaper, Item item) {
        this(food, scrapMetal, toiletPaper, new LinkedList<Item>());
        uniqueItems.add(item);
    }

    // custom complex inventory constrained
    public Inventory(int food, int scrapMetal, int toiletPaper, LinkedList<Item> uniqueItems, boolean isConstrained) {
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

    // returns a sample inventory with items
    public static Inventory getTestInventory() { // TODO remove: only for server test
        Item weapon1 = new Weapon("Sting","Glows when goblins are near",101, 150);
        Item weapon2 = new Weapon("Excalibur","Very shiny",355, 600);
        Item weapon3 = new Weapon("Wooden Club","Primitive",5, 30);
        Item curiosity1 = new Curiosity("NASA Mug","An old mug with a NASA logo",200, "Space");
        Item curiosity2 = new Curiosity("Fidget Spinner","A relic of the past",200, "A Trash Bin");
        Item curiosity3 = new Curiosity("Bottle Cap","Might hold some value",999, "A Vault");

        LinkedList<Item> itemList1 = new LinkedList<Item>();
        itemList1.add(weapon1);
        itemList1.add(weapon2);
        itemList1.add(weapon3);
        itemList1.add(curiosity1);
        itemList1.add(curiosity2);
        itemList1.add(curiosity3);
        return new Inventory(50, 25, 0, itemList1, true);
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
        sortUniqueItems();
        return uniqueItems;
    }

    public int getValue() {
        setValue();
        return value;
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

    public void setValue() {
        this.value = calculateValue(this.food, this.scrapMetal, this.toiletPaper, this.uniqueItems);
    }

    /*
     * class methods
     */

    // get the total value of an inventory
    public static int calculateValue(int food, int scrapMetal, int toiletPaper, LinkedList<Item> uniqueItems) {
        int uniqueItemsTotalValue = 0;
        for (Item item : uniqueItems) {
            uniqueItemsTotalValue += item.getTradingValue();
        }

        return (food * FOOD_VALUE) + scrapMetal + (toiletPaper * TOILETPAPER_VALUE) + uniqueItemsTotalValue;
    }

    // maintain min and max limits on an inputted resource
    public static int constrainResource(int count) {
        return Math.min(Math.max(count, RESOURCE_MIN), RESOURCE_MAX);
    }

    /*
     * instance methods
     */

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

    public void sortUniqueItems() {
        Collections.sort(uniqueItems);
    }

    // maintain min and max limits on the size of an item list.
    // returns a list of items that were removed
    public LinkedList<Item> constrainUniqueItems(LinkedList<Item> items) {
        LinkedList<Item> rtn = new LinkedList<Item>();
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

    // adds the basic resources and unique items of another inventory.
    // add an inventory with negative values to subtract
    public Inventory addInventory(@NotNull Inventory inventoryToBeAdded) {
        food += inventoryToBeAdded.getFood();
        scrapMetal += inventoryToBeAdded.getScrapMetal();
        toiletPaper += inventoryToBeAdded.getToiletPaper();
        uniqueItems.addAll(inventoryToBeAdded.getUniqueItems());
        return this;
    }

    // adds the basic resources and unique items of another inventory,
    // and if isConstrained will maintain min and max limits on all resources.
    // add an inventory with negative values to subtract.
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
        return (food <= RESOURCE_MAX && food >= RESOURCE_MIN &&
                scrapMetal <= RESOURCE_MAX && scrapMetal >= RESOURCE_MIN &&
                toiletPaper <= RESOURCE_MAX && toiletPaper >= RESOURCE_MIN &&
                uniqueItems.size() <= ITEMS_MAX);
    }

    // returns a list containing all item names
    public List<String> getItemNames() {
        sortUniqueItems();
        ArrayList<String> names = new ArrayList<String>();
        for (Item item : uniqueItems) {
            names.add(item.getName());
        }
        return names;
    }

    // returns a map with item names as keys and a list of properties as values
    public Map<String, List<String>> getItemDetails() {
        sortUniqueItems();
        Map<String, List<String>> details = new HashMap<String, List<String>>();
        for (Item item : uniqueItems) {
            details.put(item.getName(), item.getDetails());
        }
        return details;
    }

    // two inventories are equal if they share the same number of basic resources,
    // and if unique items contains the same items
    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Inventory) {
            return (food == ((Inventory) obj).food &&
                    scrapMetal == ((Inventory) obj).scrapMetal &&
                    toiletPaper == ((Inventory) obj).toiletPaper &&
                    getUniqueItems().equals(((Inventory) obj).getUniqueItems()));
        }
        else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder uniqueItemsString = new StringBuilder();
        int count = 0;
        for (Item item : getUniqueItems()) {
            uniqueItemsString.append("[").append(item.toString()).append("]");
            if (count < uniqueItems.size() - 1)
                uniqueItemsString.append(",\n\n");
            count++;
        }
        setValue();
        return "Food: "+food+
                "\nScrap Metal: "+scrapMetal+
                "\nToilet Paper: "+toiletPaper+
                "\nValue: "+value+
                "\n\n==\nUnique Items:\n==\n\n"+uniqueItemsString;
    }
}
