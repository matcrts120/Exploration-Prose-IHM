package com.example.prosecommandement;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class EcoCombatActivity extends AppCompatActivity {

    private Spinner modeSelector;
    private Button retreatButton;
    private Button requestApprovedButton;
    private Button requestDeniedButton;
    private SwitchCompat ecoModeSwitch;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_combat);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupModeSelector();
        setupButtons();
        setupEcoMode();
    }

    private void initializeViews() {
        modeSelector = findViewById(R.id.mode_selector);
        retreatButton = findViewById(R.id.btn_retreat);
        requestApprovedButton = findViewById(R.id.btn_request_approved);
        requestDeniedButton = findViewById(R.id.btn_request_denied);
        ecoModeSwitch = findViewById(R.id.eco_mode_switch);
    }

    private void setupModeSelector() {
        // Create adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.operation_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(adapter);

        // Set default selection to Combat Mode (index 2)
        modeSelector.setSelection(2);

        // Set up mode change listener
        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 2) { // If not Combat Mode
                    changeMode(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });
    }

    private void setupButtons() {
        // Retreat button
        retreatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EcoCombatActivity.this,
                        "Confirmation de repli envoyée", Toast.LENGTH_SHORT).show();
            }
        });

        // Request approved button
        requestApprovedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EcoCombatActivity.this,
                        "Demande validée", Toast.LENGTH_SHORT).show();
            }
        });

        // Request denied button
        requestDeniedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EcoCombatActivity.this,
                        "Demande refusée", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupEcoMode() {
        // Load eco mode state
        ecoModeSwitch.setChecked(true); // Default to true in eco mode

        // Set up switch listener
        ecoModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save eco mode state
                SharedPreferences.Editor editor = missionPrefs.edit();
                editor.putBoolean("eco_mode", isChecked);
                editor.apply();

                if (!isChecked) {
                    switchToNormalMode();
                }
            }
        });
    }

    private void switchToNormalMode() {
        Intent intent = new Intent(this, CombatActivity.class);
        startActivity(intent);
        finish();
    }

    private void changeMode(int position) {
        // Save eco mode state
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.putBoolean("eco_mode", ecoModeSwitch.isChecked());
        editor.putString("previous_page", "eco_combat_mode");
        editor.apply();

        Intent intent;
        switch (position) {
            case 0: // Safe Mode
                intent = new Intent(this, SurActivity.class);
                startActivity(intent);
                finish();
                break;
            case 1: // Reconnaissance Mode
                if (ecoModeSwitch.isChecked()) {
                    intent = new Intent(this, EcoRecoActivity.class);
                } else {
                    intent = new Intent(this, RecoActivity.class);
                }
                startActivity(intent);
                finish();
                break;
        }
    }
}