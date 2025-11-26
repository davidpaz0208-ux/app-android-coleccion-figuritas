package com.example.players;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeletePlayerActivity extends AppCompatActivity {

    private TextView tvInfo;
    private Button btnDelete, btnBack;
    private Player player;
    private PlayerApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_player);

        tvInfo = findViewById(R.id.tvInfo); // asegúrate que coincida con el XML
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        api = RetrofitClient.getInstance(this).create(PlayerApi.class);

        player = (Player) getIntent().getSerializableExtra("player");

        if (player != null) {
            tvInfo.setText("¿Eliminar a " + player.getName() + "?");
        }

        btnDelete.setOnClickListener(v -> deletePlayer());
        btnBack.setOnClickListener(v -> finish());
    }

    private void deletePlayer() {
        if (player == null) return;

        api.deletePlayer(player.getId()).enqueue(new Callback<Player>() {
            @Override
            public void onResponse(Call<Player> call, Response<Player> response) {
                if (response.isSuccessful()) {
                    show("Jugador eliminado");
                    finish(); // vuelve a la lista
                } else {
                    show("Error al eliminar jugador: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Player> call, Throwable t) {
                show("Error de conexión: " + t.getMessage());
            }
        });
    }

    private void show(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
