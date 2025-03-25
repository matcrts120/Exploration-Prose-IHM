package com.example.proseterrain;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class LaunchActivity extends AppCompatActivity {

    private TextView deviceIdTextView;
    private SharedPreferences missionPrefs;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        deviceIdTextView = findViewById(R.id.device_id);
        missionPrefs = getSharedPreferences("MissionPrefs", MODE_PRIVATE);

        // Définir l'adresse IP du dispositif
        ipAddress = getDeviceIpAddress();
        deviceIdTextView.setText("Votre ID : " + ipAddress);

        // Simuler l'attente de configuration
        simulateLoading();
    }

    private void simulateLoading() {
        // Attente de 5 secondes
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Afficher la popup de mission au lieu de démarrer une nouvelle activité
                showMissionPopup();
            }
        }, 5000);
    }

    private void showMissionPopup() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_mission, null);

        // Récupérer les références aux vues
        TextView teamName = dialogView.findViewById(R.id.team_name);
        TextView teamId = dialogView.findViewById(R.id.team_id);
        TextView targetInfo = dialogView.findViewById(R.id.target_info);
        Button closeButton = dialogView.findViewById(R.id.close_button);

        // Définir les valeurs
        teamName.setText("Équipe Alpha");
        teamId.setText(ipAddress);
        targetInfo.setText("Manteau noir");

        // Enregistrer les données de mission
        saveMissionData(teamName.getText().toString(), teamId.getText().toString(), targetInfo.getText().toString());

        builder.setView(dialogView);
        builder.setCancelable(false);

        final AlertDialog dialog = builder.create();

        // Configurer le bouton de fermeture
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Rediriger vers l'écran de transmission
                Intent intent = new Intent(LaunchActivity.this, TransmissionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    private String getDeviceIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("IP Address", ex.toString());
        }
        return "172.16.254.1"; // Adresse par défaut
    }

    private void saveMissionData(String teamName, String teamId, String targetInfo) {
        SharedPreferences.Editor editor = missionPrefs.edit();
        editor.putString("team_name", teamName);
        editor.putString("team_id", teamId);
        editor.putString("target_info", targetInfo);
        editor.putBoolean("mission_active", true);
        editor.apply();
    }
}