package com.example.stickers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.widget.Button;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TeamAlbumActivity extends AppCompatActivity {

    private ListView listView;
    private String team;
    private String username;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_album);

        listView = findViewById(R.id.listTeamAlbum);

        Button btnVolverAlbum =
                findViewById(R.id.btnVolverAlbum);

        btnVolverAlbum.setOnClickListener(v -> {
            finish();
        });

        TextView txtTitle =
                findViewById(R.id.txtTeamTitle);

        team =
                getIntent().getStringExtra("team");

        txtTitle.setText(
                "📖 " + team
        );

        SharedPreferences prefs =
                getSharedPreferences(
                        "UserSession",
                        MODE_PRIVATE
                );

        username =
                prefs.getString(
                        "currentUser",
                        ""
                );

        dbHelper =
                new DBHelper(this);

        cargarAlbumEquipo();
    }

    private void cargarAlbumEquipo() {

        List<TeamStickerItem> items =
                new ArrayList<>();

        List<Sticker> catalogo =
                loadCatalog();

        List<Sticker> mias =
                dbHelper.getAllStickers(username);

        for (Sticker s : catalogo) {

            if (!s.getTeam().equals(team))
                continue;

            boolean obtained = false;

            for (Sticker mine : mias) {

                if (mine.getName().equals(
                        s.getName()
                )) {

                    obtained = true;
                    break;
                }
            }

            items.add(
                    new TeamStickerItem(
                            s.getName(),
                            obtained
                    )
            );
        }

        TeamAlbumAdapter adapter =
                new TeamAlbumAdapter(
                        this,
                        items
                );

        listView.setAdapter(adapter);
    }

    private List<Sticker> loadCatalog() {

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