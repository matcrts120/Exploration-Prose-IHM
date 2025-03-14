package com.example.prosecommandement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AuditActivity extends AppCompatActivity {

    private TextView entitiesCount, alliesIds, missionStatus;
    private Button closeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audit);

        // Initialisation des éléments de l'UI
        entitiesCount = findViewById(R.id.entitiesCount);
        alliesIds = findViewById(R.id.alliesIds);
        missionStatus = findViewById(R.id.missionStatus);
        closeButton = findViewById(R.id.closeButton);

        // Exemple de données (tu peux les récupérer dynamiquement si besoin)
        entitiesCount.setText("2");
        alliesIds.setText("12");
        missionStatus.setText("Terminée");

        // Action sur le bouton "Terminer la mission"
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AuditActivity.this, "Fin de la mission", Toast.LENGTH_SHORT).show();
                finish(); // Ferme l'activité
            }
        });
    }
}
