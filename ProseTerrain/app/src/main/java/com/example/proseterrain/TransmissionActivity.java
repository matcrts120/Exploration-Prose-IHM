package com.example.proseterrain;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TransmissionActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private EditText messageInput;
    private Button sendButton;
    private Button endMissionButton;
    private Button requestBackupButton;
    private Button retreatButton;
    private Button btnEco;
    private SharedPreferences missionPrefs;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transmission);

        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialisation des vues
        initializeViews();
        setupConversation();
        setupButtons();

        // Simuler une détection d'entité aléatoire
        simulateRandomEntityDetection();
    }

    private void initializeViews() {
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        endMissionButton = findViewById(R.id.btn_end_mission);
        requestBackupButton = findViewById(R.id.btn_request_backup);
        retreatButton = findViewById(R.id.btn_retreat);
        btnEco = findViewById(R.id.btn_eco);
    }

    private void setupConversation() {
        // Utiliser la liste partagée de messages
        messageList = MessageManager.getInstance().getMessages();

        // Set up RecyclerView avec l'orientation standard (du haut vers le bas)
        messageAdapter = new MessageAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        conversationRecyclerView.setLayoutManager(layoutManager);
        conversationRecyclerView.setAdapter(messageAdapter);

        // Faites défiler jusqu'au dernier message si la liste n'est pas vide
        if (!messageList.isEmpty()) {
            conversationRecyclerView.post(() -> {
                conversationRecyclerView.scrollToPosition(messageList.size() - 1);
            });
        }
    }

    private void setupButtons() {
        // Bouton d'envoi de message
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageContent = messageInput.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    sendMessage(messageContent);
                }
            }
        });

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

        btnEco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modeEco();
            }
        });
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
            // Réinitialiser les données de mission
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.clear();
            editor.apply();

            // Retourner à l'écran principal
            Intent intent = new Intent(TransmissionActivity.this, FinMissionActivity.class);
            startActivity(intent);
            finish();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void simulateRandomEntityDetection() {
        // 50% de chance de détecter une entité
        if (random.nextFloat() < 0.5f) {
            showEnemyDialog();
        }
    }

    private void showEnemyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.popup_entities, null);
        builder.setView(customView);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        Button closeButton = customView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void modeEco() {
        // Ecran Eco
        Intent intent = new Intent(TransmissionActivity.this, EcoActivity.class);
        startActivity(intent);
        finish();
    }

}