package com.example.prosecommandement;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SurActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    private Spinner modeSelector;
    private EditText messageInput;
    private Button sendButton;
    private Button retreatButton;
    private Button requestApprovedButton;
    private Button requestDeniedButton;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sur);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupModeSelector();
        setupConversation();
        setupButtons();
    }

    private void initializeViews() {
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        modeSelector = findViewById(R.id.mode_selector);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        retreatButton = findViewById(R.id.btn_retreat);
        requestApprovedButton = findViewById(R.id.btn_request_approved);
        requestDeniedButton = findViewById(R.id.btn_request_denied);
    }

    private void setupModeSelector() {
        // Create adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.operation_modes_sur, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(adapter);

        // Set default selection to Safe Mode (index 0)
        modeSelector.setSelection(0);

        // Set up mode change listener
        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) { // If not Safe Mode
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

        // Clear input field
        messageInput.setText("");
    }

    private void showConfirmationPopup() {
        // Create dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.popup_confirmation, null);
        builder.setView(customView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        // Set up close button
        Button closeButton = customView.findViewById(R.id.cancel_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button surButton = customView.findViewById(R.id.confirm_button);
        surButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SurActivity.this, AuditActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Show dialog
        dialog.show();
    }

    private void changeMode(int position) {
        // Save current screen as previous page
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.putString("previous_page", "safe_mode");
        editor.apply();

        Intent intent;
        switch (position) {
            case 1: // Reconnaissance Mode
                intent = new Intent(this, RecoActivity.class);
                startActivity(intent);
                break;
            case 2: // Combat Mode
                intent = new Intent(this, CombatActivity.class);
                startActivity(intent);
                break;
            case 3: // Audit Mode
                showConfirmationPopup();
                break;
        }
    }
}