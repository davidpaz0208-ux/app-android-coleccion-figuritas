package com.example.stickers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class RepeatedStickersActivity extends AppCompatActivity {

    private List<Sticker> stickers;
    private StickerAdapter adapter;
    private DBHelper dbHelper;
    private String username;

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeated_stickers);

        listView = findViewById(R.id.listViewRepeated);

        dbHelper = new DBHelper(this);

        SharedPreferences prefs =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        username = prefs.getString("currentUser", "");

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        stickers = dbHelper.getRepeatedStickers(username);

        android.util.Log.d(
                "REPEATED",
                "Cantidad repetidas: " + stickers.size()
        );

        android.widget.Toast.makeText(
                this,
                "Repetidas: " + stickers.size(),
                android.widget.Toast.LENGTH_LONG
        ).show();

        adapter = new StickerAdapter(
                this,
                stickers,
                false,
                false,
                true,
                username
        );

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void tradeSticker(Sticker sticker) {

        DBHelper db = new DBHelper(this);

        List<String> users = db.getAllUsers();

        users.remove(username);

        String[] userArray = users.toArray(new String[0]);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Seleccionar usuario")
                .setItems(userArray, (dialog, which) -> {

                    String selectedUser = userArray[which];

                    List<Sticker> targetStickers =
                            db.getAllStickers(selectedUser);

                    if (targetStickers.isEmpty()) {

                        android.widget.Toast.makeText(
                                this,
                                "El usuario no tiene jugadores",
                                android.widget.Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    String[] stickerNames =
                            new String[targetStickers.size()];

                    for (int i = 0; i < targetStickers.size(); i++) {

                        stickerNames[i] =
                                targetStickers.get(i).getName();
                    }

                    new androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("¿Qué jugador querés?")
                            .setItems(stickerNames,
                                    (dialog2, pos) -> {

                                        Sticker requested =
                                                targetStickers.get(pos);

                                        android.util.Log.d(
                                                "TRADE_CREATE",
                                                "offered=" + sticker.getId()
                                                        + " requested=" + requested.getId()
                                        );

                                        db.createTradeOffer(
                                                username,
                                                selectedUser,
                                                sticker.getId(),
                                                requested.getId()
                                        );

                                        android.widget.Toast.makeText(
                                                this,
                                                "Solicitud enviada",
                                                android.widget.Toast.LENGTH_SHORT
                                        ).show();
                                    })
                            .show();

                })
                .show();
    }
}