package com.example.stickers;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StickerListActivity extends AppCompatActivity {

    private ListView listViewStickers;
    private List<Sticker> stickers;
    private StickerAdapter adapter;
    private DBHelper dbHelper;

    private TextView tvInfo, tvBudget, tvProgress;
    private ProgressBar progressBar;

    private String username;
    private String mode;

    private Random randomGen = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_list);

        listViewStickers = findViewById(R.id.listViewStickers);
        tvInfo = findViewById(R.id.tvInfo);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        tvBudget = findViewById(R.id.tvBudget);

        Button btnOpenPack = findViewById(R.id.btnOpenPack);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        SharedPreferences preferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        username = preferences.getString("currentUser", null);

        mode = getIntent().getStringExtra("mode");

        stickers = new ArrayList<>();

        boolean isMarket = "market".equals(mode);

        adapter = new StickerAdapter(
                this,
                stickers,
                isMarket,
                false,
                false,
                username
        );

        listViewStickers.setAdapter(adapter);

        if (isMarket) {

            btnOpenPack.setOnClickListener(v -> {

                int packPrice = 3000;
                int budget = dbHelper.getBudget();

                if (budget < packPrice) {
                    Toast.makeText(this, "No tenés dinero 💸", Toast.LENGTH_SHORT).show();
                    return;
                }

                startActivity(new Intent(this, PackOpeningActivity.class));

                new Handler().postDelayed(this::openPack, 2500);
            });

        } else {
            btnOpenPack.setEnabled(false);
            btnOpenPack.setText("Solo en mercado");
        }

        loadStickers();
        updateBudgetUI();

        listViewStickers.setOnItemClickListener((parent, view, position, id) -> {
            Sticker selected = stickers.get(position);
            Intent intent = new Intent(this, StickerStatsActivity.class);
            intent.putExtra("player_name", selected.getName());
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStickers();
        updateBudgetUI();
    }

    private void loadStickers() {

        stickers.clear();

        if ("market".equals(mode)) {

            List<Sticker> apiList = loadFromJSON();
            List<Sticker> filtered = new ArrayList<>();

            for (Sticker s : apiList) {
                if (!dbHelper.existsSticker(s.getNumber(), s.getTeam(), username)) {
                    filtered.add(s);
                }
            }

            stickers.addAll(filtered);
            tvInfo.setText("🛒 Mercado Mundial 2026");

        } else {

            stickers.addAll(dbHelper.getAllStickers(username));
            tvInfo.setText("📘 Mi álbum Mundial 2026");
        }

        adapter.notifyDataSetChanged();
        updateProgress();
    }

    private List<Sticker> loadFromJSON() {

        List<Sticker> list = new ArrayList<>();

        try {
            InputStream is = getAssets().open("mock_stickers.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            JSONArray array = new JSONArray(new String(buffer));

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                list.add(new Sticker(
                        0,
                        obj.getInt("number"),
                        obj.getString("name"),
                        obj.getString("team"),
                        obj.getString("rarity"),
                        false
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error cargando jugadores", Toast.LENGTH_SHORT).show();
        }

        return list;
    }

    private void openPack() {

        int packPrice = 3000;
        int budget = dbHelper.getBudget();

        if (budget < packPrice) return;

        List<Sticker> all = loadFromJSON();

        dbHelper.updateBudget(budget - packPrice);

        StringBuilder result = new StringBuilder();

        result.append("Jugadores obtenidos:\n\n");

        for (int i = 0; i < 5; i++) {

            Sticker base = all.get(randomGen.nextInt(all.size()));

            Sticker s = new Sticker(
                    0,
                    base.getNumber(),
                    base.getName(),
                    base.getTeam(),
                    base.getRarity(),
                    false
            );

            double prob = randomGen.nextDouble();

            if (prob < 0.05) s.setRarity("Legend");
            else if (prob < 0.25) s.setRarity("Rare");
            else s.setRarity("Common");

            boolean repeated =
                    dbHelper.existsSticker(
                            s.getNumber(),
                            s.getTeam(),
                            username
                    );

            if (repeated) {
                s.setRepeated(true);
            }

            dbHelper.insertSticker(s, username);

            result.append("⚽ ")
                    .append(s.getName())
                    .append(" - ")
                    .append(s.getTeam());

            if (repeated) {
                result.append(" 🔁");
            }

            result.append("\n");
        }

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("🎁 Sobre abierto")
                .setMessage(result.toString())
                .setPositiveButton("Aceptar", null)
                .show();

        loadStickers();
        updateBudgetUI();
    }

    public void updateBudgetUI() {
        tvBudget.setText("💰 $" + dbHelper.getBudget());
    }

    private void updateProgress() {

        int total = loadFromJSON().size();
        int owned = dbHelper.getAllStickers(username).size();

        int percent = total == 0 ? 0 : (owned * 100) / total;

        tvProgress.setText("Álbum: " + percent + "% (" + owned + "/" + total + ")");
        progressBar.setProgress(percent);
    }
}