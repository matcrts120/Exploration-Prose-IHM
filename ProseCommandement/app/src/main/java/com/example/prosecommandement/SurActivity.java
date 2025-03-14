package com.example.prosecommandement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

public class SurActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sur);

        Spinner modeSelector = findViewById(R.id.modeSelector);
        EditText messageInput = findViewById(R.id.messageInput);
        Button sendButton = findViewById(R.id.sendButton);

        // Set up the mode selector spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_options_sur, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(adapter);

        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMode = parent.getItemAtPosition(position).toString();
                Log.i("SurActivity", "Selected mode: " + selectedMode);
                switch (selectedMode) {
                    case "Mode Sûr":
                        // Already in SurActivity
                        break;
                    case "Mode Reconnaissance":
                        startActivity(new Intent(SurActivity.this, RecoActivity.class));
                        break;
                    case "Mode Combat":
                        startActivity(new Intent(SurActivity.this, CombatActivity.class));
                        break;
                    case "Mode Audit":
                        startActivity(new Intent(SurActivity.this, ConfirmationPopupActivity.class));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        sendButton.setOnClickListener(v -> {
            // Handle send message logic here
        });
    }
}
