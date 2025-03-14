package com.example.prosecommandement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class RecoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);

        Spinner modeSelector = findViewById(R.id.modeSelector);
        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);
        SwitchCompat ecoModeSwitch = findViewById(R.id.ecoModeSwitch);

        // Set up the mode selector spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_options_reco, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(adapter);

        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMode = parent.getItemAtPosition(position).toString();
                switch (selectedMode) {
                    case "Mode Reconnaissance":
                        // Already in RecoActivity
                        break;
                    case "Mode Sûr":
                        startActivity(new Intent(RecoActivity.this, SurActivity.class));
                        break;
                    case "Mode Combat":
                        startActivity(new Intent(RecoActivity.this, CombatActivity.class));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Listener for the Eco Mode Switch
        ecoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("ecoMode", isChecked)
                    .apply();

            if (isChecked) {
                // Switch to EcoRecoActivity
                startActivity(new Intent(RecoActivity.this, EcoRecoActivity.class));
                finish();  // Close RecoActivity to avoid the user returning to it
            }
        });

        sendButton.setOnClickListener(v -> {
            // Handle send message logic here
        });
    }
}
