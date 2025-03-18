package com.example.proseterrain;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FinMissionActivity extends AppCompatActivity {

    private TextView statusIconView;
    private TextView entitiesDetectedView;
    private TextView allyIdView;
    private TextView missionStatusView;
    private Button confirmButton;

    private SharedPreferences missionPrefs;
    private Animation pulseAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fin_mission);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize views
        initializeViews();

        // Set up animation
        setupAnimation();

        // Load mission data
        loadMissionData();

        // Set up button
        setupButton();
    }

    private void initializeViews() {
        statusIconView = findViewById(R.id.status_icon);
        entitiesDetectedView = findViewById(R.id.entities_detected);
        allyIdView = findViewById(R.id.ally_id);
        missionStatusView = findViewById(R.id.mission_status);
        confirmButton = findViewById(R.id.confirm_button);
    }

    private void setupAnimation() {
        // Create pulse animation
        pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);

        // Apply animation to status icon
        statusIconView.startAnimation(pulseAnimation);
    }

    private void loadMissionData() {
        // You could load real data from SharedPreferences or a database
        // For now we'll use sample data similar to the HTML

        String teamId = missionPrefs.getString("team_id", "0314");
        allyIdView.setText(teamId);

        // Fixed data for demonstration - in a real app, you would get this from mission stats
        entitiesDetectedView.setText("2");
        missionStatusView.setText(getString(R.string.completed_status));
    }

    private void setupButton() {
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.extraction_point)
                .setMessage(R.string.return_to_extraction)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Clear mission data
                        SharedPreferences.Editor editor = missionPrefs.edit();
                        editor.clear();
                        editor.apply();

                        // Return to main activity
                        Intent intent = new Intent(FinMissionActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }
}