package com.example.proseterrain;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MissionActivity extends AppCompatActivity {

    private SharedPreferences missionPrefs;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission);

        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Initialiser les vues et afficher les informations de mission
        initializeViews();

        // Configuration du bouton pour fermer l'écran de récapitulatif
        closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Enregistrer l'écran de transmission comme écran actuel
                SharedPreferences.Editor editor = missionPrefs.edit();
                editor.putString("current_screen", "transmission");
                editor.apply();

                // Rediriger vers l'écran de transmission
                Intent intent = new Intent(MissionActivity.this, TransmissionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initializeViews() {
        // Récupérer les références aux TextView
        TextView teamNameText = findViewById(R.id.team_name);
        TextView teamIdText = findViewById(R.id.team_id);
        TextView targetInfoText = findViewById(R.id.target_info);
        TextView missionLocationText = findViewById(R.id.mission_location);
        TextView missionTimeText = findViewById(R.id.mission_time);

        // Récupérer ou initialiser les données de mission
        String teamName = missionPrefs.getString("team_name", "Équipe Alpha");
        String teamId = missionPrefs.getString("team_id", "A001");
        String targetInfo = missionPrefs.getString("target_info", "Reconnaissance");
        String missionLocation = missionPrefs.getString("mission_location", "Zone Nord");

        // Si la date/heure n'est pas encore enregistrée, l'enregistrer maintenant
        String missionTime = missionPrefs.getString("mission_time", null);
        if (missionTime == null) {
            missionTime = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            SharedPreferences.Editor editor = missionPrefs.edit();
            editor.putString("mission_time", missionTime);
            editor.apply();
        }

        // Afficher les données dans les TextView
        teamNameText.setText(teamName);
        teamIdText.setText(teamId);
        targetInfoText.setText(targetInfo);
        missionLocationText.setText(missionLocation);
        missionTimeText.setText(missionTime);
    }
}