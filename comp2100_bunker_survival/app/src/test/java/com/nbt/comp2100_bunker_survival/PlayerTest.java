package com.nbt.comp2100_bunker_survival;

import com.nbt.comp2100_bunker_survival.model.Inventory;
import com.nbt.comp2100_bunker_survival.model.Player;
import com.nbt.comp2100_bunker_survival.model.Treasure;
import com.nbt.comp2100_bunker_survival.model.items.*;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class PlayerTest {
    private Item weapon1 = new Weapon("Sting","Glows when goblins are near",101, 150);
    private Item weapon2 = new Weapon("Excalibur","Very shiny",355, 600);
    private Item weapon3 = new Weapon("Wooden Club","Primitive",5, 30);
    private Item curiosity1 = new Curiosity("NASA Mug","An old mug with a NASA logo",200, "Space");
    private Item curiosity2 = new Curiosity("Fidget Spinner","A relic of the past",200, "A Trash Bin");
    private Item curiosity3 = new Curiosity("Bottle Cap","Might hold some value",999, "A Vault");

    private LinkedList<Item> itemListNorm1 = new LinkedList<>();
    private LinkedList<Item> itemListNorm2 = new LinkedList<>();
    private LinkedList<Item> itemListNorm3 = new LinkedList<>();

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
    }

    @Test
    public void findTreasureTest() {
        Inventory invNorm1 = new Inventory(1, 5, 25, itemListNorm1);
        Inventory invNorm2 = new Inventory(1, 7, 9, itemListNorm2);
        Inventory invAdded = new Inventory(
                invNorm1.getFood()+invNorm2.getFood(),
                invNorm1.getScrapMetal()+invNorm2.getScrapMetal(),
                invNorm1.getToiletPaper()+invNorm2.getToiletPaper(),
                itemListNorm3);
        Player player1 = new Player("Bob", invNorm1);
        Treasure treasure1 = new Treasure("name", 0, 0, 0, invNorm2);

        assertNotEquals("Treasure not yet found", invAdded, player1.getCurrentInventory());
        player1.findTreasure(treasure1);
        assertEquals("Treasure inventory should be added", invAdded, player1.getCurrentInventory());

        Inventory invOverPopulated = new Inventory(Inventory.RESOURCE_MAX+1, 0, 0);
        Treasure treasure2 = new Treasure("name", 0, 0, 0, invOverPopulated);
        player1.findTreasure(treasure2);
        assertEquals("Added inventory should retain constraint", Inventory.RESOURCE_MAX, player1.getCurrentInventory().getFood());
    }

    @Test
    public void defaultCurrentInventoryTest() {
        Player player1 = new Player("Bob", new Inventory(1, 1, 1, itemListNorm1));
        player1.defaultCurrentInventory();
        assertEquals(Inventory.defaultPlayerInventory(), player1.getCurrentInventory());
    }

    @Test
    public void clearCurrentInventoryTest() {
        Player player1 = new Player("Bob", new Inventory(1, 1, 1, itemListNorm1));
        player1.clearCurrentInventory();
        assertEquals(new Inventory(), player1.getCurrentInventory());
    }

    @Test
    public void buyItemTest() {
        Player player1 = new Player("Bob", new Inventory(0,curiosity1.getTradingValue()-1,0, itemListNorm1));
        player1.buyItem(curiosity1);
        assertEquals("Should not add item if cannot afford", 3, player1.getCurrentInventory().getUniqueItems().size());
        assertEquals("Should not subtract scrap metal if cannot afford", curiosity1.getTradingValue()-1, player1.getCurrentInventory().getScrapMetal());

        player1.getCurrentInventory().setScrapMetal(curiosity1.getTradingValue());
        player1.buyItem(curiosity1);
        assertEquals("Should add item if can afford", 4, player1.getCurrentInventory().getUniqueItems().size());
        assertEquals("Should subtract scrap metal if can afford", 0, player1.getCurrentInventory().getScrapMetal());
    }

    @Test
    public void buyInventoryTest() {
        Inventory invNorm1 = new Inventory(1, 5, 25, itemListNorm1);
        Inventory invNorm2 = new Inventory(1, invNorm1.getValue()-1, 9, itemListNorm2);
        Inventory invAdded = new Inventory(
                invNorm1.getFood()+invNorm2.getFood(),
                invNorm1.getScrapMetal()+invNorm2.getScrapMetal(),
                invNorm1.getToiletPaper()+invNorm2.getToiletPaper(),
                itemListNorm3);
        Player player1 = new Player("Bob", invNorm2);
        player1.buyInventory(invNorm1);
        assertEquals("Should not add inventory if cannot afford", 1, player1.getCurrentInventory().getFood());
        assertEquals("Should not subtract scrap metal if cannot afford", invNorm1.getValue()-1, player1.getCurrentInventory().getScrapMetal());

        player1.getCurrentInventory().setScrapMetal(invNorm1.getValue());
        player1.buyInventory(invNorm1);
        assertEquals("Should add inventory if can afford", 2, player1.getCurrentInventory().getFood());
        assertEquals("Should subtract scrap metal if can afford", invNorm1.getScrapMetal(), player1.getCurrentInventory().getScrapMetal());
    }

    @Test
    public void consumeFoodTest() {
        Inventory invNorm1 = new Inventory(3, 0, 0);
        Player player1 = new Player("Bob", invNorm1);
        assertTrue(player1.consumeFood(1));
        assertEquals("Food should be consumed", 2, player1.getCurrentInventory().getFood());

        assertFalse(player1.consumeFood(5));
        assertEquals("Food should be consumed", 0, player1.getCurrentInventory().getFood());
    }

    @Test
    public void JSONTest() throws Exception {
        Inventory invNorm1 = new Inventory(1, 5, 25, itemListNorm1);
        Player player1 = new Player("Bob", invNorm1);

        // save to string
        String s = player1.saveToJSON();

        // load from file
        Player comparisonPlayer = Player.loadFromJSON(s);

        assertEquals("Object loaded from JSON does not match original", player1, comparisonPlayer);
    }

    @Test
    public void uniqueIDTest() {
        Inventory invNorm1 = new Inventory(1, 5, 25, itemListNorm1);
        Player player1 = new Player("Bob", invNorm1);
        Player player2 = new Player("Bob", invNorm1);

        assertNotEquals("id should be unique", player1, player2);
    }
}
