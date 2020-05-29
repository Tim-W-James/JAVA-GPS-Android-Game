package com.nbt.comp2100_bunker_survival.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nbt.comp2100_bunker_survival.R;
import com.nbt.comp2100_bunker_survival.model.LeaderBoardPlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LeaderboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    HttpURLConnection connection = null;
                    URL object = new URL("https://antjma0ipl.execute-api.ap-southeast-2.amazonaws.com/dev/getTopFive");
                    connection = (HttpURLConnection) object.openConnection();
                    String auth = "comp2100BunkerAdmin:zvQzzetkP2vr45HR";
                    String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
                    String authenticationHeader = "Basic " + encodedAuth;
                    connection.setRequestProperty("Authorization",authenticationHeader);
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200){
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                        StringBuilder stringBuilder = new StringBuilder();
                        String output;
                        while ((output = bufferedReader.readLine()) != null) {
                            stringBuilder.append(output);
                        }
                        String responseJsonString = stringBuilder.toString();
                        buildLeaderBoard(responseJsonString);
                    } else {
                        throw new Exception();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(),"No Network Activity", Toast.LENGTH_SHORT).show();
                        }
                    });
                    finish();
                }
            }
        });
        thread.start();
    }
    class LeaderBoardPlayers{
        private LeaderBoardPlayer[] result;
    }

    private void buildLeaderBoard(String responseJsonString){
        Gson gson = new Gson();
        LeaderBoardPlayers LeaderBoardPlayers = gson.fromJson(responseJsonString, LeaderBoardPlayers.class);

        for (int i = 0; i <LeaderBoardPlayers.result.length; i++){
            String name = LeaderBoardPlayers.result[i].displayName;
            String value = Integer.toString(LeaderBoardPlayers.result[i].value);
            if (i == 0){
                TextView nameText = findViewById(R.id.Rank1Name);
                nameText.setText(name);
                TextView valueText = findViewById(R.id.Rank1Value);
                valueText.setText(value);
            } else if (i == 1){
                TextView nameText = findViewById(R.id.Rank2Name);
                nameText.setText(name);
                TextView valueText = findViewById(R.id.Rank2Value);
                valueText.setText(value);
            }else if (i == 2){
                TextView nameText = findViewById(R.id.Rank3Name);
                nameText.setText(name);
                TextView valueText = findViewById(R.id.Rank3Value);
                valueText.setText(value);
            }else if (i == 3){
                TextView nameText = findViewById(R.id.Rank4Name);
                nameText.setText(name);
                TextView valueText = findViewById(R.id.Rank4Value);
                valueText.setText(value);
            }else if (i == 4){
                TextView nameText = findViewById(R.id.Rank5Name);
                nameText.setText(name);
                TextView valueText = findViewById(R.id.Rank5Value);
                valueText.setText(value);
            }
        }


    }

    public void backButtonPressed(View view) {
        finish();
    }
}
