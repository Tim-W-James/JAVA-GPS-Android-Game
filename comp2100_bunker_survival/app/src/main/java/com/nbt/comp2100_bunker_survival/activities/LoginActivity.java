package com.nbt.comp2100_bunker_survival.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nbt.comp2100_bunker_survival.R;
import com.nbt.comp2100_bunker_survival.model.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //TODO get from preferences
        String playerID = readPlayerKey();
        System.out.println(playerID);
        if (!playerID.equals("Default")){
            //A player has already been created on this device - get their data
            loadingVisible();
            fetchPlayerData(playerID);

        } else {
            createUserVisible();
        }
    }

    public void loadingVisible() {
        EditText usernameDialog = findViewById(R.id.editTextUsername);
        usernameDialog.setVisibility(View.INVISIBLE);
        Button createAccountButton = findViewById(R.id.createAccount);
        createAccountButton.setVisibility(View.INVISIBLE);
        TextView loggingInText = findViewById(R.id.loggingInText);
        loggingInText.setVisibility(View.VISIBLE);
    }

    public void createUserVisible(){
        EditText usernameDialog = findViewById(R.id.editTextUsername);
        usernameDialog.setVisibility(View.VISIBLE);
        Button createAccountButton = findViewById(R.id.createAccount);
        createAccountButton.setVisibility(View.VISIBLE);
        TextView loggingInText = findViewById(R.id.loggingInText);
        loggingInText.setVisibility(View.INVISIBLE);
    }

    /**
     * Do create account code here
     * @param view
     */
    public void createAccount(View view) {
        EditText usernameInput = findViewById(R.id.editTextUsername);
        String usernameString = usernameInput.getText().toString();
        if (!usernameString.equals("")) {
            loadingVisible();
            Player player = new Player();
            player.setDisplayName(usernameString);
            createPlayer(player);
        }
    }

    public void fetchPlayerData(final String playerID){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = null;
                    URL object = new URL("https://antjma0ipl.execute-api.ap-southeast-2.amazonaws.com/dev/getUserInfo");
                    connection = (HttpURLConnection) object.openConnection();
                    String auth = "comp2100BunkerAdmin:zvQzzetkP2vr45HR";
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                    String authenticationHeader = "Basic " + encodedAuth;
                    connection.setRequestProperty("Authorization", authenticationHeader);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("id", playerID);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        //We are good, - GOTO maps ACTIVITY with responsebody
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder stringBuilder = new StringBuilder();
                        String output;
                        while ((output = bufferedReader.readLine()) != null) {
                            stringBuilder.append(output);
                        }
                        String responseJsonString = stringBuilder.toString();
                        //Cleaning up result
                        String substring = responseJsonString.substring(13);
                        substring = substring.substring(0, substring.length() -1);

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("PlayerJson", substring);
                        System.out.println(substring);
                        startActivity(intent);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Unable to login, try again later.", Toast.LENGTH_SHORT).show();
                            createUserVisible();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    public void createPlayer(final Player newPlayer){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection connection = null;
                    URL object = new URL("https://antjma0ipl.execute-api.ap-southeast-2.amazonaws.com/dev/createUser");
                    connection = (HttpURLConnection) object.openConnection();
                    String auth = "comp2100BunkerAdmin:zvQzzetkP2vr45HR";
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                    String authenticationHeader = "Basic " + encodedAuth;
                    connection.setRequestProperty("Authorization", authenticationHeader);
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    Gson gson = new Gson();
                    String jsonBody = gson.toJson(newPlayer);
                    byte[] outputInBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
                    OutputStream os = connection.getOutputStream();
                    os.write(outputInBytes);
                    os.close();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        //We are good, - GOTO maps ACTIVITY with responsebody
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder stringBuilder = new StringBuilder();
                        String output;
                        while ((output = bufferedReader.readLine()) != null) {
                            stringBuilder.append(output);
                        }
                        String responseJsonString = stringBuilder.toString();
                        writePlayerKey(newPlayer.getId());
                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        intent.putExtra("PlayerJson", responseJsonString);
                        startActivity(intent);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Unable to create account, try again later.", Toast.LENGTH_SHORT).show();
                            createUserVisible();
                        }
                    });
                }
            }
        });
        thread.start();
    }

    /**
     * Writes the player ID to preferences
     * @param PlayerKey
     */
    public void writePlayerKey(String PlayerKey) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("ID", PlayerKey);
        editor.commit();
    }

    /**
     * Reads the player ID from preferences
     * @return
     */
    public String readPlayerKey() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String PlayerKey = sharedPref.getString("ID","Default");

        return PlayerKey;
    }
}
