package com.example.players;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchPlayerActivity extends AppCompatActivity {

    private EditText etSearchQuery;
    private Button btnSearch, btnBack;
    private ListView lvSearchResults;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_player);

        etSearchQuery = findViewById(R.id.etSearchQuery);
        btnSearch = findViewById(R.id.btnSearch);
        lvSearchResults = findViewById(R.id.lvSearchResults);
        btnBack = findViewById(R.id.btnBack);
        dbHelper = new DBHelper(this);

        btnBack.setOnClickListener(v -> finish());

        btnSearch.setOnClickListener(v -> searchPlayer());
    }

    private void searchPlayer() {
        String query = etSearchQuery.getText().toString().trim();

        if (!query.isEmpty()) {
            SQLiteDatabase db = dbHelper.getReadableDB();
            try {
                Cursor cursor = db.rawQuery("SELECT * FROM players WHERE LOWER(name) LIKE ? OR LOWER(position) LIKE ?",
                        new String[]{"%" + query.toLowerCase() + "%", "%" + query.toLowerCase() + "%"});

                ArrayList<String> playerList = new ArrayList<>();

                if (cursor.moveToFirst()) {
                    do {
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String position = cursor.getString(cursor.getColumnIndex("position"));
                        playerList.add(name + " - " + position);
                    } while (cursor.moveToNext());
                }

                cursor.close();

                if (playerList.isEmpty()) {
                    showCustomToast("No se encontraron jugadores");
                } else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            R.layout.item_player, R.id.tvPlayerName, playerList);
                    lvSearchResults.setAdapter(adapter);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showCustomToast("Error al realizar la búsqueda");
            }
        } else {
            showCustomToast("Por favor ingrese un nombre o posición");
        }
    }

    private void showCustomToast(String message) {
        Toast customToast = new Toast(SearchPlayerActivity.this);
        View layout = getLayoutInflater().inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView text = layout.findViewById(R.id.tvToastMessage);
        text.setText(message);

        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setView(layout);
        customToast.show();
    }
}

