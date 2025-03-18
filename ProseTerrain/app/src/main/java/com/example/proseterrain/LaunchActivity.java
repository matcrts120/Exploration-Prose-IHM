package com.example.proseterrain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LaunchActivity extends AppCompatActivity {

    private TextView loadingText;
    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        loadingText = findViewById(R.id.loading_text);
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Simuler l'attente de configuration
        simulateLoading();
    }

    private void simulateLoading() {
        // Attente de 5 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Mise à jour du texte
                loadingText.setText(R.string.configuration_complete);

                // Attente de 2 secondes supplémentaires avant la redirection
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Marquer la mission comme active
                        SharedPreferences.Editor editor = missionPrefs.edit();
                        editor.putBoolean("mission_active", true);
                        editor.putString("current_screen", "mission");
                        editor.apply();

                        // Rediriger vers l'écran de mission
                        Intent intent = new Intent(LaunchActivity.this, MissionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        }, 5000);
    }
}