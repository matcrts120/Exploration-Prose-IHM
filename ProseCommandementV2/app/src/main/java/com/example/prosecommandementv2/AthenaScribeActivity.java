package com.example.prosecommandementv2;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Classe AthenaScribeActivity : gère tout ce qui concerne les messages et conversations
 * Cette classe remplace l'ancien MessageManager et intègre la logique de messagerie
 * des autres activités
 */
public class AthenaScribeActivity {
    // Singleton pattern
    private static AthenaScribeActivity instance;

    // Liste des messages
    private List<Message> messages;

    // SharedPreferences pour stocker les informations de mission
    private SharedPreferences missionPrefs;

    /**
     * Constructeur privé (pattern Singleton)
     */
    private AthenaScribeActivity() {
        messages = new ArrayList<>();
        // Ajouter des messages par défaut
        messages.add(new Message("ID Allié", "La cible est en ligne de mire", "12:30"));
        messages.add(new Message("ID Allié", "Demande autorisation de tirer", "12:32"));
    }

    /**
     * Initialiser les préférences
     * @param context Contexte de l'application
     */
    public void initializePreferences(Context context) {
        if (missionPrefs == null) {
            missionPrefs = context.getSharedPreferences("MissionPrefs", Context.MODE_PRIVATE);
        }
    }

    /**
     * Obtenir l'instance unique de AthenaScribeActivity
     * @return L'instance de AthenaScribeActivity
     */
    public static synchronized AthenaScribeActivity getInstance() {
        if (instance == null) {
            instance = new AthenaScribeActivity();
        }
        return instance;
    }

    /**
     * Obtenir la liste des messages
     * @return La liste des messages
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Ajouter un message à la conversation
     * @param message Le message à ajouter
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * Créer et ajouter un nouveau message de commandement
     * @param content Contenu du message
     */
    public void sendCommandMessage(String content) {
        // Obtenir l'heure actuelle
        String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        // Créer un nouveau message
        Message message = new Message("Commandement", content, currentTime);

        // Ajouter le message
        addMessage(message);
    }

    /**
     * Réinitialiser la liste des messages
     */
    public void reset() {
        messages.clear();
    }

    /**
     * Sauvegarder les données de mission
     * @param teamName Nom de l'équipe
     * @param teamId ID de l'équipe
     * @param targetInfo Informations sur la cible
     */
    public void saveMissionData(String teamName, String teamId, String targetInfo) {
        if (missionPrefs != null) {
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.putString("team_name", teamName);
            editor.putString("team_id", teamId);
            editor.putString("target_info", targetInfo);
            editor.putBoolean("mission_active", true);
            editor.apply();
        }
    }

    /**
     * Mettre à jour l'état du mode éco
     * @param ecoMode État du mode éco
     */
    public void setEcoMode(boolean ecoMode) {
        if (missionPrefs != null) {
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.putBoolean("eco_mode", ecoMode);
            editor.apply();
        }
    }

    /**
     * Vérifier si le mode éco est activé
     * @return true si le mode éco est activé, false sinon
     */
    public boolean isEcoMode() {
        return missionPrefs != null && missionPrefs.getBoolean("eco_mode", false);
    }

    /**
     * Sauvegarder l'écran précédent
     * @param previousPage Nom de l'écran précédent
     */
    public void savePreviousPage(String previousPage) {
        if (missionPrefs != null) {
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.putString("previous_page", previousPage);
            editor.apply();
        }
    }

    /**
     * Obtenir l'écran précédent
     * @return Le nom de l'écran précédent
     */
    public String getPreviousPage() {
        return missionPrefs != null ? missionPrefs.getString("previous_page", "safe_mode") : "safe_mode";
    }

    /**
     * Vérifier si une mission est active
     * @return true si une mission est active, false sinon
     */
    public boolean isMissionActive() {
        return missionPrefs != null && missionPrefs.getBoolean("mission_active", false);
    }

    /**
     * Terminer la mission actuelle
     */
    public void endMission() {
        if (missionPrefs != null) {
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.clear();
            editor.apply();
        }
        reset();
    }

    /**
     * Obtenir l'ID de l'équipe
     * @return L'ID de l'équipe
     */
    public String getTeamId() {
        return missionPrefs != null ? missionPrefs.getString("team_id", "12") : "12";
    }

    /**
     * Obtenir le nom de l'équipe
     * @return Le nom de l'équipe
     */
    public String getTeamName() {
        return missionPrefs != null ? missionPrefs.getString("team_name", "") : "";
    }

    /**
     * Obtenir les informations sur la cible
     * @return Les informations sur la cible
     */
    public String getTargetInfo() {
        return missionPrefs != null ? missionPrefs.getString("target_info", "") : "";
    }
}