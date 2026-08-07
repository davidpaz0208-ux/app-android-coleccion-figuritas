package com.example.stickers;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class UpdateStickerActivity extends AppCompatActivity {

    private ListView listViewStickers;
    private EditText etStickerNumber, etNewTeam, etNewName;
    private Button btnUpdate, btnBack;

    private List<Sticker> allStickers = new ArrayList<>();
    private Sticker selectedSticker;

    private ArrayList<String> stickerNames;

    private String username;
    private ArrayAdapter<String> adapter;

    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sticker);

        listViewStickers = findViewById(R.id.listViewStickers);
        etStickerNumber = findViewById(R.id.etStickerNumber);
        etNewTeam = findViewById(R.id.etNewTeam);
        etNewName = findViewById(R.id.etNewName);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnBack = findViewById(R.id.btnBack);

        SharedPreferences preferences =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        username = preferences.getString("currentUser", null);

        dbHelper = new DBHelper(this);

        loadStickers();

        listViewStickers.setOnItemClickListener((parent, view, position, id) -> {
            selectedSticker = allStickers.get(position);

            etStickerNumber.setText(String.valueOf(selectedSticker.getNumber()));
            etNewTeam.setText(selectedSticker.getTeam());
            etNewName.setText(selectedSticker.getName());
        });

        btnUpdate.setOnClickListener(v -> updateSticker());
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadStickers() {

        allStickers.clear();
        allStickers.addAll(dbHelper.getAllStickers(username));

        stickerNames = new ArrayList<>();

        for (Sticker s : allStickers) {
            stickerNames.add("N° " + s.getNumber() + " - " + s.getName() + " (" + s.getTeam() + ")");
        }

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                stickerNames
        );

        listViewStickers.setAdapter(adapter);
    }

    private void updateSticker() {

        if (selectedSticker == null) {
            showToast("Seleccione una figurita");
            return;
        }

        String newTeam = etNewTeam.getText().toString().trim();
        String newName = etNewName.getText().toString().trim();

        if (newTeam.isEmpty() || newName.isEmpty()) {
            showToast("Complete todos los campos");
            return;
        }

        boolean updatedTeam = dbHelper.updateStickerTeam(
                selectedSticker.getId(),
                newTeam
        );

        boolean updatedName = dbHelper.updateStickerName(
                selectedSticker.getId(),
                newName
        );

        if (updatedTeam && updatedName) {

            showToast("Figurita actualizada");

            loadStickers();

            etStickerNumber.setText("");
            etNewTeam.setText("");
            etNewName.setText("");

            selectedSticker = null;

        } else {
            showToast("Error al actualizar");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}