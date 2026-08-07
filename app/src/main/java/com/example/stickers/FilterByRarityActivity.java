package com.example.stickers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class FilterByRarityActivity extends AppCompatActivity {

    private Spinner spinnerRarity;
    private ListView listView;

    private DBHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_rarity);

        spinnerRarity = findViewById(R.id.spinnerRarity);
        listView = findViewById(R.id.listViewRarity);

        dbHelper = new DBHelper(this);

        SharedPreferences prefs =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        username = prefs.getString("currentUser", "");

        String[] rarities = {
                "Common",
                "Rare",
                "Legend"
        };

        spinnerRarity.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        rarities
                )
        );

        findViewById(R.id.btnFilter)
                .setOnClickListener(v -> loadData());

        findViewById(R.id.btnBack)
                .setOnClickListener(v -> finish());
    }

    private void loadData() {

        String rarity =
                spinnerRarity.getSelectedItem().toString();

        List<Sticker> stickers =
                dbHelper.getAllStickers(username);

        List<String> result = new ArrayList<>();

        for (Sticker s : stickers) {

            if (s.getRarity().equalsIgnoreCase(rarity)) {

                result.add(
                        "#" + s.getNumber()
                                + " - "
                                + s.getName()
                );
            }
        }

        listView.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        result
                )
        );
    }
}