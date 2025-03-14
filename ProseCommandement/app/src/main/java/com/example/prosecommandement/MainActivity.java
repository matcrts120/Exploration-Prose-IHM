package com.example.prosecommandement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText teamNameEditText;
    private EditText teamIdEditText;
    private EditText targetInfoEditText;
    private Button validateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        teamNameEditText = findViewById(R.id.teamName);
        teamIdEditText = findViewById(R.id.teamId);
        targetInfoEditText = findViewById(R.id.targetInfo);
        validateButton = findViewById(R.id.validateButton);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle validation logic here
                Intent intent = new Intent(MainActivity.this, SurActivity.class);
                startActivity(intent);
            }
        });
    }
}
