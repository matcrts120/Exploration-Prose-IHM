package com.example.prosecommandement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ConfirmationPopupActivity extends AppCompatActivity {

    private Button btnPasserAudit;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_popup);

        btnPasserAudit = findViewById(R.id.btnPasserAudit);

        // Listener pour le bouton de passage en mode audit
        btnPasserAudit.setOnClickListener(v -> showConfirmationDialog());
    }

    // Affiche la popup de confirmation
    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Êtes-vous sûr de vouloir passer en mode audit ? Cela mettra fin à la mission.")
                .setPositiveButton("Confirmer", (dialog, which) -> {
                    // Redirige vers l'écran d'audit
                    startActivity(new Intent(ConfirmationPopupActivity.this, AuditActivity.class));
                })
                .setNegativeButton("Annuler", (dialog, which) -> {
                    // Redirige vers l'écran précédent (par exemple, l'écran de mission)
                    startActivity(new Intent(ConfirmationPopupActivity.this, SurActivity.class));
                })
                .setCancelable(false) // Empêche de fermer la popup en cliquant à l'extérieur
                .show();
    }
}
