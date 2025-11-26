package com.example.players;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddPlayerActivity extends AppCompatActivity {

    private EditText etName, etPosition;
    private Button btnAdd;
    private ListView lvPlayers;

    private ArrayList<Player> playerList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_player);

        etName = findViewById(R.id.etName);
        etPosition = findViewById(R.id.etPosition);
        btnAdd = findViewById(R.id.btnAdd);
        lvPlayers = findViewById(R.id.lvPlayers);

        playerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvPlayers.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String pos = etPosition.getText().toString().trim();

            if (!name.isEmpty() && !pos.isEmpty()) {
                // Se usa el constructor simplificado
                Player newPlayer = new Player(playerList.size() + 1, name, pos);
                playerList.add(newPlayer);

                adapter.clear();
                for (Player p : playerList) adapter.add(p.toString());
                adapter.notifyDataSetChanged();

                etName.setText("");
                etPosition.setText("");
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
