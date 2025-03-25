package com.example.prosecommandement;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CombatActivity extends AppCompatActivity {

    private RecyclerView conversationRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private VideoView videoView;
    private MediaController mediaController;

    private Spinner modeSelector;
    private EditText messageInput;
    private Button sendButton;
    private Button retreatButton;
    private Button requestApprovedButton;
    private Button requestDeniedButton;
    private SwitchCompat ecoModeSwitch;

    private SharedPreferences missionPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combat);

        // Initialize SharedPreferences
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialize UI components
        initializeViews();
        setupVideoView();
        setupModeSelector();
        setupConversation();
        setupButtons();
        setupEcoMode();

        // Show target detection popup randomly
        showRandomTargetDetectionPopup();
    }

    private void initializeViews() {
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        modeSelector = findViewById(R.id.mode_selector);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);
        retreatButton = findViewById(R.id.btn_retreat);
        requestApprovedButton = findViewById(R.id.btn_request_approved);
        requestDeniedButton = findViewById(R.id.btn_request_denied);
        ecoModeSwitch = findViewById(R.id.eco_mode_switch);
        videoView = findViewById(R.id.videoView);
    }

    private void setupVideoView() {
        // Créer un MediaController pour les contrôles de lecture
        mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        // Attacher le MediaController au VideoView
        videoView.setMediaController(mediaController);

        try {
            // Pour utiliser une vidéo locale stockée dans res/raw
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_thales);

            // Alternativement, pour une vidéo en ligne
            // Uri uri = Uri.parse("https://example.com/videos/surveillance.mp4");

            videoView.setVideoURI(uri);

            // Configurer les événements de la vidéo
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Configuration du lecteur
                    mp.setLooping(true); // Lecture en boucle
                    mp.setVolume(0.5f, 0.5f); // Volume réduit

                    // Démarrer la lecture
                    videoView.start();
                }
            });

            // Gérer les erreurs
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("VideoView", "Erreur de lecture vidéo: " + what + ", " + extra);
                    Toast.makeText(CombatActivity.this,
                            "Erreur de lecture vidéo", Toast.LENGTH_SHORT).show();
                    return true; // L'erreur a été gérée
                }
            });

        } catch (Exception e) {
            Log.e("VideoView", "Erreur d'initialisation de la vidéo", e);
            Toast.makeText(this, "Impossible de charger la vidéo", Toast.LENGTH_SHORT).show();
        }
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

                // Show notification in eco mode if needed
                if (ecoModeSwitch.isChecked()) {
                    switchToEcoMode();
                }
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
        Intent intent = new Intent(this, EcoCombatActivity.class);
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
        // Save current screen as previous page
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.putString("previous_page", "combat_mode");
        editor.apply();

        Intent intent;
        switch (position) {
            case 0: // Safe Mode
                intent = new Intent(this, SurActivity.class);
                startActivity(intent);
                finish();
                break;
            case 1: // Reconnaissance Mode
                intent = new Intent(this, RecoActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void showRandomTargetDetectionPopup() {
        // Randomly show target detection popup (50% chance)
        if (Math.random() > 0.5) {
            // Create dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View customView = getLayoutInflater().inflate(R.layout.popup_target, null);
            builder.setView(customView);

            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);

            // Set up close button
            Button closeButton = customView.findViewById(R.id.close_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            // Show dialog
            dialog.show();
        }
    }

    // Gérer le cycle de vie pour les ressources vidéo
    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null && !videoView.isPlaying()) {
            videoView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}