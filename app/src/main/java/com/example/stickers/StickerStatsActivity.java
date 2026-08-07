package com.example.stickers;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class StickerStatsActivity extends AppCompatActivity {

    private TextView tvPlayer, tvNumber, tvTeam, tvRarity;
    private Button btnVolver;

    private String username;

    private DBHelper dbHelper;
    private Sticker selectedSticker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_stats);

        tvPlayer = findViewById(R.id.tvPlayer);
        tvNumber = findViewById(R.id.tvNumber);
        tvTeam = findViewById(R.id.tvTeam);
        tvRarity = findViewById(R.id.tvRarity);

        btnVolver = findViewById(R.id.btnVolver);

        Button btnAction = findViewById(R.id.btnAction);

        if (btnAction != null) {
            btnAction.setVisibility(View.GONE);
        }

        username = getIntent().getStringExtra("username");

        dbHelper = new DBHelper(this);

        String playerName = getIntent().getStringExtra("player_name");

        if (playerName == null) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadSticker(playerName);

        btnVolver.setOnClickListener(v -> finish());
    }

    private void loadSticker(String name) {

        List<Sticker> stickers = dbHelper.getAllStickers(username);

        for (Sticker s : stickers) {
            if (s.getName().equals(name)) {
                selectedSticker = s;
                break;
            }
        }

        if (selectedSticker != null) {

            tvPlayer.setText(selectedSticker.getName());
            tvNumber.setText("Número: " + selectedSticker.getNumber());
            tvTeam.setText("Equipo: " + selectedSticker.getTeam());
            tvRarity.setText("Rareza: " + selectedSticker.getRarity());

        } else {
            Toast.makeText(this, "Figurita no encontrada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
