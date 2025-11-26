package com.example.players;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EditPlayerNameActivity extends AppCompatActivity {

    private ListView lvPlayers;
    private EditText etNewName;
    private Button btnSave, btnBack;
    private ArrayList<Player> playersList;
    private Player selectedPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_player_name);

        lvPlayers = findViewById(R.id.lvPlayers);
        etNewName = findViewById(R.id.etNewName);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);

        // Datos de ejemplo
        playersList = new ArrayList<>();
        playersList.add(new Player(1, "Juan Pérez", "Delantero"));
        playersList.add(new Player(2, "Carlos Gómez", "Defensa"));
        playersList.add(new Player(3, "Luis Martínez", "Mediocampo"));

        ArrayList<String> names = new ArrayList<>();
        for (Player p : playersList) names.add(p.toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        lvPlayers.setAdapter(adapter);

        lvPlayers.setOnItemClickListener((parent, view, position, id) -> {
            selectedPlayer = playersList.get(position);
            etNewName.setText(selectedPlayer.getName());
        });

        btnSave.setOnClickListener(v -> {
            if (selectedPlayer != null) {
                String newName = etNewName.getText().toString().trim();
                if (!newName.isEmpty()) {
                    selectedPlayer.setName(newName);

                    // Refrescar la lista
                    names.clear();
                    for (Player p : playersList) names.add(p.toString());
                    adapter.notifyDataSetChanged();

                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                    etNewName.setText("");
                } else {
                    Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Seleccione un jugador", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, PlayersListActivity.class));
            finish();
        });
    }
}
