package com.example.prosecommandementv2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

/**
 * Classe AthenaGUIActivity : gère toute l'interface utilisateur et les transitions entre écrans
 * Cette classe remplace les différentes activités précédentes en une seule classe
 */
public class AthenaGUIActivity extends AppCompatActivity {

    // Constantes pour les modes
    public static final int MODE_SAFE = 0;
    public static final int MODE_RECONNAISSANCE = 1;
    public static final int MODE_COMBAT = 2;
    public static final int MODE_AUDIT = 3;
    public static final int MODE_CONFIG = 4;
    public static final int MODE_SPLASH = 5;

    // Constantes pour les types d'écran (normal ou éco)
    public static final int SCREEN_NORMAL = 0;
    public static final int SCREEN_ECO = 1;

    // Variables pour suivre l'état courant
    private int currentMode = MODE_SPLASH;
    private int currentScreenType = SCREEN_NORMAL;

    // Référence à Athena_Scribe pour la gestion des messages
    private AthenaScribeActivity scribe;

    // Composants UI communs à plusieurs modes
    private RecyclerView conversationRecyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private Spinner modeSelector;
    private EditText messageInput;
    private Button sendButton;
    private Button retreatButton;
    private Button requestApprovedButton;
    private Button requestDeniedButton;
    private SwitchCompat ecoModeSwitch;
    private VideoView videoView;
    private MediaController mediaController;

    // Composants UI spécifiques au mode config
    private EditText teamNameEditText;
    private EditText teamIdEditText;
    private EditText targetInfoEditText;
    private Button validateButton;

    // Composants UI spécifiques au mode audit
    private TextView entitiesCountTextView;
    private TextView alliesIdsTextView;
    private TextView missionStatusTextView;
    private Button endMissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialiser Athena_Scribe
        scribe = AthenaScribeActivity.getInstance();
        scribe.initializePreferences(this);

        // Déterminer le mode initial (splash screen)
        setContentView(R.layout.activity_main);
        currentMode = MODE_SPLASH;

