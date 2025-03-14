package com.example.prosecommandement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class CombatActivity extends AppCompatActivity {

    private Spinner modeSelector;
    private EditText messageInput;
    private Button sendButton;
    private Switch ecoModeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combat);

        modeSelector = findViewById(R.id.modeSelector);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        ecoModeSwitch = findViewById(R.id.ecoModeSwitch);

        // Set up the mode selector spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_options_combat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(adapter);

        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMode = parent.getItemAtPosition(position).toString();
                switch (selectedMode) {
                    case "Mode Combat":
                        // Already in CombatActivity
                        break;
                    case "Mode Sûr":
                        startActivity(new Intent(CombatActivity.this, SurActivity.class));
                        break;
                    case "Mode Reconnaissance":
                        startActivity(new Intent(CombatActivity.this, RecoActivity.class));
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
            if (isChecked) {
                // Switch to EcoCombatActivity
                startActivity(new Intent(CombatActivity.this, EcoCombatActivity.class));
                finish();  // Close CombatActivity to avoid the user returning to it
            }
        });

        sendButton.setOnClickListener(v -> {
            // Handle send message logic here
        });
    }
}
