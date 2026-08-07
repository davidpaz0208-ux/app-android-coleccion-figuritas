package com.example.stickers;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ManageStickersActivity extends AppCompatActivity {

    private ListView listView;
    private DBHelper dbHelper;

    private String username;
    private List<Sticker> stickerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stickers);

        listView = findViewById(R.id.listViewStickers);

        dbHelper = new DBHelper(this);

        username = getIntent().getStringExtra("username");

        loadStickers();
    }

    private void loadStickers() {
        stickerList = dbHelper.getAllStickers(username);

        ArrayAdapter<Sticker> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                stickerList
        );

        listView.setAdapter(adapter);
    }
}