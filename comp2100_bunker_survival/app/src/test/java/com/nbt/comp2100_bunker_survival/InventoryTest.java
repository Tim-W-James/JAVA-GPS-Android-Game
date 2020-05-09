package com.nbt.comp2100_bunker_survival;

import com.nbt.comp2100_bunker_survival.model.Inventory;
import com.nbt.comp2100_bunker_survival.model.items.*;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class InventoryTest {

    private Item weapon1 = new Weapon("Sting","Glows when goblins are near",101, 150);
    private Item weapon2 = new Weapon("Excalibur","Very shiny",355, 600);
    private Item weapon3 = new Weapon("Wooden Club","Primitive",5, 30);
    private Item curiosity1 = new Curiosity("NASA Mug","An old mug with a NASA logo",200, "Space");
    private Item curiosity2 = new Curiosity("Fidget Spinner","A relic of the past",200, "A Trash Bin");
    private Item curiosity3 = new Curiosity("Bottle Cap","Might hold some value",999, "A Vault");

    private LinkedList<Item> itemListNorm1 = new LinkedList<>();
    private LinkedList<Item> itemListNorm2 = new LinkedList<>();
    private LinkedList<Item> itemListNorm3 = new LinkedList<>();

    private LinkedList<Item> itemListOverPopulated = new LinkedList<>();

    @Before
    public void initializeLists() {
        itemListNorm1.add(weapon1);
        itemListNorm1.add(weapon2);
        itemListNorm1.add(weapon3);

        itemListNorm2.add(curiosity1);
        itemListNorm2.add(curiosity2);
        itemListNorm2.add(curiosity3);

        itemListNorm3.add(weapon1);
        itemListNorm3.add(weapon2);
        itemListNorm3.add(weapon3);
        itemListNorm3.add(curiosity1);
        itemListNorm3.add(curiosity2);
        itemListNorm3.add(curiosity3);

        for (int i = 0; i < Inventory.ITEMS_MAX + 1; i++) {
            Item item = new Weapon("name", "description", i, i);
            itemListOverPopulated.add(item);
        }
    }

    @Test
    public void addInventoryTest() {
        Inventory invNorm1 = new Inventory(1, 5, 25, itemListNorm1);
        Inventory invNorm2 = new Inventory(1, 7, 9, itemListNorm2);
        Inventory invAdded = new Inventory(
                invNorm1.getFood()+invNorm2.getFood(),
                invNorm1.getScrapMetal()+invNorm2.getScrapMetal(),
                invNorm1.getToiletPaper()+invNorm2.getToiletPaper(),
                itemListNorm3);

        assertNotEquals(invAdded, invNorm1);
        assertEquals("Result should be: "+invAdded.toString(), invAdded, invNorm1.addInventory(invNorm2));

        invNorm1 = new Inventory(1, 5, 25, itemListNorm1);
        Inventory invOverPopulated = new Inventory(Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1, itemListOverPopulated);
        invNorm1.addInventory(invOverPopulated, false);
        assertFalse("Inventory should not be valid", invNorm1.verifyInventory());
        invNorm1 = new Inventory(1, 5, 25, itemListNorm1);
        invNorm1.addInventory(invOverPopulated, true);
        assertTrue("Inventory should be valid", invNorm1.verifyInventory());
    }

    @Test
    public void defaultPlayerInventoryTest() {
        Inventory inv1 = new Inventory(50, 25, 0);
        assertEquals("Food not equal", inv1.getFood(), Inventory.defaultPlayerInventory().getFood());
        assertEquals("Scrap Metal not equal", inv1.getScrapMetal(), Inventory.defaultPlayerInventory().getScrapMetal());
        assertEquals("Toilet Paper not equal", inv1.getToiletPaper(), Inventory.defaultPlayerInventory().getToiletPaper());
        assertEquals("Unique Item Count not equal", inv1.getUniqueItems(), Inventory.defaultPlayerInventory().getUniqueItems());
        assertEquals("Default inventory should be "+inv1.toString(), inv1, Inventory.defaultPlayerInventory());
    }

    @Test
    public void constrainInventoryTest() {

        Inventory invOverPopulated = new Inventory(Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1, itemListOverPopulated);
        invOverPopulated.constrainInventory();
        assertEquals("Resource not constrained to max", Inventory.RESOURCE_MAX, invOverPopulated.getFood());
        assertEquals("Resource not constrained to max", Inventory.RESOURCE_MAX, invOverPopulated.getScrapMetal());
        assertEquals("Resource not constrained to max", Inventory.RESOURCE_MAX, invOverPopulated.getToiletPaper());
        assertEquals("Unique Item List not constrained to max size", Inventory.ITEMS_MAX, invOverPopulated.getUniqueItems().size());

        Inventory invUnderPopulated = new Inventory(Inventory.RESOURCE_MIN-1, Inventory.RESOURCE_MIN-1, Inventory.RESOURCE_MIN-1);invOverPopulated.constrainInventory();
        invUnderPopulated.constrainInventory();
        assertEquals("Resource not constrained to min", Inventory.RESOURCE_MIN, invUnderPopulated.getFood());
        assertEquals("Resource not constrained to min", Inventory.RESOURCE_MIN, invUnderPopulated.getScrapMetal());
        assertEquals("Resource not constrained to min", Inventory.RESOURCE_MIN, invUnderPopulated.getToiletPaper());
    }

    @Test
    public void verifyInventoryTest() {
        assertTrue("Inventory should be valid", new Inventory(Inventory.RESOURCE_MAX, Inventory.RESOURCE_MAX, Inventory.RESOURCE_MAX).verifyInventory());
        assertFalse("Inventory should not be valid (resource max)", new Inventory(Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1).verifyInventory());
        assertFalse("Inventory should not be valid (resource min)", new Inventory(Inventory.RESOURCE_MIN-1, Inventory.RESOURCE_MIN-1, Inventory.RESOURCE_MIN-1).verifyInventory());

        assertFalse("Inventory should not be valid (item min)", new Inventory(itemListOverPopulated).verifyInventory());
    }

    @Test
    public void verifyAndConstrainInventoryTest() {
        Inventory invOverPopulated = new Inventory(Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1, Inventory.RESOURCE_MAX+1, itemListOverPopulated);
        assertFalse("Inventory should not be valid before constrain", invOverPopulated.verifyInventory());
        invOverPopulated.constrainInventory();
        assertTrue("Inventory should be valid after constrain", invOverPopulated.verifyInventory());
    }
}
