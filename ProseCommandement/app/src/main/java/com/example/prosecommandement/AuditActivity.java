package com.example.prosecommandement;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AuditActivity extends AppCompatActivity {

    private TextView entitiesCountTextView;
    private TextView alliesIdsTextView;
    private TextView missionStatusTextView;
    private Button endMissionButton;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        loadAuditData();
        setupEndMissionButton();
    }

    private void initializeViews() {
        entitiesCountTextView = findViewById(R.id.entities_count);
        alliesIdsTextView = findViewById(R.id.allies_ids);
        missionStatusTextView = findViewById(R.id.mission_status);
        endMissionButton = findViewById(R.id.end_mission_button);
    }

    private void loadAuditData() {
        // In a real app, this data would come from SharedPreferences or a database
        // Here we're using placeholder data

        // Get team ID as allies ID
        String teamId = missionPrefs.getString("team_id", "12");
        alliesIdsTextView.setText(teamId);

        // Set entities count (static for demo)
        entitiesCountTextView.setText("2");

        // Set mission status
        missionStatusTextView.setText("Terminée");
    }

    private void setupEndMissionButton() {
        endMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndMissionConfirmation();
            }
        });
    }

    private void showEndMissionConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.end_mission);
        builder.setMessage(R.string.mission_ended);
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            // Reset mission data
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.clear();
            editor.apply();

            // Go back to config activity
            Intent intent = new Intent(AuditActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setCancelable(false);
        builder.create().show();
    }
}