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
    }

    private void setupConversation() {
        // Initialiser la liste de messages et l'adaptateur
        messageList = new ArrayList<>();

        // Ajouter quelques messages d'exemple
        messageList.add(new Message("ID Allié", "La cible est en ligne de mire", "12:30"));
        messageList.add(new Message("ID Allié", "Demande autorisation de tirer", "12:32"));

        // Configurer le RecyclerView
        messageAdapter = new MessageAdapter(messageList);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversationRecyclerView.setAdapter(messageAdapter);
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
                sendSystemMessage("Demande de renfort envoyée.");
                Toast.makeText(TransmissionActivity.this, "Demande de renfort envoyée au commandement", Toast.LENGTH_SHORT).show();
            }
        });

        // Bouton de repli
        retreatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSystemMessage("Demande de repli envoyée.");
                Toast.makeText(TransmissionActivity.this, "Demande de repli envoyée au commandement", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessage(String content) {
        // Obtenir l'heure actuelle
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Obtenir l'ID de l'équipe
        String teamId = missionPrefs.getString("team_id", "ID Team");

        // Créer et ajouter le message
        Message message = new Message(teamId, content, currentTime);
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Faire défiler vers le bas
        conversationRecyclerView.smoothScrollToPosition(messageList.size() - 1);

        // Effacer le champ de saisie
        messageInput.setText("");
    }

    private void sendSystemMessage(String content) {
        // Obtenir l'heure actuelle
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Créer et ajouter le message système
        Message message = new Message("Système", content, currentTime);
        messageList.add(message);
        messageAdapter.notifyItemInserted(messageList.size() - 1);

        // Faire défiler vers le bas
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
            Intent intent = new Intent(TransmissionActivity.this, MainActivity.class);
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
}