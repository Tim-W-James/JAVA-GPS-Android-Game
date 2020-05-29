package com.nbt.comp2100_bunker_survival;

import com.nbt.comp2100_bunker_survival.model.Inventory;
import com.nbt.comp2100_bunker_survival.model.Player;
import com.nbt.comp2100_bunker_survival.model.Treasure;
import com.nbt.comp2100_bunker_survival.model.TreasureType;
import com.nbt.comp2100_bunker_survival.model.items.*;

import org.junit.Test;

import static org.junit.Assert.*;

public class TreasureTest {
    @Test
    public void generateTreasureTest() {
        for (int i = 0; i < 10; i++) {
            Treasure t = Treasure.generateTreasure(0,0);
            assertTrue("Inventory should be valid", t.getTreasureInventory().verifyInventory());
            assertEquals("Same seed should be equal", t, Treasure.generateTreasure(0,0, t.getSeed()));

            // verify basic types
            switch (t.getType()) {
                case FOOD:
                    assertEquals(0, t.getTreasureInventory().getToiletPaper());
                    assertEquals(0, t.getTreasureInventory().getScrapMetal());
                    assertEquals(1, t.getTreasureInventory().getUniqueItems().size());
                    break;
                case SCRAP_METAL:
                    assertEquals(0, t.getTreasureInventory().getFood());
                    assertEquals(0, t.getTreasureInventory().getToiletPaper());
                    assertEquals(1, t.getTreasureInventory().getUniqueItems().size());
                    break;
                case TOILET_PAPER:
                    assertEquals(0, t.getTreasureInventory().getFood());
                    assertEquals(0, t.getTreasureInventory().getScrapMetal());
                    assertEquals(1, t.getTreasureInventory().getUniqueItems().size());
                    break;
            }
        }
    }
}
