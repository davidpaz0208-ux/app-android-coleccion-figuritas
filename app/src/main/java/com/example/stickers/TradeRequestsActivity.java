package com.example.stickers;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TradeRequestsActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private String username;
    private ListView listViewTrades;

    private List<TradeOffer> offers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade_requests);

        listViewTrades = findViewById(R.id.listViewTrades);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        dbHelper = new DBHelper(this);

        SharedPreferences prefs =
                getSharedPreferences("UserSession", MODE_PRIVATE);

        username = prefs.getString("currentUser", "");

        // 🔥 Cargar ofertas correctamente
        offers = dbHelper.getIncomingTrades(username);

        List<String> items = new ArrayList<>();

        for (TradeOffer trade : offers) {

            Sticker offered = dbHelper.getStickerById(
                    trade.offeredStickerId,
                    trade.fromUser
            );

            Sticker requested = dbHelper.getStickerById(
                    trade.requestedStickerId,
                    username
            );

            if (offered != null && requested != null) {

                items.add(
                        trade.fromUser +
                                " ofrece " +
                                offered.getName() +
                                " por " +
                                requested.getName()
                );
            }
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_list_item_1,
                        items
                );

        listViewTrades.setAdapter(adapter);

        // 🔥 CLICK en item
        listViewTrades.setOnItemClickListener((parent, view, position, id) -> {

            TradeOffer trade = offers.get(position);

            Sticker offered = dbHelper.getStickerById(
                    trade.offeredStickerId,
                    trade.fromUser
            );

            Sticker requested = dbHelper.getStickerById(
                    trade.requestedStickerId,
                    username
            );

            if (offered == null || requested == null) {
                android.widget.Toast.makeText(
                        this,
                        "Oferta inválida (datos viejos)",
                        android.widget.Toast.LENGTH_SHORT
                ).show();
                return;
            }

            String message =
                    trade.fromUser +
                            " te ofrece:\n\n" +
                            offered.getName() +
                            "\n\nA cambio de:\n\n" +
                            requested.getName();

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("⚠️ Intercambio")
                    .setMessage(message)
                    .setPositiveButton("Aceptar", (d, w) -> {
                        dbHelper.acceptTrade(trade);
                        recreate();
                    })
                    .setNegativeButton("Rechazar", (d, w) -> {
                        dbHelper.rejectTrade(trade.id);
                        recreate();
                    })
                    .show();
        });
    }
}