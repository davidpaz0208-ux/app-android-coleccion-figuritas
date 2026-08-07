package com.example.stickers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlbumActivity extends AppCompatActivity {

    private ListView listAlbum;
    private DBHelper dbHelper;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        listAlbum = findViewById(R.id.listAlbum);

        Button btnVolverMenu =
                findViewById(R.id.btnVolverMenu);

        btnVolverMenu.setOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        SharedPreferences preferences =
                getSharedPreferences(
                        "UserSession",
                        MODE_PRIVATE
                );

        username =
                preferences.getString(
                        "currentUser",
                        ""
                );

        if (username.isEmpty()) {

            Toast.makeText(
                    this,
                    "Sesión inválida",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        cargarAlbum();
    }

    private void cargarAlbum() {

        List<Sticker> catalogo =
                loadAlbumCatalog();

        List<Sticker> misFiguritas =
                dbHelper.getAllStickers(username);

        HashMap<String, Integer> totalPorPais =
                new HashMap<>();

        HashMap<String, Integer> obtenidasPorPais =
                new HashMap<>();

        for (Sticker sticker : catalogo) {

            String pais = sticker.getTeam();

            totalPorPais.put(
                    pais,
                    totalPorPais.getOrDefault(
                            pais,
                            0
                    ) + 1
            );
        }

        for (Sticker sticker : misFiguritas) {

            String pais = sticker.getTeam();

            obtenidasPorPais.put(
                    pais,
                    obtenidasPorPais.getOrDefault(
                            pais,
                            0
                    ) + 1
            );
        }

        List<AlbumProgress> progreso =
                new ArrayList<>();

        for (String pais :
                totalPorPais.keySet()) {

            progreso.add(
                    new AlbumProgress(
                            pais,
                            obtenidasPorPais.getOrDefault(
                                    pais,
                                    0
                            ),
                            totalPorPais.get(pais)
                    )
            );
        }

        AlbumProgressAdapter adapter =
                new AlbumProgressAdapter(
                        this,
                        progreso
                );

        listAlbum.setAdapter(adapter);

        listAlbum.setOnItemClickListener(
                (parent, view, position, id) -> {

                    AlbumProgress item =
                            progreso.get(position);

                    android.content.Intent intent =
                            new android.content.Intent(
                                    AlbumActivity.this,
                                    TeamAlbumActivity.class
                            );

                    intent.putExtra(
                            "team",
                            item.getTeam()
                    );

                    startActivity(intent);
                }
        );
    }

    private List<Sticker> loadAlbumCatalog() {

        List<Sticker> list =
                new ArrayList<>();

        try {

            InputStream is =
                    getAssets().open(
                            "mock_stickers.json"
                    );

            byte[] buffer =
                    new byte[is.available()];

            is.read(buffer);

            is.close();

            JSONArray array =
                    new JSONArray(
                            new String(buffer)
                    );

            for (int i = 0; i < array.length(); i++) {

                JSONObject obj =
                        array.getJSONObject(i);

                list.add(
                        new Sticker(
                                0,
                                obj.getInt("number"),
                                obj.getString("name"),
                                obj.getString("team"),
                                obj.getString("rarity"),
                                false
                        )
                );
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return list;
    }
}