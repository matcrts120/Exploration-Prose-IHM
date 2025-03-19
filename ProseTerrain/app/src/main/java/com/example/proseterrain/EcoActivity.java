package com.example.proseterrain;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EcoActivity extends AppCompatActivity {

    private Button endMissionButton;
    private Button requestBackupButton;
    private Button retreatButton;
    private Button btnEco;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupButtons();
    }

    private void initializeViews() {
        endMissionButton = findViewById(R.id.btn_end_mission);
        requestBackupButton = findViewById(R.id.btn_request_backup);
        retreatButton = findViewById(R.id.btn_retreat);
        btnEco = findViewById(R.id.btn_retour);
    }

    private void setupButtons() {
        // End mission button
        endMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndMissionDialog();
            }
        });

        // Request backup button
        requestBackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EcoActivity.this,
                        "Demande de renfort envoyée au commandement",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Retreat button
        retreatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EcoActivity.this,
                        "Confirmation de repli envoyée",
                        Toast.LENGTH_SHORT).show();
            }
        });

        btnEco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeEco();
            }
        });
    }

    private void showEndMissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.end_mission);
        builder.setMessage(R.string.end_mission_confirmation);

        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            // Reset mission data
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.clear();
            editor.apply();

            // Return to main screen
            Intent intent = new Intent(EcoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void modeEco() {
        // Reset mission data
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.clear();
        editor.apply();

        // Eco screen
        Intent intent = new Intent(EcoActivity.this, TransmissionActivity.class);
        startActivity(intent);
        finish();
    }
}