        // Délai pour le splash screen
        new android.os.Handler().postDelayed(() -> {
            checkMissionStatus();
        }, 1500); // 1.5 secondes
    }

    /**
     * Vérifier l'état de la mission et naviguer vers l'écran approprié
     */
    private void checkMissionStatus() {
        if (scribe.isMissionActive()) {
            // Récupérer l'écran précédent
            String previousPage = scribe.getPreviousPage();

            // Déterminer quel écran afficher
            switch (previousPage) {
                case "reconnaissance_mode":
                    switchToMode(MODE_RECONNAISSANCE, SCREEN_NORMAL);
                    break;
                case "eco_reconnaissance_mode":
                    switchToMode(MODE_RECONNAISSANCE, SCREEN_ECO);
                    break;
                case "combat_mode":
                    switchToMode(MODE_COMBAT, SCREEN_NORMAL);
                    break;
                case "eco_combat_mode":
                    switchToMode(MODE_COMBAT, SCREEN_ECO);
                    break;
                default:
                    switchToMode(MODE_SAFE, SCREEN_NORMAL);
                    break;
            }
        } else {
            // Si pas de mission active, aller à l'écran de configuration
            switchToMode(MODE_CONFIG, SCREEN_NORMAL);
        }
    }

    /**
     * Changer de mode d'opération
     * @param newMode Le nouveau mode
     * @param screenType Le type d'écran (normal ou éco)
     */
    private void switchToMode(int newMode, int screenType) {
        // Sauvegarder le mode précédent
        savePreviousMode();

        // Mettre à jour l'état courant
        currentMode = newMode;
        currentScreenType = screenType;

        // Charger le layout approprié
        switch (newMode) {
            case MODE_CONFIG:
                setContentView(R.layout.activity_config);
                initConfigMode();
                break;
            case MODE_SAFE:
                setContentView(R.layout.activity_sur);
                initSafeMode();
                break;
            case MODE_RECONNAISSANCE:
                if (screenType == SCREEN_ECO) {
                    setContentView(R.layout.activity_eco_reco);
                } else {
                    setContentView(R.layout.activity_reco);
                }
                initRecoMode();
                break;
            case MODE_COMBAT:
                if (screenType == SCREEN_ECO) {
                    setContentView(R.layout.activity_eco_combat);
                } else {
                    setContentView(R.layout.activity_combat);
                }
                initCombatMode();
                break;
            case MODE_AUDIT:
                setContentView(R.layout.activity_audit);
                initAuditMode();
                break;
        }
    }

    /**
     * Sauvegarder le mode précédent
     */
    private void savePreviousMode() {
        String previousPage;
        switch (currentMode) {
            case MODE_SAFE:
                previousPage = "safe_mode";
                break;
            case MODE_RECONNAISSANCE:
                previousPage = currentScreenType == SCREEN_ECO ? "eco_reconnaissance_mode" : "reconnaissance_mode";
                break;
            case MODE_COMBAT:
                previousPage = currentScreenType == SCREEN_ECO ? "eco_combat_mode" : "combat_mode";
                break;
            default:
                previousPage = "safe_mode";
                break;
        }
        scribe.savePreviousPage(previousPage);
    }

    /**
     * Initialiser le mode configuration
     */
    private void initConfigMode() {
        // Initialiser les composants UI
        teamNameEditText = findViewById(R.id.team_name);
        teamIdEditText = findViewById(R.id.team_id);
        targetInfoEditText = findViewById(R.id.target_info);
        validateButton = findViewById(R.id.validate_button);

        // Configurer le bouton de validation
        validateButton.setOnClickListener(v -> {
            String teamName = teamNameEditText.getText().toString().trim();
            String teamId = teamIdEditText.getText().toString().trim();
            String targetInfo = targetInfoEditText.getText().toString().trim();

            // Validation simple
            if (teamName.isEmpty() || teamId.isEmpty() || targetInfo.isEmpty()) {
                Toast.makeText(this, getString(R.string.fields_required), Toast.LENGTH_SHORT).show();
                return;
            }

            // Sauvegarder les données de mission
            scribe.saveMissionData(teamName, teamId, targetInfo);

            // Aller au mode sûr
            switchToMode(MODE_SAFE, SCREEN_NORMAL);
        });
    }

    /**
     * Initialiser le mode sûr
     */
    private void initSafeMode() {
        // Initialiser les vues communes
        initializeCommonViews();

        // Configurer le sélecteur de mode
        setupModeSelector(MODE_SAFE);

        // Configurer la conversation
        setupConversation();

        // Configurer les boutons
        setupButtons();
    }

    /**
     * Initialiser le mode reconnaissance
     */
    private void initRecoMode() {
        // Initialiser les vues communes
        initializeCommonViews();

        // Configurer le sélecteur de mode
        setupModeSelector(MODE_RECONNAISSANCE);

        // Configurer la conversation
        setupConversation();

        // Configurer les boutons
        setupButtons();

        // Configurer le mode éco si nécessaire
        if (currentScreenType == SCREEN_NORMAL) {
            setupEcoMode();
        } else {
            ecoModeSwitch = findViewById(R.id.eco_mode_switch);
            ecoModeSwitch.setChecked(true);
            ecoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                scribe.setEcoMode(isChecked);
                if (!isChecked) {
                    switchToMode(MODE_RECONNAISSANCE, SCREEN_NORMAL);
                }
            });
        }

        // Afficher aléatoirement la popup de détection d'entité
        showRandomEntityDetectionPopup();
    }

    /**
     * Initialiser le mode combat
     */
    private void initCombatMode() {
        // Initialiser les vues communes
        initializeCommonViews();

        // Configurer le sélecteur de mode
        setupModeSelector(MODE_COMBAT);

        // Configurer la conversation
        setupConversation();

        // Configurer les boutons
        setupButtons();

        // Configurer le mode éco si nécessaire
        if (currentScreenType == SCREEN_NORMAL) {
            setupEcoMode();
            setupVideoView();
            showRandomTargetDetectionPopup();
        } else {
            ecoModeSwitch = findViewById(R.id.eco_mode_switch);
            ecoModeSwitch.setChecked(true);
            ecoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                scribe.setEcoMode(isChecked);
                if (!isChecked) {
                    switchToMode(MODE_COMBAT, SCREEN_NORMAL);
                }
            });
        }
    }

    /**
     * Initialiser le mode audit
     */
    private void initAuditMode() {
        // Initialiser les composants UI
        entitiesCountTextView = findViewById(R.id.entities_count);
        alliesIdsTextView = findViewById(R.id.allies_ids);
        missionStatusTextView = findViewById(R.id.mission_status);
        endMissionButton = findViewById(R.id.end_mission_button);

        // Charger les données d'audit
        loadAuditData();

        // Configurer le bouton de fin de mission
        endMissionButton.setOnClickListener(v -> {
            showEndMissionConfirmation();
        });
    }

    /**
     * Initialiser les vues communes à plusieurs modes
     */
    private void initializeCommonViews() {
        // Récupérer les références UI communes
        conversationRecyclerView = findViewById(R.id.conversation_recycler_view);
        modeSelector = findViewById(R.id.mode_selector);
        retreatButton = findViewById(R.id.btn_retreat);
        requestApprovedButton = findViewById(R.id.btn_request_approved);
        requestDeniedButton = findViewById(R.id.btn_request_denied);

        // Les vues qui peuvent ne pas être présentes dans tous les modes
        try {
            messageInput = findViewById(R.id.message_input);
            sendButton = findViewById(R.id.send_button);
            ecoModeSwitch = findViewById(R.id.eco_mode_switch);
        } catch (Exception e) {
            // Certains de ces éléments peuvent ne pas être présents dans certains layouts
        }
    }

    /**
     * Configurer le sélecteur de mode
     * @param defaultMode Le mode par défaut à sélectionner
     */
    private void setupModeSelector(int defaultMode) {
        ArrayAdapter<CharSequence> adapter;

        // Créer l'adaptateur pour le spinner selon le mode
        if (defaultMode == MODE_SAFE) {
            adapter = ArrayAdapter.createFromResource(
                    this, R.array.operation_modes_sur, android.R.layout.simple_spinner_item);
        } else {
            adapter = ArrayAdapter.createFromResource(
                    this, R.array.operation_modes, android.R.layout.simple_spinner_item);
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelector.setAdapter(adapter);

        // Définir la sélection par défaut
        modeSelector.setSelection(defaultMode);

        // Configurer le listener pour les changements de mode
        modeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != defaultMode) {
                    handleModeChange(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Rien à faire
            }
        });
    }

    /**
     * Gérer le changement de mode via le spinner
     * @param position La position sélectionnée dans le spinner
     */
    private void handleModeChange(int position) {
        if (currentMode == MODE_SAFE) {
            switch (position) {
                case MODE_RECONNAISSANCE:
                    switchToMode(MODE_RECONNAISSANCE, SCREEN_NORMAL);
                    break;
                case MODE_COMBAT:
                    switchToMode(MODE_COMBAT, SCREEN_NORMAL);
                    break;
                case 3: // Position spéciale pour le mode audit dans le spinner du mode sûr
                    showConfirmationPopup();
                    break;
            }
        } else if (currentMode == MODE_RECONNAISSANCE) {
            switch (position) {
                case MODE_SAFE:
                    switchToMode(MODE_SAFE, SCREEN_NORMAL);
                    break;
                case MODE_COMBAT:
                    if (currentScreenType == SCREEN_ECO) {
                        switchToMode(MODE_COMBAT, SCREEN_ECO);
                    } else {
                        switchToMode(MODE_COMBAT, SCREEN_NORMAL);
                    }
                    break;
            }
        } else if (currentMode == MODE_COMBAT) {
            switch (position) {
                case MODE_SAFE:
                    switchToMode(MODE_SAFE, SCREEN_NORMAL);
                    break;
                case MODE_RECONNAISSANCE:
                    if (currentScreenType == SCREEN_ECO) {
                        switchToMode(MODE_RECONNAISSANCE, SCREEN_ECO);
                    } else {
                        switchToMode(MODE_RECONNAISSANCE, SCREEN_NORMAL);
                    }
                    break;
            }
        }
    }

    /**
     * Configurer la conversation
     */
    private void setupConversation() {
        // Utiliser la liste partagée de messages
        messageList = scribe.getMessages();

        // Configurer le RecyclerView
        messageAdapter = new MessageAdapter(messageList);
        conversationRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        conversationRecyclerView.setAdapter(messageAdapter);
    }

    /**
     * Configurer les boutons communs
     */
    private void setupButtons() {
        // Configurer le bouton d'envoi de message s'il existe
        if (sendButton != null && messageInput != null) {
            sendButton.setOnClickListener(v -> {
                String messageContent = messageInput.getText().toString().trim();
                if (!messageContent.isEmpty()) {
                    sendMessage(messageContent);
                    messageInput.setText("");
                }
            });
        }

        // Configurer le bouton de repli
        retreatButton.setOnClickListener(v -> {
            sendMessage("Demande de repli.");

            // Afficher la notification en mode éco si nécessaire
            if (ecoModeSwitch != null && ecoModeSwitch.isChecked()) {
                scribe.setEcoMode(true);
                if (currentMode == MODE_RECONNAISSANCE) {
                    switchToMode(MODE_RECONNAISSANCE, SCREEN_ECO);
                } else if (currentMode == MODE_COMBAT) {
                    switchToMode(MODE_COMBAT, SCREEN_ECO);
                }
            }
        });

        // Configurer le bouton d'approbation
        requestApprovedButton.setOnClickListener(v -> {
            sendMessage("Demande approvée.");
        });

        // Configurer le bouton de refus
        requestDeniedButton.setOnClickListener(v -> {
            sendMessage("Demande refusée.");
        });
    }

    /**
     * Configurer le mode éco
     */
    private void setupEcoMode() {
        if (ecoModeSwitch != null) {
            // Charger l'état du mode éco
            ecoModeSwitch.setChecked(scribe.isEcoMode());

            // Configurer le listener du switch
            ecoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                scribe.setEcoMode(isChecked);

                if (isChecked) {
                    if (currentMode == MODE_RECONNAISSANCE) {
                        switchToMode(MODE_RECONNAISSANCE, SCREEN_ECO);
                    } else if (currentMode == MODE_COMBAT) {
                        switchToMode(MODE_COMBAT, SCREEN_ECO);
                    }
                }
            });
        }
    }

    /**
     * Configurer la vue vidéo pour le mode combat
     */
    private void setupVideoView() {
        try {
            videoView = findViewById(R.id.videoView);
            if (videoView != null) {
                // Créer un MediaController pour les contrôles de lecture
                mediaController = new MediaController(this);
                mediaController.setAnchorView(videoView);

                // Attacher le MediaController au VideoView
                videoView.setMediaController(mediaController);

                try {
                    // Utiliser directement la vidéo dans res/raw
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.video_thales);
                    videoView.setVideoURI(uri);

                    // Configurer les événements de la vidéo
                    videoView.setOnPreparedListener(mp -> {
                        mp.setLooping(true); // Lecture en boucle
                        mp.setVolume(0.5f, 0.5f); // Volume réduit
                        videoView.start();
                    });

                    // Gérer les erreurs
                    videoView.setOnErrorListener((mp, what, extra) -> {
                        Log.e("VideoView", "Erreur de lecture vidéo: " + what + ", " + extra);
                        Toast.makeText(AthenaGUIActivity.this,
                                "Erreur de lecture vidéo", Toast.LENGTH_SHORT).show();
                        return true; // L'erreur a été gérée
                    });

                } catch (Exception e) {
                    Log.e("VideoView", "Erreur d'initialisation de la vidéo", e);
                    Toast.makeText(AthenaGUIActivity.this,
                            "Impossible de charger la vidéo", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e("VideoView", "Erreur dans setupVideoView", e);
        }
    }

    /**
     * Envoyer un message
     * @param content Le contenu du message
     */
    private void sendMessage(String content) {
        // Envoyer le message via Athena_Scribe
        scribe.sendCommandMessage(content);

        // Mettre à jour l'UI
        int lastPosition = messageList.size() - 1;
        messageAdapter.notifyItemInserted(lastPosition);

        // Faire défiler jusqu'au bas
        conversationRecyclerView.smoothScrollToPosition(lastPosition);
    }

    /**
     * Charger les données d'audit
     */
    private void loadAuditData() {
        // Récupérer l'ID de l'équipe
        String teamId = scribe.getTeamId();
        alliesIdsTextView.setText(teamId);

        // Définir le nombre d'entités (statique pour la démo)
        entitiesCountTextView.setText("2");

        // Définir le statut de la mission
        missionStatusTextView.setText("Terminée");
    }

    /**
     * Afficher la confirmation de fin de mission
     */
    private void showEndMissionConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.mission_ended);
        builder.setMessage(R.string.mission_ended);
        builder.setPositiveButton(R.string.confirm, (dialog, which) -> {
            // Réinitialiser les données de mission
            scribe.endMission();

            // Revenir à l'écran principal
            switchToMode(MODE_CONFIG, SCREEN_NORMAL);
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    /**
     * Afficher la confirmation pour entrer en mode audit
     */
    private void showConfirmationPopup() {
        // Créer la boîte de dialogue
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.popup_confirmation, null);
        builder.setView(customView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        // Configurer le bouton de fermeture
        Button closeButton = customView.findViewById(R.id.cancel_button);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Button confirmButton = customView.findViewById(R.id.confirm_button);
        confirmButton.setOnClickListener(v -> {
            dialog.dismiss();
            switchToMode(MODE_AUDIT, SCREEN_NORMAL);
        });

        // Afficher la boîte de dialogue
        dialog.show();
    }

    /**
     * Afficher aléatoirement la popup de détection de cible
     */
    private void showRandomTargetDetectionPopup() {
        // Vérifier si on affiche le popup (50% de chance)
        if (Math.random() > 0.5) {
            // Créer dialog
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View customView = getLayoutInflater().inflate(R.layout.popup_target, null);
            builder.setView(customView);

            final AlertDialog dialog = builder.create();
            dialog.setCancelable(false);

            // Référence au conteneur d'image dans la popup
            FrameLayout photoContainer = customView.findViewById(R.id.photo_container);

            // Créer une ImageView pour afficher la photo
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Charger l'image directement depuis res/drawable (par exemple target_image)
            // Vous devrez vous assurer que cette ressource existe
            imageView.setImageResource(R.drawable.target_photo);

            // Ajouter l'ImageView au conteneur
            photoContainer.removeAllViews();
            photoContainer.addView(imageView);

            // Configurer le bouton de fermeture
            Button closeButton = customView.findViewById(R.id.close_button);
            closeButton.setOnClickListener(v -> dialog.dismiss());

            // Afficher le dialog
            dialog.show();
        }
    }

    /**
     * Afficher aléatoirement la popup de détection d'entité
     */
    private void showRandomEntityDetectionPopup() {
        // Afficher aléatoirement la popup
        Random random = new Random();
        int chance = random.nextInt(3); // 0, 1, ou 2

        if (chance == 0) {
            // Afficher la popup d'entité inconnue
            showEntityDetectionPopup();
        } else if (chance == 1) {
            // Afficher la popup d'allié
            showAllyDetectionPopup();
        }
    }

    /**
     * Afficher la popup de détection d'entité inconnue
     */
    private void showEntityDetectionPopup() {
        // Créer dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.popup_entity, null);
        builder.setView(customView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        // Configurer les boutons
        Button ignoreButton = customView.findViewById(R.id.ignore_button);
        ignoreButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Button sureButton = customView.findViewById(R.id.sure_button);
        sureButton.setOnClickListener(v -> {
            dialog.dismiss();
            switchToMode(MODE_SAFE, SCREEN_NORMAL);
        });

        Button combatButton = customView.findViewById(R.id.combat_button);
        combatButton.setOnClickListener(v -> {
            dialog.dismiss();
            switchToMode(MODE_COMBAT, SCREEN_NORMAL);
        });

        // Afficher le dialog
        dialog.show();
    }

    /**
     * Afficher la popup de détection d'allié
     */
    private void showAllyDetectionPopup() {
        // Créer dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = getLayoutInflater().inflate(R.layout.popup_allie, null);
        builder.setView(customView);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        // Configurer le bouton de fermeture
        Button closeButton = customView.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // Afficher le dialog
        dialog.show();
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