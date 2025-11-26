package com.example.players;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Intent;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public class UpdatePlayerPositionActivity extends AppCompatActivity {

    private EditText etPlayerName, etNewPosition;
    private Button btnUpdatePosition, btnBack;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_player_position);

        etPlayerName = findViewById(R.id.etPlayerName);
        etNewPosition = findViewById(R.id.etNewPosition);
        btnUpdatePosition = findViewById(R.id.btnUpdatePosition);
        btnBack = findViewById(R.id.btnBack);
        dbHelper = new DBHelper(this);

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(UpdatePlayerPositionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnUpdatePosition.setOnClickListener(v -> updatePlayerPosition());
    }

    private void updatePlayerPosition() {
        String playerName = etPlayerName.getText().toString().trim();
        String newPosition = etNewPosition.getText().toString().trim();

        if (!playerName.isEmpty() && !newPosition.isEmpty()) {
            boolean isUpdated = dbHelper.updatePlayerPosition(playerName, newPosition);

            if (isUpdated) {
                showCustomToast("Posición actualizada correctamente");
                finish();
            } else {
                showCustomToast("Error al actualizar la posición");
            }
        } else {
            showCustomToast("Por favor complete los campos");
        }
    }

    private void showCustomToast(String message) {
        Toast customToast = new Toast(UpdatePlayerPositionActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.tvToastMessage);
        text.setText(message);

        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setView(layout);
        customToast.show();
    }
}

