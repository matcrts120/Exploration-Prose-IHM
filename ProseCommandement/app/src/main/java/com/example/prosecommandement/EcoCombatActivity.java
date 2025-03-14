package com.example.prosecommandement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatActivity;

public class EcoCombatActivity extends AppCompatActivity {

    private Switch ecoModeSwitch;
    private Spinner modeSelect;
    private Button btnRepli, btnValider, btnRefuser;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eco_combat);

        // Initialisation des éléments de l'UI
        ecoModeSwitch = findViewById(R.id.ecoModeSwitch);
        modeSelect = findViewById(R.id.modeSelect);
        btnRepli = findViewById(R.id.btnRepli);
        btnValider = findViewById(R.id.btnValider);
        btnRefuser = findViewById(R.id.btnRefuser);

        // Initialisation du spinner (menu déroulant)
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode_options_combat, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSelect.setAdapter(adapter);

        // Récupération de l'état du Mode Éco
        boolean isEcoMode = getSharedPreferences("settings", MODE_PRIVATE)
                .getBoolean("ecoMode", true);
        ecoModeSwitch.setChecked(isEcoMode);

        // Listener pour le changement de mode
        modeSelect.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: // Mode Reconnaissance
                        startActivity(new Intent(EcoCombatActivity.this, EcoRecoActivity.class));
                        break;
                    case 2: // Mode Sûr
                        startActivity(new Intent(EcoCombatActivity.this, SurActivity.class));
                        break;
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        // Listener pour le switch Mode Éco
        ecoModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("settings", MODE_PRIVATE)
                    .edit()
                    .putBoolean("ecoMode", isChecked)
                    .apply();

            Toast.makeText(EcoCombatActivity.this, isChecked ? "Mode Éco activé" : "Mode Éco désactivé", Toast.LENGTH_SHORT).show();

            if (!isChecked) {
                Intent intent = new Intent(EcoCombatActivity.this, CombatActivity.class);
                startActivity(intent);
                finish(); // Ferme l'activité actuelle pour éviter que l'utilisateur reste dans EcoCombatActivity
            }
        });

        // Listeners pour les boutons d'action
        btnRepli.setOnClickListener(v -> Toast.makeText(this, "Repli demandé", Toast.LENGTH_SHORT).show());
        btnValider.setOnClickListener(v -> Toast.makeText(this, "Demande validée", Toast.LENGTH_SHORT).show());
        btnRefuser.setOnClickListener(v -> Toast.makeText(this, "Demande refusée", Toast.LENGTH_SHORT).show());
    }
}
