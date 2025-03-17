package com.example.prosecommandement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ConfigActivity extends AppCompatActivity {

    private EditText teamNameEditText;
    private EditText teamIdEditText;
    private EditText targetInfoEditText;
    private Button validateButton;
    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI elements
        initializeViews();

        // Set up button click listener
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateMission();
            }
        });
    }

    private void initializeViews() {
        teamNameEditText = findViewById(R.id.team_name);
        teamIdEditText = findViewById(R.id.team_id);
        targetInfoEditText = findViewById(R.id.target_info);
        validateButton = findViewById(R.id.validate_button);
    }

    private void validateMission() {
        String teamName = teamNameEditText.getText().toString().trim();
        String teamId = teamIdEditText.getText().toString().trim();
        String targetInfo = targetInfoEditText.getText().toString().trim();

        // Simple validation
        if (teamName.isEmpty() || teamId.isEmpty() || targetInfo.isEmpty()) {
            Toast.makeText(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
            return;
        }

        // Save mission data
        saveMissionData(teamName, teamId, targetInfo);

        // Navigate to Safe Mode screen
        Intent intent = new Intent(ConfigActivity.this, SurActivity.class);
        startActivity(intent);
        finish(); // Close config activity
    }

    private void saveMissionData(String teamName, String teamId, String targetInfo) {
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.putString("team_name", teamName);
        editor.putString("team_id", teamId);
        editor.putString("target_info", targetInfo);
        editor.putBoolean("mission_active", true);
        editor.apply();
    }
}