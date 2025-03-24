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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EcoRecoActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private Spinner modeSelector;
    private Button retreatButton;
    private Button requestApprovedButton;
    private Button requestDeniedButton;
    private SwitchCompat ecoModeSwitch;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_reco);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupModeSelector();
        setupConversation();
        setupButtons();
        setupEcoMode();
    }

    private void initializeViews() {
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view);
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

        // Set default selection to Reconnaissance Mode (index 1)
        modeSelector.setSelection(1);

        // Set up mode change listener
        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 1) { // If not Reconnaissance Mode
                    changeMode(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });
    }

    private void setupConversation() {
        // Utiliser la liste partagée de messages
        messageList = MessageManager.getInstance().getMessages();

        // Set up RecyclerView
        messageAdapter = new MessageAdapter(messageList);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversationRecyclerView.setAdapter(messageAdapter);
    }

    private void setupButtons() {
        // Retreat button
        retreatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Demande de repli.");
            }
        });

        // Request approved button
        requestApprovedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Demande approvée.");
            }
        });

        // Request refused button
        requestDeniedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Demande refusée.");
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
        Intent intent = new Intent(this, RecoActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendMessage(String content) {
        // Get current time
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Get team ID
        String teamId = missionPrefs.getString("team_id", "ID Team");

        // Create message
        Message message = new Message("Commandement", content, currentTime);

        // Add to shared message list
        MessageManager.getInstance().addMessage(message);

        // Update UI
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Scroll to the bottom
        conversationRecyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void changeMode(int position) {
        // Save eco mode state
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.putBoolean("eco_mode", ecoModeSwitch.isChecked());
        editor.putString("previous_page", "eco_reconnaissance_mode");
        editor.apply();

        Intent intent;
        switch (position) {
            case 0: // Safe Mode
                intent = new Intent(this, SurActivity.class);
                startActivity(intent);
                finish();
                break;
            case 2: // Combat Mode
                if (ecoModeSwitch.isChecked()) {
                    intent = new Intent(this, EcoCombatActivity.class);
                } else {
                    intent = new Intent(this, CombatActivity.class);
                }
                startActivity(intent);
                finish();
                break;
        }
    }
}