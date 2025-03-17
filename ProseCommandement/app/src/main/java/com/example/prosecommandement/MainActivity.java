package com.example.prosecommandement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 1500; // 1.5 secondes
    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Splash screen delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if there's an active mission
                checkMissionStatus();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkMissionStatus() {
        boolean missionActive = missionPrefs.getBoolean("mission_active", false);

        Intent intent;
        if (missionActive) {
            // Retrieve the previous screen to go back to
            String previousPage = missionPrefs.getString("previous_page", "safe_mode");

            // Determine which screen to show
            switch (previousPage) {
                case "reconnaissance_mode":
                    intent = new Intent(this, RecoActivity.class);
                    break;
                case "eco_reconnaissance_mode":
                    intent = new Intent(this, EcoRecoActivity.class);
                    break;
                case "combat_mode":
                    intent = new Intent(this, CombatActivity.class);
                    break;
                case "eco_combat_mode":
                    intent = new Intent(this, EcoCombatActivity.class);
                    break;
                default:
                    intent = new Intent(this, SurActivity.class);
                    break;
            }
        } else {
            // If no active mission, go to config screen
            intent = new Intent(this, ConfigActivity.class);
        }

        startActivity(intent);
        finish();
    }
}