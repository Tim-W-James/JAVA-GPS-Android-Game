package com.nbt.comp2100_bunker_survival.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;

import com.nbt.comp2100_bunker_survival.R;
import com.nbt.comp2100_bunker_survival.model.Player;


public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    /**
     * Do login code here
     * @param view
     */
    public void login(View view) {
        String playerName;
        username = findViewById(R.id.editTextUsername);

        // Server call here
        playerName = "test";

        // Prepare intent
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

        // Add player name
        intent.putExtra("PlayerData", playerName);

        startActivity(intent);
    }
}
