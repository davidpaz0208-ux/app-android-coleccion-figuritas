package com.example.stickers;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DeleteStickerActivity extends AppCompatActivity {

    private ListView listViewStickers;
    private Button btnDelete, btnBack;
    private DBHelper dbHelper;

    private String username;

    private List<Sticker> stickers = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Sticker selectedSticker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_sticker);

        listViewStickers = findViewById(R.id.listViewStickers);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);

        dbHelper = new DBHelper(this);

        btnDelete.setEnabled(false);

        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = preferences.getString("currentUser", null);

        loadStickers();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                getStickerNames());

        listViewStickers.setAdapter(adapter);

        listViewStickers.setOnItemClickListener((parent, view, position, id) -> {

            selectedSticker = stickers.get(position);
            btnDelete.setEnabled(true);

            Toast.makeText(this,
                    "Seleccionado: N° " + selectedSticker.getNumber(),
                    Toast.LENGTH_SHORT).show();
        });

        btnDelete.setOnClickListener(v -> deleteSelectedSticker());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadStickers() {
        stickers.clear();
        stickers.addAll(dbHelper.getAllStickers(username));
    }

    private List<String> getStickerNames() {

        List<String> names = new ArrayList<>();

        for (Sticker s : stickers) {
            names.add("N° " + s.getNumber() + " - " + s.getName() + " (" + s.getTeam() + ")");
        }

        return names;
    }

    private void deleteSelectedSticker() {

        if (selectedSticker == null) {
            show("Selecciona una figurita primero");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Eliminar la figurita N° " + selectedSticker.getNumber() + "?")
                .setPositiveButton("Sí", (dialog, which) -> {

                    boolean deleted = dbHelper.deleteSticker(selectedSticker.getId(), username);

                    if (deleted) {

                        stickers.remove(selectedSticker);
                        adapter.clear();
                        adapter.addAll(getStickerNames());
                        adapter.notifyDataSetChanged();

                        btnDelete.setEnabled(false);
                        show("Figurita eliminada");
                    } else {
                        show("No se pudo eliminar la figurita");
                    }

                    selectedSticker = null;
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void show(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}