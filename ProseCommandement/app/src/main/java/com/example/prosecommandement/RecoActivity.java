package com.example.prosecommandement;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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

public class RecoActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private Spinner modeSelector;
    private EditText messageInput;
    private Button sendButton;
    private Button endMissionButton;
    private Button requestBackupButton;
    private Button retreatButton;
    private SwitchCompat ecoModeSwitch;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reco);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupModeSelector();
        setupConversation();
        setupButtons();
        setupEcoMode();

        showRandomEntityDetectionPopup();
    }

    private void initializeViews() {
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        modeSelector = findViewById(R.id.mode_selector);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        endMissionButton = findViewById(R.id.btn_end_mission);
        requestBackupButton = findViewById(R.id.btn_request_backup);
        retreatButton = findViewById(R.id.btn_retreat);
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
        // Initialize message list and adapter
        messageList = new ArrayList<>();

        // Add some sample messages
        messageList.add(new Message("ID Allié", "La cible est en ligne de mire", "12:30"));
        messageList.add(new Message("ID Allié", "Demande autorisation de tirer", "12:32"));

        // Set up RecyclerView
        messageAdapter = new MessageAdapter(messageList);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversationRecyclerView.setAdapter(messageAdapter);
    }

    private void setupButtons() {
        // Send message button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = messageInput.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    sendMessage(messageContent);
                }
            }
        });

        // End mission button
        endMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });

        // Request backup button
        requestBackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSystemMessage("Demande de renfort envoyée.");
            }
        });

        // Retreat button
        retreatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSystemMessage("Demande de repli envoyée.");

                // Show notification in eco mode if needed
                if (ecoModeSwitch.isChecked()) {
                    switchToEcoMode();
                }
            }
        });
    }

    private void setupEcoMode() {
        // Load eco mode state
        boolean ecoMode = missionPrefs.getBoolean("eco_mode", false);
        ecoModeSwitch.setChecked(ecoMode);

        // Set up switch listener
        ecoModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Save eco mode state
                SharedPreferences.Editor editor = missionPrefs.edit();
                editor.putBoolean("eco_mode", isChecked);
                editor.apply();

                if (isChecked) {
                    switchToEcoMode();
                }
            }
        });
    }

    private void switchToEcoMode() {
        Intent intent = new Intent(this, EcoRecoActivity.class);
        startActivity(intent);
        finish();
    }

    private void sendMessage(String content) {
        // Get current time
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Get team ID
        String teamId = missionPrefs.getString("team_id", "ID Team");

        // Create and add message
        Message message = new Message(teamId, content, currentTime);
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Scroll to the bottom
        conversationRecyclerView.smoothScrollToPosition(messageList.size() - 1);

        // Clear input field
        messageInput.setText("");
    }

    private void sendSystemMessage(String content) {
        // Get current time
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Create and add system message
        Message message = new Message("Système", content, currentTime);
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Scroll to the bottom
        conversationRecyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.end_mission);
        builder.setMessage("Êtes-vous sûr de vouloir terminer la mission ?");
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            // End mission and go to config screen
            Intent intent = new Intent(RecoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void changeMode(int position) {
        // Save current screen as previous page
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.putString("previous_page", "reconnaissance_mode");
        editor.apply();

        Intent intent;
        switch (position) {
            case 0: // Safe Mode
                intent = new Intent(this, SurActivity.class);
                startActivity(intent);
                finish();
                break;
            case 2: // Combat Mode
                intent = new Intent(this, CombatActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void showRandomEntityDetectionPopup() {
        // Randomly show target detection popup (50% chance)
        if (Math.random() > 0.5) {
            // Create dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View customView = getLayoutInflater().inflate(R.layout.popup_entity, null);
            builder.setView(customView);

            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);

            // Set up close button
            Button closeButton = customView.findViewById(R.id.ignore_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            Button surButton = customView.findViewById(R.id.sure_button);
            surButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecoActivity.this, SurActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            Button combatButton = customView.findViewById(R.id.combat_button);
            combatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RecoActivity.this, CombatActivity.class);
                    startActivity(intent);
                    finish();
                }
            });

            // Show dialog
            dialog.show();
        }
    }
}