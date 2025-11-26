package com.example.players;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PlayerStatsActivity extends AppCompatActivity {

    private TextView tvPlayerName, tvPlayerPosition, tvGoals, tvGamesPlayed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        tvPlayerName = findViewById(R.id.tvPlayerName);
        tvPlayerPosition = findViewById(R.id.tvPlayerPosition);
        tvGoals = findViewById(R.id.tvGoals);
        tvGamesPlayed = findViewById(R.id.tvGamesPlayed);

        Player p = (Player) getIntent().getSerializableExtra("player");

        if (p != null) {
            tvPlayerName.setText(p.getName());
            tvPlayerPosition.setText("Posición: " + p.getPosition());
            tvGoals.setText("Goles: " + p.getGoals());
            tvGamesPlayed.setText("Partidos: " + p.getMatchesPlayed());
        }
    }
}
