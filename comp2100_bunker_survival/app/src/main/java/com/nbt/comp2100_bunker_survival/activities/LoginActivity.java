package com.nbt.comp2100_bunker_survival.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.nbt.comp2100_bunker_survival.R;


public class LoginActivity extends AppCompatActivity {
    EditText username = (EditText)findViewById(R.id.editTextUsername);
    EditText password = (EditText)findViewById(R.id.editTextPassword);

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
        if (username.toString().equalsIgnoreCase("test") &&
        password.toString().equals("1234")) {
            // You guessed the password
        } else {
            // You didn't guess the password
        }
    }
}
