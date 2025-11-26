package com.example.players;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PlayersListActivity extends AppCompatActivity {

    private ListView lvPlayers;
    private ArrayList<Player> playerList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);

        lvPlayers = findViewById(R.id.lvPlayers);

        playerList = new ArrayList<>();
        playerList.add(new Player(1, "Juan Pérez", "Delantero"));
        playerList.add(new Player(2, "Carlos Gómez", "Defensa"));
        playerList.add(new Player(3, "Luis Martínez", "Mediocampo"));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<>());
        lvPlayers.setAdapter(adapter);

        adapter.clear();
        for (Player p : playerList) adapter.add(p.toString());
        adapter.notifyDataSetChanged();
    }
}

