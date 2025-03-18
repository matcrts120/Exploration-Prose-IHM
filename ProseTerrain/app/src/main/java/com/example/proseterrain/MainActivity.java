package com.example.proseterrain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 secondes
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
                // Check if mission is configured
                checkMissionStatus();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void checkMissionStatus() {
        boolean missionActive = missionPrefs.getBoolean("mission_active", false);

        Intent intent;
        if (missionActive) {
            // Si une mission est active, détermine quel écran afficher basé sur l'état précédent
            String previousScreen = missionPrefs.getString("current_screen", "");

            switch (previousScreen) {
                case "transmission":
                    intent = new Intent(this, TransmissionActivity.class);
                    break;
                default:
                    // Par défaut, afficher l'écran de mission
                    intent = new Intent(this, MissionActivity.class);
                    break;
            }
        } else {
            // Aucune mission active, afficher l'écran de lancement
            intent = new Intent(this, LaunchActivity.class);
        }

        startActivity(intent);
        finish();
    }
}