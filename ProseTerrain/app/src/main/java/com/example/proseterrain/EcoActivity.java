package com.example.proseterrain;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EcoActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private Button endMissionButton;
    private Button requestBackupButton;
    private Button retreatButton;
    private Button btnReturn;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupConversation();
        setupButtons();
    }

    private void initializeViews() {
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        endMissionButton = findViewById(R.id.btn_end_mission);
        requestBackupButton = findViewById(R.id.btn_request_backup);
        retreatButton = findViewById(R.id.btn_retreat);
        btnReturn = findViewById(R.id.btn_return);
    }

    private void setupButtons() {
        // Bouton de fin de mission
        endMissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndMissionDialog();
            }
        });

        // Bouton de demande de renfort
        requestBackupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Demande de renfort.");
            }
        });

        // Bouton de repli
        retreatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Demande de repli.");
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeEco();
            }
        });
    }

    private void setupConversation() {
        // Utiliser la liste partagée de messages
        messageList = MessageManager.getInstance().getMessages();

        // Set up RecyclerView avec l'orientation standard (du haut vers le bas)
        messageAdapter = new MessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        // Ne pas utiliser setStackFromEnd(true) pour conserver le comportement standard
        conversationRecyclerView.setLayoutManager(layoutManager);
        conversationRecyclerView.setAdapter(messageAdapter);

        // Faites défiler jusqu'au dernier message si la liste n'est pas vide
        if (!messageList.isEmpty()) {
            conversationRecyclerView.post(() -> {
                conversationRecyclerView.scrollToPosition(messageList.size() - 1);
            });
        }
    }

    private void sendMessage(String content) {
        // Get current time
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Get team ID
        String teamName = missionPrefs.getString("team_name", "ID Team");

        // Create message
        Message message = new Message(teamName, content, currentTime);

        // Add to shared message list
        MessageManager.getInstance().addMessage(message);

        // Update UI
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Scroll to the bottom
        conversationRecyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void showEndMissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.end_mission);
        builder.setMessage(R.string.end_mission_confirmation);

        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            // Reset mission data
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.clear();
            editor.apply();

            // Return to main screen
            Intent intent = new Intent(EcoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void modeEco() {
        // Eco screen
        Intent intent = new Intent(EcoActivity.this, TransmissionActivity.class);
        startActivity(intent);
        finish();
    }
}