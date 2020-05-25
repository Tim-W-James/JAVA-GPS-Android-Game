package com.nbt.comp2100_bunker_survival.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.nbt.comp2100_bunker_survival.R;
import com.nbt.comp2100_bunker_survival.model.Inventory;
import com.nbt.comp2100_bunker_survival.model.ItemExpListAdapter;

import java.util.List;
import java.util.Map;


public class InventoryActivity extends AppCompatActivity {

    Inventory currentInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        TextView titleText = findViewById(R.id.titleText);
        titleText.setText(getIntent().getStringExtra("header"));
        currentInventory = getIntent().getParcelableExtra("inventory");
        initializeInventory(currentInventory);
    }

    public void backPressed(View view) {
        finish();
    }

    public void backButtonPressed(View view) {
        finish();
    }

    // initializes the text fields and item list for the inventory
    public void initializeInventory(Inventory inventory) {
        TextView foodText = findViewById(R.id.foodText);
        String foodContent = "Food: "+inventory.getFood();
        foodText.setText(foodContent);

        TextView toiletPaperText = findViewById(R.id.toiletPaperText);
        String toiletPaperContent = "Toilet Paper: "+inventory.getToiletPaper();
        toiletPaperText.setText(toiletPaperContent);

        TextView scrapMetalText = findViewById(R.id.scrapMetalText);
        String scrapMetalContent = "Scrap Metal: "+inventory.getScrapMetal();
        scrapMetalText.setText(scrapMetalContent);

        ExpandableListView itemList = findViewById(R.id.itemList);

        List<String> items = inventory.getItemNames();
        Map<String, List<String>> details = inventory.getItemDetails();
        ExpandableListAdapter expListAdapter = new ItemExpListAdapter(this, items, details);
        itemList.setAdapter(expListAdapter);
    }
}